package com.github.walterfan.kata.reminder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UnixDomainSocketService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String socketPath;
    private SocketChannel channel;
    private final Object lock = new Object();

    // 新增配置参数（application.properties）
    @Value("${uds.max.retries:3}")
    private int maxRetries;

    @Value("${uds.retry.delay:1000}")
    private long retryDelayMillis;

    // Constructor to inject socket path from application properties
    public UnixDomainSocketService(@Value("${uds.path:/tmp/demo.sock}") String socketPath) {
        this.socketPath = socketPath;
    }

    // Establish a connection to the Unix socket
    private void connect() throws IOException {
        UnixDomainSocketAddress socketAddress = UnixDomainSocketAddress.of(socketPath);
        channel = SocketChannel.open(socketAddress);
        log.info("Connected to Unix socket at {}", socketPath);
    }

    // Ensure the connection is established
    private void ensureConnected() throws IOException {
        if (channel == null || !channel.isOpen() || !channel.isConnected()) {
            log.warn("Reconnecting to Unix socket at {}", socketPath);
            connect();
        }
    }

    // Send a message over the Unix socket
    public void sendToUnixSocket(Object message) throws IOException {
        String jsonMessage = objectMapper.writeValueAsString(message);
        ByteBuffer messageBuffer = ByteBuffer.wrap((jsonMessage + "\n").getBytes(StandardCharsets.UTF_8));

        synchronized (lock) {
            int attempt = 0;
            while (attempt <= maxRetries) {
                try {
                    ensureConnected();
                    writeMessage(messageBuffer);
                    return; // 发送成功立即返回
                } catch (IOException ex) {
                    if (attempt++ == maxRetries) {
                        log.error("Failed after {} attempts to send message to Unix socket", maxRetries, ex);
                        throw new IOException("Failed after " + maxRetries + " attempts", ex);
                    }
                    log.warn("Send failed (attempt {}/{}), retrying...", attempt, maxRetries);
                    closeQuietly(); // 关闭旧连接
                    sleepSafe(retryDelayMillis);
                }
            }
        }
    }

    // Write message to the Unix socket using NIO Buffer
    private void writeMessage(ByteBuffer messageBuffer) throws IOException {
        messageBuffer.rewind();
        while (messageBuffer.hasRemaining()) {
            channel.write(messageBuffer);
        }
        log.debug("Message sent to Unix socket: {}", messageBuffer);
    }

    // Clean up the connection when the service is destroyed
    @PreDestroy
    public void close() {
        synchronized (lock) {
            closeQuietly();
        }
    }

    // 明确资源关闭逻辑
    private void closeQuietly() {
        if (channel != null) {
            try {
                channel.close();
                log.info("Unix socket closed");
            } catch (IOException e) {
                log.debug("Error closing Unix socket", e);
            } finally {
                channel = null;
            }
        }
    }

    // 安全的重试间隔
    private void sleepSafe(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted during retry", e);
            throw new RuntimeException("Interrupted during retry", e);
        }
    }
}
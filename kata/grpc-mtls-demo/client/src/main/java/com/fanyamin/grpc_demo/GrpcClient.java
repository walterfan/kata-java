package com.fanyamin.grpc_demo;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import com.fanyamin.grpc_demo.HelloServiceGrpc;
import com.fanyamin.grpc_demo.HelloRequest;
import com.fanyamin.grpc_demo.HelloResponse;

public class GrpcClient {
    public static void main(String[] args) throws SSLException {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8443)
                .useTransportSecurity()
                .sslContext(GrpcSslContexts.forClient()
                        .trustManager(new File("/tmp/certs/ca.crt"))
                        .keyManager(new File("/tmp/certs/client.crt"), new File("/tmp/certs/client.pem"))
                        .build())
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);
        HelloResponse response = stub.sayHello(HelloRequest.newBuilder().setName("Walter").build());
        System.out.println(response.getMessage());

        channel.shutdown();
    }
}

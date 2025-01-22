package com.fanyamin.grpc_demo;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import java.io.File;

public class GrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = NettyServerBuilder.forPort(8443)
                .addService(new HelloServiceImpl())
                .useTransportSecurity(
                        new File("/tmp/certs/server.crt"),
                        new File("/tmp/certs/server.pem"))
                .build();

        server.start();
        System.out.println("Server started on port 8443");
        server.awaitTermination();
    }
}

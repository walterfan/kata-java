package com.fanyamin.grpc_demo;
import com.fanyamin.grpc_demo.HelloServiceGrpc;
import com.fanyamin.grpc_demo.HelloRequest;
import com.fanyamin.grpc_demo.HelloResponse;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String message = "Hello, " + request.getName();
        HelloResponse response = HelloResponse.newBuilder().setMessage(message).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

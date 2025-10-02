package com.service.content_service.grpc;

import io.grpc.Channel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class UserGrpcClient {

    @GrpcClient("user-service")
    private Channel channel;

    private UserServiceGrpc.UserServiceBlockingStub stub;

    private UserServiceGrpc.UserServiceBlockingStub stub() {
        if (stub == null) {
            stub = UserServiceGrpc.newBlockingStub(channel);
        }
        return stub;
    }

    public UserProto.UserResponse getUserById(String id) {
        return stub().getUserById(UserProto.UserRequest.newBuilder().setId(id).build());
    }
}

package com.service.user_service.grpc;

import com.service.user_service.Repository.UserRepository;
import com.service.user_service.Entity.User;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserById(UserProto.UserRequest request, StreamObserver<UserProto.UserResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProto.UserResponse resp = UserProto.UserResponse.newBuilder()
                    .setId(user.getId().toString())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}

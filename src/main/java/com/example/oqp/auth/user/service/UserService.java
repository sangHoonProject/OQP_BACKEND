package com.example.oqp.auth.user.service;

import com.example.oqp.auth.user.controller.request.RegisterRequest;
import com.example.oqp.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(RegisterRequest registerRequest) {

    }
}

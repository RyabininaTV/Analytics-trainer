package com.example.services;

import com.example.entities.User;
import com.example.exceptions.UserNotFound;
import com.example.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFound::new);
    }

}

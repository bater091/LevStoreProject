package com.demo.validator;

import com.demo.entity.User;
import com.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


@Component
public class UserValidator {
    private final UserRepository userRepository;
    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User validateUser(User user){
            if(userRepository.findByUsername(user.getUsername()) != null){
                throw new RuntimeException("username is exist");
            }else {
               return user;
            }
    }
}

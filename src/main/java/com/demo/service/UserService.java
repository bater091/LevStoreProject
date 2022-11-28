package com.demo.service;


import com.demo.entity.Role;
import com.demo.entity.User;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;
import com.demo.validator.UserValidator;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional

public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(userValidator.validateUser(user));
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        userRepository.findByUsername(username).getRoles().add(roleRepository.findByRoleName(roleName));
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ArrayList<SimpleGrantedAuthority> simpleGrantedAuthority = new ArrayList<>();
        userRepository.findByUsername(username).getRoles().forEach(role ->
                simpleGrantedAuthority.add(new SimpleGrantedAuthority(role.getRoleName())));

        return new org.springframework.security.core.userdetails.User(
                userRepository.findByUsername(username).getUsername(),
                userRepository.findByUsername(username).getPassword(),
                simpleGrantedAuthority
        );
    }


}

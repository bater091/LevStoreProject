package com.demo.controller;


import com.demo.entity.Role;
import com.demo.entity.User;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }


    @PostMapping("/register")
    public void saveUser(@RequestParam String username, @RequestParam String password,HttpServletResponse response) throws IOException {
        ArrayList<Role> arrayListRoles = new ArrayList<>();

        userService.saveUser(new User(
                null,
                password,
                username,
                arrayListRoles
        ));
        userService.addRoleToUser(username, "ROLE_USER");
        response.sendRedirect("/product");

    }


    @GetMapping("/registerForm")
    public ModelAndView saveUser() {

        return new ModelAndView("register");
    }

    @GetMapping("/loginForm")
    public ModelAndView logIn() {

        return new ModelAndView("loginForm");
    }

}

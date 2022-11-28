package com.demo.controller;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ModelAndView modelAndView = new ModelAndView("error");
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == HttpStatus.UNAUTHORIZED.value()){
            modelAndView.addObject("error","incorrect login or password");
            return modelAndView;
            }
            if(statusCode == HttpStatus.FORBIDDEN.value()){
                modelAndView.addObject("error","please log in");
                return modelAndView;
            }
        }
        return null;
        }

}

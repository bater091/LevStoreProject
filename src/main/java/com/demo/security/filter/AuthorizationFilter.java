package com.demo.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log
@WebFilter
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

private final UserService userService;
    @Autowired
    public AuthorizationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = null;
        Cookie[] a = request.getCookies();
        for (Cookie cookie : a) {
            if (cookie.getName().equals("JWT")) {
                authorizationHeader = cookie.getValue();
            }
        }
        if ((request.getServletPath().equals("/user/login") ||
                request.getServletPath().equals("/user/loginForm") ||
                request.getServletPath().equals("/user/registerForm") ||
                request.getServletPath().equals("/user/register") ||
                request.getServletPath().equals("/product") ||
                request.getServletPath().equals("/product/productMenu") ||
                request.getServletPath().equals("/user/logout")) &&
                authorizationHeader == null
                ){





            filterChain.doFilter(request, response);
        } else {



            authorizationHeader = "Bearer " + authorizationHeader;
            if (authorizationHeader.startsWith("Bearer ") && authorizationHeader.length() > 167) {
                try {

                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256("token".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT;
                    if(verifier.verify(token).getExpiresAt().before(new Date(System.currentTimeMillis()))){
                        token = JWT.create()
                                .withSubject(request.getUserPrincipal().getName())
                                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                                .withIssuer(request.getRequestURI())
                                .withClaim("roles", userService.loadUserByUsername(request.getUserPrincipal().getName())
                                        .getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                                .sign(algorithm);
                    }
                        decodedJWT = verifier.verify(token);
                        String username = decodedJWT.getSubject();
                        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                        Collection<SimpleGrantedAuthority> simpleGrantedAuthorityCollections = new ArrayList<>();
                        Arrays.stream(roles).forEach(role ->
                                simpleGrantedAuthorityCollections.add(new SimpleGrantedAuthority(role)));
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorityCollections);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        filterChain.doFilter(request, response);

                } catch (Exception e) {
                    logger.error("error " + e.getMessage());
                    response.setHeader("error ", e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);

                }

            } else {

                filterChain.doFilter(request, response);
            }
        }
    }
}



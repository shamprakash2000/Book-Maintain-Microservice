package com.example.Api.Gateway.Filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndPoints = List.of(
        "/userService/api/login",
            "/userService/api/signUp",
            "/userService/api/health",
            "/contentService/api/health",
            "/userInteractionService/api/health",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndPoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

}

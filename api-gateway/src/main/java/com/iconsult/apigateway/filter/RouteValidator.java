package com.iconsult.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/v1/customer/register",
            "/v1/customer/signup",
            "/v1/customer/login",
            "/v1/customer/getCustomer/{id}",
            "/v1/otp/**",
            "/v1/customer/suggest",
            "/v1/customer/forgetUserName",
            "/v1/customer/forgetUser",
            "/v1/customer/forgetPassword",
            "/v1/customer/resetPassword",
            "/v1/customer/verifyForgetPasswordToken",
            "/api/devices/**",
            "/v1/customer/fund/**",
            "/v1/billPayment/**",
            "/eureka",
            "/v1/feedback/**"
    );

    private final PathMatcher pathMatcher = new AntPathMatcher();

//    public Predicate<ServerHttpRequest> isSecured =
//            request -> openApiEndpoints
//                    .stream()
//                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));

}
package com.iconsult.apigateway.filter;

import com.iconsult.apigateway.exception.ServiceException;
import com.iconsult.apigateway.models.CustomResponseEntity;
import com.iconsult.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Autowired
    private RouteValidator validator;

//    @Autowired
//    private WebClient.Builder webClientBuilder;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                logger.info("Request is secured, checking for authorization header");

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.severe("Missing authorization header");
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    ResponseEntity<Boolean> response = restTemplate.getForEntity(
                            "http://localhost:8088/v1/customer/validateToken?token={token}",
                            Boolean.class,
                            authHeader
                    );

                    logger.info(Objects.requireNonNull(response.getBody()).toString());

                    if (Boolean.TRUE.equals(response.getBody())) {
                        return chain.filter(exchange);
                    } else {
                        logger.severe("Token validation failed");
                        //CustomResponseEntity.errorResponse(new ServiceException("Unauthorized access to application"));
                        return Mono.error(new ServiceException("Unauthorized access to application"));
                    }
                } catch (Exception e) {
                    logger.severe("Invalid access: " + e.getMessage());
                    return Mono.error(new ServiceException("Unauthorized access to application"));
                }

//                try {
//                    // Validate token with WebClient
//                    //String url = "http://USER-SERVICE/v1/customer/validateToken?token=" + authHeader;
//                    //logger.info("Validating token with URL: " + url);
//                    jwtUtil.validateToken(authHeader);
//                    // Add custom header to indicate the request passed through the API Gateway
//                    exchange.getRequest().mutate()
//                            .header("X-API-Gateway", "true")
//                            .build();
//                } catch (Exception e) {
//                    logger.severe("Invalid access: " + e.getMessage());
//                    throw new ServiceException("Unauthorized access to application", e);
//                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Empty class as placeholder for any future configurations
    }
}
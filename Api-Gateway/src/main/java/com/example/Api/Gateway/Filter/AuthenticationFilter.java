package com.example.Api.Gateway.Filter;

import com.example.Api.Gateway.Service.TokenBlacklistService;
import com.example.Api.Gateway.Util.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public AuthenticationFilter(){
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange,chain)->{
            if(validator.isSecured.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return ServerResponse.status(HttpStatus.FORBIDDEN).build().then();
                    //throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
                String JWT=authHeader.substring(7);
                if(tokenBlacklistService.isTokenBlacklisted(JWT)){
                    exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                    return ServerResponse.status(HttpStatus.FORBIDDEN).build().then();
                }
                if(authHeader!=null && authHeader.startsWith("Bearer ")){
                    authHeader=authHeader.substring(7);
                }
                try{
                    jwtUtil.validateToken(authHeader);
                }
                catch (Exception e){
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return ServerResponse.status(HttpStatus.FORBIDDEN).build().then();

//                    throw new RuntimeException("jwt not filtered,u authorized");
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config{

    }

}

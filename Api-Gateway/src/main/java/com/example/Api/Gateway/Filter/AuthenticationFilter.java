package com.example.Api.Gateway.Filter;

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

    public AuthenticationFilter(){
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("gateway filter");
        return ((exchange,chain)->{
            if(validator.isSecured.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    System.out.println("missing auth header line 30");
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return ServerResponse.status(HttpStatus.FORBIDDEN).build().then();
                    //throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
                if(authHeader!=null && authHeader.startsWith("Bearer ")){
                    authHeader=authHeader.substring(7);
                    System.out.println("line 34 : "+authHeader);
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

            System.out.println("Leaving AuthenticationFilter");
            return chain.filter(exchange);
        });
    }

    public static class Config{

    }

}

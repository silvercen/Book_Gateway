package com.ust.Book_Gateway.filter;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.client.RestClient;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>{

    @Autowired
    private RouteValidator routeValidator;

    public AuthenticationFilter(){
        super(Config.class);
    }

    public static class Config{

    }

    public GatewayFilter apply(Config config)
    {
        return((exchange, chain) -> {
            if(routeValidator.isSecured.test(exchange.getRequest()))
            {
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if(authHeader != null && authHeader.startsWith("Bearer"))
                {
                    authHeader = authHeader.substring(7);
                }

                try{
                    RestClient restClient = RestClient.create();
                    restClient.get().uri("http://localhost:5961/auth/validate/" + authHeader)
                            .retrieve().body(Boolean.class);
                }catch(Exception e)
                {
                    throw new RuntimeException("Unauthorized access");
                }
            }
            return chain.filter(exchange);
        });
    }
}

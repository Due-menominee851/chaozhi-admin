package com.chaozhi.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private Token token = new Token();
    private Admin admin = new Admin();

    @Data
    public static class Token {
        private String redisPrefix = "chaozhi:auth:token:";
        private int expireDays = 7;
    }

    @Data
    public static class Admin {
        private String username = "admin";
        private String password = "123456";
    }
}

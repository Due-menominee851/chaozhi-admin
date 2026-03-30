package com.chaozhi.web.service;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chaozhi.web.config.AuthProperties;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthTokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AuthProperties authProperties;

    public String createToken(UserInfoVO userInfo) {
        String token = IdUtil.fastSimpleUUID();
        userInfo.setToken(token);
        stringRedisTemplate.opsForValue().set(
                buildKey(token),
                toJson(userInfo),
                authProperties.getToken().getExpireDays(),
                TimeUnit.DAYS
        );
        return token;
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    public UserInfoVO getUserInfo(HttpServletRequest request) {
        return getUserInfoByToken(extractToken(request));
    }

    public UserInfoVO getUserInfoByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        String userInfoJson = stringRedisTemplate.opsForValue().get(buildKey(token));
        if (userInfoJson == null || userInfoJson.trim().isEmpty()) {
            return null;
        }
        return fromJson(userInfoJson);
    }

    public void removeToken(HttpServletRequest request) {
        String token = extractToken(request);
        removeTokenByValue(token);
    }

    public void removeTokenByValue(String token) {
        if (token != null && !token.trim().isEmpty()) {
            stringRedisTemplate.delete(buildKey(token));
        }
    }

    private String buildKey(String token) {
        return authProperties.getToken().getRedisPrefix() + token;
    }

    private UserInfoVO fromJson(String userInfoJson) {
        try {
            return objectMapper.readValue(userInfoJson, UserInfoVO.class);
        } catch (Exception e) {
            log.error("解析 Redis 登录态失败", e);
            return null;
        }
    }

    private String toJson(UserInfoVO userInfo) {
        try {
            return objectMapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化登录态失败", e);
        }
    }
}

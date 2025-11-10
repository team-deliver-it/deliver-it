package com.deliverit.global.infrastructure.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.deliverit.user.domain.entity.User;
import com.deliverit.user.domain.entity.UserRoleEnum;
import com.deliverit.user.presentation.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/v1/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        User user = principal.getUser();
        String username = principal.getUsername();
        UserRoleEnum role = user.getRole();

        String accessToken = jwtUtil.createAccessToken(user.getId(), username, role);
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), username, role);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.setContentType("application/json;charset=UTF-8");

        try {
            var body = new HashMap<String, Object>();
            body.put("accessToken", accessToken);
            body.put("refreshToken", refreshToken);
            body.put("userId", user.getId());
            new ObjectMapper().writeValue(response.getWriter(), body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}

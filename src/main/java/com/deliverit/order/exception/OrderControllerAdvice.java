package com.deliverit.order.exception;

import com.deliverit.global.response.ApiResponse;
import com.deliverit.global.response.code.ClientResponseCode;
import com.deliverit.global.response.code.UserResponseCode;
import com.deliverit.order.presentation.controller.OrderController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "order-service")
@RestControllerAdvice(assignableTypes = OrderController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrderControllerAdvice {
    private final View error;

    public OrderControllerAdvice(View error) {
        this.error = error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleBodyValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        String method = request.getMethod();
        String path = request.getRequestURI();

        log.warn("AccessDenied errorId={} message={} method={} path={}", errorId, message, method, path);
        return ApiResponse.create(
                ClientResponseCode.VALIDATION_FAILED,
                message,
                null
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Map<String, Object>> handleParamValidation(ConstraintViolationException ex, HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();

        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst().get();

        String method = request.getMethod();
        String path = request.getRequestURI();

        log.warn("AccessDenied errorId={} message={} method={} path={}", errorId, message, method, path);

        return ApiResponse.create(
                ClientResponseCode.VALIDATION_FAILED,
                message,
                null
        );
    }
//
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedExceptionForOrderController(AccessDeniedException ex, HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.getPrincipal() != null)
                ? String.valueOf(auth.getPrincipal()) : "anonymous";

        String roles = (auth != null && auth.getAuthorities() != null)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")) : "";

        String method = request.getMethod();
        String path = request.getRequestURI();

        log.warn("AccessDenied errorId={} user={} roles={} method={} path={}", errorId, username, roles, method, path);
        return ApiResponse.create(UserResponseCode.UNAUTHORIZED_USER, "주문에 접근할 권한이 없습니다.", null);
    }
}

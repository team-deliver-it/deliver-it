package com.deliverit.global.exception;

import com.deliverit.global.response.ApiResponse;
import com.deliverit.global.response.code.ClientResponseCode;
import com.deliverit.global.response.code.ServerResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MapException.class)
    public ResponseEntity<ApiResponse<?>> handleMapException(MapException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(
                ApiResponse.create(
                        e.getResponseCode(),
                        e.getResponseCode().getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(RestaurantException.class)
    public ResponseEntity<ApiResponse<?>> handleRestaurantException(RestaurantException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(
                ApiResponse.create(
                        e.getResponseCode(),
                        e.getResponseCode().getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiResponse<?>> handleOrderException(OrderException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(
                ApiResponse.create(
                        e.getResponseCode(),
                        e.getResponseCode().getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<?>> handlePaymentException(PaymentException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(
                ApiResponse.create(
                        e.getResponseCode(),
                        e.getResponseCode().getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<?>> handleDomainException(DomainException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(
                ApiResponse.create(
                        e.getResponseCode(),
                        e.getResponseCode().getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.create(
                        ServerResponseCode.SERVER_ERROR,
                        "서버 오류가 발생했습니다.",
                        null
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.create(
                        ClientResponseCode.MISSING_PARAMETER,
                        "요청 파라미터 누락입니다.",
                        null
                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.create(
                        ClientResponseCode.NOT_FOUND,
                        "요청한 리소스를 찾을 수 없습니다.",
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.create(
                        ClientResponseCode.VALIDATION_FAILED,
                        "유효성 검증 실패입니다.",
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.create(
                        ClientResponseCode.INVALID_TYPE,
                        "잘못된 요청 파라미터 타입입니다.",
                        null
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.create(
                        ClientResponseCode.UNREADABLE_MESSAGE,
                        "요청 본문을 읽을 수 없습니다.",
                        null
                )
        );
    }
}

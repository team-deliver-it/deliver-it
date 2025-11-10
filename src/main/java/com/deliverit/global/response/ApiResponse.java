package com.deliverit.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.deliverit.global.response.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ResponseCode responseCode;

    private String message;

    private T data;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "responseCode=" + responseCode +
                ", message='" + message + '\'' +
                '}';
    }

    public static <T> ApiResponse<T> create(ResponseCode code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
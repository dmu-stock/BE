package com.dmu.stock.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(SuccessType successType) {
        return new ApiResponse<T>(true, successType.getDesc(), null);
    }

    public static <T> ApiResponse<T> success(SuccessType successType, T body) {
        return new ApiResponse<T>(true, successType.getDesc(), body);
    }

    public static <T> ApiResponse<T> errorResponse(ErrorCode errorCode) {
        return new ApiResponse<T>(false, errorCode.getDesc(), null);
    }
}

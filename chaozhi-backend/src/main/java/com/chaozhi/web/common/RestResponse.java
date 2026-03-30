package com.chaozhi.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> RestResponse<T> success() {
        final RestResponse<T> response = new RestResponse<>();
        response.setCode(0);
        return response;
    }

    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(0);
        response.setData(data);
        return response;
    }

    public static <T> RestResponse<T> fail(int code, String message) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    private RestResponse() {
    }
}
package com.chaozhi.web.handle;

import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public <T> RestResponse<T> exceptionError(Exception e) {
        log.error("系统发生未知异常", e);
        return RestResponse.fail(CommonErrCode.UNKNOWN.getCode(), "未知错误，请联系技术人员");
    }

    @ExceptionHandler(value = BusinessException.class)
    public <T> RestResponse<T> businessExceptionError(BusinessException e) {
        return RestResponse.fail(e.getErrorCode().getCode(), e.getErrorCode().getDesc());
    }

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public <T> RestResponse<T> handleParameterVerificationException(Exception e) {
        String msg = null;
        if (e instanceof BindException) {
            FieldError fieldError = ((BindException) e).getFieldError();
            if (fieldError != null) {
                msg = fieldError.getDefaultMessage();
            }
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                msg = fieldError.getDefaultMessage();
            }
        } else if (e instanceof ConstraintViolationException) {
            msg = e.getMessage();
            if (msg != null) {
                int lastIndex = msg.lastIndexOf(':');
                if (lastIndex >= 0) {
                    msg = msg.substring(lastIndex + 1).trim();
                }
            }
        } else {
            msg = "处理参数时异常";
        }
        return RestResponse.fail(CommonErrCode.PARAM_INVALID.getCode(), msg);
    }
}

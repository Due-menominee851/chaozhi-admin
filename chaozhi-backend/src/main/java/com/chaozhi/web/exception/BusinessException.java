package com.chaozhi.web.exception;

import com.chaozhi.web.common.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessException extends RuntimeException {
    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        if (this.errorCode != null) {
            return this.errorCode.getDesc();
        }
        return super.getMessage();
    }

    public static BusinessException createBusinessException(ErrorCode errorCode, String descTemp) {
        return new BusinessException(new ErrorCode() {
            @Override
            public int getCode() { return errorCode.getCode(); }
            @Override
            public String getDesc() { return descTemp; }
        });
    }

    public BusinessException() {
        super();
    }
}
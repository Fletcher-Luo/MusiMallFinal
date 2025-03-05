package com.ByteAndHeartDance.pay.common;

import cn.hutool.http.HttpStatus;
import lombok.Data;


@Data
public class ApiResult<T> {

    /**
     * 请求成功状态码
     */
    public static final int OK = HttpStatus.HTTP_OK;

    /**
     * 接口返回码
     */
    private int code;

    /**
     * 接口返回信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 链路id
     */

    private String traceId;

    public ApiResult() {
    }

    public ApiResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResult(Integer code, String message, T data ) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
}

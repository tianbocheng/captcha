package com.teleinfo.captcha.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author TBC
 */
@Builder
@Data
public class ReturnData<T> {

    public static final Integer SUCCESS = 1;

    public static final Integer FAIL = -2;

    public static final Integer ERROR = -4;

    @Builder.Default
    private Integer status = SUCCESS;

    @Builder.Default
    private String message = "成功!";

    private T data;

}

package com.teleinfo.captcha.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通用的返回对象")
public class ReturnData<T> {

	public static final Integer SUCCESS = 1;

	public static final Integer FAIL = -2;

	public static final Integer ERROR = -4;

	public static final String MESSAGE = "成功!";

	@ApiModelProperty(value = "状态码：/1：请求成功/-2：请求失败/-4：系统错误")
	private Integer status;
	@ApiModelProperty(value = "状态信息")
	private String message;
	@ApiModelProperty(value = "详细数据")
	private T data;

	public ReturnData() {
		this.status = SUCCESS;
		this.message = MESSAGE;
	}

	public ReturnData(T data) {
		this();
		this.data = data;
	}

	public ReturnData(Integer status, String message, T data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
}

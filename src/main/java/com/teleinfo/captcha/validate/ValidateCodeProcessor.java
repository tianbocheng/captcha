/**
 * 
 */
package com.teleinfo.captcha.validate;

import org.springframework.web.context.request.ServletWebRequest;

import com.teleinfo.captcha.common.ReturnData;

import cn.hutool.json.JSONObject;

/**
 * 校验码处理器，封装不同校验码的处理逻辑
 * 
 * @author TBC
 *
 */
public interface ValidateCodeProcessor {

	/**
	 * 创建校验码
	 * 
	 * @param request
	 * @throws Exception
	 */
	ReturnData<JSONObject> create(ServletWebRequest request) throws Exception;

	/**
	 * 校验验证码
	 * 
	 * @param servletWebRequest
	 * @throws Exception
	 */
	ReturnData<JSONObject> validate(ServletWebRequest servletWebRequest);

}

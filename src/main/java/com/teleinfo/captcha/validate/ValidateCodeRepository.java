/**
 * 
 */
package com.teleinfo.captcha.validate;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 校验码存取器
 * 
 * @author TBC
 *
 */
public interface ValidateCodeRepository {

	/**
	 * 保存验证码
	 * 
	 * @param request
	 * @param code
	 * @param validateCodeType
	 */
	void save(ServletWebRequest request, ValidateCode code);

	/**
	 * 获取验证码
	 * 
	 * @param request
	 * @param validateCodeType
	 * @return
	 */
	ValidateCode get(ServletWebRequest request);

	/**
	 * 移除验证码
	 * 
	 * @param request
	 * @param codeType
	 */
	void remove(ServletWebRequest request);

}

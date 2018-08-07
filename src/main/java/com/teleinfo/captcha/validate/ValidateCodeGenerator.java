/**
 * 
 */
package com.teleinfo.captcha.validate;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 校验码生成器
 * 
 * @author TBC
 *
 */
public interface ValidateCodeGenerator {

	/**
	 * 生成校验码
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	ValidateCode generate(ServletWebRequest request) throws Exception;

}

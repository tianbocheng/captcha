/**
 * 
 */
package com.teleinfo.captcha.validate;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 验证码信息封装类
 * 
 * @author TBC
 *
 */
@Getter
@Setter
public class ValidateCode implements Serializable {

	public ValidateCode(String clientId, String token, String code, Long expireTime) {
		super();
		this.clientId = clientId;
		this.token = token;
		this.code = code;
		this.expireTime = expireTime;
	}

	public ValidateCode(String clientId, String token, String code, Long expireTime, BufferedImage image) {
		super();
		this.clientId = clientId;
		this.token = token;
		this.code = code;
		this.expireTime = expireTime;
		this.image = image;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1588203828504660915L;

	private String clientId;

	private String token;

	private String code;

	private Long expireTime;

	private BufferedImage image;

}

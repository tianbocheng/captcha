/**
 * 
 */
package com.teleinfo.captcha.validate.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.teleinfo.captcha.validate.ValidateCode;
import com.teleinfo.captcha.validate.ValidateCodeRepository;

/**
 * 基于session的验证码存取器
 * 
 * @author TBC
 *
 */
@Component
public class RedisValidateCodeRepository implements ValidateCodeRepository {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public void save(ServletWebRequest request, ValidateCode code) {
		redisTemplate.opsForValue().set(code.getClientId() + "@" + code.getToken(), code.getCode(),
				code.getExpireTime(), TimeUnit.SECONDS);
	}

	@Override
	public ValidateCode get(ServletWebRequest request) {
		try {
			String clientId = ServletRequestUtils.getStringParameter(request.getRequest(), "clientId");
			String token = ServletRequestUtils.getStringParameter(request.getRequest(), "token");

			String code = redisTemplate.opsForValue().get(clientId + "@" + token);

			if (code == null) {
				return null;
			}
			return new ValidateCode(clientId, token, code, null);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void remove(ServletWebRequest request) {
		try {
			String clientId = ServletRequestUtils.getStringParameter(request.getRequest(), "clientId");
			String token = ServletRequestUtils.getStringParameter(request.getRequest(), "token");
			redisTemplate.delete(clientId + "@" + token);
		} catch (Exception e) {
		}
	}

}

/**
 * 
 */
package com.teleinfo.captcha.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.teleinfo.captcha.common.BaseController;
import com.teleinfo.captcha.common.ReturnData;
import com.teleinfo.captcha.pojo.Client;
import com.teleinfo.captcha.repository.ClientRepository;
import com.teleinfo.captcha.validate.ValidateCodeProcessor;
import com.teleinfo.captcha.validate.ValidateCodeProcessorHolder;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONObject;

/**
 * 生成校验码的请求处理器
 * 
 * @author TBC
 *
 */
@RestController
public class ValidateCodeController extends BaseController {

	@Autowired
	private ValidateCodeProcessorHolder validateCodeProcessorHolder;

	@Autowired
	private ClientRepository clientRepository;

	/**
	 * 创建验证码，根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor}接口实现
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws Exception
	 */
	@GetMapping(value = "code/{type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ReturnData<JSONObject> createCode(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String type, @RequestParam(name = "clientId", required = true) String clientId) {
		if (clientRepository.findByClientId(clientId) == null) {
			return new ReturnData<>(ReturnData.FAIL, "clientId不存在", new JSONObject().put("detail", null));
		}
		try {
			return validateCodeProcessorHolder.findValidateCodeProcessor(type)
					.create(new ServletWebRequest(request, response));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnData<>(ReturnData.ERROR, e.getMessage(), null);
		}
	}

	/**
	 * 创建验证码，根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor}接口实现
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws Exception
	 */
	@PostMapping(value = "validate/{type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ReturnData<JSONObject> validateCode(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String type) {
		return validateCodeProcessorHolder.findValidateCodeProcessor(type)
				.validate(new ServletWebRequest(request, response));
	}

	/**
	 * 服务端校验
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@PostMapping(value = "confirm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ReturnData<JSONObject> validateCode(@RequestBody JSONObject json) {

		String clientId = json.getStr("clientId");

		String token = json.getStr("token");

		String sign = json.getStr("sign");

		if (json == null || clientId == null || token == null || sign == null) {
			return new ReturnData<JSONObject>(
					new JSONObject().put("result", ReturnData.ERROR).put("resultMsg", "请求参数缺失"));
		}

		Client client = clientRepository.findByClientId(clientId);

		if (client == null) {
			return new ReturnData<JSONObject>(
					new JSONObject().put("result", ReturnData.ERROR).put("resultMsg", "clientId不存在"));
		}

		String signStr = client.getClientId() + client.getClientSecurity() + token;

		byte[] key = client.getClientSecurity().getBytes();
		HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
		String macHex1 = mac.digestHex(signStr);

		// 验证成功
		if (StringUtils.equals(sign, macHex1)) {
			return new ReturnData<JSONObject>(
					new JSONObject().put("result", ReturnData.SUCCESS).put("resultMsg", "校验成功"));
		}

		return new ReturnData<JSONObject>(new JSONObject().put("result", ReturnData.FAIL).put("resultMsg", "校验失败"));
	}

}

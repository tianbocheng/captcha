/**
 * 
 */
package com.teleinfo.captcha.validate.click;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.teleinfo.captcha.common.ReturnData;
import com.teleinfo.captcha.pojo.Client;
import com.teleinfo.captcha.repository.ClientRepository;
import com.teleinfo.captcha.util.FdfsTool;
import com.teleinfo.captcha.validate.ValidateCode;
import com.teleinfo.captcha.validate.ValidateCodeRepository;
import com.teleinfo.captcha.validate.impl.AbstractValidateCodeProcessor;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 图片验证码处理器
 * 
 * @author TBC
 *
 */
@Component("clickValidateCodeProcessor")
public class ClickCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ValidateCodeRepository validateCodeRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private FdfsTool fdfsTool;

	@Value("${fdfs.web-server-url}")
	private String webServerUrl;

	/**
	 * 发送图形验证码，将其写到响应中
	 */
	@Override
	protected ReturnData<JSONObject> saveAndSend(ServletWebRequest request, ValidateCode imageCode) {
		try {

			// request.getResponse().setHeader("Cache-Control", "no-store, no-cache");
			// request.getResponse().setContentType("image/jpeg");
			// ImageIO.write(imageCode.getImage(), "JPEG",
			// request.getResponse().getOutputStream());
			//
			// return null;

			// 获取code
			Object[] text = new JSONObject(imageCode.getCode()).keySet().toArray();

			// 上传fdfs
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(imageCode.getImage(), "jpg", imOut);
			InputStream is = new ByteArrayInputStream(bs.toByteArray());
			String path = fdfsTool.uploadFile(is, "jpg");
			if (path == null) {
				throw new RuntimeException("上传fdfs错误");
			}

			// 保存redis
			validateCodeRepository.save(request, imageCode);

			return new ReturnData<>(new JSONObject().put("text", text).put("href", webServerUrl + path)
					.put("token", imageCode.getToken()).put("clientId", imageCode.getClientId()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnData<>(ReturnData.ERROR, "生成验证码图片错误", new JSONObject().put("detail", e.getMessage()));
		}

	}

	@Override
	public ReturnData<JSONObject> validate(ServletWebRequest request) {

		String codeType = getValidateCodeType(request);

		ValidateCode codeInSession = validateCodeRepository.get(request);

		String codeInRequest;
		try {
			codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), codeType + "Code");
		} catch (ServletRequestBindingException e) {
			return new ReturnData<>(new JSONObject().put("result", ReturnData.ERROR).put("resultMsg", "获取验证码的值失败"));
		}

		if (StringUtils.isBlank(codeInRequest)) {
			return new ReturnData<>(new JSONObject().put("result", ReturnData.FAIL).put("resultMsg", "验证码的值不能为空"));
		}

		if (codeInSession == null) {
			return new ReturnData<>(new JSONObject().put("result", ReturnData.FAIL).put("resultMsg", "验证码过期或不存在"));
		}

		if (!validateLocation(codeInSession.getCode(), codeInRequest)) {
			return new ReturnData<>(new JSONObject().put("result", ReturnData.FAIL).put("resultMsg", "验证码不匹配"));
		}

		validateCodeRepository.remove(request);

		Client client = clientRepository.findByClientId(codeInSession.getClientId());

		String signStr = client.getClientId() + client.getClientSecurity() + codeInSession.getToken();
		byte[] key = client.getClientSecurity().getBytes();
		HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
		String macHex1 = mac.digestHex(signStr);

		return new ReturnData<>(ReturnData.SUCCESS, ReturnData.MESSAGE,
				new JSONObject().put("result", ReturnData.SUCCESS).put("resultMsg", "验证成功")
						.put("token", codeInSession.getToken()).put("clientId", codeInSession.getClientId())
						.put("sign", macHex1));
	}

	private boolean validateLocation(String code, String codeInRequest) {
		JSONObject codeObj = new JSONObject(code);
		JSONObject codeReqObj = new JSONObject(codeInRequest);
		logger.info("正确的验证码******" + codeObj.toString());
		logger.info("请求的验证码******" + codeReqObj.toString());

		Object[] location = codeObj.values().toArray();
		Object[] locationReq = codeReqObj.values().toArray();

		if (location.length != locationReq.length) {
			return false;
		}

		// 判断坐标
		for (int i = 0; i < location.length; i++) {
			JSONArray item = new JSONArray(location[i]);
			JSONArray itemReq = new JSONArray(locationReq[i]);
			double fontSize = item.getDouble(2);
			double mistake = Math.hypot(item.getDouble(0) - itemReq.getDouble(0),
					item.getDouble(1) - itemReq.getDouble(1));
			if (mistake > (fontSize * 1.5)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据请求的url获取校验码的类型
	 * 
	 * @param request
	 * @return
	 */
	private String getValidateCodeType(ServletWebRequest request) {
		String type = StringUtils.substringBefore(getClass().getSimpleName(), "CodeProcessor").toLowerCase();
		return type;
	}
}

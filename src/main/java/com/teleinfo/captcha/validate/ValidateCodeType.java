/**
 * 
 */
package com.teleinfo.captcha.validate;

/**
 * 校验码类型
 * 
 * @author TBC
 *
 */
public enum ValidateCodeType {

	/**
	 * 短信验证码
	 */
	SMS {
		@Override
		public String getParamNameOnValidate() {
			return "smsCode";
		}
	},
	/**
	 * 图片验证码
	 */
	IMAGE {
		@Override
		public String getParamNameOnValidate() {
			return "imageCode";
		}
	},
	/**
	 * 点击验证码
	 */
	CLICK {
		@Override
		public String getParamNameOnValidate() {
			return "clickCode";
		}
	};

	/**
	 * 校验时从请求中获取的参数的名字
	 * 
	 * @return
	 */
	public abstract String getParamNameOnValidate();

}

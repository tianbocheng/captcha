package com.teleinfo.captcha.util;

import cn.hutool.captcha.CaptchaUtil;
import com.teleinfo.captcha.model.ClickCaptcha;

/**
 * @author TBC
 */
public class CaptchaUtil2 extends CaptchaUtil {

    /**
     * 创建点击类型的验证码
     *
     * @param width          图片宽
     * @param height         图片高
     * @param codeCount      字符个数
     * @param interfereCount 虚假字符个数
     * @return
     */
    public static ClickCaptcha createClickCaptcha(int width, int height, int codeCount, int interfereCount) {
        return new ClickCaptcha(width, height, codeCount, interfereCount);
    }

    /**
     * 创建点击类型的验证码
     *
     * @param codeCount      字符个数
     * @param interfereCount 虚假字符个数
     * @return {@link ClickCaptcha}
     */
    public static ClickCaptcha createClickCaptcha(int codeCount, int interfereCount) {
        return new ClickCaptcha(codeCount, interfereCount);
    }

    /**
     * 创建点击类型的验证码
     *
     * @param codeCount      字符个数
     * @return {@link ClickCaptcha}
     */
    public static ClickCaptcha createClickCaptcha(int codeCount) {
        return new ClickCaptcha(codeCount);
    }

}

package com.teleinfo.captcha.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.teleinfo.captcha.common.ReturnData;
import com.teleinfo.captcha.model.ClickCaptcha;
import com.teleinfo.captcha.util.CaptchaUtil2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author TBC
 */
@RestController
@RequestMapping("code/click")
public class CapthcaController {

    private static final String CLICK_PREFIX = "captcha:click:";

    private static final String CONFIRM_PREFIX = "captcha:confirm:";


    @Autowired
    private StringRedisTemplate captchaRepository;

    /**
     * 生成验证码
     *
     * @param width
     * @param height
     * @param length
     * @param randomStr
     * @return
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ReturnData<?> createCode(@RequestParam(name = "randomStr") String randomStr,
                                    @RequestParam(name = "width", required = false, defaultValue = "300") int width,
                                    @RequestParam(name = "height", required = false, defaultValue = "150") int height,
                                    @RequestParam(name = "length", required = false, defaultValue = "5") int length,
                                    @RequestParam(name = "interfereCount", required = false, defaultValue = "0") int interfereCount) {

        if (width < 260 || width > 400) {
            return ReturnData.builder().status(ReturnData.FAIL).message("宽度不符合规则(260-400)").data(null).build();
        }

        if (height < 100 || height > 200) {
            return ReturnData.builder().status(ReturnData.FAIL).message("高度不符合规则(50-400)").build();
        }

        if (length < 3 || length > 8) {
            return ReturnData.builder().status(ReturnData.FAIL).message("验证码长度不符合规则(3-8)").build();
        }

        if (interfereCount >= length) {
            return ReturnData.builder().status(ReturnData.FAIL).message("虚假字符个数不符合规则(0-" + length + ")").build();
        }

        // 生成验证码 && 加入缓存(将CodeGenerator置空否则反序列化有异常)
        ClickCaptcha captcha = CaptchaUtil2.createClickCaptcha(width, height, length, interfereCount);
        captcha.setGenerator(null);
        captchaRepository.opsForValue().set(CLICK_PREFIX + randomStr, JSONUtil.toJsonStr(captcha), 60, TimeUnit.SECONDS);
        return ReturnData.builder().data(captcha).build();
    }

    /**
     * 验证
     *
     * @param randomStr
     * @param codeInRequest
     * @return
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ReturnData<?> validateCode(
            @RequestParam(name = "width", required = false, defaultValue = "300") int width,
            @RequestParam(name = "height", required = false, defaultValue = "150") int height,
            @RequestParam(name = "randomStr") String randomStr, @RequestBody String codeInRequest) {

        if (height < 100 || height > 200) {
            return ReturnData.builder().status(ReturnData.FAIL).message("高度不符合规则(50-400)").build();
        }

        if (width < 260 || width > 400) {
            return ReturnData.builder().status(ReturnData.FAIL).message("宽度不符合规则(260-400)").data(null).build();
        }

        if (StrUtil.isBlank(codeInRequest)) {
            return ReturnData.builder().status(ReturnData.FAIL).message("验证码的值不能为空").build();
        }
        String codeInRredisStr = captchaRepository.opsForValue().get(CLICK_PREFIX + randomStr);
        if (StrUtil.isBlank(codeInRredisStr)) {
            return ReturnData.builder().status(ReturnData.FAIL).message("验证码过期或不存在").build();
        }
        ClickCaptcha codeInRredis = JSONUtil.toBean(codeInRredisStr, ClickCaptcha.class, false);
        List<Double[]> srand = codeInRredis.getSrand();
        // 对原有尺寸进行等比缩放
        int widthIn = codeInRredis.getWidth();
        int heightIn = codeInRredis.getHeight();
        BigDecimal flexWidth = new BigDecimal(width).divide(new BigDecimal(widthIn), 5, RoundingMode.HALF_UP);
        BigDecimal flexHeight = new BigDecimal(height).divide(new BigDecimal(heightIn), 5, RoundingMode.HALF_UP);

        if (flexWidth.doubleValue() != BigDecimal.ONE.doubleValue() || flexHeight.doubleValue() != BigDecimal.ONE.doubleValue()) {
            for (int i = 0; i < srand.size(); i++) {
                Double[] point = srand.get(i);
                point[0] = new BigDecimal(point[0]).multiply(flexWidth).doubleValue();
                point[1] = new BigDecimal(point[1]).multiply(flexHeight).doubleValue();
                point[2] = new BigDecimal(point[2]).multiply(new BigDecimal(Math.hypot(flexWidth.doubleValue(), flexHeight.doubleValue()))).doubleValue();
            }
        }

        if (!codeInRredis.verify(codeInRequest)) {
            return ReturnData.builder().status(ReturnData.FAIL).message("验证失败").build();
        }
        String key = IdUtil.fastUUID();
        captchaRepository.opsForValue().set(CONFIRM_PREFIX + key, key, 60, TimeUnit.SECONDS);
        return ReturnData.builder().data(key).build();
    }

    /**
     * 服务端校验
     *
     * @param confirm
     * @return
     */
    @GetMapping(value = "confirm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ReturnData<?> validateCode(@RequestParam(name = "confirm") String confirm) {
        if (captchaRepository.opsForValue().get(confirm) != null) {
            return ReturnData.builder().status(ReturnData.FAIL).message("校验失败").build();
        }
        return ReturnData.builder().data("校验成功").build();
    }


}

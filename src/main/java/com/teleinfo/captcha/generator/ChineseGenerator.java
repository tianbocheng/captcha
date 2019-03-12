package com.teleinfo.captcha.generator;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 中文汉字随机生成
 *
 * @author TBC
 */
public class ChineseGenerator implements CodeGenerator {

    @Override
    public String generate() {
        Integer hightPos = (176 + RandomUtil.randomInt(39));
        Integer lowPos = (161 + RandomUtil.randomInt(93));
        byte[] b = new byte[2];
        b[0] = hightPos.byteValue();
        b[1] = lowPos.byteValue();
        return StrUtil.str(b, CharsetUtil.charset("GB2312"));
    }

    @Override
    public int getLength() {
        return 1;
    }
}

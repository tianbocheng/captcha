package cn.teleinfo.platform.captcha.model;

import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ImageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.teleinfo.platform.captcha.generator.ChineseGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.xml.transform.Source;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

/**
 * 点击类型的验证码
 *
 * @author TBC
 */
@Slf4j
@Data
@NoArgsConstructor
public class ClickCaptcha implements ICaptcha {

    /**
     * 图片的宽度
     */
    @JsonIgnore
    protected int width = 300;
    /**
     * 图片的高度
     */
    @JsonIgnore
    protected int height = 150;
    /**
     * 验证码元素个数
     */
    @JsonIgnore
    protected int codeCount = 5;
    /**
     * 验证码干扰元素个数
     */
    @JsonIgnore
    protected int interfereCount = 0;
    /**
     * 验证码生成器
     */
    @JsonIgnore
    protected CodeGenerator generator;
    /**
     * 文字验证码
     */
    protected String code = "";
    /**
     * 验证码要校验的位置信息
     */
    @JsonIgnore
    protected List<Double[]> srand = new ArrayList<>();
    /**
     * 图片文件base64
     */
    protected String imageSource;

    public ClickCaptcha(int codeCount) {
        this(300, 150, codeCount, 0);
    }

    public ClickCaptcha(int codeCount, int interfereCount) {
        this(300, 150, codeCount, interfereCount);
    }

    public ClickCaptcha(int width, int height, int codeCount, int interfereCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.generator = new ChineseGenerator();
        if (codeCount > interfereCount) {
            this.interfereCount = interfereCount;
        }
        createCode();
    }

    @Override
    public void createCode() {
        try {
            // 图片背景
            List<String> images = FileUtil.listFileNames("image");
            Image big = ImageIO.read(ResourceUtils.getFile("classpath:image/" + RandomUtil.randomEle(images))).getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 获得画笔
            Graphics2D g = image.createGraphics();
            g.drawImage(big, 0, 0, null);
            // 将图片按照元素个数等分，字体边缘计算
            Integer avg = width / codeCount;
            Double side = avg * 0.2;

            // 生成图片
            Map<String, Double[]> allRand = new HashMap<>(codeCount);
            for (int i = 0; i < codeCount; i++) {
                String rand = generator.generate();
                // 设置每个汉字的字体大小，颜色，高度，旋转
                Double fontSize = (avg - side) * RandomUtil.randomDouble(0.6d, 0.8d);
                g.setColor(ImageUtil.randomColor());
                Double x = fontSize + avg * i;
                Double y = 0d;
                if (x / width > 0.6) {
                    y = RandomUtil.randomDouble(height / 2, (height - 10));
                } else {
                    y = RandomUtil.randomDouble(height / 5, (height - 10));
                }
                List<String> fonts = FileUtil.listFileNames("font");
                Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getFile("classpath:font/" + RandomUtil.randomEle(fonts)));
                font = font.deriveFont(Font.BOLD, fontSize.intValue());
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.PI * RandomUtil.randomDouble(-0.6, 0.3), 0, 0);
                Font rotatedFont = font.deriveFont(affineTransform);
                g.setFont(rotatedFont);
                g.drawString(rand, x.intValue(), y.intValue());

                allRand.put(rand, new Double[]{x, y, fontSize});
            }
            g.dispose();
            // 将图片base64
            this.imageSource = "data:image/jpg;base64," + ImageUtil.toBase64(image, "jpg");

            // 随机的key
            Set<String> realList = RandomUtil.randomEleSet(allRand.keySet(), codeCount - interfereCount);
            for (String key : realList) {
                // 记录
                code += key;
                srand.add(allRand.get(key));
            }
            log.info("校验的验证码******" + JSONUtil.toJsonStr(srand));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public boolean verify(String userInputCode) {
        boolean validate = JSONUtil.isJsonArray(userInputCode);
        if (!validate) {
            log.debug("错误的入参******" + userInputCode);
            return false;
        }
        JSONArray locationReq = JSONUtil.parseArray(userInputCode);
        log.info("正确的验证码******" + JSONUtil.toJsonStr(srand));
        log.info("请求的验证码******" + JSONUtil.toJsonStr(userInputCode));

        if (srand.size() != locationReq.size()) {
            log.debug("传入位置数量{}与校验位置数量{}不符******", locationReq.size(), srand.size());
            return false;
        }

        // 判断坐标
        for (int i = 0; i < srand.size(); i++) {
            Double[] item = srand.get(i);
            JSONArray itemReq = locationReq.getJSONArray(i);
            double fontSize = item[2];
            double mistake = Math.hypot(item[0] - itemReq.getDouble(0),
                    item[1] - itemReq.getDouble(1));
            if (mistake > (fontSize * 1.5)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(OutputStream out) {
        throw new IllegalAccessError("不允许的操作");
    }
}

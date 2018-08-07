package com.teleinfo.captcha.validate.click;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.teleinfo.captcha.validate.ValidateCode;
import com.teleinfo.captcha.validate.ValidateCodeGenerator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ImageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 
 * @author TBC
 *
 */
@Component("clickValidateCodeGenerator")
public class ClickCodeGenerator implements ValidateCodeGenerator {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.teleinfo.security.core.validate.code.ValidateCodeGenerator#generate(org.
	 * springframework.web.context.request.ServletWebRequest)
	 */
	@Override
	public ValidateCode generate(ServletWebRequest request) throws Exception {
		String clientId = ServletRequestUtils.getStringParameter(request.getRequest(), "clientId", null);
		int width = ServletRequestUtils.getIntParameter(request.getRequest(), "width", 300);
		int height = width / 2;
		int length = ServletRequestUtils.getIntParameter(request.getRequest(), "length", 5);
		Long expireTime = ServletRequestUtils.getLongParameter(request.getRequest(), "expireTime", 600l);

		if (width < 260 || width > 400) {
			throw new RuntimeException("宽度不符合规则");
		}

		if (length < 3) {
			throw new RuntimeException("长度不符合规则");
		}

		// 背景
		List<String> images = FileUtil.listFileNames("image");

		Image big = ImageIO.read(ResourceUtils.getFile("classpath:image/" + RandomUtil.randomEle(images)))
				.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.drawImage(big, 0, 0, null);

		// 设置字体&颜色&旋转
		Map<String, JSONArray> sRand = new HashMap<>();
		Integer avg = width / length;
		Double side = avg * 0.2;

		for (int i = 0; sRand.size() < length; i++) {
			String rand = getRandomChar();
			Double fontSize = (avg - side) * RandomUtil.randomDouble(0.6d, 0.8d);

			g.setColor(ImageUtil.randomColor());
			Double x = fontSize + avg * i;

			Double y = 0d;
			if (x / width > 0.6) {
				y = RandomUtil.randomDouble(height / 2, (height - 10));
			} else {
				y = RandomUtil.randomDouble(height / 5, (height - 10));
			}

			// 设置字体
			List<String> fonts = FileUtil.listFileNames("font");
			Font font = Font.createFont(Font.TRUETYPE_FONT,
					ResourceUtils.getFile("classpath:font/" + RandomUtil.randomEle(fonts)));
			font = font.deriveFont(Font.BOLD, fontSize.intValue());

			AffineTransform affineTransform = new AffineTransform();
			// 旋转
			affineTransform.rotate(Math.PI * RandomUtil.randomDouble(-0.6, 0.3), 0, 0);
			Font rotatedFont = font.deriveFont(affineTransform);
			// TODO 字体扭曲（）
			// shear(g, width, height, Color.white);

			g.setFont(rotatedFont);
			// g.rotate(RandomUtil.randomInt(30, 150) * Math.PI / 180);
			g.drawString(rand, x.intValue(), y.intValue());

			sRand.put(rand, new JSONArray().put(x).put(y).put(fontSize));
		}
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f)); //
		// 透明度设置开始          

		g.dispose();

		// 随机的key
		Set<String> realList = RandomUtil.randomEleSet(sRand.keySet(), length - 2);

		JSONObject realLocation = new JSONObject();

		for (String key : realList) {
			realLocation.put(key, sRand.get(key));
		}

		logger.info("生成的验证码******" + ArrayUtil.toString(sRand));

		String token = SecureUtil.simpleUUID();

		return new ValidateCode(clientId, token, realLocation.toString(), expireTime, image);
	}

	/**
	 * 生成随机汉字
	 * 
	 * @return
	 */
	public static String getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str;
	}
}

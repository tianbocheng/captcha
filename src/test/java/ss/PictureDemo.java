package ss;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;

public class PictureDemo {
	/**
	 * 将图片进行合成
	 * 
	 * @param bigPath
	 *            主图图片路径
	 * @param smallPath
	 *            商品图片路径
	 * @param erweimaPath
	 *            二维码图片路径
	 */
	public static final void overlapImage(String bigPath, String smallPath, String erweimaPath) {
		try {
			BufferedImage big = ImageIO.read(new File(bigPath));
			URL url = new URL("http://mjj.dapail.com/app/moneyUrl.png");
			BufferedImage small = ImageIO.read(url.openStream());
			BufferedImage erweima = ImageIO.read(new File(erweimaPath));
			/*
			 * int width=2015; int height=1136;
			 */
			int width = 400;
			int height = 600;
			Image image = big.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			Graphics2D g = bufferedImage2.createGraphics();

			/*
			 * int x = 707; int y = 268; int x1 = 684; int y1 = 245;
			 */

			int x = 200;
			int y = 250;

			int x1 = 200;
			int y1 = 250;

			g.drawImage(image, 0, 0, null);
			// g.drawImage(small,x1-200, y1-250, 400, 200, null);
			// 红包
			g.drawImage(small, x, y + 100, 50, 50, null);

			// g.drawImage(erweima, x1-575, y1+100, 596, 596, null);
			// 二维码
			g.drawImage(erweima, x1 - 100, y1 + 80, 200, 200, null);

			Font font = new Font("黑体", Font.PLAIN, 25);
			g.setFont(font);
			g.setPaint(Color.DARK_GRAY);
			int numWidth = x - 70;
			int numHright = y + 50;
			int num = 0;
			// g.drawString("商品名称:" , numWidth,numHright);
			// g.drawString("测试棋牌室" , numWidth,numHright);

			String name = "测试测试测试测试试试";

			if (name.length() == 2) {
				numWidth = x - 20;
			} else if (name.length() == 3) {
				numWidth = x - 35;
			} else if (name.length() == 4) {
				numWidth = x - 50;
			} else if (name.length() == 5) {
				numWidth = x - 65;
			} else if (name.length() == 6) {
				numWidth = x - 75;
			} else if (name.length() == 7) {
				numWidth = x - 90;
			} else if (name.length() == 8) {
				numWidth = x - 105;
			} else if (name.length() == 9) {
				numWidth = x - 115;
			} else if (name.length() == 10) {
				numWidth = x - 130;
			} else if (name.length() == 11) {
				numWidth = x - 145;
			} else if (name.length() == 12) {
				numWidth = x - 160;
			}

			g.drawString(name, numWidth, numHright);

			/*
			 * g.setPaint(Color.DARK_GRAY); Font font1=new Font("宋体",Font.BOLD , 15);
			 * g.setFont(font1); numWidth=numWidth-25; g.drawString("江苏美联信息科技有限公司" ,
			 * numWidth,numHright+280);
			 */
			/*
			 * num += 50; Font font2=new Font("宋体",Font.PLAIN , 40); g.setFont(font2);
			 * g.setPaint(Color.DARK_GRAY); g.drawString("原产地:", numWidth, numHright+num);
			 * num += 50; g.drawString("配送方式:",numWidth, numHright+num);
			 */
			g.dispose();
			ImageIO.write(bufferedImage2, "jpg", new File("C:/Users/TBC/Desktop/TestImage/4.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void main() {
		overlapImage("C:/Users/TBC/Desktop/TestImage/bg.png", "C:/Users/TBC/Desktop/TestImage/2.png",
				"C:/Users/TBC/Desktop/TestImage/2.png");
	}
//
//	@Test
//	public void main1() throws IOException {
//		Image big = ImageIO.read(new File("E:\\WorkSpace\\STS_WorkSpace\\TBC_Project\\teleinfo-captcha-center\\src\\main\\resources\\image\\1.jpg")).getScaledInstance(10, 50, Image.SCALE_SMOOTH);
//
//	}

}
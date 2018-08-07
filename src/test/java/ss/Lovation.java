package ss;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Rectangle;

import org.junit.jupiter.api.Test;

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

class Lovation {

	@Test
	void test() {
		String code = "{\r\n" + "	\"丰\": [153.10539400581018,\r\n" + "	113.6381401775259,\r\n"
				+ "	33.10539400581018],\r\n" + "	\"脖\": [29.481488392702605,\r\n" + "	131.17595830590625,\r\n"
				+ "	29.481488392702605],\r\n" + "	\"橇\": [217.79956959984202,\r\n" + "	105.43749790166572,\r\n"
				+ "	37.79956959984203]\r\n" + "}";

		String codeInRequest = "{\"1\":[166,94],\"2\":[46,122],\"3\":[241,106]}";
		System.out.println(validateLocation(code, codeInRequest));
	}

	private boolean validateLocation(String code, String codeInRequest) {
		JSONObject codeObj = new JSONObject(code);
		JSONObject codeReqObj = new JSONObject(codeInRequest);

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

			if (mistake > (fontSize * 0.8)) {
				return false;
			}
		}
		return true;
	}

}

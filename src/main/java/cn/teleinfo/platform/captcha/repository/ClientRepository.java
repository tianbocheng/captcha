package cn.teleinfo.platform.captcha.repository;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author TBC
 */
@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "captcha")
public class ClientRepository {

    private HashMap<String, String> clients = new HashMap<>();

    /**
     * 校验是否合法
     *
     * @param clientId
     * @param randomStr
     * @param token
     * @return 错误信息
     */
    public String qualified(String clientId, String randomStr, String token) {
        if (!clients.containsKey(clientId)) {
            log.warn("clientId = {} 不存在！" + clientId);
            return "非法用户！";
        }
        // 验证摘要算法
        if (StrUtil.equals(DigestUtil.sha256Hex(clients.get(clientId) + randomStr), token)) {
            log.debug("clientId = {} 验证成功" + clientId);
            return null;
        }
        return "身份验证错误！";

    }


}

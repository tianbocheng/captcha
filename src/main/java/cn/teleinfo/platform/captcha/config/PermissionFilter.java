package cn.teleinfo.platform.captcha.config;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import cn.teleinfo.platform.captcha.repository.ClientRepository;
import cn.teleinfo.platform.captcha.common.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 校验权限
 *
 * @author TBC
 */
@Order(1)
@WebFilter(filterName = "clientFilter", urlPatterns = {"/code/**"})
public class PermissionFilter implements Filter {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * 过滤器-初始化
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * 过滤器
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Map<String, String> param = ServletUtil.getParamMap(request);

        if (!param.containsKey("clientId") || !param.containsKey("randomStr") || !param.containsKey("token")) {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JSONUtil.toJsonStr(ReturnData.builder().status(ReturnData.FAIL).message("请求参数异常").build()));
            return;
        }

        String clientId = param.get("clientId");
        String randomStr = param.get("randomStr");
        String token = param.get("token");

        String error = clientRepository.qualified(clientId, randomStr, token);

        if (error != null) {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JSONUtil.toJsonStr(ReturnData.builder().status(ReturnData.FAIL).message(error).build()));
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    @Override
    public void destroy() {
    }
}
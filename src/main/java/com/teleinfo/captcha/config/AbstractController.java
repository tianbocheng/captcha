package com.teleinfo.captcha.config;

import cn.hutool.json.JSONUtil;
import com.teleinfo.captcha.common.ReturnData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通用异常处理
 *
 * @author TBC
 */
@Slf4j
@ControllerAdvice
public abstract class AbstractController {

    /**
     * 请求参数缺失
     *
     * @param request
     * @param response
     * @param ex
     * @return
     * @throws Exception
     * @Autor TBC
     * @Date 2017年8月17日
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected void missingServletRequestParameterException(HttpServletRequest request, HttpServletResponse response,
                                                           Throwable ex) throws Exception {
        ex.printStackTrace();
        log.error(ex.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(ReturnData.builder().status(ReturnData.ERROR).message("请求参数缺失！").build()));
    }

    /**
     * 上传文件超限
     *
     * @param request
     * @param response
     * @param ex
     * @throws Exception
     */
    @ExceptionHandler(MultipartException.class)
    protected void maxUploadSizeExceededException(HttpServletRequest request, HttpServletResponse response,
                                                  Throwable ex) throws Exception {
        ex.printStackTrace();
        log.error(ex.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(ReturnData.builder().status(ReturnData.ERROR).message("上传文件超限！").build()));
    }

    /**
     * 通用的异常场处理
     *
     * @param request
     * @param response
     * @param ex
     * @throws IOException
     * @Autor TBC
     * @Date 2017年6月23日
     */
    @ExceptionHandler
    protected void handleControllerException(HttpServletRequest request, HttpServletResponse response, Throwable ex)
            throws Exception {
        ex.printStackTrace();
        log.error(ex.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(ReturnData.builder().status(ReturnData.ERROR).message("系统异常！").build()));
    }

}

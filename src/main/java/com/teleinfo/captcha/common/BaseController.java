package com.teleinfo.captcha.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public abstract class BaseController {

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
	protected void MissingServletRequestParameterException(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws Exception {
		ex.printStackTrace();
		log.error(ex.getMessage());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(JSONUtil.toJsonStr(new ReturnData<String>(ReturnData.ERROR, "请求参数缺失！", null)));
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
	protected void MaxUploadSizeExceededException(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws Exception {
		ex.printStackTrace();
		log.error(ex.getMessage());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(JSONUtil.toJsonStr(new ReturnData<String>(ReturnData.ERROR, "上传文件超限！", null)));
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
		response.getWriter().write(JSONUtil.toJsonStr(new ReturnData<String>(ReturnData.ERROR, "系统异常！", null)));
	}

}

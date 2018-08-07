package com.teleinfo.captcha;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TeleinfoCaptchaApplication {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(TeleinfoCaptchaApplication.class).web(true).run(args);
	}
}

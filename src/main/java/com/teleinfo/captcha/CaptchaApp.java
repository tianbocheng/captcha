package com.teleinfo.captcha;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author TBC
 */

@ServletComponentScan
@SpringBootApplication
public class CaptchaApp {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaApp.class, args);
    }

}

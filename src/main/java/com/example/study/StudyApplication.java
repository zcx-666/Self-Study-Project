package com.example.study;

import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableSwagger2Doc
@Slf4j
public class StudyApplication  extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

	// 注意这里要指向原先用main方法执行的Application启动类
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(StudyApplication.class);
	}
}

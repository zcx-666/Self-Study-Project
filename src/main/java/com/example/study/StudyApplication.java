package com.example.study;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwagger2Doc
public class StudyApplication {
	/*TODO: logout
	* TODO: reserve a table
	* TODO: unsubscribe a table
	* TODO: look up reserve table
	* TODO: overdue reserve
	* TODO: have a date
	* TODO: error code
	* TODO: sql schedule
	* TODO: cookie overdue
	*  */

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

}

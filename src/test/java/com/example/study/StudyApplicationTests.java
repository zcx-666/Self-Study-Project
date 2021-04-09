package com.example.study;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@SpringBootTest
@Slf4j
class StudyApplicationTests {

	@Test
	void contextLoads() {
		Student student = new Student();
		List<Student> students = new ArrayList<>();
		System.out.println(students);
		students.add(student);
		students.add(student);
		students.add(student);
		System.out.println(students);
		System.out.println(student);
		log.error("异常数据:{}",student);
	}

	@Data
	class Student{
		private String name = "zcx";
		private Integer id = 12;
	}
}

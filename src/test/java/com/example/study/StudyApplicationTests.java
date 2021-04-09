package com.example.study;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
		log.error("异常数据:{},{}",student,student);
		Long a = 10000L;
		int s = 12800;
		Integer sb = null;
		System.out.println(sb);
	}

	@Data
	class Student{
		private String name = "zcx";
		private Integer id = 12;
	}
}

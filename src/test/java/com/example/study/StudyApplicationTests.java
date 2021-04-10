package com.example.study;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
		log.error("异常数据:{},{}",student,student);
		Student a = new Student();
		Student b = new Student();
		System.out.println(a.getName() == b.getName());
		a.setName("123321");
		b.setName("123321");
		System.out.println(a.getName() == b.getName());
		System.out.println(a == b);

		String s1 = new String("java");
		String s2 = new String("java");

		System.out.println(s1==s2);
	}

	@Data
	class Student{
		private String name = "zcx";
		private Integer id = 12;
	}
}

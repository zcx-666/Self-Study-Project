package com.example.study;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
		log.error("异常数据:{},{}",student,student);
		Student a = new Student();
		Student b = new Student();
		System.out.println(a.getName() == b.getName());
		a.setName("123321");
		b.setName("123321");
		System.out.println(a.getName() == b.getName());
		System.out.println(a == b);
	}

	@Test
	void pp(){
		Date t = new Date();
		Timestamp a = new Timestamp(t.getTime());
		System.out.println(a);
		t.setMinutes(t.getMinutes() - 1);
		Timestamp b = new Timestamp(t.getTime());
		System.out.println(b);
		System.out.println(a.getTime() - b.getTime());
		Long x = a.getTime();
		Integer s = x.intValue();
		System.out.println(x);
		System.out.println(s);
		System.out.println(Integer.MAX_VALUE);


	}

	@Data
	class Student{
		private String name = "zcx";
		private Integer id = 12;
	}

	@Data
	class People extends Student{
		private String name;
		private Integer age = 12;
		/*public People(){
			super();
		}*/
	}
}

package com.example.study;


import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@Data
@Slf4j
//@SpringBootTest
class StudyApplicationTests {
    private static final Integer MIN_RESERVE_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("min_reserve_time"));
    private static final Integer WORK_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_hour"));
    private static final Integer WORK_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_minute"));
    private static final Integer CLOSING_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_hour"));
    private static final Integer CLOSING_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_minute"));

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
        log.error("异常数据:{},{}", student, student);
        Student a = new Student();
        Student b = new Student();
        System.out.println(a.getName() == b.getName());
        a.setName("123321");
        b.setName("123321");
        System.out.println(a.getName() == b.getName());
        System.out.println(a == b);
    }

    @Test
    void pp() {
        User a = new User();
        User b = new User();
        a.setOpenid("123");
        a.setAvatar("123123");
        System.out.println(a);
        System.out.println(b);
        b.copyUser(a);
        System.out.println(b);
    }





    @Data
    class Student {
        public static final int DAY = 0;
        private String name = "zcx";
        private Integer id = 12;
    }

    @Data
    class People extends Student {
        private String name;
        private Integer age = 12;
		/*public People(){
			super();
		}*/
    }
}

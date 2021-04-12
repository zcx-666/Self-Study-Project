package com.example.study;


import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@Data
@Slf4j
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
        Timestamp a = new Timestamp(new Date().getTime());
        /*System.out.println(a);
        a.setDate(a.getDate() + 1);*/
        System.out.println(a);
        a.setHours(23);
        System.out.println(a);
        a.setMinutes(59);
        System.out.println(a);
        a.setSeconds(59);
        System.out.println(a);

    }

    @Test
    void judgeTime(){
        Date s = new Date("2021/4/12 21:1:0");
        Date e = new Date("2021/4/12 22:2:0");
        Reserve reserve = new Reserve();
        reserve.setReserve_start(new Timestamp(s.getTime()));
        reserve.setReserve_end(new Timestamp(e.getTime()));
        Integer code = judgeReserveTime(reserve, null, null);
        System.out.println(code);
    }

    public Integer judgeReserveTime(Reserve reserve, User user, Integer code) {
        Timestamp start = reserve.getReserve_start();
        Timestamp end = reserve.getReserve_end();
        if (-1 != start.compareTo(end)) {
            return -8; // 开始时间必须小于结束时间
        }
        if (end.getTime() - start.getTime() < MIN_RESERVE_MINUTE * 60 * 1000) {
            return -32;
        }
        if (start.getYear() != end.getYear()
                || start.getMonth() != end.getMonth()
                || start.getDate() != end.getDate()) {
            return -19; // 预定必须在同一天
        }
        Timestamp now = new Timestamp(new Date().getTime());
        now.setMinutes(now.getMinutes() - 5);
        if (start.compareTo(now) == -1) {
            // 只能预约现在五分钟前到未来的时间
            return -20;
        }
        if (start.getTime() - now.getTime() > 7*24*60*60*1000) {
            return -34;
        }
        Date work = new Date(start.getTime()); // 获取预定的同一天
        work.setHours(WORK_HOUR);
        work.setMinutes(WORK_MINUTE - 1);
        work.setSeconds(0);
        Date close = new Date(start.getTime()); // 获取预定的同一天
        close.setHours(CLOSING_HOUR);
        close.setMinutes(CLOSING_MINUTE + 1);
        close.setSeconds(0);
        Timestamp w = new Timestamp(work.getTime());
        Timestamp c = new Timestamp(close.getTime());
        if (!(start.compareTo(w) >= 0 && end.compareTo(c) <= 0)) {
            return -33;
        }
        if (user != null) {
            if (code == null) {
                return -99;
            }
            if (code == 0) {
                // 使用时长
                Long t = (start.getTime() - end.getTime()) / 1000; // 预定时长
                Integer vip = user.getVip_time();
                if (t > vip) {
                    return -22; // VIP时长不足
                }
                user.setUser_status(3);
            } else if (code == 1) {
                // 使用天卡
                if (user.getVip_daypass() < 1) {
                    return -22; // VIP时长不足
                }
                user.setUser_status(4);
            } else {
                return -11;
            }
        }
        return 0;
    }

    @Data
    class Student {
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

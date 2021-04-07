package com.example.study.mapper;

import com.example.study.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_form (openid, session_key, avatar, cookie, vip_daypass, vip_time, user_status, isadmin) VALUE (#{openid}, #{session_key},#{avatar},#{cookie},#{vip_daypass},#{vip_time}, #{user_status}, #{isadmin})")
//    @Insert("INSERT INTO user_form () VALUE (#{user})")

    void insertNewUser(User user);

    @Select("SELECT * FROM user_form WHERE cookie=#{cookie}")
    User selectUserByCookie(@Param("cookie") String cookie);

    @Update("UPDATE user_form SET cookie=#{cookie} WHERE openid=#{openid}")
    void updateCookie(@Param("openid") String openid, @Param("cookie") String cookie);

    @Update("UPDATE user_form SET session_key=#{session_key} WHERE openid=#{openid}")
    void updateSession_key(@Param("openid") String openid, @Param("session_key") String session_key);

    @Select("SELECT * FROM user_form WHERE openid=#{openid}")
    User selectUserByOpenId(@Param("openid") String openid);

    @Update("UPDATE user_form SET user_status=#{user_status} WHERE openid=#{openid}")
    void updateUserReserveState(User user);

    @Update("UPDATE user_form SET vip_daypass=#{vip_daypass}, vip_time=#{vip_time} WHERE openid=#{openid}")
    void updateUserVIPTime(User user);

    @Insert("INSERT INTO recharge_record_form (wechat_pay_id, vip_daypass, vip_time, openid,  create_time) VALUE (#{wechat_pay_id}, #{vip_daypass}, #{vip_time}, #{openid}, current_timestamp)")
    void insertVIPRecord(@Param("wechat_pay_id") String wechat_pay_id,
                         @Param("vip_daypass") Integer vip_daypass,
                         @Param("vip_time") Integer vip_time,
                         @Param("openid") String openid);
}

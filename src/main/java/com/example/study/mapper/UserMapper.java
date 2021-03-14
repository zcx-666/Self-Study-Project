package com.example.study.mapper;

import com.example.study.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_form (openid, is_reserve, session_key, avatar, cookie, vip_start, vip_end) VALUE (#{openid}, #{is_reserve},#{session_key},#{avatar},#{cookie},#{vip_start},#{vip_end})")
    void insertNewUser(User user);

    @Select("SELECT * FROM user_form WHERE cookie=#{cookie}")
    User selectUserByCookie(@Param("cookie") String cookie);

    @Update("UPDATE user_form SET cookie=#{cookie} WHERE openid=#{openid}")
    void updateCookie(@Param("openid") String openid, @Param("cookie") String cookie);

    @Update("UPDATE user_form SET session_key=#{session_key} WHERE openid=#{openid}")
    void updateSession_key(@Param("openid") String openid, @Param("session_key") String session_key);

    @Select("SELECT * FROM user_form WHERE openid=#{openid}")
    User selectUserByOpenId(@Param("openid") String openid);

    @Update("UPDATE user_form SET is_reserve=#{is_reserve} WHERE openid=#{openid}")
    void updateUserReserveState(@Param("openid") String openid,
                                @Param("is_reserve") Boolean is_reserve);

    @Update("UPDATE user_form SET vip_start=#{vip_start}, vip_end=#{vip_end} WHERE openid=#{openid}")
    void updateUserVIPTime(User user);

    @Insert("INSERT INTO recharge_record_form (openid, vip_start, vip_end, create_time) VALUE (#{openid}, #{vip_start}, #{vip_end}, current_timestamp)")
    void insertVIPRecord(User user);
}

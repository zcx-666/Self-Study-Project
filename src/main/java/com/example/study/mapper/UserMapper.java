package com.example.study.mapper;

import com.example.study.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_form (openid, is_reserve, session_key, avatar, cookie, vip) VALUE (#{openid}, #{is_reserve},#{session_key},#{avatar},#{cookie},#{vip})")
    void insertNewUser(@Param("openid") String openid,
                       @Param("is_reserve") Boolean is_reserve,
                       @Param("session_key") String session_key,
                       @Param("avatar") String avatar,
                       @Param("cookie") String cookie,
                       @Param("vip") Integer vip);

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
}

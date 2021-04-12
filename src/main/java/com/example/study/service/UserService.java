package com.example.study.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.study.mapper.UserMapper;
import com.example.study.model.Response;
import com.example.study.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

@Service
@Slf4j
public class UserService {

    @Resource
    private UserMapper userMapper;

    private final String cookie_name = ResourceBundle.getBundle("string").getString("cookie_name");

    private static String encryption(@NotNull String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(str.getBytes());
            return new BigInteger(messageDigest.digest()).toString(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User selectUserByOpenId(String openid) {
        return userMapper.selectUserByOpenId(openid);
    }

    public User selectUserByCookie(@NotNull HttpServletRequest servletRequest) {
        User user = null;
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length <= 0)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookie_name)) {
                user = userMapper.selectUserByCookie(cookie.getValue());
            }
        }
        return user;
    }

    public JSONObject GetOpenidAndSession(String path, String code) throws Exception {
        // String path = "https://api.weixin.qq.com/sns/jscode2session";
        ResourceBundle res = ResourceBundle.getBundle("string");
        String appid = res.getString("appid");
        String secret = res.getString("appsecret");
        // https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        path += "?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            String msg = new String(bytes);
            return JSON.parseObject(msg);
        }
        return null;
    }


    public Boolean insertNewUser(User user) {
        try {
            userMapper.insertNewUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean deleteUserCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse, String openid) {
        Cookie[] cookies = servletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookie_name)) {
                cookie.setMaxAge(0);
                servletResponse.addCookie(cookie);
                break;
            }
        }
        userMapper.updateCookie(openid, "");
        return true;
    }

    /*更新用户的cookie，并把cookie放入返回头*/
    public Boolean updateCookie(HttpServletResponse servletResponse, String openid) {
        String cookieStr = encryption(openid + System.currentTimeMillis());
        Cookie cookie = new Cookie(cookie_name, cookieStr);
        cookie.setPath("/");
        servletResponse.addCookie(cookie);
        userMapper.updateCookie(openid, cookieStr);
        return true;
    }

    public Boolean updateSession_key(String openid, String session_key) {
        userMapper.updateSession_key(openid, session_key);
        return true;
    }


    public void updateUserState(User user) {
        userMapper.updateUserReserveState(user);
    }

    public void updateUserIsAdmin(User user) {
        userMapper.updateIsAdmin(user);
    }

    public void rechargeVIP(User user, String wechat_pay_id, Integer vipDay, Integer vipTime) {
        userMapper.updateUserVIPTime(user);
        //#{wechat_pay_id}, #{vip_daypass}, #{vip_time}, #{openid}
        userMapper.insertVIPRecord(wechat_pay_id, vipDay, vipTime, user.getOpenid());
    }

    public Integer judgeUser(HttpServletRequest request, User user) {
        User user1 = selectUserByCookie(request);
        if (user1 == null) {
            Response.fail(-1, request.getCookies());
            return -1;
        }
        user.copyUser(user1);
        return 0;
    }

    public Integer judgeAdmin(HttpServletRequest request, User admin) {
        Integer code = judgeUser(request, admin);
        if (code != 0) {
            return code;
        }
        if (!admin.getIsadmin()) {
            return -12;
        }
        return 0;
    }

    public void updateUserStateAndVIPTime(User user) {
        userMapper.updateUserReserveStateAndVIPTime(user);
    }
}

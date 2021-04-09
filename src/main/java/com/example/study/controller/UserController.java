package com.example.study.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.User;
import com.example.study.model.request.BuyVipDayRequest;
import com.example.study.model.request.LoginRequest;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/*TODO: 上下机
*  TODO: 预约过期
*   TODO: 自动强制结束使用*/
@RestController
@Api(tags = "用户接口")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Response<User> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        User user;
        String url = "https://api.weixin.qq.com/sns/jscode2session"; // code换openid和session_key
        JSONObject res;
        try {
            res = userService.GetOpenidAndSession(url, request.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(-99, e.toString());
        }
        if (res == null) {
            return Response.fail(-3);
        }
        if (res.getString("errcode") != null) {
            // 服务器到微信服务器出问题
            return Response.fail(res.getInteger("errcode"), res.getString("errmsg"));
        }
        String Openid = res.getString("openid");
        String Session_key = res.getString("session_key");
        /*
         * 1. 检查 openid 是否存在
         * 2. 如果不存在就添加一条新记录
         * 3. 添加新的cookie/session_key
         * */
        user = userService.selectUserByOpenId(Openid);
        if (user == null) {
            user = new User();
            user.setOpenid(Openid);
            // TODO: user.setAvatar()
            if (!userService.insertNewUser(user)) {
                return Response.fail(-2);
            }
        }
        user.setSession_key(Session_key);
        userService.updateCookie(servletResponse, user.getOpenid());
        userService.updateSession_key(user.getOpenid(), user.getSession_key());
        return Response.success(user);
    }

    @PostMapping("/logout")
    public Response<User> logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        User user;
        user = userService.selectUserByCookie(servletRequest);
        if (user == null) {
            return Response.fail(-1);
        }
        userService.deleteUserCookie(servletRequest, servletResponse, user.getOpenid());
        return Response.success(user);
    }

    @PostMapping("/rechargeVIP")
    public Response<User> rechargeVIP(@RequestBody BuyVipDayRequest buyVipDayRequest, HttpServletRequest servletRequest) {
        User user = userService.selectUserByCookie(servletRequest);
        if(user == null){
            return Response.fail(-1);
        }
        user.setVip_daypass(user.getVip_daypass() + buyVipDayRequest.getDay());
        user.setVip_time(user.getVip_time() + buyVipDayRequest.getTime());
        userService.rechargeVIP(user, buyVipDayRequest.getWechat_pay_id(), buyVipDayRequest.getDay(), buyVipDayRequest.getTime());
        return Response.success(user);
    }

}

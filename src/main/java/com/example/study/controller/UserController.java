package com.example.study.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.User;
import com.example.study.model.request.BuyVipDayRequest;
import com.example.study.model.request.CallBackRequest;
import com.example.study.model.request.LoginRequest;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if(errRes != null){
            return errRes;
        }
        userService.deleteUserCookie(servletRequest, servletResponse, user.getOpenid());
        return Response.success(user);
    }

    @PostMapping("/rechargeVIP")
    public Response<User> rechargeVIP(@RequestBody BuyVipDayRequest request, HttpServletRequest servletRequest) {
        if (true) // TODO: 用户充值
            return Response.fail(-99, "此接口未开放");
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if(errRes != null){
            return errRes;
        }
        user.setVip_daypass(user.getVip_daypass() + request.getDay());
        user.setVip_time(user.getVip_time() + request.getTime());
        if(request.getOverdue_time() != null){
            user.setOverdue_time(request.getOverdue_time());
        }
        if(request.getOverdue_day() != null){
            user.setOverdue_day(request.getOverdue_day());
        }
        userService.rechargeVIP(user, request.getWechat_pay_id(), request.getDay(), request.getTime(), request.getNumber(), request.getOverdue_day(), request.getOverdue_time(), request.getOverdue_number());
        return Response.success(user);
    }

    @GetMapping("/getUserInfoByCookie")
    @ApiOperation(value = "刷新用户信息")
    public Response<User> getUserInfoByCookie(HttpServletRequest request){
        User user = new User();
        Response errRes = userService.judgeUser(request, user);
        if(errRes != null){
            return errRes;
        }
        return Response.success(user);
    }

    //@PostMapping("/callBack")
    public void callBack(@RequestBody CallBackRequest request){
        System.out.println(request);
    }
}

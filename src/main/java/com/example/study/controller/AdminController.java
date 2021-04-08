package com.example.study.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.User;
import com.example.study.model.request.AdminRechargeVipRequest;
import com.example.study.model.request.LoginRequest;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "管理员接口")
public class AdminController {

    @Resource
    private UserService userService;

    @PostMapping("/admin/rechargeVIP")
    public Response<User> rechargeVIP(@RequestBody AdminRechargeVipRequest request, HttpServletRequest servletRequest) {
        // TODO: 管理员怎么知道openid 用户的code生成二维码，管理员扫码获得openid
        User admin = new User();
        Integer code = userService.judgeAdmin(servletRequest, admin);
        if(code != 0){
            return Response.fail(code);
        }
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
//        String Session_key = res.getString("session_key");
        User user = userService.selectUserByOpenId(Openid);
        if(user == null){
            return Response.fail(-16);
        }
        user.setVip_daypass(user.getVip_daypass() + request.getDay());
        user.setVip_time(user.getVip_time() + request.getTime());
        userService.rechargeVIP(user, "admin:" + admin.getOpenid(), request.getDay(), request.getTime());
        return Response.success(user);
    }

    @GetMapping("/admin/loginByOpenid")
    @ApiOperation(value = "登录（测试用）")
    public Response<User> login(@RequestParam("openid") String openid, HttpServletResponse servletResponse){
        User user = userService.selectUserByOpenId(openid);
        if(user == null){
            return Response.fail(-16);
        }
        userService.updateCookie(servletResponse, user.getOpenid());
        return Response.success(user);
    }

    @PostMapping("/admin/giveAuthority")
    public Response<User> giveAuthority(LoginRequest loginRequest, HttpServletRequest request){
        User admin = new User();
        Integer code = userService.judgeAdmin(request, admin);
        if(code != 0){
            return Response.fail(code);
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session"; // code换openid和session_key
        JSONObject res;
        try {
            res = userService.GetOpenidAndSession(url, loginRequest.getCode());
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
//        String Session_key = res.getString("session_key");
        User user = userService.selectUserByOpenId(Openid);
        if(user == null){
            return Response.fail(-16);
        }
        user.setIsadmin(true);
        userService.updateUserState(user);
        return Response.success(user);
    }
}

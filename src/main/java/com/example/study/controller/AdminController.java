package com.example.study.controller;


import com.example.study.model.Response;
import com.example.study.model.entity.User;
import com.example.study.model.request.AdminRechargeVip;
import com.example.study.model.request.BuyVipDayRequest;
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
    public Response<User> rechargeVIP(@RequestBody AdminRechargeVip adminRechargeVip, HttpServletRequest servletRequest) {
        // TODO: 管理员怎么知道openid 用户的code生成二维码，管理员扫码获得openid
        User admin = userService.selectUserByCookie(servletRequest);
        if(admin == null){
            return Response.fail(-1);
        }
        if(!admin.getIsadmin()){
            return Response.fail(-12);
        }
        User user = userService.selectUserByOpenId(adminRechargeVip.getOpenid());
        if(user == null){
            return Response.fail(-16);
        }
        user.setVip_daypass(user.getVip_daypass() + adminRechargeVip.getDay());
        user.setVip_time(user.getVip_time() + adminRechargeVip.getTime());
        userService.rechargeDayVIP(user, adminRechargeVip.getWechat_pay_id(), adminRechargeVip.getDay(), adminRechargeVip.getTime());
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
}

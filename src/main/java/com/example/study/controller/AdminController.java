package com.example.study.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import com.example.study.model.request.AdminRechargeVipRequest;
import com.example.study.model.request.FinishRequest;
import com.example.study.model.request.LoginRequest;
import com.example.study.service.ReserveService;
import com.example.study.service.TableService;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;

@RestController
@Api(tags = "管理员接口")
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private TableService tableService;

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
        user.setVip_daypass(user.getVip_daypass() + request.getVipDay());
        user.setVip_time(user.getVip_time() + request.getVipTime());
        userService.rechargeVIP(user, "admin:" + admin.getOpenid(), request.getVipDay(), request.getVipTime());
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

    @GetMapping("/admin/getOpenidByCookie")
    @ApiOperation(value = "管理员获得自己的Openid")
    public Response<String> getOpenidByCookie(HttpServletRequest request){
        User admin = new User();
        Integer code = userService.judgeAdmin(request, admin);
        if(code != 0){
            return Response.fail(code);
        }else {
            return Response.success(admin.getOpenid());
        }
    }

    @GetMapping("/admin/getOpenidByCode")
    @ApiOperation(value = "管理员通过用户提供的code获得Openid")
    public Response<String> getOpenidByCode(LoginRequest loginRequest, HttpServletRequest request){
        User admin = new User();
        Integer code = userService.judgeAdmin(request, admin);
        if(code != 0){
            return Response.fail(code);
        }else {
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
            return Response.success(Openid);
        }
    }

    @PostMapping("/admin/finishUse")
    public Response<Reserve> finishUse(@RequestBody @Valid FinishRequest finishRequest, HttpServletRequest servletRequest){
        User admin = new User();
        Integer code = userService.judgeUser(servletRequest, admin);
        if (code != 0) {
            return Response.fail(code);
        }

        // 和普通一样
        Reserve reserve = reserveService.searchReserveById(finishRequest.getReserve_id());
        Table table = tableService.selectTableByTableId(reserve.getTable_id());
        User user = userService.selectUserByOpenId(reserve.getOpenid());
        if (reserve == null) {
            return Response.fail(-15);
        }
        if (!reserve.getOpenid().equals(user.getOpenid())) {
            return Response.fail(-28);
        }
        if (reserve.getReserve_status() != 3) {
            return Response.fail(-30);
        }
        if(!table.getIs_using()){
            Response.fail(-31, table);
            return Response.fail(-31);
        }
        table.setIs_using(true);
        Timestamp now = new Timestamp(new Date().getTime());
        reserve.setReserve_end(now);
        reserve.setReserve_status(0);
        if (user.getUser_status() == 1) {
            Long useTime = (reserve.getReserve_end().getTime() - reserve.getReserve_start().getTime()) / 1000;
            Long left = user.getVip_time() - useTime;
            Integer t = left.intValue();
            user.setVip_time(t);
            user.setUser_status(0);
        } else if (user.getUser_status() == 2) {
            user.setVip_daypass(user.getVip_daypass() - 1);
            user.setUser_status(5);
        } else {
            return Response.fail(-29);
        }
        userService.updateUserState(user);
        reserveService.updateReserveStatus(reserve);
        tableService.updateTableReserveState(table);
        return Response.success(reserve);
    }
}

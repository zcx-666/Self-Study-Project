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
    @ApiOperation(value = "管理员充值VIP", notes = "用户把code生成一个二维码，管理员扫描二维码进行充值，可以用来扣除VIP时间")
    public Response<User> rechargeVIP(@RequestBody AdminRechargeVipRequest request, HttpServletRequest servletRequest) {
        User admin = new User();
        Response errRes = userService.judgeAdmin(servletRequest, admin);
        if (errRes != null) {
            return errRes;
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
        if (user == null) {
            return Response.fail(-16);
        }
        if (request.getOverdue_day() != null) {
            user.setOverdue_day(request.getOverdue_day());
        }
        if (request.getOverdue_time() != null) {
            user.setOverdue_time(request.getOverdue_time());
        }
        user.setVip_daypass(user.getVip_daypass() + request.getVipDay());
        user.setVip_time(user.getVip_time() + request.getVipTime());
        userService.rechargeVIP(user, "admin:" + admin.getOpenid(), request.getVipDay(), request.getVipTime(), request.getVipNumber(), request.getOverdue_day(), request.getOverdue_time(), request.getOverdue_number());
        return Response.success(user);
    }

    @GetMapping("/admin/loginByOpenid")
    @ApiOperation(value = "登录（测试用）")
    public Response<User> login(@RequestParam("openid") String openid, HttpServletResponse servletResponse) {
        User user = userService.selectUserByOpenId(openid);
        if (user == null) {
            return Response.fail(-16);
        }
        userService.updateCookie(servletResponse, user.getOpenid());
        return Response.success(user);
    }

    @PostMapping("/admin/giveAuthority")
    public Response<User> giveAuthority(LoginRequest loginRequest, HttpServletRequest request) {
        User admin = new User();
        Response errRes = userService.judgeAdmin(request, admin);
        if (errRes != null) {
            return errRes;
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
        if (user == null) {
            return Response.fail(-16);
        }
        user.setIsadmin(true);
        userService.giveAuthority(user);
        return Response.success(user);
    }

    @GetMapping("/admin/getOpenidByCookie")
    @ApiOperation(value = "管理员获得自己的Openid")
    public Response<String> getOpenidByCookie(HttpServletRequest request) {
        User admin = new User();
        Response errRes = userService.judgeAdmin(request, admin);
        if (errRes != null) {
            return errRes;
        } else {
            return Response.success(admin.getOpenid());
        }
    }

    @GetMapping("/admin/getOpenidByCode")
    @ApiOperation(value = "管理员通过用户提供的code获得Openid")
    public Response<String> getOpenidByCode(LoginRequest loginRequest, HttpServletRequest request) {
        User admin = new User();
        Response errRes = userService.judgeAdmin(request, admin);
        if (errRes != null) {
            return errRes;
        } else {
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
    public Response<Reserve> finishUse(@RequestBody @Valid FinishRequest finishRequest, HttpServletRequest servletRequest) {
        User admin = new User();
        Response errRes = userService.judgeAdmin(servletRequest, admin);
        if (errRes != null) {
            return errRes;
        }

        Reserve reserve = reserveService.searchReserveById(finishRequest.getReserve_id());
        if (reserve == null) {
            return Response.fail(-15); // 订单不存在
        }
        Table table = tableService.selectTableByTableId(reserve.getTable_id());
        User user = userService.selectUserByOpenId(reserve.getOpenid());
        if (reserve.getReserve_status() != Reserve.USING) {
            return Response.fail(-30);
        }
        if (!table.getIs_using()) {
            Response.fail(-31, table);
            return Response.fail(-31);
        }
        table.setIs_using(false);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        reserve.setReserve_end(now);
        reserve.setReserve_status(Reserve.FINISH);
        if (user.getUsing_status() == User.TIME) {
            Long useTime = (reserve.getReserve_end().getTime() - reserve.getReserve_start().getTime()) / 1000 * finishRequest.getPower();
            Long left = user.getVip_time() - useTime;
            int t = left.intValue();
            user.setVip_time(t);
        } else if (user.getUsing_status() == User.DAY) {
            // 使用天卡的话什么都不用管，直接取消就好了，反正肯定已经是生效状态，时间也在使用的时候扣了
        } else if(user.getUsing_status() == User.NUMBER){
            // 使用次卡也不用管，反正次数已经在使用的时候扣过了
        } else {
            return Response.fail(-29);
        }
        user.setUsing_status(User.NONE);
        userService.updateUserStatus(user);
        reserveService.updateReserveStatus(reserve);
        tableService.updateTableReserveState(table);
        return Response.success(reserve);
    }
}

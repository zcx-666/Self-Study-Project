package com.example.study.controller;


import com.example.study.TimeUtils;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.TableSchedule;
import com.example.study.model.entity.User;
import com.example.study.model.request.CancleRequest;
import com.example.study.model.request.ReserveRequest;
import com.example.study.model.request.SearchTableByTimeRequest;
import com.example.study.service.ReserveService;
import com.example.study.service.TableService;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "预定接口")
public class ReserveController {


    @Resource
    private ReserveService reserveService;

    @Resource
    private UserService userService;

    @Resource
    private TableService tableService;

    @PostMapping("/reserve")
    private Response<List<Reserve>> reserveTable(@RequestBody ReserveRequest request, HttpServletRequest servletRequest) {
        /*TODO:开始使用时才扣除天卡
        * TODO:使用结束后扣除时间*/
        User user;
        Table table;
        Reserve reserve_post = new Reserve();
        try {
            reserve_post.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve_post.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.fail(-10);
        }

        user = userService.selectUserByCookie(servletRequest);
        if(user == null){
            return Response.fail(-1); // 未登录
        }
        if(user.getUser_status() != 0){
            return Response.fail(-4);
        }
        Integer code = reserveService.judgeReserveTime(reserve_post, user);
        if(code != 0){
            return Response.fail(code);
        }
        table = tableService.selectTableByTableId(request.getTable_id());
        if(table == null){
            return Response.fail(-5); // 查无此桌
        }
        if(table.getIs_using()){
            return Response.fail(-6); // 正在被使用
        }
        reserve_post.setReserve_status(2);
        reserve_post.setTable_id(table.getTable_id());
        reserve_post.setOpenid(user.getOpenid());
        reserve_post.setCreate_time(new Timestamp(System.currentTimeMillis()));
        reserveService.insertNewReserve(reserve_post);
        log.info("新记录:{}",reserve_post);
        //if(table.getIs_reserve()){ 
        // 如果已经被预定了，查看有效的预定记录，并查看预定时间是否冲突，就是两者的时间是否相交
        List<Reserve> reserves = reserveService.selectConflictingReserve(reserve_post);
        if(reserves.size() > 0){
            log.info("冲突纪录：{}",reserves);
            reserveService.deleteReserveById(reserve_post);
            return Response.fail(-7, reserves); // 冲突
        } else {
            // 无冲突，订单状态2更新为4
            reserveService.updateReserveStatus(reserve_post);
        }
        //}
        if(request.getCode() == 1){
            user.setUser_status(2);
        } else if(request.getCode() == 0){
            user.setUser_status(1);
        } else {
            return Response.fail(-11);
        }
        if(!table.getIs_reserve()){
            table.setIs_reserve(true);
            tableService.updateTableReserveState(table);
        }
        userService.updateUserState(user);
        List<Reserve> l = new ArrayList<>();
        l.add(reserve_post);
        return Response.success(l);
    }

    @PostMapping("/searchTableByTime")
    public Response<List<Table>> searchTableByTime(@RequestBody SearchTableByTimeRequest request){
        Reserve reserve = new Reserve();
        try {
            reserve.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.fail(-10);
        }
        Integer code = reserveService.judgeReserveTime(reserve, null);
        if(code != 0){
            return Response.fail(code);
        }
        List<Table> t = reserveService.searchTableByTime(reserve);
        return Response.success(t);
    }

    @GetMapping("/searchTableSchedule")
    @ApiOperation(value = "searchTableSchedule", notes = "查询所有桌子的被预定情况")
    public Response<List<TableSchedule>> searchTableSchedule(){
        return Response.success(reserveService.searchTableSchedule());
    }

    @PostMapping("/cancelReserve")
    public Response<Reserve> cancelReserve(@RequestBody CancleRequest cancleRequest, HttpServletRequest servletRequest){
        User user;
        user = userService.selectUserByCookie(servletRequest);
        if(user == null){
            return Response.fail(-1); // 未登录
        }
        if(user.getUser_status() != 4){
            return Response.fail(-13);
        }
        Reserve reserve = reserveService.searchReserveById(cancleRequest.getReserve_id());
        if(reserve == null){
            return Response.fail(-15);
        }
        if(reserve.getOpenid() != user.getOpenid()){
            return Response.fail(-12);
        }
        if(reserve.getReserve_status() != 4){
            return Response.fail(-14);
        }
        reserve.setReserve_status(5);
        reserveService.updateReserveStatus(reserve);
        return Response.success(reserve);
    }

    @PostMapping("/tt")
    public Response<Reserve> tt(@RequestBody CancleRequest cancleRequest, HttpServletRequest servletRequest){
        Reserve reserve = reserveService.searchReserveById(cancleRequest.getReserve_id());
        return Response.success(reserve);
    }
}

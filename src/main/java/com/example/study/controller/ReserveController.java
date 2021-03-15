package com.example.study.controller;


import com.example.study.TimeUtils;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import com.example.study.model.request.ReserveRequest;
import com.example.study.model.request.SearchTableByTimeRequest;
import com.example.study.service.ReserveService;
import com.example.study.service.TableService;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @ApiOperation(value = "预定一张桌子", notes = "文档不准确，data部分应该如下\n{\n" +
            "      \"reserve_id\": 27,\n" +
            "      \"table_id\": 12,\n" +
            "      \"reserve_start\": \"2021-03-10 09:02:59\",\n" +
            "      \"reserve_end\": \"2021-03-10 09:03:00\"\n" +
            "    }")
    private Response<List<Reserve>> reserveTable(@RequestBody ReserveRequest request, HttpServletRequest servletRequest) {
        /*TODO:时长不足
        *  TODO:不得早于上班时间和当前时间，晚于下班时间
        *   TODO:开始使用时才扣除天卡
        *    TODO:使用结束后扣除时间*/
        User user;
        Table table;
        Reserve reserve_post = new Reserve();
        request.getCode();
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
        if(reserve_post.getReserve_start().getTime() >= reserve_post.getReserve_end().getTime()){
            return Response.fail(-8); // 开始时间必须小于结束时间
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
        log.info("新纪录:{}",reserve_post);
        //if(table.getIs_reserve()){ // 如果已经被预定了，查看有效的预定记录，并查看预定时间是否冲突，就是两者的时间是否相交
            List<Reserve> reserves = reserveService.selectVaildReserve(reserve_post);
            if(reserves.size() > 0){
                log.info("冲突纪录：{}",reserves);
                reserveService.deleteReserveById(reserve_post);
                return Response.fail(-7, reserves); // 冲突
            }
        //}
        if(request.getCode() == 1){
            user.setUser_status(2);
        } else if(request.getCode() == 0){
            user.setUser_status(1);
        } else {
            return Response.fail(-11);
        }
        if(table.getIs_reserve() == false){
            table.setIs_reserve(true);
            tableService.updateTableReserveState(table);
        }
        userService.updateUserState(user);
        List<Reserve> l = new ArrayList<>();
        l.add(reserve_post);
        return Response.success(l);
    }

    @PostMapping("/searchTableByTime")
    public Response<List<Table>> searchTableByTime(@RequestBody SearchTableByTimeRequest searchTableByTimeRequest){
        List<Table> t = reserveService.searchTableByTime(searchTableByTimeRequest);
        return Response.success(t);
    }

    /*@GetMapping("/reserveTest")
    public Response<List<Table>> reserevTest(@RequestBody SearchTableByTimeRequest searchTableByTimeRequest){
        return Response.success(null);
    }*/
}

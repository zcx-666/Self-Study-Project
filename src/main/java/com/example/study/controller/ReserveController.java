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
    // TODO: 查找用户的预定记录


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
            return Response.fail(-10); // 非法的时间格式
        }
        user = userService.selectUserByCookie(servletRequest);
        if(user == null){
            return Response.fail(-1); // 未登录
        }
        Integer status = user.getUser_status();
        if(status == 3 || status == 4 || status == 1 || status == 2){
            return Response.fail(-4);
        }
        Integer code = reserveService.judgeReserveTime(reserve_post, user, request.getCode());
        if(code != 0){
            return Response.fail(code);
        }
        /* 在judgeReserveTime中判断
        if(request.getCode() == 1){
            user.setUser_status(4);
        } else if(request.getCode() == 0){
            user.setUser_status(3);
        } else {
            return Response.fail(-11);
        }*/
        table = tableService.selectTableByTableId(request.getTable_id());
        if(table == null){
            return Response.fail(-5); // 查无此桌
        }
        /*if(table.getIs_using()){
            return Response.fail(-6); // 正在被使用
        } // 通过订单冲突判断就好了*/
        reserve_post.setReserve_status(2);
        reserve_post.setTable_id(table.getTable_id());
        reserve_post.setOpenid(user.getOpenid());
        reserve_post.setCreate_time(new Timestamp(System.currentTimeMillis()));
        reserveService.insertNewReserve(reserve_post);
        log.info("新记录:{}",reserve_post);
        // 如果已经被预定了，查看有效的预定记录，并查看预定时间是否冲突，就是两者的时间是否相交
        List<Reserve> reserves = reserveService.selectConflictingReserve(reserve_post);
        if(reserves.size() > 0){
            log.info("冲突纪录：{}",reserves);
            reserveService.deleteReserveById(reserve_post);
            return Response.fail(-7, reserves); // 冲突
        } else {
            // 无冲突，订单状态2更新为4
            reserve_post.setReserve_status(4);
            reserveService.updateReserveStatus(reserve_post);
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
    @ApiOperation(value = "通过时间查找可用桌子", notes = "不包括用户自己的预定")
    public Response<List<Table>> searchTableByTime(@RequestBody SearchTableByTimeRequest request){
        Reserve reserve = new Reserve();
        try {
            reserve.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.fail(-10);
        }
        Integer code = reserveService.judgeReserveTime(reserve, null, null);
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

    @GetMapping("/searchReserveByCookie")
    @ApiOperation(value = "获得用户的所有订单")
    public Response<List<Reserve>> searchReserveByCookie(HttpServletRequest request){
        User user = new User();
        Integer code = userService.judgeUser(request, user);
        if(code != 0){
            return Response.fail(code);
        }
        List<Reserve> reserves = reserveService.searchReserveByOpenId(user.getOpenid());
        return Response.success(reserves);
    }

    @PostMapping("/cancelReserve")
    public Response<Reserve> cancelReserve(@RequestBody CancleRequest cancleRequest, HttpServletRequest servletRequest){
        // TODO: is_reserve的修改
        // TODO: 下机貌似可以用这里的代码
        User user;
        user = userService.selectUserByCookie(servletRequest);
        if(user == null){
            return Response.fail(-1); // 未登录
        }
        if(user.getUser_status() != 4 && user.getUser_status() != 3){
            return Response.fail(-13);
        }
        Reserve reserve = reserveService.searchReserveById(cancleRequest.getReserve_id());
        if(reserve == null){
            return Response.fail(-15);
        }
        if(!reserve.getOpenid().equals(user.getOpenid())){
            return Response.fail(-12);
        }
        if(reserve.getReserve_status() != 4){
            return Response.fail(-14);
        }
        reserve.setReserve_status(5);
        user.setUser_status(0);
        userService.updateUserState(user);
        reserveService.updateReserveStatus(reserve);
        return Response.success(reserve);
    }

    @PostMapping("/useTable")
    @ApiOperation(value = "useTable", notes = "使用桌子（使用预定过的没预定过的都可以）,先用searchTableByTime获得合适桌子的id,然后再用这个接口创建订单。" +
            "Tips:订单开始时间是“现在”，结束时间 min(下班时间, VIP时间, 下一个别人的预定的开始时间（通过searchTableSchedule获得）)，不需要用户输入")
    public Response<List<Reserve>> useTable(@RequestBody ReserveRequest request, HttpServletRequest servletRequest){
        // TODO: useTable, 可以使用未来的时间段
       /*  创建一个待确认的预定，开始时间是现在，结束时间 min(下班时间, VIP时间, 下一个预定时间，时长卡剩余时间，天卡),交给前端吧
        * 用户可以预定一个时间，然后再使用一个时间
        * 使用 List<Reserve> reserves = reserveService.selectConflictingReserve()判断冲突
        * 如果reserves为空则OK
        * 如果reserves不为空则判断长度是否为1，且openid = 自己 and 时间一致（就是使用用户自己的预定的情况，如果迟到了就把开始时间设为当前时间），
        * 否则就被占用
        * 修改user_status、reserve_status，
        * 如果用户正在使用天卡就不用扣钱了*/

        User user = new User();
        Table table = new Table();
        Reserve reserve_post = new Reserve();
        try {
            reserve_post.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve_post.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.fail(-10); // 非法的时间格式
        }
        Integer code = userService.judgeUser(servletRequest, user);
        if(code != 0){
            return Response.fail(code);
        }
        Integer status = user.getUser_status();
        if(status == 1 || status == 2){
            return Response.fail(-23);
        }
        code = reserveService.judgeUseTime(reserve_post, user, request.getCode());
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
        if(status == 3){
            // 时长预定
            List<Reserve> reserves = reserveService.searchReserveByOpenId(user.getOpenid());
            for (Reserve reserve : reserves){
                if(reserve.getReserve_status() == 4){
                    // 找到订单
                    if(reserve.getTable_id() != request.getTable_id()){
                        List<Reserve> t = new ArrayList<>();
                        t.add(reserve);
                        return Response.fail(-25, t);
                    }
                    if(reserve_post.getReserve_start().compareTo(reserve.getReserve_end()) == 1 ||
                    reserve_post.getReserve_end().compareTo(reserve.getReserve_start()) == -1){
                        // 不相交
                        // TODO: 先不写这个，等下再写
                        List<Reserve> t = new ArrayList<>();
                        t.add(reserve);
                        return Response.fail(-24, t);
                    }else {
                        // 相交
                    }
                }
            }
            log.error("异常数据:{}{}",reserves,user);
        } else if(status == 4){
            // 天卡预定0
        }
        // 要在这个时间段用，那就给你用，如果用户有预定了，那就把时间改成
        reserve_post.setReserve_status(2);
        reserve_post.setTable_id(table.getTable_id());
        reserve_post.setOpenid(user.getOpenid());
        reserve_post.setCreate_time(new Timestamp(System.currentTimeMillis()));
        reserveService.insertNewReserve(reserve_post);
        log.info("新记录:{}",reserve_post);
        // 如果新的订单不冲突就用新的，如果新的订单和自己的预定是冲突的，用时长卡就不管，
        List<Reserve> reserves = reserveService.selectConflictingReserve(reserve_post);
        if(reserves.size() > 0){
            for(Reserve reserve : reserves){
                if(reserve.getOpenid() != user.getOpenid()){
                    // 不是用户的订单
                    continue;
                }
                // 如果是用户的订单，把刚刚的占位预定记录的开始时间改成现在时间，结束时间不变，然后judgeTime，再冲突检测，，但是订单时间会和请求的不一样，导致我预定的是八点的，但是我一点就可以开始使用了
            }
            log.info("冲突纪录：{}",reserves);
            reserveService.deleteReserveById(reserve_post);
            return Response.fail(-7, reserves); // 冲突
        } else {
            // 无冲突，订单状态2更新为3,正在使用
            reserve_post.setReserve_status(3);
            reserveService.updateReserveStatus(reserve_post);
        }
        if(!table.getIs_using()){
            // 其实有点多余
            table.setIs_using(true);
            tableService.updateTableReserveState(table);
        }
        userService.updateUserState(user);
        List<Reserve> l = new ArrayList<>();
        l.add(reserve_post);
        return Response.success(l);
    }

}

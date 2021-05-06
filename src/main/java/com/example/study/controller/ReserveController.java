package com.example.study.controller;


import com.example.study.utils.TimeUtils;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import com.example.study.model.request.CancelRequest;
import com.example.study.model.request.FinishRequest;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private Response<List<Reserve>> reserveTable(@RequestBody @Valid ReserveRequest request, HttpServletRequest servletRequest) {
        User user = new User();
        Table table;
        Reserve reserve_post = new Reserve();
        try {
            reserve_post.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve_post.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            return Response.fail(-10); // 非法的时间格式
        }
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return errRes;
        }
        if (user.isReserved() || user.isUsing()) {
            return Response.fail(-4);
        }
        Integer code = reserveService.judgeReserveTime(reserve_post, user, request.getCode());
        if (code != 0) {
            return Response.fail(code);
        }
        table = tableService.selectTableByTableId(request.getTable_id());
        if (table == null) {
            return Response.fail(-5); // 查无此桌
        }
        reserve_post.setReserve_status(Reserve.CONFIRMING);
        reserve_post.setTable_id(table.getTable_id());
        reserve_post.setOpenid(user.getOpenid());
        reserve_post.setCreate_time(new Timestamp(System.currentTimeMillis()));
        reserveService.insertNewReserve(reserve_post);
        log.info("新的预约记录:{}", reserve_post);
        // 如果已经被预定了，查看有效的预定记录，并查看预定时间是否冲突，就是两者的时间是否相交
        List<Reserve> reserves = reserveService.selectConflictingReserve(reserve_post);
        if (reserves.size() > 0) {
            log.info("预约冲突纪录：{}", reserves);
            reserveService.deleteReserveById(reserve_post);
            return Response.fail(-7, reserves); // 冲突
        } else {
            // 无冲突，订单状态2更新为4
            reserve_post.setReserve_status(Reserve.WAITINGUSE);
            reserveService.updateReserveStatus(reserve_post);
        }
        if (!table.getIs_reserve()) {
            table.setIs_reserve(true);
            tableService.updateTableReserveState(table);
        }
        userService.updateUserStatus(user);
        List<Reserve> l = new ArrayList<>();
        l.add(reserve_post);
        return Response.success(l);
    }

    @PostMapping("/searchTableByTime")
    @ApiOperation(value = "通过时间查找可用桌子", notes = "如果桌子被该用户预定，也会被排除")
    public Response<List<Table>> searchTableByTime(@RequestBody @Valid SearchTableByTimeRequest request, HttpServletRequest servletRequest) {
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if(errRes != null){
            return errRes;
        }
        Reserve reserve = new Reserve();
        try {
            reserve.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            return Response.fail(-10);
        }
        Integer code = reserveService.judgeTime(reserve);
        if (code != 0) {
            return Response.fail(code);
        }
        List<Table> t = reserveService.searchTableByTime(reserve);
        return Response.success(t);
    }

    //@GetMapping("/searchTableSchedule")
    @ApiOperation(value = "searchTableSchedule（不推荐使用）", notes = "查询所有桌子的所有状态的订单，如果桌子没有订单，则除了table_id外都为空(不推荐使用，推荐使用/getValidReserve)")
    public Response<List<Reserve>> searchTableSchedule(HttpServletRequest servletRequest) {
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return errRes;
        }
        return Response.success(reserveService.searchTableSchedule());
    }

    @GetMapping("/searchReserveByCookie")
    @ApiOperation(value = "获得用户的所有订单")
    public Response<List<Reserve>> searchReserveByCookie(HttpServletRequest request) {
        User user = new User();
        Response errRes = userService.judgeUser(request, user);
        if (errRes != null) {
            return errRes;
        }
        List<Reserve> reserves = reserveService.searchReserveByOpenId(user.getOpenid());
        return Response.success(reserves);
    }

    @PostMapping("/cancelReserve")
    public Response<Reserve> cancelReserve(@RequestBody @Valid CancelRequest cancelRequest, HttpServletRequest servletRequest) {
        // TODO: is_reserve的修改
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return Response.fail(0);
        }
        if (!user.isReserved()) {
            return Response.fail(-13);
        }
        Reserve reserve = reserveService.searchReserveById(cancelRequest.getReserve_id());
        if (reserve == null) {
            return Response.fail(-15);
        }
        if (!reserve.getOpenid().equals(user.getOpenid())) {
            return Response.fail(-28);
        }
        if (reserve.getReserve_status() != Reserve.WAITINGUSE) {
            return Response.fail(-14);
        }
        reserve.setReserve_status(Reserve.CANCEL);
        user.setReserve_status(User.NONE);
        userService.updateUserStatus(user);
        reserveService.updateReserveStatus(reserve);
        return Response.success(reserve);
    }

    @PostMapping("/useTable")
    @ApiOperation(value = "useTable", notes = "使用桌子（有没有预定过都可以）," +
            "Tips:请求码根据用户的预定情况来，没有预定就手动选择，订单开始时间是“现在”，结束时间 min(下班时间, VIP时间, 下一个别人的预定的开始时间（通过getValidReserve获得,最好使用旧的，报错了再请求新数据），该用户预定过的订单的结束时间)，不需要用户输入，如果是后两者情况需要提醒用户，否则影响用户体验")
    public Response<List<Reserve>> useTable(@RequestBody @Valid ReserveRequest request, HttpServletRequest servletRequest) {
        /*  创建一个待确认的预定，开始时间是现在，结束时间 min(下班时间, VIP时间, 下一个预定时间，时长卡剩余时间，天卡),交给前端吧
         * 使用 List<Reserve> reserves = reserveService.selectConflictingReserve()判断冲突
         * 如果reserves为空则OK
         * 如果reserves不为空则判断长度是否为1，且openid = 自己 and 时间一致（就是使用用户自己的预定的情况，如果迟到了就把开始时间设为当前时间），
         * 否则就被占用
         * 修改user_status、reserve_status，
         * 如果用户正在使用天卡就不用扣钱了*/

        /* 创建一个待确认的预定，开始时间是现在，结束时间 min(下班时间, VIP时间, 下一个预定时间，时长卡剩余时间，天卡),交给前端吧
         * 如果用户正在使用 => 报错
         * 如果用户预定过 => 判断桌子是不是同一张、桌子是否被使用（判断冲突应该是可以解决），时间相交则并，否则报错
         * */

        User user = new User();
        Table table;
        Reserve reserve_post = new Reserve();
        Reserve reserve = new Reserve();
        try {
            reserve_post.setReserve_start(TimeUtils.dateToTimeStamp(request.getReserve_start()));
            reserve_post.setReserve_end(TimeUtils.dateToTimeStamp(request.getReserve_end()));
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.fail(-10); // 非法的时间格式
        }
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return errRes;
        }
        if (user.isUsing()) {
            return Response.fail(-23); // -23 您正在使用自习室
        }
        Integer errCode = reserveService.judgeUseTime(reserve_post, user, request.getCode());
        if (errCode != 0) {
            return Response.fail(errCode);
        }
        table = tableService.selectTableByTableId(request.getTable_id());
        if (table == null) {
            return Response.fail(-5); // 查无此桌
        }
        if (table.getIs_using()) {
            return Response.fail(-6); // 正在被使用
        } else {
            table.setIs_using(true);
        }
        if (user.isReserved()) {
            reserve = reserveService.searchWaitingUseReserveByOpenId(user.getOpenid());
            if (reserve == null) {
                log.error("用户状态为预定，但是找不到预定数据:{}", user);
                return Response.fail(-36);
            }
            if (!reserve.getTable_id().equals(request.getTable_id())) {
                List<Reserve> t = new ArrayList<>();
                t.add(reserve);
                return Response.fail(-25, t);
            }
            if (reserve_post.getReserve_start().compareTo(reserve.getReserve_end()) == 1 ||
                    reserve_post.getReserve_end().compareTo(reserve.getReserve_start()) == -1) {
                // 不相交
                List<Reserve> t = new ArrayList<>();
                t.add(reserve);
                return Response.fail(-24, t);
            } else {
                // 预定时间和使用时间相交，把时间并起来
                reserve_post.setReserve_id(reserve.getReserve_id());
                if (reserve_post.getReserve_start().compareTo(reserve.getReserve_start()) == 1) {
                    reserve_post.setReserve_start(reserve.getReserve_start());
                }
                if (reserve_post.getReserve_end().compareTo(reserve.getReserve_end()) == -1) {
                    reserve_post.setReserve_end(reserve.getReserve_end());
                }
                reserve_post.setCreate_time(reserve.getCreate_time());
                // 并之后的时间可能会导致VIP时长卡不足，但是可以使用，这样会扣成负的，那其实也没有关系，应该不会介意这么点时间的
                /*if (request.getCode() == ReserveRequest.TIME) {
                    Timestamp start = reserve_post.getReserve_start();
                    Timestamp end = reserve_post.getReserve_end();
                    Long t = (start.getTime() - end.getTime()) / 1000; // 预定时长
                    Integer vip = user.getVip_time();
                    if (t > vip) {
                        return Response.fail(-22); // VIP时长不足
                    }
                }*/
            }
        }
        reserve_post.setReserve_status(Reserve.CONFIRMING);
        reserve_post.setTable_id(table.getTable_id());
        reserve_post.setOpenid(user.getOpenid());
        if(user.isReserved()){
            reserveService.updateReserveStatusAndTime(reserve_post);
            user.setReserve_status(User.NONE);
        }else {
            // 如果没有预定过，那创建时间为现在，否则就是之前的时间
            reserve_post.setCreate_time(new Timestamp(System.currentTimeMillis()));
            reserveService.insertNewReserve(reserve_post);
        }
        log.info("新的使用记录:{}", reserve_post);
        List<Reserve> reserves = reserveService.selectConflictingReserve(reserve_post);
        if (reserves.size() > 0) {
            log.info("使用冲突纪录：{}", reserves);
            if(user.isReserved()){
                reserveService.updateReserveStatusAndTime(reserve);
            }else {
                reserveService.deleteReserveById(reserve_post);
            }
            return Response.fail(-7, reserves); // 冲突
        } else {
            // 无冲突，订单状态2更新为3,正在使用
            reserve_post.setReserve_status(Reserve.USING);
            reserveService.updateReserveStatus(reserve_post);
        }
        tableService.updateTableUseStatus(table);
        userService.updateUserStatus(user);
        List<Reserve> l = new ArrayList<>();
        l.add(reserve_post);
        return Response.success(l);
    }

    @GetMapping("/getValidReserve")
    @ApiOperation(value = "/getValidReserve", notes = "获得所有正在使用、已预定的订单")
    public Response<List<Reserve>> getValidReserve(HttpServletRequest servletRequest) {
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return errRes;
        }
        return Response.success(reserveService.getValidReserve());
    }

    @PostMapping("/finishUse")
    public Response<Reserve> finishUse(@RequestBody @Valid FinishRequest finishRequest, HttpServletRequest servletRequest) {
        /* 输入订单ID、cookie
         * 找到订单，判断是否在使用（reserve_status == USING）、openid是否一致，否则报错
         * 找到桌子，判断桌子是否在使用中，不在则报错
         * 把结束时间改成现在（可能存在使用超时，覆盖了下一个预定。会不会出现A先预定了，然后B在A预定时间到前使用了桌子，然后一直使用下去，直到覆盖了A的预定？除非出BUG，不然不会）
         * 把订单状态改成0
         * 如果user_status != 1 || 2, 用户不在使用中，报错
         * if user_status == 1 正在使用时长
         * end-start => 使用时长(需要毫秒 => 秒的转换)
         * user.vip_time -= 使用时长
         * else if user_status == 2
         * user.vip_daypass--
         * 在数据库中更新reserve、table和user
         * */
        User user = new User();
        Response errRes = userService.judgeUser(servletRequest, user);
        if (errRes != null) {
            return errRes;
        }
        Reserve reserve = reserveService.searchReserveById(finishRequest.getReserve_id());
        Table table = tableService.selectTableByTableId(reserve.getTable_id());
        if (reserve == null) {
            return Response.fail(-15); // 订单不存在
        }
        if (!reserve.getOpenid().equals(user.getOpenid())) {
            return Response.fail(-28);
        }
        if (reserve.getReserve_status() != Reserve.USING) {
            return Response.fail(-30);
        }
        if (!table.getIs_using()) {
            log.error("找到了使用中的订单，但是桌子并没有在使用中:{},{}",reserve,table);
            return Response.fail(-31);
        }
        table.setIs_using(false);
        // 修改订单状态
        Timestamp now = new Timestamp(System.currentTimeMillis());
        reserve.setReserve_end(now);
        reserve.setReserve_status(Reserve.FINISH);
        if (user.getUsing_status() == User.TIME) {
            Long useTime = (reserve.getReserve_end().getTime() - reserve.getReserve_start().getTime()) / 1000 * finishRequest.getPower();
            Long left = user.getVip_time() - useTime;
            Integer t = left.intValue(); // Long => Integer
            user.setVip_time(t);
        } else if (user.getUsing_status() == User.DAY) {
            // 使用天卡的话什么都不用管，直接取消就好了，反正肯定已经是生效状态，时间也在使用的时候扣了
        } else {
            return Response.fail(-29);
        }
        user.setUsing_status(User.NONE);
        userService.updateUserStatus(user);
        reserveService.updateReserveStatusAndTime(reserve);
        tableService.updateTableUseStatus(table);
        return Response.success(reserve);
    }

    @GetMapping("/hello")
    public String tt(HttpServletRequest request) {
       return "hello world!";
    }
}

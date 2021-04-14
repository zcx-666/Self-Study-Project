package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import com.example.study.model.request.ReserveRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;

    private static final Integer MIN_RESERVE_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("min_reserve_time"));
    private static final Integer WORK_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_hour"));
    private static final Integer WORK_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_minute"));
    private static final Integer CLOSING_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_hour"));
    private static final Integer CLOSING_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_minute"));


    public void insertNewReserve(Reserve reserve) {
        reserve.setCreate_time(new Timestamp(new Date().getTime()));
        reserveMapper.insertNewReserve(reserve);
    }

    public List<Reserve> selectConflictingReserve(Reserve reserve) {
        return reserveMapper.selectConflictingReserve(reserve);
    }


    public void deleteReserveById(Reserve reserve_post) {
        reserveMapper.deleteReserveById(reserve_post.getReserve_id());
    }

    public List<Table> searchTableByTime(Reserve reserve) {
        return reserveMapper.searchTableByTime(reserve);
    }

    public Reserve searchReserveById(Integer id) {
        return reserveMapper.searchReserveById(id);
    }

    public void updateReserveStatus(Reserve reserve) {
        reserveMapper.updateReserveStatus(reserve);
    }

    public List<Reserve> searchTableSchedule() {
        return reserveMapper.searchTableSchedule();
    }


    public Integer judgeReserveTime(Reserve reserve, User user, Integer code) {
        // TODO: 改成重载
        Timestamp start = reserve.getReserve_start();
        Timestamp end = reserve.getReserve_end();
        if (-1 != start.compareTo(end)) {
            return -8; // 开始时间必须小于结束时间
        }
        if (end.getTime() - start.getTime() < MIN_RESERVE_MINUTE * 60 * 1000) {
            return -32;
        }
        if (start.getYear() != end.getYear()
                || start.getMonth() != end.getMonth()
                || start.getDate() != end.getDate()) {
            return -19; // 预定必须在同一天
        }
        Timestamp now = new Timestamp(new Date().getTime());
        now.setMinutes(now.getMinutes() - 5);
        if (start.compareTo(now) == -1) {
            // 只能预约现在五分钟前到未来的时间
            return -20;
        }
        now.setHours(23);
        now.setMinutes(59);
        now.setSeconds(59);
        if (start.getTime() - now.getTime() > 7 * 24 * 60 * 60 * 1000) {
            return -34;
        }
        Date work = new Date(start.getTime()); // 获取预定的同一天
        work.setHours(WORK_HOUR);
        work.setMinutes(WORK_MINUTE - 1);
        work.setSeconds(0);
        Date close = new Date(start.getTime()); // 获取预定的同一天
        close.setHours(CLOSING_HOUR);
        close.setMinutes(CLOSING_MINUTE + 1);
        close.setSeconds(0);
        Timestamp w = new Timestamp(work.getTime());
        Timestamp c = new Timestamp(close.getTime());
        if (!(start.compareTo(w) >= 0 && end.compareTo(c) <= 0)) {
            return -33;
        }
        if (user != null) {
            if (code == null) {
                return -35;
            }
            if (code == ReserveRequest.TIME) {
                // 使用时长
                Long t = (end.getTime() - start.getTime()) / 1000; // 预定时长(秒)
                Integer vip = user.getVip_time();
                if (t > vip) {
                    return -22; // VIP时长不足
                }
                user.setReserve_status(User.TIME);
            } else if (code == ReserveRequest.DAY) {
                // 使用天卡
                // 天卡生效中 && 预定的是今天的 && 预定是这个月的 || 剩余天数足够
                // （天卡不生效 || 预定不是今天 || 预定不是这个月的） && 剩余天数不够
                Timestamp today = new Timestamp(new Date().getTime());
                if (user.getVip_daypass() < 1 &&
                        (reserve.getReserve_start().getDate() != today.getDate() ||
                                reserve.getReserve_start().getMonth() != today.getMonth() ||
                                !user.getIs_using_daypass())) {
                    return -22; // VIP时长不足
                }
                user.setReserve_status(User.DAY);
            } else {
                return -11;
            }
        }
        return 0;
    }

    public List<Reserve> searchReserveByOpenId(String openid) {
        return reserveMapper.searchReserveByOpenId(openid);
    }

    public List<Reserve> searchReserveByTableId(Integer table_id) {
        // 给“删除桌子”用的
        return reserveMapper.searchReserveByTableId(table_id);
    }

    public Integer judgeUseTime(Reserve reserve, User user, Integer code) {
        // judgeReserveTime会改变user.status
        int reserve_status = user.getReserve_status();
        Integer err = judgeReserveTime(reserve, user, code);
        user.setReserve_status(reserve_status);
        if (err != 0) {
            return err;
        }
        Timestamp now = new Timestamp(new Date().getTime());
        now.setMinutes(now.getMinutes() + 5);
        if (reserve.getReserve_start().compareTo(now) == -1) {
            return -26;
        }
        if (code == ReserveRequest.TIME) {
            // 使用时长
            user.setUsing_status(User.TIME);
        } else if (code == ReserveRequest.DAY) {
            // 使用天卡, 如果天卡生效则什么都不管，否则天卡时间-1，天卡生效
            if(!user.getIs_using_daypass()){
                user.setIs_using_daypass(true);
                user.setVip_daypass(user.getVip_daypass() - 1);
            }
            user.setUsing_status(User.DAY);
        }

        return 0;
    }

    public List<Reserve> getValidReserve() {
        return reserveMapper.getValidReserve();
    }

    public void updateReserveStatusAndTime(Reserve reserve) {
        reserveMapper.updateReserveStatusAndTime(reserve);
    }
}

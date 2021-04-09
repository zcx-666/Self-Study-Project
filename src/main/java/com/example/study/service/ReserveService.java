package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.TableSchedule;
import com.example.study.model.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;


    public void insertNewReserve(Reserve reserve) {
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

    public List<TableSchedule> searchTableSchedule() {
        return reserveMapper.searchTableSchedule();
    }

    public Integer judgeReserveTime(Reserve reserve, User user, Integer code) {
        Timestamp start = reserve.getReserve_start();
        Timestamp end = reserve.getReserve_end();
        if (-1 != start.compareTo(end)) {
            return -8; // 开始时间必须小于结束时间
        }
        if (start.getYear() != end.getYear()
                || start.getMonth() != end.getMonth()
                || start.getDate() != start.getDate()) {
            return -19; // 预定必须在同一天
        }
        Timestamp now = new Timestamp(new Date().getTime());
        now.setMinutes(now.getMinutes() - 5);
        if (start.compareTo(now) == -1) {
            // 只能预约现在五分钟前到未来的时间
            return -20;
        }
        if (false) {
            // TODO: 不在上班时间内
        }
        if (false) {
            // TODO: 只能预定一周内
        }
        if (false && user != null) {
            if (code == null) {
                return -99;
            }
            if (code == 0) {
                // 使用时长
                Long t = (start.getTime() - end.getTime()) / 1000; // 预定时长
                Integer vip = user.getVip_time();
                if (t > vip) {
                    return -22; // VIP时长不足
                }
                user.setUser_status(3);
            } else if (code == 1) {
                // 使用天卡
                if (user.getVip_daypass() < 1) {
                    return -22; // VIP时长不足
                }
                user.setUser_status(4);
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
        Integer err = judgeReserveTime(reserve, user, code);
        if(err != 0){
            return err;
        }
        Timestamp now = new Timestamp(new Date().getTime());
        now.setMinutes(now.getMinutes() + 5);
        if (reserve.getReserve_start().compareTo(now) == -1) {
            return -26;
        }
        if(code == 0){
            // 使用时长
            user.setUser_status(1);
        }else if(code == 1){
            // 使用天卡
            user.setUser_status(2);
        }

        return 0;
    }
}

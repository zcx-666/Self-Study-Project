package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.TableSchedule;
import com.example.study.model.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;


    public void insertNewReserve(Reserve reserve) {
        reserveMapper.insertNewReserve(reserve);
    }

    public List<Reserve> selectConflictingReserve(Reserve reserve){
        return reserveMapper.selectConflictingReserve(reserve);
    }


    public void deleteReserveById(Reserve reserve_post) {
        reserveMapper.deleteReserveById(reserve_post.getReserve_id());
    }

    public List<Table> searchTableByTime(Reserve reserve) {
        return reserveMapper.searchTableByTime(reserve);
    }

    public Reserve searchReserveById(Integer id){
        return reserveMapper.searchReserveById(id);
    }

    public void updateReserveStatus(Reserve reserve) {
        reserveMapper.updateReserveStatus(reserve);
    }

    public List<TableSchedule> searchTableSchedule() {
        return reserveMapper.searchTableSchedule();
    }

    public Integer judgeReserveTime(Reserve reserve, User user){
        Timestamp start = reserve.getReserve_start();
        Timestamp end = reserve.getReserve_end();
        if(-1 != start.compareTo(end)){
            return -8; // 开始时间必须小于结束时间
        }
        if(false){
            // TODO: 不在上班时间内
        }
        if(false){
            // TODO: 只能预定一周内
        }
        if(false && user != null){
            // TODO: VIP时长不足
        }
        return 0;
    }
}

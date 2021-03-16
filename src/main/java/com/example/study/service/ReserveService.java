package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.TableSchedule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    public void updateReserveStatus(Reserve reserve) {
        reserveMapper.updateReserveStatus(reserve);
    }

    public List<TableSchedule> searchTableSchedule() {
        return reserveMapper.searchTableSchedule();
    }
}

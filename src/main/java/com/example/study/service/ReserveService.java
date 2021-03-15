package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.request.SearchTableByTimeRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;


    public void insertNewReserve(Reserve reserve) {
        reserveMapper.insertNewReserve(reserve);

    }

    public List<Reserve> selectVaildReserve(Reserve reserve){
        /*for (int i = 0; i < reserves.size(); i++){
            reserves.get(i).setReserve_start(dateToTimeStamp(reserves.get(i).getReserve_start().toString()));
            reserves.get(i).setReserve_end(dateToTimeStamp(reserves.get(i).getReserve_end()));
        }*/
        return reserveMapper.selectVaildReserve(reserve);
    }

    public Boolean isTimeConflict(@NotNull Reserve r, Reserve reserve_post) {
        return reserve_post.getReserve_start().getTime() < r.getReserve_end().getTime() &&
                reserve_post.getReserve_end().getTime() > r.getReserve_start().getTime();
    }

    public void deleteReserveById(Reserve reserve_post) {
        reserveMapper.deleteReserveById(reserve_post.getReserve_id());
    }

    public List<Table> searchTableByTime(SearchTableByTimeRequest searchTableByTimeRequest) {
        return reserveMapper.searchTableByTime(searchTableByTimeRequest);
    }
}

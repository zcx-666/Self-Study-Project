package com.example.study.service;

import com.example.study.mapper.ReserveMapper;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.User;
import com.example.study.model.request.ReserveRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;


    public void insertNewReserve(Reserve reserve) {
        reserveMapper.insertNewReserve(reserve);

    }

    public List<Reserve> selectVaildReserveByTableId(Integer table_id){
        List<Reserve> reserves = reserveMapper.selectVaildReserveByTableId(table_id);
        /*for (int i = 0; i < reserves.size(); i++){
            reserves.get(i).setReserve_start(dateToTimeStamp(reserves.get(i).getReserve_start().toString()));
            reserves.get(i).setReserve_end(dateToTimeStamp(reserves.get(i).getReserve_end()));
        }*/
        return reserves;
    }

    public Boolean isTimeConflict(@NotNull Reserve r, Reserve reserve_post) {
        if(reserve_post.getReserve_start().getTime() >= r.getReserve_end().getTime() ||
                reserve_post.getReserve_end().getTime() <= r.getReserve_start().getTime()){
            return false;
            // 不存在冲突
        }
        return true;
    }

    public String timeStampToDate(String timeStamp){
        // 把 String 类型的时间戳转换为格式化的日期 String
        SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return s.format(Long.valueOf(timeStamp));
    }

    public Timestamp dateToTimeStamp(String date) throws ParseException{
        // 把格式化的日期 String 转换为 TimeStamp 类型的时间戳
        SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Long t;
        try {
            t = s.parse(date).getTime();
            return new Timestamp(t);
        } catch (ParseException e){
            throw e;
        }
    }
}

package com.example.study.mapper;

import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.TableSchedule;
import com.example.study.model.request.SearchTableByTimeRequest;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ReserveMapper {

    void insertNewReserve(Reserve reserve);

    @Select("SELECT * FROM reserve_form WHERE table_id = #{table_id} AND reserve_id != #{reserve_id} AND create_time < #{create_time} and reserve_status >= 2 and not(reserve_start >= #{reserve_end} or reserve_end <= #{reserve_start})")
    List<Reserve> selectConflictingReserve(Reserve reserve);

    @Delete("DELETE FROM reserve_form WHERE reserve_id = #{reserve_id}")
    void deleteReserveById(@Param("reserve_id") Integer reserve_id);

    @Select("SELECT * FROM table_form WHERE NOT EXISTS (SELECT reserve_form.table_id FROM reserve_form WHERE reserve_status > 2 AND NOT (reserve_start >= #{reserve_end} OR reserve_end <= #{reserve_start}) AND table_form.table_id = reserve_form.table_id)")
    List<Table> searchTableByTime(Reserve reserve); // 查找不时间冲突的桌子, 不包括待确认的订单

    @Update("UPDATE reserve_form SET reserve_status = #{reserve_status} WHERE reserve_id = #{reserve_id}")
    void updateReserveStatus(Reserve reserve);

    @Select("SELECT table_form.table_id, reserve_id, reserve_start, reserve_end, create_time, openid, reserve_status FROM table_form LEFT JOIN reserve_form ON table_form.table_id = reserve_form.table_id ORDER BY table_form.table_id")
    List<TableSchedule> searchTableSchedule();

    @Select("SELECT * FROM reserve_form WHERE reserve_id = #{id}")
    Reserve searchReserveById(Integer id);
}

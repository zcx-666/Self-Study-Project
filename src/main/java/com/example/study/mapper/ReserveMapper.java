package com.example.study.mapper;

import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReserveMapper {

    void insertNewReserve(Reserve reserve);

    @Select("SELECT * FROM reserve_form WHERE table_id=#{table_id} AND reserve_id!=#{reserve_id} AND (reserve_status=2 and create_time<#{create_time} OR reserve_status=3 OR reserve_status=4) AND NOT(reserve_start>=#{reserve_end} OR reserve_end<=#{reserve_start})")
    List<Reserve> selectConflictingReserve(Reserve reserve);

    @Delete("DELETE FROM reserve_form WHERE reserve_id = #{reserve_id}")
    void deleteReserveById(@Param("reserve_id") Integer reserve_id);

    // TODO: 待测试
    @Select("SELECT * FROM table_form WHERE NOT EXISTS (SELECT reserve_form.table_id FROM reserve_form WHERE ((reserve_status = 3  or reserve_status = 4) AND NOT (reserve_start >= #{reserve_end} OR reserve_end <= #{reserve_start})) AND table_form.table_id = reserve_form.table_id)")
    List<Table> searchTableByTime(Reserve reserve); // 查找时间不冲突的桌子, 不包括待确认的订单

    @Update("UPDATE reserve_form SET reserve_status = #{reserve_status} WHERE reserve_id = #{reserve_id}")
    void updateReserveStatus(Reserve reserve);

    @Select("SELECT table_form.table_id, reserve_id, reserve_start, reserve_end, create_time, openid, reserve_status FROM table_form LEFT JOIN reserve_form ON table_form.table_id = reserve_form.table_id ORDER BY table_form.table_id")
    List<Reserve> searchTableSchedule();

    @Select("SELECT * FROM reserve_form WHERE reserve_id = #{id}")
    Reserve searchReserveById(Integer id);

    @Select("SELECT * FROM reserve_form WHERE openid = #{openid}")
    List<Reserve> searchReserveByOpenId(String openid);

    @Select("SELECT * FROM reserve_form WHERE table_id = #{table_id}")
    List<Reserve> searchReserveByTableId(Integer table_id);

    @Select("SELECT * FROM reserve_form WHERE reserve_status = 3 or reserve_status = 4 ORDER BY table_id")
    List<Reserve> getValidReserve();

    @Update("UPDATE reserve_form SET reserve_status = #{reserve_status}, reserve_start = #{reserve_start}, reserve_end = #{reserve_end} WHERE reserve_id = #{reserve_id}")
    void updateReserveStatusAndTime(Reserve reserve);
}

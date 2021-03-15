package com.example.study.mapper;

import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.request.SearchTableByTimeRequest;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ReserveMapper {
    /*@Insert("INSERT INTO reserve_form (reserve_id, reserve_start, reserve_end, openid, table_id, is_vaild) VALUE (#{reserve_id}, #{reserve_start}, #{reserve_end},#{openid},#{table_id},#{is_vaild})")
    public void insertNewReserve(@Param("reserve_id")Integer reserve_id,
                                 @Param("openid")String openid,
                                 @Param("reserve_start") Timestamp reserve_start,
                                 @Param("reserve_end")Timestamp reserve_end,
                                 @Param("table_id")Integer table_id,
                                 @Param("is_vaild")Boolean is_vaild);*/

    void insertNewReserve(Reserve reserve);

    @Select("SELECT * FROM reserve_form WHERE table_id = #{table_id} AND reserve_id != #{reserve_id} AND create_time < #{create_time} and reserve_status >= 2 and not(reserve_start >= #{reserve_end} or reserve_end <= #{reserve_start})")
    List<Reserve> selectVaildReserve(Reserve reserve);

    @Delete("DELETE FROM reserve_form WHERE reserve_id = #{reserve_id}")
    void deleteReserveById(@Param("reserve_id") Integer reserve_id);

    @Select("SELECT table_id FROM reserve_form WHERE table_id = #{table_id} AND reserve_id != #{reserve_id} AND create_time < #{create_time} and reserve_status >= 2 and not(reserve_start >= #{reserve_end} or reserve_end <= #{reserve_start})")
    List<Table> searchTableByTime(SearchTableByTimeRequest searchTableByTimeRequest);
}

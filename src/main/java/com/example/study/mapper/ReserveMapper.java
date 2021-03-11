package com.example.study.mapper;

import com.example.study.model.entity.Reserve;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    public void insertNewReserve(Reserve reserve);

    @Select("SELECT * FROM reserve_form WHERE table_id = #{table_id} AND is_vaild = TRUE")
    public List<Reserve> selectVaildReserveByTableId(@Param("table_id") Integer table_id);

}

package com.example.study.mapper;

import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TableMapper {

    @Select("SELECT * FROM table_form WHERE table_id=#{table_id}")
    Table selectTableByTableId(@Param("table_id") Integer table_id);

    @Insert("INSERT INTO table_form (table_id, is_reserve, is_using) VALUE (#{table_id}, #{is_reserve}, #{is_using})")
    void insertNewTable(@Param("table_id")Integer table_id,
                        @Param("is_reserve")Boolean is_reserve,
                        @Param("is_using")Boolean is_using);

    @Update("UPDATE table_form SET is_reserve=#{is_reserve} WHERE table_id=#{table_id}")
    void updateTableReserveState(@Param("table_id")Integer table_id,
                                 @Param("is_reserve")Boolean is_reserve);

}

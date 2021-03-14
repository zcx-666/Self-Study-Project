package com.example.study.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class Table implements Serializable {
    private Integer table_id; // TODO：能不能删除
    private Boolean is_reserve;
    private Boolean is_using;

    public Table(Integer table_id){
        this.table_id = table_id;
        this.is_reserve = false;
        this.is_using = false;
    }

    public Table(){
        this.is_reserve = false;
        this.is_using = false;
    }
}

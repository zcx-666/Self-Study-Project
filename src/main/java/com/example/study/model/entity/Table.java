package com.example.study.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
public class Table {
    private Integer table_id;
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

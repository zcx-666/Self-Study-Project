package com.example.study.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Table implements Serializable {
    private Integer table_id;
    @Deprecated
    @ApiModelProperty("不要使用这个属性判断桌子的借阅情况")
    private Boolean is_reserve; // 不建议使用
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

package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TableSchedule {
    private Integer table_id;
    private Integer reserve_id;
    @ApiModelProperty("预定开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_start;
    @ApiModelProperty("预定结束时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_end;
    @JsonIgnore
    private Timestamp create_time;
    @JsonIgnore
    private String openid;
    @ApiModelProperty(value = "0.订单已完成 1.订单已过期 2.订单待确认（后台） 3.正在使用 4.待使用")
    private Integer reserve_status;
}

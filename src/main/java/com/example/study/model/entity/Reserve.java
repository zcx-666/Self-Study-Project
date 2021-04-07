package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Reserve implements Serializable {
    private Integer reserve_id;
    @JsonIgnore
    private String openid;
    private Integer table_id;
    @ApiModelProperty("预定开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_start;
    @ApiModelProperty("预定结束时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_end;

    @ApiModelProperty(value = "0.订单已完成 1.订单已过期 2.订单待确认（后台） 3.正在使用 4.待使用 5.取消")
    private Integer reserve_status;

    @ApiModelProperty("订单创建时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp create_time;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getReserve_start() {
        return reserve_start;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getReserve_end() {
        return reserve_end;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getCreate_time() {
        return create_time;
    }
}

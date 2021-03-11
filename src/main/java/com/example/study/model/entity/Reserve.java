package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Reserve {
    private Integer reserve_id;
    @JsonIgnore
    private String openid;
    private Integer table_id;
    @ApiModelProperty("预定开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_start;
    @ApiModelProperty("预定结束时间戳\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp reserve_end;
    @JsonIgnore
    private Boolean is_vaild;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getReserve_start() {
        return reserve_start;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getReserve_end() {
        return reserve_end;
    }
}

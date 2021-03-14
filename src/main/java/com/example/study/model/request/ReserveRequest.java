package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class ReserveRequest {
    @NotNull
    @ApiModelProperty("预定开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private String reserve_start;

    @NotNull
    @ApiModelProperty("预定结束时间\"yyyy-MM-dd hh:mm:ss\" 如果使用天卡就把预定结束时间写成当日的23：59")
    private String reserve_end;

    @NotNull
    private Integer table_id;

    @NotNull
    @ApiModelProperty("0.使用时长；1.使用天卡")
    private Integer code;
}

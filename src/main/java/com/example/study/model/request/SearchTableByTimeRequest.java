package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchTableByTimeRequest {
    @NotNull
    @ApiModelProperty("预定开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private String reserve_start;

    @NotNull
    @ApiModelProperty("预定结束时间\"yyyy-MM-dd hh:mm:ss\"")
    private String reserve_end;
}

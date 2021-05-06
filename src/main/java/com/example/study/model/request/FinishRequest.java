package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class FinishRequest {
    @NotNull
    private Integer reserve_id;

    @ApiModelProperty("时长消耗倍率(整数)")
    private int power = 1;
    /*@NotNull
    private Integer table_id;*/
}

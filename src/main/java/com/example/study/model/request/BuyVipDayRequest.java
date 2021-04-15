package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BuyVipDayRequest {
    @NotNull
    private Integer day;
    @NotNull
    @ApiModelProperty("单位(秒)")
    private Integer time;
    @NotNull
    private String wechat_pay_id;
}

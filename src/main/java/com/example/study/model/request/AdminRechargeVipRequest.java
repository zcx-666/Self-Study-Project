package com.example.study.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminRechargeVipRequest {
    @NotNull
    private Integer vipDay;
    @NotNull
    private Integer vipTime;
    @NotNull
    @ApiModelProperty("wx.login获得的code")
    private String code;
}

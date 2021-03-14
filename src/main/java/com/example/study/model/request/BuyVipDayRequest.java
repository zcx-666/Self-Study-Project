package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class BuyVipDayRequest {
    @NotNull
    private Integer day;
    @NotNull
    private Integer time;
    @NotNull
    private String wechat_pay_id;
}

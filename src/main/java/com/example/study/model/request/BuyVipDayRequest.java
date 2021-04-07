package com.example.study.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BuyVipDayRequest {
    @NotNull
    private Integer day;
    @NotNull
    private Integer time;
    @NotNull
    private String wechat_pay_id;
}

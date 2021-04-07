package com.example.study.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminRechargeVip {
    @NotNull
    private Integer day;
    @NotNull
    private Integer time;
    @NotNull
    @JsonIgnore
    private String wechat_pay_id = "adminRecharge";
    @NotNull
    private String openid;
}

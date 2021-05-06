package com.example.study.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class BuyVipDayRequest {
    @NotNull
    @ApiModelProperty("充值的天数(天)")
    private Integer day;
    @NotNull
    @ApiModelProperty("充值的时长(秒)")
    private Integer time;
    @NotNull
    private String wechat_pay_id;

    @ApiModelProperty("天卡过期时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp overdue_day;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("时长卡过期时间")
    private Timestamp overdue_time;
}

package com.example.study.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class AdminRechargeVipRequest {
    @NotNull
    private Integer vipDay;
    @NotNull
    private Integer vipTime;
    @NotNull
    @ApiModelProperty("wx.login获得的code")
    private String code;
    @ApiModelProperty("天卡过期时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp overdue_day;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("时长卡过期时间")
    private Timestamp overdue_time;
}

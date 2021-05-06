package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class User implements Serializable {
    // TODO：增加reserve_id，-1为空，增加查找效率
    public static final int NONE = 0; // 无预定/无使用
    public static final int TIME = 1; // 时长卡
    public static final int DAY = 2; // 次卡

    @JsonIgnore
    private String openid;

    @ApiModelProperty("0.无预定 1.正在使用时长预定 2.正在使用天卡预定")
    private int reserve_status = NONE;

    @ApiModelProperty("0.无使用 1.正在使用时长自习 2.正在使用天卡自习")
    private int using_status = NONE;

    @ApiModelProperty("天卡是否生效中")
    private Boolean is_using_daypass = false;

    private Boolean isadmin = false;

    @ApiModelProperty("头像链接")
    private String avatar;

    @ApiModelProperty("天卡剩余时间（天）")
    private int vip_daypass = 0;
    @ApiModelProperty("剩余可用时间（秒），可能是负数，但是过期之后会重置为0")
    private int vip_time = 0;

    @ApiModelProperty("时长卡到期时间\"yyyy-MM-dd hh:mm:ss\",当时长卡过期后返回null")
    private Timestamp overdue_time;

    @ApiModelProperty("天长卡到期时间\"yyyy-MM-dd hh:mm:ss\",当天卡过期后返回null")
    private Timestamp overdue_day;

    @JsonIgnore
    @ApiModelProperty("来自微信官方")
    private String session_key;

    @JsonIgnore
    private String cookie;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getOverdue_time() {
        return overdue_time;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getOverdue_day() {
        return overdue_day;
    }

    public void copyUser(User user) {
        openid = user.openid;
        reserve_status = user.reserve_status;
        using_status = user.using_status;
        isadmin = user.isadmin;
        avatar = user.avatar;
        vip_daypass = user.vip_daypass;
        vip_time = user.vip_time;
        session_key = user.session_key;
        cookie = user.cookie;
        is_using_daypass = user.is_using_daypass;
        overdue_time = user.overdue_time;
    }

    public Boolean isReserved() {
        return reserve_status != NONE;
    }

    public Boolean isUsing() {
        return using_status != NONE;
    }

    @Deprecated
    public void refreshOverDueTime(){
        Timestamp t = new Timestamp(System.currentTimeMillis());
        t.setDate(t.getDate() + 90);
        overdue_time = t;
    }
}

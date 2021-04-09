package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    // TODO：增加reserve_id，-1为空
    @JsonIgnore
    private String openid;

    @ApiModelProperty("0.无状态 1.正在使用时长自习 2.正在使用天卡自习 3.已使用时长预定 4.已使用天卡预定 5.正在使用天卡，但是没有在使用自习室")
    private Integer user_status = 0;

    private Boolean isadmin = false;

    @ApiModelProperty("头像链接")
    private String avatar;

    @ApiModelProperty("天卡剩余时间（天）")
    private Integer vip_daypass = 0;
    @ApiModelProperty("剩余可用时间（秒）")
    private Integer vip_time = 0;

    @JsonIgnore
    @ApiModelProperty("来自微信官方")
    private String session_key;

    @JsonIgnore
    private String cookie;

    public void copyUser(User user){
        this.setOpenid(user.getOpenid());
        this.setUser_status(user.getUser_status());
        this.setIsadmin(user.getIsadmin());
        this.setAvatar(user.getAvatar());
        this.setVip_daypass(user.getVip_daypass());
        this.setVip_time(user.getVip_time());
        this.setSession_key(user.getSession_key());
        this.setCookie(user.getCookie());
    }
}

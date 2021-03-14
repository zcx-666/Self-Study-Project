package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class User implements Serializable {
    @JsonIgnore
    private String openid;

    @ApiModelProperty("当前是否预定了自习桌")
    private Boolean is_reserve = false;

    @ApiModelProperty("头像链接")
    private String avatar;

    @ApiModelProperty("VIP开始时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp vip_start;
    @ApiModelProperty("VIP结束时间\"yyyy-MM-dd hh:mm:ss\"")
    private Timestamp vip_end;

    @JsonIgnore
    @ApiModelProperty("来自微信官方")
    private String session_key;

    @JsonIgnore
    private String cookie;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getVip_start() {
        return vip_start;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Timestamp getVip_end() {
        return vip_end;
    }
}

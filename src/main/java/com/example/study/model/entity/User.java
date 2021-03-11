package com.example.study.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class User {
    @JsonIgnore
    private String openid;

    @ApiModelProperty("当前是否预定了自习桌")
    private Boolean is_reserve = false;

    @ApiModelProperty("头像链接")
    private String avatar;

    @ApiModelProperty("Vip等级,卡片属性待定")
    private Integer vip = 0;

    @JsonIgnore
    private String session_key;

    @JsonIgnore
    private String cookie;
}

package com.example.study.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    @ApiModelProperty("wx.login获得的code")
    private String code;

}

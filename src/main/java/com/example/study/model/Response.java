package com.example.study.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {
    @ApiModelProperty("0成功，负数出错")
    private Integer code;
    @ApiModelProperty("成功为空，出错为json")
    private String msg;
    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(0, null, data);
    }

    public static <T> Response<T> fail(Integer code) {
        String msg;
        switch (code) {
            case -1:
                msg = "未登录或登录凭证过期";
                break;
            case -2:
                msg = "创建新用户失败";
                break;
            case -3:
                msg = "登录失败，服务器断开连接";
                break;
            case -4:
                msg = "您已有一项预定或正在使用自习室";
                break;
            case -5:
                msg = "预定的桌子不存在";
                break;
            case -6:
                msg = "预定的桌子正在被使用";
                break;
            case -7:
                msg = "该时段已被预定";
                break;
            case -8:
                msg = "开始时间必须小于结束时间";
                break;
            case -9:
                msg = "主键重复";
                break;
            case -10:
                msg = "非法的时间格式";
                break;
            case -11:
                msg = "非法的code";
                break;
            case -12:
                msg = "没有权限";
                break;
            case -13:
                msg = "您没有预定过";
                break;
            case -14:
                msg = "该订单已使用或过期";
                break;
            case -15:
                msg = "订单不存在";
                break;
            case -16:
                msg = "用户不存在";
                break;
            default:
                msg = "未知的错误代码";
                break;
        }
        return new Response<>(code, msg, null);
    }

    public static <T> Response<T> fail(Integer code, String msg) {
        return new Response<>(code, msg, null);
    }

    public static <T> Response<T> fail(Integer code, T data) {
        String msg;
        //fail(code);
        switch (code) {
            case -1:
                msg = "未登录或登录凭证过期";
                break;
            case -2:
                msg = "创建新用户失败";
                break;
            case -3:
                msg = "登录失败，服务器断开连接";
                break;
            case -4:
                msg = "您已有一项预定";
                break;
            case -5:
                msg = "预定的桌子不存在";
                break;
            case -6:
                msg = "预定的桌子正在被使用";
                break;
            case -7:
                msg = "该时段已被该订单预定";
                break;
            case -8:
                msg = "开始时间必须小于结束时间";
                break;
            default:
                msg = "未知的错误代码";
                break;
        }
        return new Response<>(code, msg, data);
    }
}

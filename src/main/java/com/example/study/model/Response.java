package com.example.study.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
public class Response<T> {
    @ApiModelProperty("0成功，负数出错")
    private Integer code;
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
                // TODO:  msg = "您已经预定过了";
                msg = "您已有一项预定或正在使用自习室";
                break;
            case -5:
                msg = "桌子不存在";
                break;
            case -6:
                msg = "桌子正在被使用";
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
                msg = "您没有预定";
                break;
            case -14:
                msg = "该订单不可取消";
                break;
            case -15:
                msg = "订单不存在";
                break;
            case -16:
                msg = "用户不存在";
                break;
            case -17:
                msg = "删除失败";
                break;
            case -18:
                msg = "桌子存在预定";
                break;
            case -19:
                msg = "开始、结束时间必须在同一天";
                break;
            case -20:
                msg = "预定的时间太早了";
                break;
            case -21:
                msg = "预定时长必须大于40分钟";
                break;
            case -22:
                msg = "余额不足";
                break;
            case -23:
                msg = "您正在使用自习室";
                break;
            case -24:
                msg = "您预定的时间段不是现在。如果想提前入座，请确认在您预定的时间段之前没有其他用户的预定";
                break;
            case -25:
                msg = "您预定的桌子不是这张";
                break;
            case -26:
                msg = "时间太晚了";
                break;
            case -27:
                msg = "桌子表为空";
                break;
            case -28:
                msg = "不是您的订单";
                break;
            case -29:
                msg = "您现在没有在使用自习桌";
                break;
            case -30:
                msg = "订单不在使用中";
                break;
            case -31:
                msg = "桌子不在使用中";
                break;
            case -32:
                msg = "预定时长必须大于40分钟";
                break;
            case -33:
                msg = "不在上班时间";
                break;
            case -34:
                msg = "只能预定一周内的时间";
                break;
            case -35:
                msg = "judgeReserveTime方法参数错误";
                break;
            case -36:
                msg = "用户状态为预定，但是找不到预定数据";
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
        Response response = fail(code);
        response.setData(data);
        if(data != null){
            log.error("错误代码:{},错误信息:{},错误数据:{}",code, response.msg, data);
        }
        return response;
    }
}

package com.example.study.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class CallBackRequest {

    // 2015-05-20T13:29:35+08:00
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss",timezone="GMT+8")
    public Timestamp date;
}

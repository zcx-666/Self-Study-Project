package com.example.study.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CancleRequest {
    @NotNull
    private Integer reserve_id;
}

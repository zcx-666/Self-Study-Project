package com.example.study.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class FinishRequest {
    @NotNull
    private Integer reserve_id;

    /*@NotNull
    private Integer table_id;*/
}

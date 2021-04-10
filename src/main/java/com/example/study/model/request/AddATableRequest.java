package com.example.study.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddATableRequest {

    @NotNull
    private Integer table_id;
}

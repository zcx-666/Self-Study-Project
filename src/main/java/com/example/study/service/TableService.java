package com.example.study.service;

import com.example.study.mapper.TableMapper;
import com.example.study.model.entity.Table;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;

@Service
public class TableService {

    @Resource
    private TableMapper tableMapper;

    public Table selectTableByTableId(Integer table_id){
        return tableMapper.selectTableByTableId(table_id);
    }

    public void insertNewTable(Table table) throws SQLIntegrityConstraintViolationException {
        try {
            tableMapper.insertNewTable(table.getTable_id(), table.getIs_reserve(), table.getIs_using());
        } catch (Exception e) {
            throw new SQLIntegrityConstraintViolationException(e);
        }
    }

    public void updateTableReserveState(Table table) {
        tableMapper.updateTableReserveState(table.getTable_id(),table.getIs_reserve());
    }

    public Integer deleteTableById(Integer table_id) {
        return tableMapper.deleteTableById(table_id);
    }
}

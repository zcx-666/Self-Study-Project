package com.example.study.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.Table;
import com.example.study.model.request.AddATableRequest;
import com.example.study.model.request.AddTablesRequest;
import com.example.study.service.TableService;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;

@RestController
@Api(tags = "桌子接口")
public class TableController {

    @Resource
    private TableService tableService;

    @Resource
    private UserService userService;

    private String cookie_name = ResourceBundle.getBundle("string").getString("cookie_name");

    @PostMapping("/addTable")
    @ApiOperation(value = "增加一张桌子",notes = "只能管理员账户使用（未完成）,table_id == 0 || table_id == null 则使用数据库自动获取的id，如果table_id >= 1则添加Id")
    public Response<Table> addANewTable(@RequestBody AddATableRequest addATableRequest, HttpServletRequest httpServletRequest){
        /*User user = userService.selectUserByCookie(httpServletRequest);
        if(user == null){
            return Response.fail(-1);
        }
        Table table = new Table(table_id);
        tableService.insertNewTable(table);*/
        Table table = new Table();
        table.setTable_id(addATableRequest.getTable_id());
        try {
            tableService.insertNewTable(table);
        } catch (SQLIntegrityConstraintViolationException e){
            e.printStackTrace();
            return Response.fail(-9);
        }
        return Response.success(table);
    }

    @PostMapping("/addTables")
    @ApiOperation(value = "增加多张桌子",notes = "只能管理员账户使用（未完成）,table_id == 0 || table_id == null 则使用数据库自动获取的id，如果table_id >= 1则添加Id")
    public Response<Table> addNewTables(@RequestBody AddTablesRequest addTablesRequest, HttpServletRequest httpServletRequest, HttpServletResponse response){
        /*User user = userService.selectUserByCookie(httpServletRequest);
        if(user == null){
            return Response.fail(-1);
        }
        Table table = new Table(table_id);
        tableService.insertNewTable(table);*/
        response.addCookie(new Cookie("test", "123"));
        Table table = new Table();
        for (int i = 0; i < addTablesRequest.getTable_count(); i++){
            try {
                tableService.insertNewTable(table);
            } catch (SQLIntegrityConstraintViolationException e){
                e.printStackTrace();
                return Response.fail(-9);
            }
        }
        return Response.success(table);
    }
}
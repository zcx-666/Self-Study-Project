package com.example.study.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.study.model.Response;
import com.example.study.model.entity.Reserve;
import com.example.study.model.entity.Table;
import com.example.study.model.entity.User;
import com.example.study.model.request.AddATableRequest;
import com.example.study.model.request.AddTablesRequest;
import com.example.study.service.ReserveService;
import com.example.study.service.TableService;
import com.example.study.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@Api(tags = "桌子接口")
public class TableController {

    @Resource
    private TableService tableService;

    @Resource
    private UserService userService;

    @Resource
    private ReserveService reserveService;

    private String cookie_name = ResourceBundle.getBundle("string").getString("cookie_name");

    @PostMapping("/addTable")
    @ApiOperation(value = "增加一张桌子",notes = "只能管理员账户使用,table_id == 0 || table_id == null 则使用数据库自动获取的id，如果table_id >= 1则添加Id")
    public Response<Table> addANewTable(@RequestBody AddATableRequest addATableRequest, HttpServletRequest httpServletRequest){
        User admin = userService.selectUserByCookie(httpServletRequest);
        if(admin == null){
            return Response.fail(-1);
        }
        if(!admin.getIsadmin()){
            return Response.fail(-12);
        }
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
    @ApiOperation(value = "增加多张桌子",notes = "只能管理员账户使用,输入增加的数量")
    public Response<Table> addNewTables(@RequestBody AddTablesRequest addTablesRequest, HttpServletRequest httpServletRequest){
        User user = userService.selectUserByCookie(httpServletRequest);
        if(user == null){
            return Response.fail(-1);
        }
        if(!user.getIsadmin()){
            return Response.fail(-12);
        }
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

    /*@GetMapping("/deleteTable")
    @ApiOperation(value = "删除桌子", notes = "不建议使用")
    // TODO: 管理员
    public Response<Integer> deleteTable(@RequestParam Integer table_id, HttpServletRequest request){
        User admin = new User();
        Integer code = userService.judgeAdmin(request, admin);
        if(0 != code){
            return Response.fail(code);
        }
        Table table = tableService.selectTableByTableId(table_id);
        if(table == null){
            return Response.fail(-5);
        }
        if(table.getIs_using()){
            return Response.fail(-6);
        }
        // 桌子是否有未使用的预定
        List<Reserve> reserves;
        reserves = reserveService.searchReserveByTableId(table_id);
        if(0 != reserves.size()){
            for(Reserve reserve : reserves){
                Integer status = reserve.getReserve_status();
                if(status == 4 || status == 2){
                    return Response.fail(-18);
                }
            }
        }
        Integer num = tableService.deleteTableById(table_id);
        if(0 == num){
            return Response.fail(-17);
        }
        return Response.success(table_id);
    }*/
}

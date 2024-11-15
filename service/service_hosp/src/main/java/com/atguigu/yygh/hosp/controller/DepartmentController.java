package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "科室信息管理")
@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //根据医院编号查询所有科室的列表
    @ApiOperation(value = "根据医院编号查询所有科室的列表")
    @GetMapping("getDeptList/{hosCode}")
    public Result getDeptList(@PathVariable String hosCode) {
        List<DepartmentVo> deptTree = departmentService.findDeptTree(hosCode);
        return Result.ok(deptTree);
    }
}

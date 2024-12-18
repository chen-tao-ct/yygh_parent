package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医院详细信息")
@RestController
@RequestMapping("/admin/hosp/hospital")
// @CrossOrigin
public class HospitalController {

  @Autowired private HospitalService hospitalService;

  @Autowired private ScheduleService scheduleService;

  // 医院列表(条件查询分页)
  @ApiOperation(value = "医院列表(条件查询分页)")
  @GetMapping("list/{page}/{limit}")
  public Result listHosp(
      @PathVariable Integer page, @PathVariable Integer limit, HospitalQueryVo hospitalQueryVo) {
    Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
    return Result.ok(pageModel);
  }

  // 更新医院上线状态
  @ApiOperation(value = "更新医院上线状态")
  @GetMapping("updateHospStatus/{id}/{status}")
  public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status) {
    hospitalService.updateStatus(id, status);
    return Result.ok();
  }

  // 医院详情信息
  @ApiOperation(value = "医院详情信息")
  @GetMapping("showHospDetail/{id}")
  public Result showHospDetail(@PathVariable String id) {
    Map<String, Object> map = hospitalService.getHospById(id);
    return Result.ok(map);
  }
}

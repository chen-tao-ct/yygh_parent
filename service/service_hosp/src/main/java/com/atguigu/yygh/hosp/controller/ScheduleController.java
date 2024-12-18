package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "排班信息管理")
@RestController
@RequestMapping("/admin/hosp/schedule")
// @CrossOrigin
public class ScheduleController {

  @Autowired private ScheduleService scheduleService;

  // 根据医院编号和科室编号查询排班规则数据
  @ApiOperation(value = "根据医院编号和科室编号查询排班规则数据")
  @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
  public Result getScheduleRule(
      @PathVariable Long page,
      @PathVariable Long limit,
      @PathVariable String hoscode,
      @PathVariable String depcode) {
    Map<String, Object> ruleSchedule =
        scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
    return Result.ok(ruleSchedule);
  }

  // 根据医院编号和科室编号和日期查询排班详细信息
  @ApiOperation(value = "根据医院编号和科室编号和日期查询排班详细信息")
  @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
  public Result getScheduleDetail(
      @PathVariable String hoscode, @PathVariable String depcode, @PathVariable String workDate) {
    List<Schedule> detailSchedule = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
    return Result.ok(detailSchedule);
  }
}

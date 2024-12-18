package com.atguigu.yygh.hosp;

import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hosp")
@Repository
public interface HospitalFeignClient {
  /** 根据排班id获取预约下单数据 */
  @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
  ScheduleOrderVo getScheduleOrderVo(@PathVariable(value = "scheduleId") String scheduleId);

  /** 获取医院签名信息 */
  @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
  SignInfoVo getSignInfoVo(@PathVariable(value = "hoscode") String hoscode);
}

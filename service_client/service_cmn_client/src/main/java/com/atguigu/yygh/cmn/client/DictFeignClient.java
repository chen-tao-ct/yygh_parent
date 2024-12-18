package com.atguigu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Service
public interface DictFeignClient {

  // 根据dictcode和value查询
  @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
  public String getName(
      @PathVariable(value = "dictCode") String dictCode,
      @PathVariable(value = "value") String value);

  // 根据value查询
  @GetMapping("/admin/cmn/dict/getName/{value}")
  public String getName(@PathVariable(value = "value") String value);
}

package com.atguigu.yygh.vo.msm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Data;

@Data
@ApiModel(description = "短信实体")
public class MsmVo {

  @ApiModelProperty(value = "phone")
  private String phone;

  @ApiModelProperty(value = "短信模板code")
  private String templateCode;

  @ApiModelProperty(value = "短信模板参数")
  private Map<String, Object> param;
}

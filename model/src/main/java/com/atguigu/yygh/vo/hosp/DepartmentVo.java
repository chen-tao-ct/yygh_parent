package com.atguigu.yygh.vo.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(description = "Department")
public class DepartmentVo {

  @ApiModelProperty(value = "科室编号")
  private String depcode;

  @ApiModelProperty(value = "科室名称")
  private String depname;

  @ApiModelProperty(value = "下级节点")
  private List<DepartmentVo> children;
}

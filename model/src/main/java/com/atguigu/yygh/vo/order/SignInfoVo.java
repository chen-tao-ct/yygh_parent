package com.atguigu.yygh.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * HospitalSet
 *
 * @author qy
 */
@Data
@ApiModel(description = "签名信息")
public class SignInfoVo implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "api基础路径")
  private String apiUrl;

  @ApiModelProperty(value = "签名秘钥")
  private String signKey;
}

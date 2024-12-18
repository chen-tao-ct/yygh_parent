package com.atguigu.yygh.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色查询实体
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
@ApiModel(description = "角色查询实体")
public class RoleQueryVo implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "角色名称")
  private String roleName;
}

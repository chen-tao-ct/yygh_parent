package com.atguigu.yygh.model.acl;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
@ApiModel(description = "角色")
@TableName("acl_role")
public class Role extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "角色名称")
  @TableField("role_name")
  private String roleName;

  @ApiModelProperty(value = "备注")
  @TableField("remark")
  private String remark;
}

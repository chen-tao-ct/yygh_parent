package com.atguigu.yygh.model.acl;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户角色
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
@ApiModel(description = "用户角色")
@TableName("acl_user_role")
public class UserRole extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "角色id")
  @TableField("role_id")
  private Long roleId;

  @ApiModelProperty(value = "用户id")
  @TableField("user_id")
  private Long userId;
}

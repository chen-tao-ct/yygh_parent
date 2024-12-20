package com.atguigu.yygh.user.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {

  @Autowired private UserInfoMapper userInfoMapper;

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private PatientService patientService;

  @Override
  public Map<String, Object> login(LoginVo loginVo) {
    String phone = loginVo.getPhone();
    String code = loginVo.getCode();

    // 校验参数
    if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
      throw new YyghException(ResultCodeEnum.PARAM_ERROR);
    }

    // 校验校验验证码
    String redisCode = (String) redisTemplate.opsForValue().get(phone);
    if (!code.equals(redisCode)) {
      throw new YyghException(ResultCodeEnum.CODE_ERROR);
    }

    // 绑定手机号码
    UserInfo userInfo = null;
    if (!StringUtils.isEmpty(loginVo.getOpenid())) {
      QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("openid", loginVo.getOpenid());
      userInfo = userInfoMapper.selectOne(queryWrapper);
      if (null != userInfo) {
        userInfo.setPhone(loginVo.getPhone());
        userInfoMapper.updateById(userInfo);
      } else {
        throw new YyghException(ResultCodeEnum.DATA_ERROR);
      }
    }

    if (null == userInfo) {
      // 手机号已被使用
      QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("phone", phone);
      // 获取会员
      userInfo = userInfoMapper.selectOne(queryWrapper);
      if (null == userInfo) {
        userInfo = new UserInfo();
        userInfo.setName("");
        userInfo.setPhone(phone);
        userInfo.setStatus(1);
        this.save(userInfo);
      }
    }
    // 校验是否被禁用
    if (userInfo.getStatus() == 0) {
      throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
    }

    // 返回页面显示名称
    Map<String, Object> map = new HashMap<>();
    String name = userInfo.getName();
    if (StringUtils.isEmpty(name)) {
      name = userInfo.getNickName();
    }
    if (StringUtils.isEmpty(name)) {
      name = userInfo.getPhone();
    }
    map.put("name", name);
    String token = JwtHelper.createToken(userInfo.getId(), name);
    map.put("token", token);
    return map;
  }

  @Override
  public void userAuth(Long userId, UserAuthVo userAuthVo) {
    // 根据用户id查询用户信息
    UserInfo userInfo = userInfoMapper.selectById(userId);
    // 设置认证信息
    // 认证人姓名
    userInfo.setName(userAuthVo.getName());
    // 其他认证信息
    userInfo.setCertificatesType(userAuthVo.getCertificatesType());
    userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
    userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
    userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
    // 进行信息更新
    userInfoMapper.updateById(userInfo);
  }

  // 用户列表（条件查询带分页）
  @Override
  public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
    // UserInfoQueryVo获取条件值
    String name = userInfoQueryVo.getKeyword(); // 用户名称
    Integer status = userInfoQueryVo.getStatus(); // 用户状态
    Integer authStatus = userInfoQueryVo.getAuthStatus(); // 认证状态
    String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); // 开始时间
    String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); // 结束时间
    // 对条件值进行非空判断
    QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
    if (!org.springframework.util.StringUtils.isEmpty(name)) {
      wrapper.like("name", name);
    }
    if (!org.springframework.util.StringUtils.isEmpty(status)) {
      wrapper.eq("status", status);
    }
    if (!org.springframework.util.StringUtils.isEmpty(authStatus)) {
      wrapper.eq("auth_status", authStatus);
    }
    if (!org.springframework.util.StringUtils.isEmpty(createTimeBegin)) {
      wrapper.ge("create_time", createTimeBegin);
    }
    if (!org.springframework.util.StringUtils.isEmpty(createTimeEnd)) {
      wrapper.le("create_time", createTimeEnd);
    }
    // 调用mapper的方法
    IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
    // 编号变成对应值封装
    pages.getRecords().stream()
        .forEach(
            item -> {
              this.packageUserInfo(item);
            });
    return pages;
  }

  @Override
  public void lock(Long userId, Integer status) {
    if (status.intValue() == 0 || status.intValue() == 1) {
      UserInfo userInfo = userInfoMapper.selectById(userId);
      userInfo.setStatus(status);
      userInfoMapper.updateById(userInfo);
    }
  }

  // 用户详情
  @Override
  public Map<String, Object> show(Long userId) {
    Map<String, Object> map = new HashMap<>();
    // 根据userid查询用户信息
    UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
    map.put("userInfo", userInfo);
    // 根据userid查询就诊人信息
    List<Patient> patientList = patientService.findAllUserById(userId);
    map.put("patientList", patientList);
    return map;
  }

  // 认证审批  2通过  -1不通过
  @Override
  public void approval(Long userId, Integer authStatus) {
    if (authStatus.intValue() == 2 || authStatus.intValue() == -1) {
      UserInfo userInfo = baseMapper.selectById(userId);
      userInfo.setAuthStatus(authStatus);
      baseMapper.updateById(userInfo);
    }
  }

  // 编号变成对应值封装
  private UserInfo packageUserInfo(UserInfo userInfo) {
    // 处理认证状态编码
    userInfo
        .getParam()
        .put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
    // 处理用户状态 0  1
    String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
    userInfo.getParam().put("statusString", statusString);
    return userInfo;
  }
}

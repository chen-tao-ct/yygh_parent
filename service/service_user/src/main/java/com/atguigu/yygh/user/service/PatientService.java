package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PatientService extends IService<Patient> {

    //获取就诊人列表
    List<Patient> findAllUserById(Long userId);

    //根据id获取就诊人信息
    Patient getPatientById(Long id);



}

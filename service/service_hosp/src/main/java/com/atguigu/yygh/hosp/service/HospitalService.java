package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface HospitalService {
  // 上传医院接口
  void save(Map<String, Object> paramMap);

  // 根据医院编号查询
  Hospital getHospitalByHoscode(String hoscode);

  // 医院列表(条件查询分页)
  Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

  // 更新医院上线状态
  void updateStatus(String id, Integer status);

  // 医院详情信息
  Map<String, Object> getHospById(String id);

  // 获取医院名称
  String getHospName(String hoscode);

  // 根据医院名称查询
  List<Hospital> findByHosname(String hosname);

  // 根据医院编号获取医院预约挂号详情
  Map<String, Object> item(String hoscode);
}

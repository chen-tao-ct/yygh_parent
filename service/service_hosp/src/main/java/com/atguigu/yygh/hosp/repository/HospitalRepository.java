package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
  // 判断是否存在数据
  Hospital getHospitalByHoscode(String hoscode);

  // 根据医院名称查询
  List<Hospital> findHospitalByHosnameLike(String hosname);
}

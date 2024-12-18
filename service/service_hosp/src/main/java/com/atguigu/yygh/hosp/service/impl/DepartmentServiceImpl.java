package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl implements DepartmentService {

  @Autowired private DepartmentRepository departmentRepository;

  // 查询科室接口
  @Override
  public Page<Department> findPageDepartment(
      int page, int limit, DepartmentQueryVo departmentQueryVo) {
    // 创建Pageable对象，设置当前页和每页记录数
    // 0是第一页
    Pageable pageable = PageRequest.of(page - 1, limit);
    // 创建Example对象
    Department department = new Department();
    BeanUtils.copyProperties(departmentQueryVo, department);
    department.setIsDeleted(0);

    ExampleMatcher matcher =
        ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase(true);
    Example<Department> example = Example.of(department, matcher);

    Page<Department> all = departmentRepository.findAll(example, pageable);
    return all;
  }

  // 上传科室接口
  @Override
  public void save(Map<String, Object> paramMap) {
    // paramMap 转换department对象
    String paramMapString = JSONObject.toJSONString(paramMap);
    Department department = JSONObject.parseObject(paramMapString, Department.class);

    // 根据医院编号 和 科室编号查询
    Department departmentExist =
        departmentRepository.getDepartmentByHoscodeAndDepcode(
            department.getHoscode(), department.getDepcode());
    // 判断
    if (departmentExist != null) {
      departmentExist.setUpdateTime(new Date());
      departmentExist.setIsDeleted(0);
      departmentRepository.save(departmentExist);
    } else {
      department.setCreateTime(new Date());
      department.setUpdateTime(new Date());
      department.setIsDeleted(0);
      departmentRepository.save(department);
    }
  }

  // 删除科室接口
  @Override
  public void remove(String hoscode, String depcode) {
    // 根据医院编号 和 科室编号查询
    Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    if (department != null) {
      // 调用方法删除
      departmentRepository.deleteById(department.getId());
    }
  }

  // 根据医院编号，查询医院所有科室列表
  @Override
  public List<DepartmentVo> findDeptTree(String hoscode) {
    // 创建list集合，用于最终数据封装
    List<DepartmentVo> result = new ArrayList<>();

    // 根据医院编号，查询医院所有科室信息
    Department departmentQuery = new Department();
    departmentQuery.setHoscode(hoscode);
    Example example = Example.of(departmentQuery);
    // 所有科室列表 departmentList
    List<Department> departmentList = departmentRepository.findAll(example);
    // 根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
    Map<String, List<Department>> departmentMap =
        departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
    // 遍历map集合 departmentMap
    for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
      // 大科室编号
      String bigcode = entry.getKey();
      // 大科室编号对应的全局数据
      List<Department> department1List = entry.getValue();
      // 封装大科室
      DepartmentVo departmentVo1 = new DepartmentVo();
      departmentVo1.setDepcode(bigcode);
      departmentVo1.setDepname(department1List.get(0).getBigname());

      // 封装小科室
      List<DepartmentVo> children = new ArrayList<>();
      for (Department department : department1List) {
        DepartmentVo departmentVo2 = new DepartmentVo();
        departmentVo2.setDepcode(department.getDepcode());
        departmentVo2.setDepname(department.getDepname());
        // 封装到list集合
        children.add(departmentVo2);
      }
      // 把小科室list集合放到大科室children里面
      departmentVo1.setChildren(children);
      // 放到最终result里面
      result.add(departmentVo1);
    }
    // 返回
    return result;
  }

  // 根据科室编号，和医院编号，查询科室名称
  @Override
  public String getDepName(String hoscode, String depcode) {
    Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    if (department != null) {
      return department.getDepname();
    }
    return null;
  }

  @Override
  public Department getDepartment(String hoscode, String depcode) {
    return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
  }
  //
  //    public static void main(String[] args) {
  //        Map<String, Object> map = new LinkedHashMap<>();
  //        map.put("hoscode",123);
  //        map.put("depcode",12);
  //        map.put("depname",12322);
  //        map.put("bigname",121245);
  //        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
  //            System.out.print(stringObjectEntry.getKey()+"---");
  //            System.out.println(stringObjectEntry.getValue());
  //        }
  //        System.out.println(map);
  //        System.out.println(JSONObject.toJSONString(map));
  //        String s = JSONObject.toJSONString(map);
  //        Department department  = JSONObject.parseObject(s, Department.class);
  //        System.out.println(department);
  //        System.out.println("-------------------------------");
  //
  //        List<String> list = new ArrayList<>();
  //        list.add("asda");
  //        list.add("dsf");
  //        list.add("gfh");
  //        list.add("asda");
  //        for (String s1 : list) {
  //            System.out.println(s1);
  //        }
  //        System.out.println(list);
  //        Object[] objects = list.toArray();
  //        System.out.println(Arrays.toString(objects));
  //        String[] strings = list.toArray(new String[list.size()]);
  //        System.out.println(Arrays.toString(strings));
  //    }
}

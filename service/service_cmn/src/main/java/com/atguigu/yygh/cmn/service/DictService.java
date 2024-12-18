package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DictService extends IService<Dict> {

  // 根据数据id查询子数据列表
  List<Dict> findChlidData(Long id);

  // 导出数据字典
  void exportDictData(HttpServletResponse response);

  // 导入数据字典
  void importDictData(MultipartFile file);

  // 根据dictcode和value查询
  String getDictName(String dictCode, String value);

  // 根据dictCode获取下级节点
  List<Dict> findByDictCode(String dictCode);
}

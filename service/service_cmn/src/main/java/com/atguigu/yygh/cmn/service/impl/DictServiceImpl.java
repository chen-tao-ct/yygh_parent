package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

  @Autowired private DictMapper dictMapper;

  // 根据数据id查询子数据列表
  @Override
  //    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
  public List<Dict> findChlidData(Long id) {
    QueryWrapper<Dict> wrapper = new QueryWrapper<>();
    wrapper.eq("parent_id", id);
    List<Dict> dictList = dictMapper.selectList(wrapper);
    // 向list集合每个dict对象中设置hasChildren
    for (Dict dict : dictList) {
      Long dictId = dict.getId();
      boolean isChild = this.isChildren(dictId);
      dict.setHasChildren(isChild);
    }
    return dictList;
  }

  // 导出数据字典接口
  @Override
  public void exportDictData(HttpServletResponse response) {
    // 设置下载信息
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    String fileName = "dict";
    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    // 查询数据库
    List<Dict> dictList = dictMapper.selectList(null);
    // Dict -- DictEeVo
    List<DictEeVo> dictVoList = new ArrayList<>();
    for (Dict dict : dictList) {
      DictEeVo dictEeVo = new DictEeVo();
      BeanUtils.copyProperties(dict, dictEeVo);
      dictVoList.add(dictEeVo);
    }
    // 调用方法进行写操作
    try {
      EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(dictVoList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 导入数据字典
  @Override
  @CacheEvict(value = "dict", allEntries = true)
  public void importDictData(MultipartFile file) {
    try {
      EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener()).sheet().doRead();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 根据dictcode和value查询
  @Override
  public String getDictName(String dictCode, String value) {
    // 如果dictCode为空，直接根据value查询
    if (StringUtils.isEmpty(dictCode)) {
      // 直接根据value查询
      QueryWrapper<Dict> wrapper = new QueryWrapper<>();
      wrapper.eq("value", value);
      Dict dict = dictMapper.selectOne(wrapper);
      return dict.getName();
    } else { // 如果dictCode不为空，根据dictCode和value查询
      // 根据dictcode查询dict对象，得到dict的id值
      Dict dict = this.getDictByDictCode(dictCode);
      Long parent_id = dict.getId();
      // 根据parent_id和value进行查询
      Dict finalDict =
          dictMapper.selectOne(
              new QueryWrapper<Dict>().eq("parent_id", parent_id).eq("value", value));
      return finalDict.getName();
    }
  }

  // 根据dictCode获取下级节点
  @Override
  public List<Dict> findByDictCode(String dictCode) {
    // 根据dictcode获取对应id
    Dict dict = this.getDictByDictCode(dictCode);
    // 根据id获取子节点
    List<Dict> chlidData = this.findChlidData(dict.getId());
    return chlidData;
  }

  private Dict getDictByDictCode(String dictCode) {
    QueryWrapper<Dict> wrapper = new QueryWrapper<>();
    wrapper.eq("dict_code", dictCode);
    Dict dict = dictMapper.selectOne(wrapper);
    return dict;
  }

  // 判断id下面是否有子节点
  private boolean isChildren(Long id) {
    QueryWrapper<Dict> wrapper = new QueryWrapper<>();
    wrapper.eq("parent_id", id);
    Integer count = dictMapper.selectCount(wrapper);
    return count > 0;
  }
}

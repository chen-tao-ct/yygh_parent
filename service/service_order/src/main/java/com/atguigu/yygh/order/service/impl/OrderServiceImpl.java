package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderMapper;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

  @Autowired private PatientFeignClient patientFeignClient;

  @Autowired private HospitalFeignClient hospitalFeignClient;

  // 生成挂号订单
  @Override
  public Long saveOrder(String scheduleId, Long patientId) {
    // 获取就诊人信息
    Patient patient = patientFeignClient.getPatientOrder(patientId);

    // 获取排班相关信息
    ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

    // 判断当前时间是否还可以预约
    if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
        || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
      throw new YyghException(ResultCodeEnum.TIME_NO);
    }

    // 获取签名信息
    SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

    // 添加到订单表
    OrderInfo orderInfo = new OrderInfo();
    // scheduleOrderVo 数据复制到 orderInfo
    BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
    // 向orderInfo设置其他数据
    String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
    orderInfo.setOutTradeNo(outTradeNo);
    orderInfo.setScheduleId(scheduleId);
    orderInfo.setUserId(patient.getUserId());
    orderInfo.setPatientId(patientId);
    orderInfo.setPatientName(patient.getName());
    orderInfo.setPatientPhone(patient.getPhone());
    orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
    baseMapper.insert(orderInfo);
    return orderInfo.getId();
  }

  // 根据订单id查询订单详情
  @Override
  public OrderInfo getOrder(String orderId) {
    OrderInfo orderInfo = baseMapper.selectById(orderId);
    return this.packOrderInfo(orderInfo);
  }

  // 订单列表（条件查询带分页）
  @Override
  public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
    // orderQueryVo获取条件值
    String name = orderQueryVo.getKeyword(); // 医院名称
    Long patientId = orderQueryVo.getPatientId(); // 就诊人名称
    String orderStatus = orderQueryVo.getOrderStatus(); // 订单状态
    String reserveDate = orderQueryVo.getReserveDate(); // 安排时间
    String createTimeBegin = orderQueryVo.getCreateTimeBegin();
    String createTimeEnd = orderQueryVo.getCreateTimeEnd();

    // 对条件值进行非空判断
    QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
    if (!StringUtils.isEmpty(name)) {
      wrapper.like("hosname", name);
    }
    if (!StringUtils.isEmpty(patientId)) {
      wrapper.eq("patient_id", patientId);
    }
    if (!StringUtils.isEmpty(orderStatus)) {
      wrapper.eq("order_status", orderStatus);
    }
    if (!StringUtils.isEmpty(reserveDate)) {
      wrapper.ge("reserve_date", reserveDate);
    }
    if (!StringUtils.isEmpty(createTimeBegin)) {
      wrapper.ge("create_time", createTimeBegin);
    }
    if (!StringUtils.isEmpty(createTimeEnd)) {
      wrapper.le("create_time", createTimeEnd);
    }
    // 调用mapper的方法
    IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
    // 编号变成对应值封装
    pages.getRecords().stream()
        .forEach(
            item -> {
              this.packOrderInfo(item);
            });
    return pages;
  }

  @Override
  public Boolean cancelOrder(Long orderId) {
    return null;
  }

  @Override
  public void patientTips() {}

  @Override
  public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
    return null;
  }

  @Override
  public Map<String, Object> show(Long orderId) {
    Map<String, Object> map = new HashMap<>();
    OrderInfo orderInfo = this.packOrderInfo(this.getById(orderId));
    map.put("orderInfo", orderInfo);
    Patient patient = patientFeignClient.getPatientOrder(orderInfo.getPatientId());
    map.put("patient", patient);
    return map;
  }

  // 取消预约
  //    @Override
  //    public Boolean cancelOrder(Long orderId) {
  //        //获取订单信息
  //        OrderInfo orderInfo = baseMapper.selectById(orderId);
  //        //判断是否取消
  //        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
  //        if(quitTime.isBeforeNow()) {
  //            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
  //        }
  //        //调用医院接口实现预约取消
  //        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
  //        if(null == signInfoVo) {
  //            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
  //        }
  //        Map<String, Object> reqMap = new HashMap<>();
  //        reqMap.put("hoscode",orderInfo.getHoscode());
  //        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
  //        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
  //        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
  //        reqMap.put("sign", sign);
  //
  //        JSONObject result = HttpRequestHelper.sendRequest(reqMap,
  //                signInfoVo.getApiUrl()+"/order/updateCancelStatus");
  //        //根据医院接口返回数据
  //        if(result.getInteger("code")!=200) {
  //            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
  //        } else {
  //            //判断当前订单是否可以取消
  //            if(orderInfo.getOrderStatus().intValue() ==
  // OrderStatusEnum.PAID.getStatus().intValue()) {
  //                Boolean isRefund = weixinService.refund(orderId);
  //                if(!isRefund) {
  //                    throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
  //                }
  //                //更新订单状态
  //                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
  //                baseMapper.updateById(orderInfo);
  //
  //                //发送mq更新预约数量
  //                OrderMqVo orderMqVo = new OrderMqVo();
  //                orderMqVo.setScheduleId(orderInfo.getScheduleId());
  //                //短信提示
  //                MsmVo msmVo = new MsmVo();
  //                msmVo.setPhone(orderInfo.getPatientPhone());
  //                String reserveDate = new
  // DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ?
  // "上午": "下午");
  //                Map<String,Object> param = new HashMap<String,Object>(){{
  //                    put("title",
  // orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
  //                    put("reserveDate", reserveDate);
  //                    put("name", orderInfo.getPatientName());
  //                }};
  //                msmVo.setParam(param);
  //                orderMqVo.setMsmVo(msmVo);
  //                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER,
  // orderMqVo);
  //            }
  //        }
  //        return true;
  //    }

  // 就诊通知
  //    @Override
  //    public void patientTips() {
  //        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
  //        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
  //        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
  //        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
  //        for(OrderInfo orderInfo:orderInfoList) {
  //            //短信提示
  //            MsmVo msmVo = new MsmVo();
  //            msmVo.setPhone(orderInfo.getPatientPhone());
  //            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
  // + (orderInfo.getReserveTime()==0 ? "上午": "下午");
  //            Map<String,Object> param = new HashMap<String,Object>(){{
  //                put("title",
  // orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
  //                put("reserveDate", reserveDate);
  //                put("name", orderInfo.getPatientName());
  //            }};
  //            msmVo.setParam(param);
  //            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM,
  // msmVo);
  //        }
  //    }

  // 预约统计
  //    @Override
  //    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
  //        //调用mapper方法得到数据
  //        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
  //
  //        //获取x需要数据 ，日期数据  list集合
  //        List<String> dateList =
  // orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
  //
  //        //获取y需要数据，具体数量  list集合
  //        List<Integer> countList
  // =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
  //
  //        Map<String,Object> map = new HashMap<>();
  //        map.put("dateList",dateList);
  //        map.put("countList",countList);
  //        return map;
  //    }

  private OrderInfo packOrderInfo(OrderInfo orderInfo) {
    orderInfo
        .getParam()
        .put(
            "orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
    return orderInfo;
  }
}

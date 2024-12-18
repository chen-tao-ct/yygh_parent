package com.atguigu.yygh.msm.service.impl;

import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.util.MsmConstantUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class MsmServiceImpl implements MsmService {
  @Override
  public boolean send(String phone, String code) {
    // 判断手机号是否为空
    if (StringUtils.isEmpty(phone)) {
      return false;
    }

    try {
      // 这里是实例化一个Credential，也就是认证对象，参数是密钥对；你要使用肯定要进行认证
      Credential credential =
          new Credential(MsmConstantUtils.SECRET_ID, MsmConstantUtils.SECRET_KEY);

      SmsClient smsClient = new SmsClient(credential, "ap-nanjing");

      // 实例化request封装请求信息
      SendSmsRequest request = new SendSmsRequest();

      String[] phoneNumber = {"86" + phone};
      request.setPhoneNumberSet(phoneNumber); // 设置手机号
      request.setSmsSdkAppid(MsmConstantUtils.APP_ID);
      request.setSign(MsmConstantUtils.SMS_SIGN);
      request.setTemplateID(MsmConstantUtils.TEMPLATE_ID);

      // 生成随机验证码，我的模板内容的参数只有一个
      String[] verificationCode = {code};
      request.setTemplateParamSet(verificationCode);

      // 发送短信
      SendSmsResponse response = smsClient.SendSms(request);
      System.out.println(SendSmsResponse.toJsonString(response));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}

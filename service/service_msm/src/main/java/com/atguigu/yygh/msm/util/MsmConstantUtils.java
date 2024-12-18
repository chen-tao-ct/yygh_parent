package com.atguigu.yygh.msm.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MsmConstantUtils implements InitializingBean {
  @Value("${sms-config.secretId}")
  private String secretID;

  @Value("${sms-config.secretKey}")
  private String secretKey;

  @Value("${sms-config.endPoint}")
  private String endPoint;

  @Value("${sms-config.appId}")
  private String appId;

  @Value("${sms-config.appKey}")
  private String appKey;

  @Value("${sms-config.smsSign}")
  private String smsSign;

  @Value("${sms-config.templateId}")
  private String templateId;

  public static String SECRET_ID;
  public static String SECRET_KEY;
  public static String END_POINT;
  public static String APP_ID;
  public static String APP_KEY;
  public static String SMS_SIGN;
  public static String TEMPLATE_ID;

  @Override
  public void afterPropertiesSet() throws Exception {
    SECRET_ID = secretID;
    SECRET_KEY = secretKey;
    END_POINT = endPoint;
    APP_ID = appId;
    APP_KEY = appKey;
    SMS_SIGN = smsSign;
    TEMPLATE_ID = templateId;
  }
}

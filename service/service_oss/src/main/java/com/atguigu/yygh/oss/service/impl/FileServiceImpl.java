package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
  @Override
  public String upload(MultipartFile file) {
    String endpoint = ConstantOssPropertiesUtils.EDNPOINT;
    String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
    String accessKeySecret = ConstantOssPropertiesUtils.SECRECT;
    String bucketName = ConstantOssPropertiesUtils.BUCKET;
    try {
      // 创建OSSClient实例。
      OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
      // 上传文件流。
      InputStream inputStream = file.getInputStream();
      String fileName = file.getOriginalFilename();
      // 生成随机唯一值，使用uuid，添加到文件名称里面
      String uuid = UUID.randomUUID().toString().replaceAll("-", "");
      fileName = uuid + fileName;
      // 按照当前日期，创建文件夹，上传到创建文件夹里面
      String timeUrl = new DateTime().toString("yyyy/MM/dd");
      fileName = timeUrl + "/" + fileName;
      // 调用方法实现上传
      ossClient.putObject(bucketName, fileName, inputStream);
      // 关闭OSSClient。
      ossClient.shutdown();
      // 上传之后文件路径
      String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
      // 返回
      return url;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

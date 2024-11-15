

import java.io.*;
public class test2 {
    public static void main(String[] args) throws FileNotFoundException {
        File inFile = new File("D:\\1.jpg");
        FileInputStream fileInputStream = new FileInputStream(inFile);
        //获取文件保存路径，此处可配置化，而不是写死，暂时写死
        String filePath = "D://upload//";
        long date = System.currentTimeMillis();
        String name = inFile.getName();
        String suffixName = name.substring(name.lastIndexOf(".")+1);
        String newFileName = date + "."+ suffixName;
        File targetFile = new File(filePath);
        if(!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try{
        FileOutputStream fileOutputStream = new FileOutputStream(filePath + newFileName);
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = fileInputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, length);
}
        fileOutputStream.flush();
        fileOutputStream.close();
        fileInputStream.close();
    }catch(FileNotFoundException e){
        System.out.println("文件不存在异常");
    }catch(IOException ioException) {
            System.out.println("javaIO流异常");
        }

    }
}

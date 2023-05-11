package com.ityanlan.reggie.controller;

import com.ityanlan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传请求
     * @return
     */
    @PostMapping("/upload")
    //file是一个临时文件，如果不转存到指定为止，请求完成后就会消失
    public R<String> upload(MultipartFile file){
        log.info(file.toString()) ;
        //获取原始文件名字
        String originalFilename = file.getOriginalFilename();
        //获取原文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成随机文件名字,拼接原始文件后缀
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象,不存在则创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        //文件转存
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载请求
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //获取输入流
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            //文件写入
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

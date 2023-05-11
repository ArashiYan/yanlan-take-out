package com.ityanlan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.entity.User;
import com.ityanlan.reggie.service.UserService;
import com.ityanlan.reggie.utils.SMSUtils;
import com.ityanlan.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 生成验证码，向手机发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sengMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成随机的6位验证码
            Integer code = ValidateCodeUtils.generateValidateCode(6);
            log.info("code = {}",code);
            //调用阿里云api发送短信

//            SMSUtils.sendMessage("言岚外卖", "模版", "phone手机号","code验证码");

            //将生成的验证码存到session
            session.setAttribute(phone, code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
        String codeInSession = session.getAttribute(phone).toString();

        //进行验证码比对
        if (codeInSession != null&& codeInSession.equals(code)){
            //能够比对成功则登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            //判断是否为新用户，为新用户自动注册
            if (user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}

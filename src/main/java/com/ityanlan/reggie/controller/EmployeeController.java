package com.ityanlan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.entity.Employee;
import com.ityanlan.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         1.将页面提交的密码password进行md5加密处理
         2.根据页面提交所用户名username 查询数据库
         3.如果没有查询到则返回登录失败结果
         4.密码比对，如果不一致则返回登录失效结果
         5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         6.登灵成功，將员工id存入Session并返回登录成功结果
         */
//      1.进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//      2.根据提交的用户名查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);
//        3.判断是否有查询到
        if (emp == null){
            return R.error("账号不存在");
        }
//        4.判断密码是否一致
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
//        5.查看员工状态
        if (emp.getStatus()!=1){
            return R.error("员工已被禁用");
        }
//        6.存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("账号退出成功");
    }


    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}", employee.toString());
        //设置初始密码123456，进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        long empId = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
//        构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName, name);
//        添加查询条件
        wrapper.orderByDesc(Employee::getCreateTime);
//        执行查询
        employeeService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee){

//        Long eId = (Long) httpServletRequest.getSession().getAttribute("employee");
//        employee.setUpdateUser(eId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if (emp!=null){
            return R.success(emp);
        }
        return R.error("没有查询到对应员工");
    }
}

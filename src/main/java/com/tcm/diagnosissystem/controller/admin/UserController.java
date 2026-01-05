 package com.tcm.diagnosissystem.controller.admin;


 import com.tcm.diagnosissystem.common.Result;
 import com.tcm.diagnosissystem.dto.response.admin_patient_doctor.UserInfo;
 import com.tcm.diagnosissystem.entity.User;
 import com.tcm.diagnosissystem.mapper.patient.UserMapper;
 import jakarta.servlet.http.HttpServletRequest;
 import org.springframework.beans.BeanUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.*;

 /**
  * 用户个人中心
  */
 @RestController
 @RequestMapping("/api/user")
 public class UserController {
     @Autowired
     private UserMapper userMapper;

     /**
      * 获取当前用户登录信息
      */
     @GetMapping("/info")
     public Result<UserInfo> getCurrentUserInfo(HttpServletRequest request) {
         //1从拦截器设置的属性获取用户ID
         Long userId = (Long) request.getAttribute("userId");

         if (userId == null) {
             return Result.failed("未登录或Token无效");
         }

         //查询用户信息
         User user = userMapper.selectById(userId);

         if(user == null){
             return Result.failed("用户不存在");
         }
         //转换相应对象
         UserInfo response = new UserInfo();
         BeanUtils.copyProperties(user, response);
         return Result.success(response);
     }

     /**
      * 修改用户信息
      */
     @PutMapping("/info")
     public Result<UserInfo> updateUserInfo(HttpServletRequest request, @RequestBody UserInfo updateRequest) {
         Long userId = (Long) request.getAttribute("userId");
         if (userId == null) {
             return Result.failed("未登录或Token无效");
         }

         //查询当前用户
         User user = userMapper.selectById(userId);

         if (user == null){
             return Result.failed("用户不存在");
         }

         //更新允许修改的字段
         if(updateRequest.getRealName() != null){
             user.setRealName(updateRequest.getRealName());
         }
         if(updateRequest.getEmail() != null){
             user.setEmail(updateRequest.getEmail());
         }
         if(updateRequest.getGender() != null){
             user.setGender(updateRequest.getGender());
         }

         //更新数据库
         userMapper.updateUser(user);

         //返回更新后的信息
         UserInfo response = new UserInfo();
         BeanUtils.copyProperties(user, response);
         return Result.success("修改成功",response);
     }
 }

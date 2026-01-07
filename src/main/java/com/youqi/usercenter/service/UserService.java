package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.UserLoginRequest;
import com.youqi.usercenter.model.dto.UserQueryRequest;
import com.youqi.usercenter.model.dto.UserRegisterRequest;
import com.youqi.usercenter.model.dto.UserRequest;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     */
    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户视图对象
     */
    UserVO getLoginUserVO(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 新增用户
     */
    Long addUser(UserRequest userRequest);

    /**
     * 更新用户信息
     */
    boolean updateUser(UserRequest userRequest);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 禁用用户
     */
    boolean disableUser(Long id);

    /**
     * 启用用户
     */
    boolean enableUser(Long id);

    /**
     * 修改用户角色
     */
    boolean updateUserRole(Long id, String role);

    /**
     * 修改用户密码
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     */
    boolean resetPassword(Long id);

    /**
     * 分页查询用户列表
     */
    Page<UserVO> listUser(UserQueryRequest queryRequest);

    /**
     * 根据id获取用户视图对象
     */
    UserVO getUserVO(Long id);

    /**
     * 构建查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest queryRequest);

    /**
     * 用户是否存在
     */
    boolean isUserExist(Long id);

    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExist(String username);

    /**
     * 检查邮箱是否已存在
     */
    boolean isEmailExist(String email);

    /**
     * 是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);
}
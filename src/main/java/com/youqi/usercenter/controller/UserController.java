package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.UserLoginRequest;
import com.youqi.usercenter.model.dto.UserQueryRequest;
import com.youqi.usercenter.model.dto.UserRegisterRequest;
import com.youqi.usercenter.model.dto.UserRequest;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.model.vo.UserVO;
import com.youqi.usercenter.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册请求体不能为空");
        }

        Long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result, "注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录请求体不能为空");
        }

        UserVO userVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(userVO, "登录成功");
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求对象不能为空");
        }

        boolean result = userService.userLogout(request);
        return ResultUtils.success(result, "注销成功");
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO userVO = userService.getLoginUserVO(request);
        return ResultUtils.success(userVO);
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能新增用户");
//        }
        System.out.println("username=" + userRequest.getUsername());
        System.out.println("password=" + userRequest.getPassword());
        System.out.println("role=" + userRequest.getRole());
        System.out.println("status=" + userRequest.getStatus());



        Long result = userService.addUser(userRequest);
        return ResultUtils.success(result, "用户新增成功");
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能更新用户信息");
//        }

        if (userRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户请求体不能为空");
        }

        boolean result = userService.updateUser(userRequest);
        return ResultUtils.success(result, "用户信息更新成功");
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestParam Long id, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能删除用户");
//        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }

        boolean result = userService.deleteUser(id);
        return ResultUtils.success(result, "用户删除成功");
    }

    /**
     * 禁用用户
     */
    @PostMapping("/disable")
    public BaseResponse<Boolean> disableUser(@RequestParam Long id, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能禁用用户");
//        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }

        boolean result = userService.disableUser(id);
        return ResultUtils.success(result, "用户禁用成功");
    }

    /**
     * 启用用户
     */
    @PostMapping("/enable")
    public BaseResponse<Boolean> enableUser(@RequestParam Long id, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能启用用户");
//        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }

        boolean result = userService.enableUser(id);
        return ResultUtils.success(result, "用户启用成功");
    }

    /**
     * 修改用户角色
     */
    @PostMapping("/update/role")
    public BaseResponse<Boolean> updateUserRole(@RequestParam Long id, @RequestParam String role,
            HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能修改用户角色");
//        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不能为空");
        }

        boolean result = userService.updateUserRole(id, role);
        return ResultUtils.success(result, "用户角色修改成功");
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/update/password")
    public BaseResponse<Boolean> updatePassword(@RequestParam Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest request) {
        // 检查是否是本人或管理员
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(id) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只能修改自己的密码或需要管理员权限");
        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能为空");
        }

        boolean result = userService.updatePassword(id, oldPassword, newPassword);
        return ResultUtils.success(result, "密码修改成功");
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/reset/password")
    public BaseResponse<Boolean> resetPassword(@RequestParam Long id, HttpServletRequest request) {
        // 检查管理员权限
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能重置用户密码");
        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }

        boolean result = userService.resetPassword(id);
        return ResultUtils.success(result, "密码重置成功");
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public BaseResponse<Page<UserVO>> listUser(UserQueryRequest queryRequest, HttpServletRequest request) {
        // 检查管理员权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限才能查看用户列表");
//        }

        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询请求体不能为空");
        }

        // 验证排序参数
        String sortField = queryRequest.getSortField();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(sortField) && !"created_at".equals(sortField)
                && !"updated_at".equals(sortField) && !"last_login_at".equals(sortField)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段只能是created_at、updated_at或last_login_at");
        }
        String sortOrder = queryRequest.getSortOrder();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(sortOrder) && !"ascend".equals(sortOrder)
                && !"descend".equals(sortOrder)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序方向只能是ascend或descend");
        }
        Page<UserVO> userPage = userService.listUser(queryRequest);
        return ResultUtils.success(userPage, "查询成功");
    }

    /**
     * 根据id获取用户信息
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空且必须大于0");
        }

        UserVO userVO = userService.getUserVO(id);
        return ResultUtils.success(userVO, "查询成功");
    }

    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        // 从请求头或Session中获取当前登录用户
        Object userObj = request.getSession().getAttribute("userLoginState");
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        User currentUser = (User) userObj;
        // 返回用户信息（注意：不要返回密码等敏感信息）
        UserVO userVO = userService.getUserVO(currentUser.getId());
        // 无需手动清除密码字段，service 层已脱敏
        return ResultUtils.success(userVO);
    }
}
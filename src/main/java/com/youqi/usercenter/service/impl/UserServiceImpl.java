package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.UserMapper;
import com.youqi.usercenter.model.dto.UserLoginRequest;
import com.youqi.usercenter.model.dto.UserQueryRequest;
import com.youqi.usercenter.model.dto.UserRegisterRequest;
import com.youqi.usercenter.model.dto.UserRequest;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.model.entity.UserStatistics;
import com.youqi.usercenter.model.vo.UserVO;
import com.youqi.usercenter.service.StatisticsService;
import com.youqi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "youqi_blog_system";

    /**
     * 用户登录态键
     */
    private static final String USER_LOGIN_STATE = "userLoginState";

    @Resource
    private UserMapper userMapper;

    @Resource
    private StatisticsService statisticsService;

    @Override
    @Transactional
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册请求参数不能为空");
        }
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        String email = userRegisterRequest.getEmail();
        String nickname = userRegisterRequest.getNickname();

        // 1. 校验
        if (StringUtils.isAnyBlank(username, password, confirmPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名、密码、确认密码不能为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "用户名长度不能少于4位");
        }
        if (password.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "密码长度不能少于4位");
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "两次输入的密码不一致");
        }

        // 2. 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS, "用户名已被注册");
        }

        // 3. 邮箱不能重复
        if (StringUtils.isNotBlank(email)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS, "邮箱已被注册");
            }
        }

        // 4. 加密
        byte[] passwordBytes = (SALT + password).getBytes();
        String encryptPassword = DigestUtils.md5DigestAsHex(passwordBytes);

        // 5. 插入数据
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encryptPassword);
        user.setNickname(StringUtils.isBlank(nickname) ? username : nickname);
        user.setEmail(email);
        user.setRole("user"); // 默认普通用户
        user.setStatus("active"); // 默认正常状态
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库操作异常");
        }

        // 初始化用户统计信息
        statisticsService.initUserStatistics(user.getId());

        return user.getId();
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录请求参数不能为空");
        }

        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        // 校验
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名和密码不能为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "用户名长度不能少于4位");
        }
        if (password.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "密码长度不能少于4位");
        }

        // 加密
        byte[] passwordBytes = (SALT + password).getBytes();
        String encryptPassword = DigestUtils.md5DigestAsHex(passwordBytes);
        log.info("Login username: {}. Encrypted password: {}", username, encryptPassword);

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password_hash", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        // 用户名或密码不匹配
        if (user == null) {
            log.info("user login failed, username cannot match password");
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "用户名或密码不正确");
        }

        // 检查用户状态（区分封禁/禁用）
        if ("banned".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "账号已被封禁，请联系管理员");
        }

        // 更新最后登录时间
        user.setLastLoginAt(new Date());
        userMapper.updateById(user);

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        return this.getUserVO(user.getId());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求对象不能为空");
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }

        User currentUser = (User) userObj;
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "登录状态异常，请重新登录");
        }

        // 从数据库查询（验证用户是否仍存在）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "登录状态异常，用户不存在");
        }

        // 再次校验用户状态
        if ("banned".equals(currentUser.getStatus())) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "账号已被封禁，请联系管理员");
        }

        return currentUser;
    }

    @Override
    public UserVO getLoginUserVO(HttpServletRequest request) {
        User user = this.getLoginUser(request);
        return this.getUserVO(user.getId());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求对象不能为空");
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录，无法执行退出操作");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    @Transactional
    public Long addUser(UserRequest userRequest) {
        if (userRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新增用户请求参数不能为空");
        }

        String username = userRequest.getUsername();
        String password = userRequest.getPassword();

        // 校验
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名和密码不能为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "用户名长度不能少于4位");
        }
        if (password.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "密码长度不能少于4位");
        }

        // 检查用户名是否已存在
        if (isUsernameExist(username)) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS, "用户名已被注册");
        }

        // 加密密码
        byte[] passwordBytes = (SALT + password).getBytes();
        String encryptPassword = DigestUtils.md5DigestAsHex(passwordBytes);

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encryptPassword);
        user.setNickname(StringUtils.isBlank(userRequest.getNickname()) ? username : userRequest.getNickname());
        user.setEmail(userRequest.getEmail());
        user.setAvatar(userRequest.getAvatar());
        user.setRole(StringUtils.isBlank(userRequest.getRole()) ? "user" : userRequest.getRole());
        user.setStatus(StringUtils.isNotBlank(userRequest.getStatus()) ? userRequest.getStatus() : "active");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增用户失败，数据库操作异常");
        }

        // 初始化用户统计信息
        statisticsService.initUserStatistics(user.getId());

        return user.getId();
    }

    @Override
    @Transactional
    public boolean updateUser(UserRequest userRequest) {
        if (userRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户请求参数不能为空");
        }
        String username = userRequest.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名不能为空");
        }

        // 根据用户名查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "待更新的用户不存在");
        }

        // 若更新邮箱，校验邮箱唯一性
        String newEmail = userRequest.getEmail();
        if (StringUtils.isNotBlank(newEmail) && !newEmail.equals(user.getEmail()) && isEmailExist(newEmail)) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS, "邮箱已被其他账号注册");
        }

        // 更新用户信息
        if (StringUtils.isNotBlank(userRequest.getNickname())) {
            user.setNickname(userRequest.getNickname());
        }
        if (StringUtils.isNotBlank(newEmail)) {
            user.setEmail(newEmail);
        }
        if (StringUtils.isNotBlank(userRequest.getAvatar())) {
            user.setAvatar(userRequest.getAvatar());
        }
        if (StringUtils.isNotBlank(userRequest.getStatus())) {
            user.setStatus(userRequest.getStatus());
        }

        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "待删除的用户不存在");
        }

        // 删除用户
        boolean result = userMapper.deleteById(id) > 0;

        // 删除用户统计信息
        if (result) {
            QueryWrapper<UserStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id);
            statisticsService.remove(queryWrapper);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean disableUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "待禁用的用户不存在");
        }

        // 已禁用状态无需重复操作
        if ("banned".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户已处于禁用状态");
        }

        user.setStatus("banned"); // 封禁状态
        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean enableUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "待启用的用户不存在");
        }

        // 已启用状态无需重复操作
        if ("active".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户已处于启用状态");
        }

        user.setStatus("active"); // 正常状态
        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserRole(Long id, String role) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }
        if (StringUtils.isBlank(role)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "角色不能为空");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "待更新角色的用户不存在");
        }

        // 角色未变更无需操作
        if (role.equals(user.getRole())) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户当前已为该角色");
        }

        user.setRole(role);
        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }
        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "旧密码和新密码不能为空");
        }
        if (newPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, "新密码长度不能少于8位");
        }
        // 新旧密码一致校验
        if (oldPassword.equals(newPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "新密码不能与旧密码一致");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 验证旧密码
        byte[] oldPasswordBytes = (SALT + oldPassword).getBytes();
        String oldEncryptPassword = DigestUtils.md5DigestAsHex(oldPasswordBytes);
        if (!oldEncryptPassword.equals(user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "旧密码输入错误");
        }

        // 更新密码
        byte[] newPasswordBytes = (SALT + newPassword).getBytes();
        String newEncryptPassword = DigestUtils.md5DigestAsHex(newPasswordBytes);
        user.setPasswordHash(newEncryptPassword);
        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 重置为默认密码：12345678
        byte[] defaultPasswordBytes = (SALT + "12345678").getBytes();
        String defaultPassword = DigestUtils.md5DigestAsHex(defaultPasswordBytes);
        user.setPasswordHash(defaultPassword);
        user.setUpdatedAt(new Date());

        return userMapper.updateById(user) > 0;
    }

    @Override
    public Page<UserVO> listUser(UserQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询请求参数不能为空");
        }
        if (queryRequest.getCurrent() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "页码必须为正数");
        }
        if (queryRequest.getPageSize() <= 0 || queryRequest.getPageSize() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "每页条数必须在1-100之间");
        }

        Page<User> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StringUtils.isNotBlank(queryRequest.getUsername())) {
            queryWrapper.like("username", queryRequest.getUsername());
        }
        if (StringUtils.isNotBlank(queryRequest.getNickname())) {
            queryWrapper.like("nickname", queryRequest.getNickname());
        }
        if (StringUtils.isNotBlank(queryRequest.getEmail())) {
            queryWrapper.like("email", queryRequest.getEmail());
        }
        if (StringUtils.isNotBlank(queryRequest.getRole())) {
            queryWrapper.eq("role", queryRequest.getRole());
        }
        if (StringUtils.isNotBlank(queryRequest.getStatus())) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }

        // 添加排序逻辑
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        boolean hasValidSort = false;
        if (StringUtils.isNotBlank(sortField)) {
            // 支持created_at和updated_at字段排序
            if ("created_at".equals(sortField) || "updated_at".equals(sortField) || "last_login_at".equals(sortField)) {
                hasValidSort = true;
                if (StringUtils.isNotBlank(sortOrder) && "descend".equals(sortOrder)) {
                    queryWrapper.orderByDesc(sortField);
                } else {
                    queryWrapper.orderByAsc(sortField);
                }
            } else {
                throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "支持排序的字段：created_at、updated_at、last_login_at");
            }
        }
        // 默认按创建时间降序
        if (!hasValidSort) {
            queryWrapper.orderByDesc("created_at");
        }

        Page<User> userPage = this.page(page, queryWrapper);
        Page<UserVO> userVOPage = new Page<>();
        BeanUtils.copyProperties(userPage, userVOPage);
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public UserVO getUserVO(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_VALIDATION_ERROR, "用户ID不能为空且必须为正数");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        return convertToVO(user);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest queryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        String username = queryRequest.getUsername();
        String nickname = queryRequest.getNickname();
        String email = queryRequest.getEmail();
        String role = queryRequest.getRole();
        String status = queryRequest.getStatus();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.like(StringUtils.isNotBlank(nickname), "nickname", nickname);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);

        // 精确查询
        queryWrapper.eq(StringUtils.isNotBlank(role), "role", role);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);

        // 默认按创建时间倒序
        queryWrapper.orderByDesc("created_at");

        return queryWrapper;
    }

    @Override
    public boolean isUserExist(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return userMapper.selectById(id) != null;
    }

    @Override
    public boolean isUsernameExist(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isEmailExist(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = this.getLoginUser(request);
        return isAdmin(user);
    }

    /**
     * 转换为用户VO对象
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
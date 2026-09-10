package com.xjteam4.envMgr.service;

import com.xjteam4.envMgr.domain.SysUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public interface SysUserService {
    SysUserEntity queryById(Integer userId);

    Page<SysUserEntity> queryByPage(SysUserEntity sysUser, Integer page, Integer size, String orderCol, String orderDirect);

    boolean insert(SysUserEntity sysUser);

    boolean update(SysUserEntity sysUser);

    boolean deleteById(Integer userId);

    SysUserEntity login(SysUserEntity sysUser);

    /**
     * 查询用户是否存在
     */
    SysUserEntity queryByUserPhone(SysUserEntity sysUser);

    /**
     * 修改密码
     */
    boolean updatePwd(Integer userId, String oldPwd, String newPwd);
}
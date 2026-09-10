package com.xjteam4.envMgr.mapper;

import com.xjteam4.envMgr.domain.SysUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.*;

public interface SysUserMapper{

    SysUserEntity queryById(Integer userId);

    public List<SysUserEntity> queryAllByLimit(@Param("sysUser") SysUserEntity sysUser, @Param("pageable") Pageable pageable);

    long count(@Param("sysUser") SysUserEntity sysUser);

    int insert(SysUserEntity sysUser);

    int update(SysUserEntity sysUser);

    int deleteById(Integer userId);

    SysUserEntity login(SysUserEntity sysUser);

    /**
     * 查询用户是否存在
     */
    SysUserEntity queryByUserPhone(SysUserEntity sysUser);

    /**
     * 修改密码
     * update sys_user u set user_pwd='666' where u.user_id=1 and u.user_pwd = 'd8/ZDtAN2a7IDQmuAZ1B0w=='
     */
    int updatePwd(@Param("userId") Integer userId, @Param("oldPwd") String oldPwd, @Param("newPwd") String newPwd);

}
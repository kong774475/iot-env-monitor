package com.xjteam4.envMgr.service.serviceImpl;

import com.xjteam4.envMgr.domain.SysUserEntity;
import com.xjteam4.envMgr.mapper.SysUserMapper;
import com.xjteam4.envMgr.service.SysUserService;
import com.xjteam4.envMgr.util.PasswordUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Service
public class SysUserServiceImpl implements SysUserService {
    private static int PAGE_DEFAULT = 1;
    private static int SIZE_DEFAULT = 10;

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUserEntity queryById(Integer userId) {
        return this.sysUserMapper.queryById(userId);
    }

    public Page<SysUserEntity> queryByPage(SysUserEntity sysUser, Integer page, Integer size, String orderCol, String orderDirect) {

        if (page == null || page <= 0) {
            page = PAGE_DEFAULT;
        }
        if (size == null || size <= 0) {
            size = SIZE_DEFAULT;
        }

        Sort sort = null;
        if (orderCol != null) {
            Sort.Order order = new Sort.Order(("DESC".equals(orderDirect) ? Sort.Direction.DESC : Sort.Direction.ASC), orderCol);
            sort = Sort.by(order);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if (sort != null) {
            pageRequest = PageRequest.of(page - 1, size, sort);
        }

        long total = this.sysUserMapper.count(sysUser);

        return new PageImpl<>(this.sysUserMapper.queryAllByLimit(sysUser, pageRequest), pageRequest, total);
    }

    public boolean insert(SysUserEntity sysUser) {
        return this.sysUserMapper.insert(sysUser) > 0;
    }

    public boolean update(SysUserEntity sysUser) {
        return this.sysUserMapper.update(sysUser) > 0;
    }

    public boolean deleteById(Integer userId) {
        return this.sysUserMapper.deleteById(userId) > 0;
    }

    public SysUserEntity login(SysUserEntity sysUser) {
        //对密码进行加密
        if (sysUser.getUserPwd() != null) {
            sysUser.setUserPwd(PasswordUtil.degistPwd(sysUser.getUserPwd()));
        }
        return this.sysUserMapper.login(sysUser);
    }

    /**
     * 查询用户是否存在
     */
    public SysUserEntity queryByUserPhone(SysUserEntity sysUser) {
        return this.sysUserMapper.queryByUserPhone(sysUser);

    }

    /**
     * 修改密码
     */
    public boolean updatePwd(Integer userId, String oldPwd, String newPwd) {
        if (oldPwd == null || newPwd == null || userId == null) {
            return false;//打日志
        }
        return this.sysUserMapper.updatePwd(userId,
                PasswordUtil.degistPwd(oldPwd),
                PasswordUtil.degistPwd(newPwd)) > 0;
    }
}
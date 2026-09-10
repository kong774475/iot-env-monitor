package com.xjteam4.envMgr.controller;

import com.xjteam4.envMgr.domain.SysUserEntity;
import com.xjteam4.envMgr.service.SysUserService;
import com.xjteam4.envMgr.util.*;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@RestController
@RequestMapping("sysUser")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    @PostMapping("queryById/{id}")
    public ReturnVO queryById(@PathVariable("id") Integer id) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        SysUserEntity sysUser = this.sysUserService.queryById(id);

        if(sysUser == null) {
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(sysUser);

        return returnVO;
    }

    @PostMapping("queryByPage")
    public ReturnVO queryByPage(SysUserEntity sysUser, Integer page, Integer size, String orderCol, String orderDirect) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Page<SysUserEntity> pageVO = 
            this.sysUserService.queryByPage(
                sysUser, 
                page, size,
                orderCol, orderDirect);

        //没有数据
        if(pageVO.getContent().isEmpty()){
            return returnVO;
        }

        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(pageVO);

        return returnVO;
    }

    @PostMapping("add")
    public ReturnVO add(@RequestBody SysUserEntity sysUser) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //入库
        if(!this.sysUserService.insert(sysUser)){
            //入库失败
            return returnVO;
        }

        //入库成功
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");

        return returnVO;
    }

    @PostMapping("edit")
    public ReturnVO edit(@RequestBody SysUserEntity sysUser) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //修改
        if(!this.sysUserService.update(sysUser)){
            //修改失败
            return returnVO;
        }

        //修改成功
        returnVO.setCode(1);
        returnVO.setMsg("修改成功");

        return returnVO;
    }

    @PostMapping("deleteById/{id}")
    public ReturnVO deleteById(@PathVariable("id") Integer id) {        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if(!this.sysUserService.deleteById(id)){
            //删除失败
            return returnVO;
        }

        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ReturnVO login(@RequestBody SysUserEntity sysUser) {
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        SysUserEntity logUser = this.sysUserService.login(sysUser);
        if(logUser == null){
            //登录失败
            returnVO.setMsg("用户名或密码错误");
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(logUser);
        returnVO.setMsg("登录成功");
        return returnVO;
    }

    /**
     * 查询用户是否存在
     */
    @PostMapping("/queryByUserPhone")
    public ReturnVO queryByUserPhone(@RequestBody SysUserEntity sysUser) {
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        SysUserEntity user = this.sysUserService.queryByUserPhone(sysUser);
        if (user == null) {
            //登录失败
            returnVO.setMsg("用户不存在");
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(user);
        returnVO.setMsg("用户存在");
        return returnVO;
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePwd/{newPwd}")
    public ReturnVO updatePwd(@RequestBody SysUserEntity sysUser, @PathVariable String newPwd) {
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        boolean flag = this.sysUserService.updatePwd(sysUser.getUserId(), sysUser.getUserPwd(), newPwd);
        if (!flag) {
            //修改密码失败
            returnVO.setMsg("修改密码失败");
            return returnVO;
        }
        returnVO.setMsg("修改密码成功");
        returnVO.setCode(1);
        return returnVO;
    }
}

package com.xjteam4.envMgr.controller;

import com.xjteam4.envMgr.domain.UserOperLogEntity;
import com.xjteam4.envMgr.service.UserOperLogService;
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
@RequestMapping("userOperLog")
public class UserOperLogController {
    @Resource
    private UserOperLogService userOperLogService;

    @PostMapping("queryById/{id}")
    public ReturnVO queryById(@PathVariable("id") Integer id) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        UserOperLogEntity userOperLog = this.userOperLogService.queryById(id);

        if(userOperLog == null) {
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(userOperLog);

        return returnVO;
    }

    @PostMapping("queryByPage")
    public ReturnVO queryByPage(UserOperLogEntity userOperLog, Integer page, Integer size, String orderCol, String orderDirect) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Page<UserOperLogEntity> pageVO = 
            this.userOperLogService.queryByPage(
                userOperLog, 
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
    public ReturnVO add(@RequestBody UserOperLogEntity userOperLog) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //入库
        if(!this.userOperLogService.insert(userOperLog)){
            //入库失败
            return returnVO;
        }

        //入库成功
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");

        return returnVO;
    }

    @PostMapping("edit")
    public ReturnVO edit(@RequestBody UserOperLogEntity userOperLog) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //修改
        if(!this.userOperLogService.update(userOperLog)){
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
        if(!this.userOperLogService.deleteById(id)){
            //删除失败
            return returnVO;
        }

        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

    /**
     * 独立接口：按时间区间、操作类型、操作人员昵称、自定义查询条数查询日志
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param operType 操作类型（可选）
     * @param nickName 操作人员昵称模糊（可选）
     * @param limitNum 自定义查询多少条
     */
    @PostMapping("queryLogByTimeLimit")
    public ReturnVO queryLogByTimeLimit(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer operType,
            @RequestParam(required = false) String nickName,
            @RequestParam Integer limitNum
    ) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        List<UserOperLogEntity> logList = userOperLogService.queryLogByTimeAndLimit(startTime, endTime, operType, nickName, limitNum);
        if (logList == null || logList.isEmpty()) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(logList);
        return returnVO;
    }

    /**
     * 统计该时间段+筛选条件下日志总条数
     */
    @PostMapping("countLogByTime")
    public ReturnVO countLogByTime(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer operType,
            @RequestParam(required = false) String nickName
    ) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        long total = userOperLogService.countLogByTime(startTime, endTime, operType, nickName);
        Map<String, Long> map = new HashMap<>();
        map.put("total", total);
        returnVO = ReturnVO.getSuccessDataReturnVO(map);
        return returnVO;
    }

}
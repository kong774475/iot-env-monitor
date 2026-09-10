package com.xjteam4.envMgr.service;

import com.xjteam4.envMgr.domain.UserOperLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public interface UserOperLogService {
    UserOperLogEntity queryById(Integer operId);

    Page<UserOperLogEntity> queryByPage(UserOperLogEntity userOperLog, Integer page, Integer size, String orderCol, String orderDirect);

    boolean insert(UserOperLogEntity userOperLog);

    boolean update(UserOperLogEntity userOperLog);

    boolean deleteById(Integer operId);

    /**
     * 查询用户操作日志，根据时间区间 + 操作类型 + 昵称模糊 + 自定义条数
      */

    List<UserOperLogEntity> queryLogByTimeAndLimit(String startTime, String endTime, Integer operType, String nickName, Integer limitNum);

    /**
     * 统计用户操作日志总数
     */
    long countLogByTime(String startTime, String endTime, Integer operType, String nickName);

}
package com.xjteam4.envMgr.mapper;

import com.xjteam4.envMgr.domain.UserOperLogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.*;

public interface UserOperLogMapper{

    UserOperLogEntity queryById(Integer operId);

    public List<UserOperLogEntity> queryAllByLimit(@Param("userOperLog") UserOperLogEntity userOperLog, @Param("pageable") Pageable pageable);

    long count(@Param("userOperLog") UserOperLogEntity userOperLog);

    int insert(UserOperLogEntity userOperLog);

    int update(UserOperLogEntity userOperLog);

    int deleteById(Integer operId);

    /**
     * 查询用户操作日志，根据时间区间 + 操作类型 + 昵称模糊 + 自定义条数
     */
    List<UserOperLogEntity> queryLogByTimeAndLimit(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("operType") Integer operType,
            @Param("nickName") String nickName,
            @Param("limitNum") Integer limitNum
    );

    /**
     * 统计用户操作日志总数
     * @param startTime
     * @param endTime
     * @param operType
     * @param nickName
     * @return
     */
    long countLogByTime(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("operType") Integer operType,
            @Param("nickName") String nickName
    );

}
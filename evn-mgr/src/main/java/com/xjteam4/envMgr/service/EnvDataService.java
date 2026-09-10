package com.xjteam4.envMgr.service;

import com.xjteam4.envMgr.domain.EnvDataEntity;
import com.xjteam4.envMgr.module.vo.EnvDataInfoVO;
import com.xjteam4.envMgr.module.vo.EnvDataVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public interface EnvDataService {
    EnvDataEntity queryById(Integer edId);

    Page<EnvDataEntity> queryByPage(EnvDataEntity envData, Integer page, Integer size, String orderCol, String orderDirect);

    boolean insert(EnvDataEntity envData);

    boolean insert(EnvDataVO envData);

    boolean update(EnvDataEntity envData);

    boolean deleteById(Integer edId);

    /**
     *查询每个设备最后上报的数据信息
     */
    public List<EnvDataInfoVO> queryDevInfos();

    /**
     * 查询报表中四个数据：当前温度、当前湿度、当前光照、异常数据条数
     */
    public Map<String, Object> queryReportData();

    /**
     * 查询最近24条数据
     */
    Map<String, Object> queryLast24Data();

    /**
     * 查询正常、异常数据个数
     */
    List<Map<String, Object>> queryDataCount();

    /**
     * 获取设备的平均温度取值
     */
    Map<String, Object> queryAvgTemp();



}
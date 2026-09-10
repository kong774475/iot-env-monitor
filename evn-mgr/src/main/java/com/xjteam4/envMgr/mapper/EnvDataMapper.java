package com.xjteam4.envMgr.mapper;

import com.xjteam4.envMgr.domain.EnvDataEntity;
import com.xjteam4.envMgr.module.vo.EnvDataInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.*;

public interface EnvDataMapper{

    EnvDataEntity queryById(Integer edId);

    public List<EnvDataEntity> queryAllByLimit(@Param("envData") EnvDataEntity envData, @Param("pageable") Pageable pageable);

    long count(@Param("envData") EnvDataEntity envData);

    int insert(EnvDataEntity envData);

    int update(EnvDataEntity envData);

    int deleteById(Integer edId);

    /**
     *查询每个设备最后上报的数据信息
     */
    List<EnvDataInfoVO> queryDevInfos();

    /**
     * 根据数据类型，获取最近时间的数据指标，1温度 2 湿度 3光照
     */
    Double queryLastData(Integer type);

    /**
     * 异常数据条数
     */
    Integer queryEeceptionDataCount();

    /**
     * 查询最近24条数据
     */
    List<Map<String, Object>> queryLast24Data();

    /**
     * 查询正常、异常数据个数
     */
    List<Map<String, Object>> queryDataCount();

    /**
     * 获取设备的平均温度取值
     */
    List<Map<String, Object>> queryAvgTemp();
}
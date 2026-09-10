package com.xjteam4.envMgr.mapper;

import com.xjteam4.envMgr.domain.DataThresholdEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.*;

public interface DataThresholdMapper{

    DataThresholdEntity queryById(Integer thId);

    public List<DataThresholdEntity> queryAllByLimit(@Param("dataThreshold") DataThresholdEntity dataThreshold, @Param("pageable") Pageable pageable);

    long count(@Param("dataThreshold") DataThresholdEntity dataThreshold);

    int insert(DataThresholdEntity dataThreshold);

    int update(DataThresholdEntity dataThreshold);

    int deleteById(Integer thId);

}
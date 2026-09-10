package com.xjteam4.envMgr.service;

import com.xjteam4.envMgr.domain.DataThresholdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public interface DataThresholdService {
    DataThresholdEntity queryById(Integer thId);

    Page<DataThresholdEntity> queryByPage(DataThresholdEntity dataThreshold, Integer page, Integer size, String orderCol, String orderDirect);

    boolean insert(DataThresholdEntity dataThreshold);

    boolean update(DataThresholdEntity dataThreshold);

    boolean deleteById(Integer thId);

}
package com.xjteam4.envMgr.service.serviceImpl;

import com.xjteam4.envMgr.domain.DataThresholdEntity;
import com.xjteam4.envMgr.mapper.DataThresholdMapper;
import com.xjteam4.envMgr.service.DataThresholdService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Service
public class DataThresholdServiceImpl implements DataThresholdService {
    private static int PAGE_DEFAULT = 1;
    private static int SIZE_DEFAULT = 10;

    @Resource
    private DataThresholdMapper dataThresholdMapper;

    public DataThresholdEntity queryById(Integer thId){
        return this.dataThresholdMapper.queryById(thId);
    }

    public Page<DataThresholdEntity> queryByPage(DataThresholdEntity dataThreshold, Integer page, Integer size, String orderCol, String orderDirect){

        if(page == null || page <= 0){
            page = PAGE_DEFAULT;
        }
        if(size == null || size <= 0){
            size = SIZE_DEFAULT;
        }

        Sort sort = null;
        if(orderCol != null) {
            Sort.Order order = new Sort.Order(("DESC".equals(orderDirect) ? Sort.Direction.DESC : Sort.Direction.ASC), orderCol);
            sort = Sort.by(order);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if(sort != null){
            pageRequest = PageRequest.of(page - 1, size, sort);
        }

        long total = this.dataThresholdMapper.count(dataThreshold);

        return new PageImpl<>(this.dataThresholdMapper.queryAllByLimit(dataThreshold, pageRequest), pageRequest, total);
    }

    public boolean insert(DataThresholdEntity dataThreshold){
        return this.dataThresholdMapper.insert(dataThreshold) > 0;
    }

    public boolean update(DataThresholdEntity dataThreshold){
        return this.dataThresholdMapper.update(dataThreshold) > 0;
    }

    public boolean deleteById(Integer thId){
        return this.dataThresholdMapper.deleteById(thId) > 0;
    }

}
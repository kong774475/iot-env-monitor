package com.xjteam4.envMgr.service.serviceImpl;

import com.xjteam4.envMgr.domain.UserOperLogEntity;
import com.xjteam4.envMgr.mapper.UserOperLogMapper;
import com.xjteam4.envMgr.service.UserOperLogService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Service
public class UserOperLogServiceImpl implements UserOperLogService {
    private static int PAGE_DEFAULT = 1;
    private static int SIZE_DEFAULT = 10;

    @Resource
    private UserOperLogMapper userOperLogMapper;

    public UserOperLogEntity queryById(Integer operId){
        return this.userOperLogMapper.queryById(operId);
    }

    public Page<UserOperLogEntity> queryByPage(UserOperLogEntity userOperLog, Integer page, Integer size, String orderCol, String orderDirect){

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

        long total = this.userOperLogMapper.count(userOperLog);

        return new PageImpl<>(this.userOperLogMapper.queryAllByLimit(userOperLog, pageRequest), pageRequest, total);
    }

    public boolean insert(UserOperLogEntity userOperLog){
        return this.userOperLogMapper.insert(userOperLog) > 0;
    }

    public boolean update(UserOperLogEntity userOperLog){
        return this.userOperLogMapper.update(userOperLog) > 0;
    }

    public boolean deleteById(Integer operId){
        return this.userOperLogMapper.deleteById(operId) > 0;
    }

    /**
     *  查询用户操作日志，根据时间区间 + 操作类型 + 昵称模糊 + 自定义条数
     */
    @Override
    public List<UserOperLogEntity> queryLogByTimeAndLimit(String startTime, String endTime, Integer operType, String nickName, Integer limitNum) {
        return userOperLogMapper.queryLogByTimeAndLimit(startTime, endTime, operType, nickName, limitNum);
    }

    /**
     * 统计用户操作日志总数
     * @param startTime
     * @param endTime
     * @param operType
     * @param nickName
     * @return
     */
    @Override
    public long countLogByTime(String startTime, String endTime, Integer operType, String nickName) {
        return userOperLogMapper.countLogByTime(startTime, endTime, operType, nickName);
    }
}
package com.xjteam4.envMgr.service.serviceImpl;

import com.xjteam4.envMgr.domain.EnvDataEntity;
import com.xjteam4.envMgr.mapper.EnvDataMapper;
import com.xjteam4.envMgr.module.vo.EnvDataInfoVO;
import com.xjteam4.envMgr.module.vo.EnvDataType;
import com.xjteam4.envMgr.module.vo.EnvDataVO;
import com.xjteam4.envMgr.service.EnvDataService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Service
public class EnvDataServiceImpl implements EnvDataService {
    private static int PAGE_DEFAULT = 1;
    private static int SIZE_DEFAULT = 10;

    @Resource
    private EnvDataMapper envDataMapper;

    public EnvDataEntity queryById(Integer edId){
        return this.envDataMapper.queryById(edId);
    }

    public Page<EnvDataEntity> queryByPage(EnvDataEntity envData, Integer page, Integer size, String orderCol, String orderDirect){

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

        long total = this.envDataMapper.count(envData);

        return new PageImpl<>(this.envDataMapper.queryAllByLimit(envData, pageRequest), pageRequest, total);
    }

    public boolean insert(EnvDataEntity envData){
        return this.envDataMapper.insert(envData) > 0;
    }

    public boolean insert(EnvDataVO envData) {
        //将上报的数据解析为三条(温度、湿度、光照)数据并入库
        // 温度数据
        EnvDataEntity tempData = envData.getEnvDataEntity (EnvDataType. TEMP);
        //湿度数据
        EnvDataEntity humiData = envData.getEnvDataEntity(EnvDataType.HUMI);
        //光照强度
        EnvDataEntity lightData = envData.getEnvDataEntity(EnvDataType.LIGHT);

        int count = 0;
        //入库温度数据，返回影响行数
        count += this.envDataMapper.insert(tempData);
        //入库湿度数据
        count += this.envDataMapper.insert(humiData);
        //光照数据
        count += this.envDataMapper.insert(lightData);

        return count >= 3;


    }

    public boolean update(EnvDataEntity envData){
        return this.envDataMapper.update(envData) > 0;
    }

    public boolean deleteById(Integer edId){
        return this.envDataMapper.deleteById(edId) > 0;
    }

    /**
     *查询每个设备最后上报的数据信息
     */
    public List<EnvDataInfoVO> queryDevInfos(){
        return this.envDataMapper.queryDevInfos();
    }

    /**
     * 查询报表中四个数据：当前温度、当前湿度、当前光照、异常数据条数
     */
    public Map<String, Object> queryReportData(){
        //定义返回值
        Map<String, Object> result = new HashMap<>();
        //查询当前温度
        result.put("currTemp", this.envDataMapper.queryLastData(1));
        //查询当前湿度
        result.put("currHumi", this.envDataMapper.queryLastData(2));
        //查询当前光照
        result.put("currLight", this.envDataMapper.queryLastData(3));
        //查询异常数据条数
        result.put("exDataCount", this.envDataMapper.queryEeceptionDataCount());

        return result;
    }

    /*** 查询最近24条数据
     */
    public Map<String, Object> queryLast24Data(){
        //定义温度数组
        List<Double> tempList = new ArrayList<>();
        //定义湿度数组
        List<Double> humiList = new ArrayList<>();
        //定义光照强度数组
        List<Double> lightList = new ArrayList<>();
        //x轴时间数组
        List<String> timeList = new ArrayList<>();

        List<Map<String, Object>> datas = this.envDataMapper.queryLast24Data();
        //遍历
        for(Map<String, Object> data : datas){
            tempList.add(Double.parseDouble(data.get("temp").toString()));
            humiList.add(Double.parseDouble(data.get("humi").toString()));
            lightList.add(Double.parseDouble(data.get("light").toString()));
            timeList.add(data.get("time").toString());
        }
        //封装为一个map
        Map<String, Object> result = new HashMap<>();
        result.put("tempList", tempList);
        result.put("humiList", humiList);
        result.put("lightList", lightList);
        result.put("timeList", timeList);
        return result;
    }

    /**
     * 查询正常、异常数据个数
     */
    public List<Map<String, Object>> queryDataCount(){
        return this.envDataMapper.queryDataCount();
    }

    /**
     * 获取设备的平均温度取值
     */
    public Map<String, Object> queryAvgTemp(){
        List<Map<String, Object>> avgTemp = this.envDataMapper.queryAvgTemp();
        //横轴设备名数组
        List<String> devNames = new ArrayList<>();
        //纵轴平均温度数组
        List<Double> tempList = new ArrayList<>();

        for(Map<String, Object> data : avgTemp){
            devNames.add(data.get("dev_name").toString());
            tempList.add(Double.parseDouble(data.get("avg_temp").toString()));
        }

        //封装为一个map
        Map<String, Object> result = new HashMap<>();
        result.put("devNames", devNames);
        result.put("tempList", tempList);

        return result;

    }

}
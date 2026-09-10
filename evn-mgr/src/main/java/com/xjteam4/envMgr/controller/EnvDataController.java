package com.xjteam4.envMgr.controller;

import com.xjteam4.envMgr.domain.EnvDataEntity;
import com.xjteam4.envMgr.module.vo.EnvDataInfoVO;
import com.xjteam4.envMgr.service.EnvDataService;
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
@RequestMapping("envData")
public class EnvDataController {
    @Resource
    private EnvDataService envDataService;

    @PostMapping("queryById/{id}")
    public ReturnVO queryById(@PathVariable("id") Integer id) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        EnvDataEntity envData = this.envDataService.queryById(id);

        if(envData == null) {
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(envData);

        return returnVO;
    }

    @PostMapping("queryByPage")
    public ReturnVO queryByPage(EnvDataEntity envData, Integer page, Integer size, String orderCol, String orderDirect) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Page<EnvDataEntity> pageVO = 
            this.envDataService.queryByPage(
                envData, 
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
    public ReturnVO add(@RequestBody EnvDataEntity envData) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //入库
        if(!this.envDataService.insert(envData)){
            //入库失败
            return returnVO;
        }

        //入库成功
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");

        return returnVO;
    }

    @PostMapping("edit")
    public ReturnVO edit(@RequestBody EnvDataEntity envData) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //修改
        if(!this.envDataService.update(envData)){
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
        if(!this.envDataService.deleteById(id)){
            //删除失败
            return returnVO;
        }

        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

    /**
     * 查询设备卡片信息
     */
    @PostMapping("queryDevInfos")
    public ReturnVO queryDevInfos() {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //查询
        List<EnvDataInfoVO> devInfo = this.envDataService.queryDevInfos();
        //没有查到数据
        if (devInfo.isEmpty()){
            return returnVO;
        }
        //查到数据
        returnVO.setCode(1);
        returnVO.setContent(devInfo);
        returnVO.setMsg("查到设备信息");
        return returnVO;
    }

    /**
     * 获取最近时间数据指标
     */
    @RequestMapping("queryLastReportData")
    public ReturnVO queryLastReportData(){
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Map<String, Object> map = this.envDataService.queryReportData();
        if(map == null || map.isEmpty()){
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(map);
        return returnVO;
    }

    /**
     * 获取最近24条数据
     */
    @RequestMapping("queryLast24Data")
    public ReturnVO queryLast24Data() {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Map<String, Object> result = this.envDataService.queryLast24Data();
        if (result == null || result.isEmpty()) {
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(result);
        return returnVO;
    }

    /**
     * 查询正常、异常数据个数
     */
    @RequestMapping("queryDataCount")
    public ReturnVO queryDataCount() {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        List<Map<String, Object>> list = this.envDataService.queryDataCount();
        if (list == null || list.isEmpty()) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        return returnVO;
    }

    /**
     * 获取设备的平均温度取值
     */
    @RequestMapping("queryAvgTemp")
    public ReturnVO queryAvgTemp() {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Map<String, Object> result = this.envDataService.queryAvgTemp();
        if (result == null || result.isEmpty()) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(result);
        return returnVO;
    }
}
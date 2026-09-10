package com.xjteam4.envMgr.controller;

import com.xjteam4.envMgr.domain.DataThresholdEntity;
import com.xjteam4.envMgr.service.DataThresholdService;
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
@RequestMapping("dataThreshold")
public class DataThresholdController {
    @Resource
    private DataThresholdService dataThresholdService;

    @PostMapping("queryById/{id}")
    public ReturnVO queryById(@PathVariable("id") Integer id) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        DataThresholdEntity dataThreshold = this.dataThresholdService.queryById(id);

        if(dataThreshold == null) {
            return returnVO;
        }

        returnVO = ReturnVO.getSuccessDataReturnVO(dataThreshold);

        return returnVO;
    }

    @PostMapping("queryByPage")
    public ReturnVO queryByPage(DataThresholdEntity dataThreshold, Integer page, Integer size, String orderCol, String orderDirect) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        Page<DataThresholdEntity> pageVO = 
            this.dataThresholdService.queryByPage(
                dataThreshold, 
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
    public ReturnVO add(@RequestBody DataThresholdEntity dataThreshold) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //入库
        if(!this.dataThresholdService.insert(dataThreshold)){
            //入库失败
            return returnVO;
        }

        //入库成功
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");

        return returnVO;
    }

    @PostMapping("edit")
    public ReturnVO edit(@RequestBody DataThresholdEntity dataThreshold) {

        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //修改
        if(!this.dataThresholdService.update(dataThreshold)){
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
        if(!this.dataThresholdService.deleteById(id)){
            //删除失败
            return returnVO;
        }

        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

}
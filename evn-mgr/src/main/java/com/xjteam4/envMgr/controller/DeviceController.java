package com.xjteam4.envMgr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjteam4.envMgr.module.vo.DeviceCommand;
import com.xjteam4.envMgr.service.MqttClientService;
import com.xjteam4.envMgr.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mqtt")
public class DeviceController {

    @Resource
    private MqttClientService mqttClientService;

    @GetMapping("/sendCmd/{deviceId}/{cmdId}")
    public ReturnVO sendCmd(@PathVariable String deviceId,
                            @PathVariable  Integer cmdId) throws JsonProcessingException {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        DeviceCommand command = new DeviceCommand(deviceId, cmdId);
        String json = new ObjectMapper().writeValueAsString(command);

        try {
            mqttClientService.sendDeviceCmd(deviceId, json);
            returnVO.setCode(1);
            returnVO.setMsg("命令下发成功");
            return returnVO;
        } catch (Exception e) {
            returnVO.setCode(0);
            returnVO.setMsg("命令下发失败" + e.getMessage());
            return returnVO;
        }
    }
}
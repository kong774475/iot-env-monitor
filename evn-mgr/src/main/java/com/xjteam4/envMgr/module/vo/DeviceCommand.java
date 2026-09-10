// 传感器数据模型类
package com.xjteam4.envMgr.module.vo;

//采用lombok，否则需要自定义 getters、setters 方法
import lombok.Data;

@Data
public class DeviceCommand {
    private String cmdType;
    private Object params;
    private Integer cmdId;
    private String deviceId;

    public DeviceCommand(String deviceId, Integer cmdId) {
        this.deviceId = deviceId;
        this.cmdId = cmdId;
    }
}
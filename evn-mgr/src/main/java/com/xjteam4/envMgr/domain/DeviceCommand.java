package com.xjteam4.envMgr.domain;

import lombok.Data;

@Data
public class DeviceCommand {
    private String cmdType;
    private Object params;
    private String cmdId;
}
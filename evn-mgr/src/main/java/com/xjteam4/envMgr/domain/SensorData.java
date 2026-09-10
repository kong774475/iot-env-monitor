package com.xjteam4.envMgr.domain;

import lombok.Data;

@Data
public class SensorData {
    private String deviceId;
    private Double temperature;
    private Double humidity;
    private String createTime;
}
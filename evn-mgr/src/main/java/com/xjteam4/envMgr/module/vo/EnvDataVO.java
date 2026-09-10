package com.xjteam4.envMgr.module.vo;

import com.xjteam4.envMgr.domain.EnvDataEntity;
import lombok.Data;

@Data
public class EnvDataVO {
    //设备ID
    private Integer devId;
    //设备名称
    private String devName;
    //设备位置
    private String devPosi;
    //温度
    private Double temp;
    //湿度
    private Double humi;
    //光照强度
    private Double light;

    /**
     * 根据上报的当前数据分别获取三种数据
     */
    public EnvDataEntity getEnvDataEntity(EnvDataType type){
        EnvDataEntity envData = new EnvDataEntity();
        envData.setDevId(this.devId);
        envData.setDevName(this.devName);
        envData.setDevPosi(this.devPosi);
        switch (type){
            case TEMP:
                envData.setDevVal(this.temp);
                envData.setValType(1);
                break;
            case HUMI:
                envData.setDevVal(this.humi);
                envData.setValType(2);
                break;
            case LIGHT:
                envData.setDevVal(this.light);
                envData.setValType(3);
                break;
        }
        return envData;
    }
}

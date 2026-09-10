package com.xjteam4.envMgr.domain;

import java.io.Serializable;
import java.util.*;
import lombok.Data;

@Data
public class EnvDataEntity{
    private Integer edId;// primary key 
    private Integer devId;
    private String devName;
    private String devPosi;
    private Double devVal;
    private Integer valType;
    private String collectTime;
    private Integer thFlag;
}
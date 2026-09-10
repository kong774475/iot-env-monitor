package com.xjteam4.envMgr.domain;

import java.io.Serializable;
import java.util.*;
import lombok.Data;

@Data
public class DataThresholdEntity{
    private Integer thId;// primary key 
    private Integer thType;
    private Double thMin;
    private Double thMax;
}
package com.xjteam4.envMgr.domain;

import java.io.Serializable;
import java.util.*;
import lombok.Data;

@Data
public class UserOperLogEntity{
    private Integer operId;// primary key 
    private SysUserEntity sysUser;
    private Integer operType;
    private String operDesc;
    private String operTime;
}
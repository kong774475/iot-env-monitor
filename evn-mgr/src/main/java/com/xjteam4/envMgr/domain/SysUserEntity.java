package com.xjteam4.envMgr.domain;

import java.io.Serializable;
import java.util.*;
import lombok.Data;

@Data
public class SysUserEntity{
    private Integer userId;// primary key 
    private String userName;
    private String userPwd;
    private String nickName;
    private String userPhone;
}
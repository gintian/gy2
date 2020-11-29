/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/6/1 下午7:51
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule;

import java.io.Serializable;

/**
 * 编号信息
 */
public class NumberRuleVo implements Serializable {

    private String id;
    private String systemName;//系统名称（中文）
    private String systemCode;//系统简称（英文）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
}

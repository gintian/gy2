package com.hjsj.hrms.service.core.http;

import java.io.Serializable;

public class KqRecordBean implements Serializable {
    private String empe_id;//员工编号
    private String location;//刷卡地点
    private String work_date;//刷卡日期
    private String work_time;//刷卡时间
    private String inout_flag;//进出标志（0：不限；1：进；-1：出）默认都可传0
    private String iscommon;//是否正常出勤点签到（0：非常规考勤；1：常规考勤）

    public String getEmpe_id() {
        return empe_id;
    }

    public void setEmpe_id(String empe_id) {
        this.empe_id = empe_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWork_date() {
        return work_date;
    }

    public void setWork_date(String work_date) {
        this.work_date = work_date;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public String getInout_flag() {
        return inout_flag;
    }

    public void setInout_flag(String inout_flag) {
        this.inout_flag = inout_flag;
    }

    public String getIscommon() {
        return iscommon;
    }

    public void setIscommon(String iscommon) {
        this.iscommon = iscommon;
    }


}

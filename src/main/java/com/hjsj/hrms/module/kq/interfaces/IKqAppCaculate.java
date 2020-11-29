package com.hjsj.hrms.module.kq.interfaces;

import java.util.Date;

/**
 * 考勤计算接口
 * @author zhaoxj
 * 2019-09-20
 */
public interface IKqAppCaculate {
    /**
     * 计算申请时长
     * @param nbase
     * @param a0100
     * @param appTypeId
     * @param startTime
     * @param endTime
     * @return 时长（单位不限制，自行处理）
     */
    double calcAppTimeLen(String nbase, String a0100, String appTypeId, Date startTime, Date endTime);
}

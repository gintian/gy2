package com.hjsj.hrms.module.selfservice.usercenter.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

public interface IUserCenterService {
    /**
     * @Author sheny
     * @param cardType dept:部门职责说明书 position：岗位职责说明书 employee:人员基本信息表
     * @return java.util.Map
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:52
     */
    Map getFileNameMap(String cardType, String a0100, String nbase) throws GeneralException;

    /**
     * 有无导出pdf、word权限
     * @author wangbs
     * @param cardType 进入标识
     * @return java.util.Map
     * @date 2020/6/8
     */
    Map getBtnFunction(String cardType);
}

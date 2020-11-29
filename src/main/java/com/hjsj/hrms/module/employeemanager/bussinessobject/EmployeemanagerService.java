package com.hjsj.hrms.module.employeemanager.bussinessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

/**
 * @Title EmployeemanagerService
 * @Description 员工档案接口类
 * @Company hjsj
 * @Author houby
 * @Date 2020.6.30 14:30
 * @Version 1.0.0
 */

public interface EmployeemanagerService {

    /**
       * 获取人员范围内的机构Id
       * @author houby
       * @param loadType 加载类型
       * @param queryParams 传递参数
       * @return String
       * @throws Exception
       */
    Map getOrgCode(String loadType, String queryParams) throws Exception;

    /**
       * 获取员工档案列表
       * @author houby
       * @param  loadType 加载类型
       * @param  unitid 机构编号 UNxxx
       * @param  cond 人员范围高级条件
       * @param  page 页数
       * @param  limit 每页显示数
       * @param  queryParams 传递参数
       * @return Map
       * @throws GeneralException
       */
    Map getEmployeeInfo(String loadType, String unitid, String cond, String page, String limit, String queryParams) throws Exception;
}

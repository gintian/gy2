package com.hjsj.hrms.module.employeemanager.dao;

import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * @Title 类名
 * @Description 类说明
 * @Company hjsj
 * @Author 编写人
 * @Date
 * @Version 1.0.0
 */

public interface EmployeemanagerDao {

    /**
       * 统计我的档案下属成员总人数
       * @author houby
       * @param nbase 人员库
       * @param a0100 人员编号
       * @return int
       * @throws Exception
       */
    int getTotalCount(String nbase, String a0100) throws Exception;

    /**
       * 获取员工信息列表
       * @author houby
       * @param loadType 加载类型
       * @param unitid 部门编号 UNxxx
       * @param cond 人员范围高级条件
       * @param page 页数
       * @param limit 每页几条
       * @param loadType 加载类型
       * @return Map
       * @throws GeneralException
       */
    Map getEmpMap(String loadType, String unitid, String cond, String page, String limit, String queryParams) throws GeneralException;

    /**
       * 获取快速查询指标
       * @author houby
       * @param
       * @return List
       * @throws Exception
       */
    List getfieldList() throws Exception;
}

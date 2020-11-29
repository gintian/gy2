package com.hjsj.hrms.module.employeemanager.bussinessobject.impl;

import com.hjsj.hrms.module.employeemanager.bussinessobject.EmployeemanagerService;
import com.hjsj.hrms.module.employeemanager.dao.EmployeemanagerDao;
import com.hjsj.hrms.module.employeemanager.dao.impl.EmployeemanagerDaoImpl;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.valueobject.UserView;
import org.mortbay.util.ajax.JSON;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title EmployeemanagerServiceImpl
 * @Description 员工档案接口实现类
 * @Company hjsj
 * @Author houby
 * @Date 2020.6.30 12:30
 * @Version 1.0.0
 */

public class EmployeemanagerServiceImpl implements EmployeemanagerService {
    private Connection conn;
    private UserView userView;

    public EmployeemanagerServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
       * 获取人员范围内的机构Id
       * @author houby
       * @param loadType 加载类型
       * @param queryParams 传递参数
       * @return String
       * @throws Exception
       */
    @Override
    public Map getOrgCode(String loadType, String queryParams) throws Exception {
        Map return_data = new HashMap();
        String a0100 = "";
        String nbase = "";
        if(queryParams!=null){
            HashMap queryMap=(HashMap) JSON.parse(queryParams);
            if(queryMap.size()>0){
                a0100 = (String) queryMap.get("a0100");
                nbase = (String) queryMap.get("nbase");
            }
        }
        EmployeemanagerDao employeemanagerDao = new EmployeemanagerDaoImpl(this.conn,this.userView);
        String orgType = this.userView.getManagePrivCode();
        String orgCode = this.userView.getManagePrivCodeValue();
        if("".equals(a0100)||"".equals(nbase)){
            a0100 = this.userView.getA0100();
            nbase = this.userView.getDbname();
        }
        if(orgCode.length()>0){//获取机构id和desc
            CodeItem co = AdminCode.getCode(orgType, orgCode);
        }else if("UN".equals(orgType) || userView.isSuper_admin()){//超级用户，查询顶级机构
            orgCode="ALL";
        }else{
            //没有管理范围
        }
        if("2".equals(loadType)){
            int count = 0;  //本人档案下统计下属成员总人数
            count = employeemanagerDao.getTotalCount(nbase,a0100);
            return_data.put("totalCount",count);
            return_data.put("nbase",nbase);
            return_data.put("a0100",a0100);
        }
        return_data.put("orgCode",orgCode);
        return return_data;
    }

    /**
       * 获取员工档案列表
       * @author houby
       * @param loadType 加载类型
       * @param unitid 部门编号 UNxxx
       * @param cond 人员范围高级条件
       * @param page 页数
       * @param limit 每页几条
       * @param queryParams 传递参数
       * @return Map
       * @throws Exception
       */
    @Override
    public Map getEmployeeInfo(String loadType, String unitid, String cond, String page, String limit, String queryParams) throws Exception {
        Map return_data = new HashMap();
        EmployeemanagerDao employeemanagerDao = new EmployeemanagerDaoImpl(this.conn,this.userView);
        return_data = employeemanagerDao.getEmpMap(loadType,unitid,cond,page,limit,queryParams);
        if("1".equals(loadType) && queryParams==null){
            List fieldList = employeemanagerDao.getfieldList();
            return_data.put("fieldList",fieldList);
        }
        return return_data;
    }
}

package com.hjsj.hrms.module.employeemanager.transcation;

import com.hjsj.hrms.module.employeemanager.bussinessobject.EmployeemanagerService;
import com.hjsj.hrms.module.employeemanager.bussinessobject.impl.EmployeemanagerServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title SearchMyTeamTrans
 * @Description 移动端员工档案模块交易类
 * @Company hjsj
 * @Author houby
 * @Date 2020.6.30 14:00
 * @Version 1.0.0
 */

public class SearchMyTeamTrans extends IBusiness {

    private enum TransType{
        /*请求人员权限范围内的编号*/
        getOrgCode,
        /*获取列表信息*/
        search
    }

    @Override
    public void execute() throws GeneralException {
        Map return_data = new HashMap();
        String return_code = "success";
        String requestType = (String) this.getFormHM().get("requestType");
        String loadType = (String)this.getFormHM().get("loadType");// 1、员工档案    2、本人档案
        String queryParams = (String)this.getFormHM().get("queryParams");
        EmployeemanagerService employeemanagerService = new EmployeemanagerServiceImpl(this.getFrameconn(),this.userView);
        try {
            if(StringUtils.equalsIgnoreCase(requestType, TransType.getOrgCode.toString())){
                return_data = employeemanagerService.getOrgCode(loadType,queryParams);
            }else if(StringUtils.equalsIgnoreCase(requestType, TransType.search.toString())){
                String unitid = (String) this.getFormHM().get("unitid");
                String cond = (String) this.getFormHM().get("cond");
                String limit = (String) this.getFormHM().get("limit");
                String page = (String) this.getFormHM().get("page");
                return_data = employeemanagerService.getEmployeeInfo(loadType,unitid,cond,page,limit,queryParams);
            }
        }catch (Exception e){
            e.printStackTrace();
            return_code = "fail";
            this.getFormHM().put("return_msg",e.getMessage());
        }finally {
            this.getFormHM().put("return_data",return_data);
            this.getFormHM().put("return_code",return_code);
        }
    }
}

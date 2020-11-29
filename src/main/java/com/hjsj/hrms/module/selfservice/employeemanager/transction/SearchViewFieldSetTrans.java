package com.hjsj.hrms.module.selfservice.employeemanager.transction;

import com.hjsj.hrms.module.selfservice.employeemanager.businessobject.IEmployeeManagerService;
import com.hjsj.hrms.module.selfservice.employeemanager.businessobject.impl.EmployeeManagerServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title SearchViewFieldSetTrans
 * @Description 获得查看员工指标集信息交易类
 * @Company hjsj
 * @Author houby
 * @Date 2020/04/27
 * @Version 1.0.0
 */

public class SearchViewFieldSetTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //获取人员库
        String nbase = (String) this.getFormHM().get("nbase");
        if (StringUtils.isBlank(nbase)) {
            nbase = this.userView.getDbname();
        }else{
            nbase = PubFunc.decrypt(nbase);//解密
        }
        //获取人员编号
        String a0100 = (String) this.getFormHM().get("a0100");
        if (StringUtils.isBlank(a0100)) {
            a0100 = this.userView.getA0100();
        }else{
            a0100 = PubFunc.decrypt(a0100);//解密
        }
        //个人中心进来没有照片，需请求一下
        boolean needPhoto = (Boolean) this.getFormHM().get("needPhoto");

        Map returnStr = new HashMap();
        String return_code = "success";
        String return_msg = "";
        try{
            IEmployeeManagerService employeeManagerService = new EmployeeManagerServiceImpl(this.frameconn,this.userView);
            Map return_data = employeeManagerService.searchEmployeeSetInfo(nbase, a0100, needPhoto);
            returnStr.put("return_data", return_data);
        }catch (Exception e){
            return_code = "fail";
            return_msg = e.getMessage();
            e.printStackTrace();
        }finally {
            returnStr.put("return_code", return_code);
            returnStr.put("return_msg_code", return_msg);
            this.getFormHM().put("returnStr", returnStr);
        }
    }
}

package com.hjsj.hrms.module.selfservice.employeemanager.transction;

import com.hjsj.hrms.module.selfservice.employeemanager.businessobject.IEmployeeManagerService;
import com.hjsj.hrms.module.selfservice.employeemanager.businessobject.impl.EmployeeManagerServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title ViewEmployeeYKcardTrans
 * @Description 获得查看员工子集信息数据
 * @Company hjsj
 * @Author houby
 * @Date 2020/04/27
 * @Version 1.0.0
 */

public class ViewEmployeeYKcardTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //获取人员库
        String nbase = (String) this.getFormHM().get("nbase");
        nbase = PubFunc.decrypt(nbase);//解密
        //获取人员编号
        String a0100 = (String) this.getFormHM().get("a0100");
        a0100 = PubFunc.decrypt(a0100);//解密
        //获取子集编号
        String setId = (String) this.getFormHM().get("setId");
        //获取分类名称
        String groupName = (String) this.getFormHM().get("groupName");
        //获取指标分类名称
        String sortname = (String) this.getFormHM().get("sortname");
        Map returnStr = new HashMap();
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        try{
            IEmployeeManagerService employeeManagerService = new EmployeeManagerServiceImpl(this.frameconn,this.userView);
            if(!"A01".equalsIgnoreCase(setId)){
                return_data = employeeManagerService.searchEmployeeSubSetInfo(nbase,a0100,setId,groupName,sortname);
            }
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

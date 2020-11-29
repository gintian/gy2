package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ShowMySalaryMainTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String type = (String) this.getFormHM().get("type");//操作类型
        String queryYear = (String)this.getFormHM().get("year");//查询年份
        String schemeId = (String)this.getFormHM().get("schemeId");//方案id
        String nbase = (String) this.getFormHM().get("nbase");
        String a0100 = (String) this.getFormHM().get("a0100");
        String state = "emply";//查看员工薪酬
        Map returnData = new HashMap();
        String returnCode = "success";
        String returnMsg ="";
        MySalaryService mySalaryService = new MySalaryServiceImpl(this.getFrameconn());
        if(StringUtils.equals("Main",type)){ //我的薪酬主界面
            try {
            	//UserView userView = null;
            	if((nbase==null || nbase.trim().length() == 0) && (a0100==null || a0100.trim().length() == 0) ){
            		nbase = this.userView.getDbname();
            		a0100 = this.userView.getA0100();
            		state = "self";//查看本人薪酬
            	}
            	/*员工薪酬入口进入时通过nbase和a0100 获取人员信息 获取userView 有可能为空，不走userView 
            	else{
            		userView =  mySalaryService.getEmployeeSalaryInfo(nbase, a0100);
            	}*/
                if(StringUtils.isEmpty(userView.getA0100()) && StringUtils.isEmpty(a0100)){//如果a0100没有值 则代表是业务用户并且没有关联自助用户  不允许查看我的薪酬
                    returnData.put("isSelfServiceUser",'0');
                    return;
                }else{
                    returnData.put("isSelfServiceUser",'1');
                }
                returnData = mySalaryService.getMySalaryInfo(this.userView,nbase,a0100,queryYear,schemeId,state);
                if(returnData.containsKey("return_code")){
                    returnCode = (String) returnData.get("return_code");
                    if(returnData.containsKey("return_msg")){
                        returnMsg = (String) returnData.get("return_msg");
                    }
                }
            }catch (GeneralException e){
                e.printStackTrace();
                returnCode = "fail";
                returnMsg = e.getErrorDescription();
                if (StringUtils.isEmpty(schemeId)) {//初始化时获取第一个薪酬方案有异常时  将此人所属的薪酬方案返回 避免前台页面因为第一个薪酬方案异常而空白
                    try{
                    	if(userView != null){
                    		nbase = userView.getDbname();
                    		a0100 = userView.getA0100();
                    	}
                        Map salaryScheme = ((MySalaryServiceImpl) mySalaryService).getMySalaryScheme(this.userView);
                        returnData.put("schemes", salaryScheme == null ? "[]" : salaryScheme.get("schemes"));
                    }catch (GeneralException ex){
                        returnMsg = "getSchemesError";
                    }
                }
            }
        }

        this.getFormHM().put("return_data",returnData);
        this.getFormHM().put("return_msg",returnMsg);
        this.getFormHM().put("return_code",returnCode);
    }
}

package com.hjsj.hrms.module.kq.org.transaction;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.org.businessobject.KqDeptDataBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchDeptKqAnalyseDataTrans extends IBusiness{
	
    @Override
    public void execute() throws GeneralException {
    	
    	 try 
    	 {
		 	String orgCode = (String) this.getFormHM().get("orgCode");
     		String nowDuration = (String) this.getFormHM().get("nowDuration");
//     		String nbase = (String) this.getFormHM().get("nbase");
//     		String a0100 = (String) this.getFormHM().get("a0100");
     		
     		KqDeptDataBo kqDeptDataBo = new KqDeptDataBo(this.getFrameconn(), this.userView);
     		
     		//一个考勤期间的开始，结束日期
        	String fromDate = "";
        	String toDate = "";
     		ArrayList datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), nowDuration, "-1", "");
     		fromDate = datelist.get(0).toString();
     		toDate = datelist.get(datelist.size()-1).toString();
     		//获得某段时间内某机构下考勤汇总数据
        	HashMap sumData = kqDeptDataBo.getDeptSumData(orgCode, DateUtils.getDate(fromDate, "yyyy.MM.dd"), DateUtils.getDate(toDate, "yyyy.MM.dd"));
    		 
        	this.getFormHM().put("deptKqAnalyseData", sumData); 
         } catch (Exception e) {
             e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
         }
    	
    }
}

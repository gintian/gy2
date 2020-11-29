package com.hjsj.hrms.module.kq.org.transaction;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.org.businessobject.KqDeptDataBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchDeptPersonKqDataTrans extends IBusiness{
    @Override
    public void execute() throws GeneralException {
    	
    	 try 
    	 {
		 	String orgCode = (String) this.getFormHM().get("orgCode");
     		String nowDuration = (String) this.getFormHM().get("nowDuration");
     		String limit = (String) this.getFormHM().get("limit");
     		String page = (String) this.getFormHM().get("page");
//     		String nbase = (String) this.getFormHM().get("nbase");
//     		String a0100 = (String) this.getFormHM().get("a0100");
     		
     		KqDeptDataBo kqDeptDataBo = new KqDeptDataBo(this.getFrameconn(), this.userView);
     		
     		//一个考勤期间的开始，结束日期
        	String fromDate = "";
        	String toDate = "";
     		ArrayList datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), nowDuration, "-1", "");
     		fromDate = datelist.get(0).toString();
     		toDate = datelist.get(datelist.size()-1).toString();
     		//总条数
            int sum = kqDeptDataBo.getSumCount(orgCode, fromDate, toDate);
            //获得某段时间内某机构下每个人员的考勤汇总数据
        	ArrayList personData = kqDeptDataBo.getDeptPersonSumDate(orgCode, DateUtils.getDate(fromDate, "yyyy.MM.dd"), DateUtils.getDate(toDate, "yyyy.MM.dd"), 
        			Integer.parseInt(limit), Integer.parseInt(page));
        	
        	this.getFormHM().put("totalCount", sum);
    		this.getFormHM().put("deptPersonKqData", personData);
    		 
         } catch (Exception e) {
             e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
         }
    	
    }
}

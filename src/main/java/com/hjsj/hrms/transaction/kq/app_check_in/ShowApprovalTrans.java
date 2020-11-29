package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author szk
*
*/
public class ShowApprovalTrans extends IBusiness {

 

	public void execute() throws GeneralException {
		try{
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String nbase = (String) hm.get("nbase");
        String a0100 = (String) hm.get("a0100");
        String start_date = "";
        String end_date = "";
        /**判断考勤期间*/
        ArrayList kqlist = RegisterDate.getKqDayList(this.getFrameconn());
        if (kqlist == null || kqlist.size() <= 0) {
            throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
        }
        else if (kqlist != null && kqlist.size() > 0) {
        	//开始，结束时间为当前考勤区间
            start_date = kqlist.get(0).toString();
            end_date = kqlist.get(kqlist.size() - 1).toString();
            if (start_date != null && start_date.length() > 0)
                start_date = start_date.replaceAll("\\.", "-");
            if (end_date != null && end_date.length() > 0)
                end_date = end_date.replaceAll("\\.", "-");
        }

			KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
			// 人员库sql
			StringBuffer nbasewhere = new StringBuffer();
			nbasewhere.append(" A0100 = '" + a0100 + "' and nbase ='" + nbase
					+ "'");
			// 加班类型
			StringBuffer strsql = new StringBuffer();
			strsql.append("select a0101,q1103,q11z1,q11z3,q11z4,q1107,q11z5 from  Q11 where Q11Z5 ='02' and");
			strsql.append(nbasewhere);
			// 当前考勤期间
			strsql.append(" and ");
			SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(),this.userView);
			String time = searchAllApp.getWhere2("q11", start_date, end_date,"all", "all", "1", "0");
			strsql.append(time);
			this.getFormHM().put("cond_str", "");
			this.getFormHM().put("sql_str", strsql.toString());
			/** 字段列表 */
			strsql.setLength(0);
			strsql.append("a0101,q1103,q11z1,q11z3,q11z4,q1107,q11z5");

			this.getFormHM().put("columns", strsql.toString());
			this.getFormHM().put("cond_order", " order by q11z1");
			this.getFormHM().put("viewPost", para.getKq_orgView_post());
    }
    catch (Exception e) {
    	 e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
	}
    }
}

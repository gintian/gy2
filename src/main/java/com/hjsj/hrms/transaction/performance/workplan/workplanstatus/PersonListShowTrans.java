package com.hjsj.hrms.transaction.performance.workplan.workplanstatus;


import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.performance.workplan.PersonListShowBo;
import com.hjsj.hrms.businessobject.performance.workplan.WorkPlanStatusBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonListShowTrans extends IBusiness{


	public void execute() throws GeneralException {

		try{
			String str_sql = "";
		    String str_whl = "";
		    String order_str = "";
		    String colums = "";
		    
			String report = "0";//批改状态.0表示未批，1表示其它
			
			//获得超链接中的参数
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String state = (String)hm.get("state");     //如果是日报，则获得第几天。如果是周报，则获得第几周
			String report_statue = (String)hm.get("report_statue");
			String cycle = (String)hm.get("cycle");
			String year = (String)hm.get("year");
			String type = (String)hm.get("type");
			String month = (String)hm.get("month");
			String codeitemid = (String)hm.get("codeitemid");
			String isReset = (String)hm.get("isReset");
			hm.remove("state");
			hm.remove("report_statue");
			hm.remove("cycle");
			hm.remove("year");
			hm.remove("type");
			hm.remove("month");
			hm.remove("codeitemid");
			hm.remove("isReset");
			
			
			//获得邮箱和电话的数据库字段
			AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
			String mobile_field ="a0100";//之所以有初始值，是因为邮箱和电话指标可能为空。如果为空，sql语句就会错误。
			String mobileTemp = autoSendEMailBo.getMobileField();
			if(!(mobileTemp==null || "".equals(mobileTemp)))
					mobile_field = mobileTemp;
			String email_field="a0100";
			RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			if(avo!=null)
				email_field=avo.getString("str_value");
			
			//获得操作范围和权限交集
			WorkPlanStatusBo sbo = new WorkPlanStatusBo(this.getFrameconn(),this.getUserView());
			String manageLimit = sbo.getUserViewPersonWhere(this.userView);
			ArrayList alist = sbo.getIntersection();
			
			if("1".equals(isReset)){      //说明是第一次处理页面
				this.getFormHM().put("isSelectedAll", "0");       //将全选标志设为0
			}
			
			String flag = "='"+report_statue+"'";
			if("01".equals(report_statue)){ //如果是未批状态
				flag = " in ('01','07') ";
			}
			if("02".equals(report_statue) || "03".equals(report_statue))//如果是已报或已批
				report = "1";
			
			//调用业务类
			PersonListShowBo bo = new PersonListShowBo(this.getFrameconn(),this.userView);
			str_sql = bo.getSqlSelect(report);
			str_whl = bo.getSqlFrom(cycle,type,flag,year,month,codeitemid,state,report,manageLimit,alist,email_field,mobile_field);
			order_str = " order by A0000";
			String totalsql = str_sql+str_whl+order_str;
			colums = bo.getSqlColums(report);
			
			this.getFormHM().put("str_sql", str_sql);
			this.getFormHM().put("str_whl", str_whl);
			this.getFormHM().put("order_str", order_str);
			this.getFormHM().put("colums", colums);
			this.getFormHM().put("report_flag", report); //控制页面的显示
			this.getFormHM().put("status", report_statue); //控制页面的显示
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}

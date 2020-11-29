package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchMailListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String startime=(String) this.getFormHM().get("startime");
		startime=startime==null||startime.length()<1?"":startime;
		String endtime=(String) this.getFormHM().get("endtime");
		endtime=endtime==null||endtime.length()<1?"":endtime;
		
		String stat = (String) this.getFormHM().get("state");
		StringBuffer where = new StringBuffer();
		StringBuffer str = new StringBuffer();
		String coumu = "";

		str.append("select sms_id,sender,receiver,mobile_no,status,msg,send_time,sended_count,nbase,a0100");
		coumu = "sms_id,sender,receiver,mobile_no,status,msg,send_time,sended_count,nbase,a0100,";

		if (stat == null || "".equals(stat)) {
			this.getFormHM().put("state", "-1");
			where.append(" from t_sys_smsbox where status='-1'");
		} else {

			where.append(" from t_sys_smsbox where status='");
			where.append(stat);
			where.append("'");
		}
		if(startime.length()>0)
	    	where.append(" and send_time >= "+Sql_switcher.dateValue(startime));
	    if(endtime.length()>0)
	    	where.append(" and send_time <= "+Sql_switcher.dateValue(endtime+" 23:59:59"));
	    
	    //指定的登记表类型 用于参看人员信息
	    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String empcard=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
	    this.getFormHM().put("tabid", empcard);
		this.getFormHM().put("str", str.toString());
		this.getFormHM().put("order", "order by send_time desc");
		this.getFormHM().put("conum", coumu);
		this.getFormHM().put("where", where.toString());

	}

}

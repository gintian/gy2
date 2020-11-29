package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:验证是否设置了计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:8 31, 2010</p> 
 *@author xieguiquan
 *@version 4.0
 */
public class ShowApproveProcess extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String appprocess=(String)hm.get("appprocess");	
			String frommodel=(String)hm.get("frommodel");   //  sp  ;  ff
			appprocess = SafeCode.decode(appprocess);
			String apps [] = appprocess.split("/");
			Date date2  = new Date();
			Calendar date=Calendar.getInstance();
			date2.setTime(Long.parseLong(apps[3]));
			date.setTime(date2);
			
			StringBuffer sqls = new StringBuffer();
			String tablename="salaryhistory";
			if("ff".equals(frommodel))
				tablename=this.userView.getUserName()+"_salary_"+apps[0];
			sqls.append("select appprocess from "+tablename+" where 1=1 ");
			if("sp".equals(frommodel))
				sqls.append(" and salaryid= "+apps[0]+" ");
			sqls.append(" and a0100='"+apps[1]+"' ");
			sqls.append(" and nbase='"+apps[2]+"' ");
			sqls.append(" and  "+Sql_switcher.year("a00z0")+"="+date.get(Calendar.YEAR)+" ");
			sqls.append(" and  "+Sql_switcher.month("a00z0")+"="+(date.get(Calendar.MONTH)+1)+" ");
			sqls.append(" and  a00z1="+apps[4]+" ");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sqls.toString());
			if(this.frowset.next()){
				this.getFormHM().put("appprocess",Sql_switcher.readMemo(this.frowset, "appprocess"));
			}else{
				this.getFormHM().put("appprocess","");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

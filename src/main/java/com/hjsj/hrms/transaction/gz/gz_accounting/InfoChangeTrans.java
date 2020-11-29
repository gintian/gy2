package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.GzAccountBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class InfoChangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
		HashMap hm=this.getFormHM();
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		GzAccountBo gaccountbo = new GzAccountBo(this.frameconn,salaryid);
		String sqlstr = gaccountbo.sqlStr("0",this.userView.getUserName()+"_gz_his_chg","changeflag=0");
		String column = gaccountbo.column("0");
		hm.put("changeflag","0");
		hm.put("sqlstr",sqlstr);
		hm.put("column",column);
		hm.put("orderby","order by a0000");
		hm.put("where","from "+this.userView.getUserName()+"_gz_his_chg where changeflag=0");
		hm.put("tablenamelist",gaccountbo.fieldList());
		hm.put("tableidlist1",gaccountbo.changeList());
		hm.put("totallist",gaccountbo.totalValue(this.userView,"0"));
		hm.put("varianceTotal",gaccountbo.varianceTotal(this.userView));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

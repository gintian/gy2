package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:保存子集返回的子集主页面</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 22, 2008:2:57:55 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class ShowSubsysOTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String id=(String)this.getFormHM().get("mid");
		
		reqhm.remove("id");
		BusiSQLStr bss=new BusiSQLStr();
		String[] sql=bss.getSubsysStr(id);
		hm.put("sql",sql[0]);
		hm.put("where",sql[1]);
		hm.put("column",sql[2]);
		hm.put("orderby",sql[3]);
		hm.put("mid", id);
	}

}

package com.hjsj.hrms.transaction.general.inform.emp;

import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
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
public class ShiftLibraryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null?dbname:"";
		ArrayList list=this.userView.getPrivDbList();
		if(list.size()==0)
			throw new GeneralException(ResourceFactory.getProperty("workbench.stat.noprivdbname"));
		ArrayList dblist=new ArrayList();
		for(int i=0;i<list.size();i++){
			String pre=(String)list.get(i);
			if(!pre.equalsIgnoreCase(dbname)){
				CommonData data=new CommonData(pre,AdminCode.getCodeName("@@", pre));
				dblist.add(data);
			}
		}
		this.getFormHM().put("dblist",dblist);
	}
}

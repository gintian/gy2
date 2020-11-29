package com.hjsj.hrms.transaction.gz.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SelectScaleTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");
		
		String itemid = (String)reqhm.get("item");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		reqhm.remove("item");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		ArrayList list = new ArrayList();
		String sqlstr = "select taxid,description from gz_tax_rate";
		ArrayList dylist = null;
		try {
			CommonData dataobj1 = new CommonData(" "," ");
			list.add(dataobj1);
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String taxid = dynabean.get("taxid").toString();
				String description = dynabean.get("description").toString();
				CommonData dataobj = new CommonData(taxid,description);
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("item",itemid);
		hm.put("salaryid",salaryid);
		hm.put("taxid","");
		hm.put("taxlist",list);
	}

}

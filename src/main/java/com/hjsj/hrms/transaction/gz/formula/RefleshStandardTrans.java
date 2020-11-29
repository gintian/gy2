package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.utils.ResourceFactory;
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
public class RefleshStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		String itemname = (String)hm.get("itemname");
		itemname=itemname!=null&&itemname.trim().length()>0?itemname:"";
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String itemid = (String)hm.get("item");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		ArrayList list = new ArrayList();
		String sqlstr = "select id,name from gz_stand where item='"+itemname+"' ORDER BY id desc";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String id = dynabean.get("id").toString();
				String name = dynabean.get("name").toString();
				CommonData dataobj = new CommonData(id,name);
				list.add(dataobj);
			}
			CommonData dataobj = new CommonData("0",ResourceFactory.getProperty("gz.formula.create.standart.table")+"...");
			list.add(dataobj);
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("standardid","");
		hm.put("standardlist",list);
	}

}

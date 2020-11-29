/*
 * Created on 2006-3-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetRequestValueTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String isupright=(String)hm.get("isupright");
		String dbname=(String)hm.get("dbname");
		String catalog_id=(String)this.getFormHM().get("catalog_id");
		String orgtype=(String)hm.get("orgtype");
		
		
		try{
			if(dbname==null || dbname !=null && dbname.length()!=3)
			{
				ArrayList dblist=userView.getPrivDbList();
				if(!dblist.isEmpty())
				    dbname=(String)dblist.get(0);
				else
					dbname="Usr";
		    }
			
			//历史机构归档信息
			if(catalog_id!=null && catalog_id.length()>0 && "true".equals(this.getFormHM().get("ishistory"))){
				String sql = " select name,description from hr_org_catalog where catalog_id='"+catalog_id+"'";
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					this.getFormHM().put("catalog_name", this.frowset.getString("name"));
				}
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		this.getFormHM().put("isupright",isupright);
		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("catalog_id",catalog_id);
		this.getFormHM().put("report_relations","no");
		this.getFormHM().put("orgtype", orgtype);
	}

}

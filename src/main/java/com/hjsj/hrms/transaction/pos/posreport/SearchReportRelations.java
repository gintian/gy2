package com.hjsj.hrms.transaction.pos.posreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.util.HashMap;
/**
 *<p>Title:SearchReportRelationsTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 11, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SearchReportRelations extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
//		String code=(String)hm.get("code");
//		String kind=(String)hm.get("kind");
//		if(code==null)
//		{
//			this.getFormHM().put("code","");
//		}
//		if(kind==null)
//		{
//			this.getFormHM().put("kind","3");
//		}
		
//		String isupright=(String)hm.get("isupright");
//		String dbname=(String)hm.get("dbname");
//		String catalog_id=(String)hm.get("catalog_id");
//		if(dbname==null || dbname !=null && dbname.length()!=3)
//		{
//			ArrayList dblist=userView.getPrivDbList();
//			if(!dblist.isEmpty())
//			    dbname=(String)dblist.get(0);
//			else
//				dbname="Usr";
//	    }
//		this.getFormHM().put("isupright",isupright);
//		this.getFormHM().put("dbname",dbname);
		
		/* FengXiBin add 2008-01-05 */
		String report_relations = (String)this.getFormHM().get("report_relations");
		this.getFormHM().put("report_relations","yes");
		String constant = "";
	    StringBuffer sqlstr = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)
			{
	    		sqlstr.append(" select * from constant where constant = 'PS_SUPERIOR' ");
			}
			else
			{			
				sqlstr.append(" select * from [constant] where [constant] = 'PS_SUPERIOR' ");
			}
	    	ResultSet rset = dao.search(sqlstr.toString());
	    	while(rset.next())
	        {
	        	if(Sql_switcher.searchDbServer()== Constant.ORACEL)
				{
	        		constant = Sql_switcher.readMemo(rset,"str_value");
				}
				else
				{			
					constant = rset.getString("str_value");
				}
	    	
	        }         
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("constant",constant);
	}
	
}
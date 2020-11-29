package com.hjsj.hrms.transaction.performance.implement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchIncumbencyUserTrans  extends IBusiness {

	public void execute() throws GeneralException {
	
		ArrayList list=new ArrayList();
		String codeID=(String)this.getFormHM().get("codeID");
		boolean isUsr=false;   //是否有在职人员库的权限
		
		ArrayList dblist=userView.getPrivDbList(); 
		for(Iterator t=dblist.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			if("Usr".equals(temp))
			{
				isUsr=true;
				break;
			}
			
		}
		
		
		
		String sql="select codesetid from organization where codeitemid='"+codeID+"'";
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			this.frowset=dao.search(sql);
			this.frowset.next();
			String codesetid=this.frowset.getString("codesetid").trim();
			
			
			StringBuffer sql_str=new StringBuffer("select A0100,A0101  from UsrA01 where 1=1  ");
		
			if("UN".equals(codesetid))  //单位
			{
				sql_str.append(" and B0110='"+codeID+"'");
			}
			else if("UM".equals(codesetid))  //部门
			{
				sql_str.append(" and E0122='"+codeID+"'");
			}
			else if("@K".equals(codesetid))   //职位
			{
				sql_str.append(" and E01A1='"+codeID+"'");
			}
			
			
			/*  权限控制 */
			if(!userView.isSuper_admin()&&isUsr)
		     {
				String conditionSql=" select A0100 "+userView.getPrivSQLExpression("Usr",true);
				sql_str.append(" and A0100 in ("+conditionSql+" )");
		     }
			else if(!userView.isSuper_admin()&&!isUsr)
			{
				sql_str.append(" and 1=2 ");
			}
			/*   ---------end-----------*/
			
			this.frowset=dao.search(sql_str.toString());
			while(this.frowset.next())
			{
				String codeid=this.frowset.getString("A0100");
				String codeName=this.frowset.getString("A0101");
				CommonData dataobj = new CommonData(codeid,codeName);
				list.add(dataobj);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
		}
		finally
		{
			this.getFormHM().clear();
			this.getFormHM().put("fieldlist",list);
		}
		
		
		
		
	}

}

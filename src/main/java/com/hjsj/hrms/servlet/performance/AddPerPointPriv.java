package com.hjsj.hrms.servlet.performance;


import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class AddPerPointPriv extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
	{
		
		String plan_id=request.getParameter("plan_id");
		String object_id=request.getParameter("object_id");
		
		/*  get 相应考核计划某对象的主体集合 */
		StringBuffer mb_sql=new StringBuffer("select mainbody_id from per_mainbody where ");
		  			 mb_sql.append("plan_id="+plan_id+" and object_id='"+object_id+"'");
		ArrayList mainBodyList=getMainBodyList(mb_sql.toString());
		
		 /*  get 相应考核计划的指标因子集合  */
		StringBuffer pp_sql=new StringBuffer("select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e");
		             pp_sql.append(" where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id="+plan_id);
        ArrayList perPointList=getMainBodyList(pp_sql.toString());
		
        Connection connection = null;
        Statement statement=null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        StringBuffer updateSQL = new StringBuffer();
        try
		{
			 
        	connection = (Connection) AdminDb.getConnection();	
  		    statement = connection.createStatement();
  		
			for(Iterator t=mainBodyList.iterator();t.hasNext();)
			{
				updateSQL=new StringBuffer("update per_pointpriv_"+plan_id+" set ");
				StringBuffer updateWhlSQL=new StringBuffer(" ");    //sql条件语句
				String mainbody_id=(String)t.next();
				for(Iterator tt=perPointList.iterator();tt.hasNext();)
				{
					String point_id=(String)tt.next();
					String name=mainbody_id+"\\C_"+point_id;
				
					String temp="0";
					if(request.getParameter(name)!=null&& "1".equals(request.getParameter(name)))
					{
						temp="1";
					}
					updateWhlSQL.append(" , C_"+point_id+"="+temp);
				}
				String whl=updateWhlSQL.toString();
				whl=whl.substring(3);
		
				updateSQL.append(whl);
				updateSQL.append(" where mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'");
				
				statement.addBatch(updateSQL.toString());
			
			}
			// 打开Wallet
			dbS.open(connection,updateSQL.toString());
			statement.executeBatch();
		}
        catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			 {
				 // 关闭Wallet
				 dbS.close(connection);
			 }
			 catch(Exception ee)
			 {
				 ee.printStackTrace();
			 }
			PubFunc.closeResource(statement);
			PubFunc.closeResource(connection);
		}
        
		
		return mapping.findForward("success");
		
	}
	
	
	 private ArrayList getMainBodyList(String sql)
	 {
		 ArrayList arrayList=new ArrayList();
		 Connection connection = null;
		 ResultSet resultset = null;
		 try
		 {
			 
			  connection = (Connection) AdminDb.getConnection();
			  ContentDAO dao = new ContentDAO(connection);
			  resultset = dao.search(sql);
			  while(resultset.next())
			  {
				  arrayList.add(resultset.getString(1));
			  }

		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 try
			 {
				 if (resultset != null)
						resultset.close();
				 if (connection != null)
						connection.close();
			 }
			 catch(Exception ee)
			 {
				 ee.printStackTrace();
			 }
		 }
		 
		 
		 return arrayList;
		 
		 
		 
	 }
	 
}

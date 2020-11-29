package com.hjsj.hrms.transaction.general.query.complex;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 复杂查询界面
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 25, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ComplexInterfaceTrans extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		 String complex_id=(String)this.getFormHM().get("complex_id");		 
		 if(complex_id==null||complex_id.length()<=0|| "#".equals(complex_id))
		 {
			 /*ArrayList  complexList=(ArrayList)this.getFormHM().get("complexList");
			 if(complexList!=null&&complexList.size()>0)
			 {
				 CommonData da=(CommonData)complexList.get(0);
				 complex_id=da.getDataValue();
			 }*/
			 complex_id="";
		 }
		 String complex_expr="";
		 if(complex_id!=null&&complex_id.length()>0)
		 {
			 String sql="select Lexpr from gwhere where id='"+complex_id+"'";
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 try
			 {
				 this.frowset=dao.search(sql);
				 if(this.frowset.next())
					 complex_expr=this.frowset.getString("Lexpr");
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		 ArrayList list=new ArrayList();
	     String sql="select id,name from gwhere order by id";
	     CommonData da=new CommonData();
	     ContentDAO dao=new ContentDAO(this.getFrameconn());
	     try
	    {
	    		this.frowset=dao.search(sql);
	    		while(this.frowset.next())
	    		{
	    			da=new CommonData();
	    	    	da.setDataName(this.frowset.getString("name"));
	    	    	da.setDataValue(this.frowset.getString("id"));
	    	    	list.add(da);
	    		}
	     }catch(Exception e)
	     {
	    		e.printStackTrace();
	     }
	     this.getFormHM().put("complexList", list);
		 this.getFormHM().put("complex_expr", complex_expr);		
		 this.getFormHM().put("complex_id", complex_id);
	}

}

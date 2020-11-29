package com.hjsj.hrms.transaction.performance.implement.query;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Iterator;

public class SaveQueryObjectTrans extends IBusiness {


	public void execute() throws GeneralException {		
		String str_sql = (String)this.getFormHM().get("str_sql");
		String str_whl = (String)this.getFormHM().get("str_whl");
		String plan_id=(String)this.getFormHM().get("plan_id");
		String flag=(String)this.getFormHM().get("flag");  // 1:插入考核对象  2：插入考核主体
		String objectType=(String)this.getFormHM().get("objectType");
		String isSelectAll=(String)this.getFormHM().get("isSelectAll");
		if(plan_id==null|| "".equals(plan_id))
				return ;
		
		ArrayList perObjectlist=new ArrayList();
		 try
	        {
		if("0".equals(isSelectAll))
			perObjectlist=(ArrayList)this.getFormHM().get("selectedList");		
		else if("1".equals(isSelectAll))
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = str_sql+" "+str_whl;
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				String a0100 = this.frowset.getString("a0100");
				LazyDynaBean temp=new LazyDynaBean();
				temp.set("a0100", a0100);
				perObjectlist.add(temp);
			}
		}
		if(perObjectlist==null||perObjectlist.size()==0)
            return;
		
		
		
			 	if("1".equals(flag))   //插入考核对象
			 	{
			    
			 	
			 		ArrayList selectedID=new ArrayList();  //选中的人员的id
			 		StringBuilder objStr = new StringBuilder();
					for(Iterator t=perObjectlist.iterator();t.hasNext();)
		        	{
		        		LazyDynaBean a=(LazyDynaBean)t.next();
		        		String temp=(String)a.get("a0100");
		        		selectedID.add(temp);
		        		objStr.append("'"+temp+"',");
		        	}
					objStr.deleteCharAt(objStr.length()-1);
			
					PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(),this.userView,plan_id);
					bo.handInsertObjects(objStr.toString(),plan_id,objectType);
					
			 		
					
			 	}
			 	else     //插入考核主体
			 	{
			 		String object_id=(String)this.getFormHM().get("object_id");
					String body_id=(String)this.getFormHM().get("body_id");
			 		
			 	
			 		ArrayList selectedID=new ArrayList();  //选中的人员的id
					for(Iterator t=perObjectlist.iterator();t.hasNext();)
		        	{
		        		LazyDynaBean a=(LazyDynaBean)t.next();
		        		String temp=(String)a.get("a0100");
		        		selectedID.add(temp);
		        	}
					PerMainBody perMainBody=new PerMainBody(this.getFrameconn());
					perMainBody.saveMainBody(selectedID,plan_id,object_id,body_id);

			 	}
	        
	

	        }
		    catch(Exception sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
		
		
		
	}

}

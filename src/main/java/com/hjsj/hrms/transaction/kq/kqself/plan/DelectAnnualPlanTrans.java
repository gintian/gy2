package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>Title:</p>
 * <p>Description:删除部门计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-21:13:39:00</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class DelectAnnualPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 if(selectedinfolist==null||selectedinfolist.size()==0)
	            return;
		 ArrayList list=new ArrayList();
		 if(this.userView.isSuper_admin())
		 {
			 for(int i=0;i<selectedinfolist.size();i++)
	         {
				 ArrayList one_value= new ArrayList();
		         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 		        
		         one_value.add(rec.get("q2901").toString());			        
			     list.add(one_value);
	         }
			 String sql="delete from q29 where q2901=?";
			 String sql_emp="delete from q31 where q2901=?";
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 try
			 {
				 dao.batchUpdate(sql_emp,list);
				 dao.batchUpdate(sql,list);
				 
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }	
		 }else
		 {
			 for(int i=0;i<selectedinfolist.size();i++)
	         {
				 ArrayList one_value= new ArrayList();
		         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
		         String state=rec.get("q29z5").toString();
		         if("01".equals(state))
		         {
		        	 one_value.add(rec.get("q2901").toString());
			         one_value.add("01");
			         list.add(one_value);
		         }
		         
	         }
			 String sql="delete from q29 where q2901=? and q29z5=?";
			 String sql_emp="delete from q31 where q2901=? and q31z5=?";
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 try
			 {
				 dao.batchUpdate(sql_emp,list);
				 dao.batchUpdate(sql,list);
				 
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }	
		 }
		 
	}

}

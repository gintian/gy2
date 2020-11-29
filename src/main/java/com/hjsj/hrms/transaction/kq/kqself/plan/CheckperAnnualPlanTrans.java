package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:</p>
 * <p>Description:部门休假信息状态处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-21:14:12:36</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class CheckperAnnualPlanTrans  extends IBusiness {
	public void execute() throws GeneralException 
	{
		ArrayList idlist=(ArrayList)this.getFormHM().get("planid");
		String year =(String) this.getFormHM().get("year");            
		for (int i = 0; i < idlist.size(); i++)
		{
			String[] para=idlist.get(i).toString().split("`");
			String id= para[0];
			String name= para[1];
			String q29z5= para[2];
			if ("04".equals(q29z5) && checkoutEmp(id,year))
			{
				this.getFormHM().put("warn",name+"中有员工没有填写计划，是否确定审核计划？"); 
				return;
			}
		}
		 this.getFormHM().put("warn","确定审核选择的计划？");  
	}


   public boolean checkoutEmp(String q2901,String q2903)
   {
	   boolean isCorrect=false;
	   ArrayList kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn());
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
	   try
	   {
		   for(int i=0;i<kq_dbase_list.size();i++)
		   {
			   String nbase=kq_dbase_list.get(i).toString();
			   String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
			   StringBuffer sql=new StringBuffer();
			   sql.append("select a0101 from "+nbase+"A01 A");
			   sql.append(" where not EXISTS(");
			   sql.append("select a0101 from q31 where q2901='"+q2901+"' and nbase='"+nbase+"'");
			   sql.append(" and q31.a0100=A.a0100");
			   sql.append(" and q31.a0100 in(select a0100 "+whereIN+") )"); 
			   sql.append(" and A.a0100 in(select a0100 "+whereIN+") "); 	
			   sql.append(" and EXISTS(");
			   sql.append("select 1 from q17 where A.a0100=q17.a0100 and  nbase='"+nbase+"' and q1701='"+q2903+"'");
			   sql.append(" and q17.a0100 in(select a0100 "+whereIN+") "); 
			   sql.append(" and q17.q1703>0 "); 
			   sql.append(")");
			   this.frowset=dao.search(sql.toString());			  
			   if(this.frowset.next())
			   {
				   isCorrect=true;
			   }
		   }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return isCorrect;
   }
 
}

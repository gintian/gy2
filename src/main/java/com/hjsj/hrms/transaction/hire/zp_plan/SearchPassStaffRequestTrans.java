/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchPassStaffRequestTrans</p>
 * <p>Description:引入用工需求,zp_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchPassStaffRequestTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 	RecordVo vo=(RecordVo)this.getFormHM().get("zpplanvo");
		 	String org_id = vo.getString("org_id");
		 	//String dept_id = vo.getString("dept_id");
		 	String plan_id = vo.getString("plan_id");
		 	String domain = vo.getString("domain");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		 	 /*处理被这个计划单引用过从新在引用*/
			 updateUsedGather(dao,org_id,plan_id,domain);	
		 	StringBuffer strsql=new StringBuffer();
		 	if("UN".equals(this.userView.getManagePrivCode())){
		 		strsql.append("select dept_id,gather_id from zp_gather where status = '03' and gather_type=1 and usedflag = 0 and  org_id='" + org_id +"' and org_id like '"+this.userView.getManagePrivCodeValue()+"%'");
		 	}else if("UM".equals(this.userView.getManagePrivCode())){
		 		strsql.append("select dept_id,gather_id from zp_gather where status = '03' and gather_type=1 and usedflag = 0 and  org_id='" + org_id +"' and dept_id like '"+this.userView.getManagePrivCodeValue()+"%'");
		 	}else if(!"@K".equals(this.userView.getManagePrivCode())){
		 		strsql.append("select dept_id,gather_id from zp_gather where status = '03' and gather_type=1 and  org_id='" + org_id +"' and usedflag = 0");
		 	}
		    ArrayList gatherlist = new ArrayList();
        	IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		    try
		    {
		      this.frowset = dao.search(strsql.toString());
		      while(this.frowset.next())
		      {
		      	  ResultSet rs=null;
		      	  String dept_id = this.getFrowset().getString("dept_id");
		      	  String gather_id = this.getFrowset().getString("gather_id");	      	  
		      	  String sql = "select pos_id,amount from zp_gather_pos where type='01' and gather_id = '"+gather_id+"'";
		      	  rs=dao.search(sql,gatherlist);
		      	  while(rs.next())
		      	  {
		      	  	String pos_id = rs.getString("pos_id");
		      	  	String amount = String.valueOf(rs.getInt("amount"));
		            String details_id = idg.getId("zp_plan_details.details_id");
		      	  	String delstr = "insert into zp_plan_details (details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status) values('"+details_id+"','"+dept_id+"','"+pos_id+"',"+amount+",'"+domain+"','"+plan_id+"','"+gather_id+"',0,'0','0')";		      	  	
  	      	  	    dao.update(delstr,gatherlist);
		      	  }
		      	  //做使用标志
		      	  String gather_sql = "update zp_gather set usedflag = 1 where gather_id = '"+gather_id+"'";
   	      	  	  dao.update(gather_sql,gatherlist);
		      }
		    }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    finally
		    {
		    	this.getFormHM().put("plan_id_value",plan_id);
		    }
	}
	private void updateUsedGather(ContentDAO dao,String org_id,String plan_id,String domain) throws GeneralException
	{
		StringBuffer strsql =new StringBuffer();
		 if("UN".equals(this.userView.getManagePrivCode())){
		 	strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 1 and usedflag=1 and status='03' and org_id like '"+this.userView.getManagePrivCodeValue()+"%' and org_id in (select org_id from zp_plan where plan_id='" +plan_id+  "') and gather_id in (select gather_id from zp_plan_details where plan_id='" +plan_id +  "')");
	     }else if("UM".equals(this.userView.getManagePrivCode())){
		    strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 1 and usedflag=1 and org_id = '"+org_id +"' and status='03' and dept_id like '"+this.userView.getManagePrivCodeValue()+"%' and org_id in (select org_id from zp_plan where plan_id='" +plan_id +  "') and gather_id in (select gather_id from zp_plan_details where plan_id='" +plan_id +  "')");
		 }else if(!"@K".equals(this.userView.getManagePrivCode())){
		 	strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 1 and usedflag=1 and status='03' and org_id = '"+org_id+"' and org_id  in (select org_id from zp_plan where plan_id='" +plan_id +  "') and gather_id in (select gather_id from zp_plan_details where plan_id='" +plan_id +  "')");
		 }
		 //System.out.println(strsql.toString());
		 try
		 {
		 	ArrayList list=new ArrayList();
		    this.frowset = dao.search(strsql.toString());
		    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		    while(this.frowset.next())
			{	
		    	 String ssql = "select type,pos_id,amount,reason from zp_gather_pos where type='01' and gather_id = '"+this.getFrowset().getString("gather_id")+"' and pos_id not in (select pos_id from zp_plan_details where plan_id='" +plan_id +  "' and gather_id='" +this.getFrowset().getString("gather_id")+ "')";
		    	 //System.out.println(ssql);
		    	 ResultSet rs = dao.search(ssql,list);
			   	 while(rs.next()){
		   	        try{
		   	        	String pos_id = rs.getString("pos_id");
			      	  	String amount = String.valueOf(rs.getInt("amount"));
			            String details_id = idg.getId("zp_plan_details.details_id");
			      	  	String delstr = "insert into zp_plan_details (details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status) values('"+details_id+"','"+this.getFrowset().getString("dept_id")+"','"+pos_id+"',"+amount+",'"+domain+"','"+plan_id+"','"+this.getFrowset().getString("gather_id")+"',0,'0','0')";		      	  	
	  	      	  	    dao.update(delstr);
		   	        }catch(Exception e){
		   	        	e.printStackTrace();
		            	throw GeneralExceptionHandler.Handle(e);
		   	        }finally{
		   	        }
		   	   }
		    }
		 }catch(SQLException sqle)
		 {
			   sqle.printStackTrace();
			   throw GeneralExceptionHandler.Handle(sqle);
		 }
	}

}

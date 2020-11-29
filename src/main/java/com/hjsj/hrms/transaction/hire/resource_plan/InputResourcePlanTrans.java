/*
 * Created on 2005-10-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InputResourcePlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		 RecordVo vo = (RecordVo)this.getFormHM().get("zpplanvo");		
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 /*处理被这个计划单引用过从新在引用*/
		 updateUsedGather(dao,vo);	
		 /*处理没有引用过的需求单*/
		 StringBuffer strsql=new StringBuffer();
		 if("UN".equals(this.userView.getManagePrivCode())){
		 	strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=0 and  status='03' and org_id = '"+vo.getString("org_id")+"' and org_id like '"+this.userView.getManagePrivCodeValue()+"%'");
	 	}else if("UM".equals(this.userView.getManagePrivCode())){
		    strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=0 and org_id = '"+vo.getString("org_id")+"' and status='03' and dept_id like '"+this.userView.getManagePrivCodeValue()+"%'");
		 }else  if(!"@K".equals(this.userView.getManagePrivCode())){
		 	strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=0 and org_id = '"+vo.getString("org_id")+"' and status='03'");
		 }		
		 ArrayList list=new ArrayList();
		 try
		 {
		   this.frowset = dao.search(strsql.toString());
		   while(this.frowset.next())
		   {
		   	   String ssql = "select type,pos_id,amount,reason from zp_gather_pos where gather_id = '"+this.getFrowset().getString("gather_id")+"'";
		   	   ResultSet rs = dao.search(ssql,list);
		   	   while(rs.next()){
		   	        PreparedStatement pstmt=null;
		   	        try{
		   	           IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                       String key_id = idg.getId("zp_hr_plan_details.key_id");
                       String sql = "insert into zp_hr_plan_details (key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id) values (?,?,?,?,?,?,?,?,?)";
                       list.clear();
                       list.add(key_id);
                       list.add(vo.getString("plan_id"));
                       list.add(rs.getString("type"));
                       list.add(this.getFrowset().getString("dept_id"));
                       list.add(rs.getString("pos_id"));
                       list.add(Integer.parseInt(rs.getString("amount")));
                       list.add(DateUtils.getSqlDate(new Date()));
                       list.add(rs.getString("reason"));
                       list.add(this.getFrowset().getString("gather_id"));
                      /* pstmt=this.getFrameconn().prepareStatement(sql);
                       pstmt.setString(1,key_id);
		  			   pstmt.setString(2,vo.getString("plan_id"));
		  			   pstmt.setString(3,rs.getString("type"));
		  			   pstmt.setString(4,this.getFrowset().getString("dept_id"));
		  			   pstmt.setString(5,rs.getString("pos_id"));	 
		  			   pstmt.setInt(6,Integer.parseInt(rs.getString("amount")));	
		  			   pstmt.setDate(7,DateUtils.getSqlDate(new Date()));
		  			   pstmt.setString(8,rs.getString("reason"));
		  			   pstmt.setString(9,this.getFrowset().getString("gather_id"));
		  			   pstmt.executeUpdate();*/
                       dao.insert(sql, list);
		   	        }catch(Exception e){
		   	        	e.printStackTrace();
		            	throw GeneralExceptionHandler.Handle(e);
		   	        }finally{
		   	        	try
		    			{
		    				if(pstmt!=null)
		    					pstmt.close();
		    			}
		    			catch(SQLException ee)
		    			{
		    				ee.printStackTrace();
		    			}
		   	        }
		   	   }
  			   strsql.delete(0,strsql.length());
  			   strsql.append("update zp_gather set usedflag=1 where gather_id='");
  			   strsql.append(this.getFrowset().getString("gather_id"));
  			   strsql.append("'");
  			   dao.update(strsql.toString());
		   }
		}catch(SQLException sqle)
		{
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		}
		finally
		{
		    this.getFormHM().put("zpplanlist",list); 
		}

	}
	private void updateUsedGather(ContentDAO dao,RecordVo vo) throws GeneralException
	{
		StringBuffer strsql =new StringBuffer();
		 if("UN".equals(this.userView.getManagePrivCode())){
		 	 strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=1 and  status='03' and org_id like '"+this.userView.getManagePrivCodeValue()+"%' and org_id in (select org_id from zp_hr_plan where plan_id='" +vo.getString("plan_id") +  "') and gather_id in (select gather_id from zp_hr_plan_details where plan_id='" +vo.getString("plan_id") +  "')");
	     }else if("UM".equals(this.userView.getManagePrivCode())){
		    strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=1 and org_id = '"+vo.getString("org_id")+"' and status='03' and dept_id like '"+this.userView.getManagePrivCodeValue()+"%' and org_id in (select org_id from zp_hr_plan where plan_id='" +vo.getString("plan_id") +  "') and gather_id in (select gather_id from zp_hr_plan_details where plan_id='" +vo.getString("plan_id") +  "')");
		 }else if(!"@K".equals(this.userView.getManagePrivCode())){
		 	strsql.append("select gather_id,org_id,dept_id from zp_gather where gather_type = 0 and usedflag=1 and status='03' and org_id = '"+vo.getString("org_id")+"' and org_id  in (select org_id from zp_hr_plan where plan_id='" +vo.getString("plan_id") +  "') and gather_id in (select gather_id from zp_hr_plan_details where plan_id='" +vo.getString("plan_id") +  "')");
		 }
		 try
		 {
		 	ArrayList list=new ArrayList();
		    this.frowset = dao.search(strsql.toString());
		    while(this.frowset.next())
			{	
		    	 String ssql = "select type,pos_id,amount,reason from zp_gather_pos where gather_id = '"+this.getFrowset().getString("gather_id")+"' and pos_id not in (select pos_id from zp_hr_plan_details where plan_id='" +vo.getString("plan_id") +  "' and gather_id='" +this.getFrowset().getString("gather_id")+ "')";
		    	 ResultSet rs = dao.search(ssql,list);
			   	 while(rs.next()){
		   	        PreparedStatement pstmt=null;
		   	        try{
		   	           IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                       String key_id = idg.getId("zp_hr_plan_details.key_id");
                       String sql = "insert into zp_hr_plan_details (key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id) values (?,?,?,?,?,?,?,?,?)";
                       list.clear();
                       list.add(key_id);
                       list.add(vo.getString("plan_id"));
                       list.add(rs.getString("type"));
                       list.add(this.getFrowset().getString("dept_id"));
                       list.add(rs.getString("pos_id"));
                       list.add(Integer.parseInt(rs.getString("amount")));
                       list.add(DateUtils.getSqlDate(new Date()));
                       list.add(rs.getString("reason"));
                       list.add(this.getFrowset().getString("gather_id"));
                      /* pstmt=this.getFrameconn().prepareStatement(sql);
                       pstmt.setString(1,key_id);
		  			   pstmt.setString(2,vo.getString("plan_id"));
		  			   pstmt.setString(3,rs.getString("type"));
		  			   pstmt.setString(4,this.getFrowset().getString("dept_id"));
		  			   pstmt.setString(5,rs.getString("pos_id"));	 
		  			   pstmt.setInt(6,Integer.parseInt(rs.getString("amount")));	
		  			   pstmt.setDate(7,DateUtils.getSqlDate(new Date()));
		  			   pstmt.setString(8,rs.getString("reason"));	
		  			   pstmt.setString(9,this.getFrowset().getString("gather_id"));
		  			   pstmt.executeUpdate();*/
                       dao.insert(sql, list);
		   	        }catch(Exception e){
		   	        	e.printStackTrace();
		            	throw GeneralExceptionHandler.Handle(e);
		   	        }finally{
		   	        	try
		    			{
		    				if(pstmt!=null)
		    					pstmt.close();
		    			}
		    			catch(SQLException ee)
		    			{
		    				ee.printStackTrace();
		    			}
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

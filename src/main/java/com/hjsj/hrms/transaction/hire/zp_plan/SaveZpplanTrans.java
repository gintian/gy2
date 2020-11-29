/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:SaveZpplanTrans</p>
 * <p>Description:保存招聘计划,zp_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpplanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanvo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增人力规划
             */
        	PreparedStatement pstmt=null;
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String plan_id=idg.getId("zp_plan.plan_id");
               vo.setString("plan_id",plan_id); 
               String sql = "insert into zp_plan (plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status) values(?,?,?,"+PubFunc.DateStringChange(vo.getString("start_date"))+","+PubFunc.DateStringChange(vo.getString("end_date"))+",?,0,?,0,?,?,?,?,?,'01')";
               ArrayList values = new ArrayList();
               values.add(plan_id);
               values.add(vo.getString("name"));
               values.add(vo.getString("org_id"));
               values.add(vo.getString("budget_fee"));
               values.add(vo.getString("plan_invite_amount"));
               values.add(vo.getString("dept_id"));
               values.add(vo.getString("staff_id"));
               values.add(DateUtils.getSqlDate(new Date()));
               values.add(vo.getString("domain"));	
               values.add(vo.getString("zp_object"));
               dao.insert(sql, values);
               /* pstmt=this.getFrameconn().prepareStatement(sql);
  			   pstmt.setString(1,plan_id);
  			   pstmt.setString(2,vo.getString("name"));
  			   pstmt.setString(3,vo.getString("org_id"));
  			   pstmt.setString(4,vo.getString("budget_fee"));
  			   pstmt.setString(5,vo.getString("plan_invite_amount"));
  			   pstmt.setString(6,vo.getString("dept_id"));
			   pstmt.setString(7,vo.getString("staff_id"));
  			   pstmt.setDate(8,DateUtils.getSqlDate(new Date()));
  			   pstmt.setString(9,vo.getString("domain"));	
  			   pstmt.setString(10,vo.getString("zp_object"));	
  			   pstmt.executeUpdate();*/
               this.getFormHM().put("plan_id_value",String.valueOf(plan_id));
               this.getFormHM().put("flag","0");
            }catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(e);
            }finally
			{
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
        else if("0".equals(flag))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String org_id_value="";
	        	String dept_id_value="";
	        	if(vo.getString("org_id") != null && !"".equals(vo.getString("org_id"))){
	        		org_id_value = vo.getString("org_id");
	        		dept_id_value=vo.getString("dept_id");
	        		
	        	}
	        	/*看是否后org_id变化否变化则处理子集的所有信息*/
	        	this.frowset=dao.search("select org_id,dept_id from zp_plan where plan_id='" + vo.getString("plan_id")+"'");
	        	if(this.frowset.next())
	        	{
	        		String orgid=this.frowset.getString("org_id");
	        		String deptid=this.frowset.getString("dept_id");
	        		if(!orgid.equalsIgnoreCase(org_id_value) || !deptid.equalsIgnoreCase(dept_id_value))
	        		{
	        			this.frowset=dao.search("select distinct gather_id from zp_plan_details where plan_id='" + vo.getString("plan_id")+"'");
	        			while(this.frowset.next())
	        			{
	        				dao.update("update zp_gather set usedflag=0 where gather_id='" + this.frowset.getString("gather_id") + "'");
	        			}
	        			dao.delete("delete from zp_plan_details where plan_id='" + vo.getString("plan_id")+"'",list);
	        		}
	        	}
	        	String sql="update zp_plan set name='"+vo.getString("name")+"',org_id='"+vo.getString("org_id")+"',start_date="+PubFunc.DateStringChange(vo.getString("start_date"))+",end_date="+PubFunc.DateStringChange(vo.getString("end_date"))+",budget_fee="+vo.getString("budget_fee")+",plan_invite_amount="+vo.getString("plan_invite_amount")+",dept_id = '"+vo.getString("dept_id")+"',staff_id = '"+vo.getString("staff_id")+"',domain = '"+vo.getString("domain")+"',zp_object = '"+vo.getString("zp_object")+"'  where plan_id='"+vo.getString("plan_id")+"'";
	        	dao.update(sql,list);
	        	this.getFormHM().put("plan_id_value",vo.getString("plan_id"));
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}

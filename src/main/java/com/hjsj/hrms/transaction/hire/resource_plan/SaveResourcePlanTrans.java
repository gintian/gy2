/*
 * Created on 2005-8-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:SaveResourcePlanTrans</p>
 * <p>Description:保存人力规划，zp_hr_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveResourcePlanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanvo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        String flag_mid = (String)this.getFormHM().get("flag_mid");
        String org_id_value = (String)this.getFormHM().get("org_id");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增人力规划
             */
        	PreparedStatement pstmt=null;
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String plan_id=idg.getId("zp_hr_plan.plan_id");
               vo.setString("plan_id",plan_id);
               vo.setString("staff_id",this.userView.getUserName());
               vo.setString("create_date",DateStyle.getSystemTime());
               String sql = "insert into zp_hr_plan (plan_id,org_id,name,run_date,description,create_date,staff_id,status) " +"values(?,?,?,"+PubFunc.DateStringChange(vo.getString("run_date"))+",?,?,?,'01')";
               ArrayList values = new ArrayList();
               values.add(plan_id);
               values.add(vo.getString("org_id"));
               values.add(vo.getString("name"));
               if(vo.getString("description").length()>250)
    			     values.add(vo.getString("description").substring(0,250));
			   else
    				 values.add(vo.getString("description"));
               values.add(DateUtils.getSqlDate(new Date()));
               values.add(vo.getString("staff_id"));
               dao.insert(sql, values);
               /*pstmt=this.getFrameconn().prepareStatement(sql);
  			   pstmt.setString(1,plan_id);
  			   pstmt.setString(2,vo.getString("org_id"));
  			   pstmt.setString(3,vo.getString("name"));
  			   if(vo.getString("description").length()>250)
  			     pstmt.setString(4,vo.getString("description").substring(0,250));
  			   else
  			     pstmt.setString(4,vo.getString("description"));
  			   pstmt.setDate(5,DateUtils.getSqlDate(new Date()));
  			   pstmt.setString(6,vo.getString("staff_id"));	
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
	        	if(vo.getString("org_id") != null && !"".equals(vo.getString("org_id"))){
	        		org_id_value = vo.getString("org_id");
	        	}
	        	/*看是否后org_id变化否变化则处理子集的所有信息*/
	        	this.frowset=dao.search("select org_id from zp_hr_plan where plan_id='" + vo.getString("plan_id")+"'");
	        	if(this.frowset.next())
	        	{
	        		String orgid=this.frowset.getString("org_id");
	        		if(!orgid.equalsIgnoreCase(org_id_value))
	        		{
	        			this.frowset=dao.search("select distinct gather_id from zp_hr_plan_details where plan_id='" + vo.getString("plan_id")+"'");
	        			while(this.frowset.next())
	        			{
	        				dao.update("update zp_gather set usedflag=0 where gather_id='" + this.frowset.getString("gather_id") + "'");
	        			}
	        			dao.delete("delete from zp_hr_plan_details where plan_id='" + vo.getString("plan_id")+"'",list);
	        		}
	        	}
	        	String sql="update zp_hr_plan set org_id='"+org_id_value+"',name='"+vo.getString("name")+"',run_date="+PubFunc.DateStringChange(vo.getString("run_date"))+",description='"+vo.getString("description")+"' where plan_id='"+vo.getString("plan_id")+"'";
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

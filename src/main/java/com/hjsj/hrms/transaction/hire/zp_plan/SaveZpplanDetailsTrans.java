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

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpplanDetailsTrans</p>
 * <p>Description:保存招聘计划明细,zp_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpplanDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanDetailsvo");
		String plan_id = vo.getString("plan_id");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        if(vo==null)
            return;
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag_detail))
        {
            /**
             * 新增招聘计划明细
             */
        	try{
        	   IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String details_id = idg.getId("zp_plan_details.details_id");
        	   String sql = "insert into zp_plan_details (details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status) values('"+details_id+"','"+vo.getString("dept_id")+"','"+vo.getString("pos_id")+"',"+vo.getString("amount")+",'"+vo.getString("domain")+"','"+plan_id_value+"','',0,'0','"+vo.getString("status")+"')";
               ArrayList list = new ArrayList();
               dao.update(sql,list);
               this.getFormHM().put("plan_id_value",plan_id_value);
                }catch(Exception e){
        	        e.printStackTrace();
        	        throw GeneralExceptionHandler.Handle(e);
                }
                
        }
        else if("0".equals(flag_detail))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_plan_details set dept_id = '"+vo.getString("dept_id")+"',pos_id='"+vo.getString("pos_id")+"',amount="+vo.getString("amount")+",domain = '"+vo.getString("domain")+"',gather_id ='"+vo.getString("gather_id")+"',invite_amount = "+vo.getString("invite_amount")+",invite_flag = '"+vo.getString("invite_flag")+"',status = '"+vo.getString("status")+"' where plan_id='"+vo.getString("plan_id")+"' and details_id = '"+vo.getString("details_id")+"'";
	        	dao.update(sql,list);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}

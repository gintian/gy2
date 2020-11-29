/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchZpplanDetailsTrans</p>
 * <p>Description:查询招聘计划明细,zp_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpplanDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
  
        String  plan_id_value= (String)this.getFormHM().get("plan_id_value");
        String plan_id=plan_id_value;
        String details_id = (String)hm.get("details_id");
        String gather_id = (String)hm.get("gather_id");
        RecordVo vo=new RecordVo("zp_plan_details");
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        String dept_pos_id_value = "";
        String pos_id_value = "";
        /**
         * 按新增按钮时，则不进行查询，直接退出
         */
        if("1".equals(flag_detail)){
        	if("UM".equals(this.userView.getManagePrivCode())){
        		vo.setString("dept_id",this.userView.getManagePrivCodeValue());
        		vo.setString("plan_id",plan_id_value);
        		this.getFormHM().put("zpplanDetailsvo",vo);
        		this.getFormHM().put("dept_pos_id_value", AdminCode.getCodeName("UM",this.userView.getManagePrivCodeValue())); 
        		this.getFormHM().put("posparentcode", this.userView.getManagePrivCodeValue()); 
        	}else if("UN".equals(this.userView.getManagePrivCode())){
        		vo.setString("plan_id",plan_id_value);
        		this.getFormHM().put("zpplanDetailsvo",vo);
        		this.getFormHM().put("dept_pos_id_value","");
        		this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue()); 
        	}else{
        	   String sqle = "select org_id from zp_plan where plan_id = '"+plan_id_value+"'";
        	  try{
        	     this.frowset = dao.search(sqle);
        	     while(this.frowset.next()){
        	   	    this.getFormHM().put("deptparentcode",this.frowset.getString("org_id")); 
        	     }
        	     vo.setString("plan_id",plan_id_value);
        	     this.getFormHM().put("zpplanDetailsvo",vo);
        	     this.getFormHM().put("dept_pos_id_value","");
        	  }catch(SQLException e){
        	     e.printStackTrace();
      	         throw GeneralExceptionHandler.Handle(e);  
        	  }
        	}
        	this.getFormHM().put("plan_id_value",plan_id_value);
        	this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
        	this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
        }else{
        try
        {
        	String sqle = "select org_id from zp_plan where plan_id = '"+plan_id+"'";
        	this.frowset = dao.search(sqle);
        	while(this.frowset.next()){
        		this.getFormHM().put("deptparentcode", this.getFrowset().getString("org_id"));
        	}
            vo.setString("plan_id",plan_id);
            vo.setString("details_id",details_id);
            vo.setString("gather_id",gather_id);
            String sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where details_id = '"+details_id+"'";
            ArrayList list = new ArrayList();
            ResultSet rs=dao.search(sql,list);
            while(rs.next()){
            	vo.setString("dept_id",rs.getString("dept_id"));
            	vo.setString("pos_id",rs.getString("pos_id"));
            	vo.setString("amount",String.valueOf(rs.getInt("amount")));
            	vo.setString("status",rs.getString("status"));
            	vo.setString("domain",rs.getString("domain"));
            	vo.setString("invite_amount",String.valueOf(rs.getInt("invite_amount")));
            }
            dept_pos_id_value = AdminCode.getCodeName("UM",vo.getString("dept_id"));
            pos_id_value = AdminCode.getCodeName("@K",vo.getString("pos_id"));
            this.getFormHM().put("posparentcode",vo.getString("dept_id"));
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zpplanDetailsvo",vo);
            this.getFormHM().put("plan_id_value",plan_id);
            this.getFormHM().put("dept_pos_id_value",dept_pos_id_value);
            this.getFormHM().put("pos_id_value",pos_id_value);
            this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
            this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
        }

	}

	}

}

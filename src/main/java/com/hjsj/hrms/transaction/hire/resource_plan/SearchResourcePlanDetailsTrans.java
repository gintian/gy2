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
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchResourcePlanDetailsTrans</p>
 * <p>Description:查询人力规划明细，zp_hr_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchResourcePlanDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String plan_id=(String)hm.get("a_id");
        String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        String key_id = (String)hm.get("key_id");
        RecordVo vo=new RecordVo("zp_hr_plan_details",1);
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        String pos_id_value = "";
		String dept_id_value = "";
        /**
         * 按新增按钮时，则不进行查询，直接退出
         */
        if("1".equals(flag_detail)){
        	if("UM".equals(this.userView.getManagePrivCode())){
        		vo.setString("dept_id",this.userView.getManagePrivCodeValue());
        		vo.setString("plan_id",plan_id_value);
        		this.getFormHM().put("zpplanDetailsvo",vo);
        		this.getFormHM().put("dept_id_value", AdminCode.getCodeName("UM",this.userView.getManagePrivCodeValue())); 
        		this.getFormHM().put("posparentcode", this.userView.getManagePrivCodeValue()); 
        	}else if("UN".equals(this.userView.getManagePrivCode())){
        		vo.setString("plan_id",plan_id_value);
        		this.getFormHM().put("zpplanDetailsvo",vo);
        		this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue()); 
        	}else{
        	   String sqle = "select org_id from zp_hr_plan where plan_id = '"+plan_id_value+"'";
        	  try{
        	     this.frowset = dao.search(sqle);
        	     while(this.frowset.next()){
        	   	    this.getFormHM().put("deptparentcode",this.frowset.getString("org_id")); 
        	     }
        	     vo.setString("plan_id",plan_id_value);
        	     this.getFormHM().put("zpplanDetailsvo",vo); 
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
        	String strsql = "select org_id from zp_hr_plan where plan_id = '"+plan_id+"'";
        	this.frowset = dao.search(strsql);
        	while(this.frowset.next()){
        		this.getFormHM().put("deptparentcode",this.frowset.getString("org_id")); 
        	}
            vo.setString("plan_id",plan_id);
            vo.setString("key_id",key_id);
            String sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where key_id = "+key_id;
            ArrayList list = new ArrayList();
            ResultSet rs=dao.search(sql,list);
            while(rs.next()){
            	vo.setString("dept_id",rs.getString("dept_id"));
            	vo.setString("pos_id",rs.getString("pos_id"));
            	vo.setString("amount",String.valueOf(rs.getInt("amount")));
            	vo.setString("type",rs.getString("type"));
            	vo.setString("valid_date", PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("valid_date"))));
            	vo.setString("reason",rs.getString("reason"));
            	vo.setString("gather_id", rs.getString("gather_id"));
            }
			  dept_id_value = AdminCode.getCodeName("UM",vo.getString("dept_id"));
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
            this.getFormHM().put("pos_id_value",pos_id_value);
			this.getFormHM().put("dept_id_value",dept_id_value);
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
        }

	}

	}

}

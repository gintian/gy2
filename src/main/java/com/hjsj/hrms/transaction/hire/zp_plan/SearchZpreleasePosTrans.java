/*
 * Created on 2005-8-11
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
 * <p>Title:SearchZpreleasePosTrans</p>
 * <p>Description:查询招聘发布岗位,zp_position</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpreleasePosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo vo=new RecordVo("zp_position");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String zp_pos_id=(String)hm.get("a_id");
        String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        String flag_release=(String)this.getFormHM().get("flag_release");
        String dept_id_value = "";
        String pos_id_value = "";
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag_release)){
        	if("UM".equals(this.userView.getManagePrivCode())){
        		vo.setString("dept_id",this.userView.getManagePrivCodeValue());
        		this.getFormHM().put("zppositionvo",vo);
        		this.getFormHM().put("plan_id_value",plan_id_value);
        		this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
        		this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
        		this.getFormHM().put("dept_id_value",AdminCode.getCodeName("UM",this.userView.getManagePrivCodeValue()));
        		this.getFormHM().put("posparentcode",this.userView.getManagePrivCodeValue());
        		return;
        	}else{
        	   String sql = "select org_id from zp_plan where plan_id = '"+plan_id_value+"'";
        	   try{
        	      this.frowset = dao.search(sql);
        	      while(this.frowset.next()){
        	   	     this.getFormHM().put("deptparentcode",this.getFrowset().getString("org_id"));
        	      }
        	      this.getFormHM().put("plan_id_value",plan_id_value);
                  return;
        	   }catch(SQLException sqle){
        		   sqle.printStackTrace();
      	           throw GeneralExceptionHandler.Handle(sqle);  
               }
        	}
        }        
        try
        {
        	String sql = "select org_id from zp_plan where plan_id = '"+plan_id_value+"'";
        	this.frowset = dao.search(sql);
     	    while(this.frowset.next()){
     	   	   this.getFormHM().put("deptparentcode",this.getFrowset().getString("org_id"));
     	    }
        	vo.setString("zp_pos_id",zp_pos_id);
        	ArrayList list = new ArrayList();
            String strsql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where zp_pos_id = '"+zp_pos_id+"'";
            ResultSet rs = dao.search(strsql,list);
            while(rs.next()){
               vo.setString("amount",rs.getString("amount"));
               vo.setString("plan_id",rs.getString("plan_id"));
               vo.setString("dept_id",rs.getString("dept_id"));
               vo.setString("pos_id",rs.getString("pos_id"));
               vo.setString("domain",rs.getString("domain"));
               vo.setString("status",rs.getString("status"));
               dept_id_value = AdminCode.getCodeName("UM",rs.getString("dept_id"));
               pos_id_value = AdminCode.getCodeName("@K",rs.getString("pos_id"));
               this.getFormHM().put("posparentcode",rs.getString("dept_id"));
               this.getFormHM().put("dept_id_1",rs.getString("dept_id"));
               this.getFormHM().put("pos_id_1",rs.getString("pos_id"));
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zppositionvo",vo);
            this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
            this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
            this.getFormHM().put("plan_id_value",plan_id_value);
            this.getFormHM().put("dept_id_value",dept_id_value);
            this.getFormHM().put("pos_id_value",pos_id_value);
        }

	}

}

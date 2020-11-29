/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_release_pos;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
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
		
		    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
	        String zp_pos_id=(String)hm.get("a_id");
	        String flag=(String)this.getFormHM().get("flag");
	        String pos_id_value = "";
	        String dept_id_value = "";
	        /**
	         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
	         * 用户的使用习惯。
	         */
	        if("1".equals(flag)){
	        	RecordVo vo=new RecordVo("zp_position");
	        	 vo.setString("amount","");
	             vo.setString("plan_id","");
	             if(!"UN".equalsIgnoreCase(this.userView.getManagePrivCode()))	             	
	                 vo.setString("dept_id",this.userView.getManagePrivCodeValue());
	             vo.setString("pos_id","");
	             vo.setString("domain","");
	             vo.setString("status","");
	             dept_id_value = AdminCode.getCodeName("UM",this.userView.getManagePrivCodeValue());
	             pos_id_value = AdminCode.getCodeName("@K",this.userView.getUserPosId());
	        	this.getFormHM().put("zpreleasePosvo",vo);
	            this.getFormHM().put("dept_id_value",dept_id_value);
	        	this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue());
	        	this.getFormHM().put("posparentcode",this.userView.getManagePrivCodeValue());
	        }else{
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        RecordVo vo=new RecordVo("zp_position");
	        try
	        {
	            vo.setString("zp_pos_id",zp_pos_id);
	            String sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where zp_pos_id = '"+zp_pos_id+"'";
	            this.frowset = dao.search(sql);
	            while(this.frowset.next()){
	               vo.setString("amount",this.getFrowset().getString("amount"));
	               vo.setString("plan_id",this.getFrowset().getString("plan_id"));
	               vo.setString("dept_id",this.getFrowset().getString("dept_id"));
	               vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	               vo.setString("domain",this.getFrowset().getString("domain"));
	               vo.setString("status",this.getFrowset().getString("status"));
	               dept_id_value = AdminCode.getCodeName("UM",this.getFrowset().getString("dept_id"));
	               pos_id_value = AdminCode.getCodeName("@K",this.getFrowset().getString("pos_id"));
	               this.getFormHM().put("deptparentcode",this.getFrowset().getString("dept_id"));
	               this.getFormHM().put("posparentcode",this.userView.getManagePrivCodeValue());
	            }
	        }
	        catch(SQLException sqle)
	        {
	  	      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);            
	        }
	        finally
	        {
	            this.getFormHM().put("zpreleasePosvo",vo);
	            this.getFormHM().put("dept_id_value",dept_id_value);
	            this.getFormHM().put("pos_id_value",pos_id_value);
	            this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue());
	            this.getFormHM().put("posparentcode",this.userView.getManagePrivCodeValue());
	        }
	      }

	}

}

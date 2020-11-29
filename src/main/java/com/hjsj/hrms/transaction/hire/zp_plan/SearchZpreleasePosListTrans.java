/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * <p>Title:SearchZpreleasePosListTrans</p>
 * <p>Description:查询招聘发布岗位列表,zp_position</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpreleasePosListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
		ArrayList list = new ArrayList();
        try{
        	String query_sql = "";
        	if("UN".equals(this.userView.getManagePrivCode())){
        		query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where dept_id like '"+this.userView.getManagePrivCodeValue()+"%'";
        	}else if("UM".equals(this.userView.getManagePrivCode())){
        		query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
        	}else{
        		query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position";
        	}
            this.frowset = dao.search(query_sql);
            while(this.frowset.next()){
            	RecordVo vo_pos = new RecordVo("zp_position",1);
            	vo_pos.setString("zp_pos_id",this.frowset.getString("zp_pos_id"));
            	vo_pos.setString("amount",this.frowset.getString("amount"));
            	vo_pos.setString("plan_id",this.frowset.getString("plan_id"));
            	vo_pos.setString("dept_id",this.frowset.getString("dept_id"));
            	vo_pos.setString("pos_id",this.frowset.getString("pos_id"));
            	vo_pos.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
            	vo_pos.setString("domain",PubFunc.toHtml(this.frowset.getString("domain")));
            	vo_pos.setString("status",this.frowset.getString("status"));
            	list.add(vo_pos);
            } 
	} catch (SQLException sqle) {
		 sqle.printStackTrace();
	     throw GeneralExceptionHandler.Handle(sqle); 
	}finally {
			this.getFormHM().put("zppositionlist", list);
			this.getFormHM().put("plan_id_value", plan_id_value);
			
		}

	}

}


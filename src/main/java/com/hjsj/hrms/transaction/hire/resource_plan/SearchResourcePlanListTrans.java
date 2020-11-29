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
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchResourcePlanListTrans</p>
 * <p>Description:查询人力规划列表，zp_hr_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchResourcePlanListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    StringBuffer strsql=new StringBuffer();
		    if("su".equals(this.userView.getUserId())){
		       strsql.append("select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan");
		    }else{
		    	if("UN".equals(this.userView.getManagePrivCode())){
		    		strsql.append("select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan where org_id like '"+this.userView.getManagePrivCodeValue()+"%' order by  plan_id desc");
			    }else if("UM".equals(this.userView.getManagePrivCode())){
			    	strsql.append("select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan where plan_id in (select plan_id from zp_hr_plan_details where dept_id = '"+this.userView.getManagePrivCodeValue()+"') order by  plan_id desc");    	
			    }else if(this.userView.getManagePrivCode() == null || "".equals(this.userView.getManagePrivCode())){
			    	strsql.append("select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan where plan_id in (select plan_id from zp_hr_plan_details where dept_id = '') order by  plan_id desc");
			    }
		    }
		       
		    ArrayList list=new ArrayList();
		    try
		    {
		      this.frowset = dao.search(strsql.toString());
		      while(this.frowset.next())
		      {
		          RecordVo vo=new RecordVo("zp_hr_plan",1);
		          vo.setString("plan_id",this.getFrowset().getString("plan_id"));
		          vo.setString("name",this.getFrowset().getString("name"));
		          vo.setString("org_id",this.getFrowset().getString("org_id"));
		          vo.setString("run_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("run_date"))));
		          vo.setString("create_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("create_date"))));
		          vo.setString("description",PubFunc.toHtml(this.getFrowset().getString("description"))); 
		          vo.setString("staff_id",this.getFrowset().getString("staff_id"));
		          vo.setString("status",this.getFrowset().getString("status"));
		          list.add(vo);
		      }
		    }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    finally
		    {
		        this.getFormHM().put("zpplanlist",list); 
		        this.getFormHM().put("userid",this.userView.getUserId()); 
		        this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
		        this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
		    }

	}

}

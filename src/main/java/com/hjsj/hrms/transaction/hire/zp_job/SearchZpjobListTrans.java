/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchZpjobListTrans</p>
 * <p>Description:查询招聘活动列表,zp_job</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpjobListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer strsql=new StringBuffer();
		if("su".equals(this.userView.getUserId())){
	       strsql.append("select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job order by  zp_job_id desc");
		}else if("UM".equals(this.userView.getManagePrivCode())){
	    	strsql.append("select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job where plan_id in (select plan_id from zp_plan_details where dept_id = '"+this.userView.getManagePrivCodeValue()+"') order by  zp_job_id desc");
		}else if("UN".equals(this.userView.getManagePrivCode())){
			strsql.append("select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job where plan_id in (select plan_id from zp_plan where org_id = '"+this.userView.getManagePrivCodeValue()+"') order by  zp_job_id desc");
		}else if(this.userView.getManagePrivCode() == null || "".equals(this.userView.getManagePrivCode())){
			strsql.append("select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job where plan_id in (select plan_id from zp_plan where org_id = '') order by  zp_job_id desc");
		}
	    
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_job",1);
	          vo.setString("zp_job_id",this.getFrowset().getString("zp_job_id"));
	          vo.setString("name",this.getFrowset().getString("name"));
	          vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("start_date"))));
	          vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("end_date"))));
	          vo.setString("principal",this.getFrowset().getString("principal"));
	          vo.setString("attendee",PubFunc.toHtml(this.getFrowset().getString("attendee")));
	          vo.setString("status",this.getFrowset().getString("status"));
	          vo.setString("plan_id",this.getFrowset().getString("plan_id"));
	          vo.setString("resource_id",this.getFrowset().getString("resource_id"));
	          vo.setString("description",this.getFrowset().getString("description"));
	          vo.setString("real_invite_amount",this.getFrowset().getString("real_invite_amount"));
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
	        this.getFormHM().put("zpjoblist",list);
	        this.getFormHM().put("userid",this.userView.getUserId()); 
	        this.getFormHM().put("managepriv",this.userView.getManagePrivCode()); 
	    }

	}

}

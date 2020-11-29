/*
 * Created on 2005-8-8
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
 * <p>Title:SearchZpplanListTrans</p>
 * <p>Description:查询招聘计划列表,zp_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpplanListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {		
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer strsql=new StringBuffer();
		if("su".equals(this.userView.getUserId())){
	       strsql.append("select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan  order by  plan_id desc");
		}else{
			if("UN".equals(this.userView.getManagePrivCode())){
				strsql.append("select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan where org_id like '"+this.userView.getManagePrivCodeValue()+"%'  order by  plan_id desc");
			}else if("UM".equals(this.userView.getManagePrivCode())){
				strsql.append("select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan where plan_id in (select plan_id from zp_plan_details where dept_id = '"+this.userView.getManagePrivCodeValue()+"')  order by  plan_id desc");		
			}else if(this.userView.getManagePrivCode() == null || "".equals(this.userView.getManagePrivCode())){
				strsql.append("select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan where plan_id in (select plan_id from zp_plan_details where dept_id = '')  order by  plan_id desc");
			}
		}
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_plan",1);
	          vo.setString("plan_id",this.getFrowset().getString("plan_id"));
	          vo.setString("name",this.getFrowset().getString("name"));
	          vo.setString("org_id",this.getFrowset().getString("org_id"));
	          vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("start_date"))));
	          vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("end_date"))));
	          vo.setString("budget_fee",this.getFrowset().getString("budget_fee"));
	          vo.setString("budget_fee",this.getFrowset().getString("budget_fee"));
	          vo.setString("real_fee",this.getFrowset().getString("real_fee"));
	          vo.setString("plan_invite_amount",this.getFrowset().getString("plan_invite_amount"));
	          vo.setString("real_invite_amount",this.getFrowset().getString("real_invite_amount"));
	          vo.setString("dept_id",this.getFrowset().getString("dept_id"));
	          vo.setString("staff_id",this.getFrowset().getString("staff_id"));
	          vo.setString("approve_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("approve_date"))));
	          vo.setString("domain",this.getFrowset().getString("domain"));
	          vo.setString("zp_object",this.getFrowset().getString("zp_object"));
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

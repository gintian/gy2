/*
 * Created on 2005-9-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchDeptPosListTrans</p>
 * <p>Description:保存部门岗位列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 16, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchLimitDeptPosListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
		if("su".equals(this.userView.getUserId())){
			strsql.append("select zp_pos_id,dept_id,pos_id,plan_id from zp_position where pos_id in (select codeitemid from organization)");
		}else if("UN".equals(this.userView.getManagePrivCode()) || "UM".equals(this.userView.getManagePrivCode())){
	       strsql.append("select zp_pos_id,dept_id,pos_id,plan_id from zp_position where pos_id like '"+this.userView.getManagePrivCodeValue()+"%'");
		}else if(this.userView.getManagePrivCode() == null || "".equals(this.userView.getManagePrivCode())){
			strsql.append("select zp_pos_id,dept_id,pos_id,plan_id from zp_position where pos_id = ''");	
		}
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_position");
	          vo.setString("zp_pos_id",this.getFrowset().getString("zp_pos_id"));
	          vo.setString("dept_id",this.getFrowset().getString("dept_id"));
	          vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	          vo.setString("plan_id",this.getFrowset().getString("plan_id"));
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
	        this.getFormHM().put("zpDeptPoslist",list); 
	    }

	}

}

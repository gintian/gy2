/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_release_pos;

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
		
		StringBuffer strsql=new StringBuffer();
		strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where dept_id like '" + this.userView.getManagePrivCodeValue() + "%'");

		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_position",1);
	          vo.setString("zp_pos_id",this.getFrowset().getString("zp_pos_id"));
	          vo.setString("amount",this.getFrowset().getString("amount"));
	          vo.setString("dept_id",this.getFrowset().getString("dept_id"));
	          vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	          vo.setString("plan_id",this.getFrowset().getString("plan_id"));
	          vo.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
	          vo.setString("domain",PubFunc.toHtml(this.getFrowset().getString("domain")));
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
	        this.getFormHM().put("zpreleasePoslist",list); 
	    }

	}

}

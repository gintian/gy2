/*
 * Created on 2005-8-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchStaffRequestListTrans</p>
 * <p>Description:查询临时用工申请列表,zp_gather</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchStaffRequestListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	    StringBuffer strsql=new StringBuffer();
	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	    String gather_type=(String)hm.get("gather_type");
	    if(this.userView.isSuper_admin()){
	       strsql.append("select gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status from zp_gather where gather_type=" + gather_type + " order by  gather_id desc");
	    }else{
	    	if("UN".equals(this.userView.getManagePrivCode())){
	    	   strsql.append("select gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status from zp_gather where gather_type=" + gather_type + " and org_id like '"+this.userView.getManagePrivCodeValue()+"%' order by  gather_id desc");
	    	}else if("UM".equals(this.userView.getManagePrivCode())){
	    		strsql.append("select gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status from zp_gather where gather_type=" + gather_type  + " and  dept_id like '"+this.userView.getManagePrivCodeValue()+"%' order by  gather_id desc");
	    	}else if(this.userView.getManagePrivCode() == null || "".equals(this.userView.getManagePrivCode())){
	    		strsql.append("select gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status from zp_gather where gather_type=" + gather_type + " and dept_id = ''  order by  gather_id desc");
	    	}
	    }
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_gather",1);
	          vo.setString("gather_id",this.getFrowset().getString("gather_id"));
	          vo.setString("org_id",this.getFrowset().getString("org_id"));
	          vo.setInt("gather_type",Integer.parseInt(gather_type));
	          vo.setString("dept_id",this.getFrowset().getString("dept_id"));
	          vo.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
	          vo.setString("create_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("create_date"))));
	          vo.setString("staff_id",this.getFrowset().getString("staff_id"));
	          vo.setInt("usedflag",this.getFrowset().getInt("usedflag"));
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
	        this.getFormHM().put("zpgatherlist",list); 
	        this.getFormHM().put("userid",this.userView.getUserId()); 
	        this.getFormHM().put("managepriv",this.userView.getManagePrivCode()); 
	        this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
	    }
  }

}

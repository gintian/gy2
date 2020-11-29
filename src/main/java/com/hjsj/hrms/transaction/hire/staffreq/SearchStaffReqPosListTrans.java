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

/**
 * <p>Title:SearchStaffReqPosListTrans</p>
 * <p>Description:查询临时用工申请岗位列表，zp_gather_pos</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchStaffReqPosListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from zp_gather_pos");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_gather_pos");
	          vo.setString("gather_id",this.getFrowset().getString("gather_id"));
	          vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	          vo.setString("amount",this.getFrowset().getString("amount"));
	          vo.setString("type",this.getFrowset().getString("type")); 
	          if(this.getFrowset().getString("reason").length() > 20){
	             vo.setString("reason",PubFunc.nullToStr(this.getFrowset().getString("reason").substring(0,20)+"..."));
	          }else{
	          	 vo.setString("reason",PubFunc.nullToStr(this.getFrowset().getString("reason")));
	          }
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
	        this.getFormHM().put("gatherPoslist",list);
	    }
        

	}

}

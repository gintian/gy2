/*
 * Created on 2005-8-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;


/**
 * <p>Title:SearchStaffReqPosTrans</p>
 * <p>Description:查询临时用工申请岗位，zp_gather_pos</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchStaffReqPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String gather_id=(String)hm.get("a_id");
        String gather_id_value = (String)this.getFormHM().get("gather_id_value");
        String pos_id = (String)hm.get("pos_id");
        RecordVo vo=new RecordVo("zp_gather_pos");
        String flag_pos=(String)this.getFormHM().get("flag_pos");
        /**
         * 按新增按钮时，则不进行查询，直接退出
         */
        if("1".equals(flag_pos))
        {
        	this.getFormHM().put("gatherPosvo",vo);
        	this.getFormHM().put("gather_id_value",gather_id_value);
        	String sqle = "select * from zp_gather where gather_id = '"+gather_id_value+"'";
        	try{
        	   this.frowset=dao.search(sqle);
        	   while(this.frowset.next()){
        	   	  this.getFormHM().put("posparentcode",this.getFrowset().getString("dept_id"));
        	   }
        	}catch(SQLException e){
        		e.printStackTrace();
  		        throw GeneralExceptionHandler.Handle(e); 
        	}
        }
        else
        {
	        
	        String pos_id_value = "";
	        try
	        {
	            vo.setString("gather_id",PubFunc.nullToStr(gather_id));
	            vo.setString("pos_id",PubFunc.nullToStr(pos_id));
	            String sql = "select * from zp_gather_pos where gather_id = '"+gather_id+"' and pos_id = '"+pos_id+"'";
	            this.frowset=dao.search(sql);
	            if(this.frowset.next())
	            {
	            	vo.setString("amount",String.valueOf(this.frowset.getInt("amount")));
	            	vo.setString("type",this.frowset.getString("type"));
	            	vo.setString("reason",this.frowset.getString("reason"));
	            }
	            pos_id_value = AdminCode.getCodeName("@K",pos_id);
	        }
	        catch(SQLException sqle)
	        {
	  	      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);            
	        }
	        finally
	        {
	        	this.getFormHM().put("gather_id_value",gather_id);
	        	this.getFormHM().put("gatherPosvo",vo);
	        	this.getFormHM().put("pos_id_value",pos_id_value);
	        }
	}
  }
}

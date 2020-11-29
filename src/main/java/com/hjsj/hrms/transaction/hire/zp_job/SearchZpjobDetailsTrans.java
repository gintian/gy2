/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_job;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchZpjobDetailsTrans</p>
 * <p>Description:查询招聘活动明细,zp_job_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpjobDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String zp_job_id =(String)hm.get("a_id");
        String detail_id = (String)hm.get("detail_id");
        String zp_job_id_value = (String)this.getFormHM().get("zp_job_id_value");
        RecordVo vo=new RecordVo("zp_job_details");
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        /**
         * 按新增按钮时，则不进行查询，直接退出
         */
        if("1".equals(flag_detail)){
        	this.getFormHM().put("zpjobDetailsvo",vo);
        	this.getFormHM().put("zp_job_id_value",zp_job_id_value);
        }else{
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            vo.setString("detail_id",detail_id);
            vo.setString("zp_job_id",zp_job_id);
            String sql = "select detail_id,zp_job_id,detailname,charge,realcharge from zp_job_details where detail_id = "+detail_id;
            ArrayList list = new ArrayList();
            this.frowset=dao.search(sql,list);
            while(this.frowset.next()){
            	vo.setString("detailname",this.frowset.getString("detailname"));
            	vo.setString("charge",this.frowset.getString("charge"));
            	vo.setString("realcharge",String.valueOf(this.frowset.getString("realcharge")));
                cat.debug(this.frowset.getString("charge"));
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
        	this.getFormHM().put("zp_job_id_value",zp_job_id);
            this.getFormHM().put("zpjobDetailsvo",vo);
        }

	}

	}

}

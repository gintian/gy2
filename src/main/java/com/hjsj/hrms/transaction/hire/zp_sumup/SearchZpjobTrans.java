/*
 * Created on 2005-9-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_sumup;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchZpjobTrans</p>
 * <p>Description:查询招聘活动</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 18, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		RecordVo vo=(RecordVo)this.getFormHM().get("zpSumupvo");
		RecordVo rv=new RecordVo("zp_job_details");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        
		if(!"#".equals(vo.getString("plan_id")) && vo.getString("plan_id") != null && !"".equals(vo.getString("plan_id"))){
		   this.getFormHM().put("strSql","select zp_job_id,name from zp_job where plan_id='"+vo.getString("plan_id")+"' and 1 = ?");
		}

	}

}

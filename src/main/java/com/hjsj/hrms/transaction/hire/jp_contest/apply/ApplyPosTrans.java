package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:ApplyPosTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class ApplyPosTrans extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String z0700 = (String)this.getFormHM().get("z0700");
			String posid = (String)this.getFormHM().get("posid");
			RecordVo vo = new RecordVo("zp_apply_jobs");
			vo.setString("id",posid);			
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		    a_vo.setString("state","02");
		    dao.updateValueObject(a_vo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

}
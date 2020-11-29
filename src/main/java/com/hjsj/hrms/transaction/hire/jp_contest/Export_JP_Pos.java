package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hjsj.hrms.businessobject.hire.JingPingPosBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:Export_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Export_JP_Pos extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			String state=(String)this.getFormHM().get("state");
			JingPingPosBo jpb = new JingPingPosBo(this.getFrameconn());
			String tablename=(String)this.getFormHM().get("tablename");
			String where = "";
			if(!(state==null || "".equals(state)))
			{
				if(!"00".equals(state))
					where = " where Z0713='"+state+"' ";
			}
			String outputname=jpb.exportJPExcel(tablename,where);
			outputname=SafeCode.encode(PubFunc.encrypt(outputname));
			this.getFormHM().put("outputName",outputname);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
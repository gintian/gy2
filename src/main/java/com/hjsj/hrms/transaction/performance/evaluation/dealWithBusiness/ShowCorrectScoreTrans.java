package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:修正分值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 24, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowCorrectScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String planid=(String)hm.get("planid");
			CheckPrivSafeBo bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = bo.isHavePriv(this.userView, planid);
			if(!_flag){
				return;
			}
			String objectid=PubFunc.decrypt((String)hm.get("objectid"));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String correctScore="0";
			String correctCause="";
			
			this.frowset=dao.search("select * from per_result_correct where plan_id="+planid+" and object_id='"+objectid+"'");
			if(this.frowset.next())
			{
				correctScore=this.frowset.getString("score");
				correctCause=Sql_switcher.readMemo(this.frowset,"correct_reason");
			}
			
			this.getFormHM().put("correctScore", correctScore);
			this.getFormHM().put("correctCause", correctCause);
			this.getFormHM().put("object_id",objectid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

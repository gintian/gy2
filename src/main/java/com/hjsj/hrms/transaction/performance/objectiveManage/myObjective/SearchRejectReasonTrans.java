package com.hjsj.hrms.transaction.performance.objectiveManage.myObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchRejectReasonTrans.java</p>
 * <p>Description>:SearchRejectReasonTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 13, 2009 2:46:07 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchRejectReasonTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("opt");
			String rejectreason="";
			MyObjectiveBo bo = new MyObjectiveBo(this.getFrameconn());
			String plan_id=PubFunc.decryption((String)map.get("plan_id"));
			if("1".equals(opt))
			{
				String type=(String)map.get("type");
				String object_id=PubFunc.decryption((String)map.get("object_id"));
			    rejectreason=bo.getRejectReason(object_id, plan_id,type);
			}else if("100".equals(opt))
			{
				String object_id=PubFunc.decryption((String)map.get("object_id"));
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    this.frowset=dao.search("select reasons from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'");
			    while(this.frowset.next())
			    {
			    	rejectreason=Sql_switcher.readMemo(frowset, "reasons");
			    }
			}
			else
			{
				rejectreason=bo.getDescript(plan_id);
			}
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("rejectreason",rejectreason);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
//9028000406
}

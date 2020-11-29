package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDeductMarkReasonsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)map.get("planid");
			String object_id=(String)map.get("objectid");
			String mainbody_id=(String)map.get("mainbodyid");
			this.getDeductMarkReasonsList(plan_id, object_id, mainbody_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void getDeductMarkReasonsList(String plan_id,String object_id,String mainbody_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select pointname,reasons from per_table_"+plan_id+" a ,per_point b where ");
			buf.append("UPPER(a.point_id)=UPPER(b.point_id) and UPPER(a.object_id)='"+object_id.toUpperCase()+"'");
			buf.append(" and UPPER(a.mainbody_id)='"+mainbody_id+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("pointname",this.frowset.getString("pointname"));
				bean.set("reason",Sql_switcher.readMemo(this.frowset,"reasons"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("deductMarkReasonsList", list);
	}

}

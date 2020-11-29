package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchDefinePageTrans extends IBusiness{
	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String ins_id=(String)this.getFormHM().get("ins_id");
		String taskid=(String)this.getFormHM().get("taskid");
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		StringBuffer sql=new StringBuffer();
		sql.append("select * from t_wf_task where task_id='"+taskid+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String urlPage="";
		String  url="";
		String params="";
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				url=this.frowset.getString("url_addr");
				params=this.frowset.getString("params");
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		if(url==null||url.length()<=0||params==null||params.length()<=0)
			throw new GeneralException(ResourceFactory.getProperty("固定表单路径错误！"));
		urlPage=url+"?"+params;
		this.getFormHM().put("urlPage", urlPage);
	}
}

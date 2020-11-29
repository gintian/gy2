package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveDragTemplateTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			String fromid=(String)this.getFormHM().get("fromid");
			String toid=(String)this.getFormHM().get("toid");
			String table=(String)this.getFormHM().get("table");
			StringBuffer sql = new StringBuffer("");
			if("root".equalsIgnoreCase(toid))
			{
				/**模板拖拽到模板分类下*/
				if("1".equalsIgnoreCase(fromid.substring(fromid.indexOf("#")+1)))
					throw new GeneralException("请移至具体模板分类下！");		
				else
					sql.append("update per_template_set set parent_id=null where template_setid="+fromid.substring(0,fromid.indexOf("#")));
			}
			else
			{
				/**模板拖拽到模板分类下*/
				if("1".equalsIgnoreCase(fromid.substring(fromid.indexOf("#")+1)))
					sql.append("update per_template set template_setid="+toid.substring(0,toid.indexOf("#"))+" where UPPER(template_id)='"+fromid.substring(0,fromid.indexOf("#")).toUpperCase()+"'");
				else
					sql.append("update per_template_set set parent_id="+toid.substring(0,toid.indexOf("#"))+" where template_setid="+fromid.substring(0,fromid.indexOf("#")));				
			}
			if(sql.toString().length()>0)
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.update(sql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

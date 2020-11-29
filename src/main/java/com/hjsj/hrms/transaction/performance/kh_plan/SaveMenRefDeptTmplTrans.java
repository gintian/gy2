package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SaveMenRefDeptTmplTrans.java</p>
 * <p>Description>:保存考核计划定义的部门考核模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 31, 2010 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SaveMenRefDeptTmplTrans extends IBusiness{
	
	public void execute() throws GeneralException
	{	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String opt = (String) hm.get("opt");
		String planId = (String) hm.get("plan_id");
		String newTemplate_id = (String) hm.get("newTemplate_id");
		hm.remove("newTemplate_id");
		
		String template_id="";
		if("1".equals(opt))
		{			
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			try
			{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select name from per_template where template_id='" + newTemplate_id + "'");
				this.frowset = dao.search(strsql.toString());
				if(this.frowset.next())
				{
					this.getFormHM().put("template_Name", this.frowset.getString("name"));
				}
			}catch (Exception e)
			{
				e.printStackTrace();
			}
						
			this.getFormHM().put("newTemplate_id", newTemplate_id);
		}else{
			
			String sectorTemplate=(String)this.getFormHM().get("sectorTemplate");
			if("0".equals(sectorTemplate))
			{
				template_id=(String)this.getFormHM().get("oldTemplate_id");			
				this.getFormHM().put("menRefDeptTmpl", template_id);
			}else{
			
				template_id=(String)this.getFormHM().get("newTemplate_id");
				if(template_id==null || template_id.length()<=0)
				{
					template_id=(String)this.getFormHM().get("menRefDeptTmpl");
					this.getFormHM().put("menRefDeptTmpl", template_id);
				}else{
					this.getFormHM().put("menRefDeptTmpl", template_id);
				}
			}
			this.getFormHM().put("newTemplate_id", "");
		}				
	}
}
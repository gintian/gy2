package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchMenRefDeptTmplTrans.java</p>
 * <p>Description>:考核计划定义部门考核模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 31, 2010 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchMenRefDeptTmplTrans extends IBusiness{

	public void execute() throws GeneralException
	{	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("plan_id");
		String status = (String) hm.get("status");
		String templateId = (String) hm.get("templateId");
//		hm.remove("planId");
		
		LoadXml loadxml = new LoadXml(this.getFrameconn(), planId);
		Hashtable params = loadxml.getDegreeWhole();		
		this.getFormHM().put("menRefDeptTmpl", params.get("MenRefDeptTmpl"));		
		
		String menRefDeptTmpl=(String)this.getFormHM().get("menRefDeptTmpl");
		
		if(menRefDeptTmpl==null || menRefDeptTmpl.length()<=0)
		{
			this.getFormHM().put("sectorTemplate", "0");
			this.getFormHM().put("template_Name", "");
		}else{
			if(menRefDeptTmpl.equalsIgnoreCase(templateId))
			{
				this.getFormHM().put("sectorTemplate", "0");
				this.getFormHM().put("template_Name", "");
			}else{
				this.getFormHM().put("sectorTemplate", "1");
				
				ContentDAO dao = new ContentDAO(this.getFrameconn());		
				try
				{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select name from per_template where template_id='" + menRefDeptTmpl + "'");
					this.frowset = dao.search(strsql.toString());
					if(this.frowset.next())
					{
						String name=this.frowset.getString("name");
						String template_Name=("["+menRefDeptTmpl+"]"+name);
						this.getFormHM().put("template_Name", template_Name);
					}
				}catch (Exception e)
				{
					e.printStackTrace();
				}			
			}
		}
		this.getFormHM().put("status", status);
	    this.getFormHM().put("template_id", menRefDeptTmpl);	
	    this.getFormHM().put("oldTemplate_id", templateId);
	}
}

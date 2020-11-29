package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ExamineInfoConfigTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String setid=(String)hm.get("setid");
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			HashMap infoMap=bo.getExamineInfoConfig(setid);
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			HashMap configMap=null;
			if(map!=null)
				configMap=(HashMap)map.get("infoMap");
			String title="";
			String content="";
			String commentuser="";
			String level="";
			String commentdate="";
			if(configMap!=null)
			{
				if(configMap.get("title")!=null)
					title=(String)configMap.get("title");
				if(configMap.get("content")!=null)
					content=(String)configMap.get("content");
				if(configMap.get("level")!=null)
					level=(String)configMap.get("level");
				if(configMap.get("comment_user")!=null)
					commentuser=(String)configMap.get("comment_user");
				if(configMap.get("comment_date")!=null)
					commentdate=(String)configMap.get("comment_date");
				
			}
			this.getFormHM().put("titleField", title);
			this.getFormHM().put("contentField", content);
			this.getFormHM().put("levelField", level);
			this.getFormHM().put("commentDateField", commentdate);
			this.getFormHM().put("commentUserField", commentuser);
			this.getFormHM().put("titleFieldList",(ArrayList)infoMap.get("charList"));
			this.getFormHM().put("contentFieldList",(ArrayList)infoMap.get("cmlist"));
			this.getFormHM().put("commentUserFieldList",(ArrayList)infoMap.get("charList"));
			this.getFormHM().put("levelFieldList",(ArrayList)infoMap.get("codeList"));
			this.getFormHM().put("commentDateFieldList",(ArrayList)infoMap.get("dateList"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

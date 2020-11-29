package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String name="";
			String content="";
			String title="";
			String templateId="";
			String zploop="";
			if(this.getFormHM().get("mailTempID")!=null){
				templateId=(String)this.getFormHM().get("mailTempID");
			}
			if(this.getFormHM().get("zploop")!=null)
			{
				zploop=(String)this.getFormHM().get("zploop");
			}
			String sql="select name,title,content from t_sys_msgtemplate where template_id="+templateId;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				name=this.frowset.getString("name");
				content=this.frowset.getString("content");
				title=this.frowset.getString("title");
			}
			ArrayList zbj_list = this.getZbjList(zploop);
			ArrayList zb_list=this.getZbList(zploop);
			ArrayList temList = this.getTemplateList(zploop);
			this.getFormHM().put("mailTempList",temList);
			this.getFormHM().put("mailTempID",templateId);
			this.getFormHM().put("zbj_list",zbj_list);
			this.getFormHM().put("zb_list",zb_list);
			this.getFormHM().put("content",content);
			this.getFormHM().put("title",title);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ArrayList getZbjList(String zploop){
		ArrayList list = new ArrayList();
		if("2".equals(zploop))
	    	list.add(new CommonData("1","面试安排信息"));
		else if("4".equals(zploop))
			list.add(new CommonData("4","录用信息表"));	
		else if("3".equals(zploop))
		{
			list.add(new CommonData("3","面试环节信息表"));	
		}
		return list;
	}
	public ArrayList getTemplateList(String zploop){
		ArrayList list = new ArrayList();
		list.add(new CommonData("","  "));
		try{
			String sql = "select template_id,name from t_sys_msgtemplate where zploop='"+zploop+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				list.add(new CommonData(this.frowset.getString("template_id"),this.frowset.getString("name")));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getZbList(String zploop){
		ArrayList list = new ArrayList();
		try{ 
			if("2".equals(zploop))
			{
	    		list.add(new CommonData(" ","       "));
	    		list.add(new CommonData("(~姓名~)","(~姓名~)"));
	    		list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
	    		list.add(new CommonData("(~面试时间~)","(~面试时间~)"));
	     		list.add(new CommonData("(~面试地点~)","(~面试地点~)"));
	    		list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
	    		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
    			HashMap parameterMap=parameterXMLBo.getAttributeValues();
    			if(parameterMap!=null&&parameterMap.get("interviewing_itemid")!=null)
     			{
    				list.add(new CommonData("(~短信回复提示~)","(~短信回复提示~)"));
     			}
			}else if("4".equalsIgnoreCase(zploop)){
				list.add(new CommonData(""," "));
				list.add(new CommonData("(~姓名~)","(~姓名~)"));
				list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
				list.add(new CommonData("(~报道时间~)","(~报道时间~)"));
				list.add(new CommonData("(~系统时间~)","(~系统时间~)"));		
			}
			else if("3".equalsIgnoreCase(zploop)){
				list.add(new CommonData(""," "));
				list.add(new CommonData("(~应聘人员姓名~)","(~应聘人员姓名~)"));
				list.add(new CommonData("(~面试考官姓名~)","(~面试考官姓名~)"));
				list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
				list.add(new CommonData("(~面试时间~)","(~面试时间~)"));
				list.add(new CommonData("(~面试地点~)","(~面试地点~)"));
				list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
				list.add(new CommonData("(~面试人数~)","(~面试人数~)"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

}

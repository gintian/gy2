package com.hjsj.hrms.transaction.sys.options.template;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeZploopTrans extends IBusiness {

	public void execute() throws GeneralException {
        String zploop="1";
        String template_type = "";
        if(this.getFormHM().get("zpLoopNew")!=null)
        	zploop=(String)this.getFormHM().get("zpLoopNew");
        if(this.getFormHM().get("template_type")!=null)
        	template_type = (String)this.getFormHM().get("template_type");
        String content=getContent(zploop);
        ArrayList zbj_list = getZbjList(zploop);
        ArrayList zb_list =getZbList(zploop);
		ArrayList zploopList = getZploopList();
		this.getFormHM().put("zpLoop_list",zploopList);
		this.getFormHM().put("content",content);
		this.getFormHM().put("name","");
		this.getFormHM().put("title","");
		this.getFormHM().put("address","");
		this.getFormHM().put("zpLoopNew",zploop);
		this.getFormHM().put("template_type",template_type);
		this.getFormHM().put("zbj_list",zbj_list);
		this.getFormHM().put("zb_list",zb_list);
	}
	public String getContent(String zploop){
		String content="";
		if("1".equals(zploop)){
			content="人员姓名(A0100) :\r\n    您好！\r\n   很高兴您申请我们  应聘职位(z0311)  的职位，如果有需要我们会尽快和您联系\r\n\r\n                                                                         (~系统时间~)";
		}else if("2".equals(zploop)){
			content="(~姓名~) :\r\n    您好！\r\n   很高兴您的简历符合我们  (~应聘职位~)  职位的要求，如果有时间的话，请于 (~面试时间~) 来 (~面试地点~) 面试 \r\n\r\n                                                                       (~系统时间~)";
		}else if("4".equalsIgnoreCase(zploop))
		{
			content="(~姓名~) :\r\n    您好！\r\n   您已通过我公司  (~应聘职位~)  职位的面试并被录用，请您于 (~报道时间~) 来我公司报道 \r\n\r\n                                                                    (~系统时间~)";
		}
		else
		{
	     	content="";
		}
		return content;
		
	}
	public ArrayList getZbjList(String zploop){
		ArrayList list = new ArrayList();
		if("1".equals(zploop)){
			list.add(new CommonData("","初次筛选信息表"));
		}else if("2".equals(zploop)){
			list.add(new CommonData("1","面试安排信息"));
		}else if("4".equals(zploop)){
			list.add(new CommonData("4","录用信息表"));			
		}else if("3".equals(zploop))
		{
			list.add(new CommonData("3","面试环节信息表"));	
		}
		return list;
	}
	public ArrayList getZbList(String zploop){
		ArrayList list = new ArrayList();
		try
		{
		if("1".equals(zploop)){
			list.add(new CommonData(""," "));
			list.add(new CommonData("人员姓名(A0100)","人员姓名(A0100)"));
			list.add(new CommonData("应聘职位(z0311)","应聘职位(z0311)"));
			list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
		}else if("2".equals(zploop)){
			list.add(new CommonData(""," "));
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
		}else if("3".equalsIgnoreCase(zploop)){
			list.add(new CommonData(""," "));
			list.add(new CommonData("(~应聘人员姓名~)","(~应聘人员姓名~)"));
			list.add(new CommonData("(~面试考官姓名~)","(~面试考官姓名~)"));
			list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
			list.add(new CommonData("(~面试时间~)","(~面试时间~)"));
			list.add(new CommonData("(~面试地点~)","(~面试地点~)"));
			list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
			list.add(new CommonData("(~面试人数~)","(~面试人数~)"));
		}
		else{
			
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getZploopList(){
		ArrayList list = new ArrayList();
		String sql_str = "select codeitemid,codeitemdesc from codeitem where codesetid ='36' and( codeitemid ='1' or codeitemid ='2' or codeitemid='3' or codeitemid='4') order by codeitemid";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try{
		this.frowset = dao.search(sql_str);
		    while(this.frowset.next()){
		        CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				list.add(vo);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

}

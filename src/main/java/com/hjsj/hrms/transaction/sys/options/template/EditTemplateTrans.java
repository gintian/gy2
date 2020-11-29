package com.hjsj.hrms.transaction.sys.options.template;

import com.hjsj.hrms.actionform.sys.options.template.TemplateSetForm;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
public class EditTemplateTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		try{
		   String id = (String)this.getFormHM().get("id");
		   int template_id = Integer.parseInt((String)hm.get("template_id"));
		   String title ="";
		   String address ="";
		   String name ="";
		   String zpLoop = "";
		   String content = "";
		   int template_type =0;
		   ArrayList zb_list = new ArrayList();//指标列表
		   ArrayList zbj_list = new ArrayList();//指标集列表
           ContentDAO dao =new ContentDAO(this.getFrameconn());
           String str_sql = "select * from t_sys_msgtemplate where template_id = "+template_id;
       
           this.frowset = dao.search(str_sql);
           while(this.frowset.next()){
        		title = this.frowset.getString("title");
        		address = this.frowset.getString("adress");
        		name = this.frowset.getString("name");
        		zpLoop = this.frowset.getString("zploop");
        		template_type = this.frowset.getInt("template_type");
        		content = this.frowset.getString("content");
        		
        	}
           if("1".equals(zpLoop)){
               zbj_list.add(new CommonData("","初次筛选信息表"));
			   zb_list.add(new CommonData(""," "));
			   zb_list.add(new CommonData("人员姓名(A0100)","人员姓名(A0100)"));
			   zb_list.add(new CommonData("应聘职位(z0311)","应聘职位(z0311)"));
			   zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
           }else if("2".equals(zpLoop)){
        	   zbj_list.add(new CommonData("1","面试安排信息"));
        	   zb_list.add(new CommonData(""," "));
        	   zb_list.add(new CommonData("(~姓名~)","(~姓名~)"));
        	   zb_list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
        	   zb_list.add(new CommonData("(~面试时间~)","(~面试时间~)"));
        	   zb_list.add(new CommonData("(~面试地点~)","(~面试地点~)"));
        	   zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
        	   ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
   		    	HashMap parameterMap=parameterXMLBo.getAttributeValues();
   		    	if(parameterMap!=null&&parameterMap.get("interviewing_itemid")!=null)
    			{
   		    		zb_list.add(new CommonData("(~短信回复提示~)","(~短信回复提示~)"));
    			}
           }else{
        	   if("3".equals(zpLoop))
        	   {
        		   zbj_list.add(new CommonData("3","面试信息"));
        		   
        		   zb_list.add(new CommonData("(~应聘人员姓名~)","(~应聘人员姓名~)"));
        		   zb_list.add(new CommonData("(~面试考官姓名~)","(~面试考官姓名~)"));
        		   zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
        		   zb_list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
        		   zb_list.add(new CommonData("(~面试地点~)","(~面试地点~)"));
        		   zb_list.add(new CommonData("(~面试时间~)","(~面试时间~)"));
        		   zb_list.add(new CommonData("(~面试人数~)","(~面试人数~)"));
        	   
        	   }
        	   if("4".equals(zpLoop))
        	   {
        		   zbj_list.add(new CommonData("4","录用信息"));
        		   zb_list.add(new CommonData("(~应聘职位~)","(~应聘职位~)"));
        		   zb_list.add(new CommonData("(~报道时间~)","(~报道时间~)"));
        		   zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
        		   zb_list.add(new CommonData("(~姓名~)","(~姓名~)"));
        	   }
        	   
           }
        	this.getFormHM().put("title",title);
        	this.getFormHM().put("address",address);
        	this.getFormHM().put("name",name);
        	this.getFormHM().put("zpLoopNew",zpLoop);
        	this.getFormHM().put("type",template_type+"");
        	this.getFormHM().put("content",content);
        	this.getFormHM().put("template_id",template_id+"");
        	this.getFormHM().put("id",id);
        	this.getFormHM().put("zbj_list",zbj_list);
			
			this.getFormHM().put("zb_list",zb_list);
        	TemplateSetForm setForm = new TemplateSetForm();
        	setForm.setId(id);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        
          hm.remove("b_edit");
        }
    
	}

}

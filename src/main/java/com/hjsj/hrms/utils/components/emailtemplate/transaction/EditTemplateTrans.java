package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.actionform.sys.options.template.TemplateSetForm;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:EditTemplateTrans</p>
 * <p>Description:编辑模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:04:56 PM</p>
 * @author sunming
 * @version 1.0
 */
public class EditTemplateTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		try{
		   String id = (String)this.getFormHM().get("id");
		   String opt = (String)this.getFormHM().get("opt");
		   int template_id = Integer.parseInt((String) this.getFormHM().get("template_id"));
		   String returnAddress ="";
		   String name ="";
		   String subject = "";
		   String content = "";
		   int subModule = 0;
		   String other_flag = "";
		   String ownflag="";
           ContentDAO dao =new ContentDAO(this.getFrameconn());
           String str_sql = "select * from email_name where id = "+template_id;
       
           this.frowset = dao.search(str_sql);
           while(this.frowset.next()){
        		returnAddress = this.frowset.getString("return_address");
        		name = this.frowset.getString("name");
        		subject = this.frowset.getString("subject");
        		//兼容Oracle下直接getInt()出错
        		subModule = Integer.parseInt(this.frowset.getString("sub_module"));
        		other_flag = this.frowset.getString("other_flag");
        		content = this.frowset.getString("content");
        		//content = content.replace("&amp;nbsp;","&nbsp;");
        	//	content = content.replace("&lt;","<");
        		//content = content.replace("&gt;",">");
        		//在ie中浏览器会自动添加\r\n，若替换掉 ie中会自动再换一行，所以注掉下行代码   chenxg  2016-05-27
//        		content = content.replaceAll("\r\n", "<br>");
        		if(content!=null)
        			content = content.replaceAll("<li>\\?", "<li>");
        		ownflag = this.frowset.getString("ownflag");
        	}
           TemplateBo bo = new TemplateBo(this.frameconn, new ContentDAO(
   				this.frameconn), this.getUserView());
            int fieldid = bo.getTemplateFieldId(template_id);
            //所有上级模板
            ArrayList parentTemplates = bo.getParentTemplates(opt);
            boolean isParent = false;
            if(parentTemplates.contains(String.valueOf(template_id)))
            	isParent = true;
            
            
            this.getFormHM().put("isParent",isParent);
        	this.getFormHM().put("subModule",subModule);
        	this.getFormHM().put("other_flag",other_flag);
        	this.getFormHM().put("returnAddress",returnAddress);
        	this.getFormHM().put("name",name);
        	this.getFormHM().put("subject",subject);
        	this.getFormHM().put("content",content);
        	this.getFormHM().put("template_id",template_id);
        	this.getFormHM().put("fieldid",fieldid);
        	this.getFormHM().put("ownflag", ownflag);
        	TemplateSetForm setForm = new TemplateSetForm();
        	setForm.setId(id);
        	 
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
	}

}

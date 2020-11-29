package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:InitEmailTemplateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-17 8:16:41</p>
 * @author LiZhenWei
 * @version 4.0
 */

public class InitEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("opt");
			//String id=(String)map.get("templateId");
			//邮件模板设置有问题，新增一个模板后，保存后，定位在第一个上面，并且下面的内容为空，不对。   jingq upd 2014.10.31
			String id = (String)this.getFormHM().get("templateId");
			String nmodule="2";
			if(map.get("nmodule")!=null)
			{
				nmodule=(String)map.get("nmodule");
			}
			ArrayList fieldList = new ArrayList();
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String fieldid="";
			String fieldname="";
			//----------
			if("first".equalsIgnoreCase(id))// first time
			{
				id=String.valueOf(bo.getMinTemplateId(nmodule));
				if("0".equalsIgnoreCase(id))//no template
				{
					fieldid="1";
				}else
				{
			    	fieldid=String.valueOf(bo.getTemplateFieldId(id));
				}
			}
			else if("new".equalsIgnoreCase(id))//new create
			{
				fieldid="1";
			}
			else
			{
				fieldid=String.valueOf(bo.getTemplateFieldId(id));
			}
		/*	if(!(id==null||id.equalsIgnoreCase("new")))
			{
			      fieldid=String.valueOf(bo.getTemplateFieldId(id));
			}
			else
			{
				fieldid="1";
			}
			if(id.equalsIgnoreCase("new")&&opt.equalsIgnoreCase("edit"))
			{
				id=String.valueOf(bo.getMinTemplateId());
			}*/
					
			String fieldsetid="";
			String itemid="";
			String message = "0";
			ArrayList fieldsetlist= bo.getPersonSubset(2,this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
			if("add".equalsIgnoreCase(opt))
			{
				fieldsetid= bo.getFirstFieldSetid(this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
				this.getFormHM().put("nflag","2");
				this.getFormHM().put("content","");
				this.getFormHM().put("subject","");
				this.getFormHM().put("name","");
				this.getFormHM().put("address","");
				this.getFormHM().put("id","");
				this.getFormHM().put("templateId","0");
				this.getFormHM().put("fieldid",fieldid);

			}
			else if("edit".equalsIgnoreCase(opt))
			{
				//--------------
				if("first".equalsIgnoreCase(id))
				{
					id=String.valueOf(bo.getMinTemplateId(nmodule));
				}
				else
				{
				}
				if(!"0".equalsIgnoreCase(id))
				{
					
				//----------
			    	fieldList=bo.getTemplateFieldList(Integer.parseInt(id));
			    	String itemName=bo.getEmailFieldAndName(id);
				//if(itemName!=null&&!itemName.equals("")&&itemName.indexOf("#")!=-1&&(itemName.split("#")).length>2)
				//{
     		    		itemid=itemName.split("#")[0];
    		    		fieldname=itemName.split("#")[1];
				//}
			    	fieldsetid=bo.getEmailFieldSetId2(itemid);
			    	if(fieldsetid==null||fieldsetid.length()<=0)
			    		message="1";
			    	HashMap hashmap =bo.getTemplateInfoById(id);
			    	this.getFormHM().put("nflag","1");
			    	this.getFormHM().put("content",SafeCode.encode((String)hashmap.get("content")));
			    	this.getFormHM().put("subject",(String)hashmap.get("subject"));
			    	this.getFormHM().put("name",SafeCode.encode((String)hashmap.get("name")));
			    	this.getFormHM().put("address",(String)hashmap.get("address"));
			    	this.getFormHM().put("id",id);
			      	this.getFormHM().put("templateId",id);
			    	this.getFormHM().put("fieldid",fieldid);
				}
				else
				{
					fieldsetid= bo.getFirstFieldSetid(this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
					this.getFormHM().put("nflag","2");
					this.getFormHM().put("content","");
					this.getFormHM().put("subject","");
					this.getFormHM().put("name","");
					this.getFormHM().put("address","");
					this.getFormHM().put("id","");
					this.getFormHM().put("templateId","");
					this.getFormHM().put("fieldid",fieldid);
				}
				
			}
			ArrayList itemlist = bo.getFieldItemList(2,fieldsetid,this.userView);
			this.getFormHM().put("fieldsetlist",fieldsetlist);
			this.getFormHM().put("fieldsetid",fieldsetid);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("itemid",itemid);
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("fieldname",fieldname);
			ArrayList templateList = bo.getEmailTemplateList2(nmodule,1);
			this.getFormHM().put("templateList",templateList);
			this.getFormHM().put("nmodule",nmodule);
			this.getFormHM().put("message", message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		
	}

}

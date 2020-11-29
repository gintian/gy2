package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:EditFormulaListTrans</p>
 * <p>Description:编辑公式</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:04:37 PM</p>
 * @author sunming
 * @version 1.0
 */
public class EditFormulaListTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		try{
		   String id = (String)this.getFormHM().get("id");
		   int template_id = Integer.parseInt((String) this.getFormHM().get("templateId"));
		   int fieldid = Integer.parseInt((String) this.getFormHM().get("fieldid"));
		   String fieldtitle = (String) this.getFormHM().get("fieldtitle");
           ContentDAO dao =new ContentDAO(this.getFrameconn());
           String str_sql = "select * from email_field where id = "+template_id+" and fieldid="+fieldid+" and fieldtitle='"+fieldtitle+"'";
           this.frowset = dao.search(str_sql);
           String fieldType = "";
           String fieldContent = "";
           int dateFormat = 0;
           int fieldLen = 0;
           int nDec = 0;
           String codeSet = "";
           String nFlag = "";
           String fieldset = "";
           while(this.frowset.next()){
        	   	fieldType = this.frowset.getString("fieldtype");
        	   	fieldContent = this.frowset.getString("fieldcontent");
        	   	dateFormat = this.frowset.getInt("dateformat");
        	   	fieldLen = this.frowset.getInt("fieldlen");
        	   	nDec = this.frowset.getInt("ndec");
        	   	codeSet = this.frowset.getString("codeset");
        	   	nFlag = this.frowset.getString("nflag");
        	   	fieldset = this.frowset.getString("fieldset");
        	}
        	this.getFormHM().put("fieldType",fieldType);
        	this.getFormHM().put("fieldContent",fieldContent);
        	this.getFormHM().put("dateFormat",dateFormat);
        	this.getFormHM().put("fieldlen",fieldLen);
        	this.getFormHM().put("nDec",nDec);
        	this.getFormHM().put("codeSet",codeSet);
        	this.getFormHM().put("nFlag",nFlag);
        	this.getFormHM().put("fieldset",fieldset);
        	this.getFormHM().put("template_id",template_id);
        	this.getFormHM().put("fieldid",fieldid);
        	this.getFormHM().put("fieldtitle",fieldtitle);
        	 
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
    
	}

}

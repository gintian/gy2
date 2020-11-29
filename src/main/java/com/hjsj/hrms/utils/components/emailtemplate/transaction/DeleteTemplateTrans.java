package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DeleteTemplateTrans</p>
 * <p>Description:删除模板及公式、附件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:04:08 PM</p>
 * @author sunming
 * @version 1.0
 */
public class DeleteTemplateTrans extends IBusiness{
	public void execute() throws GeneralException{
		 ArrayList tempList= (ArrayList) this.getFormHM().get("arr");
		int current = Integer.parseInt( (String) this.getFormHM().get("currentPage"));
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		//String b_delete =(String)hm.get("b_delete");
		StringBuffer str_sql = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
        for(int i=0;i<tempList.size();i++){
			str_sql.append(" or id ="+tempList.get(i));
		}
        
        try{
        	dao.delete("delete from email_name where "+str_sql.substring(3),new ArrayList());
        	dao.delete("delete from email_field where "+str_sql.substring(3),new ArrayList());
        	dao.delete("delete from email_attach where "+str_sql.substring(3),new ArrayList());
        	this.getFormHM().put("currentPage", current);
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
		
			
	//hm.remove("b_delete");		
		
	}
	

}

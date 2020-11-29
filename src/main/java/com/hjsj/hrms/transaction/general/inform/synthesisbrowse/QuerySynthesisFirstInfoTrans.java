package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class QuerySynthesisFirstInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		 ContentDAO dao=new ContentDAO(this.getFrameconn());

		 String a_code="";
		 if(userView.isSuper_admin())
		 {
			 StringBuffer sql=new StringBuffer();

			 sql.append("select codeitemid,codesetid from organization where codeitemid=parentid order by a0000,codeitemid");
			 try{
				 this.frowset=dao.search(sql.toString());
				 if(this.frowset.next())
					 a_code=this.frowset.getString("codesetid") + this.frowset.getString("codeitemid");
			 }catch(Exception e)
			 {
				 e.printStackTrace();
				 throw GeneralExceptionHandler.Handle(e); 
			 }
		 }
	     else
		 {
			a_code=userView.getManagePrivCode() + userView.getManagePrivCodeValue();
			if("UN".equals(a_code)){
				StringBuffer sql=new StringBuffer();
				sql.append("select codeitemid,codesetid from organization where codeitemid=parentid order by a0000,codeitemid");
				 try{
					 this.frowset=dao.search(sql.toString());
					 if(this.frowset.next())
						 a_code=this.frowset.getString("codesetid") + this.frowset.getString("codeitemid");
				 }catch(Exception e)
				 {
					 e.printStackTrace();
					 throw GeneralExceptionHandler.Handle(e); 
				 }
			}
		}
		 this.getFormHM().put("a_code",a_code);
	}
	

}

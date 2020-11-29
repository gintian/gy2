package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:SaveDocumentParamTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 9, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveDocumentParamTrans extends IBusiness {

  
    @Override
	public void execute() throws GeneralException
    {
    	String bz_fieldsetid=(String)this.getFormHM().get("bz_fieldsetid");
    	String bz_codesetid=(String)this.getFormHM().get("bz_codesetid");
    	DocumentParamXML documentparamXML=new DocumentParamXML(this.getFrameconn()); 
    	documentparamXML.setValue(DocumentParamXML.FILESET,"setid",bz_fieldsetid);
    	documentparamXML.setValue(DocumentParamXML.FILESET,"fielditem",bz_codesetid);
    	documentparamXML.saveParameter();
    }

}

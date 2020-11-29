package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveCardLenTrans  extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String id_len=(String)this.getFormHM().get("id_len");
    	if(id_len==null||id_len.length()<=0)
    	{
    		return;
    	}
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	kqCardLength.setId_Factory(Integer.parseInt(id_len));
    }

}

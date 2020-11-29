package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetValidflagTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String pars=(String) hm.get("pars");
		String valid="false";
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		try{
			OtherParam op=new OtherParam(this.getFrameconn());
			if(pars.startsWith("db")){
				valid=pars.substring(2,pars.length());
				op.updateElementAtrr("/param/base_fields","valid="+valid);
				
			}else{
				valid=pars;
				op.updateElementAtrr("/param/employ_type","valid="+valid);
				
				
			}
			if("false".equals(valid)){
				hm.put("itemvalid","false");
			}else{
				hm.put("itemvalid","true");
			}
			op.saveXml(dao);
			}catch(Exception e){
				e.printStackTrace();
			}
	}

}

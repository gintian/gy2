package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.PrintResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

public class PrintResumeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try{
			PrintResumeBo bo = new PrintResumeBo(this.frameconn,this.userView);
			String a0100s = (String)this.getFormHM().get("a0100s");   
			String nbase = (String)this.getFormHM().get("nbase");
			String z0301 = (String)this.getFormHM().get("z0301");
			String filetype = (String)this.getFormHM().get("filetype");
			z0301 = PubFunc.decrypt(z0301);
			bo.setFiletype(filetype);
			String fileName = bo.printResume(a0100s, z0301, nbase, 1);
			this.getFormHM().put("zipname",PubFunc.encrypt(fileName));
			
			String message = "";
			String[] personIds = a0100s.split(",");
			message = bo.getMessage();
			
			if(StringUtils.isNotEmpty(message))
			    this.getFormHM().put("infor", message);
			else
			    this.getFormHM().put("infor", "ok");
			
    	}catch(Exception e){
    		this.getFormHM().put("infor", e.toString());
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}

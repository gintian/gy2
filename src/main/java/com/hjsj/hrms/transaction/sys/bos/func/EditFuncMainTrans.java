package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class EditFuncMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		  HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		 Document doc=null;
		  if(this.getFormHM().get("function_dom")!=null){
				 doc = (Document)this.getFormHM().get("function_dom");
			}else{
				
			}
		String functionid=(String)hm.get("functionid");
	
		FuncMainBo funcbo = new FuncMainBo(this.getFrameconn());
		String functionname = funcbo.getFuncName(functionid,doc);
		this.getFormHM().put("codeitemdesc", functionname);
		this.getFormHM().put("codeitemid", functionid);
		this.getFormHM().put("precodeitemid", functionid);
		

	}


}
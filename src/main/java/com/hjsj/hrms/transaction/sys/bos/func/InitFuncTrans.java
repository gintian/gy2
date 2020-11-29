package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
public class InitFuncTrans extends IBusiness {

	public void execute() throws GeneralException {
		Document doc=null;
		if(this.getFormHM().get("function_dom")!=null){
			 doc = (Document)this.getFormHM().get("function_dom");
		}else{
		FuncMainBo fbo = new FuncMainBo();
		 doc =fbo.getDocument();
		}
		this.getFormHM().put("function_dom", doc);
			
	}
//	public Document getDocument(){
//		if(this.getFormHM().get("function_dom")!=null){
//			System.out.println("function_dom:"+this.getFormHM().get("function_dom"));
//			Document doc = (Document)this.getFormHM().get("function_dom");
//			return doc;
//		}else{
//		FuncMainBo fbo = new FuncMainBo();
//		Document doc =fbo.getDocument();
//		return doc;
//		}
//	}

}

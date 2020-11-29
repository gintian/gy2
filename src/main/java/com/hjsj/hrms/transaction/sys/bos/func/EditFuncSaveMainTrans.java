package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class EditFuncSaveMainTrans extends IBusiness {

	public void execute() throws GeneralException {
	
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");      
		 String function_id="";
		 String parentid="";
			String function_name="";
			try {
				function_name = hm.get("function_name")==null?"":new String(hm.get("function_name").toString().getBytes("iso-8859-1"),"GB2312");
				function_id = hm.get("function_id")==null?"":new String(hm.get("function_id").toString().getBytes("iso-8859-1"),"GB2312");
				parentid = hm.get("parentid")==null?"":new String(hm.get("parentid").toString().getBytes("iso-8859-1"),"GB2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  Document doc = (Document)this.getFormHM().get("function_dom");
		FuncMainBo funcbo = new FuncMainBo(this.getFrameconn());
		funcbo.editFunctionContent(function_id,function_name,parentid,doc);
		//this.getFormHM().put("function_id",function_id);
		//this.getFormHM().put("new_function_name",name);
	}
	public void executeSession(String function_id,String name, String parentid,Document doc){
		FuncMainBo funcbo = new FuncMainBo();
		funcbo.editFunctionContent(function_id,name,parentid,doc);
	}


}
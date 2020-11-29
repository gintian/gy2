package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class FindFuncMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");      
		String function_name="";
		String function_id="";
		String parentid="";
		String prefunc_id="";
		
			function_name = SafeCode.decode((String)hm.get("function_name"));
			 function_id = SafeCode.decode((String)hm.get("function_id"));
				parentid = SafeCode.decode((String)hm.get("parentid"));
				prefunc_id = SafeCode.decode((String)hm.get("prefunc_id"));

	
	   //     boolean flag =funcbo.isExist(function_id);
	       if(hm.get("b_findadd")!=null&& "add".equals(hm.get("b_findadd"))){
 	   this.getFormHM().remove("addfunction_id");
 	   this.getFormHM().remove("addfunction_name");
 	   this.getFormHM().put("addfunction_id",function_id );
 	   this.getFormHM().put("addfunction_name",function_name);
 	
    }  
	       if(hm.get("b_findedit")!=null&& "edit".equals(hm.get("b_findedit"))){
	     	   this.getFormHM().remove("editfunction_id");
	     	   this.getFormHM().remove("editfunction_name");
	     	   this.getFormHM().put("editfunction_id",function_id );
	     	   this.getFormHM().put("editfunction_name",function_name);
	     	
	        }    
	       
	       //flag 为 true 则存在,不能重复,为fales则不存在可以添加
	      Document doc = (Document)this.getFormHM().get("function_dom");
	     // System.out.println("request.Document"+doc);
	        FuncMainBo funcbo = new FuncMainBo();
			 boolean funcflag =funcbo.isExist(function_id,doc);
			 if(hm.get("b_findedit")!=null&& "edit".equals(hm.get("b_findedit"))&&funcflag&&prefunc_id.equals(function_id)){
				 funcflag =false;
			 }
			 if(hm.get("b_findadd")!=null&& "add".equals(hm.get("b_findadd"))&&!funcflag){
				 //执行增加操作
					funcbo.addFunctionContent(function_id,function_name,parentid,doc);
			 }
			 if(hm.get("b_findedit")!=null&& "edit".equals(hm.get("b_findedit"))&&!funcflag){
				 //执行修改操作
				 funcbo.editFunctionContent(function_id,function_name,prefunc_id,doc);
			 }
	        this.getFormHM().put("funcflag", String.valueOf(funcflag));
	        hm.remove("b_findadd");
	        hm.remove("b_findedit");
	}
//	public boolean executeSession(String function_id,Document doc){
//	//	System.out.println("FindFuncMainTrans.getFormHM:"+this.getFormHM().get("function_dom"));
//		FuncMainBo funcbo = new FuncMainBo();
//		return funcbo.isExist(function_id,doc);
//	}


}
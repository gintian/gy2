package com.hjsj.hrms.transaction.general.template.operation;

import com.hjsj.hrms.businessobject.general.template.privy_explain.PrivyExplain;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SohwPrivyExplainTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		PrivyExplain privyExplain=new PrivyExplain(this.getFrameconn());
		HashMap hashMap=privyExplain.getSysConstantXML();
		String constant=(String)hashMap.get("constant");
		if(constant==null||constant.length()<=0)
		{
			constant="";
		}
		this.getFormHM().put("content",constant);
	}

}

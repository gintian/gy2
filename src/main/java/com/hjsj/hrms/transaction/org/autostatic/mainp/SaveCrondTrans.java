package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SaveCrondTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String exper = (String)hm.get("expression");
		
		ArrayList factorlist=(ArrayList)hm.get("factorlist");		
		String codeids = "";
		if(factorlist!=null){
			for(int i=0;i<factorlist.size();i++){
				Factor factor=null;
				factor=(Factor)factorlist.get(i);
				codeids +=factor.getFieldname().toUpperCase()+factor.getOper()+factor.getValue()+"`";
			}
		}
		hm.put("expre",codeids+"|"+exper);
		hm.put("savecrond","1");
	}
}

package com.hjsj.hrms.transaction.standarduty;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveRelevantItem extends IBusiness{

	public void execute() throws GeneralException {
		 String fieldsetid=this.formHM.get("fieldsetid").toString();
		 String targetsetid=this.getFormHM().get("targetsetid").toString();
		 Map relevantset=(HashMap)this.formHM.get("relevantset");
		 String[] saveitems=(String[])this.formHM.get("saveitems"); 
		 HashMap reqMap = (HashMap)this.formHM.get("requestPamaHM");
		 String state = (String)reqMap.get("state");
		 reqMap.remove("state");
		 ArrayList relevantitem=new ArrayList();
		 for(int i = 0; (!"0".equals(state))&&i<saveitems.length; i++){
			 String a=PubFunc.keyWord_reback(saveitems[i]);
			 relevantitem.add(a);
		 }
		 
		 LazyDynaBean ldb= new LazyDynaBean();
		 ldb.set("target", targetsetid);
		 ldb.set("field", relevantitem);
		 relevantset.put(fieldsetid, ldb);
		 this.getFormHM().put("relevantset", relevantset);
		 this.getFormHM().put("submitflag", "ok");
	}

}

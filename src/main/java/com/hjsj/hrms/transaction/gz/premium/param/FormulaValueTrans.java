package com.hjsj.hrms.transaction.gz.premium.param;


import com.hjsj.hrms.businessobject.gz.premium.FormulaPremiumBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class FormulaValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String itemid=(String)hm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		String setid = (String)hm.get("setid");
		setid=setid!=null&&setid.length()>0?setid:"";
		
		String useflag = (String)hm.get("useflag");
		useflag=useflag!=null&&useflag.length()>0?useflag:"";
		
		String runflag = (String)hm.get("runflag");
		runflag=runflag!=null&&runflag.length()>0?runflag:"";
		
		String smode = (String)hm.get("smode");
		smode=smode!=null&&smode.length()>0?smode:"";
		String fmode = (String)hm.get("fmode");
		fmode=fmode!=null&&fmode.length()>0?fmode:"";
		FormulaPremiumBo formulaPremiumBo = new FormulaPremiumBo();
		if(useflag!=null&&useflag.length()>0){
			formulaPremiumBo.alertUseflag(this.frameconn,useflag,setid,itemid);
		}
		
		if(runflag!=null&&runflag.length()>0){
			formulaPremiumBo.alertRunflag(this.frameconn,runflag,setid,itemid);
		}else{
			//runflag = formulaPremiumBo.runFlag(this.frameconn,setid,itemid);
		}
		String formulavalue="";
		if(!"".equals(itemid)){
		 formulavalue = formulaPremiumBo.formulavalue(this.frameconn,setid,itemid);
		}
		formulavalue=SafeCode.encode(formulavalue);
		String expresion="";
		if(!"".equals(itemid)){
		 expresion = formulaPremiumBo.formulacond(this.frameconn,setid,itemid);
		expresion=SafeCode.encode(expresion);
		}
		hm.put("expresion",expresion);
		hm.put("formulavalue",formulavalue);
		hm.put("runflag",runflag);
		hm.put("smode",smode);

		//hm.put("standid",formulaPremiumBo.standId(this.frameconn,setid,itemid));
	}

}

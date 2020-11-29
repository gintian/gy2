package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String cstate = (String)reqhm.get("state");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
		reqhm.remove("state");
		
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String nflag = (String)reqhm.get("nflag");
		nflag=nflag!=null&&nflag.trim().length()>0?nflag:"0";
		reqhm.remove("nflag");
		
		if(cstate.length()>0){
			TempvarBo tempvarbo = new TempvarBo();
			hm.put("sortlist",tempvarbo.sortList(this.frameconn,cstate,type,nflag));
		}
		
		hm.put("cstate",cstate);
		hm.put("type",type);
	}

}

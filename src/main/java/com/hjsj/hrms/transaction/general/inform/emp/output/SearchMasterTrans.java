package com.hjsj.hrms.transaction.general.inform.emp.output;

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
public class SearchMasterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tabid=(String)hm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		hm.remove("tabid");
		
		String a_inforkind=(String)hm.get("a_inforkind");
		a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"";
		hm.remove("a_inforkind");
		
		String dbpre=(String)hm.get("dbpre");
		dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"";
		hm.remove("dbpre");
		
		String flag=(String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"0";
		hm.remove("flag");
		
		String checktype=(String)hm.get("checktype");
		checktype=checktype!=null&&checktype.trim().length()>0?checktype:"open";
		hm.remove("flag");
		
		String returncheck=(String)hm.get("returncheck");
		returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"0";
		hm.remove("returncheck");
		
		this.getFormHM().put("returncheck",returncheck);
		
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("a_inforkind",a_inforkind);
		this.getFormHM().put("dbpre",dbpre);
		this.getFormHM().put("result",flag);
		this.getFormHM().put("checktype",checktype);
	}

}

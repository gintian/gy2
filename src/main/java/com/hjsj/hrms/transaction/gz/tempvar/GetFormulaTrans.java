package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.frame.codec.SafeCode;
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
public class GetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		String nid = (String)hm.get("nid");
		nid=nid!=null&&nid.trim().length()>0?nid:"";
		TempvarBo tempvarbo = new TempvarBo();
		String formula = tempvarbo.cValue(this.frameconn,nid);
		
		hm.put("formula",SafeCode.encode(formula));
	}

}

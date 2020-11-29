package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
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
public class OrgTableTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String infor = (String)hm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"2";
		hm.remove("infor");
		
		String unit_type = (String)hm.get("unit_type");
		unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"3";
		hm.remove("unit_type");

		PosparameXML pos = new PosparameXML(this.frameconn); 
		String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
		ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
		ctrl_type= "1".equals(ctrl_type)?ctrl_type:"2";
		
		String nextlevel = pos.getValue(PosparameXML.AMOUNTS,"nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		
		
		this.getFormHM().put("ctrl_type",ctrl_type);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("nextlevel",nextlevel);
	}

}

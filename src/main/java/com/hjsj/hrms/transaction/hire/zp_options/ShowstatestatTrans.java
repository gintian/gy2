package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowstatestatTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String height = (String)hm.get("height");
		height= height!=null&&height.length()>0?height:"600";
		
		hm.remove("height");
		
		hm.put("height",height);
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		String returnflag="";
		if(map.get("returnflag")!=null)
			returnflag=(String)map.get("returnflag");
		else
			returnflag=(String)this.getFormHM().get("returnflag");
		this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
		hm.put("schoolPosition", schoolPosition);
	}
}

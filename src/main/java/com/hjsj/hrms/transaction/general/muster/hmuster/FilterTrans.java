package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hjsj.hrms.interfaces.analyse.FilterCond;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null?tabid:"";
		hm.remove("tabid");
		
		String seiveid = (String)hm.get("seiveid");
		seiveid=seiveid!=null?seiveid:"";
		hm.remove("seiveid");
		
		String flag = (String)hm.get("flag");
		flag=flag!=null?flag:"";
		hm.remove("flag");
		
		GzFormulaXMLBo xmlbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
		String expr=""; 
		String factor="";
		FilterCond fi = new FilterCond(this.getFrameconn());
		
		if("alert".equalsIgnoreCase(flag)){
			ArrayList list = xmlbo.getSeiveItem(seiveid);
			if(list.size()>0){
				expr=(String)list.get(0);
				String fa=(String)list.get(1);
				if(fa!=null&&fa.trim().length()>0)
					factor=fi.factorstr(fa);
				this.getFormHM().put("name",(String)list.get(2));
			}
		}

		this.getFormHM().put("salaryitemlist",fi.getFeidlItemList());
		this.getFormHM().put("tabID",tabid);
		this.getFormHM().put("factor",factor);
		this.getFormHM().put("expr",expr);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("seiveid",seiveid);
	}
	
}

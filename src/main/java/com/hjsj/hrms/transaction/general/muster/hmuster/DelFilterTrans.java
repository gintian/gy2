package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DelFilterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null?tabid:"";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null?flag:"";
		
		GzFormulaXMLBo gzbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
		
		if("alert".equalsIgnoreCase(flag)){
			String seiveid = (String)this.getFormHM().get("seiveid");
			seiveid=seiveid!=null?seiveid:"";
			if(gzbo.delSeiveItem(seiveid)){
				gzbo.updateSeive();
				
			}
		}
		this.getFormHM().put("seivelist",gzbo.getSeiveItem());
	}

}

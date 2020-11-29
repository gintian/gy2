package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveFilterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String tabid = (String)this.getFormHM().get("tabid");
			tabid=tabid!=null?tabid:"";
			
			String name = (String)this.getFormHM().get("name");
			name=name!=null?name:"";
			
			String expr = (String)this.getFormHM().get("expr");
			expr=expr!=null?expr:"";
			
			String factor = (String)this.getFormHM().get("factor");
			factor=factor!=null?factor:"";
			
			String flag = (String)this.getFormHM().get("flag");
			flag=flag!=null?flag:"";
			
			String seiveid ="";
				
			GzFormulaXMLBo gzbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
			if("alert".equalsIgnoreCase(flag)){
				seiveid = (String)this.getFormHM().get("seiveid");
				seiveid=seiveid!=null?seiveid:"";
			}else if("add".equalsIgnoreCase(flag)){
				seiveid = gzbo.getSeiveItemId();
				seiveid=seiveid!=null&&seiveid.trim().length()>0?seiveid:"1";
			}
			expr=PubFunc.reBackWord(expr);
			name=PubFunc.keyWord_reback(name);
			name=PubFunc.reBackWord(name);
			factor=PubFunc.keyWord_reback(factor);
			gzbo.setSeiveItem(seiveid,name,factor,expr);
			gzbo.updateSeive();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}

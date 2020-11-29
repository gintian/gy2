package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class LayerTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null?itemid:"";
		String flag=(String)this.getFormHM().get("flag");
		String modelFlag=(String)this.getFormHM().get("modelFlag");
		modelFlag=modelFlag!=null?modelFlag:"";
		
		String codesetid="";
		if("B0110".equalsIgnoreCase(itemid)){
			codesetid="UN";
		}else if("E0122".equalsIgnoreCase(itemid)){
			codesetid="UM";
		}
		
		HmusterBo musterBo=new HmusterBo(getFrameconn());
		musterBo.setModelFlag(modelFlag);
		ArrayList layerlist = musterBo.getGroupLayer(itemid, codesetid);
		this.getFormHM().put("layerlist",layerlist);
		this.getFormHM().put("flag", flag);
	}

}

package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


public class SearchFieldItemListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fieldSetId=(String)this.getFormHM().get("fieldSetId");
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		ArrayList fielditemlist=statCutlineBo.getFieldItemList(null,fieldSetId);
		this.getFormHM().put("fielditemlist",fielditemlist);
	}

}

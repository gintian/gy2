package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddItemtypeTrans extends IBusiness {

	public void execute() throws GeneralException {

		String name=(String)this.getFormHM().get("name");
		String typeid=(String)this.getFormHM().get("typeid");
		String status=(String)this.getFormHM().get("status");
		if(status==null|| "".equals(status))
			status="0";
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		statCutlineBo.saveOrUpdate_keyItemtype(typeid,name,status);
        this.getFormHM().put("typeid","");
        this.getFormHM().put("name","");
        this.getFormHM().put("status","");
	}

}

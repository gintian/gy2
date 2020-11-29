package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditItemtypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String typeid=(String)hm.get("type_id");
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		RecordVo vo=statCutlineBo.getItemtypeByID(typeid);
		this.getFormHM().put("status",vo.getString("status"));
		this.getFormHM().put("name",vo.getString("name"));
		this.getFormHM().put("typeid",typeid);
	}

}

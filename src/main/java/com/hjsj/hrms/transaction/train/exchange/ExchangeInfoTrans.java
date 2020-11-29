package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ExchangeInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5701 = (String) hm.get("r5701");
		r5701 = PubFunc.decrypt(r5701);
		hm.remove("r5701");
		String r5703 = (String) hm.get("r5703");
		r5703 = SafeCode.decode(r5703);
		r5703 = PubFunc.decrypt(r5703);
		hm.remove("r5703");
		
		String columns="b0110,e0122,e01a1,a0101,exchangedtime,ncount";
		String strsql="select b0110,e0122,e01a1,a0101,exchangedtime,ed.ncount";
		StringBuffer strwhere = new StringBuffer();
		strwhere.append(" from tr_award_exchange ae,tr_exchange_detail ed where exchange_id=id and flag=1 and r5701="+r5701);
		
		this.formHM.put("r5703", SafeCode.decode(r5703));
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", " order by exchangedtime desc");
	}
}
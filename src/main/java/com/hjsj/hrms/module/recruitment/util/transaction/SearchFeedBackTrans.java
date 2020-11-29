package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hjsj.hrms.module.recruitment.util.FeedBackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchFeedBackTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String zp_pos_id = (String) this.getFormHM().get("zp_pos_id");
		zp_pos_id = PubFunc.decrypt(SafeCode.decode(zp_pos_id));
		String nbase = (String) this.getFormHM().get("nbase");
		nbase = PubFunc.decrypt(SafeCode.decode(nbase));
		String a0100 = (String) this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		HashMap feedmap = new HashMap();
		feedmap.put("nbase", nbase);
		feedmap.put("a0100", a0100);
		feedmap.put("zp_pos_id", zp_pos_id);
		FeedBackBo feedBackBo = new FeedBackBo(this.frameconn);
		String queryFeedBack = feedBackBo.queryFeedBack(feedmap);
		this.getFormHM().put("queryFeedBack", queryFeedBack);
	}

}

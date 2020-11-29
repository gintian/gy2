package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hjsj.hrms.module.recruitment.util.FeedBackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.List;

public class SaveFeedBackTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		//简历信息集合
		List userInfos = (List)this.getFormHM().get("userInfos");
		String content = (String)this.getFormHM().get("content");
		ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
		FeedBackBo bo = new FeedBackBo(frameconn);
		for(int i=0;i<userInfos.size();i++)
		{
		    ArrayList<String> value = new ArrayList<String>();
		    value.add(content);
			MorphDynaBean fromObject = (MorphDynaBean) userInfos.get(i);
			String a0100 = PubFunc.decrypt(fromObject.get("a0100").toString());
			value.add(a0100);
			String nbase = PubFunc.decrypt(fromObject.get("nbase").toString());
			value.add(nbase);
			String zp_pos_id = PubFunc.decrypt(fromObject.get("zp_pos_id").toString());
			value.add(zp_pos_id);
			valueList.add(value);
		}
		bo.updateFeedBack(valueList);
	}
}

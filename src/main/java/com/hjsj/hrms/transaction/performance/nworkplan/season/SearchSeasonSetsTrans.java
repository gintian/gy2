package com.hjsj.hrms.transaction.performance.nworkplan.season;
/*该交易类获得isseason和opt来控制叶签的分类。当opt是2时，还要belong_type的控制*/

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchSeasonSetsTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String frompage = (String)hm.get("frompage");
		if(frompage==null || "".equals(frompage)){//如果从团队进
			//以下四个参数是团队传给我的
			String opt = (String)hm.get("opt");
			String isread = (String)hm.get("isread");
			String belong_type = (String)hm.get("belong_type");
		    String p0100 = (String)hm.get("p0100");
			String returnUrl = (String)hm.get("returnurl");
			NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn , this.userView);
			String isseason = bo.isYearOrSeason(p0100);
			hm.remove("opt");
			hm.remove("isread");
			hm.remove("belong_type");
			hm.remove("p0100");
			hm.remove("returnurl");
			this.getFormHM().put("opt", opt);//为了控制叶签的分类
			this.getFormHM().put("isread", isread);//暂时无用
			this.getFormHM().put("p0100", p0100);//定位人
			this.getFormHM().put("belong_type", belong_type);//控制显示个人还是处室还是部门
			this.getFormHM().put("returnUrl", returnUrl);
			this.getFormHM().put("isseason", isseason);//1:季 2：年
		}else{
			String opt = (String)hm.get("opt");
			hm.remove("opt");
			this.getFormHM().put("opt", opt);
			String isseason = (String)hm.get("isseason");
			hm.remove("isseason");
			this.getFormHM().put("isseason", isseason);//1:季 2：年
		}

	}
	
}

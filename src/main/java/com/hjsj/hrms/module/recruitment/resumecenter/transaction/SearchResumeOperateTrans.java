package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/*****
 * 查询当前页面操作按钮集合
 * <p>Title: SearchResumeOperateTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-1-4 下午06:20:46</p>
 * @author xiexd
 * @version 1.0
 */
public class SearchResumeOperateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String zp_pos_id = (String) this.getFormHM().get("zp_pos_id");
		zp_pos_id = PubFunc.decrypt(zp_pos_id);
		String nbase = (String) this.getFormHM().get("nbase");
		nbase = PubFunc.decrypt(nbase);
		String a0100 = (String) this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(a0100);
		String link_id = (String) this.getFormHM().get("link_id");
		String flowId = (String) this.getFormHM().get("z0381");
		ResumeBo rbo = new ResumeBo(this.frameconn,this.userView);
		HashMap map = new HashMap();
		map.put("link_id", link_id);
		map.put("flowId", PubFunc.decrypt(flowId));
		ArrayList operate = rbo.getOperateList(zp_pos_id, nbase, a0100,map);
		if(link_id!=null&&!StringUtils.equalsIgnoreCase(link_id,""))
		{
			this.getFormHM().put("link_name", rbo.getCustom_name(link_id).get("custom_name"));
		}
		this.getFormHM().put("operate", operate);
	}

}

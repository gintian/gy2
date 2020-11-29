package com.hjsj.hrms.module.talentmarkets.portaldashboard.transaction;

import com.hjsj.hrms.module.talentmarkets.portaldashboard.businessobject.PortalDashboardService;
import com.hjsj.hrms.module.talentmarkets.portaldashboard.businessobject.impl.PortalDashboardServiceImpl;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * @Title PortalDashboardTrans
 * @Description 人才市场门户页面交易类
 * @Company hjsj
 * @Author wangbs、hanqh
 * @Date 2019/7/30
 * @Version 1.0.0
 */
public class PortalDashboardTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		String operateType = (String)this.getFormHM().get("operateType");
		PortalDashboardService pds = new PortalDashboardServiceImpl(this.userView, this.frameconn);
		try{
			// 查询门户页面所有数据
 			if(PortalDashboardService.ALL.equals(operateType)) {
				Map returnMap = pds.getAllData();
				this.formHM.put("return_data",returnMap);

 			}
			// 查询所选机构的数据
 			else if(PortalDashboardService.ORG_DATA.equals(operateType)){
				String orgIds = (String) this.getFormHM().get("orgIds");
				Map chartOption = pds.getCompePosChartOption(orgIds, "");
				Map psnOrPosPrivMap = TalentMarketsUtils.getPsnOrPosPriv(this.userView);
				this.formHM.put("chartOption", chartOption);
				this.formHM.put("psnOrPosPrivMap", psnOrPosPrivMap);
 		    }
			this.formHM.put("return_code", "success");
		} catch (GeneralException e) {
			e.printStackTrace();
			this.formHM.put("return_code", "fail");
			this.formHM.put("return_msg", e.getErrorDescription());
		}
	}

}


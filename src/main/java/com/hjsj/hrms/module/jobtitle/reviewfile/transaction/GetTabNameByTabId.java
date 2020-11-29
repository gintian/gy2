package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 获取业务模板名称
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
public class GetTabNameByTabId extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String tabId = (String)this.getFormHM().get("tabId");//计划号
			//planId= PubFunc.decrypt(tabId);
			
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.frameconn, this.userView);
			String tabName = reviewFileBo.getTabNameByTabId(tabId);
			
			this.getFormHM().put("tabName", tabName);

			Object config = this.getFormHM().get("getconfig");//是否获取配置信息
			if(config != null && (Boolean)config){
				String type = (String)this.getFormHM().get("type");// 0：公示 1：聘委会 2：学科组 3：同行专家 4：二级单位
				JobtitleConfigBo JobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
				this.getFormHM().put("configStr", JobtitleConfigBo.getJobtitleNoticeConfigByTabId(tabId, type));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

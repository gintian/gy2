package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DownloadTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String strItemids="";
			String tab_id=(String)this.getFormHM().get("tab_id");
			if ("2".equals(tab_id)){
				strItemids=(String)this.getFormHM().get("stritemids");
			}
			strItemids=strItemids.toUpperCase();
			String flag=(String)this.getFormHM().get("flag");
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.getUserView(),true,tab_id);
			bo.setStrExportFlds(strItemids);
			String fileName = bo.downloadTemplateFactory(flag);
			/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 start */
//			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			//20/3/18 xus vfs改造
			fileName = PubFunc.encrypt(fileName);
			/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 end */
			this.getFormHM().put("fileName", fileName);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

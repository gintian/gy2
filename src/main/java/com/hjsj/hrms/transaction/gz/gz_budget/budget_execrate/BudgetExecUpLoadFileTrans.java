package com.hjsj.hrms.transaction.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecRateExpImpBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecrateBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

public class BudgetExecUpLoadFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
	
			String budgetyear=(String)this.userView.getHm().get("execrate_budgetyear");
			String budgetmonth=(String)this.userView.getHm().get("execrate_budgetmonth");
			String B0110=(String)this.userView.getHm().get("execrate_b0110");
			String tab_id=(String)this.userView.getHm().get("execrate_tab_id");

			FormFile file=(FormFile)this.getFormHM().get("templateFile");
			/* 安全问题 文件上传 薪资预算-执行率分析-导入数据 xiaoyun 2014-9-18 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题 文件上传 薪资预算-执行率分析-导入数据 xiaoyun 2014-9-18 end */
			BudgetExecRateExpImpBo bo = new BudgetExecRateExpImpBo(this.getFrameconn(),this.userView,budgetyear,budgetmonth);
			String info = bo.importData(file, B0110,tab_id);
			
			BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.frameconn,this.userView);
			ExecBO.saveActualData(B0110, Integer.parseInt(budgetyear), Integer.parseInt(budgetmonth), Integer.parseInt(tab_id));
			
			if(!"0".equals(info)){
				throw new GeneralException("",info,"","");
			}


		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

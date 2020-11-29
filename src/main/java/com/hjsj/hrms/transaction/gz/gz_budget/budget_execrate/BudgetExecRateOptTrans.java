package com.hjsj.hrms.transaction.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecRateExpImpBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecrateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetExecRateOptTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String modelflag=(String)this.getFormHM().get("modelflag");
			if (modelflag==null) {				
				try{
					HashMap hm=this.getFormHM();
					ArrayList list=(ArrayList)hm.get("budgetexec_record");
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					dao.updateValueObject(list);					
					
					String budgetyear=(String)this.userView.getHm().get("execrate_budgetyear");
					String budgetmonth=(String)this.userView.getHm().get("execrate_budgetmonth");
					String b0110=(String)this.userView.getHm().get("execrate_b0110");
					String tab_id=(String)this.userView.getHm().get("execrate_tab_id");
					BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.frameconn,this.userView);
					ExecBO.saveActualData(b0110, Integer.parseInt(budgetyear), Integer.parseInt(budgetmonth), Integer.parseInt(tab_id));
			
					return;
				}catch(Exception e){
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
			else {	
				String tab_id=(String)this.getFormHM().get("tab_id");
				String B0110=(String)this.userView.getHm().get("execrate_b0110");
				String budgetyear=(String)this.getFormHM().get("budgetyear");
				String budgetmonth=(String)this.getFormHM().get("budgetmonth");
				
				if ("downSingle".equals(modelflag)){//下载单个模板
					BudgetExecRateExpImpBo bo = new BudgetExecRateExpImpBo(this.getFrameconn(),this.userView,budgetyear,budgetmonth);
					String fileName = bo.downloadSingleTemplate(B0110, tab_id);
					/* 安全问题 文件下载 薪资预算-执行率分析-下载模版 xiaoyun 2014-9-17 start */
					fileName = SafeCode.encode(PubFunc.encrypt(fileName));
					/* 安全问题 文件下载 薪资预算-执行率分析-下载模版 xiaoyun 2014-9-17 end */
					this.getFormHM().put("fileName", fileName);
				}
				if("Statistics".equals(modelflag)){//重新统计本期实际数
					int tid =  Integer.parseInt(tab_id);
					int year = Integer.parseInt(budgetyear);
					int month = Integer.parseInt(budgetmonth);
					BudgetExecrateBo bebo = new BudgetExecrateBo(this.getFrameconn(),this.userView);
					boolean isactualdata = bebo.calcActualData(B0110, year, month, tid);
					this.getFormHM().put("isactualdata", String.valueOf(isactualdata));
					this.getFormHM().put("tab_id", tab_id);
				}
				if ("imp".equals(modelflag)){//导入数据
/*					FormFile file=(FormFile)this.getFormHM().get("templateFile");
					BudgetExecRateExpImpBo bo = new BudgetExecRateExpImpBo(this.getFrameconn(),this.userView,budgetyear,budgetmonth);
					String info = bo.importData(file, B0110,tab_id);
					BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.frameconn,this.userView);
					ExecBO.saveActualData(B0110, Integer.parseInt(budgetyear), Integer.parseInt(budgetmonth), Integer.parseInt(tab_id));
					
					if(!info.equals("0")){
						throw new GeneralException("",info,"","");
					}*/
				}
				else if ("expBatch".equals(modelflag)){//批量导出
					BudgetExecRateExpImpBo bo = new BudgetExecRateExpImpBo(this.getFrameconn(),this.userView,budgetyear,budgetmonth);
					String fileName = bo.Batchdownload(B0110);
					/* 安全问题 文件下载 执行率分析-导出 xiaoyun 2014-9-20 start */
					fileName = SafeCode.encode(PubFunc.encrypt(fileName));
					/* 安全问题 文件下载 执行率分析-导出 xiaoyun 2014-9-20 end */
					this.getFormHM().put("fileName", fileName);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

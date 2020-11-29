package com.hjsj.hrms.transaction.gz.gz_budget.budget_history;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class Budget_historyDownloadTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String tab_id=(String)this.getFormHM().get("tab_id");
			String flag=(String)this.getFormHM().get("flag");
			String bb0110 = (String)this.getFormHM().get("b0110");
			String b0110 = bb0110.substring(2);
			String budget_id = (String)this.getFormHM().get("budgetid");

			if (tab_id==null) tab_id="0";
			
			BudgetSysBo sysBo=new BudgetSysBo(this.frameconn,this.userView);
			HashMap sysOptionMap=sysBo.getSysValueMap();
			String kindstr = (String)sysOptionMap.get("kindstr");//预算表分类
			
			if ((","+kindstr+",").indexOf(","+tab_id+",")>-1) {//类别
				if ("0".equals(flag)){
					throw GeneralExceptionHandler.Handle(new Exception("请选择预算表！"));						
				}
				else {
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					this.frowset=dao.search("select min(tab_id) from gz_budget_tab where (tab_type=4 ) and budgetgroup ='"+tab_id+"'");
					if(this.frowset.next())
						tab_id=this.frowset.getString(1);		
				}	
			}
			if (tab_id==null) tab_id="0";			
			if ("0".equals(tab_id)||"计提".equals(tab_id)||"支出".equals(tab_id)){
				throw GeneralExceptionHandler.Handle(new Exception("没有可以导出的预算表！"));	
				
			}

			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
			String fileName = examBo.downloadTemplateFactory(flag, budget_id, tab_id,b0110);
			/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 start */
			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 end */
			this.getFormHM().put("fileName", fileName);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}

package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

public class SaveBudgetPropTrans extends IBusiness{

	public void execute() throws GeneralException {
		//获取要修改的数据
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			LazyDynaBean budgetBean = (LazyDynaBean)this.getFormHM().get("budgetBean");
			String tab_id = (String)budgetBean.get("tab_id");
			String budgetgroup = (String)budgetBean.get("budgetgroup");
			String codesetid = (String)budgetBean.get("codesetid");
			String tabcode = (String)budgetBean.get("tabcode");
			//从form中得到三个checkbox的值
			String analyseFlag = (String)this.getFormHM().get("analyseFlag");
			String bpFlag = (String)this.getFormHM().get("bpFlag");
			String validFlag = (String)this.getFormHM().get("validFlag");
			//保存
			StringBuffer sb_update = new StringBuffer();
			sb_update.append("update gz_budget_tab set budgetgroup='"+budgetgroup+"',codesetid='"+codesetid+"',tabcode='"+tabcode+"',analyseFlag="+analyseFlag+",bpFlag="+bpFlag+",validFlag="+validFlag);
			sb_update.append(" where tab_id="+tab_id);
			dao.update(sb_update.toString());
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

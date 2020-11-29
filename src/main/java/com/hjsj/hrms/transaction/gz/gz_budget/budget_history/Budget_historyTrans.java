package com.hjsj.hrms.transaction.gz.gz_budget.budget_history;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_history.BudgetHistoryBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Budget_historyTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String tab_id = "";
			BudgetHistoryBo bo = new BudgetHistoryBo();
			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("select budget_id,yearNum,adjustDate,budgetType from gz_budget_index where SPFlag = '04' and budgettype in (1,2,3) order by budget_id desc");
			this.frowset = dao.search(sql.toString());
			LazyDynaBean bean = null;
			SimpleDateFormat sdf=new  SimpleDateFormat("yyyy年MM月dd日");

			while (this.frowset.next()) {
				bean = new LazyDynaBean();
				String yearNum = this.frowset.getString("yearNum");
				String adjustDate = sdf.format(this.frowset.getDate("adjustDate"));
				String budgetType = this.frowset.getString("budgetType");
				String budget_id = this.frowset.getString("budget_id");
				String type = bo.getInfoStr(budgetType);
				String ze = examBo.getTotal(budget_id);
				
				bean.set("yearNum", yearNum);
				bean.set("adjustDate", adjustDate);
				bean.set("budgetType", type);
				bean.set("ze", ze);
				bean.set("budget_id", budget_id);
				list.add(bean);
			}
			
			this.getFormHM().put("list", list);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

}

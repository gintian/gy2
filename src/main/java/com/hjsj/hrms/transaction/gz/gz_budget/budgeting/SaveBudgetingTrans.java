package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveBudgetingTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm=this.getFormHM();
//			String tableName=(String) this.getFormHM().get("tableName");
			String tableName=(String)hm.get("x_table");//数据集标签setalias属性
//			ArrayList list=(ArrayList)hm.get(tableName+"_record");
			ArrayList list=(ArrayList)hm.get("x_record");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
			//更新下级代码名称错位问题
			String tabid=(String)this.userView.getHm().get("budgeting_tabid");
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,tabid);
			bo.updateChildCodeDesc(tabid);
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 预算分配机构树交易类
 * Create Time: 2012.10.18
 * @author genglz
 *
 */
public class SearchBudgetOrgTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String modelflag=(String)map.get("modelflag");
			BudgetAllocBo bo = new BudgetAllocBo(this.getFrameconn(),this.userView);
			bo.checkCanEnter();
			if(bo.getErrorMessage() != null)
				throw GeneralExceptionHandler.Handle(new Exception(bo.getErrorMessage()+"！"));
			
			String topUnitId = bo.getTopUn();
	        if (!userView.isSuper_admin()) 
	        {//2014-02-12
	            String codesetid=this.userView.getManagePrivCode();
	            if (!"UN".equalsIgnoreCase(codesetid)){
	                throw GeneralExceptionHandler.Handle(new Exception("当前用户的管理范围必须为单位！"));   
	            }
	 
	        }
			this.getFormHM().clear();
			this.getFormHM().put("topUnitId", topUnitId);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;


import com.hjsj.hrms.businessobject.gz.gz_budget.budget_revoke.BudgetRevokeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *  撤销预算交易类
 * <p>Title:DelBudgetTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 24, 2012 5:11:06 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
public class DelBudgetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			BudgetRevokeBo bo=new BudgetRevokeBo(this.getFrameconn(),this.userView);//调用撤销预算业务类
			String budget_id=bo.getBudgetId(this.getFormHM().get("vo"));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String flag=bo.validate(budget_id);
			if("01".equals(flag)){
		    	this.getFormHM().put("tishi1", ResourceFactory.getProperty("gz.budget.budget_allocation.alert1"));//向前台传值用于Js提示
		    	return;
			}
			if("02".equals(flag)){
				this.getFormHM().put("tishi2", ResourceFactory.getProperty("gz.budget.budget_allocation.alert2"));
				return;
			}
		    RecordVo vo=new RecordVo("gz_budget_index");
		    vo.setString("budget_id", budget_id);
			vo=dao.findByPrimaryKey(vo);
			
			String getZongE_Sql=bo.getZongE_Sql(budget_id);
			String getExec_Sql=bo.getExec_Sql(budget_id);
			String getCanShu_Sql=bo.getCanShu_Sql(budget_id);
			String getSC01_Sql=bo.getSC01_Sql(budget_id);
			String getSC02_Sql=bo.getSC02_Sql(budget_id);
			String getSC03_Sql=bo.getSC03_Sql(budget_id);
			dao.deleteValueObject(vo);
			dao.delete(getZongE_Sql, new ArrayList());
			dao.delete(getExec_Sql, new ArrayList());
			dao.delete(getCanShu_Sql, new ArrayList());
			dao.delete(getSC01_Sql, new ArrayList());
			dao.delete(getSC02_Sql, new ArrayList());
			dao.delete(getSC03_Sql, new ArrayList());

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

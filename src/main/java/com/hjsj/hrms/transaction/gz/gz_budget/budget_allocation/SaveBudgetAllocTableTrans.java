package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 保存预算分配数据
 * Create Time: 2012.10.20
 * @author genglz
 *
 */
public class SaveBudgetAllocTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			BudgetSysBo bo=new BudgetSysBo(this.getFrameconn(),this.userView);
			String status=(String)bo.getSysValueMap().get("ysze_status_menu");
			HashMap hm=this.getFormHM();
			ArrayList list=(ArrayList)hm.get("ysze_set_record");
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);				
				if(vo.getState()==-1){  // 新增记录状态
					throw new Exception(ResourceFactory.getProperty("gz.acount.alert.failure"));
				}
				vo.setDate("modtime", DateStyle.getSystemTime());
				vo.setString("modusername", userView.getUserName());
				// 不能更新状态
				if(status!=null)
					vo.removeValue(status.toLowerCase());
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

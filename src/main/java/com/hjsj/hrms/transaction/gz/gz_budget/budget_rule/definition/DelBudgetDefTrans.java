package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DelBudgetDefTrans extends IBusiness{

	public void execute() throws GeneralException {
		String ids = SafeCode.decode((String)this.getFormHM().get("ids"));
		ids = PubFunc.keyWord_reback(ids);
		String isSuccess = "1";//判断是否删除成功：  0 不成功 1 成功
		String errorMessage = "";//如果删除不成功，提示错误信息
		ArrayList list = new ArrayList();//存储在执行表中已经存在的表的名字
		try{
			StringBuffer sb_query = new StringBuffer();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			sb_query.append("select distinct gbt.tab_name from gz_budget_exec gbe,gz_budget_tab gbt where gbe.tab_id=gbt.tab_id and gbe.tab_id in "+ids);
			this.frowset = dao.search(sb_query.toString());
			if(this.frowset.next()){//如果有记录
				this.frowset.previous();
				while(this.frowset.next()){
					isSuccess = "0";
					String tab_name = this.frowset.getString("tab_name");
					list.add(tab_name);
				}
				for(int i=0;i<list.size();i++){
					errorMessage+=(String)list.get(i);
					if(i!=list.size()-1)
						errorMessage+="，";
				}
				errorMessage +=ResourceFactory.getProperty("gz.budget.using");
			}else{//如果没有记录
				StringBuffer sb_delete = new StringBuffer();
				//删除公式 2013-02-28 add wangrd 
				sb_delete.append("delete from gz_budget_formula where tab_id in "+ids+" and tab_id not in (1,2,3)");				
				dao.delete(sb_delete.toString(), new ArrayList());
				sb_delete.setLength(0);
				sb_delete.append("delete from gz_budget_tab where tab_id in "+ids+" and tab_type not in (1,2,3)");				
				dao.delete(sb_delete.toString(), new ArrayList());
			}
			this.getFormHM().put("isSuccess", isSuccess);
			this.getFormHM().put("errorMessage", errorMessage);
			this.getFormHM().put("isAdd", "0");
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

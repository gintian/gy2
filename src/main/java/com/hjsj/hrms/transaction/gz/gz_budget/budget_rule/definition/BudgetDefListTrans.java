package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetDefListTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String isadd=(String)hm.get("isadd"); 
		hm.remove("isadd");
		if (isadd==null) isadd="0";
		ArrayList budgetList = new ArrayList();
		try{
			int number = 1;
			StringBuffer sb = new StringBuffer();
			sb.append("select tab_id,tab_name,validFlag,tab_type,seq from gz_budget_tab order by seq");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				String tab_id = this.frowset.getString("tab_id");
				String tab_name = this.frowset.getString("tab_name");
				String validFlag = this.frowset.getString("validFlag");
				String tab_type = this.frowset.getString("tab_type");
				String seq = this.frowset.getString("seq");
				String isCheckbox = "1";//是否显示复选框   0 不显示 1 显示
				String isProperty = "1";//是否显示属性     0 不显示 1 显示
				if("1".equals(tab_type) || "2".equals(tab_type) || "3".equals(tab_type)){
					isCheckbox = "0";
				}
				if("1".equals(tab_type) || "2".equals(tab_type)){
					isProperty = "0";
				}
				bean.set("tab_id", tab_id);
				bean.set("isCheckbox", isCheckbox);
				bean.set("number", String.valueOf(number));
				bean.set("tab_name", tab_name);
				bean.set("validFlag", validFlag);
				bean.set("isProperty", isProperty);
				bean.set("seq", seq);
				
				budgetList.add(bean);
				number++;
			}
			this.getFormHM().put("isAdd", isadd);
			this.getFormHM().put("budgetList", budgetList);
			this.getFormHM().put("count", String.valueOf(number-1));//把记录总数传过去，用来控制排序
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}

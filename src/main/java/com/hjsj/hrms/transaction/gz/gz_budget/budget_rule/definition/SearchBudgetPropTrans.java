package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.definition.BudgetDefBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBudgetPropTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//获得超链接中的参数
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String tab_id = (String)hm.get("tab_id");
			hm.remove("tab_id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			//先从constant表中得到预算表分类
			BudgetDefBo bo = new BudgetDefBo(this.getFrameconn(),this.userView);
			ArrayList kindList = bo.getKindList();
			ArrayList codesetList = bo.getCodesetList();
			LazyDynaBean budgetBean = new LazyDynaBean();
			StringBuffer sb = new StringBuffer();
			sb.append("select tab_id,tab_name,budgetGroup,Codesetid,tab_type,tabCode,analyseFlag,bpFlag,validFlag from gz_budget_tab where tab_id='"+tab_id+"'");
			this.frowset = dao.search(sb.toString());
			
			//为了使用<hrms:checkbox，把他们单独拿到外面。
			String analyseFlag = "0";//是否执行分析
			String bpFlag = "0";//报批标志
			String validFlag = "0";//是否生效
			String tabType = "4"; // 预算表分类, 默认一般预算表
			
			if(this.frowset.next()){//如果有记录
				String tab_name = this.frowset.getString("tab_name");//预算表名称
				String budgetGroup = this.getName(this.frowset.getString("budgetGroup"));//预算表分类
				String codesetid = this.getName(this.frowset.getString("codesetid"));//预算项目类别
				String tabCode = this.getName(this.frowset.getString("tabCode"));//转换代码
				analyseFlag = this.frowset.getString("analyseFlag");//是否执行分析
				bpFlag = this.frowset.getString("bpFlag");//报批标志
				validFlag = this.frowset.getString("validFlag");//是否生效
				tabType = this.frowset.getString("tab_type"); // 预算表分类
				
				budgetBean.set("tab_id", tab_id);
				budgetBean.set("tab_name", tab_name);
				budgetBean.set("budgetgroup", budgetGroup);
				budgetBean.set("codesetid", codesetid);
				budgetBean.set("tabcode", tabCode);
			}
			this.getFormHM().put("tabType", tabType);
			this.getFormHM().put("analyseFlag", analyseFlag);
			this.getFormHM().put("bpFlag", bpFlag);
			this.getFormHM().put("validFlag", validFlag);
			
			this.getFormHM().put("budgetBean", budgetBean);
			this.getFormHM().put("kindList", kindList);
			this.getFormHM().put("codesetList", codesetList);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	//如果是NULL，就返回""。
	public String getName(String str){
		if(str==null)
			str="";
		return str;
	}
}

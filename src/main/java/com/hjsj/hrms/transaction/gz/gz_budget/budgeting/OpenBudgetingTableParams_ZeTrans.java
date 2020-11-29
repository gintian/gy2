package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 打开编制预算参数/总额表
 * @author akuan
 *
 */
public class OpenBudgetingTableParams_ZeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b0110=(String) hm.get("b0110");
			String canshu=(String) hm.get("canshu");
			BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
			Integer budget_id = allocBo.getCurrentBudgetId(); 
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
			LazyDynaBean bean = allocBo.getAppealStatus(b0110,budget_id.toString());
			String status =(String) bean.get("status");
			String infoStr="";
			String tabName ="";
			ArrayList fieldList=new ArrayList();
			if("params".equalsIgnoreCase(canshu)){
			   infoStr=bo.getInfoStr(ResourceFactory.getProperty("gz.budget.budgeting.params.table"));
			   tabName=((String)sysbo.getSysValueMap().get("ysparam_set")).toLowerCase();
			   fieldList=allocBo.getFieldList1(b0110);
			   this.getFormHM().put("status", status);
			}
			if("zonge".equalsIgnoreCase(canshu)){
				   infoStr=bo.getInfoStr(ResourceFactory.getProperty("gz.budget.budgeting.ze.table"));
				   tabName=((String)sysbo.getSysValueMap().get("ysze_set")).toLowerCase();
				   fieldList=allocBo.getFieldList0(b0110);
				   this.getFormHM().put("status", "ze");
				}
			this.getFormHM().put("tab_name", tabName);
			this.getFormHM().put("b0110", b0110);
			this.getFormHM().put("canshu", canshu);
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("budget_id", budget_id.toString());
			this.getFormHM().put("infoStr", infoStr);

			this.getFormHM().put("zeItemid", ((String)sysbo.getSysValueMap().get("ysze_ze_menu")).toLowerCase());
			if(!"01".equals(status)&&!"07".equals(status)&&!"04".equals(status)){
				this.getFormHM().put("zhuangtai", "readonly");
			}else{
				this.getFormHM().put("zhuangtai", "write");
			}
			
			
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	


}

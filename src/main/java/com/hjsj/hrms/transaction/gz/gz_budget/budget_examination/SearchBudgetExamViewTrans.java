package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *  
 * <p>Title:SearchBudgetExamViewTrans.java</p>
 * <p>Description>:获得预算审批界面</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 13, 2012 11:47:28 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author:dengc
 */
public class SearchBudgetExamViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			hm.remove("tab_id");
			
			String budget_id=(String)this.getFormHM().get("budget_id"); 
			String tab_id="";  
			String b0110="";

			if(hm.get("a_code")!=null)
			{
				b0110=((String)hm.get("a_code")).substring(2);
			}
			String flag=(String)this.getFormHM().get("flag");
			tab_id=(String)this.getFormHM().get("tab_id");
			if (tab_id==null ){
				tab_id ="";
			}
			
			if("init".equals(tab_id)){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if ((flag!=null) & ("2".equals(flag))){
					this.frowset=dao.search("select min(tab_id) from gz_budget_tab where (tab_type=4 or tab_type=3)  "
							  +" and tab_id in (select tab_id from gz_budget_exec where budget_id ="+budget_id+")");	
				}
				else {									
					this.frowset=dao.search("select min(tab_id) from gz_budget_tab where (tab_type=4 or tab_type=3) and validFlag=1 and bpflag =1");
				}
				if(this.frowset.next())
					tab_id=this.frowset.getString(1);
				else
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.budget_examination.definePlan")+"!"));
			}	
			if (tab_id==null || tab_id.length()<1){
			    throw GeneralExceptionHandler.Handle(new Exception("当前没有需要审批的计划，请设置需要上报的计划!"));
			    
			}
			 
			
			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
			
			String tabName="";
			if(examBo.getGzBudgetTabVo()!=null)
				tabName=examBo.getGzBudgetTabVo().getString("tab_name");
			String currentBudgetDesc=examBo.getCurrentBudgetDesc(budget_id);
			LazyDynaBean statusBean=examBo.getAppealStatus(b0110,budget_id);
			String appealStatusDesc=(String)statusBean.get("statusDesc");
			String appeal_status=(String)statusBean.get("status");
			
			ArrayList fieldList=examBo.getFieldList(b0110,budget_id,tab_id);
			String sql=examBo.getSql(b0110,budget_id,tab_id,fieldList);
		 
			if(sql.toUpperCase().indexOf("SC03")!=-1)
				this.getFormHM().put("tab_name", "sc03");
			else if(sql.toUpperCase().indexOf("SC02")!=-1)
				this.getFormHM().put("tab_name", "sc02");
			else
				this.getFormHM().put("tab_name", "t_budgetTotal");			
			
			
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("sql",sql);

			
			if ((sql.toUpperCase().indexOf("SC03")!=-1) || (sql.toUpperCase().indexOf("SC02")!=-1))
			{
				this.getFormHM().put("appealStatusDesc", ResourceFactory.getProperty("column.sys.status")+"："+appealStatusDesc);
				this.getFormHM().put("appeal_status",appeal_status);
			}
			else
			{
				this.getFormHM().put("appealStatusDesc","");
				this.getFormHM().put("appeal_status","");
			}
			this.getFormHM().put("currentBudgetDesc",currentBudgetDesc);
			this.getFormHM().put("budget_id",budget_id);

			this.getFormHM().put("tab_id",tab_id);
			this.getFormHM().put("b0110",b0110);
			if ((sql.toUpperCase().indexOf("SC03")!=-1) || (sql.toUpperCase().indexOf("SC02")!=-1))
			{	
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
					display_e0122="0";
				String desc="";
				if(Integer.parseInt(display_e0122)==0)
				{
					desc=AdminCode.getCodeName("UN",b0110);
				}
				else
				{
					CodeItem item=AdminCode.getCode("UN",b0110,Integer.parseInt(display_e0122));
	    	    	if(item==null)
	    	    	{
	    	    		desc=AdminCode.getCodeName("UN",b0110);
	        		}
	    	    	else
	    	    	{
	    	    		desc=item.getCodename(); 
	    	    	}
	    	    	
				} 
				this.getFormHM().put("b0110_desc",ResourceFactory.getProperty("gz.budget.budgeting.ysunit")+"："+desc+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			else
				this.getFormHM().put("b0110_desc","");
			this.getFormHM().put("tabName",tabName);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}

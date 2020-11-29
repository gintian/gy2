package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class BudgetValidateOptTrans extends IBusiness {

	private String strLastError="";
	public void execute() throws GeneralException {
		String flag=(String)this.getFormHM().get("flag"); //1:批准   2：驳回 4报批 上报
		if (ValidateData(flag)){
			this.getFormHM().put("info", "true");
			this.getFormHM().put("strError","");
		}
		else{
			this.getFormHM().put("info", "false");
			this.getFormHM().put("strError", this.strLastError);
			
		}
		this.getFormHM().put("flag", flag);
	
	}
private boolean ValidateData(String flag) throws GeneralException{
	boolean b=false;	
	try {

		String b0110=(String)this.getFormHM().get("b0110");
		String budget_id=(String)this.getFormHM().get("budget_id");
		BudgetSysBo bo=new BudgetSysBo(this.getFrameconn(),this.userView);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),this.userView);
		String optName="";
		if ("1".equals(flag)){
			optName="批准";			
		}
		else  if ("2".equals(flag)){
			optName="驳回";			
		}
		else if ("4".equals(flag)){
			optName="上报";			
		}
		
		boolean bPublished=false;
		String strsql = "select SPFlag from gz_budget_index where budget_id = "+budget_id+"";
		RowSet rs = dao.search(strsql.toString());
		if (rs.next()){
			String sp=rs.getString("SPFlag");
			if("04".equals(sp)){//已发布
				bPublished=true;	
			}
		}	
		
		if ("1".equals(flag)|| "2".equals(flag)|| "4".equals(flag)){
			if (bPublished) {					
				this.strLastError="本次预算已发布,不能执行"+optName+"操作!";
				return b ;
			}
		}

		
		HashMap sysOptionMap=bo.getSysValueMap(); 		
		boolean isTopUn=examBo.isTopUn(b0110);
		
		if(isTopUn)	{
			this.strLastError="顶级单位无需执行"+optName+"操作!";
			return b ;
		}
		
		if ("1".equals(flag)|| "2".equals(flag)){//当前用户顶级单位 不能批准 驳回
			String usrtopun=examBo.getTopUn();
			if (usrtopun!=null){
				if (b0110.equals(usrtopun)){
					this.strLastError="只能对下级单位执行"+optName+"操作!";
					return b ;
				}
			}
		}
		
		String sql = "select "
			+ (String) sysOptionMap.get("ysze_status_menu")
			+ " from  " + (String) sysOptionMap.get("ysze_set")
			+ " where "
			+ (String) sysOptionMap.get("ysze_idx_menu") + "="
			+ budget_id + " and b0110='" + b0110 + "'";
		this.frowset = dao.search(sql);
		if (!this.frowset.next()) {
			this.strLastError="非预算单位，无需"+optName+"!";
			return b ;
		}
		
		if("2".equals(flag)){
			boolean isReject = examBo.isReject(budget_id, b0110);
			if(!isReject){
				this.strLastError="上级单位是已报批状态,此单位不能执行"+optName+"操作!";
				return b ;
			}
		}
		b=true;
		return b ;
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
	

	
	
	
}

}

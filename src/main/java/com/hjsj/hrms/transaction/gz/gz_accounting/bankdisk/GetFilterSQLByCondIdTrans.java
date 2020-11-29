package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetFilterSQLByCondIdTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String condid=(String)this.getFormHM().get("condid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String model=(String)this.getFormHM().get("model");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			String xml=bo.getCondXML(salaryid);
			SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
			HashMap map = sLPBo.getServiceItemMap();
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if("0".equals(model))
			{
	    		if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
	    			tableName=this.userView.getUserName()+"_salary_"+salaryid;
	    		else
		    		tableName=manager+"_salary_"+salaryid;
			}
			else
			{
				tableName="salaryhistory";
			}
			String expresion=(String)map.get(condid);//形式为expr|factor
			int idx=expresion.indexOf("|");
			String sexpr=expresion.substring(0,idx);
			String sfactor=expresion.substring(idx+1);
			HashMap fieldItemMap=bo.getFieldItemMap(Integer.parseInt(salaryid),this.userView);
			FactorList factor_bo=new FactorList(sexpr.toString(),sfactor.toString().toUpperCase(),this.userView.getUserId(),fieldItemMap);
	  	    String sql=factor_bo.getSingleTableSqlExpression(tableName);
			this.getFormHM().put("sql",SafeCode.encode(PubFunc.encrypt(sql)));
			this.getFormHM().put("condid",condid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

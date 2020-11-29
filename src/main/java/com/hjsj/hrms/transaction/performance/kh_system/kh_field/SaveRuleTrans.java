package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveRuleTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String pointtype="0";
			String ltype="1";
			String add_type="0";
			String minus_type="1";
			String rule="0";
			String add_value="";
			String minus_value="";
			String add_score="";
			String minus_score="";
			String add_valid="0";
			String minus_valid="0";
			String convert="0";
			pointtype=(String)this.getFormHM().get("pointtype");
			ltype=(String)this.getFormHM().get("ltype");
			add_type=(String)this.getFormHM().get("add_type");
			minus_type=(String)this.getFormHM().get("minus_type");
			add_value=(String)this.getFormHM().get("add_value");
			add_score=(String)this.getFormHM().get("add_score");
			add_valid=(String)this.getFormHM().get("add_valid");
			minus_value=(String)this.getFormHM().get("minus_value");
			minus_score=(String)this.getFormHM().get("minus_score");
			minus_valid=(String)this.getFormHM().get("minus_valid");
			rule=(String)this.getFormHM().get("rule");
			convert=(String)this.getFormHM().get("convert");	
			this.getFormHM().put("pointtype", pointtype);
			this.getFormHM().put("ltype",ltype);
			this.getFormHM().put("add_type",add_type);
			this.getFormHM().put("minus_type", minus_type);
			this.getFormHM().put("rule",rule);
			this.getFormHM().put("add_value",add_value);
			this.getFormHM().put("minus_value", minus_value);
			this.getFormHM().put("add_score",add_score);
			this.getFormHM().put("minus_score",minus_score);
			this.getFormHM().put("add_valid",add_valid);
			this.getFormHM().put("minus_valid",minus_valid);
			this.getFormHM().put("convert",convert);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

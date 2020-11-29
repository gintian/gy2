package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitGzStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("opt");
		String standardID=(String)hm.get("standardID");
		hm.remove("opt");
		hm.remove("standardID");
		
		String hfactor="";
		String s_hfactor="";
		String vfactor="";
		String s_vfactor="";
		String item="";
		String hcontent="";
		String vcontent="";
		
		
		if(standardID==null)
		{
			this.getFormHM().put("hfactor_name","横向栏目");
			this.getFormHM().put("s_hfactor_name","横向子栏目");
			this.getFormHM().put("vfactor_name","纵向栏目");
			this.getFormHM().put("s_vfactor_name","纵向子栏目");
			this.getFormHM().put("standardID","");
			this.getFormHM().put("gzStandardName","");
		}
		else
		{
			
			
		}
		this.getFormHM().put("opt",opt);
		
		this.getFormHM().put("hfactor",hfactor);
		this.getFormHM().put("s_hfactor",s_hfactor);
		this.getFormHM().put("vfactor",vfactor);
		this.getFormHM().put("s_vfactor",s_vfactor);
		this.getFormHM().put("item",item);
		this.getFormHM().put("hcontent",hcontent);
		this.getFormHM().put("vcontent",vcontent);
	}

}

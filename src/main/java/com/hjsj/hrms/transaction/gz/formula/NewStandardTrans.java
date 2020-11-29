package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class NewStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("opt");
		String standardID=(String)hm.get("standardID");
		
		String itemname=(String)hm.get("itemname");
		itemname=itemname!=null&&itemname.trim().length()>0?itemname:"";
		hm.remove("opt");
		hm.remove("standardID");
		hm.remove("itemname");
		
		String hfactor="";
		String s_hfactor="";
		String vfactor="";
		String s_vfactor="";
		String item="";
		String hcontent="";
		String vcontent="";
		
		
		if(standardID==null)
		{
			this.getFormHM().put("hfactor_name",ResourceFactory.getProperty("gz.formula.cross.bar"));
			this.getFormHM().put("s_hfactor_name",ResourceFactory.getProperty("gz.formula.son.cross.bar"));
			this.getFormHM().put("vfactor_name",ResourceFactory.getProperty("gz.formula.columns"));
			this.getFormHM().put("s_vfactor_name",ResourceFactory.getProperty("gz.formula.son.columns"));
			this.getFormHM().put("standardID","");
			this.getFormHM().put("gzStandardName","");
		}
		this.getFormHM().put("opt",opt);
		
		this.getFormHM().put("hfactor",hfactor);
		this.getFormHM().put("s_hfactor",s_hfactor);
		this.getFormHM().put("vfactor",vfactor);
		this.getFormHM().put("s_vfactor",s_vfactor);
		this.getFormHM().put("item",itemname);
		this.getFormHM().put("hcontent",hcontent);
		this.getFormHM().put("vcontent",vcontent);
	}

}

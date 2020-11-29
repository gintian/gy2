package com.hjsj.hrms.transaction.general.template;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SelectPrivTemplateSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String flag=(String)this.getFormHM().get("flag");
		String roleid=(String)this.getFormHM().get("roleid");
		String res_flag=(String)this.getFormHM().get("res_flag");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String sorttype=(String)hm.get("sorttype");
		hm.remove("sorttype");
		/**资源查询*//*
		if(flag==null||flag.equals(""))
            flag=GeneralConstant.ROLE;
		if(res_flag==null||res_flag.equals(""))
			res_flag="0";
		*//**资源类型*//*
		int res_type=Integer.parseInt(res_flag);
		SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
		String res_str=privbo.getWarn_str();
		ResourceParser parser=new ResourceParser(res_str,res_type);
		*//**1,2,3*//*
		String str_content=","+parser.getContent()+",";
		this.getFormHM().put("law_dir",str_content);		*/
	}

}

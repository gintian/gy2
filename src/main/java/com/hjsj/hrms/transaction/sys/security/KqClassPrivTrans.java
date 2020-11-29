package com.hjsj.hrms.transaction.sys.security;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:KqClassPrivTrans.java</p>
 * <p>Description>:KqClassPrivTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 25, 2010 11:21:50 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class KqClassPrivTrans extends IBusiness {

	public void execute() throws GeneralException {
		String res_flag=(String)this.getFormHM().get("res_flag");
		String roleid=(String)this.getFormHM().get("roleid");
		String flag=(String)this.getFormHM().get("flag");
		if(res_flag==null||res_flag.length()<=0)
			res_flag="24";
		int res_type=Integer.parseInt(res_flag);
		SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
		String res_str=privbo.getWarn_str();
		ResourceParser parser=new ResourceParser(res_str,res_type);
		/**1,2,3*/
		String priv_selected=","+parser.getContent()+",";		
		this.getFormHM().put("law_dir", priv_selected);
	}

}

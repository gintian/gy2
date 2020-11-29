package com.hjsj.hrms.transaction.sys.security;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.transaction.lawbase.SaveLawResourceTrans;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 考勤班组
 * <p>Title:SaveKqGroupsPrivTrans.java</p>
 * <p>Description>:SaveKqGroupsPrivTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 30, 2010 11:31:30 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SaveKqGroupsPrivTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String priv_selected=(String)this.getFormHM().get("priv_selected");
			String flag=(String)this.getFormHM().get("flag");
			String roleid=(String)this.getFormHM().get("roleid");
			String res_flag=(String)this.getFormHM().get("res_flag");
			if(flag==null|| "".equals(flag))
	            flag=GeneralConstant.ROLE;
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);		
			//parser.addContent(law_dir);
			parser.reSetContent(priv_selected);
			res_str=parser.outResourceContent();
			privbo.saveResourceStringSql(roleid,flag,res_str);
			
			SaveLawResourceTrans slrt = new SaveLawResourceTrans();
			this.getFormHM().put("@eventlog", slrt.getEventLog(roleid, flag, res_type));
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

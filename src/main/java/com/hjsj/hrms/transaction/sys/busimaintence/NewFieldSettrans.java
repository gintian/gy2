package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class NewFieldSettrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String mid=(String)map.get("mid");
			String setdesc="";
			String changeflag=null;
			if(!"35".equalsIgnoreCase(mid)){
				changeflag="0";
			}else if("35".equalsIgnoreCase(mid)){
				changeflag="1";
			}
			String setid="";
			BusiSelStr bss = new BusiSelStr();
			String mname = bss.getMname(this.getFrameconn(), mid);
			//业务字典，薪资分析，新建子集，生成子集代号 jingq upd 2015.01.27
			String userType = SystemConfig.getPropertyValue("dev_flag");
			setid = bss.getSetid(this.getFrameconn(),userType);
			this.getFormHM().put("mid",mid);
			this.getFormHM().put("userType", userType);
			this.getFormHM().put("changeflag",changeflag);
			this.getFormHM().put("setdesc",setdesc);
			this.getFormHM().put("setid",setid);
			this.getFormHM().put("mname",mname);
			this.getFormHM().put("isrefresh", "no");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}

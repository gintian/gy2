package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:业务字典维护(保存新增子集)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 19, 2008:3:57:19 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveBusitableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String userType = (String)this.getFormHM().get("userType");
			if(userType==null||userType=="")
				userType="0";
			String setid = (String)this.getFormHM().get("setid"); //子集代号
			String SetId = setid.toUpperCase();
			String mid = (String)this.getFormHM().get("mid");
			String changeflag = (String)this.getFormHM().get("changeflag"); //类型
			String setdesc = (String)this.getFormHM().get("setdesc");  //名称
			BusiSelStr bss = new BusiSelStr();
			int Sid = bss.initial(mid, this.getFrameconn());
			int Sids = bss.initfield(setid, this.getFrameconn());
			bss.setmuster(userType, SetId, mid, changeflag, setdesc, Sid,Sids,this.getFrameconn());
			this.getFormHM().put("isrefresh", "save");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}

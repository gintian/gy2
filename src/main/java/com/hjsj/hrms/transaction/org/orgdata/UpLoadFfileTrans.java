package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.train.b_plan.CreateFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpLoadFfileTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String titleid = (String)this.getFormHM().get("titleid");
		titleid=titleid!=null&&titleid.trim().length()>0?titleid:"";
		
		String ext = (String)this.getFormHM().get("ext");
		ext=ext!=null&&ext.trim().length()>0?ext:"";
		
		String ole = (String)this.getFormHM().get("ole");
		ole=ole!=null&&ole.trim().length()>0?ole:"";
		
		String sqlstr = (String)this.getFormHM().get("sqlstr");
		sqlstr=sqlstr!=null&&sqlstr.trim().length()>0?sqlstr:"";
		sqlstr=SafeCode.decode(sqlstr);
		
		CreateFileBo cfb = new CreateFileBo(this.getFrameconn());
		String filename = cfb.downFile(this.userView, sqlstr, titleid, ext, ole);
		
		this.getFormHM().put("outName", PubFunc.encrypt(filename));
	}

}

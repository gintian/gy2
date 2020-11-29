package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GzOrgTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		String returnflag=(String)reqhm.get("returnflag"); 
		this.getFormHM().put("returnflag",returnflag);
		String gzflag = (String)reqhm.get("gzflag");
		gzflag=gzflag!=null&&gzflag.trim().length()>0?gzflag:"";
		reqhm.remove("gzflag");
		
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("gzflag",gzflag);
	}

}

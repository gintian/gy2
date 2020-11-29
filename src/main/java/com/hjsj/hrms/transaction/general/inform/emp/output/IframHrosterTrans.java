package com.hjsj.hrms.transaction.general.inform.emp.output;

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
public class IframHrosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		reqhm.remove("dbname");
		this.getFormHM().put("dbname",dbname);
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		this.getFormHM().put("a_code",a_code);
		
		String inforkind = (String)reqhm.get("infor");
		inforkind=inforkind!=null&&inforkind.trim().length()>0?inforkind:"1";
		reqhm.remove("infor");
		this.getFormHM().put("inforkind",inforkind);
		
		String result = (String)reqhm.get("flag");
		result=result!=null&&result.trim().length()>0?result:"0";
		reqhm.remove("flag");
		this.getFormHM().put("result",result);
		
	}
}

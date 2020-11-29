/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 15, 2006:5:16:36 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceFormulaUpdateTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String value = (String)hm.get("value");
		value=SafeCode.decode(value);//dml 2011-03-21
//		try {
//			if(SystemConfig.getPropertyValue("webserver")!=null&&SystemConfig.getPropertyValue("webserver").trim().equalsIgnoreCase("weblogic"))
//				value=new String(value.getBytes(), "gb2312");
//			else
//				value=new String(value.getBytes("ISO8859-1"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
		String rt = "";
		String temp = (String)hm.get("flag");
		if("1".equals(temp)){
			rt = "tb";
		}else{
			rt = "tt_";
		}
		this.getFormHM().put("rt",rt);
		
		if(value == null || "".equals(value)){
			this.getFormHM().put("expid", "");		
			this.getFormHM().put("cname" ,"");
			this.getFormHM().put("lexpr","");
			this.getFormHM().put("rexpr","");
			this.getFormHM().put("colrow","");
			this.getFormHM().put("tabid","");
		}else{
			String[] recorder=value.split("§§");
			String expid = recorder[0];
			String cname = recorder[1];
			String lexpr = recorder[2];
			String rexpr = recorder[3];
			String colrow = recorder[4];
			String tabid = recorder[5];
			
			this.getFormHM().put("expid", expid);		
			this.getFormHM().put("cname" ,cname);
			this.getFormHM().put("lexpr",lexpr);
			this.getFormHM().put("rexpr",rexpr);
			this.getFormHM().put("colrow",colrow);
			this.getFormHM().put("tabid",tabid);
		}
		this.getFormHM().put("returnflag",(String)hm.get("returnflag"));
		this.getFormHM().put("status",(String)hm.get("status"));
	}

}

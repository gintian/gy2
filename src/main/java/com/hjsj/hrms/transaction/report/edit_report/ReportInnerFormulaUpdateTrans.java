/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 15, 2006:4:56:32 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportInnerFormulaUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String value = (String)hm.get("value");
		value=SafeCode.decode(value);//dml 2011-03-21
		value=PubFunc.keyWord_reback(value);
//		try {
//			if(SystemConfig.getPropertyValue("webserver")!=null&&SystemConfig.getPropertyValue("webserver").trim().equalsIgnoreCase("weblogic"))
//				value=new String(value.getBytes(), "gb2312");
//			else//dml 2011-03-21
//				value=new String(value.getBytes("ISO8859-1"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
				
		if(value == null || "".equals(value)){
			this.getFormHM().put("expid", "");		
			this.getFormHM().put("cname" ,"");
			this.getFormHM().put("lexpr","");
			this.getFormHM().put("rexpr","");
			this.getFormHM().put("colrow","");
			this.getFormHM().put("tabid","");
			this.getFormHM().put("npercent","0");
			this.getFormHM().put("excludeexpr","");
		}else{
			//System.out.println(value);
			String[] recorder=value.split("§§");
			String expid = recorder[0];
			String cname = recorder[1];
			String lexpr = recorder[2];
			String rexpr = recorder[3];
			String colrow = recorder[4];
			String tabid = recorder[5];
			String excludeexpr ="";
			this.getFormHM().put("expid", expid);		
			this.getFormHM().put("cname" ,cname);
			
			this.getFormHM().put("rexpr",rexpr);
			this.getFormHM().put("colrow",colrow);
			this.getFormHM().put("tabid",tabid);
		
			TnameBo tbo = new TnameBo(this.getFrameconn(),tabid);
			int npercent=0;
			if("1".equals(colrow)){
				if(lexpr!=null&&lexpr.indexOf("|")!=-1){
					excludeexpr = lexpr.substring(lexpr.indexOf("|")+1,lexpr.length());
					lexpr =  lexpr.substring(0,lexpr.indexOf("|"));
				}
				String num = (String)tbo.getColMap().get(lexpr);
				if(null==num)//xiegh add 20170818 bug:30515
					throw GeneralExceptionHandler.Handle(new Exception("该表达式"+lexpr+"列不存在！"));
				int n = Integer.parseInt(num);
				ArrayList list =tbo.getRowInfoBGrid();
				if(list.size()>=n+1){
					RecordVo vo=(RecordVo)list.get(n);
					npercent = vo.getInt("npercent");
					
				}
				}
				else if("0".equals(colrow)){
					if(lexpr!=null&&lexpr.indexOf("|")!=-1){
						excludeexpr = lexpr.substring(lexpr.indexOf("|")+1,lexpr.length());
						lexpr =  lexpr.substring(0,lexpr.indexOf("|"));
					}
					String num = (String)tbo.getRowMap().get(lexpr);
					if(null==num)
						throw GeneralExceptionHandler.Handle(new Exception("该表达式"+lexpr+"行不存在！"));
					int n = Integer.parseInt(num);
					ArrayList list =tbo.getColInfoBGrid();
					if(list.size()>=n+1){
						RecordVo vo=(RecordVo)list.get(n);
						npercent = vo.getInt("npercent");
					}
					}
			this.getFormHM().put("excludeexpr",excludeexpr);
			this.getFormHM().put("lexpr",lexpr);
			this.getFormHM().put("npercent",""+npercent);	
			//System.out.println(tabid);
		}
		this.getFormHM().put("returnflag",(String)hm.get("returnflag"));
		this.getFormHM().put("status",(String)hm.get("status"));
		
	}

}

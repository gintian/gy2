/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 16, 2006:4:47:29 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportFormulaDelTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		String value = (String)this.getFormHM().get("value");
		if(value == null || "".equals(value)){
			return;
		}
		
	//	System.out.println(value);
		String[] record=value.split("&&");
		for(int i=0;i<record.length;i++){
			String record_str=record[i];
			String[] recorder=record_str.split("§§");
			String eid = recorder[0];
			if(eid == null || "".equals(eid)){
			}else{
				int expid = Integer.parseInt(recorder[0]);	
				String sql = "delete tformula where expid = "+expid;
				this.delFormula(this.getFrameconn(),sql);
			}
		}
		
		this.getFormHM().put("info","ok");
	}
	
	public void delFormula(Connection conn , String sql) throws GeneralException{
		ContentDAO dao=new ContentDAO(conn);
		try{	
			dao.delete(sql,new ArrayList());
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		
	}
	
	

}

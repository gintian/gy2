/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportSpaceCheck;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 3, 2006:1:51:57 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		String unitcodes = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("unitcodes"));
		/*if(unitcodes !=null){
			try {
				byte [] str = unitcodes.getBytes("ISO8859-1");
				unitcodes = new String(str);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}*/
		String tabids = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("tabids"));
		String [] ucs = unitcodes.split(",");
		String [] tbs = tabids.split(",");
		
		StringBuffer reportspacecheckresult = new StringBuffer();
		
		//报表表内校验页面头信息
		reportspacecheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportspacecheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportspacecheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportspacecheckresult.append(ResourceFactory.getProperty("report_collect.reportCollect")+"-"+ResourceFactory.getProperty("report_collect.reportsValidate")+"(");
		reportspacecheckresult.append(this.getCurrentDate());
		reportspacecheckresult.append(")");

		
		
		StringBuffer temp = new StringBuffer();	
		boolean flag = true;
		for(int i = 0 ; i< ucs.length; i++){
			String unitCode = ucs[i];	
			temp.append("<br><br>"+unitCode+":" +this.getUnitName(this.frameconn,unitCode) + ":<br>");
			for(int j =0; j<tbs.length; j++){
				String tabid = tbs[j];		
				ReportSpaceCheck rsc = new ReportSpaceCheck(this.getFrameconn(),tabid ,2,unitCode);
				rsc.setUserView(this.userView);
				String reportCheckValues = rsc.reportSpaceCheckResult();
				if(reportCheckValues == null || "".equals(reportCheckValues) ){
				}else{		
					flag = false;
					temp.append(tabid); //报表表号 tname表中信息
					temp.append("&nbsp;");
					temp.append(this.getTabName(this.frameconn,tabid));  //报表名称
					temp.append("&nbsp;");
					temp.append("<br>");
					temp.append(reportCheckValues); //校验错误信息
				}
			}
			
		}
		
		if(flag == true){
			Exception e = new Exception(ResourceFactory.getProperty("report_collect.reportsValidateSuccess")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}else{
			reportspacecheckresult.append(temp.toString());
		}
		this.getFormHM().put("reportSpaceCheckResult" , reportspacecheckresult.toString());
		
	}
	
	
	public String getUnitName(Connection conn , String unitcode) throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		String unitname = "";
		String sql ="select unitname from tt_organization where unitcode = '"+unitcode+"'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				unitname =this.frowset.getString("unitname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return unitname;
		
	}
	
	public String getTabName(Connection conn ,String tabid)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		String tbname = "";
		String sql =" select name from tname where tabid = " + tabid;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				tbname =this.frowset.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return tbname;
		
	}
	
	public static String getTempString(String temp){
		if(temp == null || "".equals(temp)){
			return null;
		}
		if(temp.charAt(temp.length()-1) == ','){
			return temp.substring(0,temp.length()-1);
		}else{
			return temp;
		}
	}
	
	/**
	 * 获得系统当前时间
	 * @return
	 */
	public String getCurrentDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		return sdf.format(currentTime); 
	}
	
	public static void main(String [] args){
		System.out.println(ReportInnerAnalyseTrans.getTempString("1,2,3,"));
	}
}
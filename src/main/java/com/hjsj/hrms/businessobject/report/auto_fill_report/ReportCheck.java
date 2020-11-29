
/**
 * 
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.List;


/**
 * <p>Title:报表校验:表内/表间校验</p>
 * <p>Description:上报前的校验-操作tb或tt表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2006:5:43:50 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportCheck {
	
	private Connection conn;
	private int  reportFlag;
	private List reportIDList ;
	private String sqlFlag;
	
	//保留
	private String reportCheckErrors;
	private UserView userView;
	

	/**
	 * 上报前校验-表内/表间
	 * @param conn			DB连接
	 * @param reportIDList  报表表号集合
	 * @param reportFlag	报表标识: 1 tb  /  2 tt_
	 * @param userName      用户名 表间校验时判断报表是否构库
	 * @param sqlFlag       用户名或填报单位编码（username / unitcode）
	 */
	public ReportCheck(Connection conn , List reportIDList, int  reportFlag , String sqlFlag){
		this.conn = conn;
		this.reportIDList = reportIDList;
		this.reportFlag = reportFlag;
		this.sqlFlag = sqlFlag;
	}
	
	/**
	 * 上报前的校验
	 * @return 校验是否通过
	 * @throws GeneralException 
	 */
	public boolean reportCheck() throws GeneralException{
		boolean b = true;
		
		for(int i = 0 ; i<reportIDList.size(); i++){
			String tabid = (String)reportIDList.get(i);
			//表内校验
			ReportInnerCheck ric = new ReportInnerCheck(this.conn , tabid, this.reportFlag ,this.sqlFlag);
			String ricmessage = ric.reportInnerCheck();
			
		//	System.out.println("表内校验结果="+ricmessage);
			
			if(ricmessage == null || "".equals(ricmessage) ){
			}else{
				b = false;
				break;
			}
			//表间校验
			ReportSpaceCheck  rsc = new ReportSpaceCheck(this.conn , tabid , this.reportFlag ,this.sqlFlag );
			rsc.setUserView(this.userView);
			String rscmessage = rsc.reportSpaceCheckResult();
			
		//	System.out.println("表间校验结果=" +rscmessage);
			
			if(rscmessage == null || "".equals(rscmessage)){
			}else{
				b = false;
				break;
			}
		}
		//System.out.println("校验结果=" + b);
		return b;
	}
	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

}

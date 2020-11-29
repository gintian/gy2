/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;

/**
 * <p>
 * Title:验证报表是否归档
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Aug 2, 2006:2:22:08 PM
 * </p>
 * 
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportDBCheckTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid = (String) this.getFormHM().get("tabid");
		String unitCode = (String) this.getFormHM().get("unitcode");
		
		//add by wangchaoqun on 2014-10-8 
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		
		String info = "false";
		if (this.isExistTable("ta_", tabid, this.getFrameconn())
				&& this
						.isTa_reportExistDB(tabid, unitCode, this
								.getFrameconn())) {
			info = "true";
		}
		this.getFormHM().put("info", info);
	}

	/**
	 * 判断统计结果表是否存在
	 * 
	 * @param reportPrefix
	 *            报表前缀如tb tt_
	 * @param tabid
	 *            报表ID
	 * @return
	 */
	public boolean isExistTable(String reportPrefix, String tabid,
			Connection conn) {
		boolean b = false;
		DbWizard dbWizard = new DbWizard(conn);
		Table table = new Table(reportPrefix + tabid);
		if (dbWizard.isExistTable(table.getName(),false)) {
			b = true;
		}
		return b;
	}

	/**
	 * 判断报表归档表中是否有数据
	 * 
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	public boolean isTa_reportExistDB(String tabid, String unitCode,
			Connection conn) throws GeneralException {
		boolean b = false;
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		String sql = "select * from ta_" + tabid + " where unitcode = '"
				+ unitCode + "'";
		rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				b = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return b;
	}

}

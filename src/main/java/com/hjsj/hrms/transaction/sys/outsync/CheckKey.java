package com.hjsj.hrms.transaction.sys.outsync;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

public class CheckKey extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String sys_id = (String) this.getFormHM().get("sys_id");
		ContentDAO dao = new ContentDAO(this.frameconn);
		// nbase,nbase_0,A0100,B0110_0,E0122_0,A0101,E01A1_0,flag,sDate,
		// b0110_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,grade,flag,sDate
		String columns = ",nbase,nbase_0,A0100,B0110_0,E0122_0,A0101,E01A1_0,flag,sDate,b0110_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,grade,";
		// String column = new HrSyncBo(this.frameconn).getColumn();
		// String columns = new HrSyncBo(this.frameconn).getOrgColumn();
		// 判断是否为内部代码 表 t_org_view t_hr_view
		if (columns.indexOf("," + sys_id + ",") != -1) {
			this.getFormHM().put("flag", "2");
		} else {
			RowSet re = null;
			try {
				re = dao
						.search("select * from t_sys_outsync where sys_id='"
								+ sys_id + "'");
				if (re.next()) {
					this.getFormHM().put("flag", "0");
				} else {
					this.getFormHM().put("flag", "1");
				}
			} catch (SQLException e) {
				throw new GeneralException(e.getMessage());
			}finally{
				if(re != null)
					try {
						re.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}

}

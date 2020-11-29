/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:查询功能列表
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Oct 29, 2008:3:15:01 PM
 * </p>
 * 
 * @author xgq
 * @version 1.0
 * 
 */
public class OrderStaticStatementTrans extends IBusiness {
	/**
	 */

	public void execute() throws GeneralException {

		ArrayList AllList = new ArrayList();

		String sql = " select * from tscope order by displayid";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);

			while (this.frowset.next()) {
				String scopeid = this.frowset.getString("scopeid");
				String name = this.frowset.getString("name");
				CommonData dataobj = new CommonData(scopeid, name);
				AllList.add(dataobj);
			}
			this.getFormHM().put("sortList", AllList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
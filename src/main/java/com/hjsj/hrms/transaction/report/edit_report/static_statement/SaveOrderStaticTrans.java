/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

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
public class SaveOrderStaticTrans extends IBusiness {
	/**
	 */

	public void execute() throws GeneralException {

		ArrayList alllist = new ArrayList();
		String orderstr = (String)this.getFormHM().get("sorting");
		try {
		if(orderstr!=null&&orderstr.length()>0){
			String sql = " update tscope set displayid=? where scopeid=?";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sorts[] = orderstr.split(",");
			for(int i=0;i<sorts.length;i++){
				ArrayList list = new ArrayList();
				list.add(i+1+"");
				list.add(sorts[i]);
				alllist.add(list);
				
			}
			dao.batchUpdate(sql, alllist);
			
		}
		this.getFormHM().put("info", "ok");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
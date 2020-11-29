package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * <p>
 * Title:DeleteCustomReportTrans
 * </p>
 * <p>
 * Description:删除自定制报表信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-8
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DeleteCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		//获得选择的记录
		ArrayList selectList = (ArrayList) this.getFormHM().get("selectList");
		if (selectList != null && selectList.size() >0) {
			//逐条删除记录
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for (int i = 0; i < selectList.size(); i++) {
				RecordVo vo = (RecordVo) selectList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("delete from t_custom_report where id='");
				sql.append(vo.getString("id"));
				sql.append("'");
				try {
					dao.update(sql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

}

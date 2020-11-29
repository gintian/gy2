package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>
 * Title:ReleaseOrPauseCustomReportTrans
 * </p>
 * <p>
 * Description:更改自定制报表的发布状态
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
public class ReleaseOrPauseCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		//获得选择的记录
		ArrayList selectList = (ArrayList) this.getFormHM().get("selectList");
		//获得需要更改的状态
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String) map.get("flag");
		if (selectList != null && selectList.size() >0) {
			//逐条更改记录的发布状态
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for (int i = 0; i < selectList.size(); i++) {
				RecordVo vo = (RecordVo) selectList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("update t_custom_report set flag='");
				sql.append(flag);
				sql.append("' where id='");
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

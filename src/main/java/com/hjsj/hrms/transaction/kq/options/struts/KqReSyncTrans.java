package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:KqReSyncTrans
 * </p>
 * <p>
 * Description:同步考勤数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-23
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class KqReSyncTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		DocumentSyncBo bo = new DocumentSyncBo(this.frameconn);
		List list = RegisterDate.getKqDayList(this.frameconn);//getTime();
		if (list.size() == 0 || list == null) 
		{
			this.getFormHM().put("err_message", ResourceFactory.getProperty("kq.register.session.nosave"));
			return;
		}
		String start = (String) list.get(0);
		String end = (String) list.get(1);
		bo.sync(start, end);
	}
	
	/**
	 * 查询考勤期间的开始时间和结束时间
	 * @return List<String>
	 */
	public List getTime() {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select min(kq_start) starts,");
		sql.append("min(kq_end) ends from ");
		sql.append("kq_duration where finished=0");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		ResultSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				Date start = rs.getDate("starts");
				Date ends = rs.getDate("ends");
				String st = DateUtils.format(start,"yyyy-MM-dd");
				String en = DateUtils.format(ends, "yyyy-MM-dd");
				list.add(st);
				list.add(en);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
}

package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DelImpEvCommentTrans
 * </p>
 * <p>
 * Description:删除重要信息报告及评论
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class DelImpEvCommentTrans extends IBusiness {


	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String selecteds = (String) hm.get("selecteds");
		selecteds = selecteds != null && selecteds.trim().length() > 0 ? selecteds
				: "";
		hm.remove("selecteds");
		String[] p0600s = selecteds.split(",");
		StringBuffer sql = new StringBuffer();
		try {
			this.getFrameconn().setAutoCommit(false);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("delete from per_keyevent_actor where p0600 in(");
			for (int i = 0; i < p0600s.length; i++) {
				sql.append(PubFunc.decrypt(p0600s[i]) + ",");
			}
			sql.append("0)");
			dao.delete(sql.toString(), new ArrayList());
			dao.delete(sql.toString().replaceAll("per_keyevent_actor", "p06"),
					new ArrayList());
			this.getFrameconn().commit();
		} catch (Exception e) {
			try {
				this.getFrameconn().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				this.getFrameconn().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

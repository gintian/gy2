/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:DeliverImpEvCommentTrans
 * </p>
 * <p>
 * Description:发表重要信息报告评论
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
public class DeliverImpEvCommentTrans extends IBusiness {

	/**
	 * 
	 */
	public DeliverImpEvCommentTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String chflag = (String) hm.get("flag");
		chflag = chflag != null && chflag.trim().length() > 0 ? chflag : "";
		hm.remove("flag");

		String tablename = "per_keyevent_actor";
		String itemid = "p0600";
		if ("1".equals(chflag)) {
			tablename = "per_diary_actor";
			itemid = "p0100";
		}

		String p0600 = (String) this.getFormHM().get("p0600");
		String content = (String) this.getFormHM().get("content");
		content = content != null && content.trim().length() > 0 ? content : "";
		content = content.replaceAll("\\r\\n", "<br/>");
		content = content.replaceAll(" ", "&nbsp;&nbsp;");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		date = Sql_switcher.dateValue(date);
		try {
			String sql = "update " + tablename + " set content='" + content
					+ "',commentary_date=" + date + " where NBASE='"
					+ this.userView.getDbname() + "' and A0100='"
					+ this.userView.getUserId() + "' and " + itemid + "='"
					+ p0600 + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {

		}
	}

}

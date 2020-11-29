/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:ImpEvCommentTrans
 * </p>
 * <p>
 * Description:浏览重要信息报告评论
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
public class ImpEvCommentTrans extends IBusiness {

	/**
	 * 
	 */
	public ImpEvCommentTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String p0600 = (String) hm.get("p0600");
		p0600 = p0600 != null && p0600.trim().length() > 0 ? p0600 : "";
		hm.remove("p0600");
		p0600 = PubFunc.decryption(p0600);
		
		String flag = (String) hm.get("flag");
		flag = flag != null && flag.trim().length() > 0 ? flag : "0";
		hm.remove("flag");
		
		String tablename = "per_keyevent_actor";
		String itemid = "p0600";
		if("1".equals(flag)){
			tablename = "per_diary_actor";
			itemid = "p0100";
		}
		
		List fieldlist = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql = "select b0110,e0122,a0101,commentary_date,content from "+tablename+" where "+itemid+"='"
					+ p0600 + "' order by commentary_date desc";
			this.frowset = dao.search(sql);
			RecordVo vo = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			while (this.frowset.next()) {
				if (this.getFrowset().getString("content") != null
						&& this.getFrowset().getString("content").trim()
								.length() > 0) {
					vo = new RecordVo(tablename);
					sql = "select codeitemdesc from organization where codesetid='UN' and codeitemid='"
							+ this.frowset.getString("b0110") + "'";
					ResultSet rs = dao.search(sql);
					while (rs.next()) {
						vo.setString("b0110", rs.getString("codeitemdesc"));
					}
					sql = "select codeitemdesc from organization where codesetid='UM' and codeitemid='"
							+ this.frowset.getString("e0122") + "'";
					rs = dao.search(sql);
					while (rs.next()) {
						vo.setString("e0122", rs.getString("codeitemdesc"));
					}
					vo.setString("a0101", this.frowset.getString("a0101"));
					vo.setString("commentary_date", sdf.format(this.frowset.getTimestamp("commentary_date")));
					vo.setString("content", this.frowset.getString("content"));
					fieldlist.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("fieldlist", fieldlist);
		}
	}

}

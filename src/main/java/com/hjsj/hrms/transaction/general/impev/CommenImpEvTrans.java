/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
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
 * Title:CommenImpEvTrans
 * </p>
 * <p>
 * Description:评论重要信息报告
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
public class CommenImpEvTrans extends IBusiness {

	/**
	 * 
	 */
	public CommenImpEvTrans() {
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
		p0600 = PubFunc.decrypt(p0600);
		hm.remove("p0600");
		
		String chflag = (String) hm.get("flag");
		chflag = chflag != null && chflag.trim().length() > 0 ? chflag : "0";
		hm.remove("flag");
		
		String tablename = "per_keyevent_actor";
		String itemid = "p0600";
		if("1".equals(chflag)){
			tablename = "per_diary_actor";
			itemid = "p0100";
		}
		
		String a_code = (String) hm.get("a_code");
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		hm.remove("a_code");
		List fieldlist = new ArrayList();
		String content = "";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("0".equals(chflag)){
				String sql = "update p06 set p0611="
					+ Sql_switcher.sqlNull("p0611", 0) + "+1 where p0600='"
					+ p0600 + "'";
				dao.update(sql);// 浏览次数增加一
			}
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
					vo.setString("content", this.frowset.getString("content"));
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
					fieldlist.add(vo);
				}
			}
			sql = "select content from "+tablename+" where "+itemid+"='"
					+ p0600 + "' and A0100='" + this.userView.getUserId() + "'";
			this.frowset = dao.search(sql);
			boolean flag = true;
			while (this.frowset.next()) {
				flag = false;
				content = this.frowset.getString("content");
			}
			if (true == flag) {// 如果没有此人的评论项，则插入一条没有评论的评论项
				String state= "per_diary_actor".equals(tablename)?"state,":"";
				String stateValue= "per_diary_actor".equals(tablename)?"0,":"";
				sql = "insert into "+tablename+" ("+state+"NBASE,A0100,"+itemid+",B0110,E0122,E01A1,A0101) values("+stateValue+"'"+this.userView.getDbname()+"','"
						+ this.userView.getUserId()
						+ "','"
						+ p0600
						+ "','"
						+ this.userView.getUserOrgId()
						+ "','"
						+ this.userView.getUserDeptId()
						+ "','"
						+ this.userView.getUserPosId()
						+ "','";
						if(this.userView.isAdmin()){
							sql+=this.userView.getUserName() + "')";;
						}else{
							sql+=this.userView.getUserFullName() + "')";
						}	
				dao.insert(sql, new ArrayList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			if (content != null) {
				content = content.replaceAll("<br/>", "\\\r\\\n");
				content = content.replaceAll("&nbsp;&nbsp;", " ");
			}
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("p0600", p0600);
			this.getFormHM().put("content", content);
			this.getFormHM().put("flag", chflag);
		}
	}
}

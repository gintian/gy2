/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:ChaoSongTrans
 * </p>
 * <p>
 * Description:抄送重要信息报告
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
public class ChaoSongTrans extends IBusiness {
	public void execute() throws GeneralException {
		String personstr = (String) this.getFormHM().get("personstr");
		personstr = personstr != null && personstr.trim().length() > 0 ? personstr
				: "";
		String selecteds = (String) this.getFormHM().get("selecteds");
		selecteds = selecteds != null && selecteds.trim().length() > 0 ? selecteds
				: "";
		String flag = (String) this.getFormHM().get("flag");
		flag = flag != null && flag.trim().length() > 0 ? flag : "";
		StringBuffer result = new StringBuffer();
		try {
			// 可能有未提交项，要操作到两个表，需要开启事务
			try {
				this.getFrameconn().setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if (!"".equals(flag)) {
				String[] notSubmit = flag.split(",");
				StringBuffer sb = new StringBuffer();
				sb.append("update p06 set p0609='1' where p0600 in(");
				for (int i = 0; i < notSubmit.length; i++) {
					sb.append(notSubmit[i] + ",");
				}
				sb.append("0)");
				dao.update(sb.toString());
			}
			String[] p0600s = selecteds.split(",");
			String[] personarr = personstr.split("`");
			List personlist = new ArrayList();
			for (int i = 0; i < personarr.length; i++) {
				String person = personarr[i];
				Map voPerson = new HashMap();
				if (person != null && person.length() > 3) {
					String sql = "select B0110,E0122,E01A1,A0101 from "+person.substring(0,3)+"A01 where A0100='"
						+ person.substring(3) + "'";
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						voPerson.put("DBNAME", person.substring(0,3));
						voPerson.put("A0100", person.substring(3));
						voPerson.put("B0110", this.frowset
								.getString("B0110"));
						voPerson.put("E0122", this.frowset
								.getString("E0122"));
						voPerson.put("E01A1", this.frowset
								.getString("E01A1"));
						voPerson.put("A0101", this.frowset
								.getString("A0101"));
						personlist.add(voPerson);
					}
				}
			}
			Map voPerson = null;
			String sql = "delete from per_keyevent_actor where P0600=?";
			ArrayList pelist = new ArrayList();
			for (int n = 0; n < p0600s.length; n++) {
				ArrayList listvalue = new ArrayList();
				listvalue.add(p0600s[n]);
				pelist.add(listvalue);
			}
			dao.batchUpdate(sql, pelist);

			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("insert into per_keyevent_actor (NBASE,A0100,P0600,B0110,E0122,E01A1,A0101) values(");
			sqlstr.append("?,?,?,?,?,?,?)");
			ArrayList perlist = new ArrayList();
			for (int n = 0; n < p0600s.length; n++) {
				if(p0600s[n]!=null&&p0600s[n].trim().length()>0){
					for (int i = 0; i < personlist.size(); i++) {
						voPerson = (Map) personlist.get(i);
						ArrayList listvalue = new ArrayList();
						listvalue.add((String)voPerson.get("DBNAME"));
						listvalue.add((String)voPerson.get("A0100"));
						listvalue.add(p0600s[n]);
						listvalue.add((String)voPerson.get("B0110"));
						listvalue.add((String)voPerson.get("E0122"));
						listvalue.add((String)voPerson.get("E01A1"));
						listvalue.add((String)voPerson.get("A0101"));

						perlist.add(listvalue);
					}
				}
			}
			dao.batchInsert(sqlstr.toString(), perlist);
			result.append("success");
		} catch (Exception e) {
			e.printStackTrace();
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
			this.getFormHM().put("result", result.toString());
		}
	}
}

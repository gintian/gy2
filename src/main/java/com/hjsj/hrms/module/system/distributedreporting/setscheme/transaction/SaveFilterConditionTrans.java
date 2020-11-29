package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 指标对应第一步——保存过滤条件功能的回显与保存
 * @author caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class SaveFilterConditionTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = null;
		RowSet rs = null;
		RowSet rs2 = null;
		try {
			String type = (String) this.getFormHM().get("type"); // 操作
			String setid = (String) this.getFormHM().get("setid"); // 子集编码
			String unitcodeid = (String) this.getFormHM().get("unitcodeid"); // 过滤条件
			ArrayList list = new ArrayList();
			dao = new ContentDAO(this.getFrameconn());
			if ("open".equals(type)) {// 打开过滤条件窗口
				list.add(setid);
				String express = "";
				rs = dao.search("SELECT c_expr FROM t_sys_asyn_filtercondition WHERE setid=?", list);
				if(rs.next()) {
					express = rs.getString("c_expr");
				}
				this.getFormHM().put("express", express);
			} else if ("save".equals(type)) {// 保存过滤条件窗口
				String c_expr = (String) this.getFormHM().get("c_expr"); // 过滤条件
				c_expr = SafeCode.decode(c_expr);
				list.add(setid);
				rs = dao.search("select * from t_sys_asyn_filtercondition where setid=?", list);
				list = new ArrayList<String>();
				if(rs.next()) {
					list.add(c_expr);
					list.add(setid);
					dao.update("update t_sys_asyn_filtercondition set c_expr=? where setid=?", list);
				}else {
					String desc = (String) this.getFormHM().get("desc"); // 子集名称
					int id = 0;
					String sql = "SELECT "+Sql_switcher.isnull("max(id)","0")+" as maxid from t_sys_asyn_filtercondition";
					rs2 = dao.search(sql);
					if(rs2.next()) {
						id = rs2.getInt("maxid")+1;
					}
					list.add(id);
					list.add(setid);
					list.add(desc);
					list.add(c_expr);
					list.add(unitcodeid);
					dao.insert("INSERT INTO t_sys_asyn_filtercondition (id,setid,setdesc,c_expr,unitcode) VALUES (?,?,?,?,?)", list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs2);
			PubFunc.closeResource(rs);
		}
	}

}

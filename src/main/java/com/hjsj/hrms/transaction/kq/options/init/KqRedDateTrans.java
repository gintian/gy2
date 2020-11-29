package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * <p>Title:KqRedDateTrans.java</p>
 * <p>Description>:KqRedDateTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 30, 2011 11:09:50 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class KqRedDateTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String out = (String) this.getFormHM().get("out");// 公出
		String otime = (String) this.getFormHM().get("outime");// 加班
		String rest = (String) this.getFormHM().get("rest");// 休息
		String q19 = (String) this.getFormHM().get("q19");// 调班申请表
		String q21 = (String) this.getFormHM().get("q21");// 替班申请表
		String txsq = (String) this.getFormHM().get("txsq");// 调休申请表
		String staffl = (String) this.getFormHM().get("staffl");// 员工日明细
		String staffy = (String) this.getFormHM().get("staffy");// 员工月汇总
		String shift = (String) this.getFormHM().get("shift");// 员工排班信息表
		String ypsk = (String) this.getFormHM().get("ypsk");// 员工刷卡信息表
		String bzry = (String) this.getFormHM().get("bzry");// 班组人员
		String jqgl = (String) this.getFormHM().get("jqgl");// 假期信息表
		String scope = (String) this.getFormHM().get("scope");// 时间范围标记
		String tstart = (String) this.getFormHM().get("count_start");// 开始时间
		String tend = (String) this.getFormHM().get("count_end"); // 结束时间
		String group = (String)this.getFormHM().get("group");//考勤班组

		// String deptl = (String) this.getFormHM().get("deptl");// 部门日明细
		// String depty = (String) this.getFormHM().get("depty");// 部门月汇总
		// String kqorg=(String)this.getFormHM().get("kqorg");//单位部门排班表
		KqUtilsClass kc = new KqUtilsClass();
		String table = null;
		if (!isEmpty(out)) {// 公出
			table = "Q13";
			String where = kc.procWhere(tstart, "Q13Z1", tend, "Q13Z3", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(otime)) {// 加班
			table = "Q11";
			String where = kc.procWhere(tstart, "Q11Z1", tend, "Q11Z3", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(rest)) {// 休息
			table = "Q15";
			String where = kc.procWhere(tstart, "Q15Z1", tend, "Q15Z3", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(q19)) {// 调班申请表
			table = "Q19";
			String where = kc.procWhere(tstart, "Q1905", tend, "Q1905", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(q21)) {// 替班申请表
			table = "Q21";
			String where = kc.procWhere(tstart, "Q21Z1", tend, "Q21Z3", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(txsq)) {// 调休申请表
			table = "Q25";
			String where = kc.procWhere(tstart, "Q2505", tend, "Q2505", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(staffl)) {// 员工日明细
			table = "Q03";
			String where = kc.procWhere(tstart, "Q03Z0", tend, "Q03Z0", 4);
			exeOp(table, where, scope);
		}
		if (!isEmpty(staffy)) {// 员工月汇总
			table = "Q05";
			String where = kc.procWhere(tstart, "Q03Z0", tend, "Q03Z0", 3);
			exeOp(table, where, scope);
		}
		if (!isEmpty(bzry)) {// 班组人员
			table = "kq_group_emp";
//			String where = procWhere(tstart, "Q03Z0", tend, "Q03Z0", 2);
			exeOp(table, null, "1");
		}
		if (!isEmpty(jqgl)) {// 假期信息表
			table = "Q17";
			String where = kc.procWhere(tstart, "Q17Z1", tend, "Q17Z3", 1);
			exeOp(table, where, scope);
		}
		if (!isEmpty(shift)) {// 员工排班信息表
			table = "kq_employ_shift";
			String where = kc.procWhere(tstart, "Q03Z0", tend, "Q03Z0", 4);
			exeOp(table, where, scope);
		}
		if (!isEmpty(ypsk)) {// 员工刷卡信息表
			table = "kq_originality_data";
			String where = kc.procWhere(tstart, "Work_date", tend, "Work_date", 4);
			exeOp(table, where, scope);
		}
		if (!isEmpty(group)) 
		{
			table = "kq_shift_group";
			exeOp(table);
		}
		this.getFormHM().put("mess", "2");

	}

	/**
	 * 删除表 “table” 不存在人员信息
	 * @param table
	 * @param where
	 * @param scope
	 * @return
	 * @throws GeneralException
	 */
	private boolean exeOp(String table, String where, String scope) throws GeneralException {
		List nbaselist = this.userView.getPrivDbList();
		if (nbaselist == null) {
			return true;
		} 
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM " + table + " WHERE");
		Iterator it = nbaselist.iterator();
		int i = 1;
		while(it.hasNext()){
			String nbase = (String)it.next();
			String subT = nbase + "A01";
			sql.append(" NOT EXISTS(SELECT 1 FROM " + subT
					+ " WHERE " + subT + ".A0100=" + table + ".A0100 AND UPPER(nbase)='" + nbase.toUpperCase()
					+ "') AND");
			i++;
		}
		if ("1".equals(scope)) {
			sql.delete(sql.length() - 4, sql.length());
		} else {
			sql.append(" " + where);
		}
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.delete(sql.toString(), null);
		} catch (SQLException e) {
			e.printStackTrace();
			this.getFormHM().put("mess", "3");
			throw new GeneralException(e.getMessage());
		}
		return true;
	}



	/**
	 * 判断字符串是否为空
	 * 
	 * @param pramt
	 * @return 结果为 null 时 返回 true 反之 为 false
	 */
	private boolean isEmpty(String pramt) {
		if (pramt == null || pramt.trim().length() <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 班组冗余数据处理,例如班组所属机构已不存在
	 * @param table
	 * @return
	 * @throws GeneralException 
	 */
	private boolean exeOp(String table) throws GeneralException{
		StringBuffer sb = new StringBuffer();
		sb.append("update kq_shift_group set org_id = 'UN'");
		sb.append(" where group_id in (");
		sb.append("select group_id from kq_shift_group");
		sb.append(" where org_id <> 'UN' and not exists");
		sb.append(" (select 1 from organization where organization.codeitemid = " +
				Sql_switcher.substr("kq_shift_group.org_id","3",Sql_switcher.length("kq_shift_group.org_id")) + ")");
		sb.append(")");
		ContentDAO dao = new ContentDAO(frameconn);
		try {
			dao.update(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			this.getFormHM().put("mess", "3");
			throw new GeneralException(e.getMessage());
		}
		return true;
	}

}

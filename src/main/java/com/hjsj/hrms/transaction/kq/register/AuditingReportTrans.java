package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class AuditingReportTrans extends IBusiness {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	/**
	 * 考勤审核考勤纪录，经理权限，把报审改为报批
	 */
	public void execute() throws GeneralException {

		ArrayList kqDate_list = new ArrayList();
		String kq_duration = (String) this.getFormHM().get("kq_duration");
		String select_pre = (String) this.getFormHM().get("select_pre");
		ArrayList kq_dbase_list = new ArrayList();
		if (select_pre != null && !"all".equalsIgnoreCase(select_pre))
			kq_dbase_list.add(select_pre);
		else
			kq_dbase_list = userView.getPrivDbList();
		if (kq_duration != null && kq_duration.length() > 0) {
			kqDate_list = RegisterDate.getKqDate(this.getFrameconn(),
					kq_duration);
		} else {
			kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
			kqDate_list = RegisterDate.getKqDayList(this.getFrameconn());
		}
		String overrule = (String) this.getFormHM().get("overrule");
		/*
		 * if(overrule==null||overrule.length()<=0)
		 * overrule=this.userView.getUserFullName(); else
		 * overrule=this.userView.getUserFullName()+":"+overrule;
		 */
		String start_date = kqDate_list.get(0).toString();
		String end_date = kqDate_list.get(1).toString();
		boolean isCorrect = true;
		// 考勤部门
        String field = KqParam.getInstance().getKqDepartment();
        //考勤管理范围机构编码
        String kqDeptCode = RegisterInitInfoData.getKqPrivCodeValue(userView);
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String nbase = kq_dbase_list.get(i).toString();
			String whereIN = RegisterInitInfoData.getWhereINSql(this.userView,
					nbase);
			
			if (!userView.isSuper_admin()) {
				if (whereIN.indexOf("WHERE") != -1) {
	                whereIN = whereIN.replace("WHERE", "WHERE (");
	                if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
	                    whereIN += " OR " + nbase + "A01." + field + " like '"
	                            + kqDeptCode + "%'";
	                whereIN += ")";
				}
				String whereB0110 = RegisterInitInfoData.selcet_OrgId(nbase,
						"b0110", whereIN);
				ArrayList orgidb0110List = OrgRegister.getQrgE0122List(this
						.getFrameconn(), whereB0110, "b0110");
				try {
					for (int s = 0; s < orgidb0110List.size(); s++) {
						String b0110_one = orgidb0110List.get(s).toString();

						updateSumSql(whereIN, nbase, b0110_one, kq_duration,
								overrule);
						
						StringBuffer update_Q03 = new StringBuffer();
						update_Q03.append("update Q03 set q03z5=? ");
						update_Q03.append("where nbase=?  and q03z5=? ");
						update_Q03.append(" and b0110=? ");
						update_Q03.append(" and q03z0>=? and q03z0<=? ");
						update_Q03.append(" and a0100 in(select a0100 "
								+ whereIN + ")");
						ArrayList Q03_list = new ArrayList();
						Q03_list.add("02");
						Q03_list.add(nbase);
						Q03_list.add("08");
						Q03_list.add(b0110_one);
						Q03_list.add(start_date);
						Q03_list.add(end_date);
						ContentDAO dao = new ContentDAO(this.getFrameconn());
						dao.update(update_Q03.toString(), Q03_list);

					}
				} catch (Exception e) {
					isCorrect = false;
					e.printStackTrace();
				}
			} else {
				try {
					ArrayList b0100list = RegisterInitInfoData.getAllBaseOrgid(
							nbase, "b0110", whereIN, this.getFrameconn());
					for (int t = 0; t < b0100list.size(); t++) {
						String b0110_one = b0100list.get(t).toString();
						updateSumSql(whereIN, nbase, b0110_one, kq_duration,
								overrule);
						StringBuffer update_Q03 = new StringBuffer();
						update_Q03.append("update Q03 set q03z5=? ");
						update_Q03.append(" where nbase=?  and q03z5=? ");
						update_Q03.append(" and b0110=? ");
						update_Q03.append(" and q03z0>=? and q03z0<=? ");
						update_Q03.append(" and a0100 in(select a0100 "
								+ whereIN + ")");
						ArrayList Q03_list = new ArrayList();
						Q03_list.add("02");
						Q03_list.add(nbase);
						Q03_list.add("08");
						Q03_list.add(b0110_one);
						Q03_list.add(start_date);
						Q03_list.add(end_date);
						ContentDAO dao = new ContentDAO(this.getFrameconn());
						dao.update(update_Q03.toString(), Q03_list);

					}
				} catch (Exception e) {
					isCorrect = false;
					e.printStackTrace();
				}

			}
		}
		if (isCorrect) {
			this.getFormHM().put("sp_result", "数据审核成功！");
		} else {
			this.getFormHM().put("sp_result", "数据审核失败！");
		}
		this.getFormHM().put("re_url",
				"/kq/register/audit_registerdata.do?b_query=link");
	}

	/***************************************************************************
	 * @param whereIN
	 *            select in子句
	 * @param tablename
	 *            表名
	 * @return 返回？号的update的SQL语句
	 * 
	 **************************************************************************/
	public boolean updateSumSql(String whereIN, String dbase, String code,
			String kq_duration, String overrule) throws GeneralException {
		boolean isCorrect = false;
		String overrule_value = Sql_switcher.numberToChar(Sql_switcher.isnull(
				"overrule", "''"));
		String result = RegisterInitInfoData.getResult();
		RegisterInitInfoData registerInitInfoData = new RegisterInitInfoData();
		overrule = registerInitInfoData.getOverruleFormat(overrule, "02",
				this.userView.getUserFullName());
		StringBuffer sql = new StringBuffer();
		sql.append("select overrule,a0100 from q05 where ");
		sql.append(" nbase='" + dbase + "' ");
		sql.append(" and b0110 = '" + code + "'");
		sql.append(" and Q03Z0 ='" + kq_duration + "'");
		sql.append(" and a0100 in(select a0100 " + whereIN + ")");
		sql.append(" and Q03Z5='08'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			ArrayList list = new ArrayList();
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update Q05 set ");
			updatesql.append(" Q03Z5=?,overrule=? where ");
			updatesql.append(" nbase=? ");
			updatesql.append(" and Q03Z0 =? ");
			updatesql.append(" and a0100=? ");
			updatesql.append(" and q03z5 ='08'");
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				String oldover = Sql_switcher
						.readMemo(this.frowset, "overrule");
				ArrayList u_list = new ArrayList();
				u_list.add("02");
				u_list.add(overrule + oldover);
				u_list.add(dbase);
				u_list.add(kq_duration);
				u_list.add(this.frowset.getString("a0100"));
				list.add(u_list);
			}
			dao.batchUpdate(updatesql.toString(), list);
			isCorrect = true;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return isCorrect;
	}
}

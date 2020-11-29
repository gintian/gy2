package com.hjsj.hrms.transaction.kq.register.batch;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 月汇总批量修改保存
 * 
 * @author Owner wangyao
 */
public class YueBatchSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			// String code = (String) this.getFormHM().get("code"); //ID
			// ArrayList
			// kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
			// //全部的库
			String settype = (String) hm.get("settype"); // 指标ID
			String value = (String) hm.get("value");// 值
			// if(kq_dbase_list==null||kq_dbase_list.size()<=0)
			// {
			// kq_dbase_list=kq_dbase_list=userView.getPrivDbList();
			// }
			// String showtype = (String) this.getFormHM().get("showtype");
			// //审批状态
			// if(showtype==null||showtype.length()<=0)
			// {
			// showtype="all";
			// }
			// String kq_duration
			// =RegisterDate.getKqDuration(this.getFrameconn()); //当前年月
			// String select_pre=(String)this.getFormHM().get("select_pre");
			// String kind = (String)this.getFormHM().get("kind");
			// ArrayList sql_db_list=new ArrayList();
			// if(select_pre!=null&&select_pre.length()>0&&!select_pre.equals("all"))
			// {
			// sql_db_list.add(select_pre);
			// }else
			// {
			// sql_db_list=kq_dbase_list;
			// }
			// getsql(settype,value,sql_db_list,kq_duration,code,kind,"Q05",this.userView,showtype);
			String sql_where = (String) this.getFormHM().get("strwhere");
			StringBuffer condition = new StringBuffer();
			String usernanme = this.userView.getUserName();
			condition.append("update Q05 set " + settype + " = '" + value
					+ "',modusername='" + usernanme + "',modtime="
					+ Sql_switcher.sqlNow() + " where ");
			condition
					.append("exists(SELECT 1 FROM (SELECT A0100,nbase,q03z0 "
							+ sql_where
							+ ") Q1 WHERE Q05.nbase=Q1.nbase AND Q05.A0100=Q1.A0100 AND Q05.q03z0=Q1.q03z0)");
			condition.append(" AND q03z5 IN('01', '07')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.update(condition.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("settype", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getsql(String settype, String value, ArrayList kq_dbase_list,
			String kq_duration, String code, String kind, String tablename,
			UserView userView, String showtype) {
		StringBuffer condition = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String usernanme = this.userView.getUserName();
			condition.append("update " + tablename + " set " + settype + " = '"
					+ value + "',modusername='" + usernanme + "',modtime="
					+ Sql_switcher.sqlNow() + " where ");
			condition.append("  Q03Z0 = '" + kq_duration + "'");
			if (code == null || code.length() <= 0) {
				code = RegisterInitInfoData.getKqPrivCodeValue(userView);
			}
			if ("1".equals(kind)) {
				condition.append(" and e0122 like '" + code + "%'");
			} else if ("0".equals(kind)) {
				condition.append(" and e01a1 like '" + code + "%'");
			} else {
				condition.append(" and b0110 like '" + code + "%'");
			}
			if (!"all".equals(showtype)) {
				condition.append(" and q03z5='" + showtype + "'");
			}
			condition.append(" and q03z5<>'08' ");
			// if(where_c!=null&&where_c.length()>0)
			// condition.append(" "+where_c+"");
			for (int i = 0; i < kq_dbase_list.size(); i++) {
				String userbase = kq_dbase_list.get(i).toString();
				String whereIN = RegisterInitInfoData.getWhereINSql(userView,
						userbase);
				if (i > 0) {
					condition.append(" or ");
					condition.append(" (UPPER(nbase)='"
							+ kq_dbase_list.get(i).toString().toUpperCase()
							+ "'");
					condition.append(" and a0100 in(select a0100 " + whereIN
							+ ") ");
					condition.append(")");
				} else {
					condition.append(" and ( ");
					condition.append(" (UPPER(nbase)='"
							+ kq_dbase_list.get(i).toString().toUpperCase()
							+ "'");
					condition.append(" and a0100 in(select a0100 " + whereIN
							+ ") ");
					condition.append("))");
				}
				// 原始，这会报一个缺少）；是因为如果走 and 的时候增加了一个（
				// condition.append("
				// (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
				// condition.append(" and a0100 in(select a0100 "+whereIN+") ");
				// condition.append(")");
				// if(i==kq_dbase_list.size()-1)
				// condition.append(")");
				// System.out.println("############> = "+condition.toString());
				dao.update(condition.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

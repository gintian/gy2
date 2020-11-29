package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EmptyKQCardTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String Msg = "";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String empOrUn = (String) hm.get("empOrUn");
		String nbase = (String) this.getFormHM().get("select_pre");
		ArrayList infoList = (ArrayList) this.getFormHM().get(
				"selectedinfolist");
		String code = (String) this.getFormHM().get("kq_code");
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap Kq_Paramter = para.getKqParamterMap();
		String kq_cardno = (String) Kq_Paramter.get("cardno");
		if (nbase == null && nbase.length() < 1) {
			// Msg = "请选择人员库！";
			throw new GeneralException(ResourceFactory
					.getProperty("org.autostatic.mainp.select.loibrary.staff") + "!");
		}

		if ("2".equals(empOrUn) && (infoList == null || infoList.isEmpty())) {
			// Msg = "请选择人员！";
			throw new GeneralException(ResourceFactory
					.getProperty("general.select.emp"));
		}
		//2014.10.28 xiexd回收卡号与日明细月汇总已批状态没有直接的关系
		//Msg = check();
		//if (Msg.length() > 0) {
		//	throw new GeneralException(Msg);
		//}

		if ("1".equals(empOrUn)) {
			if (emptyUn(nbase, code, kq_cardno)) {
				Msg = ResourceFactory.getProperty("label.common.success") + "!";
			} else {
				Msg = ResourceFactory.getProperty("kq.machine.error");
				throw new GeneralException(Msg);
			}
		} else {
			if (emptyEmp(infoList, kq_cardno)) {
				Msg = ResourceFactory.getProperty("label.common.success") + "!";
			} else {
				Msg = ResourceFactory.getProperty("kq.machine.error");
				throw new GeneralException(Msg);
			}
		}

		if (code != null && code.length() > 0) {
			String kind = code.substring(0, 2);
			String a_code = code.substring(2);
			hm.put("code", a_code);
			if ("@K".equalsIgnoreCase(kind)) {
                kind = "0";
            } else if ("UM".equalsIgnoreCase(kind)) {
                kind = "1";
            } else {
                kind = "2";
            }
			hm.put("kind", kind);
		}
	}

	private boolean emptyEmp(ArrayList persList, String kq_cardno) {

		if (persList == null || persList.size() <= 0)
			return true;

		ContentDAO dao = new ContentDAO(this.frameconn);
		for (int i = 0; i < persList.size(); i++) {
			LazyDynaBean bean = (LazyDynaBean) persList.get(i);
			String nbase = (String) bean.get("nbase");
			String a0100 = (String) bean.get("a0100");

			String whereSql = " WHERE a0100 = '" + a0100 + "'";
			StringBuffer sqlBuffer = new StringBuffer();
			try {
				// 删除正在使用的考勤考号
				String sql = "DELETE FROM kq_cards WHERE status<>'-1'";
				dao.delete(sql, new ArrayList());
				// 将回收的考勤卡号状态赋值为-1
				sqlBuffer.append(" INSERT INTO kq_cards(card_no,status)");
				sqlBuffer.append(" SELECT " + kq_cardno + ",-1 FROM " + nbase + "A01 ");
				sqlBuffer.append(whereSql);
				sqlBuffer.append(" AND " + Sql_switcher.isnull(kq_cardno, "0") + "<> '0'");
				sqlBuffer.append(" AND NOT EXISTS(SELECT 1 FROM kq_cards WHERE card_no = " + kq_cardno + ")");
				sqlBuffer.append(" GROUP BY " + kq_cardno);
				dao.insert(sqlBuffer.toString(), new ArrayList());

				// 将 A01 表中的考勤卡号更新为空
				sql = "UPDATE " + nbase + "A01 SET " + kq_cardno + " = NULL" + whereSql;
				dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private boolean emptyUn(String nbases, String b0110, String kq_cardno) {
		String kq_b0110 = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
		if (b0110.length() < kq_b0110.length()) {
			b0110 = kq_b0110;
		}
		String kind = b0110.substring(0, 2);
		b0110 = b0110.substring(2);
		ArrayList dblist = null;
		try {
			if ("All".equalsIgnoreCase(nbases)) {
				dblist = RegisterInitInfoData.getDbList(b0110, kind, this.getFormHM(), this.userView, this.getFrameconn());
			} else {
				dblist = new ArrayList();
				if (nbases != null && nbases.length() > 0)
					dblist.add(nbases);
			}
		
			ContentDAO dao = new ContentDAO(this.frameconn);
			// 删除正在使用的考勤卡号
			String sql = "DELETE FROM kq_cards WHERE status<>'-1'";
			dao.delete(sql, new ArrayList());
			
			String whereSql = null;
			if (b0110 == null || b0110.length() < 1) {
				whereSql = " WHERE 1=1";
			} else if ("UN".equals(kind)) {
				whereSql = " WHERE B0110 LIKE '" + b0110 + "%'";
			} else if ("UM".equals(kind)) {
				whereSql = " WHERE E0122 LIKE '" + b0110 + "%'";
			} else if ("@K".equals(kind)) {
				whereSql = " WHERE E01A1 LIKE '" + b0110 + "%'";
			}
			
			String sqlKq_cards="";
			
			Iterator it = dblist.iterator();
			while (it.hasNext()) {
				StringBuffer sqlBuffer=new StringBuffer();
				String nbase = (String) it.next();
				sqlKq_cards=" AND A0100 IN (SELECT A0100 " + RegisterInitInfoData.getWhereINSql(this.userView,nbase) + ")";
				// 将回收的考勤卡号状态赋值为-1
				sqlBuffer.append(" INSERT INTO kq_cards(card_no,status)");
				sqlBuffer.append(" SELECT " + kq_cardno + ",-1");
				sqlBuffer.append(" FROM " + nbase + "A01 ");
				sqlBuffer.append(whereSql);
				sqlBuffer.append(sqlKq_cards);
				sqlBuffer.append(" AND " + Sql_switcher.isnull(kq_cardno, "0") + "<> '0'");
				sqlBuffer.append(" AND NOT EXISTS(SELECT 1 FROM kq_cards WHERE card_no = " + kq_cardno + ")");
				sqlBuffer.append(" GROUP BY " + kq_cardno);
				dao.insert(sqlBuffer.toString(), new ArrayList());

				// 将  A01 表中的考勤卡号更新为空
				sql = "UPDATE " + nbase + "A01 SET " + kq_cardno + " = NULL" + whereSql+sqlKq_cards;
				dao.update(sql);
			}
		} catch (GeneralException e1) {
			e1.printStackTrace();
			return false;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}

	private String check() {
		Map kq_info = KqUtilsClass.getCurrKqInfo();
		String date_whereSql = "";
		String month_whereSql = "";
		if (kq_info != null && !kq_info.isEmpty()) {
			String kq_year = (String) kq_info.get("kq_year");
			String kq_month = (String) kq_info.get("kq_month");
			String startDate = (String) kq_info.get("kq_start");
			String endDate = (String) kq_info.get("kq_end");
			if (startDate != null && startDate.length() > 0 && endDate != null
					&& endDate.length() > 0) {
				date_whereSql = " AND Q03Z0 BETWEEN '" + startDate + "' AND '"
						+ endDate + "'";
			}
			
			if (kq_year != null && kq_year.length() > 0) {
				month_whereSql = " AND Q03Z0 = '" + kq_year + "." + kq_month
						+ "'";
			}
		}

		ContentDAO dao = new ContentDAO(this.frameconn);
		String errMsg = "";
		try {
			//检查日明细表当前期间是否有未批数据
			errMsg = checkKqData(dao, "q03", date_whereSql);
			
			//检查月汇总表当前期间是否有未批数据
			if (null == errMsg || "".equals(errMsg))
				errMsg = checkKqData(dao, "q05", month_whereSql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return errMsg;
	}

	private String checkKqData(ContentDAO dao, String tab, String whereSql)
			throws SQLException {
		String errMsg = "";
		String sql = "";
		
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL:
			sql = "SELECT TOP 1 1 FROM " + tab + " WHERE Q03Z5<>'03'" + whereSql;
			break;
		case Constant.ORACEL:
			sql = "SELECT 1 FROM " + tab + " WHERE Q03Z5<>'03'" + whereSql + " AND ROWNUM <= 1";
			break;
		default:
			sql = "SELECT 1 FROM " + tab + " WHERE Q03Z5<>'03'" + whereSql + " LIMIT 1";
		}
		this.frowset = dao.search(sql);
		if (this.frowset.next()) {
			// errMsg = "人员考勤明细表数据还没有通过审核，请审核后再清空人员考勤卡s！";
			if ("q03".equalsIgnoreCase(tab))
			    errMsg = ResourceFactory.getProperty("kq.card.day.err");
			else if ("q05".equalsIgnoreCase(tab))
				errMsg = ResourceFactory.getProperty("kq.card.month.err");
		}
		
		return errMsg;
	}
}

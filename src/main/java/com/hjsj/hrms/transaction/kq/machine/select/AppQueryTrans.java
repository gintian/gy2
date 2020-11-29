package com.hjsj.hrms.transaction.kq.machine.select;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AppQueryTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{

		ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");        
        String like=(String)this.getFormHM().get("like");
        this.getFormHM().put("sql_where", getWhere(factorlist,like));
	}
	private String getWhere(ArrayList factorlist, String like) {
		if (factorlist == null) {
			return "";
		}
		StringBuffer sql_where = new StringBuffer();
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");
		String cardno = (String) hashmap.get("cardno");
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		if ("1".equals(like)) {
			for (Iterator it = factorlist.iterator(); it.hasNext();) {
				Factor factor = (Factor) it.next();
				String value = factor.getValue();
				if (value != null && value.length() > 0) {
					factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
					String log = factor.getLog();
					String oper = factor.getOper();
					oper=PubFunc.keyWord_reback(oper);
					String transOper = "<>".equals(oper)?" not ":"";
					if (!(oper.length() > 0)) {
						oper = "=";
					}
					String fieldname = factor.getFieldname();
					if(g_no.equalsIgnoreCase(fieldname)){
						fieldname = "g_no";
					}else if(cardno.equalsIgnoreCase(fieldname)){
						fieldname = "card_no";
					}
					if (sql_where.length() > 0) {
						if ("+".equals(log)) {
							sql_where.append(" OR " + fieldname + transOper + " LIKE '%" + value + "%'");
						} else {
							sql_where.append(" AND " + fieldname + transOper + " LIKE '%" + value + "%'");
						}
					} else {
						sql_where.append(" AND " + fieldname + transOper + " LIKE '%" + value + "%'");
					}
				}
			}
		} else {
			for (Iterator it = factorlist.iterator(); it.hasNext();) {
				Factor factor = (Factor) it.next();
				String value = factor.getValue();
				if (value == null || value.length() <= 0) {
					value = "#";
				}
				factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
				String log = factor.getLog();
				String oper = factor.getOper();
				oper=PubFunc.keyWord_reback(oper);
				if (!(oper.length() > 0)) {
					oper = "=";
				}
				String fieldname = factor.getFieldname();
				if(g_no.equalsIgnoreCase(fieldname)){
					fieldname = "g_no";
				}else if(cardno.equalsIgnoreCase(fieldname)){
					fieldname = "card_no";
				}
				if (sql_where.length() > 0) {
					if ("+".equals(log)) {
						sql_where.append(" OR " + Sql_switcher.isnull(fieldname, "'#'") + oper + "'" + value + "'");
					} else {
						sql_where.append(" AND " + Sql_switcher.isnull(fieldname, "'#'") + oper + "'" + value + "'");
					}
				} else {
					sql_where.append(" AND " + Sql_switcher.isnull(fieldname, "'#'") + oper + "'" + value + "'");
				}
			}
		}
		return sql_where.toString();
	}
}

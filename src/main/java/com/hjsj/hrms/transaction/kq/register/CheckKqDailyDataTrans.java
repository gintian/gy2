package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 检测日明晰输入数据是否符合考勤规定
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Jun 20, 2007:1:27:25 PM
 * </p>
 * 
 * @author dengcan
 * @version 4.0
 */
public class CheckKqDailyDataTrans extends IBusiness {
	private String unit_HOUR = "01";
	private String unit_DAY = "02";
	private String unit_ONCE = "04";
	private String unit_MINUTE = "03";

	public void execute() throws GeneralException {
		ArrayList daylist = RegisterDate.getKqDurationList(frameconn);
		int day = 0;
		if (daylist != null && daylist.size() > 0) 
		{
			day = daylist.size();
		}
		String kq_value = (String) this.getFormHM().get("kq_value");
		String table = (String) this.getFormHM().get("table");
		String flag = "true";
		String mess = "";
		if (kq_value == null || kq_value.length() <= 0) {
			this.getFormHM().put("flag", "true");
		}
		String kq_item = (String) this.getFormHM().get("kq_item");
		if ("q05".equalsIgnoreCase(table)
				&& ("q03z1".equalsIgnoreCase(kq_item))) {
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("mess", mess);
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select item_name,item_unit from kq_item");
		sql.append(" where Upper(fielditemid)='" + kq_item.toUpperCase() + "'");
		String item_unit = "";
		String item_name = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {

			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				item_unit = this.frowset.getString("item_unit");
				item_name = this.frowset.getString("item_name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (item_unit == null || item_unit.length() <= 0) {
			item_unit = unit_MINUTE;
		}
		float unit = Float.parseFloat(kq_value);
		if (item_unit.equals(unit_HOUR)) {
			if ("q03".equalsIgnoreCase(table) && unit > 24) {
				flag = "false";
				mess = item_name + "指标单位为小时，你输入的值大于24小时";
			}else if ("q05".equalsIgnoreCase(table) && unit > (24 * day)) 
			{
				flag = "false";
				mess = item_name + "指标单位为小时，你输入的值大于" + (day * 24) + "小时";
			}
		} else if (item_unit.equals(unit_MINUTE)) {
			if ("q03".equalsIgnoreCase(table) && unit > 1440) {
				flag = "false";
				mess = item_name + "指标单位为分钟，你输入的值大于1440分钟";
			}else if ("q05".equalsIgnoreCase(table) && unit > (1440 * day)) 
			{
				flag = "false";
				mess = item_name + "指标单位为分钟，你输入的值大于" + 1440 * day + "分钟";
			}
		} else if (item_unit.equals(unit_DAY)) {
			if ("q03".equalsIgnoreCase(table) && unit > 1) {
				flag = "false";
				mess = item_name + "指标单位为天，你输入的值大于1天";
			}else if ("q05".equalsIgnoreCase(table) && unit > day) 
			{
				flag = "false";
				mess = item_name + "指标单位为天，你输入的值大于" + day + "天";
			}
		} else if (item_unit.equals(unit_ONCE)) {

		}
		if (!"false".equals(flag)) {
			if(this.userView.hasTheFunction("2702026")||this.userView.hasTheFunction("0C3101"))
			{
				String nbase = (String) this.getFormHM().get("nbase");
				// 28427 传过来是加密的库前缀，所以在单元格编辑完后保存不上
				if(nbase.length() > 3){
					nbase = PubFunc.decrypt(nbase);
				}
				String a0100 = (String) this.getFormHM().get("a0100");
				String q03z0 = (String) this.getFormHM().get("q03z0");
				String table_name = (String) this.getFormHM().get("table");
				if (nbase != null && nbase.length() > 0 && a0100 != null
						&& a0100.length() > 0 && q03z0 != null
						&& q03z0.length() > 0) {
					StringBuffer up = new StringBuffer();
					if (table_name != null
							&& ("q03".equalsIgnoreCase(table_name) || "q05"
									.equalsIgnoreCase(table_name))) {
						DbWizard db = new DbWizard(this.frameconn);
						if(db.isExistField(table_name, "modtime", false) && db.isExistField(table_name, "modusername", false)){
							String modusername = this.userView.getUserName();
							up.append("update " + table_name + " set modtime=" + Sql_switcher.sqlNow() + ",modusername='" + modusername);
							up.append("', " + kq_item + "='" + kq_value + "'");
							up.append(" where a0100='" + a0100 + "' and nbase='"
								+ nbase + "' and q03z0='" + q03z0
								+ "' and (q03z5='01' or q03z5='07')");
						}else{
							up.append("update " + table_name + " set ");
							up.append(" " + kq_item + "='" + kq_value + "'");
							up.append(" where a0100='" + a0100 + "' and nbase='"
								+ nbase + "' and q03z0='" + q03z0
								+ "' and (q03z5='01' or q03z5='07')");
						}
						try {
							dao.update(up.toString());
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

				}
			}			
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("mess", mess);
	}

}

package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CountFeastDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String kq_year = (String) this.getFormHM().get("kq_year");
		String hols_status = (String) this.getFormHM().get("hols_status");
		int year = 0;
		if (kq_year == null || kq_year.length() <= 0) {
			Calendar now = Calendar.getInstance();
			Date cur_d = now.getTime();
			year = DateUtils.getYear(cur_d);
		} else {
			year = Integer.parseInt(kq_year);
		}

		Date d1 = DateUtils.getDate(year, 1, 1);
		Date d2 = DateUtils.getDate(year, 12, 31);
		String feast_start = DateUtils.format(d1, "yyyy-MM-dd");
		String feast_end = DateUtils.format(d2, "yyyy-MM-dd");
		String b0110 = "";
		String codeitemid = (String) this.getFormHM().get("code");
		if (codeitemid == null || codeitemid.length() <= 0) {
			codeitemid = this.getUserView().getUserOrgId();
		}
		if (this.userView.isSuper_admin()) {
			b0110 = "UN";
		} else {
			ManagePrivCode managePrivCode = new ManagePrivCode(userView, this
					.getFrameconn());
			String userOrgId = managePrivCode.getPrivOrgId();
			b0110 = "UN" + userOrgId;
		}
		FeastComputer feastComputer = new FeastComputer(this.getFrameconn(),this.userView);
		ArrayList fieldlist = feastComputer.fieldList(this.userView);
		ArrayList exp_fieldlist = new ArrayList();
		for (int i = 0; i < fieldlist.size(); i++) {
			CommonData da = (CommonData) fieldlist.get(i);
			String exp_field = da.getDataValue();
			String exp = feastComputer.getFeastComputer(codeitemid, exp_field,
					hols_status, this.getFrameconn());
			if (exp != null && exp.length() > 0)
				exp_fieldlist.add(da);
		}

		// 是否存在上年结余字段
		if (this.getBalance().length() > 0) {
			this.getFormHM().put("existBalance", "1");
		} else {
			this.getFormHM().put("existBalance", "0");
		}

		// 是否存在结余截止日期字段
		String balanceEnd = KqUtilsClass.getBalanceEnd();
		if (balanceEnd.length() > 0) {
			this.getFormHM().put("existBalanceEnd", "1");
		} else {
			this.getFormHM().put("existBalanceEnd", "0");
		}
		this.getFormHM().put("exp_fieldlist", exp_fieldlist);
		this.getFormHM().put("feast_start", feast_start);
		this.getFormHM().put("feast_end", feast_end);
		this.getFormHM().put("kq_year", kq_year);
		this.getFormHM().put("hols_status", hols_status);
		this.getFormHM().put("dbpre", "all");
		this.getFormHM().put("dblist", getDbase());
	}

	/**
	 * 获得上年结余的字段名称
	 * 
	 * @return
	 */
	public String getBalance() {
		// 获得年假结余的列名
		String balance = "";

		ArrayList fieldList = DataDictionary.getFieldList("q17",
				Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem) fieldList.get(i);
			if ("上年结余".equalsIgnoreCase(item.getItemdesc())) {
				balance = item.getItemid();
			}
		}

		return balance;
	}

	private ArrayList getDbase() throws GeneralException {

		StringBuffer stb = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList dbaselist = RegisterInitInfoData.getDase3(this.formHM,
				this.userView, this.getFrameconn());
		ArrayList slist = new ArrayList();
		// String[] base=dlist.split(",");
		try {
			CommonData vo = null;
			vo = new CommonData("all", "全部人员库");
			slist.add(vo);
			stb.append("select * from dbname");
			this.frowset = dao.search(stb.toString());
			while (this.frowset.next()) {
				String dbpre = this.frowset.getString("pre");
				for (int i = 0; i < dbaselist.size(); i++) {
					String userbase = dbaselist.get(i).toString();
					if (dbpre != null && dbpre.equalsIgnoreCase(userbase)) {
						vo = new CommonData(this.frowset.getString("pre"),
								this.frowset.getString("dbname"));
						slist.add(vo);
					}
				}
			}
			  if(slist.size() == 2)
	            	slist.remove(0);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		return slist;
	}
}

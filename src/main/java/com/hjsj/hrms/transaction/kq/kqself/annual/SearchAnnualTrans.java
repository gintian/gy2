package com.hjsj.hrms.transaction.kq.kqself.annual;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SearchAnnualTrans extends IBusiness {
	private void getItem(String table,int unit) {
		ArrayList fieldlist = (ArrayList) DataDictionary.getFieldList(table,
				Constant.USED_FIELD_SET).clone();// 字段名
		ArrayList list = new ArrayList();
		FieldItem holiday = null;
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem field = (FieldItem) fieldlist.get(i);
			field.setValue("");
			field.setViewvalue("");
			
			if ("e01a1".equals(field.getItemid())
					|| "b0110".equals(field.getItemid())
					|| "a0101".equals(field.getItemid())
					|| "e0122".equals(field.getItemid())
					|| "nbase".equals(field.getItemid())
					|| "a0100".equals(field.getItemid())
					|| field.getItemid().equals(table.toLowerCase() + "01"))
				field.setVisible(false);
			else if ("1".equals(field.getState()))
				field.setVisible(true);
			else
				field.setVisible(false);
			
			if ("Q33".equalsIgnoreCase(table)) {
				if ("q3305".equals(field.getItemid()) ||
						"q3307".equals(field.getItemid()) ||
						"q3309".equals(field.getItemid())){
					field.setDecimalwidth(unit);//我的调休 单位小时 显示unit位小数
				}
			}
			
			if ("q1519".equals(field.getItemid())) {
				field.setVisible(true);
				field.setItemdesc("请/销假");
				holiday = field;
			} else {
				FieldItem field_n = (FieldItem) field.cloneItem();
				list.add(field_n);
			}
		}
		if (holiday != null)
			list.add(holiday);
		this.getFormHM().put("tlist", list);

	}

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table = (String) hm.get("table");
		String kq_year = (String) this.getFormHM().get("kq_year");
		String type = (String) this.getFormHM().get("type");
		String year = (String) hm.get("year");
		if (type == null)
			type = "06";
		ArrayList slist = RegisterDate.getKqYear(this.getFrameconn());
		String currKqYear = RegisterDate.getCurrKqYear(this.getFrameconn());
		if (kq_year == null) {
			if (currKqYear != null && currKqYear.length() > 0) {
				kq_year = currKqYear;
			} else {
				Calendar now = Calendar.getInstance();
				Date cur_d = now.getTime();
				kq_year = DateUtils.getYear(cur_d) + "";
			}
		}
		if (year == null) {
			if (kq_year != null && kq_year.length() > 0) {
				year = kq_year;
			} else if (slist != null && slist.size() > 0) {
				CommonData vo = (CommonData) slist.get(0);
				year = vo.getDataName();
			} else {
				Calendar now = Calendar.getInstance();
				Date cur_d = now.getTime();
				year = DateUtils.getYear(cur_d) + "";
			}
		}
		ArrayList fieldlist = DataDictionary.getFieldList(table,
				Constant.USED_FIELD_SET);// 字段名
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		String columns = "";

		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem field = (FieldItem) fieldlist.get(i);
			if (!("b0110".equals(field.getItemid()) ||
					"a0101".equals(field.getItemid()) ||
					"e0122".equals(field.getItemid()) ||
					field.getItemid().equals(table.toLowerCase() + "01"))){
				columns = columns + field.getItemid().toString() + ",";
				sql_str.append(field.getItemid() + ",");
			}
		}
		sql_str.setLength(sql_str.length() - 1);
		cond_str.append(" from ");
		cond_str.append(table);
		cond_str.append(" where a0100 ='");
		cond_str.append(userView.getA0100());
		cond_str.append("' and UPPER(nbase)='");
		cond_str.append(userView.getDbname().toUpperCase());
		cond_str.append("'");
		/*
		 * if(table.equalsIgnoreCase("q15")) {
		 * cond_str.append(" and q1517='0'"); }
		 */
		int unit = 1;
		if ("Q15".equals(table.toUpperCase())) {

		    String typeInMap = KqAppInterface.getMapTypeIdsFromHolidayMap(type);
			cond_str.append(" and q1503 in ");
			cond_str.append("(" + typeInMap + ")");
			String start_date = year + "-01-01";
			String end_date = year + "-12-31";
			cond_str.append(" and " + table + "z1");
			cond_str.append(">=");
			cond_str.append(Sql_switcher.dateValue(start_date + " 00:00:00"));
			cond_str.append(" and ");
			cond_str.append(table + "z1");
			cond_str.append("<=");
			cond_str.append(Sql_switcher.dateValue(end_date + " 23:59:59"));
		} else if ("Q17".equals(table.toUpperCase())){
			cond_str.append(" and q1701='" + year + "'");
			cond_str.append(" and q1709 in('" + type + "')");
		}else {
			String leaveUsedOvertime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            HashMap kqItem_hash = annualApply.count_Leave(leaveUsedOvertime);
            String fielditemid = (String) kqItem_hash.get("fielditemid");
            
            if (StringUtils.isBlank(fielditemid)) {
                if (!kqItem_hash.containsKey("fielditemid")) {
                    throw new GeneralException("考勤规则中没有定义“调休假”！");
                } else {
                    throw new GeneralException("考勤规则中调休假没有设置统计指标！");
                }                 
            }
            
			FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid, "Q03");
			if (fieldItem == null || !"1".equalsIgnoreCase(fieldItem.getUseflag()))
			    throw new GeneralException("考勤规则中调休假没有设置统计指标！");
			
			unit = fieldItem.getDecimalwidth();
            
            String itemUnit = (String)kqItem_hash.get("item_unit");
            
            String standardUnit = KqParam.getInstance().getSTANDARD_HOURS();
            String tranUnit = standardUnit;
            if (itemUnit == null || itemUnit.length() <= 0) {
                itemUnit = DateAnalyseImp.unit_HOUR;
            }
            if (itemUnit.equals(DateAnalyseImp.unit_HOUR)) {
                tranUnit = "60.0";
            } else if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                tranUnit = "1.0";
            } else if (itemUnit.equals(DateAnalyseImp.unit_DAY)) {
                tranUnit = "(60.0*" + standardUnit + ")";
            }
            
			sql_str.setLength(0);
			cond_str.setLength(0);
			sql_str.append("select ");
			sql_str.append("nbase,a0100,b0110,e0122,e01a1,a0101,q3303,");
			sql_str.append("ROUND(q3305/" + tranUnit + "," + unit + ") as q3305,ROUND(q3307/" + tranUnit + "," + unit + ") as q3307,ROUND(q3309/" + tranUnit + "," + unit + ") as q3309");
			cond_str.append(" from q33");					
			cond_str.append(" where a0100 ='");
			cond_str.append(userView.getA0100());
			cond_str.append("' and UPPER(nbase)='");
			cond_str.append(userView.getDbname().toUpperCase());
			cond_str.append("'");
			
			String startTime = "━";
			String endTime = "";
			KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
            HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod();
            if(period != null && !period.isEmpty()) {
                startTime = period.get("from").toString().replaceAll("-", ".");
                endTime = period.get("to").toString().replaceAll("-", ".");
            } else {
            	Date currentDate = new Date();
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            	String current_date = sdf.format(currentDate);
            	int validityTime = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT());
            	startTime = sdf.format(OperateDate.addDay(currentDate, 0 - validityTime));
            	endTime = current_date;
            }
            
			cond_str.append(" and " + table + "03 >= '" + startTime + "'");
			cond_str.append(" and " + table + "03 <= '" + endTime + "'");
			this.getFormHM().put("leaveActiveTime", startTime + " ~ " + endTime);
		}
		
		this.getItem(table,unit);
		this.getFormHM().put("sql", sql_str.toString() + cond_str.toString());
		this.getFormHM().put("com", columns);
		//this.getFormHM().put("where", cond_str.toString());
		this.getFormHM().put("slist", slist);
		this.getFormHM().put("kq_year", kq_year);
		this.getFormHM().put("table", table);
		this.getFormHM().put("type", type);
		getRestList();

	}

	private void getRestList() throws GeneralException {
		HashMap hashmap = new HashMap();
		String b0110 = "";
		FeastComputer feastComputer = new FeastComputer(this.getFrameconn(),this.userView);
		ManagePrivCode managePrivCode = new ManagePrivCode(userView,
				this.getFrameconn());
		String code = managePrivCode.getPrivOrgId();
		do {
			ArrayList list = feastComputer.getHolsType(code);
			hashmap = (HashMap) list.get(0);
			code = list.get(1).toString();
			b0110 = (String) hashmap.get("b0110");
		} while (b0110 == null || b0110.length() <= 0);
		String hols_type = (String) hashmap.get("type");
		if (hols_type == null || hols_type.length() <= 0) {
			// 51625 为空则默认显示年假 与上面type = "06"保持一致
//			throw GeneralExceptionHandler.Handle(new GeneralException("",
//					"没有定义假期管理项目！", "", ""));
			
			ArrayList list = new ArrayList();
			CommonData da = new CommonData();
			da.setDataName("年假");
			da.setDataValue("06");
			list.add(da);
			this.getFormHM().put("typelist", list);
			return;
		}
		String[] types = hols_type.split(",");
		StringBuffer str = new StringBuffer();
		str.append("select codeitemid,codeitemdesc from codeitem where codesetid='27' and codeitemid in");
		str.append("(");
		for (int i = 0; i < types.length; i++) {
			str.append("'" + types[i] + "',");
		}
		if (str.toString() != null && str.toString().length() > 0) {
			str.setLength(str.length() - 1);
		}
		str.append(")");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao.search(str.toString());

			while (this.frowset.next()) {
				CommonData da = new CommonData();
				da.setDataName(this.frowset.getString("codeitemdesc"));
				da.setDataValue(this.frowset.getString("codeitemid"));
				list.add(da);
			}
		} catch (Exception e) {

		}
		this.getFormHM().put("typelist", list);
	}
}

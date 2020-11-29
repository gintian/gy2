package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FeastType;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KqSearchCalendarTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sb = new StringBuffer();
		ArrayList turn_rest=new ArrayList();
		ArrayList holiday=new ArrayList();
		JSONObject returnJson = new JSONObject();
		String return_code="success";
		String return_msg = "success";
		JSONObject return_data = new JSONObject();
		try {
		    String jsonStr = (String)this.getFormHM().get("jsonStr");
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            String kq_year = (String) jsonObj.get("kq_year");
            if (StringUtils.isEmpty(kq_year)) {
            	kq_year=DateUtils.getYear(new Date())+"";
			}
//            String kq_year="2020";
			// 获取公休日数据
			ArrayList restList = IfRestDate.search_RestOfWeek("UN", userView, this.getFrameconn());
			String rest_date = restList.get(0).toString();
			rest_date = KQRestOper.getRestStrTurn(rest_date);
			String[] week = rest_date.split(",");
			return_data.put("week", week);
			// 获取公休日倒休数据
			sb.append("select * from kq_turn_rest where b0110 ='UN'  and turn_date between ");
			String startDate=kq_year+"-01-01";
			String endDate=kq_year+"-12-31";
			startDate=Sql_switcher.charToDate("'"+startDate+"'");
			endDate=Sql_switcher.charToDate("'"+endDate+"'");
			sb.append(startDate+" and "+endDate+"  order by turn_id ");
			this.frowset = dao.search(sb.toString());
			HashMap<String, Object> vo=null;
			while (this.frowset.next()) {
				vo=new HashMap<String, Object>();
				vo.put("turn_id", this.getFrowset().getString("turn_id"));
				vo.put("week_date", this.getFrowset().getDate("week_date"));
				vo.put("turn_date", this.getFrowset().getDate("turn_date"));
				turn_rest.add(vo);
			}
			vo=new HashMap<String, Object>();
            vo.put("turn_id", "-");
            vo.put("week_date",null);
            vo.put("turn_date",null);
            turn_rest.add(vo);
			return_data.put("turn_rest", turn_rest.toString());
			// 获取节假日数据
			sb.setLength(0);
			sb.append("select * from kq_feast order by feast_id");
			this.frowset = dao.search(sb.toString());
			String fnames = null;
			String fdate = null;
			String fid = null;
			String fyear = null;
			int i = 1;
			while (this.frowset.next()) {
				fid = this.frowset.getString("feast_id");
				fnames = this.frowset.getString("feast_name");
				fdate = this.frowset.getString("feast_dates");
				fyear = getYear(fdate);
				if (StringUtils.isEmpty(fyear)||fyear.equals(kq_year)) {
					FeastType feasty = new FeastType(fid, fnames, fdate, i);
					holiday.add(feasty);
					i++;
				}

			}
			FeastType feasty=new FeastType("-", null, null, i);
			holiday.add(feasty);
			return_data.put("holiday", holiday);
		} catch (Exception e) {
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.register.work.error");
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
			returnJson.put("return_data", return_data);
		}
		this.formHM.put("returnStr", returnJson.toString());
	}
	private String getYear(String dates) {
		String[] sdate=dates.split(",");
		String year="";
		if (sdate.length>0&&sdate[0].length()==10) {
			year=sdate[0].substring(0,4);
		}
		return year;
	}
}

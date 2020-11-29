package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.StringTokenizer;

/**
 * 保存节假日
 * 
 * @author xuanz
 *
 */
public class KqSaveHolidayTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		JSONObject returnJson = new JSONObject();
		String return_code = "success";
		String return_msg = "success";
		JSONObject return_data = new JSONObject();
		String holiday = (String) this.getFormHM().get("holiday");
		JSONObject jsonObj = JSONObject.fromObject(holiday);
		String sdate = (String) jsonObj.get("dates");
		String dates = "";
		if (sdate != null || !"".equals(sdate)) {
			dates = this.turnDate(sdate);
			String[] dateLen=dates.split(",");
			String year="";
			int len=0;
			for (int i = 0; i < dateLen.length; i++) {
				if (dateLen[i].length()==10) {
					String yearString=dateLen[i].substring(0,4);
					if (!"".equals(year)&&!yearString.equals(year)) {//节假日可以有年份，如果是多个日期，那么年份应一致
						returnJson.put("return_code", "fail");
						returnJson.put("return_msg", "3");
						returnJson.put("return_data", return_data);
						this.formHM.put("returnStr", returnJson.toString());
						return;
					}
					year=yearString;
				}
				if (len==0) {
					len=dateLen[i].length();
				}else {
					if (len!=dateLen[i].length()) {//节假日可以没有年份，如果是多个日期，那么是否有年份应一致
						returnJson.put("return_code", "fail");
						returnJson.put("return_msg", "3");
						returnJson.put("return_data", return_data);
						this.formHM.put("returnStr", returnJson.toString());
						return;
					}
				}
			}
			}
		returnJson.put("return_code", return_code);
		returnJson.put("return_msg", return_msg);
		this.formHM.put("returnStr", returnJson.toString());
	}
	
	private String turnDate(String str) {
		String[] ter;
		StringBuffer stg = new StringBuffer();
		String tar = str.replaceAll("-", ".");

		ter = tar.split(",");
		for (int n = 0; n < ter.length; n++) {
			if (ter[n].length() > 0) {
				StringTokenizer nn = new StringTokenizer(ter[n], ".");
				for (int mm = 0; nn.hasMoreTokens(); mm++) {
					int tok = 0;
					String aa = "";
					String mmm = ((String) nn.nextElement());
					if (mmm.length() == 1)
						aa = "0" + mmm;
					if (mmm.length() == 4)
						aa = mmm + ".";
					if (mmm.length() == 2)
						aa = mmm;
					if ((aa.length() == 2 && mm == 0) || (ter[n].length() >= 8 && mm == 1 && aa.length() == 2))
						tok = tok + 1;
					if (tok == 1)
						aa = aa + ".";

					stg.append(aa);

				}
				stg.append(",");
			}
		}

		return stg.toString();
	}
	
}

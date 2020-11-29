package com.hjsj.hrms.businessobject.performance.workdiary;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.ITransCode;

import java.util.Date;
import java.util.Map;

public class WeekToDate implements ITransCode {

	@Override
    public String transCode(Map map, String targetKey) {
		String week = (String) map.get(targetKey);
		int yearnum = Integer.parseInt((String) map.get("year"));
		int monthnum = Integer.parseInt((String) map.get("month"));
		int i = 0;
		if("第一周".equals(week)){
			i=1;
		}else if("第二周".equals(week)){
			i=2;
		}else if("第三周".equals(week)){
			i=3;
		}else if("第四周".equals(week)){
			i=4;
		}else if("第五周".equals(week)){
			i=5;
		}
		WeekUtils weekutils = new WeekUtils();
		Date startdate = weekutils.numWeek(yearnum,monthnum,i,1);
		String startime = weekutils.dateTostr(startdate);
		return startime;
	}

}

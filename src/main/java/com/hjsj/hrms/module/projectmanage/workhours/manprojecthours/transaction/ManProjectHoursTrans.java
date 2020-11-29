package com.hjsj.hrms.module.projectmanage.workhours.manprojecthours.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manprojecthours.businessobject.ManMonthHoursBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 员工月工时报表交易类
 * @author wangjl
 * 2015/12/24
 */
public class ManProjectHoursTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String init = (String)this.getFormHM().get("init");
			String year = (String)this.getFormHM().get("year");
			String month = (String)this.getFormHM().get("month");
			Pattern pattern = Pattern.compile("[0-9]*");
			Date now = new Date();
			year = null == year || "".equals(year) ? "" + DateUtils.getYear(now) : year;
			month = null == month || "".equals(month) ? "" + DateUtils.getMonth(now) : month;
            ManMonthHoursBo bo = new ManMonthHoursBo(this.frameconn, this.userView);
            //时间只能是数字，防止sql注入和跨站脚本
            Matcher isNum = pattern.matcher(year);
            Matcher isNum2 = pattern.matcher(month);
            if(isNum.matches()&&isNum2.matches()){
            	this.getFormHM().put("tableConfig", bo.getTableConfig(year,month));
            }
            this.getFormHM().put("year", year);
            this.getFormHM().put("month", month);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);    
		}
	}

}

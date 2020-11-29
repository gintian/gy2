/**
 * 
 */
package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.HolidayQ17Bo;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author cmq
 *
 */
public class SearchRestDaysTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String app_date=(String)this.getFormHM().get("app_date");
		String date_type=(String)this.getFormHM().get("date_type");
		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
		String b0110=managePrivCode.getPrivOrgId();
	    if(KqParam.getInstance().isHoliday(this.frameconn, b0110, type))
	    {
	    	HolidayQ17Bo q17bo=new HolidayQ17Bo(type,app_date,this.userView,this.getFrameconn());
	    	q17bo.setDate_type(date_type);
			this.getFormHM().put("days", q17bo.findRestDescription());
	    }else{
	    	this.getFormHM().put("days", "");
	    }
		
	}

}

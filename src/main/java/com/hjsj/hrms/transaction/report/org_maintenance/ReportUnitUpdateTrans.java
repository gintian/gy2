
package com.hjsj.hrms.transaction.report.org_maintenance;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ReportUnitUpdateTrans extends IBusiness {

    //修改填报单位信息JSP页面显示.
	//负责给填报单位的单位编码和单位名称赋值
	public void execute() throws GeneralException {

		//获得以get方式传递的参数集合
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String unitid = (String)hm.get("unitid");
		int id = Integer.parseInt(unitid);
		String sql = "select unitcode , unitname,start_date,end_date,parentid  from tt_organization where unitid=" + id;	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				this.getFormHM().put("unitcode",this.frowset.getString("unitcode"));
				this.getFormHM().put("parentCode",this.frowset.getString("parentid"));
				//判断是否为顶层结构  wangchaoqun  2014-11-4
				this.getFormHM().put("unitCodeFalg",this.frowset.getString("unitcode"));
				String temp = this.frowset.getString("unitname");
				this.getFormHM().put("unitname",temp.replaceAll("<br>" ,""));
				this.getFormHM().put("start_date",df.format(this.frowset.getDate("start_date")));
				this.getFormHM().put("end_date",df.format(this.frowset.getDate("end_date")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

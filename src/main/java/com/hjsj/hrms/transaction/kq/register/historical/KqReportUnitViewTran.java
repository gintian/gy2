package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.history.KqReportInit;
import com.hjsj.hrms.businessobject.kq.register.history.KqUnitViewDailyBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 考勤表 个人考勤薄
 * @author Owner
 * wangyao
 */
public class KqReportUnitViewTran extends IBusiness{

	public void execute() throws GeneralException {
		KqReportInit kqReportInit= new KqReportInit(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		KqUnitViewDailyBo kqViewDaily = new KqUnitViewDailyBo(this.getFrameconn());
		String fileunit = (String)hm.get("fileunit");  //人员库
		if("2".equalsIgnoreCase(fileunit))
		{
			String report_unitid=(String)this.getFormHM().get("report_unitid");
			String userbaseunit=(String)this.getFormHM().get("userbaseunit");
			String username=(String)this.getFormHM().get("username");
			username=SafeCode.decode(username);
			String unita0100=(String)this.getFormHM().get("unita0100");
			String coursedate=(String)hm.get("coursedate");//RegisterDate.getKqDuration(this.getFrameconn());  //年月
			ReportParseVo parsevo =kqReportInit.getParseVo("3");
			boolean boo = kqViewDaily.getkq_report("3");
			if(!boo)
			{
				parsevo=kqViewDaily.getParseVo("1");
			}
			ArrayList tablelist=new ArrayList();
			
			tablelist=kqViewDaily.getKqReportHtml(unita0100,"1",coursedate,parsevo, userView,this.getFormHM(),userbaseunit,username);
			if(tablelist!=null&&tablelist.size()>0)
			{
				String tableHtml=tablelist.get(0).toString();		
				String turnTableHtml=tablelist.get(1).toString();
				this.getFormHM().put("tableUnitHtml",tableHtml);	
				this.getFormHM().put("turnUnitTableHtml",turnTableHtml);
			}
			this.getFormHM().put("userbaseunit",userbaseunit);
			this.getFormHM().put("unita0100",unita0100);
			this.getFormHM().put("username",username);
			this.getFormHM().put("report_unitid","3");
		}else
		{
			String userbase = (String)hm.get("userbase");  //人员库
			String start_date =(String)hm.get("start_date");
			String end_date=(String)hm.get("end_date");
			String A0100 = (String)hm.get("A0100");
			String username =(String)hm.get("username"); //人员姓名
			username = SafeCode.decode(username);
			String coursedate=(String)hm.get("coursedate");//RegisterDate.getKqDuration(this.getFrameconn());  //年月
			ReportParseVo parsevo =kqReportInit.getParseVo("3");
			boolean boo = kqViewDaily.getkq_report("3");
			if(!boo)
			{
				parsevo=kqViewDaily.getParseVo("1");
			}
			ArrayList tablelist=new ArrayList();
			
			tablelist=kqViewDaily.getKqReportHtml(A0100,"1",coursedate,parsevo, userView,this.getFormHM(),userbase,username);
			//System.out.println(tablelist);
			if(tablelist!=null&&tablelist.size()>0)
			{
				String tableHtml=tablelist.get(0).toString();		
				String turnTableHtml=tablelist.get(1).toString();
				this.getFormHM().put("tableUnitHtml",tableHtml);	
				this.getFormHM().put("turnUnitTableHtml",turnTableHtml);
			}
			this.getFormHM().put("userbaseunit",userbase);
			this.getFormHM().put("unita0100",A0100);
			this.getFormHM().put("username",username);
			this.getFormHM().put("report_unitid","3");
		}
		String privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
//		this.getFormHM().put("codeValue", privCodeValue);
	}

}

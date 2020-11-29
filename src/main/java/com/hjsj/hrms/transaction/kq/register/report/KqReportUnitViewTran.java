package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.businessobject.kq.register.KqReportInit;
import com.hjsj.hrms.businessobject.kq.register.KqUnitViewDailyBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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
	    try {
    		KqReportInit kqReportInit = new KqReportInit(this.getFrameconn());
    		ReportParseVo parsevo = kqReportInit.getParseVo("3");
    		
    		KqUnitViewDailyBo kqViewDaily = new KqUnitViewDailyBo(this.getFrameconn());
    		
    		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
    		String fileunit = (String)hm.get("fileunit");  //人员库
    		
    		if("2".equalsIgnoreCase(fileunit))
    		{
    			String userbaseunit=(String)this.getFormHM().get("userbaseunit");
    			String username=(String)this.getFormHM().get("username");
    			String unita0100=(String)this.getFormHM().get("unita0100");
    			String coursedate=(String)hm.get("coursedate");
    			if (coursedate == null || coursedate.length() <= 0) {
    				coursedate=RegisterDate.getKqDuration(this.getFrameconn());  //年月
    			}
    			
    			//判断数据是否在归档表中
    			kqReportInit.checkArcData("q03", coursedate);
    			kqViewDaily.setCurTab(kqReportInit.getCurTab());
    			
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
    			this.getFormHM().put("coursedate", coursedate);
    		}else
    		{
    		    String start_date = "";
    		    String userbase = "";
    		    String A0100 = "";
    		    String username = "";
    		    
    		    if(!"3".equalsIgnoreCase(fileunit)) {
    		        //从部门考勤簿点击进入个人签到簿
    		        userbase = (String)hm.get("userbase");  //人员库
    		        userbase = PubFunc.decrypt(userbase);
    		        start_date = (String)hm.get("start_date");
    		        A0100 = (String)hm.get("A0100");
    		        A0100 = PubFunc.decrypt(A0100);
    		        username =(String)hm.get("username"); //人员姓名
    		        username = SafeCode.decode(username);
    		    } else {
    		        //取当前用户自己的数据
    		        userbase = this.userView.getDbname();
    		        A0100 = this.userView.getA0100();
    		        username = this.userView.getUserFullName();
    		        start_date = DateUtils.format(new java.util.Date(), "yyyy-MM-dd");
        		    if ("".equals(userbase) || "".equals(A0100))
        		        throw new GeneralException("","非自助用户没有签到簿功能！","","");
    		    }
    		    
    			String coursedate = RegisterDate.getDurationFromDate(start_date, this.frameconn);
    			//zxj 20170215 如果当前日期所在期间取不到，则取当前期间
    			if("".equals(coursedate))
    			    coursedate = RegisterDate.getKqDuration(this.getFrameconn());
    			
    			kqReportInit.checkArcData("q03", coursedate);
    			kqViewDaily.setCurTab(kqReportInit.getCurTab());
    			
    			boolean boo = kqViewDaily.getkq_report("3");
    			if(!boo)
    			{
    				parsevo=kqViewDaily.getParseVo("1");
    			}
    			ArrayList tablelist=new ArrayList();
    			
    			kqViewDaily.setSelfView("3".equalsIgnoreCase(fileunit));
    			tablelist=kqViewDaily.getKqReportHtml(A0100,"1",coursedate,parsevo, userView,this.getFormHM(),userbase,username);
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
    			this.getFormHM().put("coursedate", coursedate);
    		}
    		String privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
	    } catch(Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	}

}

package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.businessobject.kq.register.KqReportInit;
import com.hjsj.hrms.businessobject.kq.register.KqUnitEXCELDailyBo;
import com.hjsj.hrms.businessobject.kq.register.KqUnitViewDailyBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>
 * Title:KqReportUnitEXCELTran
 * </p>
 * <p>
 * Description:签到薄生成excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-05-19
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class KqReportUnitEXCELTran extends IBusiness{

	public void execute() throws GeneralException {
		try {
            // 是否是历史数据，2为当前月数据，1为历史数据
            String fileunit = (String) this.getFormHM().get("fileunit");
            
            KqReportInit kqReportInit= new KqReportInit(this.getFrameconn());
            
            KqUnitViewDailyBo kqViewDaily = new KqUnitViewDailyBo(this.getFrameconn());
            KqUnitEXCELDailyBo bo = new KqUnitEXCELDailyBo(this.frameconn, this.userView);
            
            if ("2".equals(fileunit)) {// 当前考勤期间
            	// 用户编号
            	String a0100 = (String) this.getFormHM().get("a0100");
            	// 人员库
            	String nbase = (String) this.getFormHM().get("nbase");
            	// 用户姓名
            	String name = (String) this.getFormHM().get("username");
            	// 报表id
            	String reportId = (String) this.getFormHM().get("reportid");
            	// 当前考勤期间
            	String coursedate = (String) this.getFormHM().get("coursedate");
            	if (coursedate == null || coursedate.length() <= 0) {
            		coursedate = RegisterDate.getKqDuration(this.getFrameconn());  //年月
            	}
            	
            	kqReportInit.checkArcData("q03", coursedate);
            	bo.setCurTab(kqReportInit.getCurTab());
            	
            	// 当前签到簿的页面设置
            	ReportParseVo parsevo = kqReportInit.getParseVo(reportId);
            	
            	boolean boo = kqViewDaily.getkq_report(reportId);
            	if(!boo)
            	{
            		parsevo=kqViewDaily.getParseVo("1");
            	}
            	
            	// 文件名
            	String fileName = bo.getKqReportExcel(a0100,coursedate, parsevo, nbase, name.replaceAll("　", ""), reportId);
            	//xiexd 2014.09.15文件名加密
            	fileName = PubFunc.encrypt(fileName);
            	this.getFormHM().put("filename", fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
	}
}

package com.hjsj.hrms.transaction.kq.register.report;


import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.*;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class CreateKqXmlPdfTrans extends IBusiness{
	public void execute()throws GeneralException
	{
		HashMap hm=(HashMap)this.getFormHM();
		
		String code=(String)hm.get("code");
		String kind=(String)hm.get("kind");					
		String report_id=(String)hm.get("report_id");	
		String coursedate=(String)hm.get("coursedate");		
		String self_flag=(String)hm.get("self_flag"); 
		String sjelement=(String)hm.get("sjelement"); //制作时间 用户可以更改
		String timeqd=(String)hm.get("timeqd"); //时间 用户可以更改
		String dbty=(String)hm.get("dbty"); //人员库
		String sort = (String) hm.get("sort");
		if (sort == null || sort.length() <= 0) {
			sort = "";
		}
		if(dbty==null|| "".equals(dbty))
			dbty="all";
		if(coursedate==null||coursedate.length()<=0)
		{
			coursedate=RegisterDate.getKqDuration(this.getFrameconn());
		}
		if(!userView.isSuper_admin())
		{
			if(kind==null||kind.length()<=0)
			{
				LazyDynaBean bean=RegisterInitInfoData.getKqPrivCodeAndKind(userView);
				code=(String)bean.get("code");
				kind=(String)bean.get("kind");
			}			
		}else
		{
			if(code==null||code.length()<=0)
			{
			  ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
 			  code=managePrivCode.getPrivOrgId();  			
			  kind="2";  
			}
		}
		if(kind==null||kind.length()<=0)
	   		kind="-2";
	   	if(!"-2".equals(kind)&&(code==null||code.length()<=0) && !userView.isSuper_admin())
		{
	   		kind = RegisterInitInfoData.getKqPrivCode(userView);
		    code = RegisterInitInfoData.getKqPrivCodeValue(userView);
	    }
	   	if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"UM".equals(RegisterInitInfoData.getKqPrivCode(userView))))
	    {
		   code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		   kind="1";
	    }else if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"@K".equals(RegisterInitInfoData.getKqPrivCode(userView))))
	    {
	    	code=RegisterInitInfoData.getKqPrivCodeValue(userView);
			kind="0";
	    }
	   	
		KqReportInit kqReportInit= new KqReportInit(this.getFrameconn());
		ReportParseVo parsevo =kqReportInit.getParseVo(report_id);
		String url="";	
		if("q03".equals(parsevo.getValue().trim())&&!"select".equals(self_flag))
		{
			ExecuteKqDailyPdf executeKqDailyPdf = new ExecuteKqDailyPdf(this.getFrameconn());
			
			executeKqDailyPdf.setSelf_flag(self_flag);
			executeKqDailyPdf.setSjelement(sjelement);
			executeKqDailyPdf.setTimeqd(timeqd);
			executeKqDailyPdf.setDbty(dbty);  //人员库
			executeKqDailyPdf.setSortItem(sort);
			url=executeKqDailyPdf.executePdf(code,kind,coursedate,parsevo,this.userView,this.getFormHM());
		}else if("q03".equals(parsevo.getValue().trim())&& "select".equals(self_flag))
		{
			ExecuteKqDailyPdf executeKqDailyPdf = new ExecuteKqDailyPdf(this.getFrameconn());
			executeKqDailyPdf.setSelf_flag(self_flag);
			
			String whereIN=(String)this.getFormHM().get("whereIN");
			executeKqDailyPdf.setWhereIN(whereIN);
			executeKqDailyPdf.setDbty(dbty); //人员库
			executeKqDailyPdf.setSortItem(sort);
			url=executeKqDailyPdf.executePdf(code,kind,coursedate,parsevo,this.userView,this.getFormHM());
		}else if("q05".equals(parsevo.getValue().trim()))
		{
			ExecuteKqSumPdf executeKqSumPdf = new ExecuteKqSumPdf(this.getFrameconn());
			executeKqSumPdf.setSjelement(sjelement);
			executeKqSumPdf.setTimeqd(timeqd);
			executeKqSumPdf.setSortItem(sort);
			url=executeKqSumPdf.executePdf(code,kind,coursedate,parsevo,this.userView,this.getFormHM());
		}

		//xiexd 2014.09.12 加密文件名
		url = SafeCode.encode(PubFunc.encrypt(url));
		this.getFormHM().put("url",url);
	}

}
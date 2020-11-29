/*
 * Created on 2006-4-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportFieldAnalyse;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
/**
 * 
 * <p>Title:报表指标分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 14, 2006:9:49:26 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportFieldAnalyseTrans extends IBusiness {
	
	//保存指标信息，不含有重复指标分析信息
	private HashSet set = new HashSet();

	//指标分析
	public void execute() throws GeneralException {
		
		//存放所有的报表分析对象
		ArrayList reportAnalyseList = new ArrayList();
		//选中的报表集合
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		
		
		StringBuffer reportFieldAnalyseResult = new StringBuffer();
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 start */
		reportFieldAnalyseResult.append("<table width='80%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
		//reportFieldAnalyseResult.append("<table width='80%' border='0' cellspacing='0' cellpadding='5'>");
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 end */
		reportFieldAnalyseResult.append("<tr><td colspan='4'   class='TableRow'>");
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 start */
		/*reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");*/
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 end */
		reportFieldAnalyseResult.append(ResourceFactory.getProperty("reportfieldanalysetrans.title"));
		reportFieldAnalyseResult.append("</td></tr>");
		reportFieldAnalyseResult.append("<tr><td>");
		reportFieldAnalyseResult.append("<table  border='0' cellspacing='0'  width='100%' class='framestyle3' align='center' cellpadding='0'>");
		reportFieldAnalyseResult.append("<tr><td colspan='4'  >");
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 start */
		reportFieldAnalyseResult.append("&nbsp;");
		/*reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");*/
		/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 end */
		reportFieldAnalyseResult.append(ResourceFactory.getProperty("reportfieldanalysetrans.builddate")+": ");
		reportFieldAnalyseResult.append(this.getCurrentDate());
		reportFieldAnalyseResult.append("  ");
		reportFieldAnalyseResult.append("<br>");
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("**********************************");
		reportFieldAnalyseResult.append("**********************************");
		reportFieldAnalyseResult.append("**********************************");
		reportFieldAnalyseResult.append("<br>");
		
		if(list == null || list.size()==0){
			Exception e = new Exception(ResourceFactory.getProperty("auto_fill_report.batchFillData.info7")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		ReportFieldAnalyse rfa = new ReportFieldAnalyse(this.getFrameconn() , this.set);
		//获得选中报表的表号
		for(int i = 0 ; i< list.size(); i++){	
			
			RecordVo vo = (RecordVo)list.get(i);
			String tabid = vo.getString("tabid");  //报表ID
			String name = vo.getString("name");    //报表名称		
			/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 start */
			reportFieldAnalyseResult.append("&nbsp;");
			/*reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;");*/
			/* 兼容hcm7.0样式 报表-提取数据-指标分析 xiaoyun 2014-6-24 end */
			reportFieldAnalyseResult.append(tabid);
			reportFieldAnalyseResult.append(".");
			reportFieldAnalyseResult.append(name);
			reportFieldAnalyseResult.append("<br>");
			rfa.fieldAnalyse(tabid);

		}
		reportFieldAnalyseResult.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		reportFieldAnalyseResult.append("**********************************");
		reportFieldAnalyseResult.append("**********************************");
		reportFieldAnalyseResult.append("**********************************");
		
		String result = rfa.getReportFieldAnalyseValues();
		reportFieldAnalyseResult.append(result);
		reportFieldAnalyseResult.append("</td></tr>");
		reportFieldAnalyseResult.append("</table>");
		reportFieldAnalyseResult.append("</table>");
		this.getFormHM().put("reportFieldAnalyseResult" , reportFieldAnalyseResult.toString());
		
		this.getFormHM().put("reportAnalyseList",reportAnalyseList);
	}
	
	/**
	 * 获得系统当前时间
	 * @return
	 */
	public String getCurrentDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		return sdf.format(currentTime); 
	}
}

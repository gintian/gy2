/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;


import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:报表单击格图表联动</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 8, 2006:9:15:05 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportChangeGridAnalyseTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String unitCode = (String)(map.get("code"));
		String tabid = (String)(map.get("tabid"));
		String scopeid = (String)(map.get("scopeid"));
		if(scopeid==null)
			scopeid="0";
		map.remove("scopeid");
		//String row = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("row"));
		//String col = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("col"));
		String row ="";
		String col="";
		String showFlag=(String)this.getFormHM().get("showFlag");
		String scopeids = "";
		if(showFlag==null)
			showFlag="1";
		String char_type=(String)this.getFormHM().get("char_type");  // 1:柱状图 2：线状图
		if(char_type==null)
			char_type="1";
		ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
		rpdba.setScopeid(scopeid);
	//	rpdba.changeReportGrid(unitCode,tabid,row,col);
		
		String r_c=(String)map.get("rc");
		r_c = PubFunc.keyWord_reback(r_c); //add by wangchaoqun on 2014-9-20
		if(r_c!=null&&!"null".equals(r_c))
		{
			if(r_c.length()<3)
			{
				this.getFormHM().put("chartTitle","");
				this.getFormHM().put("chartFlag","no");
			}
			else
			{
				if("2".equals(showFlag))  //按单位分析
				{
					scopeids=(String)map.get("scopeids");
					String[] temp=r_c.split(",");
					
					row =temp[0].replace("/", "");
					col =temp[1].replace("/", "");
				}
				String w = (String)map.get("w");
				String h = (String)map.get("h");
				
		
				HashMap dataMap=new HashMap();
				if("2".equals(showFlag))
				{
						rpdba.changeReportGrid3(scopeids,tabid,row,col);
				}
				else if("1".equals(showFlag))
				{
					    rpdba.changeReportGrid2(unitCode ,tabid ,r_c);
				}
				
				this.getFormHM().put("chartTitle",rpdba.getReportGridTitle());
				if("1".equals(char_type))
					this.getFormHM().put("list" ,rpdba.getChartDBList());
				else
					this.getFormHM().put("dataMap" ,rpdba.getChartDBmap());
				
				this.getFormHM().put("chartFlag","yes");
				if("2".equals(char_type))
					this.getFormHM().put("chartType", "11");  //柱状图
				else if("1".equals(char_type))
					this.getFormHM().put("chartType", "29");  //分组柱状图
				this.getFormHM().put("chartWidth",w);
				this.getFormHM().put("chartHeight",h);
			
			
			}
		}
		
		this.getFormHM().put("chartTitle",rpdba.getReportGridTitle());
		this.getFormHM().put("list" ,rpdba.getChartDBList());
		this.getFormHM().put("chartFlag","yes");
		
		
	}

}

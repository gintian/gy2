package com.hjsj.hrms.transaction.general.deci.browser;

import com.hjsj.hrms.businessobject.general.deci.browser.MuchFieldAnalyse;
import com.hjsj.hrms.interfaces.sys.chartset.ChartParameterXML;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

public class MuchFieldAnalyseTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		String itemid = (String)this.getFormHM().get("itemid");		//代码指标ID
		String dbFlag = (String)this.getFormHM().get("dbflag");     //DB标志
		String dbpre = (String)this.getFormHM().get("dbpre");       //人员库前缀
		String analyseType = (String)this.getFormHM().get("analyseType"); //分析类型
		String changeFlag = (String)this.getFormHM().get("changeFlag");   //按月变化标志
		String startYear = (String)this.getFormHM().get("startYear");     //起始年
		String startMonth = (String)this.getFormHM().get("startMonth");   //起始月
		String endYear = (String)this.getFormHM().get("endYear");         //终止年
		String endMonth = (String)this.getFormHM().get("endMonth");       //终止月
		String itemName = (String)this.getFormHM().get("itemName");
		String changeFlagValue = (String)this.getFormHM().get("changeFlagValue");
		
		HashMap map = new HashMap();
		MuchFieldAnalyse mfd = new MuchFieldAnalyse(this.getFrameconn(),itemid);
		if("1".equals(analyseType)){//横向分析
			if("A".equalsIgnoreCase(dbFlag)){ //人员库
				if("0".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,dbpre,changeFlag,"","","","");
				}else if("1".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType , dbFlag,dbpre,changeFlag,startYear,startMonth,endYear,endMonth);
				}else{
					map = mfd.muchFieldAnalyse(analyseType , dbFlag,dbpre,changeFlag,startYear,"",endYear,"");
				}
			}else{
				if("0".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,"","","","");
				}else if("1".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,startYear,startMonth,endYear,endMonth);
				}else{
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,startYear,"",endYear,"");
				}
			}		
			this.getFormHM().put("analyseType","1");
		}else{//纵向分析
			//当前年月
			Calendar c = Calendar.getInstance();
			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = String.valueOf(c.get(Calendar.MONTH)+1);		
			
			if("A".equalsIgnoreCase(dbFlag)){ //人员库
				if("0".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,dbpre,changeFlag,year,month,year,month);
				}else if("1".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,dbpre,changeFlag,startYear,startMonth,endYear,endMonth);
				}else{
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,dbpre,changeFlag,startYear,"",endYear,"");
				}
			}else{
				if("0".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,year,month,year,month);
				}else if("1".equals(changeFlagValue)){
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,startYear,startMonth,endYear,endMonth);
				}else{
					map = mfd.muchFieldAnalyse(analyseType ,dbFlag,"",changeFlag,startYear,"",endYear,"");
				}

			}
			this.getFormHM().put("analyseType","2");
		}
		this.getFormHM().put("chartFlag","yes");
		this.getFormHM().put("chartMap",map);
		this.getFormHM().put("chartTitle",itemName);
		this.getFormHM().put("chartType","4");
	
		this.formHM.put("itemid",itemid);    //指标ID
		this.formHM.put("dbflag",dbFlag);    //库标志	
		
		String chartsets = (String) (((HashMap) (this.getFormHM().get("requestPamaHM"))).get("chartParameters"));
		
		//System.out.println("chartsets=" + chartsets);
		
		
		if(chartsets == null || "".equals(chartsets)){
			ChartParameterXML cpxml = new ChartParameterXML(this.getFrameconn());
			ChartParameter chartParameter =cpxml.searchChartParameter(userView.getUserName());
			chartParameter.setItemLabelsVisible(true);
			
			//控制网页对话框信息显示
			String chartsetss = ChartParameter.analyseChartParameter(chartParameter);
			this.getFormHM().put("chartsets",chartsetss);
			this.getFormHM().put("chartParameters",chartParameter);
			
			/*System.out.println("用户先前设置信息");
			System.out.println(chartsetss);*/
			
		}else{
			ChartParameter chartParameter = new ChartParameter(); //图形参数对象
			try {
				chartsets = new String(chartsets.getBytes("ISO-8859-1"),"GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			chartParameter.analyseChartParameter(chartsets);
			
			//System.out.println("*********************");
			
			//将用户设置信息保存到DB中
			ChartParameterXML cpxml = new ChartParameterXML(this.getFrameconn());
			cpxml.chartParameterControl(userView.getUserName(),chartParameter);
			
			//ChartParameter chartParameter =cpxml.searchChartParameter(userView.getUserName());
			

			//控制网页对话框信息显示
			String chartsetss = ChartParameter.analyseChartParameter(chartParameter);
			this.getFormHM().put("chartsets",chartsetss);
			
			this.getFormHM().put("chartParameters",chartParameter);
		
	}
	}

}

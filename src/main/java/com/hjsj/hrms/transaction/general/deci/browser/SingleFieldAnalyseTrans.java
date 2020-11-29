/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.browser;

import com.hjsj.hrms.businessobject.general.deci.browser.SingleFieldAnalyse;
import com.hjsj.hrms.interfaces.sys.chartset.ChartParameterXML;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:单个指标分析</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 4, 2006:9:54:43 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SingleFieldAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		String factorid = (String)this.getFormHM().get("factorid"); //指标ID
		String dbflag = (String)this.getFormHM().get("dbflag");     //库标志
		String changeFlag = (String)this.getFormHM().get("changeFlag");
		String changeFlagValue =(String)this.getFormHM().get("changeFlagValue");
		
		String dbpre="";  //人员库前缀
		String analyseType =(String)this.getFormHM().get("analyseType"); //分析类型 横向/纵向			
		if("A".equals(dbflag)){
			dbpre=(String)this.getFormHM().get("dbpre");
		}		
		String startYear = (String)this.getFormHM().get("startYear");
		String startMonth = (String)this.getFormHM().get("startMonth");
		String endYear = (String)this.getFormHM().get("endYear");
		String endMonth = (String)this.getFormHM().get("endMonth");
		
		
		if(startYear == null){
			startYear ="";
		}
		if(startMonth == null){
			startMonth="";
		}
		if(endYear == null){
			endYear = "";
		}
		if(endMonth == null){
			endMonth="";
		}	
	
		SingleFieldAnalyse sfa = new SingleFieldAnalyse(this.getFrameconn(),factorid);
		if("1".equals(analyseType)){//横向
			ArrayList chartList = new ArrayList();
			if("no".equals(changeFlag)){//不按月变化
				chartList = (ArrayList) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,
						"","","","");	
			}else{//按年/月变化				
				if("1".equals(changeFlagValue)){//月
					chartList = (ArrayList) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,
							startYear,startMonth,endYear,endMonth);
					
				}else if("2".equals(changeFlagValue)){//年
					chartList = (ArrayList) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,
							startYear,"",endYear,"");
					
				}				
			}
			this.getFormHM().put("chartFlag","yes");
			this.getFormHM().put("chartList",chartList);
			this.getFormHM().put("chartTitle",sfa.getName());
			this.getFormHM().put("chartType","11");
		}else if("2".equals(analyseType)){//纵向
			HashMap chartMap = new HashMap();
			if("no".equals(changeFlag)){//不按年/月变化
				Calendar c = Calendar.getInstance();
				startYear = String.valueOf(c.get(Calendar.YEAR));
				startMonth = String.valueOf(c.get(Calendar.MONTH)+1);
				endYear = String.valueOf(c.get(Calendar.YEAR));
				endMonth = String.valueOf(c.get(Calendar.MONTH)+1);
				chartMap = (HashMap) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,startYear,startMonth,endYear,endMonth);		
			}else{
				if("1".equals(changeFlagValue)){//月
					chartMap = (HashMap) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,startYear,startMonth,endYear,endMonth);
				}else if("2".equals(changeFlagValue)){//年
					chartMap = (HashMap) sfa.singleFieldAnalyse(analyseType,dbflag,dbpre,startYear,"",endYear,"");	
				}				
			}
			
			
			this.getFormHM().put("chartFlag","yes");
			this.getFormHM().put("chartMap",chartMap);
			this.getFormHM().put("chartTitle",sfa.getName());
			this.getFormHM().put("chartType","4");
		}
		this.getFormHM().put("analyseType",analyseType);
		
		this.getFormHM().put("startYear","");
		this.getFormHM().put("startMonth","");
		this.getFormHM().put("endYear","");
		this.getFormHM().put("endMonth","");
		
		String av = sfa.getAvgValue();
		float avgValue = 0f;
		if(av == null || "".equals(av)){
		}else{
			avgValue = Float.parseFloat(sfa.getAvgValue());
		}
		float standard_value = sfa.getStandard_value();
		float control_value = sfa.getControl_value();
		
		StringBuffer controlStr= new StringBuffer();
		HashMap map = new HashMap();
		if(avgValue != 0){
			map.put("平均值",String.valueOf(avgValue));
			controlStr.append(" 平均值,");
		}
		if(standard_value !=0){
			map.put("标准值",String.valueOf(standard_value));
			controlStr.append("标准值,");
		}
		if(control_value!=0){
			map.put("控制值",String.valueOf(control_value));
			controlStr.append("控制值,");
		}
		if(controlStr == null || "".equals(controlStr.toString())){
			controlStr.append("");
		}else{
			controlStr.deleteCharAt(controlStr.length()-1);			
		}
		
		this.getFormHM().put("controlStr",controlStr.toString());//图形参数设置用
		
		String chartsets = (String) (((HashMap) (this.getFormHM().get("requestPamaHM"))).get("chartParameters"));
		
		//System.out.println("chartsets=" + chartsets);
		
		
		if(chartsets == null || "".equals(chartsets)){
			ChartParameterXML cpxml = new ChartParameterXML(this.getFrameconn());
			ChartParameter chartParameter =cpxml.searchChartParameter(userView.getUserName());
			chartParameter.setMarkerMap(map);
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
			//System.out.println("_____________________");
			String markers = chartParameter.getMarkers();
			//System.out.println(markers);
			HashMap mp = new HashMap();
			if(markers == null || "".equals(markers)){
			}else{
				if(markers.endsWith(",")){
					markers = markers.substring(0,markers.length()-1);	
					//System.out.println(markers);
				}
				String [] marker = markers.split(",");
				for(int i=0; i<marker.length; i++){
					if(map.containsKey(marker[0])){
						String v = (String)map.get(marker[i]);
						mp.put(marker[i],v);
					}
				}
				
			}
			chartParameter.setMarkerMap(mp);
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

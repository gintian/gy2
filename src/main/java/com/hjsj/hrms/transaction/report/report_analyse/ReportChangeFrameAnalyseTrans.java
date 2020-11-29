package com.hjsj.hrms.transaction.report.report_analyse;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ReportChangeFrameAnalyseTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String w = (String)map.get("w");
		String h = (String)map.get("h");
		String chartWidth = "";
		String chartHeight = "";
		
		if(w == null || "".equals(w)){
		}else{
			if(w.indexOf('.')!=-1){
				chartWidth = w.substring(0,w.indexOf("."));
			}else{
				chartWidth = w;
			}
		}
		
		if(h == null || "".equals(h)){
		}else{
			if(h.indexOf('.')!=-1){
				chartHeight = h.substring(0,h.indexOf("."));
			}else{
				chartHeight = h;
			}
		}
		
		//System.out.println("脚本传入 width = " + chartWidth + "  height=" + chartHeight);
	//	this.getFormHM().put("chartFlag","no");
		if(this.getFormHM().get("char_type")==null||"1".equals(this.getFormHM().get("char_type")))
			this.getFormHM().put("chartType","29");
		else
			this.getFormHM().put("chartType","11");
		this.getFormHM().put("chartWidth",chartWidth);
		this.getFormHM().put("chartHeight",chartHeight);
		
	}

}

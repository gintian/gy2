package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetCustomTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String tabid="";
		String gz_module="";
		String dbname="";
		String titlename="";
		String category="";
		/**=1分析历史数据，=0分析归档数据*/
		String archive="1";
		if(hm!=null){
			tabid = (String)hm.get("tabid");
			tabid=tabid!=null?tabid:"";
			hm.remove("tabid");
			
			gz_module = (String)hm.get("gz_module");
			gz_module=gz_module!=null?gz_module:"";
			hm.remove("gz_module");
			
			dbname = (String)hm.get("dbname");
			dbname=dbname!=null?dbname:"Usr";
			hm.remove("dbname");
			
			titlename = (String)hm.get("titlename");
			titlename=titlename!=null?titlename:"";
			hm.remove("titlename");
			
			category = (String)hm.get("category");
			category=category!=null?category:"";
			if(category.trim().endsWith(",") && category.trim().length()>1){
				category=category.substring(0,category.length()-1);
			}
			hm.remove("category");
			archive=(String)hm.get("archive");
			if(archive==null)
				archive="1";
		}else{
			tabid = (String)this.getFormHM().get("tabid");
			tabid=tabid!=null?tabid:"";
			
			gz_module = (String)this.getFormHM().get("gz_module");
			gz_module=gz_module!=null?gz_module:"";
			
			dbname = (String)this.getFormHM().get("dbname");
			dbname=dbname!=null?dbname:"Usr";
			
			titlename = (String)this.getFormHM().get("titlename");
			titlename=titlename!=null?titlename:"";
			
			category = (String)this.getFormHM().get("category");
			category=category!=null?category:"";
			if(category.trim().length()>1){
				category=category.substring(0,category.length()-1);
			}
			archive=(String)this.getFormHM().get("archive");
			if(archive==null)
				archive="1";
		}
//		titlename=SafeCode.decode(titlename);
		
		GzFormulaXMLBo gzbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
		ArrayList seiveItemlist = gzbo.getSeiveItem();
		CommonData dataobj = new CommonData("all",ResourceFactory.getProperty("label.gz.allman"));
		seiveItemlist.add(0,dataobj);
		dataobj = new CommonData("new",ResourceFactory.getProperty("menu.gz.new"));
		seiveItemlist.add(dataobj);

		this.getFormHM().put("pageRows","20");
		this.getFormHM().put("conditionslist",seiveItemlist);
		this.getFormHM().put("selecttime","1");
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("isAutoCount","0");
		this.getFormHM().put("printGrid","1");
		this.getFormHM().put("gz_module",gz_module);
		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("titlename",titlename);
		this.getFormHM().put("category",category);
		this.getFormHM().put("archive", archive);
	}

}

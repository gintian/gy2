package com.hjsj.hrms.transaction.hire.zp_options.positionstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PositionStatTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap amap = (HashMap)this.getFormHM().get("requestPamaHM");
		if(amap.get("init")!=null&& "0".equals(amap.get("init"))){
			this.getFormHM().remove("starttime");
			this.getFormHM().remove("endtime");
		}
		amap.remove("init");
		String returnflag="";
		if(amap.get("returnflag")!=null)
		{
			returnflag=(String)amap.get("returnflag");
		}
		else
		{
			returnflag=(String)this.getFormHM().get("returnflag");
		}
		this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		Calendar stac = Calendar.getInstance();
		int syear = stac.get(Calendar.YEAR);
		int smonth=stac.get(Calendar.MONTH)+1;
		int sday = stac.get(Calendar.DATE);
		String scurrenttime=syear+"-"+(smonth>=10?(""+smonth):("0"+smonth))+"-"+(sday>=10?(""+sday):("0"+sday));
		Calendar ecal=Calendar.getInstance();
		ecal.set(Calendar.DAY_OF_YEAR,stac.get(Calendar.DAY_OF_YEAR)-30);
		int eyear = ecal.get(Calendar.YEAR);
		int emonth=ecal.get(Calendar.MONTH)+1;
		int eday = ecal.get(Calendar.DATE);
	    String ecurrenttime=eyear+"-"+(emonth>=10?(""+emonth):("0"+emonth))+"-"+(eday>=10?(""+eday):("0"+eday));
	    String endtime = (String)this.getFormHM().get("endtime");
	    endtime=(endtime==null|| "".equals(endtime))?scurrenttime:endtime;
	    String starttime = (String)this.getFormHM().get("starttime");
	    starttime = (starttime==null|| "".equals(starttime))?ecurrenttime:starttime;
	    PositionStatBo bo = new PositionStatBo(this.getFrameconn());
	    
	    //ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
	    ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
		HashMap map=parameterXMLBo.getAttributeValues();
	    String commonQueryIds = "";
		if(map!=null&&map.get("common_query")!=null)
			commonQueryIds = (String)map.get("common_query");
		ArrayList selectedCommonQuery = bo.getSelectedCommonQueryCondList(commonQueryIds,"1");
		ArrayList recordList = bo.getRecords(starttime, endtime,selectedCommonQuery,this.userView);
	    this.getFormHM().put("recordList",recordList);
	    this.getFormHM().put("starttime",starttime);
	    this.getFormHM().put("endtime",endtime);
	    this.getFormHM().put("condlist", selectedCommonQuery);
	    ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
		this.getFormHM().put("schoolPosition", schoolPosition);
		
	}

}

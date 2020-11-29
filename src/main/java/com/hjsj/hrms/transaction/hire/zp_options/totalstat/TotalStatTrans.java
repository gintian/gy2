package com.hjsj.hrms.transaction.hire.zp_options.totalstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.totalstat.TotalHireStatBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TotalStatTrans extends IBusiness{
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=this.getFormHM();
		HashMap  map = (HashMap)this.getFormHM().get("requestPamaHM");
		if(map.get("init")!=null&& "0".equals(map.get("init"))){
			hm.remove("startime");
			hm.remove("endtime");
		}
		map.remove("init");
		String returnflag="";
		if(map.get("returnflag")!=null)
		{
			returnflag=(String)map.get("returnflag");
		}
		else
		{
			returnflag=(String)this.getFormHM().get("returnflag");
		}
		this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		TotalHireStatBo ohsb=new TotalHireStatBo(this.getFrameconn());
		Calendar stac = Calendar.getInstance();
		int syear = stac.get(Calendar.YEAR);
		int smonth=stac.get(Calendar.MONTH)+1;
		int sday = stac.get(Calendar.DATE);
		String scurrenttime=syear+"-"+(smonth>=10?(""+smonth):("0"+smonth))+"-"+(sday>=10?(""+sday):("0"+sday));
		//stac.add(Calendar.DATE,30);
		Calendar ecal=Calendar.getInstance();
		ecal.set(Calendar.DAY_OF_YEAR,stac.get(Calendar.DAY_OF_YEAR)-30);
		int eyear = ecal.get(Calendar.YEAR);
		int emonth=ecal.get(Calendar.MONTH)+1;
		int eday = ecal.get(Calendar.DATE);
	    String ecurrenttime=eyear+"-"+(emonth>=10?(""+emonth):("0"+emonth))+"-"+(eday>=10?(""+eday):("0"+eday));
		ArrayList showresumelist=new ArrayList();
		ArrayList showfiresumelist=new ArrayList();		
		String column;		
		String selectsql;
		
		String org = (String)hm.get("org");
		hm.remove("org");
		org=org!=null&&org.length()>0&&!"".equals(org)&&!"不限".equals(org)?org:"不限";
		hm.put("org", org);
		
		
		String startime=(String) hm.get("startime");
		startime=startime!=null&&startime!=""&&startime.length()>0?startime:ecurrenttime;
		hm.remove("startime");
		hm.put("startime",startime);
		
		String endtime=(String) hm.get("endtime");
		hm.remove("endtime");
		endtime=endtime!=null&&endtime!=""&&endtime.length()>0?endtime:scurrenttime;
		hm.put("endtime",endtime);
		
		String orgid = (String) hm.get("orgid");	
		hm.remove("orgid");
		orgid=orgid!=null&&orgid.length()>0&&!"".equals(orgid)?orgid:"";
		hm.put("orgid", orgid);
		
		String notes = (String) hm.get("notes");	
		hm.remove("notes");
		hm.put("notes", notes);
		
		showresumelist=(ArrayList) ohsb.getorgresumeresult(startime, endtime, orgid, dao);
		showfiresumelist=(ArrayList) ohsb.getorgfiresumeresult(startime, endtime, orgid, dao);
		column = "z0311,allnum,firstnum";
		ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
		HashMap map0 = bo2.getAttributeValues();
		String hireMajor="";			//xieguiquan 2010-09-17
		if(map0.get("hireMajor")!=null)
			hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
		boolean hireMajorIsCode=false;
		FieldItem hireMajoritem=null;
		if(hireMajor.length()>0)
		{
			hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid()))
				hireMajorIsCode=true;
		}
		selectsql = "select p2.z0311 as z0311,p2.num as allnum,p1.num as firstnum  ";
		if(hireMajorIsCode&&hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){  
		selectsql = "select case when p2.z0336='01' then (select codeitemdesc from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"' and p2.z0311=codeitemid ) else p2.z0311 end as z0311,p2.num as allnum,p2.num as firstnum  ";
		}
		String wheresql = ohsb.getTagSqlStr(startime, endtime, orgid);
		//hm.put("showresumelist",showresumelist);
		//hm.put("showfiresumelist", showfiresumelist);
		ArrayList allList=new ArrayList();
		allList.add(showresumelist);
		allList.add(showfiresumelist);
		this.getFormHM().put("allList", allList);
		hm.put("column", column);
		hm.put("selectsql", selectsql);
		hm.put("wheresql", wheresql);
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
		hm.put("schoolPosition", schoolPosition);

	}		


}

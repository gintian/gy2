package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class CheckDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String yearnum = (String)hm.get("yearnum");
		yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"";
		hm.remove("yearnum");
		
		
		String monthnum = (String)hm.get("monthnum");
		monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:"";
		monthnum = !"0".equals(monthnum)?monthnum:"";
		hm.remove("monthnum");
		
		String subset = (String)hm.get("fieldsetid");
		subset=subset!=null&&subset.trim().length()>0?subset:"";
		
		String[] arr = subset.split("-");
		if(arr.length>1){
			subset = arr[1];
		}
		
		int check=0;
		StringBuffer checksql = new StringBuffer();
		checksql.append("select itemid,itemdesc,expression from fielditem where fieldsetid='");
		checksql.append(subset);
		checksql.append("' and useflag=1 and itemdesc<>'");
		checksql.append(ResourceFactory.getProperty("hmuster.label.nybs"));
		checksql.append("' and itemdesc<>'");
		checksql.append(ResourceFactory.getProperty("hmuster.label.counts"));
		checksql.append("'");
		ArrayList checklist=dao.searchDynaList(checksql.toString());
		for(Iterator it=checklist.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			String expression = (String)dynabean.get("expression");
			if(expression!=null&&expression.length()>1){
				if("K".equalsIgnoreCase(subset.substring(0, 1))){
					if("3".equals(expression.substring(0, 1))){
						check =1;
						break;
					}
				}
				check =0;
			}else{
				check = 1;
				break;
			}
		}
		
//		WorkdiarySQLStr wss=new WorkdiarySQLStr();
//		String tempstart=wss.getDataValue(subset+"z0","=",checkTime(yearnum,monthnum));
		String flag = (String)this.getFormHM().get("flag");
		if("orgpre".equals(flag)){
			FieldSet fs = DataDictionary.getFieldSetVo(subset);
			String changeflag = fs.getChangeflag();
			if(check==1&&!"0".equals(changeflag)){
				hm.put("checkdb","ok");
			}else{
				hm.put("checkdb","no");
			}
		}else{
			String tempstart = "Id='"+checkTime(yearnum,monthnum)+"'";
			String sqlstr = "select "+subset+"Z0 from "+subset+" where "+tempstart;
			ArrayList sqllist=dao.searchDynaList(sqlstr);
			if(sqllist.size()>0&&check==1){
				hm.put("checkdb","ok");
			}else{
				hm.put("checkdb","no");
			}
		}
	}
	/**
     * 生成正确的时间
     * @param yearnum 年
     * @param monthnum 月
     * @return 日期 格式为xxxx-xx-xx
     */
	private String checkTime(String yearnum,String monthnum){
		int month = 0;
		int year = 0;
		String time = "";
		
		if(yearnum.length()!=4||!yearnum.matches("[0-9]{4}")){
			year = Calendar.getInstance().get(Calendar.YEAR);
		}else{
			year = Integer.parseInt(yearnum);
		}
		if(monthnum.length()<1||monthnum.length()>2){
			month = Calendar.getInstance().get(Calendar.MONTH)+1;
		}else{
			if(monthnum.length()==2){
				if(!monthnum.matches("[0-9]{2}")){
					month = Calendar.getInstance().get(Calendar.MONTH)+1;
				}else{
					month = Integer.parseInt(monthnum);
				}
			}else{
				if(!monthnum.matches("[1-9]{1}")){
					month = Calendar.getInstance().get(Calendar.MONTH)+1;
				}else{
					month = Integer.parseInt(monthnum);
				}
			}
		}
		if(monthnum.trim().length()>0){
			month -=1;
			if(month-1<0){
				month=12;
				year -=1;
			}else if(month-1>12){
				month=12;
			}
			String month_start = "";
			if(month<10){
				month_start = "0" + month;
			}else{
				month_start = month+"";
			}
			time = year+"."+month_start;
//			time = year+"-"+month_start+"-01";
		}else{
			time = (year-1)+"";
//			time = (year-1)+"-01-01";
		}
		return time;
	}
}

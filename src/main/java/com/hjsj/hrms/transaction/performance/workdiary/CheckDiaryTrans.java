package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**检查是否具备报批的条件*/
public class CheckDiaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String state =(String)this.getFormHM().get("state");
		state=state!=null&&state.trim().length()>0?state:"";
		
		String yearnum =(String)this.getFormHM().get("yearnum");
		yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"";
		String monthnum =(String)this.getFormHM().get("monthnum");
		monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:"";
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String firstdays=sysbo.getValue(Sys_Oth_Parameter.DIARY,"firstdays");
			firstdays=firstdays!=null&&firstdays.trim().length()>0?firstdays:"5";
			
		String lastdays=sysbo.getValue(Sys_Oth_Parameter.DIARY,"lastdays");
			lastdays=lastdays!=null&&lastdays.trim().length()>0?lastdays:"5";
			
		String strvalue =(String)this.getFormHM().get("strvalue");
		strvalue=strvalue!=null&&strvalue.trim().length()>0?strvalue:"";
		
		String strname =(String)this.getFormHM().get("strname");
		strname=strname!=null&&strname.trim().length()>0?strname:"";
		
		String[] arr = strvalue.split(",");
		String[] arrName = strname.split(",");//
		String check="ok";
		ResultSet res=null;
		boolean bool=false;
		try {
		if(arr.length>0){
			int firstdate = Integer.parseInt(firstdays);
			int lastdate = Integer.parseInt(lastdays);
			WeekUtils weekutils = new WeekUtils();
			if("1".equals(state)){
				int year = Integer.parseInt(yearnum);
				int month = Integer.parseInt(monthnum);
				for(int i=0;i<arr.length;i++){
					int week = Integer.parseInt(arr[i]);
					Date date = weekutils.numWeek(year,month,week,7);
					int checkdate = DateUtils.dayDiff(date,new Date()); 
					if(checkdate>0){
						if(!checkTime(date,firstdate)){
							check="已超过报批日期,不能报批\n(只能在本期末最后"+lastdate+"天至下期初头"+firstdate+"天之内进行报批)";
							break;
						}
					}else{
						if(!checkTime(date,lastdate)){
							check="已超过报批日期,不能报批\n(只能在本期末最后"+lastdate+"天至下期初头"+firstdate+"天之内进行报批)";
							break;
						}
					}
				}
			}else if("2".equals(state)){
				int year = Integer.parseInt(yearnum);
				for(int i=0;i<arr.length;i++){
					int month = Integer.parseInt(arr[i]);
					Date date = weekutils.lastMonth(year,month);
					int checkdate = DateUtils.dayDiff(date,new Date()); 
					if(checkdate>0){
						if(!checkTime(date,firstdate)){
							check="已超过报批日期,不能报批\n(只能在本期末最后"+lastdate+"天至下期初头"+firstdate+"天之内进行报批)";
							break;
						}
					}else{
						if(!checkTime(date,lastdate)){
							check="已超过报批日期,不能报批\n(只能在本期末最后"+lastdate+"天至下期初头"+firstdate+"天之内进行报批)";
							break;
						}
					}
				}
			}
			else if("0".equals(state)){//日报
				if(arr.length>0){
					String p0100s="";
					for(int i=0;i<arr.length;i++){
						p0100s+="'"+arr[i]+"'"+",";
					}
					p0100s=p0100s.substring(0,p0100s.length()-1);
					ContentDAO dao=new ContentDAO(this.frameconn);
					String sql="select * from p01 where p0100 in ("+p0100s+") order by p0100";
					res=dao.search(sql);
					while(res.next()){
						String limit_HH=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_HH");
						String limit_MM=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_MM");
						String dairyinfolimit=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT);
						if(dairyinfolimit==null || "".equals(dairyinfolimit)){
							dairyinfolimit = "9999";
						}
						Date date=new Date();
						java.sql.Date str=res.getDate("p0104");
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
						String dd = sdf.format(str);
						Date pd=DateUtils.getDate(dd+" 23:59","yyyy-MM-dd HH:mm");
						if(limit_HH!=null&&limit_HH.length()>0&&limit_MM!=null&&limit_MM.length()>0)
						{
							pd=DateUtils.getDate(dd+" "+limit_HH+":"+limit_MM,"yyyy-MM-dd HH:mm");
						}
						if("0".equalsIgnoreCase(dairyinfolimit))//当天				{
						{
							pd=DateUtils.getDate(dd,"yyyy-MM-dd");
							date=DateUtils.getDate(new SimpleDateFormat("yyyy-MM-dd").format(date),"yyyy-MM-dd");
							if(!pd.equals(date))
							{
								check = ResourceFactory.getProperty("workdiary.message.out.time");
								break;
								//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
							}
						}else //延后日
						{
							pd=DateUtils.addDays(pd, Integer.parseInt(dairyinfolimit));
							if(pd.before(date))
							{
								check = ResourceFactory.getProperty("workdiary.message.out.time");
								break;
								//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
							}
						}
					}
			     }
			}
		}
		/*******判断必填内容是否填写*********/
		String fillcheck="ok";
		if("ok".equals(check)){
			ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
			String[] musftFillContent=null;
			FieldItem item=null;
			String code="";
			String p0100s="";
			String sql="";
			HashMap map=new HashMap() ;
			HashMap map1=new HashMap();
			for(int i=0;i<arrName.length;i++){
				map1.put(arrName[i], arr[i]);
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			for(int i=0;i<fieldlist.size();i++){
				item=(FieldItem) fieldlist.get(i);
				if("M".equalsIgnoreCase(item.getItemtype())&&item.isVisible()&&item.isFillable()){//可见、必填、备注型
					code+=item.getItemid()+",";
				}
			}
			if(code.length()>0){
				musftFillContent=code.substring(0, code.length()-1).split(",");
			}
			if(musftFillContent!=null && musftFillContent.length>0){
				map=getMap();
				if(arrName.length>0){
					for(int i=0;i<arrName.length;i++){
						p0100s+="'"+arrName[i]+"'"+",";
					}
					p0100s=p0100s.substring(0,p0100s.length()-1);
					sql="select * from p01 where p0100 in ("+p0100s+") order by p0100";
					res=dao.search(sql);
					
					if("1".equals(state)){//周报
						int j=0;
						while(res.next()){
							for(int i=0;i<musftFillContent.length;i++){
								if(res.getString(musftFillContent[i])==null|| "".equals(res.getString(musftFillContent[i]))){
									String str=(String) map1.get(res.getString("p0100"));
									fillcheck=yearnum+"年"+monthnum+"月第"+map.get(str)+"周周报必填内容没有填写完!";
									bool=true;
									break;
								}
							}
							if(bool)
							break;
							j++;
						}
		
					}else if("2".equals(state)){//月报
						int j=0;
						while(res.next()){
							for(int i=0;i<musftFillContent.length;i++){
								if(res.getString(musftFillContent[i])==null|| "".equals(res.getString(musftFillContent[i]))){
									String str=(String) map1.get(res.getString("p0100"));
									fillcheck=yearnum+"年第"+map.get(str)+"月月报必填内容没有填写完!";
									bool=true;
									break;
								}
							}
							if(bool)
							break;
							j++;
						}
					}else if("0".equals(state)){//日报
						if(arr.length>0){
							p0100s="";
							for(int i=0;i<arr.length;i++){
								p0100s+="'"+arr[i]+"'"+",";
							}
							p0100s=p0100s.substring(0,p0100s.length()-1);
							sql="select * from p01 where p0100 in ("+p0100s+") order by p0100";
							res=dao.search(sql);
							while(res.next()){
								String limit_HH=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_HH");
								String limit_MM=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_MM");
								String dairyinfolimit=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT);
								if(dairyinfolimit==null || "".equals(dairyinfolimit)){
									dairyinfolimit = "0";
								}
								Date date=new Date();
								java.sql.Date str=res.getDate("p0104");
								SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
								String dd = sdf.format(str);
								Date pd=DateUtils.getDate(dd+" 23:59","yyyy-MM-dd HH:mm");
								if(limit_HH!=null&&limit_HH.length()>0&&limit_MM!=null&&limit_MM.length()>0)
								{
									pd=DateUtils.getDate(dd+" "+limit_HH+":"+limit_MM,"yyyy-MM-dd HH:mm");
								}
								if("0".equalsIgnoreCase(dairyinfolimit))//当天				{
								{
									pd=DateUtils.getDate(dd,"yyyy-MM-dd");
									date=DateUtils.getDate(new SimpleDateFormat("yyyy-MM-dd").format(date),"yyyy-MM-dd");
									if(!pd.equals(date))
									{
										check = ResourceFactory.getProperty("workdiary.message.out.time");
										break;
										//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
									}
								}else //延后日
								{
									pd=DateUtils.addDays(pd, Integer.parseInt(dairyinfolimit));
									if(pd.before(date))
									{
										check = ResourceFactory.getProperty("workdiary.message.out.time");
										break;
										//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
									}
								}
								for(int i=0;i<musftFillContent.length;i++){
									if(res.getString(musftFillContent[i])==null|| "".equals(res.getString(musftFillContent[i]))){
										str=res.getDate("p0104");
										sdf=new SimpleDateFormat("yyyy.MM.dd");
										String datetime=sdf.format(str);
										fillcheck=datetime+"日日报必填内容没有填写完!";
										bool=true;
										break;
									}
								}
								if(bool)
								break;
							}
					     }
					}
				}
			}
		}
		this.getFormHM().put("check",SafeCode.encode(check));
		this.getFormHM().put("fillcheck",SafeCode.encode(fillcheck));
		this.getFormHM().put("strname",strname);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(res);
		}
	}
	private boolean checkTime(Date firstdays,int days){
		boolean check=false;
		int date = DateUtils.dayDiff(firstdays,new Date()); 
		date=date<0?-(date-2):date;
		if(date<=days){
			check=true;
		}
 		return check;
	}
	/**
	 * 获取当月第一天日期(返回日期格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  date
	 * */
	public Date firstMonth(int yearnum,int monthnum){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		return mDate;
	}
	public HashMap getMap(){
		HashMap map=new HashMap();
		map.put("1", "一");
		map.put("2", "二");
		map.put("3", "三");
		map.put("4", "四");
		map.put("5", "五");
		map.put("6", "六");
		map.put("7", "七");
		map.put("8", "八");
		map.put("9", "九");
		map.put("10", "十");
		map.put("11", "十一");
		map.put("12", "十二");


		return map;
	}

}

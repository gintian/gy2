package com.hjsj.hrms.transaction.hire.zp_options.itemstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.itemstat.InitHireStatBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ShowJobDailyTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=this.getFormHM();
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		if(map.get("init")!=null&& "0".equals(map.get("init"))){
			hm.remove("startime");
			hm.remove("endtime");
		}
		map.remove("init");
		String codeid="";
		String codesetid="";	
		RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
		if(vo==null|| "#".equals(vo.getString("str_value")))
		{
				throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标,运行错误!"));
		}
		if(!this.userView.isSuper_admin())
		{
			
			codeid=this.userView.getUnitIdByBusi("7");
			if(codeid==null|| "".equals(codeid)|| "UN".equalsIgnoreCase(codeid)){
				throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
			}
			if(codeid.trim().length()==3)
			{
				codeid="";
			}else{
				if(codeid.indexOf("`")==-1)
				{
					codeid=codeid.substring(2);
				}
				else
				{
        			String[] temps=codeid.split("`");
        			codeid="";
           			for(int i=0;i<temps.length;i++)
        				codeid+=temps[i].substring(2)+"`";
				}
			}
			/**业务用户*/
//			if(this.userView.getStatus()==0/*operateType.equals("user")*/)
//			{
//				codeid=this.getUserView().getUnit_id();
//				if(codeid==null||codeid.trim().length()==0||codeid.equalsIgnoreCase("UN"))
//				{
//					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//				}else if(codeid.trim().length()==3)
//				{
//					codeid="";
//				}
//				else if(codeid.indexOf("`")==-1)
//            		codeid=codeid.substring(2);
//			}
//			else
//			{
//				codeid=this.getUserView().getManagePrivCodeValue();
//				codesetid=this.getUserView().getManagePrivCode();
//				if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//				{
//					
//					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//				}
//			}
		}

		
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String tt=xmlBo.hasSetParam(this.userView);
		if(tt!=null&&tt.trim().length()>0){
			throw GeneralExceptionHandler.Handle(new Exception(tt));
		}
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
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
		String jobid=(String) hm.get("jobid");
		hm.remove("jobid");
		jobid= jobid!=null?jobid:"";
		jobid=PubFunc.keyWord_reback(jobid);
		String startime=(String) hm.get("startime");
		hm.remove("startime");
		startime= startime!=null&&startime.length()>1?startime:strStartDate();
		hm.put("startime",startime);
		
		String endtime=(String) hm.get("endtime");
		hm.remove("endtime");
		endtime= endtime!=null&&endtime.length()>1?endtime:strDate();
		hm.put("endtime",endtime);
		
		
		HashMap dayView = new HashMap();
		dayView.put(ResourceFactory.getProperty("hire.zp_options.curriculum.vitae"),jobListView(dao,jobid,startime,endtime));
		InitHireStatBo ihsb=new InitHireStatBo();
		ArrayList joblist=ihsb.getZposlist(dao,jobid,this.frameconn);
		hm.put("jobid",jobid);
		hm.put("joblist",joblist);
		hm.put("joblistview",dayView);
		ArrayList ll = whereStr(jobid,startime,endtime);
		hm.put("dataList", ll);
		//hm.put("sql","select aweek,adate,num");
		//hm.put("where",whereStr(jobid,startime,endtime));gg
		//hm.put("column","aweek,adate,num");
		//hm.put("orderby"," order by adate ");
		hm.put("schoolPosition", schoolPosition);
	}
	/**
	 * 获取页面生成图表标签所要的ArrayList
	 * @param  dao ContentDAO
	 * @param  jobid 职位id
	 * @param  startime 开始时间
	 * @param  endtime 结束时间
	 * @return ArrayList  返回分页标签list值
	 * @throws GeneralException
	 */
	public ArrayList jobListView(ContentDAO dao,String jobid,String startime,String endtime){
		ArrayList joblistview = new ArrayList();
		
		/*StringBuffer sql = new StringBuffer(); 
		sql.append("select aweek,adate,num ");
		sql.append(whereStr(jobid,startime,endtime));jj
		sql.append(" order by adate ");
		int i = 0;
		RowSet  rs = null;*/
		
		try {
			/*rs = dao.search(sql.toString());
			while(rs.next()){
				CommonData vo =new CommonData();
				vo.setDataName(i+++"");
				vo.setDataValue(String.valueOf(rs.getString("num")));
				joblistview.add(vo);
			}*/
			ArrayList list = this.whereStr(jobid, startime, endtime);
			int j = 0;
			for(int i=0;i<list.size();i++){
				j++;
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String num="0";
				if(bean.get("num")!=null)
					num=(String)bean.get("num");
				CommonData vo =new CommonData();
				vo.setDataName("      "+((String)bean.get("date")));
				vo.setDataValue(num);
				joblistview.add(vo);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return joblistview;
	}
	/**
	 * 将星期中文形式改为数字形式
	 * @param String weekday 字符串
	 */
	public String strToweek(String date){
		String weekday = "";
		String[] week = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
		String weekstr = DateUtils.format(strTodate(date),"E");
		for(int i=0;i<week.length;i++){
			if(week[i].equals(weekstr)){
				weekday = (i+1)+"";
			}
		}
		
		return weekday;
	}
	/**
	 * 将字符串转换成日期
	 * @param String date 字符串
	 */
	public Date strTodate(String date){
		
		String[] tempvalue=date.split("-");
		if(tempvalue.length==1){
			date=date+"-01-01";
		}
		if(tempvalue.length==2){
			if(tempvalue[1].length()==1){
				date=tempvalue[0]+"-0"+tempvalue[1]+"-01";
			}else{
				date=date+"-01";
			}
		}
		if(tempvalue.length==3){
			if(tempvalue[1].length()==1){
				tempvalue[1]="0"+tempvalue[1];
			}
			if(tempvalue[2].length()==1){
				tempvalue[2]="0"+tempvalue[2];
			}
			date=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
		}
		
		int day = Integer.parseInt(date.substring(8,10));
		int month = Integer.parseInt(date.substring(5,7));
		int year = Integer.parseInt(date.substring(0,4)); 
		
		return DateUtils.getDate(year,month,day);
	}
	/**
	 * 将日期转换成字符串
	 * @param Date date 日期型
	 */
	public String dateTostr(Date date){
		int day = DateUtils.getDay(date);
		int month = DateUtils.getMonth(date);
		int year = DateUtils.getYear(date);
		
		String strday = day+"";
		String strmonth = month+"";
		
		if(day<10){
			strday = "0"+strday;
		}
		if(month<10){
			strmonth = "0"+strmonth;
		}
		
		return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 获取分页标签中要用到的where字符串
	 * @param jobid 职位id
	 * @param startime 开始时间
	 * @param endtime 结束时间
	 */
	public ArrayList   whereStr(String jobid,String startime,String endtime){
		ArrayList list = new ArrayList();
		try
		{
			String[] id = null;
			if(jobid.length()>0){
				id = jobid.split("/");
			}
			startime+=" 00:00:00";//zzk
			endtime+=" 23:59:59";
			String tart_time =" APPLY_DATE between '"+startime+"' and '"+ endtime+"'";
			String oracl_sql = " APPLY_DATE between to_date('"+startime+"','yyyy-mm-dd hh24:mi:ss') and to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss') ";
			StringBuffer buf = new StringBuffer();
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			buf.append("select max(apply_date) apply_date,count(a0100) num from zp_pos_tache where ");
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				buf.append(oracl_sql);
			else
				buf.append(tart_time);
			if(jobid.length()>0){
				buf.append(" and zp_pos_id='");
				buf.append(id[0]);
				buf.append("'");
			}
			buf.append(" group by "+Sql_switcher.year("apply_date")+","+Sql_switcher.month("apply_date")+","+Sql_switcher.day("apply_date")+" order by apply_date");
			this.frowset=dao.search(buf.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(this.frowset.next())
			{
				Date date = this.frowset.getDate(1);
				if(date==null)
					continue;
				
				LazyDynaBean bean  = new LazyDynaBean();
				bean.set("week", DateUtils.format(strTodate(format.format(date)),"E"));
				bean.set("num", this.frowset.getString(2));
				bean.set("date", format.format(date));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
			
	}
	/**
	 * 获取10天前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strStartDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -30);   
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		
			String strday = day+"";
			String strmonth = month+"";
			
			if(day<10){
				strday = "0"+strday;
			}
			if(month<10){
				strmonth = "0"+strmonth;
			}
			//return dateTostr(DateUtils.addDays(strTodate(year+"-"+strmonth+"-"+strday),20));
			return year+"-"+strmonth+"-"+strday;
	}
	
	/**
	 * 获取当前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strDate(){
			int day = Calendar.getInstance().get(Calendar.DATE);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			String strday = day+"";
			String strmonth = month+"";
			
			if(day<10){
				strday = "0"+strday;
			}
			if(month<10){
				strmonth = "0"+strmonth;
			}	
			return year+"-"+strmonth+"-"+strday;
	}
}

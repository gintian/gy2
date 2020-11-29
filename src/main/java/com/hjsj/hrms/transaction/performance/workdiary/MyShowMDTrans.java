package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MyShowMDTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		if(this.userView.getA0100()==null||userView.getA0100().trim().length()<1)
			throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		reqhm.remove("search");//xuj 2009-11-10 去除search参数我的日志浏览界面的本月和时间范围单选按钮只会地位在本月
		WeekUtils weekUtils = new WeekUtils();
		GregorianCalendar calendar = new GregorianCalendar(); 
		String yearnum=(String)hm.get("yearnum");
		yearnum=yearnum!=null&&yearnum.length()>3?yearnum:(calendar.get(Calendar.YEAR))+""; 
		String zxgflag = (String) reqhm.get("zxgflag");//铁血网绩效  赵旭光 2013-35
		reqhm.remove("zxgflag");
		String monthnum=(String)hm.get("monthnum");
		monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:(calendar.get(Calendar.MONTH)+1)+"";
		//2015/12/28 wangjl 防止接收到monthnum=0时出错
		monthnum="0".equals(monthnum)?"1":monthnum;
		hm.put("state","0");
		ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		//过滤掉叶面不显示内容
		ArrayList filterlist=this.filteritem(fieldlist);

		hm.put("yearnum",yearnum);
		hm.put("monthnum",monthnum);
		hm.put("yearlist",yearList(calendar.get(GregorianCalendar.YEAR)));
		hm.put("monthlist",monthList(calendar.get(GregorianCalendar.MONTH)));
		hm.put("tablestr",getTabele(filterlist,Integer.parseInt(yearnum),Integer.parseInt(monthnum)));
		hm.put("fieldlist",filterlist);
		hm.put("zxgflag",zxgflag);
	}
	/**
	 * 根据后台设置的显示＆隐藏进行控制
	 * @param fieldlist
	 * @return
	 */
	public ArrayList filteritem(ArrayList fieldlist ){
		ArrayList fieldlist1=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append("p0100");
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(buf.indexOf(field.getItemid().toLowerCase())!=-1)
			{
				fieldlist1.add(field);
				continue;
			}
			if("A0101".equalsIgnoreCase(field.getItemid())|| "E0122".equalsIgnoreCase(field.getItemid())|| "E01A1".equalsIgnoreCase(field.getItemid()))
				continue;
//			if(field.isVisible())
				fieldlist1.add(field);
		}
		return fieldlist1;
	}
	/**
	 * 获取当月周报的table字符串
	 * @param fieldlist
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  tablebuf
	 * */
	public String getTabele(ArrayList fieldlist,int yearnum,int monthnum){
		StringBuffer tablebuf = new StringBuffer();
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		wss.checkState(this.frameconn);
		UserView uv=this.getUserView();
		String weekstr[] = {"日","一","二","三","四","五","六"}; 

		tablebuf.append("<table width=\"98%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"0\" class=\"ListTableF\">");
		tablebuf.append("<tr>");
		for(int i=0;i<weekstr.length;i++){
//			if(i==weekstr.length-1){
//				tablebuf.append("<td align=\"center\" class=\"RecordTop2 common_background_color common_border_color\" nowrap>"+weekstr[i]+"</td>");
//			}else{
//				tablebuf.append("<td align=\"center\" class=\"RecordTop1 common_background_color common_border_color\" nowrap>"+weekstr[i]+"</td>");
//			}
			tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+weekstr[i]+"</td>");
		}
		tablebuf.append("</tr>");
		try {
			String monthstr = monthnum+"";
			if(monthnum<10)
				monthstr = "0"+monthnum;
			GregorianCalendar cal = new GregorianCalendar(); 
			WeekUtils weekutils = new WeekUtils();
			String startime=yearnum+"-"+monthstr+"-01";
			String endtime=weekutils.lastMonthStr(yearnum,monthnum);	
			cal.setTime(weekutils.strTodate(startime));
			int startWeekDay = cal.get(Calendar.DAY_OF_WEEK)-1;
			cal.setTime(weekutils.strTodate(endtime));
			int endWeekDay = cal.get(Calendar.DAY_OF_MONTH);
			String[] sql=wss.getMyworkdiaryshow(uv.getA0100(),fieldlist,startime,endtime,"0");
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append(sql[0]);
			sqlstr.append(" "+sql[1]+" ");
			sqlstr.append(" order by p0104");
			HashMap hm = resultValue(sqlstr.toString(),yearnum,monthnum);
			int n=0;
			String str[] = null;
			String times = "";
			
			for(int i=1;i<=(((endWeekDay+startWeekDay)/7)+((endWeekDay+startWeekDay)%7>0?1:0));i++){//一共有多少行

				//int row = (((endWeekDay+startWeekDay)/7)+((endWeekDay+startWeekDay)%7>0?1:0));
				tablebuf.append("<tr class=\"trShallow\">");
				for(int j=1;j<=weekstr.length;j++){//遍历每一行中的单元格
					if((i==1&&startWeekDay>=j&&startWeekDay<7)||(endWeekDay<((i-1)*7+j-startWeekDay))){//如果单元格中没有数据
						tablebuf.append("<td align=\"center\" onClick=\"javascript:tr_onclick(this,'')\" ");
//						if(i==row && j==weekstr.length)
//							tablebuf.append("class=\"RecordRowLast common_border_color\" nowrap>");
//						else
//							tablebuf.append("class=\"RecordRow1\" nowrap>");
						tablebuf.append("class=\"RecordRow_left\" nowrap>");
						tablebuf.append("&nbsp;");
						tablebuf.append("</td>");
					}else{//如果单元格中有数据
						n = (i-1)*7+j-(startWeekDay<7?startWeekDay:0);
						String nstr = n+"";
						if(n<10)
							nstr = "0"+n;	
						times = yearnum+"-"+monthstr+"-"+nstr;
						times = PubFunc.encryption(times);
						str = (String[])hm.get("date"+n);
						tablebuf.append("<td onClick=\"javascript:tr_onclick(this,'')\" ");
//						if(j==weekstr.length){
//							tablebuf.append("class=\"RecordRow2\" nowrap>");
//						}else{
//							tablebuf.append("class=\"RecordRow1\" nowrap>");
//						}
						tablebuf.append("class=\"RecordRow_left\" style=\"padding:0px;\" nowrap>");
						tablebuf.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=0 height=\"100%\">");
						if(str!=null&&str.length==4){
							if(str[2]!=null&& "02".equals(str[2])){
								tablebuf.append("<tr bgcolor=\"#F5F5DC\">");
								tablebuf.append("<td width=\"40%\">");
								tablebuf.append(n);
								tablebuf.append("</td>");
								tablebuf.append("<td>");
								tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do");
								tablebuf.append("?b_add=link&query=own&state="+PubFunc.encryption("0")+"&timestr="+times+"&p0100="+str[0]+"\">");
								tablebuf.append("报批</a>");
							}else if(str[2]!=null&& "03".equals(str[2])){
								tablebuf.append("<tr bgcolor=\"#FFE4C4\">");
								tablebuf.append("<td width=\"40%\">");
								tablebuf.append(n);
								tablebuf.append("</td>");
								tablebuf.append("<td>");
								tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do");
								tablebuf.append("?b_add=link&query=own&state="+PubFunc.encryption("0")+"&timestr="+times+"&p0100="+str[0]+"\">");
								tablebuf.append("已批</a>");
							}else if(str[2]!=null&& "01".equals(str[2])){
								tablebuf.append("<tr bgcolor=\"#E6E6FA\">");
								tablebuf.append("<td width=\"40%\">");
								tablebuf.append(n);
								tablebuf.append("</td>");
								tablebuf.append("<td>");
								tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do");
								tablebuf.append("?b_add=link&query=update&state="+PubFunc.encryption("0")+"&timestr="+times+"&p0100="+str[0]+"\">");
								tablebuf.append("起草</a>");
							}else if(str[2]!=null&& "07".equals(str[2])){
								tablebuf.append("<tr bgcolor=\"#FFC0CB\">");
								tablebuf.append("<td width=\"40%\">");
								tablebuf.append(n);
								tablebuf.append("</td>");
								tablebuf.append("<td>");
								tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do");
								tablebuf.append("?b_add=link&query=update&state="+PubFunc.encryption("0")+"&timestr="+times+"&p0100="+str[0]+"\">");
								tablebuf.append("驳回</a>");
							}else{
								tablebuf.append("<tr bgcolor=\"#FFDAB9\">");
								tablebuf.append("<td width=\"40%\">");
								tablebuf.append(n);
								tablebuf.append("</td>");
								tablebuf.append("<td>");
								tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do?b_add=link&query=add&state="+PubFunc.encryption("0")+"&timestr="+times+"\">");
								tablebuf.append("未填</a>");
							}
						}else{
							tablebuf.append("<tr>");
							tablebuf.append("<td width=\"40%\">");
							tablebuf.append(n);
							tablebuf.append("</td>");
							tablebuf.append("<td>");
							tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do?b_add=link&query=add&state="+PubFunc.encryption("0")+"&timestr="+times+"\">");
							tablebuf.append("未填</a>");
						}
						tablebuf.append("</td></tr>");
						tablebuf.append("</table>");
						tablebuf.append("</td>");
					} //如果单元格中有数据 结束
				} //遍历每一行 结束
				tablebuf.append("</tr>");

			} //外层for循环结束  遍历全部结束
			tablebuf.append("</table>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tablebuf.toString();
	}
	private ArrayList yearList(int yearmum){
		ArrayList yearlist=new ArrayList();
		for(int i=yearmum;i>=yearmum-15;i--){
			CommonData obj=new CommonData(i+"",i+"");
			yearlist.add(obj);
		}
		return yearlist;
	}
	private ArrayList monthList(int monthnum){
		ArrayList monthlist=new ArrayList();
		for(int i=1;i<=12;i++){
			CommonData obj=new CommonData(i+"",i+"");
			monthlist.add(obj);
		}
		return monthlist;
		
	}
	private HashMap resultValue(String sql,int yearnum,int monthnum){
		WeekUtils weekutils = new WeekUtils();
		GregorianCalendar gre = weekutils.lasGretMonth(yearnum, monthnum);
		ContentDAO dao = new ContentDAO(this.frameconn);
		HashMap hashMap = new HashMap();
		try {
			this.frowset = dao.search(sql);
			Date p0104 = null;
			String p0115 = "";
			String p0100 = "";
			while(this.frowset.next()){
				p0100 = this.frowset.getString("p0100");
				p0104 = this.frowset.getDate("p0104");
				p0115 = this.frowset.getString("p0115");
				gre.setTime(p0104);
				String str[] = new String[4];
				str[0] = PubFunc.encrypt(p0100);
				str[1] = gre.get(Calendar.YEAR)+"-"+(gre.get(Calendar.MONTH)+1)+"-"+gre.get(Calendar.DAY_OF_MONTH);
				str[2] = p0115;
				str[3] = gre.get(Calendar.DAY_OF_WEEK)+"";
				hashMap.put("date"+gre.get(Calendar.DAY_OF_MONTH), str);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashMap;
	}

}

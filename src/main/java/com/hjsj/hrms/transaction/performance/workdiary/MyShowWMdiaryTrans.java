package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.*;

public class MyShowWMdiaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		try{
			if(this.userView.getA0100()==null||userView.getA0100().trim().length()<1)
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
			ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
			//		过滤掉页面不显示内容
			ArrayList filterlist=this.filteritem(fieldlist);

			WeekUtils weekUtils = new WeekUtils();
			GregorianCalendar  calendar = weekUtils.numWeekcal(7);
			String yearnum=(String)hm.get("yearnum");
			yearnum=yearnum!=null&&yearnum.length()>3?yearnum:(calendar.get(Calendar.YEAR))+""; 

			String monthnum=(String)hm.get("monthnum");
			monthnum=monthnum!=null?monthnum:(calendar.get(Calendar.MONTH)+1)+"";

			String state = (String)reqhm.get("state");
			state = PubFunc.decryption(state);			
			reqhm.remove("state");
			state=state!=null?state:""; 
			if(state.length()<1){
				state = (String)hm.get("state");
				state=state!=null?state:"1"; 
			}
			hm.put("state",state);

			hm.put("fieldlist",fieldlist);
			if("1".equals(state)){//如果是周报
				hm.put("tablestr",getTabele(filterlist,Integer.parseInt(yearnum),Integer.parseInt(monthnum)));
				hm.put("yearlist",yearList(calendar.get(GregorianCalendar.YEAR)));
				hm.put("monthlist",monthList(calendar.get(GregorianCalendar.MONTH)));
				hm.put("yearnum",yearnum);
				hm.put("monthnum",monthnum);
			}else{//如果是月报
				hm.put("tablestr",getMonthtable(filterlist,Integer.parseInt(yearnum)));
				hm.put("yearlist",yearList(calendar.get(GregorianCalendar.YEAR)));
				hm.put("yearnum",yearnum);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public ArrayList filteritem(ArrayList fieldlist ){
		ArrayList item=new ArrayList();
		item.add("nbase");
		item.add("a0101");
		item.add("e0122");
		item.add("e01a1");
		item.add("b0110");
		item.add("a0100");
		item.add("p0107");
		item.add("p0108");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			
			for(int j=0;j<item.size();j++){
				String itemid=(String) item.get(j);
				if(field.getItemid().equals(itemid)){
					fieldlist.remove(i);
					i--;
				}
			}
		}
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			String tempstr=tempitem.getItemid();
			if("p0115".equals(tempstr)){
				fieldlist.add(0,tempitem);
				fieldlist.remove(i+1);
			}else if(!tempitem.isVisible()&&!"p0100".equals(tempstr)){
				fieldlist.remove(i);
				i--;
			}
		}
		return fieldlist;
		
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
		String weekstr[] = {ResourceFactory.getProperty("performance.workdiary.one.week"),
							ResourceFactory.getProperty("performance.workdiary.two.week"),
							ResourceFactory.getProperty("performance.workdiary.three.week"),
							ResourceFactory.getProperty("performance.workdiary.four.week"),
							ResourceFactory.getProperty("performance.workdiary.five.week"),
							ResourceFactory.getProperty("performance.workdiary.six.week")}; 
		
		tablebuf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"ListTableF\" >");
		tablebuf.append("<tr><td align=\"center\" width=\"30\" class=\"TableRow\" nowrap>");
		tablebuf.append("<input type=\"checkbox\" name=\"0\" value=\"true\" title=\""+ResourceFactory.getProperty("hire.alldata.initial")+"\" onclick=\"selAll(this)\"></td>");
		tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>");
		tablebuf.append(ResourceFactory.getProperty("label.serialnumber")+"</td>");
		tablebuf.append("<td align=\"center\" width=\"30\" class=\"TableRow\" nowrap>");
		tablebuf.append(ResourceFactory.getProperty("column.operation")+"</td>");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			if(!"p0100".equals(tempitem.getItemid())){
				if("D".equalsIgnoreCase(tempitem.getItemtype())){
					tablebuf.append("<td align=\"center\" width=\""+tempitem.getItemlength()*9+"\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
				}else if("A".equalsIgnoreCase(tempitem.getItemtype())){
					if("p0115".equalsIgnoreCase(tempitem.getItemid())){
						tablebuf.append("<td align=\"center\" width=\"80\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
					}else
						tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
				}else
					tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
			}
		}
		tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("general.impev.comment")+"</td>");
		tablebuf.append("</tr>");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		WeekUtils weekutils = new WeekUtils();
		int totalweek = weekutils.totalWeek(yearnum,monthnum);
		for(int i=1;i<=totalweek;i++){
			try {
				Date startdate = weekutils.numWeek(yearnum,monthnum,i,1);
				Date enddate = weekutils.numWeek(yearnum,monthnum,i,7);
				String startime = weekutils.dateTostr(startdate);
				String endtime = weekutils.dateTostr(enddate);
				
				String[] sql=wss.getMyworkdiaryshow(uv.getA0100(),fieldlist,startime,endtime,"1");
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append(sql[0]);
				sqlstr.append(" "+sql[1]+" ");
				sqlstr.append(" "+sql[3]+" ");
				
				this.frowset=dao.search(sqlstr.toString());
				if(this.frowset.next()){
					if(i%2==1)
						tablebuf.append("<tr class=\"trShallow\">");
					else
						tablebuf.append("<tr class=\"trDeep\">");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					if("07".equals(this.frowset.getString("p0115"))|| "01".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<input type=\"checkbox\" name=\""+this.frowset.getString("p0100")+"\" value="+i+"></td>");
					}else{
						tablebuf.append("&nbsp;</td>");
					}
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>"+weekstr[i-1]+"</td>");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					if("01".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<a href=\"###\" onclick=\"updatediary('"+this.frowset.getString("p0100")+"','"+PubFunc.encryption(this.frowset.getString("p0100"))+"')\">"+ResourceFactory.getProperty("label.edit.user")+"</a>");
					}else if("07".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<a href=\"###\" onclick=\"updatediary('"+this.frowset.getString("p0100")+"','"+PubFunc.encryption(this.frowset.getString("p0100"))+"')\">"+ResourceFactory.getProperty("label.edit.user")+"</a>");
					}else{
						tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do?b_add=link&query=own&state="+PubFunc.encryption("1")+"&p0100=");
						tablebuf.append(PubFunc.encryption(this.frowset.getString("p0100"))+"\">");
						tablebuf.append(ResourceFactory.getProperty("label.view")+"</a>");
					}
					tablebuf.append("</td>");
					for(int j=0;j<fieldlist.size();j++){
						FieldItem tempitem=(FieldItem)fieldlist.get(j);
						if(!"p0100".equals(tempitem.getItemid())){
							if("p0115".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
								if("01".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status1"));
								}else if("02".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status2"));
								}else if("03".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status3"));
								}else if("07".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("button.reject"));
								}else{
									tablebuf.append(ResourceFactory.getProperty("edit_report.status.wt"));
								}
								tablebuf.append("</td>");
							}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else if("p0116".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else if("p0114".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else {
								if("M".equalsIgnoreCase(tempitem.getItemtype())){
									String memo = this.frowset.getString(tempitem.getItemid());
									memo=memo!=null&&memo.length()>0?memo.replaceAll("\r\n","<br>"):"";
									String text = memo;
									memo=memo.length()>30?memo.substring(0,30)+"...":memo;
									tablebuf.append("<td  onmouseover=\"outContent('");
									tablebuf.append(SafeCode.encode(text));
									tablebuf.append("');\" onmouseout=\"tt_HideInit()\"");
									tablebuf.append("class=\"RecordRow\" nowrap style=\"word-break: break-all;word-wrap:break-word;\">");
									tablebuf.append(memo);
									tablebuf.append("</td>");
									
								}else{
									String memo = this.frowset.getString(tempitem.getItemid());
									memo=memo!=null&&memo.length()>0?memo:"";
									tablebuf.append("<td class=\"RecordRow\" nowrap style=\"word-break: break-all; word-wrap:break-word;\">");
									tablebuf.append(memo);
									tablebuf.append("</td>");
								}
							}
						}
					}
					if(!"01".equals(this.frowset.getString("p0115"))&&!"07".equals(this.frowset.getString("p0115")))
						tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap><img src=\"/images/view.gif\" border=0 onclick=\"view('"+PubFunc.encryption(this.frowset.getString("p0100"))+"');\"></td>");
					else
						tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>&nbsp;</td>");
				}else{
					if(i%2==1)
						tablebuf.append("<tr class=\"trShallow\">");
					else
						tablebuf.append("<tr class=\"trDeep\">");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					tablebuf.append("&nbsp;</td>");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>"+weekstr[i-1]+"</td>");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					tablebuf.append("<a href=\"###\" onclick=\"addiary(1,'"+startime+"','"+endtime+"','"+i+"')\">"+ResourceFactory.getProperty("performance.workdiary.no.wt.info")+"</a>");
					tablebuf.append("</td>");
					for(int j=0;j<fieldlist.size();j++){
						FieldItem tempitem=(FieldItem)fieldlist.get(j);
						if(!"p0100".equals(tempitem.getItemid())){
							if("p0115".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
								tablebuf.append(ResourceFactory.getProperty("edit_report.status.wt"));
								tablebuf.append("</td>");
							}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(startime);
								tablebuf.append("</td>");
							}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(endtime);
								tablebuf.append("</td>");
							}else{
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append("&nbsp;");
								tablebuf.append("</td>");
							}
						}
						
					}
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>&nbsp;</td>");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tablebuf.append("</tr>");
		}
		tablebuf.append("</table>");
		return tablebuf.toString();
	}
	/**
	 * 获取当年月报的table字符串
	 * @param fieldlist
	 * @param yearnum 年
	 * @return  tablebuf
	 * */
	public String getMonthtable(ArrayList fieldlist,int yearnum){
		StringBuffer tablebuf = new StringBuffer();
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		wss.checkState(this.frameconn);
		UserView uv=this.getUserView();
		
		tablebuf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"ListTableF\">");
		tablebuf.append("<tr><td align=\"center\" width=\"30\" class=\"TableRow\" nowrap><input type=\"checkbox\" name=\"0\" title=\""+ResourceFactory.getProperty("hire.alldata.initial")+"\" value=\"true\" onclick=\"selAll(this)\"></td>");
		tablebuf.append("<td align=\"center\" width=\"30\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("label.serialnumber")+"</td>");
		tablebuf.append("<td align=\"center\" width=\"30\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("column.operation")+"</td>");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			if(!"p0100".equals(tempitem.getItemid())){
				if("D".equalsIgnoreCase(tempitem.getItemtype())){
					tablebuf.append("<td align=\"center\" width=\""+tempitem.getItemlength()*9+"\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
				}else if("p0115".equalsIgnoreCase(tempitem.getItemid())){
					tablebuf.append("<td align=\"center\" width=\"80\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");
				}else
					tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+tempitem.getItemdesc()+"</td>");	
			}
		}
		tablebuf.append("<td align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("general.impev.comment")+"</td>");
		tablebuf.append("</tr>");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		WeekUtils weekutils = new WeekUtils();
		for(int i=1;i<=12;i++){
			try {
				Date enddate = weekutils.lastMonth(yearnum,i);
				
				String startime = i>9?yearnum+"-"+i+"-01":yearnum+"-0"+i+"-01";
				String endtime = weekutils.dateTostr(enddate);
				
				String[] sql=wss.getMyworkdiaryshow(uv.getA0100(),fieldlist,startime,endtime,"2");
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append(sql[0]);
				sqlstr.append(" "+sql[1]+" ");
				sqlstr.append(" "+sql[3]+" ");
				
				this.frowset=dao.search(sqlstr.toString());
				if(this.frowset.next()){
					if(i%2==1)
						tablebuf.append("<tr class=\"trShallow\">");
					else
						tablebuf.append("<tr class=\"trDeep\">");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					if("07".equals(this.frowset.getString("p0115"))|| "01".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<input type=\"checkbox\" name=\""+this.frowset.getString("p0100")+"\" value="+i+"></td>");
					}else{
						tablebuf.append("&nbsp;</td>");
					}
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>"+i+ResourceFactory.getProperty("datestyle.month")+"</td>");
					tablebuf.append("<td align=\"center\"  class=\"RecordRow\" nowrap>");
					if("01".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<a href=\"###\" onclick=\"updatediary('"+this.frowset.getString("p0100")+"','"+PubFunc.encryption(this.frowset.getString("p0100"))+"')\">"+ResourceFactory.getProperty("label.edit.user")+"</a>");
					}else if("07".equals(this.frowset.getString("p0115"))){
						tablebuf.append("<a href=\"###\" onclick=\"updatediary('"+this.frowset.getString("p0100")+"','"+PubFunc.encryption(this.frowset.getString("p0100"))+"')\">"+ResourceFactory.getProperty("label.edit.user")+"</a>");
					}else{
						tablebuf.append("<a href=\"/performance/workdiary/myworkdiaryshow.do?b_add=link&query=own&state="+PubFunc.encryption("2")+"&p0100=");
						tablebuf.append(PubFunc.encryption(this.frowset.getString("p0100"))+"\">");
						tablebuf.append(ResourceFactory.getProperty("label.view")+"</a>");
					}
					tablebuf.append("</td>");
					for(int j=0;j<fieldlist.size();j++){
						FieldItem tempitem=(FieldItem)fieldlist.get(j);
						if(!"p0100".equals(tempitem.getItemid())){
							if("p0115".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
								if("01".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status1"));
								}else if("02".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status2"));
								}else if("03".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("label.hiremanage.status3"));
								}else if("07".equals(this.frowset.getString("p0115"))){
									tablebuf.append(ResourceFactory.getProperty("button.reject"));
								}else{
									tablebuf.append(ResourceFactory.getProperty("edit_report.status.wt"));
								}
								tablebuf.append("</td>");
							}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else if("p0114".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								if(this.frowset.getDate(tempitem.getItemid())!= null);
								tablebuf.append(String.valueOf(this.frowset.getDate(tempitem.getItemid())).substring(0,10));
								tablebuf.append("</td>");
							}else{
								if("M".equalsIgnoreCase(tempitem.getItemtype())){
									String memo = this.frowset.getString(tempitem.getItemid());
									memo=memo!=null&&memo.length()>0?memo.replaceAll("\r\n","<br>"):"";
									String text = memo;
									memo=memo.length()>30?memo.substring(0,30)+"...":memo;
									tablebuf.append("<td  onmouseover=\"outContent('");
									tablebuf.append(SafeCode.encode(text));
									tablebuf.append("');\"");
									tablebuf.append(" onmouseout=\"tt_HideInit();\" class=\"RecordRow\" nowrap style=\"word-break: break-all;word-wrap:break-word;\">");
									tablebuf.append(memo);
									tablebuf.append("</td>");
									
								}else{
									String memo = this.frowset.getString(tempitem.getItemid());
									memo=memo!=null&&memo.length()>0?memo:"";
									tablebuf.append("<td class=\"RecordRow\" nowrap style=\"word-break: break-all; word-wrap:break-word;\">");
									tablebuf.append(memo);
									tablebuf.append("</td>");
								}
							}
						}
					}
					if(!"01".equals(this.frowset.getString("p0115"))&&!"07".equals(this.frowset.getString("p0115")))
						tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap><img src=\"/images/view.gif\" border=0 onclick=\"view('"+PubFunc.encryption(this.frowset.getString("p0100"))+"');\"></td>");
					else
						tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>&nbsp;</td>");
				}else{
					if(i%2==1)
						tablebuf.append("<tr class=\"trShallow\">");
					else
						tablebuf.append("<tr class=\"trDeep\">");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					tablebuf.append("&nbsp;</td>");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>"+i+ResourceFactory.getProperty("datestyle.month")+"</td>");
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
					tablebuf.append("<a href=\"###\" onclick=\"addiary(2,'"+startime+"','"+endtime+"','"+i+"')\">"+ResourceFactory.getProperty("performance.workdiary.no.wt.info")+"</a>");
					tablebuf.append("</td>");
					for(int j=0;j<fieldlist.size();j++){
						FieldItem tempitem=(FieldItem)fieldlist.get(j);
						if(!"p0100".equals(tempitem.getItemid())){
							if("p0115".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
								tablebuf.append(ResourceFactory.getProperty("edit_report.status.wt"));
								tablebuf.append("</td>");
							}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(startime);
								tablebuf.append("</td>");
							}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append(endtime);
								tablebuf.append("</td>");
							}else{
								tablebuf.append("<td class=\"RecordRow\" nowrap>");
								tablebuf.append("&nbsp;");
								tablebuf.append("</td>");
							}
						}
						
					}
					tablebuf.append("<td align=\"center\" class=\"RecordRow\" nowrap>&nbsp;</td>");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tablebuf.append("</tr>");
		}
		tablebuf.append("</table>");
		return tablebuf.toString();
	}
	public ArrayList yearList(int yearmum){
		ArrayList yearlist=new ArrayList();
		for(int i=yearmum;i>=yearmum-15;i--){
			CommonData obj=new CommonData(i+"",i+"");
			yearlist.add(obj);
		}
		return yearlist;
	}
	public ArrayList monthList(int monthnum){
		ArrayList monthlist=new ArrayList();
		for(int i=1;i<=12;i++){
			CommonData obj=new CommonData(i+"",i+"");
			monthlist.add(obj);
		}
		return monthlist;
		
	}
	
}

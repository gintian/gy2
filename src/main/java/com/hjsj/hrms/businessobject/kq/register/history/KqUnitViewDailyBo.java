package com.hjsj.hrms.businessobject.kq.register.history;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 个人考勤薄
 * @author Owner
 * wangyao
 */
public class KqUnitViewDailyBo {
	private Connection conn=null;
	private boolean kqtablejudge;  //首钢增加 true展现本月出缺勤情况统计小计，否则原始
	private ReportParseVo parsevo=null;
	private UserView userView=null;
	private HashMap codemap = new HashMap();
	
	/**初始化*/
	public KqUnitViewDailyBo(Connection conn)
	{
		this.conn=conn;
		
	}
	/**
	 * 
	 * @param codea0100 员工编号
	 * @param kind 
	 * @param kq_duration 考勤日期
	 * @param parsevo    xml参数
	 * @param userView
	 * @param formHM
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getKqReportHtml(String codea0100,String kind,String kq_duration,ReportParseVo parsevo,UserView userView,HashMap formHM,String userbase,String username)throws GeneralException
	{
		 ArrayList htmlList = new ArrayList();
		 KqViewDailyBo kqviewdailybo = new KqViewDailyBo();
		 try
		 {
			 this.kqtablejudge=kqviewdailybo.getkqtablejudge(this.conn);  //考勤表：true=展现本月出缺勤情况统计小计 false=不展现
			 this.parsevo=parsevo;	
			 this.userView=userView;
			 
			 KqReportInit kqReprotInit = new KqReportInit(this.conn);
			 ArrayList item_list=kqReprotInit.getKq_Item_list();//考勤参数,低部的符号；
			 double spare_h=getSpareHieght(item_list); //剩余高度
			 int pagesize=0;
			 if("#pr[1]".equals(parsevo.getBody_pr())&&parsevo.getBody_rn()!=null&&parsevo.getBody_rn().length()>0)
			 {
			    	pagesize=Integer.parseInt(parsevo.getBody_rn());
			 }else
			 {
			    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+13);
			    	//pagesize=1;
			 }
			 ArrayList datelist=getDateList(this.conn,kq_duration); //日期生成表头
			 int curpage = 1; //当前页面；这个永远只有1页
			 int sum_page = 1; //总页数
			 
			 StringBuffer html= new StringBuffer();
			 String titleHtml=getTableTitle();  //得到表头
			 String width=getFactWidth();  //宽度
			 
			 String headHtml=getTableHead(codea0100,kq_duration,curpage,sum_page,datelist,kqtablejudge,userbase);
			 html.append(" <table   border='0' cellspacing='0'  align='center' cellpadding='0'");			
			 html.append(" class='BackText' ");
			 html.append(" style='position:absolute;top: "+parsevo.getTop()+"");		    
			 html.append(";left: "+parsevo.getLeft()+";width: "+width+"'> \n "); 				
			 html.append(" <tr valign='middle' align='center'> \n ");
			 html.append(" <td >");
			 html.append(titleHtml);//标题信息
			 html.append("</td></tr><tr><td>");		    
			 html.append(headHtml);//表头信息
			 html.append("</td></tr><tr><td>");
			 //body信息	
			 html.append(getBodyHtml(codea0100,userbase,username,datelist,kqtablejudge,item_list,kq_duration));
			 
			 html.append("</td></tr><tr><td>");
			 html.append(getTileHtml(curpage,sum_page,item_list)); //得到表尾数据
			 html.append("</td></tr>");
			 html.append("<tr><td>");
			 String turnTable=getTurnPageCode(curpage,"",sum_page);
			 html.append(turnTable);
			 html.append("</td></tr></table>");
			 htmlList.add(html.toString());
			 
			 htmlList.add("");
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return htmlList;
	}
	/**
	 * 得到标题内容
	 * 
	 * */	 
	public String getTableTitle()	
	{
		KqViewDailyBo kqviewdailybo = new KqViewDailyBo();
        String width=getFactWidth();
        String height=getPxFormMm(parsevo.getTitle_h());
        String sytle=kqviewdailybo.getFontStyle(parsevo.getTitle_fn(),parsevo.getTitle_fi(),parsevo.getTitle_fu(),parsevo.getTitle_fb(),parsevo.getTitle_fz());
        String style_name=kqviewdailybo.getStyleName("3");
        StringBuffer titleTable=new StringBuffer("");		
		String[] temp=transAlign(7);
		String aValign=temp[0];
		String aAlign=temp[1];
//		titleTable.append(" <table  cellspacing='0'  align='left'  valign='top' cellpadding='0' border='0'> \n");
		
		// 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun  2010.4.9
		titleTable.append(" <table width='100%'  cellspacing='0'  align='left'  valign='top' cellpadding='0' border='0'> \n");
				
		titleTable.append(" <tr valign='middle' align='center'> \n ");
		titleTable.append(" <td width='"+width+"' height='"+height+"'");
		titleTable.append(" class='"+style_name+"' ");		
		titleTable.append(" valign='");
		titleTable.append(aValign);
		titleTable.append("' align='");
		titleTable.append(aAlign);
		titleTable.append("'> \n ");   
	    
		titleTable.append(" <font face='");
		if(parsevo.getTitle_fn()!=null&&parsevo.getTitle_fn().length()>0)
		{
			titleTable.append(parsevo.getTitle_fn());
		}
		else
		{
			titleTable.append(parsevo.getName());
		}
		titleTable.append("' style='");
		titleTable.append(sytle);
		titleTable.append("' > \n ");
		if(parsevo.getTitle_fw()==null||parsevo.getTitle_fw().length()<=0)
		{
			titleTable.append(parsevo.getName());
		}else
		{
			titleTable.append(parsevo.getTitle_fw());
		}			
		titleTable.append("</font></td></tr></table> \n ");        
        return titleTable.toString();
	}
	/**
	 * 
	 * @param codea0100
	 * @param userbase 人员库
	 * @param username
	 * @param datelist 日期1-31号
	 * @param kqtablejudge =true 展现本月出勤小计
	 * @param item_list 符号
	 * @param kq_duration 2009-11
	 * @return
	 * @throws GeneralException
	 */
	public String getBodyHtml(String codea0100,String userbase,String username,ArrayList datelist,boolean kqtablejudge,ArrayList item_list,String kq_duration)throws GeneralException
	{
		StringBuffer bodyhtml = new StringBuffer();
		int body_hieght=Integer.parseInt(parsevo.getBody_fz())+13;  //高度
		String fact_width=getFactWidth();  //表格宽度
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
		bodyhtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
		bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyHeadHtml(datelist));  //得到body头内容，日期第一个只是1到15号
    	bodyhtml.append("</tr>"); 
    	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyNameHtml(username,datelist));  //日期下面的人名,第一次
    	bodyhtml.append("</tr>");
    	bodyhtml.append("<tr>");
    	String one_date=getOneA0100Data(codea0100,userbase,datelist,body_hieght,item_list,"1");//内容第一次1到15号
    	bodyhtml.append(one_date);
    	bodyhtml.append("</tr>");
    	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyHeadEndHtml(datelist));  //得到body头内容，日期第二个循环只是16号到月末
    	bodyhtml.append("</tr>"); 
    	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyNameEndHtml(username,datelist));  //日期下面的人名,第二次循环
    	bodyhtml.append("</tr>");
    	bodyhtml.append("<tr>");
    	String one_dateend=getOneA0100Data(codea0100,userbase,datelist,body_hieght,item_list,"2");//内容第一次1到15号
    	bodyhtml.append(one_dateend);
    	bodyhtml.append("</tr>");
    	if(kqtablejudge)
    	{
    		KqViewDailyBo kqviewdailybo =new KqViewDailyBo();
    		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
    				Constant.USED_FIELD_SET);
        	ArrayList kqq03list=kqviewdailybo.savekqq03list(this.conn,fielditemlist);
        	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
        	bodyhtml.append(getBodyHolsHtml(kqq03list));  //月考勤小计，16 第一次
        	bodyhtml.append("</tr>");
        	bodyhtml.append("<tr>");
        	String one_value=getOneA0100Value(codea0100,userbase,kq_duration,body_hieght,kqq03list);
        	bodyhtml.append(one_value);
        	bodyhtml.append("</tr>");
        	if(kqq03list.size()>16&&kqq03list.size()!=16)
        	{
        		bodyhtml.append(" <tr valign='middle' align='center'> \n ");
        		bodyhtml.append(getBodyHolsEndHtml(kqq03list));  //月考勤小计，16 第二次
        		bodyhtml.append("</tr>");
        		bodyhtml.append("<tr>");
            	String one_valueEnd=getOneA0100ValueEnd(codea0100,userbase,kq_duration,body_hieght,kqq03list);
            	bodyhtml.append(one_valueEnd);
            	bodyhtml.append("</tr>");
        	}
        	if(kqq03list.size()>32)
        	{
        		bodyhtml.append(" <tr valign='middle' align='center'> \n ");
        		bodyhtml.append(getBodyHolsEndHtml3(kqq03list));  //月考勤小计，16 第三次
        		bodyhtml.append("</tr>");
        		bodyhtml.append("<tr>");
            	String one_valueEnd=getOneA0100ValueEnd3(codea0100,userbase,kq_duration,body_hieght,kqq03list);
            	bodyhtml.append(one_valueEnd);
            	bodyhtml.append("</tr>");
        	}
    	}
    	bodyhtml.append("</table>");
		return bodyhtml.toString();
	}
	/**
	 *  得到表头画第一个日期
	 * @param datelist 日期第一个只是1到15号
	 * @return
	 * @throws GeneralException
	 */
	public String getBodyHeadHtml(ArrayList datelist)throws GeneralException
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","日期",getStyleName("6")));
    	for(int i=1;i<=datelist.size();i++)
    	{
    		CommonData vo = (CommonData)datelist.get(i-1);		
        	String date=vo.getDataValue(); 
        	if(i<=15)
        	{
        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("6")));	
        	}else
        	{
        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        		break;
        	}
        	
    	}
		return bodyheadhtml.toString();
	}
	/**
	 * 得到头，循环第二个循环 是从16号到月末
	 * @param datelist
	 * @return
	 * @throws GeneralException
	 */
	public String getBodyHeadEndHtml(ArrayList datelist)throws GeneralException
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","日期",getStyleName("6")));
    	for(int i=16;i<=datelist.size();i++)
    	{
    		CommonData vo = (CommonData)datelist.get(i-1);		
        	String date=vo.getDataValue(); 
        	if(datelist.size()==31)
        	{
        		if(i==datelist.size())
        		{
        			bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("1")));
        		}else
        		{
        		   bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("6")));	
        		}
        	}else
        	{
        		if(i<=datelist.size())
            	{
            		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("6")));	
            	}else
            	{
            		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
            		break;
            	}
        	}
    	}
    	if(datelist.size()!=31)
    	{
    		if(datelist.size()==28)
        	{
        		 for(int p=1;p<4;p++)
        		 {
        			 if(p==3)
        			 {
        				 bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			 }else
        			 {
        				 bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));	
        			 }
        		 }
        	}else if(datelist.size()==29)
        	{
        		 for(int p=1;p<3;p++)
        		 {
        			 if(p==2)
        			 {
        				 bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			 }else
        			 {
        				 bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));	
        			 }
        		 }
        	}else if(datelist.size()==30)
        	{
        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        	}
    	}
		return bodyheadhtml.toString();
	}
	/**
	 * 本页出勤小计头部分 16个
	 * @param kqq03list
	 * @return
	 */
	public String getBodyHolsHtml(ArrayList kqq03list)
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","假别",getStyleName("6")));
    	String kqtable=SystemConfig.getPropertyValue("kqtable");//用来控制考勤表头竖型展现 0和null都是正常展现
    	for(int i=0;i<=16;i++)
    	{
    		
    		if(i<=kqq03list.size())
    		{
    			if(i==kqq03list.size())
    			{
    				break;
    			}else
    			{
    				for(int p=0;p<kqq03list.size();p++)
            		{
            			FieldItem fielditem=(FieldItem)kqq03list.get(p);		
            			String name = fielditem.getItemdesc();
            			
            			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
            			{
            				if(p<=14)
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}else
            			{
            				if(p<=14)
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}
                    	i++;
            		}
    			}
    		}else
    		{
    			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
    			{
    				if(i==16)
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}else
    			{
    				if(i==16)
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}
    		}
    	}
		return bodyheadhtml.toString();
	}
	public String getBodyHolsEndHtml(ArrayList kqq03list)
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","假别",getStyleName("6")));
    	String kqtable=SystemConfig.getPropertyValue("kqtable");//用来控制考勤表头竖型展现 0和null都是正常展现
    	for(int i=17;i<=33;i++)
    	{
    		if(i<=kqq03list.size())
    		{
    			for(int p=16;p<kqq03list.size();p++)
        		{
    				if(p<=31)
    				{
    					FieldItem fielditem=(FieldItem)kqq03list.get(p);		
            			String name = fielditem.getItemdesc();
            			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
            			{
            				if(p<=31)
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}else
            			{
            				if(p<=31)
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}
//                    	System.out.println("ppp = "+p);
                    	i++;
    				}
        			
        		}
    		}else
    		{
    			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
    			{
    				if(i==33)
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}else
    			{
    				if(i==33)
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}
    		}
//    		System.out.println("123 = "+i);
    	}
    	return bodyheadhtml.toString();
	}
	/**
	 * 本月出勤小计
	 */
	public String getOneA0100Value(String codea0100,String userbase,String kq_duration,int body_hieght,ArrayList kqq03list)
	{
		StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
//    	if(kqq03list.size()<=16)
//    	{
    		for(int i=0;i<kqq03list.size();i++)
        	{
    			if(i<=15)
    			{
    				FieldItem fielditem=(FieldItem)kqq03list.get(i);
            		if(!"i9999".equals(fielditem.getItemid()))
           	    	{
           	    		column.append(""+fielditem.getItemid()+",");  
//           	    		columnlist.add(fielditem.getItemid());
           	    		columnlist.add(fielditem);
           	    	}
    			}
        	}
//    	}else
//    	{
//    		for(int i=16;i<kqq03list.size();i++)
//        	{
//    				FieldItem fielditem=(FieldItem)kqq03list.get(i);
//            		if(!"i9999".equals(fielditem.getItemid()))
//           	    	{
//           	    		column.append(""+fielditem.getItemid()+",");  
//           	    		columnlist.add(fielditem.getItemid());
//           	    	}
//        	}
//    	}
    	StringBuffer one_date=new StringBuffer(); 
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","工日",getStyleName("6")));
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++)
         	{
         		StringBuffer sql_on_a0100=new StringBuffer();
         		FieldItem fielditem = (FieldItem)columnlist.get(t);
         		String itd=fielditem.getItemid();
         		sql_on_a0100.append("select "+itd+" as one from Q05_arc where Q03Z0='"+kq_duration+"' and ");
        		sql_on_a0100.append("nbase='"+userbase+"' and A0100='"+codea0100+"'");
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next())
        		{
        			itemid = rowSet.getString("one");
//        			if(itemid==null&&(itemid.equalsIgnoreCase("0E-8")||itemid.equals("")))
        			if(itemid != null)
        			{
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid))
        				{
        					itemid="";
        				}else
        				{
        					if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
	        					int num = fielditem.getDecimalwidth();
								if(itemid.indexOf(".")!=-1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid,num);
								} else{
									
									 
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										} else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid,num);
									
								}
        					} else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									if (!codemap.containsKey(setid+itemid)) {
										String codesetid = itemid;
										itemid = AdminCode.getCodeName(setid, itemid);
										codemap.put(setid+codesetid, itemid);
									} else {
										itemid = (String) codemap.get(setid+itemid);
									}
								}
							} 
        				}
        			}else
        			{
        				itemid="";
        			}
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
        		}else
        		{
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
//        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        		}
         	}
         	if(columnlist.size()<15)
         	{
         		for(int p=columnlist.size();p<=15;p++)
             	{
             		if(p==15)
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("1")));
             		}else
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("6")));
             		}
             	}
         	}
         	
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	    {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		 } 
    	return one_date.toString();
	}
	public String getOneA0100ValueEnd(String codea0100,String userbase,String kq_duration,int body_hieght,ArrayList kqq03list)
	{
		StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	for(int i=0;i<kqq03list.size();i++)
    	{
    		if(i>15)
    		{
    			if(i<32)
    			{
    				FieldItem fielditem=(FieldItem)kqq03list.get(i);
            		if(!"i9999".equals(fielditem.getItemid()))
           	    	{
           	    		column.append(""+fielditem.getItemid()+",");  
//           	    		columnlist.add(fielditem.getItemid());
           	    		columnlist.add(fielditem);
           	    	}
    			}
    		}
    	}
    	StringBuffer one_date=new StringBuffer(); 
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","工日",getStyleName("6")));
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++)
         	{
         		StringBuffer sql_on_a0100=new StringBuffer();
         		FieldItem fielditem = (FieldItem) columnlist.get(t);
         		String itd=fielditem.getItemid();
         		sql_on_a0100.append("select "+itd+" as one from Q05_arc where Q03Z0='"+kq_duration+"' and ");
        		sql_on_a0100.append("nbase='"+userbase+"' and A0100='"+codea0100+"'");
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next())
        		{
        			itemid = rowSet.getString("one");
//        			if(itemid==null&&(itemid.equalsIgnoreCase("0E-8")||itemid.equals("")))
        			if(itemid != null)
        			{
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid))
        				{
        					itemid="";
        				}else
        				{
//        					int id=(int)(Float.parseFloat(itemid));
//            				itemid=Integer.toString(id);
        					if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
	        					int num = fielditem.getDecimalwidth();
								if(itemid.indexOf(".")!=-1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid,num);
								} else{
									
									 
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										} else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid,num);
									
								}
        					} else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									if (!codemap.containsKey(setid+itemid)) {
										String codesetid = itemid;
										itemid = AdminCode.getCodeName(setid, itemid);
										codemap.put(setid+codesetid, itemid);
									} else {
										itemid = (String) codemap.get(setid+itemid);
									}
								}
							} 
        				}
        			}else
        			{
        				itemid="";
        			}
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
        		}else
        		{
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
//        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        		}
         	}
         	if(columnlist.size()<15)
         	{
         		for(int p=columnlist.size();p<=15;p++)
             	{
             		if(p==15)
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("1")));
             		}else
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("6")));
             		}
             	}
         	}
         	
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
    	return one_date.toString();
	}
	/**
	 * 第一个人员名称
	 * @param username 人员下面的姓名
	 * @return
	 * @throws GeneralException
	 */
	public String getBodyNameHtml(String username,ArrayList datelist)throws GeneralException
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","签名",getStyleName("6")));
    	for(int i=0;i<=datelist.size();i++)
    	{
        	if(i<=14)
        	{
        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",username,getStyleName("6")));	
        	}else
        	{
        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        		break;
        	}
        	
    	}
		return bodyheadhtml.toString();
	}
	/**
	 * 人名第二次循环 从16到月最后
	 * @param username 人员下面的姓名
	 * @return
	 * @throws GeneralException
	 */
	public String getBodyNameEndHtml(String username,ArrayList datelist)throws GeneralException
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","签名",getStyleName("6")));
    	for(int i=16;i<=datelist.size();i++)
    	{
    		if(datelist.size()==31)
    		{
    			if(i==datelist.size())
        		{
    				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",username,getStyleName("1")));
        		}else
        		{
        			bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",username,getStyleName("6")));	
        		}
    		}else
    		{
    			if(i<=datelist.size())
            	{
            		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",username,getStyleName("6")));	
            	}else
            	{
            		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
            		break;
            	}
    		}
    	}
    	if(datelist.size()!=31)
    	{
    		if(datelist.size()==28)
    		{
    			for(int p=1;p<4;p++)
    			{
    				if(p==3)
    				{
    					bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
    				}else
    				{
    					bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
    				}
    			}
    		}else if(datelist.size()==29)
    		{
    			for(int p=1;p<3;p++)
    			{
    				if(p==2)
    				{
    					bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
    				}else
    				{
    					bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
    				}
    			}
    		}else if(datelist.size()==30)
    			
    		{
    			bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
    		}
    	}
		return bodyheadhtml.toString();
	}
	/**
	 * 转换成字体布局字符
	 */
	private String[] transAlign(int Align)
	{
		String[] temp=new String[2];
		if(Align==0)
		{
			temp[0]="top";
			temp[1]="left";
		}
		else if(Align==1)
		{
			temp[0]="top";
			temp[1]="center";
		}
		else if(Align==2)
		{
			temp[0]="top";
			temp[1]="right";
		}
		else if(Align==3)
		{
			temp[0]="bottom";
			temp[1]="left";
		}
		else if(Align==4)
		{
			temp[0]="bottom";
			temp[1]="center";
		}
		else if(Align==5)
		{
			temp[0]="bottom";
			temp[1]="right";
		}
		else if(Align==6)
		{
			temp[0]="middle";
			temp[1]="left";
		}
		else if(Align==7)
		{
			temp[0]="middle";
			temp[1]="center";
		}
		else if(Align==8)
		{
			temp[0]="middle";
			temp[1]="right";
		}		
		return temp;		
	}
	/**
	 * 如果 id 3三的时候没有，就先接入1的值插入3
	 * @param report_id
	 * @return
	 */
	public ReportParseVo getParseVo(String report_id)
	{
		String kq_xpath="/kq_reports/kq_report";
		ReportParseVo parsevo = new ReportParseVo ();
		String report_sql="Select report_id,name,flag,content from kq_report where report_id='"+report_id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		int tt=1;
		try
		{
			rowSet=dao.search(report_sql);
			String content="";
			if(rowSet.next())
			{
				content=rowSet.getString("content");
			}
			StringBuffer sql=new StringBuffer();
			sql.append("INSERT INTO kq_report (report_id,name,flag,content)VALUES(?,?,?,?)");
			ArrayList list = new ArrayList();
			list.add("3");
			list.add("员工签到薄");
			list.add("-1");
			list.add(content);
			dao.insert(sql.toString(),list);	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
		return parsevo;
	}
	/**
	 * 计算表格实际总宽度
	 * 首钢更改，注销是原始的 
	 * */
	public String getFactWidth()
	{
		String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
		if("1".equals(parsevo.getOrientation().trim()))
		{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}else{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}
//		if("1".equals(parsevo.getOrientation().trim()))
//		{
//			if(unit.equals("px"))
//			{
//				double width;
//				if(this.kqtablejudge)
//				{
//					width=Double.parseDouble(parsevo.getHeight())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}else
//				{
//					width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}
//				return KqReportInit.round(width+"",0);	
//			}else
//			{
//				double width;
//				if(this.kqtablejudge)
//				{
//					width=Double.parseDouble(parsevo.getHeight())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}else
//				{
//					width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}
//				return KqReportInit.round(width+"",0);	
//			}
//		}else{
//			if(unit.equals("px"))
//			{
//				double width;
//				if(this.kqtablejudge)
//				{
//					width=Double.parseDouble(parsevo.getWidth())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}else
//				{
//					width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}
//				return KqReportInit.round(width+"",0);	
//			}else
//			{
//				double width;
//				if(this.kqtablejudge)
//				{
//					width=Double.parseDouble(parsevo.getWidth())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}else
//				{
//					width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
//				}
//				return KqReportInit.round(width+"",0);	
//			}
//		}	
	}
	 /**
     * 转换，毫米转换为像素
     * */
    public String getPxFormMm(String value)
    {
    	String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
    	if("mm".equals(unit))
		{
    		double dv=Double.parseDouble(value)/0.26;
    		return KqReportInit.round(dv+"",0);
		}else
		{
			return KqReportInit.round(value,0);
		}
    }
    /**
     * 以用高度
     * **/
    public double getIsUseHieght(ArrayList item_list)throws GeneralException
    {
       	
    	double height=Double.parseDouble(getPxFormMm(parsevo.getTop()))+Double.parseDouble(getPxFormMm(parsevo.getBottom()))+Double.parseDouble(getPxFormMm(parsevo.getTitle_h()));
    	height=height+Double.parseDouble(getPxFormMm(parsevo.getHead_h()));
    	height=height+Double.parseDouble(parsevo.getBody_fz())+13;
    	   	
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	//计算备注1
    	StringBuffer note_len_str= new StringBuffer();    	
    	note_len_str.append("备注：1.");
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     if("N".equals(fielditem.getItemtype()))
   	     {
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0)
   	    		{
   	    			note_len_str.append(fielditem.getItemdesc()+"("+kq_item[0]+")");
   	   	    	}  	    		
   	    	}
   		  }				
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());      	   	
    	int numrow_tile_1=getNumRow(strlen);      	
    	if(numrow_tile_1!=0)
    	{
    		height=height+(Double.parseDouble(parsevo.getBody_fz())+6)*numrow_tile_1;	
    	}else
    	{
    		height=height+(Double.parseDouble(parsevo.getBody_fz())+6);
    	}
    	
    	//计算表尾客户添加的文本内容
    	if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    	{
    		/*****#代表一个空格****/
    		String tile_fw="#####2."+parsevo.getTile_fw();
    		int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
    		
    		int note_tile_2=getNumRow(str_tile_2);
    		
    		if(note_tile_2!=0)
        	{
        		height=height+(Double.parseDouble(parsevo.getBody_fz())+6)*note_tile_2;	
        	}   		
    	}
    	
    	if("#c".equals(parsevo.getHead_c())||"#p".equals(parsevo.getHead_p())||"#e".equals(parsevo.getHead_e())||"#u".equals(parsevo.getHead_u())||"#d".equals(parsevo.getHead_d())||"#t".equals(parsevo.getHead_t()))
    	{
    		height=height+Double.parseDouble(getPxFormMm(parsevo.getHead_h()));
    	}
    	
    	if("#c".equals(parsevo.getTile_c())||"#p".equals(parsevo.getTile_p())||"#e".equals(parsevo.getTile_e())||"#u".equals(parsevo.getTile_u())||"#d".equals(parsevo.getTile_d())||"#t".equals(parsevo.getTile_t()))
    	{
    		height=height+Double.parseDouble(getPxFormMm(parsevo.getTile_h()));
    	}    	
    	
    	return height;
    }  
    /***
     * 剩余高度
     * */
    
    public double getSpareHieght(ArrayList item_list)throws GeneralException
    {
       	double spare_hieght=0;
       	double height=getIsUseHieght(item_list);
    	String unit=parsevo.getUnit().trim();
    	if("px".equals(unit))
    	{
    		spare_hieght=Double.parseDouble(getFactHeight())-height;
    	}else
    	{
    		spare_hieght=Double.parseDouble(getFactHeight())-height/0.26;
    	}
    	return spare_hieght;
    }
    /**
	 * 计算表格实际总高度
	 * */
	public String getFactHeight()
	{
		String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
		if("1".equals(parsevo.getOrientation().trim()))
		{
			if("px".equals(unit))
			{
				double height=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getTop()))-Double.parseDouble(getPxFormMm(parsevo.getBottom()));
				return KqReportInit.round(height+"",0);	
			}else
			{
				double height=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getTop()))-Double.parseDouble(getPxFormMm(parsevo.getBottom()));
				return KqReportInit.round(height+"",0);	
			}
		}else{
		    if("px".equals(unit))
		    {
			    double height=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getTop()))-Double.parseDouble(getPxFormMm(parsevo.getBottom()));
			    return KqReportInit.round(height+"",0);	
		    }else
		    {
			    double height=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getTop()))-Double.parseDouble(getPxFormMm(parsevo.getBottom()));
			    return KqReportInit.round(height+"",0);	
		    }
		}
	}
	 /**
	 * 计算在规定的字数中，一串字符，有多少行
	 * */
	public int getNumRow(int strlen)
	{
		 int factwidth=Integer.parseInt(getFactWidth());
		 
			 int ss=strlen/factwidth;
			 int dd=strlen%factwidth;
	  	     if(dd!=0)
	  	     {
	  	    	ss=ss+1;
	  	     } 
	  	   return ss; 		 	   
	}
	/**
	 * 得到表头内容 
	 *   #p（页码）  #c 总页数  #e 制作人  #u 制作人所在的单位  #d 日期  #t  时间  #fn宋体 字体名称  #fz15    字体大小
	 *   #fb[0|1]  黑体  #fi[0|1]   斜体  #fu[0|1]   下划线  #pr[0|1]  页行数
	 *   #fh40
	 *   首钢增加 kqtablejudge true展现本月出缺勤情况统计小计
	 * */
	public String getTableHead(String codea0100,String coursedate,int curpage,int sum_page,ArrayList datelist,boolean kqtablejudge,String userbase)throws GeneralException
	{
		StringBuffer headHtml = new StringBuffer();        
        String [] codeitem=getCodeItemDesc(codea0100,userbase);
        String fact_width=getFactWidth();
        String head_height=getPxFormMm(parsevo.getHead_h());
        String sytle=getFontStyle(parsevo.getHead_fn(),parsevo.getHead_fi(),parsevo.getHead_fu(),parsevo.getHead_fb(),parsevo.getHead_fz());
        try{
//           headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
        	// 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun  2010.4.9
        	headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
           headHtml.append(" <tr valign='middle' align='center'> \n ");           
           /**单位**/  
           String dv_content="";
           if(codeitem[0]==null||codeitem[0].length()<=0)
           {
        	   dv_content="&nbsp;&nbsp;单位：";
           }else
           {
        	   dv_content="&nbsp;&nbsp;单位："+codeitem[0];
           }
           
           String dv_style_name=getStyleName("2");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,dv_content,dv_style_name));           
           /**部门*/   
           String bm_content="";
           if(codeitem[1]==null||codeitem[1].length()<=0)
           {
        	   bm_content="&nbsp;&nbsp;部门：";
           }else
           {
        	   bm_content="&nbsp;&nbsp;部门："+codeitem[1];
           }
           
           String bm_style_name=getStyleName("2");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,bm_content,bm_style_name));
            /**日期*/      
           CommonData vo = (CommonData)datelist.get(0);	           
           String start_date=vo.getDataName();
           vo = (CommonData)datelist.get(datelist.size()-1);	 
           String end_date= vo.getDataName();    
           String da_content="&nbsp;&nbsp;  "+coursedate+" ("+start_date+"~"+end_date+")";
           String da_style_name=getStyleName("");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,da_content,da_style_name));
           //首钢 本月出缺勤情况统计小计 true展现
//           if(kqtablejudge)
//           {
//        	   headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,"本月出缺勤情况统计小计",da_style_name));
//           }
           headHtml.append("</tr></table>");
           headHtml.append("</td></tr><tr><td>");
           /**基本参数**/
//           headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
           
        // 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun  2010.4.9
           headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
           
           headHtml.append(" <tr valign='middle' align='center'> \n ");  
           /**制作人**/
           int i=0;
           String temp_p="5";
           if("#e".equals(parsevo.getHead_e().trim()))
           {
        	   
        	   String e_str="&nbsp;&nbsp;制作人: "+this.userView.getUserFullName();
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,e_str,getStyleName(temp_p)));  
           }
           /**制作人单位**/
           if("#u".equals(parsevo.getHead_u().trim()))
           {
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   
        	   String u_code="";
        	   if(!userView.isSuper_admin())
    		   {
    			  if(userView.getUserOrgId()!=null && userView.getUserOrgId().trim().length()>0)
    			  {
    				 u_code=userView.getUserOrgId();
    			  }else
    			  {
    				 u_code=RegisterInitInfoData.getKqPrivCodeValue(userView);
    			  }
    		   }
        	   String [] u_codeitem=getCodeItemDesclow(u_code);
        	   String u_str="&nbsp;制作人单位: "+u_codeitem[0];
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,u_str,getStyleName(temp_p)));
           }
           /**制作日期**/
           if("#d".equals(parsevo.getHead_d().trim()))
           {
        	   
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   
        	   String d_str="&nbsp;&nbsp;制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,d_str,getStyleName(temp_p)));  
           }
           /**制作时间**/
           if("#t".equals(parsevo.getHead_t().trim()))
           {
        	   
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   String t_str="&nbsp;&nbsp;时间: "+PubFunc.getStringDate("HH:mm:ss");
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,t_str,getStyleName(temp_p))); 
           }
           /**页码**/
           if("#p".equals(parsevo.getHead_p().trim()))
           {
        	   
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   String p_str="&nbsp;&nbsp;页码:"+curpage+"";
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,p_str,getStyleName(temp_p))); 
           }
           /**总页码**/
           if("#c".equals(parsevo.getHead_c().trim()))
           {
        	   
        	   if(i==0)
        	   {
        		   temp_p="1";
        		   i=1;
        	   }else
        	   {
        		   temp_p="5";
        	   }
        	   
        	   String c_str="&nbsp;&nbsp;总页码:"+sum_page+"";
        	   headHtml.append(executeTable(1,6,parsevo.getHead_fn(),sytle,head_height,c_str,getStyleName(temp_p))); 
           }
           headHtml.append("</tr></table>");
           /**结束**/
           //headHtml.append("</td></tr></table>");
        }catch(Exception e)
		{
			e.printStackTrace();
		}
        
        return headHtml.toString();	
    }
	/**
	 * 通过code得到codeItemDesc
	 * */
	public String[] getCodeItemDesc(String code,String userbase)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select b0110,e0122 from "+userbase+"A01 where A0100='"+code+"'";
	  String B0110="";
	  String E0122="";
	  String desc="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		B0110 = rowSet.getString("b0110");
	    		E0122 = rowSet.getString("e0122");
	    	}	  
	    	if(!"".equals(B0110)||B0110.length()>0)
	    	{
	    		String sqlb ="select codeitemdesc from organization where  codeitemid='"+B0110+"'";
	    		rowSet=dao.search(sqlb);
	    		if(rowSet.next())
	    		{
	    			desc=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc)||desc.length()>0)
	    		{
	    			codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    	if(!"".equals(E0122)||E0122.length()>0)
	    	{
	    		String sqle ="select codeitemdesc from organization where  codeitemid='"+E0122+"'";
	    		rowSet=dao.search(sqle);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc2)||desc2.length()>0)
	    		{
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
	   return codeItemDesc;
	}
	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName(String temp)
	{
		//处理虚线	L,T,R,B,
	    String style_name="RecordRow_self common_border_color";
	    if("0".equals(temp))
	    {
	    	style_name="RecordRow_self_l common_border_color";
	    }
	    else if("1".equals(temp))
	    {
	    	style_name="RecordRow_self_t common_border_color";
	    }
	    else if("2".equals(temp))
	    {
	    	style_name="RecordRow_self_r common_border_color";
	    }
	    else if("3".equals(temp))
	    {
	    	style_name="RecordRow_self_b common_border_color";
	    }else if("4".equals(temp))
	    {
	    	style_name="RecordRow_self_two common_border_color";
	    }else if("5".equals(temp))
	    {
	    	style_name="RecordRow_self_t_l common_border_color";
	    }else if("6".equals(temp))
	    {
	    	style_name="RecordRow_self_t_r common_border_color";
	    }
	    
		return style_name;
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    public String getFontStyle(String fn,String fi,String fu,String fb,String fz)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	public String executeTable(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-break:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		
		if (context == null || context.trim().length() <= 0) {
			tempTable.append("&nbsp;");
		}
		
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
			tempTable.append(context);	
		}  
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	//字体竖型排
	public String executeTables(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-wrap:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		if (context == null || context.length() <= 0) {
			tempTable.append("&nbsp;");
		}
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
//			System.out.println("TTTT = "+context.length());
			for(int p=0;p<context.length();p++)
			{
				
				String d =context.substring(p,p+1);
				tempTable.append(d);
				tempTable.append("<br>");
			}
//			tempTable.append(context);	
		} 
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	 /******
	 * 通过考勤期间得到考勤日期
	 * @param coursedate 考勤期间
	 * 
	 * @return datelist 日期 只有日期
	 * */
  public ArrayList getDateList(Connection conn,String coursedate)throws GeneralException
  {
  	ArrayList datelist=new ArrayList();
  	RowSet rowSet=null;
      String[] date=coursedate.split("-");
      String kq_year=date[0];
      String kq_duration=date[1];
      String kq_start;
      String kq_dd;
      String sql="SELECT kq_start,kq_end FROM kq_duration where kq_year='"+kq_year+"'and kq_duration='"+kq_duration+"'";
      ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(sql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				int spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				SimpleDateFormat format2 = new SimpleDateFormat("dd");
              for (int i = 0; i <=spacedate; i++) 
              {	CommonData vo = new CommonData();									
					kq_start = format1.format(d1);
					kq_dd=format2.format(d1);
					vo.setDataName(kq_start);
					vo.setDataValue(kq_dd);
					datelist.add(vo);					
					d1=DateUtils.addDays(d1,1);					
              }				
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
		}finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
		return datelist;
  }
  /**
	 * 通过code得到codeItemDesc
	 * */
	public String[] getCodeItemDesclow(String code)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select codeitemdesc,parentid from organization where codeitemid='"+code+"'";
	  String desc1="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		desc1=rowSet.getString("codeitemdesc");
	    		if(desc1==null||desc1.length()<=0) {
                    desc1="";
                }
	    		parentid=rowSet.getString("parentid");
	    		String sqlp="select codeitemdesc from organization where codeitemid='"+parentid+"'";
	    		rowSet=dao.search(sqlp);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
		    		if(desc2==null||desc2.length()<=0) {
                        desc2="";
                    }
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");
	    		}
	    	}	  
	    	if(desc2.equals(desc1))
			{
				codeItemDesc[0]="所有部门";
			}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
	   return codeItemDesc;
	}
	/**
     * 通过一个员工编号得到该员工考勤期间的数据
     * file=1 的时候输出 1到15号
     * file=2 的时候输出 16到最后
     *   姓名在这里加链接
     * */ 
	public String getOneA0100Data(String codea0100,String userbase,ArrayList datelist,int body_hieght,ArrayList item_list,String file)throws GeneralException
	{
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     /*if("N".equals(fielditem.getItemtype()))
   	     {*/
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
   		  }
    	String start_date=""; //开始时间
    	String end_date=""; //结束时间
    	if("1".equalsIgnoreCase(file))
    	{
    		CommonData start_vo = (CommonData)datelist.get(0);		
        	start_date=start_vo.getDataName();   //开始时间
        	start_vo = (CommonData)datelist.get(14);
        	end_date=start_vo.getDataName();     //结束时间
    	}else if("2".equalsIgnoreCase(file))
    	{
    		CommonData start_vo = (CommonData)datelist.get(15);		
        	start_date=start_vo.getDataName();   //开始时间
        	start_vo = (CommonData)datelist.get(datelist.size()-1);
        	end_date=start_vo.getDataName();     //结束时间
    	}
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	StringBuffer one_date=new StringBuffer(); 
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","考勤",getStyleName("6")));
    	ContentDAO dao = new ContentDAO(this.conn);            
        RowSet rowSet=null;
        try{
            String sql_one_a0100=selcet_kq_one_emp(codea0100,userbase,start_date,end_date,column.toString());
         	 rowSet=dao.search(sql_one_a0100);
         	 HashMap kq_item_map = new HashMap();
         	 while(rowSet.next())
            {
        	    String q03z0=rowSet.getString("q03z0").trim();     
        	    ArrayList list =new ArrayList();
        	    ArrayList z1_list=new ArrayList();
        	    ArrayList scq_list=new ArrayList();
        	    HashMap kq_item_all = this.querryKq_item();
        	    int rownum = 0;
        	    for(int i=0;i<fielditemlist.size();i++)
          	    {
         	      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
         	      if("N".equals(fielditem.getItemtype())&&!"i9999".equals(fielditem.getItemid()))
         	      {
           		 if("q03z1".equalsIgnoreCase(fielditem.getItemid())||(fielditem.getItemdesc().indexOf("出勤")!=-1||fielditem.getItemdesc().indexOf("出勤率")!=-1))
           		 {
           			 if("q03z1".equalsIgnoreCase(fielditem.getItemid()))
           			 {
           				 double  dv=rowSet.getDouble(fielditem.getItemid());
       			    	 if(dv!=0)
            		        {
        			        	 String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
              			         z1_list.add(kq_item);
        			        	 z1_list.add(new Double(dv));             			        	
            		         }
           			 }else
           			 {
           				 double  dv=rowSet.getDouble(fielditem.getItemid());
       			    	 if(dv!=0)
            		        {
        			        	 String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
        			        	 scq_list.add(kq_item);
        			        	 scq_list.add(new Double(dv));
        			        	 
             			        
            		         }
           			 }     
           			    	// continue;
           		  }
           		  else
           		  {        String value=rowSet.getString(fielditem.getItemid());
           			       if(value==null||value.length()<=0)
           			       {
           				      value="0";
           			       }
               		       double dv=Double.parseDouble(value);
           			       if(dv!=0)
               		       {
               			      String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);        			         			  
               			      //String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),kq_item[1]);
               			      //one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"",kq_item[0],getStyleName("5")));
               			      ArrayList one_list= new ArrayList();
               			      one_list.add(kq_item);
               			      one_list.add(new Double(dv));
               			      list.add(one_list);
               			      
               			      if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
               			    	rownum ++;
               			      }
               			      //kq_item_map.put(q03z0,list);
               			      continue;
               		       }else
               		       {
               		    	   continue;
               		       }
               	   }
           	    }else if(!"q03z0".equals(fielditem.getItemid())&&!"nbase".equals(fielditem.getItemid())&&!"a0100".equals(fielditem.getItemid())
             	    		&&!"b0110".equals(fielditem.getItemid())&&!"e0122".equals(fielditem.getItemid())&&!"e01a1".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())
             	    		&&!"q03z5".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"a0101".equals(fielditem.getItemid())&&!"i9999".equals(fielditem.getItemid()))
           	    {
//           	    	System.out.println(fielditem.getItemid());
           	    	String  sr=rowSet.getString(fielditem.getItemid());
           	    	//System.out.println(sr);
           	    	if(sr!=null&&sr.length()>0&&!"0".equals(sr))
           	    	{
           	    		String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);    
           	    		ArrayList one_list= new ArrayList();
         			        one_list.add(kq_item);
         			        one_list.add(new Double(1));
         			        list.add(one_list);
           	    	}
           	    }
           	 }             	    
//         		 if(list==null||list.size()<=0) 
//         		 {
//         			 if(scq_list==null||scq_list.size()<=0)
//         			 {
//         				list.add(z1_list);
//         			 }else
//         			 {
//         				list.add(scq_list);
//         			 }
//         		 }  
        	    if (rownum == 0) {
        	    	list.add(scq_list);
        	    }
         			
           	//System.out.println(list);
           	  kq_item_map.put(q03z0,list);
           }
         if("1".equalsIgnoreCase(file))
         {
        	 //第一个循环值，值展现1到15号的值
        	 for(int s=0;s<datelist.size();s++)
             {
        	  if(s<=14)
        	  {
        		  CommonData cur_vo = (CommonData)datelist.get(s);		
                  String cur_date=cur_vo.getDataName().trim();                   
                  ArrayList kq_item_list=(ArrayList)kq_item_map.get(cur_date);  //符号
                  if(kq_item_list!=null&&kq_item_list.size()>0)
                  {
               	   StringBuffer font_str=new StringBuffer();
               	   for(int t=0;t<kq_item_list.size();t++)
               	   {
               		   ArrayList one_list=(ArrayList)kq_item_list.get(t);
                          if(one_list!=null&&one_list.size()>0)
                          {
                       	   String [] kq_item=(String [])one_list.get(0);                	   
                       	   Double dv=(Double)one_list.get(1);
                       	   double value=dv.doubleValue();
                       	   if(value!=0)
                       	   {
                       		   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),kq_item[1]);
                       		   //one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"",kq_item[0],getStyleName("5"))); 
                       		   font_str.append(executeFont(parsevo.getHead_fn(),sytle1,kq_item[0]));
                       	   }
                       	   
                       	   /*else
                       	   {
                       		   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),kq_item[1]);
                   			   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
                       	   } */  	  
               			}/*else
                          {
                       	   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
               			   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5")));
                          }*/
               	   }
               	    //if(a0100[0].equals("00000147")&&a0100[2].equals("Usr"))
               	   //one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
               	  String sytle3=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
               	  if (font_str.toString().length() > 0) {
               		  one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",font_str.toString(),getStyleName("6")));
               	  } else {
               		one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","&nbsp;",getStyleName("6")));
               	  }
                  }else
                  {
               	   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
       			   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
                  } 
        	  }else
        	  {
        		  String sytle123=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
        		  one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        		  break;
        	  }
            }
         }else if("2".equalsIgnoreCase(file))
         {
        	 //这是第二个循环值，值展现16号以后
        	 for(int s=15;s<datelist.size();s++)
             {
//        		 System.out.println("oooo = "+datelist.size());
//        		 System.out.println("123 = "+s);
        		 
        		  CommonData cur_vo = (CommonData)datelist.get(s);		
                  String cur_date=cur_vo.getDataName().trim();                   
                  ArrayList kq_item_list=(ArrayList)kq_item_map.get(cur_date);  //符号
                  if(kq_item_list!=null&&kq_item_list.size()>0)
                  {
               	   StringBuffer font_str=new StringBuffer();
               	   for(int t=0;t<kq_item_list.size();t++)
               	   {
               		   ArrayList one_list=(ArrayList)kq_item_list.get(t);
                          if(one_list!=null&&one_list.size()>0)
                          {
                       	   String [] kq_item=(String [])one_list.get(0);                	   
                       	   Double dv=(Double)one_list.get(1);
                       	   double value=dv.doubleValue();
                       	   if(value!=0)
                       	   {
                       		   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),kq_item[1]);
                       		   //one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"",kq_item[0],getStyleName("5"))); 
                       		   font_str.append(executeFont(parsevo.getHead_fn(),sytle1,kq_item[0]));
                       	   }
               			}
               	   }
//                 	   String sytle3=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
//               	   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle3,body_hieght+"",font_str.toString(),getStyleName("5")));
               	   //String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
               	   if((s+1)==datelist.size())
               	   {
               		one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",font_str.toString(),getStyleName("1")));
               	   }else
               	   {
               		one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",font_str.toString(),getStyleName("6")));
               	   }
                  }else
                  {
               	   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
       			   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
                  } 
        	 
            }
        	 //如果不是31天的时候，也要画出表格，值不过没值
        	if(datelist.size()!=31)
        	{
        		if(datelist.size()==28)
            	{
            		 for(int p=1;p<4;p++)
            		 {
            			 String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
             			 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
            		 }
            	}else if(datelist.size()==29)
            	{
            		 for(int p=1;p<3;p++)
            		 {
            			 String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
             			 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
            		 }
            	}else if(datelist.size()==30)
            	{
            		String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
        			 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
            	}
        	}
         }
        }catch(Exception e){
	      //throw GeneralExceptionHandler.Handle(e); 
     	e.printStackTrace();
        }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    } 
        return one_date.toString();
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    public String getFontStyle(String fn,String fi,String fu,String fb,String fz,String color)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	if(color!=null&&color.length()>0)
    	{
    		style.append("color: "+color+";");
    	}else
    	{
    		style.append("color: #FF0000;");
    	}
    	
    	
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	public String executeFont(String fontName,String fontStyle,String context)
	{
		
		StringBuffer tempTable=new StringBuffer("");
		if(context!=null&&context.trim().length()>0) {
			tempTable.append("<font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' >");
			tempTable.append(context);			
			tempTable.append("</font>");
		} 
		
		return tempTable.toString();
	}
	/**
	 * 组成查询内容的sql
	 * @param codea0100
	 * @param userbase
	 * @param start_date
	 * @param end_date
	 * @param column
	 * @return
	 */
	public static String selcet_kq_one_emp(String codea0100,String userbase,String start_date,String end_date,String column)
	{
		StringBuffer sqlstr= new StringBuffer();
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
	 	sqlstr.append("select q03z0,"+columnstr+" from Q03_arc"); 	   
	 	sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
	 	sqlstr.append(" and Q03Z0 <= '"+end_date+"%'");
	 	sqlstr.append(" and a0100="+codea0100+"");
	 	sqlstr.append(" and nbase='"+userbase+"'");
	 	return sqlstr.toString();
	}
	/**
     * 得到表尾数据
     * **/
    public String getTileHtml(int curpage,int sum_page ,ArrayList item_list)throws GeneralException
    {
    	StringBuffer tilehtml = new StringBuffer();
    	String fact_width=getFactWidth();
    	String tile_height=getPxFormMm(parsevo.getTile_h());
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	 
    	  	
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	
    	StringBuffer note_str= new StringBuffer();
    	StringBuffer note_len_str= new StringBuffer();
    	note_str.append("备注：1.");
    	note_len_str.append("备注：1.");
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	    // System.out.println(fielditem.getItemdesc());
   	     /*if("N".equals(fielditem.getItemtype()))
   	     {*/
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0)
   	    		{
   	    			note_str.append(fielditem.getItemdesc()+"(");   	    		
   	   	    		note_str.append("<font color='"+kq_item[1]+"'>");
   	   	    		note_str.append(kq_item[0]);
   	   	    		note_str.append("</font>)");
   	   	    	    note_len_str.append(fielditem.getItemdesc()+"("+kq_item[0]+")");
   	   	    	}  	    		
   	    	}
   		  //}				
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());    	
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+6; 
    	int numrow_1=getNumRow(strlen);    	
    	if(numrow_1!=0)
    	{
    		note_h_1=note_h_1*numrow_1;	
    	}  
//    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
    	
    	// 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun  2010.4.9
    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
    	
    	
    	tilehtml.append(" <tr valign='middle' align='center'> \n ");   
    	tilehtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,note_h_1+"",note_str.toString(),getStyleName("1")));
    	tilehtml.append("</tr><tr>");    	
    	if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    	{
    		String tile_fw="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2."+parsevo.getTile_fw();
        	int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
        	int numrow_2=getNumRow(str_tile_2);
        	int note_h_2=Integer.parseInt(parsevo.getBody_fz())+6;
        	if(numrow_2!=0)
        	{
        		note_h_2=note_h_2*numrow_2;
        	}
        	
    		tilehtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,note_h_2+"","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2."+parsevo.getTile_fw(),getStyleName("1")));
    	}    	
    	tilehtml.append("</tr></table></td></tr><tr><td>");    	
        /**基本参数**/
    	String sytle_t=getFontStyle(parsevo.getTile_fn(),parsevo.getTile_fi(),parsevo.getTile_fu(),parsevo.getTile_fb(),parsevo.getTile_fz());
//    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
    	
    	// 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun  2010.4.9
    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
    	
    	tilehtml.append(" <tr valign='middle' align='center'> \n ");  
        /**制作人**/
        int i=0;
        String temp_p="5";        
        /**制作人单位**/
        if("#u".equals(parsevo.getTile_u().trim()))
        {
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
     	   
     	   String u_code="";
     	   if(!userView.isSuper_admin())
 		   {
 			  if(userView.getUserOrgId()!=null && userView.getUserOrgId().trim().length()>0)
 			  {
 				 u_code=userView.getUserOrgId();
 			  }else
 			  {
 				 u_code=RegisterInitInfoData.getKqPrivCodeValue(userView);
 			  }
 		   }
     	   String [] u_codeitem=getCodeItemDesclow(u_code);
     	   String u_str="&nbsp;制作人单位: "+u_codeitem[0];
     	   tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,u_str,getStyleName(temp_p)));
        }
        /**制作日期**/
        if("#d".equals(parsevo.getTile_d().trim()))
        {
     	   
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
     	   
//     	   String d_str="&nbsp;&nbsp;制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");//原来的 取得系统时间
     	   String d_str="&nbsp;&nbsp;制作日期:<input type='text' name='element' size='10' value='"+PubFunc.getStringDate("yyyy.MM.dd")+"' class='text' id='sjelement' maxlength='10'>";
     	   tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,d_str,getStyleName(temp_p)));  
        }
        if("#e".equals(parsevo.getTile_e().trim()))
        {
     	   
     	   String e_str="&nbsp;&nbsp;制作人: "+this.userView.getUserFullName();
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
     	   
     	  tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,e_str,getStyleName(temp_p)));  
        }
        /**制作时间**/
        if("#t".equals(parsevo.getTile_t().trim()))
        {
     	   
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
//     	   String t_str="&nbsp;&nbsp;时间: "+PubFunc.getStringDate("HH:mm:ss");
     	   String t_str="&nbsp;&nbsp;时间:<input type='text' name='timexr' size='10' value='"+PubFunc.getStringDate("HH:mm:ss")+"' class='text' id='timeqd' maxlength='8'>";
     	   tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,t_str,getStyleName(temp_p))); 
        }
        /**页码**/
        if("#p".equals(parsevo.getTile_p().trim()))
        {
     	   
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
     	   String p_str="&nbsp;&nbsp;页码:"+curpage+"";
     	  tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,p_str,getStyleName(temp_p))); 
        }
        /**总页码**/
        if("#c".equals(parsevo.getTile_c().trim()))
        {
     	   
     	   if(i==0)
     	   {
     		   temp_p="1";
     		   i=1;
     	   }else
     	   {
     		   temp_p="5";
     	   }
     	   
     	   String c_str="&nbsp;&nbsp;总页码:"+sum_page+"";
     	  tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,c_str,getStyleName(temp_p))); 
        }
       
    	
    	tilehtml.append("</tr></table>");
    	//System.out.println(tilehtml.toString());
    	return tilehtml.toString();
    }
    public boolean getkq_report(String report_id)
    {
    	boolean boo = false;
    	String report_sql="Select content from kq_report where report_id='"+report_id+"'";
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet=null;
    	try
    	{
    		rowSet=dao.search(report_sql);
    		if(rowSet.next())
    		{
    			String content=rowSet.getString("content");
    			if(!"".equals(content)||content.length()>0)
    			{
    				boo=true;
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
    	return boo;
    }
    /*
     * 页面设置属性，返回
     */
    private String getTurnPageCode(int curPage,String turnpage_sytle,int sum_page)
    {
    	StringBuffer code=new StringBuffer("");
    	code.append("<table width='50%' height='30' align='center' style='"+turnpage_sytle+"'>");
		code.append("<tr><td align='center'> \n");
	          
		code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
	    code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back();' class='mybutton'>");      
	    code.append("</td></tr></table>");
    	return code.toString();	
    }
    
    /**
     * 查询考勤期间的所有指标
     * @return
     */
    private HashMap querryKq_item () {
    	HashMap map = new HashMap();
    	String sql = "select item_symbol,fielditemid from kq_item";
    	ContentDAO dao = new ContentDAO(this.conn);
    	ResultSet rs = null;
    	try {
			rs = dao.search(sql);
			while (rs.next()) {
				String key = rs.getString("fielditemid");
				String value = rs.getString("item_symbol");
				if (key != null) {
					map.put(key.toLowerCase(), value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    	return map;
    }
    //第三次
    public String getBodyHolsEndHtml3(ArrayList kqq03list)
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","假别",getStyleName("6")));
    	String kqtable=SystemConfig.getPropertyValue("kqtable");//用来控制考勤表头竖型展现 0和null都是正常展现
    	for(int i=32;i<=48;i++)
    	{
    		if(i<=kqq03list.size())
    		{
    			for(int p=32;p<kqq03list.size();p++)
        		{
        			FieldItem fielditem=(FieldItem)kqq03list.get(p);		
        			String name = fielditem.getItemdesc();
        			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
        			{
        				if(p<=48)
                    	{
                    		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                    	}else
                    	{
                    		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                    		i++;
                    		break;
                    	}
        			}else
        			{
        				if(p<=48)
                    	{
                    		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                    	}else
                    	{
                    		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                    		i++;
                    		break;
                    	}
        			}
//                	System.out.println("ppp = "+p);
                	i++;
        		}
    		}else
    		{
    			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
    			{
    				if(i==48)
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}else
    			{
    				if(i==48)
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}
    		}
//    		System.out.println("123 = "+i);
    	}
    	return bodyheadhtml.toString();
	}
    public String getOneA0100ValueEnd3(String codea0100,String userbase,String kq_duration,int body_hieght,ArrayList kqq03list)
	{
		StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	for(int i=0;i<kqq03list.size();i++)
    	{
    		if(i>31)
    		{
    				FieldItem fielditem=(FieldItem)kqq03list.get(i);
            		if(!"i9999".equals(fielditem.getItemid()))
           	    	{
           	    		column.append(""+fielditem.getItemid()+",");  
//           	    		columnlist.add(fielditem.getItemid());
           	    		columnlist.add(fielditem);
           	    	}
    		}
    	}
    	StringBuffer one_date=new StringBuffer(); 
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","工日",getStyleName("6")));
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++)
         	{
         		StringBuffer sql_on_a0100=new StringBuffer();
         		FieldItem fielditem = (FieldItem)columnlist.get(t);
         		String itd=fielditem.getItemid();
         		sql_on_a0100.append("select "+itd+" as one from Q05_arc where Q03Z0='"+kq_duration+"' and ");
        		sql_on_a0100.append("nbase='"+userbase+"' and A0100='"+codea0100+"'");
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next())
        		{
        			itemid = rowSet.getString("one");
//        			if(itemid==null&&(itemid.equalsIgnoreCase("0E-8")||itemid.equals("")))
        			if(itemid != null)
        			{
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid))
        				{
        					itemid="";
        				}else
        				{
//        					int id=(int)(Float.parseFloat(itemid));
//            				itemid=Integer.toString(id);
        					if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
	        					int num = fielditem.getDecimalwidth();
								if(itemid.indexOf(".")!=-1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid,num);
								} else{
									
									 
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										} else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid,num);
									
								}
        					} else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									if (!codemap.containsKey(setid+itemid)) {
										String codesetid = itemid;
										itemid = AdminCode.getCodeName(setid, itemid);
										codemap.put(setid+codesetid, itemid);
									} else {
										itemid = (String) codemap.get(setid+itemid);
									}
								}
							}
        				}
        			}else
        			{
        				itemid="";
        			}
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
        		}else
        		{
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
//        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",itemid,getStyleName("6")));
//        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        		}
         	}
         	if(columnlist.size()<15)
         	{
         		for(int p=columnlist.size();p<=15;p++)
             	{
             		if(p==15)
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("1")));
             		}else
             		{
             			one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","",getStyleName("6")));
             		}
             	}
         	}
         	
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     } 
    	return one_date.toString();
	}
}

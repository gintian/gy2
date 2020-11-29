package com.hjsj.hrms.businessobject.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KqViewSumBo {
/**
 * 考勤月报表
 * */
	private Connection conn=null;
	private ReportParseVo parsevo=null;
	private UserView userView=null;
	private String self_flag;
	private String sortItem;
	private String sortItemDesc;
	private String column;
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getSortItem() {
		return sortItem;
	}
	public void setSortItem(String sortItem) {
		this.sortItem = sortItem;
	}
	public String getSelf_flag() {
		return self_flag;
	}
	public void setSelf_flag(String self_flag) {
		this.self_flag = self_flag;
	}
	public KqViewSumBo()
	{
	}
	/**初始化*/
	public KqViewSumBo(Connection conn)
	{
		this.conn=conn;
		
	}
	public KqViewSumBo(Connection conn,ReportParseVo parsevo,UserView userView)
	{
		this.conn=conn;
		this.parsevo=parsevo;	
		this.userView=userView;
	}
	/**
	 * 得到页面内容
	 * @param  userbase  应用库前缀
	 * @param  code  链接级别
	 * @param  coursedate  考勤期
	 * @param  curpage  当前页
	 * @param  parsevo  xml参数
	 * @return 员工日考勤页面
	 * **/
	 public ArrayList getKqReportHtml(String code,String kind,String coursedate,String curpagestr,ReportParseVo parsevo,UserView userView,HashMap formHM)throws GeneralException
	 {
		    this.parsevo=parsevo;	
		    this.userView=userView;
		    //System.out.println("实际高度="+getFactHeight());
		    //System.out.println("实际宽度="+getFactWidth());		    
		    int leng=2;
		    int itemumn=0;
		    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		         FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		         if("1".equals(fielditem.getState()))
				 {
		        	 if("N".equals(fielditem.getItemtype()))
			     	 { 	 
			     		itemumn=itemumn+1;
			     		int field_length=fielditem.getItemdesc().length();
			     		if(fielditem.getItemdesc().indexOf("(")!=-1)
			     		{
			     			field_length=field_length-2;
			     		}
			     		if(leng<field_length) {
                            leng=field_length;
                        }
			     	 }	
				 }
		     	 			
		    } 
		    HashMap hashmap=new HashMap();
		    hashmap.put("itemumn",itemumn+"");
		    hashmap.put("itemleng",leng+"");
		    leng=leng*Integer.parseInt(parsevo.getBody_fz());
		    double spare_h=getSpareHieght(leng);
		    ArrayList htmlList = new ArrayList();
		    try{
		   
		    int pagesize=0;
		    if("#pr[1]".equals(parsevo.getBody_pr()))
		    {
		    	pagesize=Integer.parseInt(parsevo.getBody_rn());
		    }else
		    {
		    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+13);
		    	//pagesize=1;
		    }
		    ArrayList kq_dbase_list=this.userView.getPrivDbList(); 	
		    //String kq_duration=RegisterDate.getKqDuration(this.conn);
		    // 过滤参数
		    KqParameter kq_paramter = new KqParameter(formHM,userView,"UN",this.conn);	              
			String kqBase=kq_paramter.getNbase();
			ArrayList list = new ArrayList();
			for (int i = 0; i < kq_dbase_list.size(); i++) {
				String str = kq_dbase_list.get(i).toString();
				String vStr = "," + kqBase + ",";
				if (vStr.contains("," + str + "")) {
					list.add(str);
				}
			}
			kq_dbase_list = list;
			
		  
		    int allrows =getAllRecordNum(code,kind,coursedate,kq_dbase_list);
		    int curpage = 1;
		    if(curpagestr!=null&&curpagestr.length()>0)
		    {
		    	curpage=Integer.parseInt(curpagestr);
		    }
		    int sum_page=(allrows-1)/pagesize+1;
		    curpage=getCurpage(curpage,pagesize,sum_page);
		    
		   // System.out.println("当前页="+curpage+"    行数="+pagesize+"  总页数="+sum_page+"  总行数="+allrows+"剩余高度="+spare_h);		    
		    StringBuffer html= new StringBuffer();
		    String titleHtml=getTableTitle();
		    String width=getFactWidth();
		    ArrayList  datelist=getDateList(this.conn,coursedate);
		    
		    String headHtml=getTableHead(code,coursedate,curpage,sum_page,datelist);
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
		    ArrayList keylist=new ArrayList();
		    keylist.add("q03z0");
		    keylist.add("a0100");
		    keylist.add("nbase");
		    ArrayList a0100list=getA0100List(code,kind,coursedate,curpage,pagesize,kq_dbase_list,keylist);
		    html.append(getBodyHtml(code,kind,coursedate,curpage,pagesize,a0100list,hashmap));
		    html.append("</td></tr><tr><td>");
		    html.append(getTileHtml(code,kind,curpage,sum_page,allrows));//表尾信息
		    html.append("</td></tr>");
		    html.append("<tr><td>");
//			  body高度
		    int body_h=a0100list.size()*(Integer.parseInt(parsevo.getBody_fz())+13);
		  
		    //System.out.println("BODY高度="+body_h);
		    double isUser_height=getIsUseHieght(leng);//以用高度
		    String isUser_h=KqReportInit.round(isUser_height+"",0);
		    int turnpage_sytle_h=Integer.parseInt(isUser_h)+Integer.parseInt(getPxFormMm(parsevo.getTop()))+body_h;
		    /***显示翻页,表格的绝对定位***/
		    //String turnpage_sytle="position:absolute;top:"+turnpage_sytle_h+";left: "+parsevo.getLeft()+";width: "+width;
		    String turnTable=getTurnPageCode(curpage,"",sum_page,coursedate);
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
		
        String width=getFactWidth();
        String height=getPxFormMm(parsevo.getTitle_h());
        String sytle=getFontStyle(parsevo.getTitle_fn(),parsevo.getTitle_fi(),parsevo.getTitle_fu(),parsevo.getTitle_fb(),parsevo.getTitle_fz());
        String style_name=getStyleName("10");
        StringBuffer titleTable=new StringBuffer("");		
		String[] temp=transAlign(7);
		String aValign=temp[0];
		String aAlign=temp[1];
		titleTable.append(" <table  cellspacing='0'  align='left'  valign='top' cellpadding='0' border='0'> \n");		
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
	 * 得到表头内容 
	 *   #p（页码）  #c 总页数  #e 制作人  #u 制作人所在的单位  #d 日期  #t  时间  #fn宋体 字体名称  #fz15    字体大小
	 *   #fb[0|1]  黑体  #fi[0|1]   斜体  #fu[0|1]   下划线  #pr[0|1]  页行数
	 *   #fh40
	 * */
	public String getTableHead(String code,String coursedate,int curpage,int sum_page,ArrayList datelist)throws GeneralException
	{
		StringBuffer headHtml = new StringBuffer();        
        String [] codeitem=getCodeItemDesc(code);
        String fact_width=getFactWidth();
        String head_height=getPxFormMm(parsevo.getHead_h());
        String sytle=getFontStyle(parsevo.getHead_fn(),parsevo.getHead_fi(),parsevo.getHead_fu(),parsevo.getHead_fb(),parsevo.getHead_fz());
        try{
           headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
           headHtml.append(" <tr valign='middle' align='center'> \n ");           
           /**单位**/           
           String dv_content="";
           if(codeitem[1]==null||codeitem[1].length()<=0)
           {
        	   dv_content="&nbsp;&nbsp;单位：所有单位";
           }else
           {
        	   dv_content="&nbsp;&nbsp;单位："+codeitem[1];
           }
           String dv_style_name=getStyleName("10");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,dv_content,dv_style_name));           
           /**部门*/           
           String bm_content="";
           if(codeitem[0]==null||codeitem[0].length()<=0)
           {
        	   bm_content="&nbsp;&nbsp;部门：全体部门";
           }else
           {
        	   bm_content="&nbsp;&nbsp;部门："+codeitem[0];
           }
           String bm_style_name=getStyleName("10");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,bm_content,bm_style_name));
            /**日期*/
           CommonData vo = (CommonData)datelist.get(0);	           
           String start_date=vo.getDataName();
           vo = (CommonData)datelist.get(datelist.size()-1);	 
           String end_date= vo.getDataName();             
           String da_content="&nbsp;&nbsp;  "+coursedate+" ("+start_date+"~"+end_date+")";
           String da_style_name=getStyleName("10");
           headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,da_content,da_style_name));
           headHtml.append("</tr></table>");
           headHtml.append("</td></tr><tr><td>");
           /**基本参数**/
           headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
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
        	   String [] u_codeitem=getCodeItemDesc(u_code);
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
	/**得到body头内容
	 * 
	 * 
	 * */
	public String getBodyHeadHtml(HashMap hashmap)throws GeneralException
    {
		int itemleng=Integer.parseInt((String)hashmap.get("itemleng"));
	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
	    StringBuffer bodyheadhtml = new StringBuffer();
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt((String)hashmap.get("itemleng"))*Integer.parseInt(parsevo.getBody_fz())+13;
    	String onefield=executeOneTable(1,7,parsevo.getHead_fn(),sytle,parsevo.getBody_fz()+"","&nbsp;姓&nbsp;&nbsp;名&nbsp;",getStyleName("10"),0);
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",onefield,getStyleName("2"))); 
	    for(int i=0;i<fielditemlist.size();i++)
	    {
	         FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	         if("1".equals(fielditem.getState()))
			 {
	          	if("N".equals(fielditem.getItemtype()) && !"i9999".equalsIgnoreCase(fielditem.getItemid()))
	     	    { 	   
	     		  onefield=executeOneTable(1,7,parsevo.getHead_fn(),sytle,parsevo.getBody_fz()+"",fielditem.getItemdesc(),getStyleName("10"),itemleng);
	     		  bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",onefield,getStyleName("2")));   
	     	    }	
			 }
	    }    	    	
	    bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","&nbsp;&nbsp;备&nbsp;&nbsp;&nbsp;注&nbsp;&nbsp;",getStyleName("")));
    	
    	return bodyheadhtml.toString();
    }
	/**
	 * 得到body信息内容
	 * 
	 * **/
    public String getBodyHtml(String code,String kind,String kq_duration,int curpage,int pagesize,ArrayList a0100list,HashMap hashmap)throws GeneralException
    {
    	StringBuffer bodyhtml = new StringBuffer();
    	int body_hieght=Integer.parseInt(parsevo.getBody_fz())+13;
    	String fact_width=getFactWidth();
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	bodyhtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");    	
    	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyHeadHtml(hashmap));
    	bodyhtml.append("</tr>");    
    	for(int i=0;i<a0100list.size();i++)
    	{
    		String a0100[]=(String[])a0100list.get(i);
    		String a0101=a0100[1];
    		
    		// 姓名两个字的中间空格
    		if (a0101 != null && a0101.trim().length() == 2) {
    			StringBuffer buff = new StringBuffer();
    			buff.append(a0101.substring(0, 1));
    			// 加全角空格
    			buff.append("　");
    			buff.append(a0101.substring(1, 2));
    			a0101 = buff.toString();
    		}
    		
    		bodyhtml.append("<tr>");
    		bodyhtml.append(executeTable(1,7,parsevo.getBody_fn(),sytle,body_hieght+"",a0101,getStyleName("6")));
    		String one_date=getOneA0100Data(code,kind,a0100,kq_duration,body_hieght);
    		bodyhtml.append(one_date);
    		bodyhtml.append("</tr>");
    		
    	}
    	bodyhtml.append("</table>");
    	return bodyhtml.toString();
    }
    /**
     * 得到表尾数据
     * **/
    public String getTileHtml(String code,String kind,int curpage,int sum_page,int allrows)throws GeneralException
    {
    	StringBuffer tilehtml = new StringBuffer();
    	String fact_width=getFactWidth();
    	String tile_height=getPxFormMm(parsevo.getTile_h());
    	  	
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+13;  
    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
    	tilehtml.append(" <tr valign='middle' align='center'> \n ");     	  	
    	StringBuffer back1=new StringBuffer();
        back1.append("&nbsp;&nbsp;&nbsp;部门当月参加考勤人数:&nbsp;&nbsp;");
        back1.append(""+allrows);
        back1.append("&nbsp;人");
    	tilehtml.append(executeTableTow(1,6,parsevo.getBody_fn(),sytle,"30%",note_h_1+"",back1.toString(),getStyleName("1")));
    	tilehtml.append(executeTableTow(1,6,parsevo.getBody_fn(),sytle,"20%",note_h_1+"","&nbsp;&nbsp;部门领导签字:",getStyleName("5")));
    	tilehtml.append(executeTableTow(1,6,parsevo.getBody_fn(),sytle,"20%",note_h_1+"","&nbsp;&nbsp;审核:",getStyleName("5")));
    	tilehtml.append(executeTableTow(1,6,parsevo.getBody_fn(),sytle,"20%",note_h_1+"","&nbsp;&nbsp;填报:",getStyleName("5")));
    	tilehtml.append("</tr></table></td>");
    	tilehtml.append("</tr><tr><td>");
    	tilehtml.append("<table  cellspacing='0' cellpadding='0' border='0' align='left'  valign='top' width='"+fact_width+"'> \n");
    	tilehtml.append("<tr valign='middle' align='center'> ");
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
    	tilehtml.append("</tr></table></td>");
    	tilehtml.append("</tr><tr><td>");    	
        /**基本参数**/
    	String sytle_t=getFontStyle(parsevo.getTile_fn(),parsevo.getTile_fi(),parsevo.getTile_fu(),parsevo.getTile_fb(),parsevo.getTile_fz());
    	tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
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
     	   String [] u_codeitem=getCodeItemDesc(u_code);
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
     	   
//     	   String d_str="&nbsp;&nbsp;制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
     	   String d_str="&nbsp;&nbsp;制作日期:<input type='text' name='element' size='10' value='"+PubFunc.getStringDate("yyyy.MM.dd")+"' class='text' id='sjelement' maxlength='10'>";
     	   tilehtml.append(executeTable(1,6,parsevo.getTile_fn(),sytle_t,tile_height,d_str,getStyleName(temp_p)));  
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
    	return tilehtml.toString();
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
		
		tempTable.append(" <td height='"+height+"'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
	    
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(fontStyle);
		tempTable.append("' > \n ");
		if(context!=null&&context.length()>0)
		{
			tempTable.append(context);	
		}
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
		}
		tempTable.append("</font></td> \n ");
		
		return tempTable.toString();
	}
	public String executeTableTow(int type,int Align,String fontName,String fontStyle,String width,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='"+width+"'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
	    
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(fontStyle);
		tempTable.append("' > \n ");
		if(context!=null&&context.length()>0)
		{
			tempTable.append(context);	
		}
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
		}
		tempTable.append("</font></td> \n ");
		
		return tempTable.toString();
	}
	/**
	 * 生成一个含table得单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	public String executeOneTable(int type,int Align,String fontName,String fontStyle,String width,String context,String style_name,int itemleng)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		tempTable.append("<table border='0' cellspacing='0'  align='center' cellpadding='0' valign='top'>");
		tempTable.append(" <td width='"+width+"'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
	    
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(fontStyle);
		tempTable.append("' > \n ");
		if(context!=null&&context.length()>0)
		{
			if(context.indexOf("(")!=-1)
			{
				context=context.replaceAll("-","\\.");
				context=context.replaceAll("-","\\.");
			}
			tempTable.append(context);
		}
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
		}
		tempTable.append("</font></td> \n ");
		tempTable.append("</table>");
		return tempTable.toString();
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
	public String getFont(String font_size,String color,String info)
	{
		StringBuffer fontStr = new StringBuffer();
		fontStr.append("<font face='宋体' style='font-weight:normal;font-size:"+font_size+"' >");
		fontStr.append(info);
		fontStr.append("</font></td></tr></table>");
		
		return fontStr.toString();
	}
	
	/**
	 * 通过code得到codeItemDesc
	 * */
	public String[] getCodeItemDesc(String code)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select codeitemdesc,parentid from organization where codeitemid='"+code+"'";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		parentid=rowSet.getString("parentid");
	    		String sqlp="select codeitemdesc from organization where codeitemid='"+parentid+"'";
	    		rowSet=dao.search(sqlp);
	    		if(rowSet.next())
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
                    e.printStackTrace();
                }
            }
	     } 
	   return codeItemDesc;
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
    	if(color==null||color.length()<=0)
    	{
    		color="";
    	}
    	if("0".equals(color.trim()))
    	{
    		style.append("color: #FF0000;");
    	}else if("1".equals(color.trim()))
    	{
    		style.append("color: #00FF00;");
    	}else if("2".equals(color.trim()))
    	{
    		style.append("color: #0000FF;");
    	}else if("3".equals(color.trim()))
    	{
    		style.append("color: #FF9900;");
    	}else
    	{
    		style.append("color: #000000;");
    	}
    	return style.toString();
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
	    }else if("10".equals(temp))
	    {

	    	style_name="TEXT";
	    }
	    
		return style_name;
	}	
    //  得到在考勤范围内的部门员工编号，并添加到list中
    public ArrayList getA0100List(String code,String kind,String kq_duration,int curpage,int pagesize,ArrayList kq_dbase_list,ArrayList keylist)throws GeneralException
    {
//    	ArrayList a0100whereIN= new ArrayList();
//		for(int i=0;i<kq_dbase_list.size();i++)
//		{
//			String dbase=kq_dbase_list.get(i).toString();
//			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
//			a0100whereIN.add(whereA0100In);			
//		}
		String whereA0100=select_Q05_a0100(code,kind,kq_duration,kq_dbase_list);
		
    	ContentDAO dao = new ContentDAO(this.conn);
        ArrayList a0100list=new ArrayList();        
        RowSet rowSet=null;
        try{
          
          /*rowSet = dao.search(whereA0100);
           int num=1;
           int start_record=(curpage-1)*pagesize+1;
           int end_record=curpage*pagesize;          
           while(rowSet.next())
           {
        	  if(num>=start_record&&num<=end_record)
        	  {
        		  String a0100[] = new String[2];
            	  a0100[0]=rowSet.getString("a0100");
            	  a0100[1]=rowSet.getString("a0101");
         	      a0100list.add(a0100); 
         	      num++;
        	  }else if(num<start_record)
        	  {
        		  num++;
        		  continue;
        	  }else if(num>end_record)
        	  {
        		  num++;
        		  break;
        	  } 
        	  
           }*/
        
        	
        	String sort = "";
    		String desc = "";
    		SortBo bo = new SortBo(this.conn, this.userView);
    		if (sortItem != null && sortItem.length() > 0 && !"not".equalsIgnoreCase(sortItem)) {
      			sort = bo.getSortSql(sortItem, "");
      			desc = bo.getSortSql(sortItem, "", false);
      		} else if(bo.isExistSort()) {
      			sort = bo.getSortSqlTable("");
      			desc = bo.getSortSqlTable("", false);
      		} else {
      			sort = "order by dbid,a0000";
      			desc = "order by dbid desc,a0000 desc";
      		}
           //rowSet=dao.search(whereA0100.toString(),pagesize,curpage,keylist);
        	if (sort != null && sort.length() > 0) {
        		whereA0100 += sort;
        	}
        	
        	StringBuffer sql = new StringBuffer();
        	if (Sql_switcher.searchDbServer() == 2) {
        		sql.append("select * from (select b.*,rownum m from (");
        		sql.append(whereA0100);
        		sql.append(") b) c where m between ");
        		sql.append(pagesize * (curpage - 1) + 1);
        		sql.append(" and ");
        		sql.append(pagesize * curpage);
        	} else {
	        	sql.append("select * from (select top "+pagesize+" * from (select top ");
	        	sql.append(pagesize * curpage);
	        	sql.append("* from (");
	        	sql.append(whereA0100.substring(0, whereA0100.lastIndexOf("order")));
	        	sql.append(") qqq ");
	        	sql.append(sort);
	        	sql.append(") vvv ");
	        	sql.append(desc);
	        	sql.append(") mmm ");
	        	sql.append(sort);
        	}
        	
//        	rowSet=dao.search(whereA0100.toString(),pagesize,curpage);
        	rowSet=dao.search(sql.toString());
           while(rowSet.next())
           {
        	  String a0100[] = new String[2];
        	  a0100[0]=rowSet.getString("a0100");
        	  a0100[1]=rowSet.getString("a0101");
     	      a0100list.add(a0100); 
           }
        }catch(Exception e){
 	      throw GeneralExceptionHandler.Handle(e); 
        }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     } 
        return a0100list;    	
    }
    
//    public String select_Q05_a0100(String code,String kind,String kq_duration,ArrayList whereINList)
//    {
// 	   StringBuffer sqlstr= new StringBuffer();
// 	  String sort = "";
//		String desc = "";
//		StringBuffer coloum = new StringBuffer();
//		SortBo bo = new SortBo(this.conn, this.userView);
//		KqReportInit init = new KqReportInit(this.conn);
//		if (sortItem != null && sortItem.length() > 0 && !"not".equalsIgnoreCase(sortItem)) {
//			sort = bo.getSortSql(sortItem, "");
//			desc = bo.getSortSql(sortItem, "", false);
//			String []strs = sortItem.split("`");
//			for (int i = 0; i < strs.length; i++) {
//				String []str = strs[i].split(":");
//				if (!",a0100,a0101,q03z0,nbase,dbid,a0000,".contains("," + str[0].toLowerCase() + ",")) {
//					coloum.append(str[0]);
//					coloum.append(",");
//				}
//			}
//		} else if(bo.isExistSort()) {
//			sort = bo.getSortSqlTable("");
//			desc = bo.getSortSqlTable("", false);
//			
//			String []strs = bo.querrySort().replaceAll(",", "`").split("`");
//			for (int i = 0; i < strs.length; i++) {
//				String []str = strs[i].split(":");
//				if (!",a0100,a0101,q03z0,nbase,dbid,a0000,".contains("," + str[0].toLowerCase() + ",")) {
//					coloum.append(str[0]);
//					coloum.append(",");
//				}
//			}
//		} else {
//			sort = "order by dbid,a0000";
//			desc = "order by dbid desc,a0000 desc";
//		}
//		if (coloum.length() > 0) {
//			sqlstr.append("select distinct a0100,a0101,q03z0,nbase,"+coloum.toString()+"dbid,a0000  from Q05_arc");	
//		} else {
//			sqlstr.append("select distinct a0100,a0101,q03z0,nbase,dbid,a0000  from Q05_arc");	
//		}
// 	   sqlstr.append(" where Q03Z0= '"+kq_duration+"'"); 	   
// 	   if(kind.equals("1"))
//		{
//	    	sqlstr.append(" and e0122 like '"+code+"%'");
//		}else if (kind.equals("-2")){
//			sqlstr.append(" and 1=2");	
//		} else if (kind.equals("0")){
//			sqlstr.append(" and e01a1 like '"+code+"%'");
//		} else{
//			sqlstr.append(" and b0110 like '"+code+"%'");	
//		}  
// 	   for(int i=0;i<whereINList.size();i++)
// 	   {   
// 		   if(i>0)
// 		   {
// 			   sqlstr.append(" or ");  
// 		   }else
// 		   {
// 			   sqlstr.append(" and ( ");    
// 		   }
// 		   sqlstr.append("  a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
// 		   if(i==whereINList.size()-1)
// 			   sqlstr.append(")");  
// 	   } 	  
// 	   //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
// 	   return sqlstr.toString();
//    }
    
    
    public String select_Q05_a0100(String code, String kind,
			String kq_duration, ArrayList kq_dbase_list) {
		StringBuffer sqlstr = new StringBuffer();
		String sort = "";
		String desc = "";
		StringBuffer coloum = new StringBuffer();
		SortBo bo = new SortBo(this.conn, this.userView);
		KqReportInit init = new KqReportInit(this.conn);
		if (sortItem != null && sortItem.length() > 0
				&& !"not".equalsIgnoreCase(sortItem)) {
			sort = bo.getSortSql(sortItem, "");
			desc = bo.getSortSql(sortItem, "", false);
			String[] strs = sortItem.split("`");
			for (int i = 0; i < strs.length; i++) {
				String[] str = strs[i].split(":");
				if (!",a0100,a0101,q03z0,nbase,dbid,a0000,".contains(","
						+ str[0].toLowerCase() + ",")) {
					coloum.append(str[0]);
					coloum.append(",");
				}
			}
		} else if (bo.isExistSort()) {
			sort = bo.getSortSqlTable("");
			desc = bo.getSortSqlTable("", false);

			String[] strs = bo.querrySort().replaceAll(",", "`").split("`");
			for (int i = 0; i < strs.length; i++) {
				String[] str = strs[i].split(":");
				if (!",a0100,a0101,q03z0,nbase,dbid,a0000,".contains(","
						+ str[0].toLowerCase() + ",")) {
					coloum.append(str[0]);
					coloum.append(",");
				}
			}
		} else {
			sort = "order by dbid,a0000";
			desc = "order by dbid desc,a0000 desc";
		}
//		if (coloum.length() > 0) {
			sqlstr.append("select distinct a0100,a0101,q03z0,nbase,"
					+ coloum.toString() + "dbid,a0000  from Q05_arc");
//		} else {
//			sqlstr.append("select distinct a0100,a0101,q03z0,nbase,dbid,a0000  from Q05_arc");
//		}
		sqlstr.append(" where Q03Z0= '" + kq_duration + "'");
		if ("1".equals(kind)) {
			sqlstr.append(" and e0122 like '" + code + "%'");
		} else if ("-2".equals(kind)) {
			sqlstr.append(" and 1=2");
		} else if ("0".equals(kind)) {
			sqlstr.append(" and e01a1 like '" + code + "%'");
		} else {
			sqlstr.append(" and b0110 like '" + code + "%'");
		}
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String dbase = kq_dbase_list.get(i).toString();
			String whereA0100In = RegisterInitInfoData.getWhereINSql(
					this.userView, dbase);
			if (i > 0) {
				sqlstr.append(" or ");
			} else {
				sqlstr.append(" and ( ");
			}
			sqlstr.append("  a0100 in(select distinct a0100 " + whereA0100In
					+ ") ");
			if (i == kq_dbase_list.size() - 1) {
                sqlstr.append(")");
            }
		}
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String dbase = kq_dbase_list.get(i).toString();
			if(i>0){
				sqlstr.append(" OR nbase='" + dbase + "'");
			}else{
				sqlstr.append(" AND (nbase='" + dbase + "'");
			}
			if (i == kq_dbase_list.size() - 1) {
                sqlstr.append(")");
            }
		}
		// sqlstr.append(" and Q03Z5 not in ('01','07','08')");
		return sqlstr.toString();
	}
    /**
     * 通过一个员工编号得到该员工考勤期间的数据
     * */
    public String getOneA0100Data(String code,String kind,String a0100[],String kq_duration,int body_hieght)throws GeneralException
    {
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ArrayList fieldList = new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     if("1".equals(fielditem.getState()))
		 {
   	    	if("N".equals(fielditem.getItemtype()))
      	     {
      	    	if(!"i9999".equals(fielditem.getItemid()))
      	    	{
      	    		column.append(""+fielditem.getItemid()+",");  
      	    		columnlist.add(fielditem.getItemid());
      	    		fieldList.add(fielditem);
      	    	}
      		  }
		 }
   	     				
   	    }  
    	         	
        ContentDAO dao = new ContentDAO(this.conn);            
        StringBuffer one_date=new StringBuffer();  
        String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
        RowSet rowSet=null;
        try{
              String sql_one_a0100=selcet_Q05_one_emp(a0100[0],kq_duration,code,kind,column.toString());
              rowSet=dao.search(sql_one_a0100);              	 
              if(rowSet.next())
             {
             	  for(int i=0;i<fieldList.size();i++ )
               	  {
             		  FieldItem fielditem=(FieldItem)fieldList.get(i);
             		  String itemid=fielditem.getItemid();
             		  String one_filed=rowSet.getString(itemid);
             		  if(one_filed==null||one_filed.length()<=0) {
                          one_filed="0";
                      }
             		  double dv=Double.parseDouble(one_filed);
             		  if(dv>0)
             		  {
             			 int num = fielditem.getDecimalwidth();
							if(one_filed.indexOf(".")!=-1) {
								for (int k = 0; k < num; k++) {
									one_filed += "0";
								}
								one_filed = PubFunc.round(one_filed,num);
							} else{
								
								 
								for (int k = 0; k < num; k++) {
									if (k == 0) {
										one_filed += "." + "0";
									} else {
										one_filed += "0";
									}
								}
								one_filed = PubFunc.round(one_filed,num);
								
							}
             			 //KQRestOper.round(dv+"",2);
             			 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",one_filed,getStyleName("6")));
             		  }else
             		  {
             			 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","&nbsp;&nbsp;",getStyleName("6")));
             		  }             		
               	  }
             	 one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"","&nbsp;&nbsp;",getStyleName("1")));
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
                    e.printStackTrace();
                }
            }
	     } 
        return one_date.toString();
    }
    public static String selcet_Q05_one_emp(String a0100,String kq_duration,String code,String kind,String column){
   	   StringBuffer sqlstr= new StringBuffer();
   	   int l=column.toString().length()-1;
   	   String columnstr=column.toString().substring(0,l);
   	   sqlstr.append("select "+columnstr+" from Q05_arc"); 	   
   	   sqlstr.append(" where Q03Z0 = '"+kq_duration+"'");  	      
        if("1".equals(kind))
 	   {
 	    	sqlstr.append(" and e0122 like '"+code+"%'");
 	   }else if("-2".equals(kind)){
 			sqlstr.append(" and 1=2");	
 	   }else if ("0".equals(kind)){
 		   sqlstr.append(" and e01a1 like '"+code+"%'");
 	   } else {
 			sqlstr.append(" and b0110 like '"+code+"%'");	
 	   }    	  
     	sqlstr.append(" and a0100="+a0100+"");
   	   
   	   return sqlstr.toString();
      }
    /**
     * 判断页码
     * 
     * */
    public int getCurpage(int curpage,int pagesize,int sum_page)
    {
    	if(curpage<=0)
    	{
    		curpage=1;
    	}else if(curpage>sum_page)
    	{
    		curpage=sum_page;
    	}
    	return curpage;
    }
    /**
     * 总纪录数
     * */
//  得到在考勤范围内的部门员工编号，并添加到list中
    public int getAllRecordNum(String code,String kind,String kq_duration,ArrayList kq_dbase_list)throws GeneralException
    { 
//    	ArrayList a0100whereIN= new ArrayList();
//		for(int i=0;i<kq_dbase_list.size();i++)
//		{
//			String dbase=kq_dbase_list.get(i).toString();
//			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
//			a0100whereIN.add(whereA0100In);			
//		}
		String whereA0100=KqReportInit.count_kq_a0100_q05(code,kind,kq_duration,kq_dbase_list,this.userView);
		    
    	ContentDAO dao = new ContentDAO(this.conn);
        String count="";  
        RowSet rowSet=null;
        try{
          
          rowSet = dao.search(whereA0100);
           if(rowSet.next())
           {
        	 count=rowSet.getString("a");
           }
        }catch(Exception e){
 	      throw GeneralExceptionHandler.Handle(e); 
        }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     } 
        int allrow=Integer.parseInt(count);
        return 	allrow;
    }
    /**
     * 以用高度
     * **/
    public double getIsUseHieght(int leng)throws GeneralException
    {
       	
    	double height=Double.parseDouble(getPxFormMm(parsevo.getTop()))+Double.parseDouble(getPxFormMm(parsevo.getBottom()))+Double.parseDouble(getPxFormMm(parsevo.getTitle_h()));
    	height=height+Double.parseDouble(getPxFormMm(parsevo.getHead_h()));
    	height=height+Double.parseDouble(leng+"")+13;
    	  	
    	//处室签字
    	height=height+Double.parseDouble(getPxFormMm(parsevo.getBody_fz()))+13;
    	
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
    
    public double getSpareHieght(int leng)throws GeneralException
    {
       	double spare_hieght=0;
       	double height=getIsUseHieght(leng);
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
	 * 计算表格实际总宽度
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
	public String getColor(String kq_color)
	{
		String color="";
		if(kq_color==null||kq_color.length()<=0)
    	{
			kq_color="";
    	}
    	if("0".equals(kq_color.trim()))
    	{
    		color="#FF0000";
    	}else if("1".equals(kq_color.trim()))
    	{
    		color="#00FF00";
    	}else if("2".equals(kq_color.trim()))
    	{
    		color="#0000FF";
    	}else if("3".equals(kq_color.trim()))
    	{
    		color="#FF9900";
    	}else
    	{
    		color="#000000";
    	}
    	return color;
	}
	
	/**
	 * 产生翻页代码 和 返回按钮
	 * 
	 */
	private String getTurnPageCode(int curPage,String turnpage_sytle,int sum_page,String coursedate)
	{
		StringBuffer code=new StringBuffer("");
		code.append("<table width='50%' height='30' align='center' style='"+turnpage_sytle+"'>");
		code.append("<tr><td align='center'> \n");
	          
		code.append("<select name='curpage' size='1' onchange='javascript:change()'>");
		 for(int i=1;i<=sum_page;i++)
		 {
			 if(i==curPage)
			 {
				 code.append("<option value='"+i+"' selected='selected'>第"+i+"页</option>");
			 }else
			 {
				 code.append("<option value='"+i+"'>第"+i+"页</option>"); 
			 }			 
		 }
	    
	    code.append("</select>");	  
	    code.append("&nbsp;&nbsp;<input type='button' value='生成PDF' onclick='excecutePDF(\""+this.sortItem+"\")' class='mybutton'>");
	    code.append("&nbsp;&nbsp;<input type='button' value='生成EXCEL' onclick='excecuteEXCEL(\""+this.sortItem+"\")' class='mybutton'>");
	    if("hist".equals(this.getSelf_flag()))
	    {
	    	 code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(6);' class='mybutton'>");
	    } else if("back".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
	    	 code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(7);' class='mybutton'>");    
	    }else
	    {
	    	 code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
	 	    if(coursedate.length()>4) {
                code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(2);' class='mybutton'>");
            } else {
                code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(3);' class='mybutton'>");
            }
	    }
	   	
	    code.append("</td></tr></table>");
	    return code.toString();	
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
	public ArrayList getTransString(String codeitemid)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT codeitemdesc FROM codeitem");
		sql.append(" WHERE codesetid='27'");
		sql.append(" AND parentid='"+codeitemid+"'");
		sql.append(" AND codeitemid<>parentid");
		RowSet rowSet=null;
		ArrayList list = new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
			  list.add(rowSet.getString("codeitemdesc"));	
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
                    e.printStackTrace();
                }
            }
	     } 
		return list;
	}	
	public ArrayList getDateList(Connection conn,String coursedate)throws GeneralException
	  {
	  	  ArrayList datelist=new ArrayList();
	  	  RowSet rowSet=null;
	  	  String kq_start;
	      String kq_dd;
	  	  if(coursedate.length()>4)
	  	  {
	  		  String[] date=coursedate.split("-");
		      String kq_year=date[0];
		      String kq_duration=date[1];		      
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
                            e.printStackTrace();
                        }
                    }
			     } 
	  	  }else
	  	  {
	  		  String sql="SELECT scope FROM q05 where q03z0='"+coursedate+"'";
		      ContentDAO dao = new ContentDAO(conn);
				try {
					rowSet = dao.search(sql.toString());
					String scope="";
					if (rowSet.next()) {
                        scope = rowSet.getString("scope");
                    }
					if(scope==null||scope.length()<=0)	
					{
						CommonData vo = new CommonData();
						vo.setDataName(coursedate);
						vo.setDataValue(coursedate);
						datelist.add(vo);	
					}else
					{
						String[] date=scope.split("-");
						kq_start=date[0];
						kq_dd=date[1];
						CommonData vo = new CommonData();
						vo.setDataName(kq_start);
						vo.setDataValue(kq_start);
						datelist.add(vo);
						vo = new CommonData();
						vo.setDataName(kq_dd);
						vo.setDataValue(kq_dd);
						datelist.add(vo);
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
                            e.printStackTrace();
                        }
                    }
			     } 
	  	  }
	      
			return datelist;
	  }
	public String getSortItemDesc() {
		return sortItemDesc;
	}
	public void setSortItemDesc(String sortItemDesc) {
		this.sortItemDesc = sortItemDesc;
	}
}

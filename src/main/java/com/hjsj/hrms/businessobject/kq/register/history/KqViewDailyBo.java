package com.hjsj.hrms.businessobject.kq.register.history;


import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
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
 * 考勤日报表
 * */
public class KqViewDailyBo {
	private Connection conn=null;
	private ReportParseVo parsevo=null;
	private UserView userView=null;
	private String self_flag;
	private String whereIN;//外部条件
	private String cardno="";
	private boolean kqtablejudge;  //首钢增加 true展现本月出缺勤情况统计小计，否则原始
	private String a0100sql = "";
	private HashMap codemap = new HashMap();
	private String uplevel = "";
	private String dbtype;
	private boolean noSelected = false;
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public KqViewDailyBo()
	{
	}
	/**初始化*/
	public KqViewDailyBo(Connection conn)
	{
		this.conn=conn;
		
	}
	public void setSelf_flag(String self_flag) {
		this.self_flag = self_flag;
	}
	public String getSelf_flag() {
		if(this.self_flag==null||this.self_flag.length()<=0) {
            this.self_flag="";
        }
		return this.self_flag;
	}
	public KqViewDailyBo(Connection conn,ReportParseVo parsevo,UserView userView)
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
	 public ArrayList getKqReportHtml(String code,String kind,String kq_duration,String curpagestr,ReportParseVo parsevo,UserView userView,HashMap formHM)throws GeneralException
	 {   
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
     	uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
     	if(uplevel==null||uplevel.length()==0) {
            uplevel="0";
        }
		 ArrayList htmlList = new ArrayList();
		 try{
			this.kqtablejudge=getkqtablejudge(this.conn);  //考勤表：true=展现本月出缺勤情况统计小计 false=不展现
		    this.parsevo=parsevo;	
		    this.userView=userView;
		    if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
		    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
		    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
		    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
		    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
		    	this.noSelected = true;
		    	
		    }
		    KqReportInit kqReprotInit = new KqReportInit(this.conn);
		    ArrayList item_list=kqReprotInit.getKq_Item_list();//考勤参数
		    double spare_h=getSpareHieght(item_list);
		    int pagesize=0;
		    if("#pr[1]".equals(parsevo.getBody_pr())&&parsevo.getBody_rn()!=null&&parsevo.getBody_rn().length()>0)
		    {
		    	pagesize=Integer.parseInt(parsevo.getBody_rn());
		    }else
		    {
		    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+13);
		    	//pagesize=1;
		    }
		    /**得到考勤权限下的人员库**/
		    KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn,this.userView);
    		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);
    		/**结束**/
		    ArrayList datelist=getDateList(this.conn,kq_duration);
		    if("self".equals(this.getSelf_flag()))
		    {
		    	if(this.userView.getUserDeptId()!=null&&this.userView.getUserDeptId().length()>0) {
                    this.whereIN=" from q03_arc WHERE e0122='"+this.userView.getUserDeptId()+"'";
                } else if(this.userView.getUserOrgId()!=null&&this.userView.getUserOrgId().length()>0) {
                    this.whereIN=" from q03_arc WHERE b0110='"+this.userView.getUserOrgId()+"'";
                } else {
                    this.whereIN=" from q03_arc  WHERE a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"'";
                }
			}
		    int allrows;
		    String dbt=this.getDbtype();
		    if("all".equalsIgnoreCase(dbt))
		    {
		    	allrows =getAllRecordNum(code,kind,datelist,kq_dbase_list);
		    }else
		    {
		    	allrows =getAllRecordNum2(code,kind,datelist,dbt);
		    }
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
		   
		    String headHtml=getTableHead(code,kq_duration,curpage,sum_page,datelist,kqtablejudge);
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
		    keylist.add("a0100");
		    keylist.add("nbase");
		    //前面固定值
		    ArrayList a0100list=getA0100List(code,kind,datelist,curpage,pagesize,kq_dbase_list,keylist);
		    //kq_duration考勤区间，用来到Q05进行查询,kqtablejudge=true 展现首钢小计
		    html.append(getBodyHtml(code,kind,datelist,curpage,pagesize,a0100list,item_list,kq_duration,kqtablejudge));
		    
		    html.append("</td></tr><tr><td>");
		    html.append(getTileHtml(code,kind,curpage,sum_page,item_list));
		    html.append("</td></tr>");
		    html.append("<tr><td>");
		    //body高度
		    int body_h=a0100list.size()*(Integer.parseInt(parsevo.getBody_fz())+13);
		    
		    /***显示翻页,表格的绝对定位***/
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
		
        String width=getFactWidth();
        String height=getPxFormMm(parsevo.getTitle_h());
        String sytle=getFontStyle(parsevo.getTitle_fn(),parsevo.getTitle_fi(),parsevo.getTitle_fu(),parsevo.getTitle_fb(),parsevo.getTitle_fz());
        String style_name=getStyleName("3");
        StringBuffer titleTable=new StringBuffer("");		
		String[] temp=transAlign(7);
		String aValign=temp[0];
		String aAlign=temp[1];
		//将表格宽度改为100%，防止该表格和后面的表格对不齐 --wangzhongjun 2010.4.9
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
	 * 得到表头内容 
	 *   #p（页码）  #c 总页数  #e 制作人  #u 制作人所在的单位  #d 日期  #t  时间  #fn宋体 字体名称  #fz15    字体大小
	 *   #fb[0|1]  黑体  #fi[0|1]   斜体  #fu[0|1]   下划线  #pr[0|1]  页行数
	 *   #fh40
	 *   首钢增加 kqtablejudge true展现本月出缺勤情况统计小计
	 * */
	public String getTableHead(String code,String coursedate,int curpage,int sum_page,ArrayList datelist,boolean kqtablejudge)throws GeneralException
	{
		StringBuffer headHtml = new StringBuffer();        
        String [] codeitem=getCodeItemDesc(code);
        String fact_width=getFactWidth();
        String head_height=getPxFormMm(parsevo.getHead_h());
        String sytle=getFontStyle(parsevo.getHead_fn(),parsevo.getHead_fi(),parsevo.getHead_fu(),parsevo.getHead_fb(),parsevo.getHead_fz());
        try{
        	// 将表格宽度改为100%,防止表格对不齐， ---wangzhongjun 2010.4.9
        	headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
        	
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
           
           String dv_style_name=getStyleName("2");
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
           if(kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected ))
           {
        	   headHtml.append(executeTable(1,6,parsevo.getBody_fn(),sytle,head_height,"本月出缺勤情况统计小计",getStyleName("0")));
           }
           headHtml.append("</tr></table>");
           headHtml.append("</td></tr><tr><td>");
           /**基本参数**/
           
        // 将表格宽度改为100%,防止表格对不齐， ---wangzhongjun 2010.4.9
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
	public String getBodyHeadHtml(ArrayList datelist)throws GeneralException
    {
    	StringBuffer bodyheadhtml = new StringBuffer();
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","序号",getStyleName("1")));
    	if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected) {
    		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","部门",getStyleName("5")));
    	}
    	if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected) {
    		KqParameter para = new KqParameter();
        	if(!"1".equals(para.getKq_orgView_post())) {
                bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","岗位",getStyleName("5")));
            }
    	}
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","姓名",getStyleName("5")));
    	bodyheadhtml.append(executeTable2(1,7,parsevo.getHead_fn(),sytle,body_height+"","签到薄",getStyleName("5")));
    	if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected) {
    		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","工号",getStyleName("5")));
    	}
    	ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
    	ArrayList kqq03list=savekqq03list(this.conn,fielditemlist);
    	if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected){
	    	for(int i=1;i<=datelist.size();i++)
	    	{
	    		CommonData vo = (CommonData)datelist.get(i-1);		
	        	String date=vo.getDataValue();         	
	    		if(i==datelist.size())
	    		{
	    			if (kqq03list.size() > 0) {
	    				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("5")));
	    			} else {
	    				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("5")));
	    			}
	    		}else
	    		{
	    		   bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",date,getStyleName("5")));	
	    		}
	    	}
    	}
    	String kqtable=SystemConfig.getPropertyValue("kqtable");//用来控制考勤表头竖型展现 0和null都是正常展现
    	if(kqq03list.size()>0 && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm())|| this.noSelected))
    	{
    		for(int t=0;t<kqq03list.size();t++)
        	{
        		FieldItem fielditem=(FieldItem)kqq03list.get(t);
        		String name = fielditem.getItemdesc();
        		if(kqtable!=null&&kqtable.length()>0&& "5".equals(kqtable))
        		{
        			if((t+1)==kqq03list.size())
            		{
            			bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("5")));
            		}else
            		{
            			bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("5")));
            		}
        		}else
        		{
        			if((t+1)==kqq03list.size())
            		{
            			bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("5")));
            		}else
            		{
            			bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("5")));
            		}
        		}
        	}
    	}
    	return bodyheadhtml.toString();
    }
	/**
	 * 得到body信息内容
	 * 首钢新增参数：kqtablejudge=trun 展现小计 kq_duration 考勤表年月用来到Q05里进行查询
	 * **/
    public String getBodyHtml(String code,String kind,ArrayList datelist,int curpage,int pagesize,ArrayList a0100list,ArrayList item_list,String kq_duration,boolean kqtablejudge)throws GeneralException
    {
    	StringBuffer bodyhtml = new StringBuffer();
    	int body_hieght=Integer.parseInt(parsevo.getBody_fz())+13;
    	String fact_width=getFactWidth();
    	String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	bodyhtml.append("<table  cellspacing='0'  align='left'  valign='top' width='"+fact_width+"'> \n");
    	bodyhtml.append(" <tr valign='middle' align='center'> \n "); 
    	bodyhtml.append(getBodyHeadHtml(datelist));  //得到body头内容
    	bodyhtml.append("</tr>");    	
    	
    	for(int i=0;i<a0100list.size();i++)
    	{
    		int num=(curpage-1)*pagesize+i+1;    		
    		bodyhtml.append("<tr>");
    		bodyhtml.append(executeTable(28,7,parsevo.getBody_fn(),sytle,body_hieght+"",num+"",getStyleName("1")));
    		String a0100[]=(String[])a0100list.get(i);  //有姓名
    		String one_date=getOneA0100Data(code,kind,a0100,datelist,body_hieght,item_list); //头下面每个人的1到30号的内容
    		bodyhtml.append(one_date);
    		if(kqtablejudge && "#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)
    		{
    			String one_value=getOneA0100Value(code,kind,a0100,kq_duration,body_hieght);
        		bodyhtml.append(one_value);
    		}
    		bodyhtml.append("</tr>");
    		
    	}
    	bodyhtml.append("</table>");
    	return bodyhtml.toString();
    }
    /**
     * 得到表尾数据
     * **/
    public String getTileHtml(String code,String kind,int curpage,int sum_page ,ArrayList item_list)throws GeneralException
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
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());    	
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+6; 
    	int numrow_1=getNumRow(strlen);    	
    	if(numrow_1!=0)
    	{
    		note_h_1=note_h_1*numrow_1;	
    	} 

    	// 将表格宽度设置为100%，防止与其他表格对不齐  ---wangzhongjun 2010.4.9
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

    	// 将表格宽度设置为100%，防止与其他表格对不齐  ---wangzhongjun 2010.4.9
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
    	return tilehtml.toString();
    }
	/**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题 30:名字
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
		// 姓名样式
		if (type == 30) {
			tempTable.append(" class='"+style_name+"' ");
			tempTable.append(" nowrap='nowrap'");
			tempTable.append("style='width:50px;'");
		}
		// 工号样式
		if (type == 29) {
			tempTable.append(" class='"+style_name+"' ");
			tempTable.append(" nowrap='nowrap'");
			tempTable.append("style='width:70px;'");
		}
		// 序号样式
		if (type == 28) {
			tempTable.append(" class='"+style_name+"' ");
			tempTable.append(" nowrap='nowrap'");
			tempTable.append("style='width:40px;'");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
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
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
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
		
		tempTable.append(" <td height='"+height+"' width='20'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"'");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
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
			for(int p=0;p<context.length();p++)
			{
				
				String d =context.substring(p,p+1);
				tempTable.append(d);
				tempTable.append("<br>");
			}
		}
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
		}
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	//签到簿宽度
	public String executeTable2(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='40'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
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
		else
		{
			tempTable.append("&nbsp;&nbsp;");	
		}
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	
	public String executeFont(String fontName,String fontStyle,String context)
	{
		
		StringBuffer tempTable=new StringBuffer("");
		if(context!=null&&context.length()>0){
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
    	if(color!=null&&color.length()>0)
    	{
    		style.append("color: "+color+";");
    	}else
    	{
    		style.append("color: #FF0000;");
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
	    }
	    
		return style_name;
	}	
    //  得到在考勤范围内的部门员工编号，并添加到list中
    public ArrayList getA0100List(String code,String kind,ArrayList datelist,int curpage,int pagesize,ArrayList kq_dbase_list,ArrayList keylist)throws GeneralException
    {
    	
    	CommonData start_vo = (CommonData)datelist.get(0);		
    	String start_date=start_vo.getDataName();
    	CommonData end_vo = (CommonData)datelist.get(datelist.size()-1);
    	String end_date=end_vo.getDataName();
    	ArrayList a0100whereIN= new ArrayList();
    	String dbt=this.getDbtype();
    	if(!"self".equals(this.getSelf_flag()))
	    {
    		if("all".equalsIgnoreCase(dbt))
    		{
    			for(int i=0;i<kq_dbase_list.size();i++)
        		{
        			String dbase=kq_dbase_list.get(i).toString();
        			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
        			a0100whereIN.add(whereA0100In);			
        		}
    		}else
    		{
    			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbt);
    			a0100whereIN.add(whereA0100In);	
    		}
	    }
    	//前面 固定值
		String whereA0100="";
		if("all".equalsIgnoreCase(dbt))
		{
			whereA0100=KqReportInit.select_kq_Distincta0100(code,kind,start_date,end_date,a0100whereIN,kq_dbase_list,this.whereIN);
		}else
		{
			whereA0100=KqReportInit.select_kq_Distincta01002(code,kind,start_date,end_date,a0100whereIN,dbt,this.whereIN);
		}
    	ContentDAO dao = new ContentDAO(this.conn);
        ArrayList a0100list=new ArrayList(); 
        RowSet rowSet=null;
        try{
          rowSet=dao.search(whereA0100.toString(),pagesize,curpage);
          while(rowSet.next())
          {
        	  String a0100[] = new String[6];
        	  a0100[0]=rowSet.getString("a0100");
        	  a0100[1]=rowSet.getString("a0101");
        	  a0100[2]=rowSet.getString("nbase");
        	  a0100[3]=getCardNO(rowSet.getString("a0100"),rowSet.getString("nbase"),dao);
        	  a0100[4]=rowSet.getString("e0122");   //考勤表加入部门
        	  a0100[5]=rowSet.getString("e01a1");   //考勤表加入职位
     	      a0100list.add(a0100); 
          }
        }catch(Exception e){
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
        this.a0100sql = whereA0100.toString();
        a0100sql = a0100sql.replace("order by b0110,e0122,e01a1,a0100", "");
        a0100sql = a0100sql.replace("order by q03_arc.b0110,q03_arc.e0122,q03_arc.e01a1,q03_arc.a0100,q03_arc.nbase"," ");
        return a0100list;    	
    }
    
    public ArrayList getList(String sql) {
    	ArrayList list = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try {
	    	rowSet=dao.search(sql);
	        while(rowSet.next()) {
	        	String a0100[] = new String[6];
	        	a0100[0]=rowSet.getString("a0100");
	        	a0100[1]=rowSet.getString("a0101");
	        	a0100[2]=rowSet.getString("nbase");
	        	a0100[3]=getCardNO(rowSet.getString("a0100"),rowSet.getString("nbase"),dao);
	        	a0100[4]=rowSet.getString("e0122");   //考勤表加入部门
	        	a0100[5]=rowSet.getString("e01a1");   //考勤表加入职位
	        	list.add(a0100); 
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
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
//  得到在考勤范围内的部门员工编号，并添加到list中;非全部人员库的时候
    public ArrayList getA0100Listusr(String code,String kind,ArrayList datelist,int curpage,int pagesize,ArrayList kq_dbase_list,ArrayList keylist,String dbt)throws GeneralException
    {
    	
    	CommonData start_vo = (CommonData)datelist.get(0);		
    	String start_date=start_vo.getDataName();
    	CommonData end_vo = (CommonData)datelist.get(datelist.size()-1);
    	String end_date=end_vo.getDataName();
    	ArrayList a0100whereIN= new ArrayList();
    	
    	if(!"self".equals(this.getSelf_flag()))
	    {
    		if("all".equalsIgnoreCase(dbt))
    		{
    			for(int i=0;i<kq_dbase_list.size();i++)
        		{
        			String dbase=kq_dbase_list.get(i).toString();
        			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
        			a0100whereIN.add(whereA0100In);			
        		}
    		}else
    		{
    			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbt);
    			a0100whereIN.add(whereA0100In);	
    		}
	    }
    	//前面 固定值
		String whereA0100="";
		if("all".equalsIgnoreCase(dbt))
		{
			whereA0100=KqReportInit.select_kq_Distincta0100(code,kind,start_date,end_date,a0100whereIN,kq_dbase_list,this.whereIN);
		}else
		{
			whereA0100=KqReportInit.select_kq_Distincta01002(code,kind,start_date,end_date,a0100whereIN,dbt,this.whereIN);
		}
    	ContentDAO dao = new ContentDAO(this.conn);
        ArrayList a0100list=new ArrayList(); 
        RowSet rowSet=null;
        try{
          rowSet=dao.search(whereA0100.toString(),pagesize,curpage);
          while(rowSet.next())
          {
        	  String a0100[] = new String[6];
        	  a0100[0]=rowSet.getString("a0100");
        	  a0100[1]=rowSet.getString("a0101");
        	  a0100[2]=rowSet.getString("nbase");
        	  a0100[3]=getCardNO(rowSet.getString("a0100"),rowSet.getString("nbase"),dao);
        	  a0100[4]=rowSet.getString("e0122");   //考勤表加入部门
        	  a0100[5]=rowSet.getString("e01a1");   //考勤表加入职位
     	      a0100list.add(a0100); 
          }
        }catch(Exception e){
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
        this.a0100sql = whereA0100.toString();
        a0100sql = a0100sql.replace("order by b0110,e0122,e01a1,a0100", "");
        a0100sql = a0100sql.replace("order by q03_arc.b0110,q03_arc.e0122,q03_arc.e01a1,q03_arc.a0100,q03_arc.nbase"," ");
        return a0100list;    	
    }
    /**
     * 通过一个员工编号得到该员工考勤期间的数据
     *   姓名在这里加链接
     * */  
    public String getOneA0100Data(String code,String kind,String a0100[],ArrayList datelist,int body_hieght,ArrayList item_list)throws GeneralException
    {
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
   		  }				
    	    CommonData start_vo = (CommonData)datelist.get(0);		
        	String start_date=start_vo.getDataName();   //开始时间
        	start_vo = (CommonData)datelist.get(datelist.size()-1);
        	String end_date=start_vo.getDataName();     //结束时间
        	ContentDAO dao = new ContentDAO(this.conn);            
            StringBuffer one_date=new StringBuffer();  
            String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
            if (a0100[1] != null && a0100[1].length() == 2) {
            	StringBuffer buff= new StringBuffer();
            	buff.append(a0100[1].substring(0, 1));
            	buff.append("　");
            	buff.append(a0100[1].substring(1, 2));
            	a0100[1] = buff.toString();
            }
            
            
            //部门
            if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected) {
            	String dd=AdminCode.getCode("UM",a0100[4])!=null?AdminCode.getCode("UM",a0100[4],Integer.parseInt(uplevel)).getCodename():"";
            	one_date.append(executeTable(29,7,parsevo.getHead_fn(),sytle,body_hieght+"",dd,getStyleName("5")));
            }
            //职位
            if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected) {
            	KqParameter para = new KqParameter();
            	if(!"1".equals(para.getKq_orgView_post())){
	            	String e01=AdminCode.getCode("@K",a0100[5])!=null?AdminCode.getCode("@K",a0100[5]).getCodename():"";
	            	one_date.append(executeTable(29,7,parsevo.getHead_fn(),sytle,body_hieght+"",e01,getStyleName("5")));
            	}
            }
            //姓名
            String str="<a href='###' onclick='openwin(\""+a0100[0]+"\",\""+a0100[2]+"\",\""+start_date+"\",\""+end_date+"\");'>"+a0100[1]+"</a>";
            one_date.append(executeTable(30,7,parsevo.getHead_fn(),sytle,body_hieght+"",str,getStyleName("5")));
            //为首钢增加 签到薄
            String strft="<a href='###' onclick='openwintable(\""+a0100[0]+"\",\""+a0100[1]+"\",\""+a0100[2]+"\",\""+start_date+"\",\""+end_date+"\");'><img src=/images/view.gif border=0></a>";
            one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_hieght+"",strft,getStyleName("5")));
            if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected) {
            if(this.cardno!=null&&this.cardno.length()>0) {
                one_date.append(executeTable(29,7,parsevo.getHead_fn(),sytle,body_hieght+"",a0100[3],getStyleName("5")));
            } else {
                one_date.append(executeTable(29,7,parsevo.getHead_fn(),sytle,body_hieght+"",a0100[0],getStyleName("5")));
            }
            }
            RowSet rowSet=null;  
            try{
                 String sql_one_a0100=KqReportInit.selcet_kq_one_emp(a0100[2],a0100[0],start_date,end_date,code,kind,column.toString());
              	 rowSet=dao.search(sql_one_a0100);
              	 HashMap kq_item_map = new HashMap();
              	 HashMap kq_item_all = querryKq_item();
              	 while(rowSet.next())
                 {
              		 int existOther = 0;
             	    String q03z0=rowSet.getString("q03z0").trim();     
             	    ArrayList list =new ArrayList();
             	    ArrayList z1_list=new ArrayList();
             	    ArrayList scq_list=new ArrayList();
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
                    			      ArrayList one_list= new ArrayList();
                    			      one_list.add(kq_item);
                    			      one_list.add(new Double(dv));
                    			      list.add(one_list);
                    			      if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
                    			    	  existOther ++;
                    			      }
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
                	    	String  sr=rowSet.getString(fielditem.getItemid());
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
              		 if(existOther == 0) {
             			if(scq_list != null && scq_list.size() > 0){
             				list.add(scq_list);
             			}
             		 }
              			
                	 kq_item_map.put(q03z0,list);
                }              	
              	if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) {
              	 for(int s=0;s<datelist.size();s++)
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
                        		   font_str.append(executeFont(parsevo.getHead_fn(),sytle1,kq_item[0]));
                        	   }  	  
                			}
                	   }
                	   one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
                   }else
                   {
                	   String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
        			   one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); 
                   }  
                }
               }      
        }catch(Exception e){
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
    /**
     * 首钢 增加 本月出缺勤情况统计小计
     * @param code
     * @param kind
     * @param a0100
     * @param kq_duration 考勤时间
     * @return
     */
    public String getOneA0100Value(String code,String kind,String a0100[],String kq_duration,int body_hieght)
    {
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	ArrayList kqq03list=savekqq03list(this.conn,fielditemlist);
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	for(int i=0;i<kqq03list.size();i++)
    	{
    		FieldItem fielditem=(FieldItem)kqq03list.get(i);
    		if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
    	}
    	StringBuffer one_date=new StringBuffer(); 
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<kqq03list.size();t++)
         	{
         		FieldItem fielditem=(FieldItem)kqq03list.get(t);
         		if ("i9999".equals(fielditem.getItemid())) {
					continue;
				}
         		StringBuffer sql_on_a0100=new StringBuffer();
         		String itd= fielditem.getItemid();
         		sql_on_a0100.append("select "+itd+" as one from Q05_arc where Q03Z0='"+kq_duration+"' and ");
        		sql_on_a0100.append("nbase='"+a0100[2]+"' and A0100='"+a0100[0]+"'");
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next())
        		{
        			itemid = rowSet.getString("one");
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
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
        		}else
        		{
        			String sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"#0000FF");
        			font_str.append(executeFont(parsevo.getHead_fn(),sytle1,itemid));
        			one_date.append(executeTable(1,7,parsevo.getHead_fn(),"",body_hieght+"",font_str.toString(),getStyleName("5")));
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
    	return one_date.toString();
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
    public int getAllRecordNum(String code,String kind,ArrayList datelist,ArrayList kq_dbase_list)throws GeneralException
    { 
    	CommonData start_vo = (CommonData)datelist.get(0);		
    	String start_date=start_vo.getDataName();
    	CommonData end_vo = (CommonData)datelist.get(datelist.size()-1);
    	String end_date=end_vo.getDataName();
    	 
		ArrayList a0100whereIN= new ArrayList();
		if(!"self".equals(this.getSelf_flag()))
		{
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				a0100whereIN.add(whereA0100In);			
			}
		}		
		String whereA0100=KqReportInit.count_kq_a0100(code,kind,start_date,end_date,a0100whereIN,this.whereIN);			    
    	ContentDAO dao = new ContentDAO(this.conn);
        String count="";  
        int allrow=0;
         RowSet rowSet=null;
        try{
         
          rowSet = dao.search(whereA0100);
           while(rowSet.next())
           {
        	 count=rowSet.getString("a");
        	 allrow=allrow+Integer.parseInt(count);
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
      
        return 	allrow;
    }
//  得到在考勤范围内的部门员工编号，并添加到list中
    public int getAllRecordNum2(String code,String kind,ArrayList datelist,String dbtype)throws GeneralException
    { 
    	CommonData start_vo = (CommonData)datelist.get(0);		
    	String start_date=start_vo.getDataName();
    	CommonData end_vo = (CommonData)datelist.get(datelist.size()-1);
    	String end_date=end_vo.getDataName();
    	 
		ArrayList a0100whereIN= new ArrayList();
		if(!"self".equals(this.getSelf_flag()))
		{
			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbtype);
			a0100whereIN.add(whereA0100In);			
			
		}		
		String whereA0100=KqReportInit.count_kq_a01002(code,kind,start_date,end_date,a0100whereIN,this.whereIN,dbtype);			    
    	ContentDAO dao = new ContentDAO(this.conn);
        String count="";  
        int allrow=0;
         RowSet rowSet=null;
        try{
         
          rowSet = dao.search(whereA0100);
           while(rowSet.next())
           {
        	 count=rowSet.getString("a");
        	 allrow=allrow+Integer.parseInt(count);
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
      
        return 	allrow;
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
	 * 首钢更改，注销是原始的 
	 * */
	public String getFactWidth()
	{
		String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
		if("1".equals(parsevo.getOrientation().trim()))
		{
			if("px".equals(unit))
			{
				double width;
				if(this.kqtablejudge)
				{
					width=Double.parseDouble(parsevo.getHeight())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}else
				{
					width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width;
				if(this.kqtablejudge)
				{
					width=Double.parseDouble(parsevo.getHeight())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}else
				{
					width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}
				return KqReportInit.round(width+"",0);	
			}
		}else{
			if("px".equals(unit))
			{
				double width;
				if(this.kqtablejudge)
				{
					width=Double.parseDouble(parsevo.getWidth())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}else
				{
					width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width;
				if(this.kqtablejudge)
				{
					width=Double.parseDouble(parsevo.getWidth())/0.14-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}else
				{
					width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				}
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
	
	
	/**
	 * 产生翻页代码 和 返回按钮
	 * 
	 */
	private String getTurnPageCode(int curPage,String turnpage_sytle,int sum_page)
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
//	    code.append("&nbsp;&nbsp;<hrms:priv func_id='27020131,0C31031'><input type='button' value='生成PDF' onclick='excecutePDF()' class='mybutton'></hrms:priv>");
	    if(this.userView.hasTheFunction("27020131")||this.userView.hasTheFunction("0C31031"))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' value='生成PDF' onclick='excecutePDF()' class='mybutton'>");
	    }
	    code.append("&nbsp;&nbsp;<input type='button' value='生成Excel' onclick='exportExcel()' class='mybutton'>");
	    if("coll".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
		    code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(2);' class='mybutton'>");      
	    }else if("select".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(4);' class='mybutton'>"); 
	    }else if("hist".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(5);' class='mybutton'>"); 
	    }else if("back".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
		    code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(7);' class='mybutton'>"); 
	    }else if(!"self".equals(this.getSelf_flag()))
	    {
	    	code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
		    code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(1);' class='mybutton'>"); 
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
                    e.printStackTrace();
                }
            }
	     } 
		return datelist;
  }
	public String getWhereIN() {
		return whereIN;
	}
	public void setWhereIN(String whereIN) {
		this.whereIN = whereIN;
	}
	
    public String getCardNO(String a0100,String nbase,ContentDAO dao)throws GeneralException
    {
    	if(this.cardno==null||this.cardno.length()<=0) {
            return a0100;
        }
    	if(a0100==null||a0100.length()<=0) {
            return a0100;
        }
    	if(nbase==null||nbase.length()<=0) {
            return a0100;
        }
    	String sql="select "+this.cardno+" as cardno from "+nbase+"A01 where a0100='"+a0100+"'";
    	String card="";
    	RowSet rr=null;
        try
        {
        	rr=dao.search(sql);
        	if(rr.next()) {
                card=rr.getString("cardno");
            }
        }catch(Exception e)
        {
        	throw GeneralExceptionHandler.Handle(e);
        }finally
	     {
				if(rr!=null) {
                    try {
                        rr.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		 } 
        return card;
    }
    /*
     * 考勤表 展现的值；
     */
    public static ArrayList savekqq03list(Connection conn,ArrayList fielditemlist)
    {
    	ArrayList list = new ArrayList();
		String content= KqParam.getInstance().getKqBookItems();  //保存指标
		try
		{
			String [] con = content.split(",");
			for(int j=0;j<fielditemlist.size();j++)
			{
				FieldItem fielditem=(FieldItem)fielditemlist.get(j);
				for(int i=0;i<con.length;i++)
				{
					if(con[i].equalsIgnoreCase(fielditem.getItemid()))
					{
						list.add(fielditem.clone());
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	return list;
    }
    /**
     * 考勤表：true=展现本月出缺勤情况统计小计 false=不展现
     * @param conn
     * @return
     */
    public boolean getkqtablejudge(Connection conn)
    {
    	boolean kqjudge=false;
    	try
    	{
    	    String content= KqParam.getInstance().getKqBookItems();
    		if(!"".equals(content)||content.length()>0)
    		{
    			String [] con=content.split(",");
    			kqjudge = con.length>0;
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return kqjudge;
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
	public String getA0100sql() {
		return a0100sql;
	}
	public void setA0100sql(String a0100sql) {
		this.a0100sql = a0100sql;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
}

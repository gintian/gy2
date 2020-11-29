/*
 * Created on 2005-5-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.taglib.ykcard;

import com.hjsj.hrms.actionform.ykcard.CardTagParamForm;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.*;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hjsj.hrms.valueobject.ykcard.RGridView;
import com.hjsj.hrms.valueobject.ykcard.RPageView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-10:16:49:00</p>
 * @author Administrator
 * @version 1.0
 * 
 */


public class YkCardTag extends TagSupport {	
	private String name;               /*执行该标签所在的form*/
	private String property;           /*所在form中的标签参数的View*/
	private String scope;              /*session page*/
	private String disting_pt="1024";  /*分辨率800*600或者1024*768*/
	private String nid;                /*人员ID*/
	private String queryflag;          /*0代表安条件查询1代表安月时间查询2代表安时间段查询3.安时间季度查询*/
	private String userbase;           /*人员库*/
	private int cyear;                 /*年*/               
	private int cmonth;                /*月*/
	private int tabid;                 /*登记表id*/
	private int pageid;                /*登记表页id*/	
	private String cardtype;           /*登记表类型常量表里设置的常量*/
	private String userpriv;           /*用户权限*/
	private String istype;             /*0表示薪酬表。2表示机构1表示职位表3登记表*/
	private String havepriv;           /*是否有权限*/
	private String sub_domain;		   /*输出登记表时显示的附件*/
	private int season;
	private int cyyear;
	private int cymonth;
	private int csyear;
	private int cdyear;
	private int cdmonth;
	private int cmmonth;
	private int cmyear;
	private int ctimes;
	private String cdatestart;
	private String cdateend;
	private int  queryflagtype=1;      /*统计方式只要不是月都是累加并且非数字的不显示*/
	private String infokind;           /*1人员登记表,2单位登记表,4职位登记表,5计划登记表,6基准岗位*/
	private String plan_id;    //活动计划编号
	private String b0110;
	private String nbase;
	private String fieldpurv;  // 1不控制用户指标权限
	private String ykcard_auto;// 登记表单元格字体自动适应大小
	private String base_url="";
	int cyearcurrent=Calendar.getInstance().get(Calendar.YEAR)-2;
	int countyear=6;
	private int degreecurrent=-1;  //次数参数
	private int left_blank=40;
	private static ArrayList<String> per_year_list=new ArrayList<String>();
	private static String date="";
	private static String per_year;
	private int tmargin=0;
	private int bmargin=0;
	private int lmargin=0;
	private int rmargin=0;	
	private String x;
	private String y;
	private String re_tabid="";
	private String year_restrict="";
	private String display_zero="";////显示/打印零,True|False,默认True
	/** pda或app移动应用 */
	private String isMobile;
	/**
	 * 业务日期  2012.8.13或2012-8-13
	 */
	private String bizDate;
	/**
	 *  "MSIE";"Firefox";"Chrome";"Safari";
	 *  
	 */
	private String browser="MSIE";
	/** 浏览器内核主版本号 */
	private int browserMajorVer=-1;
	private String returnvalue;
	
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getX() {
		if(x==null||x.length()<=0)
			x="0";
		return x;
	}
	public void setX(String x) {
		if(x==null||x.length()<=0)
			x="0";
		this.x = x;
	}
	public String getY() {		
		if(y==null||y.length()<=0)
			y="0";
		return y;
	}
	public void setY(String y) {
		if(y==null||y.length()<=0)
			y="0";
		this.y = y;
	}
	private String color="#15428b";
	public int doEndTag() throws JspException{
	    Connection conn=null;
		try{	
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			conn = AdminDb.getConnection();
			//CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(conn,userview);
			//tabid=Integer.parseInt(checkPrivSafeBo.checkResource(IResourceConstant.CARD, String.valueOf(tabid)));
			String cardid=null;
			String type=null;
			ArrayList cardidlist=null;	
			if(per_year==null||per_year.length()<=0)
				per_year=(Calendar.getInstance().get(Calendar.YEAR))+"";
			if(!per_year.equalsIgnoreCase((Calendar.getInstance().get(Calendar.YEAR))+""))
				per_year_list=null;
			//if(per_year_list==null||per_year_list.size()<=0)
			if("statCount".equals(cardtype)){//链接配置参数进入类型为statcount 统计方式 年月类型只显示最新年月不显示最后一条
				per_year_list=new ArrayList();
				for (int i = 0; i < 10; i++) {
					per_year_list.add((Calendar.getInstance().get(Calendar.YEAR))-i+"");
				}
				this.date=Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+Calendar.getInstance().get(Calendar.DATE);
			}else{
				per_year_list=getDistinctYear(conn,this.nid);
			}
			if(per_year_list==null||per_year_list.size()<=0)
				per_year_list.add((Calendar.getInstance().get(Calendar.YEAR))+"");
			if(per_year_list!=null&&per_year_list.size()>0)
			{
				this.cyearcurrent=Integer.parseInt((String)per_year_list.get(0));	
				String lastyear=(String)per_year_list.get(per_year_list.size()-1);				
				if(lastyear!=null&&lastyear.length()>0)
				{
					this.countyear=this.cyearcurrent-Integer.parseInt(lastyear)+1;	
				}else
				{
					this.countyear=per_year_list.size();
				}
				/**
				if(("1".equals(this.isMobile)&&"0".equals(this.returnvalue))){
					for(int i=0;i<per_year_list.size();i++){
						StringBuffer htmlsb = new StringBuffer();
						htmlsb.append(","+per_year_list.get(i));
					}
				}
				*/
			}
			
			if("SS_SETCARD".equalsIgnoreCase(cardtype))
			{
				CardConstantSet cardConstantSet=new CardConstantSet(userview,conn);	
				ContentDAO dao=new ContentDAO(conn);
				String relating=cardConstantSet.getSearchRelating(dao);		
				String b0110=cardConstantSet.getRelatingValue(dao,userview.getA0100(),userview.getDbname(),relating,userview.getUserOrgId());				
				this.b0110=b0110;
				XmlParameter xml=new XmlParameter("UN",this.b0110,"00");
				xml.ReadOutParameterXml("SS_SETCARD",conn,"all");				
				type=xml.getType();
				this.year_restrict=xml.getYear_restrict();
				String flag=xml.getFlag();
				boolean app = "1".equals(this.isMobile);
				if(this.userpriv!=null&& "selfinfo".equals(this.userpriv))
				{
					cardidlist=cardConstantSet.setCardidSelectSelfinfo(conn,userview,flag,this.nbase,this.nid,this.b0110,xml, app);
				}else
				{
					cardidlist=cardConstantSet.setCardidSelect(conn,userview,flag,this.nbase,this.nid,this.b0110,xml);
				}
				String tabid_str=String.valueOf(tabid);
				if(tabid_str ==null || "0".equals(tabid_str))
				{
					if(cardidlist!=null&&cardidlist.size()>0)
					{
						CommonData dataobj=(CommonData)cardidlist.get(0);
						cardid=dataobj.getDataValue();
						tabid=Integer.parseInt(cardid);
					}
				}
				cardid=String.valueOf(tabid);
				queryflag=type!=null&&type.length()>=0?type:"0";
				

			}else if("leaber".equalsIgnoreCase(cardtype))
			{
				LeadarParamXML leadarParamXML=new LeadarParamXML(conn);
				String unit_card=leadarParamXML.getTextValue(leadarParamXML.UNIT_CARD);
				if(unit_card==null||unit_card.length()<=0)
				{
					showErrors("2");   
				}else
				{
					String[] unit_cards=unit_card.split(",");
					cardidlist=leadarParamXML.getUnit_card(unit_cards);
					String tabid_str=String.valueOf(tabid);
					if(tabid_str ==null || "0".equals(tabid_str))
					{
						if(cardidlist!=null&&cardidlist.size()>0)
						{
							CommonData da=(CommonData)cardidlist.get(0);
							cardid=da.getDataValue();
							tabid=Integer.parseInt(cardid);
						}
					}
				}
				cardid=String.valueOf(tabid);
				queryflag="5";
			}else if("mycard".equalsIgnoreCase(cardtype))
			{
				
				//我的信息
				cardidlist=getUserViewCardList(userview,conn);
				String tabid_str=String.valueOf(tabid);
				if(tabid_str ==null || "0".equals(tabid_str))
				{
					if(cardidlist!=null&&cardidlist.size()>0)
					{
						CommonData da=(CommonData)cardidlist.get(0);
						cardid=da.getDataValue();
						tabid=Integer.parseInt(cardid);
					}
				}
				cardid=String.valueOf(tabid);
			}else if("jpcard".equalsIgnoreCase(cardtype))
			{
				EngageParamXML epXML = new EngageParamXML(conn);
				EngageParam ep=new EngageParam(conn);
				String employ_card=epXML.getTextValue(EngageParamXML.CARD);	
				if(employ_card==null||employ_card.length()<=0)
				{
					showErrors("5");  
					throw new Exception();
				}else
				{
					cardidlist=ep.getSelectRname(employ_card);
					String tabid_str=String.valueOf(tabid);
					if(tabid_str ==null || "0".equals(tabid_str))
					{
						if(cardidlist!=null&&cardidlist.size()>0)
						{
							CommonData da=(CommonData)cardidlist.get(0);
							cardid=da.getDataValue();
							tabid=Integer.parseInt(cardid);
						}
					}
				}
				cardid=String.valueOf(tabid);
			}
			else if("myposcard".equalsIgnoreCase(cardtype)) {
			    RecordVo constant_vo = ConstantParamter.getRealConstantVo("ZP_POS_TEMPLATE",conn);
			    tabid = Integer.parseInt(constant_vo.getString("str_value")!=null?constant_vo.getString("str_value"):"1"); 
			    cardid=String.valueOf(tabid);
			    queryflag = "0";			    
			}else if("statCount".equals(cardtype)||"myInfo".equals(cardtype)||"salaryCard".equals(cardtype)){
				this.nbase=userview.getDbname();
				cardid=String.valueOf(tabid);
				if("statCount".equals(cardtype)||"salaryCard".equals(cardtype))
					queryflag="1";
				
			}else{
			  RecordVo constant_vo=ConstantParamter.getRealConstantVo(cardtype,conn);     /*获得显示的薪筹表tabid*/
			  if("ZP_POS_TEMPLATE".equalsIgnoreCase(cardtype))
			  {
				  tabid=Integer.parseInt(constant_vo.getString("str_value")!=null?constant_vo.getString("str_value"):"1");			
				  if(!"0".equals(queryflag))
					  queryflag=constant_vo.getString("type")!=null?constant_vo.getString("type").toString():"0";
			  } 
			  if("ZP_POS_TEMPLATE2".equalsIgnoreCase(cardtype))   //招聘管理2版的扩充
			  {
					  ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
					  HashMap map=parameterXMLBo.getAttributeValues();
					  tabid=Integer.parseInt((String)map.get("posCardID"));
					  queryflag="0";
			  }
			  if("plan".equalsIgnoreCase(cardtype))
			  {
				 queryflag="5";
			  }	 
			  cardid=String.valueOf(tabid);
			}
			if(cardid !=null && !"0".equals(cardid))
			{
				JspWriter out=pageContext.getOut();
				if(per_year_list!=null&&per_year_list.size()>0)
				{
					//if(("1".equals(this.isMobile)&&"0".equals(this.returnvalue)))
					{
						StringBuffer htmlsb = new StringBuffer();
						for(int i=0;i<per_year_list.size();i++){
							htmlsb.append(","+per_year_list.get(i));
						}
						out.println("<input type='hidden' id='per_year_list_strid' value='"+htmlsb.substring(1)+"' />");
					}
				}
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
				ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);//系统设置单元格内容自适应
				CardConstantSet cardConstantSet=new CardConstantSet();
				LazyDynaBean rnameExtendAttrBean=cardConstantSet.getRnameExtendAttrBean(conn,cardid);
				if(rnameExtendAttrBean!=null)
				{
					if(ykcard_auto==null||ykcard_auto.length()<=0|| "0".equals(ykcard_auto))
					{
						ykcard_auto=(String)rnameExtendAttrBean.get("auto_size");
					}
					this.display_zero=(String)rnameExtendAttrBean.get("display_zero");
				}
				CardTagParamView cardparam;
				Object bean ;
				//cardparam =(CardTagParamView)TagUtils.getInstance().lookup(pageContext, name, property, scope);//减少和Struts的粘合  xuj update 2015-05-29
				if("application".equals(scope)){
						bean = pageContext.getServletContext().getAttribute(name);
				}else if("session".equals(scope)){
						bean = pageContext.getSession().getAttribute(name);
				}else if("request".equals(scope)){
						bean = pageContext.getRequest().getAttribute(name);
				}else{
					bean = pageContext.findAttribute(name);
				}
				cardparam = (CardTagParamView)PropertyUtils.getProperty(bean, property);
				String firstFlag="";//第一次进入标记，第一次进入我的薪酬登记表时firstFlag为“”
				if(!"5".equals(queryflag))
				{
				
					CardTagParamForm cardForm  = (CardTagParamForm)bean;
					/*CardTagParamForm cardForm;
					cardForm=(CardTagParamForm)TagUtils.getInstance().lookup(pageContext, name, scope);*/
					userbase=cardForm.getUserbase();
					firstFlag=cardForm.getFirstFlag();
					this.plan_id=cardForm.getPlan_id();
					//减少和Struts的粘合， xuj update 2015-05-29
					if(userbase==null||userbase.length()==0){
						userbase = nbase;
					}
					if (fieldpurv!=null)
						cardForm.setFieldpurv(fieldpurv);
					else
						fieldpurv = cardForm.getFieldpurv();
				}
				if(("statCount".equals(cardtype)||"salaryCard".equals(cardtype)||"SS_SETCARD".equalsIgnoreCase(cardtype))&&(this.date!=null&&!"".equals(this.date))&&"1".equals(firstFlag)){
					
					SimpleDateFormat sdf=null;
					if(this.date.length()<11)
						 sdf=new SimpleDateFormat("yyyy-MM-dd");
					else
						 sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date data=sdf.parse(this.date);
					Calendar cal=Calendar.getInstance();
					cal.setTime(data);
					cyear=cal.get(Calendar.YEAR);
				   	cmonth=cal.get(Calendar.MONTH)+1;
				    cyyear=cal.get(Calendar.YEAR);
				    cymonth=cal.get(Calendar.MONTH)+1;
				    cmyear=cal.get(Calendar.YEAR);
				    cmmonth=cal.get(Calendar.MONTH)+1;
				    csyear=cal.get(Calendar.YEAR);
				    if(cmonth>0&&cmonth<=3)
				    	season=1;
				    else if(cmonth>3&&cmonth<=6)
				    	season=2;
				    else if(cmonth>6&&cmonth<=9)
				    	season=3;
				    else if(cmonth>9&&cmonth<=12)	
				    	season=4;  
				    	
				    cdyear=cal.get(Calendar.YEAR);
				    cdatestart=cyear+"-" + cmonth+"-" +cal.get(Calendar.DATE);
				    cdateend=cyear+"-" + cmonth+"-" +cal.get(Calendar.DATE);
				    cdmonth=cal.get(Calendar.MONTH)+1;
				    ctimes=cardparam.getCtimes();	
				    
				    if(!"5".equals(queryflag)){
				    	CardTagParamForm cardForm  = (CardTagParamForm)bean;
				    	cardForm.setFirstFlag("");
				    }
				    if("SS_SETCARD".equalsIgnoreCase(cardtype)){
				    	cardparam.setCyear(cyear);
				    	cardparam.setCmonth(cmonth);
				    	cardparam.setCyyear(cyyear);
				    	cardparam.setCymonth(cymonth);
				    	cardparam.setCsyear(csyear);
				    	cardparam.setSeason(season);
				    	cardparam.setCdyear(cdyear);
				    	cardparam.setCdatestart(cdatestart);
				    	cardparam.setCdateend(cdateend);
				    	cardparam.setCdmonth(cdmonth);
				    	cardparam.setCtimes(ctimes);
				    	
				    }
				    
				}else{
					cyear=cardparam.getCyear();
				   	cmonth=cardparam.getCmonth();
				    cyyear=cardparam.getCyyear();
				    cymonth=cardparam.getCymonth();
				    cmyear=cardparam.getCyear();
				    cmmonth=cardparam.getCmonth();
				    csyear=cardparam.getCsyear();
					season=cardparam.getSeason();  
				    cdyear=cardparam.getCdyear();
				    cdatestart=cardparam.getCdatestart();
				    cdateend=cardparam.getCdateend();
				    cdmonth=cardparam.getCdmonth();
				    ctimes=cardparam.getCtimes();			   
				}
				queryflagtype=cardparam.getQueryflagtype();			    
			    pageid=cardparam.getPageid();                       /*在标签参数View中得到页*/
			    this.degreecurrent=getDegreeCurrent(conn,nid);
			    setJavaScript();                                    /*设置要执行的javascript完成提交的值得传递*/
			    
			   	int height=getTable_height(conn,tabid+"");
			   	int width=getTable_width(conn,tabid+"");			   
			   	getMargin(conn);//页边据			  
			   	height=height+this.tmargin+this.bmargin;
			   	width=width+this.lmargin+this.rmargin;
			   	//liuy 2014-10-25 start
			   	int pageTitleTop=25;//控制页标签距离页面顶部的距离
			   	if(cardidlist!=null&&cardidlist.size()>1){
			   		pageTitleTop=30;
				}
			   	//liuy end
			   	out.println("<script type=\"text/javascript\">");// 23957  changxy 20161102 页面的宽度与 选择列表的宽度 如果超出重新计算
			   	out.println("function setPageWidth(){ var pagewidth=document.getElementById('pageID').clientWidth;var tablewidth=document.getElementById('tableID').clientWidth;");
			   	out.println("if(tablewidth>pagewidth){document.getElementById('pageID').style.width=document.getElementById('tableID').clientWidth+"+this.lmargin+"+"+this.rmargin+";}");
			   	out.println("}</script>");//changxy end
			   	if(Integer.parseInt(this.getX())>=25)
			   	{
			   		out.println("<table border=\"0\" style=\"position:absolute;top:0;left:50;"+(("1".equals(this.isMobile)&&"0".equals(this.returnvalue))?"display:none":"")+"\" cellspacing=\"0\" align=\"left\" valign=\"top\" cellpadding=\"0\">");
					out.println("<tr>");					
					if("SS_SETCARD".equalsIgnoreCase(cardtype)||"leaber".equalsIgnoreCase(cardtype)||"mycard".equalsIgnoreCase(cardtype))
					{
				      setSelectCardno(conn,cardidlist); 
				    }
					setSelectRadio(conn);
					if("leaber".equalsIgnoreCase(cardtype)||"mycard".equalsIgnoreCase(cardtype)||"myInfo".equals(cardtype))
					{
					    setPdfRadio();	
					}
					setReturnBack();
				    out.println("</tr >");
				    out.println("</table >");
                    out.println("<div  style=\"position:relative;top:"+this.getX()+";left:"+this.getY()+";width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;border:0px solid #878886;\">");
/*				    if("1".equals(isMobile))  // 隐藏纸张边框
                        out.println("<div  style=\"position:relative;top:"+this.getX()+";left:"+this.getY()+";width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;border:0px solid #878886;\">");
				    else
                        out.println("<div  style=\"position:relative;top:"+this.getX()+";left:"+this.getY()+";width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;border:4px solid #878886;border-top-width:1px;border-left-width:1px;border-left-style:solid;border-top-style:solid;\">");
*/			   	}else
			   	{
					out.println("<div  id='pageID'   style=\"position:relative;width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;\">");
/*			   	    if("1".equals(isMobile))
                        out.println("<div  id='pageID'   style=\"position:relative;width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;border:0px solid #878886;margin-right: auto; margin-left: auto;\">");
			   	    else
			   	    	if(!cardtype.equals("zp_noticetemplate_flag"))
			   	    		//浏览器兼容，去掉此样式：margin-right: auto; margin-left: auto; guodd 17-11-07
			   	    		out.println("<div  id='pageID'   style=\"position:relative;width:"+(width)+";height:"+(height)+";background-color:#FFFFFF;border:4px solid #878886;border-top-width:1px;border-left-width:1px;border-left-style:solid;border-top-style:solid;\">");
*///			   	    out.println("<table id='tableID' border=\"0\" style=\"position:absolute;top:5;left:50;"/*liuy 2014-10-16 修改0为5*/+(("1".equals(this.isMobile)&&"0".equals(this.returnvalue))?"display:none":"")+"\" cellspacing=\"0\" align=\"left\" valign=\"top\" cellpadding=\"0\">");
					out.println("<table id='tableID' border=\"0\" style=\"position:absolute;top:12;left:50;"/*liuy 2014-10-16 修改0为5  wangb 20170828 修改5为12 30884*/+(("1".equals(this.isMobile)&&"0".equals(this.returnvalue))?"display:none":"")+"\" cellspacing=\"0\" align=\"left\" valign=\"top\" cellpadding=\"0\">");
					out.println("<tr>");					
					if("SS_SETCARD".equalsIgnoreCase(cardtype)||"leaber".equalsIgnoreCase(cardtype)||"mycard".equalsIgnoreCase(cardtype)||"jpcard".equalsIgnoreCase(cardtype))
					{
				      setSelectCardno(conn,cardidlist); 
				    }	
					setSelectRadio(conn);
					if("leaber".equalsIgnoreCase(cardtype)||"mycard".equalsIgnoreCase(cardtype)||"jpcard".equalsIgnoreCase(cardtype)||"myInfo".equals(cardtype))
					{
					    setPdfRadio();	
					}
					setReturnBack();
				    out.println("</tr >");
				    out.println("</table >");	
			   	}    
			   	if(this.infokind!=null&& "5".equals(this.infokind))
			   		userbase="Usr";//绩效的人员库默认Usr
			   	if(this.infokind!=null&& "2".equals(this.infokind)&&userbase==null)
			   		userbase="Usr";
			   	out.println(printTitleImage(conn,tabid,pageid));
			    setFormPageTitle(conn); //页签放置底部                            /*显示各个页title*/
			    setFormPageTopTitle(conn,pageTitleTop+20);//pageTitleTop控制页标签距离页面顶部的距离
     			printCard(userbase,conn);                           /*显示整个grid*/
     			//printBrokenLine(userbase,conn);
     			out.println("</div>");
				
			}else
			{
				showErrors(istype);                               /*出错提示*/
			}
    	}catch(Exception e)
		{
    		e.printStackTrace();
    		showErrors(istype);	
		}
    	finally{
    		try{		
    			if (conn != null){
    				conn.close();
    			}
    		}catch (SQLException sql){
    			sql.printStackTrace();
    		}
    	}
    	pageContext.setAttribute("re_tabid",this.tabid+"");	
   		return SKIP_BODY;
	}
	public void release(){
		super.release();
	}
	private void showErrors(String istype)                                                  //没有设置薪酬表的错误信息函数
	{
		try{
			pageContext.getOut().println("<script type=\"text/javascript\">");// 23957  changxy 我的薪酬没有设置薪酬表时 页面加载执行空setPageWidth方法 防止前台加载报错
			pageContext.getOut().println("function setPageWidth(){");
			pageContext.getOut().println("}</script>");//changxy end
			pageContext.getOut().println("<table>");
			pageContext.getOut().println("<tr>");
			pageContext.getOut().println("<td valign=\"middle\" align=\"center\">");
			if("0".equals(istype))
			    pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.brand.prompt") + "</font>");   
			else if("1".equals(istype))
				pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.pos.prompt") + "</font>");   	
			else if("2".equals(istype))
				pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.org.prompt") + "</font>");   	
			else if("3".equals(istype))
				pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.enrol.prompt") + "</font>");   	
			else if("4".equals(istype))
				pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.brand.prompt") + "</font>");  
			else if("5".equals(istype))
				pageContext.getOut().println("<font style=\"font-weight:bold;font-size:20pt\";>" + ResourceFactory.getProperty("ykcard.jp.prompt") + "</font>");
			pageContext.getOut().println("</td>");
			pageContext.getOut().println("</tr>");
			pageContext.getOut().println("</table>");	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void setJavaScript()                                              //执行传递参数提交的javascript函数
	{
		try{
			pageContext.getOut().println("<link href=\""+this.getBase_url()+"/css/css1_brokenline.css\" rel=\"stylesheet\" type=\"text/css\">");
			if(!("1".equals(isMobile)&&"0".equals(returnvalue))){//优化移动app程序加载速率  xuj 2013-11-06
				pageContext.getOut().println("<script type=\"text/javascript\" src=\"/js/popcalendar.js\" /> ");
				pageContext.getOut().println("<SCRIPT language=JavaScript src=\"/js/meizzDate.js\"></SCRIPT>");
				pageContext.getOut().println("<script language=\"javaScript\">");
				pageContext.getOut().print("function changepropertyvalue(pageid){");
				pageContext.getOut().print("this.document." + name + ".pageid.value=pageid;");
				pageContext.getOut().print("this.document." + name + ".disting_pt.value=screen.width;");
				pageContext.getOut().print("}");	         
				pageContext.getOut().print("function changepropertyvalueselect(obj){");			
				pageContext.getOut().print("var pageid=obj.value;");
				pageContext.getOut().print("this.document." + name + ".pageid.value=pageid;");
				pageContext.getOut().print("this.document." + name + ".disting_pt.value=screen.width;");
				//pageContext.getOut().print("location.href=\"\"");
				pageContext.getOut().print("this.document." + name + ".submit()");
				pageContext.getOut().print("}");	 
		       
				pageContext.getOut().println("function changequerytype(querytype){");
				pageContext.getOut().println("switch(querytype.value){");
				pageContext.getOut().println("case '1':");
				pageContext.getOut().println("{");
				pageContext.getOut().println("dateid.style.display='';");
				pageContext.getOut().println("yeardateid.style.display='none';");
				pageContext.getOut().println("ddateid.style.display='none';");
				pageContext.getOut().println("seasonid.style.display='none';");
				pageContext.getOut().println("break;");
				pageContext.getOut().println("}");
				pageContext.getOut().println("case '2':");
				pageContext.getOut().println("{");
				pageContext.getOut().println("ddateid.style.display='';");
				pageContext.getOut().println("yeardateid.style.display='none';");
				pageContext.getOut().println("dateid.style.display='none';");
				pageContext.getOut().println("seasonid.style.display='none';");
				pageContext.getOut().println("break;");
				pageContext.getOut().println("}");
				pageContext.getOut().println("case '3':");
				pageContext.getOut().println("{");
				pageContext.getOut().println("seasonid.style.display='';");
				pageContext.getOut().println("yeardateid.style.display='none';");
				pageContext.getOut().println("ddateid.style.display='none';");
				pageContext.getOut().println("dateid.style.display='none';");
				pageContext.getOut().println("break;");
				pageContext.getOut().println("}");
				pageContext.getOut().println("case '4':");
				pageContext.getOut().println("{");
				pageContext.getOut().println("yeardateid.style.display='';");
				pageContext.getOut().println("dateid.style.display='none';");
				pageContext.getOut().println("ddateid.style.display='none';");
				pageContext.getOut().println("seasonid.style.display='none';");
				pageContext.getOut().println("break;");
				pageContext.getOut().println("}");
				pageContext.getOut().println("}");
				pageContext.getOut().println("}");	
				
				pageContext.getOut().println("function search(){");
				pageContext.getOut().print("document.getElementById('firstFlag').value='';");
				pageContext.getOut().print("cardTagParamForm.submit(); ");
				pageContext.getOut().println("}");
				pageContext.getOut().println("function setFirstFlagValue(){document.getElementById('firstFlag').value='';}");
				pageContext.getOut().println("function check()");
				pageContext.getOut().println("{");
				if("SS_SETCARD".equalsIgnoreCase(cardtype)&&this.year_restrict!=null&&this.year_restrict.length()>0)
				{
					pageContext.getOut().println("var obj=document.getElementById(\"queryflagtypeid\")");
					pageContext.getOut().println("var flag='';");
					pageContext.getOut().println("if(obj){for(var i=0;i<obj.options.length;i++){ if(obj.options[i].selected){flag=obj.options[i].value;break;}}}");
					pageContext.getOut().println("if(flag!='2'){return true;}");
					pageContext.getOut().println("else{");
					pageContext.getOut().println("var obj=document.getElementById(\"starttimeid\");");
					pageContext.getOut().println("if(obj){var value=obj.value;");
					pageContext.getOut().println("  if(value.length>5){var year=value.substring(0,4);var yI=parseInt(year)");
					pageContext.getOut().println("   if(yI<"+year_restrict+"){alert(\"起始时间年不能小于"+year_restrict+"年\");return false;}");
					pageContext.getOut().println("  }");
					pageContext.getOut().println("}");
					pageContext.getOut().println("var obj=document.getElementById(\"endtimeid\");");
					pageContext.getOut().println("if(obj){var value=obj.value;");
					pageContext.getOut().println("  if(value.length>5){var year=value.substring(0,4);var yI=parseInt(year)");
					pageContext.getOut().println("   if(yI<"+year_restrict+"){alert(\"结束时间年不能小于"+year_restrict+"年\");return false;}");
					pageContext.getOut().println("  }");
					pageContext.getOut().println("}");				
					pageContext.getOut().println("document.getElementById('firstFlag').value='';");
					pageContext.getOut().println("return true;");
					pageContext.getOut().println("}");
					
				}else
				{
					pageContext.getOut().println("document.getElementById('firstFlag').value='';");
					pageContext.getOut().println("return true;");
				}
				pageContext.getOut().println("}");
				pageContext.getOut().print("</script>");
			}else {
				pageContext.getOut().println("<script language=\"javaScript\">");
				pageContext.getOut().println("function changepropertyvalue(pageid){");
				pageContext.getOut().println("this.document." + name + ".pageid.value=pageid;");
				pageContext.getOut().println("document.getElementsByName('pageid')[0].value=pageid;");
				pageContext.getOut().println("this.document." + name + ".disting_pt.value=screen.width;");
				pageContext.getOut().println("this.document." + name + ".submit()");
				pageContext.getOut().println("}");
				pageContext.getOut().println("</script>");
			}
			}catch(Exception e)
			{
				System.out.println("set script error");
				e.printStackTrace();
			}
	}
	private void setFormPageTitle(Connection conn)                                         //显示各个页的函数
	{
		  String url = "this.document." + name + ".submit()";
		  try{
		  	//DataEncapsulation().getSmallestTop(tabid,pageid,disting_pt)函数返回页所在高的位置
			 int height=getTable_height(conn,tabid+"")+this.tmargin+this.bmargin-22;
		  	 pageContext.getOut().println("<table style=\"position:absolute;top:" +  (height) + "px;left:" + (new DataEncapsulation().getSmallestLeft(tabid,pageid,disting_pt,conn)+this.lmargin+this.rmargin) + "px\">");
			 /*pageContext.getOut().println("<td nowrap>");
			 pageContext.getOut().println("<table border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"0\">");*/
			 pageContext.getOut().println("<tr>");
		  	 pageContext.getOut().print("<input type=\"hidden\" id=\"disting_pt\" name=\"" + property + ".disting_pt\" value=\"-1\">");	
		  	 List pageData=new DataEncapsulation().getPagecount(tabid,conn);
		     if(!pageData.isEmpty()){
		        pageContext.getOut().print("<input type=\"hidden\" id=\"pageid\" name=\"" + property + ".pageid\" value=\"-1\">");	
		       /* StringBuffer selectHtml=new StringBuffer();
		        selectHtml.append("<select name=\"tabid\" size=\"1\" onchange=\"javascript:changepropertyvalueselect(this)\">");*/
		        for(int i=0;i<pageData.size()&& pageData.size()>1;i++)
		        {
		        	pageContext.getOut().println("<td nowrap>");
		         	DynaBean rec=(DynaBean)pageData.get(i);
		         	pageContext.getOut().println("&nbsp;&nbsp;<a style=\"color:#1B4A98;text-decoration:none;\" href=\"javascript:" + url + "\" onclick=\"changepropertyvalue(" + rec.get("pageid") + ")\">");
	                pageContext.getOut().println(rec.get("title") + "</a>");	
		         	 /*String  pp_s=(String)rec.get("pageid");
		         	 if(pp_s==null||pp_s.length()<=0)
		         		pp_s="0";
		         	 int pp=Integer.parseInt(pp_s);
		         	 if(this.pageid==pp)
		        	  selectHtml.append("<option value=\""+rec.get("pageid")+"\" selected >"+rec.get("title")+"</option>");
		        	 else
		        	  selectHtml.append("<option value=\""+rec.get("pageid")+"\">"+rec.get("title")+"</option>");*/
	                pageContext.getOut().println("</td>");
		        }
		        /*selectHtml.append("</select>");
		        pageContext.getOut().println(selectHtml.toString());*/
		      } 
		      
			  pageContext.getOut().println("</tr>");
			  pageContext.getOut().println("</table>");
			  //pageContext.getOut().println("</td>"); 
		  	}catch(Exception e)
			{
		  		e.printStackTrace();
		  	} 	    
	}	
	private void setSelectRadio(Connection conn)                              //加载选择统计方式的框
	{
		
		try{
			if(queryflag!=null && "1".equals(queryflag))
			{
			JspWriter out=pageContext.getOut();
			//out.println("<table style=\"position:absolute;top:3;left:" +  (new DataEncapsulation().getMaxRightddateRadio(tabid,pageid,disting_pt,conn)+20) + "\">");
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" height='28' cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
			out.println("<tr>");
		  
			  out.println("<td nowrap valign='middle'>统计方式&nbsp;</td><td nowrap valign=\"bottom\"><select name=\"" +  property + ".queryflagtype\"" + " size=\"1\" onchange=\"javascript:changequerytype(this);\" id=\"queryflagtypeid\">");
			  if(queryflagtype==1)  
			    out.println("<option  value=\"1\" selected=\"selected\">月</option>");	
			  else
			  	out.println("<option  value=\"1\" >月</option>");
			  if(queryflagtype==3)  
			    out.println("<option  value=\"3\" selected=\"selected\">季度</option>");	
			  else
			  	out.println("<option  value=\"3\" >季度</option>");
			  if(queryflagtype==4)  
			    out.println("<option  value=\"4\" selected=\"selected\">年</option>");	
			  else
			  	out.println("<option  value=\"4\" >年</option>");			
			  if(queryflagtype==2)  
			    out.println("<option  value=\"2\" selected=\"selected\">时间段</option>");	
			  else
			  	out.println("<option  value=\"2\" >时间段</option>");
			  out.println("</select>");   
			  out.println("</td>");
			  out.println("</tr>");
			  out.println("</table>");
			  out.println("</td>");
			  setFormCardYearDate(conn);
			  setFormCardDate(conn);
			  setFormCardDoubleDate();
			  setFormCardSeason(conn);			  
			}
		}catch(Exception e)
		{e.printStackTrace();}
	}	
	private void setFormCardYearDate(Connection conn)                                    //显示时间的函数
	{
		JspWriter out=pageContext.getOut();
		try{
			//out.println("<table style=\"position:absolute;top:3;left:" + new DataEncapsulation().getMaxRight(tabid,pageid,disting_pt,conn) + "\">");
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
			out.print("<tr id=\"yeardateid\"");
			 if(queryflagtype!=4) 
			 	out.print(" style=\"display:none\"");
			out.print(">");
			out.println("<td nowrap>");
		  	out.println("<select name=\"" +  property + ".cyyear\"" + " size=\"1\" onchange=\"javascript:search();\"  >");  //添加search 方法 切换年份 自动查询 changxy
			for(int i=0;i<this.per_year_list.size();i++){		
				if("SS_SETCARD".equalsIgnoreCase(cardtype))
				{
					if(this.year_restrict!=null&&this.year_restrict.length()>0)
					{
						if(Integer.parseInt(per_year_list.get(i).toString())<Integer.parseInt(this.year_restrict))
								continue;
					}
				}
			    if(cyyear==Integer.parseInt(per_year_list.get(i).toString()))
			     out.println("<option  value=\"" + per_year_list.get(i).toString() + "\" selected=\"selected\">" + per_year_list.get(i).toString() + "</option>");
			    else
			     out.println("<option  value=\"" + per_year_list.get(i).toString() + "\">" + per_year_list.get(i).toString() + "</option>"); 
			}
			out.println("</select>年");         
			out.println("<input type=\"hidden\" name=\"" + property + ".cymonth\"" + "value=\"13\"/>");
			out.println("<input type=\"submit\"  class=\"mybuttons\" value=\"&nbsp;确定&nbsp;\" onclick=setFirstFlagValue();>");
			setPdfbutton();
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
		}catch(Exception e)
		{e.printStackTrace();}
	}
	private void setFormCardDate(Connection conn)                                    //显示时间的函数
	{
		JspWriter out=pageContext.getOut();
		try{
			//out.println("<table style=\"position:absolute;top:3;left:" + new DataEncapsulation().getMaxRight(tabid,pageid,disting_pt,conn) + "\">");
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
			out.print("<tr  id=\"dateid\"");
			 if(queryflagtype!=1) 
			 	out.print(" style=\"display:none\"");
			out.print(">");
			out.println("<td nowrap>");
		  	     out.println("<select name=\"" +  property + ".cyear\"" + " size=\"1\">");
		  	  //   int cyearcurrent=Calendar.getInstance().get(Calendar.YEAR);
		  	    /*for(int i=this.cyearcurrent;i<this.cyearcurrent +this.countyear;i++){
			     	if(cmyear==i)
			     		out.println("<option  value=\"" + i + "\" selected=\"selected\">" + i + "</option>");
			     	else
			     		out.println("<option  value=\"" + i + "\">" + i + "</option>"); 
			       }*/
		  	      for(int i=0;i<this.per_year_list.size();i++){		
		  	    	if("SS_SETCARD".equalsIgnoreCase(cardtype))
					{
						if(this.year_restrict!=null&&this.year_restrict.length()>0)
						{
							if(Integer.parseInt(per_year_list.get(i).toString())<Integer.parseInt(this.year_restrict))
									continue;
						}
					}  
			     	if(cyear==Integer.parseInt(per_year_list.get(i).toString()))
			     		out.println("<option  value=\"" + per_year_list.get(i).toString() + "\" selected=\"selected\">" + per_year_list.get(i).toString() + "</option>");
			     	else
			     		out.println("<option  value=\"" + per_year_list.get(i).toString() + "\">" + per_year_list.get(i).toString() + "</option>"); 
			       }
			     out.println("</select>年");         
			    // out.println("<select name=\"" + property + ".cmonth\"" + " size=\"1\">");
			     if(this.degreecurrent>1){
				     out.println("<select name=\"" + property + ".cmonth\"" + " size=\"1\" >"); // 按次数查询时 点击次数再查询 自动查询 changxy
			     }else{
				     out.println("<select name=\"" + property + ".cmonth\"" + " size=\"1\" onchange=\"javascript:search();\">"); // 查询月份时 切换月份时 自动查询 changxy			    	 
			     }

			      for(int i=1;i<=13;i++){
			      	if(cmmonth==i)
			      	{
			      		if(i==13){
				         	out.println("<option  value=\"13\" selected=\"selected\">全月</option>"); //全年改为全月 月份为全月 changxy  //out.println("<option  value=\"13\" selected=\"selected\">全年</option>"); 
				         }
				         else{
				         	out.println("<option  value=\"" + i + "\" selected=\"selected\">" + i + "</option>"); 
				         }
			      	}else
			      	{
				      	if(i==13){
				         	out.println("<option  value=\"13\">全月</option>"); //全年改为全月 月份为全月 changxy out.println("<option  value=\"13\">全年</option>"); 
				         }
				         else{
				           out.println("<option  value=\"" + i + "\">" + i + "</option>"); 
				         }
			      	}
			     }
			    out.println("</select>&nbsp;月");  
			   
			    if(this.degreecurrent>1)
			    {
			    	 out.println("<select name=\"" + property + ".ctimes\"" + " size=\"1\"onchange=\"javascript:search();\" >"); //次数 点击次数查询
			    	 if(ctimes==11)
				         out.println("<option  value=\"11\" selected=\"selected\">全月</option>"); 
				    else
				    	 out.println("<option  value=\"11\">全月</option>"); 
				    for(int i=1;i<=this.degreecurrent;i++)
				    {
				      	if(ctimes==i)
				      	{
				      		out.println("<option  value=\"" + i + "\" selected=\"selected\">" + i + "</option>"); 
				      	}else
				      	{
				      		out.println("<option  value=\"" + i + "\">" + i + "</option>"); 
		    	        }
				     }
				    
				    out.println("</select>&nbsp;次"); 
			    }			   
			    out.println("<input type=\"submit\"  class=\"mybuttons\" value=\"&nbsp;确定&nbsp;\" onclick=setFirstFlagValue();>");
			    setPdfbutton();
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
		}catch(Exception e)
		{e.printStackTrace();}
	}
	private void setFormCardSeason(Connection conn) throws Exception
	{
		JspWriter out=pageContext.getOut();
		try{
			//out.println("<table style=\"position:absolute;top:3;left:" + new DataEncapsulation().getMaxRight(tabid,pageid,disting_pt,conn) + "\">");
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
			out.println("<tr  id=\"seasonid\"");
			 if(queryflagtype!=3) 
			 	out.print(" style=\"display:none\"");
			 out.print(">");
			out.println("<td nowrap>");
			  out.println("<select name=\"" +  property + ".csyear\"" + " size=\"1\">");
			    /*for(int i=this.cyearcurrent;i<this.cyearcurrent +this.countyear;i++){
			     	if(csyear==i)
			     		out.println("<option  value=\"" + i + "\" selected=\"selected\">" + i + "</option>");
			     	else
			     		out.println("<option  value=\"" + i + "\">" + i + "</option>"); 
			       }*/
			      for(int i=0;i<this.per_year_list.size();i++){		
			    	  if("SS_SETCARD".equalsIgnoreCase(cardtype))
						{
							if(this.year_restrict!=null&&this.year_restrict.length()>0)
							{
								if(Integer.parseInt(per_year_list.get(i).toString())<Integer.parseInt(this.year_restrict))
										continue;
							}
						}  
			     	if(csyear==Integer.parseInt(per_year_list.get(i).toString()))
			     		out.println("<option  value=\"" + per_year_list.get(i).toString() + "\" selected=\"selected\">" + per_year_list.get(i).toString()+ "</option>");
			     	else
			     		out.println("<option  value=\"" + per_year_list.get(i).toString() + "\">" + per_year_list.get(i).toString() + "</option>"); 
			       }
			     out.println("</select>&nbsp;年");         
			     out.println("<select name=\"" + property + ".season\"" + " size=\"1\" onchange=\"javascript:search();\" >"); //查询季度时 切换季度 自动查询 changxy
			      for(int i=1;i<=5;i++){
			      	if(season==i)
			      	{
			      		if(i==5){
				         	out.println("<option  value=\"5\" selected=\"selected\">全年<option>"); 
				         }
				         else{
				         	out.println("<option  value=\"" + i + "\" selected=\"selected\">" + i + "</option>"); 
				         }
			      	}else
			      	{
				      	if(i==5){
				         	out.println("<option  value=\"5\">全年<option>"); 
				         }
				         else{
				           out.println("<option  value=\"" + i + "\">" + i + "</option>"); 
				         }
			      	}
			     }
			    out.println("</select>季度");    
			    out.println("<input type=\"submit\"  class=\"mybuttons\" value=\"&nbsp;确定&nbsp;\" onclick=setFirstFlagValue();>");
			    setPdfbutton();
			 out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
		}catch(Exception e)
		{e.printStackTrace();}
	}
	private void setFormCardDoubleDate() throws Exception
	{
		JspWriter out=pageContext.getOut();
		//out.println("<table style=\"position:absolute;top:3;left:" + new DataEncapsulation().getMaxRightddate(tabid,pageid,disting_pt) + "\">");
		pageContext.getOut().println("<td nowrap>");
		out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
		out.println("<tr id=\"ddateid\"");
		 if(queryflagtype!=2) 
		 	out.print(" style=\"display:none\"");
		 out.print(">");
		out.println("<td nowrap>");
		/*if("1".equals(this.isMobile)){
			out.println("<input type=\"text\" name=\"" +  property + ".cdatestart\"" + " value=\"" + cdatestart + "\" class=\"textColorWrite\" size=\"10\" id=\"starttimeid\" >--");
			out.println("<input type=\"text\" name=\"" +  property + ".cdateend\"" + " value=\"" + cdateend + "\"  class=\"textColorWrite\" size=\"10\" id=\"endtimeid\" >");
			out.println("<input type=\"submit\"  class=\"mybutton\" value=\"&nbsp;确定&nbsp;\" onclick=\"javascript:return validatedate();\" >");
		}else*/{
			out.println("<input type=\"text\" name=\"" +  property + ".cdatestart\"" + " value=\"" + cdatestart + "\" size=\"10\" id=\"starttimeid\" onclick=\"popUpCalendar(this,this,'','','','',false,false)\" readonly=\"readonly\" >至");
			out.println("<input type=\"text\" name=\"" +  property + ".cdateend\"" + " value=\"" + cdateend + "\" size=\"10\" id=\"endtimeid\"  onclick=\"popUpCalendar(this,this,'','','','',false,false)\" readonly=\"readonly\">");
			out.println("<input type=\"submit\"  class=\"mybuttons\" value=\"&nbsp;确定&nbsp;\"  onclick=\"setFirstFlagValue();return check();\">");
			setPdfbutton();
		}
	    out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</td>");
	}
	/**
	 * 直接按钮输出（生成PDF）
	 * liuy 2014-11-2 调整我的薪酬登记表生成pdf间距
	 * @throws Exception
	 */
	private void setPdfbutton() throws Exception{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		if("SS_SETCARD".equalsIgnoreCase(cardtype)||"statCount".equals(cardtype)||"salaryCard".equals(cardtype)){
			//liuy 2015-2-27 3242：自助平台查看员工薪酬输出花名册授权问题 start
			//String functionId = "noinfo".equalsIgnoreCase(userpriv)?"03020101":"0102010301,0102010302";
		
			JspWriter out=pageContext.getOut();
			if(userview.hasTheFunction("0302010301")&&"noinfo".equalsIgnoreCase(userpriv)||userview.hasTheFunction("0102010301")&&!"noinfo".equalsIgnoreCase(userpriv)){				
				out.println("<input type='button' value='生成PDF' class=\"mybuttons\" style=\"margin-top:4px;cursor:pointer\" onclick='excecuteword(\"false\",\"pdf\")'>");
			}
			if(userview.hasTheFunction("0302010302")&&"noinfo".equalsIgnoreCase(userpriv)||userview.hasTheFunction("0102010302")&&!"noinfo".equalsIgnoreCase(userpriv)) {
				out.println("<input type='button' value='生成Word' class=\"mybuttons\" style=\"margin-top:4px;cursor:pointer\" onclick='excecuteword(\"false\",\"word\")'>");
			}
			//liuy 2015-2-27 end
		}
	}
	private void setPdfRadio()throws Exception
	{
		/*UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		if("SS_SETCARD".equalsIgnoreCase(cardtype))
		{
			if(userview.hasTheFunction("03020101")||userview.hasTheFunction("01020101"))
			{
				JspWriter out=pageContext.getOut();
				pageContext.getOut().println("<td nowrap>");
				out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
				out.println("<tr>");		
				out.println("<td nowrap>");	  
				
			    out.println("<input type='button' value='生成PDF' class='mybutton' onclick='excecutePDF()'>");
			    out.println("</td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("</td>");
			}
		}else */
		if("leaber".equalsIgnoreCase(cardtype)||"myInfo".equals(cardtype))
		{
			JspWriter out=pageContext.getOut();
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" cellspacing=\"1\"  align=\"left\" cellpadding=\"0\" style=\"margin-top:4px\">");
			out.println("<tr>");		
			out.println("<td nowrap>");
			out.println("<button  class=\"mybuttons\" style=\"cursor:pointer\" onclick='excecuteword(\"false\",\"pdf\")'>生成PDF</button>");
			out.println("<button  class=\"mybuttons\" style=\"cursor:pointer\" onclick='excecuteword(\"false\",\"word\");'>生成Word</button>");
		    out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
		}else if("jpcard".equalsIgnoreCase(cardtype))
		{
			JspWriter out=pageContext.getOut();
			pageContext.getOut().println("<td nowrap>");
			out.println("<table border=\"0\" cellspacing=\"1\"  align=\"left\" cellpadding=\"0\">");
			out.println("<tr>");		
			out.println("<td nowrap>");
			out.println("<button  class=\"mybuttons\" style=\"cursor:pointer\" onclick='excecutePDF();'>生成PDF</button>");		    
		    out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
		}
		
	}
	//card的主要函数显示整个的Grid表格
	private String fenlei_type="";
	private void printCard(String userbase,Connection conn){
	 JspWriter out=pageContext.getOut();   
	 //System.out.println();//创建输出的对象
	 DataEncapsulation encap=new DataEncapsulation();                             //创建封装Grid数据的对象
	 UserView userview=null;
	 if("zp_noticetemplate_flag".equals(cardtype)){
		 userview=new UserView("su",conn);
		 try {
			 userview.canLogin(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }else{
		 userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
	 }
	 encap.setUserview(userview);
     List rgrids=encap.getRgrid(tabid,pageid,conn);                               //获得Grid各个cell的List的对象
     HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridMapList=encap.getGridMapList();//记录每个单元格 rleft+rwidth 等的位置信息
     HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList=encap.getGridTopIndexList();//初始化记录单元格 rtop+rheight位置信息
     HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList=encap.getLastGridTopList();
     int heightn=encap.getPagesize(tabid,pageid,conn);     
     MadeFontsizeToCell mc=new MadeFontsizeToCell(browser);                              //创建的字体适应cell大小的对象
     mc.setAuto(this.ykcard_auto);
     int fontsize;
     String fontweight="";
     ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);     
     if(this.infokind!=null&& "5".equals(this.infokind)&&this.plan_id!=null&&this.plan_id.length()>0)
     {
    	 StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
    	 alUsedFields=statisticPlan.khResultField(alUsedFields,this.plan_id);
     }
     GetCardCellValue card=new GetCardCellValue(); 
     this.fenlei_type=card.getOneFenleiYype(userview,userbase, this.nid, conn);
     card.setDisplay_zero(this.display_zero);//创建获得单元个cell值的对象
     card.setBizDate(bizDate);
     List setList=encap.GetSets(tabid,pageid,conn);     
     //获得到整个Grid所有的子集名称
     //显示页面标头等信息的结束
     //显示各个单元个得开始
     RGridView rgrid;
     int topn;                                                  //单元格的上边位置
     int leftn;                                                 //单元格的左边位置
     int heights;                                               //单元格的高
     int widthn;                                                //单元格的宽
     String hz="";                                              //单元格的内容是说明信息
     String fontStr="";                                         //字体类型比如"宋体"
     try{
        if(rgrids!=null&&!rgrids.isEmpty())
        {
        	for(int i=0;i<rgrids.size();i++)
        	{
        		rgrid=(RGridView)rgrids.get(i);          		
        		if(!"C".equals(rgrid.getFlag())){
                	 
        			if("800".equals(disting_pt))
   	                {     
	   		             leftn=(int)Float.parseFloat(rgrid.getRleft("1"))+this.lmargin;
	   		             topn=(int)Float.parseFloat(rgrid.getRtop("1"))+this.tmargin;
	   		             if("Safari".equals(browser)&&browserMajorVer<537)  // Safari7以前版本, 避免Safari7的双线问题
	   		            	 widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
	   		             else 
	   		                 widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) - 1;
	   		             heights=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
   	                }
   	                else
   	                {
	   		             leftn=(int)Float.parseFloat(rgrid.getRleft())+this.lmargin;
	   		             topn=(int)Float.parseFloat(rgrid.getRtop())+this.tmargin;
	   		             if("Safari".equals(browser)&&browserMajorVer<537)
	   		            	 widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
	   		             else
	   		            	widthn=(int)Float.parseFloat(rgrid.getRwidth());
	   		             heights=(int)Float.parseFloat(rgrid.getRheight()); 
   		            } 
        			
                 	
        			fontweight=rgrid.getFonteffect();
        		    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
    	               fontweight="bold";
    	            else
    	               fontweight="normal";
        		    hz=rgrid.getCHz();        		    
        		    String[] align=mc.getAlign(rgrid.getAlign());
        		    if("1".equals(rgrid.getSubflag()))
        		    {
        		    	align[1]="top";
        		    	align[0]="left";
        		    }
        		   HashMap<Double,ArrayList<RGridView>> gridTopMap=gridTopMapList.get(pageid+"");  
        		   HashMap<Double,ArrayList<RGridView>> LastgridTopMap=lastGridTopList.get(pageid+"");//当前单元格上方单元格  
        		   int num=0;
        		  //判断当前单元格是否单独占一行
             		if(gridTopMap.get(Double.parseDouble(rgrid.getRtop())).size()==1&&("Firefox".equals(browser)||"Safari".equals(browser)||"edge".equals(browser)||"Chrome".equals(browser))) {
             			//独占一行单元格下一行单元格数
             			ArrayList<RGridView> nextRowList=gridTopMap.get(Double.parseDouble(rgrid.getRtop())+Double.parseDouble(rgrid.getRheight()));
             			if(nextRowList!=null&&nextRowList.size()>0)
             				num=2;
             		}
        		    // Safari7 有单元格撑大问题
        		    out.println("<table border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\"  style=\"table-layout:fixed;border-collapse:collapse;BACKGROUND-COLOR:transparent;position:absolute;top:" +topn+"px;left:"+leftn+"px;width:"+(widthn-num)+"px;height:"+heights+"px\">");
   	                out.println("<tr>");
   	                String cssLineHeight = getCssLineHeight(rgrid);
   	                //liuy 2015-12-17 优化登记表不显示格线的情况下，数据显示垂直居中 begin
   	                boolean tempflag = false;
   	                if(("A".equals(rgrid.getFlag())|| "B".equals(rgrid.getFlag())|| "K".equals(rgrid.getFlag()))&& "1".equals(rgrid.getSubflag())){
   	                	XmlSubdomain xmlSubdomain=new XmlSubdomain(rgrid.getSub_domain());
   	                	xmlSubdomain.getParaAttribute();
   	                	if(StringUtils.isNotEmpty(xmlSubdomain.getFields())){   	                		
   	                		if(("7".equals(rgrid.getAlign())||"6".equals(rgrid.getAlign())||"8".equals(rgrid.getAlign()))&&"false".equals(xmlSubdomain.getVl())&&"false".equals(xmlSubdomain.getHl()))
   	                			tempflag = true;
   	                	}
   	                }
   	                
   	                if(i>0){
   	                	HashMap<Double,ArrayList<RGridView>> gridMap=gridMapList.get(pageid+"");
   	                	if(gridMap!=null) {
   	                		ArrayList<RGridView> list=gridMap.get(Double.parseDouble(rgrid.getRleft()));
   	                		if(list!=null&&list.size()>0) {
   	                			for(int j=0;j<list.size();j++) {
   	                				RGridView gridView=list.get(j);
   	                				//存储rleft+rwidth=当前rgrid 的rleft
   	                				if(!gridView.getGridno().equals(rgrid.getGridno())&&
   	                					Double.parseDouble(gridView.getRtop())<=Double.parseDouble(rgrid.getRtop())	
   	                				&&Double.parseDouble(gridView.getRtop())+Double.parseDouble(gridView.getRheight())>Double.parseDouble(rgrid.getRtop())	
   	                						) {
   	                					if("1".equals(gridView.getR())&&"1".equals(rgrid.getL()))
   	                					{
   	                						rgrid.setL("0");
   	   	                					break;
   	                					}
   	                				}
   	                			}
   	                		}
   	                		
   	                	}
   	                	/*lastRgrid=(RGridView)rgrids.get(i-1);
   	                	if(lastRgrid.getRtop().equals(rgrid.getRtop())){ //登记表一行数据rtop相等时 画表时第二列单元格 左边线设置为不显示 防止两条线并线导致线条过粗  changxy 20170412
   	                		if("1".equals(lastRgrid.getR())&&"1".equals(rgrid.getL())){//第一列单元格有右边线的存在 第二个单元格有左边线的存在 舍去当前单元格左边线
   	                			rgrid.setL("0");
   	                		}
   	                	}*/
   	                }
   	                
   	         
             	/*if(gridTopMap!=null) {
             		ArrayList<RGridView> list=gridTopMap.get(Double.parseDouble(rgrid.getRtop())+Double.parseDouble(rgrid.getRheight()));
             		if(list!=null&&list.size()>0) {
             			for(int j=0;j<list.size();j++) {
             				RGridView gridView=list.get(j);
             				if(!gridView.getGridno().equals(rgrid.getGridno())&&
             					Double.parseDouble(gridView.getRleft())<=Double.parseDouble(rgrid.getRleft())	
             				&&Double.parseDouble(gridView.getRleft())+Double.parseDouble(gridView.getRwidth())>Double.parseDouble(rgrid.getRleft())	
             						) {
             					if("1".equals(rgrid.getB())&&"1".equals(gridView.getT()))
             				   {
             						rgrid.setB("0");
             						break;
             				   }
             				}
             			}
             		}
             		
             		
             	} */
             	
             	if(LastgridTopMap!=null) {
             		//计算当前单元格上方位置 是否有单元格  ，上方单元格 底部边线是否为空，若为空 当前单元格上边线置空
             		ArrayList<RGridView> topList=LastgridTopMap.get(Double.parseDouble(rgrid.getRtop()));
             		if(topList!=null&&topList.size()>0) {
             			for (RGridView rGridView : topList) {
							if(!rGridView.getGridno().equals(rgrid.getGridno())
							 &&(Double.parseDouble((rgrid.getRleft()))+Double.parseDouble(rgrid.getRwidth())<Double.parseDouble(rGridView.getRleft())+Double.parseDouble(rGridView.getRwidth())||Double.parseDouble(rGridView.getRwidth())+Double.parseDouble(rGridView.getRleft())>Double.parseDouble(rgrid.getRleft()))
									) {
								if("1".equals(rgrid.getT())&&"0".equals(rGridView.getB())||"1".equals(rgrid.getT())&&"1".equals(rGridView.getB())) {
									rgrid.setT("0");
									break;
								}
							}
						}
             		}
             	}
             	
             	boolean borderFlag=false;
             	if(widthn!=0) {
             		if("MSIE".equalsIgnoreCase(browser)) {
             			if("1".equals(rgrid.getR())&& "1".equals(rgrid.getL())) {
             				borderFlag=true;
             			}
             		}
             	}
             	
             	
             	if(tempflag){
             		if("1".equals(rgrid.getSubflag())) {
             			out.println("<td style=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid)+";word-wrap:break-word;"
             					+ "overflow:hidden;"+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" "
             					+ "align=\"" + align[0] + "\" width="+(!"MSIE".equals(browser)?(widthn-2):(borderFlag?widthn-2.66f:widthn-1.33f))+" height="+heights+">");
             		}else {
//             			out.println("<td style=\"word-wrap:break-word;padding:0px;overflow:hidden;"+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" align=\"" + align[0] + "\" width="+(num!=0?widthn-num:widthn-1)+"  height="+heights+">");
             			out.println("<td style=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid)+";padding:0px;overflow:hidden;"
             					+ ""+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" "
             				    + "align=\"" + align[0] + "\" width="+(num!=0?widthn-num:(borderFlag?widthn-2.66f:widthn-1.33f))+"  height="+heights+">");
             		}
               }else{
            	   if("1".equals(rgrid.getSubflag())) {
            		   out.println("<td style=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid)+";word-wrap:break-word;overflow:hidden;"
            		   		+ ""+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" "
            		   	    + "valign=\"" + align[1] + "\" align=\"" + align[0] + "\" width="+(!"MSIE".equals(browser)?(widthn-2):(borderFlag?widthn-2.66f:widthn-1.33f))+" height="+ heights+">");
            	   }else {
//            		   out.println("<td style=\"word-wrap:break-word;padding:0px;overflow:hidden;"+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\" width="+(num!=0?widthn-num:widthn-1)+" height="+heights+">");
            		   out.println("<td style=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid)+";padding:0px;"
            		   		+ "width:"+((!"MSIE".equals(browser)?(num!=0?widthn-num:widthn-1):(borderFlag?widthn-2.66f:widthn-1.33f)))+"px;"
            		   		+ "height:"+heights+"px;overflow:hidden;"+cssLineHeight+"\" class=\""+new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB())+"\" "
            		   		+ "valign=\"" + align[1] + "\" align=\"" + align[0] + "\"  nowrap>");
            	   }
               }
             	if("1".equals(rgrid.getSubflag())) {
             		if(!"1".equalsIgnoreCase(ykcard_auto)) {
             			out.println("<div  class=\"outer-container\" style=\"height:"+(heights-1)+"px;width:"+(widthn-1)+"px;\">");	
             			out.println("<div  class=\"inner-container\" style=\"height:"+(heights)+"px;width:"+(widthn+16)+"px;\">");
             		}
             		out.println("<div style=\"height:"+heights+"px;width:"+(widthn-1)+"px;\">");
             	}
   	                if("A".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){                  //A人员库
   	                    byte nFlag=0;                                 //0表示人员库
                            ArrayList valueList=null;
                            
                            if("1".equalsIgnoreCase(rgrid.getIsView()))
                            {
                            	 valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
                            }else
                            {
                            	if("P01".equalsIgnoreCase(rgrid.getCSetName())){
                            		valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
                            	}else{
	                            	if(!setList.isEmpty())
	   	                             for(int j=0;j<setList.size();j++)
	   	                             {
	   	                               DynaBean fieldset=(DynaBean)setList.get(j);
	   	                               if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
	   	                            	        
	   	                               	   valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
	   	                           	       break;
	   	                               }
	   	                             }
                            	}
                            }
                            
                            if(valueList !=null &&!valueList.isEmpty())
                            {
	                              if(valueList.size()==1)
	                              {
	                            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                              out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
		                              out.println("</font>");
	                              }else
	                              {
	                            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	                            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	                            	  for(int j=0;j<valueList.size();j++)
		                              {
		                            	  
		                                  if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
		                                  {
		                                	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
				                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
		                                    if(j>0)
		                                    	 out.println("<br>");	                                    
		                                	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
		                                	 out.println("</font>");
		                                  }else{
		                                    out.println("<br>");      
		                                   }
		                                  
		                              }	
	                              }
	                         }else{
	                        	out.println("&nbsp;");    
	                         }
   	                    }else if("A".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
                        {
                        	//人员子集
   	                    	byte nFlag=0;  
   	                       out.println(viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights));   	                    	
                        }else if("B".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag()))
                        {                           //B单位库
   	                    	byte nFlag=2;                                                //2表示单位库
                            ArrayList valueList=null;
                            if(!setList.isEmpty())
	                            for(int j=0;j<setList.size();j++)
	                            {
	                                DynaBean fieldset=(DynaBean)setList.get(j);
	                                if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
	                                   	valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
	                                	  break;
	                                }
	                            }               
                            if(valueList !=null &&!valueList.isEmpty())
                            {
                              	if(valueList.size()==1)
	                            {
	                            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                              out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
		                              out.println("</font>");
	                            }else
	                            {
	                            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	                            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	                            	  for(int j=0;j<valueList.size();j++)
		                              {
		                            	  
		                                  if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
		                                  {
		                                	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
				                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
		                                    if(j>0)
		                                    	 out.println("<br>");	                                    
		                                	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
		                                	 out.println("</font>");
		                                  }else{
		                                    out.println("<br>");      
		                                   }
		                              }
		                                  
		                        }	
                           }else{
                              out.println("&nbsp;");
                          }
   	                    }else if("B".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
   	                    {
   	                        	//单位子集
   	                    	byte nFlag=2;  
    	                    out.println(viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights));   	  
   	                    }else if("K".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){                      //K岗位库
                           byte nFlag=4;                                            //4表示岗位库
                           ArrayList valueList=null;
                           if(!setList.isEmpty())
	                            for(int j=0;j<setList.size();j++)
	                            {
	                                DynaBean fieldset=(DynaBean)setList.get(j);
	                                if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
	                                   	valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
	                                	  break;
	                                }
	                            }              
                          if(valueList !=null &&!valueList.isEmpty()){                              
                        	  if(valueList.size()==1)
	                            {
	                            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                              out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
		                              out.println("</font>");
	                            }else
	                            {
	                            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	                            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	                            	  for(int j=0;j<valueList.size();j++)
		                              {
		                            	  
		                                  if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
		                                  {
		                                	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
				                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
		                                    if(j>0)
		                                    	 out.println("<br>");	                                    
		                                	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
		                                	 out.println("</font>");
		                                  }else{
		                                    out.println("<br>");      
		                                   }
		                              }
		                                  
		                        }
                          }else{
                             out.println("&nbsp;");
                         }
   	                   }else if("K".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
   	                   {
   	                	   //职位子集
   	                	  byte nFlag=4; 
   	                	  out.println(viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights));   	  
   	                   }else if("P".equals(rgrid.getFlag())){                            //p表示照片
   	                	String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
   	                	if(url==null||url.length()<=0)
   	                		url=this.getBase_url();
   	                	if("zp_noticetemplate_flag".equals(cardtype))
   	                		url=url + "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filename=";
   	                	else
   	                		url=url + "/servlet/DisplayOleContent?filename=";
   	                	String filename=ServletUtilities.createPhotoFile(userbase+"A00",nid,"P",pageContext.getSession());
   	                    if(filename!=null && filename.length()>0){
   	                    	 filename = SafeCode.encode(PubFunc.encrypt(filename));
	                         out.println("<img src=\"" + url + filename + "\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
	                     }else{
	                     	 out.println("<img src=\"/images/photo.jpg\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
	                     }
	                  }else if("H".equals(rgrid.getFlag())){                       //H表示文字说明
	                	  hz = "`".equals(hz)?"":hz;//liuy 2015-6-9 9970
                        if(hz !=null && hz.trim().length()>0){                        	
                          //fontsize=mc.ReDrawLitterRect(widthn,heights,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt,rgrid.getField_type(),rgrid.getSlope());
                          ArrayList varlist=new ArrayList();

                          String hzStr=rgrid.getCHz();
                          if(hzStr.indexOf("`")>-1&&hzStr.split("`").length>1) {
                        	 for (int j = 0; j < hzStr.split("`").length; j++) {
								varlist.add(hzStr.split("`")[j]);
							}
                          }else {
                        	  varlist.add(rgrid.getCHz());
                          }
                         
                          fontsize=mc.ReDrawLitterRect(widthn, heights, varlist, Integer.parseInt(rgrid.getFontsize()));
                        	//StringTokenizer Stok=new StringTokenizer(hz,"`");//类获取输入流并将其分析为“标记”，允许一次读取一个标记
                          int last_s=hz.lastIndexOf("`");
	                        if(last_s==(hz.length()-1))
	                        	hz=hz.substring(0,hz.length()-1);
	                        String[] a_stok=hz.split("`");
	                        out.println("<font  style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
	                                            
	                        if(a_stok!=null&&a_stok.length>0)
	                        {
	                        	
	                        	if("MSIE".equals(browser)) {//非IE下换行文字无法垂直对齐 33623  ￥
	                        		for(int s=0;s<a_stok.length;s++)
		                        	{
		                        		if(s>0)
		                        			out.print("<br/>");
		                        		out.println(a_stok[s].replaceAll("　","&nbsp;&nbsp;").replace("：",":").replace("，",",").trim());	
		                        		
		                        	}
	                        	}else {
	                        		StringBuffer sbf=new StringBuffer();
	                        		for(int s=0;s<a_stok.length;s++)
		                        	{
		                        		if(s>0)
		                        			sbf.append("<br/>");
		                        		sbf.append(a_stok[s].replaceAll("  ", "&nbsp;").replaceAll("　", "&nbsp;&nbsp;").replace("：",":").replace("，",",").trim());
		                        		
		                        	}
		                        	out.println(sbf.toString());
	                        	}
	                        	
	                        }else
	                        {
	                        	out.println("&nbsp;"); 
	                        }
	                        out.println("</font>"); 
	                     }else{
                     	     out.println("<br>");      
	                     }   
	                }else if("J".equals(rgrid.getFlag())){                           //J计划库
   	                    	byte nFlag=5;                                            //5表示计划库
   	                    	rgrid.setPlan_id(this.plan_id);
   	                    	if("0".equals(rgrid.getSubflag()))
   	                    	{
   	                    		ArrayList valueList=null;
   	                            valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);	                                       
   	                            if(valueList !=null &&!valueList.isEmpty())
   	                            {
   	                            	for(int j=0;j<valueList.size();j++)
   	                                {
   	                                   if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
   	                                    //获得显示字体的大小 
   	                                	String value=valueList.get(j).toString();
   	                                	value=value.replaceAll("@#@","<br>");     
   	                                	value=value.replaceAll("#@#","<br>");   
   	                                	value=value.replaceAll(" ", "&nbsp;&nbsp;");
   	                                	//fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"");
   	                                   	//fontsize=mc.ReDrawLitterRect(widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"",Integer.parseInt(rgrid.getFontsize()),valueList.get(j)!=null?valueList.get(j).toString():"",disting_pt,rgrid.getField_type(),rgrid.getSlope());
   	                                   	fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
   	                                   	 out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
   	                                     out.println(value!=null?value:"");	 
   	                                   	 out.println("</font>");
   	                                     out.println("<br>");  
   	                                   }else{
   	                                     out.println("<br>"); 
   	                                   }
   	                                }
   	                            }else{
   	                              out.println("&nbsp;");
   	                            }
   	                    	}else if("1".equals(rgrid.getSubflag()))//子集
   	                    	{
   	                    	    rgrid.setPlan_id(this.plan_id);
   	                    		String tmp = viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights);
   	                    		//System.out.println(tmp);
   	                    		out.println(tmp);
   	                    	
   	                    	}

	                }else if("Z".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){                  //A人员库
   	                    	byte nFlag=6;                                 //0表示人员库
                            ArrayList valueList=null;                                      
                            valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
                            if(valueList !=null &&!valueList.isEmpty())
                            {
	                              if(valueList.size()==1)
	                              {
	                            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                              out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
		                              out.println("</font>");
	                              }else
	                              {
	                            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	                            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	                            	  for(int j=0;j<valueList.size();j++)
		                              {
		                            	  
		                                  if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
		                                  {
		                                	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
				                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
		                                    if(j>0)
		                                    	 out.println("<br>");	                                    
		                                	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
		                                	 out.println("</font>");
		                                  }else{
		                                    out.println("<br>");      
		                                   }
		                                  
		                              }	
	                              }
	                         }else{
	                        	out.println("&nbsp;");    
	                         }
	                }else if("Z".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag())){
	                	byte nFlag=6;  
	                    out.println(viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights)); 
	                }else if("D".equals(rgrid.getFlag())){  // 指标公式
	                	 ArrayList valueList=card.getTextValueForCexpress(userbase, conn, card, rgrid, userview,alUsedFields,infokind,this.nid,this.plan_id);
	                	 if(valueList !=null &&!valueList.isEmpty())
                         {
	                              if(valueList.size()==1)
	                              {
	                            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                              out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
		                              out.println("</font>");
	                              }else
	                              {
	                            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	                            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	                            	  for(int j=0;j<valueList.size();j++)
		                              {
		                            	  
		                                  if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
		                                  {
		                                	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
				                              out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
		                                    if(j>0)
		                                    	 out.println("<br>");	                                    
		                                	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
		                                	 out.println("</font>");
		                                  }else{
		                                    out.println("<br>");      
		                                   }
		                                  
		                              }	
	                              }
	                      }else{
	                        	out.println("&nbsp;");    
	                      }
	                	
	                }
	        		else if("E".equals(rgrid.getFlag())){  // 基准岗位
	        			byte nFlag=7;  
	        			if("1".equals(rgrid.getSubflag())){
	                        out.println(viewSubclass(rgrid,conn,userview,cyyear,cymonth,ctimes,userbase,nid,nFlag,widthn,heights));
	        			}
	        			else{
	        				ArrayList valueList=null;                                      
	                        valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
	                        outputGridContent(valueList, rgrid, mc, widthn, heights, fontweight);
	        			}
	        		}
	                else{
	                	out.println("&nbsp;");
	                }
   	                if("1".equals(rgrid.getSubflag())) {
   	                	if(!"1".equals(ykcard_auto)) {
   	                		out.println("</div>");
   	                		out.println("</div>");
   	                	}
   	                	out.println("</div>");		
   	                }
   	                out.println("</td>");    
   	        	    out.println("</tr>");    
   	        	    out.println("</table>");        
        		}
        		else if("C".equals(rgrid.getFlag())){
        			
                } 
              	              
      		}        	
        }
     }catch(Exception e){
     	e.printStackTrace();
     }
   
    //显示各个单元个得结束
    //显示各个单元个要格式化数据的数据的开始
     RGridView rgridc;
    try
	 {
     	if(rgrids!=null&&!rgrids.isEmpty())
     	{
     		for(int i=0;i<rgrids.size();i++)
     		{
     			rgridc=(RGridView)rgrids.get(i);  
     			 if("C".equals(rgridc.getFlag())){
                    if("800".equals(disting_pt)){
			         leftn=(int)Float.parseFloat(rgridc.getRleft("1"))+this.lmargin;
			         topn=(int)Float.parseFloat(rgridc.getRtop("1"))+this.tmargin;
			         if("MSIE".equals(browser)||
   		                     "Safari".equals(browser)&&browserMajorVer<537)//IE 与 Safari 浏览器 跟谷歌浏览器显示宽度不一致  【26626	首开：手机app登陆报错】
			        	 widthn=(int)Float.parseFloat(rgridc.getRwidth("1")) + 1;
			         else
			        	 widthn=(int)Float.parseFloat(rgridc.getRwidth("1")) - 1;
			         heights=(int)Float.parseFloat(rgridc.getRheight("1")) +1;
                   }else
			       {
			         leftn=(int)Float.parseFloat(rgridc.getRleft())+this.lmargin;
			         topn=(int)Float.parseFloat(rgridc.getRtop())+this.tmargin;
			         if("MSIE".equals(browser)||
   		                     "Safari".equals(browser)&&browserMajorVer<537)//IE 与 Safari 浏览器 跟谷歌浏览器显示宽度不一致  【26626	首开：手机app登陆报错】
			        	 widthn=(int)Float.parseFloat(rgridc.getRwidth()) + 1;
			         else
			        	 widthn=(int)Float.parseFloat(rgridc.getRwidth()) - 1;
			         heights=(int)Float.parseFloat(rgridc.getRheight()) + 1;
			       }
                   fontweight=rgridc.getFonteffect();                  
                   if(fontweight !=null && "2".equals(fontweight))
 	                 fontweight="bold";
 	               else
 	                 fontweight="normal";
                   //获得适应单元格大小的字体大小
                   fontsize=mc.ReDrawLitterRect(widthn,heights,rgridc.getCHz(),Integer.parseInt(rgridc.getFontsize()),hz,disting_pt,rgridc.getField_type(),rgridc.getSlope());
                   //fontsize=mc.getFitFontSize(Integer.parseInt(rgridc.getFontsize()), widthn,heights,rgridc.getCHz());
                   hz=rgridc.getCHz();                   
                   String[] align=mc.getAlign(rgridc.getAlign());
                  out.println("<table  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\" style=\"table-layout:fixed;BACKGROUND-COLOR:transparent;position:absolute;top:" + topn+ "px;left:"+ leftn 
   				       + "px;width:" + widthn + "px;height:" + heights + "px;\">");
    	           out.println("<tr>");
    	           out.println("<td style=\"overflow:hidden; \" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgridc.getL(),rgridc.getR(),rgridc.getT(),rgridc.getB())+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\"  width=\"" + widthn + "\" height=\"" + heights + "\">");
//    	           out.println("<td style=\"word-wrap:break-word; overflow:hidden; \" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgridc.getL(),rgridc.getR(),rgridc.getT(),rgridc.getB())+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\"  width=\"" + widthn + "\" height=\"" + heights + "\">");
    	           out.println("<font  color=\""+this.color+"\" style=\"font-weight:" +
    	           		fontweight + ";font-family:"+rgridc.getFontName()+";font-size:" + fontsize + "pt\">"); 
    	           //getFormulaValue()函数是格式化显示数据的函数  
    	           out.println(card.getFormulaValue(rgridc));  
    	           out.println("</font>");    
    	           out.println("</td>");    
    	           out.println("</tr>"); 
    	           out.println("</table>");              
     			 }
     		}     		
     	}
     }catch(Exception e)
	 {
     	e.printStackTrace();
     }
     
     
     
     
     List rpageList=encap.getRpage(tabid,pageid,conn);                            //获得页面title的List的对象
     //显示各个单元个要格式化数据的数据的结束
     try{
      	//显示页面标头等信息的开始
        if(rpageList!=null&&!rpageList.isEmpty()){
          for(int i=0;i<rpageList.size();i++){
          	RPageView rpage=(RPageView)rpageList.get(i);
          	fontsize=Integer.parseInt(rpage.getFontsize());
          	fontweight=rpage.getFonteffect();    
          	
          	if(rpage.getFlag()!=6)
          	{
          		if(fontweight !=null && "2".equals(fontweight))
      	           fontweight="bold";
      	        else
      	           fontweight="normal";
                if("800".equals(disting_pt))
      	        {  
      	           fontsize=Math.round(((float)(fontsize * 800))/1024);
      	           int left_n=Integer.parseInt(rpage.getRleft("1"))+this.lmargin;
      	           out.println("<table style=\"position:absolute;top:" + ( Integer.parseInt(rpage.getRtop("1"))+this.tmargin)+ "px;left:" + left_n + "px\">");
      	        }
                else
                {
                   int left_n=Integer.parseInt(rpage.getRleft())+this.lmargin;
                   int top_n=Integer.parseInt(rpage.getRtop())+this.tmargin;
                   out.println("<table style=\"position:absolute;top:" + top_n + "px;left:" + left_n + "px\">");
                }               
                out.println("<tr>");
   		        out.println("<td valign=\"middle\" align=\"left\" nowrap>");
   		        out.println("<font  style=\"font-weight:" + fontweight + ";font-family:"+rpage.getFontname()+";font-size:" + fontsize + "pt\";>");   
   		        String title = encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,userbase,tabid,this.infokind,rpage.getExtendAttr());
   		        // 处理特殊字符
   		        title = title.replaceAll(" ", "&nbsp;");
   		        title = title.replaceAll("\r\n", "<br>");
   		        out.println(title);
   		        out.println("</font>");
   		        out.println("</td>");
   		        out.println("</tr>");
   		        out.println("</table>");
          	}	 
    	 }
       } 
      }catch(Exception e)
 	 {
         e.printStackTrace();	
      } 
	}
	
	private String getCssLineHeight(RGridView rgrid) {
	    String s = "";
	    if("H".equals(rgrid.getFlag())){
	        int fsize = Integer.parseInt(rgrid.getFontsize());
	        int feffect = Integer.parseInt(rgrid.getFonteffect());  // FIXME 转为Font.PLAIN, BOLD, ITALIC
            Font font = new Font(rgrid.getFontName(), feffect, fsize);
            BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = gg.createGraphics(); // 获得画布
            g.setFont(font);
            int aheight = g.getFontMetrics().getHeight(); // 每一行字的高度
            int awidth = g.getFontMetrics().charWidth('汉');//fontSize;  // 汉字宽
/*
    浏览器计算和分配行间距的方法
　　间距 = "line-height" – "font-size";
　　文本上下分配大小 = 间距/2;
　　字号 = 12px; line-height:3;
　　间距 = 3*12 – 12 = 24(px);
　　文本上下分配大小 = 24/2 = 12(px)
    在大多数浏览器中默认行高百分比大约是 110% 到 120%；数值大约为1, IE为1.14, IE0.92相当于没边距
*/
            //liuy 2014-11-17 819:任免管理：任免管理审批审批表在后台画好的表预演等都正确但是在前台显示任免理由就不完整 start
            double arowspace = (aheight * 0.14);
    	    String v = rgrid.getCHz();
    	    if(v == null)
    	        v = "";
    	    String[] lst = v.split("`");
    	    double vheight = lst.length * aheight + (lst.length) * arowspace*2;
    	    double difference= vheight - Float.parseFloat(rgrid.getRheight());
    	    if(difference>-1&&difference<=3) {
    	        // 任`免`理`由, 字体：宋体 16号, 单元格高 80, line-height: normal会显示不全, 170%或0.95 正合适
    	        s = "line-height: 1.2;";  // 去掉行间距
    	    }else if(difference>3&&difference<=10){
    	    	s = "line-height: 1;";
    	    }else if(difference>10){
    	    	s = "line-height: 0.93;";
    	    }
    	    //liuy end
	    }
	    return s;
	}
	
	private void outputGridContent(ArrayList valueList, RGridView rgrid, MadeFontsizeToCell mc, 
			int widthn, int heights, String fontweight){
		JspWriter out=pageContext.getOut();
		int fontsize;
		try{
			if(valueList !=null &&!valueList.isEmpty())
	        {
	              if(valueList.size()==1)
	              {
	            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
	                  out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
	                  out.println(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
	                  out.println("</font>");
	              }else
	              {
	            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	            	  for(int j=0;j<valueList.size();j++)
	                  {
	                	  
	                      if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
	                      {
	                    	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
	                          out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
	                        if(j>0)
	                        	 out.println("<br>");	                                    
	                    	 out.println(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
	                    	 out.println("</font>");
	                      }else{
	                        out.println("<br>");      
	                       }
	                      
	                  }	
	              }
	         }else{
	        	out.println("&nbsp;");    
	         }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void printBrokenLine(String userbase,Connection conn)
	{

		 JspWriter out=pageContext.getOut();                                          //创建输出的对象
		 DataEncapsulation encap=new DataEncapsulation();                             //创建封装Grid数据的对象
	     List rgrids=encap.getRgrid(tabid,pageid,conn);                   //获得Grid各个cell的List的对象
	     int heightn=encap.getPagesize(tabid,pageid,conn);
	     MadeFontsizeToCell mc=new MadeFontsizeToCell();                              //创建的字体适应cell大小的对象
	     mc.setAuto(this.ykcard_auto);
	     int fontsize;
	     String fontweight="";
	     GetCardCellValue card=new GetCardCellValue();                                //创建获得单元个cell值的对象
	     List setList=encap.GetSets(tabid,pageid,conn);                               //获得到整个Grid所有的子集名称
	   
	     //显示页面标头等信息的结束
	     //显示各个单元个得开始
	     RGridView rgrid;
	     int topn;                                                  //单元格的上边位置
	     int leftn;                                                 //单元格的左边位置
	     int heights;                                               //单元格的高
	     int widthn;                                                //单元格的宽
	     String hz="";                                              //单元格的内容是说明信息
	     String fontStr="";                                         //字体类型比如"宋体"
	     try{
	     	
	        if(!rgrids.isEmpty())
	        {
	        	for(int i=0;i<rgrids.size();i++)
	        	{
	        		rgrid=(RGridView)rgrids.get(i);	        		
	        		if(!"C".equals(rgrid.getFlag())){
	                	 UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
	                 	 if("800".equals(disting_pt))
	   	                {     
		   		             leftn=(int)Float.parseFloat(rgrid.getRleft("1"))-1+this.lmargin;
		   		             topn=(int)Float.parseFloat(rgrid.getRtop("1")) -1+this.tmargin;
		   		             widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
		   		             heights=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
	   	                }
	   	                else
	   	                {
		   		             leftn=(int)Float.parseFloat(rgrid.getRleft())+this.lmargin;
		   		             topn=(int)Float.parseFloat(rgrid.getRtop())+this.tmargin;
		   		             widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
		   		             heights=(int)Float.parseFloat(rgrid.getRheight()) +1;
	   		            }	                 	
	        			fontweight=rgrid.getFonteffect();
	        		    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
	    	               fontweight="bold";
	    	            else
	    	               fontweight="normal";
	        		    hz=rgrid.getCHz();
	        		    String[] align=mc.getAlign(rgrid.getAlign());	        		    
	        		    out.println("<table   border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\"   style=\"table-layout:fixed;position:absolute;top:" + topn + "px;left:"+ leftn 
	   				       + "px;width:" + widthn + "px;height:" + heights + "px;\">");
	   	                out.println("<tr valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
//	   	                out.println("<td style=\"word-break:break-all; overflow:hidden; \" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB()) + "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
	   	                out.println("<td style=\"overflow:hidden; \" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(),rgrid.getR(),rgrid.getT(),rgrid.getB()) + "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
	   	                     if("A".equals(rgrid.getFlag())){                  //A人员库
	   	                         byte nFlag=0;                                 //0表示人员库
	                             ArrayList valueList=null;
	                             if(!setList.isEmpty())
		                             for(int j=0;j<setList.size();j++)
		                             {
		                               DynaBean fieldset=(DynaBean)setList.get(j);
		                               if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
		                                                           
		                               	   valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
		                           	       break;
		                               }
		                             } 
	                             if(valueList !=null &&!valueList.isEmpty()){
		                            fontStr="";		                        
		                         for(int j=0;j<valueList.size();j++)
		                         {
		                             fontStr+=valueList.get(j)!=null?valueList.get(j).toString():"";
		                         }
		                         //获得显示字体的大小
		                         //fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights, rgrid.getFontName());
		                         fontsize=mc.ReDrawLitterRect(widthn,heights,fontStr,Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
		                         out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                         for(int j=0;j<valueList.size();j++)
		                         {
		                            if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
		                               out.println(valueList.get(j).toString());
		                               out.println("<br>");
		                               //System.out.println("font " + valueList.get(j).toString() + "size" + fontsize);
		                             }else{
		                               out.println("<br>");      
		                             }
		                          }
		                         out.println("</font>");
		                        }else{
		                        	out.println("&nbsp;");    
		                        }             
	   	                    }else if("B".equals(rgrid.getFlag())){                           //B单位库
	   	                    	byte nFlag=2;                                                //2表示单位库
	                            ArrayList valueList=null;
	                            if(!setList.isEmpty())
		                            for(int j=0;j<setList.size();j++)
		                            {
		                                DynaBean fieldset=(DynaBean)setList.get(j);
		                                if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
		                                   	valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
		                                	  break;
		                                }
		                            }               
	                            if(valueList !=null &&!valueList.isEmpty()){
	                            for(int j=0;j<valueList.size();j++)
	                            {
	                               if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
	                                //获得显示字体的大小 
	                               	 fontsize=mc.ReDrawLitterRect(widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"",Integer.parseInt(rgrid.getFontsize()),valueList.get(j)!=null?valueList.get(j).toString():"",disting_pt,rgrid.getField_type(),rgrid.getSlope());
	                               	 //fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights, valueList.get(j)!=null?valueList.get(j).toString():"");
	                               	 out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">" + valueList.get(j)!=null?valueList.get(j).toString():"" + "</font>");
	                                 out.println("<br>");
	                               }else{
	                                 out.println("<br>"); 
	                               }
	                            }
	                           }else{
	                              out.println("&nbsp;");
	                          }
	   	                    }else if("K".equals(rgrid.getFlag())){                      //K岗位库
	                           byte nFlag=4;                                            //4表示岗位库
	                           ArrayList valueList=null;
	                           if(!setList.isEmpty())
		                            for(int j=0;j<setList.size();j++)
		                            {
		                                DynaBean fieldset=(DynaBean)setList.get(j);
		                                if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
		                                   	valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
		                                	  break;
		                                }
		                            }              
	                          if(valueList !=null &&!valueList.isEmpty()){
	                              for(int j=0;j<valueList.size();j++)
	                              {
	                                 if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
	                                   //获得显示字体的大小
	                                	 //fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights, valueList.get(j)!=null?valueList.get(j).toString():"");
	                                   fontsize=mc.ReDrawLitterRect(widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"",Integer.parseInt(rgrid.getFontsize()),valueList.get(j)!=null?valueList.get(j).toString():"",disting_pt,rgrid.getField_type(),rgrid.getSlope());
	                                   out.println("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">" + valueList.get(j)!=null?valueList.get(j).toString():"" + "</font>");
	                                   out.println("<br>");
	                                 }else{                                   
	                                   out.println("<br>");
	                                 }
	                               }
	                          }else{
	                             out.println("&nbsp;");
	                         }
	   	                   }else if("P".equals(rgrid.getFlag())){                            //p表示照片
	   	                	String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
	   	                	if("zp_noticetemplate_flag".equals(cardtype))
	   	                		url=url + "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filename=";
	   	                	else
	   	                		url=url + "/servlet/DisplayOleContent?filename=";
	   	                	String filename=ServletUtilities.createPhotoFile(userbase+"A00",nid,"P",pageContext.getSession());	   	                	
	   	                    if(filename!=null){
	   	                    	filename = SafeCode.encode(PubFunc.encrypt(filename));
		                         out.println("<img src=\"" + url + filename + "\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
		                     }else{
		                     	 out.println("<img src=\"/images/nophoto.tmp\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
		                     }
		                  }else if("H".equals(rgrid.getFlag())){                        //H表示文字说明
	                        if(hz !=null && hz.length()>0){
	                        	//fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights, rgrid.getCHz());
	                            fontsize=mc.ReDrawLitterRect(widthn,heights,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt,rgrid.getField_type(),rgrid.getSlope());
		                        StringTokenizer Stok=new StringTokenizer(hz,"`");
		                        out.println("<font  style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
		                        for(;Stok.hasMoreTokens();)
				                {
		                        	 out.println(Stok.nextToken());
		                        	 if(Stok.hasMoreTokens()){			                      
				                       out.println("<br>");		    	
		                            }                              
		                        }
		                        out.println("</font>"); 
		                     }else{
		                     	out.println("&nbsp;");      
		                     }   
		                }	   	                    
	        		}else if("C".equals(rgrid.getFlag())){
	        			out.println("&nbsp;");  
	                }else{
	             	    
	                } 
	              out.println("</td>");    
	        	  out.println("</tr>");    
	        	  out.println("</table>");    	              
	      		}
	        }
	     }catch(Exception e){
	     	e.printStackTrace();
	     }
	   
	    //显示各个单元个得结束
	    //显示各个单元个要格式化数据的数据的开始
	     RGridView rgridc;
	    try
		 {
	     	if(!rgrids.isEmpty())
	     	{
	     		for(int i=0;i<rgrids.size();i++)
	     		{
	     			rgridc=(RGridView)rgrids.get(i);  
	     			 if("C".equals(rgridc.getFlag())){
	                    if("800".equals(disting_pt)){
				         leftn=(int)Float.parseFloat(rgridc.getRleft("1"))+this.lmargin;
				         topn=(int)Float.parseFloat(rgridc.getRtop("1"))+this.tmargin;
				         widthn=(int)Float.parseFloat(rgridc.getRwidth("1")) + 1;
				         heights=(int)Float.parseFloat(rgridc.getRheight("1")) +1;
	                   }else
				       {
				         leftn=(int)Float.parseFloat(rgridc.getRleft())+this.lmargin;
				         topn=(int)Float.parseFloat(rgridc.getRtop())+this.tmargin;
				         widthn=(int)Float.parseFloat(rgridc.getRwidth()) + 1;
				         heights=(int)Float.parseFloat(rgridc.getRheight()) + 1;
				       }
	                   fontweight=rgridc.getFonteffect();
	                   if(fontweight !=null && "2".equals(fontweight))
	 	                 fontweight="bold";
	 	               else
	 	                 fontweight="normal";
	                   //获得适应单元格大小的字体大小
	                   fontsize=mc.getFitFontSize(Integer.parseInt(rgridc.getFontsize()), widthn,heights, rgridc.getCHz());
	                   //fontsize=mc.ReDrawLitterRect(widthn,heights,rgridc.getCHz(),Integer.parseInt(rgridc.getFontsize()),hz,disting_pt,rgridc.getField_type(),rgridc.getSlope());
	                   hz=rgridc.getCHz();
	                   String[] align=mc.getAlign(rgridc.getAlign());
	                   out.println("<table width=\"70%\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\"  style=\"table-layout:fixed;position:absolute;top:" + topn + "px;left:"+ leftn 
	   				       + "px;width:" + widthn + "px;height:" + heights + "px;\">");
	    	           out.println("<tr valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
//	    	           out.println("<td style=\"word-wrap:break-word; overflow:hidden;\" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgridc.getL(),rgridc.getR(),rgridc.getT(),rgridc.getB())+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
	    	           out.println("<td style=\"overflow:hidden;\" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgridc.getL(),rgridc.getR(),rgridc.getT(),rgridc.getB())+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\">");
	    	           out.println("<font  color=\""+this.color+"\" style=\"font-weight:" +
	    	           		fontweight + ";font-family:"+rgridc.getFontName()+";font-size:" + fontsize + "pt\">"); 
	    	           //getFormulaValue()函数是格式化显示数据的函数
	    	           //System.out.println(card.getFormulaValue(rgridc));
	    	           out.println(card.getFormulaValue(rgridc));    
	    	           out.println("</font>");    
	    	           out.println("</td>");    
	    	           out.println("</tr>"); 
	    	           out.println("</table>");  	              
	     			 }
	     		}
	     	}
	     }catch(Exception e)
		 {
	     	e.printStackTrace();
	     }
	     
	     
	     
	     
	     List rpageList=encap.getRpage(tabid,pageid,conn);                            //获得页面title的List的对象
	     //显示各个单元个要格式化数据的数据的结束
	     try{
	      	//显示页面标头等信息的开始
	        if(!rpageList.isEmpty()){
	          for(int i=0;i<rpageList.size();i++){
	          	RPageView rpage=(RPageView)rpageList.get(i);
	          	fontsize=Integer.parseInt(rpage.getFontsize());
	          	fontweight=rpage.getFonteffect();
	            if(fontweight !=null && "2".equals(fontweight))
	    	          fontweight="bold";
	    	       else
	    	          fontweight="normal";
	            if("800".equals(disting_pt))
	    	       {  
	    	          fontsize=Math.round(((float)(fontsize * 800))/1024);
	    	          int left_n=Integer.parseInt(rpage.getRleft("1"))+this.lmargin;
		              int top_n=Integer.parseInt(rpage.getRtop("1"))+this.tmargin;
	    	          out.println("<table style=\"position:absolute;top:" +  top_n + "px;left:" + left_n + "px\">");
	    	       }
	            else
	            {
	               int left_n=Integer.parseInt(rpage.getRleft())+this.lmargin;
	               int top_n=Integer.parseInt(rpage.getRtop())+this.tmargin;
	               out.println("<table style=\"position:absolute;top:" + top_n + "px;left:" + left_n + "px\">");
	            }
	            out.println("<tr>");
	 		   out.println("<td valign=\"middle\" align=\"center\" nowrap>");
	 		   out.println("<font  style=\"font-weight:" + fontweight + ";font-family:"+rpage.getFontname()+"; font-size:" + fontsize + "pt\";>" + encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,userbase,tabid,this.infokind,rpage.getExtendAttr()) + "</font>");   
	 		   out.println("</td>");
	 		   out.println("</tr>");
	 		   out.println("</table>");		 
	    	   }
	       } 
	      }catch(Exception e)
	 	 {
	         e.printStackTrace();	
	      } 
		
	}
	/**
	 * 判断视图是否是年月变化
	 * */
	public String viewIsChangeflag(RGridView rgrid,Connection conn){
		String sql="select useflag from t_hr_busitable where fieldsetid='"+rgrid.getCSetName()+"'";
		ContentDAO dao=new ContentDAO(conn);
		ResultSet rs=null;
		try {
			rs=dao.search(sql);
			String useflag="";
			while(rs.next()){
				useflag=rs.getString("useflag");
			}
			return useflag;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return "";
	}
	
	
	/**
	 * @param userbase
	 * @param conn
	 * @param card
	 * @param rgrid
	 * @param userview
	 * @param nFlag
	 * @param valueList
	 * @param fieldset
	 * @return
	 * @throws Exception
	 */
	private ArrayList getTextValue(String userbase, Connection conn, GetCardCellValue card, RGridView rgrid, UserView userview, byte nFlag, ArrayList valueList, DynaBean fieldset) {
		//获得单元格的内容值
		String changeflag="0";
		if(fieldset!=null)
			changeflag=fieldset.get("changeflag").toString();
		else{
			if("1".equalsIgnoreCase(rgrid.getIsView())){
				//定义视图是否是年月变化
				changeflag=this.viewIsChangeflag(rgrid, conn);
			}
		}
		try{
		  if("0".equals(queryflag))
			valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,Integer.parseInt(queryflag),Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
		  else if(queryflagtype==1)
		  {
			  if(infokind!=null&& "5".equals(infokind))
			  {
				   StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
				   String table_name=statisticPlan.getPER_RESULT_TableName(this.plan_id);
				   rgrid.setCSetName(table_name);
				   rgrid.setPlan_id(this.plan_id);
				   valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,0,cyyear,cymonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
			  }else
			  {
				  valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
			  }
		  }
		     
		   else if(queryflagtype==2)
		 	 valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cdyear,cdmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
		   else if(queryflagtype==3)
		 	 valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),csyear,cdmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
		   else if(queryflagtype==4)
		 	 valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cyyear,cymonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
		   }
		   catch(Exception e)
		   {
			   //liuy 2014-10-20 在这里去掉打印异常方法，如果有指标未构库或被删除，valueList会为null，后台会一直打印错误信息
		   }
		 return valueList;
	}
	
	/**
	 * @return Returns the cmonth.
	 */
	public int getCmonth() {
		return cmonth;
	}
	/**
	 * @param cmonth The cmonth to set.
	 */
	public void setCmonth(int cmonth) {
		this.cmonth = cmonth;
	}
	/**
	 * @return Returns the cyear.
	 */
	public int getCyear() {
		return cyear;
	}
	/**
	 * @param cyear The cyear to set.
	 */
	public void setCyear(int cyear) {
		this.cyear = cyear;
	}
	/**
	 * @return Returns the disting_pt.
	 */
	public String getDisting_pt() {
		return disting_pt;
	}
	/**
	 * @param disting_pt The disting_pt to set.
	 */
	public void setDisting_pt(String disting_pt) {
		this.disting_pt = disting_pt;
	}
	/**
	 * @return Returns the nid.
	 */
	public String getNid() {
		return nid;
	}
	/**
	 * @param nid The nid to set.
	 */
	public void setNid(String nid) {
		this.nid = nid;
	}
	/**
	 * @return Returns the pageid.
	 */
	public int getPageid() {
		return pageid;
	}
	/**
	 * @param pageid The pageid to set.
	 */
	public void setPageid(int pageid) {
		this.pageid = pageid;
	}
	/**
	 * @return Returns the tabid.
	 */
	public int getTabid() {
		return tabid;
	}
	/**
	 * @param tabid The tabid to set.
	 */
	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	
	/**
	 * @return Returns the queryflag.
	 */
	public String getQueryflag() {
		return queryflag;
	}
	/**
	 * @param queryflag The queryflag to set.
	 */
	public void setQueryflag(String queryflag) {
		this.queryflag = queryflag;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the property.
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @param property The property to set.
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	/**
	 * @return Returns the scope.
	 */
	public String getScope() {
		return scope;
	}
	/**
	 * @param scope The scope to set.
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	/**
	 * @return Returns the cardtype.
	 */
	public String getCardtype() {
		return cardtype;
	}
	/**
	 * @param cardtype The cardtype to set.
	 */
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	/**
	 * @return Returns the userpriv.
	 */
	public String getUserpriv() {
		return userpriv;
	}
	/**
	 * @param userpriv The userpriv to set.
	 */
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}
	/**
	 * @return Returns the havepriv.
	 */
	public String getHavepriv() {
		return havepriv;
	}
	/**
	 * @param havepriv The havepriv to set.
	 */
	public void setHavepriv(String havepriv) {
		this.havepriv = havepriv;
	}
	/**
	 * @return Returns the season.
	 */
	public int getSeason() {
		return season;
	}
	/**
	 * @param season The season to set.
	 */
	public void setSeason(int season) {
		this.season = season;
	}

	
	/**
	 * @return Returns the istype.
	 */
	public String getIstype() {
		return istype;
	}
	/**
	 * @param istype The istype to set.
	 */
	public void setIstype(String istype) {
		this.istype = istype;
	}
	public String getInfokind() {
		return infokind;
	}
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	public String getPlan_id() {
		return plan_id;
	}
	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}
	public String getB0110() {
		
		return b0110;
	}
	public void setB0110(String b0110) 
	{
	  this.b0110 = b0110;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}	
	public String getSub_domain() {
		return sub_domain;
	}
	public void setSub_domain(String sub_domain) {
		this.sub_domain = sub_domain;
	}
	private void setSelectCardno(Connection conn,ArrayList cardnolist)                              //加载选择统计方式的框
	{
		if(cardnolist==null||cardnolist.size()<=0)
			return;
		try{
			if("leaber".equalsIgnoreCase(cardtype))
		    {	
			   pageContext.getOut().println("<td nowrap>");
			   pageContext.getOut().println("<B>单位职数情况</B>&nbsp;&nbsp;");
			   pageContext.getOut().println("</td>");
		    }
		    if(cardnolist.size()>1)
			{				
				JspWriter out=pageContext.getOut();
				//out.println("<table style=\"position:absolute;top:3;left:" +  (new DataEncapsulation().getMaxRightddateSelect(tabid,pageid,disting_pt,conn)-5) + "\">");
				pageContext.getOut().println("<td nowrap>");
				pageContext.getOut().println("<table border=\"0\" cellspacing=\"0\" align=\"left\" cellpadding=\"0\">");
				out.println("<tr>");
                if("SS_SETCARD".equalsIgnoreCase(cardtype))
                {
                	out.println("<td align=\"left\" nowrap>薪酬表&nbsp;</td><td align=\"left\" nowrap><select name=\"tabid\"" + " size=\"1\" onchange=\"javascript:changTabid(this)\">");
                }else
                {
                	out.println("<td align=\"left\" nowrap><select name=\"tabid\"" + " size=\"1\" onchange=\"javascript:changTabid(this)\">");
                }
				  
				  CommonData dataobj=null;
				  String tabid_str="";
				  tabid_str=this.tabid+"";
				  for(int i=0;i<cardnolist.size();i++)
				  {
					 dataobj=(CommonData)cardnolist.get(i);
					 String cardid=dataobj.getDataValue();
					 String cardname=dataobj.getDataName();
					 if(tabid_str.equals(cardid))
					 {
						 out.println("<option  value=\""+cardid+"\" selected=\"selected\">"+cardname+"</option>");
					 }else
					 {
						 out.println("<option  value=\""+cardid+"\">"+cardname+"</option>");
					 }
				  }			 
				  out.println("</select>&nbsp;&nbsp;");   
				  out.println("</td>");
				  out.println("</tr>");
				  out.println("</table>");
				  out.println("</td>");
			}
		}catch(Exception e)
		{e.printStackTrace();}
	}	
	/*****对子集的显示*****/
	public String viewSubclass(RGridView rgrid,Connection conn,UserView userview,int statYear,           //年
			int statMonth,          //月
			int ctimes,             //次数
			String userbase,
			String nId,byte nFlag,int fact_width,int fact_height)
	{
		StringBuffer html=new StringBuffer();
		String sub_domain=rgrid.getSub_domain();
		if(sub_domain==null||sub_domain.length()<=0)
			return "";
		YkcardViewSubclass ykcardViewSubclass=new YkcardViewSubclass(conn,cyyear,cymonth,ctimes,userbase,nid,userview,true);
		ykcardViewSubclass.setFenlei_type(this.fenlei_type);
		ykcardViewSubclass.setFact_width(fact_width);
		ykcardViewSubclass.setFact_height(fact_height);
		ykcardViewSubclass.setUserpriv(userpriv);
        ykcardViewSubclass.setFieldpurv(fieldpurv);
		ykcardViewSubclass.setNFlag(nFlag);
		ykcardViewSubclass.getXmlSubdomain(rgrid.getSub_domain(),rgrid);
		ykcardViewSubclass.setDisplay_zero(this.display_zero);
		ykcardViewSubclass.setBizDate(this.bizDate);
		ykcardViewSubclass.setYkcard_auto("1".equals(ykcard_auto)?true:false);
		ArrayList fieldlist=ykcardViewSubclass.getFieldList();//fieldList 在执行getXmlSubdomain（）此方法时已经将list的指标查出 可以直接使用 changxy
		ykcardViewSubclass.setSearchDateSql(getSearchDatasql(fieldlist));//拼接sql按日期查询
		html.append(ykcardViewSubclass.viewSubClassHtml(infokind,userbase,conn,userview,rgrid,disting_pt,nFlag));
		if(html.length()<=0)
            html.append("<br>");
		return html.toString();
	}
	/***
	 * 子集查询日期条件  由于使用的参数偏多故在此类中添加此方法 需要的指标集合从YkcardViewSubclass中先取出 
	 * changxy 
	 * 20160928
	 */
	public String getSearchDatasql(ArrayList fieldlist){
		boolean flag=false;
		String str=null;
		for (int i = 0; i < fieldlist.size(); i++) {
			if(fieldlist.get(i).toString()!=null&&fieldlist.get(i).toString().length()>2)
			if(fieldlist.get(i).toString()!=null&& "z0".equalsIgnoreCase(fieldlist.get(i).toString().substring(fieldlist.get(i).toString().length()-2, fieldlist.get(i).toString().length())))//指标中有没有日期标识，如果有则按照查询类型拼sql
			{
				flag=true;
				str=fieldlist.get(i).toString();
				break;
			}	
		}
		String sql=null;
		StringBuffer sbf=new StringBuffer();
		if(flag&&str!=null){//不同查询使用的年月字段不一样，
			
			switch (this.queryflagtype) {
			case 1://年月
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.cyear);//月份使用年月
				if(this.cmonth!=13)
					sbf.append("and "+Sql_switcher.month(str)+"="+this.cmonth);
				break;
			case 2://时间段
				sbf.append(" and "+Sql_switcher.dateToChar(str)+">= '"+this.cdatestart+"' and "+Sql_switcher.dateToChar(str)+"<='"+this.cdateend+"'");
				break;
			case 3://季度
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.csyear);//季度使用年份
				switch (this.season) {
				case 1:
					sbf.append(" and "+Sql_switcher.month(str)+">=1 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=3 ");					
					break;
				case 2:
					sbf.append(" and "+Sql_switcher.month(str)+">=4 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=6 ");
					break;
				case 3:
					sbf.append(" and "+Sql_switcher.month(str)+">=7 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=9 ");
					break;
				case 4:
					sbf.append(" and "+Sql_switcher.month(str)+">=10 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=12 ");
					break;
				}
				break;
			case 4://年
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.cyyear);
				break;
			}
		}
		//领导桌面子集按条件查询显示
		if("0".equals(queryflag)||"5".equals(queryflag))//按条件查询不需拼接日期sql 20161011 changxy
			return "";
		return sbf.toString();
	}
   private int getTable_width(Connection conn,String  tabid) {
		
	   int  width=0;
	   ContentDAO dao=new ContentDAO(conn);
	   /*String id=tabid;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select (MAX(ABS(RLeft) + ABS(Rwidth)) + MIN(ABS(RLeft))) AS Expr1 ");
    	sql.append(" from RGrid ");
    	sql.append(" where (Tabid = '"+id+"') and pageid="+this.pageid+"");
    	sql.append(" AND (Rtop =(SELECT MIN(rtop) FROM RGrid WHERE Tabid = '"+id+"' and pageid="+this.pageid+"))");
    	ContentDAO dao=new ContentDAO(conn);
    	int  width=0;
    	try
    	{
    		RowSet rs=dao.search(sql.toString());
    		
    		if(rs.next())
    		{
    			String ws=rs.getString("Expr1");
    			if(ws!=null&&ws.indexOf(".")!=-1)
    			  ws=ws.substring(0,ws.indexOf("."));
    			if(ws==null)
    				ws="0";
    			width=Integer.parseInt(ws);
    		} 
     	}catch(Exception e)       
        {
        	e.printStackTrace();
        }*/
	   String sql="select paperH,paperori,paperW from rname where tabid='"+tabid+"'";
     	try
     	{
     		RowSet rs=dao.search(sql);
     		float w=0;
     		if(rs.next())
     		{
     			String ori=rs.getString("paperori");
     			if(ori==null||ori.length()<=0)
     				ori="1";
     			if("2".equals(ori))
     				w=rs.getFloat("paperH");
     			else
     				w=rs.getFloat("paperW");
     		}
     		w=w*0.0393701f;
     		w=w*96f;
     		width=(int)w;
     	}catch(Exception e)
     	{
     		e.printStackTrace();
     	}
		return width;
	}
    public int getTable_height(Connection conn,String  tabid) {
    	//Sql_switcher
    	String id=tabid;
    	ContentDAO dao=new ContentDAO(conn);
    	int height=0;
    	/*StringBuffer sql=new StringBuffer();
    	sql.append("select (MAX(ABS(RTop) + ABS(RHeight)) + MIN(ABS(RTop))) AS Expr1 ");
    	sql.append(" from RGrid ");
    	sql.append(" where (Tabid = '"+id+"') and pageid="+this.pageid+"");
    	sql.append(" AND (RLeft =(SELECT MIN(RLeft) FROM RGrid WHERE Tabid = '"+id+"' and pageid="+this.pageid+"))");
    	ContentDAO dao=new ContentDAO(conn);
    	int height=0;
    	try
    	{
    		RowSet rs=dao.search(sql.toString());    		
    		if(rs.next())
    		{
    			String ws=rs.getString("Expr1");
    			if(ws!=null&&ws.indexOf(".")!=-1)
    			  ws=ws.substring(0,ws.indexOf("."));
    			if(ws==null)
    				ws="0";
    			height=Integer.parseInt(ws);
    		} 
    		sql=new StringBuffer();
         	sql.append("select (MAX(ABS(RTop) + ABS(RHeight)) + MIN(ABS(RTop))) AS Expr1 from rPage where (Tabid=");
    		sql.append(tabid);    		
    		sql.append(" and pageid="+this.pageid+")");
    		int rp_height=0;
    		rs=dao.search(sql.toString());   
    		if(rs.next())
    		{
    			String ws=rs.getString("Expr1");
    			if(ws!=null&&ws.indexOf(".")!=-1)
    			  ws=ws.substring(0,ws.indexOf("."));
    			if(ws==null)
    				ws="0";
    			rp_height=Integer.parseInt(ws);
    		}
    		if(rp_height>height)
    			height=height+(rp_height-height);    		
     	}catch(Exception e)       
        {
        	e.printStackTrace();
        }*/
    	String sql="select paperH,paperori,paperW from rname where tabid='"+tabid+"'";
     	try
     	{
     		RowSet rs=dao.search(sql);
     		float h=0;
     		if(rs.next())
     		{
     			String ori=rs.getString("paperori");
     			if(ori==null||ori.length()<=0)
     				ori="1";
     			if("1".equals(ori))
     				h=rs.getFloat("paperH");
     			else
     				h=rs.getFloat("paperW");
     		}
     			
     		h=h*0.0393701f;
     		h=h*96f;
     		height=(int)h;
     	}catch(Exception e)
     	{
     		e.printStackTrace();
     	}
		
		return height;
	}
    /**
     * 得到次数
     * @param conn
     * @return
     */
    private int getDegreeCurrent(Connection conn,String a0100)
    {
    	int degree=0;
        String table_name="";
        DbWizard dbw=new DbWizard(conn);
        // 解决潍柴并发时我的薪酬慢问题
        //liuy 2015-4-11 8639： 武汉港务集团 ：我的薪酬中的表格方式中无法从薪资归档表中取次数 begin 
    	table_name="SalaryHistory";
    	degree = getDegree(conn, a0100, table_name);
    	if(degree==0){
    		if (dbw.isExistTable("Salaryarchive", false)){
    			table_name="Salaryarchive";
    			degree = getDegree(conn, a0100, table_name);
    		}
    	}
		if(degree<=0)
			degree=1;
		//liuy 2015-4-11 end
    	return degree;
    }
    
    private int getDegree(Connection conn,String a0100,String table_name){
    	int degree = 0;
    	String sql = "select Max(A00Z1) as a00z1 from " + table_name
				+ " where " + Sql_switcher.year("A00Z0") + "=" + cyear
				+ " and " + Sql_switcher.month("A00Z0") + "=" + cmonth;
		if(a0100!=null&&a0100.length()>0)
			sql=sql+" and a0100='"+a0100+"'";
		ContentDAO dao=new ContentDAO(conn);
		try
		{
			RowSet rs=dao.search(sql);
			if(rs.next())
				degree=rs.getInt("a00z1");
		}catch(Exception e){
			e.printStackTrace();
		}
		return degree;
    }
    
    public ArrayList getDistinctYear(Connection conn,String a0100)
    {
        StringBuffer sql=new StringBuffer();
        DbWizard dbw=new DbWizard(conn);//我的薪酬模块进入按时间显示查询时间时只查最近一条记录 薪酬表其他设置不设置只显示多少年以后记录。默认最近十年
    	ArrayList list=new ArrayList();
    	int year=Calendar.getInstance().get(Calendar.YEAR);    	
    	ContentDAO dao=new ContentDAO(conn);
    	try
    	{
    		RowSet rs=null;//dao.search(sql.toString());
    		if(!(this.nbase!=null&&this.nbase.length()>0))//显示查询日期一般是关联我的薪酬，库前缀关联nbase 其他的不需要显示查询日期
    			return new ArrayList();
    		sql.setLength(0);
	    		sql.append("select "+Sql_switcher.dateToChar("max(a00z0)")+" a00z0 from (");
	          	sql.append("select max(a00z0) as a00z0 from SalaryHistory ");
	          	if(a0100!=null&&a0100.length()>0)
	          	{
	          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+this.nbase.toLowerCase()+"' and sp_flag='06' ");
	          	}
	          	sql.append(" union select max(a00z0) as a00z0 from salaryarchive ");
	          	if(a0100!=null&&a0100.length()>0)
	          	{
	          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+this.nbase.toLowerCase()+"' and sp_flag='06' ");
	          	}
	          	sql.append(")T ");
	          	
    		rs=dao.search(sql.toString());
    		while (rs.next()) {
				this.date=rs.getString("a00z0");
			}
    		if(this.date!=null&&this.date.length()>0){
    			SimpleDateFormat sbf=null;
    			if(this.date.length()<11){
    				sbf=new SimpleDateFormat("yyyy-MM-dd");
    			}else{
    				sbf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			}
    			Date date=sbf.parse(this.date);
    			Calendar cal=Calendar.getInstance();
    			cal.setTime(date);
    			//默认显示最近一条至前十年的记录
    			for (int i = 0; i < 10; i++) {
					list.add((cal.get(Calendar.YEAR)-i)+"");
				}
    		}
    		/**关记录*/
    		if(rs!=null)
    			rs.close();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}    	
    	return list;
    }
	public String getFieldpurv() {
		return fieldpurv;
	}
	public void setFieldpurv(String fieldpurv) {
		this.fieldpurv = fieldpurv;
	}
	/**
	 * 得到个人授权的登记表
	 * @param userView
	 * @param conn
	 * @return
	 */
	private ArrayList getUserViewCardList(UserView userView,Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select tabid,name  from rname where FlagA='A' order by tabid");
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try
		{
			String tab="";
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				tab=rs.getString("tabid");
				if((userView.isHaveResource(IResourceConstant.CARD,tab)))
   	            {
					CommonData dataobj=new CommonData();
					dataobj.setDataName(rs.getString("name"));
					dataobj.setDataValue(tab);
					list.add(dataobj);
   	            }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 页边据
	 * @param conn
	 */
	private void getMargin(Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select Tmargin,bmargin,lmargin,rmargin  from rname where tabid='"+this.tabid+"'");		
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		double tm_f=0;
		double bm_f=0;
		double lm_f=0;
		double rm_f=0;
		try
		{
			
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				tm_f=rs.getDouble("Tmargin");
				bm_f=rs.getDouble("bmargin");
				lm_f=rs.getDouble("lmargin");
				rm_f=rs.getDouble("rmargin");
				tm_f=tm_f/0.24;
				bm_f=bm_f/0.24;
				lm_f=lm_f/0.24;
				rm_f=rm_f/0.24;
				this.tmargin=(int)Math.round(tm_f);
				this.bmargin=(int)Math.round(bm_f);
				this.lmargin=(int)Math.round(lm_f);
				this.rmargin=(int)Math.round(rm_f);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getRe_tabid() {
		return re_tabid;
	}
	public void setRe_tabid(String re_tabid) {
		this.re_tabid = re_tabid;
	}
	private String printTitleImage(Connection conn,int tabid,int pageid)
	{
		int leftn=0;
		int topn=0;
		int widthn=0;
		int heights=0;
		DataEncapsulation encap=new DataEncapsulation();   
		List rpageList=encap.getRpage(tabid,pageid,conn);                            //获得页面title的List的对象
		/**
		 * <image>
           <ext>.jpg</ext>
           <stretch>拉伸True</stretch>
           <transparent>透明False</transparent>
           <proportional>保持比例False</proportional>
           </image>
         */
	     //显示各个单元个要格式化数据的数据的结束
		StringBuffer out=new StringBuffer();
	    try{
	      	//显示页面标头等信息的开始
	          if(rpageList!=null&&!rpageList.isEmpty()){
	             for(int i=0;i<rpageList.size();i++)
	             {
	          	    RPageView rpage=(RPageView)rpageList.get(i);
	          	    if(rpage.getFlag()==6)
	          	    {
	          	    	if("800".equals(disting_pt))
	                    {     
	        		             leftn=(int)Float.parseFloat(rpage.getRleft("1"))+this.lmargin;
	        		             topn=(int)Float.parseFloat(rpage.getRtop("1"))+this.tmargin;
	        		             widthn=(int)Float.parseFloat(rpage.getRwidth("1")) + 1;
	        		             heights=(int)Float.parseFloat(rpage.getRheight("1"))  +1;
	                    }
	                    else
	                    {
	        		             leftn=(int)Float.parseFloat(rpage.getRleft())+this.lmargin;
	        		             topn=(int)Float.parseFloat(rpage.getRtop())+this.tmargin;
	        		             widthn=(int)Float.parseFloat(rpage.getRwidth()) + 1;
	        		             heights=(int)Float.parseFloat(rpage.getRheight()) +1;
	        	        } 
	              		String extendattr=rpage.getExtendAttr();
	        			if(extendattr!=null&&extendattr.length()>0)
	        			{
	        				String ext="";
	        				String stretch="";
	        				String transparent="";
	        				String proportional="";
	        				if(extendattr.indexOf("<format>")!=-1&&extendattr.indexOf("</format>")!=-1)
	        				{
	        					ext=extendattr.substring(extendattr.indexOf("<ext>")+5,extendattr.indexOf("</ext>"));
	        				}
	        				if(extendattr.indexOf("<stretch>")!=-1&&extendattr.indexOf("</stretch>")!=-1)
	        				{
	        					stretch=extendattr.substring(extendattr.indexOf("<stretch>")+9,extendattr.indexOf("</stretch>"));
	        				}
	        				if(extendattr.indexOf("<transparent>")!=-1&&extendattr.indexOf("</transparent>")!=-1)
	        				{
	        					transparent=extendattr.substring(extendattr.indexOf("<transparent>")+13,extendattr.indexOf("</transparent>"));
	        				}
	        				if(extendattr.indexOf("<proportional>")!=-1&&extendattr.indexOf("</proportional>")!=-1)
	        				{
	        					proportional=extendattr.substring(extendattr.indexOf("<proportional>")+14,extendattr.indexOf("</proportional>"));
	        				}
	        				String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
	        				if("zp_noticetemplate_flag".equals(cardtype))
	   	                		url=url + "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filename=";
	   	                	else
	                        url=url + "/servlet/DisplayOleContent?filename=";
	                        String filename=ServletUtilities.createTitlePhotoFile(tabid,pageid,rpage.getGridno(),ext,pageContext.getSession());
	                        if(filename!=null && filename.length()>0){
	                        	filename = SafeCode.encode(PubFunc.encrypt(filename));
	                             String imageurl="file:///"+System.getProperty("java.io.tmpdir")+"\\" + filename;
	                             out.append("<div style=\"position:absolute;top:" + topn + "px;left:" + leftn+"px;");
	                        	 //out.println("<div style=\"position:absolute;top:" + topn + ";left:" + leftn + ";background-image: url("+imageurl+");");
	                        	 //out.println("list-style-image: none;list-style-type: square;");
	                        	 //out.println("<div style=\"position:absolute;top:" + topn + ";left:" + leftn + ";filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+imageurl+"',enabled='true',  sizingMethod='scale');");
	                        	 //out.println("width:expression(document.body.clientWidth>600?'"+widthn+"px':'auto');");
	                        	 //out.println(" filter:alpha(opacity=50);");
	                        	 //out.println("overflow:hidden;");
	                        	 out.append("width:" + widthn + "px;height:" + heights + "px;");
	                        	 out.append("\">");
	                        	 out.append("<img src=\"" + url + filename + "\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + "  >");//style=\"filter:alpha(opacity=50);\"
	                        	 out.append("</div>");
	                             //out.println("<img src=\"" + url + filename + "\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
	                        }
	        			}
	          	    }
	          }
	        }
	     }catch(Exception e)
	     {
	        	e.printStackTrace();
	     }
	     return out.toString();
      	
	}
	public String getBase_url() {
		if(this.base_url==null||this.base_url.length()<=0|| "null".equals(this.base_url))
			this.base_url="";
		return base_url;
	}
	public void setBase_url(String base_url) {
		this.base_url = base_url;
	}
	/**
	 * 上面的页签
	 * @param conn
	 */
	private void setFormPageTopTitle(Connection conn,int pageTitleTop)                                         //显示各个页的函数
	{
		  String url = "this.document." + name + ".submit()";
		  try{
			 int height=getTable_height(conn,tabid+"")+this.tmargin+this.bmargin-22;
		  	 pageContext.getOut().println("<table style=\"position:absolute;z-index:999;top:" + ("1".equalsIgnoreCase(isMobile)?10:pageTitleTop) /*liuy 2014-10-16 在有下拉框时，修改25为30*/ + "px;left:" + (new DataEncapsulation().getSmallestLeft(tabid,pageid,disting_pt,conn)+this.lmargin+this.rmargin) + "px\">");
			 pageContext.getOut().println("<tr>");
	   	     
		  	 List pageData=new DataEncapsulation().getPagecount(tabid,conn);
		     if(!pageData.isEmpty()){
		        for(int i=0;i<pageData.size()&& pageData.size()>1;i++)
		        {
		        	pageContext.getOut().println("<td nowrap>");
		         	DynaBean rec=(DynaBean)pageData.get(i);
		         	pageContext.getOut().println("&nbsp;&nbsp;<a style=\"color:#1B4A98;text-decoration:none;\" href=\"javascript:" + url + "\" onclick=\"changepropertyvalue(" + rec.get("pageid") + ")\">");
	                pageContext.getOut().println(rec.get("title") + "</a>");	
	                pageContext.getOut().println("</td>");
		        }
		      } 
		    
			  pageContext.getOut().println("</tr>");
			  pageContext.getOut().println("</table>"); 
		  	}catch(Exception e)
			{
		  		e.printStackTrace();
		  	} 	    
	}
	
	private void setReturnBack()throws Exception
	{
		if("SS_SETCARD".equalsIgnoreCase(cardtype)&&"1".equals(this.isMobile))
		{
			
				JspWriter out=pageContext.getOut();
				pageContext.getOut().println("<td nowrap>");
				out.println("<table border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"0\">");
				out.println("<tr>");		
				out.println("<td nowrap>");	  
			    out.println("<input id='returnbtn' type='button' value='&nbsp;返回&nbsp;' class='mybuttons' onclick='returnback();'>");
			    out.println("</td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("</td>");
			
		}
	}
	public String getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(String isMobile) {
		this.isMobile = isMobile;
	}
	public String getBizDate() {
		return bizDate;
	}
	public void setBizDate(String bizDate) {
		this.bizDate = bizDate;
	}
	public String getBrowser() {
		return browser;
	}

	/**
     * 
     * @param browser
     * @see com.hjsj.hrms.businessobject.ykcard.DataEncapsulation#analyseBrowser(String)
     */
	public void setBrowser(String browser) {
	    // browser 可能包含版本号，如Safari534.57.2 (内核版本号)
	    if(browser != null && browser.indexOf("Safari") != -1 && browser.length() > "Safari".length()){
	        this.browser = "Safari";
	        String ver = browser.substring(browser.indexOf("Safari")+"Safari".length());
	        if(ver != null && ver.indexOf(".") != -1){
	            try{
	                this.browserMajorVer = Integer.parseInt(ver.substring(0, ver.indexOf(".")));
	            }catch(Exception e){}
	        }
	    }else
	        this.browser = browser;
	}
	
}

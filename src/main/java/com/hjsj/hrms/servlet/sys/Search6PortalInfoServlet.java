package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.actionform.sys.HomeForm;
import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.GzSpFlowBo;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.performance.WarnNoscoreBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportBulletinList;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * 6.0界面调用信息
 * <p>Title:Search6PortalInfoServlet.java</p>
 * <p>Description>:Search6PortalInfoServlet.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 5, 2010 5:12:21 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 * /sys/portalinfo
 */
public class Search6PortalInfoServlet extends HttpServlet {
	private int view_base=6;
	private String scroll_h="230";
	/*private UserView userView;
	private String dbper="Usr";
	private String bosflag="";
	private String ver="";
	private boolean isagent = false;*/
	/* 主页将marquee标签替换掉，使用js实现无缝滚动 xiaoyun 2014-7-29 start */
	/** 更多前面的tr高度 */
	private int trHeight;
	/* 主页将marquee标签替换掉，使用js实现无缝滚动 xiaoyun 2014-7-29 end */
	
	private String liImg="/images/forumme1.gif";  //""+liImg+"";
	private String dliImg="/images/forumme.gif";  //""+dliImg+"";
	
	
	
	private static ThreadLocal threadLocal = new ThreadLocal(); 
	
	/**
	 * Constructor of the object.
	 */
	public Search6PortalInfoServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String flag=(String)request.getParameter("flag");
		flag=flag!=null?flag:"";
		String target=(String)request.getParameter("target");
		target=target!=null?target:"";
		String id=(String)request.getParameter("id");
		id=id!=null?id:"";
		String twinkle="0";
		String scroll="0";	
		Map paramMap = new HashMap();
		threadLocal.set(paramMap);
		
		UserView userView =  (UserView) request.getSession().getAttribute(WebConstant.userView);
		((Map)threadLocal.get()).put("userView", userView);
		//this.userView =  (UserView) request.getSession().getAttribute(WebConstant.userView);	
		//this.bosflag=this.userView.getBosflag();
		//this.isagent = this.userView.isBAgent();
		
		// zxj 20190118 首页加载完毕前注销，会出现userView没有的情况，直接退出即可。
		if(userView == null)
			return;
		
		String bosflag = userView.getBosflag();
		if(StringUtils.isEmpty(bosflag))
			bosflag = "hl";
		
		((Map)threadLocal.get()).put("bosflag", bosflag);
		boolean isagent = userView.isBAgent();
		((Map)threadLocal.get()).put("isagent", Boolean.valueOf(isagent));
		String ver="5";
		if("hl".equalsIgnoreCase(bosflag))
		{
			ver="5";
		}else if("bi".equalsIgnoreCase(bosflag))
		{
			ver="bi";
		}else
			ver="5";
		((Map)threadLocal.get()).put("ver", ver);
		
		if("hcm".equals(bosflag)){
			String curthemes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());
			liImg="/images/hcm/themes/"+curthemes+"/icon/icon6.png";  //""+liImg+"";
			dliImg="/images/hcm/themes/"+curthemes+"/icon/flash_icon6.gif";  //""+dliImg+"";
			view_base=5;
		}else{
			liImg="/images/forumme1.gif";  //""+liImg+"";
			dliImg="/images/forumme.gif";  //""+dliImg+"";
			view_base=6;
		}
		
		HomeForm homeForm=(HomeForm)request.getSession().getAttribute("homeForm");
		
		String dbper = "Usr";
		//zxj 20180304 jazz 35137 明星员工portal也用了homeForm，但用不到dbpre，dbpre会清空，
        //当门户中有明星员工面板时，导致后边常用查询面板不到dbpre，打开相应查询时报A01无效错误。
        if(homeForm == null || StringUtils.isEmpty(homeForm.getDbpre())){
            dbper = getDbPre();
        }else{
            dbper = homeForm.getDbpre();
        }
		((Map)threadLocal.get()).put("dbper", dbper);
		response.setContentType("text/html");
		/*//servlet页面默认是不缓存的
		//本页面允许在浏览器端或缓存服务器中缓存，时限为20秒。
		//20秒之内重新进入该页面的话不会进入该servlet的
		java.util.Date date = new java.util.Date();    
		response.setDateHeader("Last-Modified",date.getTime()); //Last-Modified:页面的最后生成时间 
		response.setDateHeader("Expires",date.getTime()+20000); //Expires:过时期限值 
		response.setHeader("Cache-Control", "public"); //Cache-Control来控制页面的缓存与否,public:浏览器和缓存服务器都可以缓存页面信息；
		response.setHeader("Pragma", "Pragma"); //Pragma:设置页面是否缓存，为Pragma则缓存，no-cache则不缓存
		*/
		response.setCharacterEncoding("GBK");//郭峰修改为GBK。原来是GB2312
//		String realurl = request.getSession().getServletContext().getRealPath("/system/options/customreport/html");
//		   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
//		   {
//		  	  realurl=request.getSession().getServletContext().getResource("/system/options/customreport/html").getPath();//.substring(0);
//		     if(realurl!=null){
//		      if(realurl.indexOf(':')!=-1)
//		  	  {
//				 realurl=realurl.substring(1);   
//		   	  }
//		  	  else
//		   	  {
//				 realurl=realurl.substring(0);      
//		   	  } 
//		      int nlen=realurl.length();
//		  	  StringBuffer buf=new StringBuffer();
//		   	  buf.append(realurl);
//		  	  buf.setLength(nlen-1);
//		   	  realurl=buf.toString();
//		   	  }
//		   }
//
//		realurl = URLEncoder.encode(realurl);
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");//郭峰修改为GBK。原来是GB2312
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");		
//		out.println("  <script type=\"text/javascript\" src=\"../../ext/adapter/ext/ext-base.js\"></script>");
//		out.println("  <script type=\"text/javascrip\" src=\"../../ext/ext-all.js\"></script>");
//		out.println("  <script type=\"text/javascript\" src=\"/js/constant.js\"></script>");
		/*out.println("  <script language=\"javascript\" src=\"/ajax/constant.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/basic.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/common.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/control.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/dataset.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/editor.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/dropdown.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/table.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/menu.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/tree.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/pagepilot.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/command.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/ajax/format.js\"></script>");*/
		//out.println("<script type=\"text/javascript\" src=\"../../ext/adapter/ext/ext-base.js\"></script>");
		//out.println("<script type=\"text/javascript\" src=\"../../ext/ext-all.js\"></script>");
		//out.println("<script type=\"text/javascript\" src=\"../../ext/rpc_command.js\"></script>");
		//防止打开多个页面造成数据混乱 guodd 2015-12-18
		out.println("  <script>window.document.oncontextmenu = function(){return false;};</script>");		
		out.println("  <script language=\"javascript\" src=\"/js/validate.js\"></script>");
		out.println("  <script language=\"javascript\" src=\"/js/constant.js\"></script>");
		out.println("  <SCRIPT LANGUAGE=javascript src=\"/templates/index/Portal2.js\"></SCRIPT>");
		/* 主页将marquee标签替换掉，使用js实现无缝滚动 xiaoyun 2014-7-29 start */
		// 引入无缝滚动js
		out.println("  <script language=\"javascript\" src=\"/templates/index/marquee.js\"></script>");
		out.println(" <script language=\"javascript\" src=\"/jquery/jquery-3.5.1.min.js\"></script>");
		/* 主页将marquee标签替换掉，使用js实现无缝滚动 xiaoyun 2014-7-29 end */
		/* hr桌面去掉定制信息滚动条 xiaoyun 2014-7-26 end */
		out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/css/css1.css\" />");
		out.println("  <link rel=\"stylesheet\" href=\"/css/css6.css\" type=\"text/css\">");
		/* hr桌面去掉定制信息滚动条 xiaoyun 2014-7-26 start */
		out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../../ext/hr6.css\"/>");
		/* hr桌面去掉定制信息滚动条 xiaoyun 2014-7-26 end */
		if("hcm".equals(bosflag)){
			out.println("<style>img{margin:0 5px 0 0;vertical-align:middle}body{font-size:12px; font-family:\"微软雅黑\"; color:#464646;}</style>");
		}
		//主页展示模块，调整文字间距 jingq add 2014.09.28
		out.println("<style>a{line-height:28px;}a:link{font-size:14px;}</style>");
//		out.println("  <script type=\"text/javascript\"> var realurl=\""+realurl+"\"; ");
//		//out.println("   var ViewProperties=new ParameterSet(); ");
//		out.println("  </script>");
//		out.println(" <div id='wait' style='position:absolute;top:20;left:20;display:none;'>");
//				out.println("   <table border=\"1\" width=\"300\" cellspacing=\"0\" cellpadding=\"4\" class=\"table_style\" height=\"87\" align=\"center\">");
//						out.println("   <tr>");
//
//								out.println("  <td class=\"td_style\" height=24><bean:message key=\"classdata.isnow.wiat\"/></td>");
//
//						out.println("  </tr>");
//						out.println("  <tr>");
//								out.println(" <td style=\"font-size:12px;line-height:200%\" align=center>");
//								out.println("  <marquee class=\"marquee_style\" direction=\"right\" width=\"300\" scrollamount=\"5\" scrolldelay=\"10\">");
//								out.println("  <table cellspacing=\"1\" cellpadding=\"0\">");
//								out.println("  <tr height=8>");
//								out.println(" <td bgcolor=#3399FF width=8></td>");
//								out.println("   <td></td>");
//								out.println("  <td bgcolor=#3399FF width=8></td>");
//								out.println(" <td></td>");
//								out.println("  <td bgcolor=#3399FF width=8></td>");
//								out.println(" <td></td>");
//								out.println(" <td bgcolor=#3399FF width=8></td>");
//								out.println(" <td></td>");
//						out.println(" </tr>");
//								out.println(" </table>");
//						out.println(" </marquee>");
//					out.println(" </td>");
//					out.println("  </tr>");
//			out.println(" </table>");
//		out.println(" </div>");
		out.println("  <BODY>");
		out.println("  <input id=\"htmlparam\" type=\"hidden\" name=\"html_param\"/>");
		
		Connection conn=null;
		HashMap paraMap=new HashMap();
		PortalMainBo bo = new PortalMainBo();		
		try {
			conn = AdminDb.getConnection();
			
			paraMap=new PortalTailorXml().ReadOutParameterXml(conn,userView.getUserName(),id);
            scroll=(String)paraMap.get("scroll");
            twinkle=(String)paraMap.get("twinkle");            
            if(scroll!=null&& "1".equals(scroll))
            {
                if(id!=null&&id.length()>0) {
                	this.scroll_h=bo.getpanelHeight(id);
                }
                /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
        		if(this.scroll_h!=null&&this.scroll_h.length()>0){
        			//sh=Integer.parseInt(this.scroll_h)-60;
        			if("hcm".equals(bosflag)) {
        				this.trHeight = Integer.parseInt(this.scroll_h) - 76;
    				} else {
    					this.trHeight = Integer.parseInt(this.scroll_h)-92;
    				}
        		}
        		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
            }   
			
			if("warn".equals(flag))//预警
			{
				//代理人则不显示 JiangHe 
				if(!isagent) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getWarnResult(twinkle,scroll,target,id));
					out.println(getWarnResult(twinkle,scroll,target,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("boardcontent".equals(flag))
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getBoardContent(twinkle,scroll,target,conn, "",id));
					out.println(getBoardContent(twinkle,scroll,target,conn, "",id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("muster".equals(flag))
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getCommonMuster(null,twinkle,scroll,target,conn,id));
					out.println(getCommonMuster(null,twinkle,scroll,target,conn,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("cond".equals(flag))//常用条件
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getCommonCond("1",twinkle,scroll,target,conn,id));
					out.println(getCommonCond("1",twinkle,scroll,target,conn,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("report".equals(flag))	//报表			
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getReport(twinkle,scroll,target,conn,id));
					out.println(getReport(twinkle,scroll,target,conn,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("stat".equals(flag))//常用统计				
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */					
					//out.println(getCommonStat("1",twinkle,scroll,target,conn,id));		
					out.println(getCommonStat("1",twinkle,scroll,target,conn,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("ykcard".equals(flag))				
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getCommonYkcard(twinkle,scroll,target,conn,id));
					out.println(getCommonYkcard(twinkle,scroll,target,conn,id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("matter".equals(flag))				
			{
				if(!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getMatter(twinkle,scroll,target,conn,id));
					if(userView.getVersion()<70) {//60锁显示我的任务面板
						out.println(getMatter(twinkle,scroll,target,conn,id, flag)); //去除我的任务面板 20190419
					}
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}else if("diaocha".equals(flag))
			{
				if(!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getDiaocha(twinkle,scroll,target,conn,id));//热点调查
					out.println(getDiaocha(twinkle,scroll,target,conn,id, flag));//热点调查
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			else if("bgsc".equals(flag))  //表格上传
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getBgsc(twinkle,scroll,target,conn)); //表格上传
					out.println(getBgsc(twinkle,scroll,target,conn, flag)); //表格上传
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			else if("train".equals(flag))
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getTrain(twinkle,scroll,target,conn,id));//培训评估
					out.println(getTrain(twinkle,scroll,target,conn,id, flag));//培训评估
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			else if("elearning".equalsIgnoreCase(flag))
			{
				//代理人则不显示 JiangHe 
				if(!isagent&&!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getElearning(twinkle,scroll,target,conn));
					out.println(getElearning(twinkle,scroll,target,conn, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			else if("ilearningnews".equalsIgnoreCase(flag))
			{        
			    //网络学院门户新闻
				if(!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getBoardContent(twinkle,scroll,target,conn, "11",id));
					out.println(getBoardContent(twinkle,scroll,target,conn, "11",id, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			else if("hotlesson".equalsIgnoreCase(flag))
			{
			    //热们课程
				if(!userView.isBThreeUser()) {
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
					//out.println(getHotLessons(twinkle, scroll, target, conn));
					out.println(getHotLessons(twinkle, scroll, target, conn, flag));
					/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		if(StringUtils.isNotEmpty(scroll) && StringUtils.equals("1", scroll)) {
			out.println("<script language=\"javascript\" type=\"text/javascript\">");
			out.println("$('#"+flag+"').offset({top:5, left:10});");
			/**
			 * Marquee 调用方法
			 * new Marquee({obj, name, mode, speed, autoStart, movePause});
			 * obj：Object滚动对象或者滚动对象id (*必须)
			 * name：String实例名 (*可选，默认随机)
			 * mode：String滚动模式(x=水平, y=垂直) (*可选,默认为x)
			 * speed：Number滚动速度，越小速度越快 (*可选，默认10)
			 * autoStart：Boolean自动开始 (*可选，默认True)
			 * movePause：Boolean鼠标经过是否暂停 (*可选，默认True)
			 */
			/* 解决无缝滚动记录少时滚动不灵的问题 新加属性：系统版本 add by xiaoyun 2014-9-19 start */
			out.println(" new Marquee({obj : '"+flag+"',mode : 'y',name:'"+flag+"',speed:50,bosflag:'"+bosflag+"'});");
			/* 解决无缝滚动记录少时滚动不灵的问题 新加属性：系统版本 add by xiaoyun 2014-9-19 end */
			out.println("</script>");
		}
		/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	/**
	 * 预警信息
	 * @param twinkle
	 * @param scroll
	 * @param target
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 */
	/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getWarnResult(String twinkle,String scroll,String target,String id)
	private String getWarnResult(String twinkle,String scroll,String target,String id, String flag)
	/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		String _target=target;
		if(target!=null&&target.length()>0)
			target=" target='"+target+"'";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr ><td>");
		str.append("		<tr height='"+this.trHeight+"px'><td>");
		/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		Connection conn = null;					
		try
		{
			// 绩效考核预警  JinChunhai 2012.05.21
			UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
			String bosflag = userView.getBosflag();
			String ver = (String)((Map)threadLocal.get()).get("ver");
			ArrayList planList = new ArrayList();
			HashMap roleMap = new HashMap();
			ArrayList roleList = userView.getRolelist();
			if(!userView.isBThreeUser()&&!userView.isSuper_admin() && roleList!=null && roleList.size()>0)
			{
				for (int i = 0; i < roleList.size(); i++) 
				{
					String role = (String) roleList.get(i);					
					roleMap.put(role, "role");					
				}
				conn = AdminDb.getConnection();
				WarnNoscoreBo wbo = new WarnNoscoreBo(conn,userView);
				planList = wbo.getWarnPlanList(roleMap, userView.getA0100());
			}
									
			ScanTotal st = new ScanTotal(userView);
			ArrayList alTotal = null;
			if(!userView.isBThreeUser()){
				alTotal = st.execute();
			}else{
				alTotal=new ArrayList();
			}
			int iRows = (alTotal.size()+planList.size())>this.view_base?this.view_base:(alTotal.size()+planList.size());		
			if("1".equals(scroll))
				iRows=(alTotal.size()+planList.size());
			if(iRows>0)
				content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\">");
			
			int number = 0;
			if(planList.size()>0)
			{
				int planRows = planList.size();
				if(planList.size()>iRows)
					planRows = iRows;					
				for( int i=0; i<planRows; i++){
					CommonData cData = (CommonData)planList.get(i);
					
					String cardApp = "targetAndApp";
					if(cData.getDataName().indexOf("制订")!=-1 && cData.getDataName().indexOf("审批")!=-1)					
						cardApp = "targetAndApp";
					else if(cData.getDataName().indexOf("制订")!=-1)
						cardApp = "target";
					else if(cData.getDataName().indexOf("审批")!=-1)
						cardApp = "app";
										
					content.append("		<tr class=\""+(i%2==0?"":"")+"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					{
						if(cData.getDataValue().indexOf("@")!=-1)
							content.append("		<img src=\""+this.dliImg+"\"> <a href=\"/performance/warnPlan/noScorePersonList.do?b_query=link&encryptParam="+PubFunc.encrypt("plan_id="+ cData.getDataValue().substring(cData.getDataValue().indexOf("@")+1,cData.getDataValue().length())+"&dbpre=&dbPre=")+"\" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");
						else
							content.append("		<img src=\""+this.dliImg+"\"> <a href=\"/performance/warnPlan/noAppCardPersonList.do?b_query=link&encryptParam="+PubFunc.encrypt("plan_id="+ cData.getDataValue().substring(cData.getDataValue().indexOf("$")+1,cData.getDataValue().length())+"&cardApp="+ cardApp +"&dbpre=&dbPre=")+"\" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");
					}else
					{
						if(cData.getDataValue().indexOf("@")!=-1)
							content.append("		<img src=\""+this.liImg+"\"> <a href=\"/performance/warnPlan/noScorePersonList.do?b_query=link&encryptParam="+PubFunc.encrypt("plan_id="+ cData.getDataValue().substring(cData.getDataValue().indexOf("@")+1,cData.getDataValue().length())+"&dbpre=&dbPre=")+"\" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");
						else
							content.append("		<img src=\""+this.liImg+"\"> <a href=\"/performance/warnPlan/noAppCardPersonList.do?b_query=link&encryptParam="+PubFunc.encrypt("plan_id="+ cData.getDataValue().substring(cData.getDataValue().indexOf("$")+1,cData.getDataValue().length())+"&cardApp="+ cardApp +"&dbpre=&dbPre=")+"\" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");
					}
					content.append("		</td></tr>");
					number++;
				}
			}
			
			if(number<this.view_base)
			{
				for( int i=0; i<(iRows-number); i++){
					CommonData cData = (CommonData)alTotal.get(i);
					content.append("		<tr class=\""+(i%2==0?"":"")+"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					    content.append("		<img src=\""+this.dliImg+"\"> <a href=\"/system/warn/result_manager.do?b_query=link&encryptParam="+PubFunc.encrypt("warn_wid="+ cData.getDataValue()+"&dbpre=&dbPre=&returnvalue=")+" \" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");
					else
						content.append("		<img src=\""+this.liImg+"\"> <a href=\"/system/warn/result_manager.do?b_query=link&encryptParam="+PubFunc.encrypt("warn_wid="+ cData.getDataValue()+"&dbpre=&dbPre=&returnvalue=")+" \" "+target+">"+/*(i+1)+". "+*/subText(cData.getDataName(),100)+"</a>");//liuy 2014-8-19 增加参数returnvalue，控制预警提示详情页面的返回
					content.append("		</td></tr>");
				}
			}
			if(iRows>0)
			   content.append("</table>");			
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if((alTotal.size()+planList.size())>this.view_base ){
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link&ver="+ver+"\" "+target+">>>更多(共"+(alTotal.size()+planList.size())+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");		
			if("hcm".equals(bosflag)){
					str.append("<script type=\"text/javascript\">");
					if((alTotal.size()+planList.size())>this.view_base ){
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/system/warn/info_all.do?br_query=link&encryptParam="+PubFunc.encrypt("ver="+ver)+"');");
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+_target+"');");		
					}else{
						str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
					}
					str.append("</script>");	
			}
					
			if(iRows>0)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return str.toString();
	}
	/**
	 * 公告信息
	 * @param twinkle
	 * @param scroll
	 * @param 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 * @throws GeneralException
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getBoardContent(String twinkle,String scroll,String target,Connection conn, String annouceFlag,String portalid)
	private String getBoardContent(String twinkle,String scroll,String target,Connection conn, String annouceFlag,String portalid, String flag)
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		StringBuffer content = new StringBuffer();
		StringBuffer str = new StringBuffer();
		int days=0;
		if(target==null||target.length()<=0)
			target="_self";
		
		RowSet rs=null;
		int i=0;
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		try
		{
			
			String unitcode = this.getUnit(conn);
			String unitcodeWhere = this.getUnitWhere(unitcode,annouceFlag);//得到所属单位的where 条件
			String nowDate = DateStyle.getSystemTime();
            if(nowDate!=null && nowDate.length()>0)
            	nowDate=nowDate.substring(0,10);
            
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			int announce_days = Integer.parseInt((sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS)!=null&&sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS).trim().length()>0?sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS).trim():"3"));
			String a_tempstr = "("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+nowDate+"'"),"approvetime")+")<period";
			String diff = "("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),"approvetime")+")";
			
			StringBuffer sql = new StringBuffer("select id,topic,viewcount,priority,");
			sql.append(diff);
			sql.append(" days ");
			sql.append(",noticeunit");
			sql.append(" from announce ");
			
			if (annouceFlag == null)
			    annouceFlag = "";
			
			if("".equals(annouceFlag))
			{
    			sql.append(" where approve=1");
    			sql.append(" and ");
    			sql.append(a_tempstr);
    		}
			else 
			{
                sql.append(" where flag=" + annouceFlag);
            }
			sql.append(" "+unitcodeWhere);
			sql.append(" order by priority,createtime desc");
			
			ContentDAO dao = new ContentDAO(conn);
			int view = this.view_base;
	    	if("1".equals(scroll))
	    		view=100;
	    	
			str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
			/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//str.append("<tr ><td >");
			str.append("<tr height='"+this.trHeight+"px'><td>");
			/* 将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			content.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			rs = dao.search(sql.toString());
			while (rs.next()) 
			{
				String id=rs.getString("id");
				if("".equals(annouceFlag) || !"11".equals(annouceFlag))
				{
					String noticeunit = rs.getString("noticeunit");
				    if(!(userView.isHaveResource(IResourceConstant.ANNOUNCE,id)||this.isNoticeUnit(noticeunit) || this.isBelongUnit(noticeunit, unitcode)))
    				{
    					continue;
    				}	
				}
				
				if(i<view)
				{
					days=rs.getInt("days");
					String isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
					isEpmLoginFlag = (isEpmLoginFlag==null|| "".equals(isEpmLoginFlag))?"0":isEpmLoginFlag;
					if("1".equals(isEpmLoginFlag))
						content.append("<tr><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo1\">");
					else
						content.append("<tr><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					{
							if(days>announce_days){
								content.append("<img src=\""+this.dliImg+"\">");
								content.append("<a href=\"/selfservice/welcome/welcome.do?b_view=link&encryptParam="+PubFunc.encrypt("a_id="+rs.getString("id")+"&annouceflag="+annouceFlag));
								//content.append(rs.getString("id"));
								//content.append("&annouceflag=");
								//content.append(annouceFlag);
								content.append("\" target=\""+target+"\">");
								content.append(subText(rs.getString("topic"),100));
								content.append("("+(rs.getString("viewcount")==null||rs.getString("viewcount").length()<=0?"0":rs.getString("viewcount"))+"次)</a>");
							}
							else
							{
								String text=rs.getString("topic");
								if(text==null||text.length()<=0)
									text= "";
								if(text.getBytes().length>100)
								    text=PubFunc.splitString(text,100)+"...";
								/*try {
									text = new String(text.getBytes(),"GB2312");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}*/
								content.append("<img src=\""+this.dliImg+"\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&encryptParam="+PubFunc.encrypt("a_id="+ rs.getString("id")+"&annouceflag="+annouceFlag));
                                //content.append("&annouceflag=");
                                //content.append(annouceFlag);
                                content.append("\" target=\""+target+"\">"+/*i+". "+*/text+"("+(rs.getString("viewcount")==null||rs.getString("viewcount").length()<=0?"0":rs.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
							}							
						}
						else
						{
							if(days>announce_days){
								content.append("<img src=\""+this.liImg+"\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&encryptParam="+PubFunc.encrypt("a_id="+ rs.getString("id")+"&annouceflag="+annouceFlag));
                               // content.append("&annouceflag=");
                                //content.append(annouceFlag);
                                content.append("\" target=\""+target+"\">"+subText(rs.getString("topic"),100)+"("+(rs.getString("viewcount")==null||rs.getString("viewcount").length()<=0?"0":rs.getString("viewcount"))+"次)</a>");
						    }
							else
							{
								String text=rs.getString("topic");
								if(text==null||text.length()<=0)
									text= "";
								if(text.getBytes().length>100)
								  text=PubFunc.splitString(text,100)+"...";
								/*try {
									text = new String(text.getBytes(),"GB2312");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}*/
								content.append("<img src=\""+this.liImg+"\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&encryptParam="+PubFunc.encrypt("a_id="+ rs.getString("id")+"&annouceflag="+annouceFlag));
                                //content.append("&annouceflag=");
                                //content.append(annouceFlag);
                                content.append("\" target=\""+target+"\">"+/*i+". "+*/text+"("+(rs.getString("viewcount")==null||rs.getString("viewcount").length()<=0?"0":rs.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
						}							
					}
					content.append("</td></tr>\n");
				}
				i++;
			}
			content.append("</table>");		
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			if(!"hcm".equals(bosflag)){
				if(i>this.view_base ){
					String isEpmLoginFlag="0";
					if(userView.getHm().get("isEpmLoginFlag")!=null)
						isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag");
					if("1".equals(isEpmLoginFlag))
						str.append("<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"javascript:setNavigation('/selfservice/welcome/boardTheMore.do?b_more=link','-1');\" target=\"_parent\">>>更多(共"+(i)+"项)</a></td></tr>");
					else
			    		str.append("<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/selfservice/welcome/boardTheMore.do?b_more=link&encryptParam="+PubFunc.encrypt("flag="+annouceFlag)+"\" target=\""+target+"\">>>更多(共"+(i)+"项)</a></td></tr>");
				}else
				{
					str.append("<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");		
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(i>this.view_base ){
					String isEpmLoginFlag="0";
					if(userView.getHm().get("isEpmLoginFlag")!=null)
						isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag");
					if("1".equals(isEpmLoginFlag))
						str.append("parent.document.getElementById('iframe"+portalid+"').setAttribute('more_href', 'javascript:setNavigation(\'/selfservice/welcome/boardTheMore.do?b_more=link\',\'-1\');');");
					else
						str.append("parent.document.getElementById('iframe"+portalid+"').setAttribute('more_href', '/selfservice/welcome/boardTheMore.do?b_more=link&encryptParam="+PubFunc.encrypt("flag="+annouceFlag)+"');");
					str.append("parent.document.getElementById('iframe"+portalid+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+portalid+"'));");
				}
				str.append("</script>");	
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}			
		}
		if(i>=1)
			return str.toString();
		else
			if("hcm".equals(bosflag)){
				return this.removeMoreBar(portalid);
			}else{
				return "";
			}
		 
	}
	
///////////////////////郭峰增加////////////////////////////////////////
	/**
	 * 获取本单位及上级单位的sql语句条件
	 * @param codeid
	 * @return
	 */
	public String getUnitWhere(String codeid){
		String strWhere = "";
		if(!("".equals(codeid)) && !(codeid == null)){//如果不是超级用户
			strWhere = "('";
			int n = codeid.length();
			for(int i=0;i<n;i++){
				strWhere +=codeid.substring(0,codeid.length()-i)+"','";
			}
			strWhere = strWhere.substring(0, strWhere.length()-2);
			strWhere = " and (unitcode in "+strWhere+") or unitcode is null or unitcode like '"+codeid+"%')";
		}
		return strWhere;
	}
	
	/**
	 * 获取本单位及上级单位的sql语句条件
	 * 或通知单位不为空
	 * @param codeid
	 * @return
	 */
	public String getUnitWhere(String codeid,String flag){
		String strWhere = "";
		if(!("".equals(codeid)) && !(codeid == null)){//如果不是超级用户
			strWhere = "('";
			int n = codeid.length();
			for(int i=0;i<n;i++){
				strWhere +=codeid.substring(0,codeid.length()-i)+"','";
			}
			strWhere = strWhere.substring(0, strWhere.length()-2);
			strWhere = " and (unitcode in "+strWhere+") or unitcode is null or unitcode like '"+codeid+"%'";
			if("".equals(flag)){
				strWhere+=" or (noticeunit is not null";
				if(Sql_switcher.searchDbServer()==1){
					strWhere+=" and "+Sql_switcher.sqlToChar("noticeunit")+"<>''";
				}
				strWhere+=")";
    		}
			strWhere += ")";
		}
		return strWhere;
	}
	/**
	 * 获取单位。
	 * @return
	 */
	public String getUnit(Connection conn){
		String unit = "";
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		if(!userView.isSuper_admin()){//如果不是超级用户
			int userType = userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
			if(userType==4){//如果是自助用户
				unit = userView.getUserOrgId();//得到用户所在单位
			}else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
				unit = getOperUnit(conn);
			}
		}
		return unit;
	}
	/*
	 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
	 * **/
	public String getOperUnit(Connection conn) 
	{
			String unit = "";
			UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
			{
				String[] temp = operOrg.split("`");
				String unitordepart = temp[0];
				if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
					unit = unitordepart.substring(2);
				else//如果是部门
					unit = getUnit(unitordepart.substring(2),conn);
			}
			else if((!userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
			{
				String codePrefix = userView.getManagePrivCode();
				String codeid = userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codePrefix))//如果是单位
					unit = codeid;
				else//如果是部门
					unit = this.getUnit(codeid,conn);
			}
		return unit;		
	}
	

	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid,Connection conn){
		String unit = "";
		try{
			RowSet rs=null;
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				style = rs.getString("codesetid");
				unit = rs.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit,conn);
			
			if(rs!=null)
				rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
///////////////////////郭峰增加////////////////////////////////////////
	
	/**
	 * 花名册
	 * @param type
	 * @param twinkle
	 * @param scroll
	 * @param flag add xiaoyun 2014-7-29  
	 * return
	 * @throws GeneralException
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getCommonMuster(String type,String twinkle,String scroll,String target,Connection conn,String id)throws GeneralException
	private String getCommonMuster(String type,String twinkle,String scroll,String target,Connection conn,String id, String flag)throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        //if(type==null||type.equals(""))
        //	type="3";
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		String ver = (String)((Map)threadLocal.get()).get("ver");
        try
		{

			content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			MusterBo musterbo=new MusterBo(conn);
			ArrayList musterlist = new ArrayList();//人员花名册
			if (userView.isSuper_admin()||userView.hasTheFunction("26031")||userView.hasTheFunction("0309")) {
				musterlist = musterbo.getPrivCommonMusterList("1",userView);
			}
			ArrayList orgMusterlist=new ArrayList();//单位花名册
			if (userView.isSuper_admin()||userView.hasTheFunction("23031")) {
				orgMusterlist = musterbo.getPrivCommonMusterList("2",userView);
			}
            ArrayList posMusterlist=new ArrayList();//岗位花名册
            if (userView.isSuper_admin()||userView.hasTheFunction("25031")) {
            	posMusterlist = musterbo.getPrivCommonMusterList("3",userView);
			}
            ArrayList standPosMusterlist=new ArrayList();//基准岗位花名册
            if (userView.isSuper_admin()||userView.hasTheFunction("25031")) {
            	standPosMusterlist = musterbo.getPrivCommonMusterList("4",userView);
			}
			ArrayList highMusterList=musterbo.getPrivHighMusterList("3",userView);
            ArrayList orgHighMusterList=musterbo.getPrivHighMusterList("21",userView);
            ArrayList posHighMusterList=musterbo.getPrivHighMusterList("41",userView);
			
        	int i=0;
        	int j=0;
        	int k=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	
        	for(i=0;i<highMusterList.size();i++)//高级花名册
        	{
        		RecordVo mustvo=(RecordVo)highMusterList.get(i);
        		if(!(userView.isHaveResource(IResourceConstant.HIGHMUSTER,mustvo.getString("tabid"))))
        			continue;
        		if(k>=view)
        			break;
        		appendHmusterRow(content, mustvo, twinkle, target);
				++k;
        	}
        	for(i=0;i<musterlist.size();i++)//人员花名册
        	{
				RecordVo mustvo=(RecordVo)musterlist.get(i);	  
        		if(k>=view)
        			break;
        		appendCommonMusterRow(content, mustvo,twinkle, "1", "0");
				++k;
        	}
            for(i=0;i<orgHighMusterList.size();i++)
            {
                RecordVo mustvo=(RecordVo)orgHighMusterList.get(i);
                if(!(userView.isHaveResource(IResourceConstant.HIGHMUSTER,mustvo.getString("tabid"))))
                    continue;
                if(k>=view)
                    break;
                appendHmusterRow(content, mustvo, twinkle, target);
                ++k;
            }
            for(i=0;i<orgMusterlist.size();i++)//单位花名册
            {
                RecordVo mustvo=(RecordVo)orgMusterlist.get(i);      
                if(k>=view)
                    break;
                appendCommonMusterRow(content, mustvo,twinkle, "2", "1");
                ++k;
            }
            for(i=0;i<posHighMusterList.size();i++)
            {
                RecordVo mustvo=(RecordVo)posHighMusterList.get(i);
                if(!(userView.isHaveResource(IResourceConstant.HIGHMUSTER,mustvo.getString("tabid"))))
                    continue;
                if(k>=view)
                    break;
                appendHmusterRow(content, mustvo, twinkle, target);
                ++k;
            }
            for(i=0;i<posMusterlist.size();i++)//岗位花名册
            {
                RecordVo mustvo=(RecordVo)posMusterlist.get(i);      
                if(k>=view)
                    break;
                appendCommonMusterRow(content, mustvo,twinkle, "3", "1");
                ++k;
            }
            for(i=0;i<standPosMusterlist.size();i++)//基准岗位花名册
            {
                RecordVo mustvo=(RecordVo)standPosMusterlist.get(i);                        
                if(k>=view)
                    break;
                appendCommonMusterRow(content, mustvo, twinkle,"4", "1");
                ++k;
            }
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
            int total=musterlist.size()+highMusterList.size()+orgMusterlist.size()+orgHighMusterList.size()+
                posMusterlist.size()+posHighMusterList.size();
			if(!"hcm".equals(bosflag)){
				if((total)>this.view_base ){
					str.append("		<tr ><td class=\"RecordRowPo\" style='position:fix;left:300px;top:10px' align=\"right\"><a href=\"/general/muster/emp_muster.do?b_query=link&encryptParam="+PubFunc.encrypt("returnvalue=2&checkflag=2&ver="+ver)+"\" target=\""+target+"\">>>更多(共"+total+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");	
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(total>this.view_base ){
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/general/muster/emp_muster.do?b_query=link&encryptParam="+PubFunc.encrypt("returnvalue=2&checkflag=2&ver="+ver)+"');");
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(k>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	private void appendHmusterRow(StringBuffer content, RecordVo mustvo, String twinkle, String target) {
		String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
        String hzname=mustvo.getString("cname");      
        int j=hzname.indexOf(".");
        hzname=hzname.substring(j+1);
        content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
        if("1".equals(twinkle)) 
            content.append("            <img src=\""+this.dliImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?encryptParam="+PubFunc.encrypt("b_view=link&isGetData=1&returnType=2&modelFlag="+ mustvo.getString("nmodule")+"&res=1&clears=1&operateMethod=direct&tabID="+ mustvo.getString("tabid"))+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
        else 
            content.append("            <img src=\""+this.liImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?encryptParam="+PubFunc.encrypt("b_view=link&isGetData=1&returnType=2&modelFlag="+ mustvo.getString("nmodule")+"&res=1&clears=1&operateMethod=direct&tabID="+ mustvo.getString("tabid"))+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
        
        content.append("        </td></tr>\n");
	}
	
    private void appendMusterRow(StringBuffer content, RecordVo mustvo, String twinkle, String target) {
    	String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
    	String dbper = (String)((Map)threadLocal.get()).get("dbper");
        String hzname=mustvo.getString("hzname");               
        int j=hzname.indexOf(".");
        hzname=hzname.substring(j+1);
        content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
        if("1".equals(twinkle))
        	content.append("            <img src=\""+this.dliImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/muster/open_musterdata.do?b_open=link`encryptParam="+PubFunc.encrypt("checkflag=1&a_inforkind="+mustvo.getString("flag")+"&tabid="+ mustvo.getString("tabid")+"&ver=5&res=1&dbpre="+dbper+"&isGetData=1&isImportData=1")+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>"); //添加 res=1 changxy 20160918 [22772] 中科实业集团（控股）有限公司：在员工自助的主页，选择《员工学历信息名册》和《专业技术人员名册》(这2个花名册为一般花名册)，系统报错
        else
        	content.append("            <img src=\""+this.liImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/muster/open_musterdata.do?b_open=link`encryptParam="+PubFunc.encrypt("checkflag=1&a_inforkind="+mustvo.getString("flag")+"&tabid="+ mustvo.getString("tabid")+"&ver=5&res=1&dbpre="+dbper+"&isGetData=1&isImportData=1")+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
        																																							//changxy【22921】 九宫格内点击打开的花名册返回应该是返回到九宫格页面  checkflag由2改为1 										添加isImportData 进入花名册时判断花名册是否关联常用查询设置标识
        content.append("        </td></tr>\n");
    }
    /**
     * @exception 简单花名册优化
     * @param content
     * @param mustvo
     * @param twinkle
     * @param musterType
     */
    private void appendCommonMusterRow(StringBuffer content, RecordVo mustvo,String twinkle, String musterType,String moduleID) {
    	String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
    	String dbper = (String)((Map)threadLocal.get()).get("dbper");
        String hzname=mustvo.getString("hzname");               
        content.append(" <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
        if("1".equals(twinkle))
        	content.append(" <img src=\""+this.dliImg+"\"> <a href=\"###\" onclick=\"openwinMuster('/module/muster/showmuster/ShowMuster.html?musterType="+musterType+"&moduleID="+moduleID+"&tabid="+mustvo.getString("tabid")+"&source=homepage')\">"+subText(hzname,100)+"</a>");
        else
        	content.append(" <img src=\""+this.liImg+"\"> <a href=\"###\" onclick=\"openwinMuster('/module/muster/showmuster/ShowMuster.html?musterType="+musterType+"&moduleID="+moduleID+"&tabid="+mustvo.getString("tabid")+"&source=homepage')\">"+subText(hzname,100)+"</a>");
        
        content.append(" </td></tr>\n");
    }
	
	/**
	 * 
	 * @param type
	 * @param twinkle
	 * @param scroll
	 * @param target
	 * @param conn
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 * @throws GeneralException
	 */
    /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getCommonCond(String type,String twinkle,String scroll,String target,Connection conn,String id) throws GeneralException
    private String getCommonCond(String type,String twinkle,String scroll,String target,Connection conn,String id, String flag) throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
    	UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		StringBuffer content=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        if(type==null|| "".equals(type))
        	type="1";
        if(target==null||target.length()<=0)
			target="_self";
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append(type);
        strsql.append("' order by norder");
        ContentDAO dao=new ContentDAO(conn);
        StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\" style='padding-top:-50px' cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */		
		RowSet rs=null;
        try
		{
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			rs=dao.search(strsql.toString());
        	int i=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while(rs.next())
        	{
        		if(!(userView.isHaveResource(IResourceConstant.LEXPR,rs.getString("id"))))
        			continue;
				if(i<view)
				{
					content.append("		<tr class=\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					    content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/workbench/query/gquery_interface.do?b_query=link&encryptParam="+PubFunc.encrypt("home=5&type=1&ver=5&curr_id="+ rs.getString("id")+"&dbpre="+dbper)+"\" target=\""+target+"\">"+/*i+". "+*/subText(PubFunc.nullToStr(rs.getString("name")),100)+"</a>");
					else
						content.append("			<img src=\""+this.liImg+"\"> <a href=\"/workbench/query/gquery_interface.do?b_query=link&encryptParam="+PubFunc.encrypt("home=5&type=1&ver=5&curr_id="+ rs.getString("id")+"&dbpre="+dbper)+"\" target=\""+target+"\">"+/*i+". "+*/subText(PubFunc.nullToStr(rs.getString("name")),100)+"</a>");
				
					content.append("		</td></tr>\n");
				}
				++i;
        	}			
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if(i>this.view_base){
					if(userView.getBosflag()!=null&& "hl4".equalsIgnoreCase(userView.getBosflag()))
						str.append("	<tr ><td class=\"RecordRowPo\"  align=\"right\"><a href=\"/workbench/query/query_interface.do?b_gquery=link&encryptParam="+PubFunc.encrypt("home=6&ver="+ver)+"\" target=\""+target+"\">>>更多(共"+i+"项)</a></td></tr>");
					else
						str.append("		<tr ><td class=\"RecordRowPo\"  align=\"right\"><a href=\"/workbench/query/query_interface.do?b_gquery=link&encryptParam="+PubFunc.encrypt("home=6&ver="+ver)+"\" target=\""+target+"\">>>更多(共"+i+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(i>this.view_base){
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/workbench/query/query_interface.do?b_gquery=link&encryptParam="+PubFunc.encrypt("home=6&ver="+ver)+"');");
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(i>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}	
	/**
	 * 取得报表列表
	 * @param type
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 * @throws GeneralException
	 */
    /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
    //private String getReport(String twinkle,String scroll,String target,Connection conn,String id)throws GeneralException
	private String getReport(String twinkle,String scroll,String target,Connection conn,String id, String flag)throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		StringBuffer content=new StringBuffer(); 
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
		{
        	content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			ReportBulletinList reportBulletinList=new ReportBulletinList(conn);
			ArrayList reportList=reportBulletinList.getReportList(userView);
			ArrayList customList=reportBulletinList.getCustomReportList(userView);
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	int size=0;
        	if(reportList!=null)
        	{
	        	for(i=0;i<reportList.size();i++)
	        	{        
					RecordVo temp=(RecordVo)reportList.get(i);	 	        		
	        		if(!(userView.isHaveResource(IResourceConstant.REPORT,temp.getString("tabid"))))
	        			continue; 
	        		
	        		if(size>=view)
	        			break;
	        		
					String hzname=temp.getString("name");
					j=hzname.indexOf(".");
					hzname=hzname.substring(j+1);
					int status=temp.getInt("paper");
					String	ctrollflag="0";
					if(status==-1||status==0||status==2){
						
					}else{
						ctrollflag="1";
					}
					content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\" >");
					//home不为空报表取数时会自动调用公式计算（表内表间等）
					if("1".equals(twinkle))
				    	content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/report/edit_report/reportSettree.do?b_query2=query&encryptParam="+PubFunc.encrypt("operateObject=1&operates=1&print=5&status="+status+"&ctrollflag="+ctrollflag+"&home=5&ver=5&flag=1&code="+ temp.getString("tabid")+"&dbpre="+dbper)+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
					else
						content.append("			<img src=\""+this.liImg+"\"> <a href=\"/report/edit_report/reportSettree.do?b_query2=query&encryptParam="+PubFunc.encrypt("operateObject=1&operates=1&print=5&status="+status+"&ctrollflag="+ctrollflag+"&home=5&ver=5&flag=1&code="+ temp.getString("tabid")+"&dbpre="+dbper)+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");

					content.append("		</td></tr>\n");   
					size++;
	        	}
        	}
        	//zxj 20160613 报表管理不再区分标准版专业版
        	//int priv=userView.getVersion_flag(); // 1:专业版 0:标准版
					if(size>=view)	
					{
						
					}else { //if(priv!=0)
						if(customList!=null&&customList.size()>0){
							for(int a=0;a<customList.size();a++)
				        	{        	
								LazyDynaBean temp=(LazyDynaBean)customList.get(a);	 	        		
				        		if(size>=view)
									break;
				        		String hzname=""+temp.get("name");
								j=hzname.indexOf(".");
								hzname=hzname.substring(j+1);
								//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\"  >");
								content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\" >");
								if("1".equals(twinkle)){
							    	content.append("			<img src=\""+this.dliImg+"\">");
//							    	if("0".equals(temp.get("report_type"))){
//							    		if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext")))
//							    		{
//							    			content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+subText(hzname,100)+"</a>");	
//							    		}
//							    		
//							    	}else if("1".equals(temp.get("report_type"))){
//							    		content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    		
//							    	}else if("2".equals(temp.get("report_type"))){
//							    		content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    	}else if("3".equals(temp.get("report_type"))){
//							    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
//							    			content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    		}
//							    	}
								}
								else{
							    	content.append("			<img src=\""+this.liImg+"\">");
//							    	if("0".equals(temp.get("report_type"))){
//							    		if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext")))
//							    		{
//							    			content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+subText(hzname,100)+"</a>");	
//							    		}
//							    		
//							    	}else if("1".equals(temp.get("report_type"))){
//							    		content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    		
//							    	}else if("2".equals(temp.get("report_type"))){
//							    		content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    	}else if("3".equals(temp.get("report_type"))){
//							    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
//							    			content.append("<a href=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+temp.get("id")+"\" target=\"_blank\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
//							    		}
//							    	}
								}
								if("0".equals(temp.get("report_type"))){
									if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext"))){
										content.append("<a href='javascript:openwin(\"/system/options/customreport/displaycustomreportservlet?ispriv=1`id="+temp.get("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
									}
								}else if("1".equals(temp.get("report_type"))){
						    		content.append("<a href='javascript:openwin(\"/system/options/customreport.do?b_query2=query`operateObject=1`operates=1`code="+temp.get("link_tabid")+"`status=1\")'>"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						    		
						    	}else if("2".equals(temp.get("report_type"))){
						    		content.append("<a href='javascript:openwin(\"/general/card/searchcard.do?b_query2=link`home=2`inforkind="+temp.get("flaga")+"`result=0`tableid="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						    	}else if("3".equals(temp.get("report_type"))){
						    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
						    			content.append("<a href='javascript:openwin(\"/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag="+temp.get("module")+"`a_inforkind="+temp.get("a_inforkind")+"`result=0`isGetData=1`operateMethod=direct`costID="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						    		}
						    	}else if("4".equals(temp.get("report_type"))){//添加简单名册报表显示 guodd 2018-03-29
						    		content.append("<a href='javascript:openwin(\"/components/dataview/dataview.jsp?reportid="+temp.get("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						    	}
								content.append("		</td></tr>\n");
								size++;	
				        	}    
						}
					}
				
	        	content.append("</table>");	
	        	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
				//content=autoScroll(content.toString(),scroll);
	        	content=autoScroll(content.toString(),scroll, flag);
				/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				str.append(content);
				str.append("</td></tr>");
				size=0;
				if(reportList!=null){

		        	for(i=0;i<reportList.size();i++)
		        	{        
						RecordVo temp=(RecordVo)reportList.get(i);	 	        		
		        		if((userView.isHaveResource(IResourceConstant.REPORT,temp.getString("tabid"))))
		        			size++;
		        	}
				}
				if(customList!=null){
					//if(priv==0){
						
					//}else{
						size=size+customList.size();
					//}
				}
				if(!"hcm".equals(bosflag)){	
					if(size>this.view_base ){
						if(userView.getBosflag()!=null&& "hl4".equalsIgnoreCase(userView.getBosflag()))
							str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/report/auto_fill_report/reportlist.do?b_query=link&encryptParam="+PubFunc.encrypt("sortId=-1&home=5&operateObject=1&ver="+ver+"&print=5")+"\" target=\""+target+"\">>>更多(共"+size+"项)</a></td></tr>");
	                    else
	
							str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/report/auto_fill_report/reportlist.do?b_query=link&encryptParam="+PubFunc.encrypt("sortId=-1&home=5&operateObject=1&ver="+ver+"&print=5")+"\" target=\""+target+"\">>>更多(共"+size+"项)</a></td></tr>");
	
					}else
					{
						str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
					}
				}
				str.append("</table>");	
        	
				if("hcm".equals(bosflag)){
					str.append("<script type=\"text/javascript\">");
					if(size>this.view_base ){
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/report/auto_fill_report/reportlist.do?b_query=link&encryptParam="+PubFunc.encrypt("sortId=-1&home=5&operateObject=1&ver="+ver+"&print=5")+"');");
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
					}else{
						str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
					}
					str.append("</script>");	
				}
			
			if(size>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	//获取我的任务
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getMatter(String twinkle,String scroll,String target,Connection conn,String id)throws GeneralException\
	private String getMatter(String twinkle,String scroll,String target,Connection conn,String id, String flag)throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		boolean isagent = userView.isBAgent();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		
		StringBuffer content=new StringBuffer();  
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		
		if("bi".equalsIgnoreCase(bosflag))
			target="i_body";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
		{
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			
			ZpPendingtaskBo zpbo = new ZpPendingtaskBo(conn, userView);
			ArrayList zpdatalist = zpbo.getZpapprDta();
			for(int m = 0; m < zpdatalist.size();m++,j++){
				CommonData zpdata = new CommonData();
				zpdata = (CommonData) zpdatalist.get(m);
				if (zpdata.getDataName() != null && zpdata.getDataName().length() > 0) {
					content.append("<tr class=\"\"><td " + ("hcm".equals(bosflag) ? "style=\"height:32px\"" : "") + " class=\"RecordRowPo\">");
					String url = zpdata.getDataValue();
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					content.append("<img src=\"" + this.liImg + "\"> <a href=\"" + url + "\" target=\"" + target + "\">" + zpdata.getDataName() + "</a>"); 
					content.append("</td></tr>\n");
				}
			}
			
			MatterTaskList matterTaskList=new MatterTaskList(conn,userView);
			
			matterTaskList.setReturnflag("8");
			ArrayList matterList=matterTaskList.getPendingTask();//new ArrayList();		20160513	dengc
			
			KqMatterTask kqMatterTask = new KqMatterTask(conn, userView);
			//考勤刷卡审批
			matterList = kqMatterTask.getKqCardTask(matterList);
			//加班申请审批待办
			matterList = kqMatterTask.getKqOvertimeTask(matterList); 
			//OKR 应李群要求 okr待办挪到前面
			LazyDynaBean abean=new LazyDynaBean();
			CommonData cData=null;
			ArrayList okrList =matterTaskList.getOKRPending(); 
			if(okrList!=null)
	    	{
				int okrCooperationTaskNum = 0;//okr协办任务计数
				CommonData okrCooperationTaskData = new CommonData();
	        	for(i=0;i<okrList.size();i++)
	        	{        
	        		abean=(LazyDynaBean)okrList.get(i);
	        		String name = (String)abean.get("name");
	        		if("部门协作任务申请".equals(name)){//okr协作任务待办合并 chent 20160623
	        			okrCooperationTaskNum += 1;
	        			okrCooperationTaskData.setDataName(name+"("+okrCooperationTaskNum+")");
	        			okrCooperationTaskData.setDataValue((String)abean.get("url"));
	        			continue;
	        		}
	        		cData=new CommonData();
	        		cData.setDataName(name);
	        		cData.setDataValue((String)abean.get("url"));
	        		matterList.add(cData);
	        	}
	        	if(okrCooperationTaskNum > 0){
	        		matterList.add(okrCooperationTaskData);
	        	}
			}
		
			//我的工作纪实
			matterTaskList.setReturnURL("/templates/index/portal.do?b_query=link");
			matterTaskList.setTarget("_self");
			if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){//国家电网，代办只要绩效的
				//代理人则不显示 JiangHe 
				if(!isagent){
					matterList=matterTaskList.getPerformancePending(matterList);  
//					matterList=matterTaskList.getScoreList(matterList);
				}
				this.view_base=4;
			}else{
				//代理人则不显示 JiangHe 
				if(!isagent){
					//只有干警考核才有工作纪实
					if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())){
						matterList = matterTaskList.getWorkPlanList(matterList);
					}
					
				}
	        	matterList=matterTaskList.getWaitTaskList(matterList);
	      //  	matterList=matterTaskList.getInstanceList(matterList);   //已处理的任务 不显示
	        	matterList=matterTaskList.getTmessageList(matterList);  
	        	//代理人则不显示 JiangHe 
				//if(!isagent){ 放开代理人控制，代理人也可以看绩效待办。chent 20170502
					matterList=matterTaskList.getPerformancePending(matterList);   
//					matterList=matterTaskList.getScoreList(matterList);
				//}

				
	        	WorkdiarySelStr WorkdiarySelStr=new WorkdiarySelStr(); 
	        	WorkdiarySelStr.setReturnURL("/templates/index/portal.do?b_query=link");
	        	WorkdiarySelStr.setTarget("_self");
	        	//防止冲掉其他任务
	        	ArrayList listtemp = new ArrayList();
	        	//代理人则不显示 JiangHe 
				if(!isagent)
					listtemp=WorkdiarySelStr.getLogWaittask(conn, userView, matterList);
	        	if(listtemp!=null&&listtemp.size()>0)
	        		matterList =listtemp;
			}
        	int num=0;
        	String isEpmLoginFlag="0";
			if(userView.getHm().get("isEpmLoginFlag")!=null)
				isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag");
        	if(matterList!=null)
        	{
	        	for(i=0;i<matterList.size();i++,j++)
	        	{        	
	        		if(j>=view)
	        			break;
	        		cData=(CommonData)matterList.get(i);
	        		content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
	        		if("1".equals(isEpmLoginFlag)){//国网项目，首页代办点进去后，也要显示菜单，菜单要默认打开相应的模块，所以调用这个javascript:setNavigation，35是模块号（mod_id）
	        			if("1".equals(twinkle)){
	        				String url = cData.getDataValue()+"&dbpre="+dbper+"&home=5&ver=5";
	    					if(url!=null&&url.indexOf("encryptParam")==-1){
	    						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	    						int index = url.indexOf("&");
	    						if(index>-1){
	    							String allurl = url.substring(0,index);
	    							String allparam = url.substring(index);
	    							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	    						}
	    						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	    					}
						    content.append("			<img src=\""+this.dliImg+"\"> <a href=\"javascript:setNavigation('"+ url+"','35');\" target=\""+target+"\">"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
	        			}else{
	        				String url = cData.getDataValue()+"&dbpre="+dbper+"&home=5&ver=5";
	    					if(url!=null&&url.indexOf("encryptParam")==-1){
	    						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	    						int index = url.indexOf("&");
	    						if(index>-1){
	    							String allurl = url.substring(0,index);
	    							String allparam = url.substring(index);
	    							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	    						}
	    						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	    					}
						    content.append("			<img src=\""+this.liImg+"\"> <a href=\"javascript:setNavigation('"+ url+"','35');\" target=\""+target+"\">"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
	        			}
	        		}else{
						if("1".equals(twinkle)){
							String url = cData.getDataValue()+"&dbpre="+dbper+"&home=5&ver=5";
	    					if(url!=null&&url.indexOf("encryptParam")==-1){
	    						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	    						int index = url.indexOf("&");
	    						if(index>-1){
	    							String allurl = url.substring(0,index);
	    							String allparam = url.substring(index);
	    							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	    						}
	    						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	    					}
						    content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
						}else{
							String url =  cData.getDataValue()+"&dbpre="+dbper+"&home=5&ver=5";
	    					if(url!=null&&url.indexOf("encryptParam")==-1){
	    						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	    						int index = url.indexOf("&");
	    						if(index>-1){
	    							String allurl = url.substring(0,index);
	    							String allparam = url.substring(index);
	    							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	    						}
	    						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	    					}
						    content.append("			<img src=\""+this.liImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
						}
					}
					content.append("		</td></tr>\n");   
	        	}
	        	num=matterList.size();
        	}
        	SalaryPkgBo salaryPkgBo=new SalaryPkgBo(conn,userView); 
//			ArrayList salarylist=salaryPkgBo.getEndorseRecords(); //审批薪资
			ArrayList salarylist=salaryPkgBo.getGzPending(); //审批薪资  读取待办表中数据   zhaoxg add 2014-7-25

        	if(salarylist!=null)
        	{
	        	for(i=0;i<salarylist.size();i++,j++)
	        	{        	
	        		if(j>=this.view_base)
	        			break;
	        		abean=(LazyDynaBean)salarylist.get(i);
	        		content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
	        		String url =  abean.get("url")+"&home=5&ver=5&itemid1=default";
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
	        		if("1".equals(twinkle))
					    content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+subText((String)abean.get("name"),100)+"</a>");
					else
					    content.append("			<img src=\""+this.liImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+subText((String)abean.get("name"),100)+"</a>");//itemid1为合宏达需求，主页进入审批走过滤项目  zhaoxg add
					content.append("		</td></tr>\n");   
	        	}
	        	num+=salarylist.size();
        	}
			//------------------------报表审批  zhaoxg 2013-1-28--------------------------------
			Report_isApproveBo report_isApproveBo = new Report_isApproveBo(conn,userView);
			ArrayList approveList = new ArrayList();
			LazyDynaBean approvebean=new LazyDynaBean();
			approveList = report_isApproveBo.getApprovelist(approveList);
		
			if(approveList!=null){
				for(int t=0;t<approveList.size();t++,j++){
	        		if(j>=this.view_base)
	        			break;
					approvebean=(LazyDynaBean)approveList.get(t);
					content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					String url =  approvebean.get("url")+"&home=5&ver=5";
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+approvebean.get("name")+"</a>");
					content.append("		</td></tr>\n"); 
				}
				num+=approveList.size();
			}
			
			
			
			ArrayList returnList = new ArrayList();
			LazyDynaBean returnbean=new LazyDynaBean();
			returnList = report_isApproveBo.getReturnList(returnList);
			if(returnList!=null){
				for(int t=0;t<returnList.size();t++,j++){
	        		if(j>=this.view_base)
	        			break;
	        		returnbean=(LazyDynaBean)returnList.get(t);
					content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					String url =  returnbean.get("url")+"&home=5&ver=5";
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+returnbean.get("name")+"</a>");
					content.append("		</td></tr>\n"); 
				}
				num+=returnList.size();
			}
			//--------------------------------------------------------------------------------
			
			//------------------人员信息审核审批-----------
			ArrayList personChangeList = new PersonMatterTask(conn, userView).getPersonInfoChange();
			if(personChangeList!=null){
				for(int g=0;g<personChangeList.size();g++,j++){
					if(j>=this.view_base)
						break;
					cData=(CommonData)personChangeList.get(g); 
					content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
					String url =  cData.getDataValue()+"&home=5&ver=5&returnflag=portal";
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+ url+"\" target=\""+target+"\">"+cData.getDataName()+"</a>");
					content.append("		</td></tr>\n"); 
				}
				num+=personChangeList.size();
			}
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if(j>=this.view_base )
	        	{
	        		//content.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20\" target=\"_self\">>>更多(共"+matterList.size()+"项)</a></td></tr>");
					if("1".equals(isEpmLoginFlag))
						str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><a href=\"javascript:setNavigation('/general/template/matterList.do?b_query=link&ver="+ver+"','35');\" target=\"_parent\">>>更多(共"+num+"项)</a></td></tr>");
					else
				     	str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/template/matterList.do?b_query=link&ver="+ver+"\" target=\"_parent\">>>更多(共"+num+"项)</a></td></tr>");
	        	}else
				{
	        		str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}		
			}
			str.append("</table>");
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(j>=this.view_base ){
					str.append("parent.document.getElementById('tol"+id+"').style.display='block'; ");
					if("1".equals(isEpmLoginFlag))
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', 'javascript:setNavigation(\'/general/template/matterList.do?b_query=link&ver="+ver+"\',\'35\');');");
					else
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/general/template/matterList.do?b_query=link&ver="+ver+"');");
					
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(j>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	/**
	 * 取得常用登记表
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 * @throws GeneralException
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getCommonYkcard(String twinkle,String scroll,String target,Connection conn,String id)throws GeneralException
	private String getCommonYkcard(String twinkle,String scroll,String target,Connection conn,String id, String flag)throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		boolean isagent = userView.isBAgent();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		RowSet rs=null;
        try
		{
        	
        	StringBuffer sql=new StringBuffer();
        	sql.append("select * from rname where flagA in ('A','B','K') order by flagA, tabid");  // 人员、单位、岗位登记表
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			//List cardlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			ContentDAO dao=new ContentDAO(conn);
			rs = dao.search(sql.toString());
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while( rs.next())
        	{
        		if(!(userView.isHaveResource(IResourceConstant.CARD,rs.getString("tabid"))))
        			continue;        		
        		if(i<view)
        		{
				//LazyDynaBean cardvo=(LazyDynaBean)cardlist.get(i);	  
        		String infokind = "1";
        		if("B".equals(rs.getString("flagA")))
        		    infokind = "2";
        		else if("K".equals(rs.getString("flagA")))
        		    infokind = "4";
				String hzname=rs.getString("name");//(String)cardvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
				if("1".equals(twinkle))
				    content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/module/card/cardCommonSearch.jsp?encryptParam="+PubFunc.encrypt("inforkind="+infokind+"&callbackfunc=home5&tabid="+ rs.getString("tabid")+"&dbpre="+dbper)+"\" target=\""+target+"\" >"+subText(hzname,100)+"</a>");
				else
				    content.append("			<img src=\""+this.liImg+"\"> <a href=\"/module/card/cardCommonSearch.jsp?encryptParam="+PubFunc.encrypt("inforkind="+infokind+"&callbackfunc=home5&&tabid="+ rs.getString("tabid")+"&dbpre="+dbper)+"\" target=\""+target+"\" >"+subText(hzname,100)+"</a>");

				content.append("		</td></tr>\n");   
				}        	
        		i++;
        	}
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if(i>this.view_base ){
					str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/module/card/cardCommonSearch.jsp?encryptParam="+PubFunc.encrypt("inforkind=1&callbackfunc=home5")+"\" target=\""+target+"\">>>更多(共"+i+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(i>this.view_base ){
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', \"/module/card/cardCommonSearch.jsp?encryptParam="+PubFunc.encrypt("inforkind=1&callbackfunc=home5")+"\");");
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(i>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 取得常用统计
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 * @throws GeneralException
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getCommonStat(String type,String twinkle,String scroll,String target,Connection conn,String id)throws GeneralException
	private String getCommonStat(String type,String twinkle,String scroll,String target,Connection conn,String id, String flag)throws GeneralException
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		boolean isagent = userView.isBAgent();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		RowSet rs=null;
        try
		{
        	StringBuffer sql=new StringBuffer();
        
        	sql.append("select * from sname where infokind in ('1', '2', '3') order by snorder");
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			//List statlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			ContentDAO dao=new ContentDAO(conn);
			rs = dao.search(sql.toString());
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while( rs.next())
        	{
        		if(!(userView.isHaveResource(IResourceConstant.STATICS,rs.getString("id"))))
        			continue;            		
        		if(i<view)
        		{	
        		//LazyDynaBean statvo=(LazyDynaBean)statlist.get(i);	  
				String hzname=rs.getString("name");//(String)statvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
				if("1".equals(rs.getString("type")))
					if("1".equals(twinkle))
					    content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/general/static/commonstatic/statshow.do?b_chart=link&encryptParam="+PubFunc.encrypt("querycond=&infokind="+ rs.getString("infokind")+"&isshowstatcond=1&home=1&ver=5&statid="+ rs.getString("id")+"&userbase="+dbper)+"\" target=\""+target+"\" >"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
					else
						 content.append("			<img src=\""+this.liImg+"\"> <a href=\"/general/static/commonstatic/statshow.do?b_chart=link&encryptParam="+PubFunc.encrypt("querycond=&infokind="+ rs.getString("infokind")+"&isshowstatcond=1&home=1&ver=5&statid="+ rs.getString("id")+"&userbase="+dbper)+"\" target=\""+target+"\" >"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
			    else if("2".equals(rs.getString("type")))
					if("1".equals(twinkle))
					   content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/general/static/commonstatic/statshow.do?b_doubledata=data&encryptParam="+PubFunc.encrypt("querycond=&infokind="+ rs.getString("infokind")+"&isshowstatcond=1&home=1&ver=5&statid="+ rs.getString("id")+"&userbase="+dbper)+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");				
					else
						content.append("			<img src=\""+this.liImg+"\"> <a href=\"/general/static/commonstatic/statshow.do?b_doubledata=data&encryptParam="+PubFunc.encrypt("querycond=&infokind="+ rs.getString("infokind")+"&isshowstatcond=1&home=1&&ver=5&statid="+ rs.getString("id")+"&userbase="+dbper)+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");				
			    else if("3".equals(rs.getString("type")))//liuy 2015-1-26 6975：保存多维统计后，主页中出现保存的多维统计，点击后报错，但从左侧菜单中点保存后的多维统计，不报错
			    	if("1".equals(twinkle))
			    		content.append("			<img src=\""+this.dliImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`encryptParam="+PubFunc.encrypt("&infokind="+ rs.getString("infokind")+"&type=0&home=1&statid="+ rs.getString("id"))+"\" target=\""+target+"\">"+subText(hzname,100)+"</a>");				
					else
						content.append("			<img src=\""+this.liImg+"\"> <a href=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`encryptParam="+PubFunc.encrypt("&infokind="+ rs.getString("infokind")+"&type=0&home=1&statid="+ rs.getString("id"))+"\" target=\""+target+"\">"+subText(hzname,100)+"</a>");
				content.append("		</td></tr>\n");  
				}  
        		i++;
        	}
        	content.append("</table>");	
        	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
        	//content=autoScroll(content.toString(),scroll);
        	content=autoScroll(content.toString(),scroll, flag);
        	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        	str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if(i>this.view_base ){
					str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/static/commonstatic/statshow.do?b_inizm=link&encryptParam="+PubFunc.encrypt("infokind=1,2,3&home=5&ver="+ver)+"\" target=\""+target+"\">>>更多(共"+i+"项)</a></td></tr>");//&isshowstatcond=1
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");	
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(i>this.view_base ){
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/general/static/commonstatic/statshow.do?b_inizm=link&encryptParam="+PubFunc.encrypt("infokind=1,2,3&home=5&ver="+ver)+"');");
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(i>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}		
	}
	
	
	/**
	 * 表格上传
	 * @param twinkle
	 * @param scroll
	 * @param target
	 * @param conn
	 * @param flag add by xiaoyun 2014-7-29
	 * @return
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getBgsc(String twinkle,String scroll,String target,Connection conn)
	private String getBgsc(String twinkle,String scroll,String target,Connection conn, String flag)
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		
		if(!userView.hasTheFunction("1109"))
			return "";
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
		{
        		String unitcode = this.getUnit(conn);
        		String unitcodeWhere = this.getUnitWhere(unitcode);//得到所属单位的where 条件
				content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n"); 
			 	ContentDAO dao=new ContentDAO(conn);
	       
	            RowSet rs = dao.search("select contentid,name,description,createdate,status,ext from resource_list where id='1'"+unitcodeWhere+" order by createdate desc");
	            
	        	int k=0;
	        	int view=this.view_base;
	        	if("1".equals(scroll))
	        		view=100;
	        	while(rs.next())
	        	{
	        		 
					String hzname=rs.getString("name");        		
	        		String url="/selfservice/downfile/down?encryptParam="+PubFunc.encrypt("id="+rs.getString("contentid"));    
	        		if(k<view)
	        		{ 
						content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
						if("1".equals(twinkle))
						    content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						else
							content.append("			<img src=\""+this.liImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
						
						content.append("		</td></tr>\n");
	        		}
					++k;
	        	}
	        	
	        	if(rs!=null)
	        		rs.close();
	        	content.append("</table>");
	        	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
				//content=autoScroll(content.toString(),scroll);
	        	content=autoScroll(content.toString(),scroll, flag);
				/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
				str.append(content);
				str.append("</td></tr>");
				if(k>=this.view_base ){
					str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/selfservice/downfile/downfilelist.do?b_query=linkencryptParam="+PubFunc.encrypt("fromPage=front&fileflag=1")+"\" target=\""+target+"\">>>更多(共"+k+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
				str.append("</table>");	
				if(k>=1)
					return str.toString();
				else
					return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}	
		return "";
	}
	
	
	
	
	/**
	 * 培训
	 * @param twinkle
	 * @param scroll
	 * @param target
	 * @param conn
	 * @param flag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getDiaocha(String twinkle,String scroll,String target,Connection conn,String id)
	private String getDiaocha(String twinkle,String scroll,String target,Connection conn,String id, String flag)
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		boolean isagent = userView.isBAgent();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		
		
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
		{

			content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			GzSpFlowBo gsf = new GzSpFlowBo(conn,userView);
			ArrayList list = gsf.getHotInvestigateList("rese");
        	int i=0;
        	int j=0;
        	int k=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	for(i=0;i<list.size();i++)
        	{
        		LazyDynaBean bean = (LazyDynaBean)list.get(i);	  
				String hzname=(String)bean.get("name");        		
        		String url=(String)bean.get("url");    
        		String qn_target = (String)bean.get("target"); 
        		//我的问卷弹窗显示
//        		if(qn_target!=null&&!qn_target.equals("")){
//        			target = qn_target;
//        		}
        		if(k>=view)
        			break;
        		url +="&home=5&ver=5";
        		if(url!=null&&url.indexOf("encryptParam")==-1){
					//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
					int index = url.indexOf("&");
					if(index>-1){
						String allurl = url.substring(0,index);
						String allparam = url.substring(index);
						url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
					}
					//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
				}
        		//zhangh 2020-1-15 【56152 】V77问卷调查：收集配置，将调查问卷分享给内部人员后，内部人员在热点调查中查看时，问卷名称显示不全
				//问卷名称里包含了.字符，按照.分割会导致名称不全
				/*j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);*/
				content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
				if("1".equals(twinkle))
//				    content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
					content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+url+"\" target=\""+qn_target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");// bug 37234 首页热点调查内容项点开以新页面问卷调查 wangb 20180704
				else
//					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+url+"\" target=\""+qn_target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");// bug 37234首页热点调查内容项点开以新页面问卷调查 wangb 20180704
				
				content.append("		</td></tr>\n");
				++k;
        	}
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, flag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			if(!"hcm".equals(bosflag)){
				if(list.size()>this.view_base ){
					str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/selfservice/welcome/hot_topic.do?b_more=more&encryptParam="+PubFunc.encrypt("home=5&ver="+ver+"&discriminateFlag=rese")+" \" target=\""+target+"\">>>更多(共"+list.size()+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			}
			str.append("</table>");	
			if("hcm".equals(bosflag)){
				str.append("<script type=\"text/javascript\">");
				if(list.size()>this.view_base ){
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/selfservice/welcome/hot_topic.do?b_more=more&encryptParam="+PubFunc.encrypt("home=5&ver="+ver+"&discriminateFlag=rese")+"');");
					str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
				}else{
					str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
				}
				str.append("</script>");	
			}
			if(k>=1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}	
		return "";
	}
	/**
	 * 培训评估
	 * @param twinkle
	 * @param scroll
	 * @param target
	 * @param conn
	 * @param contentFlag 内容标识 add by xiaoyun 2014-7-29
	 * @return
	 */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private String getTrain(String twinkle,String scroll,String target,Connection conn,String id)
	private String getTrain(String twinkle,String scroll,String target,Connection conn,String id, String contentFlag)
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		String bosflag = userView.getBosflag();
		boolean isagent = userView.isBAgent();
		String ver = (String)((Map)threadLocal.get()).get("ver");
		String dbper = (String)((Map)threadLocal.get()).get("dbper");
		
		
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		if(target==null||target.length()<=0)
			target="_self";
		str.append("<script type=\"text/javascript\" src=\"../../ext/adapter/ext/ext-base.js\"></script>");
		str.append("<script type=\"text/javascript\" src=\"../../ext/ext-all.js\"></script>");
		str.append("<script type=\"text/javascript\" src=\"../../ext/rpc_command.js\"></script>"); 
		
		str.append("<script>");
		str.append("function learn(courseid,classes,flag) {");	
		str.append("var map = new HashMap();");
		str.append("map.put(\"r5000\",courseid);");
		str.append("Rpc({functionId:'2020030198'},map);");	
		str.append("if(flag == \"1\"){");
		str.append("var url = \"/train/resource/mylessons/learncourse.jsp?opt=me`classes=\"+classes+\"`lesson=\" + courseid;");
		str.append("}else{");
		str.append("var url = \"/train/resource/mylessons/learncourse2.jsp?opt=sss`classes=\"+classes+\"`lesson=\" + courseid;}");
		str.append("var fram = \"/train/resource/mylessons/learniframe.jsp?src=\"+url;");
		str.append("window.open(fram,'learnwindow','left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=yes,toolbar=no');");		
		str.append("}");
		str.append("</script>");
		
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
		{

			content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			GzSpFlowBo gsf = new GzSpFlowBo(conn,userView);
			ArrayList list = gsf.getHotInvestigateList("train");
        	int i=0;
        	int j=0;
        	int k=0;

            int courseCount = 0;
            int examCount = 0;
            int recordCount = 0;
            int hotLessonCount = 0;
        	
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	
        	for(i=0;i<list.size();i++)
        	{
        		LazyDynaBean bean = (LazyDynaBean)list.get(i);	  
				String hzname=(String)bean.get("name");        		
        		String url=(String)bean.get("url");    
        		if(k>=view)
        			break;
        		url +="&home=5&ver=5";
        		if(url!=null&&url.indexOf("encryptParam")==-1){
					//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
					int index = url.indexOf("&");
					if(index>-1){
						String allurl = url.substring(0,index);
						String allparam = url.substring(index);
						url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
					}
					//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
				}
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
				if("1".equals(twinkle))
				    content.append("			<img src=\""+this.dliImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
				else
					content.append("			<img src=\""+this.liImg+"\"> <a href=\""+url+"\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>");
				
				content.append("		</td></tr>\n");
				++k;
        	}
        	//学员在线考试情况
            RowSet examRowSet = getTrainExams(conn);  
                     
            //添加在线考试链接信息
            if (examRowSet!=null)
            {    
                try
                {
                    while(examRowSet.next())
                    {
                        if((k + examCount + courseCount) < view)
                        {                
                            String hzname = "(考试) " + (String)examRowSet.getString("R5401");                                     
                            String url = "/train/resource/myexam.do?b_query=link&encryptParam="+PubFunc.encrypt("type=1&home=5&ver=5");                  
                
                            content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
                            if("1".equals(twinkle))
                                content.append("<img src=\""+this.dliImg+"\"> "); 
                            else
                                content.append("<img src=\""+this.liImg+"\"> ");
                                
                            content.append("<a href=\"" + url + "\" target=\""+target+"\">");
                            content.append(subText(hzname, 100));
                            content.append("</a>");    
                            content.append("        </td></tr>\n");     
                        }
                        
                        examCount = examRowSet.getRow();
                    }            
                    
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }finally //关闭RowSet 
				{
					if(examRowSet!=null)
						try {
							examRowSet.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}			
				}                
            }
             
            //学员未完成培训课程情况
            RowSet courseRowSet = getTrainCourses(conn);  
                     
            //添加培训课程链接信息
            if (courseRowSet!=null)
            {    
                try
                {
                    while(courseRowSet.next())
                    {
                        if((k + examCount + courseCount) < view)
                        {          
                        	String r5000 = courseRowSet.getString("R5000").toString();
                        	String r5004 = "";
                        	String hzname = "";
							String lesson_from = "";

                        	r5004 = courseRowSet.getString("R5004");
                        	r5004 = r5004 == null ? "" : r5004;
                        	lesson_from = courseRowSet.getString("lesson_from").toString();
							if ("0".equalsIgnoreCase(lesson_from)){
								hzname = "(自选) " + (String) courseRowSet.getString("R5003") + "(" + courseRowSet.getString("lprogress") + "%)";
							} else {
								hzname = "(推送) " + (String)courseRowSet.getString("R5003") + "(" + courseRowSet.getString("lprogress") + "%)";
							}
                            
							content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
                            if("1".equals(twinkle))
                                 content.append("<img src=\""+this.dliImg+"\"> "); 
                            else
                                content.append("<img src=\""+this.liImg+"\"> ");
                                    
                            content.append("<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(r5000)) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "','1')\">");
                            content.append(subText(hzname, 100));
                            content.append("</a>");    
                            content.append("        </td></tr>\n");   
                        	
                        }    
                        courseCount = courseRowSet.getRow();                    
                     
                    }                
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }finally
				{
					if(courseRowSet!=null)
						try {
							courseRowSet.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}			
				}
                
            }
            
            RowSet hotLessonRowSet = TrainCourseBo.getHotLesson(conn);
            if(hotLessonRowSet != null){
            	try {
					while(hotLessonRowSet.next()){
						//System.out.println(k+examCount+courseCount+hotLessonCount);
						String r5000 = hotLessonRowSet.getString("r5000").toString();
						String desc = getDescById(conn, r5000);
						if((k + examCount + courseCount + hotLessonCount) < view){
							//System.out.println(desc);
							if(!"".equals(desc) && desc != null){
								String[] s = desc.split(",");
								String text = "";
								String r5004 = "";
								String flag = "";
								if(s.length == 1){
									text = s[0];
								}else if(s.length == 3){
									text = s[0];
									r5004 = s[1];
									flag = s[2];
								}
								
								String hzname = "(热门课程) " + text;
								
								content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
	                            if("1".equals(twinkle))
	                                content.append("<img src=\""+this.dliImg+"\"> "); 
	                            else
	                                content.append("<img src=\""+this.liImg+"\"> ");
	                                
	                            //content.append("<a href=\"" + url + "&home=5&ver=5\" target=\""+target+"\">");
	                            content.append("<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(r5000)) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "','"+flag+"')\">");
	                            content.append(subText(hzname, 100));
	                            content.append("</a>");    
	                            content.append("        </td></tr>\n"); 

							}	
						}
						if(!"".equals(desc) && desc != null){
							//System.out.println("dd:"+desc);
							hotLessonCount++;}
						//hotLessonCount = hotLessonRowSet.getRow();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally
				{
					if(hotLessonRowSet!=null)
						try {
							hotLessonRowSet.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}			
				}
            }
            
			content.append("</table>");	
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//content=autoScroll(content.toString(),scroll);
			content=autoScroll(content.toString(),scroll, contentFlag);
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			str.append(content);
			str.append("</td></tr>");
			
			recordCount = list.size() + examCount + courseCount + hotLessonCount;
			//System.out.println("k:"+k);
			//System.out.println("examCount:"+examCount);
			//System.out.println("courseCount:"+courseCount);
			//System.out.println("hotLessonCount:"+hotLessonCount);
			/*if((k + examCount + courseCount)>this.view_base ){
				//str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/selfservice/welcome/hot_topic.do?b_more=more&home=5&ver="+ver+"&discriminateFlag=train\" target=\""+target+"\">>>更多(共"+list.size()+"项)</a></td></tr>");
			    str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/resource/mylessonsentrance.do?b_query=link&home=5&ver="+ver+"&discriminateFlag=train\" target=\""+target+"\">>>更多(共"+courseCount+"项)</a></td></tr>");
			}else if((k + examCount + courseCount) == this.view_base){
				str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/hierarchy.do?b_query=link\" target=\""+target+"\">>>更多(共"+hotLessonCount+"项)</a></td></tr>");
			}else if((k + examCount + courseCount) < this.view_base && (k + examCount + courseCount + hotLessonCount) > this.view_base){
				//str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/hierarchy.do?b_query=link\" target=\""+target+"\">>>更多(共"+hotLessonCount+"项)</a></td></tr>");
				str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/evaluationdetails.do?b_query=link\" target=\""+target+"\">>>更多(共"+hotLessonCount+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}*/
			 if(!"hcm".equals(bosflag)){
				if( recordCount > view){
					str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/evaluationdetails.do?b_query=link\" target=\""+target+"\">>>更多(共"+recordCount+"项)</a></td></tr>");
				}else{
					
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
			 }
			str.append("</table>");	
			 if("hcm".equals(bosflag)){
					str.append("<script type=\"text/javascript\">");
					if( recordCount > view){
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_href', '/train/evaluationdetails.do?b_query=link');");
						str.append("parent.document.getElementById('iframe"+id+"').setAttribute('more_target', '"+target+"');");		
					}else{
						str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+id+"'));");
					}
					str.append("</script>");	
			}
			if(recordCount >= 1)
				return str.toString();
			else
				if("hcm".equals(bosflag)){
					return this.removeMoreBar(id);
				}else{
					return "";
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}	
		return "";
	}

    /**
     * 热门课程（elearning门户）
     * @param twinkle
     * @param scroll
     * @param target
     * @param conn
     * @param contentFlag 内容标识 add by xiaoyun 2014-7-29
     * @return
     */
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
    //private String getHotLessons(String twinkle,String scroll,String target,Connection conn)
	private String getHotLessons(String twinkle,String scroll,String target,Connection conn, String contentFlag)
    /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
    {
		
		String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
		
        StringBuffer content=new StringBuffer();
        StringBuffer str=new StringBuffer();
        if(target==null||target.length()<=0)
            target="_self";
        str.append("<script type=\"text/javascript\" src=\"../../ext/adapter/ext/ext-base.js\"></script>");
		str.append("<script type=\"text/javascript\" src=\"../../ext/ext-all.js\"></script>");
		str.append("<script type=\"text/javascript\" src=\"../../ext/rpc_command.js\"></script>"); 
		
        str.append("<script>");
		str.append("function learn(courseid,classes,flag) {");	
		str.append("var map = new HashMap();");
		str.append("map.put(\"r5000\",courseid);");
		str.append("Rpc({functionId:'2020030198'},map);");
		str.append("if(flag == \"1\"){");
		str.append("var url = \"/train/resource/mylessons/learncourse.jsp?opt=me`classes=\"+classes+\"`lesson=\" + courseid;");
		str.append("}else {");
		str.append("var url = \"/train/resource/mylessons/learncourse2.jsp?opt=sss`classes=\"+classes+\"`lesson=\" + courseid;");
		str.append("}");
		str.append("var fram = \"/train/resource/mylessons/learniframe.jsp?src=\"+url;");
		str.append("window.open(fram,'learnwindow','left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=yes,toolbar=no');");		
		str.append("}");
		str.append("</script>");
		
        str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
        /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        try
        {
            content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
            int k=0;

            int recordCount = 0;
            int hotLessonCount = 0;
            
            int view=this.view_base;
            if("1".equals(scroll))
                view=100;
            
            RowSet hotLessonRowSet = TrainCourseBo.getHotLesson(conn);
            if(hotLessonRowSet != null){
                try {
                    while(hotLessonRowSet.next()){
                        String r5000 = hotLessonRowSet.getString("r5000").toString();
                        String desc = getDescById(conn, r5000);
                        if((k + hotLessonCount) < view){
                            if(!"".equals(desc) && desc != null){
                                String[] s = desc.split(",");
                                String text = "";
                                String r5004 = "";
                                String flag = "";
                                if(s.length == 1){
                                    text = s[0];
                                }else if(s.length == 3){
                                    text = s[0];
                                    r5004 = s[1];
                                    flag= s[2];
                                }
                                
                                String hzname = "(热门课程) " + text;
                                
                                content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
                                if("1".equals(twinkle))
                                    content.append("<img src=\""+this.dliImg+"\"> "); 
                                else
                                    content.append("<img src=\""+this.liImg+"\"> ");
                                    
                                content.append("<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(r5000)) +"','" + SafeCode.encode(PubFunc.encrypt(r5000)) + "','"+flag+"')\">");
                                content.append(subText(hzname, 100));
                                content.append("</a>");    
                                content.append("        </td></tr>\n"); 

                            }   
                        }
                        if(!"".equals(desc) && desc != null){
                            hotLessonCount++;}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally
                {
                    if(hotLessonRowSet!=null)
                        try {
                            hotLessonRowSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }           
                }
            }
            
            content.append("</table>");
            /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
            //content=autoScroll(content.toString(),scroll);
            content=autoScroll(content.toString(),scroll, contentFlag);
            /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
            str.append(content);
            str.append("</td></tr>");
            
            recordCount = hotLessonCount;
	        if( recordCount > view){
	            str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/evaluationdetails.do?b_query=link&flag=hot\" target=\""+target+"\">>>更多(共"+recordCount+"项)</a></td></tr>");
	        }else{
	            str.append("        <tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
	        }
            str.append("</table>"); 
            if(recordCount >= 1)
                return str.toString();
            else
                return "";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }   
        return "";
    }	
	
	/**
     * 在线学习（课程、考试等提醒）
     * @param twinkle
     * @param scroll
     * @param target
     * @param conn
     * @param flag 内容标识 add by xiaoyun 2014-7-29
     * @return
     */
    /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
    //private String getElearning(String twinkle,String scroll,String target,Connection conn)
    private String getElearning(String twinkle,String scroll,String target,Connection conn, String flag)
    /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
    {
    	
    	String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
    	String ver = (String)((Map)threadLocal.get()).get("ver");
    	
        int courseCount = 0;
        int examCount = 0;
        
        StringBuffer content = new StringBuffer();
        StringBuffer str = new StringBuffer();
        
        if(target==null||target.length()<=0)
            target="_self";     
        
        str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
        /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
		//str.append("		<tr><td >");
		str.append("		<tr height='"+this.trHeight+"px'><td >");
		/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        
        content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");

        int view=this.view_base;
        if("1".equals(scroll))
            view=100;
        
        //学员在线考试情况
        RowSet examRowSet = getTrainExams(conn);  
                 
        //添加在线考试链接信息
        if (examRowSet!=null)
        {    
            try
            {
                while(examRowSet.next())
                {
                    if((examCount + courseCount) < view)
                    {                
                        String hzname = "(考试) " + (String)examRowSet.getString("R5401");                                     
                        String url = "/train/resource/myexam.do?b_query=link";                  
            
                        content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
                        if("1".equals(twinkle))
                            content.append("<img src=\""+this.dliImg+"\"> "); 
                        else
                            content.append("<img src=\""+this.liImg+"\"> ");
                            
                        content.append("<a href=\"" + url + "&home=5&ver=5\" target=\""+target+"\">");
                        content.append(subText(hzname, 100));
                        content.append("</a>");    
                        content.append("        </td></tr>\n");     
                    }
                    
                    examCount = examRowSet.getRow();
                }            
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
         
        //学员未完成培训课程情况
        RowSet courseRowSet = getTrainCourses(conn);  
                 
        //添加培训课程链接信息
        if (courseRowSet!=null)
        {    
            try
            {
                while(courseRowSet.next())
                {
                    if((examCount + courseCount) < view)
                    {                    
                        String hzname = "(课程) " + (String)courseRowSet.getString("R5003") + "(" + courseRowSet.getString("lprogress") + "%)";                                                
                        String url = "/train/resource/mylessonsentrance.do?b_query=link";                  
            
                        content.append("        <tr class=\"\"\"><td "+("hcm".equals(bosflag)?"style=\"height:32px\"":"")+" class=\"RecordRowPo\">");
                        if("1".equals(twinkle))
                            content.append("<img src=\""+this.dliImg+"\"> "); 
                        else
                            content.append("<img src=\""+this.liImg+"\"> ");
                            
                        content.append("<a href=\"" + url + "&home=5&ver=5\" target=\""+target+"\">");                        
                        content.append(subText(hzname, 100));
                        content.append("</a>");    
                        content.append("        </td></tr>\n");   
                    }    
                 
                    courseCount = courseRowSet.getRow();                    
                }                
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
            
        }       
        
        content.append("</table>"); 
        /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */    
        //content = autoScroll(content.toString(),scroll);
        content = autoScroll(content.toString(),scroll, flag);
        /* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
        
        str.append(content);
        str.append("</td></tr>");
        
        int recordCount = courseCount + examCount;
        if(recordCount > this.view_base ){
            str.append("        <tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/train/resource/mylessonsentrance.do?b_query=link&home=5&ver="+ver+"&discriminateFlag=train\" target=\""+target+"\">>>更多(共"+recordCount+"项)</a></td></tr>");
        }else
        {
            str.append("        <tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
        }
        
        str.append("</table>"); 
        
        return str.toString();

    }
    
    private RowSet getTrainCourses(Connection conn)
    {
        RowSet rs = null;
        UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
        //查询未学完的课程
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT L.R5000,R.R5003,R.R5004,L.lprogress,CASE L.LESSON_FROM WHEN 1 THEN 0 WHEN 0 THEN 1 ELSE LESSON_FROM END LESSON_FROM");
        sql.append(" FROM tr_selected_lesson L LEFT JOIN R50 R");
        sql.append(" ON L.R5000=R.R5000");
        sql.append(" WHERE L.nbase='" + userView.getDbname() + "'");
        sql.append(" AND L.A0100='" + userView.getA0100() + "'");
        sql.append(" AND " + Sql_switcher.isnull("L.lprogress", "0") + "<100");
        sql.append(" AND R.R5022='04'");
        sql.append(" ORDER BY LESSON_FROM DESC, ID DESC");
        
        ContentDAO dao=new ContentDAO(conn);
        try
        {
            rs = dao.search(sql.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return rs;
    }
    
    private RowSet getTrainExams(Connection conn)
    {
    	UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
        RowSet rs = null;
        
        //查询学员在线考试(已启动的提前10天开始提醒）
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT R5401");
        sql.append(" FROM R54");
        sql.append(" WHERE r5411='05'");
        sql.append(" AND (R5405-10)<=" + Sql_switcher.sqlNow());
        sql.append(" AND r5400 in (SELECT r5400 FROM R55 B");
        sql.append(" WHERE B.nbase='" + userView.getDbname() + "'");
        sql.append(" AND B.A0100='" + userView.getA0100() + "'");
        sql.append(" AND B.R5513=-1)");
        
        ContentDAO dao=new ContentDAO(conn);
        try
        {
            rs = dao.search(sql.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return rs;
    }
    
   
    
    private String getDescById(Connection conn,String id){
    	String desc = "";
    	RowSet rs = null;
    	UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
    	
    	StringBuffer sql = new StringBuffer();
    	StringBuffer sqls = new StringBuffer();
    	sql.append("select r5003,r5004");
    	sql.append(" from r50");
    	sql.append(" where r5000 = "+id);
    	
    	sqls.append("select r5000");
    	sqls.append(" from tr_selected_lesson");
    	sqls.append(" where r5000 = "+id+" and nbase = '");
    	sqls.append(userView.getDbname());
    	sqls.append("' and a0100 = '");
    	sqls.append(userView.getA0100());
    	sqls.append("'");
    	ContentDAO dao=new ContentDAO(conn);
    	try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				RowSet rss = dao.search(sqls.toString());
				if(rss.next())
					desc = rs.getString("r5003") + "," + rs.getString("r5004")+",1" ;
				else
					desc = rs.getString("r5003") + "," + rs.getString("r5004")+",0" ;	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
    }
	
	private String subText(String text,int sublen)
	{
		if(text==null||text.length()<=0)
			return "";
		/*try {
			text = new String(text.getBytes(),"GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		if(text.getBytes().length>sublen)
			  text=PubFunc.splitString(text,sublen)+"...";
		return text;
	}
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
	//private StringBuffer autoScroll(String content,String bscroll)
	private StringBuffer autoScroll(String content,String bscroll, String flag)
	/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
	{
		String bosflag = (String)((Map)threadLocal.get()).get("bosflag");
		StringBuffer str=new StringBuffer();
		int sh=160;
		if("1".equals(bscroll))
		{
			if(this.scroll_h!=null&&this.scroll_h.length()>0)
			{
				/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
				//sh=Integer.parseInt(this.scroll_h)-60;
				if("hcm".equals(bosflag)) {
					sh = Integer.parseInt(this.scroll_h) - 76;
				} else {
					sh=Integer.parseInt(this.scroll_h)-92;
				}
				/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
			}
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 start */
			//str.append("<marquee scrolldelay=\"350\" height=\""+sh+"\" direction=\"up\" onmouseover='this.stop()' onmouseout='this.start()'>");
			str.append("<div id='"+flag+"' style='width:90%;height: "+sh+"px; overflow: hidden;position:absolute;'>");
			str.append(content);			
			str.append("</div>");
			//str.append("</marquee>");
			/* 首页-将marquee标签改为由js实现的无缝滚动 xiaoyun 2014-7-29 end */
		}
		else
    	{
			str.append("<table width=\"99%\" height=\"90%\">");
			str.append("<tr>");
			/* 首页-内容记录少时显示在中间了 xiaoyun 2014-7-30 start */
			//str.append("<td>");
			str.append("<td valign='top'>");
			/* 首页-内容记录少时显示在中间了 xiaoyun 2014-7-30 end */
			str.append(content);
			str.append("</td>");
			str.append("</tr>");
			str.append("</table>");
    	}
		return str;
	}
	
	/**
     * 功能：检查请求isInteger方法的参数是否为整数
     * @param str String
     * @return 返回boolean类型，false表示不是整数，true表示是整数
     */
	public static boolean isInteger(String str) {
        int begin = 0;
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        str = str.trim();
        if (str.startsWith("+") || str.startsWith("-")) {
            if (str.length() == 1) {
                return false;
            }
            begin = 1;
        }
        for (int i = begin; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }	
	
	/**
	 * 业务用户、自助用户的userView.getUnitIdByBusi("4")包含noticeunit中通知的机构即可浏览此公告信息
	 * @param noticeunit
	 * @return 有为true 没有false
	 */
	private boolean isNoticeUnit(String noticeunit){
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		boolean flag = false;
		if(noticeunit==null||noticeunit.length()==0)
			return flag;
		if(userView.isSuper_admin())
			return true;
		String unitBusi = userView.getUnitIdByBusi("4");
		if(unitBusi.length()>0 && !"UN".equalsIgnoreCase(unitBusi)){//haosl update  !"UN".equalsIgnoreCase(unitBusi) 没有权限范围则不允许看到公告 20170508 
			unitBusi = PubFunc.getTopOrgDept(unitBusi.replaceAll(",", "`"));
			if("UM`".equalsIgnoreCase(unitBusi)){
				unitBusi = "UN"+userView.getUserOrgId()+"`";
			}
			
			noticeunit = PubFunc.getTopOrgDept(noticeunit.replaceAll(",", "`"));
			String[] strS=StringUtils.split(unitBusi,"`");
	 		String[] strD=StringUtils.split(noticeunit,"`");
			for(int i=0,n=strS.length;i<n;i++){
				String busi = strS[i];
				if(busi.length()>0){
					for(int q=0,m=strD.length;q<m;q++){
						String notice = strD[q];
						if(notice.substring(2).startsWith(busi.substring(2))){
							flag = true;
							break;
						}
					}
					if(flag)
						break;
				}
			}
		}
		return flag;
	}
	
	private String removeMoreBar(String portalid){
		StringBuffer str = new StringBuffer();
		str.append("<script type=\"text/javascript\">");
//		str.append("parent.removeElementsByClassName('x-tool-right',parent.viiewportal.findById('"+portalid+"').getEl().dom)");
		str.append("parent.removeElementsByClassName('x-tool-after-title',parent.Ext.getCmp('tol"+portalid+"'));");
		str.append("</script>");
		return str.toString();
	}
	
	/**
	 * 获取权限范围内第一个人员库前缀
	 * @return
	 */
	private String getDbPre()
	{
		ArrayList dblist=null;
		String pre="";		
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		try
		{
			dblist=userView.getPrivDbList();
			if(dblist!=null&&dblist.size()>0){
				pre=(String)dblist.get(0);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return pre;
	}
	/**
	 * 是否归属于通知的机构
	 * @param noticeunit：通知到的机构
	 * @param unitcode：本人归属机构
	 * @return
	 */
	private boolean isBelongUnit(String noticeunit, String unitcode){
		UserView userView = ((UserView)((Map)threadLocal.get()).get("userView"));
		boolean flag = false;
		int userType = userView.getStatus();
		if(userType == 0){//业务用户时，不校验是否归属于通知机构
			return flag;
		}
		if(noticeunit==null||noticeunit.length()==0){
			return flag;
		}
		if(userView.isSuper_admin()){
			return true;
		}
			
		noticeunit = PubFunc.getTopOrgDept(noticeunit.replaceAll(",", "`"));
 		String[] strD=StringUtils.split(noticeunit,"`");
		for(int q=0,m=strD.length;q<m;q++){
			String notice = strD[q];
			if(unitcode.startsWith(notice.substring(2))){//所在机构归小于通知的机构
				flag = true;
				break;
			}
		}
		return flag;
	}

}

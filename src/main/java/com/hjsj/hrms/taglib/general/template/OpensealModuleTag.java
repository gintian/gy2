package com.hjsj.hrms.taglib.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class OpensealModuleTag extends BodyTagSupport {

	private String staticid="";

	public String getStaticid() {
		return staticid;
	}

	public void setStaticid(String staticid) {
		this.staticid = staticid;
	}
	public int doEndTag() throws JspException 
	{
		Connection conn=null;	
		if(staticid==null||staticid.length()<=0)
			return SKIP_BODY;	
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		String bosflag=userview.getBosflag();
		String target="il_body";
		if(bosflag!=null&&("hl".equals(bosflag)|| "hcm".equals(bosflag)))//在6.0以及以后的版本中要把target="mil_body" xcs modify @2014-5-13
			target="mil_body";
		try
		{
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			String url="#";
			pageContext.getOut().println("<table width=\"100%\" border=\"0\" cellspacing=\"0\"   valign='top' class=menu_table style=\"border:none;\" index=\"1\" cellpadding=\"0\">");
			if("37".equals(staticid))//人事异动
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>人事业务</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("37");
				if(list!=null&&list.size()>6)
					pageContext.getOut().println(" <div class=sec_menu_dc style=\"width:155;\"  id=menu1>");
				else
					pageContext.getOut().println(" <div class=sec_menu style=\"width:155;\"  id=menu1>");
				
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\" cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=37");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}				
			}
			else if("38".equals(staticid))//合同管理（劳动合同）
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>合同办理</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("38");
				
		    	if(list!=null&&list.size()>6)
					pageContext.getOut().println(" <div class=sec_menu_dc style=\"width:155;\"  id=menu1>");
				else
					pageContext.getOut().println(" <div class=sec_menu style=\"width:155;\"  id=menu1>");
				
		        
		     //       pageContext.getOut().println(" <div class=sec_menu style=\"width:159;height:0;filter:alpha(Opacity=100);display:none;\"  id=menu1>");
				
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
					String params = PubFunc.encrypt("operationname="+name+"&staticid=38");
					url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}
			else if("60".equals(staticid))//考勤业务办理
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>业务办理</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
		        ArrayList list=new ArrayList();
		        
		        TemplateTableParamBo tp=new TemplateTableParamBo(conn);
		        HashMap kqParamMap=tp.getDefineKqParamInfo(); 
		        if(kqParamMap.get("1")!=null) 
					 list.add(ResourceFactory.getProperty("general.template.overtimeApply"));
		        if(kqParamMap.get("2")!=null)
					 list.add(ResourceFactory.getProperty("general.template.leavetimeApply"));
		        if(kqParamMap.get("3")!=null)
					 list.add(ResourceFactory.getProperty("general.template.officetimeApply"));
		        
		        
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=60");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}
			else if("55".equals(staticid))//资格评审
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>资格评审</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("55");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=55");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}
			
			else if("40".equals(staticid))//出国管理
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>出国办理</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("40");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=40");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}			
			else if("34".equals(staticid))//薪资变动
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>薪资变动</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("34");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=34");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target='"+target+"'>");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target='"+target+"'>");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}
			}
			else if("39".equals(staticid))//保险变动
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>"+ResourceFactory.getProperty("sys.res.ins_bd")+"</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("39");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=39");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target='"+target+"'>");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target='"+target+"'>");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}
			}
			else if("56".equals(staticid))//机构调整
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>机构调整</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("56");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=56");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target='"+target+"'>");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target='"+target+"'>");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}
			}else if("57".equals(staticid))//岗位调整
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>岗位调整</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("57");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=57");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target='"+target+"'>");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target='"+target+"'>");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}
			}
			else if("51".equals(staticid))//警衔管理
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>警衔管理</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("51");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table  id=\"DetailTable\" cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=51");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}	
			else if("52".equals(staticid))//检察官管理
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>检察官</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("52");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table  id=\"DetailTable\" cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=52");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}	
			else if("53".equals(staticid))//法官等级
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>法官等级</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("53");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table  id=\"DetailTable\" cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=53");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}	
			else if("54".equals(staticid))//关衔管理
			{
				pageContext.getOut().println("<tr style=\"cursor:hand;\">");
				pageContext.getOut().println("<td align=\"center\" class=menu_title  id=menuTitle1><span><span id=arrow1></span>关衔管理</span></td>");
		        pageContext.getOut().println("</tr>");	
		        pageContext.getOut().println("<tr>");
		        pageContext.getOut().println("<td>");
		        pageContext.getOut().println(" <div class=sec_menu style=\"width:159;\"  id=menu1>");
				SubsysOperation subsysOperation=new SubsysOperation(conn,userview);
				ArrayList list=subsysOperation.getChackView_tag("54");
				if(list==null||list.size()<=0){
					pageContext.getOut().println("</div>");
					pageContext.getOut().println("</td></tr>");
					pageContext.getOut().println("</table>");
					return SKIP_BODY;
				}
				String name="";
				pageContext.getOut().println("<table id=\"DetailTable\"  cellpadding=\"2\" cellspacing=\"3\" align=\"center\" width=\"100%\"  class=\"DetailTable\" style=\"position:relative;top:10px;\">");
				for(int i=0;i<list.size();i++)
				{
					name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
					name=SafeCode.encode(name);
					url="/general/template/search_module.do?b_query=link&encryptParam=";
                    String params = PubFunc.encrypt("operationname="+name+"&staticid=54");
                    url = url + params;
					int r=Integer.parseInt((i+"").substring((i+"").length()-1));
					pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
					pageContext.getOut().println("<a href=\""+url+"\" target=\""+target+"\">");
					
					//pageContext.getOut().println("<img src=\"/images/"+ getImageName(r)+"\" border=0>");
					pageContext.getOut().println("<img src=\"/images/apply.gif\" border=0>");
					pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
                    pageContext.getOut().println("<tr><td align=\"center\" class=\"loginFont\">");
                    pageContext.getOut().println("<a href=\""+url+"\"  target=\""+target+"\">");
                    pageContext.getOut().println(list.get(i));
                    pageContext.getOut().println("</a>");
					pageContext.getOut().println("</td></tr>");
				}					
			}	
			pageContext.getOut().println("</table>");
			pageContext.getOut().println("</div>");
			pageContext.getOut().println("</td></tr>");
			pageContext.getOut().println("</table>");
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}					
		return SKIP_BODY;	
	}
	private String getImageName(int r)
	{
		String name="";
		switch(r)
	    {
			  case 0:
		      {
		    	  name="browser.gif";
		    	  break;
		   	  }
			  case 1:
		      {
		    	  name="lstatic.gif";
		    	  break;
		   	  }
			  case 2:
		      {
		    	  name="organization.gif";
		    	  break;
		   	  }
			  case 3:
		      {
		    	  name="bx.gif";
		    	  break;
		   	  }
			  case 4:
		      {
		    	  name="hmuster.gif";
		    	  break;
		   	  }
			  case 5:
		      {
		    	  name="ll.gif";
		    	  break;
		   	  }
			  case 6:
		      {
		    	  name="query_set.gif";
		    	  break;
		   	  }
			  case 7:
		      {
		    	  name="apply.gif";
		    	  break;
		   	  }
			  case 8:
		      {
		    	  name="organization.gif";
		    	  break;
		   	  }
			  case 9:
		      {
		    	  name="tool.gif";
		    	  break;
		   	  }
		      default:
		      {
		    	  name="browser.gif";
		    	  break;
		      }
	    }
		return name;
	}
}

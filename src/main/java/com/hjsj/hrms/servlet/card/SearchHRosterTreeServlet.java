package com.hjsj.hrms.servlet.card;

import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

public class SearchHRosterTreeServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException { 
		StringBuffer sbXml = new StringBuffer();
		String flag=req.getParameter("flag"); 
		String flaga=req.getParameter("flaga");
		String moduleflag=req.getParameter("moduleflag");
		moduleflag=moduleflag!=null&&moduleflag.trim().length()>0?moduleflag:"";
		String sortid=req.getParameter("sortid");
		String filterByMdule=req.getParameter("filterByMdule");
		String relatTableid=req.getParameter("relatTableid");
		try {
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			if("2".equals(moduleflag)|| "3".equals(moduleflag)|| "21".equals(moduleflag)|| "41".equals(moduleflag)
					|| "51".equals(moduleflag)|| "1".equals(moduleflag)|| "4".equals(moduleflag)){
				sbXml.append(loadYKCardNodes(flag,flaga,moduleflag,userview,sortid));
			}else if("15".equals(moduleflag)){
				String a_code=req.getParameter("a_code");
				a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
				
				String salarydate=req.getParameter("salarydate");
				salarydate=salarydate!=null&&salarydate.trim().length()>0?salarydate:"";
				filterByMdule=filterByMdule==null?"0":filterByMdule;
				sbXml.append(loadSalaryYKCardNodes(flag,moduleflag,userview,a_code,salarydate,sortid,filterByMdule));
			}else{
				sbXml.append(loadYKCardNodes(flag,flaga,moduleflag,userview,sortid,relatTableid));
			}
		} catch (Exception e) {
			System.out.println(e);
		}			
	    resp.setContentType("text/xml;charset=UTF-8");//"text/xml"

		resp.getWriter().println(sbXml.toString());
	}
	private String encodeXML(Object o){
		String s=o==null?"":o.toString();
		/* & --- &amp;
		< --- &lt;
		> --- &gt;
		'  --- &apos;
		" --- &quot; */
		s=s.replaceAll("&","&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
		return s;
	}
	
	private String loadYKCardNodes(String flag,String flaga,String moduleflag,UserView userview,String id) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule='");
		sqlstr.append(moduleflag);
		//if(!userview.getUserName().equalsIgnoreCase("su")){
		if(!userview.isSuper_admin()){
			sqlstr.append("' and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(") order by sortid, norder");
		}else{
			sqlstr.append("'"); 
		}
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		String tt=getLoadTreeQueryString(flag,moduleflag,id,flaga,temp,userview);
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,flaga,temp,userview)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=rec.get("sortname")!=null?rec.get("sortname").toString():"";
					treeitem.setXml("/general/muster/hmuster/searchHrostertree?flag=2&amp;moduleflag=" + nmodule  + "&amp;flaga=" + flaga+ "&amp;sortid=" + sortid);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName("X"+sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(tabid+"."+cname);
						treeitem.setTarget("nil_body");
						treeitem.setName(tabid);
						if(this.hasFactor(tabid))
						{
							treeitem.setIcon("/images/overview_n_obj.gif");
						}
						else
						{
					     	treeitem.setIcon("/images/overview_obj.gif");
						}
						/* 增加取数进度条 xiaoyun 2014-5-12 start */
						//treeitem.setAction("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&amp;changeDbpre=1&amp;historyRecord=1&amp;isGetData=1&amp;clears=1&amp;operateMethod=direct&amp;tabID="+tabid);
						/* 将取数进度条改为通用的 xiaoyun 2014-8-29 start */
						//treeitem.setAction("/general/muster/hmuster/processBar.jsp?tabID="+tabid);
						String url = "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`" +
								"historyRecord=1`isGetData=1`clears=1`operateMethod=direct`tabID="+tabid;
						url = URLEncoder.encode(url, "GBK");
						treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);
						/*treeitem.setAction("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`" +
								"historyRecord=1`isGetData=1`clears=1`operateMethod=direct`tabID="+tabid);*/
						/* 将取数进度条改为通用的 xiaoyun 2014-8-29 end */
						/* 增加取数进度条 xiaoyun 2014-5-12 end */
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			if("1".equals(flag)){
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(tabid+"."+cname);
						treeitem.setTarget("nil_body");
						treeitem.setName(tabid + moduleflag);
						if(this.hasFactor(tabid))
						{
							treeitem.setIcon("/images/overview_n_obj.gif");
						}
						else
						{
					     	treeitem.setIcon("/images/overview_obj.gif");
						}
						treeitem.setIcon("/images/overview_obj.gif");
						String url = "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord=1`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid;
						url = URLEncoder.encode(url,"GBK");
						treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);
						/*treeitem.setAction("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord=1`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid);*/     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			strXml.append("</TreeNode>\n");
			
		}
		return strXml.toString();
	}
	private String loadYKCardNodes(String flag,String flaga,String moduleflag,UserView userview,
			String id,String relatTableid) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule='");
		sqlstr.append(moduleflag);
		sqlstr.append("' and tabid<>1000 and tabid<>1010 and tabid<>1020");
		if(!userview.isSuper_admin()){
			sqlstr.append(" and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(")");
		}
		if(!"1".equals(moduleflag)&&!"4".equals(moduleflag))
		     sqlstr.append(" and nPrint="+relatTableid);
		sqlstr.append(" order by sortid,norder");
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,relatTableid,temp,userview)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					//String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&","&amp;").getBytes("GBK"),"ISO-8859-1"):"";
					String sortname=encodeXML(rec.get("sortname"));
					treeitem.setXml("/general/muster/hmuster/searchHrostertree?flag=2&amp;moduleflag=" + nmodule  + "&amp;flaga=" + flaga+ "&amp;sortid=" + sortid+"&amp;relatTableid="+relatTableid);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(tabid+"."+cname);
						treeitem.setTarget("nil_body");
						treeitem.setName(tabid + moduleflag);
						if(this.hasFactor(tabid))
						{
							treeitem.setIcon("/images/overview_n_obj.gif");
						}
						else
						{
					     	treeitem.setIcon("/images/overview_obj.gif");
						}
						String url = "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord="+getHistoryRecord(moduleflag)+"`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid;
						url = URLEncoder.encode(url,"GBK");
						treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);
						/*treeitem.setAction("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord="+getHistoryRecord(moduleflag)+"`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid);*/     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			if("1".equals(flag)){
				
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(tabid+"."+cname);
						treeitem.setTarget("nil_body");
						treeitem.setName(tabid + moduleflag);
						if(this.hasFactor(tabid))
						{
							treeitem.setIcon("/images/overview_n_obj.gif");
						}
						else
						{
					     	treeitem.setIcon("/images/overview_obj.gif");
						}
						String url = "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord="+getHistoryRecord(moduleflag)+"`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid;
						url = URLEncoder.encode(url,"GBK");
						treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);
						/*treeitem.setAction("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`changeDbpre=1`historyRecord="+getHistoryRecord(moduleflag)+"`isGetData=1`operateMethod=direct`clears=1`tabID="+tabid);*/     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			strXml.append("</TreeNode>\n");
			
		}
		return strXml.toString();
	}
	private String loadSalaryYKCardNodes(String flag,String moduleflag,UserView userview,
			String a_code,String salarydate,String id,String filterByMdule) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT tabid,cname FROM muster_name where nmodule='");
		sqlstr.append(moduleflag);
		sqlstr.append("' and tabid<>1000 and tabid<>1010 and tabid<>1020 and (sortid is null or sortid='0') order by sortid,norder");
		
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,"1",temp,userview)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=encodeXML(rec.get("sortname"));
					treeitem.setXml("/general/muster/hmuster/searchHrostertree?flag=2&amp;moduleflag=" + nmodule  + "&amp;sortid=" + sortid+"&amp;a_code="+a_code+"&amp;salarydate="+salarydate+"&amp;filterByMdule="+filterByMdule);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
					
					/*
					StringBuffer url= new StringBuffer();
					url.append("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2");
					url.append("`isGetData=1`checksalary=stipend`opt=int`operateMethod=direct`");
					url.append("clears=1`tabID=");
					url.append(tabid+"`a_code="+a_code);
					url.append("`salarydate="+salarydate);
					url.append("`filterByMdule="+filterByMdule);
					url.append("`historyRecord=1");
					url.append("`changeDbpre=1");
					*/
					String url= "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`isGetData=1`checksalary=stipend`opt=int`operateMethod=direct`clears=1`tabID="+tabid+"`a_code="+a_code+"`salarydate="+salarydate+"`filterByMdule="+filterByMdule+"`historyRecord=1`changeDbpre=1";
					url = URLEncoder.encode(url,"GBK");
					url = "/general/muster/hmuster/processBar.jsp?url="+url;
					
					treeitem.setText(tabid+"."+cname);
					treeitem.setTitle(tabid+"."+cname);
					treeitem.setTarget("nil_body");
					treeitem.setName(tabid + moduleflag);
					if(this.hasFactor(tabid))
					{
						treeitem.setIcon("/images/overview_n_obj.gif");
					}
					else
					{
				     	treeitem.setIcon("/images/overview_obj.gif");
					}
					treeitem.setAction(url.toString());     	  
					strXml.append(treeitem.toChildNodeJS() + "\n");    
				}
			}
			if("1".equals(flag)){
				
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=encodeXML(rec.get("cname"));
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
					/*
					StringBuffer url= new StringBuffer();
					url.append("/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_next2=b_next2");
					url.append("`isGetData=1`checksalary=stipend`opt=int`operateMethod=direct`");
					url.append("clears=1`tabID=");
					url.append(tabid+"`a_code="+a_code);
					url.append("`salarydate="+salarydate);
					url.append("`filterByMdule="+filterByMdule);
					url.append("`historyRecord=1");
					url.append("`changeDbpre=1");
					*/
					
					String url= "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2`isGetData=1`checksalary=stipend`opt=int`operateMethod=direct`clears=1`tabID="+tabid+"`a_code="+a_code+"`salarydate="+salarydate+"`filterByMdule="+filterByMdule+"`historyRecord=1`changeDbpre=1";
					url = URLEncoder.encode(url,"GBK");
					url = "/general/muster/hmuster/processBar.jsp?url="+url;
					
					treeitem.setText(tabid+"."+cname);
					treeitem.setTitle(tabid+"."+cname);
					treeitem.setTarget("nil_body");
					treeitem.setName(tabid + moduleflag);
					if(this.hasFactor(tabid))
					{
						treeitem.setIcon("/images/overview_n_obj.gif");
					}
					else
					{
				     	treeitem.setIcon("/images/overview_obj.gif");
					}
					treeitem.setAction(url.toString());     	  
					strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			strXml.append("</TreeNode>\n");
			
		}
		return strXml.toString();
	}
	
	private String getHistoryRecord(String moduleflag) {
		// 考勤自动取数
		return "81".equals(moduleflag)?"0":"1";
	}
	
	/* 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	private String getLoadTreeQueryString(String flag,String moduleflag,String sortid,String flaga,String temp,UserView userview) {
		StringBuffer strsql=new StringBuffer();
		String nprint=flaga;
		String flags="";
		if ("1".equals(flaga)){ // 人员库
			flags = "A";
		} else if ("3".equals(flaga)){ // 职位库
			flags = "K";
		} else if ("2".equals(flaga)){ // 单位库
			flags = "B";
		} else
			flags = "A";

		if("81".equals(moduleflag))  // 考勤
			flags="Q";
		if("5".equals(moduleflag))   // 人事异动
			flags="A";
		if("51".equals(moduleflag))  // 基准岗位
			flags="H";
		if("1".equals(flag)){
			strsql.append("select sortid,sortname,nmodule from Muster_Sort where ");
			strsql.append("nmodule='");
			strsql.append(moduleflag);
			strsql.append("' and sortid in(select SortId from muster_name");   
			if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
				strsql.append(" where tabid in (");   
				strsql.append(temp); 
				strsql.append(")");
			}
			strsql.append(") order by sortid ");
		}else{
			strsql.append("SELECT tabid,cname FROM muster_name where ");
			strsql.append("sortid='");
			strsql.append(sortid);
			strsql.append("' and ");  
			strsql.append("nmodule='");
			strsql.append(moduleflag);
			strsql.append("' and tabid<>1000 and tabid<>1010 and tabid<>1020 and flaga='");   
			strsql.append(flags);
			strsql.append("'"); 
			
			if("Q".equals(flags)|| "5".equals(moduleflag))
				strsql.append(" and nprint='"+nprint+"'");

			if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
				strsql.append(" and tabid in (");   
				strsql.append(temp); 
				strsql.append(")");
			}
		
			
			strsql.append(" order by sortid,norder");
		}
		return strsql.toString();
	}
	public boolean hasFactor(String tabid)
	{
		boolean has=false;
		Connection  con=null;
		try
		{
			con=(Connection)AdminDb.getConnection();
			HmusterXML hmxml = new HmusterXML(con,tabid);
			String factor = hmxml.getValue(HmusterXML.FACTOR);
			if(factor!=null&&factor.trim().length()>0)
			{
				has=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con!=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return has;
	}
}

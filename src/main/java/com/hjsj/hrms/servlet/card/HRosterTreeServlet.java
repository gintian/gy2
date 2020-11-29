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
import java.sql.Connection;
import java.util.List;

public class HRosterTreeServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException { 
		StringBuffer sbXml = new StringBuffer();
		String flag=req.getParameter("flag"); 
//		String flaga=req.getParameter("flaga");
//		String moduleflag=req.getParameter("moduleflag");
//		moduleflag=moduleflag!=null&&moduleflag.trim().length()>0?moduleflag:"";
		String sortid=req.getParameter("sortid");
//		String relatTableid=req.getParameter("relatTableid");
		try {
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
				
			if("1".equals(flag)){//搜集分类
				sbXml.append(loadAllCardNodes(flag, userview,sortid));
			} else {
				sbXml.append(loadYKCardNodes(flag,userview,sortid));
			}
		} catch (Exception e) {
			System.out.println(e);
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
	}
	private String loadYKCardNodes(String flag,UserView userview,String id) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') ");
//		sqlstr.append(moduleflag);
		if(!"su".equalsIgnoreCase(userview.getUserName())){
			sqlstr.append("' and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(") order by nmodule, tabid");
		}
//		else{
//			sqlstr.append("'"); 
//		}
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeString(flag,temp,userview,id)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&",	"&amp;")):"";
					treeitem.setXml("/general/muster/hmuster/searchAllHrostertree?flag=2&amp;sortid=" + sortid);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(cname);
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
						treeitem.setAction("javascript:void(0)");     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			if("1".equals(flag)){
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(cname);
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
						treeitem.setIcon("/images/overview_obj.gif");
						treeitem.setAction("javascript:void(0)");     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			strXml.append("</TreeNode>\n");
			
		}
		return strXml.toString();
	}
	
	/**查询分类*/
	private String loadAllCardNodes(String flag,UserView userview,String sortid) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeString(flag,temp,userview,sortid)); 
		if(!rs.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&",	"&amp;")):"";
					treeitem.setXml("/general/muster/hmuster/searchAllHrostertree?flag=2&amp;sortid=" + sortid);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(cname);
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
						treeitem.setAction("javascript:void(0)");     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			
			
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule in (3,21,41)");
			if(!"su".equalsIgnoreCase(userview.getUserName())){
				sqlstr.append("' and tabid in (");   
				sqlstr.append(temp); 
				sqlstr.append(") order by tabid");
			}
			List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
			if("1".equals(flag)){
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+cname);
						treeitem.setTitle(cname);
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
						treeitem.setIcon("/images/overview_obj.gif");
						treeitem.setAction("javascript:void(0)");     	  
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
		if(!"1".equals(moduleflag)&&!"4".equals(moduleflag))
		     sqlstr.append(" and nPrint="+relatTableid);
		
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,relatTableid,temp,userview)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&",	"&amp;")):"";
					treeitem.setXml("/general/muster/hmuster/searchHrostertree?flag=2&amp;moduleflag=" + nmodule  + "&amp;flaga=" + flaga+ "&amp;sortid=" + sortid+"&amp;relatTableid="+relatTableid);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
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
						treeitem.setAction("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&amp;isGetData=1&amp;operateMethod=direct&amp;clears=1&amp;tabID="+tabid);     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			if("1".equals(flag)){
				
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
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
						treeitem.setAction("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&amp;isGetData=1&amp;operateMethod=direct&amp;clears=1&amp;tabID="+tabid);     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					} 
				}
			}
			strXml.append("</TreeNode>\n");
			
		}
		return strXml.toString();
	}
	private String loadSalaryYKCardNodes(String flag,String moduleflag,UserView userview,
			String a_code,String salarydate,String id) throws Exception{
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(5);
		if(temp.trim().length()==0)
			temp="-1";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT tabid,cname FROM muster_name where nmodule='");
		sqlstr.append(moduleflag);
		sqlstr.append("' and tabid<>1000 and tabid<>1010 and tabid<>1020 and (sortid is null or sortid='0')");
		
		List rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,"1",temp,userview)); 
		if(!rs.isEmpty()||!rs1.isEmpty()){
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag)){
					String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
					String nmodule=rec.get("nmodule")!=null?rec.get("nmodule").toString():"";
					String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&",	"&amp;")):"";
					treeitem.setXml("/general/muster/hmuster/searchHrostertree?flag=2&amp;moduleflag=" + nmodule  + "&amp;sortid=" + sortid+"&amp;a_code="+a_code+"&amp;salarydate="+salarydate);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(sortname);
					treeitem.setTitle(sortname);
					treeitem.setName(sortid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
					
					StringBuffer url= new StringBuffer();
					url.append("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2");
					url.append("&amp;isGetData=1&amp;checksalary=stipend&amp;opt=int&amp;operateMethod=direct&amp;");
					url.append("clears=1&amp;tabID=");
					url.append(tabid+"&amp;a_code="+a_code);
					url.append("&amp;salarydate="+salarydate);
					
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
					String cname=rec.get("cname")!=null?new String(rec.get("cname").toString().replaceAll("&",	"&amp;")):"";
//					if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
					StringBuffer url= new StringBuffer();
					url.append("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2");
					url.append("&amp;isGetData=1&amp;checksalary=stipend&amp;opt=int&amp;operateMethod=direct&amp;");
					url.append("clears=1&amp;tabID=");
					url.append(tabid+"&amp;a_code="+a_code);
					url.append("&amp;salarydate="+salarydate);
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

		if("81".equals(moduleflag))   //考勤
			flags="Q";
		if("5".equals(moduleflag))   //人事异动
			flags="A";
		if("1".equals(flag)){
			strsql.append("select sortid,sortname,nmodule from Muster_Sort where ");
			strsql.append("nmodule='");
			strsql.append(moduleflag);
			strsql.append("' and sortid in(select SortId from muster_name");   
			if(!"5".equals(moduleflag)){
				if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
					strsql.append(" where tabid in (");   
					strsql.append(temp); 
					strsql.append(")");
				}
			}
			strsql.append(")");
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
			if(!"5".equals(moduleflag)){
				if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
					strsql.append(" and tabid in (");   
					strsql.append(temp); 
					strsql.append(")");
				}
			}
			
			strsql.append(" order by nmodule, tabid");
		}
		return strsql.toString();
	}
	
	private String getLoadTreeString(String flag, String temp,UserView userview,String sortid) {
		StringBuffer strsql=new StringBuffer();

		if("1".equals(flag)){
			strsql.append("select sortid,sortname,nmodule from Muster_Sort where ");
			strsql.append("sortid in(select SortId from muster_name where nmodule in (3,21,41)");   

			if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
				strsql.append(" where tabid in (");   
				strsql.append(temp); 
				strsql.append(")");
			}
			strsql.append(")");
		}else{
			strsql.append("SELECT tabid,cname FROM muster_name where ");
			strsql.append("sortid='");
			strsql.append(sortid);
			strsql.append("' and ");  
			strsql.append(" tabid<>1000 and tabid<>1010 and tabid<>1020 ");   
				if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
					strsql.append(" and tabid in (");   
					strsql.append(temp); 
					strsql.append(")");
				}
			
			strsql.append(" order by tabid");
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

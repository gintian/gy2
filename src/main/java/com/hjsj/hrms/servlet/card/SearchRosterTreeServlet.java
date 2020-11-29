package com.hjsj.hrms.servlet.card;

import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SearchRosterTreeServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException { 
		StringBuffer sbXml = new StringBuffer();
		String flag=req.getParameter("flag"); 
		String flaga=req.getParameter("flaga");
		String moduleflag=req.getParameter("moduleflag");
		try {
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			sbXml.append(loadYKCardNodes(flag,flaga,moduleflag,userview));
		} catch (Exception e) {
			e.printStackTrace();
		}			
		resp.setContentType("text/xml;charset=gb2312");
		resp.getWriter().println(sbXml.toString());
	}
	private String loadYKCardNodes(String flag,String flaga,String moduleflag,
			UserView userview) throws Exception{
		Connection conn=null;
		conn=AdminDb.getConnection();
		StringBuffer strXml=new StringBuffer();
		String temp=userview.getResourceString(4);
		if(temp.trim().length()==0)
			temp="-1";
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,flaga,moduleflag,temp,userview)); 
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT tabid,hzname,moduleflag from lname where flag='");
		strsql.append(flaga);
		strsql.append("'"); 
		if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
			
			strsql.append(" and tabid in (");   
			strsql.append(temp); 
			strsql.append(")");
		}
		strsql.append(" order by norder");
		List rs1=ExecuteSQL.executeMyQuery(strsql.toString());
		if(!rs.isEmpty()||!rs1.isEmpty())
		{
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++)
			{
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				if("1".equals(flag))//分类
				{
					String styleid=rec.get("styleid")!=null?rec.get("styleid").toString():"";
					String styledesc=rec.get("styledesc")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("styledesc").toString()):"";
					treeitem.setXml("/general/muster/hmuster/searchrostertree?flag=2&amp;moduleflag=" + styleid  + "&amp;flaga=" + flaga);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("javascript:void(0)");	
					treeitem.setText(styledesc);
					treeitem.setTitle(styledesc);
					treeitem.setName("X"+styleid);
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				/**花名册表*/
				}else{
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String title=rec.get("hzname")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("hzname").toString()):"";
					String module=rec.get("moduleflag")!=null?rec.get("moduleflag").toString():"";
					if(!module.substring(1,3).equals(moduleflag))
						continue;
				//	if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.getUserName().equalsIgnoreCase("su"))
//					{
						treeitem.setText(tabid+"."+title);
						treeitem.setTitle(tabid+"."+title);
						treeitem.setTarget("nil_body");
						MusterXMLStyleBo mxbo=new MusterXMLStyleBo(conn,tabid);
						String commonQueryId=mxbo.getParamValue2(MusterXMLStyleBo.Param, "usual_query");
						if(commonQueryId!=null&&!"".equals(commonQueryId.trim()))
						{
							treeitem.setIcon("/images/overview_n_obj.gif");
						}
						else
						{
							treeitem.setIcon("/images/overview_obj.gif");
						}
						treeitem.setName(tabid);
						
						String url = "/general/muster/fillout_musterdata.do?b_search=link`tabid="+ tabid+"`isGetData=1";
						url = URLEncoder.encode(url,"GBK");
						treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);     	  
						strXml.append(treeitem.toChildNodeJS() + "\n");   
//					}  
				}   	       
			}
			if("1".equals(flag)){
				
				for(int i=0;i<rs1.size();i++){
					TreeItemView treeitem=new TreeItemView();
					DynaBean rec=(DynaBean)rs1.get(i);
					String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
					String title=rec.get("hzname")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("hzname").toString()):"";
					String module=rec.get("moduleflag")!=null?rec.get("moduleflag").toString():"";
					if(!"00".equals(module.substring(1,3)))
						continue;
					treeitem.setText(tabid+"."+title);
					treeitem.setTitle(tabid+"."+title);
					treeitem.setTarget("nil_body");
					treeitem.setName(tabid);
					MusterXMLStyleBo mxbo=new MusterXMLStyleBo(conn,tabid);
					String commonQueryId=mxbo.getParamValue2(MusterXMLStyleBo.Param, "usual_query");
					if(commonQueryId!=null&&!"".equals(commonQueryId.trim()))
					{
						treeitem.setIcon("/images/overview_n_obj.gif");
					}
					else
					{
						treeitem.setIcon("/images/overview_obj.gif");
					}
					String url = "/general/muster/fillout_musterdata.do?b_search=link`tabid="+ tabid+"`isGetData=1";
					url = URLEncoder.encode(url,"GBK");
					treeitem.setAction("/general/muster/hmuster/processBar.jsp?url="+url);  
					//treeitem.setAction("/general/muster/hmuster/processBar.jsp?url=/general/muster/fillout_musterdata.do?b_search=link`tabid="+ tabid+"`isGetData=1");     	  
					strXml.append(treeitem.toChildNodeJS() + "\n");   
				}
			}
			strXml.append("</TreeNode>\n");
		}
		if(conn!=null)
		{
			try
			{
				conn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
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
	private String getLoadTreeQueryString(String flag,String flaga,String moduleflag,String temp,UserView userview) {
		StringBuffer strsql=new StringBuffer();
		if("1".equals(flag)){
			strsql.append("SELECT styleid,styledesc FROM lstyle ");
			/* zxj 
			1、超级用户、超级用户组不做限制。
			2、有新建花名册分类、新建花名册权限的也不能限制读取分类，
			   否则自己建好空的分类（分类下还没来得及建花名册）后，分类树刷新后无法显示。
			*/
			if(!(userview.isAdmin() && "1".equals(userview.getGroupId()))
			        || userview.hasTheFunction("2603101") || userview.hasTheFunction("2603103"))
			{
				String wherestr = whereStr(userview,temp,flaga);
				wherestr=wherestr!=null&&wherestr.trim().length()>0?wherestr:"";
				strsql.append(" where styleid in("+wherestr+") ");
				//某个花名册分类下的花名册都没有权限查看，则不显示改分类。新增的花名册除外 （此处参考的新建花名册分类修改）chenxg 2016-12-05
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    strsql.append(" or styleid not in (select substr(moduleflag,2,2) from lname where flag='"+flaga+"')");
                else
                    strsql.append(" or styleid not in (select substring(moduleflag,2,2) from lname where flag='"+flaga+"')");
			}
			strsql.append(" order by styleid ");
		}else{
			strsql.append("SELECT tabid,hzname,moduleflag from lname where flag='");
			strsql.append(flaga);
			strsql.append("'"); 
			if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
				
				strsql.append(" and tabid in (");   
				strsql.append(temp); 
				strsql.append(")");
			}
			strsql.append(" order by norder");
		}    
		return strsql.toString();
	}
	private String whereStr(UserView userview,String temp,String flaga){
		StringBuffer wherestr = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer("SELECT moduleflag from lname where flag='");
		sqlstr.append(flaga);
		sqlstr.append("'"); 
		if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
			sqlstr.append(" and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(")");
		}
		sqlstr.append(" group by moduleflag");
		List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
		HashMap map=new HashMap();
		if(!rs.isEmpty()){
			for(int i=0;i<rs.size();i++){
				DynaBean rec=(DynaBean)rs.get(i);
				if(rec.get("moduleflag")!=null)
					map.put(rec.get("moduleflag").toString().substring(1,3), "1");
			}
		}
		Iterator   it   =(Iterator) map.entrySet().iterator();  
		while(it.hasNext())
		{   
		  Map.Entry   entry=(Map.Entry)it.next();   
		  Object   key =entry.getKey()   ;   
		  wherestr.append(",'"+key.toString()+"'");
		}
		String str="";
		if(wherestr.length()>0)
			str=wherestr.substring(1);
		else
			str="'-1'";
		return str;
	}
	
	
	
	
}

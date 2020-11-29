package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.businessobject.general.template.templateanalyse.ParseHtml;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

public class OutputKqTemplateDataServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
        doPost(request, response);
    }
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		LazyDynaBean paramBean=new LazyDynaBean();//这里没用，为空
       response.setContentType("application/msword;charset=UTF-8");
       response.setCharacterEncoding("GBK");       
       String tab_name=request.getParameter("tab_name");
       String id=request.getParameter("id");
       Connection conn=null;
	   ParseHtml parsehtml=null;
	   RowSet rs = null;
		try{
			conn=AdminDb.getConnection();
			String path=getIsTemplate(tab_name,conn);
			
			File templateFile = new File(path);		
			
			if(!templateFile.exists())
			{
				response.setContentType("application/msword;charset=UTF-8");
			    response.setCharacterEncoding("GBK");
				PrintWriter out = response.getWriter();
				out.println("文件路径不存在！");
				out.flush();
				out.close();	
				return;
			}
			
			String a0100="";
			String pre="";
			String sql="select nbase,a0100 from "+tab_name+" where "+tab_name+"01='"+id+"'";
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next())
			{
				a0100=rs.getString("a0100");
				pre=rs.getString("nbase");
			}
			UserView userView=(UserView)request.getSession().getAttribute(WebConstant.userView);
			String where=tab_name+"01='"+id+"'";
			parsehtml=new ParseHtml(path,userView,conn,tab_name,where);
			parsehtml.setSrc_a0100(a0100);
    	    parsehtml.setSrc_per(pre);
    	    Document doc=parsehtml.getTemplateDocument();
	    	String headstr=parsehtml.getTemplateHeadDataValue();	
	    	if(headstr.indexOf("<!--[if !mso]>")!=-1)
	    	{
	    		String headstr1=headstr.substring(0,headstr.indexOf("<!--[if !mso]>"));
	    		String headstr2=headstr.substring(headstr.indexOf("<!--[if !mso]>")+14);
	    		headstr=headstr1+headstr2;
	    	}
	    	
	    	/**按个是输出**/	    	
	    	parsehtml.executeTemplateDocument(doc,tab_name,id,paramBean);
	    	String datastr=parsehtml.outTemplateDataDocument(doc);
	    	
		   	if(datastr.indexOf("</head>")!=-1)
	    	    datastr=headstr + datastr.substring(datastr.indexOf("</head>") + "</head>".length());
	    	if(datastr.indexOf("</head>".toUpperCase())!=-1)
	    		datastr=headstr + datastr.substring(datastr.indexOf("</head>".toUpperCase()) + "</head>".toUpperCase().length());
			PrintWriter out = response.getWriter();
			datastr=datastr.replaceAll("wlhxryhrp","&nbsp;");
			datastr=datastr.replaceAll("xrywlh888","<br>");
			datastr=datastr.replaceAll("<!--[if !mso]>", "");
			out.println(datastr);
			//System.out.println(datastr);
			out.flush();
			out.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		    KqUtilsClass.closeDBResource(rs);
		    KqUtilsClass.closeDBResource(conn);
		}
       
    }
	/**
	 * 得到文件路径
	 * @param template_type
	 * @param conn
	 * @return
	 */
	public String  getIsTemplate(String template_type,Connection conn){
		String name="";
		if(template_type!=null&& "q11".equalsIgnoreCase(template_type))
			name="Kq_template_Q11";
		else if(template_type!=null&& "q13".equalsIgnoreCase(template_type))
			name="Kq_template_Q13";
		else if(template_type!=null&& "q15".equalsIgnoreCase(template_type))
			name="Kq_template_Q15";
		else 
			return "" ;		
		String path="";
		RowSet rs = null;
		try{
			String sql="select description,content from kq_parameter where b0110='UN' and name='"+name+"'";
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(sql);
			
			if(rs.next())
			{
				String description=rs.getString("description");
				String content=rs.getString("content");
				path=content+"\\"+description;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			KqUtilsClass.closeDBResource(rs);
		}
		return path;
	}
}

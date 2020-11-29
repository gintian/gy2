package com.hjsj.hrms.servlet.carderm;

import com.hjsj.hrms.businessobject.general.cadrerm.CadreAppointAndRemove;
import com.hjsj.hrms.businessobject.general.cadrerm.HtmlParse;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class CadreRMServlet extends HttpServlet {

	public CadreRMServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("application/msword;charset=UTF-8");
		response.setCharacterEncoding("GBK");

		String path = getServletContext().getRealPath(File.separator);
		HtmlParse hp = new HtmlParse(path+File.separator+"general"+File.separator+"cadrerm"+File.separator+"cadrermtemplate.htm");
		
		StringBuffer html = new StringBuffer();
		
		html.append(hp.getFormatHtml().htmlHtml());
		html.append(hp.getFormatHtml().htmlHead());
		
		String userName = request.getParameter("username");
		String dbPre = request.getParameter("dbpre");
		String cadreIds = request.getParameter("cadreids");

		String url = request.getContextPath();
		Connection con = null;
		try {
			con = AdminDb.getConnection();
			String [] cadreids = cadreIds.split(",");
			for(int i = 0; i<cadreids.length; i++){
				String cadreid = cadreids[i];
				CadreAppointAndRemove	caar = new CadreAppointAndRemove(con,dbPre,userName,url);
				caar.cadreInfoTableToWord(cadreid,hp.getDoc());
				String body = hp.xmlToHtml();
				html.append(body);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			try {
				if(con != null){
					con.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	 	html.append("</html>");
		
		
		//System.out.println(html.toString());
		
		PrintWriter out = response.getWriter();
		out.println(html.toString());
		out.flush();
		out.close();
		
		/*
		response.setContentType("application/msword;charset=UTF-8");
		response.setCharacterEncoding("GBK");
		PrintWriter out = response.getWriter();
		StringBuffer temp = new StringBuffer();
		
		temp.append("<%@ page contentType=\"application/msword;charset=UTF-8\"%>");
		temp.append("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
		temp.append("<head>");
		temp.append("<title>干部任免审批表</title>");
		temp.append("<meta http-equiv=Content-Type content=\"application/msword; charset=UTF-8\">");
		temp.append("<meta name=ProgId content=Word.Document>");
		temp.append("<meta name=Generator content=\"Microsoft Word 9\">");
		temp.append("<meta name=Originator content=\"Microsoft Word 9\">");
		temp.append("<style><!--");
		temp.append("@font-face"+
			" {font-family:宋体; "+
			" panose-1:2 1 6 0 3 1 1 1 1 1; "+
			" mso-font-alt:SimSun; "+
			" mso-font-charset:134; "+
			" mso-generic-font-family:auto; "+
			" mso-font-pitch:variable; "+
			" mso-font-signature:3 135135232 16 0 262145 0;} ");
		temp.append(" @font-face "+
			"{font-family:宋体;"+
			"panose-1:2 1 6 0 3 1 1 1 1 1;"+
			"mso-font-alt:SimSun;"+
			"mso-font-charset:134;"+
			"mso-generic-font-family:auto;"+
			"mso-font-pitch:variable;"+
			"mso-font-signature:3 135135232 16 0 262145 0;}");
		temp.append("@font-face"+
			"{font-family:\"\\@宋体\";"+
			"panose-1:2 1 6 0 3 1 1 1 1 1;"+
			"mso-font-charset:134;"+
			"mso-generic-font-family:auto;"+
			"mso-font-pitch:variable;"+
			"mso-font-signature:3 135135232 16 0 262145 0;}");
		temp.append("p.MsoNormal, li.MsoNormal, div.MsoNormal"+
			"{mso-style-parent:\"\";"+
			"margin:0cm;"+
			"margin-bottom:.0001pt;"+
			"text-align:justify;"+
			"text-justify:inter-ideograph;"+
			"mso-pagination:none;"+
			"font-size:10.5pt;"+
			"mso-bidi-font-size:12.0pt;"+
			"font-family:\"Times New Roman\";"+
			"mso-fareast-font-family:宋体;"+
			"mso-font-kerning:1.0pt;}");
		temp.append("p.MsoBodyText, li.MsoBodyText, div.MsoBodyText"+
			"{margin:0cm;"+
			"margin-bottom:.0001pt;"+
			"mso-line-height-alt:0pt;"+
			"mso-pagination:none;"+
			"font-size:12.0pt;"+
			"font-family:\"Times New Roman\";"+
			"mso-fareast-font-family:宋体;"+
			"mso-font-kerning:1.0pt;}");
		temp.append("@page"+
			"{mso-page-border-surround-header:no;"+
			"mso-page-border-surround-footer:no;}");
		temp.append("@page Section1"+
			"{size:515.95pt 728.6pt;"+
			"margin:34.0pt 38.95pt 1.0cm 1.0cm;"+
			"mso-header-margin:17.0pt;"+
			"mso-footer-margin:22.7pt;"+
			"mso-paper-source:0;"+
			"layout-grid:15.6pt;}");
		temp.append("div.Section1"+
			"{page:Section1;}");
		temp.append("-->");
		temp.append("</style></head>");
		temp.append("<body lang=ZH-CN style='tab-interval:21.0pt;text-justify-trim:punctuation'>");	

		String userName = request.getParameter("username");
		String dbPre = request.getParameter("dbpre");
		String cadreIds = request.getParameter("cadreids");
		
		String url = request.getContextPath();
		String temp1 = "";
		Connection con = null;
		try {
			con = AdminDb.getConnection();
			String [] cadreids = cadreIds.split(",");
			for(int i = 0; i<cadreids.length; i++){
				String cadreid = cadreids[i];
				CadreAppointAndRemove	caar = new CadreAppointAndRemove(con,dbPre,userName,url);
				temp1 = caar.cadreInfoTableToWord(cadreid);
				temp.append(temp1);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			try {
				if(con != null){
					con.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		temp.append("</body>");
		temp.append("</HTML>");
		System.out.println(temp.toString());
		out.println(temp.toString());
		out.flush();
		out.close();
		*/
		
	}

	
	public void init() throws ServletException {

	}

}

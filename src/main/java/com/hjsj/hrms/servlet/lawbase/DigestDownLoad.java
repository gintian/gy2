package com.hjsj.hrms.servlet.lawbase;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DigestDownLoad extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public DigestDownLoad() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fileid = request.getParameter("id");
		fileid = PubFunc.decrypt(SafeCode.decode(fileid));
		String type = request.getParameter("type");
		if(type==null)
			type="";
		Statement stmt = null;
		ResultSet rs = null;
		Connection con=null;
		response.setContentType("text/html");
		ServletOutputStream sos = response.getOutputStream();
		InputStream inputstream = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			con = AdminDb.getConnection();
			String content = "";
			try {
				stmt = con.createStatement();
				
				String f_name="";
				if("original".equalsIgnoreCase(type)){
					String sql="select name,digest,originalfile from law_base_file where file_id = '"+ fileid + "'";
					dbS.open(con, sql);
					rs = stmt.executeQuery(sql);
				}else{
					String sql="select name,digest,content from law_base_file where file_id = '"
							+ fileid + "'";
					dbS.open(con, sql);
					rs = stmt.executeQuery(sql);
				}
				
				if (rs.next()) {
					
					content = rs.getString("digest");
					f_name=rs.getString("name");					
					if("original".equalsIgnoreCase(type))
						inputstream = rs.getBinaryStream("originalfile");
					else
						inputstream = rs.getBinaryStream("content");

					
				}
				
				PubFunc.closeResource(rs);
				if (content == null || "".equals(content)
						|| "null".equals(content.trim())) {
					if("original".equalsIgnoreCase(type))
						response.sendRedirect("/selfservice/lawbase/downlawbase?id="+ fileid+"&type=original");
					else
						response
							.sendRedirect("/selfservice/lawbase/downlawbase?id="
									+ fileid);
					//CommonBusiness comnbus = new CommonBusiness(con);
					//comnbus.updateViewCount(fileid);
					return;
				} else {
					//文件		
					
					StringBuffer cont=new StringBuffer();
					if(inputstream!=null)
					{
						cont.append("<br>");
						cont.append("<table>");
						cont.append("<tr>");
						cont.append("<td>");
						cont.append("文件：");
						cont.append("</td>");
						cont.append("<td>");
						if("original".equalsIgnoreCase(type))
							cont.append("<a href='/selfservice/lawbase/downlawbase?id="+fileid+"&type=original"+"' target='_blank'>");
						else
							cont.append("<a href='/selfservice/lawbase/downlawbase?id="+fileid+"' target='_blank'>");
						cont.append(f_name+"</a>");
						cont.append("</td>");
						cont.append("</tr>");
						cont.append("</table>");
					}					
                    //附件
					rs = stmt.executeQuery("select * from law_ext_file where file_id='" + fileid + "'");
					StringBuffer href = new StringBuffer("</br> ");
					href.append("<table>");
					href.append("<tr>");
					href.append("<td>");
					href.append("附件：");
					href.append("</td>");
					while (rs.next()) {
						href.append("<td>");
						href.append("<a href='/servlet/AffixDownLoad?ext_file_id="
								+ PubFunc.encrypt(rs.getString("ext_file_id"))
								+ "' target='_blank'>"
								+ rs.getString("name") + "</a>");
						href.append("</td>");
					}
					href.append("</tr>");
					href.append("</table>");
					
					//附件描述
					StringBuffer new_content = new StringBuffer();
					new_content.append("<table>");
					new_content.append("<tr>");
					new_content.append("<td>");
					new_content.append("(" + content + ")");
					new_content.append("</td>");
					new_content.append("</tr>");
					new_content.append("</table>");
					
					sos.write(cont.toString().getBytes());
					sos.write(href.toString().getBytes());
					sos.write(new_content.toString().getBytes());
					//CommonBusiness comnbus = new CommonBusiness(con);
					//comnbus.updateViewCount(fileid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (GeneralException e) {
			e.printStackTrace();
        } finally {
        	try {
        		// 关闭Wallet
        		dbS.close(con);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}

            PubFunc.closeIoResource(inputstream);
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
            PubFunc.closeResource(con);
        }
	}

}

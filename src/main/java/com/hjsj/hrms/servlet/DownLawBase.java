/*
 * 创建日期 2005-6-28
 *
 */
package com.hjsj.hrms.servlet;

import com.hjsj.hrms.transaction.lawbase.CommonBusiness;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author lzy
 * 
 * 规章制度文件下载
 */
public class DownLawBase extends HttpServlet {
	final static String[] conReadFile = { "doc", "xls", "pdf", "txt", "html", "htm", "rar" ,"zip"};

	public void doGet(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		doPost(httpservletrequest, httpservletresponse);
	}

	public static boolean checkCanOpenFile(String ext) {
		if (ext == null)
			return false;
		for (int i = 0; i < conReadFile.length; i++) {
			if (ext.equalsIgnoreCase(conReadFile[i])) {
				return true;
			}
		}
		return true;
	}

	public void doPost(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		String ext = "";
		String s = "";
		String type = "";
		StringBuffer sb = new StringBuffer("");
		String txtAppend = "";
		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = httpservletresponse.getOutputStream();
		UserView userView = (UserView)httpservletrequest.getSession().getAttribute("userView");
		
		// 安全检查1：未登录不允许
        if (userView == null) {
            return;
        }
        
        /* 安全检查2：知识中心、文档管理模块、自助-规章制度，需要有相应模块权限
                  * 说明：实际应该根据下载的内容等信息来明确到底是哪个功能，暂时无法做到。
        */
        if (!userView.hasTheFunction("090901") && !userView.hasTheFunction("28")
                && !userView.hasTheFunction("0L07") && !userView.hasTheFunction("1107")) {
           return; 
        }
        
		try {
			// 判断term用来区分是否是全文检索页面的请求
			s = httpservletrequest.getParameter("id");
			type = httpservletrequest.getParameter("type");
			connection = (Connection) AdminDb.getConnection();
			String sql = "";
			if(type==null)
				type = "";
			if("original".equalsIgnoreCase(type))
				sql = "select originalext,originalfile,name from law_base_file where file_id='"
					+ s + "'";
			else
				sql = "select ext,content,name from law_base_file where file_id='"
					+ s + "'";
			String name = "";
			ContentDAO dao = new ContentDAO(connection);
			resultset = dao.search(sql);
			// 从数据库中取得数据
            if (resultset.next()) {
                if ("original".equalsIgnoreCase(type)) {
                    ext = resultset.getString("originalext");
                    inputstream = resultset.getBinaryStream("originalfile");
                } else {
                    ext = resultset.getString("ext");
                    inputstream = resultset.getBinaryStream("content");
                }
                //zhangh 2020-1-6 【56214】发文管理和制度政策中，涉及到导出的（pdf、word、excel、zip等等）命名，请统一成： 登陆用户_相应信息
				name = userView.getUserName() + "_" + replacechar(resultset.getString("name")) + "." + ext;
            } else {
                PubFunc.closeResource(inputstream);
				return;
			}
            
			if (httpservletrequest.getParameter("term") != null) {
				String term = httpservletrequest.getParameter("term");
				String[] deletestring = { "\\(", "\\)" };
				term = CommonBusiness.deleteString(term, deletestring);
				String highlightlist = "";
				term = term.replaceAll("[a|A][n|N][d|D]", "AND");
				term = term.replaceAll("[o|O][r|R]", "OR");
				String[] termsplit = term.split("AND");
				ArrayList highWords = new ArrayList();
				ArrayList v = new ArrayList();

				for (int i = 0; i < termsplit.length; i++) {
					v.add(termsplit[i].split("OR"));
				}
				Iterator it = v.iterator();
				while (it.hasNext()) {
					String[] temp = (String[]) it.next();
					for (int i = 0; i < temp.length; i++) {
						if (!"".equals(highlightlist))
							highlightlist += ",";
						highlightlist += "\"" + temp[i].trim() + "\"";
						highWords.add(temp[i].trim());
					}
				}
				
				if (Pattern.matches("[h|H][t|T][m|M][l|L]", ext)) {
					txtAppend = "<SCRIPT LANGUAGE=javascript>"
							+ "highlightobject(document.body,new Array("
							+ highlightlist + "),0);" + "</SCRIPT>";
					sb
							.append("<SCRIPT LANGUAGE='javascript' src='/js/HighlightText.js'>");
					sb.append("</SCRIPT>");
				}
			}
			
			if (inputstream == null) {
				return;
			}
			String mime = ServletUtilities.getMimeType("." + ext);
			// 设置http头部声明为下载模式
			httpservletresponse.setContentType(mime);	
			
			//zhaoxj 20190620 文件名编码处理，避免某些浏览器中文文件名乱码
			final String userAgent = httpservletrequest.getHeader("USER-AGENT"); 
		    if(userAgent.contains("Firefox")){
	            //是火狐浏览器，使用BASE64编码
	            name = new String(name.getBytes("UTF-8"), "ISO8859-1");
	        }else{
	            //给文件名进行URL编码
	            name = URLEncoder.encode(name, "utf-8");
	        }

			httpservletresponse.setHeader("Content-Disposition", "attachment;filename=\"" + name + "\"");
				
			int len;
			byte buf[] = new byte[512];
			int sumlen=0;
			if (!"".equals(sb.toString().trim())) {
				servletoutputstream.write(sb.toString().getBytes());
			}
			
			while ((len = inputstream.read(buf)) != -1) {
				sumlen=sumlen+len;
				servletoutputstream.write(buf, 0, len);
			}

			httpservletresponse.setContentLength(sumlen);
			
			if (!"".equals(txtAppend.trim())) {
				servletoutputstream.write(txtAppend.getBytes());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
        } finally {
            /**
                           * 关闭各种资源
             */
            try {

                if (servletoutputstream != null)
                    servletoutputstream.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            PubFunc.closeResource(servletoutputstream);
            PubFunc.closeResource(inputstream);
            PubFunc.closeResource(resultset);
            PubFunc.closeResource(connection);

        }
		return;
	}
	/**
	 * 过滤文件名称中的特殊字符
	 * @param name 自定义文件名称
	 * @return name 过滤后的文件名称
	 */
	private String replacechar(String name){
		try{
			
			name=name.replace("\\", "");
			name=name.replace("/", "");
			name=name.replace(":", "");
			name=name.replace("*", "");
			name=name.replace("?", "");
			name=name.replace("\"", "");
			name=name.replace("<", "");
			name=name.replace(">", "");
			name=name.replace("[", "");
			name=name.replace("]", "");
			name=name.replace("|", "");
			name=name.replace("=", "");
			name=name.replace("%", "");
			name=name.replace("'", "");
			name=name.replace(";", "");
			name=name.replace("&", "");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

}

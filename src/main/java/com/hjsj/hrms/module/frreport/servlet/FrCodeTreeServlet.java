package com.hjsj.hrms.module.frreport.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;

public class FrCodeTreeServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public FrCodeTreeServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
    public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		this.doPost(request, response);
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
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		HttpSession session = request.getSession();
		String privType = "U";
		UserView userView = (UserView) session.getAttribute("userView");
		TreeItemView treeItem=new TreeItemView();
		String reportAction = request.getParameter("reportAction");
		String action=reportAction;
		String showType = request.getParameter("showType");
		//String urlroot = System.getProperty("reportserver");
		//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/WebReport";
		//urlroot = basePath;
		String target="mil_body";
		String treetype="org";//org,duty,employee,noum
		treeItem.setName("root");		
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget(target);
		String rootdesc="";
		
		rootdesc="无项目";
		Connection conn = null;
		RowSet rs = null;
		try{
			conn = AdminDb.getConnection();
			String sql = "select codesetdesc from codeset where codesetid=?";
			ArrayList values = new ArrayList();
			values.add(showType);
			rs = new ContentDAO(conn).search(sql, values);
			if(rs.next()){
				rootdesc = rs.getString("codesetdesc");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		
		treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setLoadChieldAction("/servlet/FrCodeAddTreeServlet?privType="+privType+"&params=root&parentid=00&issuperuser=1&showType="+showType+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		treeItem.setAction("javascript:void(0)");
		response.setContentType("text/html");
		response.setCharacterEncoding("GBK");
		PrintWriter out = response.getWriter();
		out.println("  <!DOCTYPE HTML>");
		out.println("  <HTML>");
		out.println("  <HEAD>");
		out.println("  <SCRIPT LANGUAGE=javascript src=\"/js/xtree.js\"></SCRIPT> ");
		out.println("  <link rel=\"stylesheet\" href=\"/css/xtree.css\" type=\"text/css\">"); 
		out.println("  </HEAD>");
		out.println("  <BODY>");
		out.println("  <div id=\"treemenu\" style=\" width:100%\">");
		out.println("  <SCRIPT LANGUAGE=javascript> ");
		out.println("  "+treeItem.toJS());
		out.println("  root.expand1level();");
		out.println("  </SCRIPT>");
		out.println("  </div>");
		out.println("  </BODY>");
		out.println("  </HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	@Override
    public void init() throws ServletException {
		// Put your code here
	}

}

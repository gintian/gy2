package com.hjsj.hrms.module.frreport.servlet;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

public class FrReportTreeServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public FrReportTreeServlet() {
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
		//String urlroot = System.getProperty("reportserver");
		//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/WebReport";
		//urlroot = basePath;
		String target="mil_body";
		String showType = request.getParameter("showType");
		String treetype="";
		if(showType!=null){
			if("UN".equals(showType)){
				treetype="noum";
			}else if("UM".equals(showType)){
				treetype="org";
			}
		}
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		treeItem.setTarget(target);
		String rootdesc="";
		Connection conn=null;
		try {
			conn=AdminDb.getConnection();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
			rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
			if(rootdesc==null||rootdesc.length()<=0)
			{
				rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
			}
			treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
			if(userView.isSuper_admin())
				treeItem.setLoadChieldAction("/servlet/FrReportAddOrgTreeServlet?privType="+privType+"&params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
			else
			{
				if(userView.getStatus()==4 || userView.getStatus()==0)
					treeItem.setLoadChieldAction("/servlet/FrReportAddOrgTreeServlet?privType="+privType+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
				else
					treeItem.setLoadChieldAction("/servlet/FrReportAddOrgTreeServlet?privType="+privType+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no");
			}
			treeItem.setAction("javascript:void(0)");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
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
		out.println("  root.expand2level();");
//		out.println("  </SCRIPT>");
//		out.println("  </SCRIPT>");
//		out.println("  </SCRIPT>");
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
	 * @throws ServletException if an error occurs
	 */
	@Override
    public void init() throws ServletException {
		// Put your code here
	}

}

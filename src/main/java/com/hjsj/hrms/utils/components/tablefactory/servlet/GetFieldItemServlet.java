package com.hjsj.hrms.utils.components.tablefactory.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sys.SearchFieldItems;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/****
 * 查询指标
 * <p>
 * Title: GetFieldItemServlet
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-10-9 下午02:33:17
 * </p>
 * 
 * @author xiexd
 * @version 1.0
 */
public class GetFieldItemServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String istitle = req.getParameter("istitle");
		String fieldItemId = req.getParameter("fieldItemId");
		String searchtext = (String) req.getParameter("searchtext");
		String entityFn = (String) req.getParameter("entityFn");
		String scheme ="";
		if(req.getParameter("scheme") !=null && !"".equalsIgnoreCase(req.getParameter("scheme")))//栏目显示 存在并不为空 wangb 20170803  29936 其他地方也要用到
			scheme = (String) req.getParameter("scheme");//显示栏目指标集合  wangb 20170725  29936
		
		scheme = "," + scheme + ",";
		UserView userView = (UserView) req.getSession().getAttribute(
				WebConstant.userView);
		String filterItems = ((String) req.getSession().getAttribute(
				"filterItems") != null) ? (String) req.getSession()
				.getAttribute("filterItems") : "";
		// 测试filterItems =
		// "A0405,A0406,A040A,A0410,A0415,A0420,A0425,A0430,A0435,A0440,A0443,A0444,A0445,A0450,A0455,C0401,z0301";
		String nodeid = req.getParameter("node");
		String isCheckBox = req.getParameter("isCheckBox");
		ArrayList treeItems = new ArrayList();

		if ("true".equals(istitle)) {// 查询代码类 描述
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write("信息集指标");
			return;
		}

		try {
			if (searchtext != null && searchtext.length() > 0) {
				searchtext = PubFunc.hireKeyWord_filter_reback(searchtext);
				// 前台进行encodeUrl加密，解密
				searchtext = java.net.URLDecoder.decode(searchtext, "UTF-8");
			}
			SearchFieldItems sft = new SearchFieldItems(fieldItemId,
					searchtext, userView, nodeid, entityFn, isCheckBox);
			sft.setFilterItems(filterItems.toUpperCase());
			treeItems = sft.executeFieldItemSearch();
			ArrayList listtreeItems=new ArrayList();//接收未添加的指标  wangb 20170725  29936
			for(int i = 0 ; i < treeItems.size() ; i++){
				HashMap item = (HashMap)treeItems.get(i);
 				if(scheme.toUpperCase().indexOf("," + (String)item.get("fieldItemId") + ",") ==-1)
 					listtreeItems.add(item);
			}
//			outPutTree(resp, treeItems);
			outPutTree(resp, listtreeItems);//未添加的指标，返回到前台  wangb 20170725  29936
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void outPutTree(HttpServletResponse resp, ArrayList treeItems)
			throws IOException {
		String treeJSON = "{children:" + JSON.toString(treeItems) + "}";
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(treeJSON);
	}
}
package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.collections.FastHashMap;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class HireCloseBrowse extends HttpServlet {
	private Logger log = LoggerFactory.getLogger(HireCloseBrowse.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("贵州银行-->招聘外网退出！");
		Cookie[] ck=request.getCookies();
		if(ck!=null)
		{
			for(int k=0;k<ck.length;k++)
			{
				ck[k].setMaxAge(0);
				response.addCookie(ck[k]);
			}

		}
		HttpSession session=request.getSession();
		HashMap map=new HashMap();
		Enumeration e=session.getAttributeNames();
		String name="";
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			map.put(name, session.getAttribute(name));
		}
		if(SystemConfig.getPropertyValue("webserver").trim().equalsIgnoreCase("weblogic"))
		{
			e = session.getAttributeNames();
			while(e.hasMoreElements()){
				name = (String)e.nextElement();
				session.removeAttribute(name);
				//session.setAttribute(name,null);
			}
		}
		else
		{
			removeSession(session);
			session.invalidate();
			session=request.getSession();
		}

		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			Object value = map.get(key);
			if(key.equals("employPortalForm")){
				session.setAttribute((String) key, value);
			}
		}
		EmployPortalForm employPortalForm=(EmployPortalForm) session.getAttribute("employPortalForm");
		if(employPortalForm != null) {
			String hireChannel=employPortalForm.getHireChannel();//获得招聘渠道

			//		RequestDispatcher dispatcher = request.getRequestDispatcher("/hire/hireNetPortal/search_zp_position.do?b_exit=exit");
			//		dispatcher .forward(request, response);
			if("0".equals(hireChannel)) //如果是0则表示首页的推出
				response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_homeExit=exit");
			else
				response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_exit=exit");
		}else {
			PrintWriter out = response.getWriter();
			HashMap<String, String> logoutMap = new HashMap<String, String>();
			logoutMap.put("success", "true");
			out.write(JSON.toString(logoutMap));
			out.close();
		}
	}


	/**
	 * 退出时删除此session，防止下次登录不上
	 * @param session
	 */
	private void removeSession(HttpSession session) {
		FastHashMap onlineUserMap = (FastHashMap)session.getServletContext().getAttribute("userNames");
		if (onlineUserMap == null) {
			onlineUserMap = new FastHashMap();
		}
		Iterator var5 = onlineUserMap.keySet().iterator();
		while(var5.hasNext()) {
			String sessionId = (String)var5.next();
			if(sessionId!=null&&session!=null&&sessionId.equalsIgnoreCase(session.getId())) {
				onlineUserMap.remove(sessionId);
			}
		}
	}

}

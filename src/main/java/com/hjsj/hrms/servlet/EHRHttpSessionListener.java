package com.hjsj.hrms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;

/**
 * 用于控制注销另一使用此帐号的用户
 * <p>Title: EHRHttpSessionListener </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-18 下午02:18:24</p>
 * @author xuj
 * @version 1.0
 */
public class EHRHttpSessionListener implements HttpSessionListener {

	/**
	 * 创建session时执行
	 */
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("创建session.....");
		HttpSession session= se.getSession();
		ServletContext application = session.getServletContext();
		ArrayList sessions = (ArrayList)application.getAttribute("AllSession");
		if(sessions!=null){
			sessions.add(session);
		}else{
			sessions = new ArrayList();
			sessions.add(session);
			application.setAttribute("AllSession", sessions);
		}
	}

	/**
	 * sesion销毁时执行
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("sesion销毁.....");
		HttpSession session= se.getSession();
		ServletContext application = session.getServletContext();
		ArrayList sessions = (ArrayList)application.getAttribute("AllSession");
		if(sessions!=null){
			sessions.remove(session);
		}
	}

}

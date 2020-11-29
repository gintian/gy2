package com.hjsj.hrms.servlet.sys;

import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.SecurityLock;
import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.collections.FastHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class LogoutServlet extends HttpServlet  implements SecurityType{

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession();
		String flag=req.getParameter("flag");
		if(flag!=null&&flag.startsWith(LOGOUT_OTH)){//同帐号某一时间点仅允许登陆一次
			FastHashMap hm = (FastHashMap)session.getServletContext().getAttribute("userNames");
			
			UserView userView=(UserView)session.getAttribute(WebConstant.userView);
			String username = userView.getUserName();
			OnlineUserView item=null;
			Object[] userViewArray = null;
			if(hm!=null){
				userViewArray = hm.values().toArray();
					if(userViewArray!=null && userViewArray.length>0){
						for(int i=0;i<userViewArray.length;i++){
							if(userViewArray[i]!=null){
								item =(OnlineUserView)userViewArray[i];
								HttpSession se = item.getSession();
				                if(!se.equals(session)&&username.equalsIgnoreCase(item.getUserId())){
				                	se.invalidate();
				                	//加入当前登录用户在线信息
				                	OnlineUserView onlineview = new OnlineUserView();
				                	onlineview.setUserId(userView.getUserName());
				                    onlineview.setDept(userView.getUserDeptId());
				                    onlineview.setOrgname(userView.getUserOrgId());
				                    onlineview.setPos(userView.getUserPosId());
				                    onlineview.setUsername(userView.getUserFullName());
				                    onlineview.setIp_addr(req.getRemoteAddr());
				                    onlineview.setLogin_date(DateStyle.dateformat(new Date(), "yyyy-MM-dd HH:mm:ss"));
				                    onlineview.setSession(session);
				                	hm.put(session.getId(), onlineview);
				                	SecurityLock.clearCounter(userView.getUserName());
				                    break;
				                }
							}
							
						}
					}
			}
			
			/*Iterator i = hm.values().iterator();
			UserView userView=(UserView)session.getAttribute(WebConstant.userView);
			String username = userView.getUserName();
			OnlineUserView item=null;
            do
            {
            	item = (OnlineUserView)i.next();
            	HttpSession se = item.getSession();
                if(!se.equals(session)&&username.equalsIgnoreCase(item.getUserId())){
                	se.invalidate();
                	
                	//加入当前登录用户在线信息
                	OnlineUserView onlineview = new OnlineUserView();
                	onlineview.setUserId(userView.getUserName());
                    onlineview.setDept(userView.getUserDeptId());
                    onlineview.setOrgname(userView.getUserOrgId());
                    onlineview.setPos(userView.getUserPosId());
                    onlineview.setUsername(userView.getUserFullName());
                    onlineview.setIp_addr(req.getRemoteAddr());
                    onlineview.setLogin_date(DateStyle.dateformat(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    onlineview.setSession(session);
                	hm.put(session.getId(), onlineview);
                	SecurityLock.clearCounter(userView.getUserName());
                    break;
                }
                
            } while(i.hasNext());*/
			/*for(int i=sessions.size()-1;i>=0;i--){
				HttpSession se = (HttpSession)sessions.get(i);
				UserView user=(UserView)se.getAttribute(WebConstant.userView);
				if(user!=null&&!userView.equals(user)){
					if(username.equals(user.getUserName())){
						se.invalidate();
						break;
					}
				}
			}*/
            String url= "/system/security/resetup_password.do?br_index=link";
            switch(Integer.parseInt(flag.substring(14)))
			{
			   case HJSJ_YW:
				   url="/system/security/resetup_password.do?br_aindex=link";
				   break;
			   case HJSJ_ZZ:
				   url="/system/security/resetup_password.do?br_eindex=link";
				   break;
			   case HJSJ_YW4_ZM:
				   url="/system/security/resetup_password.do?br_e4index=link";
				   break;
			   case HJSJ_YW4_EM:
				   url="/system/security/resetup_password.do?br_h4index=link";
				   break;
			   case HJSJ_YW5_ZM:
				   url="/system/security/resetup_password.do?br_index=link";
				   break;
			   case HJSJ_CEO:
				   url="/system/security/resetup_password.do?br_bindex=link";
				   break;
			   case STATE_GRID:
				   url="/system/security/resetup_password.do?br_heindex=link";
				   break;
			   case HJSJ_ILEARNING:
				   url="/system/security/resetup_password.do?br_iindex=link";
			       break;
			   case HJSJ_HCM7:
				   url="/system/security/resetup_password.do?br_hcindex=link";
			       break;
			       
			}
			//req.getRequestDispatcher(url).forward(req,resp);
            resp.sendRedirect(url);
		}else if(flag!=null&&flag.startsWith("MOBILE_APP")){//原生移动服务退出功能注销后台会话  //xuj add 2016.09.17
			HashMap hm=new HashMap();
			try{
				session.invalidate();
			    hm.put("succeed","true");
			}catch(Exception e){
				hm.put("succeed","false");
				hm.put("message",e.getMessage());
			}finally{
				resp.setContentType("text/xml;charset=UTF-8");
		        resp.setHeader("Cache-Control", "no-cache");
		        JSONObject json=JSONObject.fromObject(hm);
		        String result = json.toString();
		        resp.getWriter().write(result);
			}
		}else{//各平台注销操作
			session.invalidate();
			int securitytype=-1;
			if(!(flag==null|| "".equals(flag)))
			{
				securitytype=Integer.parseInt(flag);
			}	
			String url="";
			switch(securitytype)
			{
			   case HJSJ_YW:
				   url="/templates/index/UserLogon.jsp";
				   break;
			   case HJSJ_ZZ:
				   url="/templates/index/employLogon.jsp";
				   break;
			   case HJSJ_YW4_ZM:
				   url="/templates/index/hrlogon4.jsp";
				   break;
			   case HJSJ_YW4_EM:
				   url="/templates/index/emlogon4.jsp";
				   break;
			   case HJSJ_YW5_ZM:
				   url="/templates/index/hrlogon.jsp";
				   break;
			   case HJSJ_YW5_EM:
				   url="/templates/index/emlogon.jsp";
				   break;
			   case HJSJ_CEO:
				   url="/templates/index/bilogon.jsp";
				   break;
			   case STATE_GRID:
				   url="/templates/index/epmlogon.jsp";
				   break;
			   case HJSJ_ILEARNING:
			       url = "/templates/index/ilearning.jsp";
			       break;
			   case HJSJ_HCM7:
			       url = "/templates/index/hcmlogon.jsp";
			       break;
			       
			}
			//resp.sendRedirect(url);
			//使用转发代替重定向，解决was系统下注销后跳转错误路径导致无法跳转到登录页面 guodd 2019-09-23
			req.getRequestDispatcher(url).forward(req, resp);
			//resp.getWriter().println("window.open("+url+",\"_parent\",\"toolbar=no,location=0,directories=0,status=no,menubar=no,scrollbars=no,resizable=yes\",\"true\");");
		}
	}
}

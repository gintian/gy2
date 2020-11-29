package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.admin.OnlineListener;
import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.collections.FastHashMap;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * 激活账号
 * @Titile: HireActiveAccount
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年11月1日下午8:19:24
 * @author: wangbs
 * @version 1.0
 *
 */
public class HireActiveAccount extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    String return_code = "success";
	    Connection connection = null;
	    PrintWriter out = null;
	    HashMap<String, String> map = new HashMap<String, String>();//map回写数据
	    HashMap<String, String> returnMap = new HashMap<String, String>();//接受bo返回的数据
	    try {
	        connection = (Connection) AdminDb.getConnection();
	        ResumeBo bo = new ResumeBo(connection);
	        out = response.getWriter();
			HttpSession session = request.getSession();
			String operate=request.getParameter("operate");
			operate=SafeCode.decode(operate);

            String ativeid=request.getParameter("activeid");
            ativeid=SafeCode.decode(ativeid);
            ativeid = PubFunc.keyWord_reback(ativeid);
            ativeid = PubFunc.decrypt(ativeid);
            ativeid = PubFunc.getReplaceStr(ativeid);
            String activeDate=request.getParameter("activeDate");//发送激活邮件的时间
            activeDate=PubFunc.decryption(activeDate);
            
            Calendar calendar = Calendar.getInstance(); 
            Date date =  calendar.getTime(); //激活的时间
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String activedDate =  format1.format(date);
            Date date1=format1.parse(activeDate);
            Date date2=format1.parse(activedDate);
            long dmm = date2.getTime()-date1.getTime();
            int hour =(int) dmm/1000/60/60;
            if(hour>=1){
                return_code = "outTime";
            }
            if("success".equals(return_code)) {
                returnMap = bo.refreshState(ativeid);
                if("success".equals(returnMap.get("return_code"))) {
                    login(session, returnMap.get("username"), returnMap.get("password"), connection, map, bo, return_code, ativeid);
                }else {
                    return_code = returnMap.get("return_code");
                }
            }
            map.put("return_code", return_code);
            out.write(JSON.toString(map));
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(connection);
			PubFunc.closeResource(out);
		}
	}
	/**
	 * 保存登录信息到session
	 * @param session
	 * @param loginName
	 * @param password
	 * @param connection
	 * @param map
	 * @param bo
	 * @param return_code
	 * @param ativeid
	 * @throws Exception
	 */
	private void login(HttpSession session, String loginName, String password, Connection connection,
			HashMap<String, String> map, ResumeBo bo, String return_code, String ativeid) throws Exception {
		if("success".equals(return_code)&&session!=null) {
			String applyCode = bo.getApplyCode(ativeid);
			UserView userview=new UserView(loginName,password,connection);
			userview.setA0100(ativeid);
			userview.setUserId(ativeid);
			userview.setUserEmail(loginName);
			userview.setDbname(bo.getDbName());
			userview.getHm().put("isEmployee","1");
			userview.getHm().put("isHeadhunter","0");//是否是猎头登录用户  0：不是     1：是
			userview.getHm().put("applyCode",applyCode);//登陆时设置应聘渠道
			userview.canLogin(true);
			session.setAttribute("islogon", true);
			session.setAttribute(WebConstant.userView, userview);

			deleteOnlineUser(session,userview);

			/** 是否可以打印准考证、查看成绩 **/
			ParameterXMLBo xmlBo = new ParameterXMLBo(connection,"1");
			HashMap xmlMap = xmlBo.getAttributeValues();
			String isAttach = "0";
            if(xmlMap.get("attach") != null && ((String)xmlMap.get("attach")).length() > 0) {
                isAttach = (String)xmlMap.get("attach");
            }
			EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(connection,isAttach);
			String admissionCard = "#";
            if(xmlMap.get("admissionCard")!=null&&!"".equals((String)xmlMap.get("admissionCard"))){
                admissionCard = (String)xmlMap.get("admissionCard");
            }
			boolean canPrintExamno = employNetPortalBo.canPrintExamNo(ativeid, admissionCard);
			boolean canQueryScore = employNetPortalBo.canQueryScore(bo.getDbName(), ativeid); //能否查看成绩
            if (canQueryScore) {
                map.put("canQueryScore", "1");
            }else {
                map.put("canQueryScore", "0");
            } 
            String resumeTemplateId = employNetPortalBo.getResumeTemplateId(ativeid);
            map.put("resumeTemplateId", resumeTemplateId); //简历登记表Id
            map.put("admissionCard",admissionCard); //登记表模板号
			map.put("canPrintExamno",String.valueOf(canPrintExamno)); //能否打印准考证
			map.put("nbase", PubFunc.encrypt(bo.getDbName()));
			map.put("a0100",PubFunc.encrypt(ativeid));
			map.put("return_code", return_code);
		}else {
		    map.put("return_code", return_code);
		}
	}
	/**
	 * 每个用户只允许登录一次，再次登录删除上一个登录session
	 * @param session
	 * @param userView
	 */
	private void deleteOnlineUser(HttpSession session, UserView userView) {
		FastHashMap onlineUserMap = (FastHashMap)session.getServletContext().getAttribute("userNames");
		if (onlineUserMap == null) {
			onlineUserMap = new FastHashMap();
			session.getServletContext().setAttribute("userNames", onlineUserMap);
		}
		Iterator keys = onlineUserMap.keySet().iterator();
		while(keys.hasNext()) {
			String sessionId = (String)keys.next();
			OnlineUserView user = (OnlineUserView) onlineUserMap.get(sessionId);
			if (userView.getUserName().equalsIgnoreCase(user.getUserId())) {
				// 通过得到session注销用户
				onlineUserMap.remove(sessionId);
				if (sessionId != null && !sessionId.equalsIgnoreCase(session.getId())) {
					user.getSession().invalidate();
				}
			}
		}
		ResumeOnlineListener onLineListener = (ResumeOnlineListener)session.getAttribute("online_listener");
		if (onLineListener == null) {
			onLineListener = new ResumeOnlineListener();
			session.setAttribute("online_listener", onLineListener);
		}
		OnlineUserView onLineUser = new OnlineUserView();
		onLineUser.setDept(userView.getUserDeptId());
		onLineUser.setOrgname(userView.getUserOrgId());
		onLineUser.setPos(userView.getUserPosId());
		onLineUser.setUsername(userView.getUserFullName());
		onLineUser.setIp_addr(userView.getRemote_ip());
		onLineUser.setLogin_date(DateStyle.dateformat(new Date(), "yyyy-MM-dd HH:mm:ss"));
		onLineUser.setUserId(userView.getUserName());
		onLineUser.setSession(session);
		onLineUser.setThreerole(userView.getThreeUserRole());
		onLineUser.setLoginSeqno(CreateSequence.getUUID());
		onlineUserMap.put(session.getId(), onLineUser);
	}
}

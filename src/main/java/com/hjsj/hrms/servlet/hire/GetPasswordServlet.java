package com.hjsj.hrms.servlet.hire;

import com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.transaction.sys.GetPasswordTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

public class GetPasswordServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
		
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 用户输入校验码
			String userValidate = SafeCode.decode(request.getParameter("validatecode"));
			// 系统生成校验码
			String sysValidate = (String) request.getSession().getAttribute("validatecode");
			// 获取完成后销毁校验码
			request.getSession().removeAttribute("validatecode");
			String msg = null;
			if (userValidate == null || userValidate.length() == 0) {
				// 校验码不能为空！
				msg = ResourceFactory.getProperty("error.validate.null");
			} else if (!sysValidate.equalsIgnoreCase(userValidate)) {
				// 请输入正确的校验码！
				msg = ResourceFactory.getProperty("error.validate.equals");
			}
			/** 校验标示 0：外网招聘找回密码；1：员工登陆界面找回密码，默认为0 */
			String validateFlag = SafeCode.decode(request.getParameter("validateFlag"));
			validateFlag = validateFlag == null || validateFlag.length() == 0 ? "0" : validateFlag;
			if ("0".equals(validateFlag)) {
				this.getPasswordRecruit(request, response, msg);
			} else if ("1".equals(validateFlag)) {
				this.getPasswordLogin(request, response, msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/**===============登陆界面找回密码 ==============================*/
	
	/**
	 * 
	 * @Title: getPasswordLogin   
	 * @Description: 登陆界面找回密码
	 * @param request
	 * @param response
	 * @param msg
	 * @throws ServletException
	 * @throws IOException 
	 * @return void
	 */
	private void getPasswordLogin(HttpServletRequest request,
			HttpServletResponse response, String msg) throws ServletException,
			IOException {
		String logintype = SafeCode.decode(request.getParameter("logintype"));
		String type = SafeCode.decode(request.getParameter("type"));
		String ZE = SafeCode.decode(request.getParameter("ZE"));
		try {
			// 校验码校验成功
			if (msg == null) {
				msg = this.getPasswordLoginMsg(logintype, type, ZE);
			} else {
				// 校验码错误提示加密
				msg = SafeCode.encode(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = e.toString();
			int index = errorMsg.indexOf("description:");
			msg = SafeCode.encode(errorMsg.substring(index + 12));
		}
		// 执行完毕刷新页面
		StringBuffer url = new StringBuffer();
		url.append("/gz/gz_analyse/historydata/salary_set_list.do?br_getpassword=get");
		url.append("&bosflag=hcm");
		url.append("&logintype=" + logintype);
		url.append("&username=" + request.getParameter("ZE"));
		url.append("&type=" + type);
		url.append("&msg=" + URLEncoder.encode(msg));//对msg进行编码，否则遇到特殊字符sendRedirect报错 guodd	2016-06-08
		// 发起刷新请求
		response.sendRedirect(url.toString());
	} 
	
	/**
	 * 
	 * @Title: getPasswordLoginMsg   
	 * @Description:    
	 * @param logintype 用户登录平台=1业务=2自助
	 * @param type 找回密码方式=1根据电话=2根据邮箱
	 * @param ZE 电话或邮箱
	 * @return String
	 * @throws GeneralException 
	 */
	private String getPasswordLoginMsg(String logintype, String type, String ZE) throws GeneralException{
		RowSet frowset = null;
		Connection connection = null;
		// 利用原生GetPasswordTrans执行相应请求
		GetPasswordTrans getPasswordTrans = new GetPasswordTrans();	
		try {	
			// 初始化核心参数
			getPasswordTrans.setFormHM(new HashMap());
			connection = AdminDb.getConnection();
			getPasswordTrans.setFrameconn(connection);
			getPasswordTrans.setFrowset(frowset);
			// 填充参数
			getPasswordTrans.getFormHM().put("logintype", logintype);
			getPasswordTrans.getFormHM().put("type", type);
			getPasswordTrans.getFormHM().put("ZE", ZE);
			// 发起请求
			getPasswordTrans.execute();
		} catch (Exception e) {		
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(frowset);
			PubFunc.closeResource(connection);
		}
		return (String) getPasswordTrans.getFormHM().get("msg");
	}
	
	
/**===============外网招聘找回密码  ==============================*/
	
	/**
	 * 
	 * @Title: getPasswordRecruit   
	 * @Description: 外网招聘找回密码  
	 * @param request
	 * @param response 
	 * @param msg 
	 * @return void
	 */
	private void getPasswordRecruit(HttpServletRequest request, HttpServletResponse response, String msg) throws ServletException, IOException {
		Connection connection = null;
		ResultSet resultset = null;
		java.sql.PreparedStatement statement = null;
		boolean bool=false;
		String email=SafeCode.decode(request.getParameter("email"));//邮箱
		try
		{
			AsyncEmailBo emailbo = new AsyncEmailBo(connection, null);
			connection = (Connection) AdminDb.getConnection();
			EmployPortalForm employPortalForm=(EmployPortalForm) request.getSession().getAttribute("employPortalForm");
			// 有错误
			if(msg!=null) {
				employPortalForm.setValidateInfo(msg);
				response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?br_getpassword=get"); 
				return;
			}
			ParameterXMLBo xmlBo=new ParameterXMLBo(connection,"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(connection,isAttach);
			String dbName=employNetPortalBo.getZpkdbName();	
			String hireChannel= employPortalForm.getHireChannel();//区分是否是猎头
			String userNameCloumn="username";
			String passWordCloumn="userpassword";
			String password="";
			String a0101="";
			StringBuffer buf = new StringBuffer("");
			if("headHire".equals(hireChannel)){
				buf.append("select Username,Password from zp_headhunter_login where UPPER(Email)='"+email.toUpperCase()+"'");
			}else{
				buf.append("select a0101,"+passWordCloumn+" from "+dbName+"a01 where UPPER("+userNameCloumn+")='"+email.toUpperCase()+"'");
			}
			
			ContentDAO dao = new  ContentDAO(connection);
			resultset = dao.search(buf.toString());
			/*statement=connection.prepareStatement(buf.toString());
			// 打开Wallet
			dbS.open(connection, buf.toString());

			resultset = statement.executeQuery();*/
			boolean isExisit=false;
			while(resultset.next()){
				if("headHire".equals(hireChannel)){
					password=resultset.getString("Password");
					a0101=resultset.getString("Username");
				}else{
					password=resultset.getString(passWordCloumn);
					a0101=resultset.getString("a0101");
				}
				isExisit=true;
			}
			String fromAddr=this.getFromAddr();
			if(fromAddr==null|| "".equals(fromAddr.trim())&&!bool)
			{
				msg="系统未设置邮件服务器！";
				bool=true;
			}
			if(!isExisit&&!bool)
			{
				msg="系统未找到与你输入匹配的用户，请确认输入是否正确！";
				bool=true;
			}
			String why=SystemConfig.getPropertyValue("sys_name");
			if(why==null|| "".equals(why))
				why="";
			String str=why;
			StringBuffer content = new StringBuffer("");
			//content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+a0101+",您好：<br>");
			content.append(a0101+"，您好：<br>");//汉口银行特意要求将前面空格去掉
			//content.append("您的"+str+"注册邮箱为："+email+",密码为:"+password+"<br>");
			content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您的"+str+"注册邮箱为："+email+"，密码为："+password+"<br>");//汉口银行要求格式
			String title="找回密码";
			LazyDynaBean emails = new LazyDynaBean();
			emails.set("subject", title);
			emails.set("bodyText", content.toString());
			emails.set("toAddr", email);
			try
			{
				if(!bool)
				{
					emailbo.send(emails);
					msg = "1";
				}
				//EMailBo bo=new EMailBo(connection,true,"");
		    	//bo.sendEmail(title,content.toString(),"",fromAddr,email);
			}
			catch(Exception e)
			{
				msg="系统邮件服务器配置不正确，请联系系统管理员！";
			}
			employPortalForm.setValidateInfo(msg);
			response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?br_getpassword=get"); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(resultset);
			PubFunc.closeResource(connection);
		}
	}

	public String getFromAddr() throws GeneralException {
		String str = "";
		RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (stmp_vo == null)
			return "";
		String param = stmp_vo.getString("str_value");
		if (param == null || "".equals(param))
			return "";
		Document doc = null;
		try {
			doc = PubFunc.generateDom(param);
			Element root = doc.getRootElement();
			Element stmp = root.getChild("stmp");
			str = stmp.getAttributeValue("from_addr");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(doc);
		}
		return str;
	}
}

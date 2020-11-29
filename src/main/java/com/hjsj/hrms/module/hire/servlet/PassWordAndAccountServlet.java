package com.hjsj.hrms.module.hire.servlet;

import com.hjsj.hrms.module.hire.businessobject.GetZpAccountsBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.hire.businessobject.SendResetPasswordMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Administrator 
 * 处理忘记密码和忘记账号需要验证码的部分
 *
 */
public class PassWordAndAccountServlet extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = null;
		Connection conn = null;
		try {
			HttpSession session = req.getSession();
			resp.setCharacterEncoding("UTF-8");
			out = resp.getWriter();
			conn = AdminDb.getConnection();
			HashMap<String, Object> returnDate = new HashMap<String, Object>();
			String sessionValidateCode = (String) session.getAttribute("validatecode");
			// session中的验证码取后即销毁，避免安全漏洞
			session.removeAttribute("validatecode");
			boolean validateSuccess = (Boolean)(session.getAttribute("validateSuccess")==null?false:session.getAttribute("validateSuccess"));
			session.removeAttribute("validateSuccess");
			/*解析ajax参数start*/
			Map<String, String[]> map = req.getParameterMap();
			if(!validateSuccess&&map.isEmpty())
				return;
			String[] values = map.get("__xml");
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			if(values==null || values.length==0) {//处理旧招聘参数
				Set<String> keySet = map.keySet();
				for (String key : keySet) {
					params.put(key, map.get(key)[0]);
				}
			}else
				params = (HashMap<String, Object>) JSON.parse(values[0]);
			if(!validateSuccess&&params.isEmpty())
				return;
			/*解析ajax参数end*/
			//发送邮件的链接地址 
			String requesturl = "https://" + req.getServerName() + ":" + req.getServerPort();
			// 系统外部地址
			String zp_url = SystemConfig.getPropertyValue("zp_url");
			if (StringUtils.isNotBlank(zp_url)){
				requesturl = SystemConfig.getPropertyValue("zp_url");
			}
			String validatecode = (String) params.get("codeValue");
			String operate = (String) params.get("operate");
			String emailName = (String) params.get("emailName");
			String newHireFlag = (String) (params.get("newHireFlag")==null?"":params.get("newHireFlag"));
			//找回账号后不需要校验验证码
			if (!validateSuccess&&(StringUtils.isEmpty(sessionValidateCode) || !sessionValidateCode.equalsIgnoreCase(validatecode))) {
				returnDate.put("flag", "validatecode-error");
				out.write(JSON.toString(returnDate));
				return;
			}
			if (!"retrieveAccount".equals(operate)){
				if(validateSuccess) {
					emailName = (String) session.getAttribute("emailName");
					session.removeAttribute("emailName");
				}
				ResumeBo resumeBo = new ResumeBo(conn);
				SendResetPasswordMailBo sendbo = new SendResetPasswordMailBo(conn);
				String flag = sendbo.sendEmail(emailName, requesturl, newHireFlag);
				String address = resumeBo.getMailBoxLoginAddress(emailName);
				returnDate.put("flag", flag);
				returnDate.put("address", address);
			} else if("retrieveAccount".equals(operate)) {
				GetZpAccountsBo bo = new GetZpAccountsBo(conn);
				String nameValue = (String) params.get("nameValue");
				String phoneValue = (String) params.get("phoneValue");
				String onlyValue = (String) params.get("onlyValue");
				HashMap reData = bo.getUserName(nameValue, phoneValue, onlyValue);
				if("true".equals(reData.get("return_code"))){
					session.setAttribute("validateSuccess", true);
					session.setAttribute("emailName", reData.get("msg"));
				}
				returnDate.put("flag", reData.get("return_code"));
				returnDate.put("msg", reData.get("msg"));
			}
			out.write(JSON.toString(returnDate));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(conn);
			PubFunc.closeResource(out);
		}
	}

}

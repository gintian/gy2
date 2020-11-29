package com.hjsj.hrms.module.jobtitle.logon;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

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

@SuppressWarnings("serial")
public class LoginCheck extends HttpServlet {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = "";
		Connection conn = null;
		HttpSession session = request.getSession();
		JSONObject jsonObject = new JSONObject();
    	RowSet rs = null;
    	PrintWriter out = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String isShowValidatecode = request.getParameter("isShowValidatecode");//是否显示雁验证码，0或空，显示，1，不显示
			JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(conn,null);
			if("1".equals(isShowValidatecode)) {
				String show_Validatecode = jobtitleConfigBo.getJobtitleParamConfig("show_Validatecode");
				jsonObject.put("show_Validatecode", show_Validatecode);
			}else {
				boolean validateCheck = false;// 验证码校验
				boolean userCheck = false;// 用户名密码校验
				String errorCode = "0";
				
				String bencrypt = request.getParameter("bencrypt");//是否加密
				username = request.getParameter("username");
				String password = request.getParameter("password");
				String validatecode = "";
				String show_Validatecode = jobtitleConfigBo.getJobtitleParamConfig("show_Validatecode");
				if("true".equalsIgnoreCase(show_Validatecode))
					validatecode = request.getParameter("validatecode");
				else 
					validateCheck = true;
				
				if("true".equalsIgnoreCase(bencrypt)){//加密时，解密用户名和密码
					username = PubFunc.decrypt(username);
					password = PubFunc.decrypt(password);
				}
				String vcode = (String)session.getAttribute("validatecode");
	
				// 验证码校验
				if(("true".equalsIgnoreCase(bencrypt) || (!StringUtils.isEmpty(vcode)&&vcode.equalsIgnoreCase(validatecode)))){//加密时是二维码登录，不需要校验验证码
					validateCheck = true;
				}
				
				// 用户名密码校验
				String w0101 = null;
				//因为未启动的时候state为0，暂停的时候也是0，这样如果加上state=1条件，会报错账号密码不对，提示不明确
				//后面有专门的判断
		    	String sql = "select password,w0101 from zc_expert_user where username=?";
		    	ArrayList<String> list = new ArrayList<String>();
		    	list.add(username);
		    	rs = dao.search(sql, list);
		    	while(rs.next()){
		    		String pwd = rs.getString("password");
		    		w0101 = rs.getString("w0101");
		    		if(StringUtils.isEmpty(password)){
		    			if(StringUtils.isEmpty(pwd)){
		    				userCheck = true;
		    			}
		    		}else{
		    			if(password.equalsIgnoreCase("MD5`" + DigestUtils.md5Hex((pwd).getBytes("UTF-8")))){
		    				userCheck = true;
		    			}
		    		}
		    	}
		    	
		    	// 打印错误信息
		    	if(!userCheck){
		    		errorCode = "1";//"用户名或密码错误，请重试！";
		    	}
		    	if(!validateCheck){
		    		errorCode = "2";//"验证码输入错误！";
		    	}
		    	
		    	
		    	if("0".equals(errorCode)){// 校验通过
		    		// 初始化
		    		UserView userView = new UserView(username, conn);			
		    		userView.getHm().put("moduleFlag", "jobtitleVote");//当前模块
		    		userView.setA0100(w0101);//w0101专家编号
		    		/*int version = 50;
		    		EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
		    		if(lockclient != null){
		    			version = lockclient.getVersion();
		    		}
		    		userView.setVersion(version);//版本
	*/	    		
		    		userView.setVersion(70);//版本 陈总提，所版本写死70
	
		    		session.setAttribute("username", username);
		    		session.setAttribute("islogon", Boolean.valueOf(true));
		    		session.setAttribute("userView", userView);
		    	}
		    	jsonObject.put("errorCode", errorCode);
			}
	    	
	    	response.setContentType("text/html;charset=UTF-8");
	    	out = response.getWriter();  
	    	out.write(jsonObject.toString()); 
	    	return ;
			
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeIoResource(out);
			try {
				if (conn != null)
					conn.close();
			} catch (Exception sql) {
				sql.printStackTrace();
			}
		}
	}
}
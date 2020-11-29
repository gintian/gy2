package com.hjsj.hrms.servlet.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HrcloudLoginServlet  extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
//		String sessionId = request.getSession().getId();
		UserView userView=(UserView)request.getSession().getAttribute("userView");
		String userid = userView.getUserName();
		String a0100 = userView.getA0100();
		String nbase = userView.getDbname();
		String uniqueid = SyncConfigUtil.getUniqueidByA0100(nbase,a0100);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String time = dateFormat.format( new Date() );
		
		JSONObject tokenJson = new JSONObject();
		tokenJson.put("userid", uniqueid);
		tokenJson.put("time", time);

		String token = PubFunc.encrypt(tokenJson.toString());
		try{
			String url =SyncConfigUtil.getCloudLogonUrl(uniqueid,token,0);
			response.sendRedirect(url);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

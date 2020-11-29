package com.hjsj.hrms.servlet.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.CloudConstantParams;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * token校验servlet
 * @author xus
 *
 */
public class TokenCheckServlet  extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private String appSecret = null;
	
	public String getAppSecret() {
		JSONObject json = CloudConstantParams.getDataBaseCloudParam();
		this.appSecret = json.getString("appSecret");
		return appSecret;
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		JSONObject respJson = new JSONObject();
		JSONObject dataJson = new JSONObject();
		String success = "true";
		
		InputStream is= null;
		try{
			is = request.getInputStream();
			String bodyInfo = IOUtils.toString(is, "utf-8");
			if(bodyInfo == null || "".equals(bodyInfo)){
				success = "false";
			}else{
				/**
				 {"data":"455b0f61574429132e53026fb833c27b5e7df74a6f7e1ab4d03a8a446246c62a93a52cb6f5fcabc41568c1b1a5f9995f93a52cb6f5fcabc41568c1b1a5f9995f5501124c06ce244692b7008c4e8ca2e1cd9e1a79bcd93ed5e0c22a7239428dab05080aec2b88eea5661356ee3418839ae8250e2c1917e4968b1040ab60f99a09",
				 "sign":"bf4a6da67853761610691d16777fb4b2e9858d14",
				 "success":"true"}
				 **/
				JSONObject reqJson = JSONObject.fromObject(bodyInfo);
				if(reqJson.containsKey("data")){
					String data = reqJson.getString("data");
					String sign = reqJson.getString("sign");
					String reqsuccess = reqJson.getString("success");
					
					String resData = SyncConfigUtil.AESDecrypt(data,getAppSecret());
					JSONObject resDataJson = JSONObject.fromObject(resData);
					
					String staffNum = resDataJson.getString("staffNum");
					String token = resDataJson.getString("token");
					
					if(StringUtils.isNotBlank((token))){
						token = PubFunc.decrypt(token);
					}
					
					if(SyncConfigUtil.tockenCheck(token)){
						dataJson.put("isLogin","true");
						dataJson.put("code",null);
						dataJson.put("msg",null);
					}else{
						dataJson.put("isLogin","false");
						dataJson.put("code","1001");
						dataJson.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.logonfail"));
					}
				}
			}
			String dataEncode = SyncConfigUtil.AESEncrypt(dataJson.toString(), getAppSecret());
			respJson.put("success", success);
			respJson.put("data",dataEncode);
			
			ArrayList datalist = new ArrayList();
			datalist.add(success);
			datalist.add(dataEncode);
			String signEncode = SyncConfigUtil.digest(datalist);
			respJson.put("sign", signEncode);
			
			response.getWriter().write(respJson.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
	}
	
}

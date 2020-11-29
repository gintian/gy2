package com.hjsj.hrms.servlet.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.businessobject.ReceiveHjCloudInfo;
import com.hjsj.hrms.module.system.hrcloud.businessobject.impl.ReceiveHjCloudInfoImpl;
import com.hjsj.hrms.module.system.hrcloud.util.CloudConstantParams;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.utils.PubFunc;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 接收云参数接口
 * @author xus 
 * 19/10/17
 */
public class ReceiveHjCloudInfoServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
    private String appSecret = null;
    
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	/**
	 * post方法 获取云数据
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject respJson = new JSONObject();
		JSONObject dataJson = new JSONObject();
		String success = "true";
		String msg = "";
		
		InputStream is= null;
		int type =1;
		try{
			JSONObject json = CloudConstantParams.getDataBaseCloudParam();
			String appId = json.getString("appId");
			String appSecret = json.getString("appSecret");
			
			is = request.getInputStream();
			String bodyInfo = IOUtils.toString(is, "utf-8");
			if(bodyInfo != null && !"".equals(bodyInfo)){
				JSONObject reqJson = JSONObject.fromObject(bodyInfo);
				JSONObject vaildJson = SyncConfigUtil.validSign(reqJson);
				if(vaildJson.getBoolean("flag")){
					String data = reqJson.getString("data");
					String resData = SyncConfigUtil.AESDecrypt(data,appSecret);
					JSONObject resDataJson = JSONObject.fromObject(resData);
					type = resDataJson.getInt("type");
					//执行同步
					ReceiveHjCloudInfo rhci = new ReceiveHjCloudInfoImpl(resDataJson);
					dataJson = rhci.doExcute();
				}else{
					success = "false";
					msg = vaildJson.getString("msg");
				}
					
			}
			
			String str = "";
			if(dataJson.containsKey("data")){
				if(type == 1 || type == 2){
					str = dataJson.getJSONObject("data").toString();
				}else{
					str = dataJson.getJSONArray("data").toString();
				}
			}
			String dataEncode = SyncConfigUtil.AESEncrypt(str, appSecret);
			respJson.put("success", success);
			respJson.put("data",dataEncode);
			respJson.put("code","");
			respJson.put("appId",appId);
			respJson.put("msg",msg);
			
			ArrayList datalist = new ArrayList();
			datalist.add(appId);
			datalist.add(dataEncode);
			datalist.add(success);
			
			//code xus 19/11/29 云没用到此参数，改为直接传空
			datalist.add("");
			//msg
			datalist.add(msg);
//			System.out.println("sign加密前的list："+datalist.toString());
			String signEncode = SyncConfigUtil.digest(datalist);
//			System.out.println("加密后sign："+signEncode);
			respJson.put("sign", signEncode);
//			System.out.println("返回数据："+respJson.toString());
			
			response.getWriter().write(respJson.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
	}

}

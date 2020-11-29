package com.hjsj.hrms.transaction.mobileapp.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 
 * <p>Title: RongAuthClient </p>
 * <p>Description: 与融云服务器交互</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-8-1 下午1:41:13</p>
 * @author yangj
 * @version 1.0
 */
public class RongAuthClient {

	/**
	 * 
	 * @Title: auth   
	 * @Description: 注册及获取 Token  
	 * @param userId      用户唯一id
	 * @param name        用户名
	 * @param portraitUri 用户头像
	 * @param deviceId    用户手机机型
	 * @return String     response in json format or null if error occurred(you should use some http debug tools to get the detailed error infomations).
	 */
	public String auth(String userId, String name, String portraitUri) {
		// 测试环境
		return this.auth("82hegw5uhou8x", "Gqa0Ms89ado8a", userId, name, portraitUri);
	}
	
	/**
	 * 
	 * @Title: auth   
	 * @Description:    
	 * @param appKey       appKey
	 * @param appSecret    appSecret
	 * @param userId       用户唯一id
	 * @param name         用户名
	 * @param portraitUri  用户头像链接
	 * @return String
	 */
	private String auth(String appKey, String appSecret, String userId, String name, String portraitUri) {
		StringBuilder retSb = null;
		HttpURLConnection conn = null;
		try {
			URL AUTH_URL = new URL("http://api.cn.rong.io/user/getToken.json");
			conn = (HttpURLConnection) AUTH_URL.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			HttpURLConnection.setFollowRedirects(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("appKey", appKey);
			conn.setRequestProperty("appSecret", appSecret);
			conn.setRequestProperty("Content-Type", "Application/x-www-form-urlencoded");
			
			StringBuilder sb = new StringBuilder("userId=");
			sb.append(URLEncoder.encode(userId, "UTF-8"));
			sb.append("&name=").append(URLEncoder.encode(name, "UTF-8"));
			sb.append("&portraitUri=").append(URLEncoder.encode(portraitUri, "UTF-8"));

			OutputStream out = conn.getOutputStream();
			try {				
				out.write(sb.toString().getBytes("UTF-8"));
				out.flush();
			}finally{
				Tools.closeIoResource(out);
			}

			if(conn.getResponseCode() == 200){
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				retSb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					retSb.append(line);
				}
				reader.close();
			}
		} catch (Exception ignore) {
			
		} finally {
			try {
				if (conn != null)
					conn.disconnect();
			} catch (Exception ignore) {

			}
		}
		return retSb == null ? "" : retSb.toString();
	}
	
}

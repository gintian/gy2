package com.hjsj.hrms.servlet.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

public class CancelAssessResultServlet extends HttpServlet{
	private String appSecret = null;
	
	public String getAppSecret() {
		if(this.appSecret == null){
			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			if (recordVo != null) {
				String str = recordVo.getString("str_value");
				if(!"".equals(str)){
					JSONObject json = JSONObject.fromObject(str);
					if(json.get("appSecret") != null){
						this.appSecret = json.getString("appSecret");
					}
				}
			}
		}
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
		
		boolean hasData = false;
		InputStream is= null;
		try{
			is = request.getInputStream();
			String bodyInfo = IOUtils.toString(is, "utf-8");
			if(bodyInfo != null && "".equals(bodyInfo)){
				JSONObject reqJson = JSONObject.fromObject(bodyInfo);
				if(reqJson.containsKey("data")){
					hasData = true;
					String appId = reqJson.getString("appId");
					String data = reqJson.getString("data");
					String sign = reqJson.getString("sign");
					
					String resData = SyncConfigUtil.AESDecrypt(data,getAppSecret());
					JSONObject resDataJson = JSONObject.fromObject(resData);
					
					JSONArray list = resDataJson.getJSONArray("list");
					respJson = CancelAssessResult(list);
					SyncAssessDataLoggerUtil.start("考核数据撤销", list, "success="+String.valueOf(respJson.getBoolean("success")));
				}
			}
			if(!hasData){
				respJson.put("success", false);
				respJson.put("errcode", "1004");
				respJson.put("message", "未获取到数据");
				respJson.put("failReport","");
				SyncAssessDataLoggerUtil.start("考核数据撤销", new JSONArray(), "success=false");
			}
			response.getWriter().write(respJson.toString());
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			PubFunc.closeIoResource(is);
		}
	}

	private JSONObject CancelAssessResult(JSONArray list) {
		JSONObject returnJson = new JSONObject();
		
		Connection conn = null;
		
		boolean success = true;
		String errcode = "";
		JSONArray failReport = new JSONArray();
		int id = 0;
		
		
		HashMap returnMap = SyncConfigUtil.getHrAssessConnectConfig();
		JSONObject orgAssessSet = (JSONObject) returnMap.get("orgAssessSet");
		JSONObject empAssessSet = (JSONObject) returnMap.get("empAssessSet");
		String orgtable = orgAssessSet.getString("setid");
		//TODO 人员库？
		String emptable = "USR"+empAssessSet.getString("setid");
		String orgbusinessIdField = orgAssessSet.getString("businessId");
		String empbusinessIdField = empAssessSet.getString("businessId");
		
		if("".equals(orgtable)&&"".equals(emptable)){
			returnJson.put("success", false);
			returnJson.put("errcode", "1001");
			returnJson.put("message", "未配置考核结果集");
			returnJson.put("failReport","" );
			return returnJson;
		}
		if("".equals(orgbusinessIdField)&&"".equals(empbusinessIdField)){
			returnJson.put("success", false);
			returnJson.put("errcode", "1001");
			returnJson.put("message", "未配置考核项目标识");
			returnJson.put("failReport","" );
			return returnJson;
		}
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql = "";
			String businessIds = "";
			for(Object o :list){
				JSONObject data = (JSONObject)o;
				String businessId = data.getString("businessId");
				if(!"".equals(businessIds)){
					businessIds += ",";
				}
				businessIds += businessId;
			}
			
			if(!"".equals(orgtable)){
				sql = "delete from "+orgtable+" where "+orgbusinessIdField+" in ("+businessIds+")";
				dao.update(sql);
			}
			if(!"".equals(emptable)){
				sql = "delete from "+emptable+" where "+empbusinessIdField+" in ("+businessIds+")";
				dao.update(sql);
			}
			returnJson.put("success", true);
			returnJson.put("errcode", "");
			returnJson.put("message", "");
			returnJson.put("failReport","" );
		}catch (Exception e) {
			returnJson.put("success", false);
			returnJson.put("errcode", "1003");
			returnJson.put("message", "系统异常"+e.getMessage());
			returnJson.put("failReport","" );
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		return returnJson;
	}
}

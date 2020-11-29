package com.hjsj.hrms.module.system.hrcloud.util;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 获取云参数
 * @author xus
 *
 */
public class CloudConstantParams {
	public static RecordVo recordvo;
	public static JSONObject paramJson;
	static{
		recordvo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
		if (recordvo != null) {
			String str = recordvo.getString("str_value");
			if(!"".equals(str)){
				paramJson = JSONObject.fromObject(str);
				if(paramJson.get("appId") == null){
					paramJson.put("appId", "");
				}
				if(paramJson.get("tenantId") == null){
					paramJson.put("tenantId", "");
				}
				if(paramJson.get("appSecret") == null){
					paramJson.put("appSecret", "");
				}
				if(paramJson.get("tables") == null){
					paramJson.put("tables", null);
				}
				if(paramJson.get("cloudTOhr") == null){
					paramJson.put("cloudTOhr", null);
				}
				if(paramJson.get("frequency") == null){
					paramJson.put("frequency", null);
				}
				if(paramJson.get("used") == null){
					paramJson.put("used", "0");
				}
			}
		}
		if(paramJson.isEmpty()){
			paramJson.put("appId", "");
			paramJson.put("tenantId", "");
			paramJson.put("appSecret", "");
			paramJson.put("tables", null);
			paramJson.put("cloudTOhr", null);
			paramJson.put("frequency", null);
			paramJson.put("used", "0");
		}
	}
	public static JSONObject getParamJson(){
		return paramJson;
	}
	
	/**
	 * 在数据库中获取云配置参数
	 * @return
	 */
	public static JSONObject getDataBaseCloudParam(){
		Connection conn = null;
		RowSet rs = null;
		String sql = "select Str_Value from Constant where Constant = 'HRCLOUD_CONFIG' ";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()) {
				if(rs.getObject("Str_Value") != null ) {
					String Str_Value = Sql_switcher.readMemo(rs, "Str_Value");
					if(!"".equals(Str_Value)){
						paramJson = JSONObject.fromObject(Str_Value);
						if(paramJson.get("appId") == null){
							paramJson.put("appId", "");
						}
						if(paramJson.get("tenantId") == null){
							paramJson.put("tenantId", "");
						}
						if(paramJson.get("appSecret") == null){
							paramJson.put("appSecret", "");
						}
						if(paramJson.get("tables") == null){
							paramJson.put("tables", null);
						}
						if(paramJson.get("cloudTOhr") == null){
							paramJson.put("cloudTOhr", null);
						}
						if(paramJson.get("frequency") == null){
							paramJson.put("frequency", null);
						}
						if(paramJson.get("used") == null){
							paramJson.put("used", "0");
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		return paramJson;
	}
}

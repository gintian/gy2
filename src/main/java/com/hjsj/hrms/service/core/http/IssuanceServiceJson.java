package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class IssuanceServiceJson {

	/**
	 * 配置hr的链接
	 */
	private String hrp_logon_url="";
	private JSONObject jsonObj;
	
	private void init() {
		jsonObj = new JSONObject();
		jsonObj.put("title", "人力资源系统");
		jsonObj.put("link", this.hrp_logon_url);
		jsonObj.put("description", "集团人力资源系统");
		jsonObj.put("language", "zh-cn");
	}
	
	public IssuanceServiceJson() {
		this.hrp_logon_url = SystemConfig.getPropertyValue("hrp_logon_url");
		this.init();
	}
	
	public String saveParamAttribute(ArrayList list, String username) {
		JSONArray jsonArr = new JSONArray();
		if (list == null || list.size() <= 0) {
			jsonObj.put("items", jsonArr);
			return this.returnJsonStr("1", jsonObj.toString());
		}
		LazyDynaBean bean = null;
		for (int i = 0; i < list.size(); i++) {
			JSONObject json = new JSONObject();
			bean = (LazyDynaBean) list.get(i);
			json.put("title", bean.get("title"));
			String urlTemp = "";
			// 先判断是否已经存在了http字符,有就不再加hrp_logon_url
			if (bean.get("url") != null) {
				if (!((String) bean.get("url")).contains("http")) {
					urlTemp = this.hrp_logon_url + "" + (String) bean.get("url");
				} else {
					urlTemp = (String) bean.get("url");
				}
			}
			json.put("link", urlTemp);
			json.put("description", (String) bean.get("description"));
			json.put("author", username);
			// 申请人
			String applyname = (String) bean.get("applyname");
			applyname = applyname == null ? "" : applyname;
			json.put("applyname", applyname);
			String datetime = (String) bean.get("datetime");
			if (datetime != null && datetime.trim().length() > 0) {
				json.put("pubDate", datetime);
			}else {
				json.put("pubDate", PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
			}
			jsonArr.add(json);
		}
		jsonObj.put("items", jsonArr);

		return this.returnJsonStr("1", jsonObj.toString());
	}

	/**
	 * 返回Json格式的字符串
	 * @param message 错误信息
	 * @return
	 */
	public String errorMessage(String message) {
		JSONObject json = new JSONObject();
		json.put("flag", "0");
		json.put("msg", message);
	    return json.toString();
	}
	
	/**
	 * 返回json格式的字符串
	 * @param opt 选项值 0:失败,返回错误信息,1:成功,返回数据信息,2:成功(数据同步状态回写成功,但没有数据信息返回)
	 * @param msg 返回的信息
	 */
	private String returnJsonStr(String opt,Object msg) {
	    JSONObject json= new JSONObject();
	    if("0".equals(opt)) {
	    	json.put("flag",opt);
	    	json.put("msg",msg);
	    }else if("1".equals(opt)) {
	    	json.put("flag",opt);
	    	json.put("data",msg);
	    }
	    return json.toString();
	}
}

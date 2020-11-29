package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.weixin.utils.CommonUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingWXServerMenuTrans extends IBusiness{

	/**日志文件判断加载是否正常**/
    private Category cat = Category.getInstance(this.getClass());
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		String type = (String) this.formHM.get("type");
		boolean result = (Boolean) this.formHM.get("result");
		if(!result){
			this.formHM.put("result","2");
			return;
		}
		int wxitemid = (Integer) this.formHM.get("serverid");
//		if("save".equalsIgnoreCase(type)){//先保存在发布
//			MorphDynaBean menuData= (MorphDynaBean) this.formHM.get("menuData");
//			boolean flag = saveMenuParam(menuData,wxitemid);
//			if(!flag){
//				this.formHM.put("result","2");////保存失败
//				return;
//			}
//		}
		/**
-        * 发布操作，返回值
-        * 1.发布成功
		 * 
-        * 3.发布失败，推送微信服务号菜单功能失败
-        * 4.发布失败，请配置自定义参数
		 */
		String appid = "";
		String appsecret = "";
		String url = "";
		String app_type="";
		String str_value = "";
		String serverMenuParam = "";
		StringBuffer str = new StringBuffer();
		str.append("select appid,app_secret,url,app_type,str_value from t_sys_weixin_param where wxitemid=? and wxsetid=?");
		ArrayList list= new ArrayList();
		list.add(wxitemid);
		list.add("service");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(str.toString(), list);
			if(this.frowset.next()){
				appid = this.frowset.getString("appid");
				appsecret = this.frowset.getString("app_secret");
				url = this.frowset.getString("url");
				app_type = this.frowset.getString("app_type");
				str_value = this.frowset.getString("str_value");
			}
			cat.debug("----str_value------>"+str_value);
			if(str_value == null){
				this.formHM.put("result","4");//发布失败,请配置自定义菜单
				return;
			}
			Map<String,String> resultMap  = getAccess_token(appid,appsecret);
			String access_token =""; 
			String isSuccess = resultMap.get("isSuccess");
			cat.debug("----isSuccess------>"+isSuccess);
			if(StringUtils.equals("true", isSuccess)) {
			    access_token = resultMap.get("access_token");
			}else {
			    String errcode = resultMap.get("errcode");
			    this.formHM.put("result","3");//发布失败
			    this.formHM.put("errcode", errcode);
			    return;
			}
//			if("recruit".equalsIgnoreCase(app_type))
			serverMenuParam = getRecruitServerMenuParam(str_value,appid,url);
//			else if("other".equalsIgnoreCase(app_type))//其他类型的服务号
//				serverMenuParam = getOtherServerMenuParam(str_value,appid);
			cat.debug("---url---->"+serverMenuParam);
			boolean flag = releaseMenus(access_token,serverMenuParam);
			if(flag)
				this.formHM.put("result","1");//发布成功
			else
				this.formHM.put("result","3");//发布失败
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	private boolean saveMenuParam(MorphDynaBean menuParam,int serverid){
		boolean flag = false;
		StringBuffer str_value = new StringBuffer();
		ArrayList params = (ArrayList) menuParam.get("params");
		ArrayList param = new ArrayList();
		str_value.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		str_value.append(" <param>");
		// 遍历菜单数据并返回
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i) == null) {// 判断是否有菜单数据
				continue;
			}
			MorphDynaBean tempmap = (MorphDynaBean) params.get(i);//一级菜单数据
			String menuname = (String) tempmap.get("menuname");// 一级菜单名
			String menutype = (String) tempmap.get("menutype");// 一级菜单类型
			String firstorder = (String) tempmap.get("firstorder");// 一级菜单类型
			str_value.append("	<menu menuname=\"" + menuname + "\"  menutype=\"" + menutype + "\" order=\""+firstorder+"\">");
			if(tempmap.toString().indexOf("functions=") != -1){
				param = (ArrayList) tempmap.get("functions");
				for (int j = 0; j < param.size(); j++) {
					if (param.get(j) == null) {//判断是否有二级菜单
						continue;
					}
					MorphDynaBean tempfunction = (MorphDynaBean) param.get(j);//二级菜单数据
					String functionname = (String) tempfunction.get("functionname");// 二级菜单名
					String secondorder = (String) tempfunction.get("secondorder");// 二级菜单名
					String functiontype = (String)tempfunction.get("functiontype");// 二级菜单功能默认空值
					str_value.append("		<function functionname=\""+functionname+"\" functiontype=\""+functiontype+"\" order=\""+secondorder+"\"/>");
				}
			}
			str_value.append("	</menu>");
		}
		str_value.append(" </param>");
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE t_sys_weixin_param SET str_value = ? WHERE wxitemid = ? and wxsetid=?");
		ArrayList list = new ArrayList();
		list.add(str_value.toString());
		list.add(serverid);
		list.add("service");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			if(dao.update(sql.toString(), list) > 0 )
				flag =true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**
	 * 获取ACCESS_TOKEN
	 * @param appid  微信服务号appid
	 * @param appsecret 微信服务号密钥 appsecret
	 * @return
	 */
	private Map<String,String> getAccess_token(String appid , String appsecret){
	    Map<String,String> resultMap = new HashMap<String,String>();
	    boolean isSuccess = true;
		String access_token = "";
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		requestUrl = requestUrl.replace("APPID", appid);
		requestUrl = requestUrl.replace("APPSECRET", appsecret);
		JSONObject obj = CommonUtil.httpsRequest(requestUrl, "GET",null);
		isSuccess = obj.containsKey("access_token");
		if(!isSuccess) {//成功的话就会返回access_token,否则的话就会有errcode
		    String errcode = obj.getString("errcode");
		    resultMap.put("isSuccess", String.valueOf(isSuccess));
		    resultMap.put("errcode", String.valueOf(errcode));
		    return resultMap;
		}
		access_token = obj.getString("access_token");
		resultMap.put("isSuccess", String.valueOf(isSuccess));
        resultMap.put("access_token", String.valueOf(access_token));
		return resultMap;
	}
	
	private String getOtherServerMenuParam(String str_value,String appid){
		StringBuffer str = new StringBuffer();
		
		
		return str.toString();
	}
	
	/**
	 * 招聘服务号自定义菜单参数
	 * @param str_value 自定义菜单数据
	 * @param appid  微信服务号appid
	 * @param serverUrl 微信服务号服务地址
	 * @return
	 */
	@SuppressWarnings("rawtypes")
    private String getRecruitServerMenuParam(String str_value, String appid, String serverUrl){
		Map<String,List<Map<String,Object>>> settingBodyMap = new HashMap<String,List<Map<String,Object>>>();
        List<Map<String,Object>> buttonList = new ArrayList<Map<String,Object>>();
		try {
			Document doc = PubFunc.generateDom(str_value);
			Element rootEl = doc.getRootElement();
			List menusList = rootEl.getChildren("menu");
			for( int i = 0 ; i < menusList.size() ; i++){
			    Map<String,Object> buttonMap = new HashMap<String,Object>();
				Element menuEl = (Element) menusList.get(i);
				String name = menuEl.getAttributeValue("menuname");
//				String type = menuEl.getAttributeValue("menutype");
				String menuurl = menuEl.getAttributeValue("menuurl");
				buttonMap.put("name", name);
				List funcsList = menuEl.getChildren("function");
				if(funcsList == null || funcsList.size() == 0){
					if(menuurl.trim().length()==0)
						continue;
					buttonMap.put("key", "menu_"+(i+1));
					buttonMap.put("url", menuurl);
					buttonMap.put("type", "view");
					buttonList.add(buttonMap);
					continue;
				}
				List<Map<String,String>> sub_buttonList = new ArrayList<Map<String,String>>();
				for( int j = 0 ; j < funcsList.size() ; j++){
				    Map<String,String> sub_buttonMap = new HashMap<String,String>();
					Element funcEl = (Element) funcsList.get(j);
					String functionname = funcEl.getAttributeValue("functionname");
//					String functiontype = funcEl.getAttributeValue("functiontype");
					String functionurl = funcEl.getAttributeValue("functionurl");
					String functionUrl = getFunctionUrl(appid,serverUrl,functionurl/*,type,functiontype*/);
					String key = "function_"+(i+1)+"_"+(j+1);
//					if(!"other".equalsIgnoreCase(type))
//						functionUrl = getFunctionUrl(appid,serverUrl,type,functiontype);
//					else
//						functionUrl = functiontype;
//					if("recruit".equalsIgnoreCase(type))
//						key = type +i +"_"+ j;
//					if("selfcode".equalsIgnoreCase(type))
//						key = type;
					sub_buttonMap.put("type", "view");
					sub_buttonMap.put("key", key);
					sub_buttonMap.put("name", functionname);
					sub_buttonMap.put("url", functionUrl);
					sub_buttonList.add(sub_buttonMap);
				}
				buttonMap.put("sub_button", sub_buttonList);
				buttonList.add(buttonMap);
			}
			settingBodyMap.put("button", buttonList);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        }
		JSONObject settingJson = JSONObject.fromObject(settingBodyMap);
		String settingJsonString = settingJson.toString();
		return settingJsonString;
	}
	
	/**
	 * 服务号功能请求url
	 * @param appid   微信服务号appid
	 * @param serverUrl 微信服务号服务器地址
	 * @param functionurl 功能链接地址 或 自定义地址
	 * @return
	 */
	private String getFunctionUrl(String appid, String serverUrl,String functionurl/*String type,String value*/){
		String functionUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=${url}&response_type=code&scope=snsapi_base&state=${state}#wechat_redirect";
		if(functionurl.indexOf("/module/recruitplatform/index.jsp") == -1)// 不是配置的链接
			return functionurl;
		functionurl = functionurl.replaceAll("amp;", "");
		String url = serverUrl + "/recruitservice/oauthservlet";
		String state ="";
		if(functionurl !=null && functionurl.trim().length()>0){
			String[] funcsplit = functionurl.split("\\?");
			if(funcsplit.length ==2){
				state = funcsplit[1];
				state = URLEncoder.encode(state);
			}
		}
		
		url = URLEncoder.encode(url);
		functionUrl = functionUrl.replace("APPID", appid);
		functionUrl = functionUrl.replace("${url}", url);
		functionUrl = functionUrl.replace("${state}", state);
		return functionUrl;
	}
	
	
	/**
	 * 发布服务号
	 * @param 接口调用凭证
	 * @param str 自定义菜单
	 * @return
	 */
	private boolean releaseMenus(String access_token,String param){
		boolean flag = false;
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
		url = url.replace("ACCESS_TOKEN",access_token);
		JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", param);
		cat.debug("-----release----->"+jsonObject.toString());
		if("ok".equalsIgnoreCase(jsonObject.getString("errmsg"))) {
			flag = true;
		}
		return flag;
	}
	
}

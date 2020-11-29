package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @Titile: SearchWXEnterpriseAppParamTrans
 * @Description:加载已存在的企业号应用配置
 * @Company:hjsj
 * @Create time: 2018年6月27日下午2:10:42
 * @author: wangbs
 * @version 1.0
 *
 */
public class SearchWXEnterpriseAppParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		StringReader reader = null;
		try {
			String itemId = (String) this.formHM.get("itemid");//应用编号
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer();
			HashMap paramInfo = new HashMap();
			HashMap logo = new HashMap();
			List paramsList = null;
			List list = new ArrayList();
			list.add(itemId);
			sql.append("select * from t_sys_weixin_param where wxitemid=?");
			this.frowset = dao.search(sql.toString(),list);
			if(this.frowset.next()) {
				Document doc = PubFunc.generateDom(this.frowset.getString("str_value"));
				
				Element root = doc.getRootElement();
				paramsList = root.getChildren();
//				Element param = (Element) paramsList.get(0);
				Element param = root.getChild("param");
				logo.put("localname", param.getAttributeValue("name"));
				logo.put("fullpath", param.getAttributeValue("path"));
				logo.put("filename", param.getAttributeValue("filename"));
				paramInfo.put("logo", logo);
				paramInfo.put("name", this.frowset.getString("wxname"));
				paramInfo.put("agentid", this.frowset.getString("appid"));
				paramInfo.put("secret", this.frowset.getString("app_secret"));
				paramInfo.put("url", this.frowset.getString("url"));
				paramInfo.put("type", this.frowset.getString("app_type"));
				paramInfo.put("description", this.frowset.getString("description"));
			}
			if(paramsList.size() == 1)
				paramInfo.put("customMenu",loadInitFuncCustomMenu());
			else
				paramInfo.put("customMenu",loadFuncCustomMenu(paramsList));
			
			this.formHM.put("paramInfo", paramInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList loadInitFuncCustomMenu(){//没有配置企业号应用自定义菜单,初始化
		ArrayList list = new ArrayList();
		for(int i = 0 ; i < 3 ; i++){//主菜单共3级
			HashMap map = new HashMap();
			map.put("menuname","主菜单");
			map.put("menuurl","");
			map.put("order",(i+1));
			list.add(map);
		}
		return list;
	}
	
	private ArrayList loadFuncCustomMenu(List paramsList){
		ArrayList list = new ArrayList();
		for(int i =0 ; i < paramsList.size() ; i++){
			Element param = (Element) paramsList.get(i);
			if(!"menu".equalsIgnoreCase(param.getName()))
				continue;
			HashMap menuMap = new HashMap();
			menuMap.put("menuname", param.getAttributeValue("menuname"));// 获取当前循环到的一级列表名
			menuMap.put("menuurl", param.getAttributeValue("menuurl"));// 获取当前循环到的一级列表链接地址
			menuMap.put("order", param.getAttributeValue("order"));
			List functions = param.getChildren();
			ArrayList functionList = new ArrayList();
			for (int j = 0; j < functions.size(); j++) {// 循环当前menu节点下的所有二级菜单function节点
				Map<String,String> functionMap = new HashMap<String,String>();
				Element functionElement = (Element) functions.get(j);
				functionMap.put("functionname", functionElement.getAttributeValue("functionname"));
				functionMap.put("functionmenu",functionElement.getAttributeValue("functionmenu"));
				functionMap.put("functionurl",functionElement.getAttributeValue("functionurl"));
				functionMap.put("order", functionElement.getAttributeValue("order"));
				functionList.add(functionMap);
			}
			menuMap.put("functions", functionList);
			list.add(menuMap);
		}
		
		return list;
	}
	
}

package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务号界面初始化
 * 
 * @author caoqy 2018-5-28 14:59:06 wxsetid 微信程序类型标识 serverid 微信应用编号 name 应用名称
 *         APPID 微信应用appid号 AppSecret 微信应用app_secret号 url 应用程序服务地址 app_type 应用类型
 *         description 应用简介 str_value 菜单数据
 */
/**
 * 
 *
 * @Titile: SearchWXSettingParamTrans
 * @Description:企业号界面所需数据初始化
 * @Company:hjsj
 * @Create time: 2018年6月27日下午1:33:35
 * @author: wangbs
 * @version 1.0
 *
 */
public class SearchWXSettingParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap formMap = this.getFormHM();
		String type = (String) formMap.get("type");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		Map<String,Object> returnParamDataMap = new HashMap<String,Object>();// 用于存储返回数据的对象
		ArrayList<HashMap<String,String>> funcList = new ArrayList<HashMap<String,String>>();
		try {
			this.frowset = dao.search("select codeitemid,codeitemdesc from codeitem where codesetid='35' and invalid=1 ");
			while(this.frowset.next()){
				HashMap<String,String> itemMap = new HashMap<String,String>();
				itemMap.put("itemid", this.frowset.getString("codeitemid"));
				itemMap.put("itemdesc", this.frowset.getString("codeitemdesc"));
				funcList.add(itemMap);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		returnParamDataMap.put("funcList", funcList);
		if("service".equals(type)) {
		    StringReader reader = null;
			int serverid = 0;// 初始化时菜单数据对应的serverid
			List<Map<String,Object>> serverList = new ArrayList<Map<String,Object>>();// 服务列表
			HashMap<String,String> noticeMap = new HashMap<String,String>();// 消息模板
			List<Map<String,Object>> menuList = new ArrayList<Map<String,Object>>();// 菜单列表
			ArrayList<String> list =new ArrayList<String>();
			sql.append("SELECT wxitemid,wxname,appid,app_secret,url,app_type,description FROM t_sys_weixin_param where wxsetid=?");
			list.add("service");
			try {
				// 服务号配置列表
				this.frowset = dao.search(sql.toString(),list);
				while (this.frowset.next()) {
					Map<String,Object> serverMap = new HashMap<String,Object>();// 单个服务对象
					serverMap.put("serverid", this.frowset.getInt("wxitemid"));
					serverMap.put("servername", this.frowset.getString("wxname"));
					serverMap.put("APPID", this.frowset.getString("appid"));
					serverMap.put("AppSecret", this.frowset.getString("app_secret"));
					serverMap.put("url", this.frowset.getString("url"));
					serverMap.put("servertype", this.frowset.getString("app_type"));
					serverMap.put("description", this.frowset.getString("description"));
					serverList.add(serverMap);
				}
				this.frowset = dao.search("select wxitemid,str_value from t_sys_weixin_param where wxsetid=? order by wxitemid",list);
				// 菜单列表
				if(this.frowset.next()) {//判断是否有服务号数据，如果没有则return。
					this.frowset.previous();
				}else {
					returnParamDataMap.put("servers", serverList);// 服务号列表
					returnParamDataMap.put("params", menuList);//
					returnParamDataMap.put("serverid", serverid);// 第一个菜单列表
					this.getFormHM().put("paramInfo", returnParamDataMap);
					return;
				}
				String menuxml = "";// 菜单xml信息
				List<Map<String,Object> > menuLists = new ArrayList<Map<String,Object>>();
				while (this.frowset.next()) {
				    Map<String,Object> menuMap = new HashMap<String,Object>();
					serverid = this.frowset.getInt("wxitemid");
					menuxml = this.frowset.getString("str_value");
					if(StringUtils.isEmpty(menuxml)) {
					    StringBuffer menuTempXMl = new StringBuffer();
			            menuTempXMl.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			            menuTempXMl.append("<param><menu menuname=\"我要应聘\" menuurl=\"\" order=\"1\"></menu>");
			            menuTempXMl.append("<menu menuname=\"个人中心\" menuurl=\"\" order=\"2\"></menu>");
			            menuTempXMl.append("<menu menuname=\"了解我们\" menuurl=\"\" order=\"3\"></menu>");
			            menuTempXMl.append(" </param>");
					    menuxml = menuTempXMl.toString();
					}
					menuMap.put("wxitemid", serverid);
					menuMap.put("menuxml", menuxml);
					menuLists.add(menuMap);
				}
				
				Document doc = null;
				doc = PubFunc.generateDom(menuxml);
				String xpath = "/param";
				XPath path = XPath.newInstance(xpath);
				Element paramsxml = (Element) path.selectSingleNode(doc);
				List menus = paramsxml.getChildren();
				List functions = null;
			
				for (int i = 0; i < menus.size(); i++) {// 遍历所有的一级节点
					Map<String,Object> menuMap = new HashMap<String,Object>();
					Element menuElement = (Element) menus.get(i);
					String nodeName = menuElement.getName();
					if("noticeTemplate".equals(nodeName)) {
					    List<Attribute> menuElementList = menuElement.getAttributes();
					    for (Attribute variable : menuElementList) {
                            noticeMap.put(variable.getName(), variable.getValue());//模板内容
                        }
					} else {
					    menuMap.put("menuname", menuElement.getAttributeValue("menuname"));// 获取当前循环到的一级列表名
					    menuMap.put("menuurl", menuElement.getAttributeValue("menuurl"));// 获取当前循环到的一级列表链接地址
					    menuMap.put("order", menuElement.getAttributeValue("order"));
					    functions = menuElement.getChildren();
					    List<Map<String,String>> functionList = new ArrayList<Map<String,String>>();
					    
					    for (int j = 0; j < functions.size(); j++) {// 循环当前menu节点下的所有二级菜单function节点
					        Map<String,String> functionMap = new HashMap<String,String>();
					        Element functionElement = (Element) functions.get(j);
					        String functionurl = functionElement.getAttributeValue("functionurl");
					        functionurl = functionurl.replaceAll("&amp;", "&");
					        
					        functionMap.put("functionname", functionElement.getAttributeValue("functionname"));
					        functionMap.put("functionmenu",functionElement.getAttributeValue("functionmenu"));
					        functionMap.put("functionurl",functionurl);
					        functionMap.put("order", functionElement.getAttributeValue("order"));
					        functionList.add(functionMap);
					    }
					    menuMap.put("functions", functionList);
					    menuList.add(menuMap);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(reader);
			}
			returnParamDataMap.put("noticeMap", noticeMap);// 消息模板配置
			returnParamDataMap.put("servers", serverList);// 服务号列表
			returnParamDataMap.put("params", menuList);//
			returnParamDataMap.put("serverid", serverid);// 第一个菜单列表
		}else {
			StringReader reader = null;
			try {
				List<HashMap<String,String>> appList = new ArrayList<HashMap<String,String>>();//app列表
				Map configParams = new HashMap();//存放配置参数
				RecordVo recordVo = ConstantParamter.getConstantVo("SS_QQWX");
				HashMap<String, String> map = new HashMap<String, String>();
				// 判断数据库中是否存在
				if (recordVo != null) {
					// 读取xml转换为Document
					Document doc = PubFunc.generateDom(recordVo.getString("str_value"));

					// 读取根节点
					Element root = doc.getRootElement();
					List<?> list = root.getChildren();
					Element child;
					// 循环提取数据输出到前台
					for (int i = 0; i < list.size(); i++) {
						child = (Element) list.get(i);
						map.put(child.getAttributeValue("key"), child.getAttributeValue("value"));
						if("funcsecret".equals(child.getAttributeValue("key"))){
							List<?> childlist = child.getChildren();
							Element menu;
							for (int j = 0; j < childlist.size(); j++) {
								menu = (Element) childlist.get(j);
								map.put("menu_"+menu.getAttributeValue("menuid"), menu.getAttributeValue("secret"));
								map.put(menu.getAttributeValue("menuid"), menu.getAttributeValue("desc"));
							}
						}
					}
				}
				returnParamDataMap.put("param",map);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeIoResource(reader);
			}
		}
		this.getFormHM().put("paramInfo", returnParamDataMap);
	}
}

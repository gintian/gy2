package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 *
 * @Titile: SaveWXEnterpriseAppParamTrans
 * @Description:保存企业号应用配置
 * @Company:hjsj
 * @Create time: 2018年6月27日下午3:14:24
 * @author: wangbs
 * @version 1.0
 *
 */
public class SaveWXEnterpriseAppParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String type = (String) this.formHM.get("type");//新增：add  更新：update
			MorphDynaBean paramInfoMorphDynaBean = (MorphDynaBean) this.formHM.get("paramInfo");//编辑后的应用所有信息
			HashMap paramInfo =PubFunc.DynaBean2Map(paramInfoMorphDynaBean);
			String wxname = (String) paramInfo.get("name");//应用名称
			String appid = (String) paramInfo.get("agentid");//应用agentid
			String app_secret = (String) paramInfo.get("secret");//应用secret
			String url = (String) paramInfo.get("url");//信任域名
			String app_type = (String) paramInfo.get("type");//应用类型
			String description = (String) paramInfo.get("description");//应描述

			HashMap logo =  (HashMap) paramInfo.get("logo");//logo信息
			String name = (String)logo.get("name");//logo名称
			String path = (String)logo.get("path");//logo加密路径
			String filename = (String)logo.get("filename");//logo加密名称
			
			// 组装xml
			Element root = new Element("params");
			Element child = new Element("param");
			child.setAttribute("name", name);
			child.setAttribute("path", path);
			child.setAttribute("filename", filename);
			root.addContent(child);
			
			// 生成XMLOutputter
			Document myDocument = new Document(root);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			
			
			Connection conn = this.getFrameconn();
			ContentDAO dao = new ContentDAO(conn);
			int newWxitemid = 0;//初始化新增应用编号
			int result = 0;//是否入库成功标识
			StringBuffer sql = new StringBuffer();
			RecordVo vo = new RecordVo("t_sys_weixin_param");

			Document document = null;
			if("add".equals(type)) {
				sql.append("select MAX(wxitemid) from t_sys_weixin_param");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next()) {
					if(StringUtils.isNotBlank(this.frowset.getString(1))) {
						newWxitemid = this.frowset.getInt(1)+1;
					}
				}
				vo.setString("wxsetid", "enterprise");//企业号标识
				vo.setInt("wxitemid", newWxitemid);
				vo.setString("wxname", wxname);
				vo.setString("appid", appid);
				vo.setString("app_secret", app_secret);
				vo.setString("url", url);
				vo.setString("app_type", app_type);
				vo.setString("description", description);
				vo.setString("str_value", outputter.outputString(myDocument));
				result = dao.addValueObject(vo);
			}else {
				String itemId = (String) this.formHM.get("itemid");//需更新的应用编号
				vo.setInt("wxitemid", Integer.parseInt(itemId));
				vo.setString("wxname", wxname);
				vo.setString("appid", appid);
				vo.setString("app_secret", app_secret);
				vo.setString("url", url);
				vo.setString("app_type", app_type);
				vo.setString("description", description);
				ArrayList  list = new ArrayList();
				list.add(itemId);
				String str_value = "";
				this.frowset = dao.search("select str_value from t_sys_weixin_param where wxitemid = ?",list);
				if(this.frowset.next())
					str_value = this.frowset.getString("str_value");
				document = PubFunc.generateDom(str_value);
				Element rootEl = document.getRootElement();
				rootEl.removeChild("param");
				rootEl.addContent(child.detach());
				vo.setString("str_value", outputter.outputString(document));
				result = dao.updateValueObject(vo);
			}
			if(result==1) {
				if(app_type == null ||app_type.trim().length()==0)
					this.formHM.put("customMenu", loadInitFuncCustomMenu(document));
				this.formHM.put("result", "1");//发布应用时用到
				if("add".equals(type)) {
					this.formHM.put("wxitemid", String.valueOf(newWxitemid));
				}
				String release = (String) this.formHM.get("release");
				if(release != null || "release".equalsIgnoreCase(release)) // 发布标识
					this.formHM.put("releaseInfo", vo);
			}else {
				this.formHM.put("result", "2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private ArrayList loadInitFuncCustomMenu(Document document){//没有配置企业号应用自定义菜单,初始化
		ArrayList list = new ArrayList();
		if(document == null){
			for(int i = 0 ; i < 3 ; i++){//主菜单共3级
				HashMap map = new HashMap();
				map.put("menuname","主菜单");
				map.put("menuurl","");
				map.put("order",(i+1));
				list.add(map);
			}
			return list;
		}
		Element rootEl = document.getRootElement();
		List childList = rootEl.getChildren("menu");
		if(childList.size() == 0){
			for(int i = 0 ; i < 3 ; i++){//主菜单共3级
				HashMap map = new HashMap();
				map.put("menuname","主菜单");
				map.put("menuurl","");
				map.put("order",(i+1));
				list.add(map);
			}
			return list;
		}
		for(int i =0 ; i < childList.size() ; i++){
			Element param = (Element) childList.get(i);
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

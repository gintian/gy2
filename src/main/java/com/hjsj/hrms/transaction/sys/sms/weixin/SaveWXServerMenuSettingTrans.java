package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
public class SaveWXServerMenuSettingTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		MorphDynaBean morphDynaBean = (MorphDynaBean) this.formHM.get("menuData");
		HashMap map = PubFunc.DynaBean2Map(morphDynaBean);
		if (map == null) {// 没获取到数据返回
			return;
		}
		int serverid = (Integer) this.formHM.get("serverid");
		List params = (List) map.get("params");
		List param = new ArrayList();
		
		MorphDynaBean serviceConfigMorphDynaBean = (MorphDynaBean) this.formHM.get("serviceConfig");
		HashMap serviceConfigHM = PubFunc.DynaBean2Map(serviceConfigMorphDynaBean);
		MorphDynaBean variableSetMorphDynaBean = (MorphDynaBean) this.formHM.get("variableSet");
		HashMap<String,String> variableSetHM = PubFunc.DynaBean2Map(variableSetMorphDynaBean);
		
		Element firstMenu = null;
		Element secondMenu = null;
		ArrayList<Object> list = new ArrayList<Object>();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
		    String templateId = (String)serviceConfigHM.get("infoTemplateId");//消息模板Id
	        String templateContent = (String)serviceConfigHM.get("templateContent");//模板内容
	        String appId = (String)serviceConfigHM.get("appid");
	        String appSecret = (String)serviceConfigHM.get("appSecret");
	        String url = (String)serviceConfigHM.get("url");
		    //组装xml 创建根节点
		    Element root = new Element("param");
			// 遍历菜单数据并返回
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i) == null) {// 判断是否有菜单数据
					continue;
				}
				//创建一级菜单节点
	            firstMenu = new Element("menu");
				MorphDynaBean tempMorphDynaBean = (MorphDynaBean) params.get(i);//一级菜单数据
				HashMap tempmap =  PubFunc.DynaBean2Map(tempMorphDynaBean);
				
				String menuname = (String) tempmap.get("menuname");// 一级菜单名
				String menuurl = (String) tempmap.get("menuurl");// 一级菜单 url 路径
				String firstorder = (String) tempmap.get("firstorder");// 排序
				
				firstMenu.setAttribute("menuname", menuname);
				firstMenu.setAttribute("menuurl", menuurl);
				firstMenu.setAttribute("order", firstorder);
				
				if(tempmap.containsKey("functions")){
					param = (List) tempmap.get("functions");
					for (int j = 0; j < param.size(); j++) {
						if (param.get(j) == null) {// 判断是否有二级菜单
							continue;
						}
						//创建二级菜单节点
						secondMenu = new Element("function");
						MorphDynaBean funcMorphDynaBean = (MorphDynaBean) param.get(j);//二级菜单数据
						HashMap tempfunction =PubFunc.DynaBean2Map(funcMorphDynaBean);
						
						String functionname = (String) tempfunction.get("functionname");// 二级菜单名
						String secondorder = (String) tempfunction.get("secondorder");// 二级菜单排序
						String functionmenu = (String)tempfunction.get("functionmenu");// 二级菜单功能号
						String functionurl = (String)tempfunction.get("functionurl");// 二级菜单功能链接路径
						functionurl = functionurl.replaceAll("&", "&amp;");
						
						secondMenu.setAttribute("functionname", functionname);
						secondMenu.setAttribute("functionmenu", functionmenu);
						secondMenu.setAttribute("functionurl", functionurl);
						secondMenu.setAttribute("order", secondorder);
						firstMenu.addContent(secondMenu);
					}
				}
				root.addContent(firstMenu);
			}
			//创建消息模板节点
			Element noticeTemplate = new Element("noticeTemplate");
			noticeTemplate.setAttribute("templateId", templateId);
			noticeTemplate.setAttribute("templateContent", templateContent);
			
			for (Map.Entry<String,String> entry : variableSetHM.entrySet()) { 
		        noticeTemplate.setAttribute(entry.getKey(), entry.getValue());
			}
			root.addContent(noticeTemplate);
			// 生成XMLOutputter
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_sys_weixin_param SET appid= ?,app_secret= ?,url= ?,str_value = ? WHERE wxitemid = ?");
			list.add(appId);
			list.add(appSecret);
			list.add(url);
			list.add(outputter.outputString(myDocument));
			list.add(serverid);
			int result = dao.update(sql.toString(), list);
			if( result > 0 ) {
			    this.formHM.put("result",true);
			} else {
			    this.formHM.put("result",false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

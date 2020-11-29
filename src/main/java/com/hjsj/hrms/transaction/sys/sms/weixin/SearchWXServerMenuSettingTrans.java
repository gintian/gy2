package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 查询菜单数据
 * @author Administrator
 *
 */
public class SearchWXServerMenuSettingTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap formMap = this.getFormHM();
		MorphDynaBean map = (MorphDynaBean) formMap.get("serverParam");
		if (map == null) {// 没获取到数据返回
			return;
		}
		int serverid = (Integer) map.get("serverid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		HashMap<String,Object> menuData = new HashMap<String,Object>();// 服务列表
		ArrayList<HashMap<String,String>> params = new ArrayList<HashMap<String,String>>();// 菜单列表
		RowSet menurs  = null;
		try {
			// 菜单列表
			String menuxml = "";//菜单xml信息
			sql.setLength(0);
			sql.append("SELECT STR_VALUE FROM t_sys_weixin_param WHERE wxitemid = ?");
			menurs = dao.search(sql.toString(),Arrays.asList(serverid));
			if (menurs.next()) {
				menuxml = (String) menurs.getString("STR_VALUE");//获取菜单信息
			}
			if (StringUtils.isEmpty(menuxml)) {
//				this.getFormHM().put("menuData", "");
//				return;
			    StringBuffer menuTempXMl = new StringBuffer();
                menuTempXMl.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
                menuTempXMl.append("<param><menu menuname=\"我要应聘\"   menutype=\"recruit\" order=\"1\"></menu>");
                menuTempXMl.append("<menu menuname=\"个人中心\"   menutype=\"selfCode\" order=\"2\"></menu>");
                menuTempXMl.append("<menu menuname=\"了解我们\"   menutype=\"other\" order=\"3\"></menu>");
                menuTempXMl.append(" </param>");
                menuxml = menuTempXMl.toString();
			}
			Document doc = null;
			doc = PubFunc.generateDom(menuxml);
			String xpath = "/param";
			XPath path = XPath.newInstance(xpath);
			Element paramsxml = (Element) path.selectSingleNode(doc);
			List<Element> menus = paramsxml.getChildren();
			List<Element> functions = null;
			for (int i = 0; i < menus.size(); i++) {//遍历所有的一级菜单节点
				Element temp =  menus.get(i);
		        HashMap param = new HashMap();
				param.put("menuname", temp.getAttributeValue("menuname"));// 获取当前循环到的一级列表名
				param.put("menutype", temp.getAttributeValue("menutype"));// 获取当前循环到的一级列表类型
				param.put("order",temp.getAttributeValue("order"));
				functions = temp.getChildren();
				ArrayList<HashMap<String,String>> paramlevel2s = new ArrayList<HashMap<String,String>>();
				for (int j = 0; j < functions.size(); j++) {//循环当前menu节点下的所有二级菜单function节点
			        HashMap<String,String> paramlevel2Param = new HashMap<String,String>();
					Element templevel2 = (Element)functions.get(j);
					paramlevel2Param.put("functionname", templevel2.getAttributeValue("functionname"));
					String functiontype = templevel2.getAttributeValue("functiontype");
                    if("recruit".equals(temp.getAttributeValue("menutype"))) {
                        StringBuffer functiontypeBuffer = new StringBuffer();
                        StringBuffer functiontypeValueBuffer = new StringBuffer();
                        //将代码值转换成代码描述
                        String[] functiontypeArray = functiontype.split(",");
                        int k = 0;
                        for (String codeitem : functiontypeArray) {
                            if(k!=0) {
                                functiontypeBuffer.append("|");
                                functiontypeValueBuffer.append(",");
                            }
                            String functiontypeDesc = AdminCode.getCodeName("35",codeitem);
                            functiontypeBuffer.append(functiontypeDesc);
                            functiontypeValueBuffer.append(codeitem);
                            k++;
                        }
                        paramlevel2Param.put("functiontype", functiontypeBuffer.toString()+"`"+functiontypeValueBuffer.toString());
                    }else {
                        paramlevel2Param.put("functiontype", functiontype);
                    }
//					paramlevel2Param.put("functiontype", templevel2.getAttributeValue("functiontype"));
					paramlevel2s.add(paramlevel2Param);
				}
				param.put("functions",paramlevel2s);
				params.add(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		    PubFunc.closeResource(menurs);
		}
		menuData.put("servertype", "recruit");// 招聘
		menuData.put("params", params);// 菜单列表
		this.getFormHM().put("serverid", serverid);
		this.getFormHM().put("menuData", menuData);

	}
}

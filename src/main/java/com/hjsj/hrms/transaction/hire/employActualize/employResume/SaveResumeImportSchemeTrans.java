package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: SaveResumeImportSchemeTrans 
 * @Description: TODO保存对应指标集到数据库xml
 * @author xmsh
 * @date 2013-12-27 上午11:34:22 
 *
 */
public class SaveResumeImportSchemeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		String xml;
		//这个参数是从jspform下取得值所以过滤处理一下
		String itemID = (String) hm.get("itemID");
		itemID = PubFunc.hireKeyWord_filter(itemID);
		String secitemID = (String) hm.get("secitemID");
		secitemID = PubFunc.hireKeyWord_filter(secitemID);
		String mode = (String) hm.get("mode");
		mode = PubFunc.hireKeyWord_filter(mode);
		ArrayList codelist = (ArrayList) hm.get("codelist");
		Document doc;

		try {

			ResumeImportSchemeXmlBo xmlBo = new ResumeImportSchemeXmlBo(
					this.frameconn);

			xml = xmlBo.getXml();
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme");

			Element scheme = (Element) xPath.selectSingleNode(doc);
			List list = scheme.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				if (element.getName() == "identifyfld") {
					scheme.getChild("identifyfld").setText(itemID);
				}
				if (element.getName() == "sencondfld") {
					scheme.getChild("sencondfld").setText(secitemID);
				}
				if (element.getName() == "imptype") {
					scheme.getChild("imptype").setText(mode);
				}
			}

			for (int i = 0; i < codelist.size(); i++) {
				LazyDynaBean contentBean = (LazyDynaBean) codelist.get(i);
				String resumeset = (String) contentBean.get("resumeset");
				XPath xPath1 = XPath
						.newInstance("/scheme/sets/set[@resumeset='"
								+ resumeset + "']");
				Element set = (Element) xPath1.selectSingleNode(doc);
				String ehrset = (String) contentBean.get("ehrset");
				String selected = (String) contentBean.get("selected");
				if (!selected.equalsIgnoreCase(ehrset)) {
					// 选中改变
					set.setAttribute("ehrset", selected);
					// 把指标对应置为空
					List menu = set.getChild("menus").getChildren();
					if (menu.size() > 0) {
						for (int j = 0; j < menu.size(); j++) {
							Element child = (Element) menu.get(j);
							child.setAttribute("valid", "0");
							child.setAttribute("ehrfld", "");
						}
					} else {

					}

					// 清空已经指标集下已经对应的代码型指标的codeset节点
					XPath xPath2 = XPath.newInstance("/scheme/codesets");
					XPath xPath3 = XPath
							.newInstance("/scheme/codesets/codeset[@resumeset='"
									+ resumeset + "']");

					Element codesets = (Element) xPath2.selectSingleNode(doc);
					List codeset = xPath3.selectNodes(doc);

					for (int j = 0; j < codeset.size(); j++) {
						Element element = (Element) codeset.get(j);
						codesets.removeContent(element);
					}

				}

			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent(" ");
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(format);
			XMLOut.output(doc, bo);

			String StrValue = bo.toString();
			xmlBo.UpdateConstantXml(StrValue); // 更新数据库中的xml

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

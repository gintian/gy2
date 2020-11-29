package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: SetResumeItemTrans 
 * @Description: TODO保存代码对应设置参数
 * @author xmsh
 * @date 2013-12-27 上午11:36:03 
 *
 */
public class SetResumeItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		String xml;

		Document doc;
		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(
				this.getFrameconn());

		try {
			String resumeID = (String) hm.get("resumeID");
			String resumeitem = (String) hm.get("list");
			resumeitem = PubFunc.keyWord_reback(resumeitem);
			if(resumeitem==null||resumeitem.trim().length()<=0){
				throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.codeitem.and.no.corresponding"));
			}

			xml = resumeImportSchemeXmlBo.getXml();			//获取xml
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/codesets/codeset[@resumefld='"+ resumeID + "']/codeitems");
			Element codeitems = (Element) xPath.selectSingleNode(doc);
			List child = codeitems.getChildren();

			String[] resumeArray = resumeitem.split("\\|");
			for (int i = 0; i < resumeArray.length; i++) {
				String item = resumeArray[i];
				String[] map = item.split("=");
				String ehritemid = map[0];
				String resumeitemid = "";
				if (map.length == 2) {
					resumeitemid = map[1];
				}
				for (int j = 0; j < child.size(); j++) {
					Element element = (Element) child.get(j);
					if (element.getAttributeValue("ehritemid").equals(ehritemid)) {
						element.setAttribute("resumeitemid", resumeitemid);
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

			resumeImportSchemeXmlBo.UpdateConstantXml(StrValue);		//更新数据中的xml

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}

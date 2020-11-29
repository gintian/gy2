package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
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
 * @ClassName: AutoCorrespondTrans 
 * @Description: TODO代码自动对应
 * @author xmsh
 * @date 2013-12-27 上午11:30:30 
 *
 */
public class AutoCorrespondTrans extends IBusiness {

	public void execute() throws GeneralException {
		String xml;
		HashMap hm = (HashMap) this.getFormHM();

		Document doc;
		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(
				this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.frameconn);

		try {
			String resumeID = (String) hm.get("resumeID");
			String commonvalue = (String) hm.get("commonvalue");
			String itemname = "";

			String[] value = commonvalue.split(",");

			xml = resumeImportSchemeXmlBo.getXml();
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath
					.newInstance("/scheme/codesets/codeset[@resumefld='"
							+ resumeID + "']/codeitems");
			Element codeitems = (Element) xPath.selectSingleNode(doc);
			String ehrfld = codeitems.getParentElement().getAttributeValue(
					"ehrfld");
			this.frowset = dao
					.search("select codesetid from FIELDITEM where UPPER(ITEMID)='"
							+ ehrfld.toUpperCase()
							+ "' and UPPER(CODESETID)<>'0'");
			String codesetid = "";
			if (frowset.next()) {
				codesetid = frowset.getString("codesetid");

			}
			List child = codeitems.getChildren(); // 取得codeitem

			for (int i = 0; i < child.size(); i++) {
				Element element = (Element) child.get(i);
				String ehritemid = element.getAttributeValue("ehritemid");

				itemname = AdminCode.getCodeName(codesetid, ehritemid);
				for (int j = 0; j < value.length; j++) {

					if (value[j].contains(itemname)
							|| itemname.contains(value[j])) {
						String str = element.getAttributeValue("resumeitemid");
						str = PubFunc.keyWord_reback(str);
						if ("".equals(str) || str == null) {
							if(!"".equals(value[j])){
								element.setAttribute("resumeitemid", value[j]+ ";");
							}
							
						} else {
							if (!str.contains(value[j] + ";")) {
								element.setAttribute("resumeitemid", str
										+ value[j] + ";");
							}
						}
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
			resumeImportSchemeXmlBo.UpdateConstantXml(StrValue);		//更新数据库中的xml

		} catch (Exception e) {
//			e.printStackTrace();
			
			throw GeneralExceptionHandler.Handle(new Exception("没有代码对应项,请返回选择指标对应代码型指标!"));
		}
	}

}

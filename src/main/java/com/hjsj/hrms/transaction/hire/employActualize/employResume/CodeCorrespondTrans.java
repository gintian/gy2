package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: CodeCorrespondTrans
 * @Description: TODO代码对应
 * @author xmsh
 * @date 2013-12-27 上午11:31:30
 * 
 */
public class CodeCorrespondTrans extends IBusiness {

	public void execute() throws GeneralException {
		String xml;

		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(this.getFrameconn());

		HashMap hm1 = (HashMap) this.getFormHM().get("requestPamaHM");
		HashMap hm = (HashMap) this.getFormHM();
		Document doc;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);

		LazyDynaBean bean = new LazyDynaBean();

		ArrayList codeList = new ArrayList();
		ArrayList resumeList = new ArrayList();

		EmployResumeBo bo = new EmployResumeBo(this.getFrameconn());

		String resumeID = (String) hm1.get("resumeID");
		String from_flag = (String) hm1.get("from_flag");
		String commonvalue = "";
		resumeID = SafeCode.decode(resumeID);

		try {

			xml = resumeImportSchemeXmlBo.getXml();
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/codesets");
			Element codesets = (Element) xPath.selectSingleNode(doc);
			List codesetList = codesets.getChildren();
			// 检查codeset中的指标在resumeFld.xml中是否存在
			for (int i = 0; i < codesetList.size(); i++) {
				Element element = (Element) codesetList.get(i);

				String resumeset = element.getAttributeValue("resumeset");

				resumeImportSchemeXmlBo.checkMenu(resumeset);
			}
			// 重新获得xml
			xml = resumeImportSchemeXmlBo.getXml();
//			
//			String pathFile = System.getProperty("java.io.tmpdir");
//			pathFile += "\\" + "theXML.XML";
//			File file = new File(pathFile);
//			if(file.exists()){
//				file.delete();
//			}
//			BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
//			output.write(xml);
//			output.close();
			
			doc = PubFunc.generateDom(xml);
			xPath = XPath.newInstance("/scheme/codesets");
			codesets = (Element) xPath.selectSingleNode(doc);
			codesetList = codesets.getChildren();
			for (int i = 0; i < codesetList.size(); i++) {
				Element element = (Element) codesetList.get(i);
				String ehrfld = element.getAttributeValue("ehrfld");
				String resumefld = element.getAttributeValue("resumefld");
				String resumeset = element.getAttributeValue("resumeset");
				String itemdesc = null;

				rs = dao.search("select itemdesc from fielditem where itemid ='" + ehrfld.toUpperCase() + "'");
				if (rs.next()) {
					itemdesc = rs.getString("itemdesc");
				}

				CommonData data = new CommonData(resumefld, resumefld + " -> " + ehrfld.toUpperCase() + ":" + itemdesc);
				codeList.add(data);// 选择代码项

				if (resumeID == null || "".equals(resumeID)) {
					resumeID = resumefld;
				}

				// 获得代码项
				if (resumefld.equals(resumeID)) {

					String codesetid = null;
					this.frowset = dao.search("select codesetid from FIELDITEM where UPPER(ITEMID)='" + ehrfld.toUpperCase() + "' and UPPER(CODESETID)<>'0'");
					if (frowset.next()) {
						codesetid = frowset.getString("codesetid");

					}

					// 取得commonvalue
					ArrayList resumeXmlList = resumeImportSchemeXmlBo.getCommonvalueXmlList(resumeset, resumefld);
					for (int k = 0; k < resumeXmlList.size(); k++) {
						bean = (LazyDynaBean) resumeXmlList.get(k);
						if (bean.get("commonvalue") != null) {
							commonvalue = (String) bean.get("commonvalue");
						}

					}

					Element codeitems = element.getChild("codeitems");
					List codeitemList = codeitems.getChildren();

					for (int j = 0; j < codeitemList.size(); j++) {
						HashMap resumemap = new HashMap();
						Element codeitem = (Element) codeitemList.get(j);
						String ehritemid = codeitem.getAttributeValue("ehritemid"); // 人员库代码编号
						String resumeitemid = null;
						if (codeitem.getAttribute("resumeitemid") != null) {
							resumeitemid = codeitem.getAttributeValue("resumeitemid"); // 简历信息
						} else {
							resumeitemid = ""; // 简历信息
						}

						String itemname = null; // 人员库代码名称
						itemname = AdminCode.getCodeName(codesetid, ehritemid);

						resumemap.put("resumeID", resumeID);
						resumemap.put("input", bo.getInputHtml(ehritemid, resumeitemid));
						resumemap.put("itemname", itemname);
						resumemap.put("resumeitemid", resumeitemid);
						resumemap.put("ehritemid", ehritemid);
						resumeList.add(resumemap);

					}
				}

			}
			hm.put("commonvalue", commonvalue);
			hm.put("resumeID", resumeID);
			hm.put("clist", codeList);
			hm.put("resumeList", resumeList);
			hm.put("from_flag", from_flag);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

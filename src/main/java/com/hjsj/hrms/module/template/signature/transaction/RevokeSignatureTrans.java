package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;

public class RevokeSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String tab_id = (String) this.getFormHM().get("tab_id");
		String signxml = (String) this.getFormHM().get("signxml");
		String PageID = (String) this.getFormHM().get("PageID");
		String GridNO = (String) this.getFormHM().get("GridNO");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		Document doc = null;
    	RowSet rset = null;
		try {
			XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
			doc = PubFunc.generateDom(signxml);
			List<Element> elelist = doc.getRootElement().getChildren();
			String deletesql = "";
			ArrayList paramList = new ArrayList();
			if(elelist.size()>0) {
				for(int i=0;i<elelist.size();i++) {
					Element ele = elelist.get(i);
					List<Element> ele2list = ele.getChildren("item");
					Element ele2=(Element)ele2list.get(0);
					String SignatureHtmlID = ele2.getAttributeValue("SignatureHtmlID");
					String PageID_ = ele2.getAttributeValue("PageID");
					String GridNO_ = ele2.getAttributeValue("GridNO");
					if((PageID+"_"+GridNO).equals(PageID_+"_"+GridNO_)) {
						String documentid = tab_id+"_"+PageID+"_"+GridNO;
						deletesql="delete from htmlsignature where signatureid=? and documentid=?";
						paramList.add(SignatureHtmlID);
						paramList.add(documentid);
						dao.delete(deletesql, paramList);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
}

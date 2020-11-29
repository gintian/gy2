package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;



public class ReceiveSelectTrans extends IBusiness {

	public ReceiveSelectTrans() {
		super();
	}

	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//报表样式文件
			FormFile file = (FormFile) this.getFormHM().get("file");
			
			//文件到DOM
			byte[] data = file.getFileData();
			String source = new String(data);
			StringReader sr = new StringReader(source);
			InputSource isource = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = null;
			doc = builder.parse(isource);
			
			
			NodeList tablist = doc.getElementsByTagName("tab");
 
			String name = "";
			String tabid = "";
			ArrayList selectVoList = new ArrayList();
			for (int i = 0; i < tablist.getLength(); i++) {
				Element tabElement = (Element) tablist.item(i);
				
				name = tabElement.getAttribute("name"); //表名
				tabid = tabElement.getAttribute("tabid");//表ID
				
				ReportData rd = new ReportData();//封装报表信息
				rd.setTabid(tabid);
				rd.setTabName(name);
				ResultSet rs = dao.search("select * from tname where tabid="+ tabid);
				//ResultSet rs = dao.search("select * from tname where name='"+ name+"'");
				if (rs.next()) {
					rd.setRepeat(true);
				} else {
					rd.setRepeat(false);
				}
				
//				System.out.println(rd.getTabid()+ rd.isAppendOrModify() + rd.getTabName() );
				selectVoList.add(rd);
			}
			this.getFormHM().put("selectVoList", selectVoList);
		} catch (Exception e) {
			Exception e1 = new Exception(ResourceFactory.getProperty("edit_report.info4")+"！");
			throw GeneralExceptionHandler.Handle(e1);
		}
	}
}

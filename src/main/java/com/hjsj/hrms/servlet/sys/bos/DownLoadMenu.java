package com.hjsj.hrms.servlet.sys.bos;

import com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm;
import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class DownLoadMenu extends HttpServlet {

	public DownLoadMenu() {
		super();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String ext = "xml";
		ServletOutputStream sos = response.getOutputStream();
		String name = "menu";
		InputStream inputStream =null;
		//备份menu.xml,session中存在就从session中取出,不存在找绝对路径
		MenuMainForm menuMainForm=(MenuMainForm)request.getSession().getAttribute("menuMainForm"); 
		Document doc=menuMainForm.getMenu_dom(); 
		String parentid=menuMainForm.getParentid();
		if(doc!=null){
			XMLOutputter outputter = new XMLOutputter();
			if("-1".equals(parentid)){
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xml =outputter.outputString(doc);
				inputStream = new ByteArrayInputStream(xml.getBytes());
			}else{//dml 2011-04-19
				String xpath = "//menu[@id=\"" + parentid.toLowerCase() + "\"]";
				XPath xPath;
				try {
					xPath = XPath.newInstance(xpath);
					Element menu = (Element) xPath.selectSingleNode(doc);
					if (menu == null) {
						
					}else{
						Element pa=menu.getParentElement();
						Element hr=new Element("hrp_menu");
						String parent=pa.getAttributeValue("id");
						if(parent!=null&&parent.length()!=0){
							hr.setAttribute("parentid", parent);
						}else{
							
						}
						Element doc1=(Element)menu.clone();
						hr.addContent(doc1);
						Format format = Format.getPrettyFormat();
						format.setEncoding("UTF-8");
						outputter.setFormat(format);
						Document d=new Document(hr);
						String xml =outputter.outputString(d);
						inputStream = new ByteArrayInputStream(xml.getBytes());
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				}
	        	
			}
		 
		}else{
			MenuMainBo bo = new MenuMainBo();
			inputStream = bo.getInputStreamFromjar();
		}
		try {
		
			
					response.setContentType("text/xml");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + name +"."+ ext + "\"");
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) != -1) {
					sos.write(buf, 0, len);
				}
				if(inputStream!=null)
					inputStream.close();
		}
		finally {
			sos.close();
			PubFunc.closeIoResource(inputStream);
		}

	}

}

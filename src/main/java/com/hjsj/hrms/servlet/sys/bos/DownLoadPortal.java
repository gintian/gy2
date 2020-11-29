package com.hjsj.hrms.servlet.sys.bos;

import com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm;
import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class DownLoadPortal extends HttpServlet {

	public DownLoadPortal() {
		super();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String ext = "xml";
		ServletOutputStream sos = response.getOutputStream();
		String name = "portal";
		InputStream inputStream =null;
		try {
	        
        		//备份menu.xml,session中存在就从session中取出,不存在找绝对路径
        		PortalMainForm portalMainForm=(PortalMainForm)request.getSession().getAttribute("portalMainForm"); 
        		Document doc=portalMainForm.getPortal_dom(); 
        		if(doc!=null){
        		
        		 XMLOutputter outputter = new XMLOutputter();
        			Format format = Format.getPrettyFormat();
        			format.setEncoding("UTF-8");
        			outputter.setFormat(format);
        			String xml =outputter.outputString(doc);
        			 inputStream = new ByteArrayInputStream(xml.getBytes());
        		 
        		}else{
        			PortalMainBo bo = new PortalMainBo();
        			inputStream = bo.getInputStreamFromjar();
        		}
		
			
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
		    PubFunc.closeResource(inputStream);
			sos.close();
		}

	}

}

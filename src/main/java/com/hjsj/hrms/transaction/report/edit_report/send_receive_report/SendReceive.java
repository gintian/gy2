package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

public class SendReceive extends HttpServlet {

	

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		OutputStream os = response.getOutputStream();
		String tabids = request.getParameter("tabids");
		Connection con = null;
		try {
			con = AdminDb.getConnection();
		
			String[] tabidArray = tabids.split(",");
			
			SendXml xml = new SendXml(con, tabidArray, null);
			
			Document doc = xml.createUpDisk();
			
			String outputstr=null;
			try {
				/***
				Transformer transformer = TransformerFactory.newInstance()
						.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(new DOMSource(doc),
						new StreamResult(stringWriter));
						**/
			
				XMLSerializer serializer = new XMLSerializer();
		    	ByteArrayOutputStream output=new ByteArrayOutputStream();
		    	serializer.setOutputByteStream(output);
				OutputFormat out=new OutputFormat();
				out.setEncoding("UTF-8");
				serializer.setOutputFormat(out);
				serializer.serialize(doc);
			     outputstr=new String(output.toByteArray(),"UTF-8");		    
				
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			}
			
			String fileName = ResourceFactory.getProperty("edit_report.reportType")+".txt";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment;   filename=\""
					+ URLEncoder.encode(fileName,"UTF-8") +   "\"");
			os.write(outputstr.getBytes()); 
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
			try{
				if (con != null){
					con.close();
				}
			}catch (SQLException sql){
				sql.printStackTrace();
			}
		}
	}

	/**
	 * Constructor of the object.
	 */
	public SendReceive() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
}

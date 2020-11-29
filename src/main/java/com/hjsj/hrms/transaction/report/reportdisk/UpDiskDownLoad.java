package com.hjsj.hrms.transaction.report.reportdisk;

import com.hjsj.hrms.interfaces.decryptor.Des;
import com.hrms.frame.utility.AdminDb;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class UpDiskDownLoad extends HttpServlet {

	public UpDiskDownLoad() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection con = null;
		try {
			
			con = AdminDb.getConnection();

			// 1：编辑没上报表(单一表) 2：编辑上报后的表(可以有子单位)
			boolean contain_child = true;
			String operateObject = request.getParameter("operateObject");			
			if ("1".equals(operateObject)) {//编辑过程中的上报
				contain_child = false;
			}
			if ("2".equals(operateObject)) {
				contain_child = true;
			}
			
			// 用户输入的填报单位code
			String input_unitcode = request.getParameter("unitcode");

			// 用户输入的填报单位name 上报盘的文件名
			String input_unitname = request.getParameter("unitname");
			//System.out.println("用户输入的填表单位名称="+input_unitname);
			

			// 设置http头部声明为下载模式
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					"attachment;   filename=\"" + input_unitname + ".txt\"");
			request.setCharacterEncoding("GB2312");

			input_unitname = new String(input_unitname.getBytes("ISO-8859-1"),"GBK");
			
			//生成上报盘的报表
			String tabid_str = request.getParameter("tabid");
			//System.out.println("上报的报表ID="+tabid_str);
			
			//要上报的报表列表
			String[] tabid_array = tabid_str.split("/");

			//当前用户名
			String username = request.getParameter("username");
			username = new String(username.getBytes("ISO-8859-1"));			
			//System.out.println("当前用户名="+username);
		
			//用户实际对应的unitcode
			String db_unitcode = request.getParameter("db_unitcode");			
			//System.out.println("当前用户实际对应的填表单位编码=" + db_unitcode);
			
			//报表汇总中上报( 包含基层单位1 /只报汇总单位2 )
			String scope = request.getParameter("scope");
			if("2".equals(scope)){
				//与编辑报表过程中上报相同
				contain_child = false;
			}
			
			UpDiskDownLoad.unitUpDisk(con, input_unitcode, input_unitname, username,
					tabid_array, contain_child, response.getOutputStream(),
					db_unitcode);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
			
		} catch (Exception e) {
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
	 * 
	 * @param con 数据库链接
	 * @param input_unitcode  用户输入的填表单位编码
	 * @param input_unitname  用户输入的填表单位名称
	 * @param username        当前用户名
	 * @param tabid_array     上报的报表集合
	 * @param contain_child   false 编辑报表中上报 true 报表汇总中报表上报
	 * @param outstream		  输出流
	 * @param dbunitcode	  当前用户对应的填表单位
	 */
	public static void unitUpDisk(Connection con, String input_unitcode,
			String input_unitname, String username, String[] tabid_array,
			boolean contain_child, OutputStream outstream, String dbunitcode) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			//当前用户对应的填表单位集合(包括子节点)
			List unitlist = UpDisk.getUserUnitCode(con, username, contain_child);
			
			// 当前报送的是哪个填报单位
			String db_unitcode = (String) unitlist.get(0);		
			db_unitcode = dbunitcode;
			
			
			
			boolean flg = false;//顶层节点(units)为false,设置单位编码与单位名称
			UpDisk updisk = null;			
			UpDisk.tabidArray = tabid_array; //要上报的单位集合
			
			Element element = doc.createElement("units");
			doc.appendChild(element);
			
			Iterator iterat = unitlist.iterator();
			
			while (iterat.hasNext()) {				
				// XML文档用来存储所有报表结点<reports>
				Document doc_reports = null;
				
				//规范单位编码
				String unitcode = (String) iterat.next();
				boolean flag = false;
				if(unitcode==null|| "".equals(unitcode)){
					unitcode= input_unitcode;
					flag =true;
				}
				String replace_unitcode = unitcode.replaceFirst(db_unitcode,input_unitcode);			
				String parentcode = UpDisk.getParentUnitcode(con, unitcode);							
				parentcode = parentcode.replaceFirst(db_unitcode,input_unitcode);
				
				for (int i = 0; i < tabid_array.length; i++) {
					if ("".equals(tabid_array[i])){
						continue;
					}
					String unitname = "";
					if (!flg) {//units节点的上报单位名称
						unitname = input_unitname;//用户输入的上报单位名称
					} else {
						unitname = UpDisk.getUnitName(con, unitcode);
					}
					updisk = new UpDisk(con, tabid_array[i], unitname ,username, replace_unitcode, 
							parentcode, doc_reports, unitcode);	
					if(flag){
						doc_reports = updisk.createUpDisk("1");
					}else
					doc_reports = updisk.createUpDisk();
				}
				
				NodeList nodelist = doc_reports.getElementsByTagName("hrp_reports");
				Node hrp_reports = nodelist.item(0);

				// 将数据库中的填报单位ID转换后写入结点
				if (!flg) {
					//<units unitcode="11" unitname="tttt">
					element.setAttribute("unitcode", replace_unitcode);
					element.setAttribute("unitname", input_unitname);
					flg = true;
				}
				element.appendChild(doc.importNode(hrp_reports, true));
			}
			
			try {
				//CipherBox cipherBox = new CipherBox("Cipher_h_j_s_j");
				Des des = new Des();
				/*注掉原因：Transformer是抽象类，没有weblogic.jar 会出问题
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StringWriter stringWriter = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
				//System.out.println(stringWriter.toString());
				outstream.write(stringWriter.toString().getBytes());
				*/
			
			    	XMLSerializer serializer = new XMLSerializer();
			    	ByteArrayOutputStream output=new ByteArrayOutputStream();
			    	serializer.setOutputByteStream(output);
			        //Insert your PipedOutputStream here instead of System.out!			
					OutputFormat out=new OutputFormat();
					out.setEncoding("UTF-8");
					serializer.setOutputFormat(out);
					serializer.serialize(doc);
				    String outputstr=new String(output.toByteArray(),"UTF-8");
				    byte[] b= outputstr.getBytes();
				    outstream.write(b,0,b.length);
			
				// 输出加密文本
				// outstream.write(stringWriter.toString().getBytes());
				//outstream.write(des.EncryStr(stringWriter.toString(),"Cipher_h_j_s_j"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}

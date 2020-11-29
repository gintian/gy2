package com.hjsj.hrms.businessobject.sys.setup.findfile.parse;

import com.hjsj.hrms.businessobject.sys.setup.findfile.Findfile;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.*;

public class ParseServerxml {

	
	private static Document doc;
	
	public  ParseServerxml(){
		super();
		
		
	}
//	修改数据连接池用户名/密码
	public static String updateDBpool(String username,String password) throws JDOMException, IOException{
		return updateDBpool(username,password,null,null,null);
	}
//	修改数据库连接池 类型，数据库地址，启动程序
	public static String updateDBpool(String type,String url,String driverClassName) throws JDOMException, IOException{
		return updateDBpool(null,null,type,url,driverClassName);
	}
//	修改数据连接池用户名/密码/类型，数据库地址，启动程序
	public static String  updateDBpool(String username,String password,String type,String url,String driverClassName) throws JDOMException, IOException{
		return updateDBpool(username,password,type,url,driverClassName,null,null,null,null,null);
	}
//	修改数据连接池最大连接数量，连接空闲时间，最大等待时间，连接池名字
	public static String updateDBpool(String maxActive,String maxIdle,String maxWait,String name) throws JDOMException, IOException{
		return updateDBpool(null,null,null,null,null,null,maxActive,maxIdle,maxWait,name);
	}
//	修改数据连接池所有信息并保存文件
	public static String  updateDBpool(String username,String password,String type,
			String url,String driverClassName,String auth,
			String maxActive,String maxIdle,String maxWait,String name) throws JDOMException, IOException{
		InputStream inputStream = null;
		String outString = "";
		try{
			String serverxmlpath=Findfile.findPath("server.xml");
			File sf=new File(serverxmlpath);
			inputStream = new FileInputStream(sf);
			doc= PubFunc.generateDom(inputStream);
			StringBuffer sbnodename=new StringBuffer();
			sbnodename.append("/Server/Service/Engine/Host/Context/Resource[@auth=");
			sbnodename.append("'Container'");
			sbnodename.append("]");
			XPath xPath = XPath.newInstance(sbnodename.toString());
			Element dbresource = (Element) xPath.selectSingleNode(doc);
			if(auth!=null) {
                dbresource.getAttribute("auth").setValue(auth);
            }
			if(driverClassName!=null) {
                dbresource.getAttribute("driverClassName").setValue(driverClassName);
            }
			if(maxActive!=null) {
                dbresource.getAttribute("maxActive").setValue(maxActive);
            }
			if(maxIdle!=null) {
                dbresource.getAttribute("maxIdle").setValue(maxIdle);
            }
			if(maxWait!=null) {
                dbresource.getAttribute("maxWait").setValue(maxWait);
            }
			if(name!=null) {
                dbresource.getAttribute("name").setValue(name);
            }
			if(username!=null) {
                dbresource.getAttribute("username").setValue(username);
            }
			if(password!=null) {
                dbresource.getAttribute("password").setValue(password);
            }
			if(type!=null) {
                dbresource.getAttribute("type").setValue(type);
            }
			if(url!=null) {
                dbresource.getAttribute("url").setValue(url);
            }
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("utf-8");
			outputter.setFormat(format);
			outString=outputter.outputString(doc);
//		System.out.println(outString);
			write2hd(serverxmlpath,outString);
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(inputStream);
		}

		return outString;
	}
	private static void write2hd(String servexmlrpath,String xmlstr) throws IOException{
		File f=new File(servexmlrpath);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			fos.write(xmlstr.getBytes());
			fos.flush();			
		}finally{
			PubFunc.closeIoResource(fos);
		}
	}
	public static void main(String[] args){
		try {
			updateDBpool(null,null,null,null,null,null,null,null,null, null);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

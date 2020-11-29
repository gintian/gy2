package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.MacTool;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.lowagie.text.DocListener;
import org.apache.lucene.index.IndexWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.zip.ZipFile;

/**
 * 
 * <p>Title: Tools </p>
 * <p>Description: 工具类</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-11-12 下午2:49:49</p>
 * @author yangj2
 * @version 1.0
 */
public class Tools {
	
	private static HashMap funcNode=new HashMap();
	
	/**
	 * 
	 * @Title: encryptFile   
	 * @Description: 文件加密    
	 * @param filename 文件名
	 * @return String
	 */
	public static String encryptFile(String fileName) {
		try {
			Class pubFunc = PubFunc.class;
			// 判断类是否有加密方法，没有则跳过
			Method method = pubFunc.getDeclaredMethod("encrypt", new Class[] { String.class });
			Object[] list = {fileName};
			// 执行方法
			fileName =  (String) method.invoke(new PubFunc(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	/**
	 * 关闭与数据源的连接,释放资源.是closeIoResource和closeDbObj的并集
	 * @param resource 可以是IO流也可以是数据库资源
	 * 
	 */
	public static void closeResource(Object dataSource) {
		closeIoResource(dataSource);
		closeDbObj(dataSource);
	}
	
	/**
	 * @Title closeIoResource
	 * @Description 关闭IO资源 
	 * @param obj  支持类型：
	 *           	  InputStream、OutputStream、Reader、Writer
	 *           	  ZipFile(子类包括:java.util.jar.JarFile)
	 *           	  DocListener(子类包括:PdfWriter,DocWriter,com.lowagie.text.Document)
	 *       	      IndexWriter
	 *
	 * @return void
	 */
	public static void closeIoResource(Object obj) {
		if (obj == null) {
			return;
		}
		try {
			if (obj instanceof InputStream) {
				((InputStream) obj).close();
			} else if (obj instanceof OutputStream) {
				((OutputStream) obj).close();
			} else if (obj instanceof Reader) {
				((Reader) obj).close();
			} else if (obj instanceof Writer) {
				((Writer) obj).close();
			} else if (obj instanceof ZipFile) {
				((ZipFile) obj).close();
			} else if (obj instanceof DocListener) {
				((DocListener) obj).close();
			} else if (obj instanceof IndexWriter) {
				((IndexWriter) obj).close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库连接一系列对象 
	 * @param obj  支持类型：connection、rowset、resultset、statement、preparedstatement
	 * @return boolean 是否成功
	 */
	public static boolean closeDbObj(Object obj){
		boolean isClosed = true;		
		if(obj==null)
			return isClosed;		
		try{
			if(obj instanceof Connection){
				((Connection)obj).close();
			}else if(obj instanceof RowSet){
				((RowSet)obj).close();
			}else if(obj instanceof ResultSet){
				((ResultSet)obj).close();
			}else if(obj instanceof Statement){
				((Statement) obj).close();
			}else if(obj instanceof PreparedStatement){
				((PreparedStatement) obj).close();
			}else{
				isClosed = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			isClosed = false;
		}	
		return isClosed;
	}
	
	/**
	 * 
	 * @Title: getPhotoPath   
	 * @Description: 获取用户头像路径   
	 * @param dbpre
	 * @param a0100
	 * @throws GeneralException 
	 * @return String
	 */
	public static String getPhotoPath(String dbpre, String a0100) throws GeneralException {
		StringBuffer photourl = new StringBuffer();
		try {
			String filename = ServletUtilities.createPhotoFile(dbpre + "A00", a0100, "P", null);
			if (filename == null || filename.length() == 0) {
				photourl.append("/images/photo.jpg");	
			} else {
				photourl.append("/servlet/DisplayOleContent?filename=");
				photourl.append(filename);
			}
		} catch (Exception e) { 
			throw GeneralExceptionHandler.Handle(e);
		}
		return photourl.toString();
	}
	
	/**
	 * 获取MAC地址
	 * @return
	 * @throws Exception 
	 */
	public static String getMACAddress() throws Exception { 
		MacTool macTools = new MacTool();
		return macTools.getMacAddress();
	}
	
	/**
	 * 判断function.xml有无此节点
	 */
	public boolean hasFuncNode(String funcid){
		InputStream in = null;
        boolean flag = false;
        try
        {
        	if(funcNode.containsKey(funcid)){
        		String v = (String)funcNode.get(funcid);
        		return "true".equals(v);
        	}
        	
        	in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
	        Document doc = PubFunc.generateDom(in);
	        String xpath = "//function[@id=\"" + funcid + "\"]";
	        XPath xpath_ = XPath.newInstance(xpath);
	        Element ele = (Element) xpath_.selectSingleNode(doc);
	        if(ele!=null){
	        	funcNode.put(funcid, "true");
	        	flag = true;
	        }else{
	        	funcNode.put(funcid, "false");
	        }
        }catch(Exception ee)
        {
            ee.printStackTrace();
        }
        finally
        {
            if(in!=null){
            	try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
		return flag;
	}
	
}

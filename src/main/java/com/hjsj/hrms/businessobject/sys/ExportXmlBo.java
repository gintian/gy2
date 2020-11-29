package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.businessobject.sys.job.ExportFTPThread;
import com.hjsj.hrms.interfaces.gz.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import javax.sql.RowSet;
import java.io.*;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ExportXmlBo extends ConstantXml{

	public ExportXmlBo(Connection conn, String constant) {
		super(conn, constant);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 导出xml文件并打包上传到ftp服务器
	 * @param flag 判断是否上传到ftp服务器true为是 false为否
	 */

	public void export(String flag,String path,String filename){
		SchedulerFactory schedFact = null; 
		try {
			if("true".equals(flag)){
			   schedFact = new StdSchedulerFactory();
			   Scheduler sched  = schedFact.getScheduler();
			   sched.start();
			   JobDetail jobDetail = new JobDetail("exportftp","exportftp",ExportFTPThread.class);
			   jobDetail.getJobDataMap().put("type","FULL");
			   SimpleTrigger trigger = new SimpleTrigger("exportftp","exportftp",1,60*60*1000);
			   sched.scheduleJob(jobDetail,trigger);
			}

			String separator = System.getProperty("file.separator");

			ArrayList baseElementsName = (ArrayList)elementName("/root/base","name");
			ArrayList baseElementsFlag = (ArrayList)elementName("/root/base","flag");
			ArrayList Elements = (ArrayList)elementValue("/root/base/subset");
			ArrayList codexml = (ArrayList)elementName("/root","code");
			ArrayList fieldxml = (ArrayList)elementName("/root","field");
			ArrayList transcodexml = (ArrayList)elementName("/root","transcode");
			ArrayList strtoutfxml = (ArrayList)elementName("/root","strtoutf");
			
			String code = "false";
			String field = "false";
			String transcode = "false";
			String strtoutf = "false";
			if(codexml.size()>0){
				code = codexml.get(0).toString(); 
				field = fieldxml.get(0).toString(); 
				transcode = transcodexml.get(0).toString();
				strtoutf = strtoutfxml.get(0).toString();
			}
			if("true".equals(code)){
				exportCodeitemXML(strtoutf);
				exportCodesetXML(strtoutf);
				exportCodesetDTD();
				exportCodeitemDTD();
			}

			if("true".equals(field)){
				exportFieldsetDTD();
				exportFieldsetXML(strtoutf); 
			}

			if(Elements.size()>0){
				for(int i=0;i<baseElementsName.size();i++){
					for(int j=0;j<Elements.size();j++){
						if(Elements.get(j).toString().substring(0,1).equals(baseElementsFlag.get(i))){
							if("A".equals(baseElementsFlag.get(i))){
								String file = baseElementsName.get(i)+Elements.get(j).toString().substring(0,3);
								exportBaseXML(file,Elements.get(j).toString(),transcode,strtoutf);
								exportBaseDTD(file,Elements.get(j).toString());
							} 
							if("B".equals(baseElementsFlag.get(i))|| "K".equals(baseElementsFlag.get(i))){
								String file = Elements.get(j).toString().substring(0,3);
								exportBaseXML(file,Elements.get(j).toString(),transcode,strtoutf);
								exportBaseDTD(file,Elements.get(j).toString());
							}
						}
					}
				}
			}

			//将export目录下的文件打包生成file文件名
			if(Elements.size()>0|| "true".equals(code)|| "true".equals(field)){
				exportJar(filename,path,"export");
	
				//打包完后将export目录下的文件删除
				File zipfile = new File(path+separator+"export");
				if(zipfile.exists()){
					File filelist[] = zipfile.listFiles();
					for(int i=0;i<filelist.length;i++){
						if(filelist[i].exists()){
							filelist[i].delete();
						}
					}
				}
			}
			if("true".equals(flag)&&(Elements.size()>0|| "true".equals(code)|| "true".equals(field))){
				//将file文件上传到ftp服务器
				exportFTP(filename,path);
				//文件上传完后被删除
				File ftpfile = new File(path+filename);
				ftpfile.delete();
			}
		}catch(Exception e){
		}
	}
	/**
	 * 修改主集
	 * @param name 被选中的主集
	 * @param flag flag值
	 * @param code 代码体系
	 * @param field 指标体系
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String alertBase(String[] name,String[] flag,String code,String field,String transcode,String strtoutf){
		String  main = "";
		Element root = new Element("root");
		root.setAttribute("code",code);
		root.setAttribute("field",field);
		root.setAttribute("transcode",transcode);
		root.setAttribute("strtoutf",strtoutf);
		if(null!=name){
			for(int i=0;i<name.length;i++){
				Element base = new Element("base");
				base.setAttribute("name",name[i]);
				base.setAttribute("flag",flag[i]);
				root.addContent(base);
			}
		}
		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		main= outputter.outputString(myDocument);
		return main;
	}
	/**
	 * 修改子集
	 * @param name 被选中的子集
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String alertSubset(String[] name){
		String  main = "";
		
		try {
			init();
			XPath xPath = XPath.newInstance("/root/base");
			List list=xPath.selectNodes(this.doc);
			for(Iterator t=list.iterator();t.hasNext();){
				Element test_template =(Element)t.next();
				test_template.removeContent();
			}
			for(Iterator t=list.iterator();t.hasNext();){
				Element test_template =(Element)t.next();
				if("A".equals(test_template.getAttributeValue("flag"))){
					if(name!=null){
						for(int i=0;i<name.length;i++){
							if("A".equals(name[i].substring(0,1))){
								Element subset = new Element("subset");
								subset.setAttribute("name",name[i]);
								test_template.addContent(subset);
							}
						}
					}
				}
				if("B".equals(test_template.getAttributeValue("flag"))){
					if(name!=null){
						for(int i=0;i<name.length;i++){
							if("B".equals(name[i].substring(0,1))){
								Element subset = new Element("subset");
								subset.setAttribute("name",name[i]);
								test_template.addContent(subset);
							}
						}
					}
				}
				if("K".equals(test_template.getAttributeValue("flag"))){
					if(name!=null){
						for(int i=0;i<name.length;i++){
							if("K".equals(name[i].substring(0,1))){
								Element subset = new Element("subset");
								subset.setAttribute("name",name[i]);
								test_template.addContent(subset);
							}
						}
					}
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			main = outputter.outputString(this.doc);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return main;
	}
	/**
	 * 修改指标
	 * @param name  被选中的指标集
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String alertSubsetValue(String[] name){
		String  main = "";
		
		try {
			init();
			XPath xPath = XPath.newInstance("/root/base/subset");
			List list=xPath.selectNodes(this.doc);

			for(Iterator t=list.iterator();t.hasNext();){
				Element test_template =(Element)t.next();
				
				 Set set = new HashSet();
				 if(name!=null){
					 for(int i=0;i<name.length;i++){
						 set.add(name[i].substring(0,3));
					 }
				 }
				 Iterator iterator = set.iterator(); 
				 while(iterator.hasNext()) {
					 if(test_template.getAttributeValue("name")!=null){
				    	if(test_template.getAttributeValue("name").equals(iterator.next())){
							test_template.removeContent();
						}
					 }
				 }
			}
			for(Iterator t=list.iterator();t.hasNext();){
				Element test_template =(Element)t.next();
				String subset  = "";
				if(name!=null){
					for(int i=0;i<name.length;i++){
						if(test_template.getAttributeValue("name").equals(name[i].substring(0,3))){
							subset +=name[i]+","; 	
						}
					}
				}
				if(subset.length()>0){
					subset = subset.substring(0,subset.lastIndexOf(","));
					test_template.addContent(subset);
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			main = outputter.outputString(this.doc);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return main;
	}
	/**
	 * 修改是否导出代码体系和指标体系
	 * @param code 代码体系
	 * @param field 指标体系
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String alertRoot(String code,String field,String transcode,String strtoutf){
		String  main = "";
		
		try {
			init();
			XPath xPathRoot = XPath.newInstance("/root");
			List listroot=xPathRoot.selectNodes(this.doc);
			Element root_template = null;
			for(Iterator t=listroot.iterator();t.hasNext();){
					root_template =(Element)t.next();
					root_template.removeAttribute("code");
					root_template.removeAttribute("field");
					root_template.removeAttribute("transcode");
					root_template.removeAttribute("strtoutf");
					
					root_template.setAttribute("code",code);
					root_template.setAttribute("field",field);
					root_template.setAttribute("transcode",transcode);
					root_template.setAttribute("strtoutf",strtoutf);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			main = outputter.outputString(this.doc);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return main;
	}
	/**
	 * 获得临时路径
	 * @param file 目录名
	 * @param endname 扩展名
	 * @return pathxml 文件路径
	 */
	public String getPath(String file,String endname){
		String paths = System.getProperty("java.io.tmpdir");
		String separator = System.getProperty("file.separator");
		
		String pathxml = paths+separator+"export";
		File f= new File(pathxml);
		if(!f.exists()){
			f.mkdir();
		}
		pathxml += separator + file + endname;
		
		File filexml=new File(pathxml);
		if(filexml.exists()){
			filexml.delete();  
		}
		return pathxml;
	}
	/**
	 * 从数据库中导出数据以xml文件形式保存
	 * @param file 文件名
	 * @param elements 指标值
	 * @param strFlag 是否将字段转换成UTF8的形式
	 * @param idTodesc 字符代码型是否转换代码描述
	 * @throws IOException,SQLException
	 */
	public void exportBaseXML(String file,String elements,String transcode,String strFlag){
		String pathxml = this.getPath(file,".xml");
		DocType type=new DocType("employees_information",file+".dtd");//文档类型 
		FileOutputStream fos = null;
		try {
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from ");
			strsql.append(file);
			String[] array = elements.split(",");
		
			ContentDAO dao=new ContentDAO(conn);
			RowSet rs = dao.search(strsql.toString());
			Element root = new Element("root");
			fos = new FileOutputStream(pathxml);
			while(rs.next()){
				Element C_element = new Element("C_"+elements.substring(0,3));
				
				if("A".equals(elements.substring(0,1))){
					Element element1 = new Element("A0100");
					String A0100 = rs.getString("A0100");
					A0100 = A0100!=null?PubFunc.toXml(A0100):""; 
					A0100 = "true".equals(strFlag)&&A0100.length()>0?strToUTF8(A0100):A0100;
					element1.addContent(A0100);
					C_element.addContent(element1);
					if(!"A01".equals(elements.substring(0,3))){
						Element element2 = new Element("I9999");
						String I9999 = rs.getString("I9999");
						I9999 = I9999!=null?PubFunc.toXml(I9999):""; 
						I9999 = "true".equals(strFlag)&&I9999.length()>0?strToUTF8(I9999):I9999;
						element2.addContent(I9999);
						C_element.addContent(element2);
					}
				}
				if("B".equals(elements.substring(0,1))){
					Element element1 = new Element("B0110");
					String B0110 = rs.getString("B0110");
					B0110 = B0110!=null?PubFunc.toXml(B0110):""; 
					B0110 = "true".equals(strFlag)&&B0110.length()>0?strToUTF8(B0110):B0110;
					element1.addContent(B0110);
					C_element.addContent(element1);
					if(!"B01".equals(elements.substring(0,3))){
						Element element2 = new Element("I9999");
						String I9999 = rs.getString("I9999");
						I9999 = I9999!=null?PubFunc.toXml(I9999):""; 
						I9999 = "true".equals(strFlag)&&I9999.length()>0?strToUTF8(I9999):I9999;
						element2.addContent(I9999);
						C_element.addContent(element2);
					}
				}
				if("K".equals(elements.substring(0,1))){
					Element element1 = new Element("E01a1");
					String E01a1 = rs.getString("E01a1");
					E01a1 = E01a1!=null?PubFunc.toXml(E01a1):""; 
					E01a1 = "true".equals(strFlag)&&E01a1.length()>0?strToUTF8(E01a1):E01a1;
					element1.addContent(E01a1);
					C_element.addContent(element1);
					if(!"K01".equals(elements.substring(0,3))){
						Element element2 = new Element("I9999");
						String I9999 = rs.getString("I9999");
						I9999 = I9999!=null?PubFunc.toXml(I9999):""; 
						I9999 = "true".equals(strFlag)&&I9999.length()>0?strToUTF8(I9999):I9999;
						element2.addContent(I9999);
						C_element.addContent(element2);
					}
				}
				
				for(int i=0;i<array.length;i++){
					String itemid = array[i].substring(4,array[i].length());
					Element element = new Element(itemid);
					String temp = sqlSearch(rs,itemid);
					temp = temp!=null?PubFunc.toXml(temp):"";
					temp = "true".equals(strFlag)&&temp.length()>0?strToUTF8(temp):temp;
					element.addContent(temp);
					C_element.addContent(element);
					
				}
				root.addContent(C_element);
			}
			Document myDocument = new Document(root,type);
			Format format = Format.getPrettyFormat();
			if("true".equals(strFlag)){
				format.setEncoding("UTF-8");
			}else{
				format.setEncoding("UTF-8");
			}
			format.setExpandEmptyElements(true);
			XMLOutputter out = new XMLOutputter(format);
		    out.output(myDocument,fos) ;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	 }
	/**
	 * 将子标值以DTD文件形式保存
	 * @param file 文件名
	 * @param elements 指标值
	 * @throws IOException
	 */
	public void exportBaseDTD(String file,String elements){
			PrintWriter dtd = null;
			FileWriter filewriter = null;
			String pathdtd = this.getPath(file,".dtd");
			try {
				filewriter = new FileWriter(pathdtd,true);
				dtd = new PrintWriter(filewriter, true);

				String[] array = elements.split(",");
				
				dtd.println("<!ELEMENT root (C_"+elements.substring(0,3)+")>");
				if("A".equals(elements.substring(0,1))){
					if("A01".equals(elements.substring(0,3))){
						dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(A0100,"+elements.replaceAll(elements.substring(0,4),"")+")>");
						dtd.println("<!ELEMENT A0100 (#PCDATA)>");
					}else{
						dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(A0100,I9999,"+elements.replaceAll(elements.substring(0,4),"")+")>");
						dtd.println("<!ELEMENT A0100 (#PCDATA)>");
						dtd.println("<!ELEMENT I9999 (#PCDATA)>");
					}
				}
				if("B".equals(elements.substring(0,1))){
					if("B01".equals(elements.substring(0,3))){
						dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(A0110,"+elements.replaceAll(elements.substring(0,4),"")+")>");
						dtd.println("<!ELEMENT B0110 (#PCDATA)>");
					}else{
						dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(A0100,I9999,"+elements.replaceAll(elements.substring(0,4),"")+")>");
						dtd.println("<!ELEMENT B0100 (#PCDATA)>");
						dtd.println("<!ELEMENT I9999 (#PCDATA)>");
					}
				}
				if("K".equals(elements.substring(0,1))){
					if("K01".equals(elements.substring(0,3))){
					dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(E01a1,"+elements.replaceAll(elements.substring(0,4),"")+")>");
					dtd.println("<!ELEMENT E01a1 (#PCDATA)>");
					}else{
						dtd.println("<!ELEMENT C_"+elements.substring(0,3)+"(E0100,I9999,"+elements.replaceAll(elements.substring(0,4),"")+")>");
						dtd.println("<!ELEMENT E0100 (#PCDATA)>");
						dtd.println("<!ELEMENT I9999 (#PCDATA)>");
					}
				}
				for(int i=0;i<array.length;i++){
					dtd.println("<!ELEMENT "+array[i].substring(4,array[i].length())+" (#PCDATA)>");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					dtd.close();
				}
			}
	}
	/**
	 * 从数据库中codeset表导出数据以xml文件形式保存
	 * @throws IOException,SQLException
	 */
	public void exportCodesetXML(String strFlag){
			String pathxml = this.getPath("codeset",".xml");
			FileOutputStream fos = null;
			DocType type=new DocType("employees_information","codeset.dtd");//文档类型 
			try {
				StringBuffer strsql=new StringBuffer();
				strsql.append("select codesetid,codesetdesc,maxlength,status from codeset");
				ContentDAO dao=new ContentDAO(conn);
				RowSet rs = dao.search(strsql.toString());
				fos =  new FileOutputStream(pathxml);
				Element root = new Element("root");
				while(rs.next()){
					Element C_element = new Element("C_CODESET");
					
					Element CODESETID = new Element("CODESETID");
						String codesetid = rs.getString("codesetid");
						codesetid = codesetid!=null?PubFunc.toXml(codesetid):"";
						codesetid = "true".equals(strFlag)&&codesetid.length()>0?strToUTF8(codesetid):codesetid;
						CODESETID.addContent(codesetid);
						C_element.addContent(CODESETID);
					Element CODESETDESC = new Element("CODESETDESC");
						String codesetdesc = rs.getString("codesetdesc");
						codesetdesc = codesetdesc!=null?PubFunc.toXml(codesetdesc):"";
						codesetdesc = "true".equals(strFlag)&&codesetdesc.length()>0?strToUTF8(codesetdesc):codesetdesc;
						CODESETDESC.addContent(codesetdesc);
						C_element.addContent(CODESETDESC);
					Element MAXLENGTH = new Element("MAXLENGTH");
						String maxlength = rs.getString("maxlength");
						maxlength = maxlength!=null?PubFunc.toXml(maxlength):"";
						maxlength = "true".equals(strFlag)&&maxlength.length()>0?strToUTF8(maxlength):maxlength;
						MAXLENGTH.addContent(maxlength);
						C_element.addContent(MAXLENGTH);
					Element STATUS = new Element("STATUS");
						String status = rs.getString("status");
						status = status!=null?PubFunc.toXml(status):"";
						status = "true".equals(strFlag)&&status.length()>0?strToUTF8(status):status;
						STATUS.addContent(rs.getString("status"));
						C_element.addContent(STATUS);
					
					root.addContent(C_element);
				}
				Document myDocument = new Document(root,type);
				Format format = Format.getPrettyFormat();
				if("true".equals(strFlag)){
					format.setEncoding("UTF-8");
				}else{
					format.setEncoding("UTF-8");
				}
				format.setExpandEmptyElements(true);
				XMLOutputter out = new XMLOutputter(format);
		        out.output(myDocument,fos); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	/**
	 * 将codeset表中字段以DTD文件形式保存
	 * @throws IOException
	 */
	public void exportCodesetDTD(){
			PrintWriter dtd = null;
			FileWriter filewriter = null;
			String pathdtd = this.getPath("codeset",".dtd");
			try {
				filewriter = new FileWriter(pathdtd,true);
				dtd = new PrintWriter(filewriter, true);
				
				dtd.println("<!ELEMENT root (C_ CODESET)>");
				dtd.println("<!ELEMENT C_ CODESET (CODESETID, CODESETDESC, MAXLENGTH, STATUS)>");
				dtd.println("<!ELEMENT CODESETID (#PCDATA)>");
				dtd.println("<!ELEMENT CODESETDESC (#PCDATA)>");
				dtd.println("<!ELEMENT MAXLENGTH (#PCDATA)>");
				dtd.println("<!ELEMENT STATUS (#PCDATA)>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					dtd.close();
				}
			}
	}
	/**
	 * 从数据库中codeitem表导出数据以xml文件形式保存
	 * @throws IOException,SQLException
	 */
	public void exportCodeitemXML(String strFlag){
			String pathxml = this.getPath("codeitem",".xml");
			DocType type=new DocType("employees_information","codeitem.dtd");//文档类型 
			FileOutputStream fos = null;
			try {
				StringBuffer strsql=new StringBuffer();
				strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,flag from codeitem");
				ContentDAO dao=new ContentDAO(conn);
				RowSet rs = dao.search(strsql.toString());
				fos = new FileOutputStream(pathxml);
				Element root = new Element("root");
				while(rs.next()){
					Element C_element = new Element("C_CODEITEM");
					
					Element CODESETID = new Element("CODESETID");
						String codesetid = rs.getString("codesetid");
						codesetid = codesetid!=null?PubFunc.toXml(codesetid):"";
						codesetid = "true".equals(strFlag)&&codesetid.length()>0?strToUTF8(codesetid):codesetid;
						CODESETID.addContent(codesetid);
						C_element.addContent(CODESETID);
					Element CODEITEMID = new Element("CODEITEMID");
						String codeitemid = rs.getString("codeitemid");
						codeitemid = codeitemid!=null?PubFunc.toXml(codeitemid):"";
						codeitemid = "true".equals(strFlag)&&codeitemid.length()>0?strToUTF8(codeitemid):codeitemid;
						CODEITEMID.addContent(codeitemid);
						C_element.addContent(CODEITEMID);
					Element CODEITEMDESC = new Element("CODEITEMDESC");
						String codeitemdesc = rs.getString("codeitemdesc");
						codeitemdesc = codeitemdesc!=null?PubFunc.toXml(codeitemdesc):"";
						codeitemdesc = "true".equals(strFlag)&&codeitemdesc.length()>0?strToUTF8(codeitemdesc):codeitemdesc;
						CODEITEMDESC.addContent(codeitemdesc);
						C_element.addContent(CODEITEMDESC);
					Element PARENTID = new Element("PARENTID");
						String parentid = rs.getString("parentid");
						parentid = parentid!=null?PubFunc.toXml(parentid):"";
						parentid = "true".equals(strFlag)&&parentid.length()>0?strToUTF8(parentid):parentid;
						PARENTID.addContent(parentid);
						C_element.addContent(PARENTID);
					Element CHILDID = new Element("CHILDID");
						String childid = rs.getString("childid");
						childid = childid!=null?PubFunc.toXml(childid):"";
						childid = "true".equals(strFlag)&&childid.length()>0?strToUTF8(childid):childid;
						CHILDID.addContent(childid);
						C_element.addContent(CHILDID);
					Element FLAG = new Element("FLAG");
						String flag = rs.getString("flag");
						flag = flag!=null?PubFunc.toXml(flag):"";
						flag = "true".equals(strFlag)&&flag.length()>0?strToUTF8(flag):flag;
						FLAG.addContent(flag);
						C_element.addContent(FLAG); 
					root.addContent(C_element);
				}
				Document myDocument = new Document(root,type);
				Format format = Format.getPrettyFormat();
				if("true".equals(strFlag)){
					format.setEncoding("UTF-8");
				}else{
					format.setEncoding("UTF-8");
				}
				format.setExpandEmptyElements(true);
				XMLOutputter out = new XMLOutputter(format);
		        out.output(myDocument,fos); 
				
				//printxml.println(xmlfield);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	/**
	 * 将codeitem表中字段以DTD文件形式保存
	 * @throws IOException
	 */
	public void exportCodeitemDTD(){
			PrintWriter dtd = null;
			FileWriter filewriter = null;
			String pathdtd = this.getPath("codeitem",".dtd");
			try {
				filewriter = new FileWriter(pathdtd,true);
				dtd = new PrintWriter(filewriter, true);
				dtd.println("<!ELEMENT root (C_ CODEITEM)>");
				dtd.println("<!ELEMENT C_ CODEITEM (CODESETID, CODEITEMID, CODEITEMDESC, PARENTID, CHILDID, FLAG)>");
				dtd.println("<!ELEMENT CODESETID (#PCDATA)>");
				dtd.println("<!ELEMENT CODEITEMID (#PCDATA)>");
				dtd.println("<!ELEMENT CODEITEMDESC (#PCDATA)>");
				dtd.println("<!ELEMENT PARENTID (#PCDATA)>");
				dtd.println("<!ELEMENT CHILDID (#PCDATA)>");
				dtd.println("<!ELEMENT FLAG (#PCDATA)>");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					dtd.close();
				}
			}
	}
	/**
	 * 从数据库中fieldset表导出数据以xml文件形式保存
	 * @throws IOException,SQLException
	 */
	public void exportFieldsetXML(String strFlag){
			String pathxml = this.getPath("fieldset",".xml");
			DocType type=new DocType("employees_information","fieldset.dtd");//文档类型 
			FileOutputStream fos = null;
			RowSet rs = null;
			try {
				StringBuffer strsql=new StringBuffer();
				strsql.append("select fieldsetid,customdesc from fieldset where useflag=1");
				ContentDAO dao=new ContentDAO(conn);
				rs = dao.search(strsql.toString());
				fos = new FileOutputStream(pathxml);
				
				Element root = new Element("root");
				while(rs.next()){
					Element C_element = new Element("C_FIELDSET");
					
					Element FIELDSETID = new Element("FIELDSETID");
						String fieldsetid = rs.getString("fieldsetid");
						fieldsetid = fieldsetid!=null?PubFunc.toXml(fieldsetid):"";
						fieldsetid = "true".equals(strFlag)&&fieldsetid.length()>0?strToUTF8(fieldsetid):fieldsetid;
						FIELDSETID.addContent(fieldsetid);
						C_element.addContent(FIELDSETID);
					Element CUSTOMDESC = new Element("CUSTOMDESC");
						String customdesc = rs.getString("customdesc");
						customdesc = customdesc!=null?PubFunc.toXml(customdesc):"";
						customdesc = "true".equals(strFlag)&&customdesc.length()>0?strToUTF8(customdesc):customdesc;
						CUSTOMDESC.addContent(rs.getString("customdesc"));
						C_element.addContent(CUSTOMDESC);
					root.addContent(C_element);
				}
				Document myDocument = new Document(root,type);
				Format format = Format.getPrettyFormat();
				if("true".equals(strFlag)){
					format.setEncoding("UTF-8");
				}else{
					format.setEncoding("UTF-8");
				}
				
				format.setExpandEmptyElements(true);
				XMLOutputter out = new XMLOutputter(format);
			    out.output(myDocument,fos); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	/**
	 * 将fieldset表中字段以DTD文件形式保存
	 * @throws IOException
	 */
	public void exportFieldsetDTD(){
			PrintWriter dtd = null;
			FileWriter filewriter = null;
			String pathdtd = this.getPath("fieldset",".dtd");
			try {
				filewriter = new FileWriter(pathdtd,true);
				dtd = new PrintWriter(filewriter, true);
				
				dtd.println("<!ELEMENT root (C_ FIELDSET)>");
				dtd.println("<!ELEMENT C_ FIELDSET (FIELDSETID, CUSTOMDESC,)>");
				dtd.println("<!ELEMENT FIELDSETID (#PCDATA)>");
				dtd.println("<!ELEMENT CUSTOMDESC (#PCDATA)>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					dtd.close();
				}
			}
	}
	/**
	 * 将path路径下export目录文件夹下面的文件打包,生成zpfile文件保存到path路径下
	 * @param zpfile 文件名
	 * @param path 路径
	 * @param export 目录
	 * @throws FileNotFoundException,IOException
	 */
	public void exportJar(String zpfile,String path,String export){
		String separator = System.getProperty("file.separator");
		
		 String inputFileName = path+separator+export;
		 
		 String zipFileName = path+separator+zpfile;

		 ZipOutputStream out = null;
		 FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(zipFileName);
			out = new ZipOutputStream(fileOutputStream);
			
			File f = new File(inputFileName);
			if(f.exists()){
				File[] fl = f.listFiles();
				for (int i = 0; i < fl.length; i++) {
					out.putNextEntry(new ZipEntry(fl[i].getName()));
					FileInputStream in = null;
					try{
						in = new FileInputStream(fl[i]);
						int b;
						while ( (b = in.read()) != -1) {
							out.write(b);
						}
					}catch(IOException e){
						e.printStackTrace();
					}finally{
						in.close(); 
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				out.close();
				PubFunc.closeIoResource(fileOutputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	 }
	/**
	 * 将path路径下的ftpifile文件上传到FTP服务器
	 * @param file 文件名
	 * @param path 路径
	 * @throws GeneralException,IOException
	 */
	public void exportFTP(String ftpifile,String path){
			String separator = System.getProperty("file.separator");
			String zipFileName = path+separator+ftpifile;
			
			File uploadfile = new File(zipFileName);
			
			if(uploadfile.exists()){
				String ServerIP="";
				String UserName="";
				String PassWord="";
				String Port="";
				String ftppath= "";
				FileInputStream fis = null;
				FTPClient ftp = null;
				try {
					ftp = new FTPClient();
					ServerIP = SystemConfig.getProperty("ftpserver");
					UserName = SystemConfig.getProperty("ftp_user");
					PassWord = SystemConfig.getProperty("ftp_pwd");
					Port =  SystemConfig.getProperty("ftpport");
					ftppath =  SystemConfig.getProperty("ftppath");
					
					ftp.setDefaultPort(Integer.parseInt(Port));
					ftp.connect(ServerIP);
					ftp.login(UserName,PassWord);
					
					boolean workdir = ftp.changeWorkingDirectory(ftppath);
					if(!workdir){
						ftp.makeDirectory(ftppath);
						ftp.changeWorkingDirectory(ftppath);
					}
					
					ftp.setFileType(FTP.BINARY_FILE_TYPE); 
					ftp.enterLocalPassiveMode(); 
					
					fis = new FileInputStream(uploadfile);
					
					ftp.storeFile(ftpifile,fis);
				} catch (GeneralException e) {
					e.printStackTrace();
				}catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						fis.close();
						ftp.logout(); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
	}
	/**
	 * 将字符编码转换成UTF8
	 * @param str 字符串
	 * @throws Exception
	 */
	public static String strToUTF8( String str ){
	        try{
	            byte[] bytesStr=str.getBytes( SystemConfig.getProperty("dbencoding") ) ;
	            return new String( bytesStr, SystemConfig.getProperty("dboutencoding") ) ; 
	        }
	        catch( Exception ex){
//	        	ex.printStackTrace();
	            return str ;
	        }
	  }
	/**
	 * 判断字段类型
	 * @param str 字段
	 * @throws Exception
	 */
	public  String sqlSearch(RowSet rs, String str){
		FieldItem item=DataDictionary.getFieldItem(str);
		String temp = "";
		try {
			if(item!=null){
				if(item.isCode()){
					temp=AdminCode.getCodeName(item.getCodesetid(), rs.getString(str));
					temp = temp!=null?PubFunc.toXml(temp):"";
				}else{
					ResultSetMetaData rsetmd=rs.getMetaData();
					temp = getColumStr(rs,rsetmd,str);
					temp = temp!=null?PubFunc.toXml(temp):"";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	public String getColumStr(RowSet rset,ResultSetMetaData rsetmd,String str) throws SQLException{
		int j=rset.findColumn(str);
		String temp=null;
		switch(rsetmd.getColumnType(j)){
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
			    if(temp.indexOf("12:00:00")!=-1) {
                    temp=PubFunc.FormatDate(rset.getDate(j));
                }
				break;
		case Types.CLOB:
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	                    	
				break;
		case Types.BLOB:
				temp="二进制文件";	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  temp=String.valueOf(rset.getDouble(j));			  
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;
		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
}


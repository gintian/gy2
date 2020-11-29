package com.hjsj.hrms.businessobject.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScormXMLBo {
	// xml内容
	private String xmlContent;
	
	// 命名空间
	private String nameSpace;
	
	// 文档
	private Document doc;
	
	// reader
	private SAXReader xmlReader;
	
	public ScormXMLBo(String xmlContent, String nameSpace) {
		this.xmlContent = xmlContent;
		if (nameSpace != null && nameSpace.length() > 0) {
			this.nameSpace = nameSpace;
		} else {
			this.nameSpace = "mo";
		}
		init();
	}
	
	/**
	 * 初始化参数
	 */
	private void init() {
		
		try{   
			this.doc = (Document) PubFunc.generateDom(this.xmlContent);
		 
		}catch(Exception e){   
		            e.printStackTrace();   
		        } 
		
	}
	
	/**
	 * 根据path获得节点集合
	 * @param path
	 * @return
	 */
	public List getElement(String path) {
		HashMap xmlMap = new HashMap();
		
		xmlMap.put(this.nameSpace,this.doc.getRootElement().getNamespace().getURI());   
		XPath x = this.doc.createXPath(path);
		x.setNamespaceURIs(xmlMap);            
		List list = (List)x.selectNodes(this.doc) ;  
		return list;  
	}
	
	/**
	 * 根据path获得一个节点
	 * @param path
	 * @return
	 */
	public Element getSingleElement(String path) {
		HashMap xmlMap = new HashMap();
		
		xmlMap.put(this.nameSpace,this.doc.getRootElement().getNamespace().getURI());   
		XPath x = this.doc.createXPath(path);
		x.setNamespaceURIs(xmlMap);            
		Element el = (Element)x.selectSingleNode(doc) ;  
		return el;  
	}
	
	/**
	 * 获得所有sco及href
	 */
	public List getAllScoAndParent() {
		List list = new ArrayList();
		HashMap xmlMap = new HashMap();   
		xmlMap.put("mo", this.doc.getRootElement().getNamespace().getURI());   
		XPath x = this.doc.createXPath("/mo:manifest/mo:organizations/mo:organization");   
		x.setNamespaceURIs(xmlMap); 
		// 获得所有organization节点
		List elList = (List)x.selectNodes(this.doc);
		
		list = recursiveElments2(elList,this.doc);
		return list;
	}
	
	/**
	 * 获得所有的sco及href
	 * @return
	 */
	public List getAllScoHref() {
		List list = new ArrayList();
		HashMap xmlMap = new HashMap();   
		xmlMap.put("mo", this.doc.getRootElement().getNamespace().getURI());   
		XPath x = this.doc.createXPath("/mo:manifest/mo:organizations/mo:organization");   
		x.setNamespaceURIs(xmlMap); 
		// 获得所有organization节点
		List elList = (List)x.selectNodes(this.doc);
		
		list = recursiveElments(elList,this.doc);
		return list;
	}
	
	private List recursiveElments(List elList, Document doc) {
		List list = new ArrayList();
		for (int i = 0; i < elList.size(); i++) {
			Element el = (Element) elList.get(i);
			String identifier = el.attributeValue("identifier");
			String identifierref = el.attributeValue("identifierref");
			if (identifierref != null && identifierref.length() > 0) {
				HashMap xmlMap = new HashMap();   
				xmlMap.put("mo", doc.getRootElement().getNamespace().getURI());   
				XPath x = doc.createXPath("/mo:manifest/mo:resources/mo:resource[@identifier='" + identifierref + "']");   
				x.setNamespaceURIs(xmlMap);
				Element resEl = (Element) x.selectSingleNode(doc);
				if ("sco".equalsIgnoreCase(resEl.attributeValue("scormtype")) || "sco".equalsIgnoreCase(resEl.attributeValue("scormType"))) {
					String href = resEl.attributeValue("href");
					list.add(identifier + ";&;" + href);
				}
			}
			List childList = el.elements("item");
			// 递归调用
			if (childList.size() > 0) {
				list.addAll(recursiveElments(childList, doc));
			}
		}
		return list;
	}
	
	private List recursiveElments2(List elList, Document doc) {
		List list = new ArrayList();
		for (int i = 0; i < elList.size(); i++) {
			Element el = (Element) elList.get(i);
			String identifier = el.attributeValue("identifier");
			Element pEle = el.getParent();
			if ("organizations".equalsIgnoreCase(pEle.getName())){
				list.add(identifier + ";&;" + identifier);
			} else {
				list.add(identifier + ";&;" + pEle.attributeValue("identifier"));
			}
			
			List childList = el.elements("item");
			// 递归调用
			if (childList.size() > 0) {
				list.addAll(recursiveElments2(childList, doc));
			}
		}
		return list;
	}
	
	public static void main(String[] args) {
		 test3();
	}
	
	private static void test1() {
//		/manifest/resources/resource[@identifier='"+ identifierref +"']
		SAXReader xmlReader = new SAXReader();   
		FileInputStream fis = null;
		InputStreamReader is = null;
		try{
		fis  = new FileInputStream(new File("c:/imsmanifest.xml"));
		is = new InputStreamReader(fis);
		org.dom4j.Document document = xmlReader.read(is, "utf-8");   
		///*测试代码    适用于读取xml的节点   
		HashMap xmlMap = new HashMap();   
		xmlMap.put("mo",document.getRootElement().getNamespace().getURI());   
		XPath x = document.createXPath("/mo:manifest/mo:organizations/mo:organization");   
		x.setNamespaceURIs(xmlMap);            
		List list = (List)x.selectNodes(document);
		for (int i = 0; i <list.size(); i++) {
			Element el = (Element) list.get(i);
			System.out.println(el.getName());  
		}
		
		}catch(Exception e){   
		            e.printStackTrace();   
		}
		finally{
			PubFunc.closeIoResource(is);
		    PubFunc.closeIoResource(fis);
		}
	}
	
	private static void test2() {
//		
		SAXReader xmlReader = new SAXReader();   
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try{   
		fis = new  FileInputStream(new File("c:/imsmanifest.xml"));
		isr = new InputStreamReader(fis, "utf-8");
//		org.dom4j.Document document = xmlReader.read(new InputStreamReader(new FileInputStream(new File("c:/imsmanifest.xml")), "utf-8"));   
		org.dom4j.Document document = xmlReader.read(isr);   
		///*测试代码    适用于读取xml的节点   
		HashMap xmlMap = new HashMap();   
		xmlMap.put("mo",document.getRootElement().getNamespace().getURI());   
		XPath x = document.createXPath("/mo:manifest/mo:resources/mo:resource[@identifier='sco_1']");   
		x.setNamespaceURIs(xmlMap);            
		List list = (List)x.selectNodes(document);
		for (int i = 0; i <list.size(); i++) {
			Element el = (Element) list.get(i);
			System.out.println(el.getPath()); 
			System.out.println(el.attributeValue("scormtype"));  
		}
		
		}catch(Exception e){   
		            e.printStackTrace();   
		}finally{
			PubFunc.closeResource(isr);
			PubFunc.closeResource(fis);
		}
	}
	
	private static void test3() {
//		
		SAXReader xmlReader = new SAXReader();
		FileInputStream stream = null;
		BufferedReader buffReader = null;
		InputStreamReader reader = null;
		try{   
			StringBuffer buff = new StringBuffer();
			stream = new FileInputStream(new File("c:/imsmanifest.xml"));
			reader = new InputStreamReader(stream, "utf-8"); 
			buffReader = new BufferedReader(reader);
			String str = null;
			while ((str = buffReader.readLine()) != null) {
				buff.append(str);
				
			}
			System.out.println(buff);
			ScormXMLBo bo = new ScormXMLBo(buff.toString(),"mo");
			List list = bo.getAllScoHref();
			for (int i = 0; i < list.size(); i++) {
				System.out.println((String) list.get(i));
			}
		
		}catch(Exception e){   
		            e.printStackTrace();   
		}finally{
		    PubFunc.closeIoResource(buffReader);
		    PubFunc.closeResource(reader);
		    PubFunc.closeResource(stream);
		}
	}
	
	private static void test4() {
		
		SAXReader xmlReader = new SAXReader();   
		InputStream is = null;
		try{   
			is = new FileInputStream(new File("c:/imsmanifest.xml"));
		org.dom4j.Document document = xmlReader.read(new InputStreamReader(is, "utf-8"));   
		///*测试代码    适用于读取xml的节点   
		HashMap xmlMap = new HashMap();   
		xmlMap.put("mo",document.getRootElement().getNamespace().getURI());   
		XPath x = document.createXPath("/mo:manifest/mo:organizations/mo:organization");   
		x.setNamespaceURIs(xmlMap);            
//		
		
		List list = new ArrayList();
		 
		// 获得所有organization节点
		List elList = (List)x.selectNodes(document);
		
		//list = recursiveElments(elList, document);
		for (int i = 0; i < list.size(); i++) {
			System.out.println((String) list.get(i));
		}
		
		
		}catch(Exception e){   
		            e.printStackTrace();   
		}finally {
            PubFunc.closeIoResource(is);
        }  
	}
	
	
}

package com.hjsj.hrms.businessobject.gz.gz_analyse;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GzFormulaXMLBo {
	private Document doc;
	private Connection conn;
	private String tabid;
	public GzFormulaXMLBo(Connection conn,String tabid){
		this.conn=conn;
		this.tabid=tabid;
		init();
	}
	/**
	 * 初始化
	 */
	private void init(){
		String content="";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select Seive from muster_name where Tabid=");
		sql.append(tabid);
		try {
			RowSet rs=dao.search(sql.toString());
			if(rs.next()){
				content = rs.getString("Seive");
			}
			initDoc(content);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void initDoc(String content) throws Exception{
		content=content!=null&&content.trim().length()>0?content:"";
		if(content.length()<1){
			StringBuffer strxml=new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<Params>");
			strxml.append("</Params>");	
			content=strxml.toString();
		}				
		try {
			this.doc=PubFunc.generateDom(content);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateSeive(){
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql=new StringBuffer();
		sql.append("update muster_name set Seive=? where Tabid=?");
		ArrayList list = new ArrayList();
		list.add(getXmlconent());
		list.add(tabid);
		try {
			dao.update(sql.toString(),list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 设置过滤条件
	 * @param id //条件id
	 * @param name //条件名称
	 * @param expr //条件因子表达试
	 * @param factor //条件
	 * @throws Exception 
	 */
	 public void setSeiveItem(String id,String name,String expr,String factor) throws Exception{
		 String xpath="";
		 try{
			 xpath="/Params/Serive/SeiveItem[@ID='"+id+"']";
			 XPath reportPath = XPath.newInstance(xpath);//取得子节点的属性值
			 List childlist=reportPath.selectNodes(this.doc);
		     Iterator t = childlist.iterator();
		     if(t.hasNext()){
		    	 Element updateR=(Element)t.next(); 
		    	 updateR.setAttribute("ID",id);
		    	 updateR.setAttribute("Name",name);
		    	 updateR.setAttribute("Expr",expr);
		    	 updateR.setAttribute("Factor",factor);
		     }else{
		    	 xpath="/Params/Serive";
		    	 reportPath = XPath.newInstance(xpath);// 取得子节点值
		    	 List serivelist=reportPath.selectNodes(doc);
		    	 Iterator r = serivelist.iterator();
		    	 if(r.hasNext()){
		    		 Element recordR = (Element)r.next();  
		    		 Element recordE = new Element("SeiveItem"); 
			    	 recordE.setAttribute("ID", id);
			    	 recordE.setAttribute("Name", name);
			    	 recordE.setAttribute("Expr",expr);
			    	 recordE.setAttribute("Factor",factor);	
			    	 recordR.addContent(recordE);
		    	 }else{
		    		 xpath="/Params";
			    	 reportPath = XPath.newInstance(xpath);// 取得子节点值
			    	 List paramslist=reportPath.selectNodes(doc);
			    	 Iterator p = paramslist.iterator(); 
			    	 if(r.hasNext()){
			    		 Element recordR = (Element)p.next();
			    		 Element recordP = new Element("Serive");
			    		 Element recordE = new Element("SeiveItem");
			    		 recordE.setAttribute("ID", id);
				    	 recordE.setAttribute("Name", name);
				    	 recordE.setAttribute("Expr",expr);
				    	 recordE.setAttribute("Factor",factor);	
				    	 recordP.addContent(recordE);
				    	 recordR.addContent(recordP);
			    	 }else{
			    		 initDoc("");
						 xpath="/Params";
				    	 reportPath = XPath.newInstance(xpath);// 取得根节点值
				    	 List list=reportPath.selectNodes(doc);
				    	 Iterator pa = list.iterator();
				    	 Element recordR = (Element)pa.next();
			    		 Element recordP = new Element("Serive");
			    		 Element recordE = new Element("SeiveItem");
			    		 recordE.setAttribute("ID", id);
				    	 recordE.setAttribute("Name", name);
				    	 recordE.setAttribute("Expr",expr);
				    	 recordE.setAttribute("Factor",factor);	
				    	 recordP.addContent(recordE);
				    	 recordR.addContent(recordP);
			    	 }
		    	 }
		    	 
		     }
		 } catch (JDOMException e) {
				e.printStackTrace();
		 }
	 }
	 /**
	  * 获取过滤条件列表
	  * @return list
	  */
	 public ArrayList getSeiveItem(){
		 ArrayList list = new ArrayList();
		 String xpath="";
		try {
			xpath="/Params/Serive/SeiveItem"; //取得子节点的属性值
			XPath reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(this.doc);
		    Iterator t = childlist.iterator();
		    while(t.hasNext()){
		    	 Element updateR=(Element)t.next();
		    	 String id = updateR.getAttributeValue("ID");
		    	 String name =updateR.getAttributeValue("Name");
		    	 CommonData dataobj = new CommonData(id,name);
				 list.add(dataobj);
		    }
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	 }
	/**
	 * 获取过滤条件列表中的最大ID
	 * @return maxid
	 */
	 public String getSeiveItemId(){
		 int maxid=0;
		 String xpath="";
		try {
			xpath="/Params/Serive/SeiveItem"; //取得子节点的属性值
			XPath reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(this.doc);
		    Iterator t = childlist.iterator();
		    while(t.hasNext()){
		    	 Element updateR=(Element)t.next();
		    	 String id = updateR.getAttributeValue("ID");
		    	 if(maxid<Integer.parseInt(id))
		    		 maxid = Integer.parseInt(id);
		    }
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (maxid+1)+"";
	 }
	 /**
	  * 获取公式和公式因子
	  * @param id 
	  * @return
	  */
	 public ArrayList getSeiveItem(String id){
		 ArrayList list = new ArrayList();
		 String xpath="";
		try {
			xpath="/Params/Serive/SeiveItem[@ID='"+id+"']"; //取得子节点的属性值
			XPath reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(this.doc);
		    Iterator t = childlist.iterator();
		    if(t.hasNext()){
		    	 Element updateR=(Element)t.next();
		    	 String name = updateR.getAttributeValue("Name");
		    	 String expr = updateR.getAttributeValue("Expr");
		    	 String factor =updateR.getAttributeValue("Factor");
		    	 
				 list.add(expr);
				 list.add(factor);
				 list.add(name);
		    }
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return list;
	 }
	 /**
	  * 获取公式和公式因子
	  * @param id 
	  * @return
	  */
	 public boolean delSeiveItem(String id){
		 boolean check=false;
		 String xpath="";
		try {
			xpath="/Params/Serive/SeiveItem[@ID='"+id+"']"; //取得子节点的属性值
			XPath reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(this.doc);
		    Iterator r = childlist.iterator();
		    Element updateR = null;
		    if(r.hasNext()){
		    	 updateR=(Element)r.next();
		    	 updateR.detach();
			     check=true;
		    }
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return check;
	 }
	/**
	 * 生成XML格式的字符串
	 */
	private String getXmlconent(){
		XMLOutputter outputter = new XMLOutputter();
	    Format format=Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	   	outputter.setFormat(format);  	           
	    String xmlContent=outputter.outputString(this.doc); 
	    return xmlContent;
	}
}

package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *<p>Title:</p> 
 *<p>Description:对常量表constant进行操作</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ConstantXml {
	public Connection conn;
	public String xml;
	public Document doc;
	public String constant;
	
	public ConstantXml(){
	}
	public ConstantXml(Connection conn){
		this.conn=conn;
	}
	public ConstantXml(Connection conn,String constant){
		this.conn=conn;
		this.constant=constant;
		initXML();
	}
	public boolean initXML(){
		boolean judgment = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			String sqlstr = "select STR_VALUE  from CONSTANT where CONSTANT='"+constant+"'";
			//常量表中查找rp_param常量
			rs=dao.search(sqlstr);
			if(rs.next()){
				//获取XML文件
				if(Sql_switcher.readMemo(rs,"STR_VALUE").length()>0){
					xml = Sql_switcher.readMemo(rs,"STR_VALUE");
				}else{
					xml = "";
				}
			}else{
				List list = new ArrayList();
				list.add(constant);
				list.add(" ");
				list.add(" ");
				list.add(" ");
				dao.insert("insert into constant values(?,?,?,?)",list);
				
				xml = "";	
			}
			judgment=true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return judgment;
	}
	
	public boolean initDoc(){
		boolean judgment = false;
		if(xml!=null&&xml.trim().length()>0){
			try {
				doc = PubFunc.generateDom(xml);
				judgment=true;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					throw GeneralExceptionHandler.Handle(e);
				} catch (GeneralException e1) {
					e1.printStackTrace();
				} 
			}
		}
		return judgment;
	}
	
	public boolean init(){
		boolean judgment = false;
		initXML();
		judgment = initDoc();
		return judgment;
	}
	
	/**
	 * 修改常量表中str_value的值
	 * @param xmlvalue str_value字段的值
	 * @return boolean 修改str_value字段的值是否成功
	 * @throws Exception
	 */
	public boolean alertXML(String xmlvalue){
		boolean judgment = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			StringBuffer strsql=new StringBuffer();
			strsql.append("update constant set str_value=? where constant='");
			strsql.append(constant);
			strsql.append("'");
			List paralist=new ArrayList();
			paralist.add(xmlvalue);
			dao.update(strsql.toString(), paralist);
			judgment=true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return judgment;
	}
	
	/**
	 * 获取xml文件中的值
	 * @param perent 节点
	 * @return String xml下面perent节点下面的值
	 * @throws Exception
	 */
	public String elementValueStr(String perent){
		String  main = "";
		if(xml!=null&&xml.trim().length()>0){
			try{
				init();
				XPath xPath = XPath.newInstance(perent);
				ArrayList listmain = new ArrayList();
				List list=xPath.selectNodes(this.doc);
				for(Iterator t=list.iterator();t.hasNext();){
					Element test_template =(Element)t.next(); 
					if (test_template != null&&test_template.getValue().length()>0) {
						listmain.add(test_template.getValue());
					}
				}
				ArrayList mainlist = (ArrayList)elementSet(listmain);
				for(int i=0;i<mainlist.size();i++){
					main +=mainlist.get(i)+",";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return main;
	}
	
	/**
	 * 获取xml文件中的节点下面的值
	 * @param perent 节点
	 * @return ArrayList 节点下面的值
	 * @throws Exception
	 */
	public ArrayList elementValue(String perent){
		ArrayList  main = new ArrayList();
		if(xml!=null&&xml.trim().length()>0){
			try{
				init();
				XPath xPath = XPath.newInstance(perent);
				List list=xPath.selectNodes(this.doc);
				Iterator t=list.iterator();
				while(t.hasNext()){
					Element test_template =(Element)t.next();
					if(test_template.getValue().length()>0){
						main.add(test_template.getValue());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return main;
	}
	/**
	 * 获取xml文件中的节点的属性值
	 * @param perent 节点
	 * @param name 节点属性
	 * @return ArrayList 节点属性值
	 * @throws Exception
	 */
	public ArrayList elementName(String perent,String name){
		ArrayList main = new ArrayList();
		init();
		if(xml!=null&&xml.trim().length()>0){
			try{
				XPath xPath = XPath.newInstance(perent);
				List list=xPath.selectNodes(this.doc);
				for(Iterator t=list.iterator();t.hasNext();){
					Element test_template =(Element)t.next(); 
					if (test_template != null) {
						main.add(test_template.getAttributeValue(name));
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return main;
	}
	/**
	 * 去掉ArrayList里面重复的值
	 * @param list ArrayList值
	 * @return ArrayList  没有重复值的ArrayList
	 */
	public ArrayList elementSet(ArrayList list){
		ArrayList set = new ArrayList();
		for(int i=0;i<list.size();i++){
			if(set.size()>0){
				int n = 0;
				for(int j=0;j<set.size();j++){
					if(set.get(j).equals(list.get(i))){
						n=1;
					}
				}
				if(n==0){
					set.add(list.get(i));
				}
			}else{
				set.add(list.get(i));
			}
		}
		return set;
	}
	
	public String getXml(){
		return xml;
	}
	
	public Document getDoc(){
		return doc;
	}
}

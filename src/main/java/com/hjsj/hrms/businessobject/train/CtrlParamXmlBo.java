package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CtrlParamXmlBo {
	private Connection conn;
	private String xml;
	private Document doc;
	
	public CtrlParamXmlBo(Connection conn,String r3101)
	{
		this.conn=conn;
		this.initXML(r3101);
	}
	
	
	public CtrlParamXmlBo(Connection conn)
	{
		this.conn=conn;		
	}
	
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(String r3101){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select ctrl_param  from r31 where r3101='"+r3101+"'");
			if(rs.next()){
				//获取XML文件
				xml = Sql_switcher.readMemo(rs,"ctrl_param");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);;
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}
	
	
	
	public ArrayList getEvaluateModelList() throws Exception
	{
		ArrayList list=new ArrayList();
		
		if(xml == null || "".equals(xml.trim())){
			return list;
		}else{
			init();
			XPath xPath = XPath.newInstance("/param/eval");
			Element a_element=(Element)xPath.selectSingleNode(this.doc);
			if(a_element!=null)
			{
				List lists=a_element.getChildren();
				for(Iterator t=lists.iterator();t.hasNext();)
				{
					Element element=(Element)t.next();
					if(element.getValue()!=null&&element.getValue().trim().length()>0)
					{
						LazyDynaBean abean=new LazyDynaBean();
						String name="1"; 
						if("questionnaire".equalsIgnoreCase(element.getName())) {
                            name="0";
                        }
						abean.set("name",name);
						abean.set("type",element.getAttributeValue("type"));
						abean.set("run",element.getAttributeValue("run"));
						abean.set("end_date",element.getAttributeValue("end_date"));
						abean.set("value",element.getValue());
						
						list.add(abean);
					}
				}
			}
		}
		return list;
	}


	public Connection getConn() {
		return conn;
	}


	public void setConn(Connection conn) {
		this.conn = conn;
	}


	public Document getDoc() {
		return doc;
	}


	public void setDoc(Document doc) {
		this.doc = doc;
	}


	public String getXml() {
		return xml;
	}


	public void setXml(String xml) {
		this.xml = xml;
	}
	
	
	
	
	
}

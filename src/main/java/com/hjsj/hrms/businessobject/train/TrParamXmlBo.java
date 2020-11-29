package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;

public class TrParamXmlBo {
	private Connection conn;
	private String xml;
	private Document doc;
	
	public TrParamXmlBo(Connection conn)
	{
		this.conn=conn;
		this.initXML();
	}
	
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='TR_PARAM'");
			if(rs.next()){
				//获取XML文件
				xml = Sql_switcher.readMemo(rs,"STR_VALUE");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}
	
	
	
//	获得特定用户XML文件元素的相关属性值集合
	public HashMap getAttributeValues() throws GeneralException {
		HashMap hm = new HashMap();
		if(xml == null || "".equals(xml.trim())){
			hm.put("plan_mx","");
			return hm;
		}else{
			init();
			try {
				HashSet tempLateIDSet=new HashSet();
				XPath xPath = XPath.newInstance("/param/plan_mx");
				Element out_fields = (Element) xPath.selectSingleNode(this.doc);
				if (out_fields != null) {
					hm.put("plan_mx", out_fields.getValue());
				}
				
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
			return hm;
		
	}
	
	

}

package com.hjsj.hrms.businessobject.gz.templateset.tax_table;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

public class TaxTableXMLBo {
	private Connection conn;
	private String xml="";
	private Document doc;
	public TaxTableXMLBo(Connection conn){
		this.conn=conn;
	}
	public TaxTableXMLBo(){
		
	}
	
	public TaxTableXMLBo(String xml){
		this.xml=xml;
		try
		{
			init();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}	
	private void init() throws Exception{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (IOException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}
	
	  private void initXML(String taxid){
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	RowSet rs = null;
	    	try{
	    		rs=dao.search("select param from gz_tax_rate where taxid = "+taxid);
	    		if(rs.next()){
	    			xml=Sql_switcher.readMemo(rs,"param");
	    		}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	  public String getParam(String paramValue){
		  String return_str="";
	    	Element param = new Element("param");
	    	param.setAttribute("TaxModeCode",paramValue);
	    	Document myDocument = new Document(param);
	    	XMLOutputter outputter = new XMLOutputter();
	    	Format format = Format.getPrettyFormat();
	    	format.setEncoding("UTF-8");
	    	outputter.setFormat(format);
	    	return_str = outputter.outputString(myDocument);
	    	return return_str;
	  }
	  /**
	   * 取得对应的属性值
	   * @param name
	   * @return
	   */
	  public String getValue(String name)
	  {
		String ctrl_str = "";
  		try{
			XPath  xpath = XPath.newInstance("/param");
			Element param = (Element)xpath.selectSingleNode(this.doc);
			if(param != null)
				ctrl_str = param.getAttributeValue(name);
		}catch(JDOMException je){
			je.printStackTrace();
		}
		return ctrl_str;
	  }
	  
	  public String getParamValue(String taxid) throws Exception{
	    	String ctrl_str = "";
	    	this.initXML(taxid);
	    	if(xml == null || xml.trim().length()<=0)
	    		return ctrl_str;
	    	else{
	    		init();
	    		try{
	    			XPath  xpath = XPath.newInstance("/param");
	    			Element param = (Element)xpath.selectSingleNode(this.doc);
	    			if(param != null)
	    				ctrl_str = param.getAttributeValue("TaxModeCode");
	    		}catch(JDOMException je){
	    			je.printStackTrace();
	    		}
	    		catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    		
	    	
	    	return ctrl_str;
	    }
	  
	  public void updateParamValue(String paramValue,String taxid){
	    	try{
	    		String sql = "update gz_tax_rate set param ='"+paramValue+"' where taxid = "+taxid;
	    		ContentDAO  dao = new ContentDAO(this.conn);
	    		dao.update(sql);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	  
	  public HashMap getAllValues(String ids){
		  HashMap map =new HashMap();
		  try{
	    	    if(ids.trim().length()<=0)
	    		    return map;
	    	    if(ids.trim().length()>0&&ids.indexOf(",")==-1)
     	    	 {
	    		    map.put(ids,getParamValue(ids));
	    		    return map;
	         	 }
	        	String[] id_Arr=ids.split(",");
		
		        for(int i=0;i<id_Arr.length;i++){
			        map.put(id_Arr[i],getParamValue(id_Arr[i]));
			  
		        }
		        
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return map;
	   }

}

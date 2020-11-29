package com.hjsj.hrms.businessobject.performance.commend;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:CommendXMLBo.java</p>
 * <p>Description:在p02表中增加xml格式参数ctrl_param</p>
 * <p>Company:HJSJ</p>
 * <p>Create time: 2007.05.25  13:00:00 pm</p>
 * @author lizhenwei
 * @version 4.0
 * ctrl_param的格式
 * <?xml version='1.0' encoding="GB2312"?>
 *  <root>
 *     <vote_count>2</vote_count>#最多推荐人数
 *     <commend_field>AXXXX</commend_field>#推荐职务指标
 *  </root>   
 */


public class CommendXMLBo {
	private Connection conn;
	private Document doc;
	private HashMap map;
	private String xml;
	/**最多推荐人数*/
	public static final int vote_count=1;
	/**推荐职务指标*/
	public static final int commend_field=2;
    
	public  CommendXMLBo(){
		
	}
    public CommendXMLBo(Connection conn){
    	this.conn=conn;
    	
    }
    /**
     * 将p02表的ctrl_param参数转换成xml格式
     * @param paramValue
     * @return
     */
    public String getCtrl_param(String paramValue,String commendField){
    	String return_str="";
    	Element root = new Element("root");
    	Element voteCountElement = new Element("vote_count");
    	Element commendFieldElement= new Element("commend_field");
    	voteCountElement.addContent(paramValue);
    	commendFieldElement.addContent(commendField);
    	root.addContent(voteCountElement);
    	root.addContent(commendFieldElement);
    	Document myDocument = new Document(root);
    	XMLOutputter outputter = new XMLOutputter();
    	Format format = Format.getPrettyFormat();
    	format.setEncoding("UTF-8");
    	outputter.setFormat(format);
    	return_str = outputter.outputString(myDocument);
    	return return_str;

    }
    public String updateCtrlParamValue(String paramValue,String p0201,int elementType){
    	
    	String return_str="";
    	try{
    	    String name=this.getElementName(elementType);
    	    this.initXML(p0201);
        	if(xml == null || xml.trim().length()<=0) {
                return return_str;
            } else{
        		init();
        		XPath xPath=XPath.newInstance("/root");
        		Element element =(Element)xPath.selectSingleNode(this.doc);
        		if(element != null)
        		{
        			element.removeChild(name);
        		}
        		else
        		{
        			element = new Element("root");
        		}
        		Element childElement= new Element(name);
        		childElement.addContent(paramValue);
        		element.addContent(childElement);
   	            //Document myDocument = new Document(root);
    	        XMLOutputter outputter = new XMLOutputter();
    	        Format format = Format.getPrettyFormat();
    	        format.setEncoding("UTF-8");
    	        outputter.setFormat(format);
    	        return_str = outputter.outputString(this.doc);
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return return_str;

    }
    
    private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    private void initXML(String p0201){
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try{
    		rs=dao.search("select ctrl_param from p02 where p0201 = "+p0201);
    		if(rs.next()){
    			xml=Sql_switcher.readMemo(rs,"ctrl_param");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 得到参数的值
     */
    public String getCtrl_paraValue(String p0201,int elementType) throws GeneralException{
    	String ctrl_str = "";
    	this.initXML(p0201);
    	if(xml == null || xml.trim().length()<=0) {
            return ctrl_str;
        } else{
    		init();
    		String name=this.getElementName(elementType);
    		try{
    			XPath  xpath = XPath.newInstance("/root/"+name);
    			Element ctrl_param = (Element)xpath.selectSingleNode(this.doc);
    			if(ctrl_param != null) {
                    ctrl_str = ctrl_param.getValue();
                }
    		}catch(JDOMException je){
    			je.printStackTrace();
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    		
    	
    	return ctrl_str;
    }
    /**
     * 设置参数
     * @param list
     */
    public void setCtrl_paramValue(ArrayList list,int elementType){
    	try{
    	     for(int i=0;i<list.size();i++){
    		     LazyDynaBean bean = (LazyDynaBean)list.get(i);
    		     String s = this.updateCtrlParamValue((String)bean.get("ctrl_param"),(String)bean.get("p0201"),elementType);
    		     this.updateCtrl_paramValue(s,(String)bean.get("p0201"));
    	     }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    /**
     * 更新参数
     * @param paramValue
     * @param p0201
     */
    public void updateCtrl_paramValue(String paramValue,String p0201){
    	try{
    		String sql = "update p02 set ctrl_param ='"+paramValue+"' where p0201 = "+p0201;
    		ContentDAO  dao = new ContentDAO(this.conn);
    		dao.update(sql);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
   public String getElementName(int elementType){
	   String name="";
	   switch(elementType){
	   case vote_count:
		   name="vote_count";
		   break;
	   case commend_field:
		   name="commend_field";
		   break;
	   }
	   return name;
   }
    
}

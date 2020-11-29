package com.hjsj.hrms.businessobject.performance.achivement;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/*
<?xml version="1.0" encoding="GB2312"?>
<param  convert="0|1" >  # 
	<method  type="0|1" rule="0|1"/>#计分方式,仅对加分指标或扣分指标有效
	<l_method type="0|1" rule="0|1|2"> #基本指标，计算规则 type=0|1（差额|比例）,
		<add_score  type="0|1" value="" score="" valid="0|1"/>#加分
		<minus_score  type="0|1" value="" score="" valid="0|1"/>#扣分
	</l_method>
</param>
*/

/**
 * 
 *<p>Title:</p> 
 *<p>Description:解析考核基本指标 要素属性（xml）</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 8, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class PointCtrlXmlBo {
	private Connection conn;
	private Document doc;
	private String xml;
	private String point_id;
	/**参数根节点*/
	public static final int param=1; 
    public static final int l_method=2;
    public static final int add_score=3;
    public static final int minus_score=4;
    
    public PointCtrlXmlBo(String point_id,Connection conn)
    {
    	this.point_id=point_id;
    	this.conn=conn;
    }
    public PointCtrlXmlBo()
    {
    	
    }
    /**
     * 取得元素节点
     * @param type
     * @return
     */
    public String getElementName(int type)
    {
    	String name="";
    	switch(type)
    	{
    	case param:
    	{
    		name="param";
    		break;
    	}
    	case l_method:
    	{
    		name="l_method";
    		break;
    	}
    	case add_score:
    	{
    		name="add_score";
    		break;
    	}
    	case minus_score:
    	{
    		name="minus_score";
    		break;
    	}
    	}
    	return name;
    }
    /**
     * 取得元素路径
     * @param type
     * @return
     */
    public String getElementParentPath(int type)
    {
    	String path="/param";
    	switch(type)
    	{
    	case param:
    	{
    		path="/param";
    		break;
    	}
    	case l_method:
    	{
    		path="/param";
    		break;
    	}
    	case add_score:
    	{
    		path="/param/l_method";
    		break;
    	}
    	case minus_score:
    	{
    		path="/param/l_method";
    		break;
    	}
    	}
    	return path;
    }
    public void setPropertyValue(int type,String p1,String v1,String p2,String v2,String p3,String v3,String p4,String v4)
    {
    	String name=this.getElementName(type);
    	if(!(name==null&& "".equals(name)))
    	{
    		try
    		{
        		String path=this.getElementParentPath(type);
         		XPath xpath=XPath.newInstance(path);
         		Element element = (Element)xpath.selectSingleNode(doc);
         		Element child=null;
         		if(element!=null)
         		{
         			
             		if(type==PointCtrlXmlBo.param)
            		{
             			element.setAttribute(p1,v1);
             		}
             		else if(type == PointCtrlXmlBo.l_method)
             		{
             			child=element.getChild(name);
             			if(child!=null)
             			{
             				element.removeChildren(name);
             			}
             			child= new Element(name);
             			child.setAttribute(p1,v1);
             			child.setAttribute(p2,v2);
             		}
             		else if(type==PointCtrlXmlBo.add_score||type == PointCtrlXmlBo.minus_score)
             		{
             			child=element.getChild(name);
             			if(child!=null)
             			{
             				element.removeChildren(name);
             			}
             			child= new Element(name);
             			child.setAttribute(p1,v1);
             			child.setAttribute(p2,v2);
             			child.setAttribute(p3, v3);
             			child.setAttribute(p4,v4);
             		}
             		if(type!=PointCtrlXmlBo.param)
             		{
             			element.addContent(child);
             		}
         		}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    /**
     * 初始化xml
     *
     */
    public void initXML()
    {
    	StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<param>");
 		temp_xml.append("</param>");	
    	try
    	{
        	
     		ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select pointctrl from per_point where UPPER(point_id)='"+this.point_id.toUpperCase()+"'");
			if(rowSet.next()) {
                xml=Sql_switcher.readMemo(rowSet,"pointctrl");
            }
			if(xml==null|| "".equals(xml))
			{
				xml=temp_xml.toString();
			}
			doc=PubFunc.generateDom(xml);

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 保存参数
     *
     */
    public void updatePointCtrl()
    {
    	PreparedStatement pstmt = null;		
    	DbSecurityImpl dbS = new DbSecurityImpl();
    	try
    	{
    		StringBuffer buf=new StringBuffer();
    		StringBuffer sql = new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			sql.append(" update per_point set pointctrl=? where UPPER(point_id)='"+this.point_id.toUpperCase()+"'");
			pstmt = this.conn.prepareStatement(sql.toString());	
			switch(Sql_switcher.searchDbServer())
			{
			  case Constant.MSSQL:
			  {
				  pstmt.setString(1, buf.toString());
				  break;
			  }
			  case Constant.ORACEL:
			  {
				  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
				          getBytes())), buf.length());
				  break;
			  }
			  case Constant.DB2:
			  {
				  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
				          getBytes())), buf.length());
				  break;
			  }
			}
			// 打开Wallet
			dbS.open(this.conn, sql.toString());
			pstmt.executeUpdate();	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
		{
			try
			{
				if(pstmt!=null)
				{
					pstmt.close();
				}
				// 关闭Wallet
				dbS.close(this.conn);
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
    }
	
	public static HashMap getAttributeValues(String pointCtrl_str)
	{
		HashMap map=new HashMap();
		if(pointCtrl_str!=null&&pointCtrl_str.length()>0)
		{
			try
			{
				Document doc=init(pointCtrl_str.toLowerCase());
				XPath xPath = XPath.newInstance("/param");
				Element element= (Element) xPath.selectSingleNode(doc);
				if(element!=null)
				{
					map.put("convert", element.getAttributeValue("convert"));  //0|1,等于1按折算计分
				}
				xPath = XPath.newInstance("/param/l_method");
				element= (Element) xPath.selectSingleNode(doc);
				if(element!=null)
				{
					map.put("computeType",element.getAttributeValue("type"));  //计算方式  0|1（差额|比例） 
					map.put("computeRule",element.getAttributeValue("rule"));  //计算规则  0|1|2|3 计分规则（录分｜简单｜分段｜排名）
					
					xPath = XPath.newInstance("/param/l_method/add_score");
					Element element2= (Element) xPath.selectSingleNode(doc);
					if(element2!=null)
					{
						map.put("addValid", element2.getAttributeValue("valid"));  //0|1(无效|有效)，有效则进行加分处理
						map.put("addType",  element2.getAttributeValue("type"));   //=0|1(每低|每高)
						map.put("addValue", element2.getAttributeValue("value"));
						map.put("addScore",element2.getAttributeValue("score"));
						
					}
					xPath = XPath.newInstance("/param/l_method/minus_score");
					element2= (Element) xPath.selectSingleNode(doc);
					if(element2!=null)
					{
						map.put("minusValid", element2.getAttributeValue("valid"));  //0|1(无效|有效)，有效则进行加分处理
						map.put("minusType",  element2.getAttributeValue("type"));   //=0|1(每低|每高)
						map.put("minusValue", element2.getAttributeValue("value"));
						map.put("minusScore",element2.getAttributeValue("score"));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	

	public static Document init(String str) throws GeneralException{
		Document doc;
		byte[] b = str.getBytes();
		try {
			doc = PubFunc.generateDom(str);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return doc;
	}
	
	
	
}

package com.hjsj.hrms.businessobject.performance.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import org.jdom.Document;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class ConfigParameterBo
{

    private String redio;

    private String xmlcontent = "";

    private Document doc;

    public String getXmlcontent()
    {

	return xmlcontent;
    }

    public void setXmlcontent(String xmlcontent)
    {

	this.xmlcontent = xmlcontent;
    }

    public Document getDoc()
    {

	return doc;
    }

    public void setDoc(Document doc)
    {

	this.doc = doc;
    }

    public String getRedio()
    {

	return redio;
    }

    public void setRedio(String redio)
    {

	this.redio = redio;
    }

    private Connection conn = null;

    public ConfigParameterBo(Connection conn)
    {

	this.conn = conn;
	init();
	try
	{
	    doc = PubFunc.generateDom(xmlcontent);

	} catch (Exception ex)
	{
	    ex.printStackTrace();
	}
    }

    public RecordVo getPlanVo(String id)
    {

	RecordVo vo = new RecordVo("constant");
	try
	{
	    vo.setInt("id", Integer.parseInt(id));
	    ContentDAO dao = new ContentDAO(this.conn);
	    vo = dao.findByPrimaryKey(vo);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return vo;
    }

    /**
         * 原来的方法
         * 
         */
//    private void init()
//    {
//
//	RecordVo vo = new RecordVo("constant");
//	vo.setString("constant", "GZ_PARAM");
//	StringBuffer temp_xml = new StringBuffer();
//	temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
//	temp_xml.append("<Per_Parameters>");
//	temp_xml.append("<Plan MarkingMode=\"1\"/>");
//	temp_xml.append("</Per_Parameters>");
//	try
//	{
//	    ContentDAO dao = new ContentDAO(this.conn);
//	    vo = dao.findByPrimaryKey(vo);
//	    if (vo != null)
//		xmlcontent = vo.getString("str_value");
//	    if (xmlcontent == null || xmlcontent.equals(""))
//	    {
//		xmlcontent = temp_xml.toString();
//	    }
//	} catch (Exception ex)
//	{
//	    xmlcontent = temp_xml.toString();
//	}
//
//    }

    /**
         * modified by fanzhiguo
         */
    private void init()
    {

	StringBuffer temp_xml = new StringBuffer();
	temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
	temp_xml.append("<Per_Parameters>");
	temp_xml.append("<Plan MarkingMode=\"1\"/>");
	temp_xml.append("</Per_Parameters>");

	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search("select constant,str_value from constant where constant='GZ_PARAM'");
	    if (rowSet.next())
	    {
		String str_value = rowSet.getString("str_value");
		if (str_value != null && !"".equals(str_value)) {
            xmlcontent = str_value;
        } else {
            xmlcontent = temp_xml.toString();
        }

	    } else {
            xmlcontent = temp_xml.toString();
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
         * 如果数据库中没有这个名称记录则插入
         * 
         * @param param_name
         */
    public void ifNoParameterInsert(String param_name)
    {

	String sql = "select * from constant where UPPER(Constant)='" + param_name.toUpperCase() + "'";
	ContentDAO dao = new ContentDAO(conn);
	RowSet rs = null;
	try
	{
	    rs = dao.search(sql);
	    if (!rs.next())
	    {
		insertNewParameter(param_name);
	    } else
	    {
		String xml = rs.getString("str_value");
		if(xml==null) {
            return;
        }
		String info = xml.substring(xml.indexOf("MarkingMode") + 13, xml.indexOf("MarkingMode") + 14);
		if (!("2".equals(info) || "1".equals(info)))
		{
		    insertNewParameter(xmlcontent);
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
         * 插入
         * 
         * @param param_name
         */
    public void insertNewParameter(String param_name)
    {

	String insert = "insert into constant(Constant) values (?)";
	ArrayList list = new ArrayList();
	list.add(param_name.toUpperCase());
	ContentDAO dao = new ContentDAO(conn);
	try
	{
	    dao.insert(insert, list);

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public String getParam(String param_name)
    {

	String sql = "select * from constant where UPPER(Constant)='" + param_name.toUpperCase() + "'";
	String paramValue = "";
	ContentDAO dao = new ContentDAO(conn);
	RowSet rs = null;
	try
	{
	    rs = dao.search(sql);
	    if (rs.next())
	    {
		paramValue = rs.getString("str_value");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return paramValue;
    }
}

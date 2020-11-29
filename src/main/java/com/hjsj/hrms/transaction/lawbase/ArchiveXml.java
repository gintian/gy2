package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 * 文档指标设置
 * @author Administrator
 *
 */
public class ArchiveXml
{
    /**
     * 将参数放到xml文件中并返回一个string
     */
    public String strToXml(String usablefields, String tablefields)
    {
        Document doc = new Document();
        Element root = new Element("params");
        doc.setRootElement(root);
        if(!"".equals(usablefields))
            root.addContent(new Element("item").addContent(usablefields));
        if(!"".equals(tablefields))
        root.addContent(new Element("listing").addContent(tablefields));

        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter xmlout = new XMLOutputter(format);

        return xmlout.outputString(doc);
    }
    
    /**
     * 获取xml文件里面的某个节点的值
     */
    public String getElement(String element,String xmlMessage){
        String fields = "";
        Document doc;
        try
        {
            doc = PubFunc.generateDom(xmlMessage);
            Element root = doc.getRootElement();
            if (xmlMessage.indexOf(element)!=-1)
            {
                fields = ((Element) root.getChildren(element).get(0)).getText();
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return fields;
    }
    
    /**
     * 获取文档指标设置的一个Map
     * @param dao
     * @param fileId
     * @return
     */
    public Map getFileItemSet(ContentDAO dao,String fileId){
    	Map map = new HashMap();
    	StringBuffer sql = new StringBuffer();
    	String fieldStr = "";
		String itemStr = "";
		String listStr  = "";
			sql.append("select * from law_base_struct where base_id = '" + fileId + "'");
		ArchiveXml xml = new ArchiveXml();
		ResultSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				fieldStr = rs.getString("field_str");
			}
			
			if (fieldStr != null && !"".equals(fieldStr)) {
				itemStr = this.getElement("item", fieldStr);
				listStr = this.getElement("listing", fieldStr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (rs != null) 
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			map.put("itemStr", itemStr);
			map.put("listStr", listStr);
		}
		return map;
	}
    
    /**
     * 获取有附件的文档
     * @param base_id
     * @param dao
     * @return
     */
	public static HashMap getFileExt(String base_id,ContentDAO dao) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select * from law_base_file");
		sb.append(" inner join law_ext_file");
		sb.append(" on law_base_file.file_id = law_ext_file.file_id");
		sb.append(" where base_id = '" + base_id + "'");
		sb.append(" or base_id in (select base_id from law_base_struct where up_base_id = '" + base_id +"')");
		HashMap map = new HashMap();
		ResultSet rs = null;
		try 
		{
			rs = dao.search(sb.toString());
			while (rs.next()) 
			{
				map.put(rs.getString("file_id"), "1");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs!=null) 
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	/**
     * 获取有附件的文档
     * @param dao
     * @return map
     */
	public static HashMap getFileExtMap(ContentDAO dao) {
		StringBuffer sb = new StringBuffer();
		sb.append("select law_ext_file.file_id from law_base_file");
		sb.append(" inner join law_ext_file");
		sb.append(" on law_base_file.file_id = law_ext_file.file_id");
		HashMap map = new HashMap();
		ResultSet rs = null;
		try 
		{
			rs = dao.search(sb.toString());
			while (rs.next()) 
			{
				map.put(rs.getString("file_id"), "1");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 获取指标长度
	 * @param field
	 * @param dao
	 * @return
	 */
	public int getFiledLength(String field,ContentDAO dao){
		int len = 0;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ITEMLENGTH FROM t_hr_busifield WHERE ITEMID = '" + field + "' AND FIELDSETID = 'LAW_BASE_FILE'");
		try {
			rs = dao.search(sb.toString());
			if (rs.next()) 
			{
				len = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return len;
	}
}

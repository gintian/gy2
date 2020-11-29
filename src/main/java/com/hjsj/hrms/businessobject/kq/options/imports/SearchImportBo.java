package com.hjsj.hrms.businessobject.kq.options.imports;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
import java.util.List;
/**
 * 
 * <p>Title：考勤规则导入指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 28, 2010:6:27:23 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SearchImportBo {
	
	private Connection conn;
	private String xmlcontent="";
	private Document doc;
	public SearchImportBo(Connection conn,String akq_item1)
	{
		this.conn = conn;
		init(akq_item1);
		try
		{
			doc=PubFunc.generateDom(xmlcontent.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 
	 * @param xmlcontent
	 */
	public SearchImportBo(String xmlcontent)
	{
		try
		{
			doc=PubFunc.generateDom(xmlcontent.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 读参数的内容
	 *
	 */
	private void init(String akq_item1)
	{
		RecordVo vo=new RecordVo("kq_item");
		vo.setString("item_id",akq_item1);
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
                xmlcontent=vo.getString("other_param");
            }
			if(xmlcontent==null|| "".equals(xmlcontent))
			{
				xmlcontent=strxml.toString();
			}
		}
		catch(Exception ex)
		{
			xmlcontent=strxml.toString();
			ex.printStackTrace();
		}
	}
	/**
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(String property,String value)
	{
		boolean bflag=true;
		String name="import";
		if(value==null) {
            value="";
        }
		if("#".equals(value)) {
            value="";
        }
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					element.setAttribute(property,value);
					doc.getRootElement().addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					element.setAttribute(property,value);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				  bflag=false;
			}
		}
		return bflag;
	}
	/**
	 * 保存参数，先设置参数值，再保存
	 * @throws GeneralException
	 */
	public void saveParameter(String akq_item1)throws GeneralException
	{
	    DbSecurityImpl dbs = new DbSecurityImpl();
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		try
		{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean iscorrect=if_vo_Empty(akq_item1);
			strsql.append("update kq_item set other_param=? where item_id='"+akq_item1+"'");
			pstmt = this.conn.prepareStatement(strsql.toString());
			switch(Sql_switcher.searchDbServer())
			{
			  case Constant.MSSQL:
			  {
				  pstmt.setString(1, buf.toString());
				  break;
			  }
			  case Constant.ORACEL:
			  {
				 /* pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
				          getBytes())), buf.length());*/
				  pstmt.setString(1, buf.toString());
				  break;
			  }
			  case Constant.DB2:
			  {
				  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
				          getBytes())), buf.length());
				  break;
			  }
			}
			
			dbs.open(conn, strsql.toString());
			pstmt.executeUpdate();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try {
				if (pstmt != null) {
                    pstmt.close();
                }
			} catch (SQLException ee) {
				ee.printStackTrace();
			}	
			dbs.close(conn);
		}
	}
	public boolean if_vo_Empty(String akq_item1)
	{
		String sql="select * from kq_item where item_id='"+akq_item1+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		  boolean is_correct=true;
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  is_correct=false; 
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }	
		  return is_correct;
	}
	public String getValue(String property)
	{
		String value="";
		String name="import";
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(property);	
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value;		
	}
}

package com.hjsj.hrms.businessobject.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
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
import java.util.ArrayList;
import java.util.List;
/**
 * 
 *<p>Title:EngageParamXML.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class EngageParamXML {

	public final static int APP_COUNT=0;//#最多申请职位数
	public final static int CARD=1;//#样板表格号，登记表号
	public final static int APP_VIEW=2;//#员工申请显示指标列表
	public final static int ATTENT_VIEW=3;//#参选者名册指标列表
	public final static int TEMPLATE=4;//#模板号

	private Connection conn;
	private String xmlcontent;
	private Document doc;
	public EngageParamXML(Connection conn)
	{
		this.conn=conn;
		init();
		try
		{
			this.doc = PubFunc.generateDom(xmlcontent);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//see();
	}
	/**
	 * 初始化
	 *
	 */
	private void init()
	{
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","JP_PARAM");
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<root>");
		strxml.append("</root>");		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from constant where constant = 'JP_PARAM' ";
			RowSet rs = dao.search(sql);
			if(!rs.next()){
				dao.addValueObject(vo);
			}
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
                this.xmlcontent=vo.getString("str_value");
            }
			if(this.xmlcontent==null|| "".equals(this.xmlcontent))
			{
				this.xmlcontent=strxml.toString();
			}
		}
		catch(Exception ex)
		{
			this.xmlcontent=strxml.toString();
			ex.printStackTrace();
		}
	}
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		  case APP_COUNT:
			name="app_count";
			break;
		  case CARD:
			name="card";
			break;
		  case APP_VIEW:
			name="app_view";
			break;
		  case ATTENT_VIEW:
			name="attent_view";
			break;
		  case TEMPLATE:
				name="template";
				break;
		}
		return name;
	}
	/**
	 * 得到节点属性值
	 * @param param_type
	 * @param property
	 * @return
	 */
	public String getValue(int param_type,String property)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/root/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
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
	/**
	 * 得到节点的值
	 * @param param_type
	 * @return
	 */
	public String getTextValue(int param_type)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/root/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getText();	
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value;		
	}
	/**
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String property,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(value==null) {
            value="";
        }
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/root/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setAttribute(property,value);
				this.doc.getRootElement().addContent(element);
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
	 * 设置节点内容
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setTextValue(int param_type,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(value==null) {
            value="";
        }
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/root/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setText(value);
				this.doc.getRootElement().addContent(element);
			}
			else
			{
				element=(Element)childlist.get(0);
				element.setText(value);
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
	public void saveParameter()throws GeneralException
	{
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList values = new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean iscorrect=if_vo_Empty("JP_PARAM");
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
				values.add("JP_PARAM");
				values.add(buf.toString());
				dao.insert(strsql.toString(), values);
				/*pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "JP_PARAM");
				pstmt.setString(2, buf.toString());				*/
			}
			else
			{
				DbSecurityImpl dbS = new DbSecurityImpl();
				try{
					strsql
							.append("update constant set str_value=? where constant='JP_PARAM'");
					pstmt = this.conn.prepareStatement(strsql.toString());
					switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {
						pstmt.setString(1, buf.toString());
						break;
					}
					case Constant.ORACEL: {
						pstmt.setCharacterStream(1, new InputStreamReader(
								new ByteArrayInputStream(buf.toString()
										.getBytes())), buf.length());
						break;
					}
					case Constant.DB2: {
						pstmt.setCharacterStream(1, new InputStreamReader(
								new ByteArrayInputStream(buf.toString()
										.getBytes())), buf.length());
						break;
					}
					}
					// 打开Wallet
					dbS.open(conn, strsql.toString());
					pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						// 关闭Wallet
						dbS.close(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
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
		}
	}	
	public boolean if_vo_Empty(String constant)
	{
		  String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
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
    public ArrayList getUnit_card(String [] unit_cards)
    {
    	ContentDAO dao=new ContentDAO(this.conn);
    	CommonData da=null;
    	ArrayList card_list=new ArrayList();
    	for(int i=0;i<unit_cards.length;i++)
		{
			da=getCardMess(unit_cards[i],dao);
			if(da!=null) {
                card_list.add(da);
            }
		}
    	return card_list;
    }
	private CommonData getCardMess(String tabid,ContentDAO dao)
	{
          String sql="select tabid,name  from rname where tabid = '"+tabid+"'";	
          CommonData da=new CommonData();
          try
          {
        	  RowSet rs=dao.search(sql);
        	  if(rs.next()) {
                  da.setDataName(rs.getString("name"));
              }
				  da.setDataValue(rs.getString("tabid"));				
          }catch(Exception e)
          {
        	  e.printStackTrace();
          }
          return da;
	}
}

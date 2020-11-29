package com.hjsj.hrms.businessobject.general.deci.leader;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeadarParamXML {

	public final static int TEAM_LEADER=0;//#班子标识参数
	public final static int CANDID_LEADER=1;//#后备干部标识参数
	public final static int OUTPUT=2;//#基本材料输出指标列表
	public final static int DISPLAY=3;//#显示指标列表
	public final static int CONDI_DISPLAY=10;//#显示指标列表
	public final static int GCOND=4;//班子分析，常用统计条件
	public final static int UNIT_CARD=5;//#单位登记表,用于显示职数
	public final static int BZDBPRE=6; //#选择的班子库名
	public final static int HBDBPRE=7; //#选择的班子库名
	public final static int UNIT_ZJ=8; //#单位子集表
	public final static int LOADTYPE=9;//#设置组织树显示状态
	private Connection conn;
	private String xmlcontent;
	private Document doc;
	public LeadarParamXML(Connection conn)
	{
		this.conn=conn;
		init();
		try
		{
			//xus 20/4/23 xml 编码改造
			doc = PubFunc.generateDom(xmlcontent.toString());
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
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<root>");
		strxml.append("</root>");		
		
		RecordVo vo = ConstantParamter.getRealConstantVo("RM_PARAM", this.conn);
		if(vo==null){
			this.xmlcontent=strxml.toString();
			return;
		}
		String xml = vo.getString("str_value");
		if(xml==null || xml.length()<1)//xiegh 20170703
        {
            this.xmlcontent=strxml.toString();
        } else {
            this.xmlcontent=xml;
        }
		
		/* 针对昆仑数据库用dao.findByPrimaryKey查询constant表出错，改用上面方式
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","RM_PARAM");
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<root>");
		strxml.append("</root>");		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from constant where constant = 'RM_PARAM' ";
			RowSet rs = dao.search(sql);
			if(!rs.next()){
				dao.addValueObject(vo);
			}
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
				this.xmlcontent=vo.getString("str_value");
			if(this.xmlcontent==null||this.xmlcontent.equals(""))
			{
				this.xmlcontent=strxml.toString();
			}
		}
		catch(Exception ex)
		{
			this.xmlcontent=strxml.toString();
			ex.printStackTrace();
		}
		*/
	}
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		  case TEAM_LEADER:
			name="team_leader";
			break;
		  case CANDID_LEADER:
			name="candi_leader";
			break;
		  case OUTPUT:
			name="output";
			break;
		  case DISPLAY:
			name="display";
			break;
		  case CONDI_DISPLAY:
			name="condi_display";
			break;
		  case GCOND:
		    name="gcond";
			break;
		  case UNIT_CARD:
			name="unit_card";
			break;
		  case BZDBPRE:
			name="bzdbpre";
			break;
		  case HBDBPRE:
			name="hbdbpre";
			break;
		  case UNIT_ZJ:
			name="unit_zj";
			break;
		  case LOADTYPE:
		  	name="loadtype";
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
		try
		{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean iscorrect=if_vo_Empty("RM_PARAM");
			ContentDAO dao = new ContentDAO(conn);				
			ArrayList paramList = new ArrayList();
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");
				paramList.add("RM_PARAM");
				paramList.add(buf.toString());				
			}
			else
			{
				strsql.append("update constant set str_value=? where constant='RM_PARAM'");
				/*
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
				*/
				paramList.add(buf.toString());				
			}
			dao.update(strsql.toString(), paramList);	
			//pstmt.executeUpdate();			
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
		/*System.out.println("^^^^^^^^^^^^^^保存");
		see();*/
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
	public void see()
	{
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		System.out.println(outputter.outputString(doc));
	}
}

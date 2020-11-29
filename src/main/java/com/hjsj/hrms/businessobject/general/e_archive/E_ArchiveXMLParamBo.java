package com.hjsj.hrms.businessobject.general.e_archive;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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
import java.util.Iterator;
import java.util.List;

/** 
 * <p>Title:E_ArchiveXMLParamBo.java</p>
 * <p>Description:解析常量表的INFOM, 格式：
 * 常量表 INFOM:
  <?xml version="1.0" encoding="GB2312"?>
  <INFOM>
	  <!-- 单位: KB -->
	  <Photo MaxSize="10" />
	  <!-- 档案 -->
         <doc>
		<catalogset setid="" />   <!-- 档案目录 -->
		<docoutset setid="" />    <!-- 材料转出 -->
		<doczdset setid="" />     <!-- 档案转递 -->
		<!-- 
                  SNLen: 档案文件名中，编号位数
                  DocFileName: 档案文件名指标
		 -->
		<ftp snlen="" docfilename="" >
			<!-- 
	                  档案号段设置
			  startDocNo, endDocNo: 档案起止号
			-->
			<group startdocno="" enddocno="" />
			<gruop startdocno="" enddocno="" />
		</ftp>
 	</doc>
 	<!-- 虚拟机构 -->
 	<virtualorg showvirtualorg="True" />
 	<!-- 子集列表时，是否每条记录包含主集信息 -->
 	<subsetlist repeatmainset="False" />
 	<!-- 身份证号即时校验 -->
 	<idcard check="True" />
 	<!-- 列表时，修改数据后提示保存 -->
 	<list saveprompt="False" />
    </INFOM>
   </p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-10-12 11:41:29</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class E_ArchiveXMLParamBo {
	public static final int PHOTO=1;
	public static final int DOC=2;
	/**档案目录*/
	public static final int CATALOGSET=3;
	/**材料转出*/
	public static final int DOCOUTSET=4;
	/**档案转递*/
	public static final int DOCZDSET=5;
	public static final int FTP=6;
	/**档案号段设置*/
	public static final int GROUP=7;
	/**虚拟机构*/
	public static final int VIRTUALORG=8;
	/**子集列表时，是否每条记录包含主集信息*/
	public static final int SUBSETLIST=9;
	/**身份证号即时校验*/
	public static final int IDCORD=10;
	/**列表时修改数据提示保存*/
	public static final int LIST=11;
	/**有多个相同元素*/
	public static final int MORE=2;
	/**就有一个相同元素*/
	public static final int ONE=1;
	/**文档对象*/
	private Document doc;
	/**数据库连接*/
	private Connection conn;
    /**xml串*/
	private String xml="";
	public E_ArchiveXMLParamBo()
	{
		super();
	}
	public E_ArchiveXMLParamBo(Connection conn)
	{
		this.conn=conn;
		init();
		try
		{
			//xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(xml);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 读取常量表中INFOM字段的内容，如果不存在，取默认值
	 *
	 */
	private void init()
	{
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","INFOM");
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<infom>");
		temp_xml.append("</infom>");		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
                xml=vo.getString("str_value");
            }
			if(xml==null|| "".equals(xml))
			{
				xml=temp_xml.toString();
			}
		}
		catch(Exception ex)
		{
			xml=temp_xml.toString();
			ex.printStackTrace();
		}
	}
	/**
	 * 取得参数在xml节点处的名称
	 * @param type
	 * @return
	 */
	public String getElementName(int type)
	{
		String name="";
		switch(type)
		{
		case PHOTO:
		{
			name="photo";
			break;
		}
		case DOC:
		{
			name="doc";
			break;
		}
		case CATALOGSET:
		{
			name="catalogset";
			break;
		}
		case DOCOUTSET:
		{
			name="docoutset";
			break;
		}
		case DOCZDSET:
		{
			name="doczdset";
			break;
		}
		case FTP:
		{
			name="ftp";
			break;
		}
		case GROUP:
		{
			name="group";
			break;
		}
		case VIRTUALORG:
		{
			name="virtualorg";
			break;
		}
		case SUBSETLIST:
		{
			name="subsetlist";
			break;
		}
		case IDCORD:
		{
			name="idcord";
			break;
		}
		case LIST:
		{
			name="list";
			break;	
		}
		}
		return name;
	}
	/**
	 * 如果数据库中没有这个名称记录则插入
	 * @param param_name  
	 */
	public void ifNoParameterInsert(String param_name)
	{
		  String sql="select * from constant where UPPER(Constant)='"+param_name.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  insertNewParameter(param_name);
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	/**
	 * 插入
	 * @param param_name
	 */
	public void insertNewParameter(String param_name)
	{
		String insert="insert into constant(Constant) values (?)";
		ArrayList list=new ArrayList();
		list.add(param_name.toUpperCase());			
		ContentDAO dao = new ContentDAO(conn);
		  try
		  {
			dao.insert(insert,list);		  
			  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		
	}
	/**判断是否有这个记录*/
	public boolean isHaveParameter(String param_name)
	{
		  String sql="select * from constant where UPPER(Constant)='"+param_name.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  boolean flag=true;
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  flag=false; 
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }	
		  return flag;
	}
	/**
	 * 保存各节点参数的值，要先设置后在保存
	 * @throws GeneralException
	 */
	public void saveParameters() throws GeneralException 
	{
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		try
		{
			ifNoParameterInsert("INFOM");
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			ContentDAO dao = new ContentDAO(conn);
			boolean flag=isHaveParameter("INFOM");
			ArrayList paramList = new ArrayList();
			if(!flag)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");								
				paramList.add("INFOM");
				paramList.add(buf.toString());				
			}
			else
			{
				paramList.add(buf.toString());		
				strsql.append("update constant set str_value=? where constant='INFOM'");
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
			}
			dao.update(strsql.toString(), paramList);		
			//pstmt.executeUpdate();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(pstmt!=null)
				{
					pstmt.close();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 取得节点的路径
	 * @param type
	 * @return
	 */
	public String getPath(int type)
	{
		String path="";
		switch(type)
		{
		case CATALOGSET:
		{
			path="/INFOM/doc/";
			break;
		}
		case DOCOUTSET:
		{
			path="/INFOM/doc/";
			break;
		}
		case DOCZDSET:
		{
			path="/INFOM/doc/";
			break;
		}
		case FTP:
		{
			path="/INFOM/doc/";
			break;
		}
		case GROUP:
		{
			path="/INFOM/doc/ftp/";
			break;
		}
		default:
		{
			path="/INFOM/";
		    break;
		}
		}
		return path;
	}
	
	/**
	 * 在该父节点下就有一个子节点的设置参数值
	 * @param type
	 * @param param_name
	 * @param param_value
	 * @param isMore
	 * @return
	 */
  public boolean setPropertyValue(int type,String property_name,String param_value)
  {
	  boolean flag=true;
	  String name=this.getElementName(type);
	  if(param_value==null) {
          param_value="";
      }
	  if(!"".equals(name))
	  {
		  try
		  {
			  String ppath=this.getPath(type);
			  String path=ppath+name;
			  XPath xpath=XPath.newInstance(path);
			  List childlist=xpath.selectNodes(doc);
			  Element element=null;
			  if(childlist.size()==0)//该节点不存在，要先建立其所有父节点
			  {
				  /*element=new Element(name);
				  element.setAttribute(param_name,param_value);
				  doc.getRootElement().addContent(element);*/
				  /**先建立父节点*/
				  String temp=ppath.substring(1,ppath.length()-1);//infom/doc/ftp
				  String[] temp_arr=temp.split("/");
				  String temp_path="/infom";
				  for(int i=1;i<temp_arr.length;i++)
				  {
					  String pat=temp_path;
					  temp_path+="/"+temp_arr[i];
					  xpath=XPath.newInstance(temp_path);
					  childlist=xpath.selectNodes(doc);
					  if(childlist.size()==0)
					  {
						  element=new Element(temp_arr[i]);
						  //element.setAttribute(param_name,param_value);
						  xpath=XPath.newInstance(pat);
						  Element pelement=(Element)xpath.selectSingleNode(doc);
						  pelement.addContent(element);
					  }else
					  {
						  continue;
					  }
				  }//父节点建立完毕,建立该节点并为其赋值
				  xpath=XPath.newInstance(ppath.substring(0,ppath.length()-1));
				  Element parentelement=(Element)xpath.selectSingleNode(doc);
				  element=new Element(name);
				  element.setAttribute(property_name,param_value);
				  parentelement.addContent(element);
			  }
			  else//该节点已经存在，更新其内容
			  {
				  element=(Element)childlist.get(0);
		    	  element.setAttribute(property_name,param_value);
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
			  flag=false;
		  }
	  }
	  return flag;
  }
  /**
   * 在该父节点下有多个相同子节点的参数保存
   * @param type
   * @param param_list
   * @param value_list
   * @return
   */
  public boolean setPropertiesValue(int type,ArrayList property_list,ArrayList value_list)
  {
	  boolean flag=true;
	  try
	  {
		  String name=this.getElementName(type);
		  if(!"".equals(name))
		  {
			  String ppath=this.getPath(type);
			  String path=ppath+name;
			  XPath xpath=XPath.newInstance(path);
			  List childlist=xpath.selectNodes(doc);
			  Element element=null;
			  /**该节点不存在，要先建立其所有父节点*/
			  if(childlist.size()==0)
			  {
				  /**先建立父节点*/
				  String temp=ppath.substring(1,ppath.length()-1);//infom/doc/ftp
				  String[] temp_arr=temp.split("/");
				  String temp_path="/infom";
				  /**temp_arr为该节点的所有父节点的名字组成的数组*/
				  for(int i=1;i<temp_arr.length;i++)
				  {
					  String pat=temp_path;
					  temp_path+="/"+temp_arr[i];
					  xpath=XPath.newInstance(temp_path);
					  childlist=xpath.selectNodes(doc);
					  if(childlist.size()==0)
					  {
						  element=new Element(temp_arr[i]);
						  xpath=XPath.newInstance(pat);
						  Element pelement=(Element)xpath.selectSingleNode(doc);
						  pelement.addContent(element);
					  }else
					  {
						  continue;
					  }
				  }
                  /**父节点建立完毕,建立该节点并为其赋值*/
				  xpath=XPath.newInstance(ppath.substring(0,ppath.length()-1));
				  Element parentelement=(Element)xpath.selectSingleNode(doc);
				  for(int i=0;i<property_list.size();i++)
				  {
					  element=new Element(name);
					  element.setAttribute((String)property_list.get(i),(String)value_list.get(i));
					  parentelement.addContent(element);
				  }
			  }
			  else//该节点已经存在，更新其内容
			  {
				  xpath=XPath.newInstance(ppath.substring(0,ppath.length()-1));
				  Element parentelement=(Element)xpath.selectSingleNode(doc);
				  /**删除原先存在的所有该子节点*/
				  parentelement.removeChildren(name);
				  /**从新建立子节点，并为参数赋值*/
				  for(int i=0;i<property_list.size();i++)
				  {
					  element=new Element(name);
					  element.setAttribute((String)property_list.get(i),(String)value_list.get(i));
					  parentelement.addContent(element);
				  }
			  }
			  
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
		  flag=false;
	  }
	  return flag;
  }
  /**
   * 取得属性的值单一属性
   * @param type
   * @param property
   * @return
   */
  public String getPropertyValue(int type,String property)
  {
	  String value="";
	  String name=this.getElementName(type);
	  if(!"".equals(name))
	  {
		  try
		  {
     		  String ppath=this.getPath(type);
    		  String path=ppath+name;
    		  XPath xpath=XPath.newInstance(path); 
    		  List childlist=xpath.selectNodes(doc);
    		  Element element=null;
    		  if(childlist.size()!=0)
    		  {
    			  element=(Element)childlist.get(0);
    			  value=element.getAttributeValue(property);
    		  }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  
	  }
	  return value;
  }
  /**
   * 取得多个相同元素的所有属性的值，返回值：每个元素用","分隔，每个元素的所有属性值用"`"分隔,主要针对group节点
   * @param type
   * @param property_1
   * @param property_2
   * @return
   */
  public String getPropertiesValue(int type,String property_1,String property_2)
  {
	  StringBuffer buf = new StringBuffer();
	  String name=this.getElementName(type);
	  if(!"".equals(name))
	  {
		  try
		  {
    		  String ppath=this.getPath(type);
    		  String path=ppath+name;
    		  XPath xpath=XPath.newInstance(path);
    		  List childlist=xpath.selectNodes(doc);
    		  Element element=null;
    		  Iterator i=childlist.iterator();
    		  while(i.hasNext())
    		  {
    			  element=(Element)i.next();
    			  buf.append(element.getAttributeValue(property_1)==null?"":element.getAttributeValue(property_1)
    					  +"`"+element.getAttributeValue(property_2)==null?"":element.getAttributeValue(property_2));
    			  buf.append(",");
    		  }
    		  if(buf.toString()!=null&&buf.toString().length()>0) {
                  buf.setLength(buf.length()-1);
              }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
	  }
	  return buf.toString();
  }
  
	

}

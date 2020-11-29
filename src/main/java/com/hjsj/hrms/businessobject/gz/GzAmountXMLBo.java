package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
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
import java.util.*;


/**
 * <p>Title:GzAmountXMLBo.java</p>
 * <p>Description:薪资总额xml参数解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class GzAmountXMLBo {
	/**<?xml version="1.0" encoding="GB2312"?>
	<Params>
		<Serive>  #CS版，建投EXCEL发放进度表的过滤条件
			<SeiveItem ID="1" Name="姓名&lt;&gt;" Expr="1" Factor="A0101&lt;&gt;`"/>
		</Serive>
		<Gz_amounts setid="BXX" sp_flag="BXXXX" ctrl_type="1|0">
		<Gz_amount orgid="UNxxx"> #单位编码
			<ctrl_item planitem="Bxxx" realitem="Axxxx" balanceitem="XXXX" flag="0|1"> 
				#定义的计算公式，主要用于发放明细计算至实发项目中去
			</ctrl_item>
			。。。
	     </Gz_amount>
		。。。
		</Gz_amounts>
	</Params>
*/
	private Connection conn;
	private Document doc;
	private String xml;
	static HashMap hm;
	private String param;
	private String ctrl_by_level="ctrl_by_level";
	private String ctrl_by_level_value="0";
	private UserView uv;
	public static final int Params=1; 
	public static final int Serive = 2;
	public static final int SeiveItem = 3;
	public static final int Gz_amounts = 4;
	public static final int Gz_amount = 5;
	public static final int ctrl_item = 6;
	public static final int Gz_check = 7;
	public static final int SUBSET = 8;
	
	public String getElementName(int type)
	{
		String name = "";
		switch(type)
		{
    		case Params:
    		{
    			name = "Params";
    			break;
    		}
    		case Serive:
    		{
    			name = "Serive";
    			break;
    		}
    		case SeiveItem:
    		{
    			name="SeiveItem";
    			break;
    		}
    		case Gz_amounts:
    		{
    			name="Gz_amounts";
    			break;
    		}
    		case Gz_amount:
    		{
    			name="Gz_amount";
    			break;
    		}
    		case ctrl_item:
    		{
    			name="ctrl_item";
    			break;
    		}
    		case Gz_check:
    			name="Gz_check";
    			break;
    		case SUBSET:
    			name="subset";
    			break;
		}
		return name;
			
	}
	public String getParentPath(int type)
	{
		String path = "/Params";
		switch(type)
		{
		case Serive:
		{
			path = "/Params";
			break;
		}
		case SeiveItem:
		{
			path="/Params/Serive";
			break;
		}
		case Gz_amounts:
		{
			path="/Params";
			break;
		}
		case Gz_amount:
		{
			path="/Params/Gz_amounts";
			break;
		}
		case ctrl_item:
		{
			path="/Params/Gz_amounts/Gz_amount";
			break;
		}
		case Gz_check:
			path="/Params";
			break;
		case SUBSET:
			path="/Params/subset";
			break;
		}
		
		return path;
	}
	public String getParentPathLower(int type)
	{
		String path = "/params";
		switch(type)
		{
		case Serive:
		{
			path = "/params";
			break;
		}
		case SeiveItem:
		{
			path="/params/Serive";
			break;
		}
		case Gz_amounts:
		{
			path="/params";
			break;
		}
		case Gz_amount:
		{
			path="/params/Gz_amounts";
			break;
		}
		case ctrl_item:
		{
			path="/params/Gz_amounts/Gz_amount";
			break;
		}
		case Gz_check:
			path="/params";
		    break;
		case SUBSET:
			path="/Params/subset";
			break;
		
		}
		return path;
	}
	public GzAmountXMLBo(Connection conn,int type)
	{
		this.conn=conn;
		init();
		try
		{
			doc=PubFunc.generateDom(xml.toString());
			if(type==1)
		    	this.getAllValues();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public GzAmountXMLBo(Connection conn,String param)
	{
		this.conn=conn;
		this.param=param;
		init(param);
		try
		{
			doc=PubFunc.generateDom(xml.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getValuesMap()
	{
		return hm;
	}
	public GzAmountXMLBo()
	{
		
	}
	private void init()
	{
		
		//RecordVo vo=new RecordVo("constant");
		//vo.setString("constant","GZ_PARAM");
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<Params>");
		temp_xml.append("</Params>");		
		try
		{
			//ContentDAO dao=new ContentDAO(this.conn);
			//vo=dao.findByPrimaryKey(vo);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from Constant where upper(constant)='GZ_PARAM'");
			if(rowSet.next())
				xml=Sql_switcher.readMemo(rowSet,"str_value");
			if(xml==null|| "".equals(xml))
			{
				xml=temp_xml.toString();
			}
			doc=PubFunc.generateDom(xml.toString());

			//System.out.println(xml);
		}
		catch(Exception ex)
		{
			xml=temp_xml.toString();
		}
		
	}
	private void init(String param)
	{
		
		//RecordVo vo=new RecordVo("constant");
		//vo.setString("constant","GZ_PARAM");
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<Params>");
		temp_xml.append("</Params>");		
		try
		{
			//ContentDAO dao=new ContentDAO(this.conn);
			//vo=dao.findByPrimaryKey(vo);
			RecordVo vo=ConstantParamter.getRealConstantVo(param,this.conn);
			if(vo!=null)
				xml=vo.getString("str_value");
			if(xml==null|| "".equals(xml))
			{
				xml=temp_xml.toString();
			}

			doc=PubFunc.generateDom(xml.toString());

			//System.out.println(xml);
		}
		catch(Exception ex)
		{
			xml=temp_xml.toString();
		}
		
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
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer strsql=new StringBuffer();
		try
		{
			ifNoParameterInsert("GZ_PARAM");
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean flag=isHaveParameter("GZ_PARAM");
			if(!flag)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
				pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "GZ_PARAM");
				pstmt.setString(2, buf.toString());
			}
			else
			{
				strsql.append("update constant set str_value=? where UPPER(constant)='GZ_PARAM'");
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
			}
			// 向缓存修改一次，防止其他地方拿缓存数据，然后保存，导致错误
            RecordVo paramsVo=ConstantParamter.getConstantVo("GZ_PARAM");
            paramsVo = new RecordVo("Constant");
            paramsVo.setString("constant", "GZ_PARAM");
            paramsVo.setString("str_value", buf.toString());
            ConstantParamter.putConstantVo(paramsVo, "GZ_PARAM");
			// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();	
			this.getAllValues();
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
				// 关闭Wallet
				dbS.close(conn);
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	public boolean setProperty(int type,String property,String value,String property2,String value2,String property3,String value3,String property4,String value4,String formular,String property5,String value5,HashMap map)
	{
		boolean flag=true;
		String name=this.getElementName(type);
		if(!"".equals(name))
		{
			try
			{
	    		String path=this.getParentPath(type);
	    		XPath xpath=XPath.newInstance(path);
	    		Element element=(Element)xpath.selectSingleNode(doc);
	    		Element childelement =null;
	    		//-----
	    		if(element==null)
	    		{
	    			if(type==GzAmountXMLBo.Params)
	    			{
	    				name="Params";
	    			}
	    			path=this.getParentPathLower(type);
	    			xpath=XPath.newInstance(path);
	    			element=(Element)xpath.selectSingleNode(doc);
	    		}
	    		//--------------
	    		if(element!=null)
	    		{
	    			if(type==Gz_amounts||type==Gz_check)
	    			{
	    		    	 childelement = element.getChild(name);
	    		     	if(childelement!=null)
	    	    		{
	    		    		element.removeChild(name);
	    		    		element.removeChildren(name);
	    		    	}
	    			}
	    			  childelement = new Element(name);
	    			  childelement.setAttribute(property,value.toUpperCase());
	    			  if(type==GzAmountXMLBo.Gz_amounts)
	    			  {
	    				  childelement.setAttribute(property2,value2.toUpperCase());
	    				  childelement.setAttribute(property3,value3.toUpperCase());
	    				  childelement.setAttribute(property4,value4);
	    				  childelement.setAttribute(this.ctrl_by_level, this.ctrl_by_level_value);
	    			  }
	    			  if(type==GzAmountXMLBo.ctrl_item)
	    			  {
	    				  childelement.setAttribute(property2,value2.toUpperCase());
	    				  childelement.setAttribute(property3,value3.toUpperCase());
	    				  childelement.setAttribute(property4,value4.toUpperCase());
	    				  childelement.setAttribute(property5,value5);
	    				  childelement.setText(formular);
	    			  }
	    			  if(type==GzAmountXMLBo.Gz_check)
	    			  {
	    				  childelement.setAttribute(property2,value2.toUpperCase());
	    				  childelement.setAttribute(property3,value3.toUpperCase());
	    			  }
	    			  if(map!=null)
	    			  {
	    		    	  Set keySet = map.keySet();
	    		    	  for(Iterator t=keySet.iterator();t.hasNext();)
	    		    	  {
	    		    		  String key=(String)t.next();
	    			    	  childelement.setAttribute(key, (String)map.get(key));
	    		    	  }
	    			  }
	    			  element.addContent(childelement);
	    		}	    		
	    		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}

  public void getAllValues()
  {
	  try
	  {
		  Element element=null;
		//  Element childElement = null;
		//  ArrayList childlist = null;
		 // String ss=this.xml;
		  ArrayList unlist = new ArrayList();
		  XPath xpath = XPath.newInstance("/Params/Gz_amounts");
		  element = (Element)xpath.selectSingleNode(doc);
		  if(element==null)
		  {
			  xpath = XPath.newInstance("/params/Gz_amounts");
			  element = (Element)xpath.selectSingleNode(doc);
		  }
		  if(element!=null)
		  {
			  hm = new HashMap();
			  hm.put("setid",element.getAttributeValue("setid"));
			  hm.put("sp_flag",element.getAttributeValue("sp_flag"));
			  hm.put("ctrl_type",element.getAttributeValue("ctrl_type"));
			  hm.put("ctrl_peroid",element.getAttributeValue("ctrl_peroid"));
			  hm.put("ctrl_by_level", (element.getAttributeValue("ctrl_by_level")==null|| "".equals(element.getAttributeValue("ctrl_by_level")))?"1":element.getAttributeValue("ctrl_by_level"));
			  hm.put("amountAdjustSet", element.getAttributeValue("amountAdjustSet")==null?"":element.getAttributeValue("amountAdjustSet"));
			  hm.put("amountPlanitemDescField", element.getAttributeValue("amountPlanitemDescField")==null?"":element.getAttributeValue("amountPlanitemDescField"));
			  hm.put("surplus_compute", element.getAttributeValue("surplus_compute")==null?"":element.getAttributeValue("surplus_compute"));
			  hm.put("fc_flag", element.getAttributeValue("fc_flag")==null?"":element.getAttributeValue("fc_flag"));
			  hm.put("ctrl_field", element.getAttributeValue("ctrl_field")==null?"":element.getAttributeValue("ctrl_field").trim());
			  xpath = XPath.newInstance("/Params/Gz_amounts/Gz_amount");
			  element = (Element)xpath.selectSingleNode(doc);
			  if(element==null)
			  {
				  xpath = XPath.newInstance("/params/Gz_amounts/Gz_amount");
				  element = (Element)xpath.selectSingleNode(doc);
			  }
			  String orgid=element.getAttributeValue("orgid");
			  hm.put("orgid",orgid);
			  xpath = XPath.newInstance("/Params/Gz_amounts/Gz_amount/ctrl_item");
			  
		      List list =null;
		      if(xpath.selectNodes(doc)!=null)
		    	  list=xpath.selectNodes(doc);
		      if(list==null||list.size()==0)
		      {
		    	  xpath = XPath.newInstance("/params/Gz_amounts/Gz_amount/ctrl_item");
		    	  if(xpath.selectNodes(doc)!=null)
		    		  list =xpath.selectNodes(doc);
		      }
		      if(list.size()>0)
		      {
		    	  for(Iterator t=list.iterator();t.hasNext();)
		    	  {
		    		  element=(Element)t.next();
		    		  LazyDynaBean bean = new LazyDynaBean();
		    		  String ff=element.getAttributeValue("planitem");
		    		  if(ff==null|| "".equals(ff))
		    			  continue;
		    		  FieldItem fielditem = DataDictionary.getFieldItem(element.getAttributeValue("planitem"));
		    		  if(fielditem==null)
		    			  continue;
					  bean.set("planitem",element.getAttributeValue("planitem"));
					  bean.set("planitemdesc", fielditem.getItemdesc());
					  fielditem = DataDictionary.getFieldItem(element.getAttributeValue("realitem"));
					  bean.set("realitem",element.getAttributeValue("realitem"));
					  bean.set("realitemdesc",fielditem.getItemdesc());
					  bean.set("balanceitem",element.getAttributeValue("balanceitem"));
					  fielditem = DataDictionary.getFieldItem(element.getAttributeValue("balanceitem"));
					  bean.set("balanceitemdesc", fielditem.getItemdesc()==null?"":fielditem.getItemdesc());
					  bean.set("flag",element.getAttributeValue("flag"));
					  bean.set("formular",element.getText());
					  bean.set("salaryid",element.getAttributeValue("salarySet")==null?"":element.getAttributeValue("salarySet"));
					  String className="";
					  if(element.getAttributeValue("className")!=null)
						  className=element.getAttributeValue("className");
					  bean.set("classname", className);
					  unlist.add(bean);
		    	  }
		      }
		      hm.put("ctrl_item",unlist);
		  /*				  if(list.size()>0)
			  {
				  for(Iterator t=list.iterator();t.hasNext();)
				  {
					  element = (Element)t.next();//某一个单位的
					  String orgid=element.getAttributeValue("orgid");
					  childlist=(ArrayList)element.getChildren();//某个单位的所有参数集合
					  if(childlist.size()>0)
					  {
						  
						  for(Iterator i = childlist.iterator();i.hasNext();)
						  {
							  childElement = (Element)i.next();
							  LazyDynaBean bean = new LazyDynaBean();
							  bean.set("planitem",childElement.getAttributeValue("planitem"));
							  bean.set("realitem",childElement.getAttributeValue("realitem"));
							  bean.set("balanceitem",childElement.getAttributeValue("balanceitem"));
							  bean.set("flag",childElement.getAttributeValue("flag"));
							  unlist.add(bean);
						  }
						  hm.put(orgid.toLowerCase(),unlist);
					  }
				  }
			  }*/
		      xpath = XPath.newInstance("/Params/Gz_check");
			  element = (Element)xpath.selectSingleNode(doc);
			  if(element==null)
			  {
				  xpath = XPath.newInstance("/params/Gz_check");
				  element = (Element)xpath.selectSingleNode(doc);
			  }
			  if(element != null)
			  {
				  String hsorgid = element.getAttributeValue("orgid");
				  String deptid = element.getAttributeValue("deptid");
				  String contrlLevelId=element.getAttributeValue("contrlLevelId");
				  HashMap hs = new HashMap();
				  hs.put("orgid",hsorgid);
				  hs.put("deptid",deptid);
				  hs.put("contrlLevelId", contrlLevelId);
				  hm.put("hs",hs);
			  }
			  
		  }
		  //取得 引入单位\部门变动人员 参数设置  dengcan
		  xpath = XPath.newInstance("/Params/CHG_SET");
		  element = (Element)xpath.selectSingleNode(doc);
		  if(element==null)
		  {
			  xpath = XPath.newInstance("/params/CHG_SET");
			  element = (Element)xpath.selectSingleNode(doc);
		  }
		  if(element!=null)
		  {
			  if(hm==null)
				  hm = new HashMap();
			  hm.put("chg_set",element.getAttributeValue("chg_set"));
			  hm.put("chg_set_context",element.getText());
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  public ArrayList getAllSalarySet(String salaryid)
  {
	  ArrayList list = new ArrayList();
	  try
	  {
		 HashMap map = new HashMap();
		 if(salaryid!=null&&!"".equals(salaryid))
		 {
			 String[] arr= salaryid.split(",");
			 for(int i=0;i<arr.length;i++)
			 {
				 if(arr[i]==null|| "".equals(arr[i]))
					 continue;
				 map.put(arr[i], arr[i]);
			 }
		 }
		 String sql = "select salaryid,cname from salarytemplate where "+Sql_switcher.isnull("cstate", "0")+"<>'1' order by seq";
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rs = dao.search(sql);
		 while(rs.next())
		 {
			 if(!this.uv.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
					continue;
			 LazyDynaBean bean = new LazyDynaBean();
			 bean.set("id",  rs.getInt("salaryid")+"");
			 bean.set("name", rs.getString("cname"));
			 if(map.get(rs.getString("salaryid"))!=null)
				 bean.set("isselect", "1");
			 else
				 bean.set("isselect", "0");
			 list.add(bean);
		 }
		 rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return list;
  }
  /**
   * 
   * @param itemid
   * @return
   */
  public String getSalarySet(String itemid)
  {
	  String str="";
	try
	{
		 String path="/Params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
		 XPath xpath=XPath.newInstance(path);
		 Element element = (Element)xpath.selectSingleNode(doc);
		 if(element==null)
		 {
			 path="/params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
			 xpath=XPath.newInstance(path);
			 element = (Element)xpath.selectSingleNode(doc);
		 }
		 if(element!=null&&element.getAttributeValue("salarySet")!=null)
			 str=element.getAttributeValue("salarySet");
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return str;
  }
  public void setSalarySet(String itemid,String salaryid)
  {
	  try
	  {
		     String path="/Params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
			 XPath xpath=XPath.newInstance(path);
			 Element element = (Element)xpath.selectSingleNode(doc);
			 if(element==null)
			 {
				 path="/params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
				 xpath=XPath.newInstance(path);
				 element = (Element)xpath.selectSingleNode(doc);
			 }
			 if(element!=null)
			 {
				 element.setAttribute("salarySet", salaryid);
			 }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  public String getFormula(String itemid)
  {
	  String str="";
	try
	{
		 String path="/Params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
		 XPath xpath=XPath.newInstance(path);
		 Element element = (Element)xpath.selectSingleNode(doc);
		 if(element==null)
		 {
			 path="/params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
			 xpath=XPath.newInstance(path);
			 element = (Element)xpath.selectSingleNode(doc);
		 }
		 if(element!=null)
			 str=element.getText();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return str;
  }
  public void setFormula(String itemid,String formula)
  {
	  try
	  {
		     String path="/Params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
			 XPath xpath=XPath.newInstance(path);
			 Element element = (Element)xpath.selectSingleNode(doc);
			 if(element==null)
			 {
				 path="/params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
				 xpath=XPath.newInstance(path);
				 element = (Element)xpath.selectSingleNode(doc);
			 }
			 if(element!=null)
			 {
				 element.setText(formula);
			 }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  public HashMap getCtrl_itemMap(String itemid)
  {
	  HashMap map = new HashMap();
		try
		{
			 String path="/Params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
			 XPath xpath=XPath.newInstance(path);
			 Element element = (Element)xpath.selectSingleNode(doc);
			 if(element==null)
			 {
				 path="/params/Gz_amounts/Gz_amount/ctrl_item[@planitem='"+itemid.toUpperCase()+"']";
				 xpath=XPath.newInstance(path);
				 element = (Element)xpath.selectSingleNode(doc);
			 }
			 if(element!=null)
			 {
				map.put("realitem",element.getAttributeValue("realitem"));
				map.put("balanceitem",element.getAttributeValue("balanceitem"));
			 }
			  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
  }
  public HashMap getMap()
  {
	  HashMap  map = new HashMap();
	  try
	  {
		  ArrayList sp_list = new ArrayList();
		  String path="/Params/Gz_amounts";
		  XPath xpath=XPath.newInstance(path);
		  Element spElement=(Element)xpath.selectSingleNode(doc);
		  if(spElement==null)
		  {
			  path="/params/Gz_amounts";
			  xpath=XPath.newInstance(path);
			  spElement=(Element)xpath.selectSingleNode(doc);
		  }
		  sp_list.add(spElement.getAttributeValue("sp_flag"));
		  path="/Params/Gz_amounts/Gz_amount/ctrl_item";
		  xpath=XPath.newInstance(path);
		  ArrayList list = new ArrayList();
		  if(xpath.selectNodes(doc)==null||xpath.selectNodes(doc).size()==0)
		  {
			  path="/params/Gz_amounts/Gz_amount/ctrl_item";
			  xpath=XPath.newInstance(path);
			  list = (ArrayList)xpath.selectNodes(doc);
		  }else
			  list=(ArrayList)xpath.selectNodes(doc);
		  ArrayList planitemlist = new ArrayList();
		  ArrayList formulalist = new ArrayList();
		  if(list.size()>0)
		  {
			  for(int i=0;i<list.size();i++)
			  {
				  Element element=(Element)list.get(i);
				  planitemlist.add(element.getAttributeValue("planitem"));
				  formulalist.add(element.getText());
				  
			  }
		  }
		  map.put("sp",sp_list);
		  map.put("plan",planitemlist);
		  map.put("formulalist",formulalist);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  
  /**
	 * 取得对应节点参数的值
	 * @param param_name
	 * @return
	 */
	public String getValue(String param_name)
	{
		String value="";
		if(doc==null)
			return value;
		if(!"".equals(param_name))
		{
		  try
		  {
			String str_path="/Params/"+param_name.toLowerCase();
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement==null){
				str_path="/params/"+param_name.toLowerCase();
				xpath=XPath.newInstance(str_path);
			}
			List childlist=xpath.selectNodes(doc);
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
	 * 取得对应节点参数的值
	 * @param param_name
	 * @return
	 */
	public void setValue(String param_name,String value){
		  try{
			  String str_path="/Params/"+param_name.toLowerCase();
			  XPath xpath=XPath.newInstance(str_path);
			  Element spElement=(Element)xpath.selectSingleNode(doc);
			  if(spElement==null){
				  str_path="/params/"+param_name.toLowerCase();
				  xpath=XPath.newInstance(str_path);
				  spElement=(Element)xpath.selectSingleNode(doc);
			  }
			  if(spElement!=null){
				  spElement.setText(value);
			  }else{
				  str_path="/Params";
				  xpath=XPath.newInstance(str_path);
				  spElement=(Element)xpath.selectSingleNode(doc);
				  if(spElement!=null){
					  Element element=new Element("subset");
					  element.setText(value);
					  spElement.addContent(element);
				  }else{
					  str_path="/params";
					  xpath=XPath.newInstance(str_path);
					  spElement=(Element)xpath.selectSingleNode(doc); 
					  if(spElement!=null){
						  Element element=new Element("subset");
						  element.setText(value);
						  spElement.addContent(element);
					  }
				  }
			  }
			  
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }			
	}
	
	/**
	 * 取得对应节点参数的值
	 * @param param_name
	 * @param flag 0:context  1:atrribute
	 * @author dengcan
	 * @return
	 */
	public void setOriValue(String param_name,String attributeName,String value,int flag){
		  try{
			  String str_path="/Params/"+param_name;
			  XPath xpath=XPath.newInstance(str_path);
			  Element spElement=(Element)xpath.selectSingleNode(doc);
			  if(spElement==null)
			  {
				  str_path="/params/"+param_name;
				  xpath=XPath.newInstance(str_path);
				  spElement=(Element)xpath.selectSingleNode(doc);
			  }
			  if(spElement!=null){
				  if(flag==0)
					  spElement.setText(value);
				  else
					  spElement.setAttribute(attributeName,value);
			  
			  }else{
				  str_path="/Params";
				  xpath=XPath.newInstance(str_path);
				  spElement=(Element)xpath.selectSingleNode(doc);
				  if(spElement==null)
				  {
					  str_path="/params";
					  xpath=XPath.newInstance(str_path);
					  spElement=(Element)xpath.selectSingleNode(doc);
				  }
				  
				  if(spElement!=null){
					  Element element=new Element(param_name);
					  if(flag==0)
					  {
						  element.setText(value);
						  spElement.addContent(element);
					  }
					  else
					  {
						  element.setAttribute(attributeName,value);
						  spElement.addContent(element);
					  }
				  }else{
					  str_path="/Params";
					  xpath=XPath.newInstance(str_path);
					  spElement=(Element)xpath.selectSingleNode(doc); 
					  if(spElement!=null){
						  Element element=new Element(param_name);
						  if(flag==0)
						  {
							  element.setText(value);
							  spElement.addContent(element);
						  }
						  else
						  {
							  element.setAttribute(attributeName,value);
							  spElement.addContent(element);
						  }
					  }
				  }
			  }
			  
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }			
	}
	
	
	public void saveStrValue(){
		PreparedStatement pstmt = null;	
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer strsql=new StringBuffer();
		try{
			ifNoParameterInsert(param);
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean flag=isHaveParameter(param);
			if(!flag)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
				pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, param);
				pstmt.setString(2, buf.toString());
			}
			else
			{
				strsql.append("update constant set str_value=? where UPPER(constant)='"+param+"'");
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
			}
			
			// 打开Wallet
			dbS.open(conn, strsql.toString());
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
				// 关闭Wallet
				dbS.close(conn);
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
	/**保存INFO_PARAM常量xml得节点值*/
	public void saveInfo_paramNode(String nodename,String[] fields,Connection conn)
	{
       PreparedStatement pstmt = null;	
       DbSecurityImpl dbS = new DbSecurityImpl();
       String fieldstr="";
   	   try{
   		    for(int i=0;fields!=null && i<fields.length;i++)
   		    {
   		    	fieldstr+= fields[i]+",";
   		    }
   		    if(fieldstr.length()>1)
   		    	fieldstr = fieldstr.substring(0,fieldstr.length()-1);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("GZ_PARAM");
	        if (option_vo!=null){
	        	if(option_vo.getString("str_value")!=null 

	        		&& option_vo.getString("str_value").trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	         
			        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
			        //XPath reportPath = XPath.newInstance("//user[@id=\"" + user + "\"]");
			        //List userlist=reportPath.selectNodes(doc);
			    	Element root = doc.getRootElement(); // 取得根节点
			    	Element childnode=root.getChild(nodename);
			    	if(childnode!=null)
			    	{
			    	  	childnode.setText(fieldstr);
			    	}
			    	else
			    	{
			    	    Element newnode = new Element(nodename);
			    	    newnode.setText(fieldstr);
			    	    root.addContent(newnode);		    	  
			        }	
			    	XMLOutputter outputter = new XMLOutputter();
				    Format format=Format.getPrettyFormat();
				    format.setEncoding("UTF-8");
				    outputter.setFormat(format);
				    xmls.append(outputter.outputString(doc));
				    strsql.delete(0,strsql.length());
					strsql.append("update  constant set str_value=? where constant='GZ_PARAM'");
					pstmt = this.conn.prepareStatement(strsql.toString());	
					switch(Sql_switcher.searchDbServer()){
						 case Constant.MSSQL:
							  pstmt.setString(1, xmls.toString());
							  break;
						 case Constant.ORACEL:
							  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xmls.toString().
							          getBytes())), xmls.length());
							  break;
						  case Constant.DB2:
							  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xmls.toString().
							          getBytes())), xmls.length());
							  break;
					}
					
					// 打开Wallet
					dbS.open(conn, strsql.toString());
					pstmt.executeUpdate();	
					
				}else
				{
					Element root = new Element("Params");
		        	Document doc = new Document(root);
		        	Element child = new Element(nodename);
		        	child.setText(fieldstr);
		        	root.addContent(child);
		   	 	    XMLOutputter outputter = new XMLOutputter();
		        	Format format=Format.getPrettyFormat();
		        	format.setEncoding("UTF-8");
		        	outputter.setFormat(format);
		        	xmls.append(outputter.outputString(doc));
		        	strsql.delete(0,strsql.length());
		        	strsql.append("update  constant set str_value=? where constant='GZ_PARAM'");
		        	pstmt = this.conn.prepareStatement(strsql.toString());	
					switch(Sql_switcher.searchDbServer()){
						 case Constant.MSSQL:
							  pstmt.setString(1, xmls.toString());
							  break;
						 case Constant.ORACEL:
							  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xmls.toString().
							          getBytes())), xmls.length());
							  break;
						  case Constant.DB2:
							  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xmls.toString().
							          getBytes())), xmls.length());
							  break;
					}
					
					// 打开Wallet
					dbS.open(conn, strsql.toString());
					pstmt.executeUpdate();	
				}
		    }else{
		        Element root = new Element("Params");
	        	Document doc = new Document(root);
	        	Element child = new Element(nodename);
	        	child.setText(fieldstr);
	        	root.addContent(child);
	   	 	    XMLOutputter outputter = new XMLOutputter();
	        	Format format=Format.getPrettyFormat();
	        	format.setEncoding("UTF-8");
	        	outputter.setFormat(format);
	        	xmls.append(outputter.outputString(doc));
	        	strsql.delete(0,strsql.length());
		       	strsql.append("insert into  constant(constant,type,str_value,describe)values('GZ_PARAM','0',?,'保存薪资管理的参数')");
		       	pstmt = this.conn.prepareStatement(strsql.toString());	
				pstmt.setString(1, xmls.toString());
				
				// 打开Wallet
				dbS.open(conn, strsql.toString());
				pstmt.executeUpdate();	
		    }	 
       }catch (Exception ee)
       {
         ee.printStackTrace();
         GeneralExceptionHandler.Handle(ee);
       }
       finally
       {
         try
         {
        	// 关闭Wallet
			dbS.close(conn);
           if (pstmt != null)
           {
           	pstmt.close();
           }           
         }
         catch (SQLException ee)
         {
           ee.printStackTrace();
         }          
     } 
	}
	public String getCtrl_by_level_value() {
		return ctrl_by_level_value;
	}
	public void setCtrl_by_level_value(String ctrl_by_level_value) {
		this.ctrl_by_level_value = ctrl_by_level_value;
	}
	public UserView getUv() {
		return uv;
	}
	public void setUv(UserView uv) {
		this.uv = uv;
	}
 
}

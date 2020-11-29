package com.hjsj.hrms.businessobject.pos.posparameter;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 *<p>Title:PosparameXML.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 22, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class PosparameXML { 

	public final static int AMOUNTS=0;
	public final static int CTRL_ITEM=1;
	public final static int VIEW_SCAN=2;

	private Connection conn;
	private String xmlcontent;
	private Document doc;
	public PosparameXML(Connection conn)
	{
		this.conn=conn;
		init();
		try
		{
			this.doc=PubFunc.generateDom(xmlcontent);
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
		strxml.append("<params>");
		strxml.append("</params>");	
		RowSet rs=null;
		try
		{
			String content="";
			/*RecordVo option_vo=ConstantParamter.getRealConstantVo("UNIT_WORKOUT",this.conn);
	     	if(option_vo!=null)
	     		content=option_vo.getString("str_value");*/
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer();
    	    sql.append("select str_value from constant where upper(constant)='UNIT_WORKOUT'");
    	    rs=dao.search(sql.toString());
    	    if(rs.next()) {
                content=Sql_switcher.readMemo(rs, "str_value");
            }
	     	if(content!=null&&content.trim().length()>1)
			{
	     		if(content.indexOf("<params>")==-1) {
                    content=strxml.toString();
                }
			}else{
				content=strxml.toString();
			}
	     	xmlcontent=content;	     	
			doc=PubFunc.generateDom(xmlcontent);	     	
		}
		catch(Exception ex)
		{
			this.xmlcontent=strxml.toString();
			ex.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
	}
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		  case AMOUNTS:
			name="amounts";
			break;
		  case CTRL_ITEM:
			name="ctrl_item";
			break;
		  case VIEW_SCAN:
			name="view_scan";
			break;
		}
		return name;
	}
	/**
	 * 得到节点属性值
	 * @param param_type
	 * @param property 属性名称
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
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					//if(element.getAttributeValue("setid").toString().equalsIgnoreCase(setid))
					value=element.getAttributeValue(property);
				}
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
	 * 得到子节点属性值
	 * @param param_type
	 * @param setid BXX
	 * @param planitem 属性名称
	 * @param property 属性名称
	 * @return
	 */
	public String getChildValue(int param_type,String setid,String planitem,String property)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("setid").toString().equalsIgnoreCase(setid)){
						List list = element.getChildren("ctrl_item");
						for(int j=0;j<list.size();j++){
							Element ctrlelement = (Element)list.get(j);
							if(ctrlelement.getAttributeValue("planitem").toString().equalsIgnoreCase(planitem)) {
                                value=ctrlelement.getAttributeValue(property);
                            }
						}
					}
				}
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
	 * 得到子节点属性值
	 * @param param_type
	 * @param setid BXX
	 * @param planitem 属性名称
	 * @param property 属性名称
	 * @return
	 */
	public String getTextValue(int param_type,String setid,String planitem)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("setid").toString().equalsIgnoreCase(setid)){
						List list = element.getChildren("ctrl_item");
						for(int j=0;j<list.size();j++){
							Element ctrlelement = (Element)list.get(j);
							if(ctrlelement.getAttributeValue("planitem").toString().equalsIgnoreCase(planitem)) {
                                value=ctrlelement.getText();
                            }
						}
					}
				}
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
	 * 得到 对应amounts下所有ctrl_item属性的planitem值的list
	 * @param param_type
	 * @param setid
	 * @return
	 */
	public ArrayList getChildList(int param_type,String setid)
	{
		String value="";
		String name=getElementName(param_type);
		ArrayList clist = new ArrayList();
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("setid").toString().equalsIgnoreCase(setid)){
						List list = element.getChildren("ctrl_item");
						for(int j=0;j<list.size();j++){
							Element ctrlelement = (Element)list.get(j);
							value=ctrlelement.getAttributeValue("planitem");
							clist.add(value);
						}
					}
				}
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return clist;		
	}
	/**
	 * 得到 对应amounts下所有ctrl_item属性的method值的list
	 * @param param_type
	 * @param setid
	 * @return
	 */
	public ArrayList getMethodChildList(int param_type,String setid)
	{
		String value="";
		String name=getElementName(param_type);
		ArrayList clist = new ArrayList();
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("setid").toString().equalsIgnoreCase(setid)){
						List list = element.getChildren("ctrl_item");
						for(int j=0;j<list.size();j++){
							Element ctrlelement = (Element)list.get(j);
							value=ctrlelement.getAttributeValue("method");
							value=value!=null?value:"";
							clist.add(value);
						}
					}
				}
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return clist;		
	}
	
	/**
	 * 设置节点属性和值
	 * @param param_type
	 * @param list 里面为CommonData
	 * @return
	 */
	public boolean setValue(int param_type,ArrayList list)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			if(childlist.size()==0)
			{	
				
				element=new Element(name);
				for(int i=0;i<list.size();i++){
					CommonData data = (CommonData)list.get(i);
					element.setAttribute(data.getDataName(),data.getDataValue());
				}
				this.doc.getRootElement().addContent(element);
				
			}
			else{
				this.doc.getRootElement().removeChildren("amounts");
				element=new Element(name);
				for(int i=0;i<list.size();i++){
					CommonData data = (CommonData)list.get(i);
					element.setAttribute(data.getDataName(),data.getDataValue());
				}
				this.doc.getRootElement().addContent(element);
			}
			/*else
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					CommonData data1 = (CommonData)list.get(0);
					if(element.getAttributeValue(data1.getDataName()).toString().equalsIgnoreCase(data1.getDataValue())){
						for(int j=0;j<list.size();j++){
							CommonData data = (CommonData)list.get(j);
							element.setAttribute(data.getDataName(),data.getDataValue());
						}
						break;
					}
					else{
						element=new Element(name);
						for(int j=0;j<list.size();j++){
							CommonData data = (CommonData)list.get(j);
							element.setAttribute(data.getDataName(),data.getDataValue());
						}
						Element root = doc.getRootElement();
						root.addContent(element);
					}
				}
			}*/
			
			//saveParameter();
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
	 * 设置对应节点属性的值
	 * @param str_path 保存路径 例如：/Params/nbase
	 * @param value //值
	 * @return
	 */
	public void setAttributeValue(String str_path,String attributeName,String attributeValue){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement!=null){
				spElement.setAttribute(attributeName, attributeValue);
			}else{
				String arr[] = str_path.split("/");
				if(arr!=null&&arr.length>0){
					for(int i=1;i<arr.length;i++){
						String path = "";
						for(int j=1;j<=i;j++){
							path+="/"+arr[j];
						}
						xpath=XPath.newInstance(path);
						Element bbElement=(Element)xpath.selectSingleNode(doc);
						if(bbElement==null){
							Element element=new Element(arr[i]);
							if(i==arr.length-1) {
                                element.setAttribute(attributeName, attributeValue);
                            }
							spElement.addContent(element);
						}else{
						    spElement = bbElement;
						}
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	 /**
	 * 取得对应节点属性的值
	 * @param str_path 例如：/Params/nbase
	 * @return
	 */
	public String getNodeAttributeValue(String str_path,String attributeName){
		String value="";
		if(doc==null) {
            return value;
        }
		if(!"".equals(str_path)){
			try{
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0){
					element=(Element)childlist.get(0);
					value=element.getAttributeValue(attributeName);
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}		
		return value;		
	}

	/**
	 * 设置子节点属性和值
	 * @param param_type
	 * @param list 里面为保存有CommonData的list
	 * @return
	 */
	public boolean setChildValue(int param_type,String setid,ArrayList list)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/params/amounts";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(this.doc);
			Element element=null;
			
			for(int x=0;x<childlist.size();x++){
				element = (Element)childlist.get(x);
				if(element.getAttributeValue("setid").equalsIgnoreCase(setid)){
					element.removeChildren(name);
					for(int i=0;i<list.size();i++){
						Element ctrl=new Element(name);
						ArrayList ctrllist = (ArrayList)list.get(i);
						String method = "";
						CommonData data1 = null;
						for(int j=0;j<ctrllist.size();j++){
							CommonData data = (CommonData)ctrllist.get(j);
							if("method".equalsIgnoreCase(data.getDataName())){
								if("1".equals(data.getDataValue())){
									method = "1";
								}
							}
							if("cond".equalsIgnoreCase(data.getDataName())){
								data1 = data;
							}
							ctrl.setAttribute(data.getDataName(),data.getDataValue());
							if("1".equals(method)){
								if(data1!=null) {
                                    ctrl.setText(data1.getDataValue());
                                }
							}
						}
						element.addContent(ctrl);
					}
				}
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
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer strsql=new StringBuffer();
		List list = new ArrayList();
		try
		{
			
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			boolean iscorrect=if_vo_Empty("UNIT_WORKOUT");
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
				list.add("UNIT_WORKOUT");
				list.add(buf.toString());
			}
			else
			{
				strsql.append("update constant set str_value=? where constant='UNIT_WORKOUT'");
				list.add(buf.toString());
			}
			//pstmt.executeUpdate();			
			dao.update(strsql.toString(), list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
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
        	  if(rs.next()){
        		  da.setDataName(rs.getString("name"));
				  da.setDataValue(rs.getString("tabid"));
        	  }
          }catch(Exception e)
          {
        	  e.printStackTrace();
          }
          return da;
	}
}
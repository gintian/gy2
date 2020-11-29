package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title:FilterSetBo.java</p>
 * <p>Description>:FilterSetBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-19 下午05:03:19</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class FilterSetBo {

	/**
	 * 数据库连接
	 */
	Connection conn;
	/**
	 * 存放子集
	 */
	static HashMap fieldSetMap;
	/**
	 * 存放指标
	 */
	static HashMap fieldMap;
	private Document doc;
	public FilterSetBo(Connection conn)
	{
		this.conn=conn;
		fieldSetMap = new HashMap();
		fieldMap = new HashMap();
	}
	/**
	 * 取已经保存的参数值
	 * @param table 表名字
	 * @param cloumn 主键列
	 * @param cloumnValue 主键列的值
	 * @param flag =1是有属性带值，=0直接就是值
	 * @param property 属性名
	 * @param path 解析xml的路径
	 * @parma splitflag =1是多个子集或指标一起以符号分隔的形式
	 * @parma split 分隔的符号
	 * @param dao 
	 * @param xmlCloumn 保存xml参数的列名
	 * @param fieldFlag =1子集 =0指标
	 */
	public void putParameters(String table,String cloumn,String cloumnValue,int flag,String property,String path,int splitflag,String split,String xmlCloumn,int fieldFlag,int more)
	{
		try{
			
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select "+ xmlCloumn+" from "+table+" where UPPER("+cloumn+")='"+cloumnValue.toUpperCase()+"'";
			RowSet rs = dao.search(sql);
			String xml = "";
			while(rs.next())
			{
				xml=Sql_switcher.readMemo(rs, xmlCloumn);
			}
			
			if(xml==null|| "".equals(xml)||xml.trim().length()<=0) {
                return;
            }
			doc = PubFunc.generateDom(xml);
			XPath xpath = XPath.newInstance(path);
			if(more==1)
			{
				  Element element = (Element)xpath.selectSingleNode(doc);
				  if(element==null)
				  {
					  String a_path=path.substring(0,2).toLowerCase()+path.substring(2);
					  xpath = XPath.newInstance(a_path);
					  element = (Element)xpath.selectSingleNode(doc);
				  }
				  if(element!=null)
				  {
	    		  String v = "";
	    		  if(flag==1)
	     		  {
	     			  v = element.getAttributeValue(property);
	     		  }
	    		  else
	    		  {
	    			  v=element.getText();
	    		  }
	     		  if(splitflag==1)
	    		  {
	    			  String[] temp = v.split(split);
	    			  for(int i=0;i<temp.length;i++)
	    			  {
	    				  String temp_str=temp[i];
	    				  if(temp_str==null|| "".equals(temp_str)) {
                              continue;
                          }
		     			  if(fieldFlag==1)
			     		  {
			    			  this.fieldSetMap.put(temp_str.toUpperCase(), temp_str);
			    		  }
			    		  else
			    		  {
			    			  this.fieldMap.put(temp_str.toUpperCase(),temp_str);
			    		  }
		    		  }
	    		  }
	    		  else
	    		  {
	    			  if(fieldFlag==1)
	    			  {
	    				  this.fieldSetMap.put(v.toUpperCase(), v);
	      			  }
    				  else
	    			  {
		    			  this.fieldMap.put(v.toUpperCase(),v);
	      			  }
	    		  }
				  }
			}
			else
			{
	    		List list = (ArrayList)xpath.selectNodes(doc);
	    		  if(list==null||list.size()==0)
				  {
					  xpath = XPath.newInstance(path.substring(0,1).toUpperCase()+path.substring(1));
					  list = (ArrayList)xpath.selectNodes(doc);
				  }
	    		if(list!=null&&list.size()>0)
	    		{
	    			 Element element=null;
	    			 for(Iterator t=list.iterator();t.hasNext();)
	    	    	  {
			    		  element = (Element)t.next();
			    		  String v = "";
			    		  if(flag==1)
			     		  {
			     			  v = element.getAttributeValue(property);
			     		  }
			    		  else
			    		  {
			    			  v=element.getText();
			    		  }
			     		  if(splitflag==1)
			    		  {
			    			  String[] temp = v.split(split);
			    			  for(int i=0;i<temp.length;i++)
			    			  {
			    				  String temp_str=temp[i];
			    				  if(temp_str==null|| "".equals(temp_str)) {
                                      continue;
                                  }
				     			  if(fieldFlag==1)
					     		  {
					    			  this.fieldSetMap.put(temp_str.toUpperCase(), temp_str);
					    		  }
					    		  else
					    		  {
					    			  this.fieldMap.put(temp_str.toUpperCase(),temp_str);
					    		  }
				    		  }
			    		  }
			    		  else
			    		  {
			    			  if(fieldFlag==1)
			    			  {
			    				  this.fieldSetMap.put(v.toUpperCase(), v);
			      			  }
		    				  else
			    			  {
				    			  this.fieldMap.put(v.toUpperCase(),v);
			      			  }
			    		  }
	    	     	  }
	    		}
			}
			
		}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	/**
	 * 可以提供具体参数值，不用自己取
	 * @param value
	 * @param flag =1是子集=0是指标
	 */
	public void putValue(String value,int flag)
	{
		if(value!=null)
		{
     		if(flag==1)
    		{
     			fieldSetMap.put(value.toUpperCase(), value);
     		}
     		else
     		{
     			fieldMap.put(value.toUpperCase(), value);
     		}
		}
	}
	public HashMap getFieldSetMap()
	{
		return fieldSetMap;
	}
	public HashMap getFieldMap()
	{
		return fieldMap;
	}
	
}

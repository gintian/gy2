/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;


/**
 *<p>Title:把记录集转换成xml文件格式</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-20:上午09:39:32</p> 
 *@author cmq
 *@version 4.0
 */
public class RowSetToXmlBuilder {
	private Connection conn=null;

	public RowSetToXmlBuilder(Connection conn) {
		super();
		this.conn = conn;
	}
	/**
	 * 把当前的记录转换成xml文件格式
	<?xml version="1.0" encoding = "UTF-8" ?>
	<root columns="A0101,E0101,E0122" rowcount="10" keycolumns="AAAA,AAB">
		<record AAAA="12" AAB="12">#把主键字段按属性值填入
			< A0101>1</ A0101>
			< E0101>1.000000</ E0101>
			< E0122>1.000000</ E0122>
			…
		</ record>
		<record AAAA="12" AAB="10"> #把主键字段按属性值填入
			< A0101>1</ A0101>
			< E0101>1.000000</ E0101>
			< E0122>1.000000</ E0122>
			…
		</ record>
	</root>
	 * @param rset      记录集
	 * @param tablename 表名
	 * @return
	 * @throws GeneralException
	 */
	
	public String outPutXml(RowSet rset,String tablename) throws GeneralException
	{
		if(rset==null) {
            return "";
        }
		StringBuffer buf=new StringBuffer();
		try
		{
			int maxrows=0;	
			ResultSetMetaData metadata=rset.getMetaData();
			/**取得物理表主键列表*/
			RecordVo vo=new RecordVo(tablename);
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++)
			{
				buf.append((String)keylist.get(i));
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String keycolumns=buf.toString();
			buf.setLength(0);
			for(int i=0;i<vo.getModelAttrs().size();i++)
			{
				buf.append(vo.getModelAttrs().get(i));
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String columns=buf.toString();
			Element root = new Element("root");
	        root.setAttribute("columns", columns.toLowerCase());

	        root.setAttribute("keycolumns", keycolumns.toLowerCase());
	        Document myDocument = new Document(root);
	        while(rset.next())
	        {
	            Element record=new Element("record");
	        	for(int j=1;j<=metadata.getColumnCount();j++)
	            {   
	            	String fieldname=metadata.getColumnName(j).toLowerCase();
	                String value =getValueByFieldType(rset, metadata, j);	            	
	            	if(isKeyColumn(fieldname,keylist)) {
                        record.setAttribute(fieldname, value);
                    }
	            	Element temp=new Element(fieldname);
	            	if(value!=null&&value.length()>0){
	            		value = value.replaceAll("\r\n","<br>");
	            		value = value.replaceAll("\n","<br>");
	            		value = value.replaceAll("\r","<br>");
	            	}	            	
	            	temp.setText(value);
	            	record.addContent(temp);
	            }
	        	root.addContent(record);
	        	++maxrows;
	        }
	        root.setAttribute("rowcount", String.valueOf(maxrows));
	        XMLOutputter outputter = new XMLOutputter();
	        Format format=Format.getPrettyFormat();
	        format.setEncoding("UTF-8");
	        outputter.setFormat(format);
	        buf.setLength(0);
	        buf.append(outputter.outputString(myDocument));	        
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	/**
	 * 如果有需要根据权限来判断的，用这个方法
	 * @param rset
	 * @param tablename
	 * @return
	 * @throws GeneralException
	 */
	public String outPutXml(RowSet rset,String tablename,String privColumn,UserView userView,int model,String currentPointsetid) throws GeneralException
	{
		if(rset==null) {
            return "";
        }
		StringBuffer buf=new StringBuffer();
		try
		{
			int maxrows=0;	
			ResultSetMetaData metadata=rset.getMetaData();
			/**取得物理表主键列表*/
			RecordVo vo=new RecordVo(tablename);
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++)
			{
				buf.append((String)keylist.get(i));
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String keycolumns=buf.toString();
			buf.setLength(0);
			for(int i=0;i<vo.getModelAttrs().size();i++)
			{
				buf.append(vo.getModelAttrs().get(i));
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String columns=buf.toString();
			Element root = new Element("root");
	        root.setAttribute("columns", columns.toLowerCase());
	        root.setAttribute("keycolumns", keycolumns.toLowerCase());
	        Document myDocument = new Document(root);
	       
	        while(rset.next())
	        {
	        	if(userView!=null&&!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
            	{
            		if(privColumn!=null&&privColumn.trim().length()>0&&!userView.isHaveResource(model, rset.getString(privColumn))) {
                        continue;
                    }
            	}	
	            Element record=new Element("record");
	        	for(int j=1;j<=metadata.getColumnCount();j++)
	            {   
	            	String fieldname=metadata.getColumnName(j).toLowerCase();
	            	String value =getValueByFieldType(rset, metadata, j);	   
	            	if(isKeyColumn(fieldname,keylist)) {
                        record.setAttribute(fieldname, value);
                    }
	            	
	            	Element temp=new Element(fieldname);
	            	temp.setText(value);
	            	record.addContent(temp);
	            }
	        	root.addContent(record);
	        	++maxrows;
	        }
	        root.setAttribute("rowcount", String.valueOf(maxrows));
	        XMLOutputter outputter = new XMLOutputter();
	        Format format=Format.getPrettyFormat();
	        format.setEncoding("UTF-8");
	        outputter.setFormat(format);
	        buf.setLength(0);
	        buf.append(outputter.outputString(myDocument));	        
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	
	
	public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j) throws SQLException {
 		String temp="";
 		switch(rsetmd.getColumnType(j))
 		{
 		
 		case Types.DATE:
 			    if(rset.getDate(j)==null) {
                    temp="";
                } else {
                    temp=PubFunc.FormatDate(rset.getDate(j));
                }
 		        break;			
 		case Types.TIMESTAMP:
 		     	if(rset.getDate(j)==null) {
                    temp="";
                } else
 		     	{
 			       temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
 			       if(temp.indexOf("12:00:00")!=-1) {
                       temp=PubFunc.FormatDate(rset.getDate(j));
                   }
 		     	}
 				break;
 		case Types.CLOB:
 		case Types.LONGVARCHAR:
 			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	
 		
 				break;
 		case Types.BLOB:
 		case Types.LONGVARBINARY: 			
 				//temp="二进制文件";	 //chenmengqing added 20080327 for delphi's client
 			    temp=PubFunc.getBlobBase64(rset,j);
         	    temp=temp.replaceAll("\r\n", "");
 				break;		
 		case Types.NUMERIC:
 			  if(rset.getString(j)==null|| "".equals(rset.getString(j)))
 			  {
 				  temp="";
 			  }
 			  else
 			  {
 		    	  int preci=rsetmd.getScale(j);
 		    	  /**针对oracle库，当sql语句使用sum等函数时，取不到小数位，但是下方法取到的是这个值的小数位，而不是这个列的小数位，但能保证数值正确，先采用此办法，lizhenwei at 20100427*/
 		    	  if(preci==0&&Sql_switcher.searchDbServer()==Constant.ORACEL)
 		    	  {
 			    	  BigDecimal bd = rset.getBigDecimal(j);
 			    	  if(bd!=null) {
                          preci=bd.scale();
                      }
 			      }
 		    	  /**针对oracle float 类型 chenmengqing changed at 20091023*/
 		    	  if(Sql_switcher.searchDbServer()==Constant.ORACEL&&preci==-127)
 		    	  {
 			    	  if("I9999".equalsIgnoreCase(rsetmd.getColumnName(j))) {
                          preci=0;
                      } else {
                          preci=2;
                      }
 		    	  }
 			      temp=String.valueOf(rset.getDouble(j));
 		          temp=PubFunc.DoFormatDecimal(temp, preci);
 			  }
 			  break;
 		default:		
 				temp=rset.getString(j);
 				if(temp==null) {
                    temp="";
                }
 				char s='\u0000';
 				String ss=String.valueOf(s);
 				//temp=temp.replace(s, ' ');
 				temp=temp.replaceAll(ss, " ");
 				break;
 		}
 		return temp;
 	}   
	
	
	
	
	
	/**
	 * 把当前的记录转换成xml文件格式
	<?xml version="1.0" encoding = "UTF-8" ?>
	<root columns="姓名,单位,部门…" rowcount="10" keycolumns="姓名">
		<record 姓名="12">#把主键字段按属性值填入
			< 姓名>1</ 姓名>
			< 单位>1.000000</ 单位>
			< 部门>1.000000</ 部门>
			…
		</ record>
		
	</root>
	 * @param rset      记录集
	 * @param tablename 表名
	 * @return
	 * @throws GeneralException
	 */
	public String outPutXml2(RowSet rset,String tablename,String itemids) throws GeneralException
	{
		if(rset==null) {
            return "";
        }
		StringBuffer buf=new StringBuffer("");
		try
		{
			int maxrows=0;	
			ResultSetMetaData metadata=rset.getMetaData();
			/**取得物理表主键列表*/
			RecordVo vo=new RecordVo(tablename);
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++)
			{
				String temp=(String)keylist.get(i);
				FieldItem item=DataDictionary.getFieldItem(temp);
				if(item!=null) {
                    buf.append(item.getItemdesc());
                } else {
                    buf.append((String)keylist.get(i));
                }
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String keycolumns=buf.toString();
			buf.setLength(0);
			for(int i=0;i<vo.getModelAttrs().size();i++)
			{
				String temp=(String)vo.getModelAttrs().get(i);
				FieldItem item=DataDictionary.getFieldItem(temp);
				if(item!=null)
				{
					buf.append(item.getItemdesc());
					
				}
				else {
                    buf.append(vo.getModelAttrs().get(i));
                }
				buf.append(",");
			}
			if(buf.length()>0) {
                buf.setLength(buf.length()-1);
            }
			String columns=parseStr(buf.toString());
			
			Element root = new Element("root");
	        root.setAttribute("columns", columns.toLowerCase());

	        root.setAttribute("keycolumns", keycolumns.toLowerCase());
	        Document myDocument = new Document(root);
	        while(rset.next())
	        {
	            Element record=new Element("record");
	        	for(int j=1;j<=metadata.getColumnCount();j++)
	            {   
	            	String fieldname=metadata.getColumnName(j).toLowerCase();
	            	if (itemids.indexOf("/" + fieldname.toUpperCase() + "/") == -1)// 进行过滤，只选择勾选的薪资项目
                    {
                        continue;
                    }
	            	FieldItem item=DataDictionary.getFieldItem(fieldname);
	            	
	                String value = PubFunc.getValueByFieldType(rset, metadata, j);	            	
	            	if(isKeyColumn(fieldname,keylist))
	            	{
	            		if(item!=null) {
                            record.setAttribute(parseStr(item.getItemdesc()), value);
                        } else {
                            record.setAttribute(fieldname, value);
                        }
	            	}
	            	Element temp=null;
	            	// 由于jdom的属性中不能以数字开头，这里机上标识CHJCDATAHJ，后面全量替换，目前该方法仅薪资调用
	            	if(item!=null) {
	            		temp=new Element("CHJCDATAHJ" + parseStr(item.getItemdesc()));
	            	}else {
	            		temp=new Element("CHJCDATAHJ" + fieldname);
	            	}
	            	if(item!=null&& "A".equals(item.getItemtype())&&!"0".equals(item.getCodesetid())) {
                        value=AdminCode.getCodeName(item.getCodesetid(),value);
                    }
	            	temp.setText(value);
	            	record.addContent(temp);
	            }
	        	root.addContent(record);
	        	++maxrows;
	        }
	        root.setAttribute("rowcount", String.valueOf(maxrows));
	        XMLOutputter outputter = new XMLOutputter();
	        Format format=Format.getPrettyFormat();
	        format.setEncoding("UTF-8");
	        outputter.setFormat(format);
	        buf.setLength(0);
	        buf.append(outputter.outputString(myDocument).replace("CHJCDATAHJ", ""));	        
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	
	/**
	 * 过滤非法xml字符
	 * @param str
	 * @return
	 */
	public String parseStr(String str)
	{
		  StringBuffer   newXml   =new StringBuffer("");   
		  for(int i=0;i<str.length();i++){   
			  char  ch =str.charAt(i);   
			  if(ch==' ') {
                  continue;
              }
			  if(org.jdom.Verifier.isXMLCharacter(ch)){   
				  newXml.append(ch);  
			  }   
		  }   
		  String strr=newXml.toString();
		  strr=strr.replaceAll("\\（","");
		  strr=strr.replaceAll("\\）","");
		  strr=strr.replaceAll("、", "");
		  strr=strr.replaceAll("/", "");
		  strr=strr.replaceAll("﹒", "");
		  strr=strr.replaceAll("[%％]", "");
		  strr = this.SBCchange(strr);
		  return   strr;   
	}
	/**全角转换为半角，预防标题中含有全角的阿拉伯数字*/
	public  String SBCchange(String input)//2016-9-30 zhanghua
	 {
		if (null != input) {
			char c[] = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if ('\u3000' == c[i]) {
					c[i] = ' ';
				} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
					c[i] = (char) (c[i] - 65248);
				}
			}
			String dbc = new String(c);
			return dbc;
		} else {
			return null;
		}
	    
	  }	
	
//	/**全角转换为半角，预防标题中含有全角的阿拉伯数字*/
//	public  String SBCchange(String QJstr)
//	 {
//	     String outStr="";
//	     String Tstr="";
//	     byte[] b=null;
//
//	     for(int i=0;i<QJstr.length();i++)
//	     {     
//	      try
//	      {
//	       Tstr=QJstr.substring(i,i+1);
//	       b=Tstr.getBytes("unicode");
//	      }
//	      catch(java.io.UnsupportedEncodingException e)
//	      {
//	       e.printStackTrace();
//	      }     
//	      if (b[3]==-1)
//	      {
//	       b[2]=(byte)(b[2]+32);
//	       b[3]=0;      
//	        
//	       try
//	       {       
//	        outStr=outStr+new String(b,"unicode");
//	       }
//	       catch(java.io.UnsupportedEncodingException e)
//	       {
//	        e.printStackTrace();
//	       }      
//	      }else outStr=outStr+Tstr;
//	     }
//	    
//	     return outStr; 
//	  }

	 

	
	
	/**
	 * 导出表中全部的数据
	 * @param tablename 表名
	 * @return
	 * @throws GeneralException
	 */
	public String outPutXml(String tablename) throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			buf.append("select * from ");
			buf.append(tablename);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			return outPutXml(rset,tablename);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 是否为主键
	 * @param fieldname
	 * @param keylist
	 * @return
	 */
	private boolean isKeyColumn(String fieldname,ArrayList keylist)
	{
		boolean bflag=false;
		for(int i=0;i<keylist.size();i++)
		{
			String name=(String)keylist.get(i);
			if(name.equalsIgnoreCase(fieldname))
			{
				bflag=true;
				break;
			}
		}
		return bflag;
	}

}

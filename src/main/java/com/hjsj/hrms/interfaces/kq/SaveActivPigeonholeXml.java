package com.hjsj.hrms.interfaces.kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 归档信息变为xml
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 3, 2006:3:48:54 PM</p>
 * @author sx
 * @version 1.0
 *
 */
public class SaveActivPigeonholeXml {

	private Connection conn;	
	private String table_name;
	public SaveActivPigeonholeXml()
	{
		
	}
	/**
	 * 
	 * @param conn
	 * @param id
	 * @param table_name  归档的临时表名
	 */
	public SaveActivPigeonholeXml(Connection conn,String table_name)
	{
		this.conn=conn;		
		this.table_name=table_name;
	}
	/**
	 * 建立xml
	 * @param SrcFldSet 源表
	 * @param DestFldSet  目标表
	 */
	public String createXml(String SrcFldSet,String DestFldSet,ArrayList fieldlist)throws GeneralException
	{
		String temp = null;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		String destFldId=null;
		String destFldName=null;
		DynaBean dbean=null;
		String srcFldId=null;   	    
		String sql="select SrcFldId,SrcFldName,SrcCodeSet,DestFldId,DestFldName,DestCodeSet from "+this.table_name;
		StringBuffer wsql=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
				
				dbean=(LazyDynaBean)fieldlist.get(i);
				srcFldId=(String)dbean.get("srcfldid");	
 				destFldId=(String)dbean.get("destfldid");	
 				destFldName=(String)dbean.get("destfldname");	
 				if(destFldId!=null&&destFldId.length()>0&&destFldName!=null&&destFldName.length()>0)
 				{
 					wsql.append("'"+srcFldId+"',");
 					String update = "update " + table_name + " set DestFldId=? ,destFldName=?,DestCodeSet=? where SrcFldId=? ";
 					ArrayList list = new ArrayList();
 					list.add(destFldId);
 					list.add(destFldName);
 					list.add((String)dbean.get("destcodeset"));
 					list.add(srcFldId);
 					try {
 					dao.update(update, list);
 					} catch (Exception e) {
 						e.printStackTrace();
 					}
 				}
 				
	    }
		
		try{
			if(wsql!=null&&wsql.length()>0)
			{
				wsql.setLength(wsql.length()-1);
				String update= "update "+this.table_name+" set DestFldId='',DestFldName='',DestCodeSet='' where SrcFldId not in ("+wsql.toString()+")";
				dao.update(update);
			}
			rowSet=dao.search(sql);
			Element scheme = new Element("ArchScheme");
			Element relaSet= new Element("RelaSet");
			relaSet.setAttribute("SrcFldSet",SrcFldSet);
			relaSet.setAttribute("DestFldSet",DestFldSet);
			while(rowSet.next())
			{
				destFldId=rowSet.getString("destfldid");
				destFldName=rowSet.getString("destfldname");
				if(destFldId!=null&&destFldId.length()>0&&destFldName!=null&&destFldName.length()>0)
				{
					Element relaFld= new Element("RelaFld");
					relaFld.setAttribute("SrcFldId",rowSet.getString("srcfldid").toUpperCase());
					relaFld.setAttribute("SrcFldName",rowSet.getString("srcfldname"));
					relaFld.setAttribute("SrcCodeSet",rowSet.getString("srccodeset"));
					relaFld.setAttribute("DestFldId",destFldId.toUpperCase());
					relaFld.setAttribute("DestFldName",destFldName);
					relaFld.setAttribute("DestCodeSet",rowSet.getString("destcodeset")!=null&&rowSet.getString("destcodeset").length()>0?rowSet.getString("destcodeset"):"0");
					relaSet.addContent(relaFld);
				}
			}
			scheme.addContent(relaSet);
			Document myDocument = new Document(scheme);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp= outputter.outputString(myDocument);			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
	    	  if(rowSet!=null){
	    		  try {
	    			  rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
		return temp;
		
		
	}
}

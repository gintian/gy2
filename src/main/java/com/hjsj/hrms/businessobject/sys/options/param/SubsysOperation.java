/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.options.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *<p>Title:SubsysOperation.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:2:52:49 PM</p> 
 *@author huaitao
 *修改：郭峰  2013-7-15
 *@version 1.0
 *
 *  <?xml version='1.0' encoding="GB2312"?>
 *  <root >
 *  	。。。
 *  		<module  id="38" valid="false|true">
 *  			<rec  name="入职流程">1,2.3,4,</rec>#
 *  			<rec  name="离职流程">1,2.3,4,</rec>#
 *  		</module >
 *  		<module  id="37" valid="false|true">
 *  			<rec  name="入职流程">1,2.3,4,</rec>#
 *  			<rec  name="离职流程">1,2.3,4,</rec>#
 *  		</module >
 *  	。。。
 *  </root>
 */
public class SubsysOperation {
	private HashMap map = new HashMap() ;
	private Connection conn=null;
	private UserView userview;
	private Document doc=null;
	private String xmlcontent="";
	
	private void init()
	{
		try
		{
	     	RecordVo option_vo=ConstantParamter.getConstantVo("RSYW_PARAM");
	     	if(option_vo!=null) {
                xmlcontent=option_vo.getString("str_value");
            } else{
	     		StringBuffer strxml=new StringBuffer();
	    		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
	    		strxml.append("<root>");
	    		strxml.append("</root>");
	    		xmlcontent=strxml.toString();
	     	}	     	
			doc=PubFunc.generateDom(xmlcontent);	     	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public SubsysOperation(Connection conn) {
		super();
		this.conn = conn;
		init();
	}
	
	public SubsysOperation(Connection conn,UserView userview) {
		super();
		this.conn = conn;
		this.userview=userview;
		init();
	}
	public SubsysOperation() {
		super();
		init();
	}	
	/**
	 * 保存子集分类的子集名称
	 * @param id 业务id号
	 * @param tag rec属性name的值
	 * @param conn
	 * @return String
	 */
	public String saveView_param(String id,String tag,Connection conn)
	{
       String errmes = "";
   	   try{
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	    	ContentDAO dao = new ContentDAO(conn);
	        if (option_vo!=null){
	        	if(option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	         
			        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
			    	Element root = doc.getRootElement(); // 取得根节点
			    	List modulelist = root.getChildren("module".toLowerCase());
			    	Element module = null;
		    		if(modulelist!=null){
		    			boolean b = true;
		    			for(int j=0;j<modulelist.size();j++){
		    				module = (Element)modulelist.get(j);
		    				if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    					b=false;
		    					List reclist = module.getChildren("rec");
		    					for(int i=0;i<reclist.size();i++){
				    				Element rectemp = (Element)reclist.get(i);
				    				if(rectemp.getAttributeValue("name").equalsIgnoreCase(tag)){
					    				errmes = "11";
										return errmes;
				    				}
				    			}
		    					Element rec = new Element("rec");
					    		rec.setAttribute("name",tag);
					    		module.addContent(rec);
		    				}
		    			}
		    			if(b){
		    				module = new Element("module");
			    			module.setAttribute("id",id);
			    			module.setAttribute("valid","false");
			    			Element rec = new Element("rec");
				    		rec.setAttribute("name",tag);
				    		module.addContent(rec);
				    		root.addContent(module);
		    			}
		    		}
		    		else{
		    			module = new Element("module");
		    			module.setAttribute("id",id);
		    			module.setAttribute("valid","false");
		    			Element rec = new Element("rec");
			    		rec.setAttribute("name",tag);
			    		module.addContent(rec);
			    		root.addContent(module);
		    		}
			    	
			    	XMLOutputter outputter = new XMLOutputter();
				    Format format=Format.getPrettyFormat();
				    format.setEncoding("UTF-8");
				    outputter.setFormat(format);
				    xmls.append(outputter.outputString(doc));
				   
				    
				    /*
				    strsql.delete(0,strsql.length());
					strsql.append("update  constant set str_value=? where constant='RSYW_PARAM'");
					ContentDAO dao = new ContentDAO(conn);
					ArrayList list = new ArrayList();
					list.add(xmls.toString());
					try {
						dao.update(strsql.toString(), list);
					} catch (Exception e) {
						e.printStackTrace();
					}
					*/
					
				
					RecordVo vo=new RecordVo("constant");
					vo.setString("constant","RSYW_PARAM");
					vo=dao.findByPrimaryKey(vo);
					vo.setString("str_value", xmls.toString());
					dao.updateValueObject(vo);
					
					//strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
					//
					//System.out.println(strsql);
					//pstmt.execute(strsql.toString());
					errmes = "";
				}else
				{
					Element root = new Element("root");
		        	Document doc = new Document(root);
		        	Element module = new Element("module".toLowerCase());
		        	module.setAttribute("id",id);
	    			module.setAttribute("valid","false");
		        	Element rec = new Element("rec");
		        	//child.setText(fieldstr);
		        	rec.setAttribute("name",tag);
		        	module.addContent(rec);
		        	root.addContent(module);
		   	 	    XMLOutputter outputter = new XMLOutputter();
		        	Format format=Format.getPrettyFormat();
		        	format.setEncoding("UTF-8");
		        	outputter.setFormat(format);
		        	xmls.append(outputter.outputString(doc));
		        
		        	/*
		        	strsql.delete(0,strsql.length());
		        	strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
					//System.out.println(strsql.toString());
		        	//
			       	pstmt.execute(strsql.toString());
			       	*/
			       	 
					RecordVo vo=new RecordVo("constant");
					vo.setString("constant","RSYW_PARAM");
					vo=dao.findByPrimaryKey(vo);
					vo.setString("str_value", xmls.toString());
					dao.updateValueObject(vo);
			       	
			       	errmes = "";
				}
		    }else{
		        Element root = new Element("root");
	        	Document doc = new Document(root);
	        	Element module = new Element("module".toLowerCase());
	        	module.setAttribute("id",id);
    			module.setAttribute("valid","false");
	        	Element rec = new Element("rec");
	        	rec.setAttribute("name",tag);
	        	module.addContent(rec);
	        	root.addContent(module);
	   	 	    XMLOutputter outputter = new XMLOutputter();
	        	Format format=Format.getPrettyFormat();
	        	format.setEncoding("UTF-8");
	        	outputter.setFormat(format);
	        	xmls.append(outputter.outputString(doc));
	        	strsql.delete(0,strsql.length());
		       	strsql.append("insert into  constant(constant,type,str_value,describe)values('RSYW_PARAM','0','" + xmls.toString() + "','信息浏览字段附加功能')");
		       	dao.update(strsql.toString());
		       	errmes = "";
		    }
       }catch (Exception ee)
       {
         ee.printStackTrace();
         GeneralExceptionHandler.Handle(ee);
       }
       finally
       {
                   
     }
	return errmes; 
	}
	/**
	 * 保存子集分类的值
	 * @param id module的id号
	 * @param tag rec属性name的值
	 * @param fields 保存设置的数据值 [a01,a04...] or [a0101,a0107....]
	 * @param conn
	 */
	public void saveView_Value(String id,String tag,String[] fields,Connection conn)
	{
       Statement pstmt=null;
       String fieldstr="";
   	   try{
   		    for(int i=0;fields!=null && i<fields.length;i++)
   		    {
   		    	fieldstr+= fields[i]+",";
   		    }
   		    fieldstr = fieldstr.substring(0,fieldstr.length()-1);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
	    	Element root = doc.getRootElement(); // 取得根节点
	    	List modulelist = root.getChildren("module");
	    	if(modulelist==null||modulelist.size()<=0) {
                return;
            }
	    	for(int j=0;j<modulelist.size();j++){
	    		Element module = (Element)modulelist.get(j);
	    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
	    	    	List reclist = module.getChildren("rec");
	    	    	if(reclist==null||reclist.size()<=0) {
                        return;
                    }
	    	    	for(int i=0;i<reclist.size();i++){
	    	    		Element rec = (Element)reclist.get(i);
	    	    		if(rec.getAttributeValue("name").equalsIgnoreCase(tag)) {
                            rec.setText(fieldstr);
                        }
	    	    	}
	    		}
	    	}
	    	XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    xmls.append(outputter.outputString(doc));
		    strsql.delete(0,strsql.length());
		   
		   /* 
		    strsql.append("update  constant set str_value=? where constant='RSYW_PARAM'");
			ContentDAO dao = new ContentDAO(conn);
			ArrayList list = new ArrayList();
			list.add(xmls.toString());
			try {
				dao.update(strsql.toString(), list);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
		    ContentDAO dao = new ContentDAO(conn);
			RecordVo vo=new RecordVo("constant");
			vo.setString("constant","RSYW_PARAM");
			vo=dao.findByPrimaryKey(vo);
			vo.setString("str_value", xmls.toString());
			dao.updateValueObject(vo);
			
			
		    //strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
			//
			//pstmt.execute(strsql.toString());	
       }catch (Exception ee)
       {
         ee.printStackTrace();
         GeneralExceptionHandler.Handle(ee);
       }
       finally
       {
         try
         {
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
	
	/**
	 * 保存子集分类的值
	 * @param id module的id号
	 * @param tagname rec属性名称
	 * @param tag rec属性name的值
	 * @param fields 保存设置的数据值 [a01,a04...] or [a0101,a0107....]
	 * @param conn
	 */
	public void saveView_Value(String id,String tagname,String tag,String fields,Connection conn)
	{
       Statement pstmt=null;
   	   try{
   		   
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
	    	Element root = doc.getRootElement(); // 取得根节点
	    	List modulelist = root.getChildren("module");
	    	if(modulelist==null||modulelist.size()<=0) {
                return;
            }
	    	for(int j=0;j<modulelist.size();j++){
	    		Element module = (Element)modulelist.get(j);
	    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
	    	    	List reclist = module.getChildren("rec");
	    	    	if(reclist==null||reclist.size()<=0) {
                        return;
                    }
	    	    	for(int i=0;i<reclist.size();i++){
	    	    		Element rec = (Element)reclist.get(i);
	    	    		if(rec.getAttributeValue("name").equalsIgnoreCase(tag)) {
                            rec.setAttribute(tagname,fields);
                        }
	    	    	}
	    		}
	    	}
	    	XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    xmls.append(outputter.outputString(doc));
		    strsql.delete(0,strsql.length());
		/*
		    strsql.append("update  constant set str_value=? where constant='RSYW_PARAM'");
			ContentDAO dao = new ContentDAO(conn);
			ArrayList list = new ArrayList();
			list.add(xmls.toString());
			try {
				dao.update(strsql.toString(), list);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			
			   ContentDAO dao = new ContentDAO(conn);
				RecordVo vo=new RecordVo("constant");
				vo.setString("constant","RSYW_PARAM");
				vo=dao.findByPrimaryKey(vo);
				vo.setString("str_value", xmls.toString());
				dao.updateValueObject(vo);
			
			
			
			
		    //strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
			//
			//pstmt.execute(strsql.toString());	
       }catch (Exception ee)
       {
         ee.printStackTrace();
         GeneralExceptionHandler.Handle(ee);
       }
       finally
       {
         try
         {
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
	/**
	 * 得到该业务模板下该业务分类下的模板号。多个模板号是以逗号分隔的
	 * @param id module的id
	 * @param recname rec的 name
	 * @return String
	 */
	public String getView_value(String id,String recname){
		String value="";
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist==null||modulelist.size()<=0) {
                    return value;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				if(rec.getAttributeValue("name").equalsIgnoreCase(recname))
		    				{
		    					value = rec.getText();
		    					return value;
		    				}
		    					
		    			}
		    		}
		    	}
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
       return value;		
	}
	
	/**
	 * 得到rec的text内容，以","号隔开
	 * @param id module的id
	 * @param AttributeName 的rec 的属性名
	 * @param recname rec的 name
	 * @return String
	 */
	public String getView_value(String id,String AttributeName,String recname){
		String value="";
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist==null||modulelist.size()<=0) {
                    return value;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				if(rec.getAttributeValue("name").equalsIgnoreCase(recname)) {
                                value = rec.getAttributeValue(AttributeName);
                            }
		    			}
		    		}
		    	}
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
       return value;		
	}
	
	/**
	 * 得到业务模板下的所有业务分类的名字
	 * @param id module 的id
	 * @return ArrayList
	 */
	public ArrayList getView_tag(String id){
		ArrayList list = new ArrayList();
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");//所有的业务模板和业务分类的xml信息。如果业务分类为空，那么相应的module也不会显示
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");//得到所有的module。每个module是一个业务模板。业务模板下有许多业务分类
		    	if(modulelist==null||modulelist.size()<=0) {
                    return list;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				list.add(rec.getAttributeValue("name"));
		    			}
		    		}
		    	}
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
		return list;		
	}
	
	
	/**
	 * 得到业务模板下的所有业务分类的名字和模板ID
	 * @param id module 的id
	 * @return ArrayList
	 */
	public ArrayList getView_tag_tab(String id){
		ArrayList list = new ArrayList();
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");//所有的业务模板和业务分类的xml信息。如果业务分类为空，那么相应的module也不会显示
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");//得到所有的module。每个module是一个业务模板。业务模板下有许多业务分类
		    	if(modulelist==null||modulelist.size()<=0) {
                    return list;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				list.add(rec.getAttributeValue("name")+"~~"+rec.getText());
		    			}
		    		}
		    	}
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
		return list;		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 依据单位性质筛选出模板分类
	 * @param id module 的id
	 * @return ArrayList
	 */
	public ArrayList getChackView_tag(String id){
		ArrayList list = new ArrayList();		
		try{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");        	
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	String db_unittype=getDefaultDataUnittype();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist==null||modulelist.size()<=0) {
                    return list;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				String valid=rec.getAttributeValue("valid");
		    				String templates=rec.getText();
		    				if(!templatePriv(id,templates)) {
                                continue;
                            }
		    				if(valid!=null&&valid.length()>0) {
                                valid=valid.trim();
                            } else {
                                valid="";
                            }
		    				if(valid!=null&&("null".equalsIgnoreCase(valid.trim()))) {
                                valid="";
                            } else if("#".equals(valid))
		    				{
		    					valid="#";
		    				}
		    				else if(valid.length()>0)
		    				{
		    					valid=valid+",";
		    				}else if(db_unittype!=null&&db_unittype.length()>0&&valid.length()<=0)
		    				{
		    					valid=db_unittype+",";
		    				}		    					
		    				if(valid!=null&&valid.length()>0){
		    					ArrayList list1 = new ArrayList();
		    					ArrayList list2 = new ArrayList();
		    					String[] array1 = valid.split(",");
		    					String[] array2 = unit_type.split(",");
		    					if(array1!=null && array1.length>0){
		    						for(int k=0;k<array1.length;k++){
		    							if(!"".equals(array1[k])){
		    								list1.add(array1[k]);
		    							}
		    						}
		    					}
		    					if(array2!=null && array2.length>0){
		    						for(int k=0;k<array2.length;k++){
		    							if(!"".equals(array2[k])){
		    								list2.add(array2[k]);
		    							}
		    						}
		    					}
		    					for(int k=0;k<list2.size();k++){
		    						if(list1.contains(list2.get(k))){
		    							list.add(rec.getAttributeValue("name"));
		    							break;
		    						}
		    					}
		    				}else{
		    					list.add(rec.getAttributeValue("name"));
		    				}
		    			}
		    		}
		    	}
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
		return list;		
	}
	/**
	 * 删除rec
	 * @param id module 的id
	 * @param name rec子标签的name属性的值
	 */
	public void deleteTag(String id,String name){
		//Statement pstmt=null;
		RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
        try{
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	StringBuffer xmls=new StringBuffer();	       
	 	     	StringBuffer strsql=new StringBuffer();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist==null||modulelist.size()<=0) {
                    return;
                }
		    	for(int j=0;j<modulelist.size();j++){
		    		Element module = (Element)modulelist.get(j);
		    		if(module.getAttributeValue("id").equalsIgnoreCase(id)){
		    			List reclist = module.getChildren("rec");
		    			for(int i=0;i<reclist.size();i++){
		    				Element rec = (Element)reclist.get(i);
		    				if(rec.getAttributeValue("name").equalsIgnoreCase(name)){
		    					module.removeContent(rec);
		    					reclist.remove(rec);
		    					i = i-1;
		    				}
		    			}
		    			if(reclist.size()<=0){
		    				module.setAttribute("valid","false");
	    					this.map.put(id,"false");
		    			}
		    		}
		    	}
		    	XMLOutputter outputter = new XMLOutputter();
			    Format format=Format.getPrettyFormat();
			    format.setEncoding("UTF-8");
			    outputter.setFormat(format);
			    xmls.append(outputter.outputString(doc));
			    strsql.delete(0,strsql.length());
			    /*
			    strsql.append("update  constant set str_value=? where constant='RSYW_PARAM'");
				ContentDAO dao = new ContentDAO(conn);
				ArrayList list = new ArrayList();
				list.add(xmls.toString());
				try {
					dao.update(strsql.toString(), list);
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
				ContentDAO dao = new ContentDAO(conn);
				RecordVo vo=new RecordVo("constant");
				vo.setString("constant","RSYW_PARAM");
				vo=dao.findByPrimaryKey(vo);
				vo.setString("str_value", xmls.toString());
				dao.updateValueObject(vo);
				
			    //strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
				//
				//pstmt.execute(strsql.toString());	
	        }
        }catch (Exception ee)
 	   {
 	      ee.printStackTrace();
 	      GeneralExceptionHandler.Handle(ee);
 	   }     
	}
	
	/**
	 * 修改指标名称
	 * @param id module 的id
	 * @param name	old-name
	 * @param newname	update-name
	 */
	public String updateTag(String id,String name,String newname){
		RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
        String errmes = "";
        try{
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	StringBuffer xmls=new StringBuffer();	       
	 	     	StringBuffer strsql=new StringBuffer();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist!=null) {
                    for(int j=0;j<modulelist.size();j++){
                        Element module = (Element)modulelist.get(j);
                        if(module.getAttributeValue("id").equalsIgnoreCase(id)){
                            List reclist = module.getChildren("rec");
                            for(int i=0;i<reclist.size();i++){
                                Element rec = (Element)reclist.get(i);
                                if(rec.getAttributeValue("name").equalsIgnoreCase(name)) {
                                    continue;
                                }
                                if(rec.getAttributeValue("name").equalsIgnoreCase(newname)){
                                    errmes="11";
                                    return errmes;
                                }
                            }
                            for(int i=0;i<reclist.size();i++){
                                Element rec = (Element)reclist.get(i);
                                if(rec.getAttributeValue("name").equalsIgnoreCase(name)) {
                                    rec.setAttribute("name", newname);
                                }
                            }
                        }
                    }
                }
		    	
		    	XMLOutputter outputter = new XMLOutputter();
			    Format format=Format.getPrettyFormat();
			    format.setEncoding("UTF-8");
			    outputter.setFormat(format);
			    xmls.append(outputter.outputString(doc));
			    strsql.delete(0,strsql.length());
			    
			    /*
				strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
				//
				pstmt.execute(strsql.toString());	
				*/
			    ContentDAO dao = new ContentDAO(conn);
				RecordVo vo=new RecordVo("constant");
				vo.setString("constant","RSYW_PARAM");
				vo=dao.findByPrimaryKey(vo);
				vo.setString("str_value", xmls.toString());
				dao.updateValueObject(vo);
				
				
	        }
        }catch (Exception ee)
 	   {
 	      ee.printStackTrace();
 	      GeneralExceptionHandler.Handle(ee);
 	   }
       return errmes;
	}
	
	/**
	 * 
	 * @param id module的id
	 * @param flag true|false
	 */
	public void checkFlag(String id,String flag){
		RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
        try{
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	StringBuffer xmls=new StringBuffer();	       
	 	     	StringBuffer strsql=new StringBuffer();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
		    	Element root = doc.getRootElement(); // 取得根节点
		    	List modulelist = root.getChildren("module");
		    	if(modulelist!=null) {
                    for(int j=0;j<modulelist.size();j++){
                        Element module = (Element)modulelist.get(j);
                        if(module.getAttributeValue("id").equalsIgnoreCase(id)){
                            module.setAttribute("valid",flag);
                            this.map.put(id,flag);
                        }
                    }
                }
		    	XMLOutputter outputter = new XMLOutputter();
			    Format format=Format.getPrettyFormat();
			    format.setEncoding("UTF-8");
			    outputter.setFormat(format);
			    xmls.append(outputter.outputString(doc));
			    strsql.delete(0,strsql.length());
			    ContentDAO dao = new ContentDAO(conn);
			    RecordVo vo=new RecordVo("constant");
			    vo.setString("constant","RSYW_PARAM");
			    vo=dao.findByPrimaryKey(vo);
			    vo.setString("str_value", xmls.toString());
			    dao.updateValueObject(vo);
		//		strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='RSYW_PARAM'");
		//		
		//		pstmt.execute(strsql.toString());	
	        }
        }catch (Exception ee)
 	   {
 	      ee.printStackTrace();
 	      GeneralExceptionHandler.Handle(ee);
 	   }
	}
	/**
	 * 得到节点value属性值 module节点
	 * @param id module的id
	 * @return
	 */
	public String getAttributeValue(String id){
		RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
        String value = "";
        try {
			if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
					&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
			    Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
				Element root = doc.getRootElement(); // 取得根节点
				List modulelist = root.getChildren("module");
				if(modulelist!=null) {
                    for(int j=0;j<modulelist.size();j++){
                        Element module = (Element)modulelist.get(j);
                        if(module.getAttributeValue("id").equalsIgnoreCase(id)){
                            value = module.getAttributeValue("valid");
                        }
                    }
                }
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return value;
	}
	/**
	 * 把模板号及其是否启用放入map
	 * key 为模板id，value为ture|false字符串
	 * @return
	 */
	public HashMap getMap(){
		if(this.map.size()>0) {
            return this.map;
        } else{
			RecordVo option_vo=ConstantParamter.getRealConstantVo("RSYW_PARAM");
	        try {
				if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
						&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
				    Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml	  
					Element root = doc.getRootElement(); // 取得根节点
					List modulelist = root.getChildren("module");
					if(modulelist!=null) {
                        for(int j=0;j<modulelist.size();j++){
                            Element module = (Element)modulelist.get(j);
                            this.map.put(module.getAttributeValue("id"),module.getAttributeValue("valid"));
                        }
                    }
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this.map;
		}
	}
	private String getDefaultDataUnittype()
	{
		ContentDAO dao=new ContentDAO(conn);
		String unittype="0";
		try
		{
			RowSet rs=dao.search("select 1 from lstyle where styledesc='干部任免'");
			if(rs.next()) {
                unittype="1,2";
            } else {
                unittype="3";
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return unittype;
	}
	private boolean templatePriv(String sort_id,String templates)
	{
		if(templates==null||templates.length()<=0) {
            return false;
        }
		if(this.userview.isSuper_admin()||this.userview==null) {
            return true;
        }
		String template_id="";
		if("30".equals(sort_id)|| "37".equals(sort_id)|| "34".equals(sort_id)|| "39".equals(sort_id)|| "38".equals(sort_id)|| "40".equals(sort_id)|| "56".equals(sort_id)|| "57".equals(sort_id)|| "51".equals(sort_id)|| "52".equals(sort_id)|| "53".equals(sort_id)|| "54".equals(sort_id)|| "55".equals(sort_id)|| "61".equals(sort_id))
		{
			String templateArr[]=templates.split(",");
			for(int i=0;i<templateArr.length;i++)
			{
				template_id=templateArr[i];
				if("30".equals(sort_id)|| "37".equals(sort_id)|| "40".equals(sort_id)|| "38".equals(sort_id)|| "55".equals(sort_id)|| "61".equals(sort_id))
				{
					if(this.userview.isHaveResource(IResourceConstant.RSBD, template_id)) {
                        return true;
                    }
				}else if("34".equals(sort_id))
				{
					if(this.userview.isHaveResource(IResourceConstant.GZBD, template_id)) {
                        return true;
                    }
				}
				else if("39".equals(sort_id))
				{
					if(this.userview.isHaveResource(IResourceConstant.INS_BD, template_id)) {
                        return true;
                    }
				}
				else if("56".equals(sort_id))
				{
					if(this.userview.isHaveResource(IResourceConstant.ORG_BD, template_id)) {
                        return true;
                    }
				}
				else if("57".equals(sort_id))
				{
					if(this.userview.isHaveResource(IResourceConstant.POS_BD, template_id)) {
                        return true;
                    }
				}
				else if("51".equals(sort_id)){
					if(this.userview.isHaveResource(IResourceConstant.PSORGANS, template_id)) {
                        return true;
                    }
				}
				else if("52".equals(sort_id)){
					if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG, template_id)) {
                        return true;
                    }
				}
				else if("53".equals(sort_id)){
					if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG, template_id)) {
                        return true;
                    }
				}
				else if("54".equals(sort_id)){
					if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX, template_id)) {
                        return true;
                    }
				}
			}
		}
		return false;
	}
}


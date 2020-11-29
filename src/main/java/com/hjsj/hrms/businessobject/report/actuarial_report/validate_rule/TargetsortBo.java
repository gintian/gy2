/**
 * 
 */
package com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TargetsortBo{

	
	
	
	private  Connection conn=null;
	private  Document doc=null;
	private  String xmlcontent="";
	private  static HashMap _map = null;
	public  TargetsortBo(Connection conn){
		super();
		this.conn = conn;
		init();
	}

	public static ArrayList getPersonsid(Connection con) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from codeitem where codesetid='61' ");
		ContentDAO dao = new ContentDAO(con);
		
		ArrayList list = new ArrayList();
		
		try 
		{
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) 
			{
				list.add(rs.getString("codeitemid"));
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	 	return list;
	}
	public  void updateinit(StringBuffer xmls){
		PreparedStatement pstmt=null;
//		System.out.println("初试化操作");
		String strsql ="insert into constant(constant,str_value) values(?,?)";
		try {
			pstmt = conn.prepareStatement(strsql.toString());				
			pstmt.setString(1, "CAL_REPORT");
			pstmt.setString(2, xmls.toString());	
			pstmt.executeUpdate();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if(pstmt!=null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
	}
	public  ArrayList getTargetsortContent() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from codeitem where codesetid='61' ");
		ContentDAO dao = new ContentDAO(conn);
		
		ArrayList list = new ArrayList();
		
		try 
		{
			RowSet rs = dao.search(strsql.toString());
			
			//rs = dao.search(strsql.toString());
			while (rs.next()) 
			{
				//获得id,然后解析constant中的CAL_REPORT,获得对应的指标
				LazyDynaBean a_bean = new LazyDynaBean();
				rs.getString("codeitemid");
				a_bean.set("codeitemid", rs.getString("codeitemid"));
				a_bean.set("codeitemdesc", rs.getString("codeitemdesc"));
				a_bean.set("targetcontent",getView_value(rs.getString("codeitemid")));
				list.add(a_bean);
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	 	return list;
	}
	/**
	 * 保存指标分类的值
	 * @param nodename a01,a04....or set_a
	 * @param tag rec属性name的值
	 * @param fields 保存设置的数据值 [a01,a04...] or [a0101,a0107....]
	 * @param conn
	 */
	public static void saveView_Value(String tag,String[] fields,Connection conn)
	{
       Statement pstmt=null;
       String fieldstr="";
   	   try{
   		    for(int i=0;fields!=null && i<fields.length;i++)
   		    {
   		    // FieldItem fielditem =DataDictionary.getFieldItem(fields[i].toUpperCase());
 			if(fields[i]!=null) {
                fieldstr+= fields[i]+",";
            }
   		    }
   		
   		    
   		    fieldstr = fieldstr.substring(0,fieldstr.length()-1);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
	        //读入xml
	        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
	        String xpath = "//field[@id=\"" + tag + "\"]";
        	XPath xpath_ = XPath.newInstance(xpath);
        	if(xpath_.selectSingleNode(doc)!=null){
        		Element ele = (Element) xpath_.selectSingleNode(doc);
            	ele.setText(fieldstr);
        	}else{
        		//新增一个人员范围
        		//获得父类元素
        		Element parent = doc.getRootElement();
        		Element child = parent.getChild("fields");
        		 Element node = new Element("field"); 
        		 node.setAttribute("id",tag);
        		 node.setText(fieldstr);
        		 child.addContent(node);
        		 
        	}
        	
	    	XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    xmls.append(outputter.outputString(doc));
		    strsql.delete(0,strsql.length());
			strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='CAL_REPORT'");
			pstmt=conn.createStatement();
			pstmt.execute(strsql.toString());	
			//_map.remove(tag);
			if(_map==null){
				_map = getTargetsortMap(conn);
			}
			_map.put(tag, fieldstr);
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
	 * 得到子集相关字段，以","号隔开
	 * @param tag 子集a01,a04.... 或 指标set_a
	 * @param recname rec的子集或指标 name
	 * @return
	 */
	public static String getView_value(String tag) throws GeneralException {
		String value="";
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		      //读入xml
	            Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
		        String xpath = "//field[@id=\"" + tag + "\"]";
	        	XPath xpath_ = XPath.newInstance(xpath);
	        	if(xpath_.selectSingleNode(doc)!=null){
	        	Element ele = (Element) xpath_.selectSingleNode(doc);
	        	String str = ele.getText();
	        	if(str.length()>0){
	        		String fields []= str.split(",");
	        		 
		 			for(int i=0;i<fields.length;i++){
		 				 FieldItem fielditem =DataDictionary.getFieldItem(fields[i].toUpperCase());
	        		  if(fielditem!=null) {
                          value+= fielditem.getItemdesc()+",";
                      }
		 			}
		 			value = value.substring(0,value.length()-1);
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
	
	public static HashMap getTargetsortMap(Connection conn){
		
		String value="";
		HashMap map= new HashMap();
		try{
			if(_map==null)
			{
	        	RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
		        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
		        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
			        //读入xml
		            Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
			        ArrayList list =getPersonsid(conn);
			    	for(int i=0;i<list.size();i++){
			    		String xpath = "//field[@id=\"" + list.get(i) + "\"]";
			        	XPath xpath_ = XPath.newInstance(xpath);
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
				     
			        	value = ele.getText()==null?"":ele.getText();
			        	map.put(list.get(i).toString().toLowerCase(), value);
			        	
			        	
			    	}
			    	Element root = doc.getRootElement();
					Element check_param = root.getChild("check_param");
					List list2 = check_param.getChildren();
				
					for(int i =0;i<list2.size();i++){
						Element temp = (Element)list2.get(i);
						List listattr =(List)temp.getAttributes();
						HashMap map3= new HashMap();
						for(int j=0;j<listattr.size();j++){
							
							Attribute attr =  (Attribute) listattr.get(j);
							//System.out.println(attr.getName().toLowerCase()+"--->"+ temp.getAttributeValue(attr.getName()));
							map3.put(attr.getName().toLowerCase(), temp.getAttributeValue(attr.getName()));
						}
						map.put(temp.getAttributeValue("report_id").toString().toLowerCase(), map3);
					}
		    	
			   
			    }
		        _map=map;
			}
			else {
                map=_map;
            }
				
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
       return map;		
	}

	
	private void init()
	{
		PreparedStatement pstmt=null;
		try
		{
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT",this.conn);
	     	if(option_vo!=null){
	            if (option_vo!=null && option_vo.getString("str_value")!=null 
	            		&& option_vo.getString("str_value").trim().length()>0 && option_vo.getString("str_value").indexOf("xml")!=-1) {
	    		//读入xml
	            Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
				Element root = doc.getRootElement();
				Element check_param = root.getChild("check_param");
				if(check_param==null){
//					System.out.println("未初始化");
					check_param = new Element("check_param"); 
					root.addContent(check_param);
	        		 Element child = new Element("check_data"); 
	        		 child.setAttribute("report_id","U03");
	        		 child.setAttribute("differ","");
	        		 child.setAttribute("differPercent","");
	        		 
	        		 Element child2 = new Element("check_data");
	        		 child2.setAttribute("report_id","U05");
	        			ArrayList list =	getInitParam();
	        			for(int i=0;i<list.size();i++){
	        				child2.setAttribute("medic_differ_"+list.get(i),"");
	        				child2.setAttribute("medic_differPercent_"+list.get(i),"");
	    		    		if(!"4".equals(list.get(i))){
	    		    			child2.setAttribute("other_differ_"+list.get(i),"");
	    		    			child2.setAttribute("other_differPercent_"+list.get(i),"");
	    		    		}
	    		    		}
	        			check_param.addContent(child);
	        			check_param.addContent(child2);	
	        			
	        			 XMLOutputter outputter = new XMLOutputter();
			     		    Format format=Format.getPrettyFormat();
			     		    format.setEncoding("UTF-8");
			     		    outputter.setFormat(format);
			     		   xmlcontent=outputter.outputString(doc);
			     		
			     		   
				     		String strsql ="update  constant set str_value='" + xmlcontent+ "' where constant='CAL_REPORT'";
				     		
				     		pstmt=conn.prepareStatement(strsql);
				  			pstmt.executeUpdate();
//				  		   System.out.println("xmlcontent:"+xmlcontent);
				}else{//u05,u03缺少属性
			List list2 = check_param.getChildren();
			Element u03 =null;
			Element u05 =null;
			boolean flag =false;
			for(int i =0;i<list2.size();i++){
				Element temp = (Element)list2.get(i);
				if("U03".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                    u03=temp;
                }
				if("U05".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                    u05=temp;
                }
				
			}
			if(u03.getAttributeValue("differ")==null){
				flag=true;
				u03.setAttribute("differ","");
				u03.setAttribute("differPercent","");
			}
			ArrayList list =	getInitParam();
			for(int i=0;i<list.size();i++){
			if(u05.getAttributeValue("medic_differ_"+list.get(i))==null){
				flag=true;
				u05.setAttribute("medic_differ_"+list.get(i),"");
				u05.setAttribute("medic_differPercent_"+list.get(i),"");
	    		if(!"4".equals(list.get(i))){
	    			u05.setAttribute("other_differ_"+list.get(i),"");
	    			u05.setAttribute("other_differPercent_"+list.get(i),"");
	    		}
			}
			}
			if(flag){
			 XMLOutputter outputter = new XMLOutputter();
  		    Format format=Format.getPrettyFormat();
  		    format.setEncoding("UTF-8");
  		    outputter.setFormat(format);
  		   xmlcontent=outputter.outputString(doc);
			
     		String strsql ="update  constant set str_value='" + xmlcontent+ "' where constant='CAL_REPORT'";
     		
     		pstmt=conn.prepareStatement(strsql);
  			pstmt.executeUpdate();
			}
				}
	            }
		        	
	     	}
	     	else{
	     		StringBuffer strxml=new StringBuffer();
	    		strxml.append("<?xml version='1.0' encoding='GB2312'?>");
	    		strxml.append("<param>");
	    		strxml.append("<fields>");
	    	ArrayList list =	getInitParam();
	    	for(int i=0;i<list.size();i++){
	    		strxml.append("<field id=\""+list.get(i)+"\"></field>");
	    	}
	    		strxml.append("</fields>");
	    		strxml.append("<check_param>");
	    		strxml.append("<check_data report_id=\"U03\"  differ=\"\"  differPercent=\"\" /> ");
	    		strxml.append("<check_data report_id=\"U05\" ");
	    		for(int i=0;i<list.size();i++){
		    		strxml.append(" medic_differ_"+list.get(i)+"=\"\"");
		    		strxml.append(" medic_differPercent_"+list.get(i)+"=\"\"");
		    		if(!"4".equals(list.get(i))){
		    		strxml.append(" other_differ_"+list.get(i)+"=\"\"");
		    		strxml.append(" other_differPercent_"+list.get(i)+"=\"\"");
		    		}
		    		}
	    		strxml.append(" />");
	    		strxml.append("</check_param>");
	    		strxml.append("</param>");
	    		xmlcontent=strxml.toString();
//	    		System.out.println(xmlcontent);
	    		updateinit(strxml);
	     	}
     	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally{
			if(pstmt!=null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
	}
	public  ArrayList getInitParam() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from codeitem where codesetid='61' ");
		ContentDAO dao = new ContentDAO(conn);
		
		ArrayList list = new ArrayList();
		
		try 
		{
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) 
			{
				list.add(rs.getString("codeitemid"));
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	 	return list;
	}
	public  ArrayList getPerson() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from codeitem where codesetid='61' ");
		ContentDAO dao = new ContentDAO(conn);
		
		ArrayList list = new ArrayList();
		
		try 
		{
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) 
			{
				list.add(rs.getString("codeitemdesc"));
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	 	return list;
	}
//	public static ArrayList getPersonsid(Connection con) throws GeneralException {
//		StringBuffer strsql = new StringBuffer();
//		strsql.append("select * from codeitem where codesetid='61' ");
//		ContentDAO dao = new ContentDAO(con);
//		
//		ArrayList list = new ArrayList();
//		
//		try 
//		{
//			RowSet rs = dao.search(strsql.toString());
//			while (rs.next()) 
//			{
//				list.add(rs.getString("codeitemid"));
//			}
//
//		}
//		catch(Exception ee)
//		{
//			ee.printStackTrace();
//		    throw GeneralExceptionHandler.Handle(ee);				
//		}
//	 	return list;
//	}
//	public  void updateinit(StringBuffer xmls){
//		PreparedStatement pstmt=null;
//		System.out.println("初试化操作"+xmls);
//		String strsql ="insert into constant(constant,str_value) values(?,?)";
//		try {
//			pstmt = conn.prepareStatement(strsql.toString());				
//			pstmt.setString(1, "CAL_REPORT");
//			pstmt.setString(2, xmls.toString());	
//			pstmt.executeUpdate();	
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//	}
//	public  ArrayList getTargetsortContent() throws GeneralException {
//		StringBuffer strsql = new StringBuffer();
//		strsql.append("select * from codeitem where codesetid='61' ");
//		ContentDAO dao = new ContentDAO(conn);
//		
//		ArrayList list = new ArrayList();
//		
//		try 
//		{
//			RowSet rs = dao.search(strsql.toString());
//			
//			//rs = dao.search(strsql.toString());
//			while (rs.next()) 
//			{
//				//获得id,然后解析constant中的CAL_REPORT,获得对应的指标
//				LazyDynaBean a_bean = new LazyDynaBean();
//				rs.getString("codeitemid");
//				a_bean.set("codeitemid", rs.getString("codeitemid"));
//				a_bean.set("codeitemdesc", rs.getString("codeitemdesc"));
//				a_bean.set("targetcontent",getView_value(rs.getString("codeitemid")));
//				list.add(a_bean);
//			}
//
//		}
//		catch(Exception ee)
//		{
//			ee.printStackTrace();
//		    throw GeneralExceptionHandler.Handle(ee);				
//		}
//	 	return list;
//	}
	/**
	 * 保存指标分类的值
	 * @param nodename a01,a04....or set_a
	 * @param tag rec属性name的值
	 * @param fields 保存设置的数据值 [a01,a04...] or [a0101,a0107....]
	 * @param conn
	 */
	public static void saveView_Value( String paramcopy,HashMap map,Connection conn)
	{
       Statement pstmt=null;
      // String fieldstr="";
       RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
   	Element u03 =null;
		Element u05 =null;
		String paramlist []=null;
   	   try{
   		 if (option_vo!=null && option_vo.getString("str_value")!=null 
   	       		&& option_vo.getString("str_value").trim().length()>0 && option_vo.getString("str_value").indexOf("xml")!=-1) {
   		//读入xml
         Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
		Element root = doc.getRootElement();
		Element check_param = root.getChild("check_param");
		List list = check_param.getChildren();
	
		for(int i =0;i<list.size();i++){
			Element temp = (Element)list.get(i);
			if("U03".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                u03=temp;
            }
			if("U05".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                u05=temp;
            }
		}
   		if(paramcopy.indexOf(",")!=-1){
			 paramlist  = paramcopy.split(",");
			for(int i=0;i<paramlist.length;i++){
//				System.out.println(paramlist[i]);
				if(u03.getAttributeValue(paramlist[i])!=null){
					u03.setAttribute(paramlist[i],map.get(paramlist[i]).toString());
				}
				if(u05.getAttributeValue(paramlist[i])!=null){
//					System.out.println(paramlist[i]+"--"+map.get(paramlist[i]).toString());
					u05.setAttribute(paramlist[i],map.get(paramlist[i]).toString());
				}
			}
				
			}
   		
   		
   		
   		    
   		   // fieldstr = fieldstr.substring(0,fieldstr.length()-1);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
        	
	    	XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    xmls.append(outputter.outputString(doc));
//		    System.out.println(outputter.outputString(doc));
		    strsql.delete(0,strsql.length());
			strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='CAL_REPORT'");
			pstmt=conn.createStatement();
			pstmt.execute(strsql.toString());	
			//_map.remove(tag);
			if(_map==null){
			_map = getTargetsortMap(conn);
		}else{
			getValidesortMap(_map);
		}
//			for(int i=0;i<paramlist.length;i++){
//			if(_map==null){
//				_map = getTargetsortMap(conn);
//			}
//			_map.put(paramlist[i].toLowerCase(), map.get(paramlist[i]).toString());
//			}
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
	
	public static void getValidesortMap(HashMap map){
		
		try{
			
	        	RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
		        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
		        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
			      //读入xml
		            Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
			    	Element root = doc.getRootElement();
					Element check_param = root.getChild("check_param");
					List list2 = check_param.getChildren();
				
					for(int i =0;i<list2.size();i++){
						Element temp = (Element)list2.get(i);
						List listattr =(List)temp.getAttributes();
						HashMap map3= new HashMap();
						for(int j=0;j<listattr.size();j++){
							
							Attribute attr =  (Attribute) listattr.get(j);
							//System.out.println(attr.getName().toLowerCase()+"--->"+ temp.getAttributeValue(attr.getName()));
							map3.put(attr.getName().toLowerCase(), temp.getAttributeValue(attr.getName()));
						}
						map.put(temp.getAttributeValue("report_id").toString().toLowerCase(), map3);
					}
		    	
			   
			    }
			
				
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
	}
	/**
	 * 生成报表信息
	 * 
	 * @param reportSetList
	 * @return
	 */
	public String getTableHtml() throws GeneralException{
		StringBuffer html = new StringBuffer("");
		RecordVo option_vo=ConstantParamter.getRealConstantVo("CAL_REPORT");
    	Element u03 =null;
		Element u05 =null;
        if (option_vo!=null && option_vo.getString("str_value")!=null 
        		&& option_vo.getString("str_value").trim().length()>0 && option_vo.getString("str_value").indexOf("xml")!=-1) {
        	try {
        	  //读入xml
                Document doc = PubFunc.generateDom(option_vo.getString("str_value"));
				Element root = doc.getRootElement();
				Element check_param = root.getChild("check_param");
//				getTargetsortMap(conn);
//				String xpath = "//check_data[@report_id=\"U05\"]";
//	        	XPath xpath_ = XPath.newInstance(xpath);
//	        	Element ele = (Element) xpath_.selectSingleNode(doc);
//	        	System.out.println("ele:"+ele.getAttributeValue("report_id"));
	        	//value = ele.getText()==null?"":ele.getText();
//				List listm = check_param.getChildren();
//				XMLOutputter outputter = new XMLOutputter();
//			    Format format=Format.getPrettyFormat();
//			    format.setEncoding("UTF-8");
//			    outputter.setFormat(format);
//			    System.out.println(outputter.outputString(doc));
				
//				for(int i =0;i<listm.size();i++){
//					Element temp = (Element)listm.get(i);
//					List listattr =(List)temp.getAttributes();
//					//HashMap map2 = new HashMap();
//					for(int j=0;j<listattr.size();j++){
//						HashMap map3= new HashMap();
//						Attribute attr =  (Attribute) listattr.get(j);
//						
//						System.out.println("atrr:"+attr.getName());
//						map3.put(listattr.get(j), temp.getAttribute(listattr.get(j).toString()));
//					//map.put(temp.getAttribute("report_id"), map3);
//					
//					}
//					
//				}
				List list2 = check_param.getChildren();
			
				for(int i =0;i<list2.size();i++){
					Element temp = (Element)list2.get(i);
					if("U03".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                        u03=temp;
                    }
					if("U05".equalsIgnoreCase(temp.getAttributeValue("report_id").toString())) {
                        u05=temp;
                    }
				}
//				 String xpath = "//check_data[@report_id=\"03\"]";
//		        	XPath xpath_ = XPath.newInstance(xpath);
//		        	Element ele = (Element) xpath_.selectSingleNode(doc);
		        	if(u03!=null&& "U03".equalsIgnoreCase(u03.getAttributeValue("report_id").toString())){
				html
				.append("<thead><tr><td  valign='top' align='left' class='TableRow' colspan='4'  nowrap >&nbsp;表3 财务信息</td></thead>");
				html
				.append("<tr class='trShallow'><td   align='center'  class='RecordRow'  nowrap >&nbsp;差异金额</td>");
				html
					.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"differ\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u03.getAttributeValue("differ")+"\"/></td>");
				html
				.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;差异率</td>");
				html
					.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"differPercent\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u03.getAttributeValue("differPercent")+"\"/>%</td>");
				html
				.append("</tr>");
		        	}else{
		        		html
						.append("<thead><tr><td  valign='top' align='left' class='TableRow' colspan='4'  nowrap >&nbsp;表3 财务信息</td></thead>");
						html
						.append("<tr><td   align='center' class='RecordRow'  nowrap >&nbsp;差异金额</td>");
						html
							.append("<td class='RecordRow'><input type=\"text\" maxlength='12' stype=\"text\" maxlength='12'100px\" name=\"differ\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/></td>");
						html
						.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;差异率</td>");
						html
							.append("<td class='RecordRow' ><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"differPercent\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/>%</td>");
						html
						.append("</tr>");	
		        	}
				html
				.append("<thead><tr><td  valign='top' align='left' class='TableRow' colspan='4'  nowrap >&nbsp;表5 人员变动及人均福利对照表</td></thead>");
				
				ArrayList list =	getInitParam();
//				  xpath = "//check_data[@report_id=\"05\"]";
//		        	 xpath_ = XPath.newInstance(xpath);
//		        	  ele = (Element) xpath_.selectSingleNode(doc);
//			        	
				ArrayList listname =	getPerson();
		    	for(int i=0;i<list.size();i++){
	        		  String name=listname.get(i).toString();
		    			html
						.append("<tr class=\"trShallow\"><td   align='left' class='RecordRow' colspan=\"4\"  nowrap >&nbsp;"+name+"</td></tr>");
						
		    		if(u05!=null&& "U05".equalsIgnoreCase(u05.getAttributeValue("report_id").toString())){
		    			String sname ="";
		    			if("4".equals(list.get(i))){
		    				sname ="除";
		    			}
			        	
		    		html
					.append("<tr><td   align='center' class='RecordRow'  nowrap >&nbsp;"+sname+"医疗福利差异金额</td>");
					html
						.append("<td  class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"medic_differ_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u05.getAttributeValue("medic_differ_"+list.get(i))+"\"/></td>");
					html
					.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;"+sname+"医疗福利差异率</td>");
					html
						.append("<td  class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"medic_differPercent_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u05.getAttributeValue("medic_differPercent_"+list.get(i))+"\"/>%</td>");
					html
					.append("</tr>");
		    		}else{
		    			String sname ="";
		    			if("4".equals(list.get(i))){
		    				sname ="除";
		    			}
		    			html
						.append("<tr><td   align='center' class='RecordRow'  nowrap >&nbsp;"+sname+"医疗福利差异金额</td>");
						html
							.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"medic_differ_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/></td>");
						html
						.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;"+sname+"医疗福利差异率</td>");
						html
							.append("<td class='RecordRow' ><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"medic_differPercent_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/>%</td>");
						html
						.append("</tr>");	
		    		}
		    		if(!"4".equals(list.get(i))){
		    			if(u05!=null&& "U05".equalsIgnoreCase(u05.getAttributeValue("report_id").toString())){
				        	
		    			html
						.append("<tr><td   align='center' class='RecordRow'  nowrap >&nbsp;其它福利差异金额</td>");
						html
							.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"other_differ_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u05.getAttributeValue("other_differ_"+list.get(i))+"\"/></td>");
						html
						.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;其它福利差异率</td>");
						html
							.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"other_differPercent_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+u05.getAttributeValue("other_differPercent_"+list.get(i))+"\"/>%</td>");
						html
						.append("</tr>");
		    			}else{
		    				html
							.append("<tr><td   align='center' class='RecordRow'  nowrap >&nbsp;其它福利差异金额</td>");
							html
								.append("<td class='RecordRow' ><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"other_differ_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/></td>");
							html
							.append("<td   align='center' class='RecordRow'  nowrap >&nbsp;其它福利差异率</td>");
							html
								.append("<td class='RecordRow'><input type=\"text\" maxlength='12' style=\"width:100px\" name=\"other_differPercent_"+list.get(i)+"\"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/>%</td>");
							html
							.append("</tr>");	
		    			}
			    		}
				}
	      
	        } catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
                e.printStackTrace();
            }      
	}
        return html.toString();
	}

}

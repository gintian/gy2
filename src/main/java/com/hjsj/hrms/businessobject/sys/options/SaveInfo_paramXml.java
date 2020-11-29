/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author wlh
 *  <?xml version='1.0' encoding="GB2312"?>
 *	<root>
 *		。。。
 *	   <sp_flag flag="false|true"/>  #CS编辑信息审批标志
 *
 *	   <browser>AXXX,AYXX</browser> #BS 人员信息浏览，对主集增加其它指标  huaitao add 2008-03-13
 *		。。。
 *		<view>#新增控制参数 20080226
 *			<A01>	
 *				<rec "基本情况">AXXX,AYXX</rec>
 *				<rec "联系方式"> AXXX,AYXX</rec>
 *				<rec "其它"> AXXX,AYXX</rec>
 *				。。。
 *			</A01>
 *			<SET_A>
 *				<rec "基本">A01,A04</rec>
 *				<rec "薪资">A01,A04</rec>
 *				<rec "保险">A01,A04</rec>
 *				。。。
 *			</SET_A>
 *		</view> #BS 人员信息浏览，显示方案
 *		。。。
 *	</root>
 */
public class SaveInfo_paramXml {
	
	private Connection conn=null;
	private Document doc=null;
	private String xmlcontent="";
	
	private void init()
	{
		try
		{
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM",this.conn);
	     	if(option_vo!=null) {
                xmlcontent=option_vo.getString("str_value");
            } else{
	     		StringBuffer strxml=new StringBuffer();
	    		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
	    		strxml.append("<root>");
	    		strxml.append("</root>");
	    		xmlcontent=strxml.toString();
	     	}
	     	if(xmlcontent==null||xmlcontent.length()<=0)
	     	{
	     		StringBuffer strxml=new StringBuffer();
	    		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
	    		strxml.append("<root>");
	    		strxml.append("</root>");
	    		xmlcontent=strxml.toString();
	     	}
			doc=PubFunc.generateDom(xmlcontent.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public SaveInfo_paramXml(Connection conn) {
		super();
		this.conn = conn;
		init();
	}
	

	/**
	 * 取得对应节点参数的值
	 * @param param_name
	 * @return
	 */
	private String getValue(String param_name)
	{
		String value="";
		if(doc==null) {
            return value;
        }
		if(!"".equals(param_name))
		{
		  try
		  {
			String str_path="/root/"+param_name;
			XPath xpath=XPath.newInstance(str_path);
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
	 * 取得主集字段列表
	 * @return
	 */
	public ArrayList getMainSetFieldList()
	{
		ArrayList list=new ArrayList();
		if(xmlcontent==null|| "".equalsIgnoreCase(xmlcontent)) {
            return list;
        }
		String fields=getValue("browser");
		if(fields==null||fields.length()==0) {
            return list;
        }
		String[] fieldarr=StringUtils.split(fields, ",");
		for(int i=0;i<fieldarr.length;i++)
		{
			String field_name=fieldarr[i];
			//zxj 20150708 fielditem和t_hr_busifield中的指标有重名的，这里只取人员主集的
			FieldItem item = DataDictionary.getFieldItem(field_name, "A01");
			if(item==null) {
                continue;
            }
			/**如果未构库，则不用取*/
			if("0".equalsIgnoreCase(item.getUseflag())) {
                continue;
            }
			if(item!=null) {
                list.add(item);
            }
		}//for loop end.
		return list;
	}
	
	/**保存INFO_PARAM常量xml得节点值*/
	public void saveInfo_paramNode(String nodename,String[] fields,Connection conn)
	{
       Statement pstmt=null;
       String fieldstr="";
   	   try{
   		    for(int i=0;fields!=null && i<fields.length;i++)
   		    {
   		    	fieldstr+= "," + PubFunc.keyWord_reback(fields[i]);
   		    }
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        if (option_vo!=null){
	        	if(option_vo.getString("str_value").toLowerCase()!=null 

	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	         
			        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());      //读入xml
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
				    RecordVo vo = new RecordVo("constant");
					vo.setString("constant", "INFO_PARAM");
					ContentDAO dao=new ContentDAO(this.conn);
					vo=dao.findByPrimaryKey(vo);
					vo.setString("str_value", xmls.toString());
					dao.updateValueObject(vo);
					/*strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='INFO_PARAM'");
					
					pstmt.execute(strsql.toString());	*/
				}else
				{
					Element root = new Element("root");
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
		            RecordVo vo = new RecordVo("constant");
					vo.setString("constant", "INFO_PARAM");
					ContentDAO dao=new ContentDAO(this.conn);
					vo=dao.findByPrimaryKey(vo);
					vo.setString("str_value", xmls.toString());
					dao.updateValueObject(vo);
		        	/*strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='INFO_PARAM'");
					//System.out.println(strsql.toString());
		        	
			       	pstmt.execute(strsql.toString())*/;	
				}
		    }else{
		        Element root = new Element("root");
	        	Document doc = new Document(root);
	        	Element child = new Element(nodename);
	        	child.setText(fieldstr);
	        	root.addContent(child);
	   	 	    XMLOutputter outputter = new XMLOutputter();
	        	Format format=Format.getPrettyFormat();
	        	format.setEncoding("UTF-8");
	        	outputter.setFormat(format);
	        	xmls.append(outputter.outputString(doc));
	            RecordVo vo = new RecordVo("constant");
				vo.setString("constant", "INFO_PARAM");
				vo.setString("str_value", xmls.toString());
				vo.setString("type", "0");
				vo.setString("describe", "信息浏览字段附加功能");
				ContentDAO dao=new ContentDAO(this.conn);
				dao.addValueObject(vo);
				
	        	/*strsql.delete(0,strsql.length());
		       	strsql.append("insert into  constant(constant,type,str_value,describe)values('INFO_PARAM','0','" + xmls.toString() + "','信息浏览字段附加功能')");
		       	
		       	pstmt.execute(strsql.toString());*/	
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
	/**
	 * 取得主集浏览信息指标列表
	 * @param nodename
	 * @return
	 */
	public String getInfo_paramNode(String nodename)
	{
	   String nodevalue="";
       try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild(nodename);
		    	if(childnode!=null)
		    	{
		    		nodevalue=childnode.getText();
		    		/**考虑有的指标可能未构库*/
		    		String[] strarr=StringUtils.split(nodevalue,",");
		    		StringBuffer buf=new StringBuffer();
		    		for(int i=0;i<strarr.length;i++)
		    		{
		    			String name=strarr[i];
		    			FieldItem item=DataDictionary.getFieldItem(name);
		    			if(item==null) {
                            continue;
                        }
		    			
		    			//zxj 需要进一步判断是否为人员主集子集指标 
		    			if (!item.getFieldsetid().startsWith("A")) {
                            continue;
                        }
		    			
		    			if("0".equalsIgnoreCase(item.getUseflag())) {
                            continue;
                        }
		    			
		    			buf.append(item.getItemid());
		    			buf.append(",");
		    		}
		    		if(buf.length()>0)
		    		{
		    			buf.setLength(buf.length()-1);
		    			nodevalue=","+buf.toString();
		    		}
		    		else {
                        nodevalue="";
                    }
		    	}		    	
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
       return nodevalue;
	}
	/**
	 * 保存子集分类的子集名称
	 * @param nodename a01,a04....or set_a
	 * @param tag rec属性name的值
	 * @param conn
	 */
	public String saveView_param(String nodename,String tag,Connection conn)
	{
       String errmes = "";
   	   try{
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        ContentDAO dao = new ContentDAO(conn);
	        if (option_vo!=null){
	        	if(option_vo.getString("str_value").toLowerCase()!=null 

	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	         
			        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());    //读入xml
			    	Element root = doc.getRootElement(); // 取得根节点
			    	Element childnode=root.getChild("view");
			    	if(childnode!=null)
			    	{
			    		Element viewchild = childnode.getChild(nodename.toLowerCase());
			    		if(viewchild!=null){
			    			List reclist = viewchild.getChildren();
			    			for(int i=0;i<reclist.size();i++){
			    				Element rectemp = (Element)reclist.get(i);
			    				if(rectemp.getAttributeValue("name").equalsIgnoreCase(tag)){
				    				errmes = "11";
									return errmes;
			    				}
			    			}
			    			Element rec = new Element("rec");
				    		rec.setAttribute("name",tag);
				    		viewchild.addContent(rec);
			    		}
			    		else{
			    			viewchild = new Element(nodename.toLowerCase());
			    			Element rec = new Element("rec");
				    		rec.setAttribute("name",tag);
				    		viewchild.addContent(rec);
				    		childnode.addContent(viewchild);
			    		}
			    	}
			    	else
			    	{
			    		childnode = new Element("view");
			    		Element viewchild = new Element(nodename.toLowerCase());
			    		Element rec = new Element("rec");
			    		rec.setAttribute("name",tag);
			    		viewchild.addContent(rec);
			    		childnode.addContent(viewchild);
			    		root.addContent(childnode);
			        }	
			    	XMLOutputter outputter = new XMLOutputter();
				    Format format=Format.getPrettyFormat();
				    format.setEncoding("UTF-8");
				    outputter.setFormat(format);
				    xmls.append(outputter.outputString(doc));
				    strsql.delete(0,strsql.length());
				    /*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
					strsql.append("update  constant set str_value=? where constant='INFO_PARAM'");
					ArrayList values  = new ArrayList();
					values.add(xmls.toString());
					dao.update(strsql.toString(),values);	
					errmes = "";
				}else
				{
					Element root = new Element("root");
		        	Document doc = new Document(root);
		        	Element child = new Element("view");
		        	Element viewchild = new Element(nodename.toLowerCase());
		        	Element rec = new Element("rec");
		        	//child.setText(fieldstr);
		        	rec.setAttribute("name",tag);
		        	viewchild.addContent(rec);
		        	child.addContent(viewchild);
		        	root.addContent(child);
		   	 	    XMLOutputter outputter = new XMLOutputter();
		        	Format format=Format.getPrettyFormat();
		        	format.setEncoding("UTF-8");
		        	outputter.setFormat(format);
		        	xmls.append(outputter.outputString(doc));
		        	strsql.delete(0,strsql.length());
		        	/*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
		        	strsql.append("update  constant set str_value=? where constant='INFO_PARAM'");
		        	ArrayList values  = new ArrayList();
					values.add(xmls.toString());
					dao.update(strsql.toString(),values);	
			       	errmes = "";
				}
		    }else{
		        Element root = new Element("root");
	        	Document doc = new Document(root);
	        	Element child = new Element("view");
	        	Element viewchild = new Element(nodename.toLowerCase());
	        	Element rec = new Element("rec");
	        	rec.setAttribute("name",tag);
	        	viewchild.addContent(rec);
	        	child.addContent(viewchild);
	        	root.addContent(child);
	   	 	    XMLOutputter outputter = new XMLOutputter();
	        	Format format=Format.getPrettyFormat();
	        	format.setEncoding("UTF-8");
	        	outputter.setFormat(format);
	        	xmls.append(outputter.outputString(doc));
	        	strsql.delete(0,strsql.length());
	        	/*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
		       	strsql.append("insert into  constant(constant,type,str_value,describe) values(?,?,?,?) ");
		       	ArrayList values  = new ArrayList();
		       	values.add("INFO_PARAM");
		       	values.add("0");
				values.add(xmls.toString());
				values.add("信息浏览字段附加功能");
				dao.insert(strsql.toString(),values);	
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
	 * @param nodename a01,a04....or set_a
	 * @param tag rec属性name的值
	 * @param fields 保存设置的数据值 [a01,a04...] or [a0101,a0107....]
	 * @param conn
	 */
	public String saveView_Value(String nodename,String tag,String[] fields,Connection conn)
	{
       String fieldstr="";
       String oldsubclass = "";
   	   try{
   		   ContentDAO dao = new ContentDAO(conn);
   		    for(int i=0;fields!=null && i<fields.length;i++)
   		    {
   		    	fieldstr+= fields[i]+",";
   		    }
   		    fieldstr = fieldstr.substring(0,fieldstr.length()-1);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");

	        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
	    	Element root = doc.getRootElement(); // 取得根节点
	    	Element childnode=root.getChild("view");
	    	Element viewchild = childnode.getChild(nodename.toLowerCase());
	    	List reclist = viewchild.getChildren("rec");
	    	if(reclist==null||reclist.size()<=0) {
                return oldsubclass;
            }
	    	for(int i=0;i<reclist.size();i++){
	    		Element rec = (Element)reclist.get(i);
	    		if(rec.getAttributeValue("name").equalsIgnoreCase(tag)){
	    			oldsubclass = rec.getText();
	    			rec.setText(fieldstr);
	    		}
	    	}
	    	XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    xmls.append(outputter.outputString(doc));
		    strsql.delete(0,strsql.length());
		    /*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
			strsql.append("update  constant set str_value=? where constant='INFO_PARAM'");
			ArrayList values  = new ArrayList();
			values.add(xmls.toString());
			dao.update(strsql.toString(),values);	
			
       }catch (Exception ee)
       {
         ee.printStackTrace();
         GeneralExceptionHandler.Handle(ee);
       }
       finally
       {
         
       }
       
       return oldsubclass;
      
	}
	/**
	 * 得到子集相关字段，以","号隔开
	 * @param tag 子集a01,a04.... 或 指标set_a
	 * @param recname rec的子集或指标 name
	 * @return
	 */
	public String getView_value(String tag,String recname){
		String value="";
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild("view");
		    	if(childnode!=null)
		    	{
		    		Element viewchild=childnode.getChild(tag.toLowerCase());
		    		if(viewchild!=null){
		    			List reclist = viewchild.getChildren();
		    			Element rec = null;
		    			for(int i=0;i<reclist.size();i++){
		    				rec = (Element)reclist.get(i);
		    				if(rec.getAttributeValue("name").equalsIgnoreCase(recname)) {
                                value = rec.getText();
                            }
		    			}
			    		/**考虑有的指标可能未构库*/
			    		/*String[] strarr=StringUtils.split(value,",");
			    		StringBuffer buf=new StringBuffer();
			    		for(int i=0;i<strarr.length;i++)
			    		{
			    			String name=strarr[i];
			    			FieldItem item=DataDictionary.getFieldItem(name.toUpperCase());
			    			if(item==null)
			    				continue;
			    			if(item.getUseflag().equalsIgnoreCase("0"))
			    				continue;
			    			buf.append(item.getItemid());
			    			buf.append(",");
			    		}
			    		if(buf.length()>0)
			    		{
			    			buf.setLength(buf.length()-1);
			    			value=","+buf.toString();
			    		}
			    		else
			    			value="";*/
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
	 * 得到子集所有指标分类的字段，以","号隔开
	 * @param tag 子集a01,a04....
	 * @return
	 */
	public String getAllSetfield(String tag){
		StringBuffer value= new StringBuffer();
		try{
			RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
			if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null
					&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
				Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
				Element root = doc.getRootElement(); // 取得根节点
				Element childnode=root.getChild("view");
				if(childnode!=null)
				{
					Element viewchild=childnode.getChild(tag.toLowerCase());
					if(viewchild!=null){
						List reclist = viewchild.getChildren();
						Element rec = null;
						for(int i=0;i<reclist.size();i++){
							rec = (Element)reclist.get(i);
							value.append(","+rec.getText());
						}
					}
				}
			}
		}catch (Exception ee)
		{
			ee.printStackTrace();
			GeneralExceptionHandler.Handle(ee);
		}
		return value.toString();
	}
	/**
	 * 返回子集分类的列
	 * @return
	 */
	public ArrayList getSet_A_Name$Text(){
		ArrayList list=new ArrayList();
		try{
			Element root = this.doc.getRootElement(); // 取得根节点
	    	Element childnode=root.getChild("view");
	    	if(childnode!=null)
	    	{
	    		Element viewchild=childnode.getChild("set_a");
	    		if(viewchild!=null){
	    			List reclist = viewchild.getChildren("rec");
	    			Element rec = null;
	    			for(int i=0;i<reclist.size();i++){
	    				rec = (Element)reclist.get(i);
	    				LazyDynaBean bean=new LazyDynaBean();
	    				bean.set("name", rec.getAttribute("name"));
	    				bean.set("text", rec.getText());
	    				list.add(bean);
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
	 * 得到所有子集分类的汉字描述
	 * @param setA_Name$Test_List
	 * @return
	 */
	public ArrayList getFenLeiDesc(ArrayList setA_Name$Test_List)
	{
		ArrayList list=new ArrayList();
		for(int i=0;i<setA_Name$Test_List.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)setA_Name$Test_List.get(i);
			String text=(String)bean.get("text");
			if(text!=null&&text.length()>0)
			{
				String[] array=text.split(",");
				if(array!=null&&array.length>0)
				{
					for(int r=0;r<array.length;r++)
					{
						FieldSet set=DataDictionary.getFieldSetVo(array[r].toUpperCase());
						if(set!=null) {
                            list.add(set.getCustomdesc());
                        }
					}
				}
			}
		}
		return list;
	}
	/**
	 * 得到所有rec的name属性名称
	 * @param tag 子集a01 或 指标set_a
	 * @return
	 */
	public ArrayList getView_tag(String tag){
		ArrayList list = new ArrayList();
		try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild("view");
		    	if(childnode!=null){
			    	Element viewchild=childnode.getChild(tag.toLowerCase());
		    		if(viewchild!=null){
		    			List reclist = viewchild.getChildren();
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
	 * 删除相关子集或指标
	 * @param tag  set_a或a01
	 * @param name rec子标签的name属性的值
	 */
	public void deleteTag(String tag,String name){
		RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
        try{
        	ContentDAO dao = new ContentDAO(this.conn);
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	StringBuffer xmls=new StringBuffer();	       
	 	     	StringBuffer strsql=new StringBuffer();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());     //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild("view");
		    	if(childnode!=null){
		    		Element viewchild = childnode.getChild(tag.toLowerCase());
		    		if(viewchild!=null){
		    			List reclist = viewchild.getChildren();
		    			for(int i=0;i<reclist.size();i++){
		    				Element rec = (Element)reclist.get(i);
		    				if(rec.getAttributeValue("name").equalsIgnoreCase(name)) {
                                viewchild.removeContent(rec);
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
			    /*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
				strsql.append("update  constant set str_value=? where constant='INFO_PARAM'");
				ArrayList values  = new ArrayList();
				values.add(xmls.toString());
				dao.update(strsql.toString(),values);	
	        }
        }catch (Exception ee)
 	   {
 	      ee.printStackTrace();
 	      GeneralExceptionHandler.Handle(ee);
 	   }     
	}
	/**
	 * 
	 * @param infoFieldViewList 
	 * @param infoFielditem  显示的项
	 * @param isCorrect  true:显示指定项，false显示未定义的
	 * @return
	 */
	public List getInfoSortFielditem(List infoFieldViewList,String infoFielditem,boolean isCorrect)
	{
		List infoFieldList=new ArrayList();   
		if(infoFielditem==null||infoFielditem.length()<=0) {
            return infoFieldList;
        }
		if(isCorrect)
		{
			for(int i=0;i<infoFieldViewList.size();i++)
			{
				FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
				if(infoFielditem.indexOf(fieldItemView.getItemid())==-1)
				{
					continue;
				}
				infoFieldList.add(fieldItemView.clone());
			}
		}else
		{
			for(int i=0;i<infoFieldViewList.size();i++)
			{
				FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
				if(infoFielditem.indexOf(fieldItemView.getItemid())!=-1)
				{
					continue;
				}
				infoFieldList.add(fieldItemView.clone());
			}
		}
		return infoFieldList;
	}
	/**
	 * 
	 * @param infoSetList
	 * @param setSub  显示的项
	 * @param isCorrect  true:显示指定项，false显示未定义的
	 * @return
	 */
	public List getInfoSortFieldSet(List infoSetList,String setSub,boolean isCorrect)
	{
		List infoFieldList=new ArrayList();   
		if(setSub==null||setSub.length()<=0) {
            return infoFieldList;
        }
		setSub=setSub.toUpperCase();
		if(isCorrect)
		{
			String[] setN=setSub.split(",");
			for(int r=0;r<setN.length;r++)
			{
			    String setname=setN[r];	
			    for(int i=0;i<infoSetList.size();i++)
				{
					FieldSet fieldset=(FieldSet)infoSetList.get(i);		
					
					if(setname.indexOf(fieldset.getFieldsetid().toUpperCase())!=-1)
					{
						infoFieldList.add(fieldset);
						break;
					}
				}
			}
			
		}else
		{
			for(int i=0;i<infoSetList.size();i++)
			{
				 FieldSet fieldset=(FieldSet)infoSetList.get(i);
				if(setSub.indexOf(fieldset.getFieldsetid().toUpperCase())==-1&&!"A01".equals(fieldset.getFieldsetid().toUpperCase()))
				{
					infoFieldList.add(fieldset);
				}
			}
		}
		return infoFieldList;
	}
	
	/**
	 * 修改指标名称
	 * @param tag	a01,a04....set_a
	 * @param name	old-name
	 * @param newname	update-name
	 */
	public String updateTag(String tag,String name,String newname){
		RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
        String errmes = "";
        try{
        	ContentDAO dao = new ContentDAO(this.conn);
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null 
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
	        	StringBuffer xmls=new StringBuffer();	       
	 	     	StringBuffer strsql=new StringBuffer();
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase());    //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild("view");
		    	if(childnode!=null){
		    		Element viewchild = childnode.getChild(tag.toLowerCase());
		    		if(viewchild!=null){
		    			List reclist = viewchild.getChildren();
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
                                rec.setAttribute("name",newname);
                            }
		    			}
		    		}
		    	}
		    	childnode=root.getChild("order");
		    	childnode.setText(childnode.getText().replace(name, newname));
		    	XMLOutputter outputter = new XMLOutputter();
			    Format format=Format.getPrettyFormat();
			    format.setEncoding("UTF-8");
			    outputter.setFormat(format);
			    xmls.append(outputter.outputString(doc));
			    strsql.delete(0,strsql.length());
			    /*oracle 更新时如果 直接拼接上的参数太长，会报错，传预处理方式更新 guodd 2019-06-04 */
				strsql.append("update  constant set str_value=? where constant='INFO_PARAM'");
				ArrayList values  = new ArrayList();
				values.add(xmls.toString());
				dao.update(strsql.toString(),values);	
	        }
        }catch (Exception ee)
 	   {
 	      ee.printStackTrace();
 	      GeneralExceptionHandler.Handle(ee);
 	   }
       return errmes;
	}
	/**
	 * 得到子标签内容
	 * @param nodename 标签名
	 * @return node.getText()
	 */
	public String getInfo_param(String nodename)
	{
	   String nodevalue="";
       try{
        	RecordVo option_vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
	        if (option_vo!=null && option_vo.getString("str_value").toLowerCase()!=null
	        		&& option_vo.getString("str_value").toLowerCase().trim().length()>0 && option_vo.getString("str_value").toLowerCase().indexOf("xml")!=-1) {
		        Document doc = PubFunc.generateDom(option_vo.getString("str_value"));     //读入xml
		    	Element root = doc.getRootElement(); // 取得根节点
		    	Element childnode=root.getChild(nodename);
		    	if(childnode!=null)
		    	{
		    		nodevalue=childnode.getText();
		    		/**考虑有的指标可能未构库*/
		    		String[] strarr=StringUtils.split(nodevalue,",");
		    		StringBuffer buf=new StringBuffer();
		    		for(int i=0;i<strarr.length;i++)
		    		{
		    			buf.append(strarr[i]);
		    			buf.append(",");
		    		}
		    		if(buf.length()>0)
		    		{
		    			buf.setLength(buf.length()-1);
		    			nodevalue=buf.toString();
		    		}
		    		else {
                        nodevalue="";
                    }
		    	}		    	
		    }
       }catch (Exception ee)
	   {
	      ee.printStackTrace();
	      GeneralExceptionHandler.Handle(ee);
	   }
       return nodevalue;
	}
	/**
	 * 保存后排序
	 * @return
	 */
	/*
	public boolean reOrederSet()
	{
		boolean isCorrect=false;
		ArrayList fielditemlist = new ArrayList();
		fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		ArrayList taglists = getView_tag("set_a");
		ArrayList holdtaglist = new ArrayList();
  	    for(int i=0;i<taglists.size();i++){
  	    	String tagname = (String)taglists.get(i);
  	    	String viewvalue = getView_value("set_a",tagname);
  	    	if(!viewvalue.equalsIgnoreCase("")){
  	    		String[] viewvalues = viewvalue.split(",");
  	    		for(int j=0;j<viewvalues.length;j++){
  	    			holdtaglist.add(viewvalues[j]);
  	    		}
  	    	}
  	    }
  	    ArrayList sparetaglist = new ArrayList();
		if(fielditemlist!=null){
	    	for(int j=0;j<fielditemlist.size();j++)
	    	{
	    		FieldSet fieldset=(FieldSet)fielditemlist.get(j);
		  	    //if(this.userView.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
		  	    	//continue;
		  	    if(fieldset.getFieldsetid().equals("B01")||fieldset.getFieldsetid().equals("B00"))
		  	    	continue;
		  	    String settag  = fieldset.getFieldsetid();
		  	    int x = 0;
		  	    for(int i=0;i<holdtaglist.size();i++){
		  	    	String oldtag = holdtaglist.get(i).toString();
		  	    	if(settag.equalsIgnoreCase(oldtag)){
		  	    		x =1;
		  	    	}
		  	    }
		  	    if(x==0)
	  	    		sparetaglist.add(fieldset.getCustomdesc());
	    	}
		}	
		for(int i=0;i<taglists.size();i++){
			sparetaglist.add(taglists.get(i));
		}
		if(sparetaglist!=null&&sparetaglist.size()>0)
		{
			String tagorder[]=new String[sparetaglist.size()];
			for(int j=0;j<sparetaglist.size();j++)
	    	{
				tagorder[j]=(String)sparetaglist.get(j);				
	    	}
			saveInfo_paramNode("order",tagorder,this.conn);
		}
		
		
		return isCorrect;
	}
	*/

	public boolean reOrederSet()//zgd 2014-6-18 排序问题。防止子集分类的顺序初始化。
	{
		boolean isCorrect=false;
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
		String oldtagorder =infoxml.getInfo_param("order");
		String[] oldtagorders = oldtagorder.split(",");
		ArrayList fielditemlist = new ArrayList();
		fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		ArrayList taglists = getView_tag("set_a");
		ArrayList holdtaglist = new ArrayList();
		ArrayList sparetaglist = new ArrayList();
		
		//过滤重复的 2015-06-02  guodd
		StringBuffer sb = new StringBuffer();
		LinkedHashMap map = new LinkedHashMap();
		for(int i=0;i<oldtagorders.length;i++)
		{	map.put(oldtagorders[i],null);
										
		}
		Iterator it = map.entrySet().iterator();
		sparetaglist.clear();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			sb.append(entry.getKey());
			sb.append(",");
		}
		oldtagorders = sb.toString().split(",");
		
		for(int i=0;i<oldtagorders.length;i++){
			for(int j=0;j<taglists.size();j++){
				if(oldtagorders[i].equals(taglists.get(j))){
					sparetaglist.add(oldtagorders[i]);
					String viewvalue = getView_value("set_a",oldtagorders[i]);
					if(!"".equalsIgnoreCase(viewvalue)){
						String[] viewvalues = viewvalue.split(",");
						for(int k=0;k<viewvalues.length;k++){
							holdtaglist.add(viewvalues[k]);
						}
					}
					continue;
				}
			}
			for(int j=0;j<fielditemlist.size();j++){
				FieldSet fs = (FieldSet)fielditemlist.get(j);
				if(oldtagorders[i].trim().equalsIgnoreCase(fs.getCustomdesc())){
					holdtaglist.add(fs.getFieldsetid());
					sparetaglist.add(oldtagorders[i]);
					continue;
				}
			}
		}
		if(fielditemlist!=null){
	    	for(int j=0;j<fielditemlist.size();j++)
	    	{
	    		FieldSet fieldset=(FieldSet)fielditemlist.get(j);
		  	    if("B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())) {
                    continue;
                }
		  	    String settag  = fieldset.getFieldsetid();
		  	    int x = 0;
		  	    for(int i=0;i<holdtaglist.size();i++){
		  	    	String oldtag = holdtaglist.get(i).toString();
		  	    	if(settag.equalsIgnoreCase(oldtag)){
		  	    		x =1;
		  	    	}
		  	    }
		  	    if(x==0) {
                    sparetaglist.add(fieldset.getCustomdesc());
                }
	    	}
		}
		if(taglists!=null){
			for(int i=0;i<taglists.size();i++){
				int m = 0;
				for(int j=0;j<sparetaglist.size();j++){
					if(taglists.get(i).equals(sparetaglist.get(j))){
						m=1;
						continue;
					}
				}
				if(m==0){
					sparetaglist.add(taglists.get(i));
				}
			}
		}
		if(sparetaglist!=null&&sparetaglist.size()>0)
		{
			
			// sparetaglist可能包含重名的名称  wangzj过滤 2015-05-27
			LinkedHashMap maps = new LinkedHashMap();
			for(int j=0;j<sparetaglist.size();j++)
	    	{	maps.put((String)sparetaglist.get(j), (String)sparetaglist.get(j));
								
	    	}
			
			Iterator its = maps.entrySet().iterator();
			sparetaglist.clear();
			while (its.hasNext()) {
				Map.Entry entrys = (Map.Entry)its.next();
				sparetaglist.add(entrys.getKey());
			}
			
			
			String tagorder[]=new String[sparetaglist.size()];
			for(int j=0;j<sparetaglist.size();j++)
	    	{
				tagorder[j]=(String)sparetaglist.get(j);				
	    	}
			saveInfo_paramNode("order",tagorder,this.conn);
		}
		
		
		return isCorrect;
	}
}


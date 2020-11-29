package com.hjsj.hrms.businessobject.hire.zp_options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**2008-11-26 修改，增加定义复杂模板功能*/
public class ZpCondTemplateXMLBo {
	private Connection conn;
	private String xml="";
	private Document doc;
	/**
	 * 构造函数
	 * @param conn
	 */
	
	public ZpCondTemplateXMLBo(Connection conn){
		this.conn=conn;
		this.initXML();
		
	}
	/**
	 * 得到数据库中的xml串
	 *
	 */
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找ZP_COND_TEMPLATE常量
			rs=dao.search("select STR_VALUE  from CONSTANT where UPPER(CONSTANT)='ZP_COND_TEMPLATE'");
			if(rs.next()){
				//获取XML文件
				xml = Sql_switcher.readMemo(rs,"STR_VALUE");
			}
			else
			{
				 RecordVo vo=new RecordVo("constant");
				 vo.setString("constant","ZP_COND_TEMPLATE");
				 vo.setString("type","");
				 vo.setString("describe",ResourceFactory.getProperty("label.zp_options.cond"));
				 vo.setString("str_value","");
				 dao.addValueObject(vo);
			}
			StringBuffer strxml=new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<content>");
			strxml.append("</content>");	
			if(xml==null|| "".equals(xml))
			{
				xml=strxml.toString();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void init() throws GeneralException{
		
		try {
			if(xml!=null&&xml.length()>0)
			{
				doc = PubFunc.generateDom(xml);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (IOException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
	 * 将新值插入数据库
	 * @param list
	 */
	public void insertParam(ArrayList list,String type)
	{
		if("0".equals(type)&& xml!=null&&xml.trim().length()>0){//简单类型且原先已存在
			updateS_G(list,"simple");
	    }else if("0".equals(type)&& (xml==null||xml.trim().length()==0)){//简单类型原先不存在
	    	insert(createParamXML(list,"simple"));
	    }else if("1".equals(type)&& xml != null&&xml.trim().length()>0){//复杂类型原先存在
	    	updateS_G(list,"general");
	    }else if("1".equals(type) && (xml==null||xml.trim().length()==0)){//复杂类型原先不存在
	    	insert(createParamXML(list,"general"));
	    }	
	}
	/**
	 * 删除复杂模板
	 * @param templateid
	 */
	public void deleteComplexTemplate(String templateid)
	{
		try
		{
			init();
			String temp="";
			HashMap map = new HashMap();
			if(templateid==null|| "".equals(templateid)) {
                return;
            }
			String[] arr=templateid.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				map.put(arr[i],"1");
			}
			String path="/content/general/template";
			XPath xpath=XPath.newInstance(path);
			List childlist=xpath.selectNodes(doc);
			ArrayList newList= new ArrayList();
			if(childlist.size()!=0)
			{	
				for(int i=0;i<childlist.size();i++)
				{
					Element element=(Element)childlist.get(i);
					if(map.containsKey((element.getAttributeValue("ID"))))
					{
					}
					else
					{
						newList.add(element);
					}
					
				}
				((Element)XPath.newInstance("/content/general").selectSingleNode(doc)).removeChildren("template");	
			}
			if(newList.size()!=0)
			{
				path="/content/general";
				xpath=XPath.newInstance(path);
				Element element=(Element)xpath.selectSingleNode(doc);
				for(int i=0;i<newList.size();i++)
				{
					Element node=(Element)newList.get(i);
					/**重新设置id值，，因为有可能出现id相同的情况*/
					node.removeAttribute("ID");
					node.setAttribute("ID", String.valueOf(i+1));
					element.addContent(node);
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp= outputter.outputString(doc);
			this.insert(temp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得复杂模板的列表
	 * @return
	 */
	public ArrayList getComplexTemplateList()
	{
		ArrayList list=new ArrayList();
		try
		{
			init();
			String str_path="/content/general/template";	
			XPath xpath=XPath.newInstance(str_path);
			if(xpath!=null)
			{
	    		List childlist=xpath.selectNodes(doc);
	    		Element element=null;

	    		if(childlist.size()!=0)
	    		{
		    		LazyDynaBean bean = null;
		    		for(int i=0;i<childlist.size();i++)
		    		{
		    			element=(Element)childlist.get(i);
		    			bean = new LazyDynaBean();
		    			bean.set("seq", String.valueOf(i+1));
		    			bean.set("id",element.getAttributeValue("ID"));
			    		bean.set("name",element.getAttributeValue("Name"));
			    		list.add(bean);
		    		}//for end.
	    		}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 模板指标原先的值
	 * @param str2
	 * @return
	 */
	public HashMap getFieldValue(String str2)
	{
		HashMap map = new HashMap();
		try
		{
			if(str2==null|| "".equals(str2)) {
                return map;
            }
			String[] bds=str2.split("`");
			for(int i=0;i<bds.length;i++)
			{
			    LazyDynaBean bean=new LazyDynaBean();
				String str=bds[i];//Z0106=1
				String fieldid=str.substring(0,5);//指标
				String fieldvalue="";//值
				String oper="";//关系符号
				
				if(str.length()<=5)
				{
					oper=str.substring(5);
				}
				else if(5<str.length()&&str.length()<=6)
				{
					oper=str.substring(5);
				}
				else
				{
			    	String oper_temp=str.substring(6,7);
		    		int flag=0;
			    	if("=".equals(oper_temp)|| "<".equals(oper_temp)|| ">".equals(oper_temp))
		    		{
		    			oper=str.substring(5,7);
		    			flag=1;
		    		}
		    		else
		    		{
		    			oper=str.substring(5,6);
		    			flag=2;
		     		}
				
		    		if(flag==1) {
                        fieldvalue=str.substring(7);
                    } else {
                        fieldvalue=str.substring(6);
                    }
				}
				bean.set("oper",oper);
				bean.set("value",fieldvalue);
				map.put(fieldid.toUpperCase()+i,bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取某模板的信息
	 * @param templateid
	 * @return
	 */
	public HashMap getFactorExpr(String templateid)
	{
		HashMap map = new HashMap();
		try
		{
			init();
			String str_path="/content/general/template[@ID="+templateid+"]";
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null)
			{
				map.put("expr",element.getAttributeValue("Expr"));
				map.put("factor", element.getAttributeValue("Factor"));
				map.put("name",element.getAttributeValue("Name"));
				StringBuffer buf = new StringBuffer();
				String[] arr=element.getAttributeValue("Factor").split("`");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					if(arr[i].indexOf("createtime")!=-1)
					{
						buf.append(",'createtime'");
						continue;		
					}
					buf.append(",'");
					buf.append(arr[i].substring(0,5));
				}
				map.put("str",buf.toString());
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 修改复杂模板
	 */
	public void updateComplexParamXML(String templateid,HashMap propertyMap,String expr)
	{
		try
		{
			String temp="";
			init();
			String str_path="/content/general/template[@ID="+templateid+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0) {
                element=(Element)childlist.get(0);
            }
			if(element!=null)
			{
				/**将原先的值删除*/
				element.removeAttribute("Name");
				element.removeAttribute("Expr");
				element.removeAttribute("Factor");
				/**更新新值*/
				element.setAttribute("Name",(String)propertyMap.get("Name"));
				element.setAttribute("Expr",expr);
				element.setAttribute("Factor",(String)propertyMap.get("Factor"));
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp= outputter.outputString(doc);
			this.insert(temp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 创建复杂模板的xml串
	 * @param propertymp
	 * @param expr
	 * @return
	 */
	public String createComplexParamXML(HashMap propertymp,String expr)
	{
		int id=1;
		String temp="";
		
		try
		{
			init();
			String str_path="/content/general/template";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			ArrayList list=new ArrayList();
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					list.add(element.getAttributeValue("ID"));
				}//for end.
				Arrays.sort(list.toArray());
				id=Integer.parseInt((String)list.get(childlist.size()-1))+1;
			}
			else
			{
				xpath=XPath.newInstance("/content/general");
				element=(Element)xpath.selectSingleNode(doc);
				if(element==null)
				{
					xpath=XPath.newInstance("/content");
					Element pElement=(Element)xpath.selectSingleNode(doc);
					element= new Element("general");
					pElement.addContent(element);
				}
				else
				{
					if(element.getChild("fielditem")!=null) {
                        element.removeChild("fielditem");
                    }
				}
			}
			Element childElement=new Element("template");
			childElement.setAttribute("ID", String.valueOf(id));
			childElement.setAttribute("Name",(String)propertymp.get("Name") );
			childElement.setAttribute("Expr", expr );
			childElement.setAttribute("Factor", (String)propertymp.get("Factor") );
			if(childlist.size()==0)
			{
				element.addContent(childElement);
			}
			else
			{
			    element=(Element)element.getParent();
			    element.addContent(childElement);
			}
			//Document myDocument = new Document(content);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp= outputter.outputString(doc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
    /**
     * 创建xml
     * @param list
     * @return
     */
	private String createParamXML(ArrayList list,String node){
		String temp = null;
			Element content= new Element("content");
			Element simple= new Element("simple");
			Element general = new Element("general");
		
		for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			Element fielditem=new Element("fielditem");
			
			fielditem.setAttribute("name",(String)bean.get("itemid"));
	    	if(bean.get("s_value")==null|| "".equals((String)bean.get("s_value")))
	    	{
	    		fielditem.setAttribute("s_value","");
				
	    	}
	    	else
	    	{
	    		fielditem.setAttribute("s_value",(String)bean.get("s_value"));
	    	}
			
			if("false".equals((String)bean.get("flag"))){
				if(bean.get("e_value")==null|| "".equals((String)bean.get("e_value")))
				{
					fielditem.setAttribute("e_value","");
				}
				else
				{
	    			fielditem.setAttribute("e_value",(String)bean.get("e_value"));
				}
				fielditem.setAttribute("flag","false");
			}
			else{
				fielditem.setAttribute("e_value","");
				fielditem.setAttribute("flag","true");
				
			}
			if("simple".equals(node)) {
                simple.addContent(fielditem);
            }
			if("general".equals(node)) {
                general.addContent(fielditem);
            }
		}
		
		content.addContent(simple);
		content.addContent(general);
		
		Document myDocument = new Document(content);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);
		return temp;
	}
	/**
	 * 得到数据库中的xml元素的属性值,放到bean中
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAttributeValues(String type) throws GeneralException {
		
		ArrayList list = new ArrayList();
		if(xml == null || "".equals(xml.trim())){
			return list;
		}else{
			/**简单模板*/
			if("0".equals(type)){
				list = getS_G("simple");
			}
			/**复杂模板*/
			if("1".equals(type)){
				list = getS_G("general");
			}
		}
		return list;
			
		
	}
	/**
	 * 更新xml
	 * @param list
	 * @param node
	 */
	public void updateS_G(ArrayList list,String node){
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			init();
		    XPath xPath= XPath.newInstance("/content");
		    Element element =(Element)xPath.selectSingleNode(this.doc);
		    element.removeChild(node);
		    Element nodeElement=new Element(node);
		    for(Iterator t=list.iterator();t.hasNext();){
		    	LazyDynaBean bean = (LazyDynaBean)t.next();
		    	if(bean != null){
		    		Element fielditem= new Element("fielditem");
		    		fielditem.setAttribute("name",(String)bean.get("itemid"));
		    		if(bean.get("s_value")==null|| "".equals((String)bean.get("s_value")))
		    		{
		    			fielditem.setAttribute("s_value","");
		    		}
		    		else
		    		{
		        		fielditem.setAttribute("s_value",(String)bean.get("s_value"));
		    		}
		    		if("false".equals((String)bean.get("flag"))){
		    			if(bean.get("e_value")==null|| "".equals((String)bean.get("e_value")))
		    			{
		    				fielditem.setAttribute("e_value","");
		    			}
		    			else
		    			{
				    		fielditem.setAttribute("e_value",(String)bean.get("e_value"));
		    			}
						fielditem.setAttribute("flag","false");
					}
					else{
						fielditem.setAttribute("e_value","");
						fielditem.setAttribute("flag","true");
					}
		    		nodeElement.addContent(fielditem);
		    	}
		    	
		    }
		    element.addContent(nodeElement);
		    XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			//System.out.println(outputter.outputString(this.doc));
			dao.update("update constant set str_value ='"+outputter.outputString(this.doc)+"' where constant ='ZP_COND_TEMPLATE'");
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	/**
	 * 刚开始插入
	 * @param str_value
	 */
	public void insert(String str_value){
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			 dao.delete("delete from constant where constant='ZP_COND_TEMPLATE'",new ArrayList());
			 RecordVo vo=new RecordVo("constant");
			 vo.setString("constant","ZP_COND_TEMPLATE");
			 vo.setString("type","");
			 vo.setString("describe",ResourceFactory.getProperty("label.zp_options.cond"));
			 vo.setString("str_value",str_value);
			 dao.addValueObject(vo);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public HashMap getItemMap(List list)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			if(list!=null&&list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
                    Element tem=(Element)list.get(i);
					DynaBean bean = new LazyDynaBean();
					buf.append(",'"+tem.getAttributeValue("name").toUpperCase()+"'");
				}
				String sql = " select itemid from fielditem where UPPER(itemid) in ("+buf.toString().substring(1)+") and useflag='1'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					map.put(rs.getString("itemid").toUpperCase(), rs.getString("itemid"));
				}
				map.put("createtime".toUpperCase(), "createtime");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 得到xml元素属性的值的集合
	 * @param node
	 * @return
	 */
	public ArrayList getS_G(String node){
		ArrayList list = new ArrayList();
		try {
			init();
			XPath xPath = XPath.newInstance("/content/"+node+"/fielditem");
			List fielditems =  xPath.selectNodes(this.doc);
			
			if (fielditems != null) {
				HashMap map = this.getItemMap(fielditems);
				for(int i=0;i<fielditems.size();i++){
					Element tem=(Element)fielditems.get(i);
					if(map.get(tem.getAttributeValue("name").toUpperCase())!=null)
					{
			    		DynaBean bean = new LazyDynaBean();
			    		bean.set("name",tem.getAttributeValue("name"));
			    		bean.set("s_value",tem.getAttributeValue("s_value"));
			    		if("false".equals(tem.getAttributeValue("flag"))){
			    			bean.set("e_value",tem.getAttributeValue("e_value"));
			    			bean.set("flag",tem.getAttributeValue("flag"));
			    		}else{
			    			bean.set("e_value","");
			    			bean.set("flag","true");
		    			}	
		    			list.add(bean);
					}
					
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
		
	
	/**
	 * 得到xml元素属性的值的集合
	 * @param node
	 * @return
	 */
	public HashMap getS_GMap(String node){
		HashMap map=new HashMap();
		try {
			init();
			
			if(this.doc!=null)
			{
				XPath xPath = XPath.newInstance("/content/"+node+"/fielditem");
				List fielditems =  xPath.selectNodes(this.doc);
				
				if (fielditems != null) {
					for(int i=0;i<fielditems.size();i++){
						Element tem=(Element)fielditems.get(i);
						DynaBean bean = new LazyDynaBean();
						bean.set("name",tem.getAttributeValue("name"));
						bean.set("s_value",tem.getAttributeValue("s_value"));
						if("false".equals(tem.getAttributeValue("flag"))){
							bean.set("e_value",tem.getAttributeValue("e_value"));
							bean.set("flag",tem.getAttributeValue("flag"));
						}else{
							bean.set("e_value","");
							bean.set("flag","true");
						}
						
						map.put((tem.getAttributeValue("name")).toLowerCase(), bean);
						
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	public String getSelectedFieldsIds(String node){
		StringBuffer str= new StringBuffer();
		if(xml == null || xml.trim().length()<=0) {
            return "";
        } else{
			try{
				init();
				XPath yPath = XPath.newInstance("/content/"+node);
				if(yPath==null) {
                    return "";
                }
			    Element elementY = (Element)yPath.selectSingleNode(this.doc);
			    if(elementY == null) {
                    return "";
                }
				XPath xPath = XPath.newInstance("/content/"+node+"/fielditem");
				List fieldlist = xPath.selectNodes(this.doc);
				if(fieldlist != null){
				     for(int i=0;i<fieldlist.size();i++){
					     Element element = (Element)fieldlist.get(i);
					     str.append(",");
					     str.append("'");
					     str.append(element.getAttributeValue("name"));
					     str.append("'");
				     }
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		if(str.toString().trim().length()<=0 || str == null) {
            return "";
        } else {
            return str.toString().substring(1);
        }
		
	}
	

}

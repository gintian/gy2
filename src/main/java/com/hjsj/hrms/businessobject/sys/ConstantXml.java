package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
/**
 * <p>ConstantXml.java</p>
 * <p>Description:常量表xml参数解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class ConstantXml {
	private Connection conn;
	private Document doc;
	private String xml;
	private String param;
	
	/**
	 * 初始化不同的根接点为固定param的xml内容
	 * @param conn
	 * @param constant //constant字段内容
	 */
	public ConstantXml(Connection conn,String constant){
		this.conn=conn;
		this.param=constant;
		init(constant);
		try{
			doc = PubFunc.generateDom(xml.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 初始化不同的根接点xml的内容
	 * @param conn
	 * @param constant //constant字段内容
	 * @param param //接点路径  例如：Params
	 */
	public ConstantXml(Connection conn,String constant,String param){
		this.conn=conn;
		this.param=constant;
		initXML(constant,param);
		try{
			doc = PubFunc.generateDom(xml.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @param conn 
	 */
	public ConstantXml(Connection conn){
		this.conn=conn;
	}
	/**
	 * 取字段str_value的内容
	 * @param constant //constant字段内容
	 */
	private void init(String constant){
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<param>");
		temp_xml.append("</param>");		
		try{
			//RecordVo vo=ConstantParamter.getRealConstantVo(constant,this.conn);
			/**
			 * xuj update 2013-6-3
			 * 统一采用从静态类缓存中取，趁刚刚分完版为彻底优化以前调用此业务类同时存在每次从数据库取数据问题，此处前期未这样这样优化怕会带来一些风险，但利大于弊，如掉用此业务类更新数据时需调用saveStrValue方法，以便更新ConstantParamter缓存数据
			 * 
			 */
			RecordVo vo=ConstantParamter.getConstantVo(constant,this.conn);
			if(vo!=null) {
                xml=vo.getString("str_value");
            }
			if(xml==null|| "".equals(xml)){
				xml=temp_xml.toString();
			}
			doc=PubFunc.generateDom(xml.toString());
		}catch(Exception ex){
			xml=temp_xml.toString();
		}
	}
	/**
	 * 取字段str_value的内容
	 * @param constant //constant字段内容
	 * @param param //接点路径  例如：Params
	 */
	private void initXML(String constant,String param){
		StringBuffer temp_xml = new StringBuffer();
		param = param != null && param.trim().length() > 0 ? param : "param";
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<" + param + ">");
		temp_xml.append("</" + param + ">");
		try {
			/**
			 * xuj update 2013-6-3
			 * 统一采用从静态类缓存中取，趁刚刚分完版为彻底优化以前调用此业务类同时存在每次从数据库取数据，此处前期未这样这样优化怕会带来一些风险，但利大于弊，如掉用此业务类更新数据时需调用saveStrValue方法，以便更新ConstantParamter缓存数据
			 * 
			 * zxj update 2015-7-2
			 * 某些参数可能由于C+B或集群状况下，需要从数据库读取，如历史时点Emp_HisPoint等，
			 * 以后发现有其它类似参数，可在paramsFromDB中进行补充
			 */
			String paramsFromDB = ",Emp_HisPoint,";
		    RecordVo vo = null;
		    if (paramsFromDB.toUpperCase().contains(("," + param.toUpperCase() + ","))) {
                vo = ConstantParamter.getRealConstantVo(constant, this.conn);
            } else {
                vo = ConstantParamter.getConstantVo(constant, this.conn);
            }
		    
			if (vo != null) {
                xml = vo.getString("str_value");
            }
			
			if (xml == null || xml.length() == 0) {
				xml = temp_xml.toString();
			}
			// xml转Document
			doc = PubFunc.generateDom(xml.toString());
			// yangj update 2014-12-19 当根节点不同时，做覆盖操作
			XPath xpath = XPath.newInstance("/" + param);
			List childlist = xpath.selectNodes(this.doc);
			if (childlist == null || childlist.size() == 0) {
				this.xml = temp_xml.toString();
			}
		} catch (Exception ex) {
			xml = temp_xml.toString();
		}
	}
	/**
	 * 如果数据库中没有这个名称记录则插入
	 * @param param_name  
	 */
	public void ifNoParameterInsert(String param_name){
		  String sql="select * from constant where UPPER(Constant)='"+param_name.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try{
			rs=dao.search(sql);		  
			  if(!rs.next()){
				  insertNewParameter(param_name);
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }		 
	}
	
	public Element getRootNode() {
		return this.doc.getRootElement();
	}
	
	public Element getElement(String path) {
		Element el = null;
		try {
			XPath xpath=XPath.newInstance(path);
			el = (Element) xpath.selectSingleNode(this.doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return el;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public List getElementList(String path) {
		List list = new ArrayList();
		try {
			XPath xpath=XPath.newInstance(path);
			list = xpath.selectNodes(this.doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 删除节点
	 * @param path
	 */
	public void removeNodes(String path) {
		try {
			XPath xpath=XPath.newInstance(path);
			List list= xpath.selectNodes(this.doc);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Element el = (Element) list.get(i);
					getRootNode().removeContent(el);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 插入
	 * @param param_name
	 */
	public void insertNewParameter(String param_name){
		String insert="insert into constant(Constant) values (?)";
		ArrayList list=new ArrayList();
		list.add(param_name.toUpperCase());			
		ContentDAO dao = new ContentDAO(conn);
		try{
			dao.insert(insert,list);		    
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	/**
	 * 插入常量表记录
	 * @param param_name 常量名
	 * @param param_type 类型: A/N/D/B...
	 * @param param_value 值
	 * @param describe 描述
	 */
    public void insertNewParameter(String param_name, String param_type, String param_value, String describe){
        String insert="insert into constant(Constant,type,str_value,describe) values (?,?,?,?)";
        ArrayList list=new ArrayList();
        list.add(param_name.toUpperCase());
        list.add(param_type);
        list.add(param_value);
        list.add(describe);
        ContentDAO dao = new ContentDAO(conn);
        try{
            dao.insert(insert,list);            
        }catch(Exception e){
            e.printStackTrace();
        }       
    }
    
	/**
	 * 取得对应节点参数的值
	 * @param param_name
	 * @return
	 */
	public void setValue(String param_name,String value){
		try{
			String str_path="/Param/"+param_name.toLowerCase();
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement==null){
				str_path="/param/"+param_name.toLowerCase();
				xpath=XPath.newInstance(str_path);
				spElement=(Element)xpath.selectSingleNode(doc);
			}
			if(spElement!=null){
				spElement.setText(value);
			}else{
				str_path="/Param";
				xpath=XPath.newInstance(str_path);
				spElement=(Element)xpath.selectSingleNode(doc);
				if(spElement!=null){
					Element element=new Element(param_name);
					element.setText(value);
					spElement.addContent(element);
				}else{
					str_path="/param";
					xpath=XPath.newInstance(str_path);
					spElement=(Element)xpath.selectSingleNode(doc); 
					if(spElement!=null){
						Element element=new Element(param_name);
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
	 * 
	 * @Title: getAllChildren   
	 * @Description: 获得path路径下的所有子节点   
	 * @param path
	 * @return List    
	 * @throws
	 */
	public List getAllChildren(String path){
		List list = new ArrayList();
		try {
			XPath xpath = XPath.newInstance(path);
			Element root = (Element)xpath.selectSingleNode(doc);
			if(root != null) {
                list = root.getChildren();
            }
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return list;
	}
	 /**
	 * 取得对应节点参数的值
	 * @param param_name
	 * @return
	 */
	public String getValue(String param_name){
		String value="";
		if(doc==null) {
            return value;
        }
		if(!"".equals(param_name)){
			try{
				String str_path="/Param/"+param_name.toLowerCase();
				XPath xpath=XPath.newInstance(str_path);
				Element spElement=(Element)xpath.selectSingleNode(doc);
				if(spElement==null){
					str_path="/param/"+param_name.toLowerCase();
					xpath=XPath.newInstance(str_path);
				}
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0){
					element=(Element)childlist.get(0);
					value=element.getText();
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	/**
	 * 设置对应节点参数的值
	 * @param str_path 保存路径 例如：/Params/nbase
	 * @param value //值
	 * @return
	 */
	public void setTextValue(String str_path,String value){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement!=null){
				spElement.setText(value);
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
							int index = arr[i].indexOf("[");
							Element element= null;
							if (index != -1) {
								element = new Element(arr[i].substring(0, index));
							} else {
								element = new Element(arr[i]);
							}
							
							if(i==arr.length-1) {
                                element.setText(value);
                            }
							spElement.addContent(element);
							spElement = element;
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
							int index = arr[i].indexOf("[");
							Element element= null;
							if (index != -1) {
								element = new Element(arr[i].substring(0, index));
							} else {
								element = new Element(arr[i]);
							}
							if(i==arr.length-1) {
                                element.setAttribute(attributeName, attributeValue);
                            }
							spElement.addContent(element);
							spElement = element;
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
	 * 新增结点
	 * @param str_path 保存路径 例如：/Params/nbase
	 * @param nodeList {attributes={code=01, signtable=1, jobtable=1}, content=, name=item}
	 * @return
	 */
	public void addElement(String str_path,ArrayList nodeList){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			Element element_child= null;
			if(element!=null){
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(0);
				String name1=(String)nodeBean.get("name");
				element.removeChildren(name1);

			} 
			else
			{
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
							Element element1= null;
							element1 = new Element(arr[i]);
							element.addContent(element1);
							element=element1;
						}else{
							element = bbElement;
						}
					}
				}
			}
			for(int i=0;i<nodeList.size();i++)
			{
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(i);
				String name=(String)nodeBean.get("name");
				String content=(String)nodeBean.get("content"); 
				HashMap node_map=(HashMap)nodeBean.get("attributes");
				 
				element_child = new Element(name); 
				Set keySet=node_map.keySet();
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String key=(String)t.next(); 
					String value=(String)node_map.get(key);
					element_child.setAttribute(key, value);
				}
				if(content!=null&&content.length()>0) {
                    element_child.addContent(content);
                }
				element.addContent(element_child);
				 
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	//当constant表中str_value为空时，addElement方法会报错，修改后添加addElement2方法  jingq add 2015.07.09
	public void addElement2(String str_path,ArrayList nodeList){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			Element element_child= null;
			if(element!=null){
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(0);
				String name1=(String)nodeBean.get("name");
				element.removeChildren(name1);
			} 
			else
			{
				String[] arr = str_path.split("/");
				for (int i = 0; i < arr.length; i++) {
					if(arr[i].length()<=0) {
                        continue;
                    }
					if(element==null){
						element = new Element(arr[i]);
					} else {
						Element ele = new Element(arr[arr.length-1]);
						for (int j = arr.length-2; j >= i ; j--) {
							Element e = new Element(arr[j]);
							e.addContent(ele);
							ele = e;
						}
						element.addContent(ele);
						break;
					}
				}
				doc.setContent(element);
				element = (Element)xpath.selectSingleNode(doc);
			}
			for(int i=0;i<nodeList.size();i++)
			{
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(i);
				String name=(String)nodeBean.get("name");
				String content=(String)nodeBean.get("content"); 
				HashMap node_map=(HashMap)nodeBean.get("attributes");
				 
				element_child = new Element(name); 
				Set keySet = null;
				if(node_map!=null){
					keySet = node_map.keySet();
					for(Iterator t=keySet.iterator();t.hasNext();)
					{
						String key=(String)t.next(); 
						String value=(String)node_map.get(key);
						element_child.setAttribute(key, value);
					}
				}
				if(content!=null&&content.length()>0) {
                    element_child.addContent(content);
                }
				element.setContent(element_child);
				 
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	
	//循环加入节点时，没有覆盖原始记录而是顺序添加，添加了remove操作
	public void addElement3(String str_path,ArrayList nodeList){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			Element element_child= null;
			if(element!=null){
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(0);
				String name1=(String)nodeBean.get("name");
				element.removeChildren(name1);

			} 
			else
			{
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
							Element element1= null;
							element1 = new Element(arr[i]);
							element.addContent(element1);
							element=element1;
						}else{
							element = bbElement;
						}
					}
				}
			}
			for(int i=0;i<nodeList.size();i++)
			{
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(i);
				String name=(String)nodeBean.get("name");
				String content=(String)nodeBean.get("content"); 
				HashMap node_map=(HashMap)nodeBean.get("attributes");
				 
				element_child = new Element(name); 
				Set keySet=null;
				if(node_map!=null){
					keySet = node_map.keySet();
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String key=(String)t.next(); 
					String value=(String)node_map.get(key);
					element_child.setAttribute(key, value);
				}
				}
				if(content!=null&&content.length()>0) {
                    element_child.addContent(content);
                }
				element.removeChildren(name);
				element.addContent(element_child);				 
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	 /**
	 * 取得对应节点参数的值
	 * @param str_path 例如：/Params/nbase
	 * @return
	 */
	public String getTextValue(String str_path){
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
					value=element.getText();
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	/**
	 * 保存xml格式的内容
	 *
	 */
	public void saveStrValue(){
		PreparedStatement pstmt = null;		
		//StringBuffer strsql=new StringBuffer();
		try{
			ifNoParameterInsert(param);
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			//将GBK修改为UTF-8
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			/*strsql.append("update constant set str_value=? where UPPER(constant)='"+param+"'");
			pstmt = 	
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
					  pstmt.setString(1, buf.toString());
					  break;
				 case Constant.ORACEL:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
				  case Constant.DB2:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
			}
			pstmt.executeUpdate();*/	
			RecordVo vo=new RecordVo("constant");
    		vo.setString("constant",this.param);
    	    vo.setString("str_value",buf.toString());
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    dao.updateValueObject(vo);
			ConstantParamter.putConstantVo(vo,this.param);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 取不是xml格式内容的值，如掉用此业务类更新数据时需调用saveValue方法，以便更新ConstantParamter缓存数据
	 * @param constant
	 * @return str_value
	 */
	public String getConstantValue(String constant){
		String str_value="";
		//RecordVo vo=ConstantParamter.getRealConstantVo(constant,this.conn);
		/**
		 * xuj update 2013-6-3
		 * 统一采用从静态类缓存中取，趁刚刚分完版为彻底优化以前调用此业务类同时存在每次从数据库取数据，此处前期未这样这样优化怕会带来一些风险，但利大于弊，如掉用此业务类更新数据时需调用saveValue方法，以便更新ConstantParamter缓存数据
		 * 
		 */
		RecordVo vo=ConstantParamter.getConstantVo(constant,this.conn);
		if(vo!=null) {
            str_value=vo.getString("str_value");
        }
		return str_value;
	}
	/**
	 * 保存不是xml格式的内容
	 *
	 */
	public void saveValue(String constant,String str_value){
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		try{
			ifNoParameterInsert(constant);
			/*strsql.append("update constant set str_value=? where UPPER(Constant)='"+constant+"'");
			pstmt = 	
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
					  pstmt.setString(1, str_value);
					  break;
				 case Constant.ORACEL:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  case Constant.DB2:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
			}
			pstmt.executeUpdate();*/	
			RecordVo vo=new RecordVo("constant");
    		vo.setString("constant",constant);
    	    vo.setString("str_value",str_value);
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    dao.updateValueObject(vo);
			ConstantParamter.putConstantVo(vo,constant);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
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
	 * 取得操作用户对应节点属性的值  changxy
	 * @param str_path 例如：/Params/nbase  /param/user/database
	 * parentnode  选择子节点的父节点  /param/user（id）/database
	 * @return
	 */
	public String getNodeByUserAttributeValue(String str_path,String attributeName,String parentnode,UserView userview){
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
					for (int i = 0; i < childlist.size(); i++) {
						element=(Element)childlist.get(i);
						if(element.getParentElement().getAttributeValue("id").equalsIgnoreCase(userview.getUserName())){
							value=element.getAttributeValue(attributeName);
							break;
						}
					}
					/*element=(Element)childlist.get(0);
					value=element.getAttributeValue(attributeName);*/
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	
}

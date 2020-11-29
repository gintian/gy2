package com.hjsj.hrms.businessobject.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.XmlResourceUtil;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncMainBo  {
	private Connection conn=null;
	private UserView userView=null;
	private HttpServletRequest request;

	private String entryName="com/hjsj/hrms/constant/function.xml";

	String lockVersion = null;
	String ctrl_ver = null;
	
	public void setLockVersion(String lockVersion) {
		this.lockVersion = lockVersion;
	}
	
	public void setCtrl_ver(String ctrl_ver) {
		this.ctrl_ver = ctrl_ver;
	}
	
	public FuncMainBo() {
		
	}
	public FuncMainBo(Connection conn) {
		this.conn=conn;
	}
	public FuncMainBo(Connection conn,UserView userView) {
		this.conn=conn;
		this.userView = userView;
	}
	/**
	 * 获得绝对路径
	 */
	public String getPath(){
		return XmlResourceUtil.getResourcePath("/constant/function.xml");
	}
	public void writeFile(Document doc){
       	  //文件处理
		//path为hrpweb3.jar所在的路径
		String path = this.getPath();
		try {
			XmlResourceUtil.writeXmlDocument(path,entryName,doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public InputStream getInputStreamFromjar(){
		String file = this.getPath();
		return XmlResourceUtil.getInputStreamFromjar(file,entryName);
	}
	public Document getDocument()throws GeneralException{
		String file = this.getPath();
		Document doc = XmlResourceUtil.readXmlDocument(file,entryName);
		return doc;
	}
	
/**
 * 通过父接点找子接点
 * @param parentid
 * @return
 */
	public ArrayList getFunctionContent(String parentid,Document doc){
		ArrayList AllList = new ArrayList();
	        try
	        {
		       if("-1".equals(parentid)){
		    	   Element root = doc.getRootElement();
			        List rlist = root.getChildren("function");
		       
		    	   for (int i = 0; i < rlist.size(); i++)
			        {
			        	
			        	LazyDynaBean a_bean = new LazyDynaBean();
			          Element node = (Element) rlist.get(i);
			          /**版本之间的差异控制，市场考滤*/
				        VersionControl ver_ctrl=new VersionControl();
				        /**版本控制*/
				          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id"))) {
                              continue;
                          }
			          String func_id=node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
			          String func_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
			         
			        	  a_bean.set("codeitemid", func_id);  
						  a_bean.set("codeitemdesc",func_name);
						  AllList.add(a_bean);
						
		       }
		       }else{
		        
						String xpath = "//function[@id=\"" + parentid + "\"]";
						if(this.ctrl_ver!=null) {
							xpath = "//function[@id=\"" + parentid + "\" and @ctrl_ver=\""+this.ctrl_ver+"\"]";
						}
			        	XPath xpath_ = XPath.newInstance(xpath);
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
			        	if(ele!=null){
			        	List alist =ele.getChildren("function");
			        	if(alist.size()>0){
				         for(int j=0;j<alist.size();j++){
				        	   Element node = (Element) alist.get(j);
				        	   /**版本之间的差异控制，市场考滤*/
						        VersionControl ver_ctrl=new VersionControl();
						        /**版本控制*/
						          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id"))) {
                                      continue;
                                  }
						          String func_id=node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
						          String func_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
						          
						          //如果权限节点设置了 锁版本号，并且不包含当前版本，跳过此节点 guodd 2018-09-07
						          String ctrl_ver = node.getAttributeValue("ctrl_ver");
						          ctrl_ver = ctrl_ver==null?"":ctrl_ver;
						          if(ctrl_ver.length()>0 && this.lockVersion!=null && (","+ctrl_ver+",").indexOf(","+this.lockVersion+",")==-1) {
                                      continue;
                                  }
						          
						          LazyDynaBean a_bean = new LazyDynaBean();
						        	  a_bean.set("codeitemid", func_id);  
									  a_bean.set("codeitemdesc",func_name);
									  AllList.add(a_bean);
									  }
			        	
			        			}
			        	}else{
			        		 
			        	}
	        }
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        	return AllList;
		}
	/***
	 * 判断传入的function_id是否存在
	 * @param function_id
	 * @return
	 */
	public boolean isExist(String function_id,Document doc){
	        boolean flag = false;
	        try
	        {
		       if("-1".equals(function_id)){
		    	   flag =true;
		       }else{
						String xpath = "//function[@id=\"" + function_id + "\"]";
			        	XPath xpath_ = XPath.newInstance(xpath);
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
			        	if(ele!=null){
			        		flag =true;
			        	}
	        }
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        	return flag;
		}
	/**
	 * 增加接点
	 * @param function_id
	 * @param name
	 * @param parentid
	 */
	public void addFunctionContent(String function_id,String name,String parentid,Document doc){
		//找出父元素
		//在父元素下插入新接点
		//递归寻找父接点到-1为止

	        boolean flag = false;
	        try
	        {
		        Element children = new Element("function");
	        	children.setAttribute("id",function_id);
	        	children.setAttribute("name",name);
		        if("-1".equals(parentid)){
			    	   Element root = doc.getRootElement();
			    	   root.addContent(children);
			    	  
			       }else{
			    	  if(isExist(parentid,doc)){
			    		 
						String xpath = "//function[@id=\"" + parentid + "\"]";
			        	XPath xpath_ = XPath.newInstance(xpath);
			       
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
			        	if(ele!=null){
			        		flag =true;
			        	}
			        	ele.addContent(children);
			       }else{
			    	   Element root = doc.getRootElement();
			    	   root.addContent(children); 
			       }
			       }
		        //writeFile(doc);
			       
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        
		
	}
	public void editFunctionContent(String function_id,String name,Document doc){
		//根接点不能编辑
	        boolean flag = false;
	        try
	        {
		        Element children = new Element("function");
	        	children.setAttribute("id",function_id);
	        	children.setAttribute("name",name);
		        if("-1".equals(function_id)){
			    	  
			    	  
			       }else{
		        
						String xpath = "//function[@id=\"" + function_id + "\"]";
			        	XPath xpath_ = XPath.newInstance(xpath);
			       
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
			        	ele.setAttribute("name", name);
			        	
			     
			       }
		    	//writeFile(doc);
			       
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        
		
	}
	public void editFunctionContent(String function_id,String name,String prefunction_id,Document doc){
		//根接点不能编辑
	        boolean flag = false;
	        try
	        {
		        if("-1".equals(function_id)){
			    	  
			    	  
			       }else{
		        
						String xpath = "//function[@id=\"" + prefunction_id + "\"]";
			        	XPath xpath_ = XPath.newInstance(xpath);
			       
			        	Element ele = (Element) xpath_.selectSingleNode(doc);
			        	ele.setAttribute("name", name);
			        	ele.setAttribute("id",function_id);
			        	
			     
			       }
		    	//writeFile(doc);
			       
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        
		
	}
	/**
	 * 删除节点与子节点通过节点的id
	 * @param parentid
	 */
	public void delFunctionAllById(String parentid,Document doc){
		 boolean flag = false;
	        try
	        {
		        String xpath = "//function[@id=\"" + parentid + "\"]";
	        	XPath xpath_ = XPath.newInstance(xpath);
	       
	        	Element ele = (Element) xpath_.selectSingleNode(doc);
	        	Element parent = ele.getParentElement();
		        	parent.removeContent(ele);
		        
		       
		        	//writeFile(doc);
			       
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	}
	/**
	 * 拖动节点到另一个节点
	 * 
	 * @param parentid
	 */
	public void dragNode(String fromnodeid,String tonodeid, Document doc) {
		boolean flag = false;
		try {
			String xpath ="";
			XPath xpath_=null;
			Element ele =null;
			if("root".equals(tonodeid)){
				 ele = doc.getRootElement();
			}else{
				 xpath = "//function[@id=\"" + tonodeid + "\"]";
				 xpath_ = XPath.newInstance(xpath);

				 ele = (Element) xpath_.selectSingleNode(doc);
			}
			
			 xpath = "//function[@id=\"" + fromnodeid + "\"]";
			 xpath_ = XPath.newInstance(xpath);
			 
			 Element ele2 = (Element) xpath_.selectSingleNode(doc);
			
			 Element parent = ele2.getParentElement();
			parent.removeContent(ele2);
			 ele.addContent(ele2);


		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {

		}
	}
	public String getFuncName(String functionid,Document doc){
		 String name="";
		try
	        {
		        String xpath = "//function[@id=\"" + functionid + "\"]";
	        	XPath xpath_ = XPath.newInstance(xpath);
	       
	        	Element ele = (Element) xpath_.selectSingleNode(doc);
	        	 name = (String)ele.getAttributeValue("name");
			       
	        }
	        catch(Exception ee)
	        {
	            
	        }
	        finally
	        {
	            
	        }
	        return name;
	}
	/**
	 * 节点调整
	 * @param menuid 父节点
	 * @param doc  
	 * @return
	 */
	
	public ArrayList getFuncList(String funcid, Document doc) {
		ArrayList AllList = new ArrayList();
		try {
			String xpath = "//function[@id=\"" + funcid + "\"]";
			if(this.ctrl_ver!=null) {
				xpath = "//function[@id=\"" + funcid + "\" and @ctrl_ver=\""+this.ctrl_ver+"\"]";
			}
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele =null;
			if("root".equals(funcid)){
				 ele = doc.getRootElement();
			}else {
                ele = (Element) xpath_.selectSingleNode(doc);
            }
			if (ele != null) {
				List alist = ele.getChildren("function");
				if (alist.size() > 0) {
					for (int j = 0; j < alist.size(); j++) {
						Element node = (Element) alist.get(j);
						/** 版本之间的差异控制，市场考滤 */
						VersionControl ver_ctrl = new VersionControl();
						/** 版本控制 */
						if (!ver_ctrl.searchFunctionId(node
								.getAttributeValue("id"))) {
                            continue;
                        }
						String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
						String menu_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
						//如果权限节点设置了 锁版本号，并且不包含当前版本，跳过此节点 guodd 2018-09-07
				        String ctrl_ver = node.getAttributeValue("ctrl_ver");
				        ctrl_ver = ctrl_ver==null?"":ctrl_ver;
				        if(ctrl_ver.length()>0 && this.lockVersion!=null && (","+ctrl_ver+",").indexOf(","+this.lockVersion+",")==-1) {
                            continue;
                        }
						
						CommonData	 dataobj = new CommonData(menu_id,menu_name);
						AllList.add(dataobj);
					}

				}
		} 
		}catch (Exception ee) {

		} finally {

		}
		return AllList;
	}
	/**
	 * 保存节点调整顺序
	 * @param menuid 父节点
	 * @param doc  
	 * @return
	 */
	
	public void saveSortFuncList(String funcid,String sorting, Document doc) {
		try {
			String xpath = "//function[@id=\"" + funcid + "\"]";
			if(this.ctrl_ver!=null) {
				xpath = "//function[@id=\"" + funcid + "\" and @ctrl_ver=\""+this.ctrl_ver+"\"]";
			}
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele =null;
			HashMap map = new HashMap();
			if("root".equals(funcid)){
				 ele = doc.getRootElement();
			}else {
                ele = (Element) xpath_.selectSingleNode(doc);
            }
			if (ele != null) {
				List alist = ele.getChildren("function");
				ArrayList outVersionFunc = new ArrayList();
				if (alist.size() > 0) {
					for (int j = 0; j < alist.size(); j++) {
						Element node = (Element) alist.get(j);
						String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
						
						//如果权限节点设置了 锁版本号，并且不包含当前版本，先放到outVersionFunc中 guodd 2018-09-07
				        String ctrl_ver = node.getAttributeValue("ctrl_ver");
				        ctrl_ver = ctrl_ver==null?"":ctrl_ver;
				        if(ctrl_ver.length()>0 && this.lockVersion!=null && (","+ctrl_ver+",").indexOf(","+this.lockVersion+",")==-1) {
				        	outVersionFunc.add(node);
				        	continue;
				        }
						map.put(menu_id, node);
					}
				}
				
				String sorts[] = sorting.split(",");
				ele.removeContent();
				for(int i=0;i<sorts.length;i++){
					if(map!=null&&map.get(sorts[i])!=null) {
                        ele.addContent((Element)map.get(sorts[i]));
                    }
				}
				//最后将 outVersionFunc中的元素放到 最后 guodd 2018-09-07
				for(int i=0;i<outVersionFunc.size();i++) {
					ele.addContent((Element)outVersionFunc.get(i));
				}
			} 
			
		}catch (Exception ee) {

		} finally {

		}
	}
}


package com.hjsj.hrms.businessobject.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.XmlResourceUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuMainBo {
	private Connection conn = null;
	private UserView userView = null;
	private HttpServletRequest request;
	private String entryName = "com/hjsj/hrms/constant/menu.xml";
	public MenuMainBo() {

	}

	public MenuMainBo(Connection conn) {
		this.conn = conn;
	}

	public MenuMainBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 获得绝对路径
	 */
	public String getPath() {
		return XmlResourceUtil.getResourcePath("/constant/menu.xml");
	}
	public void writeFile(Document doc) {
		// 文件处理
		// path为hrpweb3.jar所在的路径
		String path = this.getPath();
		try {
			XmlResourceUtil.writeXmlDocument(path,entryName,doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public InputStream getInputStreamFromjar()  {
		String file = this.getPath();
		return XmlResourceUtil.getInputStreamFromjar(file,entryName);
	}
	public Document getDocument()throws GeneralException {
		String file = this.getPath();
		Document doc = XmlResourceUtil.readXmlDocument(file,entryName);
		return doc;
	}

	/**
	 * 是否有menu菜单权限
	 * @Title: havaMenuPri   
	 * @Description:    
	 * @param menuId 在menu.xml中的menu_id
	 * @return true:有权限；false：无权限
	 */
	public boolean havaMenuPri(String menuId) {
	    try {
    	    boolean isUseNewPrograme = PubFunc.isUseNewPrograme(this.userView);
    	    
    	    //zxj 20170624 70以上版本，老人事异动不启用
            if (",0501,451,401,3301,06016,08010,09010,200203,02016,02024,20070108,".contains(","+menuId+",") && isUseNewPrograme) { 
                return false;
            }
            
            //zxj 20170624 70以下版本，新人事异动不启用
            if (",0503,454,402,3305,06017,08013,09013,200213,02018,02026,20070109,".contains(","+menuId+",") && !isUseNewPrograme) {
                return false;
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        
        return true;
	}

	/**
	 * 通过父接点找子接点
	 * 
	 * @param parentid
	 * @param doc
	 * @param lock
	 * @return
	 */
	public ArrayList getMenuContent(String parentid, Document doc ,EncryptLockClient lock) {
		ArrayList AllList = new ArrayList();
		String bosflag = userView.getBosflag();
		try {
			if ("-1".equals(parentid)) {
				Element root = doc.getRootElement();
				List rlist = root.getChildren("menu");

				for (int i = 0; i < rlist.size(); i++) {

					LazyDynaBean a_bean = new LazyDynaBean();
					Element node = (Element) rlist.get(i);
				
					String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
					String menu_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
					String menu_url = node.getAttributeValue("url")==null?"":node.getAttributeValue("url");
					
					String menu_icon ="";
					// add by xiegh on date 20170102  bug33583
					if("hcm".equalsIgnoreCase(bosflag)) {
                        menu_icon = node.getAttributeValue("iconv7")==null?"":node.getAttributeValue("iconv7");
                    } else {
                        menu_icon = node.getAttributeValue("icon")==null?"":node.getAttributeValue("icon");
                    }
					
					String menu_func_id = node.getAttributeValue("func_id")==null?"":node.getAttributeValue("func_id");
					String menu_target = node.getAttributeValue("target")==null?"":node.getAttributeValue("target");
					String menuhide = node.getAttributeValue("menuhide")==null?"":node.getAttributeValue("menuhide");
					String validate=node.getAttributeValue("validate")==null?"":node.getAttributeValue("validate");//changxy 加载时添加二级验证列 20160621
					
						
					if("true".equalsIgnoreCase(validate)) //为“”或者false都为否
                    {
                        validate="是";
                    } else {
                        validate="否";
                    }
					
					if("false".equalsIgnoreCase(menuhide)){
						menuhide="否";
					}else{
						menuhide="是";
					}
					/*菜单定制 前台显示菜单控制  wangb 29856 20170727*/
					String mod_id = node.getAttributeValue("mod_id");
					if(!haveFuncPriv(menu_func_id,mod_id,lock)) {
                        continue;
                    }
						
					a_bean.set("codeitemid", menu_id);
					a_bean.set("codeitemdesc", menu_name);
					a_bean.set("codeitemurl", menu_url);
					a_bean.set("codeitemicon", menu_icon);
					a_bean.set("codeitemfunc_id", menu_func_id);
					a_bean.set("codeitemtarget", menu_target);
					a_bean.set("menuhide", menuhide);
					a_bean.set("validate", validate);
					AllList.add(a_bean);

				}
			} else {
				String xpath = "//menu[@id=\"" + parentid + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					List alist = ele.getChildren("menu");
 					if (alist.size() > 0) {
						for (int j = 0; j < alist.size(); j++) {
							Element node = (Element) alist.get(j);
							String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
							
							if(!havaMenuPri(menu_id)) {
                                continue;
                            }
				            
							String menu_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
							String menu_url = node.getAttributeValue("url")==null?"":node.getAttributeValue("url");;
							String menu_icon = node.getAttributeValue("icon")==null?"":node.getAttributeValue("icon");
							String menu_func_id = node
									.getAttributeValue("func_id")==null?"":node.getAttributeValue("func_id");
							String menu_target = node
									.getAttributeValue("target")==null?"":node.getAttributeValue("target");
							String menuhide = node.getAttributeValue("menuhide")==null?"":node.getAttributeValue("menuhide");
							String validate=node.getAttributeValue("validate")==null?"":node.getAttributeValue("validate");//changxy 加载时添加二级验证列 20160621
							if("true".equalsIgnoreCase(validate)) //为“”或者false都为否
                            {
                                validate="是";
                            } else {
                                validate="否";
                            }
							if("false".equalsIgnoreCase(menuhide)){
								menuhide="否";
							}else{
								menuhide="是";
							}
							
							/*菜单定制 前台显示菜单控制  wangb 29856 20170727*/
							String mod_id = node.getAttributeValue("mod_id");
							if(!haveFuncPriv(menu_func_id,mod_id,lock)) {
                                continue;
                            }
							
							LazyDynaBean a_bean = new LazyDynaBean();
							a_bean.set("codeitemid", menu_id);
							a_bean.set("codeitemdesc", menu_name);
							a_bean.set("codeitemurl", menu_url);
							a_bean.set("codeitemicon", menu_icon);
							a_bean.set("codeitemfunc_id", menu_func_id);
							a_bean.set("codeitemtarget", menu_target);
							a_bean.set("menuhide", menuhide);
							a_bean.set("validate", validate);
							AllList.add(a_bean);
						}

					}
				} else {

				}
			}
		} catch (Exception ee) {

		} finally {

		}
		return AllList;
	}
	
	/**
	 * 菜单权限控制   wangb  20170727  29856
	 * @param function_id
	 * @param module_id
	 * @param menuMainTree
	 * @return
	 */
	private boolean haveFuncPriv(String function_id,String module_id ,EncryptLockClient lock)
	  {
	      boolean bfunc=true,bmodule=true;
	      
	      /**
	       * 在这里进行权限分析
	       */
	       /**版本功能控制*/
	      VersionControl ver_ctrl=new VersionControl();	
	      UserView userview=userView;   
	      ver_ctrl.setVer(lock.getVersion());

	      if(!(module_id==null|| "".equals(module_id)))
       {
     	String[] modules =StringUtils.split(module_id,",");
         for(int i=0;i<modules.length;i++)
         {
         	module_id=modules[i];
         	bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
         	if(bmodule) {
                break;
            }
         }

       }	
	      
       if(!(function_id==null|| "".equals(function_id)))
       {	      
     	  String[] funcs =StringUtils.split(function_id,","); 
     	  for(int i=0;i<funcs.length;i++)
     	  {
     		  bfunc=ver_ctrl.searchFunctionId(funcs[i],true);
     		  if(bfunc) {
                  break;
              }
     	  }   
      }
		 return (bfunc&bmodule);
	  }	
	

	/***************************************************************************
	 * 判断传入的menu_id是否存在
	 * 
	 * @param menu_id
	 * @return
	 */
	public boolean isExist(String menu_id, Document doc) {
		boolean flag = false;
		try {
			if ("-1".equals(menu_id)) {
				flag = true;
			} else {
				String xpath = "//menu[@id=\"" + menu_id + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					flag = true;
				}
			}
		} catch (Exception ee) {

		} finally {

		}
		return flag;
	}

	/**
	 * 增加接点
	 * 
	 * @param menu_id
	 * @param name
	 * @param parentid
	 */
	public void addMenuContent(String menu_id, String name, String parentid,
			String menu_func_id, String menu_icon, String menu_url,
			String menu_target,String menuhide, Document doc) {
			addMenuContent(menu_id,name,parentid,menu_func_id,menu_icon,menu_url,menu_target,menuhide,doc,"false");//添加 添加二级验证参数，默认为false changxy

			}
			//changxy  添加二级验证到menu.xml中 20160620
			public void addMenuContent(String menu_id, String name, String parentid,
			String menu_func_id, String menu_icon, String menu_url,
			String menu_target,String menuhide, Document doc,String validate) {
		// 找出父元素
		// 在父元素下插入新接点
		// 递归寻找父接点到-1为止
		boolean flag = false;
		try {
			Element children = new Element("menu");
			children.setAttribute("id", menu_id);
			children.setAttribute("name", name);
			children.setAttribute("url", menu_url);
			//tiany add v7版本图标控制
			if("hcm".equals(userView.getBosflag())){
				children.setAttribute("iconv7", menu_icon);
			}else{
				children.setAttribute("icon", menu_icon);
			}
			//end
			children.setAttribute("func_id", menu_func_id);
			children.setAttribute("target", menu_target);
			children.setAttribute("menuhide", menuhide);
			children.setAttribute("validate", validate);			
			if ("-1".equals(parentid)) {
				Element root = doc.getRootElement();
				root.addContent(children);

			} else {
				if (isExist(parentid, doc)) {

					String xpath = "//menu[@id=\"" + parentid + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);

					Element ele = (Element) xpath_.selectSingleNode(doc);
					if (ele != null) {
						flag = true;
					}
					ele.addContent(children);
				} else {
					Element root = doc.getRootElement();
					root.addContent(children);
				}
			}
			// writeFile(doc);

		} catch (Exception ee) {

		} finally {

		}

	}

	public void editMenuContent(String menu_id, String name, String premenu_id,
			String menu_func_id, String menu_icon, String menu_url,
			String menu_target,String menuhide, Document doc) {
		editMenuContent(menu_id,name,premenu_id,menu_func_id,menu_icon,menu_url,menu_target,menuhide,doc,"false"); //修改 添加二级验证参数 默认为false changxy
	}

	public void editMenuContent(String menu_id, String name, String premenu_id,
			String menu_func_id, String menu_icon, String menu_url,
			String menu_target,String menuhide, Document doc,String validate) {
		// 根接点不能编辑
		boolean flag = false;
		try {
			if ("-1".equals(menu_id)) {

			} else {

				String xpath = "//menu[@id=\"" + premenu_id + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);

				UserView user =null;
				//user.getBosflag().equals("hcm")
				Element ele = (Element) xpath_.selectSingleNode(doc);
				ele.setAttribute("name", name);
				ele.setAttribute("id", menu_id);
				ele.setAttribute("url", menu_url);
				//tiany add v7版本图标控制
				if("hcm".equals(userView.getBosflag())){
					ele.setAttribute("iconv7", menu_icon);
				}else{
					ele.setAttribute("icon", menu_icon);
				}
				//end
				//children.setAttribute("icon", menu_icon);
				ele.setAttribute("func_id", menu_func_id);
				ele.setAttribute("target", menu_target);
				ele.setAttribute("menuhide", menuhide);
				ele.setAttribute("validate", validate);

			}
			// writeFile(doc);

		} catch (Exception ee) {

		} finally {

		}

	}

	/**
	 * 删除选中功能
	 * 
	 * @param list
	 */
	public void delMenuContent(ArrayList list, Document doc) {
		boolean flag = false;
		try {
			LazyDynaBean bean = (LazyDynaBean) list.get(0);
			String menu_id = (String) bean.get("codeitemid");
			String xpath = "//menu[@id=\"" + menu_id + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);

			Element ele = (Element) xpath_.selectSingleNode(doc);
			Element parent = ele.getParentElement();
			for (int i = 0; i < list.size(); i++) {
				bean = (LazyDynaBean) list.get(i);
				String id = (String) bean.get("codeitemid");
				xpath = "//menu[@id=\"" + id + "\"]";
				xpath_ = XPath.newInstance(xpath);
				ele = (Element) xpath_.selectSingleNode(doc);
				parent.removeContent(ele);
			}

			// writeFile(doc);

		} catch (Exception ee) {

		} finally {

		}

	}

	/**
	 * 删除节点与子节点通过节点的id
	 * 
	 * @param parentid
	 */
	public void delMenuAllById(String parentid, Document doc) {
		boolean flag = false;
		try {
			String xpath = "//menu[@id=\"" + parentid + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);

			Element ele = (Element) xpath_.selectSingleNode(doc);
			Element parent = ele.getParentElement();
			parent.removeContent(ele);

			// writeFile(doc);

		} catch (Exception ee) {

		} finally {

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
				 xpath = "//menu[@id=\"" + tonodeid + "\"]";
				 xpath_ = XPath.newInstance(xpath);

				 ele = (Element) xpath_.selectSingleNode(doc);
			}
			
			 xpath = "//menu[@id=\"" + fromnodeid + "\"]";
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
	public LazyDynaBean getMenuName(String menuid, Document doc) {
		LazyDynaBean a_bean = new LazyDynaBean();
		
		try {
			String xpath = "//menu[@id=\"" + menuid + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			String menu_id = ele.getAttributeValue("id")==null?"":ele.getAttributeValue("id");
			String menu_name = ele.getAttributeValue("name")==null?"":ele.getAttributeValue("name");
			String menu_url = ele.getAttributeValue("url")==null?"":ele.getAttributeValue("url");
			//tiany add v7版本图标控制 
			
			String icon = "icon";
			if("hcm".equals(userView.getBosflag())){
				icon = "iconv7";
			}
			//String menu_icon = ele.getAttributeValue("icon")==null?"":ele.getAttributeValue("icon");
			String menu_icon = ele.getAttributeValue(icon)==null?"":ele.getAttributeValue(icon);
			String menu_func_id = ele.getAttributeValue("func_id")==null?"":ele.getAttributeValue("func_id");
			String menu_target = ele.getAttributeValue("target")==null?"":ele.getAttributeValue("target");
			String menuhide = ele.getAttributeValue("menuhide")==null?"":ele.getAttributeValue("menuhide");
			String validate=ele.getAttributeValue("validate")==null?"false":ele.getAttributeValue("validate");
			if("false".equalsIgnoreCase(menuhide)){
				menuhide="false";
			}else{
				menuhide="true";
			}
			if("false".equalsIgnoreCase(validate)) {
                validate="false";
            } else {
                validate="true";
            }
			
			a_bean.set("validate", validate);
			a_bean.set("codeitemid", menu_id);
			a_bean.set("codeitemdesc", menu_name);
			a_bean.set("codeitemurl", menu_url);
			a_bean.set("codeitemicon", menu_icon);
			a_bean.set("codeitemfunc_id", menu_func_id);
			a_bean.set("codeitemtarget", menu_target);
			a_bean.set("menuhide", menuhide);

		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {

		}
		return a_bean;
	}
	/**
	 * 节点调整
	 * @param menuid 父节点
	 * @param doc  
	 * @return
	 */
	
	public ArrayList getMenuList(String menuid, Document doc) {
		ArrayList AllList = new ArrayList();
		try {
			String xpath = "//menu[@id=\"" + menuid + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele =null;
			if("root".equals(menuid)){
				 ele = doc.getRootElement();
			}else {
                ele = (Element) xpath_.selectSingleNode(doc);
            }
			if (ele != null) {
				List alist = ele.getChildren("menu");
				if (alist.size() > 0) {
					for (int j = 0; j < alist.size(); j++) {
						Element node = (Element) alist.get(j);
						String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
						String menu_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
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
	
	public void saveSortMenuList(String menuid,String sorting, Document doc) {
		try {
			String xpath = "//menu[@id=\"" + menuid + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele =null;
			HashMap map = new HashMap();
			if("root".equals(menuid)){
				 ele = doc.getRootElement();
			}else {
                ele = (Element) xpath_.selectSingleNode(doc);
            }
			if (ele != null) {
				List alist = ele.getChildren("menu");
				if (alist.size() > 0) {
					for (int j = 0; j < alist.size(); j++) {
						Element node = (Element) alist.get(j);
						String menu_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
						map.put(menu_id, node);
					}

				}
		} 
			String sorts[] = sorting.split(",");
			ele.removeContent();
			for(int i=0;i<sorts.length;i++){
				if(map!=null&&map.get(sorts[i])!=null) {
                    ele.addContent((Element)map.get(sorts[i]));
                }
			}
		}catch (Exception ee) {

		} finally {

		}
	}
}

package com.hjsj.hrms.businessobject.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.XmlResourceUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PortalMainBo {
	private Connection conn = null;
	private UserView userView = null;
	private HttpServletRequest request;
	private Document doc =null;
	private String entryName = "com/hjsj/hrms/constant/portal.xml";
	public PortalMainBo() {
		initDoc();
	}

	public PortalMainBo(UserView userView) {
		initDoc();
		this.userView = userView;
	}
	public PortalMainBo(Connection conn) {
		this.conn = conn;
	}

	public PortalMainBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}
public void initDoc(){
	try {
		this.doc =getDocument();
	} catch (GeneralException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
public String getpanelHeight(String id){
	String height="";
	String xpath = "//panel[@id=\"" + id + "\"]";
	XPath xpath_;
	try {
		xpath_ = XPath.newInstance(xpath);
		if(this.doc!=null){
		Element ele = (Element) xpath_.selectSingleNode(this.doc);
		if(ele!=null){
			height = ele.getAttributeValue("height")==null?"":ele.getAttributeValue("height");
		}
		}
	} catch (JDOMException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return height;
}

	/**
	 * 获得绝对路径
	 */
	public String getPath() {
		return new XmlResourceUtil().getResourcePath("/constant/portal.xml");
	}
	public void writeFile(Document doc) {
		// 文件处理
		// path为hrpweb3.jar所在的路径
		String path = this.getPath();
		try {
			new XmlResourceUtil().writeXmlDocument(path,entryName,doc);
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
		Document doc = new XmlResourceUtil().readXmlDocument(file,entryName);
		return doc;
	}

	/**
	 * 通过父接点找子接点
	 * 
	 * @param parentid
	 * @return
	 */
	public ArrayList getPortalContent(String parentid, Document doc,String _opt) {
		ArrayList AllList = new ArrayList();
		try {
			if ("-1".equals(parentid)) {
				Element root = doc.getRootElement();
				List rlist = root.getChildren("portal");

				for (int i = 0; i < rlist.size(); i++) {

					LazyDynaBean a_bean = new LazyDynaBean();
					Element node = (Element) rlist.get(i);
					String portal_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
					String portal_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
					String portal_url = node.getAttributeValue("url")==null?"":node.getAttributeValue("url");
					String portal_icon = node.getAttributeValue("icon")==null?"":node.getAttributeValue("icon");
					String portal_func_id = node.getAttributeValue("func_id")==null?"":node.getAttributeValue("func_id");
					String portal_target = node.getAttributeValue("target")==null?"":node.getAttributeValue("target");
					String portal_height = node.getAttributeValue("height")==null?"":node.getAttributeValue("height");
					String portal_hide = node.getAttributeValue("hide")==null?"":node.getAttributeValue("hide");
					String portal_priv = node.getAttributeValue("priv")==null?"":node.getAttributeValue("priv");
					String portal_columns = node.getAttributeValue("columns")==null?"":node.getAttributeValue("columns");

					a_bean.set("codeitemid", portal_id);
					a_bean.set("codeitemdesc", portal_name);
					a_bean.set("codeitemurl", portal_url);
					a_bean.set("codeitemicon", portal_icon);
					a_bean.set("codeitemfunc_id", portal_func_id);
					a_bean.set("codeitemtarget", portal_target);
					a_bean.set("codeitemheight", portal_height);
					a_bean.set("codeitemhide", portal_hide);
					a_bean.set("codeitempriv", portal_priv);
					a_bean.set("codeitemopt", "1");
					a_bean.set("codeitemcolumns",portal_columns);
					AllList.add(a_bean);

				}
			} else {
				if("1".equals(_opt)){
				String xpath = "//portal[@id=\"" + parentid + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					List alist = ele.getChildren("column");
					if (alist.size() > 0) {
						for (int j = 0; j < alist.size(); j++) {
							Element node = (Element) alist.get(j);
						
							String portal_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
							String portal_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
							String portal_url = node.getAttributeValue("url")==null?"":node.getAttributeValue("url");;
							String portal_icon = node.getAttributeValue("icon")==null?"":node.getAttributeValue("icon");
							String portal_func_id = node
									.getAttributeValue("func_id")==null?"":node.getAttributeValue("func_id");
							String portal_target = node
									.getAttributeValue("target")==null?"":node.getAttributeValue("target");
							String portal_height = node.getAttributeValue("height")==null?"":node.getAttributeValue("height");
							String portal_hide = node.getAttributeValue("hide")==null?"":node.getAttributeValue("hide");
							String portal_priv = node.getAttributeValue("priv")==null?"":node.getAttributeValue("priv");
							String portal_colwidth = node.getAttributeValue("colwidth")==null?"":node.getAttributeValue("colwidth");
							LazyDynaBean a_bean = new LazyDynaBean();
							a_bean.set("codeitemid", portal_id);
							a_bean.set("codeitemdesc", portal_name);
							a_bean.set("codeitemurl", portal_url);
							a_bean.set("codeitemicon", portal_icon);
							a_bean.set("codeitemfunc_id", portal_func_id);
							a_bean.set("codeitemtarget", portal_target);
							a_bean.set("codeitemheight", portal_height);
							a_bean.set("codeitemhide", portal_hide);
							a_bean.set("codeitempriv", portal_priv);
							a_bean.set("codeitemopt", "2");
							a_bean.set("codeitemcolwidth", portal_colwidth);
							AllList.add(a_bean);
						}

					}
				} 
				}else if("2".equals(_opt)){

					String xpath = "//column[@id=\"" + parentid + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					if (ele != null) {
						List alist = ele.getChildren("panel");
						if (alist.size() > 0) {
							for (int j = 0; j < alist.size(); j++) {
								Element node = (Element) alist.get(j);
								String portal_id = node.getAttributeValue("id")==null?"":node.getAttributeValue("id");
								String portal_name = node.getAttributeValue("name")==null?"":node.getAttributeValue("name");
								String portal_url = node.getAttributeValue("url")==null?"":node.getAttributeValue("url");;
								String portal_icon = node.getAttributeValue("icon")==null?"":node.getAttributeValue("icon");
								String portal_func_id = node
										.getAttributeValue("func_id")==null?"":node.getAttributeValue("func_id");
								String portal_target = node
										.getAttributeValue("target")==null?"":node.getAttributeValue("target");
								String portal_height = node.getAttributeValue("height")==null?"":node.getAttributeValue("height");
								String portal_hide = node.getAttributeValue("hide")==null?"":node.getAttributeValue("hide");
								String portal_priv = node.getAttributeValue("priv")==null?"":node.getAttributeValue("priv");
								if("false".equalsIgnoreCase(portal_hide)){
									portal_hide="是";
								}else{
									portal_hide="否";
								}
								if("true".equalsIgnoreCase(portal_priv)){
									portal_priv="是";
								}else{
									portal_priv="否";
								}
		
								LazyDynaBean a_bean = new LazyDynaBean();
								a_bean.set("codeitemid", portal_id);
								a_bean.set("codeitemdesc", portal_name);
								a_bean.set("codeitemurl", portal_url);
								a_bean.set("codeitemicon", portal_icon);
								a_bean.set("codeitemfunc_id", portal_func_id);
								a_bean.set("codeitemtarget", portal_target);
								a_bean.set("codeitemheight", portal_height);
								a_bean.set("codeitemhide", portal_hide);
								a_bean.set("codeitempriv", portal_priv);
								a_bean.set("codeitemopt", "3");
								if("是".equalsIgnoreCase(portal_priv)){
								a_bean.set("codeitemoperation", "true");
								
								}
								AllList.add(a_bean);
							}

						}
					} 
					
				}
			}
		} catch (Exception ee) {

		} finally {

		}
		return AllList;
	}

	/***************************************************************************
	 * 判断传入的portal_id是否存在
	 * 
	 * @param portal_id
	 * @return
	 */
	public boolean isExist(String portal_id, String opt,Document doc) {
		boolean flag = false;
		try {
			if ("-1".equals(portal_id)) {
				flag = true;
			} else {
				if("1".equals(opt)){
				String xpath = "//portal[@id=\"" + portal_id + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					flag = true;
					return flag;
				}
				}
				if("2".equals(opt)){
					String xpath = "//column[@id=\"" + portal_id + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					if (ele != null) {
						flag = true;
						return flag;
					}
				}
				String xpath = "//panel[@id=\"" + portal_id + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					flag = true;
					return flag;
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
	 * @param portal_id
	 * @param name
	 * @param parentid
	 */
	public void addPortalContent(String portal_id, String name, String parentid,
			String portal_func_id, String portal_icon, String portal_url,
			String portal_target,String colnum,String colwidth,String height,String hide,String priv, String opt,Document doc) {
		// 找出父元素
		// 在父元素下插入新接点
		// 递归寻找父接点到-1为止
		boolean flag = false;
		try {
			if ("-1".equals(parentid)) {
				Element children = new Element("portal");
				children.setAttribute("id", portal_id);
				children.setAttribute("name", name);
				children.setAttribute("columns", colnum);
				Element root = doc.getRootElement();
				root.addContent(children);

			} else {
				Element children=null;
				if("1".equals(opt)){
					 children = new Element("column");
					children.setAttribute("id", portal_id);
					children.setAttribute("name", name);
					children.setAttribute("colwidth", colwidth);
					
					
				}else if("2".equals(opt)){
				 children = new Element("panel");
				children.setAttribute("id", portal_id);
				children.setAttribute("name", name);
				children.setAttribute("height", height);
				children.setAttribute("url", portal_url);
				if(userView!=null&&"hcm".equals(this.userView.getBosflag())){
					children.setAttribute("iconv7", portal_icon);
				}else{
					children.setAttribute("icon", portal_icon);
				}
				children.setAttribute("hide", hide);
				children.setAttribute("priv", priv);
				}
				
				if (isExist(parentid,opt, doc,"1")) {

					String xpath = "//portal[@id=\"" + parentid + "\"]";
					if("1".equals(opt)){
						xpath = "//portal[@id=\"" + parentid + "\"]";
					}else if("2".equals(opt)){
						xpath = "//column[@id=\"" + parentid + "\"]";
					}
					XPath xpath_ = XPath.newInstance(xpath);

					Element ele = (Element) xpath_.selectSingleNode(doc);
					if (ele != null) {
						flag = true;
					}
					if("1".equals(opt)){
						ele.setAttribute("columns",ele.getChildren("column").size()+1+"");
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

	public void editPortalContent(String portal_id, String name, String preportal_id,
			String portal_func_id, String portal_icon, String portal_url,
			String portal_target,String colnum,String colwidth,String height,String hide,String priv, String opt, Document doc) {
		// 根接点不能编辑
		boolean flag = false;
		try {
			if ("-1".equals(portal_id)) {

			} else {
				if("1".equals(opt)){
					String xpath = "//portal[@id=\"" + preportal_id + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);

					Element ele = (Element) xpath_.selectSingleNode(doc);
					ele.setAttribute("name", name);
					ele.setAttribute("id", portal_id);
				}else if("2".equals(opt)){
					String xpath = "//column[@id=\"" + preportal_id + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);

					Element ele = (Element) xpath_.selectSingleNode(doc);
					ele.setAttribute("name", name);
					ele.setAttribute("id", portal_id);
					ele.setAttribute("colwidth", colwidth);
					//xus 18/10/9 工作桌面、服务大厅显示隐藏设置
					if("024".equals(preportal_id)||"025".equals(preportal_id)){
						ele.setAttribute("hide", hide);
					}
				}else if("3".equals(opt)){
					String xpath = "//panel[@id=\"" + preportal_id + "\"]";
					XPath xpath_ = XPath.newInstance(xpath);

					Element ele = (Element) xpath_.selectSingleNode(doc);
					ele.setAttribute("id", portal_id);
					ele.setAttribute("name", name);
					ele.setAttribute("height", height);
					ele.setAttribute("url", portal_url);
					if(userView!=null&&"hcm".equals(userView.getBosflag())){
						ele.setAttribute("iconv7", portal_icon);
					}else{
						ele.setAttribute("icon", portal_icon);
					}
					ele.setAttribute("hide", hide);
					ele.setAttribute("priv", priv);
				}
				

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
	public void delPortalContent(ArrayList list, Document doc) {
		boolean flag = false;
		try {
			LazyDynaBean bean = (LazyDynaBean) list.get(0);
			String portal_id = (String) bean.get("codeitemid");
			String xpath = "//portal[@id=\"" + portal_id + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);

			Element ele = (Element) xpath_.selectSingleNode(doc);
			Element parent = ele.getParentElement();
			for (int i = 0; i < list.size(); i++) {
				bean = (LazyDynaBean) list.get(i);
				String id = (String) bean.get("codeitemid");
				xpath = "//portal[@id=\"" + id + "\"]";
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
	public void delPortalAllById(String parentid, Document doc) {
		boolean flag = false;
		try {
			if(parentid.indexOf("'")!=-1){
				parentid =parentid.substring(0,parentid.indexOf("'"));
			}
			String xpath = "//portal[@id=\"" + parentid + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);

			Element ele = (Element) xpath_.selectSingleNode(doc);
			if(ele==null){
				 xpath = "//column[@id=\"" + parentid + "\"]";
				 xpath_ = XPath.newInstance(xpath);

				 ele = (Element) xpath_.selectSingleNode(doc);
				 if(ele!=null){
					 Element parent= ele.getParentElement();
					 if(parent.getChildren("column").size()>1){
					 parent.setAttribute("columns", parent.getChildren("column").size()-1+"");
					 }
				 }
			}
			if(ele==null){
				 xpath = "//panel[@id=\"" + parentid + "\"]";
				 xpath_ = XPath.newInstance(xpath);

				 ele = (Element) xpath_.selectSingleNode(doc);
			}
			Element parent = ele.getParentElement();
			parent.removeContent(ele);

			// writeFile(doc);

		} catch (Exception ee) {

		} finally {

		}
	}

	public LazyDynaBean getPortalName(String portalid,String opt, Document doc) {
		LazyDynaBean a_bean = new LazyDynaBean();
		
		try {
			String xpath = "//portal[@id=\"" + portalid + "\"]";
			if("1".equals(opt)) {
                xpath = "//portal[@id=\"" + portalid + "\"]";
            } else if("2".equals(opt)) {
                xpath = "//column[@id=\"" + portalid + "\"]";
            } else if("3".equals(opt)) {
                xpath = "//panel[@id=\"" + portalid + "\"]";
            }
		
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			double colwidths =0.0 ;
			int num=0;
			if("1".equals(opt)){
				List rlist = 	ele.getChildren("column");
				
				for (int i = 0; i < rlist.size(); i++) {
					Element node = (Element) rlist.get(i);
					String colwidth = node.getAttributeValue("colwidth")==null?"0":node.getAttributeValue("colwidth");
					num++;
					if("".equals(colwidth.trim())) {
                        colwidth="0";
                    }
					colwidths+=Double.parseDouble(colwidth);
				}
				
			}
			if("2".equals(opt)){
				
				List rlist = 	ele.getParentElement().getChildren("column");
				
				for (int i = 0; i < rlist.size(); i++) {
					
					Element node = (Element) rlist.get(i);
					String columnid = node.getAttributeValue("id")==null?"0":node.getAttributeValue("id");
					if(portalid.equals(columnid)) {
                        continue;
                    }
					String colwidth = node.getAttributeValue("colwidth")==null?"0":node.getAttributeValue("colwidth");
					num++;
					if("".equals(colwidth.trim())) {
                        colwidth="0";
                    }
					colwidths+=Double.parseDouble(colwidth);
				}
				
			}
			String portal_id = ele.getAttributeValue("id")==null?"":ele.getAttributeValue("id");
			String portal_name = ele.getAttributeValue("name")==null?"":ele.getAttributeValue("name");
			String portal_url = ele.getAttributeValue("url")==null?"":ele.getAttributeValue("url");
			String portal_icon ="";
			if(userView!=null&&"hcm".equals(this.userView.getBosflag())){
				portal_icon = ele.getAttributeValue("iconv7")==null?"":ele.getAttributeValue("iconv7");
			}else{
				portal_icon = ele.getAttributeValue("icon")==null?"":ele.getAttributeValue("icon");
			}
			String portal_func_id = ele.getAttributeValue("func_id")==null?"":ele.getAttributeValue("func_id");
			String portal_target = ele.getAttributeValue("target")==null?"":ele.getAttributeValue("target");
			String portal_height = ele.getAttributeValue("height")==null?"":ele.getAttributeValue("height");
			String portal_hide = ele.getAttributeValue("hide")==null?"":ele.getAttributeValue("hide");
			String portal_priv = ele.getAttributeValue("priv")==null?"":ele.getAttributeValue("priv");
			String columns = ele.getAttributeValue("columns")==null?"":ele.getAttributeValue("columns");
			String colwidth = ele.getAttributeValue("colwidth")==null?"":ele.getAttributeValue("colwidth");
			a_bean.set("codeitemid", portal_id);
			a_bean.set("codeitemdesc", portal_name);
			a_bean.set("codeitemurl", portal_url);
			a_bean.set("codeitemicon", portal_icon);
			a_bean.set("codeitemfunc_id", portal_func_id);
			a_bean.set("codeitemtarget", portal_target);
			a_bean.set("codeitemheight", portal_height);
			a_bean.set("codeitemhide", portal_hide);
			a_bean.set("codeitempriv", portal_priv);
			a_bean.set("columns", columns);
			a_bean.set("colwidth", colwidth);
			a_bean.set("colwidths",""+ colwidths);
			if(Integer.parseInt(columns)<=num){
			a_bean.set("columnsuper","true");	
			}else{
				a_bean.set("columnsuper","false");	
			}

		} catch (Exception ee) {

		} finally {

		}
		return a_bean;
	}
	private Document createxmlctrl_para(){
		
		Element params=new Element("portals");
//		Element notes =new Element("notes");
//		notes.setAttribute("email","false");
//		notes.setAttribute("sms","false");
//		Element sp_flag=new Element("sp_flag");
//		sp_flag.setAttribute("mode","");
//		Element edit_form=new Element("edit_form");
//		edit_form.setAttribute("url","");
//		Element appeal_form=new Element("appeal_form");
//		appeal_form.setAttribute("url","");
//		params.addContent(notes);
//		params.addContent(sp_flag);
//		params.addContent(edit_form);
//		params.addContent(appeal_form);
		Document doc =new Document(params);
		return doc;
	}
	public static void main(String[] args) {
		PortalMainBo bo = new PortalMainBo();
	//	bo.getpanelHeight("0113");
		// System.out.println(fw);
	}

	/***************************************************************************
	 * 判断传入的portal_id是否存在
	 * 
	 * @param portal_id
	 * @param addoredit 等于1为新增，等于2为编辑
	 * @return
	 */
	public boolean isExist(String portal_id, String opt,Document doc, String addoredit) {
		boolean flag = false;
		try {
			String xpath = "";
			if ("-1".equals(portal_id)) {
				flag = true;
			} else {
				if("1".equals(opt)){
				xpath = "//portal[@id=\"" + portal_id + "\"]";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					flag = true;
					return flag;
				}
				}
				if("2".equals(opt)){
					if("1".equals(addoredit)){
						xpath = "//panel[@id=\"" + portal_id + "\"]";
					}else if("2".equals(addoredit)){
						xpath = "//column[@id=\"" + portal_id + "\"]";
					}
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					if (ele != null) {
						flag = true;
						return flag;
					}
				}
				if("1".equals(addoredit)){
					xpath = "//column[@id=\"" + portal_id + "\"]";
				}else if("2".equals(addoredit)){
					xpath = "//panel[@id=\"" + portal_id + "\"]";
				}
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					flag = true;
					return flag;
				}
			}
		} catch (Exception ee) {

		} finally {

		}
		return flag;
	}

}

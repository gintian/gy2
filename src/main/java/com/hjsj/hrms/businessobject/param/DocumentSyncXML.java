package com.hjsj.hrms.businessobject.param;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:DocumentSyncXML.java
 * </p>
 * <p>
 * Description>:应用于考勤休假 机构参数 同步配置
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Dec 18, 2010 3:41:04 PM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: LiWeichao
 *          </p>
 */
public class DocumentSyncXML {
	public final static int FILESET = 0;// #班子标识参数
	private Connection conn;
	private String xmlContent;
	private Document doc;

	public DocumentSyncXML(Connection conn, String xmlContent) {
		this.conn = conn;
		this.xmlContent = xmlContent;
		try {
			this.doc = PubFunc.generateDom(xmlContent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//see();
	}

	/**
	 * 得到节点值
	 * 
	 * @param sync_id
	 *            datasource id
	 * @param property
	 * @return
	 */
	public String getValue(int sync_id, String property) {
		String value = "";
		try {
			String str_path = "/datasources/datasource[@id='" + sync_id + "']/"
					+ property;
			XPath xpath = XPath.newInstance(str_path);
			List childlist = xpath.selectNodes(this.doc);
			Element element = null;
			if (childlist.size() != 0) {
				element = (Element) childlist.get(0);
				value = element.getText();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 得到节点值
	 * 
	 * @param sync_id
	 *            datasource id
	 * @param property
	 * @return
	 */
	public Element getElement(String sync_id) {
		Element element = null;
		try {
			String str_path = "/datasources/datasource[@id='" + sync_id + "']";
			XPath xpath = XPath.newInstance(str_path);
			List childlist = xpath.selectNodes(this.doc);
			
			if (childlist.size() != 0) {
				element = (Element) childlist.get(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return element;
	}
	

	/**
	 * 根据xml的路径返回所有节点
	 * @param xmlpath
	 * @return
	 */
	public List getAllElmentList(String xmlpath) {
		List list = new ArrayList();
		try {
			XPath xpath = XPath.newInstance(xmlpath);
			list = xpath.selectNodes(this.doc);
			if (list == null) {
				list = new ArrayList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据xml的路径返回所有节点集合
	 * @param xmlpath
	 * @return
	 */
	public List getBeanList(String xmlpath) {
		List beanList = new ArrayList();
		List list = getAllElmentList(xmlpath);
		try {			
			for (int i = 0; i < list.size(); i++) {
				Element el = (Element)list.get(i);
				String id = el.getAttributeValue("id");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id", id);
				List childList = el.getChildren();
				for (int j = 0; j < childList.size(); j++) {
					Element e = (Element) childList.get(j);
					String name = e.getName();
					String value = e.getText();
					bean.set(name, value);
				}
				beanList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beanList;
	}

	/**
	 * 设置节点的值
	 * 
	 * @param sync_id
	 *            datasource id
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(String sync_id, String property, String value) {
		boolean bflag = true;
		if (value == null) {
            value = "";
        }
		try {
			String str_path = "/datasources/datasource[@id='" + sync_id + "']/"
					+ property;
			XPath xpath = XPath.newInstance(str_path);
			Element childlist = (Element)xpath.selectSingleNode(this.doc, str_path);
			childlist.setText(value);
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
		}
		return bflag;
	}

	/**
	 * 保存新增的数据源信心
	 * @param bean
	 * @throws GeneralException
	 */
	public void saveParameter(LazyDynaBean bean) throws GeneralException {
		
		Element el = putTo(bean);
		Element root = doc.getRootElement();
		root.addContent(el);				
	}
	
	/**
	 * 重新封装数据
	 * @param bean
	 * @return
	 */
	private Element putTo(LazyDynaBean bean) {
		Element el = new Element("datasource");
		el.setAttribute("id", (String) bean.get("syncxml_id"));
		// 描述节点
		Element desc = new Element("desc");
		desc.setText((String) bean.get("syncxml_desc"));
		
		// 数据库类型
		Element dbtype = new Element("dbtype");
		dbtype.setText((String) bean.get("syncxml_dbtype"));
		
		// 数据库ip
		Element ip = new Element("ip");
		ip.setText((String) bean.get("syncxml_ip"));
		// 端口
		Element port = new Element("port");
		port.setText((String) bean.get("syncxml_port"));
		
		
		// 数据库名称
		Element dbname = new Element("dbname");
		dbname.setText((String) bean.get("syncxml_dbname"));
		
		// 数据库表空间
		Element space = new Element("space");
		space.setText((String) bean.get("syncxml_space"));

		// 数据库用户名
		Element user = new Element("user");
		user.setText((String) bean.get("syncxml_user"));
		
		// 数据库密码
		Element pwd = new Element("pwd");
		pwd.setText((String) bean.get("syncxml_pwd"));

		// 数据库可用状态
		Element status = new Element("status");
		String st = (String) bean.get("syncxml_status");
		if (st == null || st.length() == 0) {
			st = "0";
		}
		status.setText(st);
		
		// 关联指标
		Element related = new Element("related");
		related.setText((String) bean.get("syncxml_related"));
		
		// 操作表
		Element options = new Element("options");
		options.setText((String) bean.get("syncxml_options"));
		
		// 操作表
		Element source = new Element("source");
		source.setText((String) bean.get("syncxml_source"));
		
		el.addContent(desc);
		el.addContent(dbtype);
		el.addContent(ip);
		el.addContent(port);
		el.addContent(dbname);
		el.addContent(space);
		el.addContent(user);
		el.addContent(pwd);
		el.addContent(status);
		el.addContent(related);
		el.addContent(options);
		el.addContent(source);
		
		return el;

	}
	
	/**
	 * 更新一个数据库连接信息
	 * @param bean
	 * @throws GeneralException
	 */
	public void updateParameter(LazyDynaBean bean) throws GeneralException {
		Map map = bean.getMap();
		String id = (String) bean.get("syncxml_id");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			String name = (String) en.getKey();
			String value = (String) en.getValue();
			if (!"syncxml_id".equalsIgnoreCase(name)) {
				this.setValue(id, name.replaceAll("syncxml_", ""), value);
			}
		}
	}
	
	/**
	 * 根据id删除一个节点
	 * @param id String 序号
	 * @throws GeneralException
	 */
	public void deleteParameter(String id) throws GeneralException {
		Element el = this.getElement(id);
		doc.getRootElement().removeContent(el);
	}

	public boolean if_vo_Empty(String constant) {
		String sql = "select * from kq_parameter where UPPER(content)='"
				+ constant.toUpperCase() + "'";
		ContentDAO dao = new ContentDAO(conn);
		boolean is_correct = true;
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (!rs.next()) {
				is_correct = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is_correct;
	}

	public String getXML() {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		return outputter.outputString(this.doc);
	}
	
//	public void see() {
//		XMLOutputter outputter = new XMLOutputter();
//		Format format = Format.getPrettyFormat();
//		format.setEncoding("UTF-8");
//		outputter.setFormat(format);
//		System.out.println(outputter.outputString(doc));
//	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
}

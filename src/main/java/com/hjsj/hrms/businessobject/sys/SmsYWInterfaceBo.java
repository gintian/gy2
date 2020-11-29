/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SmsYWInterfaceBo</p>
 * <p>Description:短信业务接口业务类</p> 
 * <p>Company:hjsj</p> 
 * create time at:2011-05-26 14:36
 * @author wangzhongjun
 * @version 1.0
 */
public class SmsYWInterfaceBo {

	// 数据库连接
	private Connection conn = null;
	// xml document对象
	private Document doc = null;
	
	public SmsYWInterfaceBo () {
		
	}
	
	public SmsYWInterfaceBo(Connection conn) {
		this.conn = conn;
		init();
	}
	
	/**
	 * 获得参数设置列表
	 * @return
	 */
	public ArrayList getList() {
		String path = "/messageset/set";
		ArrayList list = new ArrayList();
		try {
			XPath xpath = XPath.newInstance(path);
			List elList = xpath.selectNodes(doc);
			for (int i = 0; i < elList.size(); i++) {
				Element el = (Element) elList.get(i);
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("code", el.getAttributeValue("code"));
				bean.set("desc", el.getAttributeValue("desc"));
				bean.set("status", el.getAttributeValue("status"));
				bean.set("classes", el.getAttributeValue("classes"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据code找到相应的记录
	 * @param code
	 * @return
	 */
	public LazyDynaBean getBeanByCode(String code) {
		
		if (code == null || code.length() <= 0) {
			return new LazyDynaBean();
		}
		LazyDynaBean bean = new LazyDynaBean();
		ArrayList list = getList();
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean temp = (LazyDynaBean) list.get(i);
			String tempCode = (String) temp.get("code");
			if (code.equals(tempCode)) {
				bean = temp;
				break;
			}
		}
		
		return bean;
	}
	
	/**
	 * 是否存在code这样的业务代码
	 * @param code
	 * @return，boolean 存在返回true，不存在返回false
	 * @throws GeneralException
	 */
	public boolean isExistCode(String code) throws GeneralException {
		boolean flag = false;
		if (code == null || code.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"code的值不能为空！"));
		}
		ArrayList list = getList();
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean temp = (LazyDynaBean) list.get(i);
			String tempCode = (String) temp.get("code");
			if (code.equals(tempCode)) {
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 根据code更新
	 * @param bean
	 * @param code
	 * @return
	 */
	public boolean updateByCode(LazyDynaBean bean, String code) {
		boolean flag = false;
		String path = "/messageset/set[@code=\"" + code + "\"]";
		try {
			XPath xpath = XPath.newInstance(path);
			Element ele = (Element) xpath.selectSingleNode(this.doc);
			ele.setAttribute("desc", (String) bean.get("desc"));
			ele.setAttribute("status", (String) bean.get("status"));
			ele.setAttribute("classes", (String) bean.get("classes"));
			
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", "ACCEPTMESSAGESET");
			vo.setString("str_value", getOutXml());
			
			flag = update(vo);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 根据code更新
	 * @param bean
	 * @param code
	 * @return
	 */
	public boolean batchUpdateByCode(ArrayList list) {
		boolean flag = false;
		
		try {
			for (int i = 0; i < list.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				String code = (String) bean.get("code");
				String path = "/messageset/set[@code=\"" + code + "\"]";
				XPath xpath = XPath.newInstance(path);
				Element ele = (Element) xpath.selectSingleNode(this.doc);
				ele.setAttribute("desc", (String) bean.get("desc"));
				ele.setAttribute("status", (String) bean.get("status"));
				ele.setAttribute("classes", (String) bean.get("classes"));
			}
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", "ACCEPTMESSAGESET");
			vo.setString("str_value", getOutXml());
			
			flag = update(vo);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 根据code更新
	 * @param bean
	 * @param code
	 * @return
	 */
	public boolean add(LazyDynaBean bean) {
		boolean flag = false;
		
		try {
			Element ele = new Element("set");
			ele.setAttribute("code", (String) bean.get("code"));
			ele.setAttribute("desc", (String) bean.get("desc"));
			ele.setAttribute("status", (String) bean.get("status"));
			ele.setAttribute("classes", (String) bean.get("classes"));
			this.doc.getRootElement().addContent(ele);
			
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", "ACCEPTMESSAGESET");
			vo.setString("str_value", getOutXml());
			
			flag = save(vo);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 根据code更新
	 * @param bean
	 * @param code
	 * @return
	 */
	public boolean deleteByCode(String[] code) {
		boolean flag = false;
		
		try {
			for (int i = 0; i < code.length; i++) {
				String path = "/messageset/set[@code=\"" + code[i] + "\"]";
				XPath xpath = XPath.newInstance(path);
				Element ele = (Element) xpath.selectSingleNode(this.doc);
				this.doc.getRootElement().removeContent(ele);
			}
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", "ACCEPTMESSAGESET");
			vo.setString("str_value", getOutXml());
			
			flag = save(vo);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 保存参数
	 * @param vo
	 * @return
	 */
	public boolean save(RecordVo vo) {
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if (isExistConstant()) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 保存参数
	 * @param vo
	 * @return
	 */
	public boolean update(RecordVo vo) {
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.updateValueObject(vo);
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 获得更新后的xml字符窜
	 * @return
	 */
	private String getOutXml() {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		return outputter.outputString(this.doc);
	}
	/**
	 * 初始化document对象
	 */
	private void init() {
		try {
			doc = PubFunc.generateDom(getParamSet());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询参数设置
	 * @return String xml字符窜
	 */
	private String getParamSet() {
		ContentDAO dao = new ContentDAO(this.conn);
		String xml = "";
		RowSet rs = null;
		// 查询短信业务接口的参数设置
		try {
			String sql = "select str_value from constant where constant='ACCEPTMESSAGESET'";
			rs = dao.search(sql);
			if (rs.next()) {
				xml = rs.getString("str_value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// 为xml赋一个初始值
		if (xml == null || xml.length() <= 0) {
			xml = "<?xml version='1.0' encoding='GB2312'?><messageset></messageset>";
		}
		
		return xml;
	}
	
	/**
	 * 是否存在常量
	 * @return
	 */
	private boolean isExistConstant() {
		ContentDAO dao = new ContentDAO(this.conn);
		boolean flag = false;
		RowSet rs = null;
		// 查询短信业务接口的参数设置
		try {
			String sql = "select str_value from constant where constant='ACCEPTMESSAGESET'";
			rs = dao.search(sql);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return flag;
	}
	
	
}

package com.hjsj.hrms.module.workplan.config.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkPlanFunctionBo {
	private Connection conn = null;
	private UserView userView = null;
	public WorkPlanFunctionBo(){}
	public WorkPlanFunctionBo(Connection conn) {
		this.conn = conn;
	}
	public WorkPlanFunctionBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}
	
	/**
	 * 根据前台传递的数据生成xml格式的数据
	 * @param list
	 * @return
	 * @throws GeneralException 
	 * @author haosl
	 */
	private String buildXML(List<MorphDynaBean> list) throws GeneralException{
		try {
			Element root = new Element("params");  
			Document doc = new Document(root); 
			 for(int i=0;i<list.size();i++){
				 HashMap map = PubFunc.DynaBean2Map(list.get(i));
				 String id = (String) map.get("id");
				 if(StringUtils.isNotBlank(id)){
					Element el = new Element("type");
					el.setAttribute("value", id);
					String pre = (String)map.get(id+"pre");
					String now = (String)map.get(id+"now");
					String cycle =(String)map.get(id+"cycle");
					if(StringUtils.isNotEmpty(pre))
						el.setAttribute("pre", pre);
					if(StringUtils.isNotEmpty(now))
						el.setAttribute("now", now);
					if(StringUtils.isNotEmpty(cycle))
						el.setAttribute("cycle", cycle);
					root.addContent(el);
				 }else{
					 continue;
				 }
			}
			//设置xml字体编码，然后输出为字符串
			Format format=Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter output = new XMLOutputter(format);
		    return output.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}
	/**
	 * 解析xml数据</br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;如果数据库中没有已配置的方案，就向数据库插入默认的一套方案，
	 * 并且该方法返回默认方案的数据。
	 * </br>&nbsp;&nbsp;&nbsp;&nbsp;
	 * 如果配置过方案，则返回方案的数据
	 * @return Document
	 * @throws GeneralException
	 */
	private Document praseXml2Doc() throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String xml = "";
		StringReader reader = null;
		try {
			//
			String defaultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
					+ " <params><type value=\"p0\" pre=\"30\" now=\"30\" />"
							+ "<type value=\"s0\" pre=\"15\" now=\"30\" />"
							+ "<type value=\"p2\" pre=\"15\" now=\"15\" cycle=\"1,2,3,4\" />"
						    + "<type value=\"s2\" pre=\"15\" now=\"15\" cycle=\"1,2,3,4\" />"
						    + "<type value=\"p3\" pre=\"5\" now=\"5\" cycle=\"1,2,3,4,5,6,7,8,9,10,11,12\" />"
						    + "<type value=\"s3\" pre=\"5\" now=\"5\" cycle=\"1,2,3,4,5,6,7,8,9,10,11,12\" />"
						    + "<type value=\"p4\" pre=\"3\" now=\"1\" /><type value=\"s4\" pre=\"3\" now=\"1\" />"
					+ "</params>  ";
			
			String sql = "select Str_value from Constant where Constant = 'OKR_SECTION_CONFIG'";
			rs = dao.search(sql);
			if(rs!=null && rs.next()){//有记录
				xml = rs.getString("Str_value");
				if(StringUtils.isEmpty(xml))
					xml = defaultXml;
				//解析成doc对象
			}else{
				xml = defaultXml;
				//设置一套默认：年、季度、月、周
				String insertSql = "insert into Constant(constant,type,describe,str_value) values(?,?,?,?)";
				List values = new ArrayList();
				values.add("OKR_SECTION_CONFIG");
				values.add("A");
				values.add("工作计划区间设置参数");
				values.add(xml);
				dao.insert(insertSql, values);
			}	
			//xus 20/4/23 xml 编码改造
			Document doc = PubFunc.generateDom(xml);
			//构建Document对象
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 保存配置方案
	 * @param xml
	 * @throws GeneralException 
	 */
	public void saveXml(List<MorphDynaBean> list) throws GeneralException  {
		boolean succeed = false;
		ContentDAO dao  = null;
		RowSet rs = null;
		try {
			String xml = this.buildXML(list);
			dao = new ContentDAO(this.conn);
			String sql = "select * from Constant where Constant='OKR_SECTION_CONFIG'";
			rs = dao.search(sql);
			List<String> values = new ArrayList<String>();
			if(StringUtils.isEmpty(xml))
				return;
			if(rs.next()){
				sql = "update Constant set str_value=? where Constant='OKR_SECTION_CONFIG'";
				values.add(xml);
				dao.update(sql, values);
			}else{
				sql = "insert into constant (constant,type,describe,str_value) values(?,?,?,?)";
				values.add("OKR_SECTION_CONFIG");
				values.add("A");
				values.add("工作计划区间设置参数");
				values.add(xml);
				dao.insert(sql, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 解析xml数据并返回list数据
	 * @return
	 * @throws GeneralException 
	 */
	public List<HashMap<String,HashMap<String,String>>> getXmlData() throws GeneralException{
		
		List<HashMap<String,HashMap<String,String>>> list = new ArrayList<HashMap<String,HashMap<String,String>>>();
		
		try {
			//构建Document对象
			Document doc = this.praseXml2Doc();
			//获得root节点
			if(doc==null)
				return list;//返回空集合
			Element root  = doc.getRootElement();
			List<Element> typeElList = root.getChildren("type");
			
			for(Element typeEl : typeElList){
				HashMap<String,HashMap<String,String>> hm = null;
				String value = typeEl.getAttributeValue("value");
				if(StringUtils.isNotEmpty(value)){
					hm = new HashMap<String, HashMap<String,String>>();
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("value",value);
					String pre = typeEl.getAttributeValue("pre");
					if(StringUtils.isNotEmpty(pre))
						map.put("pre", pre);
					String now = typeEl.getAttributeValue("now");
					if(StringUtils.isNotEmpty(now))
						map.put("now", now);
					String cycle = typeEl.getAttributeValue("cycle");
					if(StringUtils.isNotEmpty(cycle))
						map.put("cycle", cycle);
					if(map!=null && !map.isEmpty())
						hm.put(value, map);
				}
					
				if(hm!=null && !hm.isEmpty())
					list.add(hm);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		}
		return list;
	}
	/**
	 * 清空设置
	 * @throws GeneralException 
	 */
	public void cancelSetting() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "delete from Constant where Constant = 'OKR_SECTION_CONFIG'";
			dao.delete(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}
}

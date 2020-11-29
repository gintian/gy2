/*
 * Created on 2010-02-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:PoliceSetDateTrans
 * </p>
 * <p>
 * Description:设置周期
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-2-7
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class PoliceSetDateTrans extends IBusiness {

	public void execute() throws GeneralException {

		Map map = (Map) this.getFormHM().get("requestPamaHM");

		// 狱情动态的周期，0为年，1为月，2为季度
		String news = (String) this.getFormHM().get("news");
		// 部门工作任务书的周期，0为年，1为月，2为季度
		String orgtask = (String) this.getFormHM().get("orgtask");
		// 个人工作任务书的周期，0为年，1为月，2为季度
		String persontask = (String) this.getFormHM().get("persontask");

		// 执行保存操作还是查询操作,默认为查询
		String save = (String) map.get("save");
		String fromFlag = (String) map.get("flag");  //kh:来自北京市监狱局平台调用
		map.remove("save"); 
		if (save == null || save.trim().length() == 0) {
			save = "search";
		}
		
		if(fromFlag==null||fromFlag.trim().length()==0)
			fromFlag="";
		//查询信息
		if ("search".equals(save)) {
			if (this.isExist("JYZY_CYCLE_PARAM")) {
				String str_value = this.selectStr_Value("JYZY_CYCLE_PARAM");
				analysis(str_value);
			} else {
				//设置默认数据
				this.optiondata("JYZY_CYCLE_PARAM", "1", "1", "1");
				//解析xml，查询
				String str_value = this.selectStr_Value("JYZY_CYCLE_PARAM");
				analysis(str_value);
			}
		}
		
		//保存设置信息
		if ("save".equals(save)) {
			this.optiondata("JYZY_CYCLE_PARAM", news, orgtask, persontask);
		}
		this.getFormHM().put("fromFlag",fromFlag);

	}

	/**
	 * 查询周期的值
	 * 
	 * @param constant
	 * @return
	 */
	private String selectStr_Value(String constant) {
		// 查询所获得字符窜
		String str_value = "";

		if (this.isExist(constant)) {
			// sql语句
			StringBuffer sql = new StringBuffer();
			sql.append("select Str_Value from constant where constant='");
			sql.append(constant);
			sql.append("'");

			// 查询操作
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) {
					str_value = frowset.getString("Str_Value");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str_value;
	}

	/**
	 * 判断常量是否存在
	 * @param constant 常量名称
	 * @return
	 */
	private boolean isExist(String constant) {

		// 是否存在该常量
		boolean flag = false;

		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("select * from constant where constant='");
		sql.append(constant);
		sql.append("'");

		// 查询操作
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				flag = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	/**
	 * 解析xml文件
	 * @param str_value
	 * @return
	 */
	private boolean analysis(String str_value) {
		//是否解析成功
		boolean flag = true;
		//初始化xml
		try {
			Document doc = PubFunc.generateDom(str_value);
			String xpath = "/param/data";
			// 取得子集结点
			XPath reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator it = childlist.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				this.getFormHM().put(el.getAttributeValue("name"), el.getAttributeValue("cycle"));
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 操作数据，有该常量时修改，没有该常量时插入
	 * @param constant
	 * @return
	 */
	private boolean optiondata (String constant, String yqdt, String dept, String employ) {
		//操作是否成功
		boolean flag = true;
		//xml文件内容
		StringBuffer buff = new StringBuffer();
		buff.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buff.append("<param>");
		buff.append("<data name=\"yqdt\" cycle=\"");
		buff.append(yqdt);
		buff.append("\"/>");
		buff.append("<data name=\"dept\" cycle=\"");
		buff.append(dept);
		buff.append("\"/><data name=\"employ\" cycle=\"");
		buff.append(employ);
		buff.append("\"/></param>");
		
		RecordVo vo = new RecordVo("constant");
		vo.setString("constant", constant);
		vo.setString("describe", "监狱干警职业周期设置");
		vo.setString("str_value", buff.toString());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if (this.isExist(constant)) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
		return flag;
		
	}
}

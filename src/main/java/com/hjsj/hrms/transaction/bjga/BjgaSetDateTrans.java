package com.hjsj.hrms.transaction.bjga;

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

public class BjgaSetDateTrans extends IBusiness {

	public void execute() throws GeneralException {

		Map map = (Map) this.getFormHM().get("requestPamaHM");	
		// 执行保存操作还是查询操作,默认为查询
		String save = (String) map.get("save");
		map.remove("save");
		if (save == null || save.trim().length() == 0) {
			save = "search";
		}
		
		//查询信息
		if ("search".equals(save)) {
			if (this.isExist("BJGA_CYCLE_PARAM")) {
				String str_value = this.selectStr_Value("BJGA_CYCLE_PARAM");
				analysis(str_value);
			} else {
				//设置默认数据
				this.optiondata("BJGA_CYCLE_PARAM", "1", "1", "1");
				//解析xml，查询
				String str_value = this.selectStr_Value("BJGA_CYCLE_PARAM");
				analysis(str_value);
			}
		}
		
		//保存设置信息
		if ("save".equals(save)) {
			String year=(String)this.getFormHM().get("task_cly_year");
			String quar=(String)this.getFormHM().get("task_cly_quar");
			String moth=(String)this.getFormHM().get("task_cly_moth");
			this.optiondata("BJGA_CYCLE_PARAM", year, quar, moth);
		}

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
		String year="";
		String moth="";
		String quar="";
		try {
			Document doc = PubFunc.generateDom(str_value);
			String xpath = "/param/data";
			// 取得子集结点
			XPath reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator it = childlist.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if(el.getAttributeValue("name")!=null&& "dept".equalsIgnoreCase(el.getAttributeValue("name")))
				{
					year=el.getAttributeValue("year");
					moth=el.getAttributeValue("moth");
					quar=el.getAttributeValue("quar");
				}				
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		if(year!=null&& "1".equals(year))
		{
			this.getFormHM().put("task_cly_year", "1");
		}else{
			this.getFormHM().put("task_cly_year", "0");
		}
		if(quar!=null&& "1".equals(quar))
		{
			this.getFormHM().put("task_cly_quar", "1");
		}else{
			this.getFormHM().put("task_cly_quar", "0");
		}
		if(moth!=null&& "1".equals(moth))
		{
			this.getFormHM().put("task_cly_moth", "1");
		}else{
			this.getFormHM().put("task_cly_moth", "0");
		}
		
		return flag;
	}
	
	/**
	 * 操作数据，有该常量时修改，没有该常量时插入
	 * @param constant
	 * @return
	 */
	private boolean optiondata (String constant, String year, String quar, String moth) {
		//操作是否成功
		boolean flag = true;
		//xml文件内容
		StringBuffer buff = new StringBuffer();
		buff.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buff.append("<param>");
		buff.append("<data name=\"dept\" year=\""+year+"\"");
		buff.append(" quar=\""+quar+"\"");		
		buff.append(" moth=\""+moth+"\"");	
		buff.append("/></param>");
		
		RecordVo vo = new RecordVo("constant");
		vo.setString("constant", constant);
		vo.setString("describe", "北京公安周期设置");
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
	 private boolean haveTheCycle(String str,String id)
	    {
	    	if(str.indexOf(","+id+",")==-1)
	    		return false;
	    	else
	    		return true;
	    }
}

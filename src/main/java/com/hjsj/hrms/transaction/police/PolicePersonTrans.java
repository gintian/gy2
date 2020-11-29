package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>
 * Title:PoliceYqdtTrans
 * </p>
 * <p>
 * Description:个人工作任务书
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * Create time:2010-2-8
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 */
public class PolicePersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//获得周期值
		String cycle = this.querryCycleValue("employ");
		//获得人员库
		String a0100 = this.userView.getA0100();
		if (a0100 == null || a0100.trim().length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("非自助用户！"));
		}
		String userbase = (String) this.getFormHM().get("userbase");
		if (userbase == null || userbase.trim().length() <= 0) {
			userbase = this.userView.getDbname();
		}
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("userbase", userbase);
		this.getFormHM().put("cycle", cycle);
		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("select i9999,title,ext,createtime from ");
		sql.append(userbase);
		sql.append("A00 where a0100='");
		sql.append(a0100);
		sql.append("' and flag='t' ");

		// 获得周期参数,0为按年，1为按月，2为按季度
		if ("0".equals(cycle)) {
			String taskyear = (String) this.getFormHM().get("taskyear");

			// 默认当前时间的年份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("yearlist", this.getYearList(userbase,a0100));

			// 该年份的范围
			String end = String.valueOf(Integer.parseInt(taskyear) + 1);
			sql.append(" and createtime >= ");
			sql.append(Sql_switcher.dateValue(taskyear));
			sql.append(" and createtime<" + Sql_switcher.dateValue(end));

		} else if ("1".equals(cycle)) {
			
			// 获得年份和月份
			String taskyear = (String) this.getFormHM().get("taskyear");
			String taskmonth = (String) this.getFormHM().get("taskmonth");
			
			// 初始值为当前时间的年份和月份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskmonth == null || "".equalsIgnoreCase(taskmonth)) {
				taskmonth = String.valueOf(new Date().getMonth() + 1);
			}
			
			// 获得时间范围
			String start = taskyear + "-" + taskmonth + "-" + "01";
			WeekUtils util = new WeekUtils();
			String end = util.lastMonthStr(Integer.parseInt(taskyear), Integer
					.parseInt(taskmonth));
			if (Integer.parseInt(taskmonth) == 12) {
				end = String.valueOf(Integer.parseInt(taskyear) + 1) + "-"
						+ "01-01";
			} else {
				end = taskyear + "-"
						+ String.valueOf(Integer.parseInt(taskmonth) + 1)
						+ "-01";
			}
			sql.append(" and createtime >= ");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and createtime<" + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskmonth", taskmonth);
			this.getFormHM().put("yearlist", this.getYearList(userbase,a0100));
			this.getFormHM().put("monthlist", this.getMonthList());

		} else if ("2".equals(cycle)) {
			// 获得年份和季度
			String taskyear = (String) this.getFormHM().get("taskyear");
			String taskweek = (String) this.getFormHM().get("taskweek");
			
			// 默认为当前时间的年份和季度
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskweek == null || "".equalsIgnoreCase(taskweek)) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(new Date());
				int month = ca.get(Calendar.MONTH) + 1;
				if (month % 3 != 0) {
					taskweek = String.valueOf(month / 3 + 1);
				} else {
					taskweek = String.valueOf(month / 3);
				}
			}
			
			// 时间范围
			String start = taskyear + "-"
					+ (Integer.parseInt(taskweek) * 3 - 2) + "-01";
			String end = "";
			if (Integer.parseInt(taskweek) == 4) {
				end = (Integer.parseInt(taskyear) + 1) + "-01-01";
			} else {
				end = taskyear + "-"
						+ ((Integer.parseInt(taskweek) + 1) * 3 - 2) + "-01";
			}
			sql.append(" and createtime >= ");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and createtime<" + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskweek", taskweek);
			this.getFormHM().put("yearlist", this.getYearList(userbase,a0100));
			this.getFormHM().put("weeklist",
					this.getWeekList());

		}

		this.getFormHM().put("column",
				"i9999,ext,createtime,title");
		this.getFormHM().put("sqlstr", sql.toString());
		this.getFormHM().put("order_by",
				" order by i9999");

	}

	/**
	 * 获得年列表
	 * 
	 * @param id
	 * @return
	 */
	private List getYearList(String username,String a0100) {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select createtime from ");
		sql.append(username);
		sql.append("A00 where a0100='");
		sql.append(a0100);
		sql.append("' and flag='t'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			int minyear = Integer.parseInt(PubFunc.FormatDate(new Date(),
					"yyyy"));
			if (this.frowset.next()) {
				int temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
						.getDate("createtime"), "yyyy"));
				minyear = minyear > temp ? temp : minyear;
				while (this.frowset.next()) {
					temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
							.getDate("createtime"), "yyyy"));
					minyear = minyear > temp ? temp : minyear;
				}
				int nowYear = Integer.parseInt(PubFunc.FormatDate(new Date(),
						"yyyy"));
				for (int i = nowYear; i >= minyear; i--) {
					CommonData data = new CommonData(String.valueOf(i), String
							.valueOf(i));
					list.add(data);
				}

			} else {
				CommonData data = new CommonData(PubFunc.FormatDate(new Date(),
						"yyyy"), PubFunc.FormatDate(new Date(), "yyyy"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得月列表
	 * 
	 * @param id
	 * @return
	 */
	private List getMonthList() {
		List list = new ArrayList();
		for (int i = 1; i < 13; i++) {
			CommonData data = new CommonData(String.valueOf(i), String
					.valueOf(i));
			list.add(data);
		}
		return list;
	}

	/**
	 * 获得季度列表
	 * 
	 * @return
	 */
	private List getWeekList() {
		List list = new ArrayList();
		String[] week = { "一", "二", "三", "四" };
		for (int i = 1; i <= 4; i++) {
			CommonData data = new CommonData(String.valueOf(i), "第"
					+ week[i - 1] + "季度");
			list.add(data);
		}

		return list;
	}
	
	/**
	 * 查询所需要的周期值
	 * @param name
	 * @return
	 */
	private String querryCycleValue(String name) throws GeneralException {
		String cycle = "";
		if (this.isExist("JYZY_CYCLE_PARAM")) {
			String str_value = this.selectStr_Value("JYZY_CYCLE_PARAM");
			this.analysis(str_value);
			cycle = (String) this.getFormHM().get(name);
		} else {
			throw GeneralExceptionHandler.Handle(new GeneralException("未设置周期！请首先设置周期"));
		}
		return cycle;
		
	}
	
	/**
	 * 查询周期的值
	 * 
	 * @param constant
	 * @return
	 */
	public String selectStr_Value(String constant) {
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

}

package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.regothersys.SysRegLogger;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SOAP请求请假相关的Bo类
 *  getHolidayMsg	//获取年假已休、可休天数
 *  updateHolidays	//更新年假天数
 * @author cuibl
 *
 */
public class HttpHolidayBo {

	public HttpHolidayBo(SysRegLogger regLogger) {
		super();
		this.regLogger = regLogger;
	}
	/**
	 * 认证系统日志
	 */
	private SysRegLogger regLogger;
	
	/**
	 * 日志对象,系统
	 */
	private Category log = Category.getInstance(getClass().getName());
	
	
	/**
	 * 获取年假已休、可休天数
	 * @param etoken 认证码
	 * @param jsonStr json格式的数据
	 * @return json格式的数据其中包含年假假期（可休、已休天数）
	 */
	public String getHolidayMsg(String etoken, String jsonStr) {
		String result = "";
		Connection conn = null;
		String info = "1"; // 要返回的异常信息
		double useddays = 0.0; // 已休天数
		double remaindays = 0.0; // 可休天数
		String nbase = null;
		String A0100 = null; // 人员编号
		String userid = null;
		String htype = null;
		String StrHdate = null;
		java.sql.Date hdate = null;
		JSONObject json= new JSONObject();
		try {
			JSONObject objJson = JSONObject.fromObject(jsonStr);
			userid = objJson.get("userid").toString();
			htype = objJson.get("htype").toString();
			StrHdate = objJson.get("hdate").toString().replace('.', '-');
			
			if (userid == null || userid == "" || htype == null || htype == ""
					|| StrHdate == null || StrHdate == "") {
				regLogger.start("传入的值格式不正确");
				return returnJsonStr("0","传入的值格式不正确");
			}
		} catch (Exception e) {
			log.error("获取年假已休、可休天数解析json出错:"+e);
			regLogger.start("传入的json格式错误!");
			return returnJsonStr("0","传入的json格式错误!");
		}
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			hdate = new Date(df.parse(StrHdate).getTime());
			// 抛异常就不是正确格式
			if (!StrHdate.equals(df.format(df.parse(StrHdate)))) {
				regLogger.start("传入的请假日期格式不正确");
				return returnJsonStr("0","传入的请假日期格式不正确");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			log.error("获取年假已休、可休天数解析时间参数出错:"+e2);
			regLogger.start("传入的请假日期格式不正确");
			return returnJsonStr("0","传入的请假日期格式不正确");
		}
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			ConstantXml csXML = new ConstantXml(conn, "SYS_OTH_PARAM");
			org.jdom.Element e = csXML.getElement("/param/chk_uniqueness/field[@type='0']");
			String valid = e.getAttributeValue("valid");
			String name = e.getAttributeValue("name");
			if (!"1".equals(valid)) {
				regLogger.start("无法查找对应人员！人力资源系统未设置人员唯一性指标");
				return returnJsonStr("0","无法查找对应人员！人力资源系统未设置人员唯一性指标");
			}
	
			HashMap hm = selectHtype(conn, dao, htype);
			String codeItemId = (String) hm.get("codeitemid");
			if (codeItemId == null) {
				regLogger.start("没有该请假类型");
				return returnJsonStr("0","没有该请假类型");
			}
	
			String[] dbName = getKqDbName(conn);
			if (dbName.length == 0) {
				regLogger.start("当前人力资源系统未设置考勤人员库！无法查询");
				return returnJsonStr("0","当前人力资源系统未设置考勤人员库！无法查询");
			}
	
			ArrayList emplist = null;
			emplist = getA0100ByUseridAndDbName(dao, name, userid, dbName);
	
			if (0 == emplist.size()) {
				regLogger.start("没有该人员信息");
				return returnJsonStr("0","没有该人员信息");
			}
	
			nbase = (String) emplist.get(0);
			A0100 = (String) emplist.get(1);
	
			hm = getQueryDaysInfo(conn, (String) hm.get("querytype"), nbase,
					A0100, codeItemId, hdate);
			if (hm != null && !hm.isEmpty()) {
				useddays = ((Double) hm.get("useddays")).doubleValue();
				remaindays = ((Double) hm.get("remaindays")).doubleValue();
			}
			if (useddays >= 0) {
				json.put("useddays", useddays);
			}
			if (remaindays >= 0) {
				json.put("remaindays", remaindays);
			}
		} catch (Exception e) {
			e.printStackTrace();
			info = "0";
		} finally {
			PubFunc.closeDbObj(conn);
		}
		result = returnJsonStr(info,json.toString());
		return result;
	}

	/**
	 * 更新年假天数
	 * @param etoken 认证码
	 * @param jsonStr json格式的数据
	 * @return 
	 */
	public String updateHolidays(String etoken, String jsonStr) {
		String info = "2";
		String result = "";
		Connection conn = null;
		RowSet rs = null;
		String userid = null, htype = null, hdays = null;
		String nbase = null, A0100 = null, start = "", end = "";
		String userField = "";
		
		try {
			JSONObject objJson = JSONObject.fromObject(jsonStr);
			userid = objJson.get("userid").toString();
			htype = objJson.get("htype").toString();
			hdays = objJson.get("hdays").toString();
			start = objJson.get("sdate").toString();
			end = objJson.get("edate").toString();
			
			if (userid == null || userid == "" || htype == null || htype == ""
					|| start == null || start.length() <= 0 || end == null
					|| end.length() <= 0) {
				regLogger.start("传入的值格式不正确");
				return returnJsonStr("0","传入的值格式不正确");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			regLogger.start("传入的josn格式有问题!");
			return returnJsonStr("0","传入的json格式有问题!");
		}
		try {
			DateUtils.getDate(start, "yyyy-MM-dd HH:mm:ss");
			DateUtils.getDate(end, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e2) {
			e2.printStackTrace();
			regLogger.start("传入的日期格式不正确!");
			return returnJsonStr("0","传入的日期格式不正确!");
		}

		try {
			userField = SystemConfig.getPropertyValue("interface_holiday_uniqfile");
			if (userField == null || userField.length() <= 0) {
				regLogger.start("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
				return returnJsonStr("0","在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
			}
		} catch (Exception e) {
			e.printStackTrace();
			regLogger.start("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
			return returnJsonStr("0","在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
		}
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String querytype = "0";
			HashMap hm = selectHtype(conn, dao, htype);
			querytype = (String) hm.get("querytype");
			if ("1".equals(querytype)) {
				return syncHolidayMsg(jsonStr);
			}
			String sels = getKqItem(htype, conn);
			if (sels == null || sels.length() <= 0) {
				regLogger.start("没有该请假类型!");
				return returnJsonStr("0","没有该请假类型!");
			}
			String[] dbName = getKqDbName(conn);
			if (dbName.length == 0) {
				regLogger.start("当前人力资源系统未设置考勤人员库！无法查询");
				return returnJsonStr("0","当前人力资源系统未设置考勤人员库！无法查询");
			}
			ArrayList emplist = null;
			emplist = getA0100ByUseridAndDbName(dao, userField, userid, dbName);
			if (0 == emplist.size()) {
				regLogger.start("没有该人员信息");
				return returnJsonStr("0","没有该人员信息");
			}
			nbase = (String) emplist.get(0);
			A0100 = (String) emplist.get(1);
			String b0110 = (String) emplist.get(2);
			try {
				if (Double.parseDouble(hdays) <= 0) {
					regLogger.start("请假天数必须大于0!");
					return returnJsonStr("0","请假天数必须大于0!");
				}
			} catch (Exception w) {
				w.printStackTrace();
				log.error("更新年假天数解析double出现问题:"+w);
				return returnJsonStr("0","更新年假天数解析double出现问题");
			}
			// 处理假期管理里面的数据
			AnnualApply annualApply = new AnnualApply(null, conn);
			float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则
			HashMap holidayTypeMap = new HashMap();
			ArrayList updateSqlList = new ArrayList();
			if (isHoliday(conn, holidayTypeMap, b0110, sels)) {
				HashMap kqItem_hash = annualApply.count_Leave(sels);
				java.sql.Date kq_start = DateUtils.getSqlDate(start,"yyyy-MM-dd HH:mm:ss");
				java.sql.Date kq_end = DateUtils.getSqlDate(end,"yyyy-MM-dd HH:mm:ss");
				String starts = DateUtils.format(kq_start,"yyyy.MM.dd HH:mm:ss");
				String ends = DateUtils.format(kq_end, "yyyy.MM.dd HH:mm:ss");
				float leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, A0100, nbase,b0110, kqItem_hash, holiday_rules);
				String history = annualApply.upLeaveManage(A0100, nbase, sels,starts, ends, leave_tiem, "1", b0110, kqItem_hash,holiday_rules);
				String sqlStart = "";
				String sqlEnd = "";
				if (Sql_switcher.searchDbServer() == 1) {
					sqlStart = "cast('" + start + "' as datetime)";
					sqlEnd = "cast('" + end + "' as datetime)";
				} else {
					sqlStart = "to_date('" + start + "','yyyy-MM-dd HH:mm:ss')";
					sqlEnd = "to_date('" + end + "', 'yyyy-MM-dd HH:mm:ss')";
				}
				String updateSql = "update q15 set history='" + history
						+ "' where a0100='" + A0100 + "'and upper(nbase)='"
						+ nbase.toUpperCase() + "' and q1503='" + sels
						+ "' and q15z1=" + sqlStart + " and  q15z3=" + sqlEnd;
				dao.update(updateSql);
			}
		} catch (Exception e) {
			info = "0";
			if(e instanceof GeneralException) {
				GeneralException general = (GeneralException) e;
				result = general.getErrorDescription();
			}else {
				result = e.toString();
			}
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return returnJsonStr(info,result);
	}
	
	
	/**
	 * 更新eHR系统假期表
	 * @param jsonStr
	 * @return
	 */
	private String syncHolidayMsg(String jsonStr) {
		Connection conn = null;
		double day = 0.0;// 可休天数
		String userid = null, htype = null, hdays = null, strHdate = null;
		String nbase = null, A0100 = null;
		Date hdate = null;
		try {
			JSONObject objJson = JSONObject.fromObject(jsonStr);
			userid = objJson.get("userid").toString();
			htype = objJson.get("htype").toString();
			hdays = objJson.get("hdays").toString();
			strHdate = objJson.get("hdate").toString().replace('.', '-');
	
			if (userid == null || userid == "" || htype == null || htype == ""
					|| hdays == null || hdays == "") {
				regLogger.start("传入的值格式不正确");
				return returnJsonStr("0","传入的值格式不正确");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			regLogger.start("传入的json格式有问题!");
			return returnJsonStr("0","传入的json格式有问题!");
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			hdate = new Date(df.parse(strHdate).getTime());
			if (!strHdate.equals(df.format(df.parse(strHdate)))) {// 抛异常就不是正确格式
				regLogger.start("传入的请假日期格式不正确");
				return returnJsonStr("0","传入的请假日期格式不正确");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			regLogger.start("传入的请假日期格式不正确");
			return returnJsonStr("0","传入的请假日期格式不正确");
		}
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			ConstantXml csXML = new ConstantXml(conn, "SYS_OTH_PARAM");
			org.jdom.Element e = csXML.getElement("/param/chk_uniqueness/field[@type='0']");
			String valid = e.getAttributeValue("valid");
			String name = e.getAttributeValue("name");
	
			if (!"1".equals(valid)) {
				regLogger.start("无法查找对应人员！人力资源系统未设置人员唯一性指标。");
				return returnJsonStr("0","无法查找对应人员！人力资源系统未设置人员唯一性指标。");
			}
	
			HashMap hm = selectHtype(conn, dao, htype);
			String codeItemId = (String) hm.get("codeitemid");
			if (codeItemId == null) {
				regLogger.start("没有该请假类型!");
				return returnJsonStr("0","没有该请假类型!");
			}
	
			String[] dbName = getKqDbName(conn);
			if (dbName.length == 0) {
				regLogger.start("当前人力资源系统未设置考勤人员库！无法查询。");
				return returnJsonStr("0","当前人力资源系统未设置考勤人员库！无法查询。");
			}
			ArrayList emplist = null;
			emplist = getA0100ByUseridAndDbName(dao, name, userid, dbName);
			if (0 == emplist.size()) {
				regLogger.start("没有该人员信息");
				return returnJsonStr("0","没有该人员信息");
			}
			nbase = (String) emplist.get(0);
			A0100 = (String) emplist.get(1);
			String queryType = (String) hm.get("querytype");
			try {
				if (Double.parseDouble(hdays) <= 0) {
					regLogger.start("请假天数必须大于0!");
					return returnJsonStr("0","请假天数必须大于0!");
				}
				// 得到当前剩余天数，检查天数是否够用
				HashMap hmDays = getQueryDaysInfo(conn, queryType, nbase,A0100, codeItemId, hdate);
				day = ((Double) hmDays.get("remaindays")).doubleValue();
				if (Double.parseDouble(hdays) > day) {
					regLogger.start("请假天数超过可休天数!");
					return returnJsonStr("0","请假天数超过可休天数!");
				}
			} catch (Exception d) {
				regLogger.start("传入的请假天数格式不正确!");
				return returnJsonStr("0","传入的请假天数格式不正确!");
			}
	
			nbase = (String) emplist.get(0);
			A0100 = (String) emplist.get(1);
			if (!updatedays(conn, queryType, nbase, A0100, hdate, hdays,codeItemId)) {
				regLogger.start("扣减假期天数失败!");
				return returnJsonStr("0","扣减假期天数失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnJsonStr("0","false");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		return returnJsonStr("2","");
	}

	/**
	 * 根据请假类型名称获得请假类型编码
	 * @param htype
	 * @return
	 */
	private HashMap selectHtype(Connection conn, ContentDAO dao, String htype) {
		HashMap hm = new HashMap();
		RowSet rs = null;
		try {
			ArrayList list = new ArrayList();
			list.add(htype);
			String sql = "select codeitemid from codeitem where codeitemdesc=?";
			rs = dao.search(sql, list);
			if (rs.next()) {
				String codeItemId = rs.getString(1);
				// 存在要求的请假类型，检查是否为假期管理假类，或调休假
				KqParam kqParam = KqParam.getInstance();
				String holidayTypes = "," + kqParam.getHolidayTypes(conn, new UserView("su", conn)) + ",";
				if (holidayTypes.contains("," + codeItemId + ","))
					hm.put("querytype", "0");
				else {
					String leaveTimeTypeUsedOverTime = kqParam.getLeaveTimeTypeUsedOverTime();
					if (leaveTimeTypeUsedOverTime.equalsIgnoreCase(codeItemId))
						hm.put("querytype", "1");
					else
						hm.put("querytype", "");
				}
				hm.put("codeitemid", rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("根据请假类型名称获得请假类型编码出错:");
			log.error(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return hm;
	}

	/**
	 * 根据人员标示、标示值和人员库获得人员编号
	 * @param name 唯一标示
	 * @param userid 标示值
	 * @param dbName 人员库
	 * @return 人员库，人员编号
	 */
	private ArrayList getA0100ByUseridAndDbName(ContentDAO dao, String name, String userid, String[] dbName) {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		String A0100 = null;
		try {
			StringBuffer sql = new StringBuffer();
			for (int i = 0; i < dbName.length; i++) {
				sql.append("select A0100,'");
				sql.append(dbName[i]);
				sql.append("' as nbase,b0110 ");
				sql.append(" from ");
				sql.append(dbName[i]);
				sql.append("A01 where ");
				sql.append(name);
				sql.append("='");
				sql.append(userid);
				sql.append("'");
				if (dbName.length > 1 && i < dbName.length - 1)
					sql.append(" union all ");
			}
			rs = dao.search(sql.toString());
			if (rs.next()) {
				A0100 = rs.getString(1);
				String b0110 = rs.getString("b0110");
				b0110 = b0110 == null ? "" : b0110;
				list.add(rs.getString(2));
				list.add(A0100);
				list.add(b0110);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("根据人员标示、标示值和人员库获得人员编号出错:");
			log.error(e);
		}
		return list;
	}

	/**
	 * 获得当前设置的考勤库
	 * @return 人员库
	 * @throws GeneralException
	 */
	private String[] getKqDbName(Connection conn) {
		// TODO 暂时取第一个考勤人员库参数设置,有多单位设置不同时,会有问题
		ConstantXml csXML = new ConstantXml(conn, "KQ_PARAMETER");
		org.jdom.Element e = csXML.getElement("/kq/parameter[1]/nbase");
		return e.getAttributeValue("value").split(",");
	}

	/**
	 * 得到查询的天数信息（某个年假、调休假等的已休和可休天数）
	 * @param conn
	 * @param queryType
	 * @param nbase
	 * @param A0100
	 * @param codeItemId
	 * @param hdate
	 * @return
	 */
	private HashMap getQueryDaysInfo(Connection conn, String queryType, String nbase, String A0100, String codeItemId,Date hdate) {
		HashMap hm = null;
		// 组装请求数据
		HashMap queryInfo = new HashMap();
		queryInfo.put("nbase", nbase);
		queryInfo.put("a0100", A0100);
		queryInfo.put("codeitemid", codeItemId);
		queryInfo.put("hdate", hdate);
		if ("0".equals(queryType)) {
			ContentDAO dao = new ContentDAO(conn);
			hm = getHolidayDays(dao, queryInfo);
		} else if ("1".equals(queryType))
			hm = getOverTimeToRestDays(conn, queryInfo);
		return hm;
	}

	/**
	 * 获取某人某假期天数信息
	 * @param dao
	 * @param queryInfo
	 * @return
	 */
	private HashMap getHolidayDays(ContentDAO dao, HashMap queryInfo) {
		HashMap hm = new HashMap();
		RowSet rs = null;
		try {
			String sql = "select Q1705,Q1707 from Q17 where nbase=? and A0100=? and Q1709=? and ? between Q17z1 and Q17z3";
			ArrayList list = new ArrayList();
			list.add((String) queryInfo.get("nbase"));
			list.add((String) queryInfo.get("a0100"));
			list.add((String) queryInfo.get("codeitemid"));
			list.add((Date) queryInfo.get("hdate"));
			rs = dao.search(sql, list);
			if (rs.next()) {
				double useddays = rs.getDouble(1) >= 0 ? rs.getDouble(1) : 0.0;
				double remaindays = rs.getDouble(2) >= 0 ? rs.getDouble(2) : 0.0;
				hm.put("useddays", Double.valueOf(useddays));
				hm.put("remaindays", Double.valueOf(remaindays));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("获取某人某假期天数信息出错:");
			log.error(e1);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return hm;
	}

	/**
	 * 获取某人调休天数信息
	 * @param conn
	 * @param queryInfo
	 * @return
	 */
	private HashMap getOverTimeToRestDays(Connection conn, HashMap queryInfo) {
		HashMap hm = new HashMap();
		GetValiateEndDate gve = new GetValiateEndDate(null, conn);
		int usableTime = gve.getTimesCount(new java.util.Date(), (String) queryInfo.get("nbase"),
				(String) queryInfo.get("a0100"), conn);
		// 将分钟数据转换成天（1天=8小时）
		double usableDays = usableTime / 480.0;
		hm.put("useddays", Double.valueOf(usableDays));
		hm.put("remaindays", Double.valueOf(usableDays));
		return hm;
	}
	
	/**
	 * 
	 * @param conn
	 * @param holidayTypeMap
	 * @param b0110
	 * @param leaveTypeId
	 * @return
	 */
	private boolean isHoliday(Connection conn, HashMap holidayTypeMap, String b0110, String leaveTypeId) {
	    String holiday_type = "";
	    if (holidayTypeMap.containsKey(b0110)) {
            holiday_type = (String) holidayTypeMap.get(b0110);
        } else {
            holiday_type = KqParam.getInstance().getHolidayTypes(conn, b0110);
            holidayTypeMap.put(b0110, holiday_type);
        }
        return ("," + holiday_type + ",").indexOf("," + leaveTypeId + ",") != -1;
	}
	
	/**
	 * 修改请假天数
	 * @param hdate 请假时间
	 * @param A0100  请假人编号
	 * @param field 请假类型编号
	 * @return 请假是否成功
	 */
	private boolean updatedays(Connection conn, String queryType, String nbase,
			String A0100, Date hdate, String hdays, String codeItemId) {
		boolean uptResult = false;
		HashMap updateInfo = new HashMap();
		updateInfo.put("nbase", nbase);
		updateInfo.put("a0100", A0100);
		updateInfo.put("codeitemid", codeItemId);
		updateInfo.put("hdays", hdays);
		updateInfo.put("hdate", hdate);
		if ("0".equals(queryType)) {
			ContentDAO dao = new ContentDAO(conn);
			uptResult = updateRestDays(dao, updateInfo);
		} else if ("1".equals(queryType))
			uptResult = updateOverTimeToRestDays(conn, updateInfo);
		return uptResult;
	}
	
	/**
	 * 更新年假天数
	 * @param dao
	 * @param updateInfo
	 * @return
	 */
	private boolean updateRestDays(ContentDAO dao, HashMap updateInfo) {
		int uptCount = 0;
		try {
			String sql = "update Q17 set Q1707=Q1707-?,Q1705=Q1705+? where nbase=? and  A0100=? and Q1709=? and ? between q17z1 and q17z3";
			ArrayList list = new ArrayList();
			list.add((String) updateInfo.get("hdays"));
			list.add((String) updateInfo.get("hdays"));
			list.add((String) updateInfo.get("nbase"));
			list.add((String) updateInfo.get("a0100"));
			list.add((String) updateInfo.get("codeitemid"));
			list.add((Date) updateInfo.get("hdate"));
			uptCount = dao.update(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("更新年假天数出错:"+e);
		}
		return uptCount > 0;
	}

	/**
	 * 更新调休假天数
	 * @param conn
	 * @param updateInfo
	 * @return
	 */
	private boolean updateOverTimeToRestDays(Connection conn, HashMap updateInfo) {
		boolean uptResult = false;
		int appTime = (int) (Float.parseFloat((String) updateInfo.get("hdays")) * 480);
		UpdateQ33 uptQ33 = new UpdateQ33(null, conn);
		uptResult = uptQ33.upQ33((String) updateInfo.get("nbase"), (String) updateInfo.get("a0100"), appTime);
		return uptResult;
	}
	
	/**
	 * 获取考勤信息
	 * @param item_name
	 * @param conn
	 * @return
	 */
	private String getKqItem(String item_name, Connection conn) {
		RowSet rs = null;
		String item = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select item_id from kq_item where item_name='"+ item_name + "'");
			if (rs.next()) {
				item = rs.getString("item_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return item;
	}
	
	
	/**
	 * 返回json格式的字符串
	 * @param opt 选项值 0:失败,返回错误信息,1:成功,返回数据信息,2:成功(数据同步状态回写成功,但没有数据信息返回)
	 * @param msg 返回的信息
	 */
	private String returnJsonStr(String opt,Object msg) {
	    JSONObject json= new JSONObject();
	    if("0".equals(opt)) {
	    	json.put("flag",opt);
	    	json.put("msg",msg);
	    }else if("1".equals(opt)) {
	    	json.put("flag",opt);
	    	json.put("data",msg);
	    }else if("2".equals(opt)) {
	    	json.put("flag","1");
	    	json.put("msg",msg);
	    }
	    return json.toString();
	}
	
}

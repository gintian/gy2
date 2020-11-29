package com.hjsj.hrms.module.kq.shiftcheck.transaction;

import com.hjsj.hrms.module.kq.config.parameter.businessobject.KqParameterService;
import com.hjsj.hrms.module.kq.config.parameter.businessobject.impl.KqParameterServiceImpl;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.utils.PinyinUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ExportShiftTrans extends IBusiness {
	private String monthScop = "";

	@Override
	public void execute() throws GeneralException {
		try {

			String jsonStr = (String) this.formHM.get("jsonStr");
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
			String type = jsonObj.getString("type");
			ShiftService shift = new ShiftServiceImpl(this.userView, this.frameconn);
			// 参数
			String year = jsonObj.getString("year");
			String month = jsonObj.getString("month");
			if (Integer.valueOf(month) < 10)
				month = "0" + month;

			String schemeId = jsonObj.getString("schemeId");
			String weekIndex = jsonObj.getString("weekIndex");
			String groupId = jsonObj.getString("groupId");
			String dataSql = jsonObj.getString("dataSql");
			groupId = PubFunc.decrypt(groupId);
			if(!"-1".equalsIgnoreCase(schemeId))
				schemeId = PubFunc.decrypt(schemeId);
			// 有序列codesetid
			String column = shift.getShiftcolumns(Integer.valueOf(year), Integer.valueOf(month),
			        Integer.valueOf(weekIndex), "shiftData");
			column = column.replace("[", "").replace("]", "").replace("'", "");
			String[] cloumns = column.split(",");
			// 获取数据
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("year", year);
			paramMap.put("month", month);
			paramMap.put("weekIndex", weekIndex);
			// 59053 显示条数暂时调整为5000
			paramMap.put("limit", "5000");
			paramMap.put("page", "1");
			paramMap.put("groupId", groupId);
			paramMap.put("cloumns", cloumns);
			paramMap.put("dataSql", dataSql);
			paramMap.put("dataType", "shiftData");
			ArrayList<LazyDynaBean> dataList = shift.shiftDataList(paramMap);
			if ("week".equals(type)) {
				// 导出周排班表
				// 列codesetid，text
				ArrayList<HashMap<String, String>> columnList = shift.getColumnList(Integer.valueOf(year),
				        Integer.valueOf(month), Integer.valueOf(weekIndex), "shiftData");
				// 获取排班方案表中信息
				HashMap<String, String> shiftSchemeInfoMap = getShiftSchemeInfo(groupId, schemeId);
				String fileName = exportWeekTable(columnList, dataList, shiftSchemeInfoMap);
				this.formHM.put("fileName", PubFunc.encrypt(fileName));
			} else if ("group".equals(type)) {
				// 统计汇总表
				String fileName = exportGroupTable(year, month, groupId, schemeId, dataList);
				this.formHM.put("fileName", PubFunc.encrypt(fileName));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出月汇总排班
	 * 
	 * @param schemeId
	 * @param month
	 * @param year
	 * @param schemeId2
	 * @param dataList
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws GeneralException
	 */
	private String exportGroupTable(String year, String month, String groupId, String schemeId,
	        ArrayList<LazyDynaBean> dataList) throws SQLException, GeneralException, IOException {
		String fileName = "";
		fileName = PinyinUtil.stringToHeadPinYin(this.userView.getUserName())+ "_" +"kq_month"+ ".xls";
		ArrayList values = new ArrayList();
		values.add(year);
		values.add(month);
		values.add(year);
		values.add(month);
		values.add(year);
		values.add(month);
		values.add(schemeId);
		HashMap totalMap = getGroupHeadList(groupId);
		ArrayList groupHeadList = (ArrayList) totalMap.get("groupHeadList");
		HashMap columnTextMap = (HashMap) totalMap.get("columnTextMap");
		HashMap classMap = (HashMap) totalMap.get("classMap");
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.set(Calendar.YEAR, Integer.valueOf(year));
		cal.set(Calendar.MONTH, Integer.valueOf(month) - 1);
		cal.set(Calendar.DATE, 1);
		int dayCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.monthScop = year + "." + month + ".01" + "~" + year + "." + month + "." + dayCount;
		ArrayList groupDataList = getGroupDataList(year, month, groupId, schemeId, groupHeadList, classMap, dataList);
		exportExcel(fileName, groupHeadList, groupDataList, columnTextMap, null, "month",
		        getShiftSchemeInfo(groupId, ""));
		return fileName;
	}

	/**
	 * 获取月汇总数据
	 * 
	 * @param values
	 * @param groupHeadList
	 * @param classMap
	 * @param dataList
	 * @return
	 * @throws SQLException
	 * @throws GeneralException
	 */
	private ArrayList getGroupDataList(String year, String month, String groupId, String schemeId,
	        ArrayList groupHeadList, HashMap classMap, ArrayList<LazyDynaBean> dataList)
	        throws SQLException, GeneralException {
		HashMap sqlvalueMap = getGroupDataSql(year, month, groupId, schemeId);
		String sql = (String) sqlvalueMap.get("sql");
		ArrayList values = (ArrayList) sqlvalueMap.get("values");

		ArrayList returnList = new ArrayList();
		ContentDAO dao = new ContentDAO(frameconn);
		this.frowset = dao.search(sql, values);
		String guidkey = "";
		ArrayList<String> dataGuidkeyList = new ArrayList<String>();
		HashMap dataMap = new HashMap();
		ArrayList<String> classColumns = new ArrayList<String>();
		while (this.frowset.next()) {
			if ("".equals(guidkey) || !guidkey.equals(this.frowset.getString("guidkey"))) {
				guidkey = this.frowset.getString("guidkey");
				dataMap = new HashMap();
				for (Object o : groupHeadList) {
					String code = (String) o;
					String value = "";
					if (!classMap.containsKey(code)) {
						value = this.frowset.getString(code);
						// TODO 根据类型判断
						if ("E0122".equals(code)) {
							value = AdminCode.getCodeName("UM", value);
						}
						if ("E01A1".equals(code)) {
							value = AdminCode.getCodeName("@K", value);
						}
						dataMap.put(code, value);
						if(!classColumns.contains(code))
							classColumns.add(code);
					}
				}
				returnList.add(dataMap);
				dataGuidkeyList.add(guidkey);
			}
			
			String code = this.frowset.getString("class");
			if(!classColumns.contains(code))
				classColumns.add(code);
			
			int value = this.frowset.getInt("coun");
			dataMap.put(code, String.valueOf(value));
		}

		// 排序 : 人员顺序按本周顺序显示，不在本周的人员按姓名顺序追加到最后。显示全月数据时不支持调整人员顺序和人员备注、周备注功能。
		ArrayList newReturnList = new ArrayList();
		ArrayList<String> orderList = new ArrayList<String>();
		for (LazyDynaBean bean : dataList) {
			String beanGuidkey = (String) bean.get("guidkey");
			beanGuidkey = PubFunc.decrypt(beanGuidkey);
			if (dataGuidkeyList != null && dataGuidkeyList.contains(beanGuidkey)) {
				int listIndex = dataGuidkeyList.indexOf(beanGuidkey);
				newReturnList.add(returnList.get(listIndex));
			}
			orderList.add(beanGuidkey);
		}

		for (String str : dataGuidkeyList) {
			if (!orderList.contains(str)) {
				int listIndex = dataGuidkeyList.indexOf(str);
				newReturnList.add(returnList.get(listIndex));
			}
		}
		//导出月汇总 添加合计行
		HashMap<String, String> countMap = new HashMap<String, String>();
		for (Object o : groupHeadList) {
			String code = (String) o;
			if ("E0122".equals(code) || "E01A1".equals(code) 
					|| "A0101".equals(code) || !classColumns.contains(code)) {
				if("E0122".equals(code))
					countMap.put(code, ResourceFactory.getProperty("kq.shift.total"));
				else
					countMap.put(code, "");
				
				continue;
			}
			
			double value = 0;
			for(int i = 0; i < newReturnList.size(); i++) {
				HashMap map = (HashMap) newReturnList.get(i);
				if (countMap.containsKey(code)) {
					value = Double.valueOf(StringUtils.isEmpty(countMap.get(code)) ? "0" : countMap.get(code));
					value = value + Double.valueOf((String)map.get(code));
					countMap.put(code, value + "");
				} else 
					countMap.put(code, StringUtils.isEmpty(String.valueOf(map.get(code))) ? "" : map.get(code) + "");
			}
		}
		
		newReturnList.add(countMap);
		return newReturnList;
	}

	/**
	 * 获取表头列集合
	 * 
	 * @return
	 * @throws SQLException
	 */
	private HashMap getGroupHeadList(String groupId) throws SQLException {
		HashMap totalMap = new HashMap();
		ArrayList<String> groupHeadList = new ArrayList<String>();
		HashMap<String, String> columnTextMap = new HashMap<String, String>();
		HashMap<String, String> classMap = new HashMap<String, String>();
		groupHeadList.add("E0122");
		columnTextMap.put("E0122", "部门");
		groupHeadList.add("E01A1");
		columnTextMap.put("E01A1", "班组");
		groupHeadList.add("A0101");
		columnTextMap.put("A0101", "姓名");
		String sql = "select Shift_data from kq_shift_group where shift_type=? and group_id=?";
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add("1");
		paramList.add(groupId);
		ContentDAO dao = new ContentDAO(this.frameconn);
		this.frowset = dao.search(sql, paramList);
		String shiftIds = "";
		paramList.clear();
		paramList.add("1");
		if(this.frowset.next()) {
			String shiftData = this.frowset.getString("Shift_data");
			String[] shiftDatas = shiftData.split(";");
			for(String shift : shiftDatas) {
				if(StringUtils.isEmpty(shift))
					continue;
				
				String[] shifts = shift.split(",");
				for(String dayShift : shifts) {
					if(StringUtils.isEmpty(dayShift) || paramList.contains(dayShift))
						continue;
					
					shiftIds += ",?";
					paramList.add(dayShift);
				}
			}
		}
		
		sql = "select class_id,name from kq_class where is_validate=?"; 
		sql += " and class_id in (-1" + shiftIds + ")";
		this.frowset = dao.search(sql, paramList);
		while (this.frowset.next()) {
			groupHeadList.add(this.frowset.getString("class_id"));
			columnTextMap.put(this.frowset.getString("class_id"), this.frowset.getString("name"));
			classMap.put(this.frowset.getString("class_id"), this.frowset.getString("name"));
		}
		groupHeadList.add("Work_hour");
		columnTextMap.put("Work_hour", "工时");

		totalMap.put("groupHeadList", groupHeadList);
		totalMap.put("columnTextMap", columnTextMap);
		totalMap.put("classMap", classMap);
		return totalMap;
	}

	/**
	 * 获取导出月汇总语句
	 * 
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private HashMap getGroupDataSql(String year, String month, String groupId, String schemeId)
	        throws GeneralException, SQLException {
		HashMap returnMap = new HashMap();
		String sql = "";
		ArrayList<String> values = new ArrayList<String>();
		// 获取人员库
		KqParameterService kqParam = new KqParameterServiceImpl(this.userView, this.frameconn);
		HashMap paramMap = kqParam.getKqParameter();
		String nbases = (String) paramMap.get("nbase");
		String[] dbNames = nbases.split(",");
		String a01Table = "";
		// 联合人员库表
		String whereSql = (String) this.userView.getHm().get("shiftWhere");
		for (String nbase : dbNames) {
			if (nbase.length() == 3) {
				if (a01Table.length() > 0)
					a01Table += " union ";
				a01Table += " select E0122,E01A1,A0101,GUIDKEY from " + nbase + "A01 ";
				
				if(StringUtils.isNotEmpty(whereSql)) {
					a01Table += " where " + whereSql.replace("a01.", "");
				}
			}
		}
		a01Table = "(" + a01Table + ")";

		// 查询出本月所有的Scheme_id
		HashMap<String, String> schemeMap = new HashMap<String, String>();
		String schemeSql = "select Scheme_id,scope from kq_shift_scheme where Group_id = ? and scope like '%" + year
		        + "." + month + "%' order by scope";
		values.add(groupId);
		ContentDAO dao = new ContentDAO(frameconn);
		this.frowset = dao.search(schemeSql, values);
		while (this.frowset.next()) {
			if("-1".equalsIgnoreCase(schemeId))
				schemeId = this.frowset.getString("Scheme_id");
				
			schemeMap.put(this.frowset.getString("Scheme_id"), this.frowset.getString("scope"));
		}

		values = new ArrayList<String>();
		String scope = schemeMap.get(schemeId);
		String[] strs = scope.split("-");
		String startTime = strs[0].replace(".", "-");
		String endTime = strs[1].replace(".", "-");
		// 查询当前schemeId的数据
		sql = "select a01.E0122 E0122,a01.E01A1 E01A1,a01.A0101 A0101,emp.Work_hour Work_hour,a.class class,a.coun coun,'"
		        + schemeId + "' flag,a.guidkey guidkey" + " from kq_shift_scheme_emp emp, " + a01Table + " a01,"
		        + "( select guidkey,class,sum(coun) coun" + " from ("
		        + " select guidkey,Class_id_1 class,sum(1) coun,'Class_id_1' AS grop"
		        + " from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
				+ " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
		        + "	group by guidkey,Class_id_1" + " union "
		        + "	select guidkey,Class_id_2 class,sum(1) coun,'Class_id_2' AS grop"
		        + "	from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
		        + " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
		        + "	group by guidkey,Class_id_2" + " union "
		        + "	select guidkey,Class_id_3 class,sum(1) coun,'Class_id_3' AS grop"
		        + "	from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
		        + " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
		        + "	group by guidkey,Class_id_3" + ") classtab" + "	where class is not null"
		        + "	group by guidkey,class) a" + " where emp.guidkey =a01.GUIDKEY " + "and a.guidkey=a01.GUIDKEY"
		        + " and emp.Scheme_id=?";
		values.add(startTime);
		values.add(endTime);
		values.add(startTime);
		values.add(endTime);
		values.add(startTime);
		values.add(endTime);
		values.add(schemeId);

		for (Object o : schemeMap.keySet()) {
			String sche = (String) o;
			// 跳过当前周
			if (sche.equals(schemeId))
				continue;
			scope = schemeMap.get(sche);
			strs = scope.split("-");
			startTime = strs[0].replace(".", "-");
			endTime = strs[1].replace(".", "-");

			// 循环schemeId 查询数据
			sql += " union select a01.E0122 E0122,a01.E01A1 E01A1,a01.A0101 A0101,emp.Work_hour Work_hour,a.class class,a.coun coun,'"
					+ sche + "' flag,a.guidkey guidkey" + " from kq_shift_scheme_emp emp, " + a01Table + " a01,"
			        + "( select guidkey,class,sum(coun) coun" + " from ("
			        + " select guidkey,Class_id_1 class,sum(1) coun,'Class_id_1' AS grop"
			        + " from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
					+ " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
			        + "	group by guidkey,Class_id_1" + " union "
			        + "	select guidkey,Class_id_2 class,sum(1) coun,'Class_id_2' AS grop"
			        + "	from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
			        + " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
			        + "	group by guidkey,Class_id_2" + " union "
			        + "	select guidkey,Class_id_3 class,sum(1) coun,'Class_id_3' AS grop"
			        + "	from kq_employ_shift_v2" + " WHERE " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?"
			        + " AND " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?"
			        + "	group by guidkey,Class_id_3" + ") classtab" + "	where class is not null"
			        + "	group by guidkey,class) a" + " where emp.guidkey =a01.GUIDKEY " + "and a.guidkey=a01.GUIDKEY"
			        + " and emp.Scheme_id=?";
			values.add(startTime);
			values.add(endTime);
			values.add(startTime);
			values.add(endTime);
			values.add(startTime);
			values.add(endTime);
			values.add(sche);
		}

		sql = "select E0122,E01A1,A0101,sum(Work_hour) Work_hour,class,sum(coun) coun,guidkey from (" + sql
		        + ") tota group by guidkey,A0101,class,E0122,E01A1 order by A0101";

		returnMap.put("sql", sql);
		returnMap.put("values", values);
		return returnMap;
	}

	/**
	 * 获取排班方案表中信息
	 * 
	 * @param schemeId
	 * @return HashMap
	 * @throws SQLException
	 */
	private HashMap<String, String> getShiftSchemeInfo(String grouoId, String schemeId) throws SQLException {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		ArrayList<String> values = new ArrayList<String>();
		if (StringUtils.isNotEmpty(schemeId) && !"-1".equals(schemeId))
			values.add(schemeId);
		else
			values.add(grouoId);

		returnMap.put("schemeId", schemeId);
		String sql = "select name,org_id,shift_comment,Emp_comment,Train_comment";
		sql += " from kq_shift_scheme scheme left join kq_shift_group kq_group on scheme.Group_id = kq_group.group_id";
		sql += " where Scheme_id = ?";
		if (StringUtils.isEmpty(schemeId) || "-1".equals(schemeId))
			sql = "select name,org_id from kq_shift_group where group_id=?";

		String orgId = "";
		ContentDAO dao = new ContentDAO(this.frameconn);
		this.frowset = dao.search(sql, values);
		if (this.frowset.next()) {
			returnMap.put("groupName", this.frowset.getString("name"));
			orgId = this.frowset.getString("org_id");
			String orgDesc = AdminCode.getCodeName("UN", orgId);
			if (StringUtils.isEmpty(orgDesc))
				orgDesc = AdminCode.getCodeName("UM", orgId);

			returnMap.put("orgId", orgDesc);
			if (StringUtils.isNotEmpty(schemeId) && !"-1".equals(schemeId)) {
				returnMap.put("shift_comment", this.frowset.getString("shift_comment"));
				returnMap.put("Emp_comment", this.frowset.getString("Emp_comment"));
				returnMap.put("Train_comment", this.frowset.getString("Train_comment"));
			}
		}
		
		sql = "select codeitemdesc from organization where codesetid in ('UN','UM') "
				+ "and codeitemid=parentid and codeitemid=";
		if(Sql_switcher.searchDbServer() == Constant.MSSQL)
			sql+= "left(?,LEN(codeitemid))";
		else
			sql+= "SUBSTR(?,0,lengthb(codeitemid))";
		
		values.clear();
		values.add(orgId);
		this.frowset = dao.search(sql, values);
		if(this.frowset.next())
			returnMap.put("topOrgName", this.frowset.getString("codeitemdesc"));
		
		return returnMap;
	}

	/**
	 * 列指标对应名称转为map
	 * 
	 * @param columnList
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> ColumnStringToMap(ArrayList<HashMap<String, String>> columnList) {
		HashMap<String, String> columnTextMap = new HashMap<String, String>();
		// 用来记录 数据列
		HashMap<String, String> dateHeadSpMap = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> totalMap = new HashMap<String, HashMap<String, String>>();

		// {itemDesc=单位名称, itemId=b0110} {itemDesc=01.02:周三, itemId=2019.01.02}
		columnTextMap.put("order", ResourceFactory.getProperty("label.serialnumber"));
		for (HashMap<String, String> infoMap : columnList) {
			String itemId = infoMap.get("itemId");
			String itemDesc = infoMap.get("itemDesc");
			if (itemDesc.indexOf(":") > -1) {
				itemDesc = itemDesc.replace(":", "\r\n");
				dateHeadSpMap.put(itemId, itemDesc);
			}
			
			columnTextMap.put(itemId, itemDesc);
		}

		totalMap.put("columnTextMap", columnTextMap);
		totalMap.put("dateHeadSpMap", dateHeadSpMap);
		return totalMap;
	}

	/**
	 * 递归获取表头list
	 * 
	 * @param jsonstring
	 * @param returnList
	 * @return
	 */
	public ArrayList<String> getJsonList(String jsonstring, ArrayList returnList) {
		if (jsonstring.indexOf("},{") > -1) {
			returnList.add(jsonstring.substring(0, jsonstring.indexOf("},{") + 1));
			jsonstring = jsonstring.substring(jsonstring.indexOf("},{") + 2);
			getJsonList(jsonstring, returnList);
		} else {
			if (!"".equals(jsonstring))
				returnList.add(jsonstring);
		}

		return returnList;
	}

	/**
	 * 导出周排班表
	 * 
	 * @param columnList
	 * @param dataList
	 * @param shiftSchemeInfoMap
	 * @return
	 * @throws IOException
	 * @throws GeneralException
	 */
	public String exportWeekTable(ArrayList<HashMap<String, String>> columnList,
	        ArrayList<LazyDynaBean> dataList, HashMap shiftSchemeInfoMap) throws GeneralException, IOException {
		// 文件名
		String fileName = "";
		fileName = PinyinUtil.stringToHeadPinYin(this.userView.getUserName())+ "_" +"kq_week" + ".xls";

		HashMap<String, HashMap<String, String>> totalMap = new HashMap<String, HashMap<String, String>>();
		// 根据columnJson 解析表头列<codeid:text>的map columnTextMap ；
		// 以及数据列<codeid:text>的map：dateHeadSpMap
		totalMap = ColumnStringToMap(columnList);
		HashMap<String, String> columnTextMap = totalMap.get("columnTextMap");
		HashMap<String, String> dateHeadSpMap = totalMap.get("dateHeadSpMap");

		// 获取表头列 的有序集合 headList
		ArrayList<HashMap<String, String>> headList = getHeadList(columnList);

		// 处理数据列为 List<map<codeid:value>>类型; 以及 行高map
		HashMap dealdMap = getDataList(headList, dateHeadSpMap, dataList);
		HashMap rowHeightMap = (HashMap) dealdMap.get("rowHeightMap");
		ArrayList<HashMap> newDataList = (ArrayList<HashMap>) dealdMap.get("newDataList");

		exportExcel(fileName, headList, newDataList, columnTextMap, rowHeightMap, "week", shiftSchemeInfoMap);
		return fileName;
	}

	/**
	 * 获取excel的有序列
	 * 
	 * @param columnTextMap
	 * @param cloumns
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getHeadList(ArrayList<HashMap<String, String>> columnList) {
		ArrayList<HashMap<String, String>> headList = new ArrayList<HashMap<String, String>>();
		// 添加order字段
		HashMap<String, String> columnMap = new HashMap<String, String>();
		columnMap.put("itemId", "order");
		columnMap.put("itemDesc", ResourceFactory.getProperty("label.serialnumber"));
		columnMap.put("width", "40");
		columnMap.put("decimalWidth", "0");
		columnMap.put("fieldSetId", "");
		columnMap.put("align", "center");
		headList.add(columnMap);
		for (HashMap<String, String> columnn : columnList) {
			if ("guidkey".equals(columnn.get("itemId")) || "order".equalsIgnoreCase(columnn.get("itemId")))
				continue;
			
			headList.add(columnn);
		}
		
		return headList;
	}

	/**
	 * 获取数据列
	 * 
	 * @param headList
	 * @param dateHeadSpMap
	 * @param dataList
	 * @return
	 */
	public HashMap getDataList(ArrayList<HashMap<String, String>> headList, HashMap dateHeadSpMap, ArrayList<LazyDynaBean> dataList) {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		// 格式化后的数据list
		ArrayList<HashMap<String, String>> newDataList = new ArrayList<HashMap<String, String>>();
		// 每行的行高map<index,height>
		HashMap<Integer, Integer> rowHeightMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < dataList.size(); i++) {
			LazyDynaBean databean = dataList.get(i);
			// 列高
			int rowHeight = 1;
			HashMap<String, String> rowMap = new HashMap<String, String>();
			for (HashMap<String, String> o : headList) {
				String codeset = o.get("itemId");
				String value = "";
				if (dateHeadSpMap.containsKey(codeset)) {
					if (StringUtils.isNotEmpty((String) databean.get(codeset))
					        && !"null".equals(databean.get(codeset).toString())) {
						JSONArray shiftArray = JSONArray.fromObject(databean.get(codeset));
						for (int j = 0; j < shiftArray.size(); j++) {
							JSONObject shift = JSONObject.fromObject(shiftArray.get(j));
							if (j > 0)
								value += "\r\n";
							// 班次备注
							String commentValue = (String) shift.get("comment");
							value += (String) shift.get("className")
							        + (StringUtils.isBlank(commentValue) ? "" : "（" + commentValue + "）");
						}
						if (shiftArray.size() > rowHeight) {
							rowHeight = shiftArray.size();
						}
					}
				} else {
					if ("order".equals(codeset))
						value = Integer.toString(i + 1);
					else
						value = (String) databean.get(codeset);
				}
				rowMap.put(codeset, value);
			}
			rowHeightMap.put(i, rowHeight);
			newDataList.add(rowMap);
		}
		returnMap.put("rowHeightMap", rowHeightMap);
		returnMap.put("newDataList", newDataList);
		return returnMap;
	}

	/**
	 * 导出excel
	 * 
	 * @param fileName
	 *            文件名
	 * @param headList
	 *            表头
	 * @param dataList
	 *            数据
	 * @param columnTextMap
	 *            //* @param dateHeadSpMap
	 * @param rowHeightMap
	 * @param shiftSchemeInfoMap
	 * @param type
	 *            “week”；“group”
	 * @throws GeneralException
	 * @throws IOException
	 */
	public void exportExcel(String fileName, ArrayList headList, ArrayList<HashMap> dataList, HashMap columnTextMap,
	        HashMap rowHeightMap, String type, HashMap shiftSchemeInfoMap) throws GeneralException, IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(1 + "");
		sheet.setDefaultColumnWidth(10);
		HSSFCellStyle titleStyle = getExpStyle("title", wb);
		HSSFCellStyle subheadStyle = getExpStyle("subhead", wb);
		HSSFCellStyle deptStyle = getExpStyle("dept", wb);
		HSSFCellStyle style = getExpStyle("head", wb);
		HSSFCellStyle rightStyle = getExpStyle("right", wb);
		HSSFCellStyle centerStyle = getExpStyle("center", wb);
		HSSFCellStyle leftStyle = getExpStyle("left", wb);
		// 取得第fromRowNum行
		int rowIndex = 0;
		HSSFRow row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);

		row.setHeight((short) 600);
		HSSFCell cell = row.getCell(0);
		if (cell == null)
			cell = row.createCell(0);

		cell.setCellStyle(titleStyle);
		cell.setCellValue(shiftSchemeInfoMap.get("topOrgName") + "排班表");
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, headList.size() - 1));
		if ("month".equalsIgnoreCase(type)) {
			rowIndex++;
			row = sheet.getRow(rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);

			cell = row.getCell(0);
			if (cell == null)
				cell = row.createCell(0);

			cell.setCellStyle(subheadStyle);
			cell.setCellValue(this.monthScop);
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, headList.size() - 1));
		}

		rowIndex++;
		row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);

		cell = row.getCell(0);
		if (cell == null)
			cell = row.createCell(0);

		cell.setCellStyle(deptStyle);
		String orgDesc = (String) shiftSchemeInfoMap.get("orgId");
		cell.setCellValue(orgDesc + "：" + shiftSchemeInfoMap.get("groupName"));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, headList.size() - 1));

		rowIndex++;
		row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);

		row.setHeight((short) 600);
		// 导出head行
		HashMap colIndexMap = new HashMap();
		for (int i = 0; i < headList.size(); i++) {
			cell = row.getCell(i);
			if (cell == null)
				cell = row.createCell(i);
			// 设置该单元格样式
			cell.setCellStyle(style);
			// 给该单元格赋值
			if ("week".equals(type)) {
				HashMap<String, String> cloumn = (HashMap<String, String>) headList.get(i);
				sheet.setColumnWidth(i, Integer.valueOf(cloumn.get("width")) * 32);
				String itemId = cloumn.get("itemId");
				cell.setCellValue(new HSSFRichTextString((String) columnTextMap.get(itemId)));
				colIndexMap.put(itemId, i);
			} else {
				String cloumn = (String) headList.get(i);
				cell.setCellValue(new HSSFRichTextString((String) columnTextMap.get(cloumn)));
				colIndexMap.put(cloumn, i);
			}
		}

		// 导出数据
		for (int i = 0; i < dataList.size(); i++) {
			rowIndex++;
			row = sheet.getRow(rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);
			// 设置行高
			int height = 1;
			if (rowHeightMap != null && rowHeightMap.get(i) != null)
				height = (Integer) rowHeightMap.get(i);
			
			height = height * 300;
			row.setHeight((short) height);
			
			HashMap map = dataList.get(i);
			for (int j = 0; j < headList.size(); j++) {
				String cloumn = "";
				String align = "";
				if ("week".equals(type)) {
					cloumn = ((HashMap<String, String>) headList.get(j)).get("itemId");
					align = ((HashMap<String, String>) headList.get(j)).get("align");
				} else
					cloumn = (String) headList.get(j);
				
				int colIndex = (Integer) colIndexMap.get(cloumn);
				cell = row.getCell(colIndex);
				if (cell == null)
					cell = row.createCell(colIndex);
				// 设置该单元格样式
				if ("order".equals(cloumn))
					cell.setCellStyle(centerStyle);
				else if (StringUtils.isNotEmpty(align)) {
					if("right".equalsIgnoreCase(align))
						cell.setCellStyle(rightStyle);
					else if("left".equalsIgnoreCase(align)) 
						cell.setCellStyle(leftStyle);
					else
						cell.setCellStyle(centerStyle);
				} else
					cell.setCellStyle(leftStyle);

				if ((j > 2 && "month".equals(type)) || "extra_days".equals(cloumn) || "work_hour".equals(cloumn)
				        || cloumn.startsWith("stat_"))
					cell.setCellStyle(rightStyle);
				// 给该单元格赋值
				cell.setCellValue(new HSSFRichTextString((String) map.get(cloumn)));
			}
		}

		if ("week".equals(type)) {
			if (shiftSchemeInfoMap != null && !"-1".equals(shiftSchemeInfoMap.get("schemeId"))) {
				// 人员调配
				rowIndex++;
				row = sheet.getRow(rowIndex);
				if (row == null)
					row = sheet.createRow(rowIndex);
				setColMergedCell(row, "人员调配", 0, 1, sheet, rowIndex, centerStyle);
				String emp_comment = (String) shiftSchemeInfoMap.get("Emp_comment");
				// 根据内容行数设置行高
				int rwsTemp = 1;
				if (StringUtils.isNotEmpty(emp_comment))
					rwsTemp = emp_comment.split("\n").length;

				if (rwsTemp < 1)
					rwsTemp = 1;
				
				row.setHeight((short) (300 * rwsTemp));
				setColMergedCell(row, emp_comment, 2, headList.size() - 1, sheet, rowIndex, leftStyle);
				// 培训安排
				rowIndex++;
				row = sheet.getRow(rowIndex);
				if (row == null)
					row = sheet.createRow(rowIndex);
				
				setColMergedCell(row, "培训安排", 0, 1, sheet, rowIndex, centerStyle);
				String train_comment = (String) shiftSchemeInfoMap.get("Train_comment");
				rwsTemp = 1;
				if (StringUtils.isNotEmpty(train_comment))
					rwsTemp = train_comment.split("\n").length;
				
				if (rwsTemp < 1)
					rwsTemp = 1;
				
				row.setHeight((short) (300 * rwsTemp));
				setColMergedCell(row, train_comment, 2, headList.size() - 1, sheet, rowIndex, leftStyle);
				// 备注说明
				rowIndex++;
				row = sheet.getRow(rowIndex);
				if (row == null)
					row = sheet.createRow(rowIndex);
				
				row.setHeight((short) 300);
				setColMergedCell(row, "备注说明", 0, 1, sheet, rowIndex, centerStyle);
				String shift_comment = (String) shiftSchemeInfoMap.get("shift_comment");
				rwsTemp = 1;
				if (StringUtils.isNotEmpty(shift_comment))
					rwsTemp = shift_comment.split("\n").length;
				
				if (rwsTemp < 1)
					rwsTemp = 1;
				
				row.setHeight((short) (300 * rwsTemp));
				setColMergedCell(row, shift_comment, 2, headList.size() - 1, sheet, rowIndex, leftStyle);
			}
		}

		FileOutputStream fileOut = null;
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
			fileOut = new FileOutputStream(url);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(fileOut);
			wb.close();
		}
	}

	/**
	 * 设置列合并
	 * 
	 * @param row
	 * @param text
	 * @param fromCol
	 * @param toCol
	 * @param sheet
	 * @param rowIndex
	 * @param style
	 */
	public void setColMergedCell(HSSFRow row, String text, int fromCol, int toCol, HSSFSheet sheet, int rowIndex,
	        HSSFCellStyle style) {
		HSSFCell cell = row.getCell(fromCol);
		if (cell == null)
			cell = row.createCell(fromCol);
		cell.setCellStyle(style);
		cell.setCellValue(new HSSFRichTextString(text));
		CellRangeAddress cra = new CellRangeAddress(rowIndex, rowIndex, fromCol, toCol);
		sheet.addMergedRegion(cra);
		RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
	}

	/**
	 * 获取单元格样式
	 * 
	 * @param type
	 * @param wb
	 * @return
	 */
	public HSSFCellStyle getExpStyle(String type, HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		// 自动换行
		style.setWrapText(true);
		// 设置border
		style.setBorderLeft(BorderStyle.valueOf((short) 1));
		style.setBorderRight(BorderStyle.valueOf((short) 1));
		style.setBorderTop(BorderStyle.valueOf((short) 1));
		style.setBorderBottom(BorderStyle.valueOf((short) 1));
		short borderColor = IndexedColors.BLACK.index;
		style.setLeftBorderColor(borderColor);
		style.setRightBorderColor(borderColor);
		style.setTopBorderColor(borderColor);
		style.setBottomBorderColor(borderColor);

		FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
		style.setFillPattern(fillPattern);

		short fillForegroundColor = IndexedColors.WHITE.index;
		style.setFillForegroundColor(fillForegroundColor);
		// 字体
		int fontSize = 10;// 字体大小
		String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		font.setFontName(fontName);
		// 对齐方式
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		if ("head".equals(type)) {
			font.setBold(true);// 加粗
		} else if ("right".equals(type)) {
			align = HorizontalAlignment.RIGHT;
			font.setBold(false);// 加粗
		} else if ("center".equals(type)) {
			align = HorizontalAlignment.CENTER;
			font.setBold(false);// 加粗
		} else if ("left".equals(type)) {
			align = HorizontalAlignment.LEFT;
			font.setBold(false);// 加粗
		} else if ("title".equals(type) || "subhead".equals(type) || "dept".equals(type)) {
			if ("title".equals(type)) {
				font.setBold(true);// 加粗
				font.setFontHeightInPoints((short) 16);
				align = HorizontalAlignment.CENTER;
			} else if ("dept".equals(type)) {
				font.setBold(false);// 加粗
				align = HorizontalAlignment.LEFT;
			} else if ("subhead".equals(type)) {
				font.setBold(false);// 加粗
				align = HorizontalAlignment.CENTER;
			}

			style.setBorderLeft(BorderStyle.NONE);
			style.setBorderRight(BorderStyle.NONE);
			style.setBorderTop(BorderStyle.NONE);
			style.setBorderBottom(BorderStyle.NONE);
		}
		// 对齐方式
		style.setAlignment(align);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);
		style.setWrapText(true);
		
		return style;
	}
}

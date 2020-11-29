package com.hjsj.hrms.module.kq.shiftcheck.businessobject.impl;

import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.module.kq.shiftcheck.businessobject.ShiftCheckService;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**  
 * <p>Title: ShiftCheckServiceImpl</p>  
 * <p>Description: 考勤排班审查</p>  
 * <p>Company: hjsj</p>
 * @date 2018年12月3日 下午3:47:37
 * @author linbz  
 * @version 7.5
 */  
public class ShiftCheckServiceImpl implements ShiftCheckService {
    private UserView userView;
    private Connection conn;
    private ArrayList<HashMap<String, String>> columnInfoList = new ArrayList<HashMap<String,String>>();
    public ShiftCheckServiceImpl(UserView userView, Connection connection) {
        this.userView = userView;
        this.conn = connection;
    }
    /**
     * 获取排班审查表格数据
     * getShiftCheckData
     * @param jsonObj
     * @return
     * @throws GeneralException
     * @date 2018年12月6日 下午4:13:20
     * @author linbz
     */
    @Override
    public HashMap getShiftCheckData(JSONObject jsonObj) throws GeneralException{
    	HashMap dataMap = new HashMap();
        RowSet rs = null;
        try {
        	String year = jsonObj.getString("year");
			String month = jsonObj.getString("month");
			String weekIndex = jsonObj.getString("weekIndex");
			
			Calendar cal = Calendar.getInstance();
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			if(StringUtils.isEmpty(year))
				year = String.valueOf(cal.get(Calendar.YEAR));
			
			if(StringUtils.isEmpty(month))
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			
			if(StringUtils.isEmpty(weekIndex)) 
				weekIndex = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
			
			ShiftServiceImpl shiftService = new ShiftServiceImpl(this.userView, this.conn);
			String columnJson = shiftService.getShiftcolumnsJson(Integer.valueOf(year), Integer.valueOf(month),
					Integer.valueOf(weekIndex) ,"shiftCheck");
			String column = shiftService.getShiftcolumns(Integer.valueOf(year), Integer.valueOf(month),
					Integer.valueOf(weekIndex) ,"shiftCheck");
			ArrayList<HashMap<String, String>> weekList = shiftService.weekList(Integer.valueOf(year), Integer.valueOf(month));
			String dateJson = shiftService.getShiftDate("");
			int pageRows = shiftService.getPageSize("shiftCheck");
			
			dataMap.put("columnJson", columnJson);
			dataMap.put("column", column);
			dataMap.put("weekList", weekList);
			dataMap.put("dateJson", dateJson);
			dataMap.put("year", year);
			dataMap.put("month", month);
			dataMap.put("weekIndex", weekIndex);
			dataMap.put("pageRows", pageRows + "");
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally {
	        PubFunc.closeResource(rs);
	    }
	
	    return dataMap;
    }
    /**
     * 获取排班审查SQL
     * getShiftCheckSql
     * @param dateScope
     * @return
     * @date 2018年12月6日 下午5:13:50
     * @author linbz
     */
    private String getShiftCheckSql(String dateScope, String weekIndex) {
		StringBuffer sql = new StringBuffer();
		try {
			HashMap map = KqPrivForHospitalUtil.getKqParameter(conn);
			// 工号指标
			String gNo = (String) map.get("g_no");
			boolean isNotBanlkGNo = StringUtils.isNotBlank(gNo);
			// 考勤部门指标
			String kqDeptField = (String) map.get("kq_dept");
			boolean isNotBanlkKqDept = StringUtils.isNotBlank(kqDeptField);
	    	String whereInKqDept = "1=1";
			if(isNotBanlkKqDept)
				whereInKqDept = KqPrivForHospitalUtil.getPrivB0110Whr(userView, kqDeptField, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
			String whereInb0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE01A1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E01A1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	
	    	StringBuffer columns = new StringBuffer();
			for(HashMap<String, String> columnMap : this.columnInfoList) {
				if(StringUtils.isNotEmpty(columnMap.get("fieldSetId")))
					columns.append(columnMap.get("itemId") + ",");
			}
			
			StringBuffer sqlTemp = new StringBuffer();
			ArrayList<String> nbaselist = KqPrivForHospitalUtil.getB0110Dase(userView, conn);
			for (int i = 0; i < nbaselist.size(); i++) {
				String nbase = nbaselist.get(i);
				if (StringUtils.isNotEmpty(sqlTemp.toString()))
					sqlTemp.append(" union all ");

				sqlTemp.append("select a0000," + columns + "GUIDKEY");
				sqlTemp.append(" from " + nbase +"A01 WHERE");
				sqlTemp.append(" ("+ whereInb0110);
				sqlTemp.append(" or "+ whereInE0122);
				sqlTemp.append(" or "+ whereInE01A1);
				if (!userView.isSuper_admin()) 
					sqlTemp.append(" or "+ whereInKqDept);
				sqlTemp.append(") ");
			}
			ArrayList<String> dateScopeList = new ArrayList<String>();
			if ("-1".equals(weekIndex)) {
				ShiftServiceImpl shiftService = new ShiftServiceImpl(this.userView, this.conn);
				dateScopeList = shiftService.dateScopeList("", dateScope, "shiftCheck");
				dateScope = dateScopeList.get(0);
			}

			sql.append("select a01.a0000," + columns);
			sql.append("g.name shift_group,emp.* from (");
			sql.append(sqlTemp);
			sql.append(") a01 ");
			sql.append(" left join kq_shift_scheme_emp emp on emp.guidkey=a01.guidkey ");
			sql.append(" left join kq_shift_scheme scheme on emp.Scheme_id=scheme.Scheme_id ");
			sql.append(" left join kq_shift_group g on g.Group_id=scheme.Group_id ");
			
			sql.append(" where scheme.state='04' and scheme.scope ='" + dateScope + "' ");
			
			String where = (String) this.userView.getHm().get("shiftWhere");
			if (StringUtils.isNotEmpty(where)) {
				sql.append(" and (" + where + ")");
				this.userView.getHm().remove("shiftWhere");
			}
			// 选去全月数据时特殊处理
			if ("-1".equals(weekIndex)) {
				for(int i = 1; i < dateScopeList.size(); i++) {
					dateScope = dateScopeList.get(i);
					sql.append(" union all ");
					
					sql.append("select a01.a0000," + columns);
					sql.append("g.name shift_group,emp.* from (");
					sql.append(sqlTemp);
					sql.append(") a01 ");
					sql.append(" left join kq_shift_scheme_emp emp on emp.guidkey=a01.guidkey ");
					sql.append(" left join kq_shift_scheme scheme on emp.Scheme_id=scheme.Scheme_id ");
					sql.append(" left join kq_shift_group g on g.Group_id=scheme.Group_id ");

					sql.append(" where ");
					sql.append(" scheme.state='04' and scheme.scope='" + dateScope + "'");
					if (StringUtils.isNotEmpty(where))
						sql.append(" and (" + where + ")");
					
					sql.append(" and emp.guidkey not in (select guidkey from kq_shift_scheme_emp emp1,kq_shift_scheme scheme1");
					sql.append(" where emp1.scheme_id=scheme1.scheme_id and scheme1.scope in ('#'");
					for(int n = 0; n < i; n++)
						sql.append(",'" + dateScopeList.get(n) + "'");
					
					sql.append(") and scheme1.state='04')");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();
	}
    /**
     * 获取权限内部门员工出勤人数
     * listOrgOndutyCount
     * @return
     * @throws GeneralException
     * @date 2018年12月4日 下午4:05:07
     * @author linbz
     */
    @Override
    public ArrayList listOrgOndutyCount(JSONObject jsonObj) throws GeneralException{
    	
    	ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
        	String year = jsonObj.getString("year");
			String month = jsonObj.getString("month");
			String weekIndex = jsonObj.getString("weekIndex");
			Calendar cal = Calendar.getInstance();
			if(StringUtils.isEmpty(year))
				year = String.valueOf(cal.get(Calendar.YEAR));
			
			if(StringUtils.isEmpty(month))
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			
			if(StringUtils.isEmpty(weekIndex))
				weekIndex = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
			
			ShiftServiceImpl shiftService = new ShiftServiceImpl(this.userView, this.conn);
			ArrayList<String> datesList = ShiftServiceImpl.getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month)
					, Integer.valueOf(weekIndex));
			ArrayList<String> datelist = new ArrayList<String>(); 
			for(int i=0;i<datesList.size();i++) {
				String str = datesList.get(i);
				datelist.add(str.split(":")[0]);
			}
			KqPrivForHospitalUtil kqPrivForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.conn);
			// 考勤部门指标
			String kq_dept = kqPrivForHospitalUtil.getKqDeptField();
			boolean isNotBankKqDept = StringUtils.isNotBlank(kq_dept);
			String whereInb0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE01A1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E01A1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInKqDept = "1=1";
	    	if(isNotBankKqDept)
	    		whereInKqDept = KqPrivForHospitalUtil.getPrivB0110Whr(userView, kq_dept, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
			
			ArrayList<String> dbnameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			StringBuffer a01sql = new StringBuffer("");
			for(int i = 0; i < dbnameList.size(); i++){
				String dbname = dbnameList.get(i);
				if(i > 0)
					a01sql.append(" UNION ALL ");
				
				a01sql.append(" select guidkey,E0122 from " + dbname+"A01 " );
				a01sql.append(" where ( "+ whereInb0110);
				a01sql.append(" or "+ whereInE0122);
				a01sql.append(" or "+ whereInE01A1);
				if (!userView.isSuper_admin()) 
					a01sql.append(" or "+ whereInKqDept);
				a01sql.append(")");
			}
        	
        	StringBuffer selectSql = new StringBuffer("");
        	selectSql.append("select E0122,Q03Z0,COUNT(Q03Z0) ondutyNum ");
        	selectSql.append(" from kq_employ_shift_v2 q ");
        	selectSql.append(" left join ");
        	selectSql.append(" ( ");
        	// 通过主集guidkey查
        	selectSql.append(a01sql.toString());
        	selectSql.append(" ) z on q.guidkey=z.guidkey");
        	selectSql.append(" left join kq_shift_scheme_emp emp on emp.guidkey = q.guidkey");
        	selectSql.append(" left join kq_shift_scheme scheme on scheme.Scheme_id=emp.Scheme_id");
        	
        	StringBuffer whereSql = new StringBuffer("");
        	whereSql.append(" where");
        	// 班次不是0 不为null 则定义为出勤
        	whereSql.append(" scheme.State='04' and (");
        	whereSql.append(" (Class_id_1<>0 and Class_id_1 is not null");
        	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
        		whereSql.append(" and Class_id_1<>'' ");
        	whereSql.append(")");
        	whereSql.append(" or (Class_id_2<>0 and Class_id_2 is not null");
        	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
        		whereSql.append(" and Class_id_2<>'' ");
        	whereSql.append(")");
        	whereSql.append(" or (Class_id_3<>0 and Class_id_3 is not null");
        	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
        		whereSql.append(" and Class_id_3<>'' ");
        	whereSql.append(")");
        	whereSql.append(")");
        	
        	String groupby = " group by Q03Z0,E0122 ";
        	StringBuffer sql = new StringBuffer("");
			// 显示内容为考勤管理范围内各部门所选日期范围内每一天的出勤人数，即每条曲线是一个部分，线上的点是该部门某天的出勤人数
			// 出勤人数：当天排班且排队不是休息班（id=0）的人员总数
			// 时间范围
    		String scope = "";
    		String scopeSql = "";
			if("-1".equals(weekIndex)) {
        		JSONArray weekList = jsonObj.getJSONArray("weekList");
        		// 长度减一  取消全部的范围
        		for(int i=0;i<weekList.size()-1;i++) {
        			scope = shiftService.getWeekScope(year, month, String.valueOf(i+1));
        			String fromdate = scope.split("-")[0];
        			String todate = scope.split("-")[1];
        			String strFrom = fromdate.replace(".", "-");
        			String strTo = todate.replace(".", "-");
        			scopeSql = "  and scheme.scope = '"+ scope + "' "
    						+" and Q03Z0>="+Sql_switcher.dateValue(strFrom)+" and Q03Z0<="+Sql_switcher.dateValue(strTo);
        			
        			sql.append(selectSql.toString()).append(whereSql.toString()).append(scopeSql).append(groupby);
        			if(i < weekList.size()-2)
        				sql.append(" union all ");
        		}
        	}else {
//    			// kq_shift_scheme排班方案表的 时间范围条件 与  Q03Z0时间范围条件
        		scope = shiftService.getWeekScope(year, month, weekIndex);
        		String fromdate = scope.split("-")[0];
    			String todate = scope.split("-")[1];
    			String strFrom = fromdate.replace(".", "-");
    			String strTo = todate.replace(".", "-");
    			scopeSql = "  and scheme.scope = '"+ scope + "' "
    						+" and Q03Z0>="+Sql_switcher.dateValue(strFrom)+" and Q03Z0<="+Sql_switcher.dateValue(strTo);
    			
            	sql.append(selectSql.toString()).append(whereSql.toString()).append(scopeSql).append(groupby);
        	}
			
        	ArrayList orglist = new ArrayList();
        	HashMap map = new HashMap();
        	ContentDAO dao = new ContentDAO(this.conn);
        	HashMap ondutyNumMap = new HashMap();
            rs = dao.search(sql.toString());
            while (rs.next()) {
            	map = new HashMap();
            	
            	String org_id = rs.getString("E0122");
            	if(StringUtils.isBlank(org_id))
            		continue;
            	String orgdesc = AdminCode.getCodeName("UM", org_id);
            	orgdesc = StringUtils.isBlank(orgdesc) ? AdminCode.getCodeName("UN", org_id) : orgdesc;
            	if(!orglist.contains(org_id+"`"+orgdesc) && StringUtils.isNotBlank(orgdesc))
            		orglist.add(org_id+"`"+orgdesc);
            	
            	String q03z0Str = "";
            	Object Q03Z0obj = rs.getObject("Q03Z0");
            	if(null!=Q03Z0obj && (Q03Z0obj instanceof Date || Q03Z0obj instanceof java.sql.Date)) {
            		
            		Date Q03Z0 = (Date) Q03Z0obj;
            		q03z0Str = DateUtils.format(Q03Z0, "yyyy.MM.dd");
            	}// 兼容SQL库获取date对象问题
            	else if(String.valueOf(Q03Z0obj).indexOf("-") != -1 || String.valueOf(Q03Z0obj).indexOf(".") != -1) {
            		
            		q03z0Str = String.valueOf(Q03Z0obj).replace("-", ".");
            	} else
            		continue;
            	
            	if(StringUtils.isNotBlank(q03z0Str)) {
            		int ondutyNum = rs.getInt("ondutyNum");
            		ondutyNumMap.put(org_id+"_"+q03z0Str, ondutyNum);
            	}
            }
            
            HashMap orgDataMap = new HashMap();
            for(int i=0;i<orglist.size();i++) {
            	String orgdesc = (String)orglist.get(i);
            	String orgid = orgdesc.split("`")[0];
            	ArrayList orgDataList = new ArrayList();
            	for(int j=0;j<datelist.size();j++) {
            		String dateStr = datelist.get(j);
            		String key = orgid + "_" + dateStr;
            		int ondutyNum = ondutyNumMap.containsKey(key) ? (Integer)ondutyNumMap.get(key) : 0;
            		
            		orgDataList.add(ondutyNum);
            	}
            	orgDataMap.put(orgid, orgDataList);
            }
            // 获取周集合
            ArrayList<HashMap<String, String>> weekList = shiftService.weekList(Integer.valueOf(year), Integer.valueOf(month));
            
            list.add(orglist);
            list.add(datelist);
            list.add(orgDataMap);
            list.add(weekList);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return list;
    }
    /**
     * 排班审查-导出工作分析表
     * exportWorkAnalysisTable
     * @param jsonObj
     * @return
     * @throws GeneralException
     * @date 2018年12月12日 下午2:00:58
     * @author linbz
     */
    @Override
    public String exportWorkAnalysisTable(JSONObject jsonObj) throws GeneralException{
    	
    	String fileName = this.userView.getUserName()+ "_" +"kq_" +ResourceFactory.getProperty("kq.shift.workanalysistable")+".xls";
        RowSet rs = null;
        try {
        	ArrayList<String> datesList = listScopeDate(jsonObj);
        	// 时间范围
        	String scope = datesList.get(0).split(":")[0] +"-"+ datesList.get(datesList.size()-1).split(":")[0];
        	String sql = this.getWorkAnalysisSql(datesList);
        	ArrayList<LazyDynaBean> headList = this.getWorkAnalysisHeadList();
        	// 正常上班人数统计表
        	ArrayList<LazyDynaBean> mergedCellList = this.listMergedCells(ResourceFactory.getProperty("kq.shift.workanalysistable"), scope, headList.size()-1);
        	// 导出工具类
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
            excelUtil.setHeadRowHeight((short)900);
            excelUtil.setRowHeight((short)600);
            
            ArrayList<LazyDynaBean> dateList = excelUtil.getExportData(headList, sql);
            excelUtil.exportExcel(fileName, null, mergedCellList, headList, dateList, null, 2);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return fileName;
    }
    /**
     * 排班审查-导出排班表
     * exportShiftTable
     * @param jsonObj
     * @return
     * @throws GeneralException
     * @date 2018年12月12日 下午2:01:16
     * @author linbz
     */
    @Override
    public String exportShiftTable(JSONObject jsonObj) throws GeneralException{
    	
    	String fileName = this.userView.getUserName()+ "_" + "kq_" +ResourceFactory.getProperty("kq.shift.detailtable")+".xls";
        RowSet rs = null;
        try {
        	String year = jsonObj.getString("year");
        	String month = jsonObj.getString("month");
        	String weekIndex = jsonObj.getString("weekIndex");
        	ArrayList<String> datesList = listScopeDate(jsonObj);
        	// 时间范围
        	String scope = datesList.get(0).split(":")[0] +"-"+ datesList.get(datesList.size()-1).split(":")[0];
			// 44980 更改获取排班 审查SQL方式
//			String dataSql = jsonObj.getString("dataSql");
			ShiftServiceImpl shift = new ShiftServiceImpl(this.userView, this.conn);
			// 列头集合
			this.columnInfoList = shift.getColumnList(Integer.valueOf(year), 
					Integer.valueOf(month), Integer.valueOf(weekIndex), "shiftCheck");
			
			String dataSql = this.getShiftCheckSql(scope, weekIndex);
			// 有序列codesetid
			String column = shift.getShiftcolumns(Integer.valueOf(year), Integer.valueOf(month),
			        Integer.valueOf(weekIndex), "shiftCheck");
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
			paramMap.put("groupId", "");
			paramMap.put("cloumns", cloumns);
			paramMap.put("dataSql", dataSql);
			paramMap.put("dataType", "shiftCheck");
			ArrayList<LazyDynaBean> dataList = shift.shiftDataList(paramMap);
			
			HashMap totalMap = new HashMap();
			// 根据columnJson 解析表头列<codeid:text>的map columnTextMap ； 以及数据列<codeid:text>的map：dateHeadSpMap
			totalMap = getColumnStringToMap(this.columnInfoList);
			HashMap columnTextMap = (HashMap) totalMap.get("columnTextMap");
			HashMap dateHeadSpMap = (HashMap) totalMap.get("dateHeadSpMap");
			
			// 获取表头列 的有序集合 headList
			ArrayList headList = getHeadList(cloumns);
			// 标题列头合并列 排班明细表
			KqPrivForHospitalUtil kp = new KqPrivForHospitalUtil(userView, conn);
			String orgid = kp.getPrivB0110();
			if((StringUtils.isEmpty(orgid) && this.userView.isSuper_admin()))
				orgid = kp.getTopUNCodeitemid();
			String unStr = AdminCode.getCodeName("UN", orgid);
			String orgDesc = StringUtils.isBlank(unStr) ? AdminCode.getCodeName("UM", orgid) : unStr;
			ArrayList<LazyDynaBean> mergedCellList = this.listMergedCells(orgDesc+ResourceFactory.getProperty("kq.shift.detailtable")
												, scope, headList.size()-1);
			// 处理数据列为 List<map<codeid:value>>类型; 以及 行高map
			HashMap dealdMap = getDataList(headList, dateHeadSpMap, dataList);
			HashMap rowHeightMap = (HashMap) dealdMap.get("rowHeightMap");
			ArrayList<HashMap> newDataList = (ArrayList<HashMap>) dealdMap.get("newDataList");
			
			exportShiftTableExcel(fileName, mergedCellList, headList ,newDataList ,columnTextMap ,dateHeadSpMap ,rowHeightMap);
			
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return fileName;
    }
    /**
     * 导出排班表数据方法
     * exportShiftTableExcel
     * @param fileName
     * @param headList
     * @param dataList
     * @param columnTextMap
     * @param dateHeadSpMap
     * @param rowHeightMap
     * @throws GeneralException
     * @date 2018年12月15日 下午1:54:53
     * @author linbz
     */
    private void exportShiftTableExcel(String fileName, ArrayList<LazyDynaBean> mergedCellList, ArrayList headList, ArrayList<HashMap> dataList
    		, HashMap columnTextMap, HashMap dateHeadSpMap, HashMap rowHeightMap) throws GeneralException{
    	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(1 + "");
		sheet.setDefaultColumnWidth(10);
		HSSFCellStyle rightStyle = getExpStyle("right", wb);
		HSSFCellStyle leftStyle = getExpStyle("left", wb);
		HashMap colIndexMap = new HashMap();
		for(int i=0;i<headList.size();i++){
			String cloumn = (String) headList.get(i);
			colIndexMap.put(cloumn, i);
		}
		// 取得第fromRowNum行
		int rowIndex = 0;
		HSSFRow row = null;
		// 导出数据
		for(int i = 0 ; i < dataList.size() ; i++){
			if (i == 0) {
				// 设置合并列头标题
				if (mergedCellList != null)
					addMergedCell(wb, sheet, mergedCellList, headList.size());
				// 设置表格列标题
				setShiftTableHead(wb, sheet, columnTextMap, headList, 2);
			}
			rowIndex = 2+1 + i;
			row = sheet.getRow(rowIndex);
			if(row==null)
				row = sheet.createRow(rowIndex);
			// 设置行高
			int height = 1;
			if(rowHeightMap.get(i)!=null)
				height = (Integer) rowHeightMap.get(i);
			height = height*300;
			if(height < 600)
				height = 600;
			row.setHeight((short) height);
			
			HashMap map = dataList.get(i);
			for(int j = 0 ; j<headList.size();j++){
				String cloumn = (String) headList.get(j);
				int colIndex = (Integer) colIndexMap.get(cloumn);
				HSSFCell cell = row.getCell(colIndex);
				if(cell==null)
					cell = row.createCell(colIndex);
				// 设置该单元格样式
				if("order".equals(cloumn))
					cell.setCellStyle(rightStyle);
				else
					cell.setCellStyle(leftStyle);
				// 给该单元格赋值
				cell.setCellValue(new HSSFRichTextString((String) map.get(cloumn)));
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
			PubFunc.closeIoResource(wb);
			wb = null;
		}
	}
    /**
     * 设置表格列标题
     * setHead
     * @param wb
     * @param sheet
     * @param columnTextMap
     * @param headList
     * @param headStartRowNum
     * @return
     * @throws GeneralException
     * @date 2018年12月15日 下午4:43:57
     * @author linbz
     */
    private int setShiftTableHead(HSSFWorkbook wb, HSSFSheet sheet, HashMap columnTextMap, ArrayList headList
    		, int headStartRowNum) throws GeneralException{
		int rowNum = 0;
		try {
			HSSFCellStyle style = getExpStyle("head", wb);
			HSSFRow row = sheet.getRow(2);
			if(row==null)
				row = sheet.createRow(2);
			for(int i=0;i<headList.size();i++){
				
				String cloumn = (String) headList.get(i);
				HSSFCell cell = row.getCell(i);
				if(cell==null)
					cell = row.createCell(i);
				// 设置该单元格样式
				cell.setCellStyle(style);
				// 给该单元格赋值
				cell.setCellValue(new HSSFRichTextString((String) columnTextMap.get(cloumn)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rowNum;
	}
    /**
     * 设置合并列头标题
     * addMergedCell
     * @param wb
     * @param sheet
     * @param mergedCellList
     * @param headSize
     * @throws GeneralException
     * @date 2018年12月15日 下午4:43:41
     * @author linbz
     */
    private void addMergedCell(HSSFWorkbook wb, HSSFSheet sheet, ArrayList<LazyDynaBean> mergedCellList
    		, int headSize) throws GeneralException{
		try {
			LazyDynaBean cellBean = null;
			int fromRowNum = 0;//合并单元格从第几行开始
			int toRowNum = 0;//合并单元格到地几行结束
			int fromColNum = 0;//合并单元格从第几列开始
			int toColNum = 0;//合并单元格到第几列结束
			for(int i=0;i < mergedCellList.size();i++){
				cellBean = mergedCellList.get(i);
				if (cellBean != null) {
					
					HSSFCellStyle style = getExpStyle("head", wb);
					HashMap titleStyleMap = (HashMap) cellBean.get("mergedCellStyleMap");
					
					int	fontSize = (Integer) titleStyleMap.get("fontSize");
					String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
					HSSFFont font = wb.createFont();
					font.setFontHeightInPoints((short) fontSize);
					font.setFontName(fontName);
					font.setBold(true);
					style.setFont(font);
					
					HorizontalAlignment align = (HorizontalAlignment) titleStyleMap.get("align");
					style.setAlignment(align);
					
					fromRowNum =  cellBean.get("fromRowNum")==null?0:(Integer)cellBean.get("fromRowNum");
					fromColNum = cellBean.get("fromColNum")==null?0:(Integer)cellBean.get("fromColNum");
					toRowNum = cellBean.get("toRowNum")==null?0:(Integer)cellBean.get("toRowNum");
					toColNum = cellBean.get("toColNum")==null?headSize-1:(Integer)cellBean.get("toColNum");
					String content = cellBean.get("content")==null?"":(String) cellBean.get("content");
					HSSFRow row = sheet.getRow(i);
					if(row==null)
						row = sheet.createRow(i);
					row.setHeight((short)600);
					setColMergedCell(row, content, fromColNum, toColNum, sheet, fromRowNum, style);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 合并单元格
	 * setColMergedCell
	 * @param row
	 * @param text
	 * @param fromCol
	 * @param toCol
	 * @param sheet
	 * @param rowIndex
	 * @param style
	 * @date 2018年12月15日 下午1:56:16
	 * @author linbz
	 */
	private void setColMergedCell(HSSFRow row, String text, int fromCol, int toCol, HSSFSheet sheet, int rowIndex, HSSFCellStyle style){
		
		HSSFCell cell = row.getCell(fromCol);
		if(cell==null)
			cell = row.createCell(fromCol);
		cell.setCellStyle(style);
		cell.setCellValue(new HSSFRichTextString(text));
		CellRangeAddress cra=new CellRangeAddress(rowIndex, rowIndex, fromCol, toCol);
		sheet.addMergedRegion(cra);
		RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
	}
	/**
	 * 获取单元格样式
	 * @param type
	 * @param wb
	 * @return
	 */
	private HSSFCellStyle getExpStyle(String type, HSSFWorkbook wb){
		HSSFCellStyle style = wb.createCellStyle();
		// 自动换行
		style.setWrapText(true);
		//设置border
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
		
		//字体
		int fontSize = 10;// 字体大小
		String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		font.setFontName(fontName);
		//对齐方式
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		if("head".equals(type)){
			font.setBold(true);// 加粗
		}else if("right".equals(type)){
			align = HorizontalAlignment.RIGHT;
			font.setBold(false);
		}else if("center".equals(type)){
			align = HorizontalAlignment.CENTER;
			font.setBold(false);
		}else{
			align = HorizontalAlignment.LEFT;
			font.setBold(false);
		}
		//对齐方式
		style.setAlignment(align);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);
		style.setWrapText(true);
		return style;
	}
	/**
	 * 获取导出排班明细数据
	 * getDataList
	 * @param headList
	 * @param dateHeadSpMap
	 * @param dataList
	 * @return
	 * @date 2018年12月15日 下午1:57:02
	 * @author linbz
	 */
    private HashMap getDataList(ArrayList headList, HashMap dateHeadSpMap, ArrayList<LazyDynaBean> dataList){
		HashMap returnMap = new HashMap();
		//格式化后的数据list
		ArrayList newDataList = new ArrayList();
		//每行的行高map<index,height>
		HashMap rowHeightMap = new HashMap();
		for(int i = 0;i<dataList.size();i++){
			LazyDynaBean databean = dataList.get(i);
			//列高
			int rowHeight = 1;
			HashMap rowMap = new HashMap();
			for(Object o:headList){
				String codeset = (String) o;
				String value = "";
				if(dateHeadSpMap.containsKey(codeset)){
					if(StringUtils.isNotBlank((String)databean.get(codeset)) && !"null".equals(databean.get(codeset).toString())){
						JSONArray shiftArray = JSONArray.fromObject(databean.get(codeset));
						for(int j = 0 ; j<shiftArray.size();j++){
							JSONObject shift = JSONObject.fromObject(shiftArray.get(j));
							if(j>0)
								value += "\r\n";
							// 班次备注
							String commentValue =  (String)shift.get("comment");
							value += (String)shift.get("className") + (StringUtils.isBlank(commentValue) ? "" : "（"+commentValue+"）");
						}
						if(shiftArray.size()>rowHeight){
							rowHeight = shiftArray.size();
						}
					}
				} else{
		       		if("order".equals(codeset))
		       			value = Integer.toString(i+1);
		       		else
		       			value = (String) databean.get(codeset);
		       	}
				rowMap.put(codeset,value);
			}
			rowHeightMap.put(i, rowHeight);
			newDataList.add(rowMap);
		}
		returnMap.put("rowHeightMap", rowHeightMap);
		returnMap.put("newDataList", newDataList);
		return returnMap;
	}
    /**
     * 格式化列头
     * getHeadList
     * @param cloumns
     * @return
     * @date 2018年12月15日 下午1:57:46
     * @author linbz
     */
    private ArrayList getHeadList(String[] cloumns){
		ArrayList headList = new ArrayList();
		LazyDynaBean bean = null;
		//添加order字段
		headList.add("order");
		for(String columnn:cloumns){
			if("guidkey".equals(columnn))
				continue;
			headList.add(columnn);
		}
		return headList;
	}
    /**
     * 列指标对应名称转为map
     * ColumnStringToMap
     * @param columnJson
     * @return
     * @date 2018年12月15日 下午1:58:10
     * @author linbz
     */
	private HashMap getColumnStringToMap(ArrayList<HashMap<String, String>> columnList){
		HashMap columnTextMap = new HashMap();
		HashMap dateHeadSpMap = new HashMap();//用来记录 数据列
		HashMap totalMap = new HashMap();
		
		//{itemDesc=单位名称, itemId=b0110} {itemDesc=01.02:周三, itemId=2019.01.02}
		for(HashMap<String, String> infoMap : columnList){
			String itemId = infoMap.get("itemId");
			String itemDesc = infoMap.get("itemDesc");
			if(itemDesc.indexOf(":") > -1){
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
	 * @param jsonstring
	 * @param returnList
	 * @return
	 */
	private ArrayList<String> getJsonList(String jsonstring,ArrayList returnList){
		if(jsonstring.indexOf("},{")>-1){
			returnList.add(jsonstring.substring(0, jsonstring.indexOf("},{")+1));
			jsonstring = jsonstring.substring(jsonstring.indexOf("},{")+2 );
			getJsonList(jsonstring,returnList);
		}else{
			if(!"".equals(jsonstring))
				returnList.add(jsonstring);
		}
		
		return returnList;
	}
    /**
     * 返回时间范围集合
     * listScopeDate
     * @param jsonObj
     * @return
     * @date 2018年12月14日 下午3:56:36
     * @author linbz
     */
    private ArrayList listScopeDate(JSONObject jsonObj) {
    	
    	String year = jsonObj.getString("year");
		String month = jsonObj.getString("month");
		String weekIndex = jsonObj.getString("weekIndex");
		Calendar cal = Calendar.getInstance();
		if(StringUtils.isEmpty(year))
			year = String.valueOf(cal.get(Calendar.YEAR));
		
		if(StringUtils.isEmpty(month))
			month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		
		if(StringUtils.isEmpty(weekIndex))
			weekIndex = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
		
		ArrayList<String> datelist = ShiftServiceImpl.getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month)
		        , Integer.valueOf(weekIndex));
		
		return datelist;
    }
    /**
     * 获取导出下拉集合
     * getDropDownMap
     * @return
     * @throws GeneralException
     * @date 2018年12月13日 下午2:06:11
     * @author linbz
     */
    private HashMap getDropDownMap() throws GeneralException{
    	
    	HashMap<String, ArrayList<String>> dropDownMap = new HashMap<String, ArrayList<String>>();
    	
    	StringBuffer sql = new StringBuffer("");
    	sql.append("select codeitemdesc from organization");
    	sql.append(" where codesetid='UN' or codesetid='UM'");
    	sql.append(" and ").append(Sql_switcher.isnull("invalid", "1")).append("='1'");
    	RowSet rs = null;
    	ArrayList<String> desclist = new ArrayList<String>();
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs=dao.search(sql.toString());
            while(rs.next()){
                String codeitemdesc=rs.getString("codeitemdesc");
                desclist.add(codeitemdesc);
            }
            dropDownMap.put("E0122", desclist);
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        
        return dropDownMap;
    }
    /**
     * 获取工作分析表导出SQL
     * getWorkAnalysisSql
     * @param datelist
     * @return
     * @date 2018年12月13日 下午2:10:00
     * @author linbz
     */
    private String getWorkAnalysisSql(ArrayList<String> datelist) {
    	String sqlAll = "";
    	try {
    		StringBuffer sql = new StringBuffer("");
	    	String strFrom = datelist.get(0).split(":")[0].replace(".", "-");
	        String strTo = datelist.get(datelist.size()-1).split(":")[0].replace(".", "-");
	        KqPrivForHospitalUtil kqPriv = new KqPrivForHospitalUtil(userView, conn);
	        // 考勤部门指标
	        String kqDeptField = kqPriv.getKqDeptField();
	        boolean isNotBanlkKqDept = StringUtils.isNotBlank(kqDeptField);
	        String whereInKqDept = "1=1";
	        if(isNotBanlkKqDept)
	        	whereInKqDept = KqPrivForHospitalUtil.getPrivB0110Whr(userView, kqDeptField, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	        String whereInb0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	        String whereInE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	        String whereInE01A1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E01A1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	ArrayList<String> dbnameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
	        StringBuffer a01sql = new StringBuffer("");
	        for(int i = 0; i < dbnameList.size(); i++){
	            String dbname = dbnameList.get(i);
	            if(i > 0)
	            	a01sql.append(" UNION ALL ");
	            // 暂时不考虑高级权限
//	            String whereIN = KqPrivBo.getWhereINSql(userView, dbname);
	            a01sql.append(" select guidkey,E0122 ");
	            a01sql.append(" from " + dbname +"A01 WHERE");
	            a01sql.append(" ("+ whereInb0110);
	            a01sql.append(" or "+ whereInE0122);
	            a01sql.append(" or "+ whereInE01A1);
				if (!userView.isSuper_admin()) 
					a01sql.append(" or "+ whereInKqDept);
				a01sql.append(") ");
	        }
	     	    	
	    	sql.append("select ");
	    	sql.append("E0122, a.name,Q03Z0,COUNT(Q03Z0) ondutyNum");
	    	sql.append(" from kq_shift_group a");
	    	sql.append(" left join kq_group_emp_v2 b on a.group_id=b.Group_id");
	    	sql.append(" left join kq_employ_shift_v2 q on b.Guidkey=q.guidkey");
	    	sql.append(" left join ");
	    	sql.append(" ( ");
	    	// 通过主集guidkey查
	    	sql.append(a01sql.toString());
	    	sql.append(" ) z on q.guidkey=z.guidkey");
	    	sql.append(" where");
	    	// Q03Z0时间范围条件
	    	sql.append(" Q03Z0>="+Sql_switcher.dateValue(strFrom)+" and Q03Z0<="+Sql_switcher.dateValue(strTo));
	    	// 班次不是0 不为null 则定义为出勤
	    	sql.append(" and (");
	    	sql.append(" (Class_id_1<>0 and Class_id_1 is not null");
	    	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
	    		sql.append(" and Class_id_1<>'' ");
	    	sql.append(")");
	    	sql.append(" or (Class_id_2<>0 and Class_id_2 is not null");
	    	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
	    		sql.append(" and Class_id_2<>'' ");
	    	sql.append(")");
	    	sql.append(" or (Class_id_3<>0 and Class_id_3 is not null");
	    	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
	    		sql.append(" and Class_id_3<>'' ");
	    	sql.append(")");
	    	sql.append(")");
	    	sql.append(" group by E0122,a.name,Q03Z0");
	    	
    		sqlAll = "select ";
	    	if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
	    		sqlAll += "ROW_NUMBER() OVER(ORDER BY E0122) ROWNUM,";
	    	else if(Constant.ORACEL == Sql_switcher.searchDbServer()) 
	    		sqlAll += "ROWNUM,";
	    	sqlAll += "E0122, name, Q03Z0, ondutyNum from ("+sql.toString()+") z";
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return sqlAll;
    }
    /**
     * 获取列表 表头
     * listMergedCells
     * @return
     * @date 2018年12月12日 下午6:32:14
     * @author linbz
     */
    private ArrayList<LazyDynaBean> listMergedCells(String title, String scope, int size) {
    	
    	ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
    	LazyDynaBean bean = new LazyDynaBean();
    	bean.set("content", title);
    	bean.set("fromRowNum", 0);
    	bean.set("toRowNum", 0);
    	bean.set("fromColNum", 0);
    	bean.set("toColNum", size);
    	HashMap<String, Object> styleMap = new HashMap<String, Object>();// 样式
    	styleMap.put("align", HorizontalAlignment.CENTER);
		styleMap.put("fontSize", 14);// 字号
		styleMap.put("isFontBold", true);
//		styleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
		bean.set("mergedCellStyleMap", styleMap);
		mergedCellList.add(bean);
		
		bean = new LazyDynaBean();
    	bean.set("content", "时间范围："+scope);
    	bean.set("fromRowNum", 1);
    	bean.set("toRowNum", 1);
    	bean.set("fromColNum", 0);
    	bean.set("toColNum", size);
    	styleMap = new HashMap<String, Object>();// 样式
    	styleMap.put("align", HorizontalAlignment.LEFT);
		styleMap.put("fontSize", 10);
		styleMap.put("isFontBold", true);
		bean.set("mergedCellStyleMap", styleMap);
		mergedCellList.add(bean);
		
    	return mergedCellList;
    }
    /**
     * 获取工作分析表列头
     * getHeadList
     * @return
     * @date 2018年12月13日 下午2:10:58
     * @author linbz
     */
    private ArrayList<LazyDynaBean> getWorkAnalysisHeadList() {
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
//        ROWNUM, E0122, a.name,Q03Z0,COUNT(Q03Z0) ondutyNum
//        序号		部门		班组      日期			人数
        LazyDynaBean bean = new LazyDynaBean();
        bean.set("itemid", "ROWNUM");
        bean.set("content", "序号");
        bean.set("codesetid", "0");
        bean.set("colType", "N");
        headList.add(bean);
        
        bean = new LazyDynaBean();
        bean.set("itemid", "E0122");
        bean.set("content", "部门");
        bean.set("codesetid", "UM");
        bean.set("colType", "A");
        headList.add(bean);
        
        bean = new LazyDynaBean();
        bean.set("itemid", "name");
        bean.set("content", "班组");
        bean.set("codesetid", "0");
        bean.set("colType", "A");
        headList.add(bean);
        
        bean = new LazyDynaBean();
        bean.set("itemid", "Q03Z0");
        bean.set("content", "日期");
        bean.set("codesetid", "0");
        bean.set("colType", "D");
        bean.set("dateFormat", "yyyy-MM-dd");
        headList.add(bean);
        
        bean = new LazyDynaBean();
        bean.set("itemid", "ondutyNum");
        bean.set("content", "人数");
        bean.set("codesetid", "0");
        bean.set("colType", "N");
//        bean.set("decwidth", "");
        headList.add(bean);
        
        return headList;
    }
    
}

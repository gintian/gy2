package com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class ShiftServiceImpl implements ShiftService {
	private UserView userView;
	private Connection conn = null;
	//数据总条数
	private int totalCount = 0;
	//是否是发布状态
	private String pushscheme = "false";
	//班组名称
	private String groupName = "";
	//年
	private int year;
	//月
	private int month;
	//第几周
	private int weekIndex;
	//调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	private String dataType = "shiftData";
	//考勤方案对应的时间范围
	private ArrayList<String> dateScopeList = new ArrayList<String>();
	//需要统计数据的列统计后的值
	private HashMap<String, HashMap<String, String>> countDataMap = new HashMap<String, HashMap<String, String>>();
	//页面显示的列的map
	private LinkedHashMap<String, ColumnsInfo> columnMap = new LinkedHashMap<String, ColumnsInfo>();
	//页面显示的列的list
	private ArrayList<ColumnsInfo> columnInfoList = new ArrayList<ColumnsInfo>();
	//每页数据的条数
	private int pageSize = 20;
	
	public ShiftServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 获取排序表格的列
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 * @return
	 */
	@Override
    public String getShiftcolumns(int year, int month, int weekIndex, String dataType) {
		this.year = year;
		this.month = month;
		this.weekIndex = weekIndex;
		this.dataType = dataType;
		getPanelColumnsSetting();
		StringBuffer columns = new StringBuffer("[");
		try {
			for(ColumnsInfo info : this.columnInfoList) {
				String columnId = info.getColumnId();
				if(info.getLoadtype() == ColumnsInfo.LOADTYPE_HIDDEN)
				    continue;
				
				columns.append("'" + columnId + "',");
				if("a0101".equalsIgnoreCase(columnId))
					columns.append("'guidkey',");
				
			}
			
			if(columns.length() > 1)
				columns.setLength(columns.length() - 1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		columns.append("]");
		return columns.toString();
	}

	/**
	 * 获取排序表格的列的json
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param dataType
	 *            调用的页面参数=shiftData：排班页面;=shiftCheck：排班审查页面
	 * @return
	 */
	@Override
    public String getShiftcolumnsJson(int year, int month, int weekIndex, String dataType) {
		this.year = year;
		this.month = month;
		this.weekIndex = weekIndex;
		this.dataType = dataType;
		getPanelColumnsSetting();
		StringBuffer columnJson = new StringBuffer("[");
		try {
			boolean shiftPivFlag = this.userView.hasTheFunction("27202020402");
			ArrayList<String> dayList = getWeekDateByWeekInMonth(this.year, this.month, this.weekIndex);
			columnJson.append("{xtype: 'rownumberer',dataIndex:'rownumber',width:40,text:'");
			columnJson.append(ResourceFactory.getProperty("label.serialnumber"));
			columnJson.append("',height:75,sortable:false,locked:true}");
			for(ColumnsInfo info : this.columnInfoList) {
				String columnId = info.getColumnId();
				String columnDesc = info.getColumnDesc();
				if(ColumnsInfo.LOADTYPE_HIDDEN == info.getLoadtype())
					continue;
				
				columnJson.append(",{dataIndex:'" + columnId + "'");
				if(columnId.indexOf(".") > -1) {
					for (String dayColumn : dayList) {
						String[] dayColumns = dayColumn.split(":");
						if(!columnId.equals(dayColumns[0]))
							continue;
						
						String dateDesc = dayColumns[1].split("<br>")[1];
						String dayDesc = dayColumns[1].split("<br>")[0];
						columnDesc = "<div style=\"height:30px;line-height:30px;color:" + dayColumns[2] + "\">" + dateDesc
								+ "</div><div style=\"height:30px;border-top:1px solid #c5c5c5;line-height:30px;color:" + dayColumns[2] + "\">"
								+ dayDesc + "</div>";
						break;
					}
				}
				
				columnJson.append(",text:'" + columnDesc + "'");
				columnJson.append(",draggable:false,height:75,filterable:true,hideable:false");
				columnJson.append(",locked:" + info.isLocked());
				columnJson.append(",columnType:'" + info.getColumnType() + "'");
				columnJson.append(",align:'" + info.getTextAlign() + "'");
				columnJson.append(",codesetid:'" + info.getCodesetId() + "'");
				columnJson.append(",width:" + info.getColumnWidth());
				if(",UN,UM,@K,".contains("," + info.getCodesetId() + ","))
					columnJson.append(",nmodule:'11'");
				
				if("a0101".equalsIgnoreCase(columnId) && -1 != weekIndex && "shiftData".equalsIgnoreCase(dataType)) {
					columnJson.append(",renderer: function (value, c, record, rowIndex, colIndex){");
					columnJson.append("var guidkey = record.get('guidkey');");
					columnJson.append("var rowNumnber = record.get('rownumber');");
					columnJson.append("return '<div onmouseover=\"shiftGrid.changeIcon(' + rowNumnber + ', 1)\"" 
							+ " onmouseout=\"shiftGrid.changeIcon(' + rowNumnber + ', 0)\">'"
							+ " + value + '<img src=\"../../../../module/kq/images/shift_change.png\" title=\"' + kq.shift.shiftChange + '\""
							+ " align=\"absmiddle\" style=\"display:none;float:right;cursor:pointer;\" id=\"img_' + rowNumnber + '\""
							+ " onclick=\"shiftGrid.showPersonChange(\\\''+guidkey+'\\\')\"/></div>'}");
				} else if(",group_name,extra_days,".contains("," + columnId +","))
					columnJson.append(",format:'text',editor:new Ext.form.TextField()");
				else if("shift_comment".equalsIgnoreCase(columnId))
					columnJson.append(",format:'text',editor:new Ext.form.field.TextArea()");
				
				for (String dayColumn : dayList) {
					if(!dayColumn.split(":")[0].equalsIgnoreCase(columnId))
						continue;
					
					columnJson.append(",renderer: function (value, c, record, rowIndex, colIndex){");
					columnJson.append("var info = Ext.create('ShiftURL.ShiftInfo', {regionSelect:shiftGrid.regionSelect,");
					columnJson.append("data:record,rowIdx:rowIndex,colIdx:colIndex,dataIndex:'" + columnId);
					columnJson.append("',dataType:'" + dataType + "',shiftPivFlag:" + shiftPivFlag + "});return info.getHtml();},sortable:false");
				}
				
				columnJson.append("}");
			}
			
			columnJson.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnJson.toString();
	}

	/**
	 * 获取快速查询的查询条件
	 * 
	 * @param type
	 *            类型
	 * @param valuesList
	 *            查询的条件
	 * @param exp
	 * @param cond
	 */
	@Override
    public void filterSql(String type, ArrayList<String> valuesList, String exp, String cond) {
		try {
			String where = "";
			if ("1".equals(type)) {
				// 输入的内容
				if (valuesList.size() > 0)
					where = getWhereSql(valuesList);

			} else if ("2".equals(type)) {
				// 解析表达式并获得sql语句
				FactorList parser = new FactorList(SafeCode.decode(exp), PubFunc.keyWord_reback(SafeCode.decode(cond)),
				        userView.getUserName());
				where = parser.getSingleTableSqlExpression("kq3");
				if (StringUtils.isEmpty(exp))
					where = "1=1";
			}

			this.userView.getHm().put("shiftWhere", where);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取查询条件
	 * 
	 * @param valuesList
	 *            输入的内容
	 * @return
	 */
	private String getWhereSql(ArrayList<String> valuesList) {
		StringBuffer where = new StringBuffer();
		try {
			HashMap paramMap = KqPrivForHospitalUtil.getKqParameter(this.conn);
			String gNo = (String) paramMap.get("g_no");
			for (int i = 0; i < valuesList.size(); i++) {
				String sqlwhere = valuesList.get(i);
				if (StringUtils.isEmpty(sqlwhere))
					continue;

				sqlwhere = SafeCode.decode(sqlwhere);
				if (StringUtils.isEmpty(sqlwhere))
					continue;

				if (where != null && where.length() > 1)
					where.append(" OR");

				where.append(" a01.a0101 like '%" + sqlwhere + "%'");
				if (StringUtils.isNotEmpty(gNo))
					where.append(" OR a01." + gNo + " like '%" + sqlwhere + "%'");
			}

			if (where == null || where.length() < 1)
				where.append("");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return where.toString();
	}

	/**
	 * 获取每周的日期
	 * 
	 * @param year
	 * @param month
	 * @param weekIndex
	 * @return
	 */
	public static ArrayList<String> getWeekDateByWeekInMonth(int year, int month, int weekIndex) {
		ArrayList<String> dayList = new ArrayList<String>();
		int dayCount = 7;
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		if (-1 == weekIndex) {
			cal.set(Calendar.DATE, 1);
			dayCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		} else {
			cal.set(Calendar.WEEK_OF_MONTH, weekIndex);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}

		int[] days = new int[dayCount];
		for (int i = 0; i < dayCount; i++) {
			days[i] = cal.get(Calendar.DAY_OF_MONTH);
			int weekDay = cal.get(Calendar.DAY_OF_WEEK);
			String dayDesc = "";
			if (1 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zri");
			else if (2 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zyi");
			else if (3 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zer");
			else if (4 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zsan");
			else if (5 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zsi");
			else if (6 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zwu");
			else if (7 == weekDay)
				dayDesc = ResourceFactory.getProperty("kq.date.column.zliu");

			StringBuffer day = new StringBuffer();
			day.append(DateUtils.format(cal.getTime(), "yyyy.MM.dd"));
			day.append(":" + dayDesc + "<br>");
			day.append(DateUtils.format(cal.getTime(), "MM.dd"));
			if (1 == weekDay || 7 == weekDay)
				day.append(":" + "#2DC02D");
			else
				day.append(":" + "#000000");
			
			dayList.add(day.toString());
				
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}

		return dayList;
	}

	/**
	 * 获取排班数据
	 * 
	 * @param paramMap
	 *            参数集合：{year:年,month:月,weekIndex:第几周,imit:每页的条数,page:第几页,
	 *            groupId:班组id,cloumns:列,dataSql:查询数据的sql,
	 *            dataType 调用的页面参数=shiftData：排班页面;=shiftCheck排班审查}
	 * @return
	 */
	@Override
    public ArrayList<LazyDynaBean> shiftDataList(HashMap<String, Object> paramMap) {
		ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
		int totalCount = 0;
		try {
			String year = (String) paramMap.get("year");
			String month  = (String) paramMap.get("month");
			String weekIndex = (String) paramMap.get("weekIndex");
			String limit = (String) paramMap.get("limit");
			String page = (String) paramMap.get("page");
			String groupId = (String) paramMap.get("groupId");
			String[] cloumns = (String[]) paramMap.get("cloumns");
			String dataSql = (String) paramMap.get("dataSql");
			// 44980 更改获取排班 审查SQL方式
//			dataSql = StringUtils.isNotEmpty(dataSql) ? PubFunc.decrypt(dataSql) : "";
			String dataType = (String) paramMap.get("dataType");
			String filterParam = (String) paramMap.get("filterParam");
			String where = (String) this.userView.getHm().get("shiftFilterWhere");
			if(StringUtils.isNotEmpty(filterParam)) {
				where = getFilterWhere(filterParam);
				this.userView.getHm().put("shiftFilterWhere", where);
			}
			
			this.year = Integer.valueOf(year);
			this.month = Integer.valueOf(month);
			this.weekIndex = Integer.valueOf(weekIndex);
			this.dataType = dataType;
			getPanelColumnsSetting();
				
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month),
			        Integer.valueOf(weekIndex));

			String[] dates = new String[dateList.size()];
			for (int i = 0; i < dateList.size(); i++) {
				String date = dateList.get(i);
				dates[i] = date.split(":")[0];
			}

			String startDate = dates[0];
			String endDate = dates[dates.length - 1];
			String orderby = " order by display_Id";
			if ("shiftCheck".equalsIgnoreCase(dataType))
				orderby = " order by a0000,b0110,e0122";
			
			if (StringUtils.isEmpty(dataType) || "shiftData".equalsIgnoreCase(dataType))
				dataSql = this.getSql(startDate + "-" + endDate, groupId, weekIndex);
			else if("shiftCheck".equalsIgnoreCase(dataType)) {
				// 44980 由于获取全月SQL导致长度过长传参失败，这里直接在后台获取相应SQL
				String dateScope = dateList.get(0).split(":")[0] +"-"+ dateList.get(dateList.size()-1).split(":")[0];
				dataSql = this.getShiftCheckSql(dateScope, weekIndex);
			}

			if(StringUtils.isNotEmpty(where))
				dataSql += where;
			
			PaginationManager paginationm = new PaginationManager(dataSql, "", "", orderby, cloumns, "");
			paginationm.setBAllMemo(true);
			paginationm.setPagerows(Integer.valueOf(limit));
			totalCount = paginationm.getMaxrows();
			int rownumber = Integer.valueOf(limit) * (Integer.valueOf(page) -1);
			dataList = (ArrayList<LazyDynaBean>) paginationm.getPage(Integer.valueOf(page));
			if (dataList.isEmpty() && Integer.valueOf(page) != 1) {
				rownumber = Integer.valueOf(limit) * (Integer.valueOf(page) -2);
				dataList = (ArrayList<LazyDynaBean>) paginationm.getPage(Integer.valueOf(page) - 1);
			}

			String guidKeys = "";
			for (LazyDynaBean bean : dataList) {
				String guidKey = (String) bean.get("guidkey");
				if (StringUtils.isNotEmpty(guidKeys))
					guidKeys += ",";

				guidKeys += guidKey;
			}

			HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> shiftMap = getShiftMap(startDate,
			        endDate, guidKeys, groupId, weekIndex, dataType);
			ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
			for (LazyDynaBean bean : dataList) {
				String guidKey = (String) bean.get("guidkey");
				HashMap<String, ArrayList<HashMap<String, String>>> shiftInfoMap = shiftMap.get(guidKey);
				for (int i = 0; i < dates.length; i++) {
					String date = dates[i];
					if (shiftInfoMap != null && shiftInfoMap.size() > 0) {
						ArrayList<HashMap<String, String>> shift = shiftInfoMap.get(date);
						StringBuffer shiftStr = new StringBuffer();
						if(shift != null && shift.size() > 0) {
							shiftStr.append("[");
							for(HashMap<String, String> map : shift) {
								shiftStr.append("{");
								for (Entry<String, String> entry : map.entrySet()) 
									shiftStr.append(entry.getKey() + ":'" + entry.getValue() + "',"); 
								
								if(shiftStr.toString().endsWith(","))
									shiftStr.setLength(shiftStr.length() - 1);
								
								shiftStr.append("},");
							}
							
							if(shiftStr.toString().endsWith(","))
								shiftStr.setLength(shiftStr.length() - 1);

							shiftStr.append("]");
						}
						
						bean.set(date, shiftStr.toString());
					} else {
						bean.set(date, "");
					}
				}

				bean.set("guidkey", PubFunc.encrypt(guidKey));
				
				for(ColumnsInfo info : this.columnInfoList) {
					String codeSetId = info.getCodesetId();
					if(StringUtils.isEmpty(codeSetId) || "0".equalsIgnoreCase(codeSetId))
						continue;
					
					String columnId = info.getColumnId();
					String codeItemId = (String) bean.get(columnId);
					String codeItemDesc = AdminCode.getCodeName(codeSetId, codeItemId);
					if("UN".equalsIgnoreCase(codeSetId) && StringUtils.isEmpty(codeItemDesc))
						codeItemDesc = AdminCode.getCodeName("UM", codeItemId);
					else if("UM".equalsIgnoreCase(codeSetId) && StringUtils.isEmpty(codeItemDesc))
						codeItemDesc = AdminCode.getCodeName("UN", codeItemId);
					
					bean.set(columnId, codeItemDesc);
				}

				for (HashMap<String, String> map : shiftStatisticList) {
					String statistic = map.get("statisticsType");
					if (bean.get("stat_" + statistic) != null) {
						String statValue = (String) bean.get("stat_" + statistic);
						double value = 0.0;
						if (StringUtils.isNotEmpty(statValue))
							value = Double.valueOf(statValue);

						String unit = map.get("unit");
						if ("01".equals(unit))
							value = value / 60.0;
						else if ("02".equals(unit))
							value = value / 60.0 / 8.0;

						bean.set("stat_" + statistic, PubFunc.round(value + "", 1));
					}
				}
				rownumber++;
				bean.set("rownumber", rownumber);
			}

			if ("-1".equals(weekIndex)) {
				HashMap<String, HashMap<String, Double>> statisticsMap = statClasses(groupId, startDate, endDate,
				        guidKeys, dataType);
				for (LazyDynaBean bean : dataList) {
					String guidKey = (String) bean.get("guidkey");
					guidKey = PubFunc.decrypt(guidKey);
					HashMap<String, Double> statMap = statisticsMap.get(guidKey);
					if (statMap == null || statMap.size() < 1)
						continue;

					bean.set("work_hour", PubFunc.round((statMap.get("workHours") / 60.0) + "", 1));
					for (HashMap<String, String> map : shiftStatisticList) {
						String statistic = map.get("statisticsType");
						if (statMap.get(statistic) != null) {
							double value = statMap.get(statistic);
							String unit = map.get("unit");
							if ("01".equals(unit))
								value = value / 60.0;
							else if ("02".equals(unit))
								value = value / 60.0 / 8.0;

							bean.set("stat_" + statistic, PubFunc.round(value + "", 1));
						}
					}
				}
			}

			this.setTotalCount(totalCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}

	/**
	 * 获取查询排班信息的sql
	 * 
	 * @param dateScope
	 *            时间范围
	 * @param groupId
	 *            班组编号
	 * @return
	 */
	private String getSql(String dateScope, String groupId, String weekindex) {
		StringBuffer sql = new StringBuffer();
		try {
			StringBuffer columns = new StringBuffer();
			StringBuffer searchColumns = new StringBuffer();
			StringBuffer fromSql = new StringBuffer("#dbname#A01 A01");
			HashMap<String, String> itemMap = new HashMap<String, String>();
			for(ColumnsInfo info : this.columnInfoList) {
				if(StringUtils.isNotEmpty(info.getFieldsetid())) {
					columns.append(info.getColumnId() + ",");
					searchColumns.append(info.getFieldsetid() + "." + info.getColumnId() + ",");
					String fieldSetId = info.getFieldsetid();
					if(StringUtils.isNotEmpty(fieldSetId) && !"A01".equalsIgnoreCase(fieldSetId)) {
						if(itemMap.containsKey(fieldSetId)) {
							String items = itemMap.get(fieldSetId);
							items += "," + info.getColumnId();
							itemMap.put(fieldSetId, items);
						} else
							itemMap.put(fieldSetId, info.getColumnId());
					}
				}
			}
			
			for (Map.Entry<String, String> entry : itemMap.entrySet()) {
				String fieldSetId = entry.getKey();
				String fielditemes = entry.getValue();
				fromSql.append(" left join (select a0100," + fielditemes + " from #dbname#" + fieldSetId);
				fromSql.append(" a where exists (");
				fromSql.append("select 1 from (select MAX(I9999) I9999,A0100 from #dbname#" + fieldSetId);
				fromSql.append(" group by A0100) b where a.A0100=b.A0100 and a.I9999=b.I9999))" + fieldSetId);
				fromSql.append(" on A01.A0100=" + fieldSetId + ".a0100");
			}
			
			ArrayList<String> dbNames = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			StringBuffer sqlTemp = new StringBuffer();
			for (String nbase : dbNames) {
				if (StringUtils.isNotEmpty(sqlTemp.toString()))
					sqlTemp.append(" union all ");

				sqlTemp.append("select " + searchColumns + " A01.GUIDKEY");
				sqlTemp.append(" from " + fromSql.toString().replace("#dbname#", nbase));
			}

			ArrayList<String> dateScopeList = dateScopeList(groupId, dateScope, "shiftData");
			if ("-1".equals(weekindex))
				dateScope = dateScopeList.get(0);

			sql.append("select " + columns);
			sql.append("emp.* from (");
			sql.append(sqlTemp);
			sql.append(") a01,kq_shift_scheme scheme,kq_shift_scheme_emp emp");

			sql.append(" where emp.guidkey=a01.GUIDKEY and emp.Scheme_id=scheme.Scheme_id");
			sql.append(" and scheme.scope='" + dateScope + "' and scheme.Group_id='" + groupId + "'");
			String where = (String) this.userView.getHm().get("shiftWhere");
			if (StringUtils.isNotEmpty(where)) {
				sql.append(" and (" + where + ")");
//				this.userView.getHm().remove("shiftWhere");
			}
			
			if ("-1".equals(weekindex)) {
				for(int i = 1; i < dateScopeList.size(); i++) {
					dateScope = dateScopeList.get(i);
					sql.append(" union all ");
					sql.append("select " + columns);
					sql.append("emp.* from (");
					sql.append(sqlTemp);
					sql.append(") a01,kq_shift_scheme scheme,kq_shift_scheme_emp emp");

					sql.append(" where emp.guidkey=a01.GUIDKEY and emp.Scheme_id=scheme.Scheme_id");
					sql.append(" and scheme.scope='" + dateScope + "' and scheme.Group_id='" + groupId + "'");
					if (StringUtils.isNotEmpty(where))
						sql.append(" and (" + where + ")");
					
					sql.append(" and emp.guidkey not in (select guidkey from kq_shift_scheme_emp emp1,kq_shift_scheme scheme1");
					sql.append(" where emp1.scheme_id=scheme1.scheme_id and scheme1.scope in ('#'");
					for(int n = 0; n < i; n++)
						sql.append(",'" + dateScopeList.get(n) + "'");
					
					sql.append(") and scheme1.Group_id='" + groupId + "')");
				}
				
				sql.insert(0, "select * from (");
				sql.append(") kqEmpShift where 1=1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();
	}

	/**
	 * 获取全月中有效班次的时间范围
	 * 
	 * @param groupId
	 *            班组编号
	 * @param dateScope
	 *            全月的时间范围
	 * @return
	 */
	public ArrayList<String> dateScopeList(String groupId, String dateScope, String dataType) {
		ArrayList<String> dateScopeList = new ArrayList<String>();
		RowSet rs = null;
		try {
			if(this.dateScopeList != null && this.dateScopeList.size() > 0)
				return this.dateScopeList;
			
			int year = Integer.valueOf(dateScope.split("-")[0].split("[.]")[0]);
			int month = Integer.valueOf(dateScope.split("-")[0].split("[.]")[1]);
			int weekcount = weekCount(year, month);
			String scopes = "";
			ArrayList<String> scopeList = new ArrayList<String>();
			for (int i = 1; i <= weekcount; i++) {
				ArrayList<String> dateList = getWeekDateByWeekInMonth(year, month, i);
				scopeList.add(dateList.get(0).split(":")[0] + "-" + dateList.get(dateList.size() - 1).split(":")[0]);
				scopes += ",?";
			}

			StringBuffer schemeSql = new StringBuffer();
			schemeSql.append("select distinct scope from kq_shift_scheme");
			schemeSql.append(" where scope in ('#'");
			schemeSql.append(scopes);
			schemeSql.append(")");
			// linbz 兼容排班审查
			if(StringUtils.isNotBlank(groupId) && "shiftData".equalsIgnoreCase(dataType)) {
				schemeSql.append(" and Group_id=? ");
				scopeList.add(groupId);				
			}else if ("shiftCheck".equalsIgnoreCase(dataType)){
				schemeSql.append(" and state='04' ");
			}
			schemeSql.append(" order by scope desc");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(schemeSql.toString(), scopeList);
			while (rs.next())
				dateScopeList.add(rs.getString("scope"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		this.dateScopeList = dateScopeList;
		return dateScopeList;
	}

	/**
	 * 获取班组的所属机构
	 * 
	 * @param GroupId
	 *            班组编号
	 * @return
	 */
	private String getOrgId(String GroupId) {
		String orgId = "";
		RowSet rs = null;
		try {
			String sql = "select org_id from kq_shift_group where group_id=?";
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(GroupId);
			rs = dao.search(sql, paramList);
			if (rs.next())
				orgId = rs.getString("org_id");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return orgId;
	}

	/**
	 * 获取每天的排班信息
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> getShiftMap(String startDate,
	        String endDate, String guidKeys, String groupId, String weekIndex, String dataType) {
		HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> shiftMap = new 
				HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
		RowSet rs = null;
		try {
			if (StringUtils.isEmpty(guidKeys))
				return shiftMap;

			HashMap<String, String> classMap = classMap();
			StringBuffer sql = new StringBuffer();
			ArrayList<String> paramList = new ArrayList<String>();
			sql.append("select " + Sql_switcher.dateToChar("Q03Z0", "yyyy.MM.dd") + " as Q03Z0,");
			sql.append("emp.guidkey,Class_id_1,Class_id_2,Class_id_3,Comment_1,Comment_2,Comment_3,");
			sql.append("Comment_color_1,Comment_color_2,Comment_color_3,scheme.state");
			sql.append(" from kq_employ_shift_v2 shift,kq_shift_scheme scheme,kq_shift_scheme_emp emp");
			sql.append(" where shift.guidkey=emp.guidkey and emp.Scheme_id=scheme.Scheme_id");
			// linbz 兼容排班审查
			if(StringUtils.isNotBlank(groupId) && "shiftData".equalsIgnoreCase(dataType)) {
				sql.append(" and scheme.group_id=? ");
				paramList.add(groupId);
			}
			
			if("shiftCheck".equalsIgnoreCase(dataType))
				sql.append(" and scheme.state='04' ");
			
			if("-1".equals(weekIndex)) {
				sql.append(" and scheme.scope in ('#'");
				
				ArrayList<String> dateScopeList = dateScopeList(groupId, startDate, dataType);
				String dateSql = "";
				ArrayList<String> dateList = new ArrayList<String>();
				for(String dateScope : dateScopeList) {
					paramList.add(dateScope);
					sql.append(",?");
					
					String[] dates = StringUtils.split(dateScope, "-");
					dateList.add(dates[0].replace(".", "-"));
					dateList.add(dates[1].replace(".", "-"));
					dateSql += " or (" + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + ">=?"
							+" and " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + "<=?)";
				}
				sql.append(")");
				// kq_employ_shift_v2时间范围条件
				if(StringUtils.isNotBlank(dateSql)) {
					sql.append(" and (" + dateSql.substring(4) +")");
					paramList.addAll(dateList);
				}
				
			} else {
				sql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd"));
				sql.append(">=?");
				sql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd"));
				sql.append("<=? and emp.guidkey in ('#'");
				String[] personIds = guidKeys.split(",");
				for (String personId : personIds)
					sql.append(",?");
				
				sql.append(")");
				
				paramList.add(startDate.replace(".", "-"));
				paramList.add(endDate.replace(".", "-"));
				for (String personId : personIds)
					paramList.add(personId);
				
				if("shiftData".equalsIgnoreCase(dataType)) {
					sql.append(" and scheme.scope=?");
					paramList.add(startDate + "-" + endDate);
				}
			}

			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), paramList);
			while (rs.next()) {
				String guidKey = rs.getString("guidkey");
				if (StringUtils.isEmpty(guidKey))
					continue;

				String state = rs.getString("state");
				ArrayList<HashMap<String, String>> shifList = new ArrayList<HashMap<String, String>>();
				String classId1 = rs.getString("Class_id_1");
				String comment1 = rs.getString("Comment_1");
				String commentColor1 = rs.getString("Comment_color_1");
				classId1 = StringUtils.isEmpty(classId1) ? "" : classId1;
				if (StringUtils.isEmpty(classId1))
					continue;

				comment1 = StringUtils.isEmpty(comment1) ? "" : comment1;
				commentColor1 = StringUtils.isEmpty(commentColor1) ? "" : commentColor1;
				String className1 = classMap.get(classId1);
				className1 = StringUtils.isEmpty(className1) ? "" : className1;
				HashMap<String, String> shiftInfoMap1 = new HashMap<String, String>();
				shiftInfoMap1.put("classId", PubFunc.encrypt(classId1));
				shiftInfoMap1.put("className", StringUtils.isEmpty(className1) ? classId1 : className1.split(":")[0]);
				shiftInfoMap1.put("classColor", StringUtils.isEmpty(className1) ? "#000000" : className1.split(":")[1]);
				shiftInfoMap1.put("comment", comment1);
				shiftInfoMap1.put("commentColor", commentColor1);
				shiftInfoMap1.put("state", state);
				shifList.add(shiftInfoMap1);

				String classId2 = rs.getString("Class_id_2");
				if (StringUtils.isNotEmpty(classId2)) {
					String comment2 = rs.getString("Comment_2");
					String commentColor2 = rs.getString("Comment_color_2");
					classId2 = StringUtils.isEmpty(classId2) ? "" : classId2;
					comment2 = StringUtils.isEmpty(comment2) ? "" : comment2;
					commentColor2 = StringUtils.isEmpty(commentColor2) ? "" : commentColor2;
					String className2 = classMap.get(classId2);
					if(StringUtils.isNotEmpty(className2)) {
						HashMap<String, String> shiftInfoMap2 = new HashMap<String, String>();
						shiftInfoMap2.put("classId", PubFunc.encrypt(classId2));
						shiftInfoMap2.put("className", StringUtils.isEmpty(className2) ? classId2 : className2.split(":")[0]);
						shiftInfoMap2.put("classColor", StringUtils.isEmpty(className2) ? "#000000" : className2.split(":")[1]);
						shiftInfoMap2.put("comment", comment2);
						shiftInfoMap2.put("commentColor", commentColor2);
						shifList.add(shiftInfoMap2);
					}
				}

				String classId3 = rs.getString("Class_id_3");
				if (StringUtils.isNotEmpty(classId3)) {
					String comment3 = rs.getString("Comment_3");
					String commentColor3 = rs.getString("Comment_color_3");
					classId3 = StringUtils.isEmpty(classId3) ? "" : classId3;
					comment3 = StringUtils.isEmpty(comment3) ? "" : comment3;
					commentColor3 = StringUtils.isEmpty(commentColor3) ? "" : commentColor3;
					String className3 = classMap.get(classId3);
					if(StringUtils.isNotEmpty(className3)) {
						HashMap<String, String> shiftInfoMap3 = new HashMap<String, String>();
						shiftInfoMap3.put("classId", PubFunc.encrypt(classId3));
						shiftInfoMap3.put("className", StringUtils.isEmpty(className3) ? classId3 : className3.split(":")[0]);
						shiftInfoMap3.put("classColor", StringUtils.isEmpty(className3) ? "#000000" : className3.split(":")[1]);
						shiftInfoMap3.put("comment", comment3);
						shiftInfoMap3.put("commentColor", commentColor3);
						shifList.add(shiftInfoMap3);
					}
				}

				String q03z0 = rs.getString("Q03Z0");
				if (StringUtils.isEmpty(q03z0))
					continue;

				q03z0 = q03z0.replace("-", ".");
				HashMap<String, ArrayList<HashMap<String, String>>> map = shiftMap.get(guidKey);
				if (map != null && map.size() > 0) {
					map.put(q03z0, shifList);
					shiftMap.put(guidKey, map);
				} else {
					map = new HashMap<String, ArrayList<HashMap<String, String>>>();
					map.put(q03z0, shifList);
					shiftMap.put(guidKey, map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return shiftMap;
	}

	/**
	 * 获取所有的班次
	 * 
	 * @return
	 */
	private HashMap<String, String> classMap() {
		HashMap<String, String> classMap = new HashMap<String, String>();
		RowSet rs = null;
		try {
			String sql = "select class_id,name,abbreviation,color from kq_class ";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next()) {
				String color = rs.getString(4);
				String displayName = StringUtils.isEmpty(rs.getString(3)) ? rs.getString(2) : rs.getString(3);
				color = StringUtils.isEmpty(color) ? "#000000" : color;
				classMap.put(rs.getString(1), displayName + ":" + color);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return classMap;
	}


	/**
	 * 获取班次中设置的统计属性
	 * 
	 * @return
	 */
	private ArrayList<HashMap<String, String>> getShiftStatistics() {
		ArrayList<HashMap<String, String>> statisticList = new ArrayList<HashMap<String, String>>();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct statistics_type,item_unit from kq_class,kq_item,codeitem");
			sql.append(" where kq_class.statistics_type=codeitem.codeitemid and codeitem.codeitemdesc=kq_item.item_name");
			sql.append(" and kq_class.is_validate=1 and codeitem.codesetid='85'");
			sql.append(" and " + Sql_switcher.isnull("kq_class.statistics_type", "'#'"));
			sql.append("<>'#' order by kq_class.statistics_type");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("statisticsType", rs.getString("statistics_type"));
				map.put("unit", rs.getString("item_unit"));
				statisticList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return statisticList;
	}

	/**
	 * 排班数据总数
	 * 
	 * @return
	 */
	@Override
    public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 获取当前方案编号
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param groupId
	 *            班组id
	 * @return
	 */
	@Override
    public String getSchemeId(String year, String month, String weekIndex, String groupId) {
		String schemeId = "";
		RowSet rs = null;
		try {
			addStatColumns();
			if ("-1".equalsIgnoreCase(weekIndex))
				return "-1";

			String pushscheme = "false";
			String groupName = "";
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month),
			        Integer.valueOf(weekIndex));
			String sDate = dateList.get(0).split(":")[0];
			String eDate = dateList.get(dateList.size() - 1).split(":")[0];
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select name from kq_shift_group where Group_id=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(groupId);
			rs = dao.search(searchSql.toString(), paramList);
			if (rs.next())
				groupName = rs.getString("name");
			
			searchSql.setLength(0);
			searchSql.append("select Scheme_id,state");			
			searchSql.append(" from kq_shift_scheme scheme");
			searchSql.append(" where scheme.Group_id=? and scope=?");
			paramList.clear();
			paramList.add(groupId);
			paramList.add(sDate + "-" + eDate);
			rs = dao.search(searchSql.toString(), paramList);
			if (rs.next()) {
				schemeId = rs.getString("Scheme_id");
				if("04".equalsIgnoreCase(rs.getString("state")))
					pushscheme = "true";
			}
			
			if (StringUtils.isEmpty(schemeId))
				schemeId = addShiftScheme(groupId, sDate + "-" + eDate, "");

			this.pushscheme = pushscheme;
			this.groupName = groupName;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return schemeId;
	}

	/**
	 * 新增排班方案
	 * 
	 * @param groupId
	 *            班组id
	 * @param scope
	 *            有效期
	 * @param flag
	 *            为空增加方案表+关联方案的所有人；=1只增加方案表         linbz    
	 * @return
	 */
	private String addShiftScheme(String groupId, String scope, String flag) {
		String schemeId = "";
		try {
			StringBuffer insertSql = new StringBuffer();
			insertSql.append("insert into kq_shift_scheme");
			insertSql.append(" (Scheme_id,Group_id,scope,State) values");
			insertSql.append(" (?,?,?,?)");
			ArrayList<String> paramList = new ArrayList<String>();
			IDGenerator idg = new IDGenerator(2, this.conn);
			schemeId = Integer.valueOf(idg.getId("kq_shift_scheme.scheme_id")) + "";
			paramList.add(schemeId);
			paramList.add(groupId);
			paramList.add(scope);
			paramList.add("01");

			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(insertSql.toString(), paramList);
			
			if(!"1".equals(flag) && 1 == diffDate(year, month, weekIndex))
				insertSchemePerson(groupId, schemeId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return schemeId;
	}

	/**
	 * 排班方案中新增人员
	 * 
	 * @param groupId
	 * @param schemeId
	 */
	private void insertSchemePerson(String groupId, String schemeId) {
		try {
			DbWizard db = new DbWizard(this.conn);
			if (db.isExistTable("T_kq_group_emp", false))
				db.dropTable("T_kq_group_emp");

			ArrayList<String> dbnameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			StringBuffer a01Sql = new StringBuffer();
			for(String dbname : dbnameList) {
				if(StringUtils.isNotEmpty(a01Sql.toString()))
					a01Sql.append(" union all ");
				
				a01Sql.append("select guidkey,a0000,");
				a01Sql.append("(select dbid from dbname where Pre='");
				a01Sql.append(dbname +"') dbid from " + dbname + "A01");
			}
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer insertSql = new StringBuffer();
			if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
				insertSql.append("select Identity(Int, 1,1) displayId,emp.guidkey,group_id");
				insertSql.append(" into T_kq_group_emp from kq_group_emp_v2 emp");
				insertSql.append(" left join (").append(a01Sql).append(") a01 ");
				insertSql.append(" on emp.Guidkey=a01.GUIDKEY");
				insertSql.append(" where Group_id='" + groupId + "'");
				insertSql.append(" order by a01.dbid,a01.A0000");
				dao.update(insertSql.toString());
				
				insertSql.setLength(0);
				insertSql.append("insert into kq_shift_scheme_emp");
				insertSql.append(" (Scheme_id,guidkey,display_Id) ");
				insertSql.append(" select '" + schemeId + "' as Scheme_id,Guidkey,displayId");
				insertSql.append(" from T_kq_group_emp where Group_id='" + groupId + "'");
			} else {
				insertSql.append("insert into kq_shift_scheme_emp");
				insertSql.append(" (display_Id,Scheme_id,guidkey) ");
				insertSql.append(" select rownum as displayId,'" + schemeId + "' as Scheme_id,emp.guidkey");
				insertSql.append(" from kq_group_emp_v2 emp");
				insertSql.append(" left join (").append(a01Sql).append(") a01 ");
				insertSql.append(" on emp.Guidkey=a01.GUIDKEY");
				insertSql.append(" where Group_id='" + groupId + "'");
				insertSql.append(" order by a01.dbid,a01.A0000");
			}
			
			dao.update(insertSql.toString());

			db.dropTable("T_kq_group_emp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测排班方案人员表是否包含统计项目列，如果不包含则新增
	 */
	private void addStatColumns() {
		try {
			ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
			DbWizard db = new DbWizard(this.conn);
			Table table = new Table("kq_shift_scheme_emp");

			for (HashMap<String, String> map : shiftStatisticList) {
				String statisticsType = map.get("statisticsType");
				if (db.isExistField("kq_shift_scheme_emp", "stat_" + statisticsType, false))
					continue;

				FieldItem item = new FieldItem();
				item.setItemid("stat_" + statisticsType);
				item.setItemtype("N");
				item.setItemlength(8);
				item.setDecimalwidth(1);
				table.addField(item);
			}

			if (table.getCount() > 0)
				db.addColumns(table);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取月份中总周数
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return
	 */
	private static int weekCount(int year, int month) {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int weekCount = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
		return weekCount;
	}

	/**
	 * 获取当前月份的周数
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return
	 */
	@Override
    public ArrayList<HashMap<String, String>> weekList(int year, int month) {
		ArrayList<HashMap<String, String>> weekList = new ArrayList<HashMap<String, String>>();

		String dateFormatter = "yyyy.MM.dd HH:mm";
		String now = DateUtils.FormatDate(new Date(), dateFormatter);

		int weekCount = weekCount(year, month);
		for (int i = 1; i <= weekCount; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", i + "");
			String name = "";
			if (1 == i)
				name = ResourceFactory.getProperty("kq.shift.firstWeek");
			else if (2 == i)
				name = ResourceFactory.getProperty("kq.shift.secondWeek");
			else if (3 == i)
				name = ResourceFactory.getProperty("kq.shift.thirdWeek");
			else if (4 == i)
				name = ResourceFactory.getProperty("kq.shift.fourthWeek");
			else if (5 == i)
				name = ResourceFactory.getProperty("kq.shift.fifthWeek");
			else if (6 == i)
				name = ResourceFactory.getProperty("kq.shift.sixthWeek");

			ArrayList<String> dayList = getWeekDateByWeekInMonth(year, month, i);
			String start = dayList.get(0).split(":")[0];
			String end = dayList.get(dayList.size() - 1).split(":")[0];
			// 判断是否本周
			if(now.compareTo(start) >= 0 && now.compareTo(end) <=0)
				name = ResourceFactory.getProperty("kq.shift.thisWeek");

			name += " (" + start + "-" + end + ")";
			map.put("name", name);
			weekList.add(map);
		}

		String allDesc = ResourceFactory.getProperty("kq.shift.allWeek");
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> dayList = getWeekDateByWeekInMonth(year, month, -1);
		allDesc += " (" + dayList.get(0).split(":")[0] + "-" + dayList.get(dayList.size() - 1).split(":")[0] + ")";
		map.put("id", "-1");
		map.put("name", allDesc);
		weekList.add(map);

		return weekList;
	}

	/**
	 * 获取快速查询支持的列
	 * 
	 * @return
	 */
	@Override
    public String getFields() {
		StringBuffer fieldArray = new StringBuffer();
		try {
			fieldArray.append("[{'type':'A',");
			fieldArray.append("'itemid':'b0110',");
			FieldItem fi = DataDictionary.getFieldItem("b0110", "A01");
			fieldArray.append("'itemdesc':'" + fi.getItemdesc() + "',");
			fieldArray.append("'codesetid':'UN',");
			fieldArray.append("'format':'" + fi.getFormat() + "',");
			fieldArray.append("'ctrltype':'3',");
			fieldArray.append("'codesetValid':false,");
			fieldArray.append("'nmodule':'11'},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'e0122',");
			fi = DataDictionary.getFieldItem("e0122", "A01");
			fieldArray.append("'itemdesc':'" + fi.getItemdesc() + "',");
			fieldArray.append("'codesetid':'UN',");
			fieldArray.append("'format':'" + fi.getFormat() + "',");
			fieldArray.append("'ctrltype':'3',");
			fieldArray.append("'codesetValid':false,");
			fieldArray.append("'nmodule':'11'},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'a0101',");
			fi = DataDictionary.getFieldItem("a0101", "A01");
			fieldArray.append("'itemdesc':'" + fi.getItemdesc() + "',");
			fieldArray.append("'codesetid':'0',");
			fieldArray.append("'format':'" + fi.getFormat() + "'},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'group_name',");
			fieldArray.append("'itemdesc':'" + ResourceFactory.getProperty("kq.shift.group_name") + "',");
			fieldArray.append("'codesetid':'0',");
			fieldArray.append("'format':''},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'shift_comment',");
			fieldArray.append("'itemdesc':'" + ResourceFactory.getProperty("kq.shift.shiftComment") + "',");
			fieldArray.append("'codesetid':'0',");
			fieldArray.append("'format':''},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'extra_days',");
			fieldArray.append("'itemdesc':'" + ResourceFactory.getProperty("kq.shift.extraDays") + "',");
			fieldArray.append("'codesetid':'0',");
			fieldArray.append("'format':''},");

			fieldArray.append("{'type':'A',");
			fieldArray.append("'itemid':'work_hour',");
			fieldArray.append("'itemdesc':'" + ResourceFactory.getProperty("kq.shift.workHour") + "',");
			fieldArray.append("'codesetid':'0',");
			fieldArray.append("'format':''}]");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fieldArray.toString();
	}
	
	/**
	 * 获取有效的班次信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param classIds
	 *            已选的班次
	 * @param shiftInfoFlag
	 *            编辑班次的功能标识：="":表格直接双击单元格设置班次；=changgeShift：更换班次；=addShifts：新增班次
	 * @return
	 */
	@Override
    public ArrayList<HashMap<String, String>> getShiftInfoList(String groupId, String classIds, String shiftInfoFlag) {
		ArrayList<HashMap<String, String>> shiftInfoList = new ArrayList<HashMap<String, String>>();
		RowSet rs = null;
		try {
		    String orgId = "";
			groupId = StringUtils.isEmpty(groupId) ? groupId : PubFunc.decrypt(groupId);
			String sql = "select Shift_data,org_id from kq_shift_group where shift_type=? and group_id=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add("1");
			paramList.add(groupId);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql, paramList);
			String shiftIds = "";
			paramList.clear();
			if(rs.next()) {
				orgId = rs.getString("org_id");
				String shiftData = rs.getString("Shift_data");
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
			paramList.add(0, "1");
			sql = "select class_id,name,abbreviation,color from kq_class where is_validate=?";
			//排班页面进行排班时，如果班组中没有设置每个周中的班次，就显示班组所属机构内的班次
			if(StringUtils.isNotEmpty(shiftIds))
			    sql += " and class_id in (0" + shiftIds + ")";
			else {
				// 50709 获取顶级机构 设置顶级机构的班次为共享班次
				KqPrivForHospitalUtil kp = new KqPrivForHospitalUtil(userView, conn);
				String orgidTop = kp.getTopUNCodeitemid();
			    if(!"UN".equalsIgnoreCase(orgId) || !orgidTop.equalsIgnoreCase(orgId) )
			        sql += " and (org_id='"+ orgId + "' or org_id='"+orgidTop+"')";
			}
            
			rs = dao.search(sql, paramList);
			while (rs.next()) {
				String classId = rs.getString("class_id");
				if(StringUtils.isNotEmpty(shiftInfoFlag) && !"0".equals(classId)
						&& classIds.contains("," + PubFunc.encrypt(classId) + ","))
					continue;
				
				HashMap<String, String> shiftInfoMap = new HashMap<String, String>();
				String displayName = StringUtils.isEmpty(rs.getString("abbreviation")) ? rs.getString("name") : rs.getString("abbreviation");
				shiftInfoMap.put("name", displayName);
				shiftInfoMap.put("classId", PubFunc.encrypt(classId));
				if (StringUtils.isNotEmpty(classIds) && classIds.contains("," + PubFunc.encrypt(classId) + ",")
						&& StringUtils.isEmpty(shiftInfoFlag))
					shiftInfoMap.put("checked", "1");
				else
					shiftInfoMap.put("checked", "0");

				String color = rs.getString("color");
				color = StringUtils.isEmpty(color) ? "#000000" : color;
				shiftInfoMap.put("nameColor", color);
				
				shiftInfoList.add(shiftInfoMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return shiftInfoList;
	}

	/**
	 * 保存排班信息
	 * 
	 * @param date
	 *            排班日期
	 * @param record
	 *            对应人员的数据
	 * @throws GeneralException
	 */
	@Override
    public void saveShiftInfoS(String date, JSONObject record) throws GeneralException {
		RowSet rs = null;
		try {
			String guidKey = record.getString("guidkey");
			guidKey = PubFunc.decrypt(guidKey);
			int weekIndex = getWeekIndex(date);
			String shiftInfos = (String) record.get(date);
			JSONArray shiftInfoArr = new JSONArray();
			if(StringUtils.isNotEmpty(shiftInfos))
				shiftInfoArr = JSONArray.fromObject(shiftInfos);
			
			String sql = "select 1 from kq_employ_shift_v2 where guidkey=? and "
			        + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "=?";
			date = date.replace(".", "-");
			ArrayList<Object> paramList = new ArrayList<Object>();
			paramList.add(guidKey);
			paramList.add(date);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql, paramList);
			paramList.clear();
			for (int i = 0; i < shiftInfoArr.size(); i++) {
				JSONObject shiftInfoJson = (JSONObject) shiftInfoArr.get(i);
				String classId = shiftInfoJson.getString("classId");
				classId = StringUtils.isNotEmpty(classId) ? PubFunc.decrypt(classId) : "";
				if (StringUtils.isEmpty(classId))
					continue;

				String comment = shiftInfoJson.getString("comment");
				String commentColor = shiftInfoJson.getString("commentColor");
				if (StringUtils.isEmpty(comment)) {
					comment = null;
					commentColor = null;
				}

				paramList.add(classId);
				paramList.add(comment);
				paramList.add(commentColor);

			}

			for (int i = shiftInfoArr.size(); i < 3; i++) {
				paramList.add(null);
				paramList.add(null);
				paramList.add(null);

			}

			paramList.add(guidKey);
			StringBuffer updateSql = new StringBuffer();
			if (rs.next()) {
				paramList.add(date);
				updateSql.append("update kq_employ_shift_v2 set class_id_1=?,Comment_1=?,Comment_color_1=?,");
				updateSql.append("class_id_2=?,comment_2=?,comment_color_2=?,");
				updateSql.append("class_id_3=?,comment_3=?,comment_color_3=?");
				updateSql.append(" where guidkey=? and ");
				updateSql.append(Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + "=?");
			} else {
				paramList.add(new Timestamp(DateUtils.getDate(date, "yyyy-MM-dd").getTime()));
				updateSql.append("insert into kq_employ_shift_v2 (");
				updateSql.append("class_id_1,Comment_1,Comment_color_1,");
				updateSql.append("class_id_2,comment_2,comment_color_2,");
				updateSql.append("class_id_3,comment_3,comment_color_3,guidkey,q03z0");
				updateSql.append(") values (?,?,?,?,?,?,?,?,?,?,?)");
			}

			dao.update(updateSql.toString(), paramList);

			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(date.split("-")[0]),
			        Integer.valueOf(date.split("-")[1]), Integer.valueOf(weekIndex));
			String sDate = dateList.get(0).split(":")[0];
			String eDate = dateList.get(dateList.size() - 1).split(":")[0];
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select scheme.scheme_id from kq_shift_scheme scheme,kq_shift_scheme_emp emp");
			searchSql.append(" where scheme.scheme_id=emp.scheme_id");
			searchSql.append(" and scheme.scope=? and emp.guidkey=?");
			paramList.clear();
			paramList.add(sDate + "-" + eDate);
			paramList.add(guidKey);
			rs = dao.search(searchSql.toString(), paramList);
			String schemeId = "";
			if (rs.next())
				schemeId = rs.getString("scheme_id");

			ArrayList<String> schemeIdList = new ArrayList<String>();
			schemeIdList.add(schemeId);
			countClasses(schemeIdList, guidKey);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 保存排班方案人员对应表中的信息
	 * 
	 * @param schemeId
	 *            方案编号
	 * @param itemId
	 *            修改的指标
	 * @param record
	 *            修改人员的数据
	 * @throws GeneralException
	 */
	@Override
    public void saveSchemeEmp(String schemeId, String itemId, JSONObject record) throws GeneralException {
		try {
			schemeId = StringUtils.isNotEmpty(schemeId) ? PubFunc.decrypt(schemeId) : "";
			if (StringUtils.isEmpty(schemeId))
				return;

			String guidKey = record.getString("guidkey");
			guidKey = PubFunc.decrypt(guidKey);
			String itemValue = (String) record.get(itemId);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(itemValue);
			paramList.add(guidKey);
			paramList.add(schemeId);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update kq_shift_scheme_emp");
			updateSql.append(" set " + itemId + "=?");
			updateSql.append(" where guidkey=? and scheme_id=?");
			dao.update(updateSql.toString(), paramList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 获取排班的年月
	 * 
	 * @param GroupId
	 *            班组编号
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public String getShiftDate(String groupId) throws GeneralException {
		StringBuffer shiftDate = new StringBuffer("[");
		RowSet rs = null;
		try {
			int nowYear = DateUtils.getYear(new Date());
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select distinct " + Sql_switcher.year("shift.q03z0") + " as shiftYear, ");
			searchSql.append(Sql_switcher.month("shift.q03z0") + " as shiftMonth");
			searchSql.append(" from kq_shift_scheme scheme left join kq_shift_scheme_emp emp");
			searchSql.append(" on emp.Scheme_id=scheme.Scheme_id");
			searchSql.append(" left join kq_employ_shift_v2 shift on shift.guidkey=emp.guidkey");
			searchSql.append(" where q03z0 is not null");
			ArrayList<String> paramList = new ArrayList<String>();
			if(StringUtils.isNotEmpty(groupId)) {
				searchSql.append(" and scheme.Group_id=?");
				paramList.add(groupId);
			}
			
			searchSql.append(" order by shiftYear,shiftMonth");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(searchSql.toString(), paramList);
			ArrayList<HashMap<String, String>> dateList = new ArrayList<HashMap<String, String>>();
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("year", rs.getString("shiftYear"));
				map.put("month", rs.getString("shiftMonth"));
				dateList.add(map);
			}

			if (dateList == null || dateList.size() < 1) {
				shiftDate.append(getDateJson(nowYear - 1, 1, 12));
				shiftDate.append(getDateJson(nowYear, 1, 12));
				shiftDate.append(getDateJson(nowYear + 1, 1, 12));
			} else {
				int yearTemp = 0;
				int monthTemp = 0;
				for (HashMap<String, String> date : dateList) {
					int year = Integer.valueOf(date.get("year"));
					int month = Integer.valueOf(date.get("month"));
					if (shiftDate.length() == 1) {
						if (year > nowYear) {
							shiftDate.append(getDateJson(nowYear - 1, 1, 12));
							shiftDate.append(getDateJson(nowYear, 1, 12));
							shiftDate.append(getDateJson(nowYear + 1, 1, 12));
						}

					}

					if ((year - nowYear) > 1)
						break;

					if (yearTemp != year) {
						if (month > 1)
							shiftDate.append(getDateJson(year, 1, month - 1));

						if (monthTemp > 0 && monthTemp < 12)
							shiftDate.append(getDateJson(yearTemp, monthTemp + 1, 12));

						yearTemp = year;
						shiftDate.append(
						        "{year:'" + year + "',monthOrder:" + month + ",desc:'" + month + ResourceFactory.getProperty("kq.duration.yue") + "',state:1},");
					} else {
						if((monthTemp + 1) != month)
							shiftDate.append(getDateJson(yearTemp, monthTemp + 1, month));
						else
							shiftDate.append(
									"{year:'" + year + "',monthOrder:" + month + ",desc:'" + month + ResourceFactory.getProperty("kq.duration.yue") + "',state:1},");
					}

					monthTemp = month;
				}

				if (monthTemp < 12)
					shiftDate.append(getDateJson(yearTemp, monthTemp + 1, 12));

				if(yearTemp <= nowYear) {
					for(int i = yearTemp + 1; i < nowYear + 2; i++)
						shiftDate.append(getDateJson(i, 1, 12));
				}
			}

			if (shiftDate.toString().endsWith(","))
				shiftDate.setLength(shiftDate.length() - 1);

			shiftDate.append("]");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}

		return shiftDate.toString();
	}

	/**
	 * 生成没有排班的年月的json
	 * 
	 * @param year
	 *            年
	 * @param formMonth
	 *            开始月
	 * @param toMonth
	 *            结束月
	 * @return
	 */
	private String getDateJson(int year, int formMonth, int toMonth) {
		StringBuffer dateJson = new StringBuffer();
		for (int i = formMonth; i <= toMonth; i++) {
			dateJson.append("{year:'" + year + "',monthOrder:" + i + ",desc:'" + i + ResourceFactory.getProperty("kq.duration.yue") + "',state:2},");
		}

		return dateJson.toString();
	}

	/**
	 * 保存拖拽人员的顺序
	 * 
	 * @param schemeId
	 *            方案编号
	 * @param guidkey
	 *            被拖拽的人员的guidke
	 * @param modelGuidKey
	 *            拖拽到的位置的人员的guidkey
	 * @param dropPosition
	 *            拖拽到目标行的位置，=before：目标行上面；=after：目标行下面
	 * @throws GeneralException
	 */
	@Override
    public void saveDropSort(String schemeId, String guidkey, String modelGuidKey, String dropPosition)
	        throws GeneralException {
		RowSet rs = null;
		try {
			schemeId = StringUtils.isEmpty(schemeId) ? schemeId : PubFunc.decrypt(schemeId);
			guidkey = StringUtils.isEmpty(guidkey) ? guidkey : PubFunc.decrypt(guidkey);
			modelGuidKey = StringUtils.isEmpty(modelGuidKey) ? modelGuidKey : PubFunc.decrypt(modelGuidKey);
			if (StringUtils.isEmpty(schemeId) || StringUtils.isEmpty(guidkey) || StringUtils.isEmpty(modelGuidKey))
				return;

			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey,display_Id from kq_shift_scheme_emp");
			sql.append(" where scheme_id=? and guidkey in (?,?)");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(schemeId);
			paramList.add(guidkey);
			paramList.add(modelGuidKey);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), paramList);
			int displayId = 0;
			int modelDisplayId = 0;
			while (rs.next()) {
				if (guidkey.equalsIgnoreCase(rs.getString("guidkey")))
					displayId = rs.getInt("display_Id");
				else if (modelGuidKey.equalsIgnoreCase(rs.getString("guidkey")))
					modelDisplayId = rs.getInt("display_Id");
			}

			if (displayId == 0 || modelDisplayId == 0)
				return;
			// 更新被拖拽的人员的序号改前与改后的之间显示的人员的序号
			sql.setLength(0);
			paramList.clear();
			sql.append("update kq_shift_scheme_emp set display_Id=");
			paramList.add(schemeId);
			if (displayId > modelDisplayId) {
				if ("after".equalsIgnoreCase(dropPosition))
					modelDisplayId++;

				sql.append("display_Id+1");
				paramList.add((modelDisplayId - 1) + "");
				paramList.add(displayId + "");
			} else {
				if ("before".equalsIgnoreCase(dropPosition))
					modelDisplayId--;

				sql.append("display_Id-1");
				paramList.add(displayId + "");
				paramList.add((modelDisplayId + 1) + "");
			}

			sql.append(" where scheme_id=? and display_Id>? and display_Id<?");
			dao.update(sql.toString(), paramList);
			// 更新拖动位置的人员序号
			sql.setLength(0);
			sql.append("update kq_shift_scheme_emp set display_Id=?");
			sql.append(" where scheme_id=? and guidkey=?");
			paramList.clear();
			paramList.add(modelDisplayId + "");
			paramList.add(schemeId);
			paramList.add(guidkey);
			dao.update(sql.toString(), paramList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 获取班组人员表格对象 
	 * getShiftGroupEmpTableConfig
	 * @param groupid
	 *            班组id
	 * @param selectedflag
	 *            =0已选/=1待选 标识
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月28日 下午2:29:09
	 * @author linbz
	 */
	@Override
    public String getShiftGroupEmpTableConfig(JSONObject jsonObj) throws GeneralException {

		String config = "";
		try {
			ArrayList columnList = this.listShiftGroupEmpColumns();
			// 已选选表格
			String sql = this.getShiftGroupEmpSql(jsonObj);
			TableConfigBuilder builder = new TableConfigBuilder("kqshiftemp_01", columnList, "kqshiftemp_01",
			        this.userView, this.conn);
			builder.setTitle("");
			builder.setSelectable(false);// 选框
			builder.setDataSql(sql);
			builder.setOrderBy(" order by E0122,A0000 ");
			builder.setColumnFilter(true);
			// 44032 在排班页面点击班组成员创建表格控件对象时 重写行高样式导致排班的表格对象行高也改变
			builder.setTdMaxHeight(0);
			builder.setTableTools(this.listShiftGroupEmpButtons());
			String config1 = builder.createExtTableConfig();
			// 待选表格
			jsonObj.put("selectedflag", "1");
			sql = this.getShiftGroupEmpSql(jsonObj);
			TableConfigBuilder builder1 = new TableConfigBuilder("kqshiftemp_02", columnList, "kqshiftemp_02",
			        this.userView, this.conn);
			builder1.setTitle("");
			builder1.setSelectable(false);// 选框
			builder1.setDataSql(sql);
			builder1.setOrderBy(" order by E0122,A0000 ");
			builder1.setColumnFilter(true);
			builder1.setTdMaxHeight(0);
			builder1.setTableTools(this.listShiftGroupEmpButtons());
			String config2 = builder1.createExtTableConfig();
			config = config1 + "`" + config2;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return config;
	}

	private ArrayList<ButtonInfo> listShiftGroupEmpButtons() {
		ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();

		ButtonInfo querybox = new ButtonInfo();
		querybox.setFunctionId("KQ00021305");
		querybox.setType(ButtonInfo.TYPE_QUERYBOX);
		// "姓名、工号"
		querybox.setText(ResourceFactory.getProperty("kq.group.search_namegno"));
		buttonList.add(querybox);
		// 全部加入
		querybox = new ButtonInfo(ResourceFactory.getProperty("label.all")+ResourceFactory.getProperty("kq.shift.add"), "shiftManage.selectAllPerson(0)");
		buttonList.add(querybox);
		// 全部取消
		querybox = new ButtonInfo(ResourceFactory.getProperty("label.all")+ResourceFactory.getProperty("kq.register.kqduration.cancel")
			, "shiftManage.selectAllPerson(1)");
		buttonList.add(querybox);
				
		return buttonList;
	}
	/**
	 * 获取班组成员已选或待选 SQL
	 * getShiftGroupEmpSql
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月24日 上午11:48:00
	 * @author linbz
	 */
	private String getShiftGroupEmpSql(JSONObject jsonObj) throws GeneralException {

		StringBuffer sql = new StringBuffer("");
		try {
			// =0已选人员标签；=1 未选人员标签 标识
			String selectedflag = jsonObj.getString("selectedflag");
			// 班组编号
			String groupid = jsonObj.getString("group_id");
			groupid = PubFunc.decrypt(groupid);
			// 班组编号
			String orgid = jsonObj.getString("org_id");
			if (orgid.indexOf("`") != -1)
				orgid = StringUtils.split(orgid, "`")[0];
			// 排班方案id
			String schemeid = jsonObj.getString("scheme_id");
			schemeid = PubFunc.decrypt(schemeid);
			KqPrivForHospitalUtil kqPrivForHospitalUtil = new KqPrivForHospitalUtil(userView, conn);
			String g_no = kqPrivForHospitalUtil.getG_no();
			boolean isNotBankGno = StringUtils.isNotBlank(g_no);
			// 没有配置工号 提示信息
			if(!isNotBankGno) {
				throw new Exception(ResourceFactory.getProperty("kq.param.not"));
			}
			String kq_dept = kqPrivForHospitalUtil.getKqDeptField();
			boolean isNotBankKqDept = StringUtils.isNotBlank(kq_dept);
			// 人员库范围：当前操作用户人员库权限；
			// kq_shift_scheme_emp排班方案人员表 display_Id人员顺序号
			// 班组所属机构权限
	    	String whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "org_id", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInb0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE01A1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E01A1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
			String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
			FieldItem pinyinFi = DataDictionary.getFieldItem(pinyin_field);
	    	// 考勤部门指标
	    	String whereInKqDept = "1=1";
	    	if(isNotBankKqDept)
	    		whereInKqDept = KqPrivForHospitalUtil.getPrivB0110Whr(userView, kq_dept, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
			boolean isNotSuper = !userView.isSuper_admin();
			// 增加主集信息查询条件
			String A01StrSql = "";
			ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("A01", 1);
			for(int i=0;i<fieldList.size();i++) {
				FieldItem field = fieldList.get(i);
				if(null == field)
					continue;
				// 去除没有启用的指标
				if (!"1".equals(field.getUseflag())) 
					continue;
				// 去除隐藏的指标
				if (field.getDisplaywidth() <= 0) 
					continue;
				// 去除大字段指标
				if ("M".equalsIgnoreCase(field.getItemtype())) 
					continue;
				String itemId = field.getItemid();
				if((",guidkey,e0122,a0101,"+g_no.toLowerCase()+","+pinyin_field.toLowerCase()+",").indexOf("," + itemId.toLowerCase() + ",") != -1) 
					continue;
				// 校验指标权限
				String priv_flag = userView.analyseFieldPriv(itemId);
				if(!"1".equals(priv_flag) && !"2".equals(priv_flag))
					continue;
				A01StrSql += "b."+itemId+",";
			}
			/**
			 * 轮岗人员目前只支持 基于当前时间是否轮岗 只有未选人员列表支持
			 */
			String kqDateStr = DateUtils.format(new Date(), "yyyy-MM");
			//轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            //轮岗结束时间
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept))
                hasChangeSet = true;
            // 机构SQL条件
            String orgSql = "(b0110 like '"+orgid+"%' or E0122 like '"+orgid+"%' or E01A1 like '"+orgid+"%' ";
			// 44808 应包含 考勤部门为所属机构的人员 条件
			if(isNotBankKqDept)
				orgSql += " or "+kq_dept+" like '"+orgid+"%' ";
			orgSql += ")";
				
	    	// 当前操作用户人员库权限
			ArrayList<String> nbaselist = KqPrivForHospitalUtil.getB0110Dase(userView, conn);
			for (int i = 0; i < nbaselist.size(); i++) {
				String nbase = nbaselist.get(i);
				if ("0".equals(selectedflag)) {

					sql.append("select "+A01StrSql+"'1' is_validate, a.guidkey, case when  (E0122 is null or E0122='') then B0110 else E0122 end as E0122, a0101");
					if (isNotBankGno)
						sql.append(", ").append(g_no).append(" g_no");

					if(pinyinFi != null && !"0".equalsIgnoreCase(pinyinFi.getUseflag()))
						sql.append(", b." + pinyin_field);
					
					sql.append(", '1' display_Id,b.A0000 from kq_group_emp_v2 a ");
					sql.append(" left join kq_shift_group c on a.group_id=c.group_id ");
					sql.append(" left join ").append(nbase).append("A01 b on a.guidkey=b.guidkey ");
					sql.append(" where a.group_id='").append(groupid).append("' ").append(" and b.guidkey is not null ");
					if (Constant.MSSQL == Sql_switcher.searchDbServer())
						sql.append(" and b.guidkey<>'' ");
					sql.append(" and ("+ whereInOrg);
					
					// 显示待选人员  用户权限应与所属机构为交集
					sql.append(" or ").append(orgSql);
					sql.append(")");
				} else {
					// 55078 去重 防止有多条轮岗数据
					sql.append("select distinct "+A01StrSql+"'0' is_validate, guidkey, case when (E0122 is null or E0122='') then B0110 else E0122 end as E0122, a0101");
					if (isNotBankGno)
						sql.append(", ").append(g_no).append(" g_no");

					if(pinyinFi != null && !"0".equalsIgnoreCase(pinyinFi.getUseflag()))
						sql.append(", b." + pinyin_field);
					
					sql.append(", '1' display_Id,b.A0000 from ").append(nbase).append("A01 b ");
					//有变动子集的数据时，优先按照变动部门创建考勤数据
	                if(hasChangeSet){
	                    sql.append(" left join (select a0100,"+changDept+" from "+nbase+changSetId+" cset"); 
	                    sql.append(" where (");
	                    sql.append(Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDateStr+"'"
	                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+">='"+kqDateStr+"' ");
	                    sql.append(" or (("+changeEndField+" is null or "+changeEndField+"='')"
	                    		+ " and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDateStr+"') ");
	                    sql.append(" or ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+">='"+kqDateStr+"'"
	                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+"<='"+kqDateStr+"') ");
	                    sql.append(" or ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+">='"+kqDateStr+"'"
	                    		+ " and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDateStr+"') ");
	                    sql.append(" or ("+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+">='"+kqDateStr+"'"
	                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+"<='"+kqDateStr+"') ");
	                    sql.append(") ");
	                    sql.append(") cset ");
	                    sql.append("on cset.a0100=b.a0100");
	                }
					
					sql.append(" where ( ( (").append(whereInb0110);
					sql.append(" or ").append(whereInE0122);
					sql.append(" or ").append(whereInE01A1).append(") ");
					if(isNotBankKqDept && isNotSuper)
						sql.append("and ( ").append(kq_dept).append(" is null or ").append(kq_dept).append("=''  )");
					sql.append(")");
					if (isNotSuper) 
						sql.append(" or "+ whereInKqDept);
					sql.append(")");
					
					// 待选人员范围：班组所属机构内没有加入任何其它班组的人员
					sql.append(" and guidkey NOT IN (")
					        .append("select guidkey from kq_group_emp_v2 where group_id in ")
					        		.append("(select group_id from kq_shift_group where ").append(whereInOrg).append("")
				        				// 50400 增加当前班组所属机构校验
			        					.append(" or org_id like '"+orgid+"%')")
	        		.append(")");
					// 51025 未选人员增减轮岗条件
					if(hasChangeSet){
						sql.append(" and ( "+changDept+" like '"+orgid+"%'");
						sql.append(" or (").append(orgSql).append(")");
						sql.append(" or (("+changDept+" is null or "+changDept+"='')");
						sql.append(" and ").append(orgSql);
		            	sql.append(" and not exists (");
		            	sql.append(" select 1 from "+nbase+changSetId +" z");
		            	sql.append(" where z.A0100=b.a0100");
		            	sql.append(" and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDateStr+"'"
		            				+ " and ("+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+">='"+kqDateStr+"'");
		            	sql.append(" or "+changeEndField+" is null or "+changeEndField+"='')");
		            	sql.append(")");
		            	sql.append("))");
		            }else {
		            	// 44728 显示待选人员  用户权限应与所属机构为交集
		            	sql.append(" and ").append(orgSql);
		            }
				}
				
				if (i < nbaselist.size() - 1)
					sql.append(" UNION ALL ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
	/**
	 * 获取班组人员窗口列头集合
	 * listShiftGroupEmpColumns
	 * @return
	 * @date 2018年12月24日 上午10:37:51
	 * @author linbz
	 */
	private ArrayList<ColumnsInfo> listShiftGroupEmpColumns() {

		ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
		try {
			FieldItem fi = DataDictionary.getFieldItem("e0122", "a01");
			ColumnsInfo columnsInfo = getColumnsInfo("E0122", fi.getItemdesc(), 200, "UM", "A", 100, 0, "shiftGroupEmp");
			// columnsInfo.setCtrltype("1");
			// columnsInfo.setNmodule("10");
			columnList.add(columnsInfo);
			
			fi = DataDictionary.getFieldItem("a0101", "a01");
			columnsInfo = getColumnsInfo("a0101", fi.getItemdesc(), 130, "0", "A", 100, 0, "shiftGroupEmp");
			columnList.add(columnsInfo);
			// 工号取参数设置指标描述
			HashMap paramMap = KqPrivForHospitalUtil.getKqParameter(this.conn);
			String gNo = (String) paramMap.get("g_no");
			fi = DataDictionary.getFieldItem(gNo, "a01");
			columnsInfo = getColumnsInfo("g_no", fi.getItemdesc(), 130, "0", "A", 100, 0, "shiftGroupEmp");
			columnList.add(columnsInfo);
			// 是否加入排班
			columnsInfo = getColumnsInfo("is_validate", ResourceFactory.getProperty("kq.shift.isaddemp"), 68, "0", "A", 100, 0, "");
			columnsInfo.setRendererFunc("shiftManage.renderValidate");
			columnsInfo.setTextAlign("center");
			//【55878】考勤管理 ：班组管理/班组成员，“是否加入排班”的过滤建议取消
			columnsInfo.setFilterable(false);
			columnsInfo.setSortable(false);
			columnList.add(columnsInfo);
			// 隐藏
			columnsInfo = getColumnsInfo("guidkey", "guidkey", 150, "0", "A", 100, 0, "");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			columnList.add(columnsInfo);
			// 增加主集信息查询条件
			ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("A01", 1);
			for(int i=0;i<fieldList.size();i++) {
				FieldItem field = fieldList.get(i);
				if(null == field)
					continue;
				// 去除没有启用的指标
				if (!"1".equals(field.getUseflag())) 
					continue;
				// 去除隐藏的指标
				if (field.getDisplaywidth() <= 0) 
					continue;
				String itemId = field.getItemid();
				if((",guidkey,e0122,a0101,"+gNo+",").indexOf("," + itemId.toLowerCase() + ",") != -1) 
					continue;
				// 校验指标权限
				String priv_flag = userView.analyseFieldPriv(itemId);
				if(!"1".equals(priv_flag) && !"2".equals(priv_flag))
					continue;
				columnsInfo = getColumnsInfo(itemId, field.getItemdesc(), 150, field.getCodesetid(), field.getItemtype(), 100, 0, "A01");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
				columnList.add(columnsInfo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnList;
	}

	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String codesetId,
	        String columnType, int columnLength, int decimalWidth, String fieldsetid) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setFieldsetid(fieldsetid);
		columnsInfo.setQueryable(true);
		// columnsInfo.setReadOnly(true);// 是否只读

		return columnsInfo;
	}

	/**
	 * 添加班组人员 
	 * addShiftGroupEmp
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月30日 下午4:35:53
	 * @author linbz
	 */
	@Override
    public String addShiftGroupEmp(JSONObject jsonObj) throws GeneralException {

		String config = "";
		RowSet rs = null;
		try {
			JSONArray addArray = jsonObj.getJSONArray("addArray");
			if(addArray.size() < 1)
				return config;
			// 班组编号
			String groupid = jsonObj.getString("group_id");
			groupid = PubFunc.decrypt(groupid);
			// 班组编号
			String orgid = jsonObj.getString("org_id");
			if (orgid.indexOf("`") != -1)
				orgid = StringUtils.split(orgid, "`")[0];
			// 调入日期
			String addyear = jsonObj.getString("addyear");
			String addmonth = jsonObj.getString("addmonth");
			String addweekIndex = jsonObj.getString("addweekIndex");
			addweekIndex = "-1".equals(addweekIndex) ? "1" : addweekIndex;
			// 排班方案id
			String schemeid = jsonObj.getString("scheme_id");
			schemeid = PubFunc.decrypt(schemeid);
			// 通过 kq_shift_scheme 获取时间范围scope 范围之前的都更新 条件Group_id
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(addyear), Integer.valueOf(addmonth),
			        Integer.valueOf(addweekIndex));
			String startDate = ((String) dateList.get(0)).split(":")[0];
			String endDate = ((String) dateList.get(dateList.size() - 1)).split(":")[0];
			String scope = startDate + "-" + endDate;
			
			ArrayList empv2lists = new ArrayList();
			ArrayList list = new ArrayList();
			ArrayList<String> guidkeylist = new ArrayList<String>();
			String guidkeySql = "";
			for (int i = 0; i < addArray.size(); i++) {
				String guidkey = PubFunc.decrypt((String) addArray.get(i));
				guidkeylist.add(guidkey);
				guidkeySql += ",?";
				list = new ArrayList();
				list.add(guidkey);
				list.add(groupid);
				empv2lists.add(list);
			}
			guidkeySql = guidkeySql.substring(1);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			// 查找调入人员该方案周期是否存在其他班组
			sql.append("select guidkey  from kq_shift_scheme_emp emp");
			sql.append(" left join kq_shift_scheme scheme");
			sql.append(" on emp.scheme_id = scheme.scheme_id");
			sql.append(" where scheme.scope>=?");
			sql.append(" and emp.guidkey in (").append(guidkeySql).append(") ");
			list = new ArrayList();
			list.add(scope);
			list.addAll(guidkeylist);
			String guidkeys = "";
			rs = dao.search(sql.toString(), list);
			while (rs.next()) {
				guidkeys += ",'" + rs.getString("guidkey") + "'";
			}
			if(StringUtils.isNotBlank(guidkeys)) {
				guidkeys = guidkeys.substring(1);
				String names = getA0101ByGuidkey(guidkeys);
				config = "所选调入人员中" + names + "已存在其他班组方案，请重新调整。";
				return config;
			}
			// 人员班组对应表 kq_group_emp_v2 guidkey,group_id
			sql.setLength(0);
			sql.append("insert into kq_group_emp_v2");
			sql.append(" (guidkey,group_id) values (?,?)");
			// 新增人员班组对应表记录
			dao.batchInsert(sql.toString(), empv2lists);
			//排班方案编号、年、月、周 其中任意一个值为空时，是固定班制的班组设置人员，不用更新排班方案人员表的数据
			if(StringUtils.isEmpty(schemeid))
				return config;
			
			list = new ArrayList();
			sql.setLength(0);
			list.add(scope);
			list.add(groupid);
			sql.append("select scheme_id,scope from kq_shift_scheme where scope>=? and group_id=?");
			
			PubFunc.closeResource(rs);
			rs = dao.search(sql.toString(), list);
			ArrayList<String> schemeidList = new ArrayList<String>();
			ArrayList<String> scopeList = new ArrayList<String>();
			while (rs.next()) {
				schemeidList.add(rs.getString("scheme_id"));
				scopeList.add(rs.getString("scope"));
			}
			// 44578	维护班组成员调入时间到当前周都需要增加 若没有则新增方案
			String nowdate = DateUtils.format(new Date(), "yyyy.MM.dd");
			ArrayList<String> scopesList = getWeekScopes(startDate, nowdate);
			String schemeidValue = "";
			for(String scopeVlaue : scopesList) {
				if(scopeList.contains(scopeVlaue))
					continue;
				schemeidValue = addShiftScheme(groupid, scopeVlaue, "1");
				schemeidList.add(schemeidValue);
			}
			
			for (int i = 0; i < schemeidList.size(); i++) {
				String scheme_id = schemeidList.get(i);
				list = new ArrayList();
				list.add(scheme_id);
				sql.setLength(0);
				// 获取最大排序号
				sql.append("select MAX(display_Id) maxnum from kq_shift_scheme_emp where scheme_id=?");
				int maxnum = 0;
				rs = dao.search(sql.toString(), list);
				if (rs.next()) {
					maxnum = rs.getInt("maxnum");
				}
				ArrayList emplists = new ArrayList();
				for (int j = 0; j < addArray.size(); j++) {
					String guidkey = PubFunc.decrypt((String) addArray.get(j));
					list = new ArrayList();
					list.add(scheme_id);
					list.add(guidkey);
					// 校验用户是否已存在该方案
					if(isSchemeGuidkey(rs, dao, list))
						continue;
					maxnum++;
					list.add(maxnum);

					emplists.add(list);
				}
				sql.setLength(0);
				sql.append("insert into kq_shift_scheme_emp");
				sql.append(" (scheme_id,guidkey,display_Id) values (?,?,?)");
				// 新增排班方案人员表记录
				dao.batchInsert(sql.toString(), emplists);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return config;
	}
	/**
	 * 通过guidkey获取a0101
	 * getA0101ByGuidkey
	 * @param guidkeys
	 * @return
	 * @date 2019年1月5日 下午4:13:26
	 * @author linbz
	 */
	private String getA0101ByGuidkey(String guidkeys) {
		String A0101s = "";
		RowSet rs = null;
		try {
			ArrayList<String> kq_dbase_list = KqPrivForHospitalUtil.getB0110Dase(userView, conn);
			StringBuffer sql = new StringBuffer("");
			for(int i=0;i<kq_dbase_list.size();i++) {
				String dbname = kq_dbase_list.get(i);
				if(StringUtils.isBlank(dbname))
					continue;
				sql.append("select A0101 from ").append(dbname).append("A01 where GUIDKEY in ( ").append(guidkeys).append(")");
				if(i < kq_dbase_list.size()-1)
					sql.append(" UNION ALL ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next()) {
				A0101s += "," + rs.getString("A0101");
			}
			if(StringUtils.isNotEmpty(A0101s))
				A0101s = A0101s.substring(1);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return A0101s;
	}
	/**
	 * 校验该排班方案是否包含这个用户
	 * isSchemeGuidkey
	 * @param rs
	 * @param dao
	 * @param valueList
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月28日 上午11:38:58
	 * @author linbz
	 */
	private boolean isSchemeGuidkey(RowSet rs, ContentDAO dao, ArrayList valueList) throws GeneralException {
		boolean isBe = false;
		try {
			rs = dao.search("select scheme_id from kq_shift_scheme_emp where scheme_id=? and guidkey=?", valueList);
			if(rs.next())
				isBe = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return isBe;
	}
	/**
	 * 取消班组人员
	 * deleteShiftGroupEmp
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月30日 下午5:15:23
	 * @author linbz
	 */
	@Override
    public String deleteShiftGroupEmp(JSONObject jsonObj) throws GeneralException {

		String config = "";
		RowSet rs = null;
		try {
			JSONArray removeArray = jsonObj.getJSONArray("removeArray");
			if(removeArray.size() < 1)
				return config;
			
			// 班组编号
			String groupid = jsonObj.getString("group_id");
			groupid = PubFunc.decrypt(groupid);
			// 班组编号
			String orgid = jsonObj.getString("org_id");
			if (orgid.indexOf("`") != -1)
				orgid = StringUtils.split(orgid, "`")[0];
			// 排班方案id
			String schemeid = jsonObj.getString("scheme_id");
			schemeid = PubFunc.decrypt(schemeid);
			
			ArrayList list = new ArrayList();
			ArrayList<String> guidkeylist = new ArrayList<String>();
			String guidkeySql = "";
			for (int i = 0; i < removeArray.size(); i++) {
				String guidkey = PubFunc.decrypt((String) removeArray.get(i));
				guidkeySql += ",?";
				guidkeylist.add(guidkey);
			}
			guidkeySql = guidkeySql.substring(1);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			// 删除人员班组对应表记录 kq_group_emp_v2
			sql.append("delete from kq_group_emp_v2 where guidkey in ("+guidkeySql+") and group_id=? ");
			list = new ArrayList();
			list.addAll(guidkeylist);
			list.add(groupid);
			dao.delete(sql.toString(), list);
			//排班方案编号为空时，是固定班制的人员设置，不用删除排班方案人员表的数据
			if(StringUtils.isEmpty(schemeid))
				return config;
			
			// 调出日期
			String removeyear = jsonObj.getString("removeyear");
			String removemonth = jsonObj.getString("removemonth");
			String removeweekIndex = jsonObj.getString("removeweekIndex");
			// 通过 kq_shift_scheme 获取时间范围scope  
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(removeyear), Integer.valueOf(removemonth),
			        Integer.valueOf("-1".equals(removeweekIndex) ? "1" : removeweekIndex));
			String startDate = ((String) dateList.get(0)).split(":")[0];
			String endDate = ((String) dateList.get(dateList.size() - 1)).split(":")[0];
			
			String startYearAndMonth = startDate.substring(0, 7);
			String endYearAndMonth = endDate.substring(0, 7);
			//排班页面移出班组人员时，如果选择的是全月的话，选择的月份的第一周是跨月的则从第二周开始移出排班人员
			if("-1".equals(removeweekIndex) && !startYearAndMonth.equalsIgnoreCase(endYearAndMonth)) {
				dateList = getWeekDateByWeekInMonth(Integer.valueOf(removeyear), Integer.valueOf(removemonth), 2);
				startDate = ((String) dateList.get(0)).split(":")[0];
				endDate = ((String) dateList.get(dateList.size() - 1)).split(":")[0];
			}
			
			// 删除排班方案人员表kq_shift_scheme_emp
			sql.setLength(0);
			list = new ArrayList();
			//查询排版方案
			list.add(startDate + "-" + endDate);
			list.add(groupid);
			list.addAll(guidkeylist);
			sql.append("delete from kq_shift_scheme_emp ");
			sql.append(" where scheme_id in ( ");
			sql.append(" select scheme_id from kq_shift_scheme where scope>=? and group_id=? ");
			sql.append(" ) ");
			sql.append(" and guidkey in ("+guidkeySql+") ");
			
			dao.delete(sql.toString(), list);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return config;
	}
	/**
	 * 班组成员变动维护
	 * changeShiftGroupEmp
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月1日 下午6:48:48
	 * @author linbz
	 */
	@Override
    public String changeShiftGroupEmp(JSONObject jsonObj) throws GeneralException {

		String config = "";
		try {
			// 班组编号
			String groupid = jsonObj.getString("group_id");
			groupid = PubFunc.decrypt(groupid);
			// 人数
			int selectLen = jsonObj.getInt("selectLen");
			ArrayList list = new ArrayList();
			list.add(selectLen);
			list.add(groupid);
			StringBuffer sql = new StringBuffer("");
			sql.append("update kq_shift_group set Member_count=? where Group_id=?");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString(), list);

			deleteShiftGroupEmp(jsonObj);
			config = addShiftGroupEmp(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return config;
	}

	/**
	 * 获取调换人员的信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemId
	 *            方案编号
	 * @param guidKey
	 *            guidkey
	 * @param filterValue
	 *            查询条件
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList<HashMap<String, String>> searchPsrsonInfo(String groupId, String schemId, String guidKey,
                                                               String filterValue) throws GeneralException {
		ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();
		RowSet rs = null;
		try {
			// 班组编号
			groupId = StringUtils.isEmpty(groupId) ? groupId : PubFunc.decrypt(groupId);
			schemId = StringUtils.isEmpty(schemId) ? schemId : PubFunc.decrypt(schemId);
			guidKey = StringUtils.isEmpty(guidKey) ? guidKey : PubFunc.decrypt(guidKey);
			if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(schemId))
				return personList;

			HashMap paramMap = KqPrivForHospitalUtil.getKqParameter(this.conn);
			String gNo = (String) paramMap.get("g_no");
			ArrayList<String> dbNames = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			StringBuffer a01Sql = new StringBuffer();
			ArrayList<String> paramList = new ArrayList<String>();
			for (String nbase : dbNames) {
				if (StringUtils.isNotEmpty(a01Sql.toString()))
					a01Sql.append(" union all ");

				a01Sql.append("select '" + nbase + "' as nbase,a0100,guidkey,a0101 from " + nbase + "a01");
				a01Sql.append(" where 1=1");
				if (StringUtils.isNotEmpty(guidKey)) {
					a01Sql.append(" and guidkey=?");
					paramList.add(guidKey);
				} else {
					a01Sql.append(" and (a0101 like ?");
					paramList.add("%" + filterValue + "%");
					if (StringUtils.isNotEmpty(gNo)) {
						a01Sql.append(" or " + gNo + " like ?");
						paramList.add("%" + filterValue + "%");
					}
					a01Sql.append(")");
				}
			}

			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select a01.nbase,a01.a0100,a01.guidkey,a01.a0101");
			searchSql.append(" from (" + a01Sql + ") a01,kq_shift_scheme scheme,kq_shift_scheme_emp emp");
			searchSql.append(" where a01.GUIDKEY=emp.guidkey and scheme.Scheme_id=emp.Scheme_id");
			searchSql.append(" and scheme.Group_id=? and scheme.Scheme_id=?");
			paramList.add(groupId);
			paramList.add(schemId);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(searchSql.toString(), paramList);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String nbase = rs.getString("nbase");
				String guidkey = rs.getString("guidkey");
				String a0101 = rs.getString("a0101");
				String a0100 = rs.getString("a0100");
				map.put("guidkey", PubFunc.encrypt(guidkey));
				map.put("a0101", a0101);
				map.put("photoPath", getPhotoPath(nbase, a0100));
				personList.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}

		return personList;
	}

	/**
	 * 获取人员照片
	 * 
	 * @param nbase
	 *            人员库
	 * @param a0100
	 *            人员编号
	 * @return
	 */
	private String getPhotoPath(String nbase, String a0100) {
		if (nbase == null || nbase.length() < 1)
			return "/images/photo.jpg";

		StringBuffer photoUrl = new StringBuffer();
		try {
			PhotoImgBo imgBo = new PhotoImgBo(conn);
			String imgRootPath = null;
			imgRootPath = imgBo.getPhotoRootDir();

			if (imgRootPath != null && imgRootPath.length() > 0) {
				String path = imgRootPath + imgBo.getPhotoRelativeDir(nbase, a0100);
				// szk 查找是否有头像设置的图片，没有的话创建默认photo.jpg
				String fileWName = imgBo.getPersonImageWholeName(path, "h_img");
				// 如果不存在文件，创建文件
				if (fileWName.length() < 1) {
					fileWName = imgBo.getPersonImageWholeName(path, "photo");
					if (fileWName.length() < 1)
						fileWName = imgBo.createPersonPhoto(path, conn, nbase, a0100, "photo");

				}

				String filepath = path + fileWName;
				// 如果创建失败，使用默认图像
				if (fileWName.length() < 1) {
					photoUrl.append("/images/photo.jpg");
				} else { // 如果有图片或创建了图片，使用新图片
					filepath = PubFunc.encryption(filepath);
					filepath = SafeCode.encode(filepath);
					photoUrl.append("/servlet/DisplayOleContent?filePath=").append(filepath).append("&bencrypt=true");
				}

			} else { // 如果没有设置附件路径，则直接去库里拿图片
				String filename = "";
				if ("".equals(a0100))
					filename = "";
				else {
					filename = imgBo.getPhotoPath(nbase, a0100);
				}

				if (!"".equals(filename)) {
					photoUrl.append("/servlet/DisplayOleContent?filename=");
					filename = PubFunc.encryption(filename);
					filename = SafeCode.encode(filename);
					photoUrl.append(filename);
				} else
					photoUrl.append("/images/photo.jpg");

			}

			if (photoUrl.toString().length() < 1)
				photoUrl.append("/images/photo.jpg");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return photoUrl.toString();
	}

	/**
	 * 保存人员调换的数据
	 * 
	 * @param paramMap
	 *            参数对象:oldGuidKey:被调换人的guidkey; newGuidKey: 调换人的guidkey; year:年份
	 *            month:月份 weekIndex:第几周;schemeId:方案编号;
	 * @throws GeneralException
	 */
	@Override
    public void savePersonChange(HashMap<String, String> paramMap)
	        throws GeneralException {
		RowSet rs = null;
		try {
			String oldGuidKey = paramMap.get("oldGuidKey");
			String newGuidKey = paramMap.get("newGuidKey");
			String year = paramMap.get("year");
			String month = paramMap.get("month");
			String weekIndex = paramMap.get("weekIndex");
			String schemeId = paramMap.get("schemeId");
			oldGuidKey = StringUtils.isEmpty(oldGuidKey) ? oldGuidKey : PubFunc.decrypt(oldGuidKey);
			newGuidKey = StringUtils.isEmpty(newGuidKey) ? newGuidKey : PubFunc.decrypt(newGuidKey);
			schemeId = StringUtils.isEmpty(schemeId) ? schemeId : PubFunc.decrypt(schemeId);
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month),
			        Integer.valueOf(weekIndex));
			
			String sDate = dateList.get(0).split(":")[0];
			String eDate = dateList.get(dateList.size() - 1).split(":")[0];
			StringBuffer sql = new StringBuffer();
			sql.append("select * from kq_employ_shift_v2");
			sql.append(" where guidkey in (?,?)");
			sql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy.MM.dd"));
			sql.append(">=? and " + Sql_switcher.dateToChar("Q03Z0", "yyyy.MM.dd"));
			sql.append("<=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(oldGuidKey);
			paramList.add(newGuidKey);
			paramList.add(sDate.replace(".", "-").replace(".", "-"));
			paramList.add(eDate.replace(".", "-").replace(".", "-"));
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), paramList);
			ArrayList<ArrayList<Object>> oldList = new ArrayList<ArrayList<Object>>();
			ArrayList<ArrayList<Object>> newList = new ArrayList<ArrayList<Object>>();
			while (rs.next()) {
				ArrayList<Object> valueList = new ArrayList<Object>();
				valueList.add(new Timestamp(DateUtils.getDate(rs.getString("q03z0"), "yyyy-MM-dd").getTime()));
				valueList.add(rs.getString("class_id_1"));
				valueList.add(rs.getString("comment_1"));
				valueList.add(rs.getString("Comment_color_1"));
				valueList.add(rs.getString("class_id_2"));
				valueList.add(rs.getString("comment_2"));
				valueList.add(rs.getString("Comment_color_2"));
				valueList.add(rs.getString("class_id_3"));
				valueList.add(rs.getString("comment_3"));
				valueList.add(rs.getString("Comment_color_3"));
				valueList.add(rs.getString("extra_hour"));
				String guidKey = rs.getString("guidkey");
				if (guidKey.equalsIgnoreCase(oldGuidKey)) {
					valueList.add(newGuidKey);
					oldList.add(valueList);
				} else {
					valueList.add(oldGuidKey);
					newList.add(valueList);
				}
			}

			sql.setLength(0);
			sql.append("delete from kq_employ_shift_v2");
			sql.append(" where guidkey in (?,?)");
			sql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy.MM.dd"));
			sql.append(">=? and " + Sql_switcher.dateToChar("Q03Z0", "yyyy.MM.dd"));
			sql.append("<=?");
			dao.delete(sql.toString(), paramList);

			sql.setLength(0);
			sql.append("insert into kq_employ_shift_v2");
			sql.append("(q03z0,class_id_1,comment_1,Comment_color_1,class_id_2,comment_2,Comment_color_2,"
			        + "class_id_3,comment_3,Comment_color_3,extra_hour,guidkey)");
			sql.append(" values (?,?,?,?,?,?,?,?,?,?,?,?)");
			if (oldList != null && oldList.size() > 0)
				dao.batchInsert(sql.toString(), oldList);

			if (newList != null && newList.size() > 0)
				dao.batchInsert(sql.toString(), newList);
			
			
			String columns = "display_Id,work_hour";

			ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
			for (HashMap<String, String> map : shiftStatisticList)
				columns += ",stat_" + map.get("statisticsType");
			
			String[] column = columns.split(",");
			sql.setLength(0);
			sql.append("select guidkey," + columns + " from kq_shift_scheme_emp");
			sql.append(" where scheme_id=? and guidkey in (?,?)");
			paramList.clear();
			paramList.add(schemeId);
			paramList.add(oldGuidKey);
			paramList.add(newGuidKey);
			rs = dao.search(sql.toString(), paramList);
			ArrayList<ArrayList<String>> paramsList = new ArrayList<ArrayList<String>>();
			while (rs.next()) {
				ArrayList<String> valueList = new ArrayList<String>();
				for(String columnId : column)
					valueList.add(rs.getString(columnId));
				
				valueList.add(schemeId);
				String guidkey = rs.getString("guidkey");
				if(oldGuidKey.equalsIgnoreCase(guidkey))
					valueList.add(newGuidKey);
				else
					valueList.add(oldGuidKey);
					
				paramsList.add(valueList);
			}
			
			sql.setLength(0);
			sql.append("update kq_shift_scheme_emp set");
			for(String columnId : column)
				sql.append(" " + columnId + "=?,");
			
			sql.setLength(sql.length() - 1);
			sql.append(" where scheme_id=? and guidkey =?");
			
			dao.batchUpdate(sql.toString(), paramsList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 获取页面显示数据的日期范围
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            月份中的第几周
	 * @return
	 */
	@Override
    public String getWeekScope(String year, String month, String weekIndex) {
		ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month),
		        Integer.valueOf(weekIndex));
		String sDate = dateList.get(0).split(":")[0];
		String eDate = dateList.get(dateList.size() - 1).split(":")[0];
		return sDate + "-" + eDate;
	}

	/**
	 * 获取页面显示数据的日期范围
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            月份中的第几周
	 * @return
	 */
	@Override
    public String getLastWeekScope(String year, String month, String weekIndex) {
		if (Integer.valueOf(weekIndex) < 1)
			return "";

		ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(year), Integer.valueOf(month),
		        Integer.valueOf(weekIndex) - 1);
		String sDate = dateList.get(0).split(":")[0];
		String eDate = dateList.get(dateList.size() - 1).split(":")[0];
		return sDate + "-" + eDate;
	}

	/**
	 * 删除排班信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 * @param weekScope
	 *            时间范围
	 * @throws GeneralException
	 */
	@Override
    public void deleteShiftInfo(String groupId, String schemeId, String weekScope) throws GeneralException {
		RowSet rs = null;
		try {
			groupId = StringUtils.isEmpty(groupId) ? groupId : PubFunc.decrypt(groupId);
			schemeId = StringUtils.isEmpty(schemeId) || "-1".equals(schemeId) ? schemeId : PubFunc.decrypt(schemeId);
			if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(schemeId))
				return;

			ArrayList<String> paramList = new ArrayList<String>();
			String[] dates = weekScope.split("-");
			paramList.add(dates[0].replace(".", "-"));
			paramList.add(dates[1].replace(".", "-"));
			paramList.add(groupId);

			StringBuffer sql = new StringBuffer();
			sql.append("delete from kq_employ_shift_v2");
			sql.append(" where " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
			sql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + " <=?");
			sql.append(" and guidkey in (select emp.guidkey");
			sql.append(" from kq_shift_scheme_emp emp left join kq_shift_scheme scheme");
			sql.append(" on emp.Scheme_id=scheme.Scheme_id");
			sql.append(" where scheme.Group_id=?");
			if (!"-1".equals(schemeId)) {
				sql.append(" and scheme.Scheme_id=?");
				paramList.add(schemeId);
			}

			sql.append(")");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql.toString(), paramList);
			// 自动更新工时、统计项的数据
			ArrayList<String> schemeIdList = new ArrayList<String>();
			if ("-1".equals(schemeId)) {
				String date = weekScope.split("-")[0];
				date = date.substring(0, date.lastIndexOf("."));
				StringBuffer searchSql = new StringBuffer();
				paramList.clear();
				searchSql.append("select scheme_id,scope from kq_shift_scheme where Group_id=? and scope like ?");
				paramList.add(groupId);
				paramList.add("%" + date + "%");
				rs = dao.search(searchSql.toString(), paramList);
				ArrayList<String> schemeIdTemp = new ArrayList<String>();
				while(rs.next()) {
					String scope = rs.getString("scope");
					String start = scope.split("-")[0];
					String end = scope.split("-")[1];
					if(start.startsWith(date) && end.startsWith(date))
						schemeIdTemp.add(rs.getString("scheme_id"));
					else
						schemeIdList.add(rs.getString("scheme_id"));
				}
				
				ArrayList<HashMap<String, String>> statisticsLsit = getShiftStatistics();
				StringBuffer updateSql = new StringBuffer();
				paramList.clear();
				updateSql.append("update kq_shift_scheme_emp set Work_hour=?");
				paramList.add("0");
				for (HashMap<String, String> map : statisticsLsit) {
					updateSql.append(",stat_" + map.get("statisticsType") + "=?");
					paramList.add("0");
				}

				updateSql.append(" where scheme_id=?");
				ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				for(String tempId : schemeIdTemp) {
					ArrayList<String> tempList = new ArrayList<String>();
					tempList.addAll(paramList);
					tempList.add(tempId);
					list.add(tempList);
				}
				
				dao.batchUpdate(updateSql.toString(), list);
			} else
				schemeIdList.add(schemeId);
			
			ArrayList<HashMap<String, String>> statisticsLsit = getShiftStatistics();
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update kq_shift_scheme_emp set Work_hour=0");
			for (HashMap<String, String> map : statisticsLsit)
			    updateSql.append(",stat_" + map.get("statisticsType") + "=0");
			
			updateSql.append(" where scheme_id=?");
			
			ArrayList<ArrayList<String>> paramsList = new ArrayList<ArrayList<String>>();
			for(String scheme : schemeIdList) {
			    ArrayList<String> valueLsit = new ArrayList<String>();
			    valueLsit.add(scheme);
			    paramsList.add(valueLsit);
			}
            
			dao.batchUpdate(updateSql.toString(), paramsList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 查询排班方案的备注信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            排班方案编号
	 * @return
	 */
	@Override
    public HashMap<String, String> getShiftRemark(String groupId, String schemeId) {
		HashMap<String, String> remarkMap = new HashMap<String, String>();
		RowSet rs = null;
		try {
			groupId = StringUtils.isNotEmpty(groupId) ? PubFunc.decrypt(groupId) : groupId;
			schemeId = StringUtils.isNotEmpty(schemeId) ? PubFunc.decrypt(schemeId) : schemeId;
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("Select shift_comment,emp_comment,train_comment from kq_shift_scheme");
			searchSql.append(" where Group_id=? and scheme_id=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(groupId);
			paramList.add(schemeId);

			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(searchSql.toString(), paramList);
			if (rs.next()) {
				remarkMap.put("shiftComment", rs.getString("shift_comment"));
				remarkMap.put("empComment", rs.getString("emp_comment"));
				remarkMap.put("trainComment", rs.getString("train_comment"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return remarkMap;
	}

	/**
	 * 保存排班方案的备注信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            排班方案编号
	 * @param shiftComment
	 *            排班备注
	 * @param empComment
	 *            人员调换备注
	 * @param trainComment
	 *            培训备注
	 * @return
	 */
	@Override
    public void saveShiftRemark(String groupId, String schemeId, String shiftComment, String empComment,
                                String trainComment) {
		try {
			groupId = StringUtils.isNotEmpty(groupId) ? PubFunc.decrypt(groupId) : groupId;
			schemeId = StringUtils.isNotEmpty(schemeId) ? PubFunc.decrypt(schemeId) : schemeId;
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update kq_shift_scheme set shift_comment=?,emp_comment=?,train_comment=? ");
			updateSql.append(" where Group_id=? and scheme_id=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(PubFunc.keyWord_filter(shiftComment));
			paramList.add(PubFunc.keyWord_filter(empComment));
			paramList.add(PubFunc.keyWord_filter(trainComment));
			paramList.add(groupId);
			paramList.add(schemeId);

			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(updateSql.toString(), paramList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制上周排班的数据
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 * @param weekScope
	 *            本周时间范围
	 * @param lastWeekScope
	 *            上周时间范围
	 * @param copyType
	 *            复制方式
	 */
	@Override
    public void copyShiftInfo(String groupId, String schemeId, String weekScope, String lastWeekScope,
                              String copyType) {
		RowSet rs = null;
		try {
			groupId = StringUtils.isNotEmpty(groupId) ? PubFunc.decrypt(groupId) : groupId;
			schemeId = StringUtils.isNotEmpty(schemeId) ? PubFunc.decrypt(schemeId) : schemeId;
			String lastSchemeId = "";
			ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
			String searchSql = "select scheme_id from kq_shift_scheme where scope=? and group_id=?";
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(lastWeekScope);
			paramList.add(groupId);
			rs = dao.search(searchSql, paramList);
			if (rs.next())
				lastSchemeId = rs.getString("scheme_id");
			
			int personCount = getPersonCount(schemeId);
			if(personCount > 0) {
				if(!ExistSchemeShift(lastSchemeId, lastWeekScope)) {
					this.userView.getHm().put("shiftMsg", "上周没有排班，不能进行复制！");
					return;
				}
			} else {
				personCount = getPersonCount(lastSchemeId);
				if(personCount < 1) {
					//如果上周的排班方案中没有人的话直接将班组的人员添加到排班方案中
					insertSchemePerson(groupId, schemeId);
					return;
				} else {
					copylastWeekPerson(groupId, lastSchemeId, schemeId);
				}
			}
			
			if ("2".equalsIgnoreCase(copyType)) {
				StringBuffer deleteSql = new StringBuffer();
				deleteSql.append("delete from kq_employ_shift_v2 where guidkey");
				deleteSql.append(" in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
				deleteSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
				deleteSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?");
				paramList.clear();
				paramList.add(schemeId);
				paramList.add(weekScope.split("-")[0].replace(".", "-"));
				paramList.add(weekScope.split("-")[1].replace(".", "-"));
				
				dao.delete(deleteSql.toString(), paramList);
			}
			
			StringBuffer updateSql = new StringBuffer();
			StringBuffer updateColumns = new StringBuffer();
			if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				updateColumns.append("group_name=empLast.group_name,");
				updateColumns.append("display_Id=empLast.display_Id,");
				updateColumns.append("shift_comment=empLast.shift_comment,");
				updateColumns.append("extra_days=empLast.extra_days,");
				updateColumns.append("work_hour=empLast.work_hour,");
				updateColumns.append("extra_hour=empLast.extra_hour");
				for (HashMap<String, String> map : shiftStatisticList) {
					String statisticsType = map.get("statisticsType");
					updateColumns.append(",").append("stat_" + statisticsType);
					updateColumns.append("=empLast.").append("stat_" + statisticsType);
				}

				updateSql.append("update kq_shift_scheme_emp set ");
				updateSql.append(updateColumns);
				updateSql.append(" from (select * from kq_shift_scheme_emp where Scheme_id=?) empLast");
				updateSql.append(" where kq_shift_scheme_emp.Scheme_id=?");
				updateSql.append(" and kq_shift_scheme_emp.guidkey=empLast.guidkey");
				paramList.clear();
				paramList.add(lastSchemeId);
				paramList.add(schemeId);
				dao.update(updateSql.toString(), paramList);

				updateSql.setLength(0);
				updateSql.append("update kq_employ_shift_v2");
				updateSql.append(" set class_id_1=lastShift.class_id_1,");
				updateSql.append("class_id_2=lastShift.class_id_2,");
				updateSql.append("class_id_3=lastShift.class_id_3,");
				updateSql.append("comment_1=lastShift.comment_1,");
				updateSql.append("comment_2=lastShift.comment_2,");
				updateSql.append("comment_3=lastShift.comment_3,");
				updateSql.append("comment_color_1=lastShift.comment_color_1,");
				updateSql.append("comment_color_2=lastShift.comment_color_2,");
				updateSql.append("comment_color_3=lastShift.comment_color_3,");
				updateSql.append("extra_hour=lastShift.extra_hour");
				updateSql.append(" from (select * from kq_employ_shift_v2");
				updateSql.append(" where " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
				updateSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?");
				updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)) lastShift");
				updateSql.append(" where " + Sql_switcher.dateToChar("kq_employ_shift_v2.Q03Z0", "yyyy-MM-dd") + ">=?");
				updateSql.append(" and " + Sql_switcher.dateToChar("kq_employ_shift_v2.Q03Z0", "yyyy-MM-dd") + "<=?");
				updateSql.append(" and kq_employ_shift_v2.guidkey=lastShift.guidkey");
				updateSql.append(" and kq_employ_shift_v2.q03z0=" + Sql_switcher.addDays("lastShift.q03z0", "7"));
				updateSql.append(" and kq_employ_shift_v2.guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");

				paramList.clear();
				paramList.add(lastWeekScope.split("-")[0].replace(".", "-"));
				paramList.add(lastWeekScope.split("-")[1].replace(".", "-"));
				paramList.add(lastSchemeId);
				paramList.add(weekScope.split("-")[0].replace(".", "-"));
				paramList.add(weekScope.split("-")[1].replace(".", "-"));
				paramList.add(schemeId);

				if ("1".equalsIgnoreCase(copyType)) {
					updateSql.append(" and kq_employ_shift_v2.guidkey in (select distinct guidkey from kq_employ_shift_v2");
					updateSql.append(" where " + Sql_switcher.isnull("class_id_1", "-1"));
					updateSql.append("=-1 and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
					updateSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?");
					updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?))");
					paramList.add(weekScope.split("-")[0].replace(".", "-"));
					paramList.add(weekScope.split("-")[1].replace(".", "-"));
					paramList.add(schemeId);
				} 
				
				dao.update(updateSql.toString(), paramList);
			} else {
				updateColumns.append("group_name,display_Id,shift_comment,extra_days,");
				updateColumns.append("work_hour,extra_hour");
				for (HashMap<String, String> map : shiftStatisticList)
					updateColumns.append(",").append("stat_" + map.get("statisticsType"));

				updateSql.append("update kq_shift_scheme_emp emp set (");
				updateSql.append(updateColumns);
				updateSql.append(")=(select ");
				updateSql.append(updateColumns);
				updateSql.append(" from kq_shift_scheme_emp empLast where Scheme_id=?");
				updateSql.append(" and emp.guidkey=empLast.guidkey)");
				updateSql.append(" where emp.Scheme_id=?");

				paramList.clear();
				paramList.add(lastSchemeId);
				paramList.add(schemeId);
				dao.update(updateSql.toString(), paramList);

				updateSql.setLength(0);
				updateSql.append("update kq_employ_shift_v2 shift");
				updateSql.append(" set (class_id_1,class_id_2,class_id_3,");
				updateSql.append("comment_1,comment_2,comment_3,");
				updateSql.append("comment_color_1,comment_color_2,comment_color_3,extra_hour)");
				updateSql.append("=(select class_id_1,class_id_2,class_id_3,comment_1,comment_2,");
				updateSql.append("comment_3,comment_color_1,comment_color_2,comment_color_3,");
				updateSql.append("extra_hour from kq_employ_shift_v2 lastShift");
				updateSql.append(" where " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
				updateSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?");
				updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
				updateSql.append(" and shift.guidkey=lastShift.guidkey");
				updateSql.append(" and shift.q03z0=" + Sql_switcher.addDays("lastShift.q03z0", "7") +")");
				updateSql.append(" where " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + ">=?");
				updateSql.append(" and " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + "<=?");
				updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
				paramList.clear();
				paramList.add(lastWeekScope.split("-")[0].replace(".", "-"));
				paramList.add(lastWeekScope.split("-")[1].replace(".", "-"));
				paramList.add(lastSchemeId);
				paramList.add(weekScope.split("-")[0].replace(".", "-"));
				paramList.add(weekScope.split("-")[1].replace(".", "-"));
				paramList.add(schemeId);
				if ("1".equalsIgnoreCase(copyType)) {
					updateSql.append(" and shift.guidkey in (select distinct guidkey from kq_employ_shift_v2");
					updateSql.append(" where " + Sql_switcher.isnull("class_id_1", "-1"));
					updateSql.append("=-1 and " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + ">=?");
					updateSql.append(" and " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + "<=?");
					updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?))");
					paramList.add(weekScope.split("-")[0].replace(".", "-"));
					paramList.add(weekScope.split("-")[1].replace(".", "-"));
					paramList.add(schemeId);
				}

				dao.update(updateSql.toString(), paramList);
			}

			updateSql.setLength(0);
			updateSql.append("insert into kq_employ_shift_v2 (q03z0,guidkey,");
			updateSql.append("class_id_1,class_id_2,class_id_3,comment_1,comment_2,comment_3,");
			updateSql.append("comment_color_1,comment_color_2,comment_color_3,extra_hour)");
			updateSql.append(" select " + Sql_switcher.addDays("q03z0", "7") + " as q03z0,");
			updateSql.append("guidkey,class_id_1,class_id_2,class_id_3,comment_1,comment_2,comment_3,");
			updateSql.append("comment_color_1,comment_color_2,comment_color_3,extra_hour");
			updateSql.append(" from kq_employ_shift_v2");
			updateSql.append(" where " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + ">=?");
			updateSql.append(" and " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + "<=?");
			updateSql.append(" and guidkey not in (select distinct guidkey from kq_employ_shift_v2");
			updateSql.append(" where " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + ">=?");
			updateSql.append(" and " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") +"<=?)");
			updateSql.append(" and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
			paramList.clear();
			paramList.add(lastWeekScope.split("-")[0].replace(".", "-"));
			paramList.add(lastWeekScope.split("-")[1].replace(".", "-"));
			paramList.add(weekScope.split("-")[0].replace(".", "-"));
			paramList.add(weekScope.split("-")[1].replace(".", "-"));
			paramList.add(lastSchemeId);
			dao.update(updateSql.toString(), paramList);
			ArrayList<String> schemeIdList = new ArrayList<String>();
			schemeIdList.add(schemeId);
			countClasses(schemeIdList, null);
		} catch (Exception e) {
			e.printStackTrace();
			this.userView.getHm().put("shiftMsg", e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 判断某周是否进行了排班
	 * 
	 * @param schemeId
	 *            方案编号
	 * @param dateScop
	 *            时间范围
	 * @return
	 */
	private boolean ExistSchemeShift(String schemeId, String dateScop) {
		boolean flag = false;
		if(StringUtils.isEmpty(schemeId) || StringUtils.isEmpty(dateScop))
			return flag;
		
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select 1 from kq_employ_shift_v2");
			sql.append(" where " + Sql_switcher.dateToChar("q03z0","yyyy-MM-dd"));
			sql.append(">=? and " + Sql_switcher.dateToChar("q03z0","yyyy-MM-dd"));
			sql.append("<=? and guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(dateScop.split("-")[0].replace(".", "-"));
			paramList.add(dateScop.split("-")[1].replace(".", "-"));
			paramList.add(schemeId);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), paramList);
			if(rs.next())
				flag = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	/**
	 * 自动排班
	 * 
	 * @param groupId
	 *            班组编号
	 * @param fromDate
	 *            开始时间
	 * @param toDate
	 *            结束时间
	 * @param shfitType
	 *            排班类型
	 */
	@Override
    public void autoShift(String groupId, String fromDate, String toDate, String shfitType) {
		RowSet rs = null;
		try {
			groupId = StringUtils.isEmpty(groupId) ? groupId : PubFunc.decrypt(groupId);
			String restType = "1";
			String sql = "select rest_type from kq_shift_group where group_id=?";
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(groupId);
			rs = dao.search(sql, paramList);
			if(rs.next()) 
				restType = rs.getString("rest_type");
			
			restType = StringUtils.isEmpty(restType) ? "1" : restType;
			sql = "select scheme_id,scope from kq_shift_scheme where group_id=?";
			rs = dao.search(sql, paramList);
			ArrayList<String> schemeIdList = new ArrayList<String>();
			HashMap<String, String> schemeIdMap = new HashMap<String, String>();
			while (rs.next()) {
				String scope = rs.getString("scope");
				String[] scopes = scope.split("-");
				if (fromDate.compareTo(scopes[1]) > 0 || toDate.compareTo(scopes[0]) < 0)
					continue;

				schemeIdList.add(rs.getString("scheme_id"));
				schemeIdMap.put(rs.getString("scheme_id"), scope);
			}
			
			ArrayList<String> scopesList = getWeekScopes(fromDate, toDate);
			for(String scope : scopesList) {
				if(schemeIdMap.containsValue(scope))
					continue;
				
				String date = scope.split("-")[0];
				this.year = Integer.valueOf(date.split("[.]")[0]);
				this.month = Integer.valueOf(date.split("[.]")[1]);
				this.weekIndex = getWeekIndex(date);
				String schemeId = addShiftScheme(groupId, scope, "");
				schemeIdList.add(schemeId);
				schemeIdMap.put(schemeId, scope);
			}

			int shiftCycle = 0;
			String[] shiftDatas = null;
			sql = "select shift_cycle,shift_data from kq_shift_group where Group_id=?";
			rs = dao.search(sql, paramList);
			if (rs.next()) {
				shiftCycle = rs.getInt("shift_cycle");
				String shiftData = rs.getString("shift_data");
				shiftDatas = StringUtils.isEmpty(shiftData) ? null : shiftData.split(";");
			}

			if (shiftDatas == null || shiftDatas.length < 1)
				return;

			ArrayList<ArrayList<Object>> insetValueList = new ArrayList<ArrayList<Object>>();
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select guidkey from kq_shift_scheme_emp where scheme_id=?");
			if ("1".equals(shfitType)) {
				searchSql.append(" and guidkey not in (select distinct guidkey from kq_employ_shift_v2");
				searchSql.append(" where " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + ">=?");
				searchSql.append(" and " + Sql_switcher.dateToChar("Q03Z0", "yyyy-MM-dd") + "<=?)");
			}

			StringBuffer deleteSql = new StringBuffer();
			deleteSql.append(
			        "delete from kq_employ_shift_v2 where guidkey in (select guidkey from kq_shift_scheme_emp where Scheme_id=?)");
			deleteSql.append(" and " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + ">=? and ");
			deleteSql.append(Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") +"<=?");
			
			IfRestDate ifRestDate = new IfRestDate();
			ArrayList restList=IfRestDate.search_RestOfWeek("UN",userView,conn);
			//休息日
			String restdate = ResourceFactory.getProperty("kq.date.work");
			
			for (String schemeId : schemeIdList) {
				paramList.clear();
				String fDate = fromDate;
				String tDate = toDate;
				String scope = schemeIdMap.get(schemeId);
				String[] scopes = scope.split("-");
				paramList.add(schemeId);
				if (fromDate.compareTo(scopes[0]) < 0)
					fDate = scopes[0];

				paramList.add(fDate.replace(".", "-"));
				if (toDate.compareTo(scopes[1]) > 0)
					tDate = scopes[1];

				paramList.add(tDate.replace(".", "-"));
				if(!"1".equals(shfitType))
					dao.delete(deleteSql.toString(), paramList);
				
				paramList.clear();
				paramList.add(schemeId);
				if ("1".equals(shfitType)) {
					paramList.add(fromDate.replace(".", "-"));
					paramList.add(toDate.replace(".", "-"));

				}

				rs = dao.search(searchSql.toString(), paramList);
				while (rs.next()) {
					String guidkey = rs.getString("guidkey");
					Calendar cal = Calendar.getInstance(Locale.CHINA);
					cal.setTime(DateUtils.getDate(tDate, "yyyy.MM.dd"));
					Date tdate = cal.getTime();
					cal.setTime(DateUtils.getDate(fDate, "yyyy.MM.dd"));
					Date fdate = cal.getTime();
					int feastCount = 0;
					while (fdate.compareTo(tdate) < 1) {
						//是否是休息日
						boolean isRest = false;
						//法定节假日周末自动排休
						if("1".equalsIgnoreCase(restType)) {
							String cur_date = DateUtils.format(fdate, "yyyy.MM.dd");
							for(int i = 0; i < restList.size(); i++) {
								String rest_date = (String)restList.get(i);
								if(StringUtils.isEmpty(rest_date))
									continue;
								
								String returnFlag = IfRestDate.is_RestDate(cur_date, userView, rest_date, "UN", this.conn);
								//如果是公休日中的休息日，则跳出循环
								if(!restdate.equalsIgnoreCase(returnFlag)) {
									isRest = true;
									feastCount++;
									break;
								}
							}
						}
						
						ArrayList<Object> valueList = new ArrayList<Object>();
						int diffDays = DateUtils.dayDiff(DateUtils.getDate(fromDate, "yyyy.MM.dd"), fdate) - feastCount;
						int shiftIndex = diffDays % shiftCycle;
						String shiftClass = "";
						String[] shiftClasses = null;

						valueList.add(new Timestamp(fdate.getTime()));
						valueList.add(guidkey);
						int index = 0;
						
						
						if(isRest) {
							index++;
							valueList.add("0");
						} else {
							if (shiftIndex < shiftDatas.length) {
								shiftClass = shiftDatas[shiftIndex];
								shiftClasses = shiftClass.split(",");
							} else {
								cal.add(Calendar.DATE, 1);
								fdate = cal.getTime();
								continue;
							}
							
							for (String classId : shiftClasses) {
								index++;
								valueList.add(classId);
							}
						}
						

						for (int i = index + 1; i < 4; i++) {
							index++;
							valueList.add(null);
						}

						insetValueList.add(valueList);
						cal.add(Calendar.DATE, 1);
						fdate = cal.getTime();
					}

				}
			}

			StringBuffer insertSql = new StringBuffer();
			insertSql.append("insert into kq_employ_shift_v2");
			insertSql.append(" (Q03Z0,guidkey,Class_id_1,Class_id_2,Class_id_3)");
			insertSql.append(" values (?,?,?,?,?)");
			dao.batchInsert(insertSql.toString(), insetValueList);
			countClasses(schemeIdList, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 自动统计班次信息
	 * 
	 * @param schemeIdList
	 *            方案编号的List
	 * @param guidKey
	 *            人员唯一的id
	 */
	private void countClasses(ArrayList<String> schemeIdList, String guidKey) {
		RowSet rs = null;
		try {
			if (schemeIdList == null || schemeIdList.size() < 1)
				return;

			HashMap<String, String> schemeIdMap = new HashMap<String, String>();
			ContentDAO dao = new ContentDAO(this.conn);
			String groupId = "";
			String sql = "select scheme_id,group_id,scope from kq_shift_scheme where scheme_id in (0";
			for(String schemeId : schemeIdList)
				sql += ",?";
			
			sql += ")";
			rs = dao.search(sql, schemeIdList);
			while (rs.next()) {
				if (StringUtils.isEmpty(groupId))
					groupId = rs.getString("group_id");

				schemeIdMap.put(rs.getString("scheme_id"), rs.getString("scope"));
			}

			HashMap<String, HashMap<String, String>> countMap = new HashMap<String, HashMap<String, String>>();
			ArrayList<HashMap<String, String>> statisticsLsit = getShiftStatistics();
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update kq_shift_scheme_emp set Work_hour=?");
			for (HashMap<String, String> map : statisticsLsit)
				updateSql.append(",stat_" + map.get("statisticsType") + "=?");

			updateSql.append(" where guidkey=? and scheme_id=?");
			ArrayList<ArrayList<String>> updateValueList = new ArrayList<ArrayList<String>>();
			for (String schemeId : schemeIdList) {
				String scope = schemeIdMap.get(schemeId);
				HashMap<String, HashMap<String, Double>> statisticsMap = statClasses(groupId, scope.split("-")[0],
				        scope.split("-")[1], guidKey, "shiftData");

				for (String key : statisticsMap.keySet()) {
					HashMap<String, String> statDataMap = new HashMap<String, String>();
					ArrayList<String> valueList = new ArrayList<String>();
					HashMap<String, Double> map = statisticsMap.get(key);
					valueList.add(PubFunc.round((map.get("workHours") / 60.0) + "", 1));
					statDataMap.put("work_hour", PubFunc.round((map.get("workHours") / 60.0) + "", 1));
					for (HashMap<String, String> statMap : statisticsLsit) {
						String value = "0.0";
						String keyValue = statMap.get("statisticsType");
						if (map.get(keyValue) != null)
							value = map.get(statMap.get("statisticsType")) + "";

						valueList.add(value);
						keyValue = "stat_" + keyValue;
						
						String unit = statMap.get("unit");
						Double DoubleValue = 0.00;
						if ("01".equals(unit)) {
							DoubleValue = Double.valueOf(value) / 60.0;
							statDataMap.put(keyValue, DoubleValue + "");
						} else if ("02".equals(unit)) {
							DoubleValue = Double.valueOf(value) / 60.0 / 8.0;
							statDataMap.put(keyValue, DoubleValue + "");
						} else
							statDataMap.put(keyValue, value);
					}

					valueList.add(key);
					valueList.add(schemeId);
					updateValueList.add(valueList);
					countMap.put(PubFunc.encrypt(key), statDataMap);
				}
			}
			
			dao.batchUpdate(updateSql.toString(), updateValueList);
			this.countDataMap = countMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 获取班次的统计数据
	 * 
	 * @param groupId
	 *            班组编号
	 * @param fdate
	 *            开始时间
	 * @param eDate
	 *            结束时间
	 * @param guidKeys
	 *            人员唯一性编码
	 * @return
	 */
	private HashMap<String, HashMap<String, Double>> statClasses(String groupId, String fdate, String eDate,
	        String guidKeys, String dataType) {
		RowSet rs = null;
		HashMap<String, HashMap<String, Double>> statisticsMap = new HashMap<String, HashMap<String, Double>>();
		try {
			if (StringUtils.isEmpty(fdate) || StringUtils.isEmpty(eDate))
				return statisticsMap;

			ContentDAO dao = new ContentDAO(this.conn);
			HashMap<String, HashMap<String, String>> classInfoMap = getClassInfo();
			ArrayList<String> paramList = new ArrayList<String>();
			StringBuffer shiftSql = new StringBuffer();
			shiftSql.append(
			        "select distinct shift.* from kq_employ_shift_v2 shift,kq_shift_scheme scheme,kq_shift_scheme_emp emp");
			shiftSql.append(" where shift.guidkey=emp.guidkey and emp.Scheme_id=scheme.Scheme_id");

			if (StringUtils.isNotEmpty(guidKeys)) {
				shiftSql.append(" and shift.guidkey in ('#'");

				String[] personIds = guidKeys.split(",");
				for (String personId : personIds) {
					paramList.add(personId);
					shiftSql.append(",?");
				}

				shiftSql.append(")");
			}
			// linbz 兼容排班审查
			if(StringUtils.isNotBlank(groupId) && "shiftData".equalsIgnoreCase(dataType)) {
				shiftSql.append(" and scheme.Group_id=?");
				shiftSql.append(" and " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + ">=?");
				shiftSql.append(" and " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + "<=?");
				paramList.add(groupId);
				paramList.add(fdate.replace(".", "-"));
				paramList.add(eDate.replace(".", "-"));
			}
			// 审查分方案时间范围查
			else if ("shiftCheck".equalsIgnoreCase(dataType)) {
				shiftSql.append(" and scheme.state='04' ");
				StringBuffer dateSql = new StringBuffer("");
				for(int i = 0; i < dateScopeList.size(); i++) {
					String dateScope = dateScopeList.get(i);
					String[] dates = StringUtils.split(dateScope, "-");
					paramList.add(dates[0].replace(".", "-"));
					paramList.add(dates[1].replace(".", "-"));
					dateSql.append("or (" + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + ">=?");
					dateSql.append(" and " + Sql_switcher.dateToChar("shift.Q03Z0", "yyyy-MM-dd") + "<=?)");
				}
				
				if(StringUtils.isNotBlank(dateSql.toString())) {
					String sqlCheck = dateSql.toString().substring(2);
					shiftSql.append(" and (" + sqlCheck + ") ");
				}
			}
			
			rs = dao.search(shiftSql.toString(), paramList);
			while (rs.next()) {
				String guid = rs.getString("guidkey");
				HashMap<String, Double> map = statisticsMap.get(guid);
				if (map == null || map.size() < 1)
					map = new HashMap<String, Double>();

				String classId1 = rs.getString("class_id_1");
				String classId2 = rs.getString("class_id_2");
				String classId3 = rs.getString("class_id_3");
				HashMap<String, String> infoMap = classInfoMap.get(classId1);
				String statistics = "";
				String workHours = "0";
				Double statisticsCount = 0.0;
				Double workHourCount = 0.0;
				// 54866 当全部取消排班 班次时  不应直接跳出  应置为0
				if (infoMap == null || infoMap.size() < 1) {
					statistics = "00";
					if (map.containsKey(statistics))
						statisticsCount = map.get(statistics);
					map.put(statistics, statisticsCount);
					if (map.containsKey("workHours"))
						workHourCount = map.get("workHours");
					map.put("workHours", workHourCount);
					statisticsMap.put(guid, map);
					continue;
				}
				
				statistics = infoMap.get("statisticsType");
				workHours = infoMap.get("workHours");
				if (map.containsKey(statistics))
					statisticsCount = map.get(statistics);
				
				if (map.containsKey("workHours"))
					workHourCount = map.get("workHours");
				
				workHourCount = workHourCount + Double.valueOf(workHours);
				if ("04".equals(infoMap.get("unit"))) {
					statisticsCount++;
				} else
					statisticsCount = statisticsCount + Double.valueOf(statisticsCount);
				
				if(StringUtils.isNotEmpty(statistics))
					map.put(statistics, statisticsCount);

				infoMap = classInfoMap.get(classId2);
				if (infoMap != null && infoMap.size() > 0) {
					statistics = infoMap.get("statisticsType");
					workHours = infoMap.get("workHours");
					statisticsCount = 0.0;
					if (map.containsKey(statistics))
						statisticsCount = map.get(statistics);

					workHourCount = workHourCount + Double.valueOf(workHours);
					if ("04".equals(infoMap.get("unit"))) {
						statisticsCount++;
					} else
						statisticsCount = statisticsCount + Double.valueOf(statisticsCount);

					if(StringUtils.isNotEmpty(statistics))
						map.put(statistics, statisticsCount);
				}

				infoMap = classInfoMap.get(classId3);
				if (infoMap != null && infoMap.size() > 0) {
					statistics = infoMap.get("statisticsType");
					workHours = infoMap.get("workHours");
					statisticsCount = 0.0;
					if (map.containsKey(statistics))
						statisticsCount = map.get(statistics);

					workHourCount = workHourCount + Double.valueOf(workHours);
					if ("04".equals(infoMap.get("unit"))) {
						statisticsCount++;
					} else
						statisticsCount = statisticsCount + Double.valueOf(statisticsCount);
					
					if(StringUtils.isNotEmpty(statistics))
						map.put(statistics, statisticsCount);
				}

				map.put("workHours", workHourCount);
				statisticsMap.put(guid, map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return statisticsMap;
	}

	/**
	 * 获取班次的统计项及班次的工作时长
	 * 
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> getClassInfo() {
		HashMap<String, HashMap<String, String>> classInfoMap = new HashMap<String, HashMap<String, String>>();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select class_id,work_hours,statistics_type,item_unit");
			sql.append(" from kq_class left join (select codeitemid,codeitemdesc from codeitem where codesetid='85') item");
			sql.append(" on kq_class.statistics_type=item.codeitemid");
			sql.append(" left join kq_item on item.codeitemdesc=kq_item.item_name");
			sql.append(" where is_validate=1 order by statistics_type");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap<String, String> infoMap = new HashMap<String, String>();
				String workHours = rs.getString("work_hours");
				String statisticsType = rs.getString("statistics_type");
				String itemUnit = rs.getString("item_unit");
				infoMap.put("workHours", StringUtils.isEmpty(workHours) ? "0" : workHours);
				infoMap.put("statisticsType", StringUtils.isEmpty(statisticsType) ? "" : statisticsType);
				infoMap.put("unit", StringUtils.isEmpty(itemUnit) ? "01" : itemUnit);
				classInfoMap.put(rs.getString("class_id"), infoMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return classInfoMap;
	}

	/**
	 * 获取某年月日是当前月的第几周
	 * 
	 * @param date
	 *            时间
	 * @return
	 */
	private static int getWeekIndex(String date) {
		int weekIndex = 1;
		boolean flag = true;
		while (flag) {
			ArrayList<String> dateList = getWeekDateByWeekInMonth(Integer.valueOf(date.split("[.]")[0]),
					Integer.valueOf(date.split("[.]")[1]), weekIndex);
			Date sDate = DateUtils.getDate(dateList.get(0).split(":")[0], "yyyy.MM.dd");
			Date eDate = DateUtils.getDate(dateList.get(dateList.size() - 1).split(":")[0], "yyyy.MM.dd");
			Date nDate = DateUtils.getDate(date, "yyyy.MM.dd");
			if(DateUtils.dayDiff(sDate, nDate) >= 0 && DateUtils.dayDiff(nDate, eDate) >= 0)
				break;
			
			weekIndex++;
		}
		
		return weekIndex;
	}

	private String getWhereSql() {
		StringBuffer whereSql = new StringBuffer();
		try {
			String unitCode = this.userView.getUnitIdByBusi("11");
			if (StringUtils.isEmpty(unitCode) || unitCode.length() < 2)
				whereSql.append("1=2");
			else if (this.userView.isSuper_admin() || unitCode.contains("UN`") || "UN".equalsIgnoreCase(unitCode))
				whereSql.append("1=1");
			else {
				unitCode = PubFunc.getHighOrgDept(unitCode.replaceAll("`", ","));
				String[] unitCodes = unitCode.split(",");
				for (String orgId : unitCodes) {
					if (StringUtils.isNotEmpty(whereSql.toString()))
						whereSql.append(" or ");

					if(orgId.startsWith("UN") || orgId.startsWith("UM") || orgId.startsWith("@k"))
						orgId = orgId.substring(2);
					
					whereSql.append(" b0110 like '" + orgId + "%'");
					whereSql.append(" or e0122 like '" + orgId + "%'");
					whereSql.append(" or e01a1 like '" + orgId + "%'");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return whereSql.toString();
	}

	/**
	 * 发布班次方案
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 */
	@Override
    public void pushShiftScheme(HashMap<String, String> paramMap) {
		try {
			String groupId = paramMap.get("groupId");
			String schemeId = paramMap.get("schemeId");
			String state = paramMap.get("state");
			groupId = StringUtils.isNotEmpty(groupId) ? PubFunc.decrypt(groupId) : "";
			schemeId = StringUtils.isNotEmpty(schemeId) ? PubFunc.decrypt(schemeId) : "";
			if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(schemeId))
				return;

			String sql = "update kq_shift_scheme set state=? where group_id=? and scheme_id=?";
			ArrayList<String> paramList = new ArrayList<String>();
			if("push".equalsIgnoreCase(state))
				paramList.add("04");
			else
				paramList.add("01");
				
			paramList.add(groupId);
			paramList.add(schemeId);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql, paramList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 粘帖排班信息保存
	 * 
	 * @param records
	 *            粘帖的排班数据
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 */
	@Override
    public void copyShifts(JSONArray records, String groupId, String schemeId) {
		RowSet rs = null;
		try {
			boolean shiftFlag = false;
			schemeId = PubFunc.decrypt(schemeId);
			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey," + Sql_switcher.dateToChar("q03z0", "yyyy.MM.dd"));
			sql.append(" q03z0 from kq_employ_shift_v2");
			sql.append(" where " + Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd"));
			sql.append(" in ('#'");

			String dateTime = "";
			ArrayList<String> paramList = new ArrayList<String>();
			ArrayList<String> dateList = new ArrayList<String>();
			Iterator<String> it = ((JSONObject) records.get(0)).keys();
			while (it.hasNext()) {
				String date = it.next();
				if ("guidkey".equalsIgnoreCase(date))
					continue;

				if (date.indexOf(".") > -1)
					shiftFlag = true;

				dateTime = date;
				dateList.add(date);
				paramList.add(date.replace(".", "-"));
				sql.append(",?");
			}

			HashMap<String, String> existShiftsMap = new HashMap<String, String>();
			ContentDAO dao = new ContentDAO(this.conn);
			if (shiftFlag) {
				sql.append(") and guidkey in ('#'");
				for (int i = 0; i < records.size(); i++) {
					JSONObject record = (JSONObject) records.get(i);
					paramList.add(PubFunc.decrypt(record.getString("guidkey")));
					sql.append(",?");
				}

				sql.append(")");

				rs = dao.search(sql.toString(), paramList);
				while (rs.next()) {
					String guidkey = rs.getString("guidkey");
					String q03z0 = rs.getString("q03z0");
					String dates = ",";
					if (existShiftsMap.containsKey(guidkey))
						dates = existShiftsMap.get(guidkey);

					dates += q03z0.replace("-", ".") + ",";
					existShiftsMap.put(guidkey, dates);
				}
			}

			ArrayList<ArrayList<Object>> updateParamList = new ArrayList<ArrayList<Object>>();
			ArrayList<ArrayList<Object>> insertParamList = new ArrayList<ArrayList<Object>>();
			for (int i = 0; i < records.size(); i++) {
				JSONObject record = (JSONObject) records.get(i);
				String guidkey = record.getString("guidkey");
				guidkey = PubFunc.decrypt(guidkey);
				String dates = existShiftsMap.get(guidkey);
				if (shiftFlag) {
					for (String date : dateList) {
						ArrayList<Object> valueList = new ArrayList<Object>();
						String shiftInfos = (String) record.get(date);
						JSONArray shiftInfoArr = new JSONArray();
						if(StringUtils.isNotEmpty(shiftInfos))
							shiftInfoArr = JSONArray.fromObject(shiftInfos);
						
						int m = 0;
						for (m = 0; m < shiftInfoArr.size(); m++) {
							JSONObject shiftInfoJson = (JSONObject) shiftInfoArr.get(m);
							String classId = shiftInfoJson.getString("classId");
							classId = StringUtils.isNotEmpty(classId) ? PubFunc.decrypt(classId) : "";
							if (StringUtils.isEmpty(classId))
								continue;

							String comment = shiftInfoJson.getString("comment");
							String commentColor = shiftInfoJson.getString("commentColor");
							if (StringUtils.isEmpty(comment)) {
								comment = null;
								commentColor = null;
							}

							valueList.add(classId);
							valueList.add(comment);
							valueList.add(commentColor);
						}
						
						while (m < 3) {
							valueList.add(null);
							valueList.add(null);
							valueList.add(null);
							m++;
						}

						valueList.add(guidkey);
						if (StringUtils.isNotEmpty(dates) && dates.contains("," + date + ",")) {
							valueList.add(date.replace(".", "-"));
							updateParamList.add(valueList);
						} else {
							valueList.add(new Timestamp(DateUtils.getDate(date, "yyyy.MM.dd").getTime()));
							insertParamList.add(valueList);
						}
					}
				} else {
					ArrayList<Object> valueList = new ArrayList<Object>();
					for (String date : dateList) {
						String value = (String) record.get(date);
						valueList.add(value);
					}

					valueList.add(schemeId);
					valueList.add(guidkey);
					updateParamList.add(valueList);
				}
			}

			StringBuffer updateSql = new StringBuffer();
			if (shiftFlag) {
				if (updateParamList != null && updateParamList.size() > 0) {
					updateSql.append("update kq_employ_shift_v2 set class_id_1=?,Comment_1=?,Comment_color_1=?,");
					updateSql.append("class_id_2=?,comment_2=?,comment_color_2=?,");
					updateSql.append("class_id_3=?,comment_3=?,comment_color_3=?");
					updateSql.append(" where guidkey=? and ");
					updateSql.append(Sql_switcher.dateToChar("q03z0", "yyyy-MM-dd") + "=?");
					dao.batchUpdate(updateSql.toString(), updateParamList);
				}

				if (insertParamList != null && insertParamList.size() > 0) {
					StringBuffer isnertSql = new StringBuffer();
					isnertSql.append("insert into kq_employ_shift_v2 (");
					isnertSql.append("class_id_1,Comment_1,Comment_color_1,");
					isnertSql.append("class_id_2,comment_2,comment_color_2,");
					isnertSql.append("class_id_3,comment_3,comment_color_3,guidkey,q03z0");
					isnertSql.append(") values (?,?,?,?,?,?,?,?,?,?,?)");
					dao.batchInsert(isnertSql.toString(), insertParamList);
				}
				// 自动更新工时、统计项的数据
				ArrayList<String> schemeIdList = new ArrayList<String>();
				if ("-1".equals(schemeId)) {
					String date = dateTime.substring(0, dateTime.lastIndexOf("."));
					StringBuffer searchStr = new StringBuffer();
					paramList.clear();
					searchStr.append("select scheme_id,scope from kq_shift_scheme where Group_id=? and scope like ?");
					paramList.add(groupId);
					paramList.add("%" + date + "%");
					rs = dao.search(searchStr.toString(), paramList);
					ArrayList<String> schemeIdTemp = new ArrayList<String>();
					while(rs.next()) {
						String scope = rs.getString("scope");
						String start = scope.split("-")[0];
						String end = scope.split("-")[1];
						if(start.startsWith(date) && end.startsWith(date))
							schemeIdTemp.add(rs.getString("scheme_id"));
						else
							schemeIdList.add(rs.getString("scheme_id"));
					}
					
					ArrayList<HashMap<String, String>> statisticsLsit = getShiftStatistics();
					updateSql.setLength(0);
					paramList.clear();
					updateSql.append("update kq_shift_scheme_emp set Work_hour=?");
					paramList.add("0");
					for (HashMap<String, String> map : statisticsLsit) {
						updateSql.append(",stat_" + map.get("statisticsType") + "=?");
						paramList.add("0");
					}
					
					updateSql.append(" where scheme_id=?");
					ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
					for(String tempId : schemeIdTemp) {
						ArrayList<String> tempList = new ArrayList<String>();
						tempList.addAll(paramList);
						tempList.add(tempId);
						list.add(tempList);
					}
					
					dao.batchUpdate(updateSql.toString(), list);
				} else
					schemeIdList.add(schemeId);
				
				countClasses(schemeIdList, null);
			} else {
				updateSql.append("update kq_shift_scheme_emp set");
				for (String date : dateList)
					updateSql.append(" " + date + "=?,");

				updateSql.setLength(updateSql.length() - 1);
				updateSql.append(" where scheme_id=? and guidkey=?");
				dao.batchUpdate(updateSql.toString(), updateParamList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 获取班组人员维护选择日期所需数据
	 * getGroupChangeEmpData
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2019年1月4日 下午3:38:15
	 * @author linbz
	 */
	@Override
    public HashMap getGroupChangeEmpData(JSONObject jsonObj) throws GeneralException{
    	HashMap dataMap = new HashMap();
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
			
			ArrayList<HashMap<String, String>> weekList = this.weekList(Integer.valueOf(year), Integer.valueOf(month));
			// 下拉数据取消 全部 选项
			weekList.remove(weekList.size()-1);
			String dateJson = this.getShiftDate("");
			
			dataMap.put("weekList", weekList);
			dataMap.put("dateJson", dateJson);
			dataMap.put("year", year);
			dataMap.put("month", month);
			dataMap.put("weekIndex", weekIndex);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	    return dataMap;
    }
	
	@Override
    public String getPushScheme() {
		return pushscheme;
	}
	
	@Override
    public String getGroupName() {
		return groupName;
	}
	
	@Override
    public HashMap<String, HashMap<String, String>> getCountDataMap() {
		return countDataMap;
	}
	
	/**
	 * 获取页面显示的所有的列，格式：[{itemid:"b0110",itemDesc:"单位"},{itemid:"e0122",itemDesc:"部门"}...]
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            周
	 * @param dataType
	 *            获取数据的页面 =shiftData：排班页面;=shiftCheck排班审查
	 * @return
	 */
	@Override
    public ArrayList<HashMap<String, String>> getColumnList(int year, int month, int weekIndex,
                                                            String dataType) {
		ArrayList<HashMap<String, String>> columnList = new ArrayList<HashMap<String, String>>();
		try {
			this.year = year;
			this.month = month;
			this.weekIndex = weekIndex;
			this.dataType = dataType;
			getPanelColumnsSetting();
			ArrayList<String> dayList = getWeekDateByWeekInMonth(year, month, weekIndex);
			//【54092】考勤管理包：导出排班表中，标题序号没显示出来
			HashMap<String, String> fristColumnMap = new HashMap<String, String>();
			fristColumnMap.put("itemId", "order");
			fristColumnMap.put("itemDesc", "序号");
			columnList.add(fristColumnMap);
			for(ColumnsInfo info : this.columnInfoList) {
				if(ColumnsInfo.LOADTYPE_HIDDEN == info.getLoadtype())
					continue;
				
				HashMap<String, String> columnMap = new HashMap<String, String>();
				String columnId = info.getColumnId();
				String columnDesc = info.getColumnDesc();
				if(columnId.indexOf(".") > -1) {
					for (String dayColumn : dayList) {
						String[] dayColumns = dayColumn.split(":");
						if(!columnId.equals(dayColumns[0]))
							continue;
						
						String dateDesc = dayColumns[1].split("<br>")[1];
						String dayDesc = dayColumns[1].split("<br>")[0];
						columnDesc = dateDesc + ":" + dayDesc;
						break;
					}
				}
				
				columnMap.put("itemId", columnId);
				columnMap.put("itemDesc", columnDesc);
				columnMap.put("width", info.getColumnWidth() + "");
				columnMap.put("decimalWidth", info.getDecimalWidth() + "");
				columnMap.put("fieldSetId", info.getFieldsetid() + "");
				columnMap.put("align", info.getTextAlign());
				columnList.add(columnMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnList;
	}

	/**
	 * 获取自动排班时间内所有的周对应的日期范围
	 * 
	 * @param fDate
	 *            开始时间
	 * @param tDate
	 *            结束时间
	 * @return
	 */
	private ArrayList<String> getWeekScopes(String fDate, String tDate) {
		ArrayList<String> scopesList = new ArrayList<String>();
		int fYear = Integer.valueOf(fDate.split("[.]")[0]);
		int fMonth = Integer.valueOf(fDate.split("[.]")[1]);
		int tYear = Integer.valueOf(tDate.split("[.]")[0]);
		int tMonth = Integer.valueOf(tDate.split("[.]")[1]);
		boolean flag = true;
		Calendar ca = Calendar.getInstance(Locale.CHINA);
		ca.set(Calendar.YEAR, fYear);
		ca.set(Calendar.MONTH, fMonth - 1);
		while (flag) {
			if (fYear > tYear || (fYear == tYear && fMonth > tMonth)) {
				flag = false;
			} else {
				int weekCount = weekCount(fYear, fMonth);
				for (int i = 1; i <= weekCount; i++) {
					ArrayList<String> dateList = getWeekDateByWeekInMonth(fYear, fMonth, i);
					String monday = dateList.get(0).split(":")[0];
					String sunday = dateList.get(dateList.size() - 1).split(":")[0];
					if (fDate.compareTo(sunday) > 0 || tDate.compareTo(monday) < 0)
						continue;

					String scope = dateList.get(0).split(":")[0] + "-" + dateList.get(dateList.size() - 1).split(":")[0];
					if (!scopesList.contains(scope))
						scopesList.add(scope);

				}

				ca.add(Calendar.MONTH, 1);
				fYear = ca.get(Calendar.YEAR);
				fMonth = ca.get(Calendar.MONTH) + 1;
			}
		}

		return scopesList;
	}

	/**
	 * 判断所选的某年某月第某周是不是比当前时间所在的年月周要早
	 * 
	 * @param year
	 *            选择的年分
	 * @param month
	 *            选择的月份
	 * @param weekIndex
	 *            选择的第几周
	 * @return
	 */
	private int diffDate(int year, int month, int weekIndex) {
		int flag = 0;
		String nowDate = DateUtils.format(new Date(), "yyyy.MM.dd");
		int nowYear = Integer.valueOf(nowDate.split("[.]")[0]);
		int nowMont = Integer.valueOf(nowDate.split("[.]")[1]);
		if (year < nowYear || (year == nowYear && month < nowMont)
		        || (year == nowYear && month == nowMont && weekIndex < getWeekIndex(nowDate)))
			flag = 0;
		else
			flag = 1;

		return flag;
	}

	/**
	 * 按设置的过滤条件生成sql
	 * 
	 * @param filterParam
	 *            过滤条件
	 * @return
	 */
	private String getFilterWhere(String filterParam) {
		// "kqEmpShift";
		StringBuffer filterWhere = new StringBuffer(" and (");
		try {
			if (StringUtils.isEmpty(filterParam))
				return "";

			JSONObject json = JSONObject.fromObject(SafeCode.keyWord_reback(filterParam));
			String itemid = json.getString("field");
			String itemtype = json.getString("itemtype");
			JSONArray factor = json.getJSONArray("factor");
			String expr = json.getString("expr");
			// 如果为空或者没有数据，返回 原sql
			if (factor == null || factor.isEmpty())
				return "";

			String symbol;
			String value = "";

			if ("C".equals(itemtype)) {// C代码型指标
				expr = "or";
				for (int i = 0; i < factor.size(); i++) {
					String f = factor.getString(i);
					value = f.substring(f.indexOf("`") + 1);
					filterWhere.append(itemid + " like '" + value + "%' or ");
				}
			} else if ("D".equals(itemtype)) {// 时间类型
				String plan = json.getString("plan");
				for (int i = 0; i < factor.size(); i++) {
					String f = factor.getString(i);
					symbol = f.substring(0, f.indexOf("`"));
					value = f.substring(f.indexOf("`") + 1);
					if ("custom".equals(plan)) {
						filterWhere.append(itemid + symbol + Sql_switcher.dateValue(value) + " " + expr + " ");
						continue;
					}

					Calendar c = Calendar.getInstance();
					if ("nextMonth".equals(symbol))
						filterWhere.append(Sql_switcher.month(itemid) + "=" + (c.get(Calendar.MONTH) + 2) + " " + expr + " ");
					else if ("thisMonth".equals(symbol))
						filterWhere.append(Sql_switcher.month(itemid) + "=" + (c.get(Calendar.MONTH) + 1) + " " + expr + " ");
					else if ("lastMonth".equals(symbol))
						filterWhere.append(Sql_switcher.month(itemid) + "=" + c.get(Calendar.MONTH) + " " + expr + " ");
					else if ("nextYear".equals(symbol))
						filterWhere.append(Sql_switcher.year(itemid) + "=" + (c.get(Calendar.YEAR) + 1) + " " + expr + " ");
					else if ("thisYear".equals(symbol))
						filterWhere.append(Sql_switcher.year(itemid) + "=" + c.get(Calendar.YEAR) + " " + expr + " ");
					else if ("lastYear".equals(symbol))
						filterWhere.append(Sql_switcher.year(itemid) + "=" + (c.get(Calendar.YEAR) - 1) + " " + expr + " ");
					else {
						int nextYear = -1;
						int lastYear = -1;
						String nextSeason = "";
						String thisSeason = "";
						String lastSeason = "";
						if (c.get(Calendar.MONTH) < 3) {
							thisSeason = "1,2,3";
							lastYear = c.get(Calendar.YEAR) - 1;
						} else if (c.get(Calendar.MONTH) < 6) {
							nextSeason = "7,8,9";
							thisSeason = "4,5,6";
							lastSeason = "1,2,3";
						} else if (c.get(Calendar.MONTH) < 9) {
							nextSeason = "10,11,12";
							thisSeason = "7,8,9";
							lastSeason = "4,5,6";
						} else {
							thisSeason = "10,11,12";
							nextYear = c.get(Calendar.YEAR) + 1;
						}

						if ("nextSeason".equals(symbol)) {
							if (nextYear > 0)
								filterWhere.append(Sql_switcher.month(itemid) + " in (1,2,3) and "
								        + Sql_switcher.year(itemid) + "=" + nextYear + " " + expr + " ");
							else
								filterWhere.append(Sql_switcher.month(itemid) + " in (" + nextSeason + ") " + expr + " ");

						} else if ("thisSeason".equals(symbol))
							filterWhere.append(Sql_switcher.month(itemid) + " in (" + thisSeason + ") " + expr + " ");
						else if ("lastSeason".equals(symbol)) {
							if (lastYear > 0)
								filterWhere.append(Sql_switcher.month(itemid) + " in (10,11,12) and "
								        + Sql_switcher.year(itemid) + "=" + lastYear + " " + expr + " ");
							else
								filterWhere.append(Sql_switcher.month(itemid) + " in (" + lastSeason + ") " + expr + " ");
						}
					}
					break;
				}
			} else if ("N".equals(itemtype)) {// int型
				for (int i = 0; i < factor.size(); i++) {
					String f = factor.getString(i);
					symbol = f.substring(0, f.indexOf("`"));
					value = f.substring(f.indexOf("`") + 1);
					filterWhere.append(itemid + symbol + value + " " + expr + " ");
				}
			} else {// M(文本)型和A(字符)型
				for (int i = 0; i < factor.size(); i++) {
					String f = factor.getString(i);
					symbol = f.substring(0, f.indexOf("`"));
					try {
						value = URLDecoder.decode(URLDecoder.decode(f.substring(f.indexOf("`") + 1), "UTF-8"), "UTF-8");
						value = PubFunc.hireKeyWord_filter(value);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					if ("sta".equals(symbol)) // 开头是
						filterWhere.append(itemid + " like '" + value + "%' " + expr + " ");
					else if ("stano".equals(symbol)) // 开头不是
						filterWhere.append(itemid + " not like '" + value + "%' " + expr + " ");
					else if ("end".equals(symbol)) // 结尾是
						filterWhere.append(itemid + " like '%" + value + "' " + expr + " ");
					else if ("endno".equals(symbol)) // 结尾不是
						filterWhere.append(itemid + " not like '%" + value + "' " + expr + " ");
					else if ("cont".equals(symbol)) // 包含
						filterWhere.append(itemid + " like '%" + value + "%' " + expr + " ");
					else if ("contno".equals(symbol)) // 不包含
						filterWhere.append(itemid + " not like '%" + value + "%' " + expr + " ");
					else {
						if ("=".equals(symbol) && value.indexOf("？") + value.indexOf("＊") > -2) {
							symbol = " like ";
							value = value.replaceAll("？", "?");
							value = value.replaceAll("＊", "%");
						}

						if (value.length() == 0 && "=".equals(symbol)) {
							filterWhere.append(" (" + Sql_switcher.sqlToChar(itemid) + symbol + " '' or "
							        + Sql_switcher.sqlToChar(itemid) + " is null ) " + expr + " ");
						} else
							filterWhere.append(Sql_switcher.sqlToChar(itemid) + symbol + " '" + value + "' " + expr + " ");
					}
				}
			}

			if ("or".equals(expr))
				filterWhere.append(" 1=2 ");
			else
				filterWhere.append(" 1=1 ");

			filterWhere.append(" )");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return filterWhere.toString();
	}
	/**
	 * 获取栏目设置需要显示的列
	 */
	private void getPanelColumnsSetting() {
		RowSet rs = null;
        try {
        	if(this.columnMap != null && this.columnMap.size() > 0
        			&& this.columnInfoList != null && this.columnInfoList.size() > 0)
        		return;
        	//固定显示的指标的名称
        	HashMap<String, String> columnDescMap = new HashMap<String, String>();
        	columnDescMap.put("shift_group", ResourceFactory.getProperty("kq.kq_rest.group.name"));
        	columnDescMap.put("group_name", ResourceFactory.getProperty("kq.shift.group_name"));
        	columnDescMap.put("shift_comment", ResourceFactory.getProperty("kq.shift.shiftComment"));
        	columnDescMap.put("extra_days", ResourceFactory.getProperty("kq.shift.extraDays"));
        	columnDescMap.put("work_hour", ResourceFactory.getProperty("kq.shift.workHour"));

            boolean flag = false;
            String submoduleId = "shiftManage";
			if ("shiftCheck".equalsIgnoreCase(dataType))
				submoduleId = "shiftCheck";
			
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            //判断是否存在个人保存的栏目设置
            sql.append("select 1 from t_sys_table_scheme");
            sql.append(" where submoduleid=? and is_share=0 and username=?");
            ArrayList<String> paramList = new ArrayList<String>();
            paramList.add(submoduleId);
            paramList.add(this.userView.getUserName());
            rs = dao.search(sql.toString(), paramList);
            if (rs.next())
                flag = true;
            
            sql.setLength(0);
            sql.append("select * from t_sys_table_scheme_item");
            sql.append(" where scheme_id = (select scheme_id");
            sql.append(" from t_sys_table_scheme where submoduleid=?");
            if(flag)
            	sql.append(" and is_share='0' and username=?");
            else
            	sql.append(" and is_share='1'");
            
            sql.append(") order by displayorder");
            
            if(!flag)
            	paramList.remove(1);
            
            int dayLastIndex = 0;
            int columnIndex = 0;
            ArrayList<String> dayList = getWeekDateByWeekInMonth(this.year, this.month, this.weekIndex);
            ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
            int workHourIndex = 0;
            rs = dao.search(sql.toString(), paramList);
            while (rs.next()) {
                String columnId = rs.getString("itemid");
                if("work_hour".equalsIgnoreCase(columnId))
                	columnIndex = workHourIndex;
                
                String desc = rs.getString("itemdesc");
                String fieldSetId = rs.getString("fieldsetid");
                String locked = rs.getString("is_lock");
                String align = rs.getString("align");
                String textAlign = "left";
                if("1".equals(align))
                	textAlign = "left";
                else if("2".equals(align))
                	textAlign = "center";
                else if("3".equals(align))
                	textAlign = "right";
                
                String display = rs.getString("is_display");
                int displayStyle = ColumnsInfo.LOADTYPE_BLOCK;
                if("0".equals(display))
                	displayStyle = ColumnsInfo.LOADTYPE_HIDDEN;
                
                ColumnsInfo info = new ColumnsInfo();
                //人员信息集的指标
                if(StringUtils.isNotEmpty(fieldSetId)) {
                	FieldItem item = DataDictionary.getFieldItem(columnId, fieldSetId);
                	if(item == null|| "0".equals(item.getUseflag()))
                		continue;
                	
                	if(StringUtils.isEmpty(desc))
                		desc = item.getItemdesc();
                	
                	info.setColumnType(item.getItemtype());
                	info.setCodesetId(item.getCodesetid());
                	if("1".equals(rs.getString("is_removable")))
                		info.setRemovable(true);
                }
                //排班日期指标：切换不同的周时将把表中保存的替换为需要显示的日期的名称与指标id
                if(columnId.indexOf(".") > -1) {
                	if(dayList != null && dayList.size() > 0) {
                		dayLastIndex = rs.getRow();
                		String[] dayColumns = dayList.get(0).split(":");
                		String dateDesc = dayColumns[1].split("<br>")[1];
                		info = new ColumnsInfo();
                		info.setColumnType("A");
                		info.setColumnId(dayColumns[0]);
                		info.setColumnDesc(dateDesc);
                		info.setLoadtype(displayStyle);
                		info.setColumnWidth(rs.getInt("displaywidth"));
                		info.setFieldsetid("");
                		info.setSortable(false);
                		if("1".equals(locked))
                        	info.setLocked(true);
                		
                		info.setTextAlign(textAlign);
                		this.columnMap.put(dayColumns[0], info);
                		this.columnInfoList.add(info);
                		workHourIndex++;
                		dayList.remove(0);
                	}
                	
                	continue;
                }
                //显示统计信息：如果表中保存的统计指标不在权限范围内则去掉，同时当表中保存的日期指标的个数比需要显示的指标个数多时，超出的部分不加载
                if(columnId.startsWith("stat_")) {
                	for (HashMap<String, String> map : shiftStatisticList) {
                		String statisticsType = map.get("statisticsType");
                		if(!columnId.equalsIgnoreCase("stat_" + statisticsType))
                			continue;
                			
                		String statisticSesc = AdminCode.getCodeName("85", statisticsType);
                		String unit = map.get("unit");
                		if ("01".equals(unit))
                			statisticSesc += ResourceFactory.getProperty("kq.shift.hour");
                		else if ("02".equals(unit))
                			statisticSesc += ResourceFactory.getProperty("kq.shift.day");
                		else if ("03".equals(unit))
                			statisticSesc += ResourceFactory.getProperty("kq.shift.Minute");
                		else if ("04".equals(unit))
                			statisticSesc += ResourceFactory.getProperty("kq.shift.times");
                		
                		info = new ColumnsInfo();
                		info.setColumnType("N");
                		info.setColumnId("stat_" + statisticsType);
                		info.setColumnDesc(statisticSesc);
                		info.setLoadtype(displayStyle);
                		info.setColumnWidth(100);
                		info.setFieldsetid("");
                		info.setSortable(false);
                		info.setTextAlign(textAlign);
                		if("1".equals(locked))
                        	info.setLocked(true);
                		
                		this.columnMap.put("stat_" + statisticsType, info);
                		this.columnInfoList.add(info);
                		shiftStatisticList.remove(map);
                		workHourIndex++;
                		break;
                	}
                	
                	continue;
                }
                
                if("work_hour,extra_days".contains(columnId))
                	info.setColumnType("N");
                
                info.setColumnId(columnId);
                info.setColumnDesc(StringUtils.isNotEmpty(desc) ? desc : columnDescMap.get(columnId));
                info.setLoadtype(displayStyle);
                info.setColumnWidth(rs.getInt("displaywidth"));
                info.setFieldsetid(rs.getString("fieldsetid"));
                info.setTextAlign(textAlign);
                info.setSortable(false);
                if("1".equals(locked))
                	info.setLocked(true);
                
                this.columnMap.put(columnId, info);
                this.columnInfoList.add(info);
                workHourIndex++;
            }
            //当需要显示的日期指标比栏目设置表保存的多时，添加没有添加完的日志指标
            if(dayLastIndex != 0 && dayList != null && dayList.size() > 0) {
            	for (String dayColumn : dayList) {
    				String[] dayColumns = dayColumn.split(":");
    				String dateDesc = dayColumns[1].split("<br>")[1];
    				ColumnsInfo info = new ColumnsInfo();
    				info = new ColumnsInfo();
    				info.setColumnType("A");
    				info.setColumnId(dayColumns[0]);
    				info.setColumnDesc(dateDesc);
    				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
    				// 43285列宽在审查页面时减半
    				if ("shiftCheck".equalsIgnoreCase(this.dataType))
    					info.setColumnWidth(105);
    				else
    					info.setColumnWidth(120);

    				info.setFieldsetid("");
    				info.setSortable(false);
    				info.setTextAlign("left");
    				this.columnMap.put(dayColumns[0], info);
    				this.columnInfoList.add(dayLastIndex, info);
    				dayLastIndex++;
    				columnIndex++;
    			}
            }
            //当需要显示的统计属性指标比栏目设置表保存的多时，添加没有添加完的统计属性指标
            if(this.columnInfoList != null && this.columnInfoList.size() > 0
            		&& shiftStatisticList != null && shiftStatisticList.size() > 0) {
            	for (HashMap<String, String> map : shiftStatisticList) {
            		String statisticsType = map.get("statisticsType");
            		String statisticSesc = AdminCode.getCodeName("85", statisticsType);
            		String unit = map.get("unit");
            		if ("01".equals(unit))
            			statisticSesc += ResourceFactory.getProperty("kq.shift.hour");
            		else if ("02".equals(unit))
            			statisticSesc += ResourceFactory.getProperty("kq.shift.day");
            		else if ("03".equals(unit))
            			statisticSesc += ResourceFactory.getProperty("kq.shift.Minute");
            		else if ("04".equals(unit))
            			statisticSesc += ResourceFactory.getProperty("kq.shift.times");
            		
            		ColumnsInfo info = new ColumnsInfo();
            		info.setColumnType("N");
            		info.setColumnId("stat_" + statisticsType);
            		info.setColumnDesc(statisticSesc);
            		info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            		info.setColumnWidth(100);
            		info.setFieldsetid("");
            		info.setSortable(false);
            		info.setTextAlign("right");
            		this.columnMap.put("stat_" + statisticsType, info);
            		this.columnInfoList.add(info);
            	}
            }
            
            if(this.columnInfoList == null || this.columnInfoList.size() < 1)
            	getInitialColumns();
            else {
            	//显示全月的数据时，如果加载的指标中有“排班备注”和“存假或存班”则把这两个指标去除
            	//不显示全月数据时，如果加载的指标中没有“排班备注”和“存假或存班”则把这两个指标加上
            	if (-1 != weekIndex) {
            		if(!columnMap.containsKey("shift_comment")) {
            			ColumnsInfo info = new ColumnsInfo();
            			info.setColumnType("M");
            			info.setColumnId("shift_comment");
            			info.setColumnDesc(ResourceFactory.getProperty("kq.shift.shiftComment"));
            			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            			info.setColumnWidth(100);
            			info.setFieldsetid("");
            			info.setCodesetId("0");
            			info.setSortable(false);
            			this.columnMap.put("shift_comment", info);
            			this.columnInfoList.add(columnIndex, info);
            			
            			info = new ColumnsInfo();
            			info.setColumnType("N");
            			info.setColumnId("extra_days");
            			info.setColumnDesc(ResourceFactory.getProperty("kq.shift.extraDays"));
            			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            			info.setColumnWidth(100);
            			info.setFieldsetid("");
            			info.setCodesetId("0");
            			info.setSortable(false);
            			info.setTextAlign("right");
            			this.columnMap.put("extra_days", info);
            			this.columnInfoList.add(columnIndex + 1, info);
            		}
    			} else {
    				if(columnMap.containsKey("shift_comment")) {
            			ColumnsInfo info = new ColumnsInfo();
            			this.columnMap.remove("shift_comment");
            			this.columnMap.remove("extra_days");
            			columnIndex--;
            			this.columnInfoList.remove(columnIndex);
            			columnIndex--;
            			this.columnInfoList.remove(columnIndex);
            		}
    			}
            }
           
            getPageSize(this.dataType);
            TableDataConfigCache config = new TableDataConfigCache();
            config.setTableColumns(this.columnInfoList);
            config.setColumnMap(this.columnMap);
            config.setPageSize(this.pageSize);
            if ("shiftCheck".equalsIgnoreCase(dataType))
            	this.userView.getHm().put("shiftCheck_001", config);
            else
            	this.userView.getHm().put("shiftManage_0001", config);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			PubFunc.closeResource(rs);
		}
    }
	/**
	 * 加载页面默认显示的指标
	 */
	private void getInitialColumns() {
		try {
			String[] a01Columns = new String[4];
			a01Columns[0] = "b0110";
			a01Columns[1] = "e0122";
			a01Columns[2] = "a0101";
			HashMap paramMap = KqPrivForHospitalUtil.getKqParameter(this.conn);
			String gNo = (String) paramMap.get("g_no");
			if (StringUtils.isNotEmpty(gNo))
				a01Columns[3] = gNo;
			
			ColumnsInfo info = new ColumnsInfo();
			for(String itemid : a01Columns) {
				FieldItem fi = DataDictionary.getFieldItem(itemid, "A01");
				if(fi == null || "0".equals(fi.getUseflag()))
					continue;

				info = new ColumnsInfo();
				info.setColumnType(fi.getItemtype());
				info.setColumnId(itemid);
				info.setColumnDesc(fi.getItemdesc());
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("A01");
				info.setCodesetId(fi.getCodesetid());
				info.setSortable(false);
				info.setTextAlign("left");
				info.setLocked(true);
				if("N".equalsIgnoreCase(fi.getItemtype()))
					info.setTextAlign("right");
				
				this.columnMap.put(itemid, info);
				this.columnInfoList.add(info);
			}
			// 班组名称
			if ("shiftCheck".equalsIgnoreCase(this.dataType)) {
				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("shift_group");
				info.setColumnDesc(ResourceFactory.getProperty("kq.kq_rest.group.name"));
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				info.setTextAlign("left");
				this.columnMap.put("shift_group", info);
				this.columnInfoList.add(info);
			}

			if ("shiftData".equalsIgnoreCase(this.dataType)) {
				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("group_name");
				info.setColumnDesc(ResourceFactory.getProperty("kq.shift.group_name"));
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				info.setTextAlign("left");
				this.columnMap.put("group_name", info);
				this.columnInfoList.add(info);
			}
			
			ArrayList<String> dayList = getWeekDateByWeekInMonth(this.year, this.month, this.weekIndex);
			for (String dayColumn : dayList) {
				String[] dayColumns = dayColumn.split(":");
				String dateDesc = dayColumns[1].split("<br>")[1];
				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId(dayColumns[0]);
				info.setColumnDesc(dateDesc);
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				// 43285列宽在审查页面时减半
				if ("shiftCheck".equalsIgnoreCase(this.dataType))
					info.setColumnWidth(105);
				else
					info.setColumnWidth(120);

				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				info.setTextAlign("left");
				this.columnMap.put(dayColumns[0], info);
				this.columnInfoList.add(info);
			}

			if (-1 != weekIndex) {
				info = new ColumnsInfo();
				info.setColumnType("M");
				info.setColumnId("shift_comment");
				info.setColumnDesc(ResourceFactory.getProperty("kq.shift.shiftComment"));
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				this.columnMap.put("shift_comment", info);
				this.columnInfoList.add(info);
				
				info = new ColumnsInfo();
				info.setColumnType("N");
				info.setColumnId("extra_days");
				info.setColumnDesc(ResourceFactory.getProperty("kq.shift.extraDays"));
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				info.setTextAlign("right");
				this.columnMap.put("extra_days", info);
				this.columnInfoList.add(info);
			}

			info = new ColumnsInfo();
			info.setColumnType("N");
			info.setColumnId("work_hour");
			info.setColumnDesc(ResourceFactory.getProperty("kq.shift.workHour"));
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setCodesetId("0");
			info.setSortable(false);
			info.setTextAlign("right");
			this.columnMap.put("work_hour", info);
			this.columnInfoList.add(info);
			
			ArrayList<HashMap<String, String>> shiftStatisticList = getShiftStatistics();
			for (HashMap<String, String> map : shiftStatisticList) {
				String statisticsType = map.get("statisticsType");
				String statisticSesc = AdminCode.getCodeName("85", statisticsType);
				String unit = map.get("unit");
				if ("01".equals(unit))
					statisticSesc += ResourceFactory.getProperty("kq.shift.hour");
				else if ("02".equals(unit))
					statisticSesc += ResourceFactory.getProperty("kq.shift.day");
				else if ("03".equals(unit))
					statisticSesc += ResourceFactory.getProperty("kq.shift.Minute");
				else if ("04".equals(unit))
					statisticSesc += ResourceFactory.getProperty("kq.shift.times");
				
				info = new ColumnsInfo();
				info.setColumnType("N");
				info.setColumnId("stat_" + statisticsType);
				info.setColumnDesc(statisticSesc);
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setCodesetId("0");
				info.setSortable(false);
				info.setTextAlign("right");
				this.columnMap.put("stat_" + statisticsType, info);
				this.columnInfoList.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更改栏目设置保存后的submoduleid
	 * 
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 */
	@Override
    public void changeSubmoudleId(String dataType) {
		RowSet rs = null;
		try {
			String submoduleId = "shiftManage_0001";
			String changeSubmoduleId = "shiftManage";
			if ("shiftCheck".equalsIgnoreCase(dataType)) {
				submoduleId = "shiftCheck_001";
				changeSubmoduleId = "shiftCheck";
			}
			
			String sql = "select scheme_id from t_sys_table_scheme where submoduleid=? and username=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(changeSubmoduleId);
			paramList.add(this.userView.getUserName());
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql, paramList);
			paramList.clear();
			if(rs.next())
				paramList.add(rs.getString("scheme_id"));
			
			if(paramList != null && paramList.size() > 0) {
				sql = "delete from t_sys_table_scheme_item where scheme_id=?";
				dao.delete(sql, paramList);
				sql = "delete from t_sys_table_scheme where scheme_id=?";
				dao.delete(sql, paramList);
			}
			
			sql = "update t_sys_table_scheme set submoduleid=? where submoduleid=? and username=?";
			paramList.clear();
			paramList.add(changeSubmoduleId);
			paramList.add(submoduleId);
			paramList.add(this.userView.getUserName());
			dao.update(sql, paramList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 获取页面显示记录的数量
	 */
	@Override
    public int getPageSize(String dataType) {
		RowSet rs = null;
		try {
			boolean flag = false;
			String submoduleId = "shiftManage";
			if ("shiftCheck".equalsIgnoreCase(dataType))
				submoduleId = "shiftCheck";
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			//判断是否存在个人保存的栏目设置
			sql.append("select 1 from t_sys_table_scheme");
			sql.append(" where submoduleid=? and is_share=0 and username=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(submoduleId);
			paramList.add(this.userView.getUserName());
			rs = dao.search(sql.toString(), paramList);
			if (rs.next())
				flag = true;
			
			//查询栏目设置表中保存的每页显示的数据数量
			sql.setLength(0);
			sql.append("select rows_per_page from t_sys_table_scheme");
			sql.append(" where submoduleid=? and username=? and is_share=?");
			paramList.clear();
			paramList.add(submoduleId);
			paramList.add(this.userView.getUserName());
			if(flag)
				paramList.add("0");
			else
				paramList.add("1");
			
			rs = dao.search(sql.toString(), paramList);
			if (rs.next())
				this.pageSize = rs.getInt("rows_per_page");
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
        
		return this.pageSize;
	}
	
	/**
	 * 获取页面权限内的按钮
	 * @return
	 */
	@Override
    public String getButtons() {
		StringBuffer buttons = new StringBuffer();
		try {
			//导出
			if(this.userView.hasTheFunction("27202020405") || this.userView.hasTheFunction("27202020406")) {
				buttons.append("{xtype:'button',text: kq.label.exportDesc,height:22,");
				buttons.append("arrowAlign: 'right',menu:[");
				if(this.userView.hasTheFunction("27202020405")) {
					buttons.append("{text:kq.label.exportWeekShift,icon:'../../../../images/export.gif',");
					buttons.append("handler: function() {shiftManage.exportWorkingTable('0');}},");
				}
				
				if(this.userView.hasTheFunction("27202020406")) {
					buttons.append("{text:kq.label.exportMonthShift,icon:'../../../../images/export.gif',");
					buttons.append("handler: function() {shiftManage.exportWorkingTable('1');}},");
				}
				
				buttons.setLength(buttons.length() - 1);
				
				buttons.append("]},");
			}
			//班组成员
			if(this.userView.hasTheFunction("27202020401")){
				buttons.append("{xtype:'button',text: kq.shift.person,id:'personId',");
				buttons.append("height:22,listeners:{click:{");
				buttons.append("element: 'el',fn: function(){shiftManage.getShiftEmpTableConfig('0');");
				buttons.append("}}}},");
			}
			//排班
			if(this.userView.hasTheFunction("27202020402")){
				buttons.append("{xtype:'button',text:kq.label.shift,id:'shiftId',");
				buttons.append("height:22,arrowAlign: 'right',");
				buttons.append("menu:[{text:kq.label.autoShift,");
				buttons.append("icon:'../../../../module/kq/images/shift_autoShift.png',");
				buttons.append("handler: shiftManage.autoShift},{");
				buttons.append("text:kq.shift.copyLastWeekShift,");
				buttons.append("icon:'../../../../module/kq/images/shift_copy.png',");
				buttons.append("handler: shiftManage.copyLastWeekShiftInfo},{");
				buttons.append("text:kq.label.clearShift,");
				buttons.append("icon:'../../../../module/kq/images/shift_clear.png',");
				buttons.append("handler: shiftManage.deleteShiftInfo},{");
				buttons.append("text:kq.shift.shiftComment,");
				buttons.append("id:'shiftCommentId',");
				buttons.append("icon:'../../../../module/kq/images/shift_remark.png',");
				buttons.append("handler: shiftManage.searchRemark}]},");
			}
			//发布
			if(this.userView.hasTheFunction("27202020404")){
				buttons.append("{xtype:'button',id:'publishButtonId',text:kq.label.publish,");
				buttons.append("height:22,handler: function () {var state = 'push';");
				buttons.append("if('true'==shiftManage.pushScheme)state='edit';");
				buttons.append("shiftManage.pushShift(state);}},");
			}
			
			buttons.insert(0, "[");
			// 45183 排班 返回增加班组页面初始刷新方法
			buttons.append("{xtype:'button',height:22,text: kq.label.returnDesc,handler: function() {");
			// 49775 doRefresh刷新导致获取不到对象
			buttons.append("shiftManage.calBackFunc();}},");
			buttons.append("'->',{xtype:'checkboxfield',boxLabel: kq.shift.areaOperation,");
			buttons.append("name: 'checkbox',inputValue:'1',id: 'checkboxId',hidden:" + !this.userView.hasTheFunction("27202020402"));
			buttons.append(",listeners:{change:function (object, newValue, oldValue) {");
			buttons.append("shiftManage.reloadTable();}}}");
			buttons.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buttons.toString();
	}

	/**
	 * 查询对应的方案中的人员数量
	 * 
	 * @param schemeId
	 *            排班方案编号
	 * @return
	 */
	private int getPersonCount(String schemeId) {
		int personCount = 0;
		RowSet rs = null;
		try {
			String searchSql = "select COUNT(1) count from  kq_shift_scheme_emp where Scheme_id=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(schemeId);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(searchSql, paramList);
			if (rs.next())
				personCount = rs.getInt("count");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return personCount;
	}

	/**
	 * 复制上周的人员
	 * 
	 * @param groupId
	 *            班组编号
	 * @param lastSchemeId
	 *            上周排班方案编号
	 * @param schemeId
	 *            本周排班方案
	 */
	private void copylastWeekPerson(String groupId, String lastSchemeId, String schemeId) {
		try {
			DbWizard db = new DbWizard(this.conn);
			if (db.isExistTable("T_kq_scheme_emp", false))
				db.dropTable("T_kq_scheme_emp");

			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer insertSql = new StringBuffer();
			if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				insertSql.append("select Identity(Int, 1,1) displayId,guidkey,'" + schemeId + "' as SchemeId");
				insertSql.append(" into T_kq_scheme_emp from kq_shift_scheme_emp emp");
				insertSql.append(" where scheme_id='" + lastSchemeId + "'");
				insertSql.append(" and exists (select 1 from kq_group_emp_v2 groupEmp where Group_id='" + groupId + "'");
				insertSql.append(" and groupEmp.Guidkey=emp.guidkey)");
				insertSql.append(" order by emp.displayId");
				dao.update(insertSql.toString());

				insertSql.setLength(0);
				insertSql.append("insert into kq_shift_scheme_emp");
				insertSql.append(" (Scheme_id,guidkey,display_Id) ");
				insertSql.append(" select SchemeId,Guidkey,displayId");
				insertSql.append(" from T_kq_scheme_emp where SchemeId='" + schemeId + "'");
			} else {
				insertSql.append("insert into kq_shift_scheme_emp");
				insertSql.append(" (display_Id,Scheme_id,guidkey) ");
				insertSql.append(" select rownum as displayId,'" + schemeId + "' as Scheme_id,emp.guidkey");
				insertSql.append(" from kq_group_emp_v2 emp");
				insertSql.append(" where scheme_id='" + lastSchemeId + "'");
				insertSql
				        .append(" and exists (select 1 from kq_group_emp_v2 groupEmp where Group_id='" + groupId + "'");
				insertSql.append(" and groupEmp.Guidkey=emp.guidkey)");
				insertSql.append(" order by emp.displayId");
			}

			dao.update(insertSql.toString());
			db.dropTable("T_kq_scheme_emp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取排班审查所需SQL
	 * getShiftCheckSql
	 * @param dateScope		方案期间
	 * @param weekIndex		第几周标识
	 * @return
	 * @date 2019年3月8日 下午6:03:06
	 * @author linbz
	 */
	private String getShiftCheckSql(String dateScope, String weekIndex) {
		StringBuffer sql = new StringBuffer();
		try {
			HashMap map = KqPrivForHospitalUtil.getKqParameter(conn);
			// 考勤部门指标
			String kqDeptField = (String) map.get("kq_dept");
			boolean isNotBanlkKqDept = StringUtils.isNotBlank(kqDeptField);
	    	String whereInKqDept = "1=1";
			if(isNotBanlkKqDept)
				whereInKqDept = KqPrivForHospitalUtil.getPrivB0110Whr(userView, kqDeptField, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
			String whereInb0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	String whereInE01A1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "E01A1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	
	    	ArrayList<HashMap<String, String>> columnInfoList = this.getColumnList(Integer.valueOf(year), 
	    			Integer.valueOf(month), Integer.valueOf(weekIndex), "shiftCheck");
	    	StringBuffer columns = new StringBuffer();
			for(HashMap<String, String> columnMap : columnInfoList) {
				// 50390 获取主集字段即可
				if("A01".equalsIgnoreCase(columnMap.get("fieldSetId")))
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
				
				sql.insert(0, "select * from (");
				sql.append(") shiftCheck where 1=1");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();
	}
}

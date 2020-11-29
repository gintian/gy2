package com.hjsj.hrms.module.kq.card.businessobject.impl;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.module.kq.card.businessobject.KqCardDataAnalysisService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 分析结果页面接口实现类
 * @Title:        KqCardDataAnalysisServiceImpl.java
 * @Description:  用于实现分析结果页面的业务
 * @Company:      hjsj     
 * @Create time:  2019年9月30日 下午3:20:16
 * @author        chenxg
 * @version       7.5
 */
public class KqCardDataAnalysisServiceImpl implements KqCardDataAnalysisService, Runnable{
	private UserView userView;
	private Connection conn;
	private String subModuleId = "KqCardDataAnalysis_01";
	private JSONObject dateParam;
	
	public KqCardDataAnalysisServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	@Override
	public String getTableConfig() {
		String config = "";
		try {
			TableConfigBuilder builder = new TableConfigBuilder(this.subModuleId, gatColumnsInfos(), this.subModuleId,
			        this.userView, this.conn);
			builder.setLockable(true);
			builder.setDataSql(getSql(null));
			builder.setOrderBy(" order by dbid,a0000,kq_date");
			builder.setAutoRender(false);
			builder.setSetScheme(true);
			builder.setScheme(false);
			builder.setPageSize(20);
			builder.setTableTools(getButtons());
			builder.setColumnFilter(true);
			builder.setSelectable(true);
			builder.setSchemeItemKey("A01");
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			config = builder.createExtTableConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return config;
	}

	/**
	 * 页面按钮
	 * 
	 * @return
	 */
	private ArrayList<Object> getButtons() {
        ArrayList<Object> buttonsList = new ArrayList<Object>();
        try {
            if (this.userView.hasTheFunction("272060401")) {
                ArrayList<LazyDynaBean> menuList = new ArrayList<LazyDynaBean>();
                LazyDynaBean item = null;
                if (this.userView.hasTheFunction("27206040101")) {
                    item = new LazyDynaBean();
                    item.set("id", "exportButtonId");
                    item.set("text", ResourceFactory.getProperty("kq.card.exportData"));
                    item.set("handler", "KqCardDataAnalysis.exportCardData()");
                    menuList.add(item);
                }
                String menu = KqDataUtil.getMenuStr(ResourceFactory.getProperty("kq.card.menu"), "menuId", menuList);
                buttonsList.add(menu);
            }
            
            ButtonInfo buttonInfo = null;
            if (this.userView.hasTheFunction("272060402")) {
                buttonInfo = new ButtonInfo();
                buttonInfo.setId("dataAnalyseId");
                buttonInfo.setText(ResourceFactory.getProperty("kq.card.analysis.count"));
                buttonInfo.setHandler("KqCardDataAnalysis.dataAnalys");
                buttonsList.add(buttonInfo);
            }
            
            buttonInfo = new ButtonInfo();
            buttonInfo.setId("returnId");
            buttonInfo.setText(ResourceFactory.getProperty("button.return"));
            buttonInfo.setHandler("KqCardDataAnalysis.closeWin");
            buttonsList.add(buttonInfo);

            String sDate = getDateTime("s");
            String eDate = getDateTime("e");
            StringBuffer datefieldJson = new StringBuffer();
            datefieldJson.append("<jsfn>{xtype:'datetimefield',id:'sDate',format:'Y.m.d',value:'" + sDate + "',");
            datefieldJson.append("height:23,listeners:{change:KqCardDataAnalysis.searchCardData}}</jsfn>");
            buttonsList.add(datefieldJson.toString());
            buttonsList.add("<font>-</font>");
            datefieldJson = new StringBuffer();
            datefieldJson.append("<jsfn>{xtype:'datetimefield',id:'eDate',format:'Y.m.d',value:'" + eDate + "',");
            datefieldJson.append("height:23,listeners:{change:KqCardDataAnalysis.searchCardData}}</jsfn>");
            buttonsList.add(datefieldJson.toString());
        } catch (Exception e) {
            this.userView.getHm().put("errorMsg", e.toString());
            e.printStackTrace();
        }

        return buttonsList;
    }
	
	/**
	 * 查询数据的sql
	 * 
	 * @param param
	 *            日期参数：{sDate:开始时间，eDate：结束时间}
	 * @return
	 */
	private String getSql(JSONObject param) {
		StringBuffer sql = new StringBuffer();
		try {
			String sDate = "";
			String eDate = "";
			if (param != null) {
				sDate = param.getString("sDate");
				eDate = param.getString("eDate");
			} else {
				sDate = getDateTime("s");
				eDate = getDateTime("e");
			}

			StringBuffer columns = new StringBuffer();
			ArrayList<ColumnsInfo> columnInfoList = gatColumnsInfos();
			for (ColumnsInfo columnsInfo : columnInfoList) {
				String columnId = columnsInfo.getColumnId();
				ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
				if(childColumns != null && childColumns.size() > 0) {
					for(ColumnsInfo columnInfo : childColumns) {
						String childColumnId = columnInfo.getColumnId();
						if("guidkey".equalsIgnoreCase(childColumnId)) {
							columns.append("analyse." + childColumnId + ",");
						} else {
							columns.append(childColumnId + ",");
						}
					}
				} else {
					if("guidkey".equalsIgnoreCase(columnId)) {
						columns.append("analyse." + columnId + ",");
					} else {
						columns.append(columnId + ",");
					}
				}
			}

			ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			if (dbNameList == null || dbNameList.size() < 1) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.card.setNbase"), "", "");
			}
			String whereSql = getPrivWhere();
			StringBuffer a01Sql = new StringBuffer();
			for(String nbase : dbNameList) {
				if(StringUtils.isNotEmpty(a01Sql.toString())) {
					a01Sql.append(" union all ");
				}
				
				a01Sql.append("select (select dbid from dbname where pre='" + nbase + "') as dbid,");
				a01Sql.append("guidkey,a0000 from " + nbase + "a01");
				a01Sql.append(" where 1=2 or ");
				a01Sql.append(whereSql);
			}
			
			sql.append("select " + columns + "dbid,a0000 from kq_analyse_data analyse");
			sql.append(" left join (" + a01Sql + ") a01 on analyse.guidkey=a01.guidkey");
			sql.append(" where 1=1");
			if (StringUtils.isNotEmpty(sDate)) {
				sql.append(" and " + Sql_switcher.dateToChar("kq_date", "yyyy-MM-dd") + ">='" + sDate.replace(".", "-") + "'");
			}

			if (StringUtils.isNotEmpty(eDate)) {
				sql.append(" and " + Sql_switcher.dateToChar("kq_date", "yyyy-MM-dd") + "<='" + eDate.replace(".", "-") + "'");
			}

			sql.append(" and " + whereSql);
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return "select * from (" + sql.toString() + ") analyseData where 1=1";
	}
	/**
	 * 表格显示的列
	 * 
	 * @return
	 */
	private ArrayList<ColumnsInfo> gatColumnsInfos() {
		ArrayList<ColumnsInfo> columnInfoList = new ArrayList<ColumnsInfo>();
		try {
			ColumnsInfo info = new ColumnsInfo();
			info.setColumnType("D");
			info.setColumnId("kq_date");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.kqDate"));
			info.setCodesetId("0");
			info.setDisFormat("yyyy.MM.dd");
			info.setColumnLength(10);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			info.setColumnWidth(100);
			info.setSortable(true);
			info.setTextAlign("left");
			info.setFieldsetid("");
			columnInfoList.add(info);

			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("guidkey");
			info.setColumnDesc("");
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
			info.setColumnWidth(100);
			info.setEncrypted(true);
			info.setSortable(true);
			info.setTextAlign("left");
			info.setFieldsetid("");
			columnInfoList.add(info);
			
			FieldItem fi = DataDictionary.getFieldItem("e0122");
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("e0122");
			info.setColumnDesc(fi.getItemdesc());
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setCodesetId("UM");
			info.setFieldsetid("");
			info.setSortable(true);
			info.setTextAlign("left");
			info.setCtrltype("3");
            info.setNmodule("11");
			columnInfoList.add(info);
			
			fi = DataDictionary.getFieldItem("e01A1");
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("e01A1");
			info.setColumnDesc(fi.getItemdesc());
			info.setCodesetId("@K");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(true);
			info.setTextAlign("left");
			info.setCtrltype("3");
            info.setNmodule("11");
			columnInfoList.add(info);
			
			fi = DataDictionary.getFieldItem("a0101");
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("a0101");
			info.setColumnDesc(fi.getItemdesc());
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(true);
			info.setTextAlign("left");
			columnInfoList.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("card_no");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.card_no"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(true);
			info.setTextAlign("left");
			columnInfoList.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("card_data");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.cardData"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(120);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			columnInfoList.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("kq_status");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.status"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			info.setRendererFunc("KqCardDataAnalysis.showStatus");
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> ondutyColumnsList1 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_be_late_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.late"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			ondutyColumnsList1.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_absent_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			ondutyColumnsList1.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.onduty1"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(ondutyColumnsList1);
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> offdutyColumnsList1 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_leave_early_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList1.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_absent_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList1.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_1");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.offduty1"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(offdutyColumnsList1);
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> childColumnsList2 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_be_late_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.late"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			childColumnsList2.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_absent_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			childColumnsList2.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.onduty2"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(childColumnsList2);
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> offdutyColumnsList2 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_leave_early_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList2.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_absent_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList2.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_2");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.offduty2"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(offdutyColumnsList2);
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> childColumnsList3 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_be_late_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.late"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			childColumnsList3.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_absent_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			childColumnsList3.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("onduty_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.onduty3"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(childColumnsList3);
			columnInfoList.add(info);
			
			ArrayList<ColumnsInfo> offdutyColumnsList3 = new ArrayList<ColumnsInfo>();
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_leave_early_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList3.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_absent_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("left");
			offdutyColumnsList3.add(info);
			
			info = new ColumnsInfo();
			info.setColumnType("A");
			info.setColumnId("offduty_3");
			info.setColumnDesc(ResourceFactory.getProperty("kq.card.analysis.offduty3"));
			info.setCodesetId("0");
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info.setColumnWidth(100);
			info.setFieldsetid("");
			info.setSortable(false);
			info.setTextAlign("center");
			info.setChildColumns(offdutyColumnsList3);
			columnInfoList.add(info);

		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return columnInfoList;
	}
	@Override
	public String getFieldsArray() {
		StringBuffer fieldsArray = new StringBuffer("[");
		try {
			ArrayList<ColumnsInfo> fieldList = gatColumnsInfos();
			for (ColumnsInfo columnsInfo : fieldList) {
				if (ColumnsInfo.LOADTYPE_HIDDEN == columnsInfo.getLoadtype()
				        || "M".equalsIgnoreCase(columnsInfo.getColumnType())
				        || "guidkey".equalsIgnoreCase(columnsInfo.getColumnId())) {
					continue;
				}

				ArrayList<ColumnsInfo> childColumnList = columnsInfo.getChildColumns();
				if(childColumnList != null && childColumnList.size() > 0) {
					for(ColumnsInfo childColumn : childColumnList) {
						fieldsArray.append(getFieldJson(childColumn));
					}
				} else {
					fieldsArray.append(getFieldJson(columnsInfo));
				}
			}

			if (fieldsArray.toString().endsWith(",")) {
				fieldsArray.setLength(fieldsArray.length() - 1);
			}

		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		fieldsArray.append("]");
		return fieldsArray.toString();
	}

	private String getFieldJson(ColumnsInfo columnsInfo) {
		StringBuffer columnJson = new StringBuffer();
		try {
			String itemId = columnsInfo.getColumnId();
			columnJson.append("{'itemid':'" + itemId + "',");
			if ("cardtime".equalsIgnoreCase(columnsInfo.getColumnId())) {
				columnJson.append("'type':'D',");
			} else {
				columnJson.append("'type':'" + columnsInfo.getColumnType() + "',");
			} 
			
			if("onduty_be_late_1".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyLate1") + "',");
			} else if("onduty_absent_1".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyAbsent1") + "',");
			} else if("offduty_leave_early_1".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyLeaveEarly1") + "',");
			} else if("offduty_absent_1".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'" 
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyAbsent1") + "',");
			} else if("onduty_be_late_2".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyLate2") + "',");
			} else if("onduty_absent_2".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyAbsent2") + "',");
			} else if("offduty_leave_early_2".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyLeaveEarly2") + "',");
			} else if("offduty_absent_2".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'" 
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyAbsent2") + "',");
			} else if("onduty_be_late_3".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyLate3") + "',");
			} else if("onduty_absent_3".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.ondutyAbsent3") + "',");
			} else if("offduty_leave_early_3".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'"
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyLeaveEarly3") + "',");
			} else if("offduty_absent_3".equalsIgnoreCase(itemId)) {
				columnJson.append("'itemdesc':'" 
						+ ResourceFactory.getProperty("kq.card.analysis.offdutyAbsent3") + "',");
			} else {
				columnJson.append("'itemdesc':'" + columnsInfo.getColumnDesc() + "',");
			}
			
			columnJson.append("'codesetid':'" + columnsInfo.getCodesetId() + "',");
			
			String formatStr = "";
			if ("D".equals(columnsInfo.getColumnType()) && StringUtils.isEmpty(formatStr)) {
				formatStr = "Y-m-d";
			}
			
			columnJson.append("'format':'" + formatStr + "',");
			if (!"0".equals(columnsInfo.getCodesetId())) {
				columnJson.append("'ctrltype':'0'},");
			} else {
				columnJson.append("},");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return columnJson.toString();
	}
	
	@Override
	public void searchCardData(JSONObject param) {
		try {
			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(this.subModuleId);
			catche.setTableSql(getSql(param));
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public String exportCardData(JSONArray param) {
		String fileName = this.userView.getUserName() +"_"+"kqCardAnalyse" + ".xls";
		try {
			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(this.subModuleId);
			String sql = catche.getTableSql();
			String orderBySql = catche.getSortSql();
			String querySql = catche.getQuerySql();
			if (StringUtils.isNotEmpty(querySql)) {
				sql += querySql.replace("myGridData", "analyseData");
			}
			
			String filterSql = catche.getFilterSql();
			if (StringUtils.isNotEmpty(filterSql)) {
				sql += filterSql;
			}

			StringBuffer personWhere = new StringBuffer();
			for(int i = 0; i < param.size(); i++) {
				JSONObject jsObject = param.getJSONObject(i);
				
				if(i > 0) {
					personWhere.append(" or ");
				}
				
				String guidkey = jsObject.getString("guidkey");
				guidkey = PubFunc.decrypt(guidkey);
				String kqDate = jsObject.getString("kq_date");
				personWhere.append("(guidkey='" + guidkey + "' and ");
				personWhere.append(Sql_switcher.dateToChar("kq_date", "yyyy-MM-dd"));
				personWhere.append("='" + kqDate + "')");
			}
			
			if(StringUtils.isNotEmpty(personWhere.toString())) {
				sql += " and (" + personWhere + ")";
			}
			// 60012 选人条件拼接错误
			sql += " " + orderBySql;
			
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
			ArrayList<LazyDynaBean> mergedCellList = getExcleMergedList(catche.getDisplayColumns());
			ArrayList<LazyDynaBean> list = getHeadList(catche.getDisplayColumns(), mergedCellList, false, 0);
			int headStartRowNum = 0;
			if (!mergedCellList.isEmpty()) {
				headStartRowNum = 1;
			}

			fileName = excelUtil.exportExcelBySql(fileName, null, mergedCellList, list, sql, null, headStartRowNum);

		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return fileName;
	}
	
	/**
	 * 获取导出需要的表头的合并列
	 * 
	 * @param fieldList
	 *            表头的列头
	 * @return
	 */
	private ArrayList<LazyDynaBean> getExcleMergedList(ArrayList<ColumnsInfo> fieldList) {
		ArrayList<LazyDynaBean> mergedList = new ArrayList<LazyDynaBean>();
		int num = 0;
		int ind = 0;
		for (int i = 0; i < fieldList.size(); i++) {
			ColumnsInfo columnsInfo = fieldList.get(i);
			ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
			// 合并列在表头中为hashmap，非hashmap的都为非合并列
			if (!childColumns.isEmpty()) {
				LazyDynaBean bean = new LazyDynaBean();
				// 设置合并列的起始行
				bean.set("fromRowNum", 0);
				// 设置合并列的起始列
				bean.set("fromColNum", ind + num);
				// 设置合并列的终止行
				bean.set("toRowNum", 0);
				// 设置合并列的终止列
				bean.set("toColNum", ind + num + childColumns.size() - 1);
				// 设置合并列的名称
				bean.set("content", columnsInfo.getColumnDesc());
				mergedList.add(bean);
				num = num + childColumns.size() - 1;
			} else {
				ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
				String itemid = info.getColumnId();
				if ("guidkey".equalsIgnoreCase(itemid) || 4 == info.getLoadtype()) {
					continue;
				}
			}

			ind++;
		}

		return mergedList;
	}

	/**
	 * 获取导出数据的列头
	 * 
	 * @param fieldList
	 *            表格中显示的列
	 * @param mergedList
	 *            合并的列
	 * @param flag
	 *            是否是合并列中的子列
	 * @param index
	 *            从第几列开始
	 * @return
	 */
	public ArrayList<LazyDynaBean> getHeadList(ArrayList fieldList, ArrayList<LazyDynaBean> mergedList, boolean flag,
	        int index) {
		int num = 0;
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		int ind = 0;
		for (int i = 0; i < fieldList.size(); i++) {
			ColumnsInfo columnsInfo = (ColumnsInfo) fieldList.get(i);
			ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
			// 合并列在表头中为hashmap，非hashmap的都为非合并列
			if (!childColumns.isEmpty()) {
				// 获取合并列中包含的指标
				headList.addAll(getHeadList(childColumns, mergedList, true, ind + num));
				num = num + childColumns.size();
			} else {
				ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
				LazyDynaBean bean = new LazyDynaBean();
				String itemid = info.getColumnId();
				if ("guidkey".equalsIgnoreCase(itemid) || 4 == info.getLoadtype()) {
					continue;
				}

				bean.set("itemid", itemid);
				bean.set("content", info.getColumnDesc() + "");
				bean.set("codesetid", info.getCodesetId());
				bean.set("colType", info.getColumnType());
				bean.set("decwidth", info.getDecimalWidth() + "");
				if (mergedList != null && mergedList.size() > 0) {
					if (flag) {
						// 设置合并列中包含的指标起始与终止的行
						bean.set("fromRowNum", 1);
						bean.set("toRowNum", 1);
						// 设置指标的起始与终止的列
						bean.set("fromColNum", ind + index);
						bean.set("toColNum", ind + index);
					} else {
						// 设置非合并列中包含的指标起始与终止的行
						bean.set("fromRowNum", 0);
						bean.set("toRowNum", 1);
						// 设置指标的起始与终止的列
						bean.set("fromColNum", ind + num);
						bean.set("toColNum", ind + num);
					}
				}

				headList.add(bean);
				ind++;
			}
		}

		return headList;
	}
	/**
	 * 数据分析
	 * 
	 * @param param
	 *            日期参数：{sDate:开始时间，eDate：结束时间}
	 */
	@Override
	public void dataAnalys(JSONObject param) {
		CallableStatement call = null;
		boolean needCloseConnFlag = false;
		try {
			String sDate = "";
			String eDate = "";
			if (param != null) {
				sDate = param.getString("sDate");
				eDate = param.getString("eDate");
			}
			
			if(this.conn == null || this.conn.isClosed()) {
			    this.conn = AdminDb.getConnection();
			    needCloseConnFlag = true;
			}
			
			KqPrivForHospitalUtil kq = new KqPrivForHospitalUtil(this.userView, this.conn);
			String cardItem = kq.getKqCard_no();
			//【60662】由于未知的原因，在测试环境中数据库链接会关闭，因此需要判断数据库链接是否存在或关闭，如果不存在或已关闭则重新获取数据库链接
			if(this.conn == null || this.conn.isClosed()) {
			    this.conn = AdminDb.getConnection();
			    needCloseConnFlag = true;
			}
			
			String whereSql = getPrivWhere();
			StringBuffer sql = new StringBuffer(); 
			sql.append("delete from kq_analyse_data");
			sql.append(" where " + Sql_switcher.dateToChar("kq_date", "yyyy-MM-dd"));
			sql.append(" between '" + sDate.replace(".", "-") + "'");
			sql.append(" and '" + eDate.replace(".", "-") + "'");
			sql.append(" and " + whereSql);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString());
			ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			StringBuffer a01Sql = new StringBuffer();
			for (String nbase : dbNameList) {
				if (a01Sql.length() > 0) {
					a01Sql.append(" union all ");
				}
				
				a01Sql.append("select '" + nbase + "' nbase,a0100,guidkey,a0101,b0110,e0122,e01a1");
				if(StringUtils.isNotEmpty(cardItem)) {
					a01Sql.append("," + cardItem);
				}
					
				a01Sql.append(" from " + nbase + "a01");
			}
			
			sql.setLength(0);
			sql.append("insert into kq_analyse_data (kq_date,guidkey,a0101,b0110,e0122,e01a1");
			if(StringUtils.isNotEmpty(cardItem)) {
				sql.append(",card_no");
			}
			
			sql.append(") select distinct " + Sql_switcher.charToDate("'#date#'"));
			sql.append(",guidkey,a0101,b0110,e0122,e01a1");
			if(StringUtils.isNotEmpty(cardItem)) {
				sql.append("," + cardItem);
			}
			
			sql.append(" from (" + a01Sql +") a01");
			sql.append(" where 1=1 and " + whereSql);
			
			IfRestDate ifRestDate = new IfRestDate();
			ArrayList restList=IfRestDate.search_RestOfWeek("UN",userView,conn);
			String restdate = ResourceFactory.getProperty("kq.date.work");
			
			ArrayList<String> sqlList = new ArrayList<String>();
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			cal.setTime(DateUtils.getDate(eDate, "yyyy.MM.dd"));
			Date tdate = cal.getTime();
			cal.setTime(DateUtils.getDate(sDate, "yyyy.MM.dd"));
			Date fdate = cal.getTime();
			//节假日日期
			StringBuffer restDates = new StringBuffer();
			//公休日日期
			StringBuffer feastDates = new StringBuffer();
			String b0110 = "UN";
			while (fdate.compareTo(tdate) < 1) {
				String updateSql = sql.toString();
				updateSql = updateSql.replace("#date#", DateUtils.format(fdate, "yyyy.MM.dd"));
				sqlList.add(updateSql);
				
				String curDate = DateUtils.format(fdate, "yyyy.MM.dd");
				for(int i = 0; i < restList.size(); i++) {
					String restDate = (String)restList.get(i);
					if(StringUtils.isEmpty(restDate)) {
						continue;
					}
					
					String returnFlag = IfRestDate.is_RestDate(curDate, userView, restDate, "UN", this.conn);
					//如果是公休日中的休息日，则跳出循环
					String feast_name= IfRestDate.if_Feast(curDate,conn);
					//判断是不是节假日
					if(StringUtils.isNotEmpty(feast_name)) {
						String turn_date= IfRestDate.getTurn_Date(b0110,curDate,conn);
						if(StringUtils.isEmpty(turn_date))
							feastDates.append(curDate + ",");
						else {
							if(IfRestDate.if_Rest(curDate,userView,restDate))
								feastDates.append(curDate + ",");
							
						}
						
					}else{
						//判断公休日
						if(IfRestDate.if_Rest(curDate,userView,restDate)) {
							String turn_date= IfRestDate.getTurn_Date(b0110,curDate,conn);
							if(turn_date==null||turn_date.length()<=0)
								restDates.append(curDate + ",");
						} else {
							//公休是否倒休
							String g_rest_date= IfRestDate.getWeek_Date(b0110,curDate,conn);
							//有倒休日期，上班
							if(StringUtils.isNotEmpty(g_rest_date))
								restDates.append(curDate + ",");
						}
					}
					break;
//					if(!restdate.equalsIgnoreCase(returnFlag)) {
//					}
				}
				cal.add(Calendar.DATE, 1);
				fdate = cal.getTime();
			}
			
			dao.batchUpdate(sqlList);
			String nbases = "";
			for(String nbase : dbNameList) {
				nbases += nbase + ",";
			}
			
			sql.setLength(0);
			sql.append("{call KQ_CARDDATE_ANALYSIS(?,?,?,?,?,?)}");
			call = this.conn.prepareCall(sql.toString());
			call.setString(1, sDate);
			call.setString(2, eDate);
			call.setString(3, nbases);
			call.setString(4, " and " + whereSql);
			call.setString(5, restDates.toString());
			call.setString(6, feastDates.toString());
			call.execute();
			
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(call);
			this.userView.getHm().put("finish", "true");
			if(needCloseConnFlag) {
			    PubFunc.closeResource(this.conn);
			    this.conn = null;
			}
		}
		
	}
	/**
	 * 获取当前月的第一天或最后一天
	 * 
	 * @param flag
	 *            =s:获取第一天；=e：获取最后一天
	 * @return
	 */
	private String getDateTime(String flag) {
		String dateTime = "";
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			if ("s".equalsIgnoreCase(flag)) {
				int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, firstDay);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				dateTime = DateUtils.format(cal.getTime(), "yyyy.MM.dd");
			} else if ("e".equalsIgnoreCase(flag)) {
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, lastDay);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				dateTime = DateUtils.format(cal.getTime(), "yyyy.MM.dd");
			}
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return dateTime;
	}
	/**
	 * 获取用户的权限相关的sql条件
	 * @return
	 */
	private String getPrivWhere() {
	    StringBuffer whereSql = new StringBuffer();
	    try {
	        String b0110Priv = KqPrivForHospitalUtil.getPrivB0110Whr(this.userView, "b0110",
	                KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	        String e0122Priv = KqPrivForHospitalUtil.getPrivB0110Whr(this.userView, "e0122",
	                KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	        
	        if(StringUtils.isNotEmpty(b0110Priv) && !"1=1".equals(b0110Priv)) {
	            whereSql.append("(" + b0110Priv + ")");
	        }
	        
	        if(StringUtils.isNotEmpty(e0122Priv) && !"1=1".equals(e0122Priv)) {
	            if(StringUtils.isNotEmpty(whereSql.toString())) {
	                whereSql.append(" or ");
	            }
	            
	            whereSql.append("(" + e0122Priv + ")");
	        }
            
	        if(StringUtils.isEmpty(whereSql.toString())) {
	            whereSql.append("1=1");
	        }
        } catch (Exception e) {
            e.printStackTrace();
        } 
	    
	    return "(" + whereSql.toString() + ")";
    }

    @Override
    public void run() {
        try {
            this.dataAnalys(this.dateParam);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    @Override
    public void setDateParam(JSONObject dateParam) {
        this.dateParam = dateParam;
    }
}

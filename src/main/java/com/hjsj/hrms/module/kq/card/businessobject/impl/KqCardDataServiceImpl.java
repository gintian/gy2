package com.hjsj.hrms.module.kq.card.businessobject.impl;

import com.hjsj.hrms.module.kq.card.businessobject.KqCardDataService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.*;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 打开数据实现类
 * 
 * @Title: KqCardDataServiceImpl.java
 * @Description: 用于实现打卡数据页面相关的接口
 * @Company: hjsj
 * @Create time: 2019年8月20日 下午4:43:38
 * @author chenxg
 * @version 7.5
 */
public class KqCardDataServiceImpl implements KqCardDataService {
	private UserView userView;
	private Connection conn;
	private String subModuleId = "kqCardData_01";
	HashMap<String, ArrayList<String>> msgMap = new HashMap<String, ArrayList<String>>();

	public KqCardDataServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 页面显示的表格参数
	 * @throws GeneralException 
	 */
	@Override
	public String getTableConfig() throws GeneralException {
	    String config = "";
	    try {
		TableConfigBuilder builder = new TableConfigBuilder(this.subModuleId, gatColumnsInfos(), "kqCardData_01",
		        this.userView, this.conn);
		builder.setLockable(true);
        builder.setDataSql(getSql(null));
        builder.setOrderBy(" order by dbid,a0000,cardtime");
        builder.setAutoRender(false);
        builder.setTitle(ResourceFactory.getProperty("kq.card.title"));
        builder.setSetScheme(true);
        builder.setPageSize(20);
        builder.setTableTools(getButtons());
        builder.setColumnFilter(true);
        builder.setSelectable(true);
        if(this.userView.hasTheFunction("2720605")) {
            builder.setScheme(true);
        } else {
            builder.setScheme(false);
        }
        
        builder.setSchemeItemKey("A01");
        builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
        config = builder.createExtTableConfig();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
        }
	    
		return config;
	}

	/**
	 * 表格显示的列
	 * 
	 * @return
	 */
	private ArrayList<ColumnsInfo> gatColumnsInfos() {
		ArrayList<ColumnsInfo> columnInfoList = new ArrayList<ColumnsInfo>();
		try {
			HashMap<String, String> itemMap = new HashMap<String, String>();
			itemMap.put("nbase", ResourceFactory.getProperty("kq.card.nbase"));
			itemMap.put("a0100", ResourceFactory.getProperty("kq.card.a0100"));
			itemMap.put("e0122", ResourceFactory.getProperty("kq.card.e0122"));
			itemMap.put("e01A1", ResourceFactory.getProperty("kq.card.e01A1"));
			itemMap.put("a0101", ResourceFactory.getProperty("kq.card.a0101"));
			itemMap.put("card_no", ResourceFactory.getProperty("kq.card.card_no"));
			itemMap.put("cardtime", ResourceFactory.getProperty("kq.card.cardtime"));
			itemMap.put("inout_flag", ResourceFactory.getProperty("kq.card.inout_flag"));
			itemMap.put("location", ResourceFactory.getProperty("kq.card.workLocation"));

			TableFactoryBO tableBo = new TableFactoryBO(this.subModuleId, this.userView, conn);
			HashMap scheme = tableBo.getTableLayoutConfig();
			StringBuffer coulumns = new StringBuffer(",");
			if (scheme != null) {
				Integer schemeId = (Integer) scheme.get("schemeId");
				ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);

				for (int i = 0; i < columnConfigList.size(); i++) {
					ColumnConfig column = columnConfigList.get(i);
					if (null == column) {
						continue;
					}
					
					String itemid = column.getItemid();
					FieldItem fi = null;
					if (StringUtils.isNotEmpty(column.getFieldsetid())) {
						fi = DataDictionary.getFieldItem(itemid, column.getFieldsetid());
					}
					
					if ("e0122".equalsIgnoreCase(itemid) || "e01A1".equalsIgnoreCase(itemid)
					        || "a0101".equalsIgnoreCase(itemid)) {
						fi = DataDictionary.getFieldItem(itemid, "A01");
					}
					
					String itemDesc = column.getItemdesc();
					if (StringUtils.isEmpty(itemDesc)) {
						if (fi != null) {
							itemDesc = fi.getItemdesc();
						} else {
							itemDesc = itemMap.get(itemid);
						}
					}

					String codesetid = "0";
					if (fi != null) {
						codesetid = fi.getCodesetid();
					}
					
					ColumnsInfo info = new ColumnsInfo();
					// column不为null说明该表格栏目设置有私有方案
					info.setColumnId(itemid);
					info.setColumnDesc(itemDesc);
					info.setColumnType(column.getItemtype());
					info.setColumnWidth(column.getDisplaywidth());
					info.setFieldsetid(column.getFieldsetid());
					info.setSortable(true);
					info.setCodesetId(codesetid);
					info.setTextAlign(column.getAlign() + "");
					// a0100单独处理
					if ("a0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)) {
						info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						info.setEncrypted(true);
					} else if ("0".equalsIgnoreCase(column.getIs_display())) {
						info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
					} else {
						info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
					}
					
					if ("cardtime".equalsIgnoreCase(itemid)) {
						info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
					}
					
					if (fi != null && "N".equalsIgnoreCase(fi.getItemtype())) {
						info.setDecimalWidth(fi.getDecimalwidth());
						info.setTextAlign("right");
					}

					if(",b0110,e0122,e01a1,".contains("," + itemid.toLowerCase() + ",")) {
					    info.setCtrltype("3");
		                info.setNmodule("11");
					}
					
					info.setEditableValidFunc("false");
					KqPrivBo.setKqPrivCodeSource(fi, info);
					columnInfoList.add(info);
					coulumns.append(column.getItemid() + ",");
				}

				if(coulumns.toString().indexOf("nbase") < 0) {
					ColumnsInfo info = new ColumnsInfo();
					info.setColumnType("A");
					info.setColumnId("nbase");
					info.setColumnDesc(itemMap.get("nbase"));
					info.setCodesetId("0");
					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
					info.setColumnWidth(100);
					info.setEncrypted(true);
					info.setSortable(false);
					info.setTextAlign("left");
					info.setFieldsetid("");
					columnInfoList.add(info);
					
					info = new ColumnsInfo();
					info.setColumnType("A");
					info.setColumnId("a0100");
					info.setColumnDesc(itemMap.get("a0100"));
					info.setCodesetId("0");
					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
					info.setColumnWidth(100);
					info.setEncrypted(true);
					info.setSortable(false);
					info.setTextAlign("left");
					info.setFieldsetid("");
					columnInfoList.add(info);
				}
			} else {
				ColumnsInfo info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("nbase");
				info.setColumnDesc(itemMap.get("nbase"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
				info.setColumnWidth(100);
				info.setEncrypted(true);
				info.setSortable(false);
				info.setTextAlign("left");
				info.setFieldsetid("");
				columnInfoList.add(info);

				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("a0100");
				info.setColumnDesc(itemMap.get("a0100"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
				info.setColumnWidth(100);
				info.setEncrypted(true);
				info.setSortable(false);
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
				info.setSortable(false);
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
				info.setSortable(false);
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
				info.setSortable(false);
				info.setTextAlign("left");
				columnInfoList.add(info);

				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("card_no");
				info.setColumnDesc(itemMap.get("card_no"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setSortable(false);
				info.setTextAlign("left");
				columnInfoList.add(info);

				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("cardtime");
				info.setColumnDesc(itemMap.get("cardtime"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(120);
				info.setFieldsetid("");
				info.setSortable(false);
				info.setTextAlign("left");
				columnInfoList.add(info);

				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("inout_flag");
				info.setColumnDesc(itemMap.get("inout_flag"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setSortable(false);
				info.setTextAlign("left");
				columnInfoList.add(info);

				info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("location");
				info.setColumnDesc(itemMap.get("location"));
				info.setCodesetId("0");
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				info.setColumnWidth(100);
				info.setFieldsetid("");
				info.setSortable(false);
				info.setTextAlign("left");
				columnInfoList.add(info);
			}

		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return columnInfoList;
	}

	/**
	 * 页面按钮
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList<Object> getButtons() throws GeneralException {
        ArrayList<Object> buttonsList = new ArrayList<Object>();
        try {
            if(this.userView.hasTheFunction("2720601")) {
                ArrayList<LazyDynaBean> menuList = new ArrayList<LazyDynaBean>();
                LazyDynaBean item = null;
                if(this.userView.hasTheFunction("272060101")) {
                    item = new LazyDynaBean();
                    item.set("id", "exportId");
                    item.set("text", ResourceFactory.getProperty("kq.card.exportData"));
                    item.set("handler", "KqCardData.exportCardData()");
                    menuList.add(item);
                }
                
                String menu = KqDataUtil.getMenuStr(ResourceFactory.getProperty("kq.card.menu"), "buttonMenuId", menuList);
                buttonsList.add(menu);
            }


            ButtonInfo buttonInfo = null;
            if(this.userView.hasTheFunction("2720602")) {
                buttonInfo = new ButtonInfo();
                buttonInfo.setId("importId");
                buttonInfo.setText(ResourceFactory.getProperty("kq.card.importData"));
                buttonInfo.setHandler("KqCardData.importCardData");
                buttonsList.add(buttonInfo);
            }

            if (this.userView.hasTheFunction("2720603")) {
                buttonInfo = new ButtonInfo();
                buttonInfo.setId("deleteId");
                buttonInfo.setText(ResourceFactory.getProperty("kq.card.delete"));
                buttonInfo.setHandler("KqCardData.deleteData");
                buttonsList.add(buttonInfo);
            }
            
            if (this.userView.hasTheFunction("2720604")) {
                buttonInfo = new ButtonInfo();
                buttonInfo.setId("analyseId");
                buttonInfo.setText(ResourceFactory.getProperty("kq.card.analysis"));
                buttonInfo.setHandler("KqCardData.dataAnalysis");
                buttonsList.add(buttonInfo);
            }
            
            String sDate = getDateTime("s");
            String eDate = getDateTime("e");
            StringBuffer datefieldJson = new StringBuffer();
            datefieldJson.append("<jsfn>{xtype:'datetimefield',id:'fromDate',format:'Y.m.d H:i',value:'" + sDate + "',");
            datefieldJson.append("height:23,listeners:{change:KqCardData.searchCardData}}</jsfn>");
            buttonsList.add(datefieldJson.toString());
            buttonsList.add("<font>-</font>");
            datefieldJson = new StringBuffer();
            datefieldJson.append("<jsfn>{xtype:'datetimefield',id:'toDate',format:'Y.m.d H:i',value:'" + eDate + "',");
            datefieldJson.append("height:23,listeners:{change:KqCardData.searchCardData}}</jsfn>");
            buttonsList.add(datefieldJson.toString());
        } catch (Exception e) {
            this.userView.getHm().put("errorMsg", e.toString());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
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
			StringBuffer a01Columns = new StringBuffer();
			ArrayList<ColumnsInfo> columnInfoList = gatColumnsInfos();
			for (ColumnsInfo columnsInfo : columnInfoList) {
				String columnId = columnsInfo.getColumnId();
				if ("cardtime".equalsIgnoreCase(columnId)) {
					columns.append("(" + joinDateFieldSql("cardData.work_date", "cardData.work_time") + ") cardtime,");
				} else if ("inout_flag".equalsIgnoreCase(columnId)) {
					columns.append("case when cardData.inout_flag='1' then '"
					        + ResourceFactory.getProperty("kq.card.swork") + "' ");
					columns.append(
					        " when cardData.inout_flag='-1' then '" + ResourceFactory.getProperty("kq.card.ework"));
					columns.append("' else '" + ResourceFactory.getProperty("kq.card.Unlimited") + "' end inout_flag,");
				} else if ("A01".equalsIgnoreCase(columnsInfo.getFieldsetid())) {
					columns.append("a01." + columnId + ",");
					a01Columns.append(columnId + ",");
				} else {
					columns.append("cardData." + columnId + ",");
				}
			}
			// 60012 增加工作日期 时间 选人导出时用到
			columns.append("cardData.work_date,cardData.work_time,");
			
			ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			if (dbNameList == null || dbNameList.size() < 1) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.card.setNbase"), "", "");
			}
			
			if(a01Columns.toString().indexOf("b0110") < 0) {
				a01Columns.append("b0110,");
			}
			
			if(a01Columns.toString().indexOf("e0122") < 0) {
                a01Columns.append("e0122,");
            }
			a01Columns.setLength(a01Columns.length() - 1);
			StringBuffer a01Sql = new StringBuffer();
			for (String nbase : dbNameList) {
				if (a01Sql.length() > 0) {
					a01Sql.append(" union all ");
				}
				
				a01Sql.append("select (select dbid from dbname where pre='" + nbase + "') as dbid,'" + nbase + "' nbase,a0000,a0100," + a01Columns + " from " + nbase + "a01");
			}

			columns.setLength(columns.length() - 1);
			sql.append("select a01.dbid,a01.a0000," + columns + " from kq_originality_data cardData");
			sql.append(" left join (" + a01Sql + ") a01");
			sql.append(" on cardData.nbase=a01.nbase and cardData.a0100=a01.a0100");
			sql.append(" where 1=1");
			if (StringUtils.isNotEmpty(sDate)) {
				sql.append(" and (" + joinDateFieldSql("cardData.work_date", "cardData.work_time") + ")>='" + sDate + "'");
			}

			if (StringUtils.isNotEmpty(eDate)) {
				sql.append(" and (" + joinDateFieldSql("cardData.work_date", "cardData.work_time") + ")<='" + eDate + "'");
			}

			String whereSql = getPrivWhere();
			whereSql = whereSql.replace("b0110", "a01.b0110").replace("e0122", "a01.e0122");
			sql.append(" and " + whereSql);
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}
		return "select * from (" + sql.toString() + ") cardatas where 1=1 ";
	}

	/**
	 * 切换日期查询
	 * 
	 * @param param
	 *            日期参数：{sDate:开始时间，eDate：结束时间}
	 */
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

	/**
	 * 删除数据
	 * 
	 * @param param
	 *            要删除数据的参数：{nbase:人员库，a0100:人员编号，cardtime:打卡时间}
	 */
	@Override
	public void deleteCardData(JSONArray param) {
		try {
			ArrayList<ArrayList<String>> paramsList = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < param.size(); i++) {
				ArrayList<String> valueList = new ArrayList<String>();
				JSONObject valueJson = (JSONObject) param.get(i);
				String nbase = valueJson.getString("nbase");
				nbase = PubFunc.decrypt(nbase);
				valueList.add(nbase);
				String a0100 = valueJson.getString("a0100");
				a0100 = PubFunc.decrypt(a0100);
				valueList.add(a0100);
				String cardtime = valueJson.getString("cardtime");
				valueList.add(cardtime);
				paramsList.add(valueList);
			}

			StringBuffer sql = new StringBuffer();
			sql.append("delete from kq_originality_data");
			sql.append(" where nbase=? and a0100=? and ");
			joinDateFieldSql("work_date", "work_time");
			sql.append(" (" + joinDateFieldSql("work_date", "work_time") + ")=?");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.batchUpdate(sql.toString(), paramsList);
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 获取页面显示的指标用于生成快速查询功能
	 * 
	 * @return
	 */
	@Override
	public String getFieldsArray() {
		StringBuffer fieldsArray = new StringBuffer("[");
		try {
			ArrayList<ColumnsInfo> fieldList = gatColumnsInfos();
			for (ColumnsInfo ColumnInfo : fieldList) {
				if (ColumnsInfo.LOADTYPE_HIDDEN == ColumnInfo.getLoadtype()
				        || "M".equalsIgnoreCase(ColumnInfo.getColumnType())
				        || "nbase".equalsIgnoreCase(ColumnInfo.getColumnId())
				        || "a0100".equalsIgnoreCase(ColumnInfo.getColumnId())) {
					continue;
				}
				
				fieldsArray.append("{'itemid':'" + ColumnInfo.getColumnId() + "',");
				if ("cardtime".equalsIgnoreCase(ColumnInfo.getColumnId())) {
					fieldsArray.append("'type':'D',");
				} else {
					fieldsArray.append("'type':'" + ColumnInfo.getColumnType() + "',");
				}
				
				fieldsArray.append("'itemdesc':'" + ColumnInfo.getColumnDesc() + "',");
				fieldsArray.append("'codesetid':'" + ColumnInfo.getCodesetId() + "',");

				String formatStr = "";
				if ("D".equals(ColumnInfo.getColumnType()) && StringUtils.isEmpty(formatStr)) {
					formatStr = "Y-m-d";
				}
				
				if ("cardtime".equalsIgnoreCase(ColumnInfo.getColumnId())) {
					formatStr = "Y-m-d H:i";
				}
				
				fieldsArray.append("'format':'" + formatStr + "',");
				if (!"0".equals(ColumnInfo.getCodesetId())) {
					fieldsArray.append("'ctrltype':'0'},");
				} else {
					fieldsArray.append("},");
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

	/**
	 * 导出数据
	 * 
	 * @param param
	 *            要导出的数据：{nbase:人员库，a0100:人员编号，cardtime:打卡时间}
	 */
	@Override
	public String exportCardData(JSONArray param) {
		String fileName = this.userView.getUserName() +"_kqCard" +  ".xls";
		try {
			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(this.subModuleId);
			String sql = catche.getTableSql();
			String orderBySql = catche.getSortSql();
			String querySql = catche.getQuerySql();
			if (StringUtils.isNotEmpty(querySql)) {
				// 58679  在有条件查询时，拼接条件SQL会带有myGridData.比较字段，该字段是由GetTableDataTrans表格控件固定的标识而来，但是导出时并不需要，故替换为空
				sql += querySql.replace("myGridData.", "");
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
				
				String nbase = jsObject.getString("nbase");
				nbase = PubFunc.decrypt(nbase);
				String a0100 = jsObject.getString("a0100");
				a0100 = PubFunc.decrypt(a0100);
				String cardtime = jsObject.getString("cardtime");
				personWhere.append("(cardatas.nbase='" + nbase + "' and cardatas.a0100='" + a0100 + "'");
				personWhere.append(" and (" + joinDateFieldSql("cardatas.work_date", "cardatas.work_time") + ")='" + cardtime + "')");
			}
			
			if(StringUtils.isNotEmpty(personWhere.toString())) {
				sql += " and (" + personWhere + ")";
			}
			// 60012 选人导出需拼接到最后
			sql += " " + orderBySql;
			
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
			ArrayList<LazyDynaBean> mergedCellList = getExcleMergedList(catche.getDisplayColumns());
			ArrayList<LazyDynaBean> list = getHeadList(catche.getDisplayColumns(), mergedCellList, false, 0);
			int headStartRowNum = 0;
			if (!mergedCellList.isEmpty()) {
				headStartRowNum = 1;
			}
			
			excelUtil.exportExcelBySql(fileName, null, mergedCellList, list, sql, null, headStartRowNum);
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return fileName;
	}

	/**
	 * 生成拼接两个指标的sql片段
	 * 
	 * @param firstField
	 *            第一个指标
	 * @param secondField
	 *            第二个指标
	 * @return
	 */
	private String joinDateFieldSql(String firstField, String secondField) {
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(Sql_switcher.trim(firstField));
			sql.append(Sql_switcher.concat());
			sql.append("' '");
			sql.append(Sql_switcher.concat());
			sql.append(Sql_switcher.trim(secondField));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();
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
				if ("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype() 
						|| "nbase".equalsIgnoreCase(itemid)) {
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
				if ("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype() 
						|| "nbase".equalsIgnoreCase(itemid)) {
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
	 * 导出模板
	 * 
	 * @param importType
	 *            模板类型 =1：单列；=2多列
	 */
	@Override
	public String exportCardTemplate(String importType) {
		String fileName = this.userView.getUserName() + "_kqCard_Template" + ".xls";
		RowSet rs = null;
		HSSFWorkbook wb = new HSSFWorkbook(); 
		FileOutputStream fileOut = null;
		try {
			ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
			LazyDynaBean bean = new LazyDynaBean();
			if("2".equals(importType)) {
				bean.set("itemid", "cardDate");
				bean.set("content", ResourceFactory.getProperty("kq.card.cardDate") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "D");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "e0122");
				bean.set("content", ResourceFactory.getProperty("kq.card.e0122"));
				bean.set("colType", "A");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "a0101");
				bean.set("content", ResourceFactory.getProperty("kq.card.a0101"));
				bean.set("colType", "A");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "cardNo");
				bean.set("content", ResourceFactory.getProperty("kq.card.card_no") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "A");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "startTime");
				bean.set("content", ResourceFactory.getProperty("kq.card.startTime") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "A");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("itemid", "startLocation");
				bean.set("content", ResourceFactory.getProperty("kq.card.startLocation"));
				bean.set("colType", "A");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "endTime");
				bean.set("comment", "endTime");
				bean.set("content", ResourceFactory.getProperty("kq.card.endTime") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "A");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("itemid", "endLocation");
				bean.set("content", ResourceFactory.getProperty("kq.card.endLocation"));
				bean.set("colType", "A");
				list.add(bean);
			} else {
				bean.set("itemid", "a0101");
				bean.set("content", ResourceFactory.getProperty("kq.card.a0101"));
				bean.set("colType", "A");
				list.add(bean);

				bean = new LazyDynaBean();
				bean.set("itemid", "cardNo");
				bean.set("content", ResourceFactory.getProperty("kq.card.card_no") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "A");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("itemid", "cardDate");
				bean.set("content", ResourceFactory.getProperty("kq.card.cardDate") 
                        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "D");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("itemid", "workTime");
				bean.set("content", ResourceFactory.getProperty("kq.card.cardtime") 
				        + ResourceFactory.getProperty("kq.card.mustFillCause"));
				bean.set("colType", "A");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("itemid", "location");
				bean.set("content", ResourceFactory.getProperty("kq.card.workLocation"));
				bean.set("colType", "A");
				list.add(bean);
			}
			
			Sheet sheet = wb.createSheet();
			Cell cell = null;
			Row row = sheet.createRow(0); 
			row.setHeight((short) 600);
			HSSFComment comm = null;
			HSSFPatriarch patr = (HSSFPatriarch) sheet.createDrawingPatriarch();
			HSSFDataFormat df = wb.createDataFormat();
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 10);
			HSSFCellStyle style = wb.createCellStyle();
			style.setFont(font);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setWrapText(true);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setBottomBorderColor((short) 8);
			style.setLeftBorderColor((short) 8);
			style.setRightBorderColor((short) 8);
			style.setTopBorderColor((short) 8);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			
			HSSFCellStyle styleA = wb.createCellStyle();
			styleA.setWrapText(true);
			styleA.setBorderBottom(BorderStyle.THIN);
			styleA.setBorderLeft(BorderStyle.THIN);
			styleA.setBorderRight(BorderStyle.THIN);
			styleA.setBorderTop(BorderStyle.THIN);
			styleA.setBottomBorderColor((short) 8);
			styleA.setLeftBorderColor((short) 8);
			styleA.setRightBorderColor((short) 8);
			styleA.setTopBorderColor((short) 8);
			styleA.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			
			HSSFCellStyle styleD = wb.createCellStyle();
			styleD.setWrapText(true);
			styleD.setBorderBottom(BorderStyle.THIN);
			styleD.setBorderLeft(BorderStyle.THIN);
			styleD.setBorderRight(BorderStyle.THIN);
			styleD.setBorderTop(BorderStyle.THIN);
			styleD.setBottomBorderColor((short) 8);
			styleD.setLeftBorderColor((short) 8);
			styleD.setRightBorderColor((short) 8);
			styleD.setTopBorderColor((short) 8);
			styleD.setAlignment(HorizontalAlignment.LEFT);
			styleD.setDataFormat(df.getFormat("yyyy.MM.dd"));
			
			HSSFCellStyle styleT = wb.createCellStyle();
			styleT.setWrapText(true);
			styleT.setBorderBottom(BorderStyle.THIN);
			styleT.setBorderLeft(BorderStyle.THIN);
			styleT.setBorderRight(BorderStyle.THIN);
			styleT.setBorderTop(BorderStyle.THIN);
			styleT.setBottomBorderColor((short) 8);
			styleT.setLeftBorderColor((short) 8);
			styleT.setRightBorderColor((short) 8);
			styleT.setTopBorderColor((short) 8);
			styleT.setAlignment(HorizontalAlignment.RIGHT);
			styleT.setDataFormat(df.getFormat("HH:mm"));
			
			HSSFCellStyle styleN = wb.createCellStyle();
			styleN.setWrapText(true);
			styleN.setBorderBottom(BorderStyle.THIN);
			styleN.setBorderLeft(BorderStyle.THIN);
			styleN.setBorderRight(BorderStyle.THIN);
			styleN.setBorderTop(BorderStyle.THIN);
			styleN.setBottomBorderColor((short) 8);
			styleN.setLeftBorderColor((short) 8);
			styleN.setRightBorderColor((short) 8);
			styleN.setTopBorderColor((short) 8);
			styleN.setDataFormat(df.getFormat("@"));
			
			for (int i = 0; i < list.size(); i++) {
				bean = list.get(i);
				String itemid = (String) bean.get("itemid");
				String fieldLabel = (String) bean.get("content");
				cell=row.getCell(i);
				if(cell==null) {
					cell=row.createCell(i);
				}
				
				cell.setCellValue(new HSSFRichTextString(fieldLabel));
				cell.setCellStyle(style);
				if("cardDate".equalsIgnoreCase(itemid)) {
					sheet.setDefaultColumnStyle(i, styleD);
				} // 58799 增加单列模板中 打卡时间workTime 校验
				else if("workTime".equalsIgnoreCase(itemid) || "startTime".equalsIgnoreCase(itemid) || "endTime".equalsIgnoreCase(itemid)) {
					sheet.setDefaultColumnStyle(i, styleT);
				} else if("cardNo".equalsIgnoreCase(itemid)) {
					sheet.setDefaultColumnStyle(i, styleN);
				} else {
					sheet.setDefaultColumnStyle(i, styleA);
				}
				
				sheet.setColumnWidth(i, 5000);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
				comm.setString(new HSSFRichTextString(itemid));
				cell.setCellComment(comm);
			}
			

			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
			wb.write(fileOut);
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}

		return fileName;
	}

	/**
	 * 导入模板
	 * 
	 * @param param
	 *            要导出的数据：{path:模板路径，filename:文件名称}
	 */
	@Override
	public void importCardTemplate(JSONObject param) {
		try {
			// 上传组件 vfs改造
			String fileid = param.getString("fileid");
			this.userView.getHm().remove("errorMsg");
			ArrayList<Object> cardList = readExcel(fileid);
			if(cardList != null && cardList.size() > 0) {
				checkCardData(cardList);
			}
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 解析模板数据
	 * 
	 * @param fileName
	 *            导入的文件路径
	 * @return
	 */
	private ArrayList<Object> readExcel(String fileid) {
		ArrayList<Object> cardList = new ArrayList<Object>();
		StringBuffer errorMsg = new StringBuffer();
		InputStream stream = null;
		Workbook wb = null;
		try {
			stream = VfsService.getFile(fileid);
			wb = WorkbookFactory.create(stream);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			if (row == null) {
				errorMsg.append(ResourceFactory.getProperty("kq.card.sysTemplate"));
				return cardList;
			}

			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			HashMap<String, String> colsMap = new HashMap<String, String>();
			HashMap<String, Integer> colsIndexMap = new HashMap<String, Integer>();
			ArrayList<String> keyList = new ArrayList<String>();
			for (int i = 0; i < cols; i++) {
				Cell cell = row.getCell(i);
				if (cell == null) {
					continue;
				}
				
				Comment myComment = cell.getCellComment();
				if (myComment == null) {
					continue;
				}
				
				keyList.add(myComment.getString().toString().toLowerCase());
				colsMap.put(myComment.getString().toString().toLowerCase(), cell.getStringCellValue());
				colsIndexMap.put(myComment.getString().toString().toLowerCase(), i);
			}
			
			if(keyList == null || keyList.size() < 1) {
				errorMsg.append(ResourceFactory.getProperty("kq.card.sysTemplate"));
				return null;
			}
			
			cardList.add(keyList);
			ArrayList<HashMap<String, String>> valueList = new ArrayList<HashMap<String, String>>();
			for (int m = 1; m < rows; m++) {
				row = sheet.getRow(m);
				if (row == null) {
					continue;
				}
				
				String cardNo = "";
				String workDate = "";
				String dateTmp = "";
				HashMap<String, String> valueMap = new HashMap<String, String>();
				for (int i = 0; i < keyList.size(); i++) {
					String itemid = keyList.get(i);
					String value = "";
					int index = colsIndexMap.get(itemid);
					Cell cell = row.getCell(index);
					if (cell == null) {
						continue;
					}
					
					switch (cell.getCellTypeEnum()) {
					case FORMULA:
						value = cell.getStringCellValue().trim();
						break;
					case NUMERIC:
						if(",carddate,starttime,endtime,worktime,".indexOf("," + itemid.toLowerCase() + ",") < 0) {
							ArrayList<String> sb = this.msgMap.get(cardNo);
							if (sb == null) {
								sb = new ArrayList<String>();
							}
							
							String msg = "";
							msg = ResourceFactory.getProperty("kq.card.import.charError");
							msg = msg.replace("{0}", (m + 1) + "").replace("{1}", colsMap.get(itemid));
							sb.add((sb.size() + 1) + ".&nbsp;" + msg);
							this.msgMap.put(cardNo, sb);
						}
						double y = cell.getNumericCellValue();
						value = Double.toString(y);
						if ("carddate".equalsIgnoreCase(itemid)) {
							value = DateUtils.format(cell.getDateCellValue(), "yyyy.MM.dd").trim();
						}// 58849 单列模板中 worktime 同样是时间 需要校验
						else if ("starttime".equalsIgnoreCase(itemid) || "endtime".equalsIgnoreCase(itemid)
								|| "worktime".equalsIgnoreCase(itemid)) {
							value = DateUtils.format(cell.getDateCellValue(), "HH:mm").trim();
						}
						
						break;
					case STRING:
						value = cell.getStringCellValue().trim();
						break;
					default:
						value = cell.getStringCellValue().trim();
					}

					if (("carddate".equalsIgnoreCase(itemid) || "starttime".equalsIgnoreCase(itemid)
					        || "endtime".equalsIgnoreCase(itemid) || "worktime".equalsIgnoreCase(itemid)) 
							&& StringUtils.isNotEmpty(value)) {
						if("carddate".equalsIgnoreCase(itemid)) {
							dateTmp = value;
							value = checkdate(value);
							value = "false".equalsIgnoreCase(value) ? "" : value;
						} else if("starttime".equalsIgnoreCase(itemid) || "endtime".equalsIgnoreCase(itemid)
								|| "worktime".equalsIgnoreCase(itemid)) {
							String valueTmp = value;
							String[] valueTemps = value.split(":");
							if(valueTemps.length > 2) {
								value = valueTemps[0] + ":" + valueTemps[1];
							}
							
							if(valueTemps.length < 2) {
							    value = "false";
							} else {
							    value = checkdate(value);
							}
							
							if("false".equalsIgnoreCase(value)) {
								ArrayList<String> sb = this.msgMap.get(cardNo);
								if (sb == null) {
									sb = new ArrayList<String>();
								}
								
								String msg = ResourceFactory.getProperty("kq.card.import.dateError");;
								msg = msg.replace("{0}", (m + 1) + "").replace("{1}", colsMap.get(itemid)).replace("{2}", valueTmp);
								sb.add((sb.size() + 1) + ".&nbsp;" + msg);
								this.msgMap.put(cardNo, sb);
								value = "";
							}
						}
						
					}
					
					if ("cardDate".equalsIgnoreCase(itemid)) {
						workDate = value;
					} else if ("cardNo".equalsIgnoreCase(itemid)) {
						cardNo = value;
					}
					
					valueMap.put(itemid, value);
				}

				if(StringUtils.isEmpty(workDate) || StringUtils.isEmpty(cardNo)) {
					if(StringUtils.isEmpty(workDate) && StringUtils.isEmpty(cardNo)) {
						continue;
					}
					
					if(StringUtils.isEmpty(workDate)) {
						ArrayList<String> sb = this.msgMap.get(cardNo);
						if (sb == null) {
							sb = new ArrayList<String>();
						}
						
						String msg = "";
						if(StringUtils.isEmpty(dateTmp)) {
							msg = ResourceFactory.getProperty("kq.card.import.fieldEmpty");
							msg = msg.replace("{0}", (m + 1) + "").replace("{1}", colsMap.get("carddate"));
						} else {
							msg = ResourceFactory.getProperty("kq.card.import.dateError");
							msg = msg.replace("{0}", (m + 1) + "").replace("{1}", colsMap.get("carddate")).replace("{2}", dateTmp);
						}
						
						sb.add((sb.size() + 1) + ".&nbsp;" + msg);
						this.msgMap.put(cardNo, sb);
					}
					
					if(StringUtils.isEmpty(cardNo)) {
						ArrayList<String> sb = this.msgMap.get(cardNo);
						if (sb == null) {
							sb = new ArrayList<String>();
						}
						
						String msg = ResourceFactory.getProperty("kq.card.import.fieldEmpty");
						msg = msg.replace("{0}", (m + 1) + "").replace("{1}", colsMap.get("cardno"));
						sb.add((sb.size() + 1) + ".&nbsp;" + msg);
						this.msgMap.put(cardNo, sb);
					}
					
					continue;
				}

				valueList.add(valueMap);
			}

			cardList.add(valueList);
		} catch (Exception e) {
			errorMsg.setLength(0);
			errorMsg.append(e.toString());
			e.printStackTrace();
		} finally {
			this.userView.getHm().put("errorMsg", errorMsg.toString());
			PubFunc.closeIoResource(stream);
			PubFunc.closeIoResource(wb);
		}

		return cardList;
	}

	/**
	 * 校验数据
	 * 
	 * @param cardList
	 *            模板中的数据
	 */
	private void checkCardData(ArrayList<Object> cardList) {
		RowSet rs = null;
		try {
			KqPrivForHospitalUtil kq = new KqPrivForHospitalUtil(this.userView, this.conn);
			ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			String cardItem = kq.getKqCard_no();
			if (dbNameList == null || dbNameList.size() < 1) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.card.setNbase"), "", "");
			}
			
			if (StringUtils.isEmpty(cardItem)) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.card.setCardNo"), "", "");
			}
			
			boolean isMoreColumns = false;
			ArrayList<String> keyList = (ArrayList<String>) cardList.get(0);
			if (keyList.contains("startlocation")) {
				isMoreColumns = true;
			}
			
			ArrayList<HashMap<String, String>> valueList = (ArrayList<HashMap<String, String>>) cardList.get(1);
			ArrayList<String> cardNoList = new ArrayList<String>();
			for (HashMap<String, String> map : valueList) {
				String cardNo = map.get("cardno");
				if (!cardNoList.contains(cardNo)) {
					cardNoList.add(cardNo);
				}
			}

			String cardNos = "";
			ArrayList<String> paramList = new ArrayList<String>();
			int sum = 0;
			for (String cardNo : cardNoList) {
				cardNo = PubFunc.keyWord_filter(cardNo);
				cardNos += cardNo + "','";
				sum++;

				if (sum >= 1000) {
					cardNos = cardNos.substring(0, cardNos.length() - 3);
					paramList.add(cardNos);
					cardNos = "";
					sum = 0;
				}

			}

			if (StringUtils.isNotEmpty(cardNos)) {
				cardNos = cardNos.substring(0, cardNos.length() - 3);
				paramList.add(cardNos);
			}

			StringBuffer a01Sql = new StringBuffer();
			for (String nbase : dbNameList) {
				if (a01Sql.length() > 0) {
					a01Sql.append(" union all ");
				}
				
				a01Sql.append("select '" + nbase + "' nbase,b0110,e0122,e01a1,a0100,a0101," + cardItem + " from "
				        + nbase + "a01");
			}

			StringBuffer sql = new StringBuffer();
			sql.append("select nbase,b0110,e0122,e01a1,a0100,a0101," + cardItem + " from (");
			sql.append(a01Sql);
			sql.append(") A01 where " + cardItem);
			sql.append(" in ('##')");
			String where = getPrivWhere();
			sql.append(" and " + where);
			ArrayList<HashMap<String, String>> cardDataList = new ArrayList<HashMap<String, String>>();
			ContentDAO dao = new ContentDAO(this.conn);
			for (String cardNumbers : paramList) {
				rs = dao.search(sql.toString().replace("##", cardNumbers));
				while (rs.next()) {
					String cardNum = rs.getString(cardItem);
					String nbase = rs.getString("nbase");
					String b0110 = rs.getString("b0110");
					String e0122 = rs.getString("e0122");
					String e01a1 = rs.getString("e01a1");
					String a0100 = rs.getString("a0100");
					String a0101 = rs.getString("a0101");
					a0101 = StringUtils.isEmpty(a0101) ? "" : a0101;
					
					HashMap<String, String> tempMap = new HashMap<String, String>();
					for (int i = valueList.size(); i > 0; i--) {
						HashMap<String, String> map = valueList.get(i - 1);
						String cardNo = map.get("cardno");
						if (!cardNum.equalsIgnoreCase(cardNo))
							continue;

						String workDate = map.get("carddate");
						if (isMoreColumns) {
							HashMap<String, String> sCardDataMap = new HashMap<String, String>();
							HashMap<String, String> eCardDataMap = new HashMap<String, String>();
							sCardDataMap.put("carNo", cardNum);
							sCardDataMap.put("nbase", nbase);
							sCardDataMap.put("b0110", b0110);
							sCardDataMap.put("e0122", e0122);
							sCardDataMap.put("e01a1", e01a1);
							sCardDataMap.put("a0100", a0100);
							sCardDataMap.put("a0101", a0101);
							sCardDataMap.put("workDate", workDate);
							sCardDataMap.put("workTime", map.get("starttime"));
							sCardDataMap.put("location", map.get("startlocation"));
							sCardDataMap.put("inout_flag", "1");
							if(StringUtils.isNotBlank(map.get("starttime"))) {
								if (!tempMap.containsKey(cardNo + "=" + workDate + " " + map.get("starttime"))) {
									cardDataList.add(sCardDataMap);
									tempMap.put(cardNo + "=" + workDate + " " + map.get("starttime"), "1");
								}
							}

							eCardDataMap.put("carNo", cardNum);
							eCardDataMap.put("nbase", nbase);
							eCardDataMap.put("b0110", b0110);
							eCardDataMap.put("e0122", e0122);
							eCardDataMap.put("e01a1", e01a1);
							eCardDataMap.put("a0100", a0100);
							eCardDataMap.put("a0101", a0101);
							eCardDataMap.put("workDate", workDate);
							eCardDataMap.put("workTime", map.get("endtime"));
							eCardDataMap.put("location", map.get("endlocation"));
							eCardDataMap.put("inout_flag", "-1");
							if(StringUtils.isNotBlank(map.get("endtime"))) {
								if (!tempMap.containsKey(cardNo + "=" + workDate + " " + map.get("endtime"))) {
									cardDataList.add(eCardDataMap);
									tempMap.put(cardNo + "=" + workDate + " " + map.get("endtime"), "1");
								}
							}
						} else {
							HashMap<String, String> cardDataMap = new HashMap<String, String>();
							cardDataMap.put("carNo", cardNum);
							cardDataMap.put("nbase", nbase);
							cardDataMap.put("b0110", b0110);
							cardDataMap.put("e0122", e0122);
							cardDataMap.put("e01a1", e01a1);
							cardDataMap.put("a0100", a0100);
							cardDataMap.put("a0101", a0101);
							cardDataMap.put("workDate", workDate);
							cardDataMap.put("workTime", map.get("worktime"));
							cardDataMap.put("location", map.get("location"));
							cardDataMap.put("inout_flag", "0");
							if(StringUtils.isNotBlank(map.get("worktime"))) {
								if (!tempMap.containsKey(cardNo + "=" + workDate + " " + map.get("worktime"))) {
									cardDataList.add(cardDataMap);
									tempMap.put(cardNo + "=" + workDate + " " + map.get("worktime"), "1");
								}
							}
						}

						valueList.remove(map);
					}
				}
			}
			// 检验权限外的人员
			for (HashMap<String, String> map : valueList) {
				String cardNo = map.get("cardno");
				String a0101 = map.get("a0101");
				a0101 = StringUtils.isEmpty(a0101) ? "" : a0101.replace("'", "\\'");
				
				ArrayList<String> sb = this.msgMap.get(cardNo);
				if (sb == null)
					sb = new ArrayList<String>();
				
				sb.add((sb.size() + 1) + ".&nbsp;" + a0101 + ResourceFactory.getProperty("kq.card.cardNoError"));
				this.msgMap.put(cardNo, sb);
			}
			
			sql.setLength(0);
			sql.append("select card_no,work_date,work_time from kq_originality_data");
			sql.append(" where " + where);
			ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
			rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String cardNo = rs.getString("card_no");
				String workDate = rs.getString("work_date");
				String workTime = rs.getString("work_time");
				map.put("cardNo", cardNo);
				map.put("workDate", workDate);
				map.put("workTime", workTime);
				dataList.add(map);
			}
			
			for (HashMap<String, String> map : cardDataList) {
				String cardNo = map.get("carNo");
				String workDate = map.get("workDate");
				String workTime = map.get("workTime");
				for (HashMap<String, String> valueMap : dataList) {
					String dataCardNo = valueMap.get("cardNo");
					String dataWorkDate = valueMap.get("workDate");
					String dataWorkTime = valueMap.get("workTime");
					if (dataCardNo.equalsIgnoreCase(cardNo) && dataWorkDate.equalsIgnoreCase(workDate)
							&& dataWorkTime.equalsIgnoreCase(workTime)) {
						map.put("updateFlag", "1");
						break;
					}
				}
			}
			
			this.userView.getHm().put("cardData", cardDataList);
		} catch (Exception e) {
			e.printStackTrace();
			this.userView.getHm().put("errorMsg", e.toString());
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 获取模板数据的异常信息
	 */
	@Override
	public String getErrorMsg() {
		StringBuffer errorMsg = new StringBuffer();
		try {
			Iterator<Entry<String, ArrayList<String>>> it = this.msgMap.entrySet().iterator();
			while (it.hasNext()) {
				if(errorMsg.length() < 1)
					errorMsg.append("[");
				
				Entry<String, ArrayList<String>> entry = it.next();
				String cardNo = entry.getKey();
				ArrayList<String> msgList = entry.getValue();
				errorMsg.append("{cardNo:'" + cardNo + "',message:'");
				for (int i = 0; i < msgList.size(); i++) {
					String msg = msgList.get(i);
					if (i > 0)
						errorMsg.append("<br>");

					errorMsg.append(msg);
				}

				errorMsg.append("'},");
			}

			if (errorMsg.toString().endsWith(","))
				errorMsg.setLength(errorMsg.length() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if(errorMsg.length() > 1)
			errorMsg.append("]");
		
		return errorMsg.toString();
	}

	/**
	 * 保存导入的数据
	 */
	@Override
	public int saveCardData() {
		int sum = 0;
		try {
			ArrayList<HashMap<String, String>> cardDataList = (ArrayList<HashMap<String, String>>) this.userView.getHm().get("cardData");
			ArrayList<ArrayList<String>> updateParamList = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> insertParamList = new ArrayList<ArrayList<String>>();
			for (HashMap<String, String> map : cardDataList) {
				if ("1".equalsIgnoreCase(map.get("updateFlag"))) {
					ArrayList<String> updateValueList = new ArrayList<String>();
					updateValueList.add(map.get("nbase"));
					updateValueList.add(map.get("b0110"));
					updateValueList.add(map.get("e0122"));
					updateValueList.add(map.get("e01a1"));
					updateValueList.add(map.get("a0100"));
					updateValueList.add(map.get("a0101"));
					updateValueList.add(map.get("location"));
					updateValueList.add(map.get("inout_flag"));
					updateValueList.add("03");
					updateValueList.add(map.get("carNo"));
					updateValueList.add(map.get("workDate"));
					updateValueList.add(map.get("workTime"));
					updateParamList.add(updateValueList);
				} else {
					ArrayList<String> insertValueList = new ArrayList<String>();
					insertValueList.add(map.get("nbase"));
					insertValueList.add(map.get("b0110"));
					insertValueList.add(map.get("e0122"));
					insertValueList.add(map.get("e01a1"));
					insertValueList.add(map.get("a0100"));
					insertValueList.add(map.get("a0101"));
					insertValueList.add(map.get("carNo"));
					insertValueList.add(map.get("location"));
					insertValueList.add(map.get("inout_flag"));
					insertValueList.add(map.get("workDate"));
					insertValueList.add(map.get("workTime"));
					insertValueList.add("03");
					insertParamList.add(insertValueList);
				}
			}

			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update kq_originality_data set ");
			updateSql.append("nbase=?,b0110=?,e0122=?,e01a1=?,a0100=?,a0101=?,");
			updateSql.append("location=?,inout_flag=?,sp_flag=?");
			updateSql.append(" where card_no=? and work_date=? and work_time=?");

			StringBuffer insertSql = new StringBuffer();
			insertSql.append("insert into  kq_originality_data");
			insertSql.append(" (nbase,b0110,e0122,e01a1,a0100,a0101,card_no,");
			insertSql.append("location,inout_flag,work_date,work_time,sp_flag)");
			insertSql.append(" values (?,?,?,?,?,?,?,?,?,?,?,?)");
			ContentDAO dao = new ContentDAO(this.conn);
			if (updateParamList != null && updateParamList.size() > 0) {
				dao.batchUpdate(updateSql.toString(), updateParamList);
				sum = sum + updateParamList.size();
			}

			if (insertParamList != null && insertParamList.size() > 0) {
				dao.batchInsert(insertSql.toString(), insertParamList);
				sum = sum + insertParamList.size();
			}

		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}

		return sum;
	}

	/**
	 * 校验日期数据是否是有效的数据
	 * 
	 * @param str
	 *            需要校验的日期数据
	 * @return
	 */
	private String checkdate(String str) {
		str = StringUtils.isEmpty(str) ? "" : str.replace("/", ".").replace("-", ".");
		if (str.indexOf("日") > -1)
			str = str.replace(" ", "");

		String dateStr = "false";
		if (str.length() < 4 && str.indexOf(":") < 0)
			dateStr = "false";
		else if (str.length() == 4 && str.indexOf(":") < 0) {
			Pattern p = Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str + ".01.01";
			else
				dateStr = "false";
		} else if (str.length() < 6 && str.indexOf(":") < 0) {
			Pattern p = Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str.replace("年", ".") + "01.01";
			else
				dateStr = "false";
		} else if (str.length() == 7 && str.indexOf(":") < 0) {
			if (str.indexOf("月") != -1) {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					if (str.indexOf("月") != -1)
						dateStr = str.replace("年", ".").replace("-", ".").replace("月", ".") + "01";
					else
						dateStr = str.replace("年", ".").replace("-", ".") + ".01";
				} else
					dateStr = "false";
			} else {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if (m.matches())
					dateStr = str.replace("年", ".").replace("-", ".") + ".01";
				else
					dateStr = "false";
			}
		} else if (str.length() < 8 && str.indexOf(":") < 0) {// 2010年3 2010年3月
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				if (str.indexOf("月") != -1)
					dateStr = str.replace("年", ".").replace(".", ".").replace("月", ".") + "01";
				else
					dateStr = str.replace("年", ".").replace(".", ".") + ".01";
			} else
				dateStr = "false";
		} else if (str.length() == 8 && str.indexOf(":") < 0) {// 2010年3 2010年3月1
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				str = str.replace("年", ".").replace("-", ".").replace("月", ".");
				if (str.lastIndexOf("-") == str.length()) {
					if (str.length() < 10)
						dateStr = str + "01";
				} else {
					String[] temps = str.split("[.]");
					if (temps.length > 2)
						dateStr = checkMothAndDay(str);
					else
						dateStr = "false";
				}
			} else {
				dateStr = "false";
			}
		} else if (str.length() > 8 && str.length() <= 11) {// 2017年1月1日
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				String temp = str.replace("年", ".").replace(".", ".").replace("月", ".").replace("日", "");
				dateStr = checkMothAndDay(temp);
			} else
				dateStr = "false";

		} else {// 2017年1月1日1时1分 2017年1月1日1时1分1秒
			str = str.replace("时", ":").replace("分", ":");
			if (str.endsWith(":"))
				str = str.substring(0, str.length() - 1);

			Pattern p = null;
			// 58688 正则校验 错误 不需要连接符*
			if(str.split(" ").length > 1 || str.split("日").length > 1) {
				if (str.split(":").length < 3)
					p = Pattern.compile("^(\\d{4})[-.年]([0]\\d{1}|1[0-2])[-.月]([0]\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]\\d{1}|2[0-3])[:时]([0-5]\\d{1})[:分]*$");
				else
					p = Pattern.compile("^(\\d{4})[-.年]([0]\\d{1}|1[0-2])[-.月]([0]\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]\\d{1}|2[0-3])[:时]([0-5]\\d{1})[:分]([0-5]\\d{1})[秒]*$");
			} else {
				if (str.split(":").length < 3)
					p = Pattern.compile("^([01]\\d{1}|2[0-3])[:时]([0-5]\\d{1})[:分]*$");
				else
					p = Pattern.compile("^([01]\\d{1}|2[0-3])[:时]([0-5]\\d{1})[:分]([0-5]\\d{1})[秒]*$");
			}
				

			Matcher m = p.matcher(str);
			if (m.matches()) {
				String tempDate = str.replace("年", ".").replace("-", ".").replace("月", ".").replace("日", " ");
				if(tempDate.split(" ").length > 1) {
					String temp = tempDate.split(" ")[0];
					dateStr = checkMothAndDay(temp);
					if (!"false".equalsIgnoreCase(dateStr)) {
						String tempTime = tempDate.split(" ")[1];
						dateStr += " " + tempTime;
					}
				} else
					dateStr = tempDate;
			} else
				dateStr = "false";
		}

		if (!"false".equals(dateStr))
			dateStr = formatDate(dateStr);

		return dateStr;
	}

	/**
	 * 校验月与日是否符合规则
	 * 
	 * @param date
	 *            日期数据
	 * @return
	 */
	private String checkMothAndDay(String date) {
		String tempDate = "false";
		String[] dates = date.split("[.]");
		if (dates[0].length() > 0 && dates[1].length() > 0 && dates[2].length() > 0) {
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12: {
				if (1 <= day && day <= 31)
					tempDate = date;

				break;
			}
			case 4:
			case 6:
			case 9:
			case 11: {
				if (1 <= day && day <= 30)
					tempDate = date;

				break;
			}
			case 2: {
				if (isLeapYear(year)) {
					if (1 <= day && day <= 29)
						tempDate = date;

				} else {
					if (1 <= day && day <= 28)
						tempDate = date;
				}
				break;
			}
			}
		}
		return tempDate;
	}

	/**
	 * 闰年的条件是： ① 能被4整除，但不能被100整除； ② 能被100整除，又能被400整除。
	 * 
	 * @param year
	 *            年份
	 * @return
	 */
	private boolean isLeapYear(int year) {
		boolean flag = false;
		if (year % 4 == 0) {
			if (year % 100 != 0) {
				flag = true;
			} else if (year % 400 == 0) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 将日期数据1900.1.1或 1:1:1转换成1900.01.01或 01:01:01
	 * 
	 * @param date
	 *            校验完成的数据
	 * @return
	 */
	private String formatDate(String date) {
		String newDate = "";
		if (date.indexOf(".") > -1) {
			String year = date.split("[.]")[0];
			String month = date.split("[.]")[1];
			if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12)
				return newDate;

			month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month : month;
			String day = date.split("[.]")[2];
			day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day : day;
			if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12)
				return newDate;

			newDate = year + "." + month + "." + day;
		} else if (date.indexOf(":") > -1) {
			String[] oldTime = date.split(":");
			String hour = oldTime[0];
			hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour : hour;
			newDate += " " + hour;
			if (oldTime.length > 1) {
				String min = oldTime[1];
				min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min : min;
				newDate += ":" + min;
			}

			if (oldTime.length > 2) {
				String second = oldTime[2];
				second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second : second;
				newDate += ":" + second;
			}
		}

		return newDate;
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
				dateTime = DateUtils.format(cal.getTime(), "yyyy.MM.dd HH:mm");
			} else if ("e".equalsIgnoreCase(flag)) {
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, lastDay);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				dateTime = DateUtils.format(cal.getTime(), "yyyy.MM.dd HH:mm");
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
	 * @throws GeneralException 
     */
    private String getPrivWhere() throws GeneralException {
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
            this.userView.getHm().put("errorMsg", e.toString());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
        
        return "(" + whereSql.toString() + ")";
    }
}

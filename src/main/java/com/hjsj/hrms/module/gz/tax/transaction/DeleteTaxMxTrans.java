package com.hjsj.hrms.module.gz.tax.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class DeleteTaxMxTrans extends IBusiness {
	//这些指标如果一个月多次只取最大的，不取合计
	private String notExistsFiled = ",a0101,a00z1,a00z3,a0100,b0110,e0122,taxmode,declare_tax,description,a00z2,a00z0,tax_date,ljse,sl,ljsde,sskcs,lj_basedata,basedata,";

	@Override
    public void execute() throws GeneralException {
		try {
			String tax_ids = (String) getFormHM().get("tax_ids");
			String salaryid = (String) getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String table = (String) getFormHM().get("tablename");
			String datetime = (String) getFormHM().get("datetime");
			String taxMode = (String) getFormHM().get("taxMode");// 计税方式选择，0：全部，1：工资薪金.....codesetid为46的
			// 申报明细标识
			String exportDetail = (String) getFormHM().get("exportDetail");
			// 申报汇总标识
			String exportCount = (String) getFormHM().get("exportCount");
			String exporttype = (String) getFormHM().get("exporttype");

			exporttype = ("".equals(exporttype)) || (exporttype == null) ? "0" : exporttype;
			TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView, salaryid);
			if ("true".equals(exportDetail)) {
				String exportType_detail = (String) getFormHM().get("exportType_detail");
				if ((!"1".equals(exporttype)) && (!"0".equals(exporttype))) {
					return;
				}
				String filename = "";
				if ("geshui".equalsIgnoreCase(exportType_detail)) {
					filename = exportdetail_new_new(PubFunc.decrypt(table), datetime, exporttype, taxMode);
				} else
					filename = taxbo.exportDetail(PubFunc.decrypt(table), datetime, exporttype, taxMode);
				getFormHM().put("filename", SafeCode.encode(PubFunc.encrypt(filename)));
			} else if ("true".equals(exportCount)) {
				if ("JEPWw5tnIio@3HJD@".equals(datetime)) {
					datetime = PubFunc.decrypt(datetime);
				}
				String filename = taxbo.exportCount(PubFunc.decrypt(table), datetime, salaryid, taxMode);
				getFormHM().put("filename", SafeCode.encode(PubFunc.encrypt(filename)));
			} else {
				taxbo.deleteData(Boolean.valueOf(false), tax_ids, PubFunc.decrypt(table), PubFunc.decrypt(datetime));
			}

			getFormHM().put("tablename", PubFunc.encrypt(taxbo.getTablename()));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public String exportdetail_new(String fromtable, String datetime, String salaryid, String taxMode) {
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		StringBuffer ss = new StringBuffer();
		ExportExcelUtil export = new ExportExcelUtil(this.frameconn, this.userView);
		String fileName = this.userView.getUserName() + "_gz.xls";
		String sheetName = "个人所得税扣缴申报表";
		ArrayList headList = new ArrayList();
		HashMap colStyleMap = new HashMap();
		HashMap map_ = new HashMap();
		LazyDynaBean bean = new LazyDynaBean();
		ArrayList<Field> fieldlist = getAllField();
		String firstRow = ",1,2,3,4,5,6,39,30,31,";
		String secondRow = ",10,22,32,33,34,35,36,37,38,";
		FileInputStream fis = null;
		HSSFWorkbook wb = null;
		try {
			StringBuffer ff = new StringBuffer();

			TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView, "");
			if ((StringUtils.isBlank(taxMode)) || ("0".equals(taxMode))) {
				String pre = taxbo.getPrivPre();

				if ("JEPWw5tnIio@3HJD@".equals(datetime)) {
					datetime = PubFunc.decrypt(datetime);
				}
				String timewhere = getFilterCond(datetime, taxMode);
				ff.append(" where (" + pre + ")");
				if ((timewhere != null) && (!"".equals(timewhere))) {
					ff.append(timewhere);
				}
				rs = dao.search("select taxmode from " + fromtable + ff.toString());
				while (rs.next()) {
					String tt = rs.getString("taxmode");
					if ((StringUtils.isBlank(ss.toString())) || ((ss + ",").indexOf("," + tt + ",") == -1)) {
						ss.append("," + rs.getString("taxmode"));
					}
				}
				taxMode = StringUtils.isBlank(ss.toString()) ? "0" : ss.substring(1);
			}
			String splitF = System.getProperty("file.separator");
			String url = System.getProperty("catalina.home") + splitF + "webapps" + splitF + "hrms" + splitF
					+ "templatefile" + splitF + "TaxDeclaration.xls";
			File file = new File(url);
			fis = new FileInputStream(file);
			wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			HashMap map1 = new HashMap();

			ArrayList list_code = new ArrayList();
			map1.putAll(getComment(sheet, 4));
			map1.putAll(getComment(sheet, 5));
			map1.putAll(getComment(sheet, 6));

			int rowFirst = 4;
			int rowSecond = 5;
			int rowThird = 6;

			HashMap map_other = (HashMap) map1.clone();
			Iterator iter = map1.entrySet().iterator();
			while (iter.hasNext()) {
				String comment = "";
				Map.Entry entry = (Map.Entry) iter.next();
				int key = ((Integer) entry.getKey()).intValue();
				String val = (String) entry.getValue();

				String value = val.split(",")[0];
				int toRow;
				if (StringUtils.isBlank(value)) {
					int fromRow = rowThird;
					toRow = rowThird;
					if (firstRow.indexOf("," + key + ",") != -1) {
						fromRow = rowFirst;
						toRow = rowThird;
					} else if (secondRow.indexOf("," + key + ",") != -1) {
						fromRow = rowSecond;
						toRow = rowThird;
					}
					map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], "", "", "A", fromRow, toRow, key, key, 3000, "0"));
					map_other.remove(Integer.valueOf(key));
				} else {
					if ((value.contains("+")) && (StringUtils.isNotBlank(value))) {
						int row_num = rowThird;
						if (firstRow.indexOf("," + key + ",") != -1)
							row_num = rowFirst;
						else if (secondRow.indexOf("," + key + ",") != -1) {
							row_num = rowSecond;
						}
						comment = value;
						map_.put(Integer.valueOf(key), getStyleList(val.split(",")[1], comment.replace("+", "_"),comment, "N", row_num, rowThird, key, key, 3000, "0"));
					}
					for (Field field : fieldlist) {
						if (val.split(",")[0].equalsIgnoreCase(field.getName())) {
							if (firstRow.indexOf("," + key + ",") != -1) {
								comment = val.split(",")[0].toUpperCase();
								map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], field.getName(), comment,
											taxbo.getvarType(field.getDatatype()), rowFirst, rowThird, key, key,3000, field.getCodesetid()));
							} else if (secondRow.indexOf("," + key + ",") != -1) {
								comment = val.split(",")[0].toUpperCase();
								map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], field.getName(), comment,
											taxbo.getvarType(field.getDatatype()), rowSecond, rowThird, key, key,3000, field.getCodesetid()));
							} else if ((24 <= key) && (key <= 39)) {
								comment = "∑" + val.split(",")[0].toUpperCase();
								map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], field.getName(), comment,
											taxbo.getvarType(field.getDatatype()), rowThird, rowThird, key, key,3000, field.getCodesetid()));
							} else {
								comment = val.split(",")[0].toUpperCase();
								map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], field.getName(), comment,
											taxbo.getvarType(field.getDatatype()), rowThird, rowThird, key, key,3000, field.getCodesetid()));
							}
						}
					}
					if (StringUtils.isBlank(comment)) {
						int fromRow = rowThird;
						toRow = rowThird;
						if (firstRow.indexOf("," + key + ",") != -1) {
							fromRow = rowFirst;
							toRow = rowThird;
						} else if (secondRow.indexOf("," + key + ",") != -1) {
							fromRow = rowSecond;
							toRow = rowThird;
						}
						map_.put(Integer.valueOf(key),getStyleList(val.split(",")[1], "", value, "A", fromRow, toRow, key, key, 3000, "0"));
						map_other.remove(Integer.valueOf(key));
					}
				}
			}
			HashMap hashMap = getExportSqlForDetail(fromtable, salaryid, datetime, "1", taxMode, map_other, fieldlist);
			StringBuffer sql = (StringBuffer) hashMap.get("sql");
			sql.append(" order by id asc ");

			map_.put(0, getStyleList("序号", "id", "", "", rowFirst, rowThird, 0, 0, 3000, "0"));
			map_.put(21,getStyleList("累计收入额", "income",hashMap.get("income_field") != null ? (String) hashMap.get("income_field") : "", "N",
							rowSecond, rowThird, 21, 21, 3000, "0"));
			map_.put(23,getStyleList("累计专项扣除", "special",hashMap.get("specialDed_field") != null ? (String) hashMap.get("specialDed_field") : "",
							"N", rowSecond, rowThird, 23, 23, 3000, "0"));
			map_.put(29,getStyleList("累计其他扣除", "other",hashMap.get("otherDed_field") != null ? (String) hashMap.get("otherDed_field") : "", "N",
							rowThird, rowThird, 29, 29, 3000, "0"));
			map_.put(38,getStyleList("应补（退）税额", "YBSE", hashMap.get("ybse") != null ? (String) hashMap.get("ybse") : "",
							"N", rowSecond, rowThird, 38, 38, 3000, "0"));

			if (map_.get(1) == null)
				map_.put(1, getStyleList("姓名", "A0101", "A0101", "A", rowFirst, rowThird, 1, 1, 3000, "0"));
			if (map_.get(6) == null)
				map_.put(6, getStyleList("所得项目", "TaxMode", "TaxMode", "A", rowFirst, rowThird, 6, 6, 3000, "0"));
			if (map_.get(22) == null)
				map_.put(22, getStyleList("累计减除费用", "lj_basedata", "lj_basedata", "N", rowSecond, rowThird, 22, 22,3000, "0"));
			if (map_.get(32) == null)
				map_.put(32, getStyleList("应纳税所得额", "LJSDE", "LJSDE", "N", rowSecond, rowThird, 32, 32, 3000, "0"));
			if (map_.get(33) == null)
				map_.put(33, getStyleList("税率/预扣率", "SL", "SL", "N", rowSecond, rowThird, 33, 33, 3000, "0"));
			if (map_.get(34) == null)
				map_.put(34, getStyleList("速算扣除数", "Sskcs", "Sskcs", "N", rowSecond, rowThird, 34, 34, 3000, "0"));
			if (map_.get(35) == null)
				map_.put(35, getStyleList("应纳税额", "LJSE", "LJSE", "N", rowSecond, rowThird, 35, 35, 3000, "0"));
			if (map_.get(37) == null)
				map_.put(37, getStyleList("已扣缴税额", "YJSE", "∑SDS", "N", rowSecond, rowThird, 37, 37, 3000, "0"));
			int size = map_.size();

			// 排序
			for (int i = 0; i < size; i++) {
				if (map_.get(Integer.valueOf(i)) != null)
					headList.addAll((ArrayList) map_.get(Integer.valueOf(i)));
			}
			HashMap dataMap = getExportData(headList, sql.toString());
			ArrayList dataList = (ArrayList) dataMap.get("dataList");
			HashMap sum_count = (HashMap) dataMap.get("sum_count");
			ArrayList mergedCellList = new ArrayList();
			HashMap font = new HashMap();

			mergedCellList.addAll(mergedCellStyleMap("本月（次）情况", rowFirst, rowFirst, 7, 20, 42000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("收入额计算", rowSecond, rowSecond, 7, 9, 9000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("专项扣除", rowSecond, rowSecond, 11, 14, 12000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("其他扣除", rowSecond, rowSecond, 15, 20, 18000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("累计情况（工资、薪金）", rowFirst, rowFirst, 21, 29, 27000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("累计专项附加扣除", rowSecond, rowSecond, 24, 29, 18000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("税款计算", rowFirst, rowFirst, 32, 38, 21000,
					HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("个人所得税扣缴申报表", 0, 0, 0, 39, 117000, HorizontalAlignment.CENTER,
					Short.valueOf((short) -1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("税款所属日期：    年     月     日至     年      月      日", 1, 1, 0, 39,
					117000, HorizontalAlignment.LEFT, Short.valueOf((short) -1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人名称：", 2, 2, 0, 39, 117000, HorizontalAlignment.LEFT,
					Short.valueOf((short) -1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人纳税人识别号（统一社会信用代码）：", 3, 3, 0, 30, 90000,
					HorizontalAlignment.LEFT, Short.valueOf((short) -1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("金额单位：         人民币元（列至角分）", 3, 3, 31, 39, 27000,
					HorizontalAlignment.RIGHT, Short.valueOf((short) -1), true, font));

			font.put("fontSize", Integer.valueOf(10));
			font.put("fontName", ResourceFactory.getProperty("gz.gz_acounting.m.font"));
			mergedCellList.addAll(mergedCellStyleMap("合计", dataList.size() + rowThird + 1, dataList.size() + rowThird + 1, 0,
							6, 27000, HorizontalAlignment.CENTER, Short.valueOf((short) 1), true, font));
			for (int i = 7; i < 40; i++) {
				String val = sum_count.get(Integer.valueOf(i)) == null ? "0.00" : String.valueOf((BigDecimal) sum_count.get(Integer.valueOf(i)));
				if (i == 39) {
					val = "";
				}
				mergedCellList.addAll(mergedCellStyleMap(val, dataList.size() + rowThird + 1, dataList.size() + rowThird + 1,
								i, i, 3000, HorizontalAlignment.RIGHT, Short.valueOf((short) 1), true, font));
			}

			font = new HashMap();
			mergedCellList.addAll(mergedCellStyleMap("谨声明:本扣缴申报表是根据国家税收法律法规及相关规定填报的，是真实的、可靠的、完整的。",
					dataList.size() + rowThird + 2, dataList.size() + rowThird + 2, 0, 39, 117000,
					HorizontalAlignment.LEFT, Short.valueOf((short) -1), false, font));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人（签章）：                                                        年     月       日",
					dataList.size() + rowThird + 3, dataList.size() + rowThird + 3, 0, 39, 117000,
					HorizontalAlignment.RIGHT, Short.valueOf((short) -1), false, font));
			mergedCellList.addAll(mergedCellStyleMap("代理机构签章：\r\n\r\n代理机构统一社会信用代码：\r\n\r\n经办人签字：\r\n\r\n经办人身份证件号码：",
					dataList.size() + rowThird + 4, dataList.size() + rowThird + 10, 0, 19, 60000,
					HorizontalAlignment.LEFT, Short.valueOf((short) 1), true, font));
			mergedCellList.addAll(mergedCellStyleMap("受理人：\r\n\r\n\r\n受理税务机关（章）：\r\n\r\n\r\n受理日期:    年     月     日",
					dataList.size() + rowThird + 4, dataList.size() + rowThird + 10, 20, 39, 60000,
					HorizontalAlignment.LEFT, Short.valueOf((short) 1), true, font));
			export.setHeadRowHeight((short) 600);
			export.exportExcel(fileName, sheetName, mergedCellList, headList, dataList, null, rowThird);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(fis);
			PubFunc.closeIoResource(wb);
		}
		return fileName;
	}

	private ArrayList<LazyDynaBean> mergedCellStyleMap(String name, int fromRowNum, int toRowNum, int fromColNum,
			int toColNum, int columnWidth, HorizontalAlignment align, Short border, boolean show_btborder,HashMap font) {
		ArrayList headList = new ArrayList();
		try {
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("content", name);
			bean.set("fromRowNum", Integer.valueOf(fromRowNum));
			bean.set("toRowNum", Integer.valueOf(toRowNum));
			bean.set("fromColNum", Integer.valueOf(fromColNum));
			bean.set("toColNum", Integer.valueOf(toColNum));
			HashMap headStyleMap = new HashMap();
			headStyleMap.put("columnWidth", Integer.valueOf(columnWidth));
			if (font.size() == 0) {
				headStyleMap.put("fontSize", Integer.valueOf(12));
				headStyleMap.put("fontName", "黑体");
			} else {
				headStyleMap.put("fontSize", (Integer) font.get("fontSize"));
				headStyleMap.put("fontName", (String) font.get("fontName"));
			}
			headStyleMap.put("align", align);
			headStyleMap.put("border", border);

			bean.set("mergedCellStyleMap", headStyleMap);
			headList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headList;
	}

	private ArrayList<LazyDynaBean> getStyleList(String name, String itemid, String comment, String colType,
			int fromRowNum, int toRowNum, int fromColNum, int toColNum, int columnWidth, String codeitemid) {
		ArrayList headList = new ArrayList();
		try {
			LazyDynaBean bean = new LazyDynaBean();
			HashMap colStyleMap = new HashMap();
			bean.set("content", name);
			bean.set("itemid", itemid.length() > 30?itemid.substring(0, 29):itemid);
			bean.set("comment", comment);
			bean.set("colType", colType);
			bean.set("fromRowNum", Integer.valueOf(fromRowNum));
			bean.set("toRowNum", Integer.valueOf(toRowNum));
			bean.set("fromColNum", Integer.valueOf(fromColNum));
			bean.set("toColNum", Integer.valueOf(toColNum));
			bean.set("decwidth", "2");
			bean.set("codesetid", codeitemid);
			if ("N".equalsIgnoreCase(colType))
				colStyleMap.put("align", HorizontalAlignment.RIGHT);
			else
				colStyleMap.put("align", HorizontalAlignment.CENTER);
			HashMap headStyleMap = new HashMap();
			headStyleMap.put("columnWidth", Integer.valueOf(columnWidth));
			headStyleMap.put("fontSize", Integer.valueOf(12));
			headStyleMap.put("fontName", "黑体");
			bean.set("colStyleMap", colStyleMap);
			bean.set("headStyleMap", headStyleMap);
			headList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headList;
	}

	public HashMap<String, Object> getExportSqlForDetail(String fromtable, String salaryid, String datetime,
			String exporttype, String taxMode, HashMap list_code, ArrayList<Field> fieldlist) {
		HashMap map = new HashMap();

		TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView, "");

		StringBuffer feildstr = new StringBuffer();
		StringBuffer feildstr_sum = new StringBuffer();
		StringBuffer feildstr_sum_gz = new StringBuffer();
		StringBuffer feildstr_sum_gz_equal = new StringBuffer();
		StringBuffer feildstr_sum_archive = new StringBuffer();
		StringBuffer exits_filed = new StringBuffer(",");
		Iterator iter = list_code.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String val = (String) entry.getValue();
			String value_id = val.split(",")[0];

			if (exits_filed.indexOf("," + value_id + ",") == -1) {
				exits_filed.append(value_id + ",");

				// 求和的（简单的求和，复杂求和暂时不支持。如，两个求和之间相减等）
				if ((value_id.indexOf("Σ") != -1) || (value_id.indexOf("∑") != -1)) {
					int length = 0;
					String value_id_temp = "";
					String itemid = "";
					boolean flag_max = false;
					char nPos_ = value_id.charAt(length);
					String isnull = "isnull";
					if (Sql_switcher.searchDbServer() == 2) {
						isnull = "nvl";
					}
					if ((nPos_ != 'Σ') && (nPos_ != '∑')) {
						value_id_temp = "," + isnull + "(max(";
						flag_max = true;
					}
					while (length < value_id.length()) {
						char nPos = value_id.charAt(length);
						length++;

						if ((nPos == '-') || (nPos == '+')) {
							value_id_temp = value_id_temp + "),0)" + nPos;
							// 判断是否是存在的指标，组成sum或者max
							boolean isExists = isExistsField(itemid, fieldlist);
							if ((flag_max) && (feildstr_sum_archive.indexOf("max(0) as " + itemid.toLowerCase()) == -1)) {
								if ("sds".equalsIgnoreCase(itemid)) {
									feildstr_sum_gz_equal.append(",sum(" + (isExists ? itemid : "0") + ") as " + itemid.toLowerCase());
								}else {
									feildstr_sum_gz_equal.append(",max(" + (isExists ? itemid : "0") + ") as " + itemid.toLowerCase());
								}
								feildstr_sum_gz.append(",max(0) as " + itemid.toLowerCase());
								feildstr_sum_archive.append(",max(0) as " + itemid.toLowerCase());
							} else if (feildstr_sum_archive.indexOf(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase()) == -1) {
								feildstr_sum_gz_equal.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
								feildstr_sum_gz.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
								feildstr_sum_archive.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
							}
							itemid = "";
							nPos = value_id.charAt(length);
							if ((nPos == 'Σ') || (nPos == '∑')) {
								value_id_temp = value_id_temp + isnull + "(sum(sum_";
								flag_max = false;
							} else if (nPos == '(') {
								nPos = value_id.charAt(length);
								if ((nPos == 'Σ') || (nPos == '∑')) {
									value_id_temp = value_id_temp + isnull + "(sum(sum_";
									flag_max = false;
								} else {
									value_id_temp = value_id_temp + isnull + "(max(" + nPos;
									itemid = itemid + nPos;
									flag_max = true;
								}
							} else {
								value_id_temp = value_id_temp + isnull + "(max(" + nPos;
								itemid = itemid + nPos;
								flag_max = true;
							}
							length++;
						} else if ((nPos == 'Σ') || (nPos == '∑')) {
							value_id_temp = value_id_temp + "," + isnull + "(sum(sum_";
							flag_max = false;
						} else {
							value_id_temp = value_id_temp + nPos;
							itemid = itemid + nPos;
						}
					}

					boolean isExists = isExistsField(itemid, fieldlist);
					if ((flag_max) && (feildstr_sum_archive.indexOf("max(0) as " + itemid.toLowerCase()) == -1)) {
						if ("sds".equalsIgnoreCase(itemid))
							feildstr_sum_gz_equal.append(",sum(" + (isExists ? itemid : "0") + ") as " + itemid.toLowerCase());
						else {
							feildstr_sum_gz_equal.append(",max(" + (isExists ? itemid : "0") + ") as " + itemid.toLowerCase());
						}
						feildstr_sum_gz.append(",max(0) as " + itemid.toLowerCase());
						feildstr_sum_archive.append(",max(0) as " + itemid.toLowerCase());
					} else if (feildstr_sum_archive.indexOf(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase()) == -1) {
						feildstr_sum_gz_equal.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
						feildstr_sum_gz.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
						feildstr_sum_archive.append(",sum(" + (isExists ? itemid : "0") + ") as sum_" + itemid.toLowerCase());
					}
					value_id_temp = value_id_temp + "),0)";
					
					String alias = value_id.toUpperCase().replace("+", "_").replace("-", "_").replaceAll("(?:Σ|∑)", "sum_");
					alias = alias.length() > 30?alias.substring(0, 29) : alias;
					if (value_id_temp.equalsIgnoreCase("," + isnull + "(max(LJSE),0)-" + isnull + "(sum(sum_SDS),0)+" + isnull + "(max(SDS),0)"))
						feildstr_sum.append(",(case when (" + value_id_temp.substring(1) + ")<0 then 0 else "
								+ value_id_temp.substring(1) + " end) as " + alias);
					else
						feildstr_sum.append(value_id_temp + " as " + alias);
				} else if (value_id.indexOf("+") != -1) {
					String[] val_temp = value_id.replace("+", "_").replaceAll("(?:\\(|\\))", "").split("_");

					feildstr.append(getFieldStr(val_temp, value_id, 1, fieldlist));
				} else if (value_id.indexOf("-") != -1) {
					String[] val_temp = value_id.replace("-", "_").replaceAll("(?:\\(|\\))", "").split("_");

					feildstr.append(getFieldStr(val_temp, value_id, 1, fieldlist));
				} else {
					String flg = "max";

					FieldItem fi = DataDictionary.getFieldItem(value_id);
					if (StringUtils.isNotBlank(value_id)) {
						if (fi == null) {
							if (this.notExistsFiled.indexOf("," + value_id.toLowerCase() + ",") == -1) {
								flg = "sum";
							}
						} else if ("N".equalsIgnoreCase(fi.getItemtype())) {
							flg = "sum";
						}
					}
					String alias = value_id.toUpperCase().replace("+", "_").replace("-", "_").replaceAll("(?:\\(|\\))", "");
					alias = alias.length() > 30?alias.substring(0, 29) : alias;
					feildstr.append("," + flg + "(" + value_id + ") as " + alias);
				}
			}
		}
		StringBuffer sqlsb = new StringBuffer(
				"select taxunit,max(dbid) as dbid,a0100,max(A0000) as a0000,max(a00z0) as a00z0,max(a00z1) as a00z1,max(b0110) as b0110,max(e0122) as e0122,max(nbase) as nbase,");

		String pre = taxbo.getPrivPre();

		if ("JEPWw5tnIio@3HJD@".equals(datetime)) {
			datetime = PubFunc.decrypt(datetime);
		}
		String timewhere = getFilterCond(datetime, taxMode);
		sqlsb.append(feildstr.substring(1) + " from " + fromtable
				+ " left join (select dbid,pre from dbname) dbname on upper(" + fromtable + ".nbase)=upper(dbname.pre) ");
		sqlsb.append(" where (" + pre + ") ");
		if ((timewhere != null) && (!"".equals(timewhere))) {
			sqlsb.append(timewhere);
		}
		// 根据页面的查询控制显示
		HashMap map_ = taxbo.getPageSql();
		String sql_ = (String) map_.get("sql");
		if (StringUtils.isNotBlank(sql_)) {
			sqlsb.append(" and " + sql_);
		}

		sqlsb.append(" group by upper(nbase),a0100," + Sql_switcher.dateToChar("tax_date", "yyyy-MM") + ",taxmode,taxunit");

		String[] datearr = StringUtils.split(datetime, ".");
		String theyear = datearr[0];
		String themonth = datearr[1];

		StringBuffer ss = new StringBuffer();
		ss.append(" select t.*,f.* from (");
		ss.append(sqlsb.toString());
		ss.append(") t,(select max(nbase) as nbase,a0100,taxmode,taxunit" + feildstr_sum + " from (");
		ss.append(getSql(feildstr_sum_gz_equal.toString(), "gz_tax_mx", theyear, themonth, taxMode, fromtable, sql_, "=", pre));
		ss.append(" union all ");
		ss.append(getSql(feildstr_sum_gz.toString(), "gz_tax_mx", theyear, themonth, taxMode, fromtable, sql_, "<", pre));
		ss.append(" union all ");
		ss.append(getSql(feildstr_sum_archive.toString(), "taxarchive", theyear, themonth, taxMode, fromtable, sql_, "<", pre));
		ss.append(") al group by upper(nbase),a0100,taxmode,taxunit) f");

		ss.append(" where upper(T.nbase) = upper(F.nbase) and T.a0100 = F.a0100 and T.taxmode = F.taxmode and " + Sql_switcher.isnull("T.taxunit", "''") + "=" + Sql_switcher.isnull("F.taxunit", "''"));
		String sortSql = (String) map_.get("sortSql");
		if (StringUtils.isNotBlank(sortSql))
			ss.append(" " + sortSql);
		else {
			ss.append(" order by a0000,a00z0,a00z1,b0110,e0122 ");
		}
		map.put("sql", ss);
		return map;
	}

	private boolean isExistsField(String value_, ArrayList<Field> fieldlist) {
		boolean isExists = false;
		for (Field field : fieldlist) {
			if (value_.equalsIgnoreCase(field.getName())) {
				isExists = true;
				break;
			}
		}
		return isExists;
	}

	private String getFieldStr(String[] val_temp, String value_id, int type, ArrayList<Field> fieldlist) {
		String feildstr = "";
		boolean temp_exits = false;
		try {
			String value_id_ = value_id.toUpperCase();
			String temp = ",";
			for (int i = 0; i < val_temp.length; i++) {
				temp_exits = false;
				String value_ = val_temp[i].trim().toUpperCase();
				if (StringUtils.isNotBlank(value_)) {
					for (Field field : fieldlist) {
						if ((value_.equalsIgnoreCase(field.getName())) && (temp.indexOf(value_) == -1)) {
							temp_exits = true;
							temp = temp + temp + value_ + ",";

							value_id_ = replace_val(value_id_, value_, value_);

							break;
						}
					}
				}
				if (!temp_exits) {
					value_id_ = replace_val(value_id_, value_, "''");
				}
			}
			String alias = value_id.toUpperCase().replace("+", "_").replace("-", "_").replaceAll("(?:Σ|∑|\\(|\\))", "");
			alias = alias.length() > 30?alias.substring(0, 29) : alias;
			if ((StringUtils.isNotBlank(value_id_)) && (value_id_.indexOf("Σ") == -1) && (value_id_.indexOf("∑") == -1))
				feildstr = "," + value_id_ + " as " + alias;
			else
				feildstr = ",'' as " + alias;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feildstr;
	}

	private String replace_val(String value_id_, String ori_value, String value_) {
		String flg = "max";

		FieldItem fi = DataDictionary.getFieldItem(value_);
		if (!"''".equalsIgnoreCase(value_)) {
			if (fi == null) {
				if (this.notExistsFiled.indexOf("," + value_.toLowerCase() + ",") == -1) {
					flg = "sum";
				}
			} else if ("N".equalsIgnoreCase(fi.getItemtype())) {
				flg = "sum";
			}
		}

		value_id_ = value_id_.replace("Σ" + ori_value, Sql_switcher.isnull("sum(" + ("''".equalsIgnoreCase(value_) ? "0" : value_) + ")", "0"));
		value_id_ = value_id_.replace("∑" + ori_value, Sql_switcher.isnull("sum(" + ("''".equalsIgnoreCase(value_) ? "0" : value_) + ")", "0"));
		value_id_ = value_id_.replace("-" + ori_value, "-" + Sql_switcher .isnull(new StringBuilder(String.valueOf(flg)).append("(").append(value_).append(")").toString(), "0"));
		value_id_ = value_id_.replace("+" + ori_value, "+" + Sql_switcher .isnull(new StringBuilder(String.valueOf(flg)).append("(").append(value_).append(")").toString(), "0"));
		value_id_ = value_id_.replace(ori_value + "+",
				Sql_switcher.isnull(
						new StringBuilder(String.valueOf(flg)).append("(").append(value_).append(")").toString(), "0")
						+ "+");
		value_id_ = value_id_.replace(ori_value + "-",
				Sql_switcher.isnull(
						new StringBuilder(String.valueOf(flg)).append("(").append(value_).append(")").toString(), "0")
						+ "-");
		return value_id_;
	}

	private String getSql(String income_field, String income_sum, String specialDed_field, String specialDed_sum,
			String otherDed_field, String otherDed_sum, String sum_sp, String fromtable, String theyear,
			String themonth, String taxMode, String searchTable) {
		StringBuffer ss = new StringBuffer();
		try {
			ss.append("select max(nbase) as nbase,a0100");
			if (StringUtils.isNotBlank(income_field.toString()))
				ss.append("," + income_sum.substring(1) + " as income");
			else {
				ss.append(",0  as income");
			}
			if (StringUtils.isNotBlank(specialDed_field.toString()))
				ss.append("," + specialDed_sum.substring(1) + " as special");
			else {
				ss.append(",0  as special");
			}

			if (StringUtils.isNotBlank(otherDed_field.toString()))
				ss.append("," + otherDed_sum.substring(1) + " as other");
			else {
				ss.append(",0  as other");
			}
			ss.append(sum_sp + ",sum(SDS) AS sds_all from " + fromtable + " where ");

			ss.append(Sql_switcher.year("Declare_tax"));
			ss.append("=");
			ss.append(theyear);
			ss.append(" and ");
			ss.append(Sql_switcher.month("Declare_tax"));
			if (("gz_tax_mx".equalsIgnoreCase(searchTable)) && ("taxarchive".equalsIgnoreCase(fromtable)))
				ss.append("<");
			else
				ss.append("<=");
			ss.append(themonth);
			if ((StringUtils.isNotBlank(taxMode)) && (!"0".equals(taxMode))) {
				ss.append(" and taxmode in (");
				ss.append(taxMode + ")");
			}
			ss.append(" group by upper(nbase),a0100");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss.toString();
	}

	/**
	 * 获取合计的sql，有Σ符号的
	 * 
	 * @param sum_sp
	 * @param fromtable
	 * @param theyear
	 * @param themonth
	 * @param taxMode
	 * @param searchTable
	 *            gz_tax_mx|taxarchive
	 * @param sql_page
	 *            页面传过来的sql
	 * @return
	 */
	private String getSql(String sum_sp, String fromtable, String theyear, String themonth, String taxMode,
			String searchTable, String sql_page, String character, String pre) {
		StringBuffer ss = new StringBuffer();
		try {
			ss.append("select max(nbase) as nbase,a0100,taxmode,taxunit");

			ss.append(sum_sp + " from " + fromtable + " where " + pre + " and ");

			ss.append(Sql_switcher.year("Declare_tax"));
			ss.append("=");
			ss.append(theyear);
			ss.append(" and ");
			ss.append(Sql_switcher.month("Declare_tax"));
			ss.append(character);
			ss.append(themonth);
			if ((StringUtils.isNotBlank(taxMode)) && (!"0".equals(taxMode))) {
				ss.append(" and taxmode in (");
				ss.append(taxMode + ")");
			}

			if (StringUtils.isNotBlank(sql_page)) {
				ss.append(" and " + sql_page);
			}

			ss.append(" group by upper(nbase),a0100,taxmode,taxunit");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss.toString();
	}

	private String getHiredate(String tablename) {
		StringBuffer sf = new StringBuffer();
		StringBuffer sf_linkedTable = new StringBuffer();

		String hiredateField = SystemConfig.getPropertyValue("hiredateField");
		try {
			if (StringUtils.isNotBlank(hiredateField)) {
				sf.append("(select case when " + Sql_switcher.dateToChar("max(tem.hiredate)")
						+ " is not null then max(tem.hiredate)");
				sf.append(" else " + Sql_switcher.charToDate(new StringBuilder("CONCAT(")
						.append(Sql_switcher.year(new StringBuilder(String.valueOf(tablename)).append(".a00z2").toString()))
						.append(",'-01-01')").toString()) + " end");
				sf.append(" from (");
				ArrayList list = this.userView.getPrivDbList();

				String[] hiredateField_tem = hiredateField.split(";");

				for (int j = 0; j < list.size(); j++) {
					String nbase = (String) list.get(j);

					for (int i = 0; i < hiredateField_tem.length; i++) {
						String field = nbase + hiredateField_tem[i].split(",")[0];
						String fielditem = hiredateField_tem[i].split(",")[1];
						sf_linkedTable.append(" union all select max(" + fielditem + ") as hiredate," + field + ".a0100 from ");
						sf_linkedTable.append(field);
						sf_linkedTable.append(" where " + field + ".a0100 = " + tablename + ".a0100");
						sf_linkedTable.append(" group by a0100");
					}
				}

				sf.append(sf_linkedTable.substring(10) + ") tem group by a0100)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sf.toString();
	}

	/**
	 * 取得报税时间过滤条件
	 * 
	 * @param declaredate
	 *            日期
	 * @param taxMode
	 *            计税方式
	 * @return
	 */
	private String getFilterCond(String declaredate, String taxMode) {
		StringBuffer buf = new StringBuffer();
		if ((StringUtils.isNotBlank(taxMode)) && (!"0".equals(taxMode))) {
			buf.append(" and taxmode in (");
			buf.append(taxMode + ")");
		}
		if ((declaredate == null) || ("".equalsIgnoreCase(declaredate)) || ("all".equalsIgnoreCase(declaredate)))
			return StringUtils.isBlank(buf.toString()) ? "" : buf.toString();
		String[] datearr = StringUtils.split(declaredate, ".");
		String theyear = datearr[0];
		String themonth = datearr[1];
		buf.append(" and ");
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);
		return buf.toString();
	}

	private ArrayList<Field> getAllField() {
		ArrayList fieldlist = new ArrayList();
		TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView, "");
		// 个税明细表获取固定字段
		fieldlist.addAll(taxbo.searchCommonItemList());
		// 个税明细表动态维护的指标
		fieldlist.addAll(taxbo.searchDynaItemList());

		return fieldlist;
	}

	private HashMap getComment(HSSFSheet sheet, int rowNum) {
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		int rows = sheet.getPhysicalNumberOfRows();
		String not_insert = ",21,23,29,37,38,32,33,34,35,37,";
		if (rows < 1)
			return null;
		HSSFRow row = sheet.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c = (short) (c + 1)) {
				if (not_insert.indexOf("," + c + ",") == -1) {
					String value = "";
					HSSFCell cell = row.getCell(c);
					HSSFComment cc = cell.getCellComment();
					String comment = "";
					if (cell != null) {
						comment = cell.getCellComment() != null ? cell.getCellComment().getString().getString() : "";
					}
					if ((StringUtils.isNotBlank(comment)) && (24 <= cell.getColumnIndex())
							&& (cell.getColumnIndex() <= 29) && (comment.length() > 5)) {
						comment = comment.substring(1);
					}

					if ("AXXXX".equalsIgnoreCase(comment.trim())) {
						comment = "";
					}
					if (StringUtils.isNotBlank(cell.getStringCellValue()))
						map.put(Integer.valueOf(cell.getColumnIndex()),
								comment.trim() + "," + cell.getStringCellValue());
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取excel中的批注
	 * @param sheet
	 * @param rowNum
	 * @return
	 */
	private HashMap getComment_new(HSSFSheet sheet, int rowNum) {
		HashMap map = new HashMap();
		try {
			int rows = sheet.getPhysicalNumberOfRows();
			if (rows < 1)
				return null;
			HSSFRow row = sheet.getRow(rowNum);
			if (row != null) {
				int cells = row.getPhysicalNumberOfCells();
				for (short c = 0; c < cells; c = (short) (c + 1)) {
					String value = "";
					HSSFCell cell = row.getCell(c);
					HSSFComment cc = cell.getCellComment();
					String comment = "";
					if (cell != null && cell.getCellComment() != null) {
						comment = cell.getCellComment().getString().getString();
					}

					if ("AXXXX".equalsIgnoreCase(comment.trim())) {
						comment = "";
					}
					if (StringUtils.isNotBlank(cell.getStringCellValue()))
						map.put(Integer.valueOf(cell.getColumnIndex()), comment.trim() + "," + cell.getStringCellValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap getExportData(ArrayList<LazyDynaBean> headList, String sql) throws SQLException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		LazyDynaBean rowDataBean = null;
		LazyDynaBean dataBean = null;
		LazyDynaBean bean = new LazyDynaBean();
		Date d = null;
		RowSet rowSet = null;
		HashMap finally_map = new HashMap();
		HashMap map = new HashMap();
		ArrayList dataList = new ArrayList();
		String notCount = ",0,1,2,3,4,5,6,39,";// 这些列不需要合计值，也无法合计值
		try {
			String itemid = "";
			String itemtype = "";
			String codesetid = "";
			int decwidth = 0;
			String dateFormat = "";
			SimpleDateFormat df = null;
			if (StringUtils.isBlank(sql))
				return null;
			rowSet = dao.search(sql);

			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(32);
			if ((StringUtils.isBlank(display_e0122)) || ("00".equals(display_e0122)))
				display_e0122 = "0";
			BigDecimal bigDecimal_ = new BigDecimal(0);
			while (rowSet.next()) {
				rowDataBean = new LazyDynaBean();
				for (int i = 0; i < headList.size(); i++) {
					dataBean = new LazyDynaBean();
					bean = (LazyDynaBean) headList.get(i);
					itemid = (String) bean.get("itemid");
					if ("".equals(itemid)) {
						dataBean.set("content", "");
						rowDataBean.set(itemid, dataBean);
					} else {
						itemtype = (String) bean.get("colType");
						codesetid = (String) bean.get("codesetid");
						if (bean.get("decwidth") != null)
							decwidth = Integer.parseInt((String) bean.get("decwidth"));
						dateFormat = (String) bean.get("dateFormat");
						if (StringUtils.isEmpty(codesetid)) {
							codesetid = "0";
						}
						Pattern pattern = Pattern.compile("^-?[0-9]+.*[0-9]*$");
						String value = rowSet.getString(itemid);
						if ((notCount.indexOf("," + i + ",") == -1) && (StringUtils.isNotBlank(value))
								&& (pattern.matcher(value).matches())) {
							BigDecimal sum_ = map.get(Integer.valueOf(i)) == null ? bigDecimal_ : (BigDecimal) map.get(Integer.valueOf(i));
							BigDecimal value_ = new BigDecimal(PubFunc.round(value, decwidth));
							map.put(Integer.valueOf(i), sum_.add(value_));
						}

						if ("D".equals(itemtype)) {
							if (StringUtils.isEmpty(dateFormat))
								df = new SimpleDateFormat("yyyy-MM-dd");
							else
								df = new SimpleDateFormat(dateFormat);
							d = null;
							d = rowSet.getDate(itemid);
							if (d != null)
								dataBean.set("content", df.format(d));
							else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						} else if ("A".equals(itemtype)) {
							String itemidR = rowSet.getString(itemid);
							if ("0".equals(codesetid)) {
								DecimalFormat dformat = new DecimalFormat("0.0");
								if (("sl2".equals(itemid)) && (!rowSet.isLast()))
									dataBean.set("content", (itemidR == null) || (itemidR == "") ? "小计"
											: dformat.format(Double.parseDouble(itemidR)));
								else
									dataBean.set("content", itemidR == null ? "" : itemidR);
							} else {
								dataBean.set("content", itemidR == null ? "" : AdminCode.getCodeName(codesetid, itemidR));
							}
							rowDataBean.set(itemid, dataBean);
						} else if ("N".equals(itemtype)) {
							if (rowSet.getString(itemid) != null) {
								dataBean.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
							} else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						} else {
							if (rowSet.getString(itemid) != null)
								dataBean.set("content", rowSet.getString(itemid));
							else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						}
					}
				}
				dataList.add(rowDataBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		finally_map.put("sum_count", map);
		finally_map.put("dataList", dataList);
		return finally_map;
	}

	/**
	 * 获取数据
	 * 
	 * @param headList
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public HashMap getExportData_new(ArrayList<LazyDynaBean> headList, String sql) throws SQLException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		LazyDynaBean rowDataBean = null;
		LazyDynaBean dataBean = null;
		LazyDynaBean bean = new LazyDynaBean();
		Date d = null;
		RowSet rowSet = null;
		HashMap finally_map = new HashMap();
		HashMap map = new HashMap();
		ArrayList dataList = new ArrayList();
		try {
			String itemid = "";
			String itemtype = "";
			String codesetid = "";
			int decwidth = 0;
			String dateFormat = "";
			SimpleDateFormat df = null;
			if (StringUtils.isBlank(sql))
				return null;
			rowSet = dao.search(sql);

			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(32);
			if ((StringUtils.isBlank(display_e0122)) || ("00".equals(display_e0122)))
				display_e0122 = "0";
			BigDecimal bigDecimal_ = new BigDecimal(0);
			while (rowSet.next()) {
				rowDataBean = new LazyDynaBean();
				for (int i = 0; i < headList.size(); i++) {
					dataBean = new LazyDynaBean();
					bean = (LazyDynaBean) headList.get(i);
					itemid = (String) bean.get("itemid");
					itemid = itemid.length() > 30?itemid.substring(0, 29):itemid;
					if ("".equals(itemid)) {
						dataBean.set("content", "");
						rowDataBean.set(itemid, dataBean);
					} else {
						itemtype = (String) bean.get("colType");
						codesetid = (String) bean.get("codesetid");
						if (bean.get("decwidth") != null)
							decwidth = Integer.parseInt((String) bean.get("decwidth"));
						dateFormat = (String) bean.get("dateFormat");
						if (StringUtils.isEmpty(codesetid)) {
							codesetid = "0";
						}
						Pattern pattern = Pattern.compile("^-?[0-9]+.*[0-9]*$");

						if ("D".equals(itemtype)) {
							if (StringUtils.isEmpty(dateFormat))
								df = new SimpleDateFormat("yyyy-MM-dd");
							else
								df = new SimpleDateFormat(dateFormat);
							d = null;
							d = rowSet.getDate(itemid);
							if (d != null)
								dataBean.set("content", df.format(d));
							else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						} else if ("A".equals(itemtype)) {
							String itemidR = rowSet.getString(itemid);
							if ("0".equals(codesetid)) {
								DecimalFormat dformat = new DecimalFormat("0.0");
								if (("sl2".equals(itemid)) && (!rowSet.isLast()))
									dataBean.set("content", (itemidR == null) || (itemidR == "") ? "小计" : dformat.format(Double.parseDouble(itemidR)));
								else
									dataBean.set("content", itemidR == null ? "" : itemidR);
							} else {
								dataBean.set("content", itemidR == null ? "" : AdminCode.getCodeName(codesetid, itemidR));
							}
							rowDataBean.set(itemid, dataBean);
						} else if ("N".equals(itemtype)) {
							if (rowSet.getString(itemid) != null) {
								dataBean.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
							} else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						} else {
							if (rowSet.getString(itemid) != null)
								dataBean.set("content", rowSet.getString(itemid));
							else
								dataBean.set("content", "");
							rowDataBean.set(itemid, dataBean);
						}
					}
				}
				dataList.add(rowDataBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		finally_map.put("dataList", dataList);
		return finally_map;
	}

	/**
	 * 取得权限过滤语句
	 * 
	 * @return
	 * @see #hasModulePriv()
	 */
	public String getPrivPre(String fromTable) {
		TaxMxBo tax = new TaxMxBo(this.frameconn, this.userView);
		StringBuffer pre = new StringBuffer();
		StringBuffer prelast = new StringBuffer(" (");
		try {
			ArrayList list = this.userView.getPrivDbList();
			StringBuffer nbases = new StringBuffer();
			for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
				Object object = localIterator.next();
				nbases.append("'" + object + "',");
			}
			nbases.setLength(nbases.length() - 1);
			if (this.userView.isSuper_admin()) {
				prelast.append("1=1)");
			} else {
				if ((list == null) || (list.size() <= 0)) {
					pre.append("1=2");
				} else {
					String nunit = this.userView.getUnitIdByBusi("3");
					pre.append(" 1=2 ");
					String[] unitarr = nunit.split("`");
					for (int i = 0; i < unitarr.length; i++) {
						String codeid = unitarr[i];
						if ((codeid != null) && (!"".equals(codeid))) {
							if ((codeid != null) && (codeid.trim().length() > 2)) {
								if ("true".equalsIgnoreCase(tax.getDeptID())) {
									pre.append(" or (case when nullif(" + fromTable + ".deptid,'') is not null then deptid ");
									if ("UN".equalsIgnoreCase(codeid.substring(0, 2))) {
										pre.append(" else " + fromTable + ".B0110 end like  '" + codeid.substring(2) + "%') ");
									} else if ("UM".equalsIgnoreCase(codeid.substring(0, 2))) {
										pre.append(" else " + fromTable + ".e0122 end like  '" + codeid.substring(2) + "%') ");
									}
								} else if ("UN".equalsIgnoreCase(codeid.substring(0, 2))) {
									pre.append(" or " + fromTable + ".B0110  like  '" + codeid.substring(2) + "%' ");
								} else if ("UM".equalsIgnoreCase(codeid.substring(0, 2))) {
									pre.append(" or " + fromTable + ".e0122  like  '" + codeid.substring(2) + "%' ");
								}

							} else if ((codeid != null) && ("UN".equalsIgnoreCase(codeid))) {
								pre.append(" or 1=1 ");
							}
						}
					}
					pre.append(")");
					if (nbases.length() > 0) {
						pre.append(" and UPPER(" + fromTable + ".nbase) in (" + nbases.toString().toUpperCase() + ") ");
					} else
						pre.append(" and 1=2 ");
				}

				prelast.append(pre);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return prelast.toString();
	}

	public String exportdetail_new_new(String fromtable, String datetime, String salaryid, String taxMode)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		StringBuffer ss = new StringBuffer();
		ExportExcelUtil export = new ExportExcelUtil(this.frameconn, this.userView);
		String fileName = this.userView.getUserName() + "_gz.xls";
		String sheetName = "个人所得税扣缴申报表";
		ArrayList headList = new ArrayList();
		HashMap colStyleMap = new HashMap();
		HashMap map_ = new HashMap();
		LazyDynaBean bean = new LazyDynaBean();
		ArrayList<Field> fieldlist = getAllField();
		int row_num = 0;
		boolean error_flag = false;
		FileInputStream fis = null;
		HSSFWorkbook wb = null;
		try {
			StringBuffer ff = new StringBuffer();

			TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView, "");
			if ((StringUtils.isBlank(taxMode)) || ("0".equals(taxMode))) {
				String pre = taxbo.getPrivPre();

				if ("JEPWw5tnIio@3HJD@".equals(datetime)) {
					datetime = PubFunc.decrypt(datetime);
				}
				String timewhere = getFilterCond(datetime, taxMode);
				ff.append(" where (" + pre + ")");
				if ((timewhere != null) && (!"".equals(timewhere))) {
					ff.append(timewhere);
				}
				// 找出对应的计税方式
				rs = dao.search("select taxmode from " + fromtable + ff.toString());
				while (rs.next()) {
					String tt = rs.getString("taxmode");
					if (StringUtils.isNotBlank(tt) && ((StringUtils.isBlank(ss.toString())) || ((ss + ",").indexOf("," + tt + ",") == -1))) {
						ss.append("," + tt);
					}
				}
				taxMode = StringUtils.isBlank(ss.toString()) ? "0" : ss.substring(1);
			}
			String splitF = System.getProperty("file.separator");
			String url = System.getProperty("catalina.home") + splitF + "webapps" + splitF + "hrms" + splitF + "templatefile" + splitF + "TaxDeclaration.xls";
			File file = new File(url);
			boolean exists = file.exists();
			if (!exists) {
				error_flag = true;
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.notSetTemplate")));
			}
			fis = new FileInputStream(file);
			wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			String name = sheet.getSheetName();
			if (StringUtils.isNotBlank(name))
				sheetName = name;
			HashMap map1 = new HashMap();

			map1.putAll(getComment_new(sheet, 0));

			HashMap map_other = (HashMap) map1.clone();
			Iterator iter = map1.entrySet().iterator();
			while (iter.hasNext()) {
				String comment = "";
				Map.Entry entry = (Map.Entry) iter.next();
				int key = ((Integer) entry.getKey()).intValue();
				String val = (String) entry.getValue();

				String value = val.split(",")[0];
				// 如果没有批注
				if (StringUtils.isBlank(value)) {
					map_.put(Integer.valueOf(key), getStyleList(val.split(",")[1], "", "", "A", 0, 0, key, key, 3000, "0"));
					map_other.remove(Integer.valueOf(key));
				} else {
					if ((StringUtils.isNotBlank(value)) && ((value.indexOf("Σ") != -1) || (value.indexOf("∑") != -1)
							|| (value.indexOf("+") != -1) || (value.indexOf("-") != -1))) {
						comment = value;
						map_.put(Integer.valueOf(key),
								getStyleList(val.split(",")[1], comment.replace("+", "_").replace("-", "_").replaceAll("(?:Σ|∑)", "sum_"),
										comment, "N", 0, 0, key, key, 3000, "0"));
					}

					if (StringUtils.isBlank(comment)) {
						for (Field field : fieldlist) {
							if (val.split(",")[0].equalsIgnoreCase(field.getName())) {
								comment = val.split(",")[0].toUpperCase();
								map_.put(Integer.valueOf(key),
										getStyleList(val.split(",")[1], field.getName(), comment,
												taxbo.getvarType(field.getDatatype()), 0, 0, key, key, 3000, field.getCodesetid()));
								break;
							}
						}
					}
					if (StringUtils.isBlank(comment)) {
						map_.put(Integer.valueOf(key), getStyleList(val.split(",")[1], "", value, "A", 0, 0, key, key, 3000, "0"));
						map_other.remove(Integer.valueOf(key));
					}
				}
			}
			if (map_other.size() == 0) {
				error_flag = true;
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.notSetTemplate")));
			}
			// 获取sql
			HashMap hashMap = getExportSqlForDetail(fromtable, salaryid, datetime, "1", taxMode, map_other, fieldlist);
			StringBuffer sql = (StringBuffer) hashMap.get("sql");

			int size = map_.size();

			for (int i = 0; i < size; i++) {
				if (map_.get(Integer.valueOf(i)) != null)
					headList.addAll((ArrayList) map_.get(Integer.valueOf(i)));
			}
			HashMap dataMap = getExportData_new(headList, sql.toString());
			ArrayList dataList = (ArrayList) dataMap.get("dataList");
			export.setHeadRowHeight((short) 600);
			export.exportExcel(fileName, sheetName, null, headList, dataList, null, row_num);
		}catch (Exception e) {
			e.printStackTrace();
			if (error_flag) {
				throw GeneralExceptionHandler.Handle(e);
			}
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.expFail")));
		}finally {
			PubFunc.closeIoResource(fis);
			PubFunc.closeIoResource(wb);
		}
		return fileName;
	}
}
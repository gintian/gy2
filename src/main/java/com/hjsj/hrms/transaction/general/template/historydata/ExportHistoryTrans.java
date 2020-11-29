package com.hjsj.hrms.transaction.general.template.historydata;

import com.hjsj.hrms.businessobject.general.template.HistoryDataBo;
import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * <p>
 * Title:ExportTemplateTrans.java
 * </p>
 * <p>
 * Description:人事异动导出excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-06-09 11:00:00
 * </p>
 * 
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class ExportHistoryTrans extends IBusiness
{

    public void execute() throws GeneralException {

		String tabid = (String) this.getFormHM().get("tabid");//模板id
//	String sqlStr = (String) this.getFormHM().get("sqlStr");//查询语句
		//获得templateSetList
//	sqlStr =SafeCode.decode(sqlStr);

		//String needcondition = (String) this.userView.getHm().get("template_sql_1");//前台不允许存放sql，将needcondition存放在了userview中(String) this.getFormHM().get("needcondition");//查询语句
		String needcondition = (String) this.getFormHM().get("needcondition");//gaohy,获取时间范围
		needcondition = PubFunc.keyWord_reback(SafeCode.decode(needcondition));
		String ids = (String) this.getFormHM().get("ids");
		ids = SafeCode.keyWord_reback(ids);
		StringBuffer headtarg = new StringBuffer(); //组合表头
		StringBuffer strsql = new StringBuffer();
		//组合sql
		String table_name = (String) this.getFormHM().get("table_name");
		String codeid = (String) this.getFormHM().get("codeid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList fieldlist = new ArrayList();

		ContentDAO dao = new ContentDAO(this.frameconn);
		String cname = "";

		TemplateListBo bo = new TemplateListBo(this.getFormHM().get("tabid").toString(), this.getFrameconn(), this.userView);
		bo.setClass_type(1);
		HistoryDataBo historybo = new HistoryDataBo(this.getFormHM().get("tabid").toString(), this.getFrameconn(), this.userView);
		ArrayList templateSetList = bo.getAllCell();
		ArrayList headSetList = new ArrayList();
		FieldItem field = null;
		ArrayList partlist = historybo.getpartCells();
		DbWizard dbw = new DbWizard(this.getFrameconn());
		for (int i = 0; i < partlist.size(); i++) {
			LazyDynaBean abean = (LazyDynaBean) partlist.get(i);
			if ("content_pdf".equals(abean.get("field_name"))) {
				continue;
			}
			FieldItem item = DataDictionary.getFieldItem(abean.get("field_name").toString());
			headtarg.append(abean.get("field_name") + ",");

			if (item != null) {
				Field item_0 = (Field) item.cloneField();
				FieldItem item_1 = (FieldItem) item.cloneItem();
				if ("id".equals(abean.get("field_name")))
					item_1.setItemdesc(abean.get("hz").toString());//不取数据字典中描述
				fieldlist.add(item_1);
			} else {
				FieldItem fielditem = new FieldItem("" + abean.get("field_name"), "" + abean.get("hz").toString().replace("`", ""));
				fielditem.setItemid("" + abean.get("field_name"));
				fielditem.setCodesetid("0");
				if ("lasttime".equals(abean.get("field_name")))
					fielditem.setItemtype("D");
				else
					fielditem.setItemtype("A");
				fielditem.setItemdesc(abean.get("hz").toString().replace("`", ""));
				fieldlist.add(fielditem);
			}
		}
		for (int i = 0; i < templateSetList.size(); i++) {
			LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
			if (!"0".equals(abean.get("isvar")))//去掉临时变量
				continue;
			if (!"0".equals(abean.get("subflag")))//去掉子集
				continue;
			if (bo.getBo().getInfor_type() != 1 && ("codesetid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "codeitemdesc".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "corcode".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "parentid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "start_date".equalsIgnoreCase(abean.get("field_name").toString().trim()))) {

			} else {
				if (!this.getUserView().isSuper_admin() && ("0".equalsIgnoreCase(this.getUserView().analyseFieldPriv(abean.get("field_name").toString().trim())) && "0".equals(bo.getBo().getUnrestrictedMenuPriv_Input())))
					continue;
			}
			if (!dbw.isExistField("template_archive", abean.get("field_name") + "_" + abean.get("chgstate"), false)) {
				continue;
			}
			headtarg.append(abean.get("field_name") + "_" + abean.get("chgstate") + ",");
			headSetList.add(abean);
			FieldItem item = DataDictionary.getFieldItem(abean.get("field_name").toString());
			String desc = abean.get("hz").toString().replace("`", "");
			if ("2".equals(abean.get("chgstate")))
				desc = "拟[" + desc + "]";
			if (item != null) {
				Field item_0 = (Field) item.cloneField();
				FieldItem item_1 = (FieldItem) item.cloneItem();


				item_1.setItemid(abean.get("field_name") + "_" + abean.get("chgstate"));
				item_1.setItemdesc(desc);
				fieldlist.add(item_1);
			} else {
				String cname2 = "" + abean.get("field_name");
				if (cname2 == null)
					cname2 = "";
				if ("codesetid".equalsIgnoreCase(cname2) || "codeitemdesc".equalsIgnoreCase(cname2) || "corcode".equalsIgnoreCase(cname2) || "parentid".equalsIgnoreCase(cname2) || "start_date".equalsIgnoreCase(cname2)) {
					FieldItem fielditem = new FieldItem();
					fielditem.setItemid(cname2 + "_" + abean.get("chgstate"));
					fielditem.setItemdesc(desc);
					fielditem.setFieldsetid("B01");
					if ("start_date".equalsIgnoreCase(cname2))
						fielditem.setItemtype("D");
					else
						fielditem.setItemtype("A");
					if ("parentid".equalsIgnoreCase(cname2))
						fielditem.setCodesetid("UM");
					else if ("codesetid".equalsIgnoreCase(cname2))
						fielditem.setCodesetid("orgType");
					else
						fielditem.setCodesetid("0");
					if (!"start_date".equalsIgnoreCase(cname2))
						fielditem.setItemlength(50);
					fielditem.setUseflag("1");
					fieldlist.add(fielditem);
				}
			}

		}
		strsql.append(" select  ");
		strsql.append(headtarg.toString().endsWith(",") ? "" + headtarg.toString().substring(0, headtarg.toString().length() - 1) : headtarg.toString());
		strsql.append(" from " + table_name);
		strsql.append(" where 1=1 ");
		strsql.append(" and tabid=" + tabid + " ");

		if (ids != null && !"".equals(ids) && ids.trim().length() > 0) {
			strsql.append(" and id in (" + ids.substring(0, ids.length() - 1)
					+ ") ");
		}
		if (!this.userView.isSuper_admin()) {
			String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
			if (operOrg == null) {
				strsql.append(" and 1=2 ");
			} else if (!"UN`".equalsIgnoreCase(operOrg)) {
				strsql.append(" and ( ");
				if (operOrg != null && operOrg.length() > 3) {
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int j = 0; j < temp.length; j++) {
						if (temp[j] != null && temp[j].length() > 0) {
							tempSql.append(historybo.getSubSql(temp[j], headSetList));
						}
					}
					if (tempSql.length() > 0)
						strsql.append(tempSql.substring(3));

				} else
					strsql.append("  1=2 ");
				strsql.append(" )");
			}
		}

		if (needcondition.length() > 0)
			strsql.append(" and " + needcondition);
		try(HSSFWorkbook wb = new HSSFWorkbook()) { // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font2);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			style2.setBottomBorderColor((short) 8);
			style2.setLeftBorderColor((short) 8);
			style2.setRightBorderColor((short) 8);
			style2.setTopBorderColor((short) 8);
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.CENTER);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderBottom(BorderStyle.THIN);
			style1.setBorderLeft(BorderStyle.THIN);
			style1.setBorderRight(BorderStyle.THIN);
			style1.setBorderTop(BorderStyle.THIN);
			style1.setBottomBorderColor((short) 8);
			style1.setLeftBorderColor((short) 8);
			style1.setRightBorderColor((short) 8);
			style1.setTopBorderColor((short) 8);
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

			HSSFCellStyle styleN = dataStyle(wb);
			styleN.setAlignment(HorizontalAlignment.RIGHT);
			styleN.setWrapText(true);
			HSSFDataFormat df = wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));

			HSSFCellStyle styleCol0 = dataStyle(wb);
			HSSFFont font0 = wb.createFont();
			font0.setFontHeightInPoints((short) 5);
			styleCol0.setFont(font0);
			styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
			styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle styleCol0_title = dataStyle(wb);
			styleCol0_title.setFont(font2);
			styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
			styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle styleF1 = dataStyle(wb);
			styleF1.setAlignment(HorizontalAlignment.RIGHT);
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = wb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

			HSSFCellStyle styleF2 = dataStyle(wb);
			styleF2.setAlignment(HorizontalAlignment.RIGHT);
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = wb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

			HSSFCellStyle styleF3 = dataStyle(wb);
			styleF3.setAlignment(HorizontalAlignment.RIGHT);
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = wb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

			HSSFCellStyle styleF4 = dataStyle(wb);
			styleF4.setAlignment(HorizontalAlignment.RIGHT);
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = wb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

			HSSFCellStyle styleF5 = dataStyle(wb);
			styleF5.setAlignment(HorizontalAlignment.RIGHT);
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = wb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

			sheet.setColumnWidth((short) 0, (short) 1000);//标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
			HSSFPatriarch patr = sheet.createDrawingPatriarch();

			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell((short) 0);
			HSSFComment comm = null;

			String fieldExplain = "";

			ArrayList codeCols = new ArrayList();
			for (int i = 0; i < fieldlist.size(); i++) {
				field = (FieldItem) fieldlist.get(i);
				String fieldName = field.getItemid().toLowerCase();
				String fieldLabel = field.getItemdesc();

				if ("b0110".equalsIgnoreCase(fieldName) || "e0122".equalsIgnoreCase(fieldName))
					sheet.setColumnWidth((short) (i + 1), (short) 5000);

				cell = row.createCell((short) (i));

				cell.setCellValue(cellStr(fieldLabel));
				cell.setCellStyle(style2);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 2), 0, (short) (i + 3), 1));
				comm.setString(new HSSFRichTextString(fieldName));
				cell.setCellComment(comm);
				if (!"0".equals(field.getCodesetid()))
					codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString() + ":" + fieldName);
			}

			try {
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
				//bug32265  导出历史数据部门多级显示
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122))
					display_e0122 = "0";
				this.frowset = dao.search("select name from template_table where tabid=" + tabid);
				if (this.frowset.next())
					cname = this.frowset.getString("name");
				int rowCount = 1;
				//处理人员库 显示人员库的描述
				String strsqls = strsql.toString();
				if (strsqls.indexOf("basepre") != -1)
					strsqls = strsqls.replaceFirst("basepre", "(select dbname from dbname where upper(pre)=upper(basepre) ) basepre");
				this.frowset = dao.search(strsqls);

				while (this.frowset.next()) {
					row = sheet.createRow(rowCount++);
					row.setHeightInPoints(30);

					for (int i = 0; i < fieldlist.size(); i++) {
						field = (FieldItem) fieldlist.get(i);
						String fieldName = field.getItemid().toLowerCase();
						String itemtype = field.getItemtype();
						int decwidth = field.getDecimalwidth();
						String codesetid = field.getCodesetid();

						//		    String pri = this.userView.analyseFieldPriv(fieldName);
						//		    if (pri.equals("1"))// 只读
						//		    {
						//
						//		    }

						cell = row.createCell((short) (i));
						if ("N".equals(itemtype)) {
							if (decwidth == 0)
								cell.setCellStyle(styleN);
							else if (decwidth == 1)
								cell.setCellStyle(styleF1);
							else if (decwidth == 2)
								cell.setCellStyle(styleF2);
							else if (decwidth == 3)
								cell.setCellStyle(styleF3);
							else if (decwidth == 4)
								cell.setCellStyle(styleF4);
							// else if(decwidth==5)
							// cell.setCellStyle(styleF5);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							Double value = 0.0;
							try {
								value = this.frowset.getDouble(fieldName);
							} catch (Exception e) {
								String doubles = this.frowset.getString(fieldName);
								if (doubles != null && !"".equals(doubles))
									value = Double.parseDouble(doubles);
							}
							if (value == null)
								cell.setCellValue(0.0);
							else
								cell.setCellValue(value);
						} else if ("D".equals(itemtype)) {
							cell.setCellStyle(style1);
							//不能直接以date类型进行获取，因为数据表中某些字段可能存储多个数据，以text格式进行存储，导致类型转换异常，故应该用String类型接收，再进行转换 20150907 liuzy
							Date date = null;
							try {
								date = this.frowset.getDate(fieldName);
							} catch (Exception e) {
								try {
									String fname = this.frowset.getString(fieldName);
									if (fname != null && !"".equals(fname)) {
										date = sdf.parse(fname);
									}
								} catch (ParseException e1) {
								}
							}
							if (date == null) {
								cell.setCellValue("");
							} else {
								String value = "";
								if ("lasttime".equals(fieldName))
									value = sdf.format(date);
								else
									value = sdf2.format(date);
								cell.setCellValue(new HSSFRichTextString(value));
							}

						} else {
							String value = this.frowset.getString(fieldName);
							if (value != null) {
								String codevalue = value;
								if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid)) {
									//bug32265  导出历史数据部门多级显示
									if ("UM".equals(codesetid.toUpperCase())) {
										if (Integer.parseInt(display_e0122) == 0) {
											value = AdminCode.getCodeName(codesetid, codevalue) != null ? AdminCode.getCodeName(codesetid, codevalue) : "";
										} else {
											CodeItem item = AdminCode.getCode(codesetid, codevalue, Integer.parseInt(display_e0122));
											if (item != null) {
												value = item.getCodename();
											} else {
												value = AdminCode.getCodeName(codesetid, codevalue) != null ? AdminCode.getCodeName(codesetid, codevalue) : "";
											}

										}
									} else
										value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
								}
								cell.setCellValue(new HSSFRichTextString(value));
							}
							cell.setCellStyle(style1);
						}

					}
				}
				rowCount--;
				rowCount = 1000;//默认设置1000行代码型指标有下拉框
				int index = 0;
				String[] lettersUpper = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN"};
				/**导出数据的时候影响程序运行速度
				 for (int n = 0; n < codeCols.size(); n++)
				 {
				 String codeCol = (String) codeCols.get(n);
				 String[] temp = codeCol.split(":");
				 String codesetid = temp[0];
				 int codeCol1 = Integer.valueOf(temp[1]).intValue();
				 String filename = temp[2];
				 StringBuffer codeBuf = new StringBuffer();
				 if (!codesetid.equalsIgnoreCase("UM") && !codesetid.equalsIgnoreCase("UN") && !codesetid.equalsIgnoreCase("@k"))
				 {
				 codeBuf.append("select count(*) from codeitem where upper(codesetid)='" + codesetid.toUpperCase() + "' and codeitemid=childid  ");
				 this.frowset = dao.search(codeBuf.toString());
				 if(this.frowset.next())
				 if(this.frowset.getInt(1)<200)
				 {
				 codeBuf.setLength(0);
				 codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where upper(codesetid)='" + codesetid.toUpperCase() + "' and codeitemid=childid  order by codeitemid ");
				 }else{
				 continue;
				 }

				 } else
				 {
				 if(!codesetid.equalsIgnoreCase("UN")){
				 codeBuf.append("select count(*) from organization where upper(codesetid)='" + codesetid.toUpperCase()
				 + "' and  upper(codesetid) not in (select parentid from organization where upper(codesetid)='" + codesetid.toUpperCase() + "'  ) ");
				 this.frowset = dao.search(codeBuf.toString());

				 if(this.frowset.next()){
				 if(this.frowset.getInt(1)<200)
				 {
				 codeBuf.setLength(0);
				 codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='" + codesetid.toUpperCase()
				 + "' and  upper(codesetid) not in (select parentid from organization where upper(codesetid)='" + codesetid.toUpperCase() + "') order by codeitemid ");
				 }else{
				 continue;
				 }
				 }
				 }
				 else if(codesetid.equalsIgnoreCase("UN"))
				 {
				 codeBuf.append("select count(*) from organization where upper(codesetid)='UN'");
				 this.frowset = dao.search(codeBuf.toString());
				 if(this.frowset.next())
				 if(this.frowset.getInt(1)==1)
				 {
				 codeBuf.setLength(0);
				 codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='UN'");
				 }
				 else if(this.frowset.getInt(1)<200)
				 {
				 codeBuf.setLength(0);
				 codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='" + codesetid.toUpperCase()
				 + "' and  upper(codesetid) not in (select parentid from organization where upper(codesetid)='" + codesetid.toUpperCase() + "'  ) order by codeitemid");
				 }else{
				 continue;
				 }
				 }
				 }

				 this.frowset = dao.search(codeBuf.toString());

				 int m = 0;
				 while (this.frowset.next())
				 {
				 row = sheet.getRow(m + 0);
				 if(row==null)
				 row = sheet.createRow(m + 0);
				 cell = row.createCell((short) (208+index));
				 cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
				 m++;
				 }

				 sheet.setColumnWidth((short)(208+index), (short)0);

				 if(m>0){
				 int div = 0;
				 int mod = 0;
				 div = index/26;
				 mod = index%26;
				 String strFormula = "$"+lettersUpper[7+div]+""+lettersUpper[mod]+"$1:$"+lettersUpper[7+div]+""+lettersUpper[mod]+"$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据

				 //		HSSFDataValidation data_validation = new HSSFDataValidation((short) 1, (short) codeCol1, (short) rowCount, (short) codeCol1); // 定义生成下拉筐的范围
				 //		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
				 //		data_validation.setFirstFormula(strFormula);
				 //		data_validation.setSecondFormula(null);
				 //		data_validation.setExplicitListFormula(true);
				 //		data_validation.setSurppressDropDownArrow(false);
				 //		data_validation.setEmptyCellAllowed(false);
				 //		data_validation.setShowPromptBox(false);
				 //		sheet.addValidationData(data_validation);

				 CellRangeAddressList addressList = new CellRangeAddressList( 1, rowCount, codeCol1, codeCol1);
				 DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				 HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				 dataValidation.setSuppressDropDownArrow(false);
				 sheet.addValidationData(dataValidation);
				 }
				 index++;
				 }**/

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			cname = cname.replace("\\", "").replace("/", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "");
			String outName = cname + "_";//liuyz bug32153 生成pdf和excel名称应按照平台统一规范，例如：模板名称_登录用户.pdf
			outName += this.userView.getUserName() + ".xls";

			try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName)) {
				for (int i = 0; i < fieldlist.size(); i++) {
					sheet.autoSizeColumn((short) i);
				}
				wb.write(fileOut);
			}
			//outName = outName.replace(".xls", "#");人事异动安全改造,这些不用了
//		getFormHM().put("outName", SafeCode.decode(PubFunc.encrypt(outName)));
			//20/3/18 xus vfs改造
			getFormHM().put("outName", PubFunc.encrypt(outName));
			sheet = null;
		} catch (IOException e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	    public HSSFRichTextString cellStr(String context)
	    {
	
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	    }
	
	    public String decimalwidth(int len)
	    {
	
		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
		    decimal.append(".");
		for (int i = 0; i < len; i++)
		{
		    decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	    }
	    public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
	    {
	
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		return style;
    }
}

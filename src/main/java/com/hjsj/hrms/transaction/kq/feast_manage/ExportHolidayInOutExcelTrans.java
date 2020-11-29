package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description:导出模板
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2013-05-30 15:00:00
 * </p>
 * 
 * @author yhj
 * @version 1.0
 * 
 */
public class ExportHolidayInOutExcelTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            String sqlstr = (String) this.getUserView().getHm().get("key_kq_sql1");	
            String colums = (String) this.getUserView().getHm().get("key_kq_sql2");	
            String tablename = (String) this.getFormHM().get("tablename");
            tablename = tablename != null ? tablename : "Q17";
            HashMap calcuItemMap = new HashMap();
            ArrayList list = new ArrayList();
            sqlstr = SafeCode.decode(sqlstr);
            sqlstr = PubFunc.keyWord_reback(sqlstr);
            
            /** 项目过滤 */
            ArrayList fieldlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
            if ("Q05".equalsIgnoreCase(tablename)) {
                fieldlist = CollectRegister.newFieldItemList(fieldlist, this.frameconn);
            } else if ("Q17".equalsIgnoreCase(tablename)) {
                fieldlist = RegisterInitInfoData.newFieldItemList(fieldlist, this.userView, this.frameconn);
            }
            
            KqParameter kq_paramter = new KqParameter(this.userView, "", this.getFrameconn());
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_gno = (String) hashmap.get("g_no");
            
            FieldItem field_gno = new FieldItem();
            field_gno.setItemid("f1");
            field_gno.setItemdesc("工号");
            field_gno.setItemtype("A");
            field_gno.setCodesetid("0");
            field_gno.setVisible(true);
            
            //把工号指标固定插入到a0101之后
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if ("a0101".equalsIgnoreCase(field.getItemid())) {
                    fieldlist.add(i + 1, field_gno);
                }
            }
            // export_limits:设置可以导出的只读指标项
            HashMap readOnlyFldsCanExport = new HashMap();
            String export_limits = SystemConfig.getPropertyValue("export_limits");
            if (export_limits != null && !"".equals(export_limits)) {
                String[] readOnlyFlds = export_limits.split(",");
                for (int m = 0; m < readOnlyFlds.length; m++) {
                    String temp = readOnlyFlds[m].trim();
                    if (temp.length() > 0)
                        readOnlyFldsCanExport.put(temp.toUpperCase(), "");
                }
            }

            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (SystemConfig.getPropertyValue("excel_template_limit") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_limit"))) {
                    // 去除公式计算项了
                    if (calcuItemMap.get(field.getItemid().toLowerCase()) != null)
                        continue;
                    
                    // 去除只读项
                    if (readOnlyFldsCanExport.size() > 0) {
                        if (readOnlyFldsCanExport.get(field.getItemid().toUpperCase()) == null)// 不属于允许导出的只读项
                            continue;
                    } else
                        // 没有设置允许导出的只读项
                        continue;

                }
                
                // 前台查询的所有列colums,而field则是业务字典里的所有字段
                if (colums.indexOf(field.getItemid()) == -1) {
                    continue;
                }
                
                // 把假期类型遍历出来
                if (!field.isVisible() && !"q1709".equalsIgnoreCase(field.getItemid())) {
                    continue;
                }
                
                // if(field.getItemid().equalsIgnoreCase("q03z3")||field.getItemid().equalsIgnoreCase("c010k")||field.getItemid().equalsIgnoreCase("q03z5")){
                // continue;
                // }
                // c010k 可以导出，c010k是A01主集中的信息
                if ("q03z3".equalsIgnoreCase(field.getItemid()) || "q03z5".equalsIgnoreCase(field.getItemid()) || "modtime".equalsIgnoreCase(field.getItemid()) || "modusername".equalsIgnoreCase(field.getItemid())) {
                    continue;
                }
                
                if ("nbase".equalsIgnoreCase(field.getItemid())) {
                    continue;
                }
                list.add(field);
            }

            HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
            HSSFSheet sheet = wb.createSheet();
            // sheet.setProtect(true);
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
            styleN.setLocked(false);

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

            sheet.setColumnWidth(0, 0);
            HSSFPatriarch patr = sheet.createDrawingPatriarch();

            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(cellStr("主键标识串"));
            cell.setCellStyle(style2);

            String fieldExplain = "";
            HSSFComment comm = null;
            ArrayList codeCols = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                FieldItem field = (FieldItem) list.get(i);
                String fieldName = field.getItemid().toLowerCase();
                String fieldLabel = field.getItemdesc();
                if(!"f1".equals(fieldName)){
                fieldExplain = DataDictionary.getFieldItem(fieldName).getExplain();
	                if (SystemConfig.getPropertyValue("excel_template_desc") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_desc")) && fieldExplain != null && fieldExplain.trim().length() > 0) {
	                    fieldLabel += "\r\n如：" + fieldExplain;
	                    sheet.setColumnWidth(i + 1, 5000);
	                }
                }

                cell = row.createCell(i + 1);
                // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(cellStr(fieldLabel));
                cell.setCellStyle(style2);
                comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 2), 0, (short) (i + 3), 1));
                comm.setString(new HSSFRichTextString(fieldName));
                cell.setCellComment(comm);
                if (!"0".equals(field.getCodesetid()))
                    codeCols.add(field.getCodesetid() + ":" + new Integer(i + 1).toString());
            }
            HashMap dbMap = new HashMap();
            try {
                int rowCount = 1;
                ContentDAO dao = new ContentDAO(this.frameconn);

                String dbSql = "select pre , dbname  from dbname";
                this.frowset = dao.search(dbSql);
                while (this.frowset.next()) {
                    dbMap.put(this.frowset.getString("pre").toLowerCase(), this.frowset.getString("dbname"));
                }
                sqlstr = sqlstr + " order by i,b0110,e0122,e01a1,a0100";
                RowSet rset = dao.search(sqlstr);
                while (rset.next()) {
                    String nASE = rset.getString("NBASE");
                    String a0100 = rset.getString("A0100");
                    String q03z0 = "";// rset.getString("q03z0");
                    String b0110 = rset.getString("b0110");
                    String e0122 = rset.getString("e0122");

                    String flag = nASE + "|" + a0100 + "|" + q03z0;
                    b0110 = b0110 != null ? AdminCode.getCodeName("UN", b0110) : "";
                    e0122 = e0122 != null ? AdminCode.getCodeName("UM", e0122) : "";
                    row = sheet.getRow(rowCount);
                    if (row == null)
                        row = sheet.createRow(rowCount);
                    rowCount++;

                    cell = row.createCell(0);
                    // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellValue(cellStr(flag));
                    cell.setCellStyle(style1);

                    for (int i = 0; i < list.size(); i++) {
                        FieldItem field = (FieldItem) list.get(i);
                        String fieldName = field.getItemid().toLowerCase();
                        String itemtype = "";
                        int decwidth = 0;
                        String codesetid = "";
                        //szk获取工号参数
                        if(fieldName == "f1"){
                        	 itemtype = field.getItemtype();
                             decwidth = 0;
                             codesetid = field.getCodesetid();
                        }
                        else{
                        itemtype = DataDictionary.getFieldItem(fieldName).getItemtype();
                         decwidth = DataDictionary.getFieldItem(fieldName).getDecimalwidth();
                         codesetid = DataDictionary.getFieldItem(fieldName).getCodesetid();
                        }
                        cell = row.createCell(1 + i);
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
                            double d = rset.getDouble(fieldName);
                            if (d != 0) {
                                cell.setCellValue(d);
                            } else {
                                cell.setCellValue("0.0");
                            }
                        } else {
                            String value = "";
                            if (!"D".equalsIgnoreCase(itemtype))
                                value = rset.getString(fieldName);
                            else {
                                value = DateUtils.FormatDate(rset.getDate(fieldName));
                                value = null == value ? "" : value;
                            }

                            if (value != null) {
                                String codevalue = value;
                                if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid) && !"nbase".equalsIgnoreCase(fieldName))
                                {
                                    value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
                                } else if ("nbase".equalsIgnoreCase(fieldName)) {
                                    value = dbMap.get(codevalue.toLowerCase()) != null ? (String) dbMap.get(codevalue.toLowerCase()) : "";
                                } else if ("D".equalsIgnoreCase(itemtype)) {
                                    // 如果是日期型字段，则截取前10个字符
                                    if (!"".equals(value))                                    
                                        value = value.substring(0, 10);
                                }

                                cell.setCellValue(new HSSFRichTextString(value));
                            }
                            cell.setCellStyle(style1);
                        }

                    }
                }
                rowCount--;
                int index = 0;
                String[] lettersUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
                for (int n = 0; n < codeCols.size(); n++) {
                    String codeCol = (String) codeCols.get(n);
                    String[] temp = codeCol.split(":");
                    String codesetid = temp[0];
                    int codeCol1 = Integer.valueOf(temp[1]).intValue();
                    StringBuffer codeBuf = new StringBuffer();
                    // 添加codesetid=27的年假类型判断
                    if ("27".equals(codesetid)) {
                        codeBuf.append("select item_id, item_name FROM kq_item  where sdata_src ='Q15'");
                    }
                    else if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@k".equalsIgnoreCase(codesetid) && !"@@".equals(codesetid) && !"27".equals(codesetid)) {
                        codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' and codeitemid=childid");
                    } else {
                        if (!"UN".equals(codesetid) && !"@@".equals(codesetid))
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid + "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
                        else if ("UN".equals(codesetid)) {
                            codeBuf.append("select count(*) from organization where codesetid='UN' AND codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
                            rset = dao.search(codeBuf.toString());
                            if (rset.next())
                                if (rset.getInt(1) <= 1) {
                                    codeBuf.setLength(0);
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
                                } else if (rset.getInt(1) > 1) {
                                    codeBuf.setLength(0);
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid + "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
                                }
                        } else if ("@@".equals(codesetid)) {
                            codeBuf.setLength(0);
                            codeBuf.append("select pre codeitemid, dbname codeitemdesc from dbname");
                        }
                    }
                    rset = dao.search(codeBuf.toString());

                    int m = 0;
                    while (rset.next()) {
                        row = sheet.getRow(m + 0);
                        if (row == null)
                            row = sheet.createRow(m + 0);

                        cell = row.createCell(208 + index);
                        if (codeBuf.toString().indexOf("kq_item") != -1) {
                            cell.setCellValue(new HSSFRichTextString(rset.getString("item_name")));
                        } else {
                            cell.setCellValue(new HSSFRichTextString(rset.getString("codeitemdesc")));
                        }

                        m++;
                    }
                    if (m > 1) {
                        sheet.setColumnWidth(208 + index, 0);
                        String strFormula = "$H" + lettersUpper[index] + "$1:$H" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
                        // HSSFDataValidation data_validation = new
                        // HSSFDataValidation(
                        // (short) 1, (short) codeCol1, (short) rowCount,
                        // (short) codeCol1); // 定义生成下拉筐的范围
                        // data_validation
                        // .setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
                        // data_validation.setFirstFormula(strFormula);
                        // data_validation.setSecondFormula(null);
                        // data_validation.setExplicitListFormula(true);
                        // data_validation.setSurppressDropDownArrow(false);
                        // data_validation.setEmptyCellAllowed(false);
                        // data_validation.setShowPromptBox(false);
                        // sheet.addValidationData(data_validation);

                        CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);
                        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                        dataValidation.setSuppressDropDownArrow(false);
                        sheet.addValidationData(dataValidation);
                    }
                    index++;
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            String outName = "holiday_" + PubFunc.getStrg();
            outName += this.userView.getUserName() + ".xls";

            try {
                FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
                wb.write(fileOut);
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
            //outName = outName.replace(".xls", "#");
          //xiexd 2014.09.15加密文件名
            outName = PubFunc.encrypt(outName);
            this.getFormHM().put("outName",outName);
            sheet = null;
            wb = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HSSFRichTextString cellStr(String context) {

        HSSFRichTextString textstr = new HSSFRichTextString(context);
        return textstr;
    }

    public String decimalwidth(int len) {

        StringBuffer decimal = new StringBuffer("0");
        if (len > 0)
            decimal.append(".");
        for (int i = 0; i < len; i++) {
            decimal.append("0");
        }
        decimal.append("_ ");
        return decimal.toString();
    }

    public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {

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

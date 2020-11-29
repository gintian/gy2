package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * <p>
 * Title:SearchBonusTrans.java
 * </p>
 * <p>
 * Description:奖金导出excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-06 13:00:00
 * </p>
 *
 * @author FanZhiGuo
 * @version 1.0
 */
public class ExportExcelTrans extends IBusiness {
    public void execute() throws GeneralException {

        String sql = (String) this.getFormHM().get("sql");
        sql = SafeCode.decode(sql);
        String bonusSet = (String) this.getFormHM().get("bonusSet");
        String jobnumFld = (String) this.getFormHM().get("jobnumFld");

        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		FileOutputStream fileOut=null;
        try {
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

            HSSFCellStyle style1 = wb.createCellStyle();
            style1.setFont(font2);
            style1.setAlignment(HorizontalAlignment.LEFT);
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

            String fieldEn = "dbname,b0110,e0122,a0101,";
            String fieldCn = "人员库,单位,部门,姓名,";
            if (jobnumFld.length() > 0) {
                fieldEn += jobnumFld + ",";
                fieldCn += "工号,";
            }

            ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
            for (int i = 0; i < list.size(); i++) {
                FieldItem fielditem = (FieldItem) list.get(i);
                Field field = fielditem.cloneField();
                String itemid = field.getName();
                if ("0".equals(this.userView.analyseFieldPriv(itemid, 0))
                        && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
                    continue;
                fieldEn += itemid + ",";
                fieldCn += field.getLabel() + ",";
            }
            fieldEn += "CreateUserName";
            fieldCn += "录入员";

            String[] fieldCnArray = fieldCn.split(",");
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(new HSSFRichTextString("序号"));
            cell.setCellStyle(style2);

            for (int i = 0; i < fieldCnArray.length; i++) {
                cell = row.createCell((short) (i + 1));
                cell.setCellValue(new HSSFRichTextString(fieldCnArray[i]));
                cell.setCellStyle(style2);
            }

            sql = sql.substring(0, sql.indexOf("order"));
            String sql1 = "select " + fieldEn + " from (" + sql + ") b";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            RowSet rs = dao.search(sql1);
            int rowCount = 1;
            String[] fieldEnArray = fieldEn.split(",");
            while (rs.next()) {
                row = sheet.createRow(rowCount);
                cell = row.createCell((short) 0);
                cell.setCellValue(new HSSFRichTextString(Integer.toString(rowCount++)));
                cell.setCellStyle(style2);

                cell = row.createCell((short) 1);
                cell.setCellValue(new HSSFRichTextString(rs.getString("dbname")));
                cell.setCellStyle(style1);
                int k = 1;
                for (; k < fieldEnArray.length - 1; k++) {
                    cell = row.createCell((short) (k + 1));
                    String itemid = fieldEnArray[k];
                    FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                    String itemType = fieldItem.getItemtype();
                    String codesetid = fieldItem.getCodesetid();
                    int decwidth = fieldItem.getDecimalwidth();
                    if ("N".equals(itemType)) {
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
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
                        if (rs.getString(fieldEnArray[k]) != null)
                            cell.setCellValue(new Double(PubFunc.round(rs.getString(fieldEnArray[k]), decwidth)).doubleValue());

                    } else if ("D".equals(itemType)) {
                        cell.setCellStyle(styleN);
                        Object date = rs.getObject(fieldEnArray[k]);
                        if (date != null) {
                            String value = DateStyle.dateformat(rs.getDate(fieldEnArray[k]), "yyyy-MM-dd");
                            cell.setCellValue(new HSSFRichTextString(value));
                        }

                    } else {
                        cell.setCellStyle(style1);
                        String value = rs.getString(fieldEnArray[k]);
                        if (!"0".equals(codesetid) && value != null)
                            value = AdminCode.getCode(codesetid, value) != null
                                    ? AdminCode.getCode(codesetid, value).getCodename() : "";
                        if (rs.getString(fieldEnArray[k]) != null)
                            cell.setCellValue(new HSSFRichTextString(value));
                    }
                }
                cell = row.createCell((short) (k + 1));
                cell.setCellStyle(style1);
                cell.setCellValue(new HSSFRichTextString(rs.getString("CreateUserName")));
            }
            String outName = "奖金信息.xls";

            fileOut = new FileOutputStream(
                    System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
            fileOut.close();

            getFormHM().put("outName", SafeCode.encode(PubFunc.encrypt(outName)));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        } finally {
            PubFunc.closeResource(wb);
            PubFunc.closeResource(fileOut);
        }

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

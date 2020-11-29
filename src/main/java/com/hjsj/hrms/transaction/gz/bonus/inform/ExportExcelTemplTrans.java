package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
import java.util.ArrayList;

//import org.apache.poi.hssf.util.HSSFDataValidation;

/**
 * <p>
 * Title:ExportExcelTemplTrans.java
 * </p>
 * <p>
 * Description:奖金导出excel模板
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
public class ExportExcelTemplTrans extends IBusiness {
    public void execute() throws GeneralException {

        String bonusSet = (String) this.getFormHM().get("bonusSet");
        String jobnumFld = (String) this.getFormHM().get("jobnumFld");
        ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);

        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
        FileOutputStream fileOut = null;
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
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(new HSSFRichTextString("人员库"));
            cell.setCellStyle(style2);

            cell = row.createCell((short) 1);
            cell.setCellValue(new HSSFRichTextString("姓名"));
            cell.setCellStyle(style2);

            int i = 2;

            if (jobnumFld != null && jobnumFld.trim().length() > 0) {
                cell = row.createCell((short) i++);
                cell.setCellValue(new HSSFRichTextString("工号"));
                cell.setCellStyle(style2);
            }

            cell = row.createCell((short) i++);
            cell.setCellValue(new HSSFRichTextString("金额"));
            cell.setCellStyle(style2);

            cell = row.createCell((short) i++);
            cell.setCellValue(new HSSFRichTextString("业务日期"));
            cell.setCellStyle(style2);

            int bonusItemCol = i;// 奖金项目的列号

            cell = row.createCell((short) i++);
            cell.setCellValue(new HSSFRichTextString("奖金项目"));
            cell.setCellStyle(style2);

            int bonusUnCol = 0;// 奖金审批单位列
            int moneyflagCol = 0;// 进工资总额标识
            int moneyflagCol2 = 0;// 进工资标识
            for (int k = 0; k < list.size(); k++) {
                FieldItem fielditem = (FieldItem) list.get(k);
                String itemid = fielditem.getItemid();
                if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
                    continue;
                if ("金额".equals(fielditem.getItemdesc()) || "业务日期".equals(fielditem.getItemdesc()) || "奖金项目".equals(fielditem.getItemdesc()) || "处理状态".equals(fielditem.getItemdesc()))
                    continue;
                if ("奖金审批单位".equals(fielditem.getItemdesc()))
                    bonusUnCol = i;
                if ("进工资总额标识".equals(fielditem.getItemdesc()))
                    moneyflagCol = i;
                if ("进工资标识".equals(fielditem.getItemdesc()))
                    moneyflagCol2 = i;

                cell = row.createCell((short) i++);
                cell.setCellValue(new HSSFRichTextString(fielditem.getItemdesc()));
                cell.setCellStyle(style2);
            }

            ArrayList dbList = getNbase();
            // 人员库下拉
            short m = 0;
            for (int x = 0; x < dbList.size(); x++) {
                String dbname = (String) dbList.get(x);
                row = sheet.getRow(m + 0);
                if (row == null)
                    row = sheet.createRow(m + 0);
                cell = row.createCell((short) 52);
                cell.setCellValue(new HSSFRichTextString(dbname));
                m++;
            }
            sheet.setColumnWidth((short) 52, (short) 0);
            String strFormula = "$BA$1:$BA$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
//	HSSFDataValidation data_validation = new HSSFDataValidation((short) 1, (short) 0, (short) 1000, (short) 0); // 定义生成下拉筐的范围
//	data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//	data_validation.setFirstFormula(strFormula);
//	data_validation.setSecondFormula(null);
//	data_validation.setExplicitListFormula(true);
//	data_validation.setSurppressDropDownArrow(false);
//	data_validation.setEmptyCellAllowed(false);
//	data_validation.setShowPromptBox(false);
//	sheet.addValidationData(data_validation);	

            CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 0, 0);
            DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
            HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
            dataValidation.setSuppressDropDownArrow(false);
            sheet.addValidationData(dataValidation);


            // 奖金项目下拉
            m = 0;
            ArrayList bonusItems = this.getCodeDescList("49");
            for (int h = 0; h < bonusItems.size(); h++) {
                String codeitemdesc = (String) bonusItems.get(h);
                row = sheet.getRow(m + 0);
                if (row == null)
                    row = sheet.createRow(m + 0);
                cell = row.createCell((short) 53);
                cell.setCellValue(new HSSFRichTextString(codeitemdesc));
                m++;
            }

            strFormula = "$BB$1:$BB$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
//	data_validation = new HSSFDataValidation((short) 1, (short) bonusItemCol, (short) 1000, (short) bonusItemCol); // 定义生成下拉筐的范围
//	data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//	data_validation.setFirstFormula(strFormula);
//	data_validation.setSecondFormula(null);
//	data_validation.setExplicitListFormula(true);
//	data_validation.setSurppressDropDownArrow(false);
//	data_validation.setEmptyCellAllowed(false);
//	data_validation.setShowPromptBox(false);
//	sheet.addValidationData(data_validation);

            addressList = new CellRangeAddressList(1, 1000, bonusItemCol, bonusItemCol);
            dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
            dataValidation = new HSSFDataValidation(addressList, dvConstraint);
            dataValidation.setSuppressDropDownArrow(false);
            sheet.addValidationData(dataValidation);

            sheet.setColumnWidth((short) 53, (short) 0);

            // 奖金审批单位下拉
            if (bonusUnCol != 0) {
                m = 0;
//	    ArrayList items = AdminCode.getCodeItemList("50");
                ArrayList items = this.getCodeDescList("50");
                for (int h = 0; h < items.size(); h++) {
//		CodeItem codeitem = (CodeItem) items.get(h);
//		String codeitemdesc = codeitem.getCodename();		
                    String codeitemdesc = (String) items.get(h);
                    row = sheet.getRow(m + 0);
                    if (row == null)
                        row = sheet.createRow(m + 0);

                    cell = row.createCell((short) 54);
                    cell.setCellValue(new HSSFRichTextString(codeitemdesc));
                    m++;
                }

                strFormula = "$BC$1:$BC$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
//	    data_validation = new HSSFDataValidation((short) 1, (short) bonusUnCol, (short) 1000, (short) bonusUnCol); // 定义生成下拉筐的范围
//	    data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//	    data_validation.setFirstFormula(strFormula);
//	    data_validation.setSecondFormula(null);
//	    data_validation.setExplicitListFormula(true);
//	    data_validation.setSurppressDropDownArrow(false);
//	    data_validation.setEmptyCellAllowed(false);
//	    data_validation.setShowPromptBox(false);
//	    sheet.addValidationData(data_validation);

                addressList = new CellRangeAddressList(1, 1000, bonusUnCol, bonusUnCol);
                dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                dataValidation.setSuppressDropDownArrow(false);
                sheet.addValidationData(dataValidation);

                sheet.setColumnWidth((short) 54, (short) 0);
            }

            // 进工资标志下拉
            if (moneyflagCol != 0 || moneyflagCol2 != 0) {
                m = 0;
                ArrayList items = this.getCodeDescList("45");
                for (int h = 0; h < items.size(); h++) {
                    String codeitemdesc = (String) items.get(h);
                    row = sheet.getRow(m + 0);
                    if (row == null)
                        row = sheet.createRow(m + 0);

                    cell = row.createCell((short) 55);
                    cell.setCellValue(new HSSFRichTextString(codeitemdesc));
                    m++;
                }

                strFormula = "$BD$1:$BD$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
                if (moneyflagCol != 0) {
//		data_validation = new HSSFDataValidation((short) 1, (short) moneyflagCol, (short) 1000, (short) moneyflagCol); // 定义生成下拉筐的范围
//		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//		data_validation.setFirstFormula(strFormula);
//		data_validation.setSecondFormula(null);
//		data_validation.setExplicitListFormula(true);
//		data_validation.setSurppressDropDownArrow(false);
//		data_validation.setEmptyCellAllowed(false);
//		data_validation.setShowPromptBox(false);
//		sheet.addValidationData(data_validation);

                    addressList = new CellRangeAddressList(1, 1000, moneyflagCol, moneyflagCol);
                    dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                    dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                    dataValidation.setSuppressDropDownArrow(false);
                    sheet.addValidationData(dataValidation);
                }
                if (moneyflagCol2 != 0) {
//		data_validation = new HSSFDataValidation((short) 1, (short) moneyflagCol2, (short) 1000, (short) moneyflagCol2); // 定义生成下拉筐的范围
//		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//		data_validation.setFirstFormula(strFormula);
//		data_validation.setSecondFormula(null);
//		data_validation.setExplicitListFormula(true);
//		data_validation.setSurppressDropDownArrow(false);
//		data_validation.setEmptyCellAllowed(false);
//		data_validation.setShowPromptBox(false);
//		sheet.addValidationData(data_validation);

                    addressList = new CellRangeAddressList(1, 1000, moneyflagCol2, moneyflagCol2);
                    dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                    dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                    dataValidation.setSuppressDropDownArrow(false);
                    sheet.addValidationData(dataValidation);
                }
                sheet.setColumnWidth((short) 55, (short) 0);
            }

            String outName = "奖金信息" + PubFunc.getStrg() + ".xls";

            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
            outName = outName.replace(".xls", "#");
            getFormHM().put("outName", SafeCode.decode(outName));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(fileOut);
            PubFunc.closeResource(wb);
        }
    }

    public ArrayList getCodeDescList(String codesetid) throws GeneralException {
        ArrayList list = new ArrayList();
        String sql = "select * from codeitem where codesetid='" + codesetid + "' order by codeitemid";
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            RowSet rs = dao.search(sql);
            while (rs.next()) {
                String codeitemdesc = rs.getString("codeitemdesc");
                list.add(codeitemdesc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    public ArrayList getNbase() throws GeneralException {
        ArrayList list = new ArrayList();
        String sql = "select * from dbname";
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            RowSet rs = dao.search(sql);
            while (rs.next()) {
                String codeitemdesc = rs.getString("dbname");
                list.add(codeitemdesc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
}

package com.hjsj.hrms.businessobject.kq.kqself;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class EducePlanInfoExcel {
    private Connection conn;
    private UserView   userView;
    private int        num = 0;

    public EducePlanInfoExcel(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 建立表格Excel
     * 
     * @param datelist
     * @param a_code
     * @param nbase
     * @return
     */
    public String createTableExcel(String sql, ArrayList fieldlist, String plan_id) throws GeneralException {
        String excel_filename = "kq_plan_" + com.hjsj.hrms.utils.PubFunc.getStrg() + this.userView.getUserName() + ".xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        short n = executeTableTitel(plan_id, sheet, workbook, fieldlist);
        n = executeTableDate(sql, n, sheet, workbook, fieldlist);
        executeTableEnd(plan_id, sheet, workbook, n);
        try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + excel_filename);
            workbook.write(fileOut);
            fileOut.close();
            sheet = null;
            workbook = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excel_filename;
    }

    /**
     * 表头
     * 
     * @param plan_id
     * @param sheet
     * @param workbook
     * @param fieldlist
     * @return
     */
    public short executeTableTitel(String plan_id, HSSFSheet sheet, HSSFWorkbook workbook, ArrayList fieldlist) {
        short n = 0;
        String sql = "select q2905,q2909 from q29 where q2901='" + plan_id + "'";
        String name = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            if (rs.next()) {
                name = rs.getString("q2905");
            }
        
            HSSFRow row = null;
            HSSFCell csCell = null;
            // 写标题
            HSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setColor(HSSFFont.COLOR_NORMAL);
            font.setBold(true);
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setFont(font);
            row = sheet.createRow(n);
            row.setHeight((short) 600);
            csCell = row.createCell(Short.parseShort(String.valueOf(0)));
            csCell.setCellStyle(cellStyle);
            // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
            csCell.setCellValue(name);
            int a = 0;
            short b = 0;
            int c = 0;
            short d = 4;
            short b1 = b;
            while (++b1 <= d) {
            	csCell = row.createCell(b1);
            }
            for (int a1 = a + 1; a1 <= c; a1++) {
            	row = sheet.createRow(a1);
            	b1 = b;
            	while (b1 <= d) {
            		csCell = row.createCell(b1);
            		b1++;
            	}
            }
            ExportExcelUtil.mergeCell(sheet, a, b, c, d);
            n++;
            row = sheet.createRow(n);
            row.setHeight((short) (40 * 10));
            csCell = row.createCell(Short.parseShort(String.valueOf(0)));
            // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue("序号");
            int r = 1;
            for (int i = 0; i < fieldlist.size(); i++) {
            	
            	FieldItem field = (FieldItem) fieldlist.get(i);
            	if ("1".equals(field.getState())) {
            		csCell = row.createCell(Short.parseShort(String.valueOf(r)));
            		// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		csCell.setCellStyle(cellStyle);
            		csCell.setCellValue(field.getItemdesc());
            		sheet.setColumnWidth((short) r, (short) (300 * 10));
            		r++;
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return ++n;
    }

    /**
     * 内容
     * 
     * @param sql
     * @param n
     * @param sheet
     * @param workbook
     * @param fieldlist
     * @return
     */
    private short executeTableDate(String sql, short n, HSSFSheet sheet, HSSFWorkbook workbook, ArrayList fieldlist) {
        HSSFRow row = null;
        HSSFCell csCell = null;
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFFont.COLOR_NORMAL);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        int num = 0;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);

            while (rs.next()) {
                row = sheet.createRow(n);
                row.setHeight((short) (35 * 10));
                csCell = row.createCell(Short.parseShort(String.valueOf(0)));
                // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                csCell.setCellStyle(cellStyle);
                csCell.setCellValue(++num);
                int r = 1;
                for (int i = 0; i < fieldlist.size(); i++) {

                    FieldItem field = (FieldItem) fieldlist.get(i);
                    if ("1".equals(field.getState())) {
                        csCell = row.createCell(Short.parseShort(String.valueOf(r)));
                        // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellStyle(cellStyle);
                        String value = "";
                        if ("q31z1".equalsIgnoreCase(field.getItemid()) || "q31z3".equalsIgnoreCase(field.getItemid()) || "q3105".equalsIgnoreCase(field.getItemid())) {
                            Date dd = rs.getDate(field.getItemid());
                            if (dd != null) {
                                value = DateUtils.format(dd, "yyyy.MM.dd");
                            }
                        } else if ("M".equals(field.getItemtype())) {
                            value = Sql_switcher.readMemo(rs, field.getItemid());
                            value = value != null && value.length() > 0 ? value : "";
                        } else if ("q31z5".equals(field.getItemid())) {
                            value = rs.getString(field.getItemid());
                            value = AdminCode.getCodeName("23", value);
                            value = value != null && value.length() > 0 ? value : "";
                        } else if ("q31z0".equals(field.getItemid())) {
                            value = rs.getString(field.getItemid());
                            value = AdminCode.getCodeName("30", value);
                            value = value != null && value.length() > 0 ? value : "";
                        } else if ("e01a1".equals(field.getItemid())) {
                            value = rs.getString(field.getItemid());
                            value = AdminCode.getCodeName("@K", value);
                            value = value != null && value.length() > 0 ? value : "";
                        } else if ("nbase".equals(field.getItemid())) {
                            value = rs.getString(field.getItemid());
                            value = AdminCode.getCodeName("@@", value);
                            value = value != null && value.length() > 0 ? value : "";
                        } else {
                            value = rs.getString(field.getItemid());
                            value = value != null && value.length() > 0 ? value : "";
                        }
                        value = value.replaceAll("<br>", "");
                        value = value.replaceAll("<br", "");
                        value = value.replaceAll("<b", "");
                        csCell.setCellValue(value);
                        r++;
                    }
                }
                n++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        this.num = num;
        return ++n;
    }

    public short executeTableEnd(String plan_id, HSSFSheet sheet, HSSFWorkbook workbook, short n) {
        String sql = "select q2911,q2909 from q29 where q2901='" + plan_id + "'";
        String createTime = "";
        String createemp = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            if (rs.next()) {
                Date date = rs.getDate("q2909");
                if (date != null) {
                    createTime = DateUtils.format(date, "yyyy.MM.dd");
                }
                createemp = rs.getString("q2911");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        HSSFRow row = null;
        HSSFCell csCell = null;
        // 写标题
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFFont.COLOR_NORMAL);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        row = sheet.createRow(n);
        csCell = row.createCell(Short.parseShort(String.valueOf(0)));
        csCell.setCellStyle(cellStyle);
        // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
        csCell.setCellValue("创建时间：" + createTime + ";  创建人：" + createemp + ";  计划人：" + this.num + " 人。");
        int a = n;
        short b = n;
        int c = 0;
        short d = 6;
        short b1 = b;
        while (++b1 <= d) {
            csCell = row.createCell(b1);
        }
        for (int a1 = a + 1; a1 <= c; a1++) {
            row = sheet.createRow(a1);
            b1 = b;
            while (b1 <= d) {
                csCell = row.createCell(b1);
                b1++;
            }
        }
        return ++n;
    }
}

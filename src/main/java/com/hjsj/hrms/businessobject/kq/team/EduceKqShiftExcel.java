package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EduceKqShiftExcel implements KqClassArrayConstant, KqClassConstant {
    private Connection conn;
    private UserView   userView;
    private ContentDAO dao;
    private float      tiems      = 0;
    private String     inwhere;
    private String     start_date = "";
    private String     end_date   = "";
    private String     ghField;

    public String getInwhere() {
        return inwhere;
    }

    public void setInwhere(String inwhere) {
        this.inwhere = inwhere;
    }

    public EduceKqShiftExcel(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.dao = new ContentDAO(this.conn);
        this.ghField = getGhField();
    }

    /**
     * 建立表格Excel
     * @param datelist
     * @param a_code
     * @param nbase
     * @return
     */
    public String createTableExcel(ArrayList datelist, String a_code, String nbase, String his) throws GeneralException {
        String excel_filename = getShiftDataExcelFileName();
        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = null;
        HSSFCell csCell = null;
        this.start_date = datelist.get(0).toString();
        this.end_date = datelist.get(datelist.size() - 1).toString();
        short n = executeTableTitel("排班", a_code, nbase, sheet, workbook); //写上表头 和 标题	

        int trN = 0;

        if ("1".equals(his)) {
            trN = getExcelDataInfoHis(datelist, a_code, nbase, sheet, workbook, n);
        } else {
            trN = getExcelDataInfo(datelist, a_code, nbase, sheet, workbook, n);
        }

        row = sheet.createRow(trN + 1);
        csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
        //		 csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
        csCell.setCellValue(" 合计（小时）：" + this.tiems);
        try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + excel_filename);
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
     * 表格头
     * @param title
     * @param sheet
     * @param workbook
     * @return
     */
    private short executeTableTitel(String title, String a_code, String nbase, HSSFSheet sheet, HSSFWorkbook workbook) {
        short n = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        //			写标题
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFFont.COLOR_NORMAL);
        font.setBold(true);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        row = sheet.createRow(n);
        csCell = row.createCell(Integer.parseInt(String.valueOf(3)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
        csCell.setCellValue(title);
        n++;
        n = getCodeMessage(a_code, nbase, sheet, workbook, n);
        n++;
        row = sheet.createRow(n);
        csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("日");
        csCell = row.createCell(Integer.parseInt(String.valueOf(1)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("一");
        csCell = row.createCell(Integer.parseInt(String.valueOf(2)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("二");
        csCell = row.createCell(Integer.parseInt(String.valueOf(3)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("三");
        csCell = row.createCell(Integer.parseInt(String.valueOf(4)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("四");
        csCell = row.createCell(Integer.parseInt(String.valueOf(5)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("五");
        csCell = row.createCell(Integer.parseInt(String.valueOf(6)));
        csCell.setCellStyle(cellStyle);
        //			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
        csCell.setCellValue("六");
        n++;

        n++;
        return n;
    }

    /**
     * 表格EXCEL主体
     * @param datelist
     * @param a_code
     * @param nbase
     * @param sheet
     * @param workbook
     * @param sn
     * @return
     * @throws GeneralException
     */
    private int getExcelDataInfo(ArrayList datelist, String a_code, String nbase, HSSFSheet sheet, HSSFWorkbook workbook, short sn)
            throws GeneralException {
        KqShiftClass kqShiftClass = new KqShiftClass(this.conn, this.userView);
        int theRows = datelist.size() / 7;
        int mod = datelist.size() % 7;
        if (mod > 0) {
            theRows = theRows + 2;
        }
        String fristday = datelist.get(0).toString();
        String end_day = datelist.get(datelist.size() - 1).toString();
        String flag = "1";
        if ("UN".equals(a_code) && (this.inwhere == null || this.inwhere.length() <= 0)) {
            flag = "0";
        }
        kqShiftClass.setWhere_c(this.inwhere);
        ArrayList recordlist = kqShiftClass.getRecord(datelist.size(), fristday, end_day, a_code, nbase,"","","");

        Date date = DateUtils.getDate(fristday, "yyyy.MM.dd");
        String FirstDay = KqUtilsClass.getWeekName(date);
        if (datelist.size() == 28) {
            if (!FirstDay.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday"))) {
                theRows = theRows + 1;
            }
        }

        String rest = KQRestOper.getRestStrTurn(FirstDay);
        if (rest.indexOf("7") != -1) {
            rest = "0,";
        }
        rest = rest.substring(0, 1);
        int theFirstDay = Integer.parseInt(rest);
        int theMonthLen = theFirstDay + datelist.size();
        int n = 0;
        int day = 0;
        String day_str = "";
        HSSFRow row = null;
        HSSFCell csCell = null;
        for (int i = 0; i < theRows; i++) {
            row = sheet.createRow(i + sn);
            row.setHeight((short) 0x179);
            for (int j = 0; j < 7; j++) {
                n++;
                if (n > theFirstDay && n <= theMonthLen) {
                    day = n - theFirstDay - 1;
                    day_str = datelist.get(day).toString();
                    String tsd_str = getTdStr(recordlist, day_str, flag);
                    csCell = row.createCell(j);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    csCell.setCellValue(tsd_str);
                    sheet.setColumnWidth(j, 30 * 8 * 20);
                } else {
                    csCell = row.createCell(j);
                    csCell.setCellValue("");
                }
            }
        }
        short trN = Short.parseShort(theRows + "");
        int ii = sn + trN;
        return ii;
    }

    /**
      * 表格EXCEL主体  历史
      * @param datelist
      * @param a_code
      * @param nbase
      * @param sheet
      * @param workbook
      * @param sn
      * @return
      * @throws GeneralException
      */
    private int getExcelDataInfoHis(ArrayList datelist, String a_code, String nbase, HSSFSheet sheet, HSSFWorkbook workbook,
            short sn) throws GeneralException {
        com.hjsj.hrms.businessobject.kq.team.historical.KqShiftClass kqShiftClass = new com.hjsj.hrms.businessobject.kq.team.historical.KqShiftClass(
                this.conn, this.userView);
        int theRows = datelist.size() / 7;
        int mod = datelist.size() % 7;
        if (mod > 0) {
            theRows = theRows + 1;
        }
        String fristday = datelist.get(0).toString();
        String end_day = datelist.get(datelist.size() - 1).toString();
        String flag = "1";
        if ("UN".equals(a_code) && (this.inwhere == null || this.inwhere.length() <= 0)) {
            flag = "0";
        }
        kqShiftClass.setWhere_c(this.inwhere);
        ArrayList recordlist = kqShiftClass.getRecord(datelist.size(), fristday, end_day, a_code, nbase);

        Date date = DateUtils.getDate(fristday, "yyyy.MM.dd");
        String FirstDay = KqUtilsClass.getWeekName(date);
        if (datelist.size() == 28) {
            if (!FirstDay.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday"))) {
                theRows = theRows + 1;
            }
        }

        String rest = KQRestOper.getRestStrTurn(FirstDay);
        if (rest.indexOf("7") != -1) {
            rest = "0,";
        }
        rest = rest.substring(0, 1);
        int theFirstDay = Integer.parseInt(rest);
        int theMonthLen = theFirstDay + datelist.size();
        int n = 0;
        int day = 0;
        String day_str = "";
        HSSFRow row = null;
        HSSFCell csCell = null;
        for (int i = 0; i < theRows; i++) {
            row = sheet.createRow(i + sn);
            row.setHeight((short) 0x179);
            for (int j = 0; j < 7; j++) {
                n++;
                if (n > theFirstDay && n <= theMonthLen) {
                    day = n - theFirstDay - 1;
                    day_str = datelist.get(day).toString();
                    String tsd_str = getTdStr(recordlist, day_str, flag);
                    csCell = row.createCell(j);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    csCell.setCellValue(tsd_str);
                    sheet.setColumnWidth(j, (int) ((30 * 8) / ((double) 1 / 20)));
                } else {
                    csCell = row.createCell(j);
                    csCell.setCellValue("");
                }
            }
        }
        short trN = Short.parseShort(theRows + "");
        int ii = sn + trN;
        return ii;
    }

    private String getTdStr(ArrayList recordlist, String day_str, String flag) throws GeneralException {
        StringBuffer str_html = new StringBuffer();
        if (!"3".equals(flag)) {
            str_html.append(day_str);
        }
        String a0100 = "";
        String nbase = "";
        if (!"0".equals(flag)) {
            for (int i = 0; i < recordlist.size(); i++) {
                HashMap hashmap = (HashMap) recordlist.get(i);
                String q03z0 = hashmap.get("q03z0").toString();
                if (day_str.equals(q03z0.trim())) {
                    String n_a0100 = (String) hashmap.get("a0100");
                    String n_nbase = (String) hashmap.get("nbase").toString();
                    if (a0100 != null && a0100.length() > 0) {
                        if (!a0100.equals(n_a0100)) {
                            ;
                        }
                        break;
                    } else {
                        a0100 = n_a0100;
                    }
                    if (nbase != null && nbase.length() > 0 && !"all".equalsIgnoreCase(nbase)) {
                        if (!nbase.equals(n_nbase)) {
                            ;
                        }
                        break;
                    } else {
                        nbase = n_nbase;
                    }
                    String name = (String) hashmap.get("name");
                    String onduty_1 = (String) hashmap.get("onduty_1");
                    String offduty_1 = (String) hashmap.get("offduty_1");
                    String onduty_2 = (String) hashmap.get("onduty_2");
                    String offduty_2 = (String) hashmap.get("offduty_2");
                    String onduty_3 = (String) hashmap.get("onduty_3");
                    String offduty_3 = (String) hashmap.get("offduty_3");
                    String onduty_4 = (String) hashmap.get("onduty_4");
                    String offduty_4 = (String) hashmap.get("offduty_4");
                    str_html.append("\n\r " + name);
                    if (onduty_1 != null && onduty_1.length() > 0 && offduty_1 != null && offduty_1.length() > 0) {
                        str_html.append("\n\r " + onduty_1 + "~" + offduty_1);
                        getWork_Time(onduty_1, offduty_1);
                    }
                    if (onduty_2 != null && onduty_2.length() > 0 && offduty_2 != null && offduty_2.length() > 0) {
                        str_html.append("\n\r " + onduty_2 + "~" + offduty_2);
                        getWork_Time(onduty_2, offduty_2);
                    }
                    if (onduty_3 != null && onduty_3.length() > 0 && offduty_3 != null && offduty_3.length() > 0) {
                        str_html.append("\n\r " + onduty_3 + "~" + offduty_3);
                        getWork_Time(onduty_3, offduty_3);
                    }
                    if (onduty_4 != null && onduty_4.length() > 4 && offduty_4 != null && offduty_4.length() > 4) {
                        str_html.append("\n\r " + onduty_4 + "~" + offduty_4);
                        getWork_Time(onduty_4, offduty_4);
                    }
                    break;
                }
            }
        } else {
            str_html.append("\n");
        }
        return str_html.toString();
    }

    /**
     * 返回信息
     * @param a_code
     * @param nbase
     * @param sheet
     * @param workbook
     * @param n
     * @return
     */
    private short getCodeMessage(String a_code, String nbase, HSSFSheet sheet, HSSFWorkbook workbook, short n) {
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
        HSSFRow row = null;
        HSSFCell csCell = null;
        row = sheet.createRow(n);
        csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
        
        if (a_code != null && a_code.indexOf("EP") == 0) {
        	String select_a0100 = a_code.substring(2);
            csCell.setCellValue("当前操作对象：" + kqUtilsClass.getACodeDesc("EP" + select_a0100, nbase));
        } else if (!"UN".equals(a_code)) 
		{
            csCell.setCellValue("当前操作对象：" + kqUtilsClass.getACodeDesc(a_code, nbase));
        }
        n++;
        
        return n;
    }

    private String getGhField() {
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        return para.getG_no();
    }
    
    private String getGh(String nbase, String A0100) {
        String sql = "SELECT " + ghField + " FROM " + nbase + "A01 WHERE A0100='" + A0100 + "'";
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                return rs.getString(ghField);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return null;
    }

    /**
     * 按记录方式
     * @param datelist
     * @param a_code
     * @param db_list
     * @return
     * @deprecated 兼容性保留，请不要再使用
     */
    @Deprecated
    public String returnRecordExcel(ArrayList datelist, String a_code, ArrayList db_list) throws GeneralException {
        return returnRecordExcel(datelist, a_code, db_list, "", "", "");
    }
    
    /**
     * 按记录方式
     * @param datelist
     * @param a_code
     * @param db_list
     * @param startDate 当班查询日期
     * @param name 当班查询班次
     * @selectShowBar 当班查询
     * @return
     */
    public String returnRecordExcel(ArrayList datelist, String a_code, ArrayList db_list, String startDate, String name, String selectShowBar) throws GeneralException {
        String excel_filename = getShiftDataExcelFileName();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        short n = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        n = executeRecordTitel1("排班", datelist, sheet, workbook);
        String fristday = datelist.get(0).toString();
        String end_day = datelist.get(datelist.size() - 1).toString(); 
        ArrayList a0100List = getA0100Record(fristday, end_day, a_code, db_list, name, selectShowBar, datelist.size());
        
        for (int i = 0; i < a0100List.size(); i++) {
            row = sheet.createRow(n);
            row.setHeight((short) 0x149);
            HashMap hash = (HashMap) a0100List.get(i);
            String a0100 = (String) hash.get("a0100");
            String nbase = (String) hash.get("nbase");
            String a0101 = (String) hash.get("a0101");
            String e0122 = (String) hash.get("e0122");
            String b0110 = (String) hash.get("b0110");
            
            csCell = row.createCell(0);
            csCell.setCellValue(a0101);
            
            csCell = row.createCell(1);
            csCell.setCellValue(this.getGh(nbase, a0100));
            
            csCell = row.createCell(2);
            csCell.setCellValue(AdminCode.getCodeName("UN", b0110));
            
            csCell = row.createCell(3);
            csCell.setCellValue(AdminCode.getCodeName("UM", e0122));
            
            KqShiftClass kqShiftClass = new KqShiftClass(this.conn, this.userView);
            ArrayList recordlist = kqShiftClass.getRecord(datelist.size(), fristday, end_day, "EP" + a0100, nbase, startDate, name, selectShowBar);
            
            int cellOffSet = 3;
            for (int j = 1; j <= datelist.size(); j++) {
                String day_str = datelist.get(j - 1).toString();
                String tsd_str = getTdStr(recordlist, day_str, "3");
                csCell = row.createCell(j + cellOffSet);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING); 
                csCell.setCellValue(tsd_str);
                sheet.setColumnWidth((int) j + cellOffSet, (int) ((30 * 8) / ((double) 1 / 20))); 
            }
            n++;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + excel_filename);
            workbook.write(fileOut);
            fileOut.close();
            sheet = null;
            workbook = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excel_filename;
    }

    public String returnRecordExcelHis(ArrayList datelist, String a_code, ArrayList db_list) throws GeneralException {
        String excel_filename = getShiftDataExcelFileName();
        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        short n = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        n = executeRecordTitel1("排班", datelist, sheet, workbook);
        String fristday = datelist.get(0).toString();
        String end_day = datelist.get(datelist.size() - 1).toString();
        ArrayList a0100List = getA0100RecordHis(fristday, end_day, a_code, db_list);
        //n++;
        for (int i = 0; i < a0100List.size(); i++) {
            row = sheet.createRow(n);
            row.setHeight((short) 0x149);
            HashMap hash = (HashMap) a0100List.get(i);
            String a0100 = (String) hash.get("a0100");
            String nbase = (String) hash.get("nbase");
            String a0101 = (String) hash.get("a0101");
            csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
            //			  csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
            csCell.setCellValue(a0101);
            csCell = row.createCell(Integer.parseInt(String.valueOf(1)));
            csCell.setCellValue(this.getGh(nbase, a0100));
            com.hjsj.hrms.businessobject.kq.team.historical.KqShiftClass kqShiftClass = new com.hjsj.hrms.businessobject.kq.team.historical.KqShiftClass(
                    this.conn, this.userView);
            ArrayList recordlist = kqShiftClass.getRecord(datelist.size(), fristday, end_day, "EP" + a0100, nbase);
            for (int j = 1; j <= datelist.size(); j++) {
                String day_str = datelist.get(j - 1).toString();
                String tsd_str = getTdStr(recordlist, day_str, "3");
                csCell = row.createCell(j + 1);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(tsd_str);
                sheet.setColumnWidth((int) j + 1, (int) ((30 * 8) / ((double) 1 / 20)));
            }
            n++;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + excel_filename);
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
    * 表格头
    * @param title
    * @param sheet
    * @param workbook
    * @return
    */
    private short executeRecordTitel1(String title, ArrayList datelist, HSSFSheet sheet, HSSFWorkbook workbook) {
        short n = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        //				写标题
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFFont.COLOR_NORMAL);
        font.setBold(true);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        row = sheet.createRow(n);
        csCell = row.createCell(Integer.parseInt(String.valueOf(4)));
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue(title);
        n++;
        row = sheet.createRow(n);
        
        csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("姓名");
        
        csCell = row.createCell(Integer.parseInt(String.valueOf(1)));
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("工号");
        
        csCell = row.createCell(Integer.parseInt(String.valueOf(2)));
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue(ResourceFactory.getProperty("b0110.label"));
        
        csCell = row.createCell(Integer.parseInt(String.valueOf(3)));
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue(ResourceFactory.getProperty("e0122.label"));
        
        
        for (int i = 0; i < datelist.size(); i++) {
            csCell = row.createCell(Integer.parseInt(String.valueOf(i + 4)));
            csCell.setCellStyle(cellStyle);
            //					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
            csCell.setCellValue(datelist.get(i).toString());
        }
        n++;
        return n;
    }

    /**
        * 当前页的纪录
        * @param start_day
        * @param end_day
        * @param a_code
        * @param db_list
        * @param pagesize
        * @param curpage
        * @return
        * @throws GeneralException
        * @deprecated 兼容性保留，请不要再使用 
        */
    @Deprecated
    private ArrayList getA0100Record(String start_day, String end_day, String a_code, ArrayList db_list) throws GeneralException {
        return getA0100Record(start_day, end_day, a_code, db_list, "", "", 0);
    }
    
    /**
     * 当前页的纪录
     * @param start_day
     * @param end_day
     * @param a_code
     * @param db_list
     * @param pagesize
     * @param curpage
     * @return
     * @throws GeneralException
     */
     private ArrayList getA0100Record(String start_day, String end_day, String a_code, ArrayList db_list, String name,String selectShowBar, int dateSize) throws GeneralException {
         ArrayList list = new ArrayList();
         
         StringBuffer day_where = new StringBuffer();
         day_where.append(" and " + kq_employ_shift_q03z0 + ">='" + start_day + "'");
         day_where.append(" and " + kq_employ_shift_q03z0 + "<='" + end_day + "'");
         if(StringUtils.isNotEmpty(start_date)&&"1".equals(selectShowBar)&& StringUtils.isNotBlank(name) &&dateSize==7){
             start_date = start_date.replace("-",".");
             day_where.append(" and a0100 in (select a0100 from kq_employ_shift LEFT JOIN kq_class ON kq_employ_shift.class_id=kq_class.class_id ");
             day_where.append("where q03z0='"+start_date+"' and ");
             day_where.append( "name in ( "+name+" ))");
         }
         
         String kqTypeWhr = new KqUtilsClass(this.conn, this.userView).getKqTypeWhere(KqConstant.KqType.STOP, true);
         
         KqShiftClass kqShiftClass = new KqShiftClass(this.conn, this.userView);
         
         StringBuffer sql = new StringBuffer();
         String ltable = kq_employ_shift_table;//目标表     
         for (int i = 0; i < db_list.size(); i++) {
             String nbase = (String) db_list.get(i);
             String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
             if (!whereA0100In.toUpperCase().contains(" WHERE ")) {
                 whereA0100In = whereA0100In + " WHERE 1=1";
             }
             whereA0100In = whereA0100In + kqTypeWhr;
             
             String code_where = kqShiftClass.getCodeItemWhere(a_code, (String) db_list.get(i));
             //linbz 右连接主集获取a0000排序
             sql.append("select '" + nbase + "' nbase,b.a0100,b.a0000,b.a0101,b.e0122,b.b0110 from (");
             
             sql.append("select distinct a0100 ");
             sql.append(" from " + ltable + " ");
             
             sql.append(" where nbase='").append(nbase).append("'");
             if (day_where != null && day_where.length() > 0) {
                 sql.append(" " + day_where);
             }
             if (code_where != null && code_where.length() > 0) {
                 sql.append(" and " + code_where);
             }
             if (this.inwhere != null && this.inwhere.length() > 0) {
                 sql.append(this.inwhere);
             }
             
             sql.append(" and   a0100 in(select distinct a0100 " + whereA0100In + ") ");
             sql.append(") a ");
             sql.append(" right join ");
             sql.append(" (select a0100,a0000,a0101,e0122,b0110 " + whereA0100In);
             
             if (code_where != null && code_where.length() > 0) {
                 sql.append(" and " + code_where);
             }
             if (this.inwhere != null && this.inwhere.length() > 0) {
                 sql.append(this.inwhere);
             }
             
             sql.append(" ) b ");
             
             sql.append(" on a.a0100=b.a0100 ");
             sql.append(" where  a.a0100 is not null ");
             sql.append(" union ");
         }
         if (sql != null && sql.toString().length() > 0) {
             sql.setLength(sql.length() - 7);
         }
         
         StringBuffer sqlAll = new StringBuffer();
         sqlAll.append(" select * from ( ");
         sqlAll.append(sql.toString());
         sqlAll.append(" ) x ");
         sqlAll.append(" order by nbase,a0000,a0100 ");
         
         ContentDAO dao = new ContentDAO(this.conn);
         RowSet rs = null;
         HashMap hashmap = null;
         try {
             rs = dao.search(sqlAll.toString());
             while (rs.next()) {
                 hashmap = new HashMap();
                 hashmap.put("a0100", StringUtils.isNotBlank(rs.getString("a0100")) ? rs.getString("a0100") : "");
                 hashmap.put("a0101", StringUtils.isNotBlank(rs.getString("a0101")) ? rs.getString("a0101") : "");
                 hashmap.put("nbase", StringUtils.isNotBlank(rs.getString("nbase")) ? rs.getString("nbase") : "");
                 hashmap.put("e0122", StringUtils.isNotBlank(rs.getString("e0122")) ? rs.getString("e0122") : "");
                 hashmap.put("b0110", StringUtils.isNotBlank(rs.getString("b0110")) ? rs.getString("b0110") : "");
                 list.add(hashmap);
             }
         } catch (Exception e) {
             e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
         } finally {
             PubFunc.closeDbObj(rs);
         }
         return list;
     }

    /**
     * 当前页的纪录
     * @param start_day
     * @param end_day
     * @param a_code
     * @param db_list
     * @param pagesize
     * @param curpage
     * @return
     * @throws GeneralException
     */
    private ArrayList getA0100RecordHis(String start_day, String end_day, String a_code, ArrayList db_list)
            throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer day_where = new StringBuffer();
        day_where.append(" and " + kq_employ_shift_q03z0 + ">='" + start_day + "'");
        day_where.append(" and " + kq_employ_shift_q03z0 + "<='" + end_day + "'");
        KqShiftClass kqShiftClass = new KqShiftClass(this.conn, this.userView);
        //		    	 StringBuffer nbaseS=new StringBuffer();
        StringBuffer sql = new StringBuffer();
        String ltable = kq_employ_shift_table_arc;//目标表		
        for (int i = 0; i < db_list.size(); i++) {
            String nbase = (String) db_list.get(i);
            String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
            String code_where = kqShiftClass.getCodeItemWhere(a_code, (String) db_list.get(i));
            sql.append("select distinct nbase,a0100,a0101");
            sql.append(" from " + ltable + " ");
            sql.append(" where nbase='" + nbase + "'");
            if (day_where != null && day_where.length() > 0) {
                sql.append(" " + day_where);
            }
            if (code_where != null && code_where.length() > 0) {
                sql.append(" and " + code_where);
            }
            if (code_where != null && code_where.length() > 0) {
                sql.append(" and " + code_where);
            }
            if (this.inwhere != null && this.inwhere.length() > 0) {
                sql.append(this.inwhere);
            }
            sql.append(" and   a0100 in(select distinct a0100 " + whereA0100In + ") ");
            sql.append(" union ");
        }
        if (sql != null && sql.toString().length() > 0) {
            sql.setLength(sql.length() - 7);
        }
        sql.append(" order by nbase,a0100");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        HashMap hashmap = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                hashmap = new HashMap();

                hashmap.put("a0100", rs.getString("a0100") != null && rs.getString("a0100").length() > 0 ? rs.getString("a0100")
                        : "");
                hashmap.put("a0101", rs.getString("a0101") != null && rs.getString("a0101").length() > 0 ? rs.getString("a0101")
                        : "");
                hashmap.put("nbase", rs.getString("nbase") != null && rs.getString("nbase").length() > 0 ? rs.getString("nbase")
                        : "");
                list.add(hashmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 计算时间
     * @param sb_time
     * @param xb_time
     * @return
     * @throws GeneralException 
     */
    private float getWork_Time(String sb_time, String xb_time) throws GeneralException {
        float work_tiem = 0;
        Date sb_T = DateUtils.getDate(sb_time, "HH:mm");
        Date xb_T = DateUtils.getDate(xb_time, "HH:mm");
        work_tiem = KQRestOper.toHourFormMinute(sb_T, xb_T);
        if (work_tiem < 0) {
            sb_T = DateUtils.getDate("2007.03.08 " + sb_time, "yyyy.MM.dd HH:mm");
            xb_T = DateUtils.getDate("2007.03.09 " + xb_time, "yyyy.MM.dd HH:mm");
            work_tiem = KQRestOper.toHourFormMinute(sb_T, xb_T);
        }
        this.tiems = this.tiems + work_tiem;
        return work_tiem;
    }
    
    private String getShiftDataExcelFileName() {
    	return userView.getUserName() + "_" + ResourceFactory.getProperty("kq.init.shift")+".xls";
    }
}

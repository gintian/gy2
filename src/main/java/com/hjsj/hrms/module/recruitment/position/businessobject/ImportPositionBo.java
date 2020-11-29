package com.hjsj.hrms.module.recruitment.position.businessobject;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportPositionBo {
    Connection conn;
    ContentDAO dao;
    UserView userview;
    private int index = 0;
    private RowSet frowset;

    public ImportPositionBo(Connection conn, ContentDAO contentDAO, UserView userview) {
        this.conn = conn;
        this.dao = contentDAO;
        this.userview = userview;

    }

    /**
     * 获取Excel 工作簿
     * @return 返回工作簿的名称
     */
    public String creatExcel() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
        String fieldsetid = "Z03";
        String columnids = "";
        ContentDAO dao = new ContentDAO(this.conn);
        PositionBo bo = new PositionBo(this.conn, dao, this.userview);
        ArrayList positionColumn = bo.getColumnList(false);
        ArrayList list = new ArrayList();
        FieldItem item = new FieldItem();
        FileOutputStream fileOut = null;
        String outName = this.userview.getUserName()  + "_hire.xls";
        try {
	        for (int i = 0; i < positionColumn.size(); i++) {
	            ColumnsInfo column = (ColumnsInfo)positionColumn.get(i);
	            String hiddenColumns = ",z0309,z0319,z0367,z0371,z0307,z0301,z0323,z0369,z0310,z0384,z0385,z0365,z0373,position,publishtime,accepteandall,newandall,responsposi,depresponsposi,";  
	           
	            if(hiddenColumns.contains(","+column.getColumnId()+","))
	                continue;
	            
	            if(columnids.contains(",z0381"))
	                continue;
	            
	            item = DataDictionary.getFieldItem(column.getColumnId());
	                
	            if(item == null){
	                item = new FieldItem();
	                item.setItemdesc(column.getColumnDesc());
	                item.setItemid(column.getColumnId());
	                item.setFieldsetid(column.getFieldsetid());
	                item.setItemtype(column.getColumnType());
	                item.setItemlength(column.getColumnLength());
	                item.setCodesetid(column.getCodesetId());
	            }
	            
	            if (StringUtils.isNotEmpty(column.getColumnDesc())){
	            	if("flow".equalsIgnoreCase(column.getColumnId()))
	            		continue;
	            	
	                item.setItemdesc(column.getColumnDesc());
	                columnids =  columnids + "," + column.getColumnId();
	            }
	            
	            list.add(item);
	        }
	        
	        columnids = columnids + ",";
	        if (!columnids.contains(",z0315,") )
	        	this.getFieldExist("z0315",list);
	        
	        if (!columnids.contains(",z0101,")) 
	        	this.getFieldExist("z0101",list);
	        
	        if (!columnids.contains(",z0351,"))
	        	this.getFieldExist("z0351",list);
	        
	        item = new FieldItem();
	        item.setItemdesc("自动接收职位申请");
	        item.setItemid("accept_post");
	        item.setFieldsetid("Z03");
	        item.setItemtype("A");
	        item.setItemlength(1);
	        item.setCodesetid("45");
	        item.setDisplaywidth(15);
	        item.setFillable(false);
	        list.add(item);
	        item = new FieldItem();
	        item.setItemdesc("不符合职位筛选规则不允许申请");
	        item.setItemid("apply_control");
	        item.setFieldsetid("Z03");
	        item.setItemtype("A");
	        item.setItemlength(1);
	        item.setCodesetid("45");
	        item.setDisplaywidth(20);
	        item.setFillable(false);
	        list.add(item);
	        creatSheet(fieldsetid, list, wb);
	        
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
        } catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(fileOut);
            PubFunc.closeResource(wb);
        }
        return outName;
    }

    /**
     * 获取sheet
     * @param fieldsetid
     * @param list
     * @param wb
     * @throws Exception
     */
    private void creatSheet(String fieldsetid, ArrayList list, HSSFWorkbook wb) throws Exception {
        String fieldsetdesc = "招聘职位信息表";
        String SheetName = fieldsetdesc + "(" + fieldsetid + ")";
        SheetName = SheetName.replaceAll("/", "_").replaceAll("／", "_");
        HSSFSheet sheet = wb.createSheet(SheetName);
        
        String hiddenSheet = null;
        HSSFDataValidation validation = null; // 数据验证
        int startRow = 1; // 开始行 
        int endRow = 100; // 结束行
        DVConstraint constraint = null;
        hiddenSheet = "category1Hidden";
        Name category1Name = wb.createName(); 
        HSSFSheet category1Hidden = wb.createSheet(hiddenSheet); // 创建隐藏域
        category1Name.setNameName(hiddenSheet); 
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
        style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); // 文本格式
        HSSFCellStyle styleN = dataStyle(wb);
        styleN.setAlignment(HorizontalAlignment.RIGHT);
        styleN.setWrapText(true);
        HSSFDataFormat df = wb.createDataFormat();
        styleN.setDataFormat(df.getFormat(decimalwidth(0)));

        HSSFCellStyle styleCol0 = dataStyle(wb);
        HSSFFont font0 = wb.createFont();
        font0.setFontHeightInPoints((short) 5);
        styleCol0.setFont(font0);
        styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); // 文本格式
        styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleCol0_title = dataStyle(wb);
        styleCol0_title.setFont(font2);
        styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); // 文本格式
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

        //sheet.setColumnWidth((short) 0, (short) 1000);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
        HSSFPatriarch patr = sheet.createDrawingPatriarch();

        HSSFRow row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
        }
        HSSFCell cell = null;
        HSSFComment comm = null;

        int z0381Column = -1;
        int z0101Column = -1;
        ArrayList codeCols = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            FieldItem field = (FieldItem) list.get(i);
            String fieldName = field.getItemid().toLowerCase();
            String fieldLabel = field.getItemdesc();
            //判断该字段在业务字典和页面业务中是否设置为必填项了
            boolean required = this.getRequired(field);
            
            if ("z0381".equalsIgnoreCase(fieldName))
            {
                z0381Column = i;
            }

            if ("z0101".equalsIgnoreCase(fieldName))
            {
                z0101Column = i;
            }
            
            int w = field.getDisplaywidth();
            if (w == 0) {
                w = 8;
            }
            if (w > 50) 
                w = 50;
            sheet.setColumnWidth((i), w * 350);
            cell = row.getCell(i);
            if (cell == null) 
                cell = row.createCell(i);
            
            if(required)
                cell.setCellValue(cellStr(fieldLabel)+"*");
            else
                cell.setCellValue(cellStr(fieldLabel));
            
            if ("z0101".equalsIgnoreCase(fieldName))
            {
                cell.setCellValue(cellStr("招聘批次"));
            }
            cell.setCellStyle(style2);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short)(i + 1), 0, (short)(i + 2), 1));
            comm.setString(new HSSFRichTextString(fieldName));
            cell.setCellComment(comm);
            if ("z0101".equalsIgnoreCase(field.getItemid()) || "z0381".equalsIgnoreCase(field.getItemid()) ||     "A".equalsIgnoreCase(field.getItemtype()) && (field.getCodesetid() != null && !"".equals(field.getCodesetid()) && !"0".equals(field.getCodesetid())))
                codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
        }

        try {
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs;
            String z0103;
            String z0101;
            String flowId;
            String name;
            RecruitPrivBo rpbo =  new RecruitPrivBo();
            RecruitBatchBo bo = new RecruitBatchBo(this.conn, this.userview);
            String priv = rpbo.getPrivB0110Whr(userview,"codeitemid", RecruitPrivBo.LEVEL_SELF_CHILD);
            String sqlZ01 = bo.getDataSql("04");
            StringBuffer sql = new StringBuffer();
            StringBuffer recruitmentBatch = new StringBuffer();
            sql.append(sqlZ01);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                    z0103 = rs.getString("Z0103");
                    z0101 = rs.getString("Z0101");
                    recruitmentBatch.append(",");
                    recruitmentBatch.append(z0101);
                    recruitmentBatch.append(":");
                    recruitmentBatch.append(z0103);
            }
            String z01 =recruitmentBatch.toString();
            if(StringUtils.isNotEmpty(z01))
                z01 =z01.substring(1);

            sql.setLength(0);
            recruitmentBatch.setLength(0);
            sql.append("select flow_id, name from zp_flow_definition where valid = 1");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                flowId = rs.getString("flow_id");
                name = rs.getString("name");
                recruitmentBatch.append(",");
                recruitmentBatch.append(flowId);
                recruitmentBatch.append(":");
                recruitmentBatch.append(name);
            }
            String flowName =recruitmentBatch.toString();
            if(StringUtils.isNotEmpty(flowName))
                flowName =flowName.substring(1);

            String[] Z0103s = z01.split(",");
            String[] names = flowName.split(",");
            
            int rowCount = 1;
            while (rowCount < 1001) {
                row = sheet.getRow(rowCount);
                if (row == null) {
                    row = sheet.createRow(rowCount);
                }
                
                for (int i = 0; i < list.size(); i++) {
                    FieldItem field = (FieldItem) list.get(i);
                    String itemtype = field.getItemtype();
                    int decwidth = field.getDecimalwidth();

                    cell = row.getCell(i);
                    if (cell == null) 
                        cell = row.createCell(i);
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
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue("");
                    } else {
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    }

                }
                rowCount++;
            }
            rowCount--;

            HSSFSheet codesetSheet = sheet;
            // 下拉数据放到最后，依次为 HZ、HY、HX......   
            String[] firstUpper = {"H","G","F","E","D","C","B","A"};
            String[] lettersUpper = {"Z","Y","X","W","V","U","T","S","R","Q","P","O","N","M","L","K","J","I","H","G","F","E","D","C","B","A"};

            for (int n = 0; n < codeCols.size(); n++) {
                int m = 2001; //初始行为2001行
                String columnIndex = firstUpper[index / 26] + lettersUpper[index % 26]; //当前列的列标识 
                int cellIndex = columnToIndex(columnIndex); // 通过列标识计算出列的index 
                String codeCol = (String) codeCols.get(n);
                String[] temp = codeCol.split(":");
                String codesetid = temp[0];
                int codeCol1 = Integer.valueOf(temp[1]).intValue();
                String[] cellValues = null;
                if (codeCol1 == z0101Column || codeCol1 == z0381Column) {
                    if (codeCol1 == z0101Column) 
                        cellValues = Z0103s;
                    else
                        cellValues = names;

                    for (int i = 0, length = cellValues.length; i < length; i++) {
                        row = codesetSheet.getRow(m + 0);
                        if (row == null) 
                            row = codesetSheet.createRow(m + 0);
                        
                        cell = row.createCell((cellIndex));
                        cell.setCellValue(new HSSFRichTextString(cellValues[i]));
                        m++;
                    }
                } else {
                    StringBuffer codeBuf = new StringBuffer();
                    if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                        codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'"); // and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
                        this.frowset = dao.search(codeBuf.toString());
                        if (this.frowset.next()) {
                            if (this.frowset.getInt(1) < 500) { // 代码型中指标大于500的时候，就不再加载了 
                                codeBuf.setLength(0);
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' order by codeitemid"); // zhaoguodong 2013.09.23 使获取的字段按codeitemid排序
                            } else {
                                continue;
                            }
                        }
                    } else {
                        if (!"UN".equals(codesetid)) {
                            m = loadorg(codesetSheet, row, cell, cellIndex, m, dao, codesetid);
                        } else if ("UN".equals(codesetid)) {
                            codeBuf.setLength(0);
                            if (this.userview.isSuper_admin()) {
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid");
                            } else {
                                rpbo =  new RecruitPrivBo();
                            	priv = rpbo.getPrivB0110Whr(userview,"codeitemid", RecruitPrivBo.LEVEL_SELF_CHILD);
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date  and "+priv+" order by a0000,codeitemid");
                            }
                        }
                    }
                    if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                        this.frowset = dao.search(codeBuf.toString());
                        while (this.frowset.next()) {
                            row = codesetSheet.getRow(m + 0);
                            if (row == null) 
                                row = codesetSheet.createRow(m + 0);
                            
                            cell = row.createCell((cellIndex));
                            if ("UN".equals(codesetid)) {
                                int grade = this.frowset.getInt("grade");
                                StringBuffer message = new StringBuffer();
                                message.setLength(0);
                                for (int i = 1; i < grade; i++) {
                                    message.append("  ");
                                }
                                cell.setCellValue(new HSSFRichTextString(message.toString() + this.frowset.getString("codeitemdesc") + "(" + this.frowset.getString("codeitemid") + ")"));
                            } else {
                                cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
                            }
                            m++;
                        }
                    }
                    if (m == 2001) 
                        continue;

                }
                String strFormula = "";
                strFormula = "$" + firstUpper[index / 26] + lettersUpper[index % 26] + "$2001:$" + firstUpper[index / 26] + lettersUpper[index % 26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
                CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1); //rowCount
                DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                dataValidation.setSuppressDropDownArrow(false);
                sheet.addValidationData(dataValidation);
                index++;
            }

        } catch(SQLException e1) {
            e1.printStackTrace();
        }

    }

    int columnToIndex(String column) {
        if (!column.matches("[A-Z]+")) {
            try {
                throw new Exception("Invalid parameter");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            index += ((int) chars[i] - (int)'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
        }
        return index - 1;
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

    private int loadorg(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int index, int m, ContentDAO dao, String type) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        DbSecurityImpl dbs = new DbSecurityImpl();
        try {
            st = this.conn.createStatement();
            String sql = "";
            if (this.userview.isSuper_admin()) {
                sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
            } else {
                String manpriv = this.userview.getManagePrivCode();
                String manprivv = this.userview.getManagePrivCodeValue();
                if (manprivv.length() > 0) 
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid='" + manprivv + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
                else if (manpriv.length() >= 2) 
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
                else 
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where 1=2";
            }
            //rs = dao.search(sql);
            dbs.open(this.conn, sql);
            rs = st.executeQuery(sql);
            String codeitemid = "";
            String childid = "";
            String codeitemdesc = "";
            int grade = 0;
            while (rs.next()) {
                codeitemid = rs.getString("codeitemid");
                childid = rs.getString("childid");
                codeitemdesc = rs.getString("codeitemdesc");
                grade = rs.getInt("grade");
                row = sheet.getRow(m + 0);
                if (row == null) 
                    row = sheet.createRow(m + 0);            
                cell = row.createCell((index));
                StringBuffer message = new StringBuffer();
                message.setLength(0);
                for (int i = 1; i < grade; i++) {
                    message.append("  ");
                }
                cell.setCellValue(new HSSFRichTextString(message.toString() + codeitemdesc + "(" + codeitemid + ")"));
                m++;
                if (!codeitemid.equals(childid)) 
                    m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(st);
        }
        return m;
    }

    private int loadchild(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int index, int m, ContentDAO dao, String parentid, String type) throws Exception {
        ResultSet rs = null;
        Statement st = null;
        DbSecurityImpl dbs = new DbSecurityImpl();
        try {
            String sql = null;
            st = this.conn.createStatement();
            if ("@K".equalsIgnoreCase(type)) {
                sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='" + parentid + "' and parentid<>codeitemid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
            } else {
                sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='" + parentid + "' and codesetid<>'@K' and parentid<>codeitemid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
            }
            dbs.open(this.conn, sql);
            rs = st.executeQuery(sql);
            String codeitemid = "";
            String childid = "";
            String codeitemdesc = "";
            int grade = 0;
            while (rs.next()) {
                codeitemid = rs.getString("codeitemid");
                childid = rs.getString("childid");
                codeitemdesc = rs.getString("codeitemdesc");
                grade = rs.getInt("grade");
                row = sheet.getRow(m + 0);
                if (row == null) 
                    row = sheet.createRow(m + 0);
               
                cell = row.createCell((index));
                StringBuffer message = new StringBuffer();
                message.setLength(0);
                for (int i = 1; i < grade; i++) {
                    message.append("  ");
                }
                cell.setCellValue(new HSSFRichTextString(message.toString() + codeitemdesc + "(" + codeitemid + ")"));
                m++;
                if (!codeitemid.equals(childid)) m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(st);
        }
        return m;
    }

    /**
     * @Title: 招聘职位导入数据   
     * @param  path 导入文件的路径
     * @param  filename 导入文件的名称
     * @return ArrayList<Object> 报错信息集合    
     * @throws
     */
    @Deprecated
    public ArrayList <Object> importPosition(String path, String filename) throws GeneralException {
        //存放错误信息
        ArrayList < Object > msg = new ArrayList < Object > (); 
        ArrayList < Object > msgList = new ArrayList < Object > ();     
        Sheet sheet = this.getSheet(path, filename);
        Row headRow = sheet.getRow(0); // 获取表头
        return importPositions(msg, msgList, sheet);
    }

	private ArrayList<Object> importPositions(ArrayList<Object> msg, ArrayList<Object> msgList, Sheet sheet) {
		Row headRow = sheet.getRow(0); // 获取表头
        if (headRow == null) {
            msg.add("请用导出的Excel模板来导入数据");
            msgList.add(msg);
            return msgList;
        }
        // 存放职位名称的位置
        int JobTitleIndex = 0;
        
        // 存放支持修改的字段
        Map < Integer,String > map = new HashMap < Integer,String > ();
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap msgMap = new HashMap();
        try {
            int headCols = headRow.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            if (headCols >= 1 && rows >= 1) {
                // 用来判断是不是存在职位名称
                boolean isJobTitle = false;
                DbWizard dWizard = new DbWizard(conn);
                Cell cell = null;
                Comment comment = null;
                // 拿到要添加的指标
                for (int c = 0; c < headCols; c++) {
                    cell = headRow.getCell(c);
                    String field = "";
                    String title = "";

                    if (cell != null) {
                        switch (cell.getCellType()) {

                        case Cell.CELL_TYPE_FORMULA:
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            double y = cell.getNumericCellValue();
                            title = Double.toString(y);
                            break;

                        case Cell.CELL_TYPE_STRING:
                            title = cell.getStringCellValue();
                            break;
                        }

                        comment = cell.getCellComment();
                        // 表头存在，批注为空
                        if (comment == null) {
                            msg.add("请用导出的Excel模板来导入数据！");
                            msgList.add(msg);
                            return msgList;
                        }

                        //拿到标注
                        field = comment.getString().toString().trim();

                        if ("Z0101".equalsIgnoreCase(field)) {
                            map.put(c, "Z0101");
                            continue;
                        }
                        
                        //记录要要改的字段的位置
                        if (!dWizard.isExistField("z03", field.toUpperCase(), false)) {
                            msg.add("导入的Excel中“" + title + "”这个指标在招聘信息表里面不存在或未构库，请用导出的Excel模板来导入数据！");
                            msgList.add(msg);
                            return msgList;
                        }

                        if ("Z0301".equalsIgnoreCase(field)) {
                            continue;
                        }
                        //找到职位名称所在的字段的位置
                        if ("Z0351".equalsIgnoreCase(field)) {
                            JobTitleIndex = c;
                            isJobTitle = true;
                        } else {
                            map.put(c, field);
                        }

                    }
                }
                
                if (!isJobTitle) {
                    FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");                               
                    msg.add("[" + item.getItemdesc() + "]不存在，请用导出的Excel模板来导入数据！");
                    msgList.add(msg);
                    return msgList;
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        ArrayList < Object > info = this.getExcelInfo(sheet, JobTitleIndex, map);
        return info;
	}

    /**
     * map中存放的是需要修改的字段的位置和字段名
     * @param sheet
     * @param JobTitleIndex 职位名称所在位置
     * @param map 要导入的位置和字段
     * @return
     */
    private ArrayList <Object> getExcelInfo(Sheet sheet, int JobTitleIndex, Map < Integer, String > map) {
        //返回信息
        ArrayList <Integer> indexs = new ArrayList <Integer> ();
        Row row = null;
        Cell cell = null;       
        Cell jobTitleCell = null;      
        StringBuffer sql = new StringBuffer();
        String unitId = this.userview.getUnitIdByBusi("7"); //获取用户的操作单位    
        String name = this.userview.getUserFullName();
        String userName = this.userview.getUserName();
        RecruitUtilsBo bo = new RecruitUtilsBo(conn);

        if (unitId.length() > 0) 
            unitId = PubFunc.getTopOrgDept(unitId);

        if (unitId != null && unitId.length() > 0) {
            String[] unitIds = unitId.split("`");
            unitId = "";
            for (int i = 0; i < unitIds.length; i++) {
                unitId += unitIds[i].substring(2) + "`";
            }
            unitId = unitId.substring(0, unitId.length() - 1);
        }
        Date now = new Date();
        DateFormat Time = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = Time.format(now);
        sql.append("INSERT INTO  z03 ( Z0319, Z0310, Z0309, ");
        // 存放Excel表中数据
        ArrayList <Object> rowList = null;
        ArrayList valueLists = new ArrayList < ArrayList <Object>> ();
        // 存放Excel表中职位名称的数据
        ArrayList z0301Lists = new ArrayList < ArrayList <Object>> ();
        // 存放Excel表中错误数据的行数
        ArrayList deletelist = new ArrayList < ArrayList <Object>> ();
        //存放错误信息
        ArrayList <Object> msg = new ArrayList < Object > ();
        ArrayList <Object> msglist = new ArrayList < Object > ();
        for (Entry < Integer, String > entry: map.entrySet()) {
            indexs.add(entry.getKey());
            sql.append(entry.getValue() + ", ");
        }
        sql.append("Z0351, Z0301, Z0307 ");
        sql.append(") VALUES ( '01' , '" + name + "' ,'" + userName + "' , ");
        for (Entry < Integer, String > entry: map.entrySet()) {
            sql.append("? , ");
        }
        sql.append("? , ? ,? ) ");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        //判断是不是第一次记录
        Boolean is = false;
        ArrayList <String> jobTitleList = new ArrayList <String> ();
        try {
            //用于计数
            int count = 0;
            String jobTitles = "";
            ArrayList <String> jobTitlesList = new ArrayList <String> ();          
            int numberOfRows = sheet.getPhysicalNumberOfRows();        
            String startTime;
            String endTime;
            //起始日期字段名
            String startTimeName = "";
            //终止日期字段名
            String endTimeName = "";
            //判断登录用户的招聘渠道的权限
            RecruitPrivBo rpbo =  new RecruitPrivBo();
            HashMap<String, Object> parame =  rpbo.getChannelPrivMap(userview, conn);
            boolean setFlag = (Boolean) parame.get("setFlag");
            ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
            HashMap codemapUN = new HashMap();
            HashMap codemapUM = new HashMap();
            HashMap codemapJob = new HashMap();
            HashMap leafItemMmaps = new HashMap();
            //excel每行数据遍历
            for (int i = 1; i < numberOfRows; i++) {
                //起始时间
                startTime = "";
                //结束时间
                endTime = "";
                String tmp = "";
                //判断本行数据是否需要导入
                boolean tmpFlag = true;
                String jobTitle = null;             
                row = sheet.getRow(i);
                rowList = new ArrayList < Object > ();
                //把每一行中要修改的数据添加到list中
                if (row == null) 
                    continue;
                
                boolean as =isRowEmpty(row);
                if(isRowEmpty(row))
                    continue;
                
                
                String unit ="";
                String division ="";
               
                //单行数据每列遍历
                for (Integer c: indexs) {
                    HashMap codemap = new HashMap();
                    HashMap childCodemap = new HashMap();
                    Object value = null;
                    cell = row.getCell(c);
                    //拿到标注 
                    String field = map.get(c);
                    FieldItem item = new FieldItem();
                    if ("accept_post".equalsIgnoreCase(field)) {
                        item.setItemdesc("自动接收职位申请");
                        item.setItemid("accept_post");
                        item.setFieldsetid("Z03");
                        item.setItemtype("A");
                        item.setItemlength(1);
                        item.setCodesetid("45");
                        item.setFillable(false);
                        item.setDisplaywidth(15);
                    } else if ("apply_control".equalsIgnoreCase(field)) {
                        item.setItemdesc("不符合职位筛选规则不允许申请");
                        item.setItemid("apply_control");
                        item.setFieldsetid("Z03");
                        item.setItemtype("A");
                        item.setItemlength(1);
                        item.setCodesetid("45");
                        item.setFillable(false);
                        item.setDisplaywidth(20);
                    } else
                        item = (FieldItem) DataDictionary.getFieldItem(field);
                    
                    //判断该字段在业务字典和页面业务中是否设置为必填项了
                    boolean required = this.getRequired(item);
                    if (cell != null) {
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        
                        String cellValue = cell.getStringCellValue();
                        if(required && StringUtils.isEmpty(cellValue)){
                            msg.clear();
                            msg.add("导入的Excel中[" + item.getItemdesc() + "]列必填项中有数据为空！请填写完整再导入");
                            msglist.add(msg);
                            valueLists.clear();
                            userview.getHm().put("valueLists", valueLists);
                            return msglist;
                        }
                      
                        if (StringUtils.isNotEmpty(cellValue)) 
                        {
                            //判断成绩的数据类型代码类
                            String Codesql = "";
                            if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype()) ) {
                                if(i == 1 ){
                                    DbWizard db = new DbWizard(conn);
                                    //判断是否勾选只能选择末级节点
                                    if(db.isExistField("codeset", "leaf_node", false)) {
                                        this.frowset = dao.search("select leaf_node from codeset where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"'");
                                        if(this.frowset.next()) {
                                            String leafNode = this.frowset.getString("leaf_node");
                                            //当 leafNode = "1" 时，只能选择末级节点
                                            if("1".equals(leafNode)) 
                                                leafItemMmaps.put(item.getCodesetid().trim(),true);
                                            else
                                                leafItemMmaps.put(item.getCodesetid().trim(),false);
                                        }
                                    }
                                }
                                
                                if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid()) &&  i == 1 ) {
                                    String priv = rpbo.getPrivB0110Whr(userview,"codeitemid", RecruitPrivBo.LEVEL_SELF_CHILD);
                                    Codesql = "select codeitemdesc,codeitemid from organization where upper(codesetid)='" + item.getCodesetid().toUpperCase() + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date and "+priv;
                                    this.frowset = dao.search(Codesql);
                                    while (this.frowset.next()) {
                                        codemap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
                                    }
                                    //单位的编码集合
                                    if ("UN".equalsIgnoreCase(item.getCodesetid())){
                                        codemapUN = codemap;
                                    }
                                    //部门的编码集合
                                    if ("UM".equalsIgnoreCase(item.getCodesetid())){
                                        codemapUM = codemap;
                                    }
                                    //职位的编码集合
                                    if ("@K".equalsIgnoreCase(item.getCodesetid())){
                                        codemapJob = codemap;
                                    }
                                
                                } 
                                
                                if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
                                    if ("UN".equalsIgnoreCase(item.getCodesetid()))
                                        codemap = codemapUN;
                                    else if ("UM".equalsIgnoreCase(item.getCodesetid()))
                                        codemap = codemapUM;
                                    else if ("@K".equalsIgnoreCase(item.getCodesetid()))
                                        codemap = codemapJob;
                                  
                                }else{
                                    ArrayList codeItemList = AdminCode.getCodeItemList(item.getCodesetid());
                                    for(int j = 0 ; j < codeItemList.size() ; j++) {
                                        CodeItem codeItem = (CodeItem) codeItemList.get(j);
                                        codemap.put(codeItem.getCodename(),codeItem.getCodeitem());
                                        childCodemap.put(codeItem.getCodename(),codeItem.getCcodeitem());
                                      }
                                }
                                cellValue = cellValue == null ? "": cellValue.trim();
                                if ("e01a1".equalsIgnoreCase(item.getItemid()) || "UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
                                   int begin=cellValue.indexOf("(");
                                   int last=cellValue.indexOf(")");
                                   String cellValueId = cellValue.substring(begin+1,last);
                                   cellValue = cellValue.substring(0,begin);
                                   String valueId = (String) codemap.get(cellValueId);
                                    if (StringUtils.isNotEmpty(valueId) && valueId.equalsIgnoreCase(cellValue)) { //直接输入机构名称 并且机构名称存在  ,将  机构名称  转为  机构 编码 
                                        value = cellValueId; //获取 机构  id  
                                        if ("UN".equalsIgnoreCase(item.getCodesetid()))
                                            unit =(String) cellValueId;
                                        
                                        if ("UM".equalsIgnoreCase(item.getCodesetid()))
                                            division =(String) cellValueId;
                                        
                                    } else {//走到这里说明值 是空，并且 机构树上也没有选择机构
                                        int number = msg.size();
                                        msg.add((number + 1) +". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中的值未找到对应代码值！" );
                                        tmpFlag = false;
                                        continue;
                                    }
                                } else {
                                    if (!codemap.containsKey(cellValue) && !"".equals(cellValue)) {
                                        int number = msg.size();
                                        msg.add((number + 1) +". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中的值未找到对应代码值！" );
                                        tmpFlag = false;
                                        continue;
                                    } 
                                    
                                    if (!"".equals(cellValue)) {
                                        value = (String) codemap.get(cellValue);
                                        String childValue = (String) childCodemap.get(cellValue);
                                        if(!leafItemMmaps.isEmpty()) {
                                            boolean isEndCode =  (Boolean) leafItemMmaps.get(item.getCodesetid());
                                            if(isEndCode && !((String) value).equalsIgnoreCase(childValue)){
                                                int number = msg.size();
                                                msg.add((number + 1) +". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中的值对应代码值不是末级代码！" );
                                                tmpFlag = false;
                                                continue;
                                            }
                                        }
                                        
                                    }
                                   
                                    if("z0336".equalsIgnoreCase(field)){
                                        if(setFlag){
                                            boolean hpFlag = true;
                                            String  hpValue = "";
                                            String  codeValue = (String) codemap.get(cellValue);
                                            for (int y = 0; y < hirePriv.size(); y++) {
                                                hpValue = hirePriv.get(y);
                                                if(codeValue.startsWith((hpValue) )){
                                                    hpFlag = false; 
                                                    break;
                                                }
                                            }
                                            
                                            if(hpFlag){
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]中的 [" +cellValue+ "]无权限，不予导入");
                                                tmpFlag = false;
                                            }
                                        }
                                    }
                                }
                                rowList.add(value);
                                continue;
                            } 
                            
                            if (item != null && item.isSequenceable() && "A".equalsIgnoreCase(item.getItemtype())) {
                                if ((cellValue == null || cellValue.length() == 0)) {
                                    IDGenerator idg = new IDGenerator(2, this.conn);
                                    String idd = idg.getId(item.getSequencename());
                                    value = idd;
                                }
                            }

                            //判断数据类型
                            if ("N".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                if (cell.getStringCellValue() != null) 
                                {
                                    value = cell.getStringCellValue().trim();
                                    int fieldLength = item.getItemlength();
                                    String values = (String) value;
                                    values = PubFunc.doStringLength(values, fieldLength);
                                    int valueLength = values.length();
                                    
                                    if (valueLength > fieldLength) 
                                    {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度！");
                                        continue;
                                    }

                                    Pattern pattern = Pattern.compile("[0-9]*");
                                    Pattern pattern2 = Pattern.compile("[0-9]*\\.[0-9]*");

                                    if (!"".equals(value)) 
                                    {
                                        Matcher isNum = pattern.matcher((String) value);
                                        Matcher isNum2 = pattern2.matcher((String) value);

                                        if (isNum.matches() || isNum2.matches()) 
                                        {
                                            if (((String) value).indexOf(".") > 0) 
                                            {
                                                String intNum = ((String) value).substring(0, ((String) value).indexOf("."));
                                                String floatNum = ((String) value).substring(((String) value).indexOf(".") + 1);
                                                if (floatNum.length() > DataDictionary.getFieldItem(map.get(c)).getItemlength()) 
                                                    floatNum = floatNum.substring(0, DataDictionary.getFieldItem(map.get(c)).getDecimalwidth());
                                                
                                                value = intNum + "." + floatNum;
                                                value = Float.parseFloat((String) value);
                                            }
                                        } else {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行" + row.getCell(JobTitleIndex) + "的" + DataDictionary.getFieldItem(map.get(c)).getItemdesc() + "数据格式错误");
                                            tmpFlag = false;
                                            continue;
                                        }

                                    } else 
                                        value = null;
                                }
                            } else if ("D".equals(item.getItemtype())) {
                                try {
                                    value = cell.getStringCellValue().trim();
                                    if (!"".equals(value)) 
                                    {
                                        String valueas = (String) value;
                                        tmp = this.checkdate(valueas);
                                        if ("false".equals(tmp)) 
                                        {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
                                            tmpFlag = false;
                                            continue;
                                        } else {
                                            value = tmp;
                                        }
                                    }

                                    if ("Z0329".equalsIgnoreCase(item.getItemid()))
                                    {
                                        startTimeName = item.getItemdesc();
                                        startTime = ((String) value).replace("-", "");
                                        String z0329Format = bo.getDateFormat("Z0329");
                                        String dateFormat = this.getDateFormat((String) value);
                                        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                                        SimpleDateFormat z0329df = new SimpleDateFormat(z0329Format);
                                        Date date = df.parse((String) value);
                                        Timestamp dateValue = new Timestamp(z0329df.parse(z0329df.format(date)).getTime());
                                        value =  dateValue;
                                    }
                                    
                                    if ("Z0331".equalsIgnoreCase(item.getItemid()))
                                    {
                                        endTimeName = item.getItemdesc();
                                        endTime = ((String) value).replace("-", "");
                                        String numberTime = ((String) nowTime).replace("-", "");
                                        int result = numberTime.compareTo(endTime);
                                        if (result > 0) 
                                        {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + item.getItemdesc() + "]"+ ResourceFactory.getProperty("error.nothan.later.now")); 
                                            tmpFlag = false;
                                            continue;
                                        }
                                        
                                        String z0331Format = bo.getDateFormat("Z0331");
                                        String dateFormat = this.getDateFormat((String) value);
                                        
                                		if("yyyy-MM-dd".equalsIgnoreCase(dateFormat)){
                                			value =  value + " 23:59:59";
                                			dateFormat = "yyyy-MM-dd HH:mm:ss";
                                		}else if("yyyy-MM-dd HH:mm".equalsIgnoreCase(dateFormat)){
                                			value =  value + ":59";
                                			dateFormat = "yyyy-MM-dd HH:mm:ss";
                                		}
                                        
                                        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                                        SimpleDateFormat z0331df = new SimpleDateFormat(z0331Format);
                                        Date date = df.parse((String) value);
                                        Timestamp dateValue = new Timestamp(z0331df.parse(z0331df.format(date)).getTime());
                                        value = dateValue;
                                    }

                                    if ("Z0375".equalsIgnoreCase(item.getItemid()))
                                    {
                                        endTime = ((String) value).replace("-", "");
                                        String numberTime = ((String) nowTime).replace("-", "");
                                        int result = numberTime.compareTo(endTime);
                                        if (result > 0) 
                                        {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + item.getItemdesc() + "]"+ ResourceFactory.getProperty("error.nothan.later.now")); 
                                            tmpFlag = false;
                                            continue;
                                        }
                                    }
                                    
                                    if(!"Z0331".equalsIgnoreCase(item.getItemid()) && !"Z0329".equalsIgnoreCase(item.getItemid())){
                                        Timestamp create_time = new Timestamp(Time.parse(tmp).getTime());
                                        value = create_time;
                                    }
                                  
                                 
                                    
                                } catch(Exception e) {
                                    int number = msg.size();
                                    msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getStringCellValue());
                                    break;
                                }
                               
                            } else if ("A".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                if (cell.getStringCellValue() != null) 
                                {
                                    value = cell.getStringCellValue().trim();
                                    int fieldLength = item.getItemlength();
                                    String values = (String) value;
                                    values = PubFunc.doStringLength(values, fieldLength);
                                    int valueLength = values.length();
                                            
                                    if ("Z0101".equalsIgnoreCase(item.getItemid())) 
                                    {
                                        String[] z0103Values = values.split(":");
                                        value =  z0103Values[0];
                                        rowList.add(value);
                                        continue;
                                    }  
                                    
                                    if ("z0381".equalsIgnoreCase(item.getItemid())) 
                                    {
                                        String[] z0381Values = values.split(":");
                                        value =  z0381Values[0];
                                        StringBuffer z0381Sql = new StringBuffer();
                                        RecruitPrivBo privBo = new RecruitPrivBo();
                                        String privB0110 = privBo.getPrivB0110Whr(userview, "B0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
                                        z0381Sql.append("select * from zp_flow_definition where valid = 1 and flow_id =");
                                        z0381Sql.append(value);
                                        z0381Sql.append(" and " +privB0110);
                                        rs = dao.search(z0381Sql.toString());
                                        if(rs.next())
                                        {
                                            rowList.add(value);
                                            continue;
                                        } else {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]中的流程不存在或已停用，或者没有该流程的权限，不予导入");
                                            tmpFlag = false;
                                            break;
                                        }
                                    }  
                                    
                                    if (valueLength > fieldLength) 
                                    {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度！");
                                        continue;
                                    }  
                                    
                                    value = StringUtils.isEmpty(cell.getStringCellValue()) ? null: cell.getStringCellValue().trim();
                                }
                            } else if ("M".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                    value = StringUtils.isEmpty(cell.getStringCellValue()) ? null: cell.getStringCellValue();
                            } else {
                                    value = StringUtils.isEmpty(cell.getStringCellValue()) ? null: cell.getStringCellValue().trim(); 
                            }
                        }else
                            value = null;
                    }else
                        value = null;
                    
                    rowList.add(value);
                }

                if (StringUtils.isNotEmpty(unit) && StringUtils.isNotEmpty(division) &&  !division.startsWith(unit)) {
                    int number = msg.size();
                    msg.add((number + 1) + ". 第" + (i + 1) + "行的单位和部门不匹配， 请修改。");
                    continue;
                }
                
                
                if ("false".equals(tmp)) {
                    continue;
                }

                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    int result = startTime.compareTo(endTime);
                    if (result > 0) {
                        int number = msg.size();
                        msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + startTimeName + "]大于[" + endTimeName + "]， 数据错误， 请修改。");
                        continue;
                    }
                } 
                
                jobTitleCell = row.getCell(JobTitleIndex);
                if (jobTitleCell != null) {
                    jobTitleCell.setCellType(Cell.CELL_TYPE_STRING);
                    jobTitle = row.getCell(JobTitleIndex).getStringCellValue();
                    if (StringUtils.isBlank(jobTitle)) {
                         boolean isEmpty = true;
                         for (int z = 0; z < rowList.size() ; z++) {
                             if(!"".equals(rowList.get(z)) && rowList.get(z) != null){
                                 isEmpty = false;
                             }
                         }
                         if (!isEmpty) { 
                             int number = msg.size();
                             FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");  
                             msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]的值为空，不予导入。");
                             continue; 
                         }
                        
                    }else {
            	        FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");  
            	        int fieldLength = item.getItemlength();
            	        String jobValue = PubFunc.doStringLength(jobTitle, fieldLength);
            	        if (jobTitle.length() > jobValue.length()) 
                        {
                            int number = msg.size();
                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度！");
                            continue;
                        }     
                    }
                    
                    String jobTitlesql = "select Z0351 from Z03";
                    boolean repeatJobTitle = false;
                    this.frowset = dao.search(jobTitlesql);
                    while (this.frowset.next()) {
                        if (jobTitle.equalsIgnoreCase(this.frowset.getString("Z0351"))) 
                            repeatJobTitle = true;
                    }
                    if (repeatJobTitle) {
                        int number = msg.size();
                        FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");  
                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]的值在数据库中有相同的值。");
                    }

                    rowList.add(jobTitle);
                    if (StringUtils.isEmpty(jobTitle)) {
                        jobTitleList.add("");
                        continue;
                    }


                    if (!is) {
                        is = true;
                        jobTitleList.add(jobTitle);
                    } else {
                        boolean duplicateData = true;
                        boolean notImport = false;
                        
                        if(valueLists.size() !=0){
                        	for (int j = 0; j < jobTitleList.size(); j++) {
                           	 ArrayList singleList = new ArrayList <Object>();
                           	 for (int y = 0; y < ((ArrayList) valueLists.get(j)).size()-2; y++) {
                           		 singleList.add(((ArrayList) valueLists.get(j)).get(y));
                           	 }
                           	 
                               if (!jobTitleList.get(j).equalsIgnoreCase(jobTitle) || "".equalsIgnoreCase(jobTitle) || "false".equals(tmp)) 
                                   continue;
                               
                               boolean repeat =  !rowList.retainAll(singleList);
                               
                               if (repeat) {
                                   notImport = true;
                                   int number = msg.size();
                                   FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");             
                                   msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]的值：" + jobTitle + "，在导入的Excel中有完全重复的数据， 都不予导入");
                                   if(!deletelist.contains(j)){
                                       deletelist.add(j);
                                   }                                       
                                   continue;
                               }
                               
                           }
                        	
                        }
                        
                        if (notImport) {
                            continue;
                        }
                        jobTitleList.add(jobTitle);
                    }
                }

                //记录导入信息
                if (StringUtils.isNotEmpty(jobTitle)) {
                    if (count == 0) 
                        jobTitles = "'" + jobTitle + "'";
                    else 
                        jobTitles += ",'" + jobTitle + "'";

                    count++;
             
                    if (!"false".equals(tmp) && tmpFlag ) {
                    	 //记录职位编号
                        IDGenerator idg = new IDGenerator(2, this.conn);
                        String az0301 = idg.getId("Z03.Z0301");
                        rowList.add(az0301);
                        Timestamp create_time = new Timestamp(Time.parse(nowTime).getTime());
                        rowList.add(create_time);
                        valueLists.add(rowList);
                        z0301Lists.add(rowList.get(rowList.size()-2));
                    }                 
                }
            }
          
            if (msg.size() > 0) {               
                userview.getHm().put("ImportPositionSql", sql.toString());
                userview.getHm().put("valueLists", valueLists);
                userview.getHm().put("z0301Lists", z0301Lists);
                for (int b = 0;  b < deletelist.size(); b++) {
                    int number = (Integer) deletelist.get(b);                   
                    valueLists.remove(number);
                }
                msglist.add(msg);
                
            } else {
                if (StringUtils.isNotEmpty(jobTitles)) 
                    jobTitlesList.add(jobTitles);
                String message = this.importExcel(sql.toString(), valueLists, z0301Lists);
                msglist.add(msg);
                msglist.add(message);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }        
            return msglist;
    }

    
    /**
     * 判断excel本行数据是否为空
     * 
     * @param file
     * @param dao 
     * @param hashMap
     * @return
     * @throws Exception
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
        Cell cell = row.getCell(c);
        String cellValue = cell.getStringCellValue();
        if (StringUtils.isNotEmpty(cellValue))
            return false;
        }
            return true;
    }
    
    public String importExcel(String sql, ArrayList valueLists, ArrayList z0301Lists) throws SQLException {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.batchInsert(sql, valueLists);
            StringBuffer z0301Sql = new StringBuffer();
            z0301Sql.append("INSERT INTO zp_members (a0101, a0100, member_type, z0301, member_id, nbase, create_fullname, create_user ) VALUES (?, ?, 1, ?, ?, ?, ?, ?) ");
            String a0101= this.userview.getUserFullName();
            String a0100= this.userview.getA0100();
            String nbase= this.userview.getDbname();
            String fuName= this.userview.getUserFullName();
            String userId= this.userview.getUserId();
            IDGenerator idg = new IDGenerator(2, this.conn);
            
            ArrayList sqlList = new ArrayList < ArrayList < Object >> ();
                for(int i = 0 ; i < z0301Lists.size() ; i++) {
                    ArrayList value = new ArrayList <String> ();
                    value.add(a0101);
                    value.add(a0100);
                    value.add(z0301Lists.get(i));
                    String member_id = idg.getId("zp_members.member_id");
                    value.add(member_id);
                    value.add(nbase);
                    value.add(fuName);
                    value.add(userId);
                    sqlList.add(value);
                  }
                dao.batchInsert(z0301Sql.toString(), sqlList);
            return  "导入完成，导入" + valueLists.size() + "条数据" ;
     } catch (Exception e) { 
         e.printStackTrace();
         return "导入数据出错";
     }
        
    }

    /**
     * 获取导入的sheet
     * @return 拿到要导入的Excel
     * @throws GeneralException
     */
    @Deprecated
    public Sheet getSheet(String path, String filename) throws GeneralException {
        File file = new File(path + "/" + filename);
        InputStream input = null;
        Workbook work = null;
        Sheet sheet = null;
        try {
            // 判断是否为文件
            if (!FileTypeUtil.isFileTypeEqual(file))
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));

            input = new FileInputStream(file);
            work = WorkbookFactory.create(input);
            sheet = work.getSheetAt(0);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(input);
            PubFunc.closeResource(work);
        }
        return sheet;
    }

    private String checkdate(String str) {
        str = StringUtils.isEmpty(str) ? "": str.replace("/", "-");
        if (str.indexOf("日") > -1) 
            str = str.replace(" ", "");

        String dateStr = "false";
        if (str.length() < 4) 
            dateStr = "false";
        else if (str.length() == 4) {
            Pattern p = Pattern.compile("^(\\d{4})$");
            Matcher m = p.matcher(str);
            if (m.matches()) 
                dateStr = str + "-01-01";
            else 
                dateStr = "false";
        } else if (str.length() < 6) {
            Pattern p = Pattern.compile("^(\\d{4})年$");
            Matcher m = p.matcher(str);
            if (m.matches()) 
                dateStr = str.replace("年", "-") + "01-01";
            else 
                dateStr = "false";
        } else if (str.length() == 7) {
            if (str.indexOf("月") != -1) {
                Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
                Matcher m = p.matcher(str);
                if (m.matches()) {
                    if (str.indexOf("月") != -1) 
                        dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
                    else 
                        dateStr = str.replace("年", "-").replace(".", "-") + "-01";
                } else 
                    dateStr = "false";
            } else {
                Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
                Matcher m = p.matcher(str);
                if (m.matches()) 
                    dateStr = str.replace("年", "-").replace(".", "-") + "-01";
                else 
                    dateStr = "false";
            }
        } else if (str.length() < 8) {
            Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
            Matcher m = p.matcher(str);
            if (m.matches()) {
                if (str.indexOf("月") != -1) 
                    dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
                else 
                    dateStr = str.replace("年", "-").replace(".", "-") + "-01";
            } else 
                dateStr = "false";
        } else if (str.length() == 8) { //2010年3  2010年3月1
            Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
            Matcher m = p.matcher(str);
            if (m.matches()) {
                str = str.replace("年", "-").replace(".", "-").replace("月", "-");
                if (str.lastIndexOf("-") == str.length()) {
                    if (str.length() < 10) 
                        dateStr = str + "01";
                } else {
                    String[] temps = str.split("-");
                    if (temps.length > 2) 
                        dateStr = checkMothAndDay(str);
                    else 
                        dateStr = "false";
                }
            } else {
                dateStr = "false";
            }
        } else if (str.length() <= 11) { //2017年1月1日
            Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
            Matcher m = p.matcher(str);
            if (m.matches()) {
                String temp = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
                dateStr = checkMothAndDay(temp);
            } else dateStr = "false";

        } else { //2017年1月1日1时1分      2017年1月1日1时1分1秒 
            str = str.replace("时", ":").replace("分", ":");
            if (str.endsWith(":")) 
                str = str.substring(0, str.length() - 1);

            Pattern p = null;
            if (str.split(":").length < 3) 
                p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]*$");
            else 
                p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]([0-5]*\\d{1})[秒]*$");

            Matcher m = p.matcher(str);
            if (m.matches()) {
                String tempDate = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", " ");
                String temp = tempDate.split(" ")[0];
                dateStr = checkMothAndDay(temp);
                if (!"false".equalsIgnoreCase(dateStr)) {
                    String tempTime = tempDate.split(" ")[1];
                    dateStr += " " + tempTime;
                }
            } else dateStr = "false";
        }

        if (!"false".equals(dateStr)) 
            dateStr = formatDate(dateStr);

        return dateStr;
    }

    /**
     * 校验月与日是否符合规则
     * @param date 日期数据
     * @return
     */
    private String checkMothAndDay(String date) {
        String tempDate = "false";
        String[] dates = date.split("-");
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
            case 12:
                {
                    if (1 <= day && day <= 31) 
                        tempDate = date;

                    break;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                {
                    if (1 <= day && day <= 30) 
                        tempDate = date;

                    break;
                }
            case 2:
                {
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
     *  闰年的条件是：
     *  ① 能被4整除，但不能被100整除；
     *  ② 能被100整除，又能被400整除。
     * @param year
     * @return
     */
    private boolean isLeapYear(int year) {
        boolean t = false;
        if (year % 4 == 0) {
            if (year % 100 != 0) {
                t = true;
            } else if (year % 400 == 0) {
                t = true;
            }
        }
        return t;
    }

    /**
     * 将日期数据1900-1-1 1:1:1转换成1900-01-01 01:01:01
     * @param date 校验完成的数据
     * @return
     */
    private String formatDate(String date) {
        String newDate = "";
        String[] dates = date.split(" ");
        String year = dates[0].split("-")[0];
        String month = dates[0].split("-")[1];
        month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month: month;
        String day = dates[0].split("-")[2];
        day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day: day;
        newDate = year + "-" + month + "-" + day;

        if (dates.length == 2) {
            String[] oldTime = dates[1].split(":");
            String hour = oldTime[0];
            hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour: hour;
            newDate += " " + hour;
            if (oldTime.length > 1) {
                String min = oldTime[1];
                min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min: min;
                newDate += ":" + min;
            }

            if (oldTime.length > 2) {
                String second = oldTime[2];
                second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second: second;
                newDate += ":" + second;
            }
        }

        return newDate;
    }
    
    /**
     * 判断该字段在业务字典和页面业务中是否设置为必填项了
     * @param field 业务字典里的字段
     * @return
     */
    private boolean getRequired(FieldItem field) {
        //业务字典中是否设置为必填项了
        boolean required = field.isFillable();
        String fieldName = field.getItemid().toLowerCase();
        //字段为职位名称，需求单位，招聘人数，有效起始和结束时间 ， 招聘流程，招聘渠道时默认为必填项
        if (required || "Z0351".equalsIgnoreCase(fieldName) || "Z0381".equalsIgnoreCase(fieldName)
                || "Z0377".equalsIgnoreCase(fieldName) || "Z0329".equalsIgnoreCase(fieldName)
                || "Z0331".equalsIgnoreCase(fieldName) || "Z0321".equalsIgnoreCase(fieldName)
                || "Z0336".equalsIgnoreCase(fieldName) ) {
            return true;
        }
        return false;
    }
    
    //根据导入日期字符串长度获取对应的日期格式
  	public String getDateFormat(String dateValue){
  		int itemLength =dateValue.length();
  		String dateFormat ="yyyy-MM-dd";
  		if(itemLength ==4){
  			dateFormat ="yyyy";
  		}else if(itemLength ==7){
  			dateFormat ="yyyy-MM";
  		}else if(itemLength ==10){
  			dateFormat ="yyyy-MM-dd";
  		}else if(itemLength ==16){
  			dateFormat ="yyyy-MM-dd HH:mm";
  		}else if(itemLength >=18){
  			dateFormat ="yyyy-MM-dd HH:mm:ss";
  		}
  		return dateFormat;
  	}
    
    /**
     * 判断该字段在业务字典和页面业务中是否存在和构库
     * @param field 业务字典里的字段
     * @param list  下载模板列集合
     */
    private void getFieldExist(String itemId,ArrayList list) {
    	 FieldItem item = DataDictionary.getFieldItem(itemId);
    	 if (item != null) {
	    	 String useflag = item.getUseflag();
	    	 if("1".equalsIgnoreCase(useflag)){
	    		 if("z0315".equalsIgnoreCase(itemId))
	    			 list.add(item);
	    		 else
	    			 list.add(0,item);
	    	 }
	     }
    }

	/**
	 * @Title: 招聘职位导入数据   
     * @param fileId 文件的加密id
     * @return ArrayList<Object> 报错信息集合    
     * @throws
	 */
	public ArrayList<Object> importPosition(String fileId) {
		//存放错误信息
        ArrayList < Object > msg = new ArrayList < Object > (); 
        ArrayList < Object > msgList = new ArrayList < Object > (); 
		Sheet sheet = this.getSheet(fileId);
		return importPositions(msg, msgList, sheet);
	}
	
	/**
	 * 获取导入的sheet
     * @return 拿到要导入的Excel
     * @throws GeneralException
	 */
	private Sheet getSheet(String fileId) {
        InputStream input = null;
        Workbook work = null;
        Sheet sheet = null;
        try {
            input = VfsService.getFile(fileId);
            work = WorkbookFactory.create(input);
            sheet = work.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(input);
            PubFunc.closeResource(work);
        }
        return sheet;
    }
}

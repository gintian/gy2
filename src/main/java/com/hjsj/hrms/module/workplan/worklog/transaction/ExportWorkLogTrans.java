package com.hjsj.hrms.module.workplan.worklog.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 员工监控导出
 * @Title: ExportWorkLogTrans.java
 * @Description: 用于员工监控导出数据
 * @Company: hjsj
 * @Create time: 2017-3-21 上午11:39:30
 * @author chenxg
 * @version 1.0
 */
public class ExportWorkLogTrans extends IBusiness {

    private boolean haveData = true;

    @Override
    public void execute() throws GeneralException {
        TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("employlog_00001");
        ArrayList outputcolumns = (ArrayList) this.getFormHM().get("outputcolumns");
        int headLevel = (Integer) this.getFormHM().get("headLevel");
        HashMap columnMap = tableCache.getColumnMap();
        String selectPersons = (String) this.getFormHM().get("selectPersons");
        String personSql = getPersonsSql(selectPersons);
        
        ArrayList dataList = getDataList(tableCache, personSql);

        if (outputcolumns.size() > 255) {
            createXLSXFile(outputcolumns, columnMap, dataList, headLevel);
        } else {
            createXLSFile(outputcolumns, columnMap, dataList, headLevel);
        }
    }

    private void createXLSFile(ArrayList outputcolumns, HashMap columnMap, ArrayList dataList,
            int headLevel) {
        // 否则使用xls格式
        // 65536 最大行
        String filename = userView.getUserName()+"_grid.xls";
        HSSFWorkbook workbook = null;
        FileOutputStream fileOut = null;
        try {
        	workbook = new HSSFWorkbook();
            HSSFCellStyle cellstyle = workbook.createCellStyle();
            cellstyle.setAlignment(HorizontalAlignment.CENTER);// 表头居中
            cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellstyle.setBorderBottom(BorderStyle.THIN);
            cellstyle.setBorderTop(BorderStyle.THIN);
            cellstyle.setBorderLeft(BorderStyle.THIN);
            cellstyle.setBorderRight(BorderStyle.THIN);
            
            HSSFCellStyle cellstyle1 = workbook.createCellStyle();
            cellstyle1.setVerticalAlignment(VerticalAlignment.CENTER);
            cellstyle1.setBorderBottom(BorderStyle.THIN);
            cellstyle1.setBorderTop(BorderStyle.THIN);
            cellstyle1.setBorderLeft(BorderStyle.THIN);
            cellstyle1.setBorderRight(BorderStyle.THIN);
            // 起始行为0
            int maxRowNum = 0;
            for (int f = 0; f < Math.ceil((double) dataList.size() / (double) (60000 - headLevel)); f++) {
                // 数据行 每页最大数据量需要减去列头行数
                int colnum = 0;
                int rownum = 0;
                HSSFSheet sheet = workbook.createSheet("第" + (f + 1) + "页");

                // 为了方便合并，先把表头单元格全部创建
                for (; rownum < headLevel; rownum++) {
                    HSSFRow row = sheet.createRow(rownum);
                    maxRowNum++;
                    for (int col = 0; col < outputcolumns.size(); col++) {
                        HSSFCell cell = row.createCell(col);
                        cell.setCellStyle(cellstyle);
                    }
                }
                // 写列头
                for (int i = 0; i < outputcolumns.size(); i++) {
                    DynaBean column = (DynaBean) outputcolumns.get(i);
                    String columnid = column.get("columnid").toString();
                    Integer width = (Integer) column.get("width");
                    ColumnsInfo ci = (ColumnsInfo) columnMap.get(columnid);
                    ArrayList ups = (ArrayList) column.get("ups");
                    Collections.reverse(ups);
                    int b = 0;
                    for (; b < ups.size(); b++) {
                        HSSFCell cell = sheet.getRow(b).getCell(colnum);
                        cell.setCellValue(ups.get(b).toString());
                    }

                    HSSFCell cell = sheet.getRow(b).getCell(colnum);
                    cell.setCellValue(ci.getColumnDesc());
                    sheet.setColumnWidth(colnum, width.intValue() * 40);
                    ExportExcelUtil.mergeCell(sheet, b, colnum, headLevel - 1, colnum);
                    colnum++;
                }
                colnum = 0;
                // 合并相同的列
                for (int k = 0; k < headLevel - 1; k++) {
                    HSSFRow currentRow = sheet.getRow(k);
                    String cellValue = "";
                    String cellId = "";
                    int startIndex = 0;
                    for (int c = 0; c < outputcolumns.size(); c++) {
                        if (currentRow.getCell(c) == null) {
                            startIndex = c;
                            continue;
                        }
                        
                        String value = currentRow.getCell(c).getStringCellValue();
                        currentRow.getCell(c).setCellValue(value.split("`")[0]);
                        if (!cellValue.equals(value)) {
                            if (c - startIndex > 1 && !"".equals(cellValue)) {
                                // 防止同一行连续多列内容为空时，导致列其他行数据重叠 changxy 20160727
                                ExportExcelUtil.mergeCell(sheet, k, startIndex, k, c - 1);
                            }
                            
                            cellValue = value;
                            startIndex = c;
                            continue;
                        }
                        
                        if (c == outputcolumns.size() - 1 && c - startIndex > 1) {
                            ExportExcelUtil.mergeCell(sheet, k, startIndex, k, c);
                        }

                    }

                }
                //显示部门层数
                Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
                String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                if (uplevel == null || uplevel.length() == 0)
                    uplevel = "0";
                
                HSSFCell cell = null;
                HSSFRow row = null;
                // 写数据
                for (int i = 0; i < dataList.size(); i++) {
                    if (maxRowNum - headLevel * (f + 1) < dataList.size()) {
                        DynaBean ldb = (DynaBean) dataList.get(maxRowNum - headLevel * (f + 1));
                        if (ldb == null)
                            continue;
                        // 判断当前行是否超出当前页的65535行
                        if (maxRowNum > (f + 1) * 60000 - 1)
                            break;
                        
                        row = sheet.createRow(rownum);
                        rownum++;
                        colnum = 0;
                        maxRowNum++;

                        for (int k = 0; k < outputcolumns.size(); k++) {
                            cell = row.createCell(colnum);
                            cell.setCellStyle(cellstyle1);
                            DynaBean column = (DynaBean) outputcolumns.get(k);
                            String columnid = column.get("columnid").toString();
                            String value = ldb.get(columnid) == null ? "" : ldb.get(columnid).toString();
                            if (value.length() < 1) {
                                cell.setCellValue(value);
                                colnum++;
                                continue;
                            }
                            ColumnsInfo ci = (ColumnsInfo) columnMap.get(columnid);
                            ArrayList operationData = (ArrayList) column.get("operationData");
                            if (operationData.size() > 0) {
                                for (int c = 0; c < operationData.size(); c++) {
                                    DynaBean valueBean = (DynaBean) operationData.get(c);
                                    if (valueBean.get("dataValue").toString().equals(value)) {
                                        value = valueBean.get("dataName").toString();
                                        cell.setCellValue(value);
                                        break;
                                    }
                                }
                            } else if (StringUtils.isNotEmpty(ci.getCodesetId())
                                    && !"0".equals(ci.getCodesetId())) {
                                String codeName = "";
                                if (haveData)
                                    codeName = value.split("`").length > 1 ? value.split("`")[1]
                                            : AdminCode.getCodeName(ci.getCodesetId(), value);
                                else {
                                    // 当codesetid时UM时，兼容UN
                                    codeName = AdminCode.getCodeName(ci.getCodesetId(), value);
                                    if ("um".equalsIgnoreCase(ci.getCodesetId()) && codeName.length() < 1)
                                        codeName = AdminCode.getCodeName("UN", value);
                                    else if(StringUtils.isNotEmpty(value) && "UM".equalsIgnoreCase(ci.getCodesetId()))
                                        codeName=AdminCode.getCode("UM",value,Integer.parseInt(uplevel)).getCodename();
                                }
                                cell.setCellValue(codeName);
                            } else if ("D".equals(ci.getColumnType())) {
                                String datevalue = value.replace(".", "-");
                                if (ci.getColumnLength() > 0 && ci.getColumnLength() < 17 && datevalue.length() > ci.getColumnLength())
                                    datevalue = datevalue.substring(0, ci.getColumnLength());
                                
                                cell.setCellValue(datevalue);
                            } else if ("M".equals(ci.getColumnType())) {
                                value = value.replace("<br>", "\n");
                                value = value.replace("&nbsp;", " ");
                                cell.setCellValue(value);
                            } else if ("N".equals(ci.getColumnType())) {
                                if (ci.getDecimalWidth() > 0) {
                                    cell.setCellValue(Double.parseDouble(value));
                                    // 设置小数点后格式 changxy
                                    String str = "0000000000";
                                    cellstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0."
                                            + str.substring(1, ci.getDecimalWidth() + 1)));
                                    cell.setCellStyle(cellstyle);
                                } else
                                    cell.setCellValue(Integer.parseInt(value));
                            } else {
                                cell.setCellValue(value);
                            }
                            colnum++;
                        }
                    }
                }

            }

            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + filename);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(fileOut);
            PubFunc.closeResource(workbook);
        }

        filename = PubFunc.encrypt(filename);
        this.getFormHM().put("filename", filename);

    }

    /**
     * 文件超出255列时 生成XLSX格式文件
     * */
    private void createXLSXFile(ArrayList outputcolumns, HashMap columnMap, ArrayList dataList,
            int headLevel) {
    	XSSFWorkbook workbook = null;
        FileOutputStream fileOut = null;
        // 超出255列使用xlsx格式
        String filename = "grid_" + userView.getUserName() + ".xlsx";
        try {
        	workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();

            XSSFCellStyle cellstyle = workbook.createCellStyle();
            cellstyle.setAlignment(HorizontalAlignment.CENTER);// 表头居中
            cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellstyle.setBorderBottom(BorderStyle.THIN);
            cellstyle.setBorderTop(BorderStyle.THIN);
            cellstyle.setBorderLeft(BorderStyle.THIN);
            cellstyle.setBorderRight(BorderStyle.THIN);
            
            XSSFCellStyle cellstyle1 = workbook.createCellStyle();
            cellstyle1.setVerticalAlignment(VerticalAlignment.CENTER);
            cellstyle1.setBorderBottom(BorderStyle.THIN);
            cellstyle1.setBorderTop(BorderStyle.THIN);
            cellstyle1.setBorderLeft(BorderStyle.THIN);
            cellstyle1.setBorderRight(BorderStyle.THIN);
        	
            int colnum = 0;
            int rownum = 0;
            // 为了方便合并，先把表头单元格全部创建
            for (; rownum < headLevel; rownum++) {
                XSSFRow row = sheet.createRow(rownum);

                for (int col = 0; col < outputcolumns.size(); col++) {
                    XSSFCell cell = row.createCell(col);
                    cell.setCellStyle(cellstyle);
                }
            }

            // 写列头
            for (int i = 0; i < outputcolumns.size(); i++) {
                DynaBean column = (DynaBean) outputcolumns.get(i);
                String columnid = column.get("columnid").toString();
                Integer width = (Integer) column.get("width");
                ColumnsInfo ci = (ColumnsInfo) columnMap.get(columnid);
                ArrayList ups = (ArrayList) column.get("ups");
                Collections.reverse(ups);
                int b = 0;
                for (; b < ups.size(); b++) {
                    XSSFCell cell = sheet.getRow(b).getCell(colnum);
                    cell.setCellValue(ups.get(b).toString());
                }

                XSSFCell cell = sheet.getRow(b).getCell(colnum);
                cell.setCellValue(ci.getColumnDesc());
                sheet.setColumnWidth(colnum, width.intValue() * 40);
                ExportExcelUtil.mergeCell(sheet, b, colnum, headLevel - 1, colnum);
                colnum++;
            }
            colnum = 0;

            // 合并相同的列
            for (int k = 0; k < headLevel - 1; k++) {
                XSSFRow currentRow = sheet.getRow(k);
                String cellValue = "";
                int startIndex = 0;
                for (int c = 0; c < outputcolumns.size(); c++) {
                    if (currentRow.getCell(c) == null) {
                        startIndex = c;
                        continue;
                    }
                    String value = currentRow.getCell(c).getStringCellValue();
                    currentRow.getCell(c).setCellValue(value.split("`")[0]);
                    if (!cellValue.equals(value)) {
                        if (c - startIndex > 1 && !"".equals(cellValue)) {
                            // 防止同一行连续多列内容为空时，导致列其他行数据重叠 changxy 20160727
                            ExportExcelUtil.mergeCell(sheet, k, startIndex, k, c - 1);
                        }
                        cellValue = value;
                        startIndex = c;
                        continue;
                    }
                    if (c == outputcolumns.size() - 1 && c - startIndex > 1) {
                        ExportExcelUtil.mergeCell(sheet, k, startIndex, k, c);
                    }

                }

            }

            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            
            XSSFCell cell = null;
            XSSFRow row = null;
            // 写数据
            for (int i = 0; i < dataList.size(); i++) {

                DynaBean ldb = (DynaBean) dataList.get(i);
                if (ldb == null)
                    continue;
                
                row = sheet.createRow(rownum);
                rownum++;
                colnum = 0;

                for (int k = 0; k < outputcolumns.size(); k++) {
                    cell = row.createCell(colnum);
                    cell.setCellStyle(cellstyle1);
                    DynaBean column = (DynaBean) outputcolumns.get(k);
                    String columnid = column.get("columnid").toString();
                    String value = ldb.get(columnid) == null ? "" : ldb.get(columnid).toString();
                    if (value.length() < 1) {
                        cell.setCellValue(value);
                        colnum++;
                        continue;
                    }
                    ColumnsInfo ci = (ColumnsInfo) columnMap.get(columnid);
                    ArrayList operationData = (ArrayList) column.get("operationData");
                    if (operationData.size() > 0) {
                        for (int c = 0; c < operationData.size(); c++) {
                            DynaBean valueBean = (DynaBean) operationData.get(c);
                            if (valueBean.get("dataValue").toString().equals(value)) {
                                value = valueBean.get("dataName").toString();
                                cell.setCellValue(value);
                                break;
                            }
                        }
                    } else if (StringUtils.isNotEmpty(ci.getCodesetId()) && !"0".equals(ci.getCodesetId())) {
                        String codeName = "";
                        if (haveData)
                            codeName = value.split("`").length > 1 ? value.split("`")[1]
                                    : AdminCode.getCodeName(ci.getCodesetId(), value);
                        else {
                            // 当codesetid时UM时，兼容UN
                            codeName = AdminCode.getCodeName(ci.getCodesetId(), value);
                            if ("um".equalsIgnoreCase(ci.getCodesetId()) && codeName.length() < 1)
                                codeName = AdminCode.getCodeName("UN", value);
                            else if(StringUtils.isNotEmpty(value))
                                codeName=AdminCode.getCode("UM",value,Integer.parseInt(uplevel)).getCodename();
                        }
                        
                        cell.setCellValue(codeName);
                    } else if ("D".equals(ci.getColumnType())) {
                        String datevalue = value.replace(".", "-");
                        if (ci.getColumnLength() > 0 && ci.getColumnLength() < 17 && datevalue.length() > ci.getColumnLength())
                            datevalue = datevalue.substring(0, ci.getColumnLength());
                        
                        cell.setCellValue(datevalue);
                    } else if ("M".equals(ci.getColumnType())) {
                        value = value.replace("<br>", "\n");
                        value = value.replace("&nbsp;", " ");
                        cell.setCellValue(value);
                    } else if ("N".equals(ci.getColumnType())) {
                        if (ci.getDecimalWidth() > 0) {
                            cell.setCellValue(Double.parseDouble(value));
                            // 设置小数点后格式 changxy
                            String str = "0000000000";
                            cellstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0."
                                    + str.substring(1, ci.getDecimalWidth() + 1)));
                            cell.setCellStyle(cellstyle);
                        } else
                            cell.setCellValue(Integer.parseInt(value));
                    } else {
                        cell.setCellValue(value);
                    }
                    colnum++;
                }
            }

            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + filename);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(fileOut);
            PubFunc.closeResource(workbook);
        }

        filename = SafeCode.encode(PubFunc.encrypt(filename));
        this.getFormHM().put("filename", filename);
    }

    // 获取数据
    private ArrayList getDataList(TableDataConfigCache tableCache, String whereSql) {
        ArrayList list = null;
        if (tableCache.getTableData() != null) {
            list = tableCache.getTableData();
        } else {
            haveData = false;
            // 获取完整的查询sql ，包含快速过滤和方案查询的条件
            String tableSql = (String) tableCache.get("combineSql");
            String sortSql = tableCache.getSortSql();
            try {
                if(StringUtils.isNotEmpty(whereSql)) 
                    list = (ArrayList)ExecuteSQL.executeMyQuery(tableSql+" and " + whereSql + " "+sortSql, this.frameconn);
                else
                    list = (ArrayList)ExecuteSQL.executeMyQuery(tableSql+" "+sortSql, this.frameconn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }
    
    private String getPersonsSql (String selectPersons) {
        if(StringUtils.isEmpty(selectPersons))
            return "";
        
        StringBuffer personsSql = new StringBuffer("");
        HashMap<String, String> map = new HashMap<String, String>();
        String[] persons = selectPersons.split(",");
        for(int i = 0; i < persons.length; i++){
            String person = persons[i];
            if(StringUtils.isEmpty(person))
                continue;
            
            String nbase = person.split(":")[0];
            nbase = StringUtils.isEmpty(nbase) ? "" : PubFunc.decrypt(nbase); 
            String a0100 = person.split(":")[1];
            a0100 = StringUtils.isEmpty(a0100) ? "" : PubFunc.decrypt(a0100); 
            
            if(StringUtils.isEmpty(nbase) || StringUtils.isEmpty(a0100))
                continue;
            
            String a0100s = map.get(nbase);
            if(StringUtils.isEmpty(a0100s))
                map.put(nbase, "'" + a0100 + "'");
            else
                map.put(nbase, a0100s + ",'" + a0100 + "'");
        }
        
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Entry<String, String> entry = iter.next();
            String nbase = entry.getKey();
            String a0100 = entry.getValue();
            
            if(StringUtils.isNotEmpty(personsSql.toString()))
                personsSql.append(" or");
                
            personsSql.append(" (nbase='" + nbase + "'");
            personsSql.append(" and a0100 in (" + a0100 + "))");
        }
        
        return personsSql.toString();
    }

}

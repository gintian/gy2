package com.hjsj.hrms.module.gz.standard.utils;

import com.hjsj.hrms.module.gz.standard.standard.utils.StandardItemVoUtil;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 〈类功能描述〉
 * 〈导出Excel工具类〉
 *
 * @Author houby
 * @Date 2019/12/05
 * @since 1.0.0
 */

public class StandardUtil {
    /**
     * 导出Excel
     *
     * @param vo 存放表格所有数据的map
     * @param sheetName 表名
     * @param num 创建工作表的Index
     * @param type true true为单张导出 false为多张导出
     * @param wb
     * @return
     */
    public static void executeSingleStandardSheet(StandardItemVoUtil vo, String sheetName, int num, Boolean type, HSSFWorkbook wb)
            throws GeneralException {
        try {
            HSSFSheet sheet = null;
            HSSFCellStyle style = null;
            HSSFCellStyle style_l = null;
            HSSFCellStyle style_r = null;
            HSSFPatriarch patr = null;
            String contentType = "";
            sheet = wb.createSheet();
            short rowNum = 1;

            if (sheetName.indexOf("/") != -1) {
                sheetName = sheetName.replaceAll("/", "");
            }
            wb.setSheetName(num - 1, sheetName);

            //依据规则，数值型居右，其他类型居左。值类型，是数值型(N)、字符型(S)、日期型(D)、备注型(M)
            FieldItem fieldItem = DataDictionary.getFieldItem(vo.getItem());
            if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
                contentType = "R";//数值型居右
            } else {
                contentType = "L";//其他类型居左
            }
            style = getStyle("r", wb);
            style_l = getStyle("l", wb);
            style_r = getStyle("r", wb);

            int h_bottomColumn_num = vo.getH_bottomColumn_num();
            String resultItemType = vo.getResultItemType();   // N ; C
            String codesetid = vo.getCodesetid();
            ArrayList h_List = vo.getH_List();
            ArrayList v_List = vo.getV_List();
            boolean is_h2 = getIsSubItem(h_List);
            boolean is_v2 = getIsSubItem(v_List);
            if (h_List.size() == 0) {   //如果没有设置横向指标
                h_bottomColumn_num = 1;
            }
            executeTableNameCell((short) 1, sheetName, sheet, rowNum);
            rowNum++;
            rowNum = executeTitle(is_h2, is_v2, h_List, vo, type, getStyle("c", wb), style_l, style_r, sheet, patr, rowNum);//列标题居中

            String[] codeSetItemDesc = null;
            //如果是代码类 且为单表导出，则在excel结果数据中填充下拉列表  目前设计情况：所见即所得，Excel中填充下拉列表代码型超出长度。
            //            if(!StringUtils.isBlank(codesetid)&&type){
            //                ArrayList<CodeItem> codeItems= AdminCode.getCodeItemList(codesetid);
            //                if(codeItems!=null){
            //                    codeSetItemDesc=new String[codeItems.size()];//此处单位 部门 需要特殊处理，待修改。
            //                    for(int i=0;i<codeItems.size();i++){
            //                        CodeItem code=codeItems.get(i);
            //                        codeSetItemDesc[i]=code.getCodename();
            //                    }
            //                    int rSnum=0,cSnum=0,rFnum=3,cFnum=2;//c列 r行 s结束 f开始
            //
            //                    if(is_h2) {
            //                        rFnum++;
            //                    }
            //                    if(is_v2) {
            //                        cFnum++;
            //                    }
            //                    rSnum=rFnum+(vo.getV_bottomColumn_num()==0?1:vo.getV_bottomColumn_num())-1;
            //
            //                    cSnum=cFnum+h_bottomColumn_num-1;
            //                    CellRangeAddressList regions = new CellRangeAddressList(rFnum, rSnum, cFnum, cSnum);// 起始行 终止行 起始列 终止列
            //                    DVConstraint constraint = DVConstraint.createExplicitListConstraint(codeSetItemDesc);
            //                    HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
            //                    sheet.addValidationData(data_validation_list);
            //                }
            //            }

            //写表体
            int index = 0;
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            if (v_List.size() == 0) {
                String itemid = vo.getItem();
                FieldItem item = DataDictionary.getFieldItem(itemid);
                String itemdesc = "";
                String contentType_item = "";//未设置纵向标题，结果指标的标题类型 是否是数字，数字居右，其他类型居左
                if (item != null) {
                    itemdesc = item.getItemdesc();
                }
                if (pattern.matcher(itemdesc).matches()) {
                    contentType_item = "R";
                } else {
                    contentType_item = "L";
                }
                executeCell(rowNum, (short) 1, rowNum, (short) 1, itemdesc, contentType_item, style, style_l, style_r, sheet);
                for (int j = 0; j < h_bottomColumn_num; j++) {
                    if ("N".equals(resultItemType)) {
                        String value = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("value");
                        executeCell(rowNum, Short.parseShort(String.valueOf(j + 2)), rowNum, Short.parseShort(String.valueOf(j + 2)), value,
                                contentType, style, style_l, style_r, sheet);
                    } else if ("C".equals(resultItemType)) {
                        String viewvalue = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("viewvalue");
                        executeCell(rowNum, Short.parseShort(String.valueOf(j + 2)), rowNum, Short.parseShort(String.valueOf(j + 2)),
                                viewvalue, contentType, style, style_l, style_r, sheet);
                    }
                    index++;
                }
                rowNum++;
            } else {
                for (int i = 0; i < v_List.size(); i++) {

                    LazyDynaBean v_abean = (LazyDynaBean) v_List.get(i);
                    String name = (String) v_abean.get("name").toString();
                    String childNum = (String) v_abean.get("childNum");
                    String id = (String) v_abean.get("id").toString();
                    String contentType_v = "";//纵向标题的标题类型  是否是数字，数字居右，其他类型居左
                    //判断此处的纵向标题是否是数值型
                    if (pattern.matcher(name).matches()) {
                        contentType_v = "R";
                    } else {
                        contentType_v = "L";
                    }
                    if (!is_v2 || ("0".equals(childNum) && is_v2)) {
                        int columnIndex = 0;
                        if (is_v2 && "0".equals(childNum)) {
                            executeCell(rowNum, (short) 1, rowNum, (short) 2, name, contentType_v, id, type, style, style_l, style_r, sheet,
                                    patr);
                            columnIndex++;
                        } else {
                            executeCell(rowNum, (short) 1, rowNum, (short) 1, name, contentType_v, id, type, style, style_l, style_r, sheet,
                                    patr);
                        }
                        for (int j = 0; j < h_bottomColumn_num; j++) {

                            if ("N".equals(resultItemType)) {
                                String value = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("value");
                                executeCell(rowNum, Short.parseShort(String.valueOf(j + 2 + columnIndex)), rowNum,
                                        Short.parseShort(String.valueOf(j + 2 + columnIndex)), value, contentType, style, style_l, style_r,
                                        sheet);
                            } else if ("C".equals(resultItemType)) {
                                String viewvalue = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("viewvalue");
                                executeCell(rowNum, Short.parseShort(String.valueOf(j + 2 + columnIndex)), rowNum,
                                        Short.parseShort(String.valueOf(j + 2 + columnIndex)), viewvalue, contentType, style, style_l,
                                        style_r, sheet);

                            }
                            index++;
                        }
                        rowNum++;
                    } else {
                        ArrayList s_factor_list = (ArrayList) v_abean.get("s_factor_list");
                        short rowNum2 = Short.parseShort(String.valueOf(rowNum + s_factor_list.size() - 1));
                        executeCell(rowNum, (short) 1, rowNum2, (short) 1, name, contentType_v, id, type, style, style_l, style_r, sheet,
                                patr);
                        for (int j = 0; j < s_factor_list.size(); j++) {
                            LazyDynaBean s_v_abean = (LazyDynaBean) s_factor_list.get(j);
                            String a_name = (String) s_v_abean.get("name").toString();
                            String a_id = (String) s_v_abean.get("id").toString();
                            String contentType_v_s = "";//纵向标题的二级标题的类型 是否是数字，数字居右，其他类型居左
                            if (pattern.matcher(a_name).matches()) {
                                contentType_v_s = "R";
                            } else {
                                contentType_v_s = "L";
                            }
                            if (j != 0) {
                                rowNum++;
                            }
                            executeCell(rowNum, (short) 2, rowNum, (short) 2, a_name, contentType_v_s, a_id, type, style, style_l, style_r,
                                    sheet, patr);
                            for (int e = 0; e < h_bottomColumn_num; e++) {

                                if ("N".equals(resultItemType)) {
                                    String value = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("value");
                                    executeCell(rowNum, Short.parseShort(String.valueOf(e + 3)), rowNum,
                                            Short.parseShort(String.valueOf(e + 3)), value, contentType, style, style_l, style_r, sheet);
                                } else if ("C".equals(resultItemType)) {
                                    String viewvalue = (String) ((LazyDynaBean) vo.getGzItemList().get(index)).get("viewvalue");
                                    executeCell(rowNum, Short.parseShort(String.valueOf(e + 3)), rowNum,
                                            Short.parseShort(String.valueOf(e + 3)), viewvalue, contentType, style, style_l, style_r,
                                            sheet);
                                }
                                index++;
                            }

                        }
                        rowNum++;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 判断该一级指标是否有二级指标
     *
     * @param list 表头信息的list
     * @return flag false 没有二级指标 true 有二级指标
     */
    public static boolean getIsSubItem(ArrayList list) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) list.get(i);
            String childNum = (String) abean.get("childNum");
            if (!"0".equals(childNum)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 创建一个单元格存放表名
     *
     * @param columnIndex 在哪一列创建
     * @param value 标准表名称
     * @param rowNum 行号
     * @return
     */
    public static void executeTableNameCell(short columnIndex, String value, HSSFSheet sheet, short rowNum) {
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell cell = row.createCell(columnIndex);

        cell.setCellValue(value);
    }

    /**
     * 设置单元格数据style
     *
     * @param align 位置
     * @param wb
     * @return a_style
     */
    public static HSSFCellStyle getStyle(String align, HSSFWorkbook wb) {
        HSSFCellStyle a_style = wb.createCellStyle();
        a_style.setBorderBottom(BorderStyle.THIN);
        a_style.setBottomBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderLeft(BorderStyle.THIN);
        a_style.setLeftBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderRight(BorderStyle.THIN);
        a_style.setRightBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderTop(BorderStyle.THIN);
        a_style.setTopBorderColor(HSSFColor.BLACK.index);
        a_style.setVerticalAlignment(VerticalAlignment.CENTER);

        if ("c".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
        } else if ("l".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.LEFT);
        } else if ("r".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.RIGHT);
        } else if ("title".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
            a_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            a_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        }
        return a_style;
    }

    /**
     * 创建表头
     * @param is_h2 是否有行二级指标
     * @param is_v2 是否有列二级指标
     * @param h_List 行表头信息的list
     * @param vo 该表的所有信息
     * @param type 是否多张导出
     * @param style
     * @param style_l
     * @param style_r
     * @param sheet
     * @param patr
     */
    public static short executeTitle(boolean is_h2, boolean is_v2, ArrayList h_List, StandardItemVoUtil vo, Boolean type,
            HSSFCellStyle style, HSSFCellStyle style_l, HSSFCellStyle style_r, HSSFSheet sheet, HSSFPatriarch patr, short rowNum) {

        short columnNum = 0;
        if (is_h2 && is_v2) {
            executeCell(rowNum, (short) 1, rowNum + 1, (short) 2, "", "C", style, style_l, style_r, sheet);
            columnNum = 2;
        } else if (is_v2) {
            executeCell(rowNum, (short) 1, rowNum, (short) 2, "", "C", style, style_l, style_r, sheet);
            columnNum = 2;
        } else if (is_h2) {
            executeCell(rowNum, (short) 1, rowNum + 1, (short) 1, "", "C", style, style_l, style_r, sheet);
            columnNum = 1;
        } else {
            executeCell(rowNum, (short) 1, rowNum, (short) 1, "", "C", style, style_l, style_r, sheet);
            columnNum = 1;
        }
        if (h_List.size() == 0) // 如果没选横栏
        {
            String itemid = vo.getItem();
            String comment = itemid;
            FieldItem item = DataDictionary.getFieldItem(itemid);
            String itemdesc = "";

            if (item != null) {
                itemdesc = item.getItemdesc();
            }
            executeCell(rowNum, Short.parseShort(String.valueOf(columnNum + 1)), rowNum, Short.parseShort(String.valueOf(columnNum + 1)),
                    itemdesc, "C", comment, type, style, style_l, style_r, sheet, patr);
        } else {
            short a_columnNum = columnNum;
            for (int i = 0; i < h_List.size(); i++) {
                LazyDynaBean h_abean = (LazyDynaBean) h_List.get(i);
                String childNum = (String) h_abean.get("childNum");
                String name = (String) h_abean.get("name").toString();
                String id = (String) h_abean.get("id");
                if (is_h2) {
                    if ("0".equals(childNum)) {
                        executeCell(rowNum, Short.parseShort(String.valueOf((i + 1) + a_columnNum)), rowNum + 1,
                                Short.parseShort(String.valueOf((i + 1) + a_columnNum)), name, "C", id, type, style, style_l, style_r,
                                sheet, patr);
                    } else {
                        executeCell(rowNum, Short.parseShort(String.valueOf((i + 1) + a_columnNum)), rowNum,
                                Short.parseShort(String.valueOf((i) + a_columnNum + Integer.parseInt(childNum))), name, "C", id, type,
                                style, style_l, style_r, sheet, patr);
                        a_columnNum = Short.parseShort(String.valueOf(Integer.parseInt(childNum) + a_columnNum - 1));
                    }
                } else {
                    executeCell(rowNum, Short.parseShort(String.valueOf((i + 1) + a_columnNum)), rowNum,
                            Short.parseShort(String.valueOf((i + 1) + a_columnNum)), name, "C", id, type, style, style_l, style_r, sheet,
                            patr);
                }

            }
        }
        rowNum++;
        if (is_h2) {
            int num = 0;
            for (int i = 0; i < h_List.size(); i++) {
                LazyDynaBean h_abean = (LazyDynaBean) h_List.get(i);
                ArrayList s_factor_list = (ArrayList) h_abean.get("s_factor_list");
                //String id=(String) h_abean.get("id");
                for (int j = 0; j < s_factor_list.size(); j++) {

                    LazyDynaBean s_h_abean = (LazyDynaBean) s_factor_list.get(j);
                    String name = (String) s_h_abean.get("name").toString();
                    String id = (String) s_h_abean.get("id");
                    executeCell(rowNum, Short.parseShort(String.valueOf((num + 1) + columnNum)), rowNum,
                            Short.parseShort(String.valueOf((num + 1) + columnNum)), name, "C", id, type, style, style_l, style_r, sheet,
                            patr);
                    num++;
                }
            }
            rowNum++;
        }
        return rowNum;
    }

    /**
     * 创建数据单元格
     * @param firstRow 数据行第一行
     * @param firstCol 第一列
     * @param lastRow 最后一行
     * @param lastCol 最后一列
     * @param content 单元格数据
     * @param style 单元格内数据的位置
     * @param hs_style
     * @param style_l
     * @param style_r
     * @param sheet
     */
    public static void executeCell(int firstRow, short firstCol, int lastRow, short lastCol, String content, String style,
            HSSFCellStyle hs_style, HSSFCellStyle style_l, HSSFCellStyle style_r, HSSFSheet sheet) {
        try {
            HSSFRow row = sheet.getRow(firstRow);
            if (row == null) {
                row = sheet.createRow(firstRow);
            }

            row.setHeight((short) 400);
            HSSFCell cell = row.getCell(firstCol);
            if (cell == null) {
                cell = row.createCell(firstCol);
            }
            if ("c".equalsIgnoreCase(style)) {
                cell.setCellStyle(hs_style);
            } else if ("l".equalsIgnoreCase(style)) {
                cell.setCellStyle(style_l);
            } else if ("R".equalsIgnoreCase(style)) {
                cell.setCellStyle(style_r);
            }
            cell.setCellValue(content);

            short b1 = firstCol;
            while (++b1 <= lastCol) {
                cell = row.getCell(b1);
                if (cell == null) {
                    cell = row.createCell(b1);
                }

                cell.setCellStyle(hs_style);
            }

            for (int a1 = firstRow + 1; a1 <= lastRow; a1++) {
                row = sheet.createRow(a1);
                if (row == null) {
                    row = sheet.createRow(a1);
                }
                b1 = firstCol;
                while (b1 <= lastCol) {
                    cell = row.getCell(b1);
                    if (cell == null) {
                        cell = row.createCell(b1);
                    }

                    cell.setCellStyle(hs_style);
                    b1++;
                }
            }

            ExportExcelUtil.mergeCell(sheet, firstRow, firstCol, lastRow, lastCol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据每一列创建数据单元格
     * @param firstRow 数据行第一行
     * @param firstCol 第一列
     * @param lastRow 最后一行
     * @param lastCol 最后一列
     * @param content 单元格数据
     * @param type
     * @param style 单元格内数据的位置
     * @param hs_style
     * @param style_l
     * @param style_r
     * @param sheet
     * @param patr
     */
    public static void executeCell(int firstRow, short firstCol, int lastRow, short lastCol, String content, String style, String comment,
            Boolean type, HSSFCellStyle hs_style, HSSFCellStyle style_l, HSSFCellStyle style_r, HSSFSheet sheet, HSSFPatriarch patr) {
        try {
            HSSFRow row = sheet.getRow(firstRow);
            if (patr == null) {
                patr = sheet.createDrawingPatriarch();
            }
            if (row == null) {
                row = sheet.createRow(firstRow);
            }

            row.setHeight((short) 400);
            HSSFCell cell = row.getCell(firstCol);
            if (cell == null) {
                cell = row.createCell(firstCol);
            }

            if ("c".equalsIgnoreCase(style)) {
                cell.setCellStyle(hs_style);
            } else if ("l".equalsIgnoreCase(style)) {
                cell.setCellStyle(style_l);
            } else if ("R".equalsIgnoreCase(style)) {
                cell.setCellStyle(style_r);
            }

            if (StringUtils.isNotBlank(comment) && type) {//当注释不为空时
                HSSFComment comm = patr.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 5, 3));
                comm.setString(new HSSFRichTextString(comment));
                cell.setCellComment(comm);
            }

            cell.setCellValue(content);

            short b1 = firstCol;
            while (++b1 <= lastCol) {
                cell = row.getCell(b1);
                if (cell == null) {
                    cell = row.createCell(b1);
                }

                cell.setCellStyle(hs_style);
            }

            for (int a1 = firstRow + 1; a1 <= lastRow; a1++) {
                row = sheet.createRow(a1);
                if (row == null) {
                    row = sheet.createRow(a1);
                }
                b1 = firstCol;
                while (b1 <= lastCol) {
                    cell = row.getCell(b1);
                    if (cell == null) {
                        cell = row.createCell(b1);
                    }

                    cell.setCellStyle(hs_style);
                    b1++;
                }
            }

            ExportExcelUtil.mergeCell(sheet, firstRow, firstCol, lastRow, lastCol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取表头的List
     * @param factor 横纵向一级指标
     * @param s_factor 横纵向二级指标
     * @param content 横纵向指标项列表
     * @param itemNameMap 表头名字的集合
     * @return 表头信息的List集合
     */
    public static ArrayList get_List(String factor, String s_factor, String content, HashMap itemNameMap) {
        ArrayList list = new ArrayList();
        content = PubFunc.keyWord_reback(content);
        if (factor != null && s_factor != null && factor.trim().length() > 0 && s_factor.trim().length() > 0) {
            String[] temps = content.split(";");
            HashMap h_itemMap = (HashMap) itemNameMap.get(factor.toLowerCase());
            HashMap s_h_itemMap = (HashMap) itemNameMap.get(s_factor.toLowerCase());

            for (int i = 0; i < temps.length; i++) {
                LazyDynaBean a_LazyDynaBean = new LazyDynaBean();
                String temp = temps[i];
                String id = temp.substring(0, temp.indexOf("["));
                String name = "";
                if (h_itemMap.get(id) != null) {
                    name = (String) h_itemMap.get(id);
                }
                a_LazyDynaBean.set("id", id);
                a_LazyDynaBean.set("name", name);
                a_LazyDynaBean.set("isFactor", "1");  //1: hfactor  2:s_hfactor
                String s_hfactor_value = temp.substring(temp.indexOf("[") + 1, temp.indexOf("]"));
                if (s_hfactor_value.length() == 0) {
                    a_LazyDynaBean.set("childNum", "0");
                    a_LazyDynaBean.set("s_factor_list", new ArrayList());
                } else {
                    String[] temp2 = s_hfactor_value.split(",");
                    a_LazyDynaBean.set("childNum", String.valueOf(temp2.length));

                    ArrayList s_factor_list = new ArrayList();
                    for (int j = 0; j < temp2.length; j++) {
                        LazyDynaBean a_LazyDynaBean2 = new LazyDynaBean();
                        a_LazyDynaBean2.set("id", temp2[j]);
                        if (s_h_itemMap.get(temp2[j].toLowerCase()) != null) {
                            a_LazyDynaBean2.set("name", (String) s_h_itemMap.get(temp2[j].toLowerCase()));
                        } else if (s_h_itemMap.get(temp2[j].toUpperCase()) != null) {
                            a_LazyDynaBean2.set("name", (String) s_h_itemMap.get(temp2[j].toUpperCase()));
                        } else {
                            a_LazyDynaBean2.set("name", "");
                        }
                        a_LazyDynaBean2.set("isFactor", "2");  //1: factor  2:s_factor
                        a_LazyDynaBean2.set("childNum", "0");
                        s_factor_list.add(a_LazyDynaBean2);
                    }
                    a_LazyDynaBean.set("s_factor_list", s_factor_list);
                }

                list.add(a_LazyDynaBean);
            }
        } else if (factor != null && factor.trim().length() > 0) {
            String[] temps = content.split(";");
            HashMap h_itemMap = (HashMap) itemNameMap.get(factor.toLowerCase());
            for (int i = 0; i < temps.length; i++) {
                LazyDynaBean a_LazyDynaBean = new LazyDynaBean();
                String temp = temps[i];
                String id = "";
                if (temp.indexOf("[") == -1) {
                    id = temp;
                } else {
                    id = temp.substring(0, temp.indexOf("["));
                }

                String name = "";
                if (h_itemMap.get(id) != null) {
                    name = (String) h_itemMap.get(id);
                }
                a_LazyDynaBean.set("id", id);
                a_LazyDynaBean.set("name", name);
                a_LazyDynaBean.set("isFactor", "1");  //1: hfactor  2:s_hfactor
                a_LazyDynaBean.set("childNum", "0");
                a_LazyDynaBean.set("s_factor_list", new ArrayList());
                list.add(a_LazyDynaBean);
            }
        } else if (s_factor != null && s_factor.trim().length() > 0) {

            HashMap s_h_itemMap = (HashMap) itemNameMap.get(s_factor.toLowerCase());
            String s_factor_value = content.substring(content.indexOf("[") + 1, content.indexOf("]"));
            String[] temp2 = s_factor_value.split(",");
            for (int j = 0; j < temp2.length; j++) {
                LazyDynaBean a_LazyDynaBean = new LazyDynaBean();
                a_LazyDynaBean.set("id", temp2[j]);
                if (s_h_itemMap.get(temp2[j].toLowerCase()) != null) {
                    a_LazyDynaBean.set("name", (String) s_h_itemMap.get(temp2[j].toLowerCase()));
                } else if (s_h_itemMap.get(temp2[j].toUpperCase()) != null) {
                    a_LazyDynaBean.set("name", (String) s_h_itemMap.get(temp2[j].toUpperCase()));
                } else {
                    a_LazyDynaBean.set("name", "");
                }
                a_LazyDynaBean.set("isFactor", "2");  //1: factor  2:s_factor
                a_LazyDynaBean.set("childNum", "0");
                a_LazyDynaBean.set("s_factor_list", new ArrayList());
                list.add(a_LazyDynaBean);
            }
        }
        return list;
    }

    /**
     * 获取表头的长度
     * @param list 表头信息的集合
     * @return num 表头的长度
     */
    public static int get_bottomColumn_num(ArrayList list) {
        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            LazyDynaBean a_LazyDynaBean = (LazyDynaBean) list.get(i);
            String isFactor = (String) a_LazyDynaBean.get("isFactor");
            String childNum = (String) a_LazyDynaBean.get("childNum");
            if ("1".equals(isFactor) && !"0".equals(childNum)) {
                num += Integer.parseInt(childNum);
            } else {
                num++;
            }
        }
        return num;
    }

    /**
     * 获取单元格数据
     * @param vo 保存着标准表的数据
     * @param itemValueMap 标准表的单元格数据map
     * @return 单元格数据list
     */
    public static ArrayList gzItemList(StandardItemVoUtil vo, HashMap itemValueMap) {
        ArrayList itemList = new ArrayList();
        ArrayList hList = vo.getH_List();
        ArrayList vList = vo.getV_List();
        if (vList.size() == 0) {
            addItemVo(hList, null, null, itemList, vo.getResultItemType(), vo.getCodesetid(), itemValueMap);
        } else {
            for (int i = 0; i < vList.size(); i++) {
                LazyDynaBean a_LazyDynaBean = (LazyDynaBean) vList.get(i);
                String id = (String) a_LazyDynaBean.get("id");
                String isFactor = (String) a_LazyDynaBean.get("isFactor");
                String childNum = (String) a_LazyDynaBean.get("childNum");
                if ("1".equals(isFactor) && !"0".equals(childNum)) {
                    ArrayList s_factor_list = (ArrayList) a_LazyDynaBean.get("s_factor_list");
                    for (int j = 0; j < s_factor_list.size(); j++) {
                        LazyDynaBean a_LazyDynaBean2 = (LazyDynaBean) s_factor_list.get(j);
                        String id2 = (String) a_LazyDynaBean2.get("id");
                        addItemVo(hList, id, id2, itemList, vo.getResultItemType(), vo.getCodesetid(), itemValueMap);
                    }

                } else {
                    if ("1".equals(isFactor)) {
                        addItemVo(hList, id, null, itemList, vo.getResultItemType(), vo.getCodesetid(), itemValueMap);
                    } else {
                        addItemVo(hList, null, id, itemList, vo.getResultItemType(), vo.getCodesetid(), itemValueMap);
                    }
                }
            }
        }
        return itemList;
    }

    /**
     * 添加item的数据
     * @param hList 行表头的集合
     * @param vfactor 纵向指标
     * @param s_vfactor 二级纵向指标
     * @param list 用于保存单元格数据的list
     * @param resultItemType 结果指标的类型
     * @param codesetid 代码项
     * @param itemValueMap 保存着单元格数据的map集合
     * @return 单元格数据list
     */
    public static void addItemVo(ArrayList hList, String vfactor, String s_vfactor, ArrayList list, String resultItemType, String codesetid,
            HashMap itemValueMap) {
        if (hList.size() == 0) {
            LazyDynaBean itemBean = new LazyDynaBean();
            if (vfactor != null) {
                itemBean.set("vvalue", vfactor);
            }
            if (s_vfactor != null) {
                itemBean.set("s_vvalue", s_vfactor);
            }
            String key = getMapKey(null, null, vfactor, s_vfactor).toLowerCase();
            if (itemValueMap.get(key) != null) {
                itemBean.set("value", (String) itemValueMap.get(key));
                if ("C".equalsIgnoreCase(resultItemType)) {
                    itemBean.set("viewvalue", AdminCode.getCodeName(codesetid, (String) itemValueMap.get(key)));
                } else {
                    //                    itemBean.set("value", "");
                    itemBean.set("viewvalue", "");
                }
            }
            list.add(itemBean);
        } else {
            for (int i = 0; i < hList.size(); i++) {
                LazyDynaBean a_LazyDynaBean = (LazyDynaBean) hList.get(i);
                String id = (String) a_LazyDynaBean.get("id");
                String isFactor = (String) a_LazyDynaBean.get("isFactor");
                String childNum = (String) a_LazyDynaBean.get("childNum");
                if ("1".equals(isFactor) && !"0".equals(childNum)) {
                    ArrayList s_factor_list = (ArrayList) a_LazyDynaBean.get("s_factor_list");
                    for (int j = 0; j < s_factor_list.size(); j++) {
                        LazyDynaBean a_LazyDynaBean2 = (LazyDynaBean) s_factor_list.get(j);
                        String id2 = (String) a_LazyDynaBean2.get("id");

                        LazyDynaBean itemBean = new LazyDynaBean();
                        if (vfactor != null) {
                            itemBean.set("vvalue", vfactor);
                        }
                        if (s_vfactor != null) {
                            itemBean.set("s_vvalue", s_vfactor);
                        }
                        itemBean.set("hvalue", id);
                        itemBean.set("s_hvalue", id2);
                        String key = getMapKey(id, id2, vfactor, s_vfactor).toLowerCase();
                        if (itemValueMap.get(key) != null) {
                            itemBean.set("value", (String) itemValueMap.get(key));
                            if ("C".equalsIgnoreCase(resultItemType)) {
                                itemBean.set("viewvalue", AdminCode.getCodeName(codesetid, (String) itemValueMap.get(key)));
                            }
                        } else {
                            itemBean.set("value", "");
                            itemBean.set("viewvalue", "");
                        }
                        list.add(itemBean);
                    }

                } else {
                    LazyDynaBean itemBean = new LazyDynaBean();
                    if (vfactor != null) {
                        itemBean.set("vvalue", vfactor);
                    }
                    if (s_vfactor != null) {
                        itemBean.set("s_vvalue", s_vfactor);
                    }
                    String a_hvalue = "";
                    String a_shvalue = "";
                    if ("1".equals(isFactor)) {
                        itemBean.set("hvalue", id);
                        a_hvalue = id;
                        a_shvalue = null;
                        //	itemBean.set("s_hvalue",null);
                    } else {
                        //	itemBean.set("hvalue",null);
                        itemBean.set("s_hvalue", id);
                        a_hvalue = null;
                        a_shvalue = id;
                    }
                    String key = getMapKey(a_hvalue, a_shvalue, vfactor, s_vfactor).toLowerCase();
                    if (itemValueMap.get(key) != null) {
                        itemBean.set("value", (String) itemValueMap.get(key));
                        if ("C".equalsIgnoreCase(resultItemType)) {
                            itemBean.set("viewvalue", AdminCode.getCodeName(codesetid, (String) itemValueMap.get(key)));
                        }
                    } else {
                        itemBean.set("value", "");
                        itemBean.set("viewvalue", "");
                    }
                    list.add(itemBean);
                }
            }
        }
    }

    /**
     * 获取itemValueMap的key
     * @param hvalue 行一级指标的id
     * @param s_hvalue 行二级指标的id
     * @param vvalue 纵向指标值
     * @param s_vvalue 二级纵向指标值
     * @return String key：itemValueMap的key
     */
    public static String getMapKey(String hvalue, String s_hvalue, String vvalue, String s_vvalue) {
        StringBuffer key = new StringBuffer("");
        if (hvalue == null) {
            key.append("#" + "|");
        } else {
            key.append(hvalue + "|");
        }
        if (s_hvalue == null) {
            key.append("#" + "|");
        } else {
            key.append(s_hvalue + "|");
        }
        if (vvalue == null) {
            key.append("#" + "|");
        } else {
            key.append(vvalue + "|");
        }
        if (s_vvalue == null) {
            key.append("#");
        } else {
            key.append(s_vvalue);
        }
        return key.toString();
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 获取导入数据 并进行数据校验。
     * @Author houby
     * @param fileId :待导入的文件对象id
     * @param fileName :错误详情文件名
     * @param bean :当前标准表的表结构
     * @return ArrayList
     * @Date 2019/12/06 16:03
     */
    public static Map<String, Object> getInputDataList(String fileId, String fileName, LazyDynaBean bean) {
        ArrayList dataList = new ArrayList();
        List<List<Object>> errCellItems = new ArrayList<List<Object>>();
        HSSFSheet sheet = null;
        InputStream stream = null;
        Map<String,Object> returnMap = new HashMap<String,Object>();
        String isError = null;
        HSSFWorkbook wb = null;
        try {
            int sCol = 0, sRow = 0, eCol = 0, eRow = 0;
            int colCount = 0;//纵向栏目数量
            int rowCount = 0;//横线栏目数量
            sCol = 1;
            sRow = 2;//下标从0开始 设定其实单元格为左上第一个空格
            try {
                stream = VfsService.getFile(fileId);
                wb = new HSSFWorkbook(stream);
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                e.printStackTrace();
                isError = "请用导出的Excel模板导入数据！  ";
            }
            String sheetName = sheet.getSheetName();
            if (!bean.get("name").toString().equalsIgnoreCase(sheetName)) {
                isError = "导入文件和当前标准表名称不匹配！  ";
                returnMap.put("isError",isError);
                returnMap.put("dataList",dataList);
                return returnMap;
            }
            Row row = sheet.getRow(2);
            if (row == null) {
                throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));
            }
            int cols = row.getPhysicalNumberOfCells();// 获取不为空的列个数。
            int rows = sheet.getPhysicalNumberOfRows();// 是获取不为空的行个数。
            eCol = cols;
            eRow = rows;
            if (!StringUtils.isBlank((String) bean.get("hfactor"))) {
                rowCount++;
            }
            if (!StringUtils.isBlank((String) bean.get("s_hfactor"))) {
                rowCount++;
            }
            if (!StringUtils.isBlank((String) bean.get("vfactor"))) {
                colCount++;
            }
            if (!StringUtils.isBlank((String) bean.get("s_vfactor"))) {
                colCount++;
            }
            String item = (String) bean.get("item");
            FieldItem fieldItem = DataDictionary.getFieldItem(item);
            Map<String, Object> resultMap = getDataList(sCol, sRow, eCol, eRow, colCount, rowCount, sheet, fieldItem, bean, sheet);
            dataList = (ArrayList) resultMap.get("dataList");
            errCellItems = (List<List<Object>>) resultMap.get("errCellItems");
            if (errCellItems != null && errCellItems.size() > 0) {//如果存在错误数据
                setErrorText(stream, fileName, wb, sheet,errCellItems);
                isError = "1";
                returnMap.put("isError",isError);
                returnMap.put("dataList",dataList);
                return returnMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(stream);
            PubFunc.closeIoResource(wb);
        }
        returnMap.put("isError",isError);
        returnMap.put("dataList",dataList);
        return returnMap;
    }

    /**
     * 获取数据 并检查数据正确性
     * @param sCol 开始列
     * @param sRow    开始行
     * @param eCol 结束列
     * @param eRow 结束行
     * @param colCount 纵排标题数量
     * @param rowCount 横排标题数量
     * @param sheet
     * @param fieldItem
     * @param bean
     * @param rows
     */
    public static Map<String, Object> getDataList(int sCol, int sRow, int eCol, int eRow, int colCount, int rowCount, HSSFSheet sheet,
            FieldItem fieldItem, LazyDynaBean bean, HSSFSheet rows) {
        ArrayList dataList = new ArrayList();
        List<List<Object>> errCellItems = new ArrayList<List<Object>>();
        try {
            HashMap<Integer, String> HfactorMap = new HashMap<Integer, String>();
            HashMap<Integer, String> s_HfactorMap = new HashMap<Integer, String>();

            String Vfactor = "", s_Vfactor = "";
            Row row = sheet.getRow(2);
            Cell cell = null;
            String tempComment = "", colComment = "";
            //获取横排标题批注
            if (rowCount != 0) {
                for (int i = sCol + (colCount == 0 ? 1 : colCount); i <= eCol; i++) {//获取第一标题行数据批注
                    cell = row.getCell(i);
                    tempComment = getComment(cell);
                    if (!StringUtils.isBlank(tempComment)) {
                        colComment = tempComment;
                    }

                    if (StringUtils.isBlank(colComment) || (StringUtils.isBlank(tempComment) && !StringUtils
                            .isBlank(cell.getStringCellValue())))//未设置批注
                    {
                        setErrCellItems(cell, "未设置批注", sheet, errCellItems);
                    }
                    if (StringUtils.isBlank((String) bean.get("hfactor"))) {
                        s_HfactorMap.put(i, colComment);
                    } else {
                        HfactorMap.put(i, colComment);
                    }
                }
            }
            if (rowCount == 2) {//获取第二标题行数据批注
                row = sheet.getRow(3);
                for (int i = sCol + (colCount == 0 ? 1 : colCount); i <= eCol; i++) {
                    cell = row.getCell(i);
                    colComment = getComment(cell);

                    s_HfactorMap.put(i, colComment);

                }
            }
            //获取codeMap 用于转换数据
            HashMap dataMap = new HashMap<String, Object>();
            HashMap codeColMap = new HashMap<String, String>();
            if (!"0".equals(fieldItem.getCodesetid())) {
                ArrayList<CodeItem> codeItems = AdminCode.getCodeItemList(fieldItem.getCodesetid());

                for (int i = 0; i < codeItems.size(); i++) {
                    CodeItem code = codeItems.get(i);
                    codeColMap.put(code.getCodename(), code.getCcodeitem());
                }
            }

            String tempVfactor = "";
            for (int j = sRow + (rowCount == 0 ? 1 : rowCount); j <= eRow; j++) {//获取数据区域

                row = sheet.getRow(j);

                for (int i = sCol; i <= eCol; i++) {

                    cell = row.getCell(i);
                    Object value;
                    if (colCount == 0 && i == sCol) {
                        continue;
                    } else if (colCount >= 1 && i == sCol && !StringUtils.isBlank((String) bean.get("vfactor"))) {
                        tempVfactor = getComment(cell);
                        if (!StringUtils.isBlank(tempVfactor)) {
                            Vfactor = tempVfactor;
                        }

                        if (StringUtils.isBlank(Vfactor))//未设置批注
                        {
                            setErrCellItems(cell, "未设置批注", sheet, errCellItems);
                        }
                        continue;

                    } else if (colCount == 1 && i == sCol) {
                        s_Vfactor = getComment(cell);
                        if (StringUtils.isBlank(s_Vfactor))//未设置批注
                        {
                            setErrCellItems(cell, "未设置批注", sheet, errCellItems);
                        }
                        continue;
                    } else if (colCount == 2 && i == sCol + 1) {
                        s_Vfactor = getComment(cell);
                        // 暂时无法判断单元格是否为合并。若第二个单元格没有备注且没有内容 则需第一个单元格备注信息
                        if (StringUtils.isBlank(s_Vfactor) && StringUtils.isBlank(cell.getStringCellValue()) && !StringUtils
                                .isBlank(Vfactor)) {
                            s_Vfactor = s_Vfactor;
                        } else if (StringUtils.isBlank(s_Vfactor))//未设置批注
                        {
                            setErrCellItems(cell, "未设置批注", sheet, errCellItems);
                        }
                        continue;

                    }
                    dataMap = new HashMap<String, Object>();

                    value = getCellValue(cell, fieldItem, codeColMap, sheet, errCellItems);

                    if (HfactorMap.size() != 0) {
                        dataMap.put("Hfactor", HfactorMap.get(i));
                    } else {
                        dataMap.put("Hfactor", "");
                    }

                    if (s_HfactorMap.size() != 0) {
                        dataMap.put("s_Hfactor", s_HfactorMap.get(i));
                    } else {
                        dataMap.put("s_Hfactor", "");
                    }

                    dataMap.put("Vfactor", Vfactor);
                    dataMap.put("s_Vfactor", s_Vfactor);

                    dataMap.put("value", value);

                    dataList.add(dataMap);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("dataList", dataList);
        resultMap.put("errCellItems", errCellItems);
        return resultMap;
    }

    /**
     * 获取批注
     * @param cell 单元格
     * @return
     * @throws GeneralException
     */
    private static String getComment(Cell cell) throws GeneralException {
        try {
            String colComment = "";
            // 如果excel的列没有注释说明，不能导入
            if (cell.getCellComment() == null) {
                colComment = "";
            } else {
                colComment = cell.getCellComment().getString().getString().trim();
            }
            return colComment;

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 写入错误单元格数据
     * @param cell 错误单元格
     * @param errStr 错误内容
     * @param sheet
     */
    private static void setErrCellItems(Cell cell, String errStr, HSSFSheet sheet, List<List<Object>> errCellItems) {
        List<Object> arr = new ArrayList<Object>();
        arr.add(cell);
        arr.add(errStr);
        errCellItems.add(arr);
    }

    /**
     * 输出错误信息excel
     * @param stream 文件对象流
     * @param fileName 错误详情文件名
     * @param wb
     * @param sheet
     * @param errCellItems
     */
    private static void setErrorText(InputStream stream, String fileName, HSSFWorkbook wb, HSSFSheet sheet, List<List<Object>> errCellItems) {
        FileOutputStream out = null;
        try {
            HSSFPatriarch patr = sheet.getDrawingPatriarch();
            for (List<Object> list : errCellItems) {
                Cell cell = (Cell) list.get(0);
                String errorText = (String) list.get(1);
                if (StringUtils.isNotBlank(errorText)) {//当注释不为空时
                    HSSFComment comm = patr.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 7, 5));
                    comm.setString(new HSSFRichTextString(errorText));
                    cell.setCellComment(comm);
                }
                HSSFCellStyle style = wb.createCellStyle();
                style.setFillForegroundColor(HSSFColor.YELLOW.index);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setBorderBottom(BorderStyle.THIN); //下边框
                style.setBorderLeft(BorderStyle.THIN);//左边框
                style.setBorderTop(BorderStyle.THIN);//上边框
                style.setBorderRight(BorderStyle.THIN);//右边框
                cell.setCellStyle(style);
            }
            String newPath = System.getProperty("java.io.tmpdir");
            newPath = newPath + File.separator + fileName;
            File outFile = new File(newPath);
            out = new FileOutputStream(outFile);
            out.flush();
            wb.write(out);
            //vfs改造后需将文件写到临时目录下，以供下载
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(out);
        }
    }

    /**
     * 获取单元格内容
     * @param cell 单元格数据
     * @param fieldItem 指标对象
     * @param codeColMap 行指标集合
     * @param sheet
     * @return
     */
    private static Object getCellValue(Cell cell, FieldItem fieldItem, HashMap<String, String> codeColMap, HSSFSheet sheet,
            List<List<Object>> errCellItems) throws GeneralException {
        Object value = null;
        try {
            String itemtype = fieldItem.getItemtype(); // 值类型，是数值型(N)、字符型(S)、日期型(D)、备注型(M)
            String codesetid = fieldItem.getCodesetid(); // 代码类id
            int decwidth = fieldItem.getDecimalwidth(); // 小数点位数
            int width = fieldItem.getItemlength();
            width += decwidth;
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_FORMULA:
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        double numericCell = cell.getNumericCellValue();//获取数值型单元格的值
                        if (String.valueOf(numericCell).indexOf('.') > 0 && decwidth == 0) {
                            width++;
                        }
                        value = getNumValue(decwidth, numericCell, width + 1, cell, sheet, errCellItems);
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = cell.getRichStringCellValue().toString();
                        value = getStrValue(value.toString(), codesetid, codeColMap, itemtype, decwidth, cell, width, sheet, errCellItems);
                        break;
                    case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
                        break;
                    default:
                        value = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return value;
    }

    /**
     * 当单元格是数值时 获取其值
     * @param decwidth 小数点位数
     * @param numericCell 数值型单元格的值
     * @param sheet
     * @param errCellItems
     * @return
     * @throws GeneralException
     */
    private static Double getNumValue(int decwidth, double numericCell, int width, Cell cell, HSSFSheet sheet,
            List<List<Object>> errCellItems) throws GeneralException {
        try {
            String value = Double.toString(numericCell);
            if (value.indexOf("E") > -1) {
                String x1 = value.substring(0, value.indexOf("E"));
                String y1 = value.substring(value.indexOf("E") + 1);

                value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
            }
            if (value.length() > width) {
                setErrCellItems(cell, "数字长度过长", sheet, errCellItems);
                return 0.0;
            }

            value = PubFunc.round(value, decwidth);
            return new Double((PubFunc.round(value, decwidth)));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 当单元格是字符时 获取值
     * @param value 单元格的值
     * @param codesetid 代码项
     * @param codeColMap 列代码项集合
     * @param itemtype 代码类型
     * @param decwidth 小数点位数
     * @param sheet
     * @param errCellItems
     * @return
     */
    private static Object getStrValue(String value, String codesetid, HashMap codeColMap, String itemtype, int decwidth, Cell cell,
            int width, HSSFSheet sheet, List<List<Object>> errCellItems) {
        Object value1 = null;
        try {
            if (StringUtils.isNotBlank(value)) {
                if (!"0".equals(codesetid) && StringUtils.isNotBlank(codesetid)) {   // 代码类id{
                    if (codeColMap.get(value.trim()) != null) {
                        value = (String) codeColMap.get(value.trim());
                    } else if (value != null && !StringUtils.isBlank(value)) {
                        setErrCellItems(cell, "系统中不存在此代码", sheet, errCellItems);
                    } else {
                        value = null;
                    }
                }
                if ("N".equals(itemtype)) {
                    try {
                        value = PubFunc.round(value, decwidth);
                    } catch (Exception e) {
                        setErrCellItems(cell, "应写入数字格式", sheet, errCellItems);
                        return "";
                    }
                    if (value.length() > width) {
                        setErrCellItems(cell, "数字长度过长", sheet, errCellItems);
                        return "";
                    }
                    if (decwidth == 0) {
                        value1 = new Integer(value);
                    } else {
                        value1 = new Double(value);
                    }
                } else if ("D".equals(itemtype) && !"0".equals(value)) {
                    java.sql.Date d_t = null;
                    value = value.replaceAll("\\.", "-");
                    java.util.Date src_d_t = DateUtils.getDate(value, "yyyy-MM-dd");
                    if (src_d_t != null) {
                        d_t = new java.sql.Date(src_d_t.getTime());
                    }
                    value1 = d_t;
                } else {
                    value1 = value;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrCellItems(cell, e.getMessage(), sheet, errCellItems);
        }
        return value1;
    }
    /**
     * 获取权限 (操作单位>管理范围)
     * @author wangbs
     * @param userView 用户信息
     * @return String
     * @date 2019/12/12 14:25
     */
    public static String getUnitOrManagePriv(UserView userView) {
        String orgPriv = "";
        //操作单位
        String unit_id = userView.getUnit_id();
        //管理范围
        String managePrivCode = userView.getManagePrivCode();
        String managePrivCodeValue = userView.getManagePrivCodeValue();
        if (userView.isSuper_admin() || IStandardPackageService.ALL_PRIV.equalsIgnoreCase(unit_id)) {
            orgPriv = "all";
        } else if (StringUtils.isNotBlank(unit_id) && unit_id.length() > IStandardPackageService.UNIT_ID_LENGTH) {
            String[] unitIdArr = unit_id.split("`");
            orgPriv = unitIdArr[0].substring(2);
        } else {
            if (IStandardPackageService.ALL_PRIV.contains(managePrivCode) && StringUtils.isBlank(managePrivCodeValue)) {
                orgPriv = "all";
            } else if (StringUtils.isBlank(managePrivCode)) {
                orgPriv = "no";
            } else {
                orgPriv = managePrivCodeValue;
            }
        }
        return orgPriv;
    }

    /**
     * 获取权限 (业务范围>操作单位>管理范围)
     * @author qinxx
     * @param userView 用户信息
     * @return String
     * @date 2019/1/07 14:25
     */
    public static String getPriv(UserView userView) {
        String orgPriv = userView.getUnitIdByBusi("1");
        if (userView.isSuper_admin() || IStandardPackageService.ALL_PRIV.equalsIgnoreCase(orgPriv)) {
            orgPriv = "all";
        } else if (StringUtils.isBlank(orgPriv) || StringUtils.equalsIgnoreCase(orgPriv, "UN")) {
            orgPriv = "no";
        } else {
            orgPriv = orgPriv.substring(2, orgPriv.indexOf('`'));
        }
        return orgPriv;
    }
}

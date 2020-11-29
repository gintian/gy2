package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class OutDataExcelTrans extends IBusiness
{
    HashMap map = new HashMap();
    private ArrayList fieldList = null;

    public void execute() throws GeneralException
    {
        String fAnalyseTempTab = (String) this.getFormHM().get("fAnalyseTempTab");
        ArrayList fields = (ArrayList) this.getFormHM().get("fields");
        if (fAnalyseTempTab == null)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "找不到数据分析结果表", "", ""));
        
        String sql = (String) this.getFormHM().get("data");
        sql = SafeCode.decode(PubFunc.keyWord_reback(sql));
        
        this.fieldList = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList usefieldlist = useFieldList(fields, this.fieldList);
        String excel_filename = createExcelFile(sql, usefieldlist);
		//xiexd 2014.09.12 加密文件名
        excel_filename = PubFunc.encrypt(excel_filename);
        this.getFormHM().put("excelfile", excel_filename);
    }

    private boolean itemVisible(String itemid)
    {
        boolean visible = true;

        FieldItem item = null;
        for (int i = 0; i < this.fieldList.size(); i++)
        {
            item = (FieldItem) this.fieldList.get(i);
            if (itemid.equalsIgnoreCase(item.getItemid()))
            {
                visible = item.isVisible();
                break;
            }
        }

        return visible;
    }

    private String createExcelFile(String sql, ArrayList fieldList)
    {
        String excel_filename="kq_" + userView.getUserName() + ".xls";
        
        int sheetIndex = 0;
        int maxRowInSheet = 50000;
        
        HSSFWorkbook workbook = new HSSFWorkbook();
        
        HSSFRow row = null;
        HSSFCell csCell = null;
        HSSFSheet sheet = null;
        
        RowSet rs = null;
        ResultSet rss = null;
        try
        {
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
            uplevel = uplevel != null && uplevel.trim().length() > 0 ? uplevel : "0";
            int nlevel = Integer.parseInt(uplevel);
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs = dao.search(sql);
            String e0122 = "";
            String e01a1 = "";
            String b0110 = "";
            int curCellNO = 0;
            
            int n = 0;
            while (rs.next())
            {
                if (n == 0) {
                    sheet = workbook.createSheet(sheetIndex + 1 + "");
                    n = executeTableTitel("员工考勤统计表", fieldList, sheet, workbook); // 写上表头
                    n++;
                }
                    
                row = sheet.getRow(n);
                if (row == null)
                    row = sheet.createRow(n);

                curCellNO = 0;
                if (itemVisible("e0122"))
                {
                    csCell = row.createCell(curCellNO);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    e0122 = "";
                    if (rs.getString("e0122") != null && rs.getString("e0122").length() > 0)
                    {
                        CodeItem codeItem = null;
                        if (nlevel > 0)
                            codeItem = AdminCode.getCode("UM", rs.getString("e0122"), nlevel);
                        else
                            codeItem = AdminCode.getCode("UM", rs.getString("e0122"));
                        
                        if (null != codeItem)
                            e0122 = codeItem.getCodename();
                    }
                        
                    csCell.setCellValue(e0122);

                    // 修改：天津水泥要求 增加单位，职位，结果
                    sheet.setColumnWidth(0, (short) ((30 * 8) / ((double) 1 / 20)));
                    
                    curCellNO++;
                }

                if (itemVisible("e01a1"))
                {
                    csCell = row.createCell(curCellNO);
                    // csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    if (rs.getString("e01a1") != null && rs.getString("e01a1").length() > 0)
                    {
                        CodeItem item = AdminCode.getCode("@K", rs.getString("e01a1"));
                        if (item != null)
                            e01a1 = item.getCodename();
                    }
                    else
                        e01a1 = "";
                    csCell.setCellValue(e01a1);
                    sheet.setColumnWidth(0, (short) ((30 * 8) / ((double) 1 / 20)));
                    
                    curCellNO++;
                }

                if (itemVisible("b0110"))
                {
                    csCell = row.createCell(curCellNO);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    
                    b0110 = "";
                    CodeItem item = AdminCode.getCode("UN", rs.getString("b0110"));
                    if (item != null)
                        b0110 = item.getCodename();
                    
                    csCell.setCellValue(b0110);
                    sheet.setColumnWidth(0, (short) ((30 * 8) / ((double) 1 / 20)));
                    
                    curCellNO++;
                }

                csCell = row.createCell(curCellNO);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(rs.getString("isok"));
                sheet.setColumnWidth(0, (short) ((30 * 8) / ((double) 1 / 20)));
                curCellNO++;

                if (itemVisible("a0101"))
                {
                    csCell = row.createCell(curCellNO);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    csCell.setCellValue(rs.getString("a0101"));

                    sheet.setColumnWidth(1, (short) ((30 * 8) / ((double) 1 / 20)));
                    
                    curCellNO++;
                }

                csCell = row.createCell(curCellNO);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(rs.getString("g_no"));
                curCellNO++;

                sheet.setColumnWidth(1, (short) ((30 * 8) / ((double) 1 / 20)));
                csCell = row.createCell(curCellNO);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(rs.getString("card_no"));
                curCellNO++;

                sheet.setColumnWidth(2, (short) ((30 * 8) / ((double) 1 / 20)));
                csCell = row.createCell(curCellNO);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(rs.getString("q03z0"));
                sheet.setColumnWidth(3, (short) ((30 * 8) / ((double) 1 / 20)));
                curCellNO++;
                
                csCell = row.createCell(curCellNO);
                csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                csCell.setCellValue(rs.getString("card_time"));
                sheet.setColumnWidth(4, (short) ((30 * 8) / ((double) 1 / 20)));
                curCellNO++;
                
                for (int r = 0; r < fieldList.size(); r++)
                {
                    FieldItem fielditem = (FieldItem) fieldList.get(r);
                    csCell = row.createCell(r + curCellNO);
                    csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    if ("N".equalsIgnoreCase(fielditem.getItemtype()))
                    {
                        if (rs.getString(fielditem.getItemid()) != null && rs.getString(fielditem.getItemid()).length() > 0)
                        {
                            String v = PubFunc.round(rs.getString(fielditem.getItemid()), fielditem.getDecimalwidth());
                            csCell.setCellValue(v);
                        }
                        else
                            csCell.setCellValue("");
                    }
                    else
                    {
                        // if (fielditem.getItemid().equalsIgnoreCase("c010k"))
                        // {//修改班次显示为代码的bug 2010-4-1 wangzhongjun
                        // csCell.setCellValue(rs.getString("c010k"));
                        // } else
                        if ("A".equals(fielditem.getItemtype()) && (!"0".equalsIgnoreCase(fielditem.getCodesetid())))
                        {
                            String key = fielditem.getCodesetid() + rs.getString(fielditem.getItemid());
                            if (map.containsKey(key))
                            {
                                csCell.setCellValue(map.get(key).toString());
                            }
                            else
                            {
                            	if ("UN".equals(fielditem.getCodesetid()) || "UM".equals(fielditem.getCodesetid()) || "@K".equals(fielditem.getCodesetid())) 
								{
									String keys = AdminCode.getCodeName(fielditem.getCodesetid(), rs.getString(fielditem.getItemid()));
									csCell.setCellValue(keys);
								}else 
								{
									String sql2 = "select codesetid,codeitemid, codeitemdesc from codeitem  " + "where codesetid='" + fielditem.getCodesetid() + "'";
									ContentDAO dao2 = new ContentDAO(this.frameconn);
									rss = dao2.search(sql2);
									while (rss.next())
									{
										String keys = rss.getString("codesetid") + rss.getString("codeitemid");
										String values = rss.getString("codeitemdesc");
										map.put(keys, values);
									}
									if (map.containsKey(key))
									{
										csCell.setCellValue(map.get(key).toString());
									}
								}
                            }

                        }
                        else
                        {
                            if ("ctime".equalsIgnoreCase(fielditem.getItemid()))
                            {
                                csCell.setCellValue(rs.getString("card_time"));
                            }
                            else
                            {
                                csCell.setCellValue(rs.getString(fielditem.getItemid()));
                            }
                        }
                    }
                    sheet.setColumnWidth((r + 8), (short) ((30 * 8) / ((double) 1 / 20)));
                }
                n++;
                
                if (n-2 >= maxRowInSheet) {
                    addSignatureRow(sheet, n);
                    sheetIndex++;
                    n = 0;
                }
            }
            
            if (n > 0) 
                addSignatureRow(sheet, n);
            
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + excel_filename);
            workbook.write(fileOut);
            fileOut.close();
            sheet = null;
            workbook = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
            KqUtilsClass.closeDBResource(rss);
        }
        return excel_filename;
    }

    /**
     * 添加签名行
     * @Title: addSignatureRow   
     * @Description:
     */
    private void addSignatureRow(HSSFSheet sheet, int curRow) {
        HSSFRow row = sheet.createRow(curRow + 1);
        HSSFCell csCell = row.createCell(0);
        csCell.setCellValue(" 部门主管签字");
        csCell = row.createCell(6);
        csCell.setCellValue(" 人力资源部主任签字：");
    }
    
    private short executeTableTitel(String title, ArrayList fieldList, HSSFSheet sheet, HSSFWorkbook workbook)
    {
        short n = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        // 写标题
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFFont.COLOR_NORMAL);
        font.setBold(true);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        row = sheet.createRow(n);
        csCell = row.createCell(4);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue(title);
        n++;
        row = sheet.createRow(n);
        
        int curCellNO = 0;
        if (itemVisible("e0122"))
        {            
            csCell = row.createCell(curCellNO);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue(DataDictionary.getFieldItem("e0122", "Q03").getItemdesc());
            curCellNO++;
        }

        if (itemVisible("e01A1"))
        {
            csCell = row.createCell(curCellNO);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue(DataDictionary.getFieldItem("e01a1","Q03").getItemdesc());
            curCellNO++;
        }

        if (itemVisible("b0110"))
        {
            csCell = row.createCell(curCellNO);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue(DataDictionary.getFieldItem("b0110","Q03").getItemdesc());
            curCellNO++;
        }

        csCell = row.createCell(curCellNO);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("结果");
        curCellNO++;

        if (itemVisible("a0101"))
        {
            csCell = row.createCell(curCellNO);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue("姓名");
            curCellNO++;
        }

        csCell = row.createCell(curCellNO);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("工号");
        curCellNO++;

        csCell = row.createCell(curCellNO);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("考勤卡号");
        curCellNO++;

        csCell = row.createCell(curCellNO);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("日期");
        curCellNO++;

        csCell = row.createCell(curCellNO);
        csCell.setCellStyle(cellStyle);
        csCell.setCellValue("刷卡时间");
        curCellNO++;

        for (int r = 0; r < fieldList.size(); r++)
        {
            FieldItem fielditem = (FieldItem) fieldList.get(r);
            csCell = row.createCell(r + curCellNO);
            csCell.setCellStyle(cellStyle);
            csCell.setCellValue(fielditem.getItemdesc());
        }
        return n;
    }

    private ArrayList useFieldList(ArrayList fields, ArrayList fieldList)
    {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fields.size(); i++)
        {
            String itemid = (String) fields.get(i);
            if (itemid == null || itemid.length() <= 0)
                continue;
            if ("name".equalsIgnoreCase(itemid))
            {
                FieldItem fielditem_c = new FieldItem();
                fielditem_c.setItemdesc("班次");
                fielditem_c.setItemid("NAME");
                fielditem_c.setItemtype("A");
                fielditem_c.setCodesetid("0");
                fielditem_c.setVisible(true);
                list.add(fielditem_c);
                continue;
            }
            for (int r = 0; r < fieldList.size(); r++)
            {
                FieldItem fielditem = (FieldItem) fieldList.get(r);
                if (fielditem.getItemid().equalsIgnoreCase(itemid))
                {
                    list.add(fielditem.clone());
                    continue;
                }
            }
        }
        return list;
    }
}

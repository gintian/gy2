package com.hjsj.hrms.transaction.kq.app_check_in.exportExcel;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ExportExcel extends IBusiness {
    private static final long serialVersionUID = 1L;
    
    private String table = "";
    private ArrayList fileList = null;
    private String sql = "";
    ///下载类别 1：下载申请模板 否则、下载申请记录
    private String flag = ""; 
    private String[] fileLists = null;
    private String fileListStr = "";
    private String g_no = "";

    public void execute() throws GeneralException {
        try {
            getFormFieldValue();
            g_no = getGNO();

            this.getFormHM().put("mess", "");

            if ("1".equals(flag)) {
                downTemplate();
            } else {
                downRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void downRecord() {
        String tableName = getTableDesc(table);
        ArrayList selectFileList = new ArrayList();
        
        ContentDAO dao = new ContentDAO(this.frameconn);
        RowSet rs = null;
        
        for (int i = 0; i < fileLists.length; i++) {
            if(g_no.equalsIgnoreCase(fileLists[i])){
                ArrayList usedfieldid = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
                Iterator its = usedfieldid.iterator();
                while (its.hasNext()) {
                    FieldItem field = (FieldItem) its.next();
                    if (field.getItemid().equalsIgnoreCase(g_no)) {
                        selectFileList.add(field);
                    }
                }
            }else{
                ArrayList usedfield = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);
                Iterator it = usedfield.iterator();
                while (it.hasNext()) {
                    FieldItem field = (FieldItem) it.next();
                    if (fileLists[i].equalsIgnoreCase(field.getItemid().toUpperCase())) {
                        selectFileList.add(field);
                    }
                }
            }
        }
        /**
         * 兼容公出 加班
         */
        String app_desc = "请假";
        if ("q13".equalsIgnoreCase(table)){
        	app_desc = "公出";
        }else if ("q11".equalsIgnoreCase(table)) {
        	app_desc = "加班";
        }
        // 销假标识只有 请假公出加班存在  其他导出（调班、调休）暂时不需要
        if(",q11,q13,q15,".contains(","+table.toLowerCase()+",")) {
        	FieldItem fieldItem1 = new FieldItem();
        	fieldItem1.setItemid(table+"19");
        	fieldItem1.setItemdesc(app_desc+"/销假");
        	fieldItem1.setItemtype("A");
        	fieldItem1.setCodesetid("0");
        	selectFileList.add(fieldItem1);
        }
        
        HSSFWorkbook work = new HSSFWorkbook();
        HSSFSheet sheet = work.createSheet(tableName);
        HSSFCellStyle style = work.createCellStyle();
        sql = PubFunc.keyWord_reback(sql);
        try {
            rs = dao.search(sql);
            HSSFFont font = work.createFont();
            HSSFRow row = sheet.createRow(1);
            row.setHeight((short)1024);
            font.setFontHeight((short)500);
            //font.setItalic(true);
            ExportExcelUtil.mergeCell(sheet, 1,1,1,selectFileList.size());
            HSSFCell cell = row.createCell(1);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font);
            //style.setAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(style);
            cell.setCellValue(tableName);
            
            
            row = sheet.createRow(2);
            style = this.getTableStyle(work);
            font = work.createFont();
            font.setBold(true);
            style.setFont(font);
            for (int n = 0; n < selectFileList.size(); n++) {
                FieldItem field = (FieldItem) selectFileList.get(n);
                cell = row.createCell(n + 1);
                cell.setCellStyle(style);
                cell.setCellValue(field.getItemdesc());
            }
            Map classMap = new KqUtilsClass(this.frameconn).getClassDescMap();
            style =getTableStyle(work);
            while (rs.next()) {
                row = sheet.createRow(rs.getRow() + 2);
                for (int i = 0; i < selectFileList.size(); i++) {
                    cell = row.createCell(i + 1);
                    cell.setCellType(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(style);
                    FieldItem field = (FieldItem) selectFileList.get(i);
                    String type = field.getItemtype();
                    Object obj = (Object) rs.getObject(field.getItemid());
                    if (obj == null) {
                        if (field.getItemid().equals(table+"19"))
                        {
                            cell.setCellValue(app_desc);
                        }
                    } else if ("A".equals(type)) {
                        if ("0".equals(field.getCodesetid())) {
                            if (field.getItemid().equals(table+"19"))
                            {
                                cell.setCellValue("销假");
                            }else {
                                cell.setCellValue((String) obj);
                            }
                        } else {
                            if ("e0122".equalsIgnoreCase(field.getItemid()))
                            {
                                Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.frameconn);
                                String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                                if (uplevel == null || uplevel.length() <= 0) {
                                    uplevel = "0";
                                }
                                CodeItem codeItem = AdminCode.getCode(field.getCodesetid(), PubFunc.nullToStr((String) obj),Integer.parseInt(uplevel));                                     
                                String value = codeItem == null ? "" : codeItem.getCodename();
                                cell.setCellValue(value);
                            }else 
                            {
                                cell.setCellValue(AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr((String) obj)));
                            }
                        }
                    } else if ("D".equals(type)) {
                        if (obj instanceof String) {
                            cell.setCellValue((String)obj);
                        }else{
                            cell.setCellValue(OperateDate.dateToStr((Date)obj,"yyyy-MM-dd HH:mm"));
                        }
                    } else if ("N".equals(type)) {
                        String itemid = field.getItemid().toUpperCase();
                        if((table + "04").equalsIgnoreCase(itemid) || "Q25Z7".equals(itemid)){
                            cell.setCellValue((String)classMap.get(obj.toString()));
                        }else{
                            cell.setCellValue(obj.toString());
                        }
                    } else {
                        cell.setCellValue(obj.toString());
                    }
                }
            }
            for(int i = 1; i < (selectFileList.size()+1); i++){
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000)  ;
            }
            createExcelFile(work);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    private void createExcelFile(HSSFWorkbook work) throws FileNotFoundException, IOException {
    	String xlsName = this.userView.getUserName() + "_" +getTableDesc(table) + ".xls";
        String pathFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + xlsName;
        File file = new File(pathFile);
        FileOutputStream out = new FileOutputStream(file);
        work.write(out);
        out.close();
        //加密文件名
        xlsName = PubFunc.encrypt(xlsName);
        this.getFormHM().put("name", xlsName);
    }

    private String getGNO() {
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String g_no = (String) hashmap.get("g_no");
        return g_no;
    }

    private void getFormFieldValue() {
        table = (String) this.getFormHM().get("table");
        //2014.09.11 xiexd从服务器取得导出模板的sql语句
        sql = (String) this.userView.getHm().get("kq_sql_1");
        flag = (String) this.getFormHM().get("flag");
        fileList = (ArrayList) this.getFormHM().get("fileList");
        
        fileListStr = fileList.toString().toUpperCase();
        fileListStr=fileListStr.replace("[","");
        fileListStr=fileListStr.replace("]","");
        fileListStr=fileListStr.replace(" ","");
        
        fileLists = fileListStr.split(",");
    }

    private String getTableDesc(String table) {
        String tableName = null;

        if ("Q11".equalsIgnoreCase(table)) {
            tableName = "加班申请登记表";
        } else if ("Q15".equalsIgnoreCase(table)) {
            tableName = "请假申请登记表";
        } else if ("Q13".equalsIgnoreCase(table)) {
            tableName = "公出申请登记表";
        } else if ("Q19".equalsIgnoreCase(table)) {
            tableName = "调班申请登记表";
        } else if ("Q25".equalsIgnoreCase(table)) {
            tableName = "调休申请登记表";
        }
        return tableName;
    }

    private HSSFCellStyle getTableStyle(HSSFWorkbook work){
        HSSFCellStyle style = work.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(false);
        return style;
    }
    
    private void downTemplate() throws Exception {
        if (!(fileListStr.indexOf(g_no)>=0&&fileListStr.indexOf(table+"Z1")>=0
                &&fileListStr.indexOf(table+"Z3")>=0&&fileListStr.indexOf(table+"03")>=0)) {
            this.getFormHM().put("mess", "请选择工号、起始时间、结束时间、申请类型等必选指标！");
            
            return;
        }
        
        ContentDAO dao = new ContentDAO(this.frameconn);
        RowSet rs = null;
    
        ArrayList selectFileList = new ArrayList();
        
        for (int i = 0; i < fileLists.length; i++) {
            if(g_no.equalsIgnoreCase(fileLists[i])){
                ArrayList usedfieldid = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
                Iterator its = usedfieldid.iterator();
                while (its.hasNext()) {
                    FieldItem field = (FieldItem) its.next();
                    if (field.getItemid().equalsIgnoreCase(g_no)) {
                        selectFileList.add(field);
                    }
                }
            }else{
                ArrayList usedfield = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);
                Iterator it = usedfield.iterator();
                while (it.hasNext()) {
                    FieldItem field = (FieldItem) it.next();
                    if (fileLists[i].equalsIgnoreCase(field.getItemid().toUpperCase())) {
                        selectFileList.add(field);
                    }
                }
            }
        }

        HSSFWorkbook work = new HSSFWorkbook();
        HSSFSheet sheet = work.createSheet(getTableDesc(table));
        HSSFCellStyle style = work.createCellStyle();
        HSSFPatriarch patr = sheet.createDrawingPatriarch();
        HSSFComment comm = null;
        
        HSSFFont font2 = work.createFont();
        font2.setFontHeightInPoints((short) 11);
        HSSFCellStyle style2 = work.createCellStyle();
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
        
        sql = PubFunc.keyWord_reback(sql);
        ArrayList indexArray = new ArrayList();
        try {
            rs = dao.search(sql);
            HSSFFont font = work.createFont();
            
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            row = sheet.createRow(0);
            style = this.getTableStyle(work);
            font = work.createFont();
            font.setBold(true);
            style.setFont(font);
            int colNum = 0;
            short indexIsOverTimeForLeave = -1;
            for (int n = 0; n < selectFileList.size(); n++) {
                FieldItem field = (FieldItem) selectFileList.get(n);
                
                //【网易需求】申请开始结束日期各拆分成日期和时间两个单元格
                if("D".equalsIgnoreCase(field.getItemtype())){
                    if((table+"Z3").equalsIgnoreCase(field.getItemid())){
                        cell = row.createCell(colNum);
                        cell.setCellStyle(style2);
                        cell.setCellValue(new HSSFRichTextString("终止日期\n(格式:2016-01-01)"));
                        comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short)colNum, 0, (short)(colNum + 1), 1));
                        comm.setString(new HSSFRichTextString(field.getItemid().toLowerCase()+"_date"));
                        cell.setCellComment(comm);
                        sheet.setColumnWidth(colNum, 18*256);
                        indexArray.add(String.valueOf(colNum));
                        colNum++;
                        
                        cell = row.createCell(colNum);
                        cell.setCellStyle(style2);
                        cell.setCellValue(new HSSFRichTextString("终止时间\n(格式:09:00)"));
                        comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (colNum), 0, (short) (colNum + 1), 1));
                        comm.setString(new HSSFRichTextString(field.getItemid().toLowerCase()+"_time"));
                        cell.setCellComment(comm);
                        sheet.setColumnWidth(colNum, 13*256);
                        indexArray.add(String.valueOf(colNum));
                        colNum++;
                        continue;
                    }
                    if((table+"Z1").equalsIgnoreCase(field.getItemid())){
                        cell = row.createCell(colNum);
                        cell.setCellStyle(style2);
                        cell.setCellValue(new HSSFRichTextString("起始日期\n(格式:2016-01-01)"));
                        comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,  (short) (colNum), 0, (short) (colNum + 1), 1));
                        comm.setString(new HSSFRichTextString(field.getItemid().toLowerCase()+"_date"));
                        cell.setCellComment(comm);
                        sheet.setColumnWidth(colNum, 18*256);
                        indexArray.add(String.valueOf(colNum));
                        colNum++;
                        cell = row.createCell(colNum);
                        cell.setCellStyle(style2);
                        cell.setCellValue(new HSSFRichTextString("起始时间\n(格式:09:00)"));
                        sheet.setColumnWidth(colNum, 13*256);
                        comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,  (short) (colNum), 0, (short) (colNum + 1), 1));
                        comm.setString(new HSSFRichTextString(field.getItemid().toLowerCase()+"_time"));
                        cell.setCellComment(comm);
                        indexArray.add(String.valueOf(colNum));
                        colNum++;
                        continue;
                    }
                }
                
                //加班“是否调休”特殊处理,提供下拉列表选项
                if ("是否调休".equals(field.getItemdesc()) && "45".equals(field.getCodesetid())) {
                    indexIsOverTimeForLeave = (short) (colNum);
                }
                
                cell = row.createCell(colNum);
                cell.setCellStyle(style2);
                cell.setCellValue(field.getItemdesc());
                //加标注
                comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,
                        (short) (colNum), 0, (short) (colNum + 1), 1));
                comm.setString(new HSSFRichTextString(field.getItemid().toLowerCase()));
                cell.setCellComment(comm);
                sheet.setColumnWidth(colNum, 10*256);
                indexArray.add(String.valueOf(colNum));
                colNum++;
            }
            short index = -1;
            short indexClassId = -1;
            int gnoIndex = -1;
            
            int codeFieldIndex = 0;
            int offset = 0;
            for (int j = 0; j < fileLists.length; j++) {
                String field = fileLists[j];
                if((table+"Z3").equalsIgnoreCase(field) || (table+"Z1").equalsIgnoreCase(field)) {
                    offset++;
                    continue;
                }
                
                if((table + "03").equalsIgnoreCase(field)) {
                    index = (short) (j+offset);
                    continue;
                }
                
                if(("Q1104").equalsIgnoreCase(field)) {
                    indexClassId = (short) (j+offset);
                    continue;
                }
                
                if(g_no.equalsIgnoreCase(field)){
                    gnoIndex = (short) (j+offset);
                    continue;
                }
                
                FieldItem item = DataDictionary.getFieldItem(field, table);
                if (item == null)
                    continue;
                
                String codeSetId = item.getCodesetid();
                if (!"0".equals(codeSetId) && !"@@".endsWith(codeSetId)) {
                    executeSelect((short)(j+offset), table, sheet, work, codeSetId, codeFieldIndex);
                    codeFieldIndex++;
                }
                    
            }
            
            int relase = executeSelect((short)index,table, sheet,work);
            
            if (indexClassId != -1) {
                executeSelectP((short)indexClassId,table, sheet,work, relase);
            }
            
            HSSFCellStyle cellStyle2 = work.createCellStyle();
            HSSFDataFormat format = work.createDataFormat();
            cellStyle2.setDataFormat(format.getFormat("@"));
            //设置工号列为文本格式
            for(int q =1;q<=999;q++){
                HSSFRow rows = sheet.createRow((short)q);
                HSSFCell onecell = rows.createCell(gnoIndex);
                onecell.setCellStyle(cellStyle2);
                for (int i = 0; i < indexArray.size(); i++) 
                {
                    int num = Integer.parseInt((String)indexArray.get(i));
                    HSSFCell othercell = rows.createCell(num);
                    othercell.setCellStyle(cellStyle2);
                }
            }
            
            createExcelFile(work);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }
    
    private void executeSelect(short cells, String table, HSSFSheet sheet,
            HSSFWorkbook workbook, String codeSetId, int codeFieldIndex) {
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        
        HSSFRow row = null;
        HSSFCell csCell = null;
        row = sheet.getRow(m);
        if(row == null)
          row = sheet.createRow(m);
        csCell = row.createCell(212 + codeFieldIndex);
        csCell.setCellValue("");
        m++;
        
        ArrayList codeItems = AdminCode.getCodeItemList(codeSetId);
        // jazz 61000
        // 1、代码太多，实际选择也很困难；
        // 2、如果超过整数限制（32767），导致下边循环错误。
        // 3、暂与员工管理导出模板保持一致，超出500个就不做下拉框了。
        if (codeItems.size() > 500) {
            return;
        }

        for (int i = 0; i < codeItems.size(); i++) {
            CodeItem codeItem = (CodeItem)codeItems.get(i);
            row = sheet.getRow(m);
            if(row == null)
              row = sheet.createRow(m);
            //212是HE列
            csCell = row.createCell(212 + codeFieldIndex);
            csCell.setCellValue(codeItem.getCodename());
            m++;
        }
        
        String columnName = "$H" + (char)(69 + codeFieldIndex) + "$";
        String strFormula = columnName + s + ":" + columnName + (1050) + ""; // 表示HE列1001-1050行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }
    
    private int executeSelect(short cells,String table,  HSSFSheet sheet,
            HSSFWorkbook workbook) {
        int relase = 0;
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql = new StringBuffer();
        sql.append("select item_name,item_id from kq_item");
        sql.append(" where sdata_src = '" + table.toUpperCase() +  "'");
        sql.append(" ORDER BY item_id");
        
        HSSFRow row = null;
        HSSFCell csCell = null;
        
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        
        try {
            row = sheet.getRow(m);
            if(row == null)
              row = sheet.createRow(m);
            csCell = row.createCell(208);
            csCell.setCellValue("");// 申请类型
            m++;
            relase ++;
            
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                //if (userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, classValue) || "0".equals(classValue)){
                    row = sheet.getRow(m);
                    if(row == null)
                        row = sheet.createRow(m);
                    csCell = row.createCell(208);
                    csCell.setCellValue(this.frowset.getString("item_name"));// 申请类型
                    m++;
                    relase ++;
                //}
            }
            sheet.setColumnHidden(208, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$HA$" + s + ":$HA$" + (1050) + ""; // 表示AA列1-2行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
        
        return relase;
    }
    
    
    private void executeSelectP(short cells,String table,  HSSFSheet sheet,
            HSSFWorkbook workbook, int relase) {
        int x = 0;
        HSSFRow row = null;
        HSSFCell csCell = null;
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        try {
            x ++;
            row = sheet.getRow(m);
            if(row == null)
              row = sheet.createRow(m);
            csCell = row.createCell(210);  //在HC列生成数据
            csCell.setCellValue("");// 考勤班次号
            m++;
            //获取权限内班次集合
            KqUtilsClass kqutil = new KqUtilsClass(this.frameconn, this.userView);
            ArrayList res = kqutil.getKqClassList("");
            //29403 linbz 之前是根据IResourceConstant.KQ_BASE_CLASS老的权限获取班次，现改为新的获取权限内班次的方法getKqClassList
            for(int i=0;i<res.size();i++){
            	LazyDynaBean ldb = (LazyDynaBean) res.get(i);
            	String name = (String)ldb.get("name");
	            //增加过滤 班次时间不完整直接过滤掉，不在模板显示
	            String onduty = (String) ldb.get("onduty_1");
	            String offduty = "";
	            for (int j = 3; j > 0; j--) {
	                offduty = (String) ldb.get("offduty_" + j);
	                if (offduty != null && offduty.length() == 5)
	                    break;
	            }
	            if (StringUtils.isEmpty(onduty) && StringUtils.isEmpty(offduty)) {
	                continue;
	            }
            	x ++;
                row = sheet.getRow(m);
                if(row == null)
                  row = sheet.createRow(m);
                csCell = row.createCell(210);  //在HC列生成数据
                csCell.setCellValue(name);// 考勤班次号
                m++;
            }
            sheet.setColumnHidden(210, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$HC$" + s + ":$HC$" + (1050) + ""; // 表示HC列1001-1050行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }
    
}

package com.hjsj.hrms.module.performance.score.transaction;

import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.util.*;

/**
 * 导出评分
 *
 * @author ZhangHua
 * @date 15:30 2018/5/16
 */
public class ExportScoreTemplateTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));

        String mainbody_Id = this.getUserView().getUserName();//考核主体ID
        ArrayList<String> ExportPreList = (ArrayList<String>) this.getFormHM().get("exportPreList");//模板id
        ArrayList<String> exportPreList = new ArrayList<String>();
        for (String str : ExportPreList) {
            exportPreList.add(PubFunc.decrypt(str));
        }
        ArrayList<String> exportPreNameList = (ArrayList<String>) this.getFormHM().get("exportPreNameList");//模板id


        String template_Id = (String) this.getFormHM().get("template_Id");//模板id
        template_Id = PubFunc.decrypt(template_Id);

        KhScoreBo bo = new KhScoreBo(this.frameconn, this.getUserView(), relation_Id, model, mainbody_Id);

        HashMap<String, HashMap<String, String>> mainbodyStatusMap = bo.getMainbodyStatus(exportPreList, template_Id);
        ArrayList<String> detailList = new ArrayList<String>();
        ArrayList<String> detailArchiveList = new ArrayList<String>();


        Iterator iter = mainbodyStatusMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            HashMap<String, String> val = (HashMap<String, String>) entry.getValue();
            if ("1".equals(val.get("Archive_flag"))) {
                detailArchiveList.add(val.get("kh_object_id"));
            } else {
                detailList.add(val.get("kh_object_id"));
            }
        }

        InitScoreViewTrans initScoreViewTrans = new InitScoreViewTrans();

        LinkedHashMap<String, LazyDynaBean> templateMap = bo.getTemplateMap(template_Id, null);
        ArrayList<HashMap<String, Object>> templateData = initScoreViewTrans.buildScoreTree(templateMap);

        HashMap<String, HashMap<String, HashMap<String, Object>>> dataMap = new HashMap<String, HashMap<String, HashMap<String, Object>>>();

        if (detailList.size() > 0) {
            dataMap.putAll(bo.getScoreFromObejctList(exportPreList, template_Id, mainbody_Id, "kh_detail"));
        }

        if (detailArchiveList.size() > 0) {
            dataMap.putAll(bo.getScoreFromObejctList(exportPreList, template_Id, mainbody_Id, "kh_detail_archive"));
        }

        LinkedHashMap<String, HashMap<String, HashMap<String, Object>>> linkDataMap = new LinkedHashMap<String, HashMap<String, HashMap<String, Object>>>();

        for (String str : exportPreList) {
            linkDataMap.put(str, dataMap.get(str));
        }
        String fileName = this.exportExcel(templateData, linkDataMap, exportPreNameList, mainbody_Id);
        this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));

    }

    private String exportExcel(ArrayList<HashMap<String, Object>> templateData,
                               LinkedHashMap<String, HashMap<String, HashMap<String, Object>>> dataMap, ArrayList<String> submitPreNameList, String mainbody_Id) throws GeneralException {
        String fileName = "";
        FileOutputStream fileOut = null;
        try {
            HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿

            Iterator iter = dataMap.entrySet().iterator();
            ArrayList<LazyDynaBean> colunm = new ArrayList<LazyDynaBean>();
            colunm.add(this.getColumnBean("任务名称", "text", 6000, "left", "A"));
            colunm.add(this.getColumnBean("指标解释", "description", 24000, "left", "A"));
            colunm.add(this.getColumnBean("标准分", "totalScore", 4000, "left", "N"));
            colunm.add(this.getColumnBean("评价", "score", 4000, "left", "N"));

            HSSFCellStyle headStyle = this.getStyle(10, "CENTER", "head", wb, true);
            HSSFCellStyle titleStyle = this.getStyle(10, "left", "head", wb, true);
            HSSFCellStyle cellStyle = this.getStyle(10, "left", "cell", wb, true);
            HSSFCellStyle cellStyleNoBorder = this.getStyle(10, "left", "cell", wb, false);
            HSSFCellStyle numStyle = this.getStyle(10, "right", "num", wb, true);


            int sheetNum = 0;
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String object_Key = (String) entry.getKey();
                HashMap<String, HashMap<String, Object>> point_map = (HashMap<String, HashMap<String, Object>>) entry.getValue();
                HSSFSheet sheet = wb.createSheet(submitPreNameList.get(sheetNum));
                int rowIndex = 0;
                HSSFRow row = sheet.createRow(0);
                rowIndex++;
                this.wirteHead(sheet, row, colunm, headStyle);


                for (int i = 0; i < templateData.size(); i++) {
                    row = sheet.createRow(rowIndex);
                    HashMap<String, Object> parentMap = templateData.get(i);
                    this.wirtecell(row, 0, parentMap.get("text"), "A", titleStyle);
                    for (int j = 1; j < colunm.size(); j++) {
                        this.wirtecell(row, j, "", "A", cellStyleNoBorder);
                    }

                    rowIndex++;

                    ArrayList<HashMap<String, Object>> childList = (ArrayList<HashMap<String, Object>>) parentMap.get("children");
                    for (HashMap<String, Object> childMap : childList) {
                        row = sheet.createRow(rowIndex);
                        for (int j = 0; j < colunm.size(); j++) {
                            LazyDynaBean columnBean = colunm.get(j);
                            String key = (String) columnBean.get("id");
                            String type = (String) columnBean.get("type");
                            //首列为子项目名称
                            if(j==0){
                                this.wirtecell(row, j, "     "+String.valueOf(childMap.get(key)), "A", cellStyle);
                                continue;
                            }
                            if ("score".equalsIgnoreCase(key)) {
                                String content = "";
                                if (point_map.containsKey(childMap.get("id"))) {
                                    content = (String) (point_map.get(childMap.get("id"))).get("score");
                                } else {
                                    content = "";
                                }
                                this.wirtecell(row, j, content, "N", numStyle);
                            } else {
                                if ("A".equalsIgnoreCase(type)) {
                                    this.wirtecell(row, j, childMap.get(key), "A", cellStyle);
                                } else {
                                    this.wirtecell(row, j, childMap.get(key), "N", numStyle);
                                }
                            }
                        }
                        rowIndex++;
                    }


                }
                sheetNum++;
            }
            fileName = "考核评分表_"  +String.valueOf(System.currentTimeMillis())+ ".xls";
            String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
            fileOut = new FileOutputStream(url);
            wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(fileOut);
        }
        return fileName;
    }

    private LazyDynaBean getColumnBean(String text, String id, int width, String align, String type) {
        LazyDynaBean bean = new LazyDynaBean();
        bean.set("text", text);
        bean.set("id", id);
        bean.set("width", width);
        bean.set("align", align);
        bean.set("type", type);
        return bean;
    }

    private void wirteHead(HSSFSheet sheet, HSSFRow row, ArrayList<LazyDynaBean> colunm, HSSFCellStyle headStyle) throws GeneralException {
        try {

            for (int i = 0; i < colunm.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) colunm.get(i);
                HSSFCell cell = row.getCell(i);
                sheet.setColumnWidth((short) i, Integer.parseInt(bean.get("width").toString()));
                if (cell == null) {
                    cell = row.createCell(i);
                }
                //设置该单元格样式
                cell.setCellStyle(headStyle);
                //给该单元格赋值
                cell.setCellValue(new HSSFRichTextString((String) bean.get("text")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void wirtecell(HSSFRow row, int colIndex, Object content, String contentType, HSSFCellStyle cellStyle) throws GeneralException {
        try {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            //设置该单元格样式
            cell.setCellStyle(cellStyle);
            //给该单元格赋值
            //if(contentType.equalsIgnoreCase("A"))
            cell.setCellValue(new HSSFRichTextString(content.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private HSSFCellStyle getStyle(int fontSize, String Align, String type, HSSFWorkbook wb, boolean Border) {
        HSSFCellStyle a_style = wb.createCellStyle();
        a_style.setWrapText(true);// 自动换行
        short border = (short) 1;
        short borderColor = IndexedColors.BLACK.index;
        HorizontalAlignment align;
        FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
        short fillForegroundColor = IndexedColors.WHITE.index;
        String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
        boolean fontBoldWeight = false;
        boolean isFontBold = false;// 是否加粗

        if ("right".equalsIgnoreCase(Align)) {
            align = HorizontalAlignment.RIGHT;
        } else if ("left".equalsIgnoreCase(Align)) {
            align = HorizontalAlignment.LEFT;
        } else {
            align = HorizontalAlignment.CENTER;
        }
        // 没有设置单元格样式  默认头部字体是加粗
        if ("head".equals(type)) {
            isFontBold = true;
        }
        //HSSFFont fonttitle = null;
        if ("head".equals(type)) {
            a_style.setFillPattern(fillPattern);
            a_style.setFillForegroundColor(fillForegroundColor);
            if (isFontBold) {
                fontBoldWeight = true;
            }
            // 设置字体

            HSSFFont font = wb.createFont();
            font.setFontHeightInPoints((short) fontSize);
            font.setBold(fontBoldWeight);// 加粗
            a_style.setFont(font);

        } else {
            if (fontSize == 0) {
                fontSize = 10;
            }
            if (isFontBold) {
                fontBoldWeight = true;
            }
            // 设置字体
            HSSFFont font = wb.createFont();
            font.setFontHeightInPoints((short) fontSize);
            font.setBold(fontBoldWeight);// 加粗
            a_style.setFont(font);
        }

        if (Border) {
            a_style.setBorderBottom(BorderStyle.valueOf(border));
            a_style.setBottomBorderColor(borderColor);
            a_style.setBorderLeft(BorderStyle.valueOf(border));
            a_style.setLeftBorderColor(borderColor);
            a_style.setBorderRight(BorderStyle.valueOf(border));
            a_style.setRightBorderColor(borderColor);
            a_style.setBorderTop(BorderStyle.valueOf(border));
            a_style.setTopBorderColor(borderColor);
        }
        a_style.setVerticalAlignment(VerticalAlignment.CENTER);
        a_style.setAlignment(align);
        a_style.setLocked(false);
        return a_style;
    }


}

package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.ExportReportBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ExportSettingsModel;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.jxcell.CellException;
import com.jxcell.CellFormat;
import com.jxcell.View;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * 表格控件按页面设置导出excel类
 *
 * @author ZhangHua
 * @date 10:02 2019/12/31
 */
public class ExportExcelTrans extends IBusiness {

    private boolean haveData = true; //判断数据是否来自于页面，页面数据代码类指标不需要解析，sql查询内容 代码类需要翻译
    private static final int EXCEL_ROWS = 5000;
    /**
     * 标题
     */
    private final int titleFormat = 0;
    /**
     * 页头
     */
    private final int topTextFormat = 1;
    /**
     * 页尾
     */
    private final int bottomTextFormat = 2;
    /**
     * 正文文本
     */
    private final int textFormat = 3;
    /**
     * 正文数字
     */
    private final int textNumFormat = 4;
    /**
     * 表头
     */
    private final int textHeadFormat = 5;

    private CellFormat textLeftFormat = null;
    private CellFormat textCenterFormat = null;
    private CellFormat textRightFormat = null;

    private CellFormat numLeftFormat = null;
    private CellFormat numCenterFormat = null;
    private CellFormat numRightFormat = null;

    @Override
    public void execute() throws GeneralException {
        String subModuleId = (String) this.getFormHM().get("subModuleId");
        boolean showRowNumber = (boolean) this.getFormHM().get("showRowNumber");
        TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(subModuleId);
        ArrayList outputcolumns = (ArrayList) this.getFormHM().get("outputcolumns");
//        int headLevel = (Integer) this.getFormHM().get("headLevel");
        HashMap columnMap = tableCache.getColumnMap();
        String gridTitle = (String) tableCache.get("title");
        gridTitle = gridTitle == null ? "grid" : gridTitle;
//        String tableName = gridTitle + "_" + getUserName();

        ExportReportBO exportReportBO = new ExportReportBO(this.getUserView(), this.getFrameconn());
        try {
            ExportSettingsModel exportSettingsModel = exportReportBO.getExportSettingsModel(subModuleId);
            deatilDataFile(gridTitle, outputcolumns, columnMap, tableCache, exportSettingsModel, showRowNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 按页面设置导出excel
     *
     * @param tableName           表名
     * @param outputcolumns       页面输出列
     * @param columnMap           列信息
     * @param tableCache          页面数据
     * @param exportSettingsModel 页面设置数据
     * @author ZhangHua
     * @date 17:38 2019/12/30
     */
    private void deatilDataFile(String tableName, ArrayList outputcolumns, HashMap columnMap, TableDataConfigCache tableCache, ExportSettingsModel exportSettingsModel, boolean showRowNumber) {
        ArrayList dataList = getDataList(tableCache);
        String tableSql = (String) tableCache.get("combineSql");
        String sortSql = tableCache.getSortSql();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
        String filename = "";
        int pageCount = 1;

        View view = new View();
        view.getLock();
        try {
            boolean isXlsxFlag = false;
            PaginationManager paginationm = null;
            int rowCount = 0;
            if (dataList == null) {//获取完整的查询sql ，包含快速过滤和方案查询的条件
                rs = dao.search("select count(*) maxRowCount from ( " + tableSql + " )  tableCache");
                if (rs.next()) {
                    rowCount = rs.getInt("maxRowCount");
                }
                if (rowCount > 120000 || outputcolumns.size() > 255 || rowCount * outputcolumns.size() > 10000000) {
                    isXlsxFlag = true;
                }
                String[] fields = new String[outputcolumns.size()];
                for (int i = 0; i < outputcolumns.size(); i++) {
                    fields[i] = ((DynaBean) outputcolumns.get(i)).get("columnid").toString();
                }
                haveData = false;
                paginationm = new PaginationManager(tableSql, "", "", sortSql, fields, "");
                paginationm.setBAllMemo(true);
                paginationm.setPagerows(EXCEL_ROWS);
                paginationm.setKeylist(splitKeys(tableCache.getIndexkey()));
                int pageIndex = 0;
                pageCount = (int) Math.ceil((float) rowCount / (float) EXCEL_ROWS);
                dataList = (ArrayList) paginationm.getPage(pageIndex + 1);
            } else {
                rowCount = dataList.size();
                pageCount = (int) Math.ceil((float) rowCount / (float) EXCEL_ROWS);
            }
            ArrayList<ColumnsInfo> columnsInfos = this.getColumnsInfos(outputcolumns, columnMap, showRowNumber);

            //标题样式
            CellFormat titleCellFormat = this.getCellFormat(view, titleFormat, exportSettingsModel);
            //页头样式
            CellFormat topTextCellFormat = this.getCellFormat(view, topTextFormat, exportSettingsModel);
            //页尾样式
            CellFormat bottomCellTextFormat = this.getCellFormat(view, bottomTextFormat, exportSettingsModel);
            //正文数字样式
            CellFormat textNumCellFormat = this.getCellFormat(view, textNumFormat, exportSettingsModel);
            //正文文本样式
            CellFormat textCellFormat = this.getCellFormat(view, textFormat, exportSettingsModel);
            //正文表头样式
            CellFormat textHeadCellFormat = this.getCellFormat(view, textHeadFormat, exportSettingsModel);

            this.initAlignment(view, textCellFormat, textNumCellFormat);
            //zhangh 2020-1-13 【57392】问卷调查：结果分析，原始数据，导出，弹出的页面空白,没有任何数据时pageCount需要默认为1
            pageCount=pageCount==0?1:pageCount;
            view.setNumSheets(pageCount);
            view.setPrintVCenter(true);
            view.setPrintHCenter(true);

            //papersize 长度单位为Twips 1cm=567twips
            view.setPrintPaperSize((int) (exportSettingsModel.getWidth() * 56.7), (int) (exportSettingsModel.getHeight() * 56.7));

            //长度单位为英寸
            view.setPrintTopMargin(exportSettingsModel.getTop() / 25.4);
            view.setPrintBottomMargin(exportSettingsModel.getBottom() / 25.4);
            view.setPrintLeftMargin(exportSettingsModel.getLeft() / 25.4);
            view.setPrintRightMargin(exportSettingsModel.getRight() / 25.4);

            view.setPrintLandscape("0".equals(exportSettingsModel.getOrientation()) ? false : true);

            int rowNumber = 1;
            view.setDefaultRowHeight(550);

            int maxColumnsCount = columnsInfos.size() - 1;
            for (int i = 0; i < pageCount; i++) {
                view.setSheet(i);
                view.setSheetName(i, "第" + (i + 1) + "页");

                for (int j = 0; j < outputcolumns.size(); j++) {
                    DynaBean column = (DynaBean) outputcolumns.get(j);
                    Integer width = (Integer) column.get("width");
                    view.setColWidth(j, width * 40);
                }
                if (i > 0) {
                    dataList = (ArrayList) paginationm.getPage(i + 1);
                }
                //翻译正文值
                this.conversionListValue(columnsInfos, dataList);
                int realRowCount = 0;

                if (i == 0) {
                    //写标题
                    realRowCount += this.writeTitleCell(titleCellFormat, view, exportSettingsModel.getTitle_content(), maxColumnsCount,tableName);
                }

                //写页头
                realRowCount += this.writeHeaderOrBottom(topTextCellFormat,
                        exportSettingsModel, true, view, i == 0, realRowCount, maxColumnsCount, tableName, i + 1, rowCount);

                //表头
                realRowCount += this.setHeadTitle(textHeadCellFormat, columnsInfos, outputcolumns, view, realRowCount);

                //正文
                this.writeTextData(dataList, view, columnsInfos, realRowCount, rowNumber);
                rowNumber += dataList.size();

                realRowCount += dataList.size();

                //合计
                if ((i == pageCount - 1) && this.getFormHM().containsKey("summaryData")) {
                    this.createSumData(view, realRowCount, columnsInfos, (DynaBean) this.getFormHM().get("summaryData"));
                    realRowCount++;
                }

                //页尾
                realRowCount += this.writeHeaderOrBottom(bottomCellTextFormat, exportSettingsModel, false, view, i == pageCount - 1,
                        realRowCount, maxColumnsCount, tableName, i + 1, rowCount);
                view.setRowHeight(0, realRowCount, 550, false, true);
            }
            view.setSheet(0);
            tableName=   getUserName()+ "_" +tableName;
            //生成文件
            if (isXlsxFlag) {
                filename = tableName + ".xlsx";
                view.writeXLSX(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + filename);
            } else {
                filename = tableName + ".xls";
                view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + filename);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            view.releaseLock();
            PubFunc.closeDbObj(rs);
        }

        filename = PubFunc.encrypt(filename);
        this.getFormHM().put("filename", filename);

    }

    /**
     * 写页头页尾
     *
     * @param cellFormat          头尾样式
     * @param exportSettingsModel 页面设置数据
     * @param isHeader            是否为页头
     * @param view
     * @param isFirstOrEndPage    是否为首页或者尾页
     * @param rowCount            当前行
     * @param maxColCount         最大列
     * @return
     * @throws CellException
     * @author ZhangHua
     * @date 17:40 2019/12/30
     */
    private int writeHeaderOrBottom(CellFormat cellFormat, ExportSettingsModel exportSettingsModel, boolean isHeader, View view, boolean isFirstOrEndPage, int rowCount, int maxColCount,
                                    String tableTitle, int pageCount, int dataLenght) throws CellException {
        String left = isHeader ? exportSettingsModel.getHead_left() : exportSettingsModel.getTail_left(),
                center = isHeader ? exportSettingsModel.getHead_center() : exportSettingsModel.getTail_center(),
                right = isHeader ? exportSettingsModel.getHead_right() : exportSettingsModel.getTail_right();

        boolean leftEveryPage = isHeader ? StringUtils.isBlank(exportSettingsModel.getHead_flw_hs()) : StringUtils.isBlank(exportSettingsModel.getTail_flw_hs()),
                centerEveryPage = isHeader ? StringUtils.isBlank(exportSettingsModel.getHead_fmw_hs()) : StringUtils.isBlank(exportSettingsModel.getTail_fmw_hs()),
                rightEveryPage = isHeader ? StringUtils.isBlank(exportSettingsModel.getHead_frw_hs()) : StringUtils.isBlank(exportSettingsModel.getTail_frw_hs());

        if (StringUtils.isBlank(left) && StringUtils.isBlank(center) && StringUtils.isBlank(right)) {
            return 0;
        }
        ReportParseVo parseVo = new ReportParseVo();

        ContentDAO dao = new ContentDAO(this.getFrameconn());

        left = parseVo.getRealcontent(left, userView, dataLenght, tableTitle, pageCount, dao);
        center = parseVo.getRealcontent(center, userView, dataLenght, tableTitle, pageCount, dao);
        right = parseVo.getRealcontent(right, userView, dataLenght, tableTitle, pageCount, dao);


        int returnRowCount = 0, textCount = 0, leftCol = -1, centerCol = -1, rightCol = maxColCount;

        if (StringUtils.isNotBlank(left) && (isFirstOrEndPage || leftEveryPage)) {
            textCount++;
            leftCol = 0;
        }
        if (StringUtils.isNotBlank(center) && (isFirstOrEndPage || rightEveryPage)) {
            textCount++;
            centerCol = maxColCount / 2;
        }
        if (StringUtils.isNotBlank(right) && (isFirstOrEndPage || centerEveryPage)) {
            textCount++;
        }
        if (maxColCount < textCount) {
            return 0;
        }


        if (StringUtils.isNotBlank(left) && (isFirstOrEndPage || leftEveryPage)) {
            view.setText(rowCount, 0, left);
            if (centerCol != 1 && rightCol != 1) {
                view.setCellFormat(cellFormat, rowCount, 0, rowCount, 1);
            } else {
                view.setCellFormat(cellFormat, rowCount, 0, rowCount, 0);
            }
            returnRowCount = 1;
        }
        if (StringUtils.isNotBlank(center) && (isFirstOrEndPage || centerEveryPage)) {
            view.setText(rowCount, centerCol, center);

            if (centerCol + 1 != rightCol) {
                view.setCellFormat(cellFormat, rowCount, centerCol, rowCount, centerCol + 1);
            } else {
                view.setCellFormat(cellFormat, rowCount, centerCol, rowCount, centerCol);
            }
            centerCol++;

            returnRowCount = 1;
        }
        if (StringUtils.isNotBlank(right) && (isFirstOrEndPage || rightEveryPage)) {

            if (centerCol != rightCol - 1 && leftCol != rightCol - 1) {
                view.setText(rowCount, rightCol - 1, right);
                view.setCellFormat(cellFormat, rowCount, rightCol - 1, rowCount, rightCol);
            } else {
                view.setText(rowCount, rightCol, right);
                view.setCellFormat(cellFormat, rowCount, rightCol, rowCount, rightCol);
            }
            returnRowCount = 1;
        }

        return returnRowCount;
    }

    /**
     * 写标题
     *
     * @param cfTitle     标题样式
     * @param view
     * @param titleValue  标题内容
     * @param maxColCount 最大列
     * @return
     * @throws CellException
     * @author ZhangHua
     * @date 17:40 2019/12/30
     */
    private int writeTitleCell(CellFormat cfTitle, View view, String titleValue, int maxColCount,String tableName) throws CellException {
        if (StringUtils.isBlank(titleValue)) {
            return 0;
        }
        int titleRow = titleValue.replace("\r\n", "rn").replace("\n", "rn").length() - titleValue.length();

        ReportParseVo parseVo = new ReportParseVo();

        ContentDAO dao = new ContentDAO(this.getFrameconn());

        titleValue = parseVo.getRealcontent(titleValue, userView, 0, tableName, 0, dao);
        view.setText(0, 0, titleValue);

        view.setCellFormat(cfTitle, 0, 0, titleRow, maxColCount);
        return titleRow + 1;
    }

    /**
     * 拼接列数据
     *
     * @param outputcolumns
     * @param columnMap
     * @return
     * @author ZhangHua
     * @date 17:40 2019/12/30
     */
    private ArrayList<ColumnsInfo> getColumnsInfos(ArrayList outputcolumns, HashMap columnMap, boolean showRowNum) {
        ArrayList columnsList = new ArrayList();
        if (showRowNum) {
            DynaBean dynaBean = new LazyDynaBean();
            dynaBean.set("columnid", "exportRowNum");
            ArrayList list = new ArrayList();
            dynaBean.set("ups", list);
            dynaBean.set("width", 50);
            outputcolumns.add(0, dynaBean);


            ColumnsInfo columnsInfo = new ColumnsInfo();
            columnsInfo.setColumnDesc("序号");
            columnsInfo.setColumnId("exportRowNum");
            columnsInfo.setColumnType("N");
            columnsInfo.setColumnWidth(50);
            columnMap.put("exportRowNum", columnsInfo);

        }


        for (int i = 0; i < outputcolumns.size(); i++) {
            DynaBean column = (DynaBean) outputcolumns.get(i);
            String columnid = column.get("columnid").toString();
            columnsList.add(columnMap.get(columnid));
        }
        return columnsList;
    }

    /**
     * 翻译代码数据
     *
     * @param columnsInfos
     * @param dataList
     * @return
     * @author ZhangHua
     * @date 17:41 2019/12/30
     */
    private ArrayList conversionListValue(ArrayList<ColumnsInfo> columnsInfos, ArrayList<DynaBean> dataList) {

        //处理自定义代码
        HashMap<String,HashMap<String,String>> codeMap=new HashMap();
        for (ColumnsInfo columnsInfo : columnsInfos) {
            //判断OperationData为ArrayList<CommonData> 类型
            if(columnsInfo.getOperationData()!=null&& columnsInfo.getOperationData() instanceof ArrayList ){
                ArrayList<CommonData> list=(ArrayList<CommonData>)columnsInfo.getOperationData();
                HashMap<String,String> codes=new HashMap();
                for (CommonData commonData : list) {
                    codes.put(commonData.getDataValue(),commonData.getDataName());
                }
                codeMap.put(columnsInfo.getColumnId(),codes);
            }
        }

        for (int j = 0; j < dataList.size(); j++) {
            DynaBean bean = dataList.get(j);
            if(bean == null) {
            	continue;
            }
            	
            
            for (int i = 0; i < columnsInfos.size(); i++) {
                ColumnsInfo columnsInfo = columnsInfos.get(i);
                if (StringUtils.isNotEmpty(columnsInfo.getCodesetId()) && !"0".equals(columnsInfo.getCodesetId())) {
                    String value = (String) bean.get(columnsInfo.getColumnId());
                    String codeName = "";
                    if (haveData) {
                        codeName = value.split("`").length > 1 ? value.split("`")[1] : AdminCode.getCodeName(columnsInfo.getCodesetId(), value);
                    } else {
                        //当codesetid时UM时，兼容UN
                        codeName = AdminCode.getCodeName(columnsInfo.getCodesetId(), value);
                        if ("UM".equalsIgnoreCase(columnsInfo.getCodesetId()) && codeName.length() < 1) {
                            codeName = AdminCode.getCodeName("UN", value);
                        }
                        if ("UN".equalsIgnoreCase(columnsInfo.getCodesetId()) && codeName.length() < 1) {
                            codeName = AdminCode.getCodeName("UM", value);
                        }
                    }
                    bean.set(columnsInfo.getColumnId(), codeName);
                }else if("D".equalsIgnoreCase(columnsInfo.getColumnType())){
                    String value = (String) bean.get(columnsInfo.getColumnId());
                    int columnlength=columnsInfo.getColumnLength();
                    if(columnlength>0&&StringUtils.isNotBlank(value)&&value.length()>columnlength){
                        value=value.substring(0,columnlength);
                    }
                    bean.set(columnsInfo.getColumnId(), value.replaceAll("\\.","-"));
                }
                //若为自定义代码类
                else if(codeMap.containsKey(columnsInfo.getColumnId())){
                    String value = (String) bean.get(columnsInfo.getColumnId());
                    HashMap<String,String> codes=codeMap.get(columnsInfo.getColumnId());
                    if(codes.containsKey(value)){
                        value=codes.get(value);
                    }
                    bean.set(columnsInfo.getColumnId(), value);
                }
            }
        }
        return dataList;

    }
    /**
     * 创建合计行
     *
     * @param view
     * @param rowsCount    当前行
     * @param columnsInfos
     * @param dynaBean
     * @throws CellException
     * @author ZhangHua
     * @date 17:42 2019/12/30
     */
    private void createSumData(View view, int rowsCount, ArrayList<ColumnsInfo> columnsInfos, DynaBean dynaBean) throws CellException {
        HashMap map = PubFunc.DynaBean2Map(dynaBean);
        for (int i = 0; i < columnsInfos.size(); i++) {
            ColumnsInfo columnsInfo = columnsInfos.get(i);
            if (map.containsKey(columnsInfo.getColumnId())) {
                view.setNumber(rowsCount, i, Double.parseDouble((String) dynaBean.get(columnsInfo.getColumnId())));
                view.setCellFormat(this.numRightFormat, rowsCount, i, rowsCount, i);
            } else if (i == 0) {
                view.setText(rowsCount, 0, ResourceFactory.getProperty("planar.stat.total"));
                view.setCellFormat(this.textLeftFormat, rowsCount, 0, rowsCount, 0);
            } else {
                view.setCellFormat(this.textLeftFormat, rowsCount, i, rowsCount, i);
            }
        }
    }


    //获取数据
    private ArrayList getDataList(TableDataConfigCache tableCache) {
        ArrayList list = null;
        if (this.getFormHM().containsKey("outputdata")) {
            list = (ArrayList) this.getFormHM().get("outputdata");
        } else {
            if (tableCache.getTableData() != null) {
                list = tableCache.getTableData();
            }
        }
        return list;
    }


    private String getUserName() {
        String username = userView.getUserName();
        username = PubFunc.getPinYin(username);
        return username;
    }

    private ArrayList splitKeys(String indexkey) {
        if (indexkey == null || "".equals(indexkey)) {
            return null;
        }
        ArrayList list = new ArrayList();
        String temp = indexkey.toLowerCase();
        StringTokenizer st = new StringTokenizer(temp, ",");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }


    /**
     * 创建样式
     *
     * @param cellFormat
     * @param view
     * @param fontsize   字体大小
     * @param fontblob   粗体
     * @param fontitalic 斜体
     * @param underline  下划线
     * @param delline    删除线
     * @param color      颜色
     * @param fontface   字体
     * @return
     * @throws CellException
     * @author ZhangHua
     * @date 17:43 2019/12/30
     */
    private CellFormat setFormat(CellFormat cellFormat, View view, int fontsize, String fontblob, String fontitalic, String underline, String delline, String color, String fontface) throws CellException {
        //标题的样式
        cellFormat.setFontSize(fontsize);
        cellFormat.setFontBold(StringUtils.isNotBlank(fontblob) ? true : false);
        cellFormat.setFontItalic(StringUtils.isNotBlank(fontitalic) ? true : false);
        cellFormat.setFontStrikeout(StringUtils.isNotBlank(delline) ? true : false);
        if (StringUtils.isNotBlank(underline)) {
            cellFormat.setFontUnderline((short) 1);
        }
        if ("FF0000".equalsIgnoreCase(color)) {
            cellFormat.setFontColor(view.getPaletteEntry(3));
        } else {
            cellFormat.setFontColor(new Color(Integer.parseInt(color.substring(1, 3), 16),
                    Integer.parseInt(color.substring(3, 5), 16),
                    Integer.parseInt(color.substring(5, 7), 16)));
        }
        cellFormat.setFontName(fontface);

        return cellFormat;
    }

    /**
     * 获取样式
     *
     * @param view
     * @param formatType          样式类型，使用本类常量
     * @param exportSettingsModel 页面设置数据
     * @return
     * @throws CellException
     * @author ZhangHua
     * @date 17:44 2019/12/30
     */
    private CellFormat getCellFormat(View view, int formatType, ExportSettingsModel exportSettingsModel) throws CellException {
        CellFormat cellFormat = view.getCellFormat();
        cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
        cellFormat.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
        cellFormat.setWordWrap(true);
        cellFormat.setMergeCells(true);
        switch (formatType) {

            case titleFormat: {
                this.setFormat(cellFormat, view, exportSettingsModel.getTitle_fontsize(), exportSettingsModel.getTitle_fontblob(), exportSettingsModel.getTitle_fontitalic(),
                        exportSettingsModel.getTitle_underline(), exportSettingsModel.getTitle_delline(), exportSettingsModel.getTitle_color(), exportSettingsModel.getTitle_fontface());

            }
            break;
            case topTextFormat: {
                //页头的样式
                this.setFormat(cellFormat, view, exportSettingsModel.getHead_fontsize(), exportSettingsModel.getHead_fontblob(), exportSettingsModel.getHead_fontitalic(),
                        exportSettingsModel.getHead_underline(), exportSettingsModel.getHead_delline(), exportSettingsModel.getHead_fc(), exportSettingsModel.getHead_fontface());
                cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
            }
            break;
            case bottomTextFormat: {
                //页尾的样式
                this.setFormat(cellFormat, view, exportSettingsModel.getTail_fontsize(), exportSettingsModel.getTail_fontblob(), exportSettingsModel.getTail_fontitalic(),
                        exportSettingsModel.getTail_underline(), exportSettingsModel.getTail_delline(), exportSettingsModel.getTail_fc(), exportSettingsModel.getTail_fontface());
                cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
            }
            break;
            case textFormat: {
                cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
            }
            break;
            case textNumFormat: {
                // 水平居右
                cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);
            }
            break;
            case textHeadFormat: {
                cellFormat.setMergeCells(true);
                cellFormat.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
                cellFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
                if ("FF0000".equalsIgnoreCase(exportSettingsModel.getPhead_fc())) {
                    cellFormat.setFontColor(view.getPaletteEntry(3));
                } else {
                    cellFormat.setFontColor(new Color(Integer.parseInt(exportSettingsModel.getPhead_fc().substring(1, 3), 16),
                            Integer.parseInt(exportSettingsModel.getPhead_fc().substring(3, 5), 16),
                            Integer.parseInt(exportSettingsModel.getPhead_fc().substring(5, 7), 16)));
                }
                //字体
                cellFormat.setFontName(exportSettingsModel.getPhead_fn());
                // 设置字体大小
                cellFormat.setFontSize(exportSettingsModel.getPhead_fz());
                //粗体
                cellFormat.setFontBold(StringUtils.isNotBlank(exportSettingsModel.getPhead_fb()) ? true : false);
                //斜线
                cellFormat.setFontItalic(StringUtils.isNotBlank(exportSettingsModel.getPhead_fi()) ? true : false);
                //下划线
                if (StringUtils.isNotBlank(exportSettingsModel.getPhead_fu())) {
                    cellFormat.setFontUnderline((short) 1);
                }

            }
            break;
            default: {
            }
        }
        if (formatType == textFormat || formatType == textNumFormat || formatType == textHeadFormat) {
            // 设置边框为细实线
            cellFormat.setBottomBorder(CellFormat.PatternSolid);
            cellFormat.setTopBorder(CellFormat.PatternSolid);
            cellFormat.setLeftBorder(CellFormat.PatternSolid);
            cellFormat.setRightBorder(CellFormat.PatternSolid);
        }

        if (formatType == textFormat || formatType == textNumFormat) {
            cellFormat.setMergeCells(false);
            //颜色
            if ("FF0000".equalsIgnoreCase(exportSettingsModel.getText_fc())) {
                cellFormat.setFontColor(view.getPaletteEntry(3));
            } else {
                cellFormat.setFontColor(new Color(Integer.parseInt(exportSettingsModel.getText_fc().substring(1, 3), 16),
                        Integer.parseInt(exportSettingsModel.getText_fc().substring(3, 5), 16),
                        Integer.parseInt(exportSettingsModel.getText_fc().substring(5, 7), 16)));
            }
            cellFormat.setFontName(exportSettingsModel.getText_fn());
            // 设置字体大小
            cellFormat.setFontSize(exportSettingsModel.getText_fz());
            //粗体
            cellFormat.setFontBold(StringUtils.isNotBlank(exportSettingsModel.getText_fb()) ? true : false);
            //斜线
            cellFormat.setFontItalic(StringUtils.isNotBlank(exportSettingsModel.getText_fi()) ? true : false);
            //下划线
            if (StringUtils.isNotBlank(exportSettingsModel.getText_fu())) {
                cellFormat.setFontUnderline((short) 1);
            }
        }

        view.setDefaultColWidth(15 * 256);// 固定列宽

        return cellFormat;
    }

    /**
     * 初始化各种对齐方式样式
     *
     * @param view
     * @param textCellFormat
     * @param numCellFormat
     * @throws CellException
     * @author ZhangHua
     * @date 17:45 2019/12/30
     */
    private void initAlignment(View view, CellFormat textCellFormat, CellFormat numCellFormat) throws CellException {

        this.textLeftFormat = textCellFormat;
        this.textCenterFormat = view.getCellFormat();
        this.textCenterFormat.copy(textCellFormat);
        this.textCenterFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);

        this.textRightFormat = view.getCellFormat();
        this.textRightFormat.copy(textCellFormat);
        this.textRightFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);

        this.numRightFormat = numCellFormat;
        this.numRightFormat.setCustomFormat("0.00");

        this.numCenterFormat = view.getCellFormat();

        this.numCenterFormat.copy(textCellFormat);
        this.numCenterFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
        this.numCenterFormat.setCustomFormat("0.00");

        this.numLeftFormat = view.getCellFormat();
        this.numLeftFormat.copy(textCellFormat);
        this.numLeftFormat.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
        this.numLeftFormat.setCustomFormat("0.00");

    }


    /**
     * 写表体内容
     *
     * @param dataList      数据，翻译后的
     * @param view
     * @param columnsInfos  列数据
     * @param startRowCount 开始行号
     * @return
     * @throws CellException
     * @author ZhangHua
     * @date 17:46 2019/12/30
     */
    private int writeTextData(ArrayList<DynaBean> dataList,
                              View view, ArrayList<ColumnsInfo> columnsInfos, int startRowCount, int rowNumber) throws CellException {

        int realRowPoint = startRowCount;
        for (int rowNum = 0; rowNum < dataList.size(); rowNum++) {
            DynaBean data = dataList.get(rowNum);
            if (data ==null) {
            	continue;
            }
            for (int colNum = 0; colNum < columnsInfos.size(); colNum++) {
                ColumnsInfo columnsInfo = columnsInfos.get(colNum);

                if ("A".equalsIgnoreCase(columnsInfo.getColumnType())||"M".equalsIgnoreCase(columnsInfo.getColumnType())) {
                    view.setText(realRowPoint, colNum, (String) data.get(columnsInfo.getColumnId()));
                    String align = columnsInfo.getTextAlign();
                    if ("right".equalsIgnoreCase(align)) {
                        view.setCellFormat(this.textRightFormat, realRowPoint, colNum, realRowPoint, colNum);
                    } else if ("center".equalsIgnoreCase(align)) {
                        view.setCellFormat(this.textCenterFormat, realRowPoint, colNum, realRowPoint, colNum);
                    } else {
                        view.setCellFormat(this.textLeftFormat, realRowPoint, colNum, realRowPoint, colNum);
                    }
                } else if ("N".equalsIgnoreCase(columnsInfo.getColumnType()) || "D".equalsIgnoreCase(columnsInfo.getColumnType())) {
                    if ("N".equalsIgnoreCase(columnsInfo.getColumnType())) {
                        if ("exportRowNum".equalsIgnoreCase(columnsInfo.getColumnId())) {
                            view.setNumber(realRowPoint, colNum, rowNumber);
                        }else {
                        	DynaProperty[] properties =  data.getDynaClass().getDynaProperties();
                        	Boolean flag = false;
                        	for(DynaProperty property : properties){
                                String propertyName = property.getName();
                                if(propertyName.equalsIgnoreCase(columnsInfo.getColumnId()))
                                	flag = true;
                            }
                        	  
                    	    if(flag) {
                          	    String dataInfo=  String.valueOf(data.get(columnsInfo.getColumnId()));
                          	    //zhangh 2020-1-13 【57392】问卷调查：结果分析，原始数据，导出，弹出的页面空白，判断了null，没有判断null字符串
                                if (StringUtils.isNotBlank(dataInfo)&&!"null".equalsIgnoreCase(dataInfo)) {
                                    view.setNumber(realRowPoint, colNum, Float.parseFloat(dataInfo));
                                } else {
                                    view.setText(realRowPoint, colNum, "");
                                }
                    	  }
                        	
                        }
                    } else {
                        view.setText(realRowPoint, colNum, (String) data.get(columnsInfo.getColumnId()));
                    }
                    String align = columnsInfo.getTextAlign();
                    int decimal=columnsInfo.getDecimalWidth();
                    String format="";
                    if(decimal!=2){
                        format=decimal>0?"0.":"0";
                        for (int i = 0; i < decimal; i++) {
                            format+="0";
                        }
                    }
                    CellFormat cellFormat;
                    if ("left".equalsIgnoreCase(align)) {
                        cellFormat=this.numLeftFormat;
                    } else if ("center".equalsIgnoreCase(align)) {
                        cellFormat=this.numCenterFormat;
                    } else {
                        cellFormat=this.numRightFormat;
                    }
                    CellFormat realCellFormat;
                    if(StringUtils.isNotBlank(format)){
                        realCellFormat=view.getCellFormat();
                        realCellFormat.copy(cellFormat);
                        realCellFormat.setCustomFormat(format);
                    }else{
                        realCellFormat=cellFormat;
                    }

                    view.setCellFormat(realCellFormat, realRowPoint, colNum, realRowPoint, colNum);
                }
            }
            realRowPoint++;
            rowNumber++;
        }
        return realRowPoint;
    }


    /**
     * 设置表头 支持多层表头
     *
     * @param cfTitle       表头样式
     * @param columnsInfos  列数据
     * @param outputcolumns 输出列数据
     * @param view
     * @param row           当前行
     * @return
     * @author ZhangHua
     * @date 17:46 2019/12/30
     */
    private int setHeadTitle(CellFormat cfTitle, ArrayList<ColumnsInfo> columnsInfos, ArrayList<DynaBean> outputcolumns, View view, int row) {
        int maxRow = 0;
        try {
            HashSet<String> columnIds = new HashSet<>();

            //首先梳理数据，获取最大层数，和所有多层表头的key
            for (int i = 0; i < outputcolumns.size(); i++) {
                DynaBean bean = outputcolumns.get(i);
                ArrayList upsList = (ArrayList) bean.get("ups");
                if (upsList != null && upsList.size() > 0) {
                    for (int j = 0; j < upsList.size(); j++) {
                        columnIds.add((String) upsList.get(j));
                    }
                    if (upsList.size() > 1) {
                        ArrayList list = new ArrayList();
                        for (int j = upsList.size() - 1; j >= 0; j--) {
                            list.add(upsList.get(j));
                        }
                        bean.set("ups", list);
                    }

                    if (maxRow < upsList.size()) {
                        maxRow = upsList.size();
                    }
                }
            }

            //循环输出多层表头
            for (int i = 0; i < outputcolumns.size(); i++) {
                DynaBean bean = outputcolumns.get(i);
                //多层表头
                ArrayList upsList = (ArrayList) bean.get("ups");
                ColumnsInfo columnsInfo = columnsInfos.get(i);
                if (upsList != null && upsList.size() > 0) {

                    //如果存在多层表头的情况，则遍历每一层表头，查找每层表头往后面的列找到表头终止的列位置，并合并单元格
                    for (int j = 0; j < upsList.size(); j++) {
                        String value = (String) upsList.get(j);
                        if (columnIds.contains(value)) {
                            for (int k = i + 1; k < outputcolumns.size(); k++) {
                                DynaBean dynaBean = outputcolumns.get(k);
                                ArrayList dynaBeanUpsList = (ArrayList) dynaBean.get("ups");
                                if (dynaBeanUpsList == null || dynaBeanUpsList.size() == 0 || dynaBeanUpsList.size() < j || k + 1 == outputcolumns.size() ||
                                        !value.equalsIgnoreCase((String) dynaBeanUpsList.get(j))) {

                                    if (k + 1 == outputcolumns.size()) {

                                        view.setText(row + j, i, value.split("`")[0]);
                                        view.setCellFormat(cfTitle, row + j, i, row + j, k);
                                        columnIds.remove(value);
                                        break;

                                    } else {
                                        view.setText(row + j, i, value.split("`")[0]);
                                        view.setCellFormat(cfTitle, row + j, i, row + j, k - 1);
                                        columnIds.remove(value);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //填充真正的列头
                    view.setText(row + upsList.size(), i, columnsInfo.getColumnDesc());
                    view.setCellFormat(cfTitle, row + upsList.size(), i, row + maxRow, i);

                } else {
                    //不存在多层表头，则直接填充列头
                    view.setText(row, i, columnsInfo.getColumnDesc());
                    view.setCellFormat(cfTitle, row, i, row + maxRow, i);
                }
            }

        } catch (CellException e) {
            e.printStackTrace();
        }
        maxRow++;
        return maxRow;
    }

}
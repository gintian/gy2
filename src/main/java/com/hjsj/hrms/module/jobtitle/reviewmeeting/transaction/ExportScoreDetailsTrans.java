package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.TreeUtils;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 职称评审 打分导出明细excel
 *
 * @author ZhangHua
 * @date 11:47 2018/5/28
 */
public class ExportScoreDetailsTrans extends IBusiness {

    //多层表头最大层级
    private int maxLevel = 0;

    @Override
    public void execute() throws GeneralException {
        // 为空：初次进入页面 ；不为空：快速查询
        String subModuleId = (String) this.getFormHM().get("subModuleId");

        String w0301 = PubFunc.decrypt((String) this.getFormHM().get("w0301_e"));
        //1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
        String review_links = (String) this.getFormHM().get("review_links");
        // 所选的分组id串
        String groupIds = this.getFormHM().get("groupids") == null ? "" : (String) this.getFormHM().get("groupids");
        String[] groupList = groupIds.split(",");
        //获取当前页面显示的列对象
        TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
        String filterSql=tableCache.getFilterSql();
        filterSql+=StringUtils.isBlank(tableCache.getQuerySql())?"":tableCache.getQuerySql();
        ArrayList displayColumns = tableCache.getDisplayColumns();
        ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
        for (Object displayColumn : displayColumns) {
            ColumnsInfo column = (ColumnsInfo) displayColumn;
            if (column.isBeExport() && column.getLoadtype() == ColumnsInfo.LOADTYPE_BLOCK && column.getColumnId() != null && !column.getColumnId().toLowerCase().startsWith("c_")) {
                if (column.getChildColumns() == null || column.getChildColumns().size() == 0) {
                    String desc = column.getColumnId();
                    if ("categories_id".equalsIgnoreCase(desc) || DataDictionary.getFieldItem(desc) != null) {
                        columns.add(column);
                    }
                } else {
                    for (Object o : column.getChildColumns()) {
                        column = (ColumnsInfo) o;
                        String desc = column.getColumnId().toLowerCase();
                        boolean isScore = !desc.startsWith("c_") && ("categories_id".equals(desc));
                        if (isScore && column.getLoadtype() == ColumnsInfo.LOADTYPE_BLOCK) {
                            columns.add(column);
                        }
                    }
                }
            }
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        //获取评审模板名称和id
        LinkedHashMap<String, String> tempLateMap = this.getTempLateMap(dao, w0301, review_links);
        StartReviewBo sbo = new StartReviewBo(this.getFrameconn());
        //获取配置的考核模板
        ArrayList<String> tempLateIds = sbo.getKh_Template_Ids(w0301, review_links);

        //申报人的总分和排名信息
        HashMap<String, HashMap<String, String[]>> scoreAndSeqMap = new HashMap<String, HashMap<String, String[]>>();

        //获取模板配置的要素信息
        HashMap<String, LinkedHashMap<String, String>> pointMap = this.getPointMap(dao, w0301, review_links, tempLateIds);

        //获取w05中当前申报人的信息
        ArrayList memberList = this.getMemberData(dao, columns, groupIds, scoreAndSeqMap, tempLateIds,filterSql);

        //获取当前申报人评分信息
        HashMap<String, HashMap<String, HashMap<String, String>>> scoreMap = this.getScoreData(dao, groupIds, review_links, w0301);

        //导出excel
        String fileName = this.exportExcel(memberList, scoreMap, columns, tempLateMap, pointMap, scoreAndSeqMap);

        this.getFormHM().put("fileName", PubFunc.encrypt(fileName));

    }

    /**
     * 获取模板id和名称
     *
     * @param dao
     * @param w0301        会议id
     * @param review_links 阶段id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:10 2018/5/28
     */
    private LinkedHashMap<String, String> getTempLateMap(ContentDAO dao, String w0301, String review_links) throws GeneralException {
        LinkedHashMap<String, String> tempLateMap = new LinkedHashMap<String, String>();
        RowSet rs = null;
        try {
            StartReviewBo bo = new StartReviewBo(this.getFrameconn());
            ArrayList<String> tempLateId = bo.getKh_Template_Ids(w0301, review_links);

            if (tempLateId.size() == 0) {
                return null;
            }
            StringBuffer strSql = new StringBuffer(" select template_id,name from per_template where template_id in(");
            ArrayList dataList = new ArrayList();
            for (String id : tempLateId) {
                strSql.append("?,");
                dataList.add(id);
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                tempLateMap.put(rs.getString("template_id"), rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tempLateMap;
    }

    /**
     * 获取考核模板要素信息
     *
     * @param dao
     * @param w0301        会议id
     * @param review_links 阶段id
     * @param tempLateId   配置的考核模板
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:53 2018/5/28
     */
    private HashMap<String, LinkedHashMap<String, String>> getPointMap(ContentDAO dao, String w0301, String review_links, ArrayList<String> tempLateId) throws GeneralException {
        HashMap<String, LinkedHashMap<String, String>> pointMap = new HashMap<String, LinkedHashMap<String, String>>();
        RowSet rs = null;
        try {
            if (tempLateId.size() == 0) {
                return null;
            }
            StringBuffer strSql = new StringBuffer();
            strSql.append(" select pt.template_id,pp.point_id,pp.pointname from per_template_item pt ");
            strSql.append(" inner join per_template_point ptp on ptp.item_Id =pt.item_Id ");
            strSql.append(" inner join per_point pp on ptp.Point_id=pp.Point_id ");
            strSql.append(" where pt.template_id in( ");
            ArrayList dataList = new ArrayList();
            for (String id : tempLateId) {
                strSql.append("?,");
                dataList.add(id);
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");
            strSql.append(" order by pt.template_id ,pt.seq,ptp.seq ");
            rs = dao.search(strSql.toString(), dataList);
            String strTemplate_Id = "";
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            while (rs.next()) {
                if (StringUtils.isBlank(strTemplate_Id)) {
                    strTemplate_Id = rs.getString("template_id");
                }
                if (!strTemplate_Id.equalsIgnoreCase(rs.getString("template_id"))) {

                    pointMap.put(strTemplate_Id, map);
                    map = new LinkedHashMap<String, String>();
                    strTemplate_Id = rs.getString("template_id");
                }
                map.put(rs.getString("point_id"), rs.getString("pointname"));
            }
            pointMap.put(strTemplate_Id, map);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return pointMap;
    }


    /**
     * 获取w05中申报人的信息
     *
     * @param dao
     * @param displayColumns 列信息
     * @param groupIds       分组id
     * @param scoreAndSeqMap 回传的评分和排名信息
     * @param tempLateIds    当前配置的模板id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:54 2018/5/28
     */
    private ArrayList getMemberData(ContentDAO dao, ArrayList<ColumnsInfo> displayColumns, String groupIds, HashMap<String, HashMap<String, String[]>> scoreAndSeqMap, ArrayList<String> tempLateIds,String filterSql) throws GeneralException {
        RowSet rs = null;
        ArrayList memberList = new ArrayList();
        try {
            String[] groupIdsList = groupIds.split(",");
            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();
            strSql.append(" SELECT  Z.name categories_id ,w05.* ");
            strSql.append(" FROM W05 ");
            strSql.append(" INNER JOIN ( SELECT zc.name ,");
            strSql.append(" zc.categories_id ,");
            strSql.append(" zr.w0501");
            strSql.append(" FROM   zc_categories_relations zr");
            strSql.append(" INNER JOIN zc_personnel_categories zc ON zr.categories_id = zc.categories_id");
            if (groupIdsList.length > 0) {
                strSql.append(" AND zc.categories_id IN (");
                for (String str : groupIdsList) {
                    strSql.append("?,");
                    dataList.add(PubFunc.decrypt(str));
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(")");
            }
            strSql.append(") z ON z.w0501 = W05.W0501");

            if(StringUtils.isNotBlank(filterSql)){
                strSql.append(" where 1=1 ").append(filterSql);
            }
            rs = dao.search(strSql.toString(), dataList);
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            while (rs.next()) {
                ArrayList list = new ArrayList();
                list.add(rs.getString("w0505"));
                for (int i = 0; i < displayColumns.size(); i++) {
                    ColumnsInfo col = displayColumns.get(i);
                    String columnType = col.getColumnType();
                    String value = rs.getString(col.getColumnId());
                    if ("A".equalsIgnoreCase(columnType)) {
                        String codesetid = col.getCodesetId();
                        String content = value;
                        if (!StringUtils.isBlank(codesetid) && !"0".equals(codesetid)) {
                            if ("un".equalsIgnoreCase(codesetid)) {
                                content = AdminCode.getCodeName("UN", value);
                                if (StringUtils.isBlank(content)) {
                                    content = AdminCode.getCodeName("UM", value);
                                }
                            } else if ("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
                            {
                                if ("e0122".equalsIgnoreCase(col.getColumnId())) {
                                    if (Integer.parseInt(display_e0122) == 0) {
                                        content = AdminCode.getCodeName("UM", value);
                                    } else {
                                        CodeItem item = AdminCode.getCode("UM", value, Integer.parseInt(display_e0122));
                                        if (item != null) {
                                            content = item.getCodename();
                                        } else {
                                            content = AdminCode.getCodeName("UM", value);
                                        }
                                    }
                                } else {
                                    content = (AdminCode.getCodeName("UM", value) == null || (AdminCode.getCodeName("UM", value) != null && AdminCode.getCodeName("UM", value).trim().length() == 0)) ? AdminCode.getCodeName("UN", value) : AdminCode.getCodeName("UM", value);
                                }

                            } else {
                                content = AdminCode.getCodeName(codesetid, value);
                            }

                        }
                        list.add(content);
                    } else {
                        list.add(value);
                    }

                }

                memberList.add(list);


                //分模板写入总分和排名信息
                for (String tempLateId : tempLateIds) {
                    String score = rs.getString("C_" + tempLateId) == null ? "" : rs.getString("C_" + tempLateId);
                    String seq = rs.getString("C_" + tempLateId + "_seq") == null ? "" : rs.getString("C_" + tempLateId + "_seq");

                    if (scoreAndSeqMap.containsKey(tempLateId)) {
                        HashMap<String, String[]> memberMap = scoreAndSeqMap.get(tempLateId);
                        memberMap.put(rs.getString("w0505"), new String[]{score, seq});
                    } else {
                        HashMap<String, String[]> memberMap = new HashMap<String, String[]>();
                        memberMap.put(rs.getString("w0505"), new String[]{score, seq});
                        scoreAndSeqMap.put(tempLateId, memberMap);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return memberList;
    }

    /**
     * 分考核要素获取打分信息
     *
     * @param dao
     * @param groupIds     分组id
     * @param review_links 阶段id
     * @param w0301        会议id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:56 2018/5/28
     */
    private HashMap<String, HashMap<String, HashMap<String, String>>> getScoreData(ContentDAO dao, String groupIds, String review_links, String w0301) throws GeneralException {
        RowSet rs = null;
        HashMap<String, HashMap<String, HashMap<String, String>>> dataMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
        try {
            String scoreTableName = "kh_detail", relation_Id = "1_" + w0301 + "_" + review_links;
            String[] groupIdsList = groupIds.split(",");
            StringBuffer strSql = new StringBuffer(" SELECT w0321 FROM W03 WHERE W0301=? ");
            ArrayList dataList = new ArrayList();
            dataList.add(w0301);
            rs = dao.search(strSql.toString(), dataList);
            if (rs.next()) {
                if ("06".equalsIgnoreCase(rs.getString("w0321"))) {
                    scoreTableName = "kh_detail_archive";
                }
            }

            strSql.setLength(0);
            dataList.clear();
            strSql.append(" SELECT pr.pointname,score.* FROM (");
            strSql.append(" SELECT ko.Object_id,AVG(").append(Sql_switcher.isnull("kd.Score", "0")).append(") AS score ,kd.Point_id,kd.template_id FROM kh_object ko ");
            strSql.append(" inner join kh_mainbody km on km.kh_object_id = ko.id ");
            strSql.append(" inner join " + scoreTableName).append(" kd ON ko.id=kd.kh_object_id and kd.kh_mainbody_id = km.id ");
            strSql.append(" WHERE ko.Relation_id=? AND  ko.Object_id IN (SELECT W0505 FROM W05 WHERE W0501 IN (SELECT W0501 FROM zc_categories_relations ");
            dataList.add(relation_Id);
            if (groupIdsList.length > 0) {
                strSql.append(" WHERE categories_id IN (");
                for (String str : groupIdsList) {
                    strSql.append("?,");
                    dataList.add(PubFunc.decrypt(str));
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(")");

            }
            strSql.append(")) and km.status = 2 ");
            strSql.append(" GROUP BY ko.Object_id,kd.Point_id,kd.template_id) score");
            strSql.append(" INNER JOIN per_point pr ON pr.point_id = score.Point_id");
            strSql.append(" INNER JOIN per_template_point pp ON pp.point_id = pr.point_id");
            strSql.append(" ORDER BY score.template_id,Object_id,pp.seq");

            rs = dao.search(strSql.toString(), dataList);

            String templateId = "", objectId = "";

            HashMap<String, HashMap<String, String>> memberMap = new HashMap<String, HashMap<String, String>>();
            HashMap<String, String> pointMap = new HashMap<String, String>();
            while (rs.next()) {
                if (StringUtils.isBlank(templateId)) {
                    templateId = rs.getString("template_id");
                }
                if (StringUtils.isBlank(objectId)) {
                    objectId = rs.getString("Object_id");
                }
                if (!objectId.equalsIgnoreCase(rs.getString("Object_id"))) {
                    memberMap.put(objectId, pointMap);
                    pointMap = new HashMap<String, String>();
                    objectId = rs.getString("Object_id");

                }
                if (!templateId.equalsIgnoreCase(rs.getString("template_id"))) {
                    dataMap.put(templateId, memberMap);
                    memberMap = new HashMap<String, HashMap<String, String>>();
                    templateId = rs.getString("template_id");
                }

                float f = rs.getFloat("score");
                BigDecimal b = new BigDecimal(f);
                String value = String.format("%.2f", (b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()));
                pointMap.put(rs.getString("Point_id"), value);
            }
            memberMap.put(objectId, pointMap);
            dataMap.put(templateId, memberMap);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataMap;
    }


    /**
     * 导出excel
     *
     * @param MemberData     w05中的人员信息
     * @param scoreDataMap   分要素的打分信息
     * @param displayColumns 列对象
     * @param templateName   模板名称
     * @param pointData      考核要素信息
     * @param scoreAndSeqMap 人员的总分和排名
     * @return
     * @throws GeneralException
     */
    private String exportExcel(ArrayList<ArrayList> MemberData, HashMap<String, HashMap<String, HashMap<String, String>>> scoreDataMap,
                               ArrayList<ColumnsInfo> displayColumns, HashMap<String, String> templateName,
                               HashMap<String, LinkedHashMap<String, String>> pointData, HashMap<String, HashMap<String, String[]>> scoreAndSeqMap) throws GeneralException {
        String fileName = "";
        FileOutputStream fileOut = null;
        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
        try {
            Iterator iter = templateName.entrySet().iterator();
            HSSFCellStyle headStyle = this.getStyle(10, "CENTER", "head", wb, "A", "");
            HSSFCellStyle numStyle = this.getStyle(10, "right", "cell", wb, "N", "0.00");
            HSSFCellStyle seqStyle = this.getStyle(10, "right", "cell", wb, "N", "");
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                //模板id
                String tempLate_Id = (String) entry.getKey();
                //构建多层表头树结构
                ScoreTreeNode node = this.buildTree(tempLate_Id);
                //找到最深的层级和下级节点数量
                this.findMaxLevelAndNodeNum(node);

                ColumnsInfo pingfen = this.getColumn("评分", "c_" + tempLate_Id, 50, "right", "N", 2);
                ColumnsInfo paiming = this.getColumn("排名", "c_" + tempLate_Id + "_seq", 50, "right", "N", 0);

                //获取模板要素列表
                ArrayList<ColumnsInfo> pointColums = this.getColumnInfos(tempLate_Id, pointData);

                //获取w05中的分数和排名
                HashMap<String, String[]> memberScoreAndSeqFromW05 = scoreAndSeqMap.get(tempLate_Id);
                HSSFSheet sheet = wb.createSheet(String.valueOf(entry.getValue()));

                //表头的最下层位置
                int headNum = this.maxLevel;
                HSSFRow row;
                //画w05中的列头
                this.wirtePointHead(sheet, this.maxLevel - 1, displayColumns, headStyle, 0);

                //画多层列头
                this.wirteItemHead(sheet, node, headStyle, displayColumns.size() - 1);
                ArrayList<ColumnsInfo> tList = new ArrayList<ColumnsInfo>();
                tList.add(pingfen);
                tList.add(paiming);

                //画评分和列表的列头
                this.wirtePointHead(sheet, this.maxLevel - 1, tList, headStyle, displayColumns.size() + pointColums.size());

                //取得人员打分信息
                HashMap<String, HashMap<String, String>> memberMap = scoreDataMap.get(tempLate_Id);
                //遍历所有人员
                for (int rowNum = 0; rowNum < MemberData.size(); rowNum++) {

                    ArrayList dataList = (ArrayList) MemberData.get(rowNum).clone();
                    String a0100 = String.valueOf(dataList.get(0));
                    String[] scoreAndSeq = memberScoreAndSeqFromW05.get(a0100);
                    dataList.remove(0);
                    row = sheet.getRow(rowNum + headNum);
                    if (row == null) {
                        row = sheet.createRow(rowNum + headNum);
                    }
                    int colNum = 0;
                    //首先输出w05中的人员信息
                    for (; colNum < dataList.size(); colNum++) {
                        ColumnsInfo column = displayColumns.get(colNum);

                        if ("A".equalsIgnoreCase(column.getColumnType())) {
                            HSSFCellStyle Style = this.getStyle(10, column.getTextAlign(), "cell", wb, "A", "");
                            String value = dataList.get(colNum) != null ? String.valueOf(dataList.get(colNum)) : "";
                            this.wirtecell(row, colNum, value, Style, column);
                        } else if ("N".equalsIgnoreCase(column.getColumnType())) {
                            String format = "0";
                            if (column.getDecimalWidth() > 0)
                                format += ".";
                            for (int i = 0; i < column.getDecimalWidth(); i++) {
                                format += "0";
                            }
                            HSSFCellStyle Style = this.getStyle(10, column.getTextAlign(), "cell", wb, "N", format);
                            String value = dataList.get(colNum) != null ? String.valueOf(dataList.get(colNum)) : "";
                            this.wirtecell(row, colNum, value, Style, column);
                        } else if ("D".equalsIgnoreCase(column.getColumnType())) {
                            HSSFCellStyle Style = this.getStyle(10, column.getTextAlign(), "cell", wb, "D", "");
                            String itemfmt = "";
							switch (column.getColumnLength()) {
								case 4:
									itemfmt = "yyyy";
									break;
								case 7:
									itemfmt = "yyyy-MM";
									break;
								case 10:
									itemfmt = "yyyy-MM-dd";
									break;
								case 16:
									itemfmt = "yyyy-MM-dd HH:mm";
									break;
								case 18:
									itemfmt = "yyyy-MM-dd HH:mm:ss";
									break;
								default:
									itemfmt = "yyyy-MM-dd";
									break;
							}
							SimpleDateFormat dateFormat = new SimpleDateFormat(itemfmt);
							String value = dataList.get(colNum)==null?"":dateFormat.format(dateFormat.parse((String)dataList.get(colNum)));
							
                            //String value = dataList.get(colNum) != null ? String.valueOf(dataList.get(colNum)) : "";
                            this.wirtecell(row, colNum, value, Style, column);
                        } else if ("M".equalsIgnoreCase(column.getColumnType())) {
                            HSSFCellStyle Style = this.getStyle(15, column.getTextAlign(), "cell", wb, "M", "");
                            String value = dataList.get(colNum) != null ? String.valueOf(dataList.get(colNum)) : "";
                            this.wirtecell(row, colNum, value, Style, column);
                        }
                    }

                    //w05中的信息输出完成之后 输出各个要素的打分信息
                    if (colNum > 0) {
                        HashMap<String, String> pointMap = new HashMap<String, String>();
                        if (memberMap != null && memberMap.containsKey(a0100)) {
                            pointMap = memberMap.get(a0100);
                        }
                        for (int i = 0; i < pointColums.size(); i++) {
                            ColumnsInfo columnsInfo = pointColums.get(i);
                            String columnId = columnsInfo.getColumnId().toUpperCase();
                            String context = pointMap.containsKey(columnId) ? pointMap.get(columnId) : "";
                            this.wirtecell(row, colNum, context, numStyle, columnsInfo);
                            colNum++;
                        }
                        this.wirtecell(row, colNum, scoreAndSeq == null ? "" : scoreAndSeq[0], numStyle, pingfen);
                        this.wirtecell(row, colNum + 1, scoreAndSeq == null ? "" : scoreAndSeq[1], seqStyle, paiming);

                    }
                }
            }

            fileName = this.userView.getUserName() + "_评分明细表" + ".xls";
            String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
            fileOut = new FileOutputStream(url);
            wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(fileOut);
            PubFunc.closeIoResource(wb);
        }
        return fileName;
    }

    private ColumnsInfo getColumn(String text, String id, int width, String align, String type, int decimalWidth) {
        ColumnsInfo bean = new ColumnsInfo();
        bean.setColumnDesc(text);
        bean.setColumnId(id.toUpperCase());
        bean.setColumnWidth(width);
        bean.setTextAlign(align);
        bean.setColumnType(type);
        bean.setDecimalWidth(decimalWidth);
        return bean;
    }

    private void wirtePointHead(HSSFSheet sheet, int headNum, ArrayList<ColumnsInfo> colunm, HSSFCellStyle headStyle, int colNum) throws GeneralException {
        try {
            HSSFRow row;
            for (int i = 0; i < colunm.size(); i++) {
                ColumnsInfo columnsInfo = colunm.get(i);
                int col = colNum + i;
                sheet.setColumnWidth((short) col, columnsInfo.getColumnWidth() * 50);
                for (int i1 = 0; i1 <= headNum; i1++) {
                    row = sheet.getRow(i1);
                    if (row == null) {
                        row = sheet.createRow(i1);
                    }
                    HSSFCell cell = row.getCell(col);
                    if (cell == null) {
                        cell = row.createCell(col);
                    }
                    //设置该单元格样式
                    cell.setCellStyle(headStyle);
                    //给该单元格赋值
                    cell.setCellValue(new HSSFRichTextString(columnsInfo.getColumnDesc()));
                }
                sheet.addMergedRegion(new CellRangeAddress(0, headNum, col, col));

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void wirtecell(HSSFRow row, int colIndex, String content, HSSFCellStyle cellStyle, ColumnsInfo column) throws GeneralException {
        try {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            //设置该单元格样式
            cell.setCellStyle(cellStyle);

            String type = column.getColumnType();
            if ("N".equalsIgnoreCase(type)) {
                if (StringUtils.isNotBlank(content.toString())) {
                    BigDecimal bd = new BigDecimal(Float.parseFloat(content));
                    bd = bd.setScale(column.getDecimalWidth(), BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bd.doubleValue());
                } else {
                    cell.setCellValue("");
                }

            } else {
                cell.setCellValue(new HSSFRichTextString(content.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 画多层表头
     *
     * @param sheet
     * @param treeNode           树节点
     * @param headStyle
     * @param displayColumnsSize w05中列头的长度
     */
    private void wirteItemHead(HSSFSheet sheet, ScoreTreeNode treeNode, HSSFCellStyle headStyle, int displayColumnsSize) {
        int col = displayColumnsSize + 1;
        for (ScoreTreeNode scoreTreeNode : treeNode.getTreeNodesList()) {
            this.wirteHeadByTreeNode(sheet, scoreTreeNode, headStyle, 0, col);
            col += scoreTreeNode.getChildNum() == 0 ? 1 : scoreTreeNode.getChildNum();
        }
    }

    /**
     * 递归画多层表头
     *
     * @param sheet
     * @param treeNode
     * @param headStyle
     * @param rowNum    当前行
     * @param colNum    当前列
     */
    private void wirteHeadByTreeNode(HSSFSheet sheet, ScoreTreeNode treeNode, HSSFCellStyle headStyle, int rowNum, int colNum) {

        HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        HSSFCell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        //设置该单元格样式
        cell.setCellStyle(headStyle);
        //给该单元格赋值
        cell.setCellValue(new HSSFRichTextString(treeNode.getName()));
        int firstRow = rowNum, lastRow = rowNum, firstCol = colNum, lastCol = colNum;

        //是否需要合并
        boolean isNeedMerged = false;
        //如果画到要素了 但是行数不够 那么就合并当前行 到最大行之间的单元格
        if (treeNode.getChildNum() == 0 && rowNum < (this.maxLevel - 1)) {
            lastRow = this.maxLevel - 1;
            isNeedMerged = true;
        }

        if (treeNode.getChildNum() != 0) {
            //是否所有子节点都是要素
            boolean isAllPoint = true;
            //如果剩下的全是指标了 且行数不够，则合并当前行 到要素行之前的单元格
            for (ScoreTreeNode scoreTreeNode : treeNode.getTreeNodesList()) {
                if (!scoreTreeNode.isPoint()) {
                    isAllPoint = false;
                }
            }
            if (isAllPoint && rowNum != (this.maxLevel - 2)) {//如果全是指标 且当前行不是倒数第二行
                lastRow = this.maxLevel - 2;
                isNeedMerged = true;
            }
            lastCol = colNum + treeNode.getChildNum() - 1;
            if (lastCol > colNum) {
                isNeedMerged = true;
            }
        }

        //如果需要合并 则需要将待合并的单元格设置上样式
        if (isNeedMerged) {
            for (int i = firstRow; i <= lastRow; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    row = sheet.createRow(i);
                }
                for (int j = firstCol; j <= lastCol; j++) {
                    cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.createCell(j);
                    }
                    cell.setCellStyle(headStyle);
                }
            }
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        }

        int col = colNum;
        lastRow++;
        if (treeNode.getTreeNodesList() != null) {
            for (ScoreTreeNode scoreTreeNode : treeNode.getTreeNodesList()) {

                //画子节点
                this.wirteHeadByTreeNode(sheet, scoreTreeNode, headStyle, lastRow, col);
                col += scoreTreeNode.getChildNum() == 0 ? 1 : scoreTreeNode.getChildNum();
            }
        }


    }

    private HSSFCellStyle getStyle(int fontSize, String Align, String place, HSSFWorkbook wb, String type, String format) {
        HSSFCellStyle a_style = wb.createCellStyle();
        a_style.setWrapText(true);// 自动换行
        short border = (short) 1;
        short borderColor = IndexedColors.BLACK.index;
        HorizontalAlignment align;
        FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
        short fillForegroundColor = IndexedColors.WHITE.index;
        boolean fontBoldWeight = false;
        boolean isFontBold = false;// 是否加粗
        if (StringUtils.isBlank(Align)) {
            Align = "A".equalsIgnoreCase(type) ? "left" : "right";
        }
        if ("right".equalsIgnoreCase(Align)) {
            align = HorizontalAlignment.RIGHT;
        } else if ("left".equalsIgnoreCase(Align)) {
            align = HorizontalAlignment.LEFT;
        } else {
            align = HorizontalAlignment.CENTER;
        }
        // 没有设置单元格样式  默认头部字体是加粗
        if ("head".equals(place)) {
            isFontBold = true;
        }
        //HSSFFont fonttitle = null;
        if ("head".equals(place)) {
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

        if ("N".equalsIgnoreCase(type) && StringUtils.isNotBlank(format)) {
            HSSFDataFormat df = wb.createDataFormat();
            a_style.setDataFormat(df.getFormat(format));
        }

        a_style.setBorderBottom(BorderStyle.valueOf(border));
        a_style.setBottomBorderColor(borderColor);
        a_style.setBorderLeft(BorderStyle.valueOf(border));
        a_style.setLeftBorderColor(borderColor);
        a_style.setBorderRight(BorderStyle.valueOf(border));
        a_style.setRightBorderColor(borderColor);
        a_style.setBorderTop(BorderStyle.valueOf(border));
        a_style.setTopBorderColor(borderColor);

        a_style.setVerticalAlignment(VerticalAlignment.CENTER);
        a_style.setAlignment(align);
        a_style.setLocked(false);
        return a_style;
    }


    private ArrayList<ColumnsInfo> getColumnInfos(String template_Id, HashMap<String, LinkedHashMap<String, String>> pointData) throws GeneralException {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try {
            LinkedHashMap<String, String> map = pointData.get(template_Id);
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                list.add(this.getColumn(String.valueOf(entry.getValue()), String.valueOf(entry.getKey()), 50, "right", "N", 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    /**
     * 构建多层表头树结构
     *
     * @param templateId
     * @return
     * @throws GeneralException
     */
    private ScoreTreeNode buildTree(String templateId) throws GeneralException {
        ScoreTreeNode treeNode = new ScoreTreeNode("root", "", "root");
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            ArrayList<ScoreTreeNode> list = new ArrayList<ScoreTreeNode>();
            list.add(treeNode);

            //首先获取所有项目
            StringBuffer strSql = new StringBuffer();
            // 如果考核模板中没有设置指标的项目不显示在excel中
            strSql.append("SELECT max(pti.itemdesc) itemdesc,pti.item_id,max(pti.parent_id) parent_id FROM per_template_point ptp ");
            strSql.append(" INNER JOIN per_template_item pti ON pti.item_id = ptp.item_id ");
            strSql.append(" WHERE template_id=? group by pti.item_id,pti.seq ORDER BY pti.seq ");
            rs = dao.search(strSql.toString(), Arrays.asList(new Object[]{templateId}));
            while (rs.next()) {
                String pid = rs.getString("parent_id");
                if (StringUtils.isBlank(pid)) {
                    pid = "root";
                }

                ScoreTreeNode node = new ScoreTreeNode(rs.getString("item_id"), pid, rs.getString("itemdesc"));
                list.add(node);
            }
            //获取所有要素
            strSql.setLength(0);
            strSql.append("SELECT ptp.point_id AS item_id,ptp.item_id AS parent_id ,ppt.pointname AS itemdesc FROM per_template_point ptp ");
            strSql.append("INNER JOIN per_point ppt ON ppt.point_id = ptp.point_id INNER JOIN per_template_item pti ON pti.item_id = ptp.item_id ");
            strSql.append("WHERE pti.template_id=? ORDER BY pti.seq,ptp.seq");

            rs = dao.search(strSql.toString(), Arrays.asList(new Object[]{templateId}));
            while (rs.next()) {
                ScoreTreeNode node = new ScoreTreeNode(rs.getString("item_id"), rs.getString("parent_id"), rs.getString("itemdesc"));
                node.setPoint(true);
                list.add(node);
            }
            //构建树
            TreeUtils.createTree(list, treeNode, "id", "pid", "treeNodesList", "levelNum");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return treeNode;
    }

    /**
     * 找到最深的层级和下级节点数量
     *
     * @param treeNode
     * @return
     */
    private int findMaxLevelAndNodeNum(ScoreTreeNode treeNode) {

        ArrayList<ScoreTreeNode> list = treeNode.getTreeNodesList();
        if (treeNode.getLevelNum() > this.maxLevel) {
            this.maxLevel = treeNode.getLevelNum();
        }
        int num = 0;
        if (list != null) {
            for (ScoreTreeNode scoreTreeNode : list) {
                num += this.findMaxLevelAndNodeNum(scoreTreeNode);
            }
        }
        treeNode.setChildNum(num);
        return treeNode.getTreeNodesList() == null ? 1 : num;


    }


}

/**
 * 多层表头树节点对象
 */
class ScoreTreeNode {
    //id
    private String id;
    //父节点id
    private String pid;
    //名称
    private String name;
    //层级
    private int levelNum;
    //子节点
    private ArrayList<ScoreTreeNode> treeNodesList;
    //下级节点数量
    private int childNum;

    public ScoreTreeNode(String id, String pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    public int getChildNum() {
        return childNum;
    }

    public void setChildNum(int childNum) {
        this.childNum = childNum;
    }

    public boolean isPoint() {
        return isPoint;
    }

    public void setPoint(boolean point) {
        isPoint = point;
    }

    private boolean isPoint = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public ArrayList<ScoreTreeNode> getTreeNodesList() {
        return treeNodesList;
    }

    public void setTreeNodesList(ArrayList<ScoreTreeNode> treeNodesList) {
        this.treeNodesList = treeNodesList;
    }


}

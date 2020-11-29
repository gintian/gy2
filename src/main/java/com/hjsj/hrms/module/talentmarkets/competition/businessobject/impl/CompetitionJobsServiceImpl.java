package com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.ykcard.YkcardOutWord;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.transaction.mobileapp.template.MobileTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.*;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 内部竞聘岗位列表接口实现类
 * @Author wangz
 * @Date 2019/7/24 10:42
 * @Version V1.0
 **/
public class CompetitionJobsServiceImpl implements CompetitionJobsService {

    private Connection conn;

    private UserView userView;
    private ContentDAO dao;
    //用于存储前台显示的列的fieldsetid:itemid 便于拼接sql语句（固定显示项不在内）
    //private static List<String> columnsItemIdList = new ArrayList<String>();
    private static List<FieldItem> newColumnsList = new ArrayList<FieldItem>();
    private String subModuleId = "competitionJobsTable";
    private int index = 0;

    public CompetitionJobsServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        dao = new ContentDAO(this.conn);
    }

    /**
     * 保存发布通知信息
     *
     * @param selectList
     * @param postList
     * @param topic
     */
    @Override
    public void savePublishedNotice(ArrayList selectList, ArrayList postList, String topic) {
        RowSet rowSet = null;
        try {
            //更新通知对象
            for (int i = 0; i < selectList.size(); i++) {
                String privStr = "";
                String orgStr = "";
                if (StringUtils.isNotEmpty(postList.get(i).toString())) {
                    //发送通知数据准备
                    RecordVo vo = new RecordVo("announce");
                    IDFactoryBean idf = new IDFactoryBean();
                    int announce_id = Integer.parseInt(idf.getId("announce.id", "", this.conn));
                    vo.setInt("id", announce_id);
                    vo.setString("topic", topic);
                    vo.setString("content", postList.get(i).toString());
                    vo.setInt("period", 5);
                    vo.setInt("priority", 1);
                    vo.setInt("approve", 1);
                    vo.setDate("approvetime", DateStyle.getSystemTime());
                    vo.setString("approveuser", this.userView.getUserFullName());
                    vo.setInt("viewcount", 0);
                    vo.setString("ext", "");
                    vo.setInt("flag", 1);
                    vo.setString("createuser", this.userView.getUserFullName());
                    vo.setDate("createtime", DateStyle.getSystemTime());
                    // 插入通知
                    ContentDAO dao = new ContentDAO(this.conn);
                    int result = dao.addValueObject(vo);

                    if (StringUtils.isNotEmpty(selectList.get(i).toString())) {
                        //String z8115 = selectList.get(i).toString().substring(1, selectList.get(i).toString().length() - 1);
                        String z8115 = selectList.get(i).toString();
                        String[] z8115Arr = z8115.split("`");
                        //说明只选了一个竞聘范围
                        //if (z8115Arr[0].split(",").length == 1) {
                        //    String id = z8115Arr[0];
                        //    List orgDataList = this.getCompetitiveScopeOrgData(id);
                        //    Map orgMap = (Map) orgDataList.get(0);
                        //    String orgpre = (String) orgMap.get("orgpre");
                        //    orgStr += (orgpre + id + "`");
                        //} else if (z8115Arr[0].split(",").length > 1) {
                        for (int j = 0; j < z8115Arr[0].split(",").length; j++) {
                            String id = z8115Arr[0].split(",")[j];
                            if (StringUtils.isBlank(id)) {
                                continue;
                            }
                            List orgDataList = this.getCompetitiveScopeOrgData(id);
                            Map orgMap = (Map) orgDataList.get(0);
                            String orgpre = (String) orgMap.get("orgpre");
                            orgStr += (orgpre + id + "`");
                        }

                        //}
                        if (StringUtils.isNotEmpty(orgStr)) {
                            orgStr += ",";
                        }
                    } else {
                        String sql = "select codeitemid from organization where parentid = codeitemid and grade = 1";
                        rowSet = dao.search(sql);
                        while (rowSet.next()) {
                            orgStr += "UN" + rowSet.getString("codeitemid");
                            if (StringUtils.isNotEmpty(orgStr)) {
                                orgStr += "`";
                            }
                        }
                    }
                    privStr = orgStr;

                    BoardBo boardBo = new BoardBo(this.conn, this.userView);
                    boardBo.savePriv(privStr, String.valueOf(announce_id));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }

    }

    /**
     * 公示后修改竞聘人员状态
     *
     * @param ids
     */
    @Override
    public void changePersonnelStatus(String ids) throws GeneralException {
        String[] idArray = ids.split(",");
        ArrayList paramList = new ArrayList();
        StringBuffer updateSql = new StringBuffer();
        updateSql.append("update z83 set Z8303 = '10' where Z8101 in(");
        try {
            for (String id : idArray) {
                id = PubFunc.decrypt(id);
                paramList.add(id);
                updateSql.append("?").append(",");
            }
            updateSql.setLength(updateSql.length() - 1);
            updateSql.append(") and z8303 = '08'");
            dao.update(updateSql.toString(), paramList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPos.changePersonnelStatusFail");
        }
    }

    @Override
    public String exportInterviewList(String z8101s_e) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer partSql = new StringBuffer();
        StringBuffer sql = new StringBuffer();
        List<String> paramList = new ArrayList<String>();
        List<Map<String, String>> peopleData = new ArrayList<Map<String, String>>();
        HSSFWorkbook wb = new HSSFWorkbook();
        String fileName = "";
        RowSet rs = null;
        FileOutputStream fileOut = null;
        try {
            String[] z8101eArray = z8101s_e.split(",");
            //获取登录认证库
            String loginTableStr = TalentMarketsUtils.getLoginTableStr();
            if (StringUtils.isNotBlank(loginTableStr)) {
                String[] loginTableArr = loginTableStr.split(",");
                for (int i = 0; i < loginTableArr.length; i++) {
                    String daPre = loginTableArr[i];
                    partSql.append("select z8101, A0101, B0110, E0122, E01A1 from z83 ");
                    partSql.append("inner join ").append(daPre).append("A01 on z83.z8301 = ");
                    partSql.append(daPre).append("A01.guidkey");
                    partSql.append(" where z8303 = '04'");
                    if (i < loginTableArr.length - 1) {
                        partSql.append(" union all ");
                    }
                }
            }
            sql.append("select Z81.B0110 yp_b0110, Z81.E0122 yp_E0122, Z81.E01A1 yp_E01A1,t.B0110,A0101,t.E0122,t.E01A1 from z81 inner join");
            sql.append("(").append(partSql.toString()).append(") t").append(" on t.z8101 = z81.z8101");
            sql.append(" where z81.z8101 in (");
            for (int i = 0; i < z8101eArray.length; i++) {
                sql.append("?");
                if (i < z8101eArray.length - 1) {
                    sql.append(",");
                }
                paramList.add(PubFunc.decrypt(z8101eArray[i]));
            }
            sql.append(")");
            String privSql = TalentMarketsUtils.getPrivSql("z81", "4", this.userView);
            sql.append(privSql);
            rs = dao.search(sql.toString(), paramList);
            while (rs.next()) {
                String yp_b0110 = rs.getString("yp_b0110");
                String yp_e0122 = rs.getString("yp_E0122");
                String yp_e01a1 = rs.getString("yp_E01A1");
                String a0101 = rs.getString("A0101");
                String b0110 = rs.getString("B0110");
                String e0122 = rs.getString("E0122");
                String e01a1 = rs.getString("E01A1");
                Map<String, String> temp = new HashMap<String, String>();
                temp.put("yp_b0110", AdminCode.getCodeName("UN", yp_b0110));
                temp.put("yp_e0122", AdminCode.getCodeName("UM", yp_e0122));
                temp.put("yp_e01a1", AdminCode.getCodeName("@K", yp_e01a1));
                temp.put("a0101", a0101);
                temp.put("b0110", AdminCode.getCodeName("UN", b0110));
                temp.put("e0122", AdminCode.getCodeName("UM", e0122));
                temp.put("e01a1", AdminCode.getCodeName("@K", e01a1));
                peopleData.add(temp);
            }
            this.exportExcel(peopleData, wb);
            fileName = this.userView.getUserName() + "_面试名单" + ".xls";
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + File.separator + fileName);
            fileName = PubFunc.encrypt(fileName);
            wb.write(fileOut);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPos.msg.exportInterviewListError");
        } finally {
        	PubFunc.closeResource(wb);
            PubFunc.closeResource(rs);
            PubFunc.closeResource(fileOut);
        }
        return fileName;


    }

    /**
     * 导出面试名单excel
     *
     * @param dataList
     */
    private void exportExcel(List<Map<String, String>> dataList, HSSFWorkbook wb) {
        int columnWidth = 5500;
        HashMap<String, CellStyle> cellStyleMap = createCellStyle(wb);
        HSSFSheet sheet = wb.createSheet("第1页");
        //表头样式
        CellStyle cellstyle = cellStyleMap.get("cellstyle");
        Row row = sheet.createRow(0);
        row.setHeight((short) 400);

        Cell a0101Cell = row.createCell(0);
        a0101Cell.setCellStyle(cellstyle);
        a0101Cell.setCellValue("姓名");
        sheet.setColumnWidth(0, columnWidth);

        Cell b0110Cell = row.createCell(1);
        b0110Cell.setCellStyle(cellstyle);
        b0110Cell.setCellValue("单位名称");
        sheet.setColumnWidth(1, columnWidth);

        Cell e0122Cell = row.createCell(2);
        e0122Cell.setCellStyle(cellstyle);
        e0122Cell.setCellValue("部门名称");
        sheet.setColumnWidth(2, columnWidth);


        Cell e01a1Cell = row.createCell(3);
        e01a1Cell.setCellStyle(cellstyle);
        e01a1Cell.setCellValue("职位名称");
        sheet.setColumnWidth(3, columnWidth);

        Cell ypE01a1Cell = row.createCell(4);
        ypE01a1Cell.setCellStyle(cellstyle);
        ypE01a1Cell.setCellValue("应聘职位名称");
        sheet.setColumnWidth(4, columnWidth);

        Cell ypB0110Cell = row.createCell(5);
        ypB0110Cell.setCellStyle(cellstyle);
        ypB0110Cell.setCellValue("应聘职位所属单位名称");
        sheet.setColumnWidth(5, columnWidth);

        Cell ypE0122Cell = row.createCell(6);
        ypE0122Cell.setCellStyle(cellstyle);
        ypE0122Cell.setCellValue("应聘职位所属部门名称");
        sheet.setColumnWidth(6, columnWidth);
        Cell cell;
        CellStyle cellLeft = cellStyleMap.get("cellLeft");
        CellStyle cellCenter = cellStyleMap.get("cellCenter");
        cellCenter.setWrapText(true);
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> record = dataList.get(i);
            row = sheet.createRow(i + 1);
            row.setHeight((short) 400);
            cell = row.createCell(0);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("a0101"));


            cell = row.createCell(1);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("b0110"));

            cell = row.createCell(2);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("e0122"));

            cell = row.createCell(3);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("e01a1"));

            cell = row.createCell(4);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("yp_e01a1"));

            cell = row.createCell(5);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("yp_b0110"));

            cell = row.createCell(6);
            cell.setCellStyle(cellCenter);
            cell.setCellValue(record.get("yp_e0122"));
        }

    }

    private HashMap<String, CellStyle> createCellStyle(Workbook wb) {
        HashMap<String, CellStyle> map = new HashMap<String, CellStyle>();

        CellStyle cellstyle = wb.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellstyle.setBorderBottom(BorderStyle.THIN);
        cellstyle.setBorderTop(BorderStyle.THIN);
        cellstyle.setBorderLeft(BorderStyle.THIN);
        cellstyle.setBorderRight(BorderStyle.THIN);

        CellStyle cellCenter = wb.createCellStyle();//1
        cellCenter.setAlignment(HorizontalAlignment.CENTER);//居中
        cellCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellCenter.setBorderBottom(BorderStyle.THIN);
        cellCenter.setBorderTop(BorderStyle.THIN);
        cellCenter.setBorderLeft(BorderStyle.THIN);
        cellCenter.setBorderRight(BorderStyle.THIN);

        CellStyle cellLeft = wb.createCellStyle();//2
        cellLeft.setAlignment(HorizontalAlignment.LEFT);//居左
        cellLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellLeft.setBorderBottom(BorderStyle.THIN);
        cellLeft.setBorderTop(BorderStyle.THIN);
        cellLeft.setBorderLeft(BorderStyle.THIN);
        cellLeft.setBorderRight(BorderStyle.THIN);
        cellLeft.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle cellRight = wb.createCellStyle();//3
        cellRight.setAlignment(HorizontalAlignment.RIGHT);//居右
        cellRight.setVerticalAlignment(VerticalAlignment.CENTER);
        cellRight.setBorderBottom(BorderStyle.THIN);
        cellRight.setBorderTop(BorderStyle.THIN);
        cellRight.setBorderLeft(BorderStyle.THIN);
        cellRight.setBorderRight(BorderStyle.THIN);
        map.put("cellRight", cellRight);
        map.put("cellLeft", cellLeft);
        map.put("cellCenter", cellCenter);
        map.put("cellstyle", cellstyle);

        return map;
    }

    @Override
    public String getCompetitionJobsGridConfigs(String statusFlag) throws GeneralException {
        List<ColumnsInfo> columns = this.getCompetitionJobsGridColumns();
        TableConfigBuilder builder = new TableConfigBuilder(subModuleId, (ArrayList) columns, subModuleId, this.userView, this.conn);

        builder.setTableTools(this.getCompetitionJobsGridButtons(statusFlag));
        builder.setDataSql(this.getTableSql(statusFlag));
        builder.setSelectable(true);
        //builder.setLockable(true);
        builder.setColumnFilter(true);
        //保存方案按钮 权限控制
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction("4010112")) {
            builder.setShowPublicPlan(true);
        }
        builder.setItemKeyFunctionId("TM000000012");
        builder.setTitle(ResourceFactory.getProperty("talentmarkets.competitionJobs.competitivePosition"));
        builder.setEditable(true);
        builder.setScheme(true);
        builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
        builder.setOrderBy(" order by create_time desc");
        String gridConfig = builder.createExtTableConfig();
        return gridConfig;
    }

    /**
     * 用于获取竞聘岗位列表显示的列
     *
     * @return ColumnsInfo 列的集合
     */
    private List<ColumnsInfo> getCompetitionJobsGridColumns() throws GeneralException {
        ArrayList<ColumnsInfo> columnsInfoArrayList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo;
        Map<String, Object> exparam = new HashMap<String, Object>();
        try {
            //获取快速审批配置项
            boolean quickapprove = TalentMarketsUtils.getQuickApprove(this.conn);
            //获取面试安排配置项
            boolean openInterview = TalentMarketsUtils.getOpenInterview(this.conn);
            //id 主键
            exparam.put("editableValidFunc", "false");
            exparam.put("encrypted", true);
            exparam.put("fieldsetid", "z81");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "Z8101", "", ColumnsInfo.LOADTYPE_ONLYLOAD, 0, exparam);
            columnsInfoArrayList.add(columnsInfo);
            exparam.clear();

            //岗位竞聘状态
            exparam.put("editableValidFunc", "false");
            exparam.put("fieldsetid", "z81");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "86", "Z8103", DataDictionary.getFieldItem("z8103", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //所属单位
            exparam.put("ctrltype", "3");
            exparam.put("nmodule", "4");
            columnsInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("B0110").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //所属部门
            exparam.put("doFilterOnLoad", true);
            columnsInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("E0122").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //所属岗位
            exparam.remove("doFilterOnLoad");
            exparam.put("locked", true);
            exparam.put("rendererFunc", "competitionJobs.renderJobsColumnFunc");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "@K", "E01A1", DataDictionary.getFieldItem("E01A1", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);
            exparam.clear();

            //缺编人数
            exparam.put("fieldsetid", "z81");
            exparam.put("columnLength", 4);
            //exparam.put("editableValidFunc", "false");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", "Z8105", DataDictionary.getFieldItem("Z8105", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //拟招聘人数
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", "Z8107", DataDictionary.getFieldItem("Z8107", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //应聘人数
            exparam.put("rendererFunc", "competitionJobs.applyPsnCountRenderFunc");
            exparam.put("editableValidFunc", "false");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", "Z8109", DataDictionary.getFieldItem("Z8109", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //开始日期
            exparam.clear();
            exparam.put("columnLength", 16);
            exparam.put("textAlign", "left");
            exparam.put("fieldsetid", "z81");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("D", "0", "Z8111", DataDictionary.getFieldItem("Z8111", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //结束日期
            columnsInfo = TalentMarketsUtils.getColumnsInfo("D", "0", "Z8113", DataDictionary.getFieldItem("Z8113", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //面试安排
            if ((this.userView.isSuper_admin() || this.userView.hasTheFunction(INTERVIEW_ARRANGEMENT_FUNC_ID)) && openInterview) {
                exparam.clear();
                exparam.put("textAlign", "center");
                exparam.put("editableValidFunc", "false");
                exparam.put("rendererFunc", "competitionJobs.interviewArrangementRenderFunc");
                exparam.put("beExport", false);
                exparam.put("sortable", false);
                exparam.put("filterable", false);
                exparam.put("queryable", false);
                columnsInfo = TalentMarketsUtils.getColumnsInfo("", "0", "interviewArrangement", ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewArrangement"), ColumnsInfo.LOADTYPE_BLOCK, 70, exparam);
                columnsInfoArrayList.add(columnsInfo);
            }

            //竞聘范围
            exparam.clear();
            //exparam.put("editableValidFunc", "false");
            //exparam.put("beExport", false);
            exparam.put("rendererFunc", "competitionJobs.competitiveScopeRenderFunc");
            exparam.put("fieldsetid", "z81");
            exparam.put("ctrltype", "3");
            exparam.put("nmodule", "4");
            exparam.put("sortable", false);
            exparam.put("filterable", false);
            exparam.put("queryable", false);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "Z8115", DataDictionary.getFieldItem("Z8115", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            columnsInfoArrayList.add(columnsInfo);

            //审批过程
            if(!quickapprove){
                exparam.clear();
                exparam.put("textAlign", "center");
                exparam.put("editableValidFunc", "false");
                exparam.put("rendererFunc", "competitionJobs.approvalProcessRenderFunc");
                exparam.put("fieldsetid", "z81");
                exparam.put("sortable", false);
                exparam.put("filterable", false);
                columnsInfo = TalentMarketsUtils.getColumnsInfo("M", "0", "Z8117", DataDictionary.getFieldItem("Z8117", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 150, exparam);
                columnsInfoArrayList.add(columnsInfo);
            }

            //业务字典z81其它列
            ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Z81", Constant.USED_FIELD_SET);
            exparam.clear();
            //CompetitionJobsServiceImpl.columnsItemIdList.clear();
            //if (fieldList != null) {
            //    for (int i = 0; i < fieldList.size(); i++) {
            //        FieldItem fieldItem = fieldList.get(i);
            //        //过滤掉固定项
            //        if (StringUtils.contains("," + DEFALUT_FIELDS + ",", "," + "z81:" + fieldItem.getItemid().toUpperCase() + ",")) {
            //            continue;
            //        }
            //        //业务字典为 隐藏状态的指标不显示
            //        if (StringUtils.equalsIgnoreCase(fieldItem.getState(), "0")) {
            //            continue;
            //        }
            //        CompetitionJobsServiceImpl.columnsItemIdList.add(fieldItem.getItemid());
            //        //日期居左
            //        if (StringUtils.equalsIgnoreCase(fieldItem.getItemtype(), "D")) {
            //            exparam.put("textAlign", "left");
            //        }
            //        columnsInfo = TalentMarketsUtils.getColumnsInfo(fieldItem, ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
            //        columnsInfoArrayList.add(columnsInfo);
            //        exparam.clear();
            //    }
            //}
            TableFactoryBO tableFactoryBO = new TableFactoryBO(this.subModuleId, this.userView, this.conn);
            newColumnsList.clear();
            //获取栏目设置方案
            HashMap schemeData = tableFactoryBO.getTableLayoutConfig();
            if (schemeData != null) {
                ArrayList columns = new ArrayList();
                String mergedesc = "";
                int mergedescIndex = 0;
                int num = 0;
                Integer scheme_str = (Integer) schemeData.get("schemeId");
                int schemeId = scheme_str.intValue();
                ArrayList<ColumnConfig> columnConfigLst = tableFactoryBO.getTableColumnConfig(schemeId);
                for (int i = 0; i < columnConfigLst.size(); i++) {
                    ColumnConfig columnConfig = columnConfigLst.get(i);
                    if (StringUtils.equalsIgnoreCase("interviewarrangement", columnConfig.getItemid())) {
                        continue;
                    }

                    if (StringUtils.contains("," + DEFALUT_FIELDS.toUpperCase() + ",", "," + columnConfig.getFieldsetid().toUpperCase() + ":" + columnConfig.getItemid().toUpperCase() + ",")) {
                        continue;
                    } else {
                        FieldItem fieldItem = DataDictionary.getFieldItem(columnConfig.getItemid(), columnConfig.getFieldsetid());
                        if (fieldItem != null) {
                            ////业务字典表  新增字段可删除
                            //if(StringUtils.equalsIgnoreCase("z81",fieldItem.getFieldsetid())){
                            //    columnConfig.setIs_removable("1");
                            //}
                            columnsInfo = new ColumnsInfo(fieldItem);
                            columnsInfo.setColumnWidth(columnConfig.getDisplaywidth());
                            columnsInfo.setRemovable(true);
                            columnsInfo.setTextAlign(columnConfig.getAlign() + "");
                            if (!StringUtils.equalsIgnoreCase(fieldItem.getFieldsetid(), "z81")) {
                                columnsInfo.setEditableValidFunc("false");
                            }
                            String order = "";
                            if (StringUtils.equalsIgnoreCase(columnConfig.getIs_order(), "1")) {
                                order = "true";
                            } else {
                                order = "false";
                            }
                            columnsInfo.setSortable(Boolean.parseBoolean(order));
                            if (columnConfig.getIs_sum() == "1") {
                                columnsInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
                            } else if (columnConfig.getIs_sum() == "2") {
                                columnsInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
                            } else if (columnConfig.getIs_sum() == "3") {
                                columnsInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
                            } else if (columnConfig.getIs_sum() == "4") {
                                columnsInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
                            }
                            if ("0".equalsIgnoreCase(columnConfig.getIs_fromdict())) {
                                columnsInfo.setFromDict(Boolean.parseBoolean("false"));
                            }
                            if (columnConfig.getMergedesc() != null && columnConfig.getMergedesc().length() > 0) {
                                if (mergedesc.equalsIgnoreCase(columnConfig.getMergedesc()) && mergedescIndex == i - 1) {
                                    TalentMarketsUtils.addTopHeadList(columnsInfoArrayList, mergedesc, mergedescIndex, num, columnsInfo);
                                    num += 1;
                                    mergedescIndex = i;
                                } else {
                                    mergedesc = columnConfig.getMergedesc();
                                    mergedescIndex = i;
                                }
                            }
                            columnsInfoArrayList.add(columnsInfo);
                            newColumnsList.add(fieldItem);
                        }
                    }
                }
            }
        } catch (GeneralException ex) {
            throw new GeneralException("tm.contendPos.getColumnsError");
        }


        ////应聘人数
        //columnsInfo = new ColumnsInfo();
        //columnsInfo.setColumnId("numberOfApplicants");
        //columnsInfo.setColumnDesc(ResourceFactory.getProperty("talentmarkets.competitionJobs.numberOfApplicants"));
        //columnsInfo.setColumnType("N");
        //columnsInfo.setColumnWidth(100);
        //columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        //columnsInfo.setEditableValidFunc("false");
        //columnsInfoArrayList.add(columnsInfo);
        return columnsInfoArrayList;
    }

    /**
     * 获取gridpanel 展现的功能按钮
     *
     * @param statusFlag
     * @return 按钮集合
     */
    private ArrayList getCompetitionJobsGridButtons(String statusFlag) {
        String all = "all";
        ArrayList buttonList = new ArrayList();
        //获取是否开启面试环节配置项
        boolean openInterview = TalentMarketsUtils.getOpenInterview(this.conn);
        //功能导航集合
        ArrayList<LazyDynaBean> functionalNavigation = new ArrayList<LazyDynaBean>();

        LazyDynaBean exportExcelBean = new LazyDynaBean();
        exportExcelBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionJobs.exportExcel"));
        exportExcelBean.set("handler", "competitionJobs.exportFunc");

        //导出面试名单
        LazyDynaBean exportInterviewBean = new LazyDynaBean();
        exportInterviewBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionJobs.exportInterviewList"));
        exportInterviewBean.set("handler", "competitionJobs.exportInterviewList");

        //导入
        LazyDynaBean importExcelBean = new LazyDynaBean();
        importExcelBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionJobs.import"));
        importExcelBean.set("handler", "competitionJobs.importFunc");

        //导出excel按钮
        this.assemblyPrivButton(EXPORT_EXCEL_FUNC_ID, functionalNavigation, exportExcelBean);
        if (StringUtils.equalsIgnoreCase(all, statusFlag)) {
            if(openInterview){
                //导出面试名单
                this.assemblyPrivButton(EXPORT_INTERVIEW_LIST_FUNC_ID, functionalNavigation, exportInterviewBean);
            }
            //导入excel按钮
            this.assemblyPrivButton(IMPORT_FUNC_ID, functionalNavigation, importExcelBean);
        }
        if (functionalNavigation.size() > 0) {
            //功能导航
            buttonList.add(TalentMarketsUtils.getMenuStr(ResourceFactory.getProperty("talentmarkets.competitionJobs.functionalNavigation"), functionalNavigation));
            buttonList.add("-");
        }
        if (StringUtils.equalsIgnoreCase(all, statusFlag)) {
            //获取快速审批配置项
            boolean quickApprove = TalentMarketsUtils.getQuickApprove(this.conn);
            if(!quickApprove){
                //岗位编制
                this.assemblyPrivButton(JOB_PREPARATION_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.jobPreparation"), "competitionJobs.jobPreparationFunc"));
            }
            //新建
            this.assemblyPrivButton(CREATE_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.create"), "competitionJobs.createFunc"));
            //保存
            buttonList.add(new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.save"), "competitionJobs.saveFunc"));
            //删除
            this.assemblyPrivButton(DELETE_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.delete"), "competitionJobs.deleteFunc"));
            if(quickApprove){
                //报批
                this.assemblyPrivButton(REPORT_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.report"), "competitionJobs.reportFunc"));
                //批准
                this.assemblyPrivButton(APPROVE_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.approve"), "competitionJobs.approveFunc"));
                //退回
                this.assemblyPrivButton(REFUSE_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.refuse"), "competitionJobs.refuseFunc"));
            }
            //暂停
            this.assemblyPrivButton(SUSPEND_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.status.suspend"), "competitionJobs.suspendFunc"));
            //发布
            this.assemblyPrivButton(PUBLISH_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.publish"), "competitionJobs.publishFunc"));
            //结束
            this.assemblyPrivButton(END_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.status.end"), "competitionJobs.endFunc"));
            //公示
            this.assemblyPrivButton(PUBLICITY_FUNC_ID, buttonList, new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.publicity"), "competitionJobs.publicityFunc"));
        } else {
            buttonList.add(new ButtonInfo(ResourceFactory.getProperty("talentmarkets.competitionJobs.return"), "competitionJobs.returnHomePage"));
        }

        //快速查询面板
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("TM000000002");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        //请输入岗位名称
        querybox.setText(ResourceFactory.getProperty("talentmarkets.competitionJobs.msgPleaseInputName"));
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 获取当前登录用户有权限的功能按钮
     *
     * @param funcid     功能按钮权限号
     * @param buttonList 功能按钮权集合
     * @param button     按钮对象
     */
    private void assemblyPrivButton(String funcid, List buttonList, Object button) {
        boolean isHasButton = false;
        if (userView.isSuper_admin() || userView.hasTheFunction(funcid)) {
            isHasButton = true;
        }
        if (isHasButton) {
            buttonList.add(button);
        }
    }


    /**
     * @param status -1 全部 01 申请中 02 已发布 03 暂停 04 结束
     * @return tabledatasql
     */
    private String getTableSql(String status) {
        StringBuffer tableSqlBuffer = new StringBuffer();
        String all = "all";
        StringBuffer newFieldBuffer = new StringBuffer();
        StringBuffer subTableSqlBuffer = new StringBuffer();
        tableSqlBuffer.append("select z81.create_time,Z8101,Z8103,z81.B0110,z81.E01A1,Z8105,Z8107,Z8109,Z8111,Z8113,Z8115,z81.E0122,Z8117 ");
        ////fixme 业务字典其它列可能出错,等业务字典z81表出来 再具体测试
        //for (int i = 0; i < this.columnsItemIdList.size(); i++) {
        //    String[] temps = this.columnsItemIdList.get(i).split(":");
        //    String fieldSetId = temps[0];
        //    String itemId = temps[1];
        //    if (StringUtils.equalsIgnoreCase(fieldSetId, "z81")) {
        //        tableSqlBuffer.append(itemId);
        //        if (i < this.columnsItemIdList.size() - 1) {
        //            tableSqlBuffer.append(",");
        //        }
        //    }
        //}
        for (FieldItem fieldItem : newColumnsList) {
            newFieldBuffer.append(",").append(fieldItem.getFieldsetid()).append(".").append(fieldItem.getItemid());
            if (!StringUtils.contains(subTableSqlBuffer.toString(), fieldItem.getFieldsetid().toLowerCase())) {
                //岗位子集
                if (fieldItem.isPos()) {
                    String tablename = fieldItem.getFieldsetid().toLowerCase();
                    if (StringUtils.equalsIgnoreCase("k01", tablename)) {
                        subTableSqlBuffer.append(" left join (select a1.* from   " + tablename + " a1) " + tablename + " on " + "z81.E01A1=" + tablename + ".E01A1");
                    } else {
                        subTableSqlBuffer.append(" left join (select a1.* from   " + tablename + " a1 where  a1.i9999=(select MAX(b1.I9999) from " + tablename + " b1 where b1.E01A1=a1.E01A1)) " + tablename + " on " + "z81.E01A1=" + tablename + ".E01A1");
                    }
                }
            }
        }
        tableSqlBuffer.append(newFieldBuffer.toString());
        tableSqlBuffer.append(" from z81");
        tableSqlBuffer.append(subTableSqlBuffer.toString());
        tableSqlBuffer.append("  where 1=1");
        if (!StringUtils.equalsIgnoreCase(status, all)) {
            tableSqlBuffer.append(" and (z8103 = ");
            if (StringUtils.equalsIgnoreCase(status, "current")) {
                tableSqlBuffer.append(competitivePositionStatus.published.getValue());
                tableSqlBuffer.append(" or z8103 = ");
                tableSqlBuffer.append(competitivePositionStatus.suspend.getValue());
            } else if (StringUtils.equalsIgnoreCase(status, "history")) {
                tableSqlBuffer.append(competitivePositionStatus.end.getValue());
                tableSqlBuffer.append(" or z8103 = ");
                tableSqlBuffer.append(competitivePositionStatus.publicized.getValue());
                tableSqlBuffer.append(" or z8103 = ");
                tableSqlBuffer.append(competitivePositionStatus.publicizend.getValue());
            } else {
                if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.drafting.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.drafting.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.application.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.application.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.approved.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.approved.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.published.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.published.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.suspend.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.suspend.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.end.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.end.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.publicized.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.publicized.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.approvalFailed.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.approvalFailed.getValue());
                } else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.publicizend.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.publicizend.getValue());
                }else if (StringUtils.equalsIgnoreCase(status, competitivePositionStatus.refuse.toString())) {
                    tableSqlBuffer.append(competitivePositionStatus.refuse.getValue());
                }
            }
            tableSqlBuffer.append(" ) ");
        }
        String privSql = TalentMarketsUtils.getPrivSql("z81", "4", this.userView);
        tableSqlBuffer.append(privSql);
        return tableSqlBuffer.toString();
    }

    @Override
    public void refsTableData(String queryMethod) {
        TableDataConfigCache configCache = (TableDataConfigCache) this.userView.getHm().get(this.subModuleId);
        String sql = this.getTableSql(queryMethod);
        configCache.setTableSql(sql);
        this.userView.getHm().put(this.subModuleId, configCache);
    }

    @Override
    public String getSqlCondition(List<String> valueList) {
        StringBuffer buf = new StringBuffer();
        try {
            for (int i = 0; i < valueList.size(); i++) {
                buf.append(" and (");
                String value = SafeCode.decode(valueList.get(i).toString());
                buf.append(" lower((select organization.codeitemdesc from organization where organization.codeitemid = myGridData.E01A1 and codesetid = '@K')) like ");
                buf.append("'%" + value + "%'");
                buf.append(" )");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    @Override
    public void saveCompetitionJobsData(List<MorphDynaBean> dataList) throws GeneralException {
        List<RecordVo> valueList = new ArrayList<RecordVo>();
        try {
            for (MorphDynaBean bean : dataList) {
                Map record = PubFunc.DynaBean2Map(bean);
                Integer z8105 = (Integer) record.get("z8105");
                Integer z8107 = (Integer) record.get("z8107");

                String z8111 = (String) record.get("z8111");
                String z8113 = (String) record.get("z8113");
                if (StringUtils.isBlank(z8111)) {
                    z8111 = null;
                }
                if (StringUtils.isBlank(z8113)) {
                    z8113 = null;
                }
                String z8101 = PubFunc.decrypt((String) record.get("z8101_e"));
                String z8115 = ((String) record.get("z8115")).split("`")[0];
                RecordVo recordVo = new RecordVo("Z81");
                recordVo.setString("z8101", z8101);
                recordVo = dao.findByPrimaryKey(recordVo);
                if (z8105 != null) {
                    recordVo.setObject("z8105", z8105.intValue());
                } else {
                    recordVo.setObject("z8105", 0);
                }
                if (z8107 != null) {
                    recordVo.setObject("z8107", z8107.intValue());
                } else {
                    recordVo.setObject("z8107", 0);
                }
                recordVo.setDate("z8111", PubFunc.DateStringChangeValue(z8111));
                recordVo.setDate("z8113", PubFunc.DateStringChangeValue(z8113));
                if (StringUtils.isNotEmpty(z8115)) {
                    if (!(z8115.endsWith(",") && z8115.startsWith(","))) {
                        z8115 = "," + z8115 + ",";
                    }
                }
                recordVo.setObject("z8115", z8115);
                for (FieldItem fieldItem : CompetitionJobsServiceImpl.newColumnsList) {
                    if (!StringUtils.equalsIgnoreCase(fieldItem.getFieldsetid(), "z81")) {
                        continue;
                    }
                    //加此判断 是因为业务字典表新增的数值型字段如果值没有 tablebuilder组件获取数据时 会将此字段给过滤掉。
                    if (record.containsKey(fieldItem.getItemid())) {
                        Object value = record.get(fieldItem.getItemid());
                        //代码型指标值需要做特殊处理
                        if (fieldItem != null) {
                            String itemType = fieldItem.getItemtype();
                            String codeSetId = fieldItem.getCodesetid();
                            if (StringUtils.equalsIgnoreCase(itemType, "A") && !StringUtils.equalsIgnoreCase(codeSetId, "0")) {
                                value = ((String) value).split("`")[0];
                            } else if (StringUtils.equalsIgnoreCase(itemType, "D")) {
                                value = PubFunc.DateStringChangeValue((String) value);
                            }
                        }
                        recordVo.setObject(fieldItem.getItemid(), value);
                    }
                }
                valueList.add(recordVo);
            }
            dao.updateValueObject(valueList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPos.msg.saveFail");
        }
    }

    @Override
    public void changeState(String status, String ids, String notice_time) throws GeneralException {
        List<RecordVo> valueList = new ArrayList<RecordVo>();
        RowSet rowSet = null;
        try {

            String[] idArray = ids.split(",");
            String publicityStartDate = null;
            String publicityEndDate = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            if (StringUtils.isNotEmpty(notice_time)) {
                publicityStartDate = simpleDateFormat.format(new Date());
                Calendar calen = Calendar.getInstance();
                calen.add(Calendar.DATE, Integer.parseInt(notice_time));
                Date endDate = calen.getTime();
                publicityEndDate = simpleDateFormat1.format(endDate) + "  23:59:59";
            }
            for (String id : idArray) {
                id = PubFunc.decrypt(id);
                RecordVo recordVo = new RecordVo("z81");
                recordVo.setObject("z8101", id);
                recordVo.setObject("z8103", status);
                if (StringUtils.isNotEmpty(publicityStartDate) && StringUtils.isNotEmpty(publicityEndDate)) {
                    recordVo.setDate("pub_startdate", publicityStartDate);
                    recordVo.setDate("pub_enddate", publicityEndDate);
                }
                valueList.add(recordVo);
            }
            dao.updateValueObject(valueList);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "tm.contendPos.msg.changeStateFail";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        } finally {
            PubFunc.closeResource(rowSet);
        }

    }

    @Override
    public String deleteCompetitionJobsData(String ids, String isConfim) throws GeneralException {
        RowSet rowSet = null;
        String isNeedConfim = "0";
        try {
            String[] idArray = ids.split(",");
            StringBuffer delSql = new StringBuffer();
            delSql.append("DELETE FROM Z81 where z8101 in ( ");
            StringBuffer delSubSql = new StringBuffer();
            delSubSql.append("delete from z83  where z8101 in (");
            StringBuffer searchSql = new StringBuffer();
            searchSql.append("SELECT Z8101 FROM Z83 WHERE Z8101 IN (");
            StringBuffer delInterviewSql = new StringBuffer();
            delInterviewSql.append("DELETE FROM JP_INTERVIEWER where z8101 in ( ");
            List<String> paramList = new ArrayList<String>();
            for (String id : idArray) {
                id = PubFunc.decrypt(id);
                delSql.append("?").append(",");
                searchSql.append("?").append(",");
                delSubSql.append("?").append(",");
                delInterviewSql.append("?").append(",");
                paramList.add(id);
            }
            delSql.setLength(delSql.length() - 1);
            searchSql.setLength(searchSql.length() - 1);
            delSubSql.setLength(delSubSql.length() - 1);
            delInterviewSql.setLength(delInterviewSql.length() - 1);
            delSql.append(")");
            searchSql.append(")");
            delSubSql.append(")");
            delInterviewSql.append(")");
            String needConfim = "0";
            String notNeedConfim = "1";
            //说明要删除除的岗位下有竞聘人员数据
            if (StringUtils.equalsIgnoreCase(isConfim, needConfim)) {
                rowSet = dao.search(searchSql.toString(), paramList);
                if (rowSet.next()) {
                    isNeedConfim = "1";
                } else {
                    dao.delete(delSql.toString(), paramList);
                    dao.delete(delInterviewSql.toString(), paramList);
                }
            } else if (StringUtils.equalsIgnoreCase(isConfim, notNeedConfim)) {
                dao.delete(delSubSql.toString(), paramList);
                dao.delete(delSql.toString(), paramList);
                dao.delete(delInterviewSql.toString(), paramList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPos.msg.delRecordSql");
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return isNeedConfim;
    }

    @Override
    public Map getNoticeData() throws GeneralException {
        Map<String, String> noticeData = new HashMap<String, String>();
        List<ColumnsInfo> columnsInfoList = this.getCompetitionJobsGridColumns();
        String alternativeItems = this.getNoticeField(columnsInfoList);
        String groupItems = this.getGroupField();
        noticeData.put("alternativeItems", alternativeItems);
        noticeData.put("groupItems", groupItems);
        return noticeData;
    }

    @Override
    public List getPublicityPeopleData(String ids) throws GeneralException {
        RowSet rowset = null;
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        String[] idArray = ids.split(",");
        StringBuffer sqlBuffer = new StringBuffer();
        List<String> paramList = new ArrayList<String>();
        StringBuffer checkSql = new StringBuffer();
        ArrayList checkList = new ArrayList();
        try {
            sqlBuffer.append("SELECT A0100,A0101,Z8305,B0110,Z8307,(select create_time from z81 where z81.z8101 = z83.z8101) postCreateTime");
            sqlBuffer.append(" FROM Z83 INNER JOIN USRA01 a01 ON A01.guidkey = Z83.z8301 WHERE Z8101 IN (");
            checkSql.append("select Z8101,z8303 from z83 where Z8101 in (");
            for (String ide : idArray) {
                String id = PubFunc.decrypt(ide);
                sqlBuffer.append("?").append(",");
                paramList.add(id);
                checkSql.append("?").append(",");
                checkList.add(id);
            }
            checkSql.setLength(checkSql.length() - 1);
            checkSql.append(") and z8303 in ('07')");
            rowset = dao.search(checkSql.toString(), checkList);
            //检验竞聘竞聘岗位下是否存在录用审批中的人员，若存在则不允许公示
            if (rowset.next()) {
                throw new GeneralException("tm.contendPos.msg.ExistInApproval");
            }
            sqlBuffer.setLength(sqlBuffer.length() - 1);
            sqlBuffer.append(")");
            sqlBuffer.append(" and z8303 = 08");
            sqlBuffer.append(" order by postCreateTime desc,z8305 desc,create_time desc");
            rowset = dao.search(sqlBuffer.toString(), paramList);
            while (rowset.next()) {
                Map<String, String> record = new HashMap<String, String>();
                String a0100 = rowset.getString("a0100");
                String a0101 = rowset.getString("a0101");
                String z8305 = rowset.getString("z8305");
                String postE0122 = TalentMarketsUtils.getOrgItemid(z8305,dao,"UM");
                String postE0122Desc = AdminCode.getCodeName("UM",postE0122);
                String z8305Desc = AdminCode.getCodeName("@K", z8305);
                String b0110 = rowset.getString("b0110");
                String b0110Desc = AdminCode.getCodeName("UN", b0110);
                String z8307 = rowset.getString("z8307");
                record.put("A0100", a0100);
                record.put("A0101", a0101);
                record.put("Z8305", z8305 + "`" + z8305Desc);
                record.put("B0110", b0110 + "`" + b0110Desc);
                record.put("Z8307", z8307);
                record.put("postE0122", postE0122+"`"+postE0122Desc);
                dataList.add(record);

            }
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "tm.contendPos.msg.publicitySql";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        } finally {
            PubFunc.closeResource(rowset);
        }
        return dataList;
    }

    /**
     * 获取信息公示备用指标（为公示选择指标组件用 暂时不用公示指标组件）
     *
     * @param columns
     * @return
     */
    private String getNoticeField(List<ColumnsInfo> columns) {
        //String defaultItem = "Z8305,A0100,A0101,B0110,Z8307,";
        ArrayList exceptItems = new ArrayList();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dataValue", "Z8305");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.competitivePosition"));
        map.put("selected", "1");
        exceptItems.add(map);

        map = new HashMap<String, String>();
        map.put("dataValue", "postE0122");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.competitivePositionE0122"));
        map.put("selected", "1");
        exceptItems.add(map);

        map = new HashMap<String, String>();
        map.put("dataValue", "A0100");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.employeeID"));
        map.put("selected", "1");
        exceptItems.add(map);

        map = new HashMap<String, String>();
        map.put("dataValue", "A0101");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.proposedHire"));
        map.put("selected", "1");
        exceptItems.add(map);

        map = new HashMap<String, String>();
        map.put("dataValue", "B0110");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.currentUnit"));
        map.put("selected", "1");
        exceptItems.add(map);

        map = new HashMap<String, String>();
        map.put("dataValue", "Z8307");
        map.put("dataName", ResourceFactory.getProperty("talentmarkets.competitionJobs.competitiveScore"));
        map.put("selected", "1");
        //获取面试安排配置项
        boolean openInterview = TalentMarketsUtils.getOpenInterview(this.conn);
        //如果开启面试安排 则添加竞聘成绩
        if (openInterview) {
            exceptItems.add(map);
        }
        return JSON.toString(exceptItems);
    }

    /**
     * 信息公示分组指标（为公示选择指标组件用 暂时不用公示指标组件）
     *
     * @return
     */
    private String getGroupField() {
        ArrayList list = new ArrayList();
        String[] items = new String[]{"E01A1"};
        FieldItem fieldItem = null;
        for (String item : items) {
            fieldItem = DataDictionary.getFieldItem(item);
            if (StringUtils.equalsIgnoreCase(item, "E01A1")) {
                CommonData data = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
                list.add(data);
            }
            if (fieldItem != null && "1".equals(fieldItem.getUseflag())) {
                CommonData data = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
                list.add(data);
            }
        }
        return JSONArray.fromObject(list).toString();
    }


    /**
     * 导入职位-下载模板
     *
     * @throws GeneralException
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-13 11:44
     */
    @Override
    public String downloadTemplate() throws GeneralException {
        String fileName = "";
        Map<String, String> map = new HashMap<String, String>();
        //封装所需指标
        List<FieldItem> fieldItemList = this.getTemplateFields();
        // 创建新的Excel 工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        fileName = this.createSheet(CompetitionService.exportExcelType.scope.getValue(), fieldItemList, wb);
        FileOutputStream fileOut = null;
        try {
            String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
            fileOut = new FileOutputStream(filePath);
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(wb);
        	PubFunc.closeResource(fileOut);
        }
        return fileName;
    }

    /**
     * 通过模板导入职位
     * @param fileId 文件的加密id
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-14 13:59
     */
    @Override
    public String importData(String fileId) throws GeneralException {
        //存放错误信息
        ArrayList<Object> msg = new ArrayList<Object>();
        ArrayList<Object> msgList = new ArrayList<Object>();
        Sheet sheet = this.getSheet(fileId);
        Row headRow = sheet.getRow(0); // 获取表头
        if (headRow == null) {
            throw new GeneralException("请用导出的Excel模板来导入数据");
        }
        // 存放支持修改的字段
        Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            int headCols = headRow.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            if (headCols >= 1 && rows >= 1) {
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
                            default:
                        }

                        comment = cell.getCellComment();
                        // 表头存在，批注为空
                        if (comment == null) {
                            throw new GeneralException("请用导出的Excel模板来导入数据");
                        }

                        //拿到标注
                        field = comment.getString().toString().trim();

                        /*if ("Z8301".equalsIgnoreCase(field)) {
                            map.put(c, "Z8301");
                            continue;
                        }*/

                        //记录要要改的字段的位置
                        if (!dWizard.isExistField("z83", field.toUpperCase(), false)
                                && !dWizard.isExistField("z81", field.toUpperCase(), false)
                                && !dWizard.isExistField("usra01", field.toUpperCase(), false)) {
                            throw new GeneralException("导入的Excel中“" + title + "”这个指标在竞聘人员表里面不存在或未构库，请用导出的Excel模板来导入数据！");
                        }
                        map.put(c, field);
                    }
                }
                boolean isOwnExcel = false;
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    String itemId = entry.getValue();
                    if ("Z8105".equalsIgnoreCase(itemId)) {
                        isOwnExcel = true;
                        break;
                    }
                }
                if (!isOwnExcel) {
                    throw new GeneralException("请用导出的Excel模板来导入数据");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = "";
            if (e instanceof GeneralException) {
                error = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(error);
        }
        ArrayList<Object> info = this.getExcelInfo(sheet, map);
        return exportErrorLog(info);
    }

    /**
     * 获取竞聘范围机构信息
     *
     * @param codeItemId
     * @return
     */
    public List getCompetitiveScopeOrgData(String codeItemId) {
        ArrayList orgData = new ArrayList();
        ArrayList param = new ArrayList();
        param.add(codeItemId);
        String sql = "select codesetid,codeitemdesc from organization where codeitemid = ?";
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(sql, param);
            while (rowSet.next()) {
                HashMap data = new HashMap();
                data.put("orgpre", rowSet.getString("codesetid"));
                data.put("name", rowSet.getString("codeitemdesc"));
                orgData.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orgData;
    }

    private ArrayList<Object> getExcelInfo(Sheet sheet, Map<Integer, String> map) {
        //返回信息
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        StringBuffer sql = new StringBuffer();
        //组装插入的sql语句
        sql.append("INSERT Into z81 (Z8101");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            sql.append(", ").append(entry.getValue());
            indexs.add(entry.getKey());
        }
        sql.append(", Z8103 ,B0110,E0122,create_time) VALUES ( ?");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            sql.append(" , ?");
        }
        sql.append(" , '01' , ? , ?,?) ");
        Row row = null;
        Cell cell = null;
        // 存放Excel表中数据
        ArrayList<Object> rowList = null;
        ArrayList valueLists = new ArrayList<ArrayList<Object>>();
        // 存放Excel表中职位名称的数据
        ArrayList z0301Lists = new ArrayList<ArrayList<Object>>();
        // 存放Excel表中错误数据的行数
        ArrayList deletelist = new ArrayList<ArrayList<Object>>();
        //存放错误信息
        ArrayList<Object> msg = new ArrayList<Object>();
        ArrayList<Object> msglist = new ArrayList<Object>();
//        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            Date now = new Date();
            DateFormat Time = new SimpleDateFormat("yyyy-MM-dd");
            String nowTime = Time.format(now);
            //用于计数
            int count = 0;
            String jobTitles = "";
            String startTime = "";
            String endTime = "";
            //起始日期字段名
            String startTimeName = "";
            //用于判断此表格内岗位是否重复的list
            List<String> e01a1List = new ArrayList();
            //终止日期字段名
            String endTimeName = "";
            ArrayList<String> jobTitlesList = new ArrayList<String>();
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            //判断登录用户的招聘渠道的权限
            RecruitPrivBo rpbo = new RecruitPrivBo();
//            HashMap<String, Object> parame = rpbo.getChannelPrivMap(userView, conn);
            //excel每行数据遍历
            for (int i = 1; i < numberOfRows; i++) {
                String tmp = "";
                String jobTitle = null;
                String rowE01A1 = "";
                String rowB0110 = "";
                String rowE0122 = "";
                row = sheet.getRow(i);
                rowList = new ArrayList<Object>();
                //把每一行中要修改的数据添加到list中
                if (row == null) {
                    continue;
                }

                boolean as = isRowEmpty(row);
                if (as) {
                    continue;
                }
                String unit = "";
                String division = "";
                boolean tmpFlag = true;
                //生成Z81主键Z8101
                IDGenerator ids = new IDGenerator(2, this.conn);
                String z8101 = ids.getId("z81.z8101");
                rowList.add(z8101);
                //单行数据每列遍历
                for (Integer c : indexs) {
                    Object value = null;
                    cell = row.getCell(c);
                    //拿到标注
                    String field = map.get(c);
                    FieldItem item = DataDictionary.getFieldItem(field);
                    String itemId = item.getItemid();
                    /*if (!"Z8301".equals(itemId.toUpperCase())
                            && !"Z8307".equals(itemId.toUpperCase())
                            && !"Z8305".equals(itemId.toUpperCase())) {
                        continue;
                    }*/
                    //判断该字段在业务字典和页面业务中是否设置为必填项了
                    boolean required = this.getRequired(item);
                    if (cell != null) {
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }

                        String cellValue = cell.getStringCellValue();
                        if (required && StringUtils.isEmpty(cellValue)) {
                            userView.getHm().put("valueLists", valueLists);
                            valueLists.clear();
                            throw new GeneralException("导入的Excel中[" + item.getItemdesc() + "]列必填项中有数据为空！请填写完整再导入");
                        }

                        if (StringUtils.isNotEmpty(cellValue)) {
                            //判断成绩的数据类型代码类
                            String codeSql = "";
                            //判断数据类型
                            //代码型
                            if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype())) {
                                String tempCellValue = cell.getStringCellValue().trim();
                                if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
                                    int begin = cellValue.indexOf("(");
                                    int last = cellValue.indexOf(")");
                                    String cellValueId = StringUtils.trim(cellValue.substring(begin + 1, last));
                                    cellValue = StringUtils.trim(cellValue.substring(0, begin));
                                    String valueId = (String) this.getUnitId(cellValueId);
                                    if (StringUtils.isNotEmpty(valueId) && valueId.equalsIgnoreCase(cellValue)) { //直接输入机构名称 并且机构名称存在  ,将  机构名称  转为  机构 编码
                                        value = cellValueId; //获取 机构  id
                                        if ("E01A1".equalsIgnoreCase(itemId)) {
                                            rowE01A1 = (String) value;
                                            String selectCodesetid = this.getCodeSetId(rowE01A1);
                                            //说明此人没有此岗位权限 跳过
                                            if (!isHaveE01a1(rowE01A1)) {
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行岗位您没有权限导入。");
                                                tmpFlag = false;
                                                break;
                                            }
                                            if (!"@k".equalsIgnoreCase(selectCodesetid)) {
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行[岗位名称]列，已选择项不是岗位。");
                                                tmpFlag = false;
                                                continue;
                                            }
                                            //判断此岗位是否已经存在于Z81
                                            boolean e01a1Flag = this.isExistE01A1(rowE01A1);
                                            if (e01a1Flag) {
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行[岗位名称]列单元格中的岗位在竞聘岗位表中已经存在！");
                                                tmpFlag = false;
                                                continue;
                                            }
                                            if (e01a1List.indexOf(rowE01A1) != -1) {
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行[岗位名称]列单元格中的岗位在此Excel中重复");
                                                tmpFlag = false;
                                                continue;
                                            }
                                            e01a1List.add(rowE01A1);
                                        }
                                        //if ("E0122".equalsIgnoreCase(itemId)) {
                                        //    String selectCodesetid = this.getCodeSetId((String) value);
                                        //    if (!"UM".equalsIgnoreCase(selectCodesetid)) {
                                        //        int number = msg.size();
                                        //        msg.add((number + 1) + ". 第" + (i + 1) + "行[部门]列，已选择项不是部门。");
                                        //        tmpFlag = false;
                                        //        continue;
                                        //    }
                                        //}
                                        //if ("UM".equalsIgnoreCase(item.getCodesetid())) {
                                        //    unit = (String) cellValueId;
                                        //}
                                        //
                                        //if ("@K".equalsIgnoreCase(item.getCodesetid())) {
                                        //    division = (String) cellValueId;
                                        //}

                                    } else {//走到这里说明值 是空，并且 机构树上也没有选择机构
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中的没有对应的组织机构！");
                                        tmpFlag = false;
                                        continue;
                                    }
                                }
                            }
                            //数值型
                            else if ("N".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                if (cell.getStringCellValue() != null) {
                                    value = cell.getStringCellValue().trim();
                                    int fieldLength = item.getItemlength();
                                    String values = (String) value;
                                    int valueLength = values.length();
                                    PubFunc.doStringLength(values, fieldLength);
                                    if (StringUtils.contains(values, ".")) {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列不能为小数！");
                                        continue;
                                    }
                                    if (StringUtils.contains(values, "-")) {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列不能为负数！");
                                        continue;
                                    }
                                    if (valueLength > fieldLength) {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中值的长度超过指标长度！");
                                        continue;
                                    }
                                    if (StringUtils.isNotBlank((String) value)) {
                                        try {
                                            value = Float.parseFloat((String) value);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格中值不能为非数字！");
                                            continue;
                                        }
                                    } else {
                                        //数值型默认为0
                                        value = 0;
                                    }
                                }
                            } else if ("D".equals(item.getItemtype())) {
                                try {
                                    value = cell.getStringCellValue().trim();
                                    if (!"".equals(value)) {
                                        String valueas = (String) value;
                                        tmp = this.checkdate(valueas);
                                        if ("false".equals(tmp)) {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
//                                            tmpFlag = false;
                                            continue;
                                        } else {
                                            value = tmp;
                                        }
                                    }
                                    //开始时间
                                    if ("Z8111".equalsIgnoreCase(item.getItemid())) {
                                        startTimeName = item.getItemdesc();
                                        startTime = ((String) value).replace("-", "");
                                        String z8111Format = this.getDateFormat("Z8111");
                                        String dateFormat = this.getDateValueFormat((String) value);
                                        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                                        SimpleDateFormat z8111df = new SimpleDateFormat(z8111Format);
                                        Date date = df.parse((String) value);
                                        Timestamp dateValue = new Timestamp(z8111df.parse(z8111df.format(date)).getTime());
                                        value = dateValue;
                                    }
                                    //结束时间
                                    if ("Z8113".equalsIgnoreCase(item.getItemid())) {
                                        endTimeName = item.getItemdesc();
                                        endTime = ((String) value).replace("-", "");
                                        String numberTime = ((String) nowTime).replace("-", "");
                                        int result = numberTime.compareTo(endTime);
                                        if (result > 0) {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + item.getItemdesc() + "]不能早于今天！");
                                            tmpFlag = false;
                                            continue;
                                        }

                                        String z8113Format = this.getDateFormat("Z8113");
                                        String dateFormat = this.getDateValueFormat((String) value);
                                        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                                        SimpleDateFormat z8113df = new SimpleDateFormat(z8113Format);
                                        Date date = df.parse((String) value);
                                        Timestamp dateValue = new Timestamp(z8113df.parse(z8113df.format(date)).getTime());
                                        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
                                        if (dateValue.before(nowTimestamp)) {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]不能早于今天！");
                                        }
                                        value = dateValue;
                                    }
                                } catch (Exception e) {
                                    int number = msg.size();
                                    msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getStringCellValue());
                                    break;
                                }

                            } else if ("A".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                if (cell.getStringCellValue() != null) {
                                    value = cell.getStringCellValue().trim();
                                    int fieldLength = item.getItemlength();
                                    String values = (String) value;
                                    int valueLength = values.length();
                                    PubFunc.doStringLength(values, fieldLength);

                                    if (valueLength > fieldLength) {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度！");
                                        continue;
                                    }
                                    value = StringUtils.isEmpty(cell.getStringCellValue()) ? null : cell.getStringCellValue().trim();
                                }
                            } else if ("M".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                value = StringUtils.isEmpty(cell.getStringCellValue()) ? null : cell.getStringCellValue();
                            } else {
                                value = StringUtils.isEmpty(cell.getStringCellValue()) ? null : cell.getStringCellValue().trim();
                            }
                        } else {
                           /* if (StringUtils.equalsIgnoreCase("E0122", itemId)) {
                                int number = msg.size();
                                msg.add((number + 1) + ". 第" + (i + 1) + "行的[部门]不能为空， 请修改。");
                                continue;
                            } else */if (StringUtils.equalsIgnoreCase("E01A1", itemId)) {
                                int number = msg.size();
                                msg.add((number + 1) + ". 第" + (i + 1) + "行的[岗位名称]不能为空， 请修改。");
                                continue;
                            }
                            value = null;
                        }
                    } else {
                        value = null;
                    }
                    rowList.add(value);
                }
                if (tmpFlag == false) {
                    continue;
                }
                //if (StringUtils.isNotEmpty(unit) && StringUtils.isNotEmpty(division) && !division.startsWith(unit)) {
                //    int number = msg.size();
                //    msg.add((number + 1) + ". 第" + (i + 1) + "行的部门和岗位不匹配， 请修改。");
                //    continue;
                //}
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    int result = startTime.compareTo(endTime);
                    if (result > 0) {
                        int number = msg.size();
                        msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + startTimeName + "]晚于[" + endTimeName + "]， 数据错误， 请修改。");
                        continue;
                    }
                }
                CodeItem e01a1Item = AdminCode.getCode("@K",rowE01A1);
                String e01a1ParentCode = "";
                if(e01a1Item != null){
                    e01a1ParentCode = e01a1Item.getPcodeitem();
                    CodeItem e0122Item = AdminCode.getCode("UM",e01a1ParentCode);
                    if(e0122Item != null){
                        rowE0122 = e01a1ParentCode;
                        rowB0110 = TalentMarketsUtils.getB0100(rowE0122);
                    }else{
                        CodeItem b0110Item = AdminCode.getCode("UN",e01a1ParentCode);
                        if(b0110Item != null){
                            rowB0110 = e01a1ParentCode;
                        }
                    }
                }
                rowList.add(rowB0110);
                rowList.add(rowE0122);
                //添加创建时间
                Timestamp createTime = new Timestamp(System.currentTimeMillis());
                rowList.add(createTime);
                valueLists.add(rowList);
/*                jobTitleCell = row.getCell(jobTitleIndex);
                if (jobTitleCell != null) {
                    jobTitleCell.setCellType(Cell.CELL_TYPE_STRING);
                    jobTitle = row.getCell(jobTitleIndex).getStringCellValue();
                    if ("".equalsIgnoreCase(jobTitle)) {
                        boolean isEmpty = true;
                        for (int z = 0; z < rowList.size(); z++) {
                            if (!"".equals(rowList.get(z)) && rowList.get(z) != null) {
                                isEmpty = false;
                            }
                        }
                        if (!isEmpty) {
                            int number = msg.size();
                            FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");
                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]的值为空，不予导入。");
                            continue;
                        }

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

                        if (valueLists.size() != 0) {
                            for (int j = 0; j < jobTitleList.size(); j++) {
                                ArrayList singleList = new ArrayList<Object>();
                                for (int y = 0; y < ((ArrayList) valueLists.get(j)).size() - 2; y++) {
                                    singleList.add(((ArrayList) valueLists.get(j)).get(y));
                                }

                                if (!jobTitleList.get(j).equalsIgnoreCase(jobTitle) || "".equalsIgnoreCase(jobTitle) || "false".equals(tmp)) {
                                    continue;
                                }

                                boolean repeat = !rowList.retainAll(singleList);

                                if (repeat) {
                                    notImport = true;
                                    int number = msg.size();
                                    FieldItem item = (FieldItem) DataDictionary.getFieldItem("Z0351");
                                    msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]的值：" + jobTitle + "，在导入的Excel中有完全重复的数据， 都不予导入");
                                    if (!deletelist.contains(j)) {
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
                        valueLists.add(rowList);
                    }
                }*/
                //记录导入信息
                if (StringUtils.isNotEmpty(jobTitle)) {
                    if (count == 0) {
                        jobTitles = "'" + jobTitle + "'";
                    } else {
                        jobTitles += ",'" + jobTitle + "'";
                    }
                    count++;
                }
            }

            if (msg.size() > 0) {
                userView.getHm().put("ImportPositionSql", sql.toString());
                userView.getHm().put("valueLists", valueLists);
                userView.getHm().put("z0301Lists", z0301Lists);
                for (int b = 0; b < deletelist.size(); b++) {
                    int number = (Integer) deletelist.get(b);
                    valueLists.remove(number);
                }
                msglist.add(msg);

            } else {
                if (StringUtils.isNotEmpty(jobTitles)) {
                    jobTitlesList.add(jobTitles);
                }
                String message = this.importExcel(sql.toString(), valueLists);
                msglist.add(msg);
                msglist.add(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return msglist;
    }
    /**
     * 获取组织机构类型
     *
     * @param rowE01A1: 机构编码
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-22 17:41
     */
    private String getCodeSetId(String rowE01A1) {
        List<String> list = new ArrayList<String>();
        list.add(rowE01A1);
        String codeSetId = "";
        RowSet rs = null;
        try {
            rs = dao.search("SELECT codesetid FROM organization WHERE codeitemid=?", list);
            if (rs.next()) {
                codeSetId = rs.getString("codesetid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeSetId;
    }

    /**
     * 判断此竞聘岗位是否已在Z81中存在
     *
     * @param rowE01A1:
     * @author: caoqy
     * @return: boolean
     * @date: 2019-8-21 14:39
     */
    private boolean isExistE01A1(String rowE01A1) {
        List<String> list = new ArrayList<String>();
        list.add(rowE01A1);
        RowSet rs = null;
        boolean flag = false;
        try {
            rs = this.dao.search("select count(*) as num from z81 where e01a1=? and (z8103='01' or z8103='02' or z8103='03' or z8103='04' or z8103='05' or z8103 ='10')", list);
            if (rs.next()) {
                int num = rs.getInt("num");
                if (num >= 1) {
                    flag = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return flag;
    }

    /**
     * 获取直接上级单位
     *
     * @param rowE01A1:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-20 12:01
     */
    private String getB0110(String rowE01A1) {
        String codeSetId = "";
        String codeItemId = "";
        RowSet rs = null;
        List list = new ArrayList();
        try {
            list.add(rowE01A1);
            rs = dao.search("SELECT codesetid,codeitemid FROM organization WHERE codeitemid=(SELECT parentid FROM organization WHERE codeitemid=?)", list);
            if (rs.next()) {
                codeSetId = rs.getString("codesetid");
                codeItemId = rs.getString("codeitemid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        if ("UN".equalsIgnoreCase(codeSetId)) {
            return codeItemId;
        } else {
            return this.getB0110(codeItemId);
        }
    }

    /**
     * 根据导入日期字符串长度获取对应的日期格式
     *
     * @param dateValue:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-14 18:09
     */
    public String getDateValueFormat(String dateValue) {
        int itemLength = dateValue.length();
        String dateFormat = "yyyy-MM-dd";
        if (itemLength == 4) {
            dateFormat = "yyyy";
        } else if (itemLength == 7) {
            dateFormat = "yyyy-MM";
        } else if (itemLength == 10) {
            dateFormat = "yyyy-MM-dd";
        } else if (itemLength == 16) {
            dateFormat = "yyyy-MM-dd HH:mm";
        } else if (itemLength >= 18) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        return dateFormat;
    }

    /**
     * 根据字段长度获取对应的日期格式
     *
     * @param itemId:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-14 16:56
     */
    public String getDateFormat(String itemId) {
        FieldItem dateItem = DataDictionary.getFieldItem(itemId);
        int itemLength = dateItem.getItemlength();
        String dateFormat = "yyyy-MM-dd";
        if (itemLength == 4) {
            dateFormat = "yyyy";
        } else if (itemLength == 7) {
            dateFormat = "yyyy-MM";
        } else if (itemLength == 10) {
            dateFormat = "yyyy-MM-dd";
        } else if (itemLength == 16) {
            dateFormat = "yyyy-MM-dd HH:mm";
        } else if (itemLength >= 18) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        return dateFormat;
    }

    /**
     * 导入excel数据
     *
     * @param sql:
     * @param valueLists:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-7 15:20
     */
    private String importExcel(String sql, ArrayList valueLists) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.batchInsert(sql, valueLists);
            return "导入完成，导入" + valueLists.size() + "条数据";
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("导入出错");
        }
    }

    /**
     * 校验日期字符串
     *
     * @param str: 日期字符串
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-7 15:18
     */
    private String checkdate(String str) {
        str = StringUtils.isEmpty(str) ? "" : str.replace("/", "-");
        if (str.indexOf("日") > -1) {
            str = str.replace(" ", "");
        }

        String dateStr = "false";
        if (str.length() < 4) {
            dateStr = "false";
        } else if (str.length() == 4) {
            String patternStr = "^(\\d{4})$";
            Pattern p = Pattern.compile(patternStr);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                dateStr = str + "-01-01";
            } else {
                dateStr = "false";
            }
        } else if (str.length() < 6) {
            String pStr = "^(\\d{4})年$";
            Pattern p = Pattern.compile(pStr);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                dateStr = str.replace("年", "-") + "01-01";
            } else {
                dateStr = "false";
            }
        } else if (str.length() == 7) {
            if (str.indexOf("月") != -1) {
                String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$";
                Pattern p = Pattern.compile(pStr);
                Matcher m = p.matcher(str);
                if (m.matches()) {
                    if (str.indexOf("月") != -1) {
                        dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
                    } else {
                        dateStr = str.replace("年", "-").replace(".", "-") + "-01";
                    }
                } else {
                    dateStr = "false";
                }
            } else {
                String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$";
                Pattern p = Pattern.compile(pStr);
                Matcher m = p.matcher(str);
                if (m.matches()) {
                    dateStr = str.replace("年", "-").replace(".", "-") + "-01";
                } else {
                    dateStr = "false";
                }
            }
        } else if (str.length() < 8) {
            String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$";
            Pattern p = Pattern.compile(pStr);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                if (str.indexOf("月") != -1) {
                    dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
                } else {
                    dateStr = str.replace("年", "-").replace(".", "-") + "-01";
                }
            } else {
                dateStr = "false";
            }
        } else if (str.length() == 8) { //2010年3  2010年3月1
            String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$";
            Pattern p = Pattern.compile(pStr);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                str = str.replace("年", "-").replace(".", "-").replace("月", "-");
                if (str.lastIndexOf("-") == str.length()) {
                    if (str.length() < 10) {
                        dateStr = str + "01";
                    }
                } else {
                    String[] temps = str.split("-");
                    if (temps.length > 2) {
                        dateStr = checkMothAndDay(str);
                    } else {
                        dateStr = "false";
                    }
                }
            } else {
                dateStr = "false";
            }
        } else if (str.length() <= 11) { //2017年1月1日
            String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$";
            Pattern p = Pattern.compile(pStr);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                String temp = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
                dateStr = checkMothAndDay(temp);
            } else {
                dateStr = "false";
            }

        } else { //2017年1月1日1时1分      2017年1月1日1时1分1秒
            str = str.replace("时", ":").replace("分", ":");
            if (str.endsWith(":")) {
                str = str.substring(0, str.length() - 1);
            }

            Pattern p = null;
            if (str.split(":").length < 3) {
                String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]*$";
                p = Pattern.compile(pStr);
            } else {
                String pStr = "^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]([0-5]*\\d{1})[秒]*$";
                p = Pattern.compile(pStr);
            }

            Matcher m = p.matcher(str);
            if (m.matches()) {
                String tempDate = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", " ");
                String temp = tempDate.split(" ")[0];
                dateStr = checkMothAndDay(temp);
                if (!"false".equalsIgnoreCase(dateStr)) {
                    String tempTime = tempDate.split(" ")[1];
                    dateStr += " " + tempTime;
                }
            } else {
                dateStr = "false";
            }
        }

        if (!"false".equals(dateStr)) {
            dateStr = formatDate(dateStr);
        }

        return dateStr;
    }

    /**
     * 格式化日期
     *
     * @param date:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-7 15:19
     */
    private String formatDate(String date) {
        String newDate = "";
        String[] dates = date.split(" ");
        String year = dates[0].split("-")[0];
        String month = dates[0].split("-")[1];
        month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month : month;
        String day = dates[0].split("-")[2];
        day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day : day;
        newDate = year + "-" + month + "-" + day;

        if (dates.length == 2) {
            String[] oldTime = dates[1].split(":");
            String hour = oldTime[0];
            hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour : hour;
            newDate += " " + hour;
            if (oldTime.length > 1) {
                String min = oldTime[1];
                min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min : min;
                newDate += ":" + min;
            }

            if (oldTime.length > 2) {
                String second = oldTime[2];
                second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second : second;
                newDate += ":" + second;
            }
        }
        return newDate;
    }

    /**
     * 检查月/日
     *
     * @param date:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-7 16:17
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
                case 12: {
                    if (1 <= day && day <= 31) {
                        tempDate = date;
                    }

                    break;
                }
                case 4:
                case 6:
                case 9:
                case 11: {
                    if (1 <= day && day <= 30) {
                        tempDate = date;
                    }

                    break;
                }
                case 2: {
                    if (isLeapYear(year)) {
                        if (1 <= day && day <= 29) {
                            tempDate = date;
                        }

                    } else {
                        if (1 <= day && day <= 28) {
                            tempDate = date;
                        }
                    }
                    break;
                }
                default:
            }
        }
        return tempDate;
    }

    /**
     * 是否闰年
     *
     * @param year:
     * @author: caoqy
     * @return: boolean
     * @date: 2019-8-7 15:16
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
     * 根据名称获取机构编码
     *
     * @param tempCellValue:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-8 18:44
     */
    private String getUnitId(String tempCellValue) {
        tempCellValue = StringUtils.trim(tempCellValue);
        String unitCodeId = "";
        RowSet rs = null;
        List<String> list = new ArrayList<String>();
        list.add(tempCellValue);
        String sql = "select codeitemdesc from organization where codeitemid=?";
        try {
            rs = dao.search(sql, list);
            if (rs.next()) {
                unitCodeId = rs.getString("codeitemdesc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unitCodeId;
    }

    /**
     * 判断excel本行数据是否为空
     *
     * @param row
     * @return
     * @throws Exception
     */
    private boolean isRowEmpty(Row row) {
        Cell cell = row.getCell(0);
        if (cell == null) {
            return true;
        }
        if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
            return true;
        }
        String cellValue = cell.getStringCellValue();
        if (StringUtils.isNotEmpty(cellValue)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 输出导入成绩错误日志
     *
     * @param msgList:
     * @author: caoqy
     * @return: java.lang.String 文件名，空值为导入成功
     * @date: 2019-8-8 17:10
     */
    private String exportErrorLog(ArrayList<Object> msgList) {
        String logFileName = userView.getUserName() + "_岗位导入错误日志.txt";
        String logPath = System.getProperty("java.io.tmpdir") + File.separator + logFileName;
        //错误信息不为0输出错误日志
        StringBuffer textStr = new StringBuffer();
        List errorLogList = (List) msgList.get(0);
        if (errorLogList.size() != 0) {
            textStr.append("导入岗位数据情况如下：" + "\r\n");
            for (int i = 0; i < errorLogList.size(); i++) {
                String msgStr = (String) errorLogList.get(i);
                textStr.append(msgStr + "\r\n");
            }
        } else {
            return "";
        }
        OutputStreamWriter osw = null;
        // write
        try {
            osw = new OutputStreamWriter(new FileOutputStream(logPath, false), "utf-8");
            osw.write(textStr.toString());
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(osw);
        }
        return PubFunc.encrypt(logFileName);
    }

    /**
     * 获取excel表页
     * @param fileId: 文件的加密id
     * @author: caoqy
     * @return: org.apache.poi.ss.usermodel.Sheet
     * @date: 2019-8-7 15:05
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
        	PubFunc.closeResource(work);
            PubFunc.closeIoResource(input);
        }
        return sheet;
    }

    /**
     * 创建根据指标创建excel页
     *
     * @param value         :
     * @param fieldItemList :
     * @param wb
     * @author: caoqy
     * @return: String
     * @date: 2019-8-5 15:04
     */
    private String createSheet(String value, List<FieldItem> fieldItemList, HSSFWorkbook wb) {
        String sheetName = this.userView.getUserName() + "_岗位导入模板";
        String fieldsetdesc = sheetName + ".xls";
        HSSFSheet sheet = wb.createSheet(sheetName);

/*        String hiddenSheet = null;
        HSSFDataValidation validation = null; // 数据验证
        int startRow = 1; // 开始行
        int endRow = 100; // 结束行
        DVConstraint constraint = null;
        hiddenSheet = "category1Hidden";
        Name category1Name = wb.createName();
        HSSFSheet category1Hidden = wb.createSheet(hiddenSheet); // 创建隐藏域
        category1Name.setNameName(hiddenSheet);*/
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

        HSSFCellStyle styleCol0Title = dataStyle(wb);
        styleCol0Title.setFont(font2);
        styleCol0Title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); // 文本格式
        styleCol0Title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0Title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

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
        for (int i = 0; i < fieldItemList.size(); i++) {
            FieldItem field = fieldItemList.get(i);
            String fieldName = field.getItemid().toLowerCase();
            String fieldLabel = field.getItemdesc();
            //判断该字段在业务字典和页面业务中是否设置为必填项了
            boolean required = this.getRequired(field);

/*            if ("z0381".equalsIgnoreCase(fieldName)) {
                z0381Column = i;
            }

            if ("z0101".equalsIgnoreCase(fieldName)) {
                z0101Column = i;
            }*/

            int w = field.getDisplaywidth();
            if (w == 0) {
                w = 8;
            }
            if (w > 50) {
                w = 50;
            }
            sheet.setColumnWidth((i), w * 350);
            cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }

            if (required) {
                cell.setCellValue(cellStr(fieldLabel) + "*");
            } else {
                cell.setCellValue(cellStr(fieldLabel));
            }

            cell.setCellStyle(style2);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
            comm.setString(new HSSFRichTextString(fieldName));
            cell.setCellComment(comm);
            if ("A".equalsIgnoreCase(field.getItemtype()) && (field.getCodesetid() != null && !"".equals(field.getCodesetid()) && !"0".equals(field.getCodesetid()))) {
                codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
            }
        }
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
/*            String z0103;
            String z0101;
            String flowId;
            String name;
            StringBuffer sql = new StringBuffer(" select Z0101, Z0103 from Z01 where z0129 = '04'");
            StringBuffer recruitmentBatch = new StringBuffer();
            rs = dao.search(sql.toString());
            while (rs.next()) {
                z0103 = rs.getString("Z0103");
                z0101 = rs.getString("Z0101");
                recruitmentBatch.append(",");
                recruitmentBatch.append(z0101);
                recruitmentBatch.append(":");
                recruitmentBatch.append(z0103);
            }
            String z01 = recruitmentBatch.toString();
            if (StringUtils.isNotEmpty(z01)) {
                z01 = z01.substring(1);
            }

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
            String flowName = recruitmentBatch.toString();
            if (StringUtils.isNotEmpty(flowName)) {
                flowName = flowName.substring(1);
            }

            String[] Z0103s = z01.split(",");
            String[] names = flowName.split(",");*/

            int rowCount = 1;
            //查询数据
            while (rowCount < 1001) {
                row = sheet.getRow(rowCount);
                if (row == null) {
                    row = sheet.createRow(rowCount);
                }
                for (int i = 0; i < fieldItemList.size(); i++) {
                    FieldItem field = fieldItemList.get(i);
                    String itemtype = field.getItemtype();
                    String itemId = field.getItemid();
                    int decwidth = field.getDecimalwidth();

                    cell = row.getCell(i);
                    if (cell == null) {
                        cell = row.createCell(i);
                    }
                    //数值型
                    if ("N".equals(itemtype)) {
                        if (decwidth == 0) {
                            cell.setCellStyle(styleN);
                        } else if (decwidth == 1) {
                            cell.setCellStyle(styleF1);
                        } else if (decwidth == 2) {
                            cell.setCellStyle(styleF2);
                        } else if (decwidth == 3) {
                            cell.setCellStyle(styleF3);
                        } else if (decwidth == 4) {
                            cell.setCellStyle(styleF4);
                        }
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    }
                    //字符型
                    else if ("A".equals(itemtype) && "0".equals(field.getCodesetid())) {
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    }
                    //代码型
                    else if ("A".equals(itemtype) && !"0".equals(field.getCodesetid())) {
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    }
                    //日期型
                    else if ("D".equals(itemtype)) {
                        cell.setCellStyle(style1);
                    }
                    //备注型
                    else if ("M".equalsIgnoreCase(itemtype)) {
                        cell.setCellStyle(styleN);
                        // 判断数据字典里的指标类型
                        String richText = Sql_switcher.readMemo(rs, itemId);
                    }
                }
                rowCount++;
            }
            rowCount--;

            HSSFSheet codesetSheet = sheet;
            // 下拉数据放到最后，依次为 HZ、HY、HX......
            String[] firstUpper = {"H", "G", "F", "E", "D", "C", "B", "A"};
            String[] lettersUpper = {"Z", "Y", "X", "W", "V", "U", "T", "S", "R", "Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"};

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
/*                    if (codeCol1 == z0101Column) {
                        cellValues = Z0103s;
                    } else {
                        cellValues = names;
                    }

                    for (int i = 0, length = cellValues.length; i < length; i++) {
                        row = codesetSheet.getRow(m + 0);
                        if (row == null) {
                            row = codesetSheet.createRow(m + 0);
                        }

                        cell = row.createCell((cellIndex));
                        cell.setCellValue(new HSSFRichTextString(cellValues[i]));
                        m++;
                    }*/
                } else {
                    StringBuffer codeBuf = new StringBuffer();
                    if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                        codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'"); // and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
                        rs = dao.search(codeBuf.toString());
                        if (rs.next()) {
                            if (rs.getInt(1) < 500) { // 代码型中指标大于500的时候，就不再加载了
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
                            if (this.userView.isSuper_admin()) {
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid");
                            } else {
                                String manpriv = this.userView.getManagePrivCode();
                                String manprivv = this.userView.getManagePrivCodeValue();
                                if (manprivv.length() > 0) {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and codeitemid like '" + manprivv + "%' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid");
                                } else if (manpriv.length() >= 2) {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid");
                                } else {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                                }
                            }
                        }
                    }
                    if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                        rs = dao.search(codeBuf.toString());
                        while (rs.next()) {
                            row = codesetSheet.getRow(m + 0);
                            if (row == null) {
                                row = codesetSheet.createRow(m + 0);
                            }

                            cell = row.createCell((cellIndex));
                            if ("UN".equals(codesetid)) {
                                int grade = rs.getInt("grade");
                                StringBuffer message = new StringBuffer();
                                message.setLength(0);
                                for (int i = 1; i < grade; i++) {
                                    message.append("  ");
                                }
                                cell.setCellValue(new HSSFRichTextString(message.toString() + rs.getString("codeitemdesc") + "(" + rs.getString("codeitemid") + ")"));
                            } else {
                                cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
                            }
                            m++;
                        }
                    }
                    if (m == 2001) {
                        continue;
                    }

                }
                String strFormula = "";
                strFormula = "$" + firstUpper[index / 26] + lettersUpper[index % 26] + "$2001:$" + firstUpper[index / 26] + lettersUpper[index % 26] + "$" + m; // 表示BA列1到m行作为下拉列表来源数据
                CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1); //rowCount
                DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                dataValidation.setSuppressDropDownArrow(false);
                sheet.addValidationData(dataValidation);
                index++;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return fieldsetdesc;
    }

    /**
     * 加载机构
     *
     * @param sheet:
     * @param row:
     * @param cell:
     * @param index:
     * @param m:
     * @param dao:
     * @param type:
     * @author: caoqy
     * @return: int
     * @date: 2019-8-5 15:28
     */
    private int loadorg(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int index, int m, ContentDAO dao, String type) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        DbSecurityImpl dbs = new DbSecurityImpl();
        try {
            st = this.conn.createStatement();
            String sql = "";
            if (this.userView.isSuper_admin()) {
                sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
            } else {
                List<String> list = new ArrayList<String>();
                String manpriv = this.userView.getManagePrivCode();
                //组织机构业务范围>操作单位>人员范围
                String manprivv = this.userView.getUnitIdByBusi("4");
                if (StringUtils.isBlank(manprivv) || StringUtils.equalsIgnoreCase("UN", manprivv) || StringUtils.equalsIgnoreCase("UM", manprivv)) {
                    throw new GeneralException("此人员无组织范围权限");
                } else {
                    String[] manprivvArr = StringUtils.split(manprivv, "`");
                    for (String tempManprivv : manprivvArr) {
                        list.add(tempManprivv.substring(2));
                    }
                }

                if (list.size() > 0) {
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where  (1=2  ";
                    for (String tempList : list) {
                        sql += " or codeitemid='" + tempList + "'";
                    }
                    sql += ") and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date  order by a0000,codeitemid";
                } else if (manpriv.length() >= 2) {
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
                } else {
                    sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where 1=2";
                }
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
                if (row == null) {
                    row = sheet.createRow(m + 0);
                }
                cell = row.createCell((index));
                StringBuffer message = new StringBuffer();
                message.setLength(0);
                for (int i = 1; i < grade; i++) {
                    message.append("  ");
                }
                cell.setCellValue(new HSSFRichTextString(message.toString() + codeitemdesc + "(" + codeitemid + ")"));
                m++;
                if (!codeitemid.equals(childid)) {
                    m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(st);
        }
        return m;
    }

    /**
     * 加载机构子节点
     *
     * @param sheet:
     * @param row:
     * @param cell:
     * @param index:
     * @param m:
     * @param dao:
     * @param parentid:
     * @param type:
     * @author: caoqy
     * @return: int
     * @date: 2019-8-5 15:29
     */
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
                if (row == null) {
                    row = sheet.createRow(m + 0);
                }

                cell = row.createCell((index));
                StringBuffer message = new StringBuffer();
                message.setLength(0);
                for (int i = 1; i < grade; i++) {
                    message.append("  ");
                }
                cell.setCellValue(new HSSFRichTextString(message.toString() + codeitemdesc + "(" + codeitemid + ")"));
                m++;
                if (!codeitemid.equals(childid)) {
                    m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(st);
        }
        return m;
    }

    /**
     * 求列数
     *
     * @param column:
     * @author: caoqy
     * @return: int
     * @date: 2019-8-5 15:29
     */
    private int columnToIndex(String column) {
        if (!column.matches("[A-Z]+")) {
            try {
                throw new Exception("Invalid parameter");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            index += ((int) chars[i] - (int) 'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
        }
        return index - 1;
    }

    /**
     * 返回富文本单元格
     *
     * @param context:
     * @author: caoqy
     * @return: org.apache.poi.hssf.usermodel.HSSFRichTextString
     * @date: 2019-8-5 15:30
     */
    private HSSFRichTextString cellStr(String context) {
        return new HSSFRichTextString(context);
    }

    /**
     * 判断此指标，在指标体系或业务字典中是否为必填项
     *
     * @param field:
     * @author: caoqy
     * @return: boolean
     * @date: 2019-8-5 15:30
     */
    private boolean getRequired(FieldItem field) {
        //业务字典中是否设置为必填项了
        boolean required = field.isFillable();
        String fieldName = field.getItemid().toLowerCase();
        //下载模板为导入成绩，主键与分数指标为必填
        return required || "Z8101".equalsIgnoreCase(fieldName);
    }

    /**
     * 根据长度得到占位符
     *
     * @param len:
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-5 15:32
     */
    private String decimalwidth(int len) {
        StringBuffer decimal = new StringBuffer("0");
        if (len > 0) {
            decimal.append(".");
        }
        for (int i = 0; i < len; i++) {
            decimal.append("0");
        }
        decimal.append("_ ");
        return decimal.toString();
    }

    /**
     * 设置单元格格式
     *
     * @param wb:
     * @author: caoqy
     * @return: org.apache.poi.hssf.usermodel.HSSFCellStyle
     * @date: 2019-8-5 15:33
     */
    private HSSFCellStyle dataStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
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


    /**
     * 获取下载模板指标List
     *
     * @author: caoqy
     * @return: java.util.List<com.hrms.hjs0j.sys.FieldItem>
     * @date: 2019-8-13 11:49:39
     */
    private List<FieldItem> getTemplateFields() {
        List<String> fieldList = new ArrayList<String>();
        List<FieldItem> list = new ArrayList<FieldItem>();
//        //封装唯一性指标
//        list.add(DataDictionary.getFieldItem("Z8101"));
//        //所属部门
//        fieldList.add("E0122");
        //所属岗位
        fieldList.add("E01A1");
        //缺编人数
        fieldList.add("Z8105");
        //拟招聘人数
        fieldList.add("Z8107");
        //开始时间
        fieldList.add("Z8111");
        //结束时间
        fieldList.add("Z8113");
        //审批过程
        fieldList.add("Z8317");
        for (String itemId : fieldList) {
            FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
            if (fieldItem != null) {
                list.add(fieldItem);
            }
        }
        return list;
    }

    @Override
    public int getReleaseCompetitionJobsTotal(String orgId, String jobName) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        list.add(this.userView.getGuidkey());//当前用户guidkey
        String b0110 = this.userView.getUserOrgId();//单位
        String e0122 = this.userView.getUserDeptId();//部门
        if (e0122 != null && e0122.trim().length() > 0) {
            list.add("%," + e0122 + ",%");
            list = this.getOrgIds("UM", e0122, list);
        } else {
            if (b0110 != null && b0110.trim().length() > 0) {
                list.add("%," + b0110 + ",%");
                list = this.getOrgIds("UN", b0110, list);
            }
        }
        String whereSql = getWhereRepleaseCompetitionJobsSQL(orgId, jobName, list);
        sql.append("select count(1) count from Z81 " + whereSql);
        if (orgId != null && orgId.trim().length() > 0) {
            list.add(orgId + "%");
        }
        if (jobName != null && jobName.trim().length() > 0) {
            list.add("%" + jobName + "%");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //查询发布岗位数出错
            throw new GeneralException("searchReleaseJobsTotalError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return 0;
    }

    @Override
    public List listReleaseCompetitionJobs(String orgId, String jobName, int pageIndex, int pageSize) throws GeneralException {
        List dataList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        list.add(this.userView.getGuidkey());//当前用户guidkey
        String b0110 = this.userView.getUserOrgId();//单位
        String e0122 = this.userView.getUserDeptId();//部门
        if (e0122 != null && e0122.trim().length() > 0) {
            list.add("%," + e0122 + ",%");
            list = this.getOrgIds("UM", e0122, list);
        } else {
            if (b0110 != null && b0110.trim().length() > 0) {
                list = this.getOrgIds("UN", b0110, list);
            }
        }
        String whereSql = getWhereRepleaseCompetitionJobsSQL(orgId, jobName, list);
        String dataToCharSql = Sql_switcher.dateToChar("Z81.Z8113", "yyyy-MM-dd");
        sql.append("select Z8101,Z81.B0110,E0122,E01A1,Z81.Z8107," + dataToCharSql + " Z8113 from Z81 " + whereSql);
        if (orgId != null && orgId.trim().length() > 0) {
            list.add(orgId + "%");
        }
        if (jobName != null && jobName.trim().length() > 0) {
            list.add("%" + jobName + "%");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list, pageSize, pageIndex);
            while (rs.next()) {
                Map map = new HashMap();
                map.put("id", PubFunc.encrypt(rs.getString("Z8101")));
                if (StringUtils.isNotBlank(rs.getString("B0110"))) {
                    if (AdminCode.getCode("UN", rs.getString("B0110")) == null) {
                        this.addCodeItem("UN", rs.getString("B0110"));
                    }
                    map.put("B0110", AdminCode.getCode("UN", rs.getString("B0110")).getCodename());
                }
                if (StringUtils.isNotBlank(rs.getString("E0122"))) {
                    if (AdminCode.getCode("UM", rs.getString("E0122")) == null) {
                        this.addCodeItem("UM", rs.getString("E0122"));
                    }
                    map.put("E0122", AdminCode.getCode("UM", rs.getString("E0122")).getCodename());
                }
                if (StringUtils.isNotBlank(rs.getString("E01A1"))) {
                    if (AdminCode.getCode("@K", rs.getString("E01A1")) == null) {
                        this.addCodeItem("@K", rs.getString("E01A1"));
                    }
                    map.put("E01A1", AdminCode.getCode("@K", rs.getString("E01A1")).getCodename());
                }
                map.put("Z8107", rs.getInt("Z8107"));
                map.put("Z8113", rs.getString("Z8113"));
                dataList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //查询发布岗位出错
            throw new GeneralException("searchReleaseJobsError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }

    /**
     * 获取发布竞聘岗位条件sql语句
     *
     * @param orgId
     * @param jobName 模糊查询 岗位名称
     * @return
     */
    private String getWhereRepleaseCompetitionJobsSQL(String orgId, String jobName, List orgList) {
        StringBuffer sql = new StringBuffer();
        String b0110 = this.userView.getUserOrgId();
        String e0122 = this.userView.getUserDeptId();
        sql.append(" where Z8103='04' and not EXISTS(select Z8101 from Z83 where Z83.Z8101=Z81.Z8101 and Z8301=?) ");
        if (Sql_switcher.searchDbServer() == 1) { //发布时间未过的岗位
            sql.append(" and Z8113 > GETDATE() and Z8111 <= GETDATE() ");
        } else if (Sql_switcher.searchDbServer() == 2) {
            sql.append(" and Z8113 > sysdate and Z8111 <= sysdate ");
        }
        /**过滤竞聘范围*/
        sql.append(" and (Z8115 is null or Z8115 = '' or Z8115 = 'UN`' or ");
        for (int i = 0; i < orgList.size() - 1; i++) {
            sql.append(" Z8115 like ? or ");
        }
        sql.setLength(sql.length() - 3);
        sql.append(") ");

        if (orgId != null && orgId.trim().length() > 0 && AdminCode.getCode("UN", orgId) != null) {
            sql.append(" and B0110 like ?");
        }
        if (orgId != null && orgId.trim().length() > 0 && AdminCode.getCode("UM", orgId) != null) {
            sql.append(" and E0122 like ?");
        }
        if (jobName != null && jobName.trim().length() > 0) {
            sql.append(" and E01A1 in (select codeitemid from organization where codesetid='@K' and codeitemdesc like ?)");
        }
        return sql.toString();
    }

    /**
     * 获取当前用户的机构和上级机构
     *
     * @param codeitemid
     * @return
     */
    private List getOrgIds(String codesetid, String codeitemid, List list) {
        CodeItem codeitem = AdminCode.getCode(codesetid, codeitemid);
        if (codeitem == null) {
            codeitem = AdminCode.getCode("UN", codeitemid);
        }
        String parentId = codeitem.getPcodeitem();
        if (!codeitemid.equalsIgnoreCase(parentId)) {
            list.add("%," + parentId + ",%");
            return getOrgIds(codeitem.getCodeid(), parentId, list);
        }
        return list;
    }

    @Override
    public Map getCompetitionJobDetailData(String id) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        Map dataHM = new HashMap();
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String canceBtn = "0";//撤回按钮状态  =1 显示
        String applyBtn = "";//报名按钮状态 =1显示
        String tabid = TalentMarketsUtils.getApplyTemplate();
        MobileTemplateBo bo = new MobileTemplateBo(this.conn, this.userView);
        String  jsonstr = bo.getApplyTemplate(1);//获取全部模板 1全部
        if(jsonstr.length()==0){
			jsonstr="[]";
		}
        JSONArray jArray=JSONArray.fromObject(jsonstr);
        for(int i= 0 ; i< jArray.size() ; i++) {
        	JSONObject bean = (JSONObject)jArray.get(i);
        	if(StringUtils.equalsIgnoreCase((String)bean.get("tabid"), tabid)) {//校验是否有移动端表单权限 
        		canceBtn = "1";
        		applyBtn = "1";
        		break;
        	}
        }
        //根据当前竞聘岗位状态，显示报名&撤回按钮
        sql.append("select Z8303 from Z83 where Z8101=? and Z8301=? ");
        list.add(PubFunc.decrypt(id));
        list.add(this.userView.getGuidkey());
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                String z8303 = rs.getString("Z8303");
                if (StringUtils.isNotBlank(z8303)) {
                    applyBtn = "0";
                }
                if (!StringUtils.equalsIgnoreCase("01", z8303)) {
                    canceBtn = "0";
                }
            } else {
                canceBtn = "0";
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            //获取竞聘岗位状态出错
            throw new GeneralException("getCompetitionJobStateError");
        }
        sql.setLength(0);
        list.clear();
        //查询当前正在竞聘的岗位
        sql.append("select count(1) count from Z83 where Z8301=? and Z8303 not in ('03','06','09','12','13')");
        list.add(this.userView.getGuidkey());
        int competitionJobsNum = 0;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                competitionJobsNum = rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //获取正在竞聘中岗位数出错
            throw new GeneralException("getCompetitionJobsNumError");
        }

        sql.setLength(0);
        list.clear();

        //岗位详情指标处理
        List<String> postFieldList = TalentMarketsUtils.listPostFields();
        sql.append("select Z8101,E01A1,B0110,E0122,");
        String notFields = ",B0110,E0122,E01A1,Z8105,Z8115,Z8117,".toUpperCase();
        for (int i = 0; i < postFieldList.size(); i++) {
            if (notFields.indexOf("," + postFieldList.get(i).toUpperCase() + ",") != -1)//过滤指标
            {
                continue;
            }
            if ("D".equalsIgnoreCase(DataDictionary.getFieldItem(postFieldList.get(i)).getItemtype())) {
                sql.append(Sql_switcher.dateToChar(postFieldList.get(i), "yyyy-MM-dd hh:mm") + " " + postFieldList.get(i) + ",");
            } else {
                sql.append(postFieldList.get(i) + ",");
            }
        }
        sql.setLength(sql.length() - 1);
        sql.append(" from Z81 where Z8101=?");
        list.add(PubFunc.decrypt(id));
        ArrayList fieldList = new ArrayList();
        HashMap recordHM = new HashMap();
        recordHM.put("guidkey", this.userView.getGuidkey());
        recordHM.put("a0100", this.userView.getA0100());
        recordHM.put("nbase", this.userView.getDbname());
        String E01A1 = "";
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                Map E01A1HM = new HashMap();
                recordHM.put("z8101", rs.getString("Z8101"));
                E01A1 = rs.getString("E01A1");
                recordHM.put("e01a1", E01A1);
                fieldList.add(this.getFieldHM("E01A1", E01A1));
                String B0110 = rs.getString("B0110");
                fieldList.add(this.getFieldHM("B0110", B0110));
                String E0122 = rs.getString("E0122");
                fieldList.add(this.getFieldHM("E0122", E0122));
                recordHM.put("b0110", B0110);
                recordHM.put("e0122", E0122);
                for (int i = 0; i < postFieldList.size(); i++) {
                    if (notFields.indexOf("," + postFieldList.get(i).toUpperCase() + ",") != -1)//过滤指标
                    {
                        continue;
                    }
                    FieldItem fieldItem = DataDictionary.getFieldItem(postFieldList.get(i).toLowerCase());
                    if("M".equalsIgnoreCase(fieldItem.getItemtype())){
                    	fieldList.add(this.getFieldHM(postFieldList.get(i), rs.getString(postFieldList.get(i))));
                    }else{
                    	fieldList.add(this.getFieldHM(postFieldList.get(i), rs.getObject(postFieldList.get(i))));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取岗位详情出错
            throw new GeneralException("getCompetitionJobDetailError");
        } catch (Exception e) {
            e.printStackTrace();
            //获取岗位详情出错
            throw new GeneralException("getCompetitionJobDetailError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        dataHM.put("fieldList", fieldList);

        if ("1".equalsIgnoreCase(applyBtn)) {//显示报名按钮
            //获取最大竞聘岗位数  等接口
            int maxCompetitionJobsNum = TalentMarketsUtils.getMaxCompetitionPost();
            int jobsNum = maxCompetitionJobsNum - competitionJobsNum;
            dataHM.put("laveJobNum", jobsNum);//剩下竞聘岗位数
            dataHM.put("applyBtn", applyBtn);
            if (jobsNum > 0) {
//                String tabid = TalentMarketsUtils.getApplyTemplate();
                //拼接业务模板报名格式
                String param = "{\"tabid\":\"" + tabid + "\",\"isEdit\":\"1\",\"taskid\":\"\",\"ins_id\":\"\",\"fromMessage\":\"0\",\"object_id\":\"" + userView.getDbname() + userView.getA0100() + "\"}";
                //          String jsonObject = this.getApplyTemplateData(param);
                param = SafeCode.encode(param);
                dataHM.put("param", param);
                //        dataHM.put("templateData", jsonObject);
                dataHM.put("templateType", "applyTemplate");
                ArrayList recordList = new ArrayList();
                recordList.add(recordHM);
                dataHM.put("records", recordList);
            }
        }
        if ("1".equalsIgnoreCase(canceBtn)) {
//            String tabid = TalentMarketsUtils.getApplyTemplate();
            Map map = TalentMarketsUtils.getApplyTemplateRelation();
            //拼接业务模板撤回格式
            RecordVo recordVo = new RecordVo("templet_" + tabid);
            recordVo.setString("a0100", this.userView.getA0100());
            recordVo.setString("basepre", this.userView.getDbname());
            recordVo.setString((String) map.get("z8101"), (String) recordHM.get("Z8101"));
            sql.setLength(0);
            sql.append("select tt.task_id,tt.state,tt.node_id,t.ins_id from templet_" + tabid + " t,t_wf_task tt where t.ins_id=tt.ins_id ");
            sql.append(" and t.a0100=? ");
            sql.append(" and t.basepre=? ");
            sql.append(" and t." + (String) map.get("z8101") + "=? ");
            sql.append(" order by tt.task_id desc");
            int taskid = 0;
            int ins_id = 0;
            String state = "";
            String node_id = "";
            try {
                rs = dao.search(sql.toString(), Arrays.asList(this.userView.getA0100(), this.userView.getDbname(), (String) recordHM.get("z8101")));
                if (rs.next()) {
                    taskid = rs.getInt("task_id");
                    ins_id = rs.getInt("ins_id");
                    state = rs.getString("state");
                    node_id = rs.getString("node_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //获取报名中得竞聘岗位模板信息出错
                throw new GeneralException("getApplyTemplateTaskidAndInsidError");
            }
            String isEdit = "0";
            if (StringUtils.equalsIgnoreCase("07", state)) {
                try {
                    rs = dao.search("select nodetype from t_wf_node where node_id = ? and tabid = ? ", Arrays.asList(node_id, tabid));
                    if (rs.next()) {
                        //驳回到发起人状态
                        if (StringUtils.equalsIgnoreCase("1", rs.getString("nodetype"))) {
                            isEdit = "1";
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    //获取报名中得竞聘岗位模板信息出错
                    throw new GeneralException("getApplyTemplateTaskidAndInsidError");
                } finally {
                    PubFunc.closeDbObj(rs);
                }

            }
            String param = "{\"tabid\":\"" + tabid + "\",\"isEdit\":\"" + isEdit + "\",\"taskid\":\"" + taskid + "\",\"ins_id\":\"" + ins_id + "\",\"fromMessage\":\"0\",\"object_id\":\"" + userView.getDbname() + userView.getA0100() + "\"}";
            String jsonObject = this.getApplyTemplateData(param);
            param = SafeCode.encode(param);
            dataHM.put("param", param);
            dataHM.put("templateData", jsonObject);
            dataHM.put("canceBtn", canceBtn);
        }

        //配置岗位登记表处理
        String postDetailRname = TalentMarketsUtils.getPostDetailRname();
        if (postDetailRname != null && postDetailRname.trim().length() > 0) {
            YkcardOutWord ykcardOutWord = new YkcardOutWord(this.userView, this.conn);
            /*
             * 参数说明：
             * tabid ：x 登记表id 获取业务参数配置的岗位详情登记表
             *	nid ：4 岗位（固定）
             *	queryType: 0 固定
             *	infokind: 4 岗位（固定）
             *	dbName: null  不需要人员库
             *	userpriv: selfinfo 固定
             *	havepriv: 0
             *	fieldpurv: 1
             */
            String filename = ykcardOutWord.outPdfYkcard(Integer.parseInt(postDetailRname), E01A1, "0", "4", "", "selfinfo", "0", "1");
            filename = PubFunc.encrypt(filename);
            dataHM.put("filename", filename);
        }
        return dataHM;
    }

    /**
     * 获取报名模板数据
     *
     * @param param 模板参数
     * @return
     * @throws GeneralException
     */
    private String getApplyTemplateData(String param) throws GeneralException {
        String jsonStr = "";
        MobileTemplateBo bo = new MobileTemplateBo(this.conn, this.userView);
        try {
            jsonStr = bo.getTemplateInfo(param);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //获取报名模板数据出错
            throw new GeneralException("getApplyTemplateError");
        }
        return jsonStr;
    }

    /**
     * 获取竞聘岗位指标数据
     *
     * @param itemid 指标id
     * @param value  指标值
     * @return
     */
    private Map getFieldHM(String itemid, Object value) {
        Map map = new HashMap();
        FieldItem fieldItem = null;
        map.put("itemid", itemid);
        if ("b0110".equalsIgnoreCase(itemid.toLowerCase()) || "e0122".equalsIgnoreCase(itemid.toLowerCase()) || "e01a1".equalsIgnoreCase(itemid.toLowerCase())) {
            fieldItem = DataDictionary.getFieldItem(itemid.toLowerCase());
            if (StringUtils.isNotBlank((String) value)) {
                map.put("value", AdminCode.getCode(fieldItem.getCodesetid(), (String) value).getCodename());
            } else {
                map.put("value", "");
            }
            map.put("name", fieldItem.getItemdesc());
            map.put("itemtype", fieldItem.getItemtype());
        } else {
            fieldItem = DataDictionary.getFieldItem(itemid.toLowerCase());
            if ("A".equalsIgnoreCase(fieldItem.getItemtype()) && !"0".equalsIgnoreCase(fieldItem.getCodesetid())) {
                if (StringUtils.isNotBlank((String) value)) {
                    if (AdminCode.getCode(fieldItem.getCodesetid(), (String) value) == null) {
                        this.addCodeItem("Z81", itemid.toLowerCase());
                    }
                    map.put("value", AdminCode.getCode(fieldItem.getCodesetid(), (String) value).getCodename());
                } else {
                    map.put("value", "");
                }
            } else {
                map.put("value", value);
            }
            map.put("name", fieldItem.getItemdesc());
            map.put("itemtype", fieldItem.getItemtype());

        }
        return map;
    }


    /**
     * 获取竞聘分析的相关数据
     *
     * @param nmoudle  相关模块 4为组织机构
     * @param userView 登录用户的信息
     * @param paramMap 参数对象
     * @return HashMap
     * @throws GeneralException 抛出异常
     * @author wangbs、hanqh
     */
    @Override
    public HashMap getChartsData(String nmoudle, UserView userView, Map<String, String> paramMap) throws GeneralException {
        RowSet rs = null;
        HashMap returnMap = new HashMap();
        String subSql = "";
        StringBuffer sqlBuffer = new StringBuffer();

        //选中的组织机构id
        String orgId = paramMap.get("orgId");
        //开始时间
        String startDate = paramMap.get("startDate");
        //结束时间
        String endDate = paramMap.get("endDate");

        // 存放相应的数据 1.需求人数   2.申报人数  3.录用人数 4.岗位名称
        List recordNeed = new ArrayList();
        List recordDc = new ArrayList();
        List recordEmploy = new ArrayList();
        List recordDesc = new ArrayList();
        try {
            //组织机构业务范围>操作单位>人员范围
            String unitPriv = userView.getUnitIdByBusi(nmoudle);
            //没有任何权限,所有数据都不展示
            if (!this.userView.isSuper_admin() && (StringUtils.isBlank(unitPriv)||StringUtils.equalsIgnoreCase("UN",unitPriv)||StringUtils.equalsIgnoreCase("UM",unitPriv))) {
                returnMap.put("seriesNeedData", recordNeed);
                returnMap.put("seriesDCData", recordDc);
                returnMap.put("seriesEmployData", recordEmploy);
                returnMap.put("xAxisData", recordDesc);
                return returnMap;
            }

            if (StringUtils.isEmpty(orgId)) {
                subSql = TalentMarketsUtils.getConditionsSql(this.userView, nmoudle, "e01a1", "pos");
            } else {// 选择了特定的部门
                subSql = "pos.e01a1 like '" + orgId + "%'";
            }

            sqlBuffer.append("select pos.e01a1,pos.z8107,pos.z8109,psn.psnc from Z81 pos ");
            sqlBuffer.append("left join (select z8101,count(z8303) psnc from z83 ");
            sqlBuffer.append("where z8303 in ('08', '10', '11', '13') group by z8101) psn ");
            sqlBuffer.append("on pos.z8101=psn.z8101 ");
            sqlBuffer.append("where (" + subSql + ") ");
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                startDate = startDate + " 00:00:00";
                endDate = endDate + " 23:59:59";
                sqlBuffer.append("and pos.create_time between ");
                if (Sql_switcher.searchDbServer() == 2) {
                    sqlBuffer.append("to_date('" + startDate + "','yyyy-MM-dd hh24:mi:ss') ");
                    sqlBuffer.append("and to_date('" + endDate + "','yyyy-MM-dd hh24:mi:ss') ");
                } else {
                    sqlBuffer.append("convert(datetime, '" + startDate + "') ");
                    sqlBuffer.append("and convert(datetime, '" + endDate + "') ");
                }
            }
            //结束 已公示 公示结束状态的数据
            sqlBuffer.append("and pos.z8103 in" + TalentMarketsUtils.END_STATUS);
            sqlBuffer.append("order by z8109 desc");
            //oracle中认为null最大，实际业务中按应聘人数降序时需放在最后面
            if (Sql_switcher.searchDbServer() == 2) {
                sqlBuffer.append(" nulls last");
            }
            rs = dao.search(sqlBuffer.toString());
            //遍历放入rocord
            while (rs.next()) {
                int z8107 = rs.getInt("z8107");//拟招聘人数
                int z8109 = rs.getInt("z8109");//应聘人数
                int psnc = rs.getInt("psnc");//录用人数
                String codeItemId = rs.getString("e01a1");//岗位itemId
                String codeItemDesc = AdminCode.getCodeName("@K", codeItemId);

                recordNeed.add(z8107);
                recordDc.add(z8109);
                recordEmploy.add(psnc);
                recordDesc.add(codeItemDesc);
            }

            returnMap.put("seriesNeedData", recordNeed);
            returnMap.put("seriesDCData", recordDc);
            returnMap.put("seriesEmployData", recordEmploy);
            returnMap.put("xAxisData", recordDesc);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return returnMap;
    }


    /**
     * 添加机构和Z81表缓存
     *
     * @param codesetid
     * @param codeitemid
     * @return
     */
    private void addCodeItem(String codesetid, String codeitemid) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            if (",UN,UM,@K,".indexOf("," + codesetid.toUpperCase() + ",") != -1) {
                CodeItem codeItem = new CodeItem();
                rs = dao.search("select * from organization where codesetid=? and codeitemid=?", Arrays.asList(codesetid, codeitemid));
                if (rs.next()) {
                    codeItem.setCcodeitem(rs.getString("childid"));
                    codeItem.setCodeid(rs.getString("codesetid"));
                    codeItem.setCodeitem(rs.getString("codeitemid"));
                    codeItem.setCodename(rs.getString("codeitemdesc"));
                    codeItem.setEndDate(rs.getDate("end_date"));
                    codeItem.setPcodeitem(rs.getString("parentid"));
                    codeItem.setStartDate(rs.getDate("start_date"));
                    AdminCode.addCodeItem(codeItem);
                }
            } else {
                rs = dao.search("select * from t_hr_busifield where fieldsetid=? and fielditemid=?", Arrays.asList(codesetid, codeitemid));
                if (rs.next()) {
                    FieldItem fieldItem = new FieldItem();
                    fieldItem.setFieldsetid(rs.getString("fieldsetid"));
                    fieldItem.setItemid(rs.getString("fielditemid"));
                    fieldItem.setItemtype(rs.getString("itemtype"));
                    fieldItem.setItemlength(rs.getInt("itemlength"));
                    fieldItem.setItemdesc(rs.getString("itemdesc"));
                    fieldItem.setDisplayid(rs.getInt("displayid"));
                    fieldItem.setDecimalwidth(rs.getInt("decimalwidth"));
                    fieldItem.setCodesetid(rs.getString("codesetid"));
                    fieldItem.setUseflag(rs.getString("useflag"));
                    DataDictionary.addFieldItem(codeitemid, fieldItem, 1);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 用于判断导入的岗位是否是在此人权限以内
     *
     * @param e01a1
     * @return
     */
    private boolean isHaveE01a1(String e01a1) {
        boolean isHaveE01a1Flag = false;
        if (StringUtils.isEmpty(e01a1)) {
            return isHaveE01a1Flag;
        } else {
            //组织机构业务范围>操作单位>人员范围
            String privStr = this.userView.getUnitIdByBusi("4");
            privStr = privStr.replaceAll("`", ",");
            String[] units = privStr.split(",");
            if (StringUtils.equalsIgnoreCase("UN`", privStr) || userView.isSuper_admin()) {
                isHaveE01a1Flag = true;
            } else {
                for (String itemid : units) {
                    if (StringUtils.contains(e01a1, itemid.substring(2))) {
                        isHaveE01a1Flag = true;
                    }
                }
            }
        }
        return isHaveE01a1Flag;
    }
    @Override
    public boolean isHaveThePosTab() throws GeneralException {
        String poscard = TalentMarketsUtils.getPostDetailRname();
        return this.userView.isHaveResource(IResourceConstant.CARD, poscard);
    }

    /*public boolean getQuickApprove(){
        boolean quickApprove = false;
        RecordVo constantVo = ConstantParamter.getRealConstantVo("TALENTMARKETS_PARAM");
        String strValue=constantVo.getString("str_value");
        if(StringUtils.isNotEmpty(strValue)){
            JSONObject configObj = JSONObject.fromObject(strValue);
            JSONObject competition = configObj.getJSONObject("competition");
            JSONObject templates = competition.getJSONObject("templates");
            if(templates.containsKey("quickApprove")){
                quickApprove = templates.getBoolean("quickApprove");
            }
        }
        return quickApprove;
    }*/

    @Override
    public Map getCreatePostFieldList() throws GeneralException{
        StringBuffer sql = new StringBuffer();
        RowSet rs = null;
        //固定指标list
        Map defaultFieldMap = new HashMap();
        String defaultFieldStr = ",b0110,e0122,e01a1,z8101,z8103,z8105,z8107,z8109,z8111,z8113,z8115,z8117,interviewArrangement,";
        //额外指标list
        List extraFieldList = new ArrayList();
        Map fieldListMap = new HashMap();
        try {
            //sql.append("select * from  T_HR_BUSIFIELD where fieldsetid = 'Z81' and state = 1 and useflag = 1");
            //rs = dao.search(sql.toString());
            //while(rs.next()){
            //    Map fieldMap = new HashMap();
            //    String itemid = rs.getString("itemid");
            //    String itemtype = rs.getString("itemtype");
            //    String itemdesc = rs.getString("itemdesc");
            //    String codesetid = rs.getString("codesetid");
            //    String itemlength = rs.getString("itemlength");
            //    String decimalwidth = rs.getString("decimalwidth");
            //    fieldMap.put("itemid", itemid.toLowerCase());
            //    fieldMap.put("itemtype", itemtype);
            //    fieldMap.put("itemdesc", itemdesc);
            //    fieldMap.put("codesetid", codesetid);
            //    fieldMap.put("itemlength", itemlength);
            //    fieldMap.put("decimalwidth", decimalwidth);
            //    if (StringUtils.contains(defaultFieldStr, "," + itemid.toLowerCase() + ",")) {
            //        defaultFieldMap.put(itemid.toLowerCase(),fieldMap);
            //    }else{
            //        extraFieldList.add(fieldMap);
            //    }
            //}
            TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(this.subModuleId);
            //List<ColumnsInfo> columns = cache.getDisplayColumns();//栏目设置有合并列时，只显示最顶层的列信息，报空指针异常
            List<ColumnsInfo> columns = cache.getTableColumns();
            for(int i = 0;i<columns.size();i++){
                ColumnsInfo columnsInfo = columns.get(i);
                String itemid = columnsInfo.getColumnId();
                String itemtype = columnsInfo.getColumnType();
                String itemdesc = columnsInfo.getColumnDesc();
                String codesetid = columnsInfo.getCodesetId();
                String itemlength = String.valueOf(columnsInfo.getColumnLength());
                String decimalwidth =String.valueOf(columnsInfo.getDecimalWidth());
                Map fieldMap = new HashMap();
                fieldMap.put("itemid", itemid.toLowerCase());
                fieldMap.put("itemtype", itemtype);
                fieldMap.put("itemdesc", itemdesc);
                fieldMap.put("codesetid", codesetid);
                fieldMap.put("itemlength", itemlength);
                fieldMap.put("decimalwidth", decimalwidth);
                if(StringUtils.equalsIgnoreCase("interviewArrangement",itemid)){
                    continue;
                }
                if (StringUtils.contains(defaultFieldStr, "," + itemid.toLowerCase() + ",")) {
                    defaultFieldMap.put(itemid.toLowerCase(),fieldMap);
                }else{
                    if (StringUtils.equalsIgnoreCase(columnsInfo.getFieldsetid(), "z81")) {//z81表中 栏目设置显示的字段
                        if (columnsInfo.getLoadtype() == 1){
                            extraFieldList.add(fieldMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new GeneralException("getCreatePostFieldListError");
        }
        fieldListMap.put("defaultFieldMap",defaultFieldMap);
        fieldListMap.put("extraFieldList",extraFieldList);
        return fieldListMap;
    }

    @Override
    public void saveCreatePostData(Map basicInformation, List competitiveScopeData, List interviewerData) throws GeneralException {
        try {
            String e01a1s = ((String) basicInformation.get("e01a1")).split("`")[0];
            String[] e01a1Arr = e01a1s.split(",");
            for(int k = 0;k<e01a1Arr.length;k++){
                String e01a1 = e01a1Arr[k];
                RecordVo z81vo = new RecordVo("z81");
                IDGenerator ids = new IDGenerator(2, this.conn);
                String z8101 = ids.getId("z81.z8101");
                //生成Z81主键Z8101
                z81vo.setObject("z8101", z8101);
                z81vo.setObject("z8103", "01");
                z81vo.setDate("create_time", new Date());
                z81vo.setObject("create_user", this.userView.getUserName());
                //更新 z8115 竞聘范围
                StringBuffer z8115 = new StringBuffer();
                for (Object orgid : competitiveScopeData) {
                    z8115.append(PubFunc.decrypt((String) orgid)).append(",");
                }
                if (z8115.length() > 0) {
                    z8115.insert(0, ",");
                    z81vo.setObject("z8115", z8115.toString());
                }
                for (Object key : basicInformation.keySet()) {
                    if(StringUtils.equalsIgnoreCase("e01a1",(String)key)){
                        continue;
                    }
                    Object value = basicInformation.get(key);
                    FieldItem fieldItem = DataDictionary.getFieldItem((String) key, "z81");
                    //代码型指标
                    if (StringUtils.equalsIgnoreCase(fieldItem.getItemtype(), "A") && !StringUtils.equalsIgnoreCase(fieldItem.getCodesetid(), "0")) {
                        value = ((String) value).split("`")[0];
                    } else if (StringUtils.equalsIgnoreCase(fieldItem.getItemtype(), "N")) {
                        if (StringUtils.isEmpty((String) value)) {
                            value = "0";
                        }
                    } else if (StringUtils.equalsIgnoreCase(fieldItem.getItemtype(), "D")) {
                        value = PubFunc.DateStringChangeValue((String) value);
                        z81vo.setDate((String) key, (String) value);
                        continue;
                    }
                    z81vo.setObject((String) key, value);
                }
                //String e01a1 = ((String) basicInformation.get("e01a1"));
                String e0122 = TalentMarketsUtils.getOrgItemid(e01a1, dao, "UM");
                String b0110 = TalentMarketsUtils.getOrgItemid(e01a1, dao, "UN");
                z81vo.setObject("e0122", e0122);
                z81vo.setObject("b0110", b0110);
                z81vo.setObject("e01a1", e01a1);
                dao.addValueObject(z81vo);
                //更新面试官表
                for (int j = 0; j < interviewerData.size(); j++) {
                    RecordVo interviewerVo = new RecordVo("jp_interviewer");
                    interviewerVo.setObject("z8101", z8101);
                    interviewerVo.setObject("interviewer", PubFunc.decrypt((String) interviewerData.get(j)));
                    interviewerVo.setObject("isjoin", "1");
                    dao.addValueObject(interviewerVo);
                }
            }

        } catch (Exception e) {
            throw new GeneralException("saveCreatePostDataError");
        }


    }
    /**
     * 检验发布申请岗位状态
     * @param postList
     */
    @Override
    public boolean checkPostStatus(ArrayList postList) throws GeneralException{
        boolean isExitsFlag = false;
        StringBuffer checkSql = new StringBuffer();
        checkSql.append("select Z81.Z8101,Z81.Z8103 from z81,z81 temp where Z81.E01A1 = ? and z81.Z8101 = temp.Z8101 and z81.Z8101 = (select max(Z8101) from z81 where Z81.E01A1 = ?)");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try {
            for(int i=0;i<postList.size();i++){
                ArrayList paramList = new ArrayList();
                //String str = postList.get(i);
                paramList.add(postList.get(i));
                paramList.add(postList.get(i));
                rowSet = dao.search(checkSql.toString(),paramList);
                while (rowSet.next()){
                    String status = rowSet.getString("z8103");
                    if(StringUtils.equalsIgnoreCase(status,"01") || StringUtils.equalsIgnoreCase(status,"02") || StringUtils.equalsIgnoreCase(status,"03")
                            || StringUtils.equalsIgnoreCase(status,"04")|| StringUtils.equalsIgnoreCase(status,"05") || StringUtils.equalsIgnoreCase(status,"10")){
                        isExitsFlag = true;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            String msg = "checkStatusSqlFail";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return isExitsFlag;
    }

    @Override
    public List getIngE01a1() throws GeneralException {
        RowSet rowSet = null;
        List e01a1List = new ArrayList();
        try {
            StringBuffer checkSql = new StringBuffer();
            checkSql.append("select e01a1 from z81 where z8103 in ('01','02','03','04','05','10')");
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(checkSql.toString());
            while (rowSet.next()) {
                String e01a1 = rowSet.getString("e01a1");
                e01a1List.add(e01a1);
            }

        } catch (Exception e) {
            throw new GeneralException("getIngE01a1Error");
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return e01a1List;
    }

    @Override
    public List isHaveIngPersonInPostList(String ids,String status) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try{
            StringBuffer checkSql = new StringBuffer();
            checkSql.append("select z8305 from z83 where Z8101 in (");
            List<String> checkList = new ArrayList<String>();
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                id = PubFunc.decrypt(id);
                checkList.add(id);
                checkSql.append("?").append(",");
            }
            checkSql.setLength(checkSql.length() - 1);
            checkSql.append(") and z8303 in( ").append(status).append(")");
            checkSql.append(" group by z83.Z8305,z83.Z8101 order by z83.Z8101 desc");
            //检验竞聘岗位下是否存在报名审批中的人员，若存在则不允许结束
            rs = dao.search(checkSql.toString(), checkList);
            while(rs.next()){
            	CodeItem codeItem = AdminCode.getCode("@K", rs.getString("z8305"));
                list.add(codeItem.getCodename());
            }
        }catch (Exception e){
            throw new GeneralException("isHaveIngPersonInPostError");
        }finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    @Override
    public void endApplyTask(String postIds) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String[] idArray = postIds.split(",");
        StringBuffer postIdBuffer = new StringBuffer();
        for (String id : idArray) {
            id = PubFunc.decrypt(id);
            postIdBuffer.append("'").append(id).append("'").append(",");
        }
        postIdBuffer.setLength(postIdBuffer.length() - 1);
        try {
            //查询 当前竞聘人员报名审批中的
            JSONObject fieldRelation = TalentMarketsUtils.getApplyTemplateRelation();
            String tabid = TalentMarketsUtils.getApplyTemplate();
            WF_Instance ins = new WF_Instance(Integer.parseInt(tabid), this.conn, this.userView);
            TemplateParam paramBo = new TemplateParam(this.conn, this.userView, Integer.parseInt(tabid));
            String z8101Field = fieldRelation.getString("z8101");
            String z8301Field = fieldRelation.getString("z8301");
            StringBuffer sql = new StringBuffer();
            sql.append("select U.ins_id,U.tabid,T.task_id");
            sql.append(",").append(z8101Field).append(",").append(z8301Field);;
            sql.append(" from t_wf_task T,t_wf_instance U,templet_").append(tabid).append(" tt");
            sql.append(" where T.ins_id = U.ins_id and tt.ins_id =T.ins_id and task_topic not like '%共0人%' and task_topic not like '%共0条%'");
            sql.append(" and task_type = '2'").append(" and finished = '2'").append(" and (task_state = '3' or task_state = '6')");
            sql.append(" and U.tabid = ").append(tabid);
            sql.append(" and(").append(z8101Field).append(" in (").append(  postIdBuffer.toString() + "))");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String taskId = rs.getString("task_id");
                //改变竞聘报名人员 状态为报名未通过
                String z8101 = rs.getString(z8101Field);
                String z8301 = rs.getString(z8301Field);
                RecordVo z83vo = new RecordVo("z83");
                z83vo.setObject("z8101",z8101);
                z83vo.setObject("z8301",z8301);
                if(dao.isExistRecordVo(z83vo)){
                    z83vo.setObject("z8303","03");
                    dao.updateValueObject(z83vo);
                }
                //终止流程
                if (paramBo.getIsAotuLog() || paramBo.getIsRejectAotuLog()) {//终止任务调用删除变动日志，删除对应单子的变动信息
                    TempletChgLogBo chgLogBo = new TempletChgLogBo(conn, this.userView, paramBo);
                    chgLogBo.deleteChangeInfoInProcess(taskId, tabid);
                }
                ins.processEnd(Integer.valueOf(taskId), Integer.valueOf(tabid), userView, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPos.endApplyTaskError");
        }
    }

    @Override
    public void assembleDescAndEncryptData(Map<String, String> z8115Map, Map<String, String> e01a1Map) throws GeneralException {
        try {
            //回写描述
            for (String key : z8115Map.keySet()) {
                String value = z8115Map.get(key);
                //根据编码获取描述
                StringBuffer desc = new StringBuffer();
                if (StringUtils.isNotEmpty(value)) {
                    String itemIds = value.split("`")[0];
                    String[] itemIdArray = itemIds.split(",");
                    for (String item : itemIdArray) {
                        if (StringUtils.isEmpty(item)) {
                            continue;
                        }
                        String itemdesc = AdminCode.getCodeName("UN", item);
                        if (StringUtils.isBlank(itemdesc)) {
                            itemdesc = AdminCode.getCodeName("UM", item);
                        }
                        desc.append(itemdesc).append(",");
                    }
                    if (desc.length() > 0) {
                        desc.setLength(desc.length() - 1);
                    }
                }
                z8115Map.put(key, desc.toString());
            }

            //回写加密岗位编号
            for (String key : e01a1Map.keySet()) {
                String value = e01a1Map.get(key);
                e01a1Map.put(key, PubFunc.encrypt(value));
            }
        } catch (Exception e) {
            throw new GeneralException("tm.contendPos.assembleDescAndEncryptError");
        }
    }
}

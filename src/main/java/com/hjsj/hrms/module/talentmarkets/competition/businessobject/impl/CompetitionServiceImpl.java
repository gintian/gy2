package com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl;

import com.aspose.words.Document;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.card.businessobject.YkcardOutWord;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.SendEmailBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.*;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Title CompetitionServiceImpl
 * @Description 竞聘人员列表页面接口实现类
 * @Company hjsj
 * @Author wangbs、caoqy
 * @Date 2019/7/24
 * @Version 1.0.0
 */
public class CompetitionServiceImpl implements CompetitionService {

    private UserView userView;
    private Connection conn;
    private ContentDAO dao;

    /** 栏目设置新增指标 便于拼接sql语句（固定显示项不在内） */
    private List<FieldItem> newColumnsList = new ArrayList<FieldItem>();

    public CompetitionServiceImpl(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
        this.dao = new ContentDAO(this.conn);
    }

    /**
     * 获取tableConfig
     * @author wangbs
     * @param fromValue   页面从哪来
     * @param statusValue 查询什么状态的数据
     * @param posValue    点击柱子查询某岗位的申报人列表
     * @return String
     */
    @Override
    public String getTableConfig(String fromValue, String statusValue, String posValue) {
        //获取列头
        ArrayList<ColumnsInfo> columnsInfo = getColumnList(posValue);
        //查询table所需数据的sql
        String dataSql = getTableSql(statusValue, PubFunc.decrypt(posValue));
        //创建表格对象
        TableConfigBuilder builder = new TableConfigBuilder(COMPETITIONPSN, columnsInfo, COMPETITIONPSN, this.userView, this.conn);

        //竞聘人员
        builder.setTitle(ResourceFactory.getProperty("talentmarkets.competitionPsn"));
        builder.setPageSize(20);

        //查看历史状态的数据不让其编辑
        if (!HISTORY.equals(statusValue)) {
            builder.setEditable(true);
        }
        //过滤
        builder.setColumnFilter(true);
        //是否有复选框列
        builder.setSelectable(true);
        //锁列
        builder.setLockable(true);
        //右上角的齿轮：栏目设置
        builder.setScheme(true);
        //保存方案按钮 权限控制
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction(SAVE_INIT_PLAN)) {
            builder.setShowPublicPlan(true);
        }
        //栏目设置添加信息集指标
        builder.setItemKeyFunctionId("TM000000005");

        if (StringUtils.isNotBlank(dataSql)) {
            builder.setDataSql(dataSql);
            //排序指标
            builder.setOrderBy(" order by z8303");
        } else {
            builder.setDataList(new ArrayList());
        }

        builder.setTableTools(getButtonList(fromValue, statusValue));

        return builder.createExtTableConfig();
    }

    /**
     * 查询table所需数据的sql
     * @author wangbs
     * @param statusValue 查询什么状态的数据
     * @param posValue 点击柱子查询某岗位的申报人列表
     * @return String
     */
    private String getTableSql(String statusValue, String posValue) {
        StringBuffer dataSql = new StringBuffer();
        //获取登录认证库
        String loginTableStr = TalentMarketsUtils.getLoginTableStr();

        if (StringUtils.isNotBlank(loginTableStr)) {
            String[] loginTableArr = loginTableStr.split(",");
            for (int i = 0; i < loginTableArr.length; i++) {
                //存放每个子集需要查询的指标sql
                Map subsetFieldMap = new HashMap();
                String daPre = loginTableArr[i];
                if (i > 0) {
                    dataSql.append(" union all ");
                }

                dataSql.append("select '" + daPre + "' nbase,c.a0100,c.z8101,c.z8301,c.z8303,c.z8305,c.a0101,c.b0110,c.e0122,c.e01a1,c.z8307,c.z8309");

                //外层select拼接栏目设置中新增的指标
                for (FieldItem fieldItem : this.newColumnsList) {
                    String setId = fieldItem.getFieldsetid();
                    String itemId = fieldItem.getItemid();
                    if (StringUtils.equalsIgnoreCase("Z83", setId) || StringUtils.equalsIgnoreCase("A01", setId)) {
                        dataSql.append(",c." + itemId);
                    } else if (!StringUtils.equalsIgnoreCase("A01", setId)) {
                        String tableName = "," + setId + ".";
                        dataSql.append(tableName + itemId);

                        String subsetField = (String) subsetFieldMap.get(setId);
                        //如果未存储该子集sql
                        if (StringUtils.isBlank(subsetField)) {
                            subsetFieldMap.put(setId, itemId);
                        }else{
                            subsetFieldMap.put(setId, subsetField + "," + itemId);
                        }
                    }
                }

                //内层select拼接栏目设置中新增的Z8、A01指标
                dataSql.append(" from(select a.z8101,a.z8301,a.z8303,a.z8305,b.a0101,b.b0110,b.e0122,b.e01a1,a.z8307,a.z8309,b.a0100");
                for (FieldItem fieldItem : this.newColumnsList) {
                    String setId = fieldItem.getFieldsetid();
                    String itemId = fieldItem.getItemid();
                    if (StringUtils.equalsIgnoreCase("Z83", setId)) {
                        dataSql.append(",a." + itemId);
                    } else if (StringUtils.equalsIgnoreCase("A01", setId)) {
                        dataSql.append(",b." + itemId);
                    }
                }

                dataSql.append(" from Z83 a left join " + daPre + "A01 b on a.Z8301 = b.guidkey where b.a0101 is not null) c ");
                //存放已连接（left join）的子集
                List childSetList = new ArrayList();
                //left join 查询相关子集每个人i9999最大的所有指标
                for (FieldItem fieldItem : this.newColumnsList) {
                    String setId = fieldItem.getFieldsetid();
                    if (!StringUtils.equalsIgnoreCase("A01", setId) && !StringUtils.equalsIgnoreCase("Z83", setId)) {
                        //已经查询过该子集信息continue
                        if (childSetList.contains(setId)) {
                            continue;
                        }
                        childSetList.add(setId);
                        dataSql.append(" left join (select * from (select a0100," + subsetFieldMap.get(setId) + ",");
                        //rownum是oracle的关键字  rowcount是sql的关键字都不能用作别名 否则报错  此处用rowC
                        //根据a0100分组i9999降序并编号，取rowC = 1的也就是i9999最大的那条记录数据
                        dataSql.append(" row_number() over (partition by a0100 order by i9999 desc) as rowC from " + daPre + setId + ") temp where rowC = 1) ");
                        dataSql.append(setId + " on " + setId + ".a0100=c.a0100 ");
                    }
                }

                //获取组织机构权限sql
                String conditionSql = TalentMarketsUtils.getConditionsSql(this.userView, "4", "z8305","c");
                dataSql.append("where (" + conditionSql + ")");

                if (StringUtils.isNotBlank(statusValue)) {
                    //点击柱子穿透条件
                    if (StringUtils.isNotBlank(posValue)) {
                        dataSql.append(" and c.z8101='" + posValue + "' ");
                    }else{
                        //点击门户页面上方的方块进来
                        dataSql.append(" and c.z8101 in (select z8101 from z81 where z8103 in");
                        if (CURRENT.equals(statusValue)) {
                            dataSql.append("('04','05'))");
                        } else if (HISTORY.equals(statusValue)) {
                            dataSql.append(TalentMarketsUtils.END_STATUS + ")");
                        }
                    }
                }
            }
        }
        return dataSql.toString();
    }

    /**
     * 拼装生成数据页面列头信息
     * @author wangbs
     * @param posValue 岗位编号
     * @return ArrayList
     */
    private ArrayList<ColumnsInfo> getColumnList(String posValue) {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try {
            // 竞聘岗位编号
            Map extraParam = new HashMap();
            extraParam.put("encrypted", true);
            ColumnsInfo columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8101","z83").clone(), ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            //人员guidkey--z8301
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8301","z83").clone(), ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);

            //人员编号 a0100
            extraParam.put("encrypted", true);
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("a0100").clone(), ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);

            //人员库 nbase
            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "nbase", "", ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            // 状态
            extraParam.put("editableValidFunc", "false");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8303").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);

            // 竞聘岗位
            if (StringUtils.isBlank(posValue)) {
                extraParam.put("doFilterOnLoad", true);
            }
            extraParam.put("ctrltype", "3");
            extraParam.put("nmodule", "4");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8305").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);
            extraParam.remove("nmodule");
            extraParam.remove("ctrltype");
            extraParam.remove("doFilterOnLoad");

            // 姓名
            extraParam.put("rendererFunc", "CompetitionScope.renderNameColumn");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("a0101").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, extraParam);
            list.add(columnInfo);
            extraParam.remove("rendererFunc");

            // 单位
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("b0110").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);

            // 部门
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("e0122").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);

            // 岗位
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("e01a1").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            // 面试分数
            if (TalentMarketsUtils.getOpenInterview(this.conn)) {
                extraParam.put("textAlign", "right");
                extraParam.put("decimalWidth", 2);
                extraParam.put("columnLength", 3);
                extraParam.put("validFunc", "CompetitionScope.gradeValidate");
                extraParam.put("defaultValue","0");
                columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8307").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, extraParam);
                list.add(columnInfo);
                extraParam.clear();
            }

            // 审批流程
            extraParam.put("editableValidFunc", "false");
            extraParam.put("textAlign", "center");
            extraParam.put("filterable", false);
            extraParam.put("rendererFunc", "CompetitionScope.approvalProcessRenderFunc");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8309").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);

            TableFactoryBO tableFactoryBo = new TableFactoryBO(COMPETITIONPSN, this.userView, this.conn);
            //获取栏目设置方案
            HashMap schemeData = tableFactoryBo.getTableLayoutConfig();
            if (MapUtils.isNotEmpty(schemeData)) {
                ArrayList columns = new ArrayList();
                String mergedesc = "";
                int mergedescIndex = 0;
                int num = 0;
                //方案号
                Integer scheme_str = (Integer) schemeData.get("schemeId");
                int schemeId = scheme_str.intValue();
                //根据方案号获取列信息
                ArrayList<ColumnConfig> columnConfigLst = tableFactoryBo.getTableColumnConfig(schemeId);
                for (int i = 0; i < columnConfigLst.size(); i++) {
                    ColumnConfig columnConfig = columnConfigLst.get(i);
                    String setFieldId = "," + columnConfig.getFieldsetid().toUpperCase() + ":" + columnConfig.getItemid().toUpperCase() + ",";
                    if (StringUtils.contains("," + DEFALUT_FIELDS.toUpperCase() + ",", setFieldId)) {
                        continue;
                    }
                    FieldItem fieldItem = DataDictionary.getFieldItem(columnConfig.getItemid(), columnConfig.getFieldsetid());
                    if (fieldItem != null) {
                        String fieldSetId = fieldItem.getFieldsetid();
                        columnInfo = new ColumnsInfo(fieldItem);
                        columnInfo.setColumnWidth(columnConfig.getDisplaywidth());
                        columnInfo.setTextAlign(columnConfig.getAlign() + "");

                        //人员信息集指标不允许编辑
                        if (!StringUtils.equalsIgnoreCase(fieldSetId, "z83")) {
                            columnInfo.setEditableValidFunc("false");
                        }

                        String order = "";
                        if (StringUtils.equalsIgnoreCase(columnConfig.getIs_order(), "1")) {
                            order = "true";
                        } else {
                            order = "false";
                        }
                        columnInfo.setSortable(Boolean.parseBoolean(order));
                        if (columnConfig.getIs_sum() == "1") {
                            columnInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
                        } else if (columnConfig.getIs_sum() == "2") {
                            columnInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
                        } else if (columnConfig.getIs_sum() == "3") {
                            columnInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
                        } else if (columnConfig.getIs_sum() == "4") {
                            columnInfo.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
                        }
                        if ("0".equalsIgnoreCase(columnConfig.getIs_fromdict())) {
                            columnInfo.setFromDict(Boolean.parseBoolean("false"));
                        }
                        if (StringUtils.isNotBlank(columnConfig.getMergedesc())) {
                            if (mergedesc.equalsIgnoreCase(columnConfig.getMergedesc()) && mergedescIndex == i - 1) {
                                TalentMarketsUtils.addTopHeadList(columns, mergedesc, mergedescIndex, num, columnInfo);
                                num += 1;
                                mergedescIndex = i;
                            } else {
                                mergedesc = columnConfig.getMergedesc();
                                mergedescIndex = i;
                            }
                        }
                        list.add(columnInfo);
                        this.newColumnsList.add(fieldItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取工具栏按钮（需校验是否有按钮权限）
     * @author wangbs
     * @param from 页面来源
     * @param status 数据状态
     * @return ArrayList
     */
    private ArrayList getButtonList(String from,String status) {
        ArrayList buttonList = new ArrayList();
        //功能导航按钮下拉菜单拼装
        ArrayList list = new ArrayList();

        //导出Excel
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction(EXPORT_EXCEL_FUNCID)) {
            LazyDynaBean oneBean = new LazyDynaBean();
            oneBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionPsn.exportExcel"));
            oneBean.set("fntype", "export");
            oneBean.set("cusMenu", "cusMenu");
            list.add(oneBean);
        }

        //导出简历PDF
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction(EXPORT_PDF_FUNCID)) {
            LazyDynaBean oneBean = new LazyDynaBean();
            oneBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionPsn.exportAttachPdf"));
            oneBean.set("handler", "CompetitionScope.exportPDF");
            list.add(oneBean);
        }

        //功能导航
        if (CollectionUtils.isNotEmpty(list)) {
            buttonList.add(TalentMarketsUtils.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), list));
            buttonList.add("-");
        }

        //非历史状态数据才显示按钮
        int cusBtnCount = 0;
        if (StringUtils.isBlank(status) || StringUtils.equals("apply", status)) {
            if (TalentMarketsUtils.getOpenInterview(this.conn)) {
                //导入成绩
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction(IMPORT_GRADE)) {
                    buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.importGrade"), "CompetitionScope.importScore"));
                    cusBtnCount++;
                }
                //面试通过
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction(INTERVIEW_PASS)) {
                    buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.interviewPass"), "CompetitionScope.interviewPass"));
                    cusBtnCount++;
                }
                //面试未通过
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction(INTERVIEW_NOT_PASS)) {
                    buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.interviewNotPass"), "CompetitionScope.interviewNotPass"));
                    cusBtnCount++;
                }
            }

            //拟录用审批
            if (this.userView.isSuper_admin() || this.userView.hasTheFunction(DRAFT_EMPLOY_APPROVE)) {
                buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.draftEmployApproval"), "CompetitionScope.draftEmployApprovalFunc"));
                cusBtnCount++;
            }

            //保存
            buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.save"), "CompetitionScope.savePsnBtnData"));
            cusBtnCount++;
        }

        if (StringUtils.isNotBlank(from)) {
            if (cusBtnCount > 0) {
                buttonList.add("-");
            }
            //返回
            buttonList.add(newButton(ResourceFactory.getProperty("talentmarkets.competitionPsn.return"), "CompetitionScope.returnHomePage('" + from + "')"));
        }

        //查询框
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("TM000000004");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        //请输入姓名或竞聘岗位名称
        querybox.setText(ResourceFactory.getProperty("talentmarkets.competitionPsn.fillNameOrPos"));
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 生成按钮
     * @author wangbs
     * @param text 按钮显示文字
     * @param handler 按钮触发方法
     * @return ButtonInfo
     */
    private ButtonInfo newButton(String text, String handler) {
        ButtonInfo button = new ButtonInfo(text, handler);
        button.setGetData(true);
        return button;
    }

    /**
     * 保存人员列表修改的数据
     * @author wangbs
     * @param modifyDataList 改动的数据
     * @return String
     * @throws GeneralException 抛出异常
     */
    @Override
    public void saveGridData(List<MorphDynaBean> modifyDataList) throws GeneralException {
        List voList = new ArrayList();
        try {
            for (MorphDynaBean record : modifyDataList) {
                RecordVo vo = new RecordVo("Z83");

                String z8101 = PubFunc.decrypt((String) record.get("z8101"));
                String z8301 = (String) record.get("z8301");
                vo.setString("z8101", z8101);
                vo.setString("z8301", z8301);
                vo = dao.findByPrimaryKey(vo);

                Map<String, Object> map = PubFunc.DynaBean2Map(record);
                for (String key: map.keySet()) {
                    if ("z8101".equalsIgnoreCase(key) || "z8301".equalsIgnoreCase(key)) {
                        continue;
                    }
                    FieldItem fieldItem = DataDictionary.getFieldItem(key, "z83");
                    //代码型特殊处理
                    if (!StringUtils.equals("0", fieldItem.getCodesetid())) {
                        String value = (String) map.get(key);
                        value = value.split("`")[0];
                        vo.setString(key, value);
                    }else{
                        vo.setObject(key, map.get(key));
                    }
                }
                voList.add(vo);
            }
            this.dao.updateValueObject(voList);
        } catch (Exception e) {
            e.printStackTrace();
            //保存失败
            throw new GeneralException("tm.saveFail");
        }
    }

    /**
     * 根据输入的内容生成查询条件
     * @author wangbs
     * @param valueList 输入的内容集合
     * @return String 条件语句
     */
    @Override
    public String getSqlCondition(List<String> valueList) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < valueList.size(); i++) {
            String queryVal = valueList.get(i);
            String valueSql = "'%" + queryVal + "%'";
            if (i == 0) {
                buf.append(" and (");
            } else {
                buf.append(" and ");
            }
            buf.append("(myGridData.a0101 like ");
            buf.append(valueSql);
            buf.append(" or lower((select codeitemdesc from organization ");
            buf.append("where codesetid = '@K' and codeitemid = myGridData.z8305)) like " + valueSql + ")");
        }
        if (valueList.size() > 0) {
            //组装成一个大条件
            buf.append(")");
        }
        return buf.toString();
    }

    /**
     * 根据竞聘岗位编号获取面试页面信息
     * @author wangbs
     * @param selectId 竞聘岗位编号
     * @return String
     * @throws GeneralException 抛出异常
     */
    @Override
    public Map getInterviewPageInfo(String selectId) throws GeneralException {
        Map interviewPageInfo = new HashMap();
        try {
            Map interviewersMap = getInterviewersInfo(selectId);
            List interviewersList = (ArrayList) interviewersMap.get("interviewersList");
            //面试官是否是继承上次标识
            boolean extendFlag = (Boolean) interviewersMap.get("extendFlag");

            Map candidatesTableInfo = getCandidatesTableInfo(selectId, 1, 10);

            //根据组织机构业务范围过滤可选面试官
            String privOrgIdStr = TalentMarketsUtils.getAllPrivOrgIdStr(this.userView);

            interviewPageInfo.put("interviewers", interviewersList);
            interviewPageInfo.put("extendFlag", extendFlag);
            interviewPageInfo.put("privOrgIdStr", privOrgIdStr);
            interviewPageInfo.put("candidatesTableInfo", candidatesTableInfo);
        } catch (GeneralException e) {
            e.printStackTrace();
            //获取信息出错
            throw new GeneralException(e.getErrorDescription());
        }
        return interviewPageInfo;
    }

    /**
     * 获取查询sql
     * @author wangbs
     * @param list sql参数list
     * @param selectId 选择的岗位编号
     * @param currentDataFlag 是否查selectId的数据
     * @return String
     */
    private String getPosDataSql(List list, String selectId, Boolean currentDataFlag) {
        StringBuffer sql = new StringBuffer();
        //得到系统中配置的邮箱电话指标
        String emailId = ConstantParamter.getEmailField().toLowerCase();
        String phoneId = ConstantParamter.getMobilePhoneField().toLowerCase();
        //获取登录认证库
        String loginTableStr = TalentMarketsUtils.getLoginTableStr();
        if (StringUtils.isNotBlank(loginTableStr)) {
            String[] loginTableArr = loginTableStr.split(",");
            for (int i = 0; i < loginTableArr.length; i++) {
                String daPre = loginTableArr[i];
                if (i > 0) {
                    sql.append(" union all ");
                }
                sql.append("select a.z8101,a.create_time,b.interviewer,c.a0101,c.a0100,");
                //未配置邮箱指标
                if (StringUtils.isBlank(emailId)) {
                    sql.append("'' email");
                }else{
                    sql.append("c." + emailId + " email");
                }

                //未配置电话指标
                if (StringUtils.isBlank(phoneId)) {
                    sql.append(",'' phone");
                }else{
                    sql.append(",c." + phoneId + " phone");
                }

                sql.append(",'" + daPre + "' nbsePre ");
                sql.append("from (select z8101,create_time from z81 where ");
                if (currentDataFlag) {
                    sql.append("z8101 = ?) a ");
                }else{
                    sql.append("e01a1 = (select e01a1 from z81 where z8101 = ?) and z8103 in" + TalentMarketsUtils.END_STATUS + ") a ");
                }
                sql.append("left join jp_interviewer b on a.z8101 = b.z8101 ");
                sql.append("left join " + daPre + "a01 c on b.interviewer=c.guidkey ");
                sql.append("where b.interviewer is not null and c.a0101 is not null ");
                list.add(selectId);
            }
        }
        if (StringUtils.isNotBlank(sql.toString())) {
            sql.append("order by a.z8101,a.create_time desc");
        }
        return sql.toString();
    }
    /**
     * 根据竞聘岗位编号获取面试官信息
     * @author wangbs
     * @param selectId 竞聘岗位编号
     * @return Map
     * @throws GeneralException 抛出异常
     */
    private Map getInterviewersInfo(String selectId) throws GeneralException{
        List list = new ArrayList();
        Map interviewersMap = new HashMap();
        //默认不是继承的上次的面试官
        interviewersMap.put("extendFlag", false);
        List interviewersList = new ArrayList();

        try {
            String posId = "";
            String sql = getPosDataSql(list, selectId, true);
            if (StringUtils.isNotBlank(sql)) {
                posId = assemblyData(sql, list, interviewersList);
            }

            if (CollectionUtils.isEmpty(interviewersList)) {
                list.clear();
                sql = getPosDataSql(list, selectId, false);
                posId = assemblyData(sql, list, interviewersList);
            }
            if (StringUtils.isNotBlank(posId) && !StringUtils.equalsIgnoreCase(posId, selectId)) {
                interviewersMap.put("extendFlag", true);
            }
            interviewersMap.put("interviewersList", interviewersList);
        } catch (Exception e) {
            e.printStackTrace();
            //获取面试官信息失败
            throw new GeneralException("tm.contendPsn.getInterviewerInfoError");
        }
        return interviewersMap;
    }

    /**
     * 拼装面试官数据
     * @author wangbs
     * @param sql SQL语句
     * @param list sql参数list
     * @param interviewersList 面试官集合
     * @return String
     * @throws GeneralException 抛出异常
     */
    private String assemblyData(String sql,List list, List interviewersList) throws GeneralException {
        RowSet rs = null;
        String posId = "";
        try {
            if (StringUtils.isNotBlank(sql)) {
                rs = this.dao.search(sql, list);
                while (rs.next()) {
                    String z8101 = rs.getString("z8101");

                    if (StringUtils.isBlank(posId)) {
                        posId = z8101;
                    } else {
                        if (!StringUtils.equals(posId, z8101)) {
                            break;
                        }
                    }
                    Map interviewerInfo = new HashMap();
                    //人员库前缀
                    String nbsePre = rs.getString("nbsePre");

                    //面试官唯一值
                    String guidkey = rs.getString("interviewer");
                    String interviewerEmail = StringUtils.isNotBlank(rs.getString("email")) ? rs.getString("email") : "";
                    String interviewerPhone = StringUtils.isNotBlank(rs.getString("phone")) ? rs.getString("phone") : "";
                    //姓名
                    String interviewerName = rs.getString("a0101");
                    //已存在的人不在personpicker选人控件中显示
                    String nbaseA0100 = PubFunc.encrypt(nbsePre + rs.getString("a0100"));

                    interviewerInfo.put("nbaseA0100", nbaseA0100);
                    interviewerInfo.put("interviewerGuidkey", PubFunc.encrypt(guidkey));
                    interviewerInfo.put("interviewerEmail", interviewerEmail);
                    interviewerInfo.put("interviewerPhone", interviewerPhone);
                    interviewerInfo.put("interviewerName", interviewerName);
                    PhotoImgBo imgBo = new PhotoImgBo(this.conn);
                    interviewerInfo.put("interviewerPhotoPath", imgBo.getPhotoPath(nbsePre, rs.getString("a0100")));

                    interviewersList.add(interviewerInfo);
                }
            }
            return posId;
        } catch (SQLException e) {
            e.printStackTrace();
            //获取面试官信息失败
            throw new GeneralException("tm.contendPsn.getInterviewerInfoError");
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 获取面试安排候选人table列
     * @author wangbs
     * @return ArrayList
     */
    private ArrayList getInterviewColumnList() {
        ArrayList list = new ArrayList();
        Map extraParam = new HashMap();
        ColumnsInfo columnInfo;
        try {
            //人员guidkey--z8301
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8301","z83").clone(), ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);

            //更新标识
            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "changeflag", "", ColumnsInfo.LOADTYPE_ONLYLOAD, 0, extraParam);
            list.add(columnInfo);

            //姓名
            extraParam.put("editableValidFunc", "false");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("a0101").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 80, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            //面试日期
            String interviewDate = ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewDate");
            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "interviewdate", interviewDate, ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            //面试时间
            String interviewTime = ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewTime");
            extraParam.put("editableValidFunc", "false");
            extraParam.put("rendererFunc", "InterviewArrange.interviewTimeRenderFunc");
            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "interviewtime", interviewTime, ColumnsInfo.LOADTYPE_ALWAYSLOAD, 265, extraParam);
            list.add(columnInfo);
            extraParam.clear();

            //面试地点
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z0503").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 250, extraParam);
            list.add(columnInfo);

            //应聘状态
            extraParam.put("editableValidFunc", "false");
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8303").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 150, extraParam);
            list.add(columnInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据posid获取候选人信息tablebuilderconfig
     * @author wangbs
     * @param posId      岗位编号
     * @param targetPage 加载哪一页
     * @param pageSize   一页几条
     * @return Map
     * @throws GeneralException 抛出异常
     */
    @Override
    public Map getCandidatesTableInfo(String posId, int targetPage, int pageSize) throws GeneralException {
        Map tableInfo = new HashMap();
        TableConfigBuilder builder = new TableConfigBuilder(INTERVIEWPLAN, this.getInterviewColumnList(), INTERVIEWPLAN, this.userView, this.conn);
        try {
            ArrayList dataList = this.getCandidatesDataList(posId);
            builder.setPageSize(pageSize);
            builder.setCurrentPage(targetPage);

            builder.setSortable(false);
            builder.setEditable(true);
            //锁列
            builder.setLockable(true);

            //是否有复选框列 潍柴需求勾选谁才给谁发邮件，不勾选给出提示
            builder.setSelectable(true);

            builder.setDataList(dataList);

            tableInfo.put("candidatesDataList", dataList);
            tableInfo.put("candidatesTableConfig", builder.createExtTableConfig());
        } catch (GeneralException e) {
            e.printStackTrace();
            throw new GeneralException(e.getErrorDescription());
        }
        return tableInfo;
    }

    /**
     * 根据竞聘岗位编号获取候选人信息
     * @author wangbs
     * @param selectId 竞聘岗位编号
     * @return ArrayList
     * @throws GeneralException 抛出异常
     */
    private ArrayList getCandidatesDataList(String selectId) throws GeneralException {
        ArrayList candidatesList = new ArrayList();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        //得到系统中配置的邮箱电话指标
        String emailId = ConstantParamter.getEmailField().toLowerCase();
        String phoneId = ConstantParamter.getMobilePhoneField().toLowerCase();
        try {
            //获取登录认证库
            String loginTableStr = TalentMarketsUtils.getLoginTableStr();
            if (StringUtils.isNotBlank(loginTableStr)) {
                String[] loginTableArr = loginTableStr.split(",");
                for (int i = 0; i < loginTableArr.length; i++) {
                    String daPre = loginTableArr[i];
                    if (i > 0) {
                        sql.append(" union all ");
                    }
                    sql.append("select b.a0101,");

                    //未配置邮箱指标
                    if (StringUtils.isBlank(emailId)) {
                        sql.append("'' email");
                    }else{
                        sql.append("b." + emailId + " email");
                    }

                    //未配置电话指标
                    if (StringUtils.isBlank(phoneId)) {
                        sql.append(",'' phone");
                    }else{
                        sql.append(",b." + phoneId + " phone");
                    }

                    sql.append(",c.starttime,c.endtime,c.address,a.z8303,b.guidkey,");
                    sql.append("b.a0100,'" + daPre + "' nbase,");
                    sql.append("case when c.starttime is null then 'insert' else 'update' end changeflag ");
                    sql.append("from Z83 a ");
                    sql.append("left join " + daPre + "A01 b on a.z8301 = b.guidkey and a.z8101 ='" + selectId + "' ");
                    sql.append("left join jp_interview_notice c on a.z8101=c.z8101 and b.guidkey=c.z8301 ");
                    sql.append("where a.z8101 = '" + selectId + "' and a.z8303='02' ");
                    sql.append("and b.a0101 is not null ");
                }
            }
            if (StringUtils.isNotBlank(sql.toString())) {
                sql.append("order by z8303");
                rs = this.dao.search(sql.toString());
                while (rs.next()) {
                    LazyDynaBean candidateInfo = new LazyDynaBean();
                    String z8301 = rs.getString("guidkey");
                    String nbase = rs.getString("nbase");
                    String a0100 = rs.getString("a0100");
                    String emailValue = StringUtils.isNotBlank(rs.getString("email")) ? rs.getString("email") : "";
                    String phoneValue = StringUtils.isNotBlank(rs.getString("phone")) ? rs.getString("phone") : "";
                    String changeFlag = rs.getString("changeflag");
                    String name = rs.getString("a0101");
                    //面试开始时间
                    String startTime = "";
                    if (rs.getTimestamp("starttime") != null) {
                        startTime = DateStyle.dateformat(rs.getTimestamp("starttime"), "yyyy-MM-dd HH:mm:ss.mmm");
                    }
                    //面试结束时间
                    String endTime = "";
                    if (rs.getTimestamp("endtime") != null) {
                        endTime = DateStyle.dateformat(rs.getTimestamp("endtime"), "yyyy-MM-dd HH:mm:ss.mmm");
                    }
                    //面试地点
                    String address = StringUtils.isNotBlank(rs.getString("address")) ? rs.getString("address") : "";
                    //竞聘状态
                    String status = rs.getString("z8303");

                    //面试日期 年-月-日
                    String interviewDate = StringUtils.isNotBlank(startTime) ? startTime.substring(0, 10) : "";

                    startTime = StringUtils.isNotBlank(startTime) ? startTime.substring(11, 16) : "";
                    endTime = StringUtils.isNotBlank(endTime) ? endTime.substring(11, 16) : "";

                    String interviewTime = StringUtils.isNotBlank(startTime) ? startTime + "-" + endTime : "00:00-00:00";
                    candidateInfo.set("a0101", name);
                    candidateInfo.set("interviewdate", interviewDate);
                    candidateInfo.set("interviewtime", interviewTime);
                    candidateInfo.set("z0503", address);
                    candidateInfo.set("z8303", status);
                    candidateInfo.set("z8301", z8301);
                    candidateInfo.set("emailValue", emailValue);
                    candidateInfo.set("phoneValue", phoneValue);
                    candidateInfo.set("nbaseA0100", PubFunc.encrypt(nbase + a0100));
                    candidateInfo.set("changeflag", changeFlag);

                    candidatesList.add(candidateInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //获取候选人信息失败
            throw new GeneralException("tm.contendPsn.getCandidateInfoError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return candidatesList;
    }

    /**
     * 保存面试安排信息
     * @author wangbs
     * @param compePosNum 竞聘岗位编号
     * @param interviewPlan 面试安排信息
     * @param extendFlag 面试官是否是继承上次标识
     * @return void
     * @throws GeneralException 抛出异常
     */
    @Override
    public void saveInterviewPlan(String compePosNum, Map interviewPlan, boolean extendFlag) throws GeneralException {
        try {
            List<String> delInterviewersList = (ArrayList<String>) interviewPlan.get("delInterviewers");
            List addInterviewersList = (ArrayList) interviewPlan.get("addInterviewers");
            List candidatesDataList = (ArrayList) interviewPlan.get("candidatesData");

            // 删除前台去掉的面试官
            delInterviewers(compePosNum, delInterviewersList);

            //如果是继承过上次的面试官，上次的面试官也存进去
            if (extendFlag) {
                for (String guidkey_e : delInterviewersList) {
                    if (!addInterviewersList.contains(guidkey_e)) {
                        addInterviewersList.add(guidkey_e);
                    }
                }
            }

            //新增面试官
            if (CollectionUtils.isNotEmpty(addInterviewersList)) {
                addInterviewers(compePosNum, addInterviewersList);
            }
            saveCandidatesData(compePosNum, candidatesDataList);

        } catch (GeneralException e) {
            e.printStackTrace();
            throw new GeneralException(e.getErrorDescription());
        }
    }

    private void setVoValue(RecordVo vo, List oneData) throws GeneralException {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //加上时间
        try {
            vo.setString("username", (String) oneData.get(0));
            Date startTime = sDateFormat.parse((String) oneData.get(1));
            Date endTime = sDateFormat.parse((String) oneData.get(2));
            vo.setDate("starttime", startTime);
            vo.setDate("endtime", endTime);

            vo.setString("address", (String) oneData.get(3));
            vo.setString("send_interviewer", (String) oneData.get(4));
            vo.setString("context", (String) oneData.get(5));
            vo.setString("send_competitor", (String) oneData.get(6));
        } catch (ParseException e) {
            e.printStackTrace();
            //处理候选人信息失败
            throw new GeneralException("tm.contendPsn.handlerCandidateError");
        }
    }
    /**
     * @author wangbs
     * @param compePosNum 岗位编号
     * @param candidatesDataList 候选人信息
     * @return void
     * @throws GeneralException 抛出异常
     */
    @Override
    public void saveCandidatesData(String compePosNum, List candidatesDataList) throws GeneralException {
        try {
            Map operateDbMap = getOperateDbMap(compePosNum, candidatesDataList);
            List batchInsertList = (ArrayList) operateDbMap.get("batchInsertList");
            List batchUpdateList = (ArrayList) operateDbMap.get("batchUpdateList");

            //批量新增面试信息
            List addBatchList = new ArrayList();
            for (int i = 0; i < batchInsertList.size(); i++) {
                RecordVo vo = new RecordVo("jp_interview_notice");
                List oneData = (ArrayList) batchInsertList.get(i);
                setVoValue(vo, oneData);
                vo.setString("z8301", (String) oneData.get(7));
                vo.setString("z8101", (String) oneData.get(8));

                addBatchList.add(vo);
            }
            if (CollectionUtils.isNotEmpty(batchInsertList)) {
                this.dao.addValueObject(addBatchList);
            }

            //批量更新面试信息
            List updateBatchList = new ArrayList();
            for (int i = 0; i < batchUpdateList.size(); i++) {
                RecordVo vo = new RecordVo("jp_interview_notice");
                List oneData = (ArrayList)batchUpdateList.get(i);
                vo.setString("z8301", (String) oneData.get(7));
                vo.setString("z8101", (String) oneData.get(8));
                vo = dao.findByPrimaryKey(vo);
                setVoValue(vo, oneData);

                updateBatchList.add(vo);
            }
            if (CollectionUtils.isNotEmpty(batchUpdateList)) {
                this.dao.updateValueObject(updateBatchList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //处理候选人信息失败
            throw new GeneralException("tm.contendPsn.handlerCandidateError");
        }
    }

    /**
     * 校验发送通知的配置是否配好
     * @author wangbs
     * @param noticeWay 通知方式
     * @return List
     * @throws GeneralException 抛出异常
     */
    @Override
    public List checkSendNoticeServer(String noticeWay) throws GeneralException{
        List errorMsgList = new ArrayList();
        SendEmailBo sendEmailbo = new SendEmailBo(this.conn, this.userView);
        try {
            String[] noticeWayArr = noticeWay.split("`");
            //校验邮箱配置
            if (StringUtils.equalsIgnoreCase(noticeWayArr[0], "true")) {
                String fromAddr = sendEmailbo.getFromAddr();
                if (StringUtils.isBlank(fromAddr)) {
                    //系统未设置邮件服务器！
                    errorMsgList.add("tm.contendPsn.noHaveEmailServer");
                }
            }
            //校验短信配置
            if (StringUtils.equalsIgnoreCase(noticeWayArr[1], "true")) {
                //'短信相关设置未配置！';
                if (!TalentMarketsUtils.isSendSMS()) {
                    errorMsgList.add("tm.contendPsn.messageConfigError");
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            //读取邮箱配置失败
            throw new GeneralException("tm.contendPsn.readEmailConfigError");
        }
        return errorMsgList;
    }


    /**
     * 给面试官和候选人发通知并改变人员状态
     * @author wangbs
     * @param compePosNum 岗位编号
     * @param noticeWay 通知方式
     * @param noticeTitle 通知标题
     * @param noticeContent 通知内容
     * @param posDesc 岗位描述
     * @param interviewersList 面试官信息
     * @param sendCandidatesNoticeList 符合条件的候选人信息
     * @param changeCompeStatusList 需改变状态的人
     * @return List
     */
    @Override
    public List sendNoticeAndChangeStatus(String compePosNum,String noticeWay, String noticeTitle, String noticeContent, String posDesc, List interviewersList, List sendCandidatesNoticeList, List changeCompeStatusList){
        //发送任何一种通知成功都更改状态
        boolean successFlag = false;
        List errorMsgList = new ArrayList();
        try {
            //发送邮件
            String[] noticeWayArr = noticeWay.split("`");
            if (StringUtils.equalsIgnoreCase(noticeWayArr[0], "true")) {
                String mailErrorDesc = sendEmail(noticeTitle, noticeContent, posDesc, interviewersList, sendCandidatesNoticeList);
                if (StringUtils.isNotBlank(mailErrorDesc)) {
                    errorMsgList.add(mailErrorDesc);
                } else {
                    successFlag = true;
                }
            }

            //发送短信
            if (StringUtils.equalsIgnoreCase(noticeWayArr[1], "true")) {
                String msgErrorDesc = sendMessage(noticeContent, posDesc, interviewersList, sendCandidatesNoticeList);
                if (StringUtils.isNotBlank(msgErrorDesc)) {
                    errorMsgList.add(msgErrorDesc);
                } else {
                    successFlag = true;
                }
            }
            //通知发出了就更新状态
            if (successFlag) {
                //更新候选人状态
                changePsnStatus(compePosNum, changeCompeStatusList);
            }
        } catch (GeneralException e) {
            errorMsgList.add(e.getErrorDescription());
            return errorMsgList;
        }
        return errorMsgList;
    }
    /**
     * 更新候选人状态
     * @author wangbs
     * @param compePosNum 岗位编号
     * @param changeCompeStatusList 需改变的人
     * @return void
     * @throws
     */
    private void changePsnStatus(String compePosNum, List changeCompeStatusList) throws GeneralException{
        try {
            //批量更新竞聘状态
            for (int i = 0; i < changeCompeStatusList.size(); i++) {
                //changeCompeStatusList中加入更新状态用到的参数
                List updateList = (ArrayList) changeCompeStatusList.get(i);
                updateList.add(compePosNum);
            }
            String sql1 = "update z83 set z8303='04' where z8301=? and z8101=?";
            if (CollectionUtils.isNotEmpty(changeCompeStatusList)) {
                this.dao.batchUpdate(sql1, changeCompeStatusList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException("更新候选人状态失败！");
        }
    }
    /**
     * 发邮件
     * @author wangbs
     * @param noticeTitle 标题
     * @param noticeContent 内容
     * @param posDesc 岗位描述
     * @param interviewersList 面试官信息
     * @param sendCandidatesNoticeList 候选人信息
     * @return String
     */
    private String sendEmail (String noticeTitle, String noticeContent, String posDesc, List interviewersList, List sendCandidatesNoticeList) {
        SendEmailBo sendEmailbo = new SendEmailBo(this.conn, this.userView);

        //给面试官发送邮件
        for (Object interviewerObj : interviewersList) {
            Map interviewerInfo = PubFunc.DynaBean2Map((MorphDynaBean) interviewerObj);
            String name = (String) interviewerInfo.get("interviewerName");
            String email = (String) interviewerInfo.get("interviewerEmail");
            String content = getInterviewerMailContent(posDesc, name, noticeContent, sendCandidatesNoticeList);

            if (StringUtils.isNotBlank(email)) {
                String errorMsg = sendEmailbo.sendEmail(email, noticeTitle, content);
                if (!StringUtils.equalsIgnoreCase(errorMsg, "1")) {
                    return errorMsg;
                }
            }
        }
        //给候选人发送邮件
        for (Object candidateObj : sendCandidatesNoticeList) {
            Map candidateInfo = PubFunc.DynaBean2Map((MorphDynaBean) candidateObj);
            String name = (String) candidateInfo.get("a0101");
            String email = (String) candidateInfo.get("emailValue");
            String content = getCandidateMailContent(posDesc, name, noticeContent, candidateInfo);

            if (StringUtils.isNotBlank(email)) {
                String errorMsg = sendEmailbo.sendEmail(email, noticeTitle, content);
                if (!StringUtils.equalsIgnoreCase(errorMsg, "1")) {
                    return errorMsg;
                }
            }
        }
        return "";
    }

    /**
     * 获取候选人邮件内容
     * @author wangbs
     * @param posDesc       岗位描述
     * @param name          姓名
     * @param noticeContent 通知内容
     * @param candidateInfo 候选人信息
     * @return String
     */
    private String getCandidateMailContent(String posDesc, String name, String noticeContent, Map candidateInfo) {
        StringBuffer content = new StringBuffer();
        //，您好：
        content.append(name + ResourceFactory.getProperty("talentmarkets.competitionJobs.hello") + "\\n");
        content.append(noticeContent + "\\n");
        //面试岗位：
        content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewPos")+ posDesc + "\\n");
        //面试地点：
        content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewAddress1") + candidateInfo.get("z0503") + "\\n");
        //面试时间：
        content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewTime1"));
        content.append("" + candidateInfo.get("interviewdate") + " " + candidateInfo.get("interviewtime") + "\\n");
        return content.toString().replace("\\n", "\n");
    }

    /**
     * 获取面试官邮件内容
     * @author wangbs
     * @param posDesc 岗位描述
     * @param name 姓名
     * @param noticeContent 通知内容
     * @param sendCandidatesNoticeList 候选人信息
     * @return String
     */
    private String getInterviewerMailContent(String posDesc, String name, String noticeContent, List sendCandidatesNoticeList) {
        StringBuffer content = new StringBuffer();
        content.append(name + ResourceFactory.getProperty("talentmarkets.competitionJobs.hello") + "\\n");
        content.append(noticeContent + "\\n");
        //岗位竞聘，您为面试官，已有人员进入面试安排阶段，面试安排如下：
        content.append(posDesc + ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewerTip") + "\\n\\n");
        content.append("<table cellspacing='0px' style='width:100%;'><tr align='center' style='height: 40px;background:#F0F0F0'>");
        //'姓名'
        content.append(createTdHtml(false, true, 80, ResourceFactory.getProperty("talentmarkets.competitionJobs.name")));
        //'面试日期'
        content.append(createTdHtml(false, true, 200, ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewDate")));
        //'面试时间'
        content.append(createTdHtml(false, true, 250, ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewTime")));
        //'面试地点
        content.append(createTdHtml(true, true, 250, ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewAddress")));
        content.append("</tr>");

        for (Object oneCandidateObj : sendCandidatesNoticeList) {
            Map oneCandidateData = PubFunc.DynaBean2Map((MorphDynaBean) oneCandidateObj);

            content.append("<tr align='center' style='height: 40px;'>");
            content.append(createTdHtml(false, false, 80, (String) oneCandidateData.get("a0101")));
            content.append(createTdHtml(false, false, 200, (String) oneCandidateData.get("interviewdate")));
            content.append(createTdHtml(false, false, 250, (String) oneCandidateData.get("interviewtime")));
            content.append(createTdHtml(true, false, 250, (String) oneCandidateData.get("z0503")));
            content.append("</tr>");
        }
        content.append("</table>");

        return content.toString().replace("\\n", "\n");
    }

    /**
     * 创建tdhtml
     * @author wangbs
     * @param borderRight 是否有右边框
     * @param borderTop 是否有上边框
     * @param minWidth 最小宽度
     * @param text 值
     * @return String
     */
    private String createTdHtml (boolean borderRight, boolean borderTop, int minWidth, String text) {
        StringBuffer tdHtml = new StringBuffer();
        tdHtml.append("<td style='border:1px solid #b5b8c8 !important;");
        if (!borderRight) {
            //去掉右边线否则边线重合 丑
            tdHtml.append("border-right:none !important;");
        }
        if (!borderTop) {
            //去掉上边线否则边线重合 丑
            tdHtml.append("border-top:none !important;");
        }
        tdHtml.append("min-width:" + minWidth + "px;'>" + text + "</td>");
        return tdHtml.toString();
    }

    /**
     * 发短信
     * @author wangbs
     * @param noticeContent 通知内容
     * @param posDesc 岗位描述
     * @param interviewersList 面试官信息
     * @param sendCandidatesNoticeList 候选人信息
     * @return String
     * @throws GeneralException 抛出异常
     */
    private String sendMessage (String noticeContent, String posDesc, List interviewersList, List sendCandidatesNoticeList) throws GeneralException {
        try {
            //'短信相关设置未配置!';
            if(!TalentMarketsUtils.isSendSMS()) {
                return ResourceFactory.getProperty("talentmarkets.competitionJobs.msgConfigError");
            }
            SmsBo smsBo = new SmsBo(this.conn, this.userView);
            for (Object interviewerObj : interviewersList) {
                Map interviewerInfo = PubFunc.DynaBean2Map((MorphDynaBean) interviewerObj);
                String nbaseA0100 = PubFunc.decrypt((String) interviewerInfo.get("nbaseA0100"));

                StringBuffer content = new StringBuffer();
                content.append(interviewerInfo.get("interviewerName") + ResourceFactory.getProperty("talentmarkets.competitionJobs.hello"));
                if (StringUtils.isNotBlank(noticeContent) && !noticeContent.endsWith("。")) {
                    content.append(noticeContent + "。");
                }
                content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewPos") + posDesc);
                content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewerTip").substring(0, 10) + "。");
                smsBo.sendMessage(this.userView.getDbname() + this.userView.getA0100(), nbaseA0100, content.toString());
            }

            for (Object candidateObj : sendCandidatesNoticeList) {
                Map candidateInfo = PubFunc.DynaBean2Map((MorphDynaBean) candidateObj);
                String nbaseA0100 = PubFunc.decrypt((String) candidateInfo.get("nbaseA0100"));
                String z0503 = (String) candidateInfo.get("z0503");
                String interviewDate = (String) candidateInfo.get("interviewdate");
                String interviewTime = (String) candidateInfo.get("interviewtime");

                StringBuffer content = new StringBuffer();
                content.append(candidateInfo.get("a0101") + ResourceFactory.getProperty("talentmarkets.competitionJobs.hello"));
                if (StringUtils.isNotBlank(noticeContent) && !noticeContent.endsWith("。")) {
                    content.append(noticeContent + "。");
                }
                content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewPos") + posDesc + ",");
                content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewAddress1") + z0503 + ",");
                content.append(ResourceFactory.getProperty("talentmarkets.competitionJobs.interviewTime1") + interviewDate + " " + interviewTime);
                smsBo.sendMessage(this.userView.getDbname() + this.userView.getA0100(), nbaseA0100, content.toString());
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            //短信发送失败
            throw new GeneralException(ResourceFactory.getProperty("talentmarkets.competitionJobs.sendMsgError"));
        }
		return "";
    }
    /**
     * 获取候选人的批量更新和新增数组
     * @author wangbs
     * @param compePosNum 岗位编号
     * @param candidatesDataList 候选人信息
     * @return Map
     */
    private Map getOperateDbMap(String compePosNum, List candidatesDataList) {
        Map operateDbMap = new HashMap();
        List batchInsertList = new ArrayList();
        List batchUpdateList = new ArrayList();
        String userName = this.userView.getUserName();

        for (int i = 0; i < candidatesDataList.size(); i++) {
            List candidateData = (ArrayList) candidatesDataList.get(i);
            String changeFlag = (String) candidateData.get(0);
            candidateData.remove(0);

            candidateData.add(0, userName);
            candidateData.add(compePosNum);
            if (StringUtils.equalsIgnoreCase(changeFlag, "insert")) {
                batchInsertList.add(candidateData);
            } else if(StringUtils.equalsIgnoreCase(changeFlag, "update")) {
                batchUpdateList.add(candidateData);
            }
        }

        operateDbMap.put("batchInsertList", batchInsertList);
        operateDbMap.put("batchUpdateList", batchUpdateList);
        return operateDbMap;
    }
    /**
     * 删除前台去掉的面试官
     * @author wangbs
     * @param compePosNum 竞聘岗位编号
     * @param delInterviewersList 要删除的面试官
     * @return void
     * @throws GeneralException 抛出异常
     */
    private void delInterviewers(String compePosNum, List delInterviewersList) throws GeneralException{
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("delete from jp_interviewer ");
            sql.append("where z8101 =? ");
            sql.append("and interviewer not in (");

            List guidkeyList = new ArrayList();
            guidkeyList.add(compePosNum);
            for (int i = 0; i < delInterviewersList.size(); i++) {
                String guidkey = PubFunc.decrypt((String) delInterviewersList.get(i));
                if (i == delInterviewersList.size() - 1) {
                    sql.append("?)");
                } else {
                    sql.append("?,");
                }
                guidkeyList.add(guidkey);
            }

            if (CollectionUtils.isEmpty(delInterviewersList)) {
                //保存时会出现一个面试官都没有的情况，需删掉该竞聘岗位的所有面试官
                sql.append("?)");
                guidkeyList.add("1");
            }

            this.dao.delete(sql.toString(), guidkeyList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPsn.handlerInterviewerError");
        }
    }
    /**
     * 新增面试官
     * @author wangbs
     * @param compePosNum 竞聘岗位编号
     * @param addInterviewersList 要新增的面试官
     * @return void
     * @throws GeneralException 抛出异常
     */
    private void addInterviewers(String compePosNum, List addInterviewersList) throws GeneralException {
        try {
            String sql = "insert into jp_interviewer values (?,?,?)";
            List batchList = new ArrayList();
            //遍历出加密的guidkey
            for (Object guidkey_e : addInterviewersList) {
                List list = new ArrayList();
                String guidkey = PubFunc.decrypt((String) guidkey_e);
                list.add(compePosNum);
                list.add(guidkey);
                list.add("1");
                batchList.add(list);
            }
            this.dao.batchInsert(sql, batchList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("tm.contendPsn.handlerInterviewerError");
        }
    }


    /**
     * 功能描述: 导出简历PDF
     *
     * @author: caoqy
     * @param paramsMap:
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @date: 2019-8-2 11:22
     */
    @Override
    public Map<String,String> exportPdf(Map paramsMap) throws GeneralException {
        Map<String,String> dataMap = new HashMap();
        try {//导出简历PDF
            String flag = (String) paramsMap.get("flag");
            String selectIds = (String) paramsMap.get("selectIds");//“XXXXXX,XXXXX,XXXXX”//人员的guidkey，多个使用逗号进行分割
            String cyear = (String) paramsMap.get("cyear");
            String querytype = (String) paramsMap.get("querytype");
            String cmonth = (String) paramsMap.get("cmonth");
            String userpriv = (String) paramsMap.get("userpriv");
            String istype = (String) paramsMap.get("istype");              /*0代表薪酬1登记表*/
            String season = (String) paramsMap.get("season");
            String ctimes = (String) paramsMap.get("ctimes");
            String cdatestart = (String) paramsMap.get("cdatestart");
            String cdateend = (String) paramsMap.get("cdateend");
            String cardid = TalentMarketsUtils.getApplyResumeRname();
            String infokind = (String) paramsMap.get("infokind");
            String userbase = (String) paramsMap.get("userbase");
            String fieldpurv = (String) paramsMap.get("fieldpurv");
            String fileFlag = (String) paramsMap.get("fileFlag");
            String autoSize = (String) paramsMap.get("autoSize");

            boolean isMobile = "1".equals(paramsMap.get("isMobile"));//手机端标识
            String flagType = (String) paramsMap.get("flagType");
            ArrayList nid = new ArrayList();
            if ("all".equals(flag) || "1".equals(flag)) {//查询多个
                if (paramsMap.get("nid") != null) {
                    nid = (ArrayList) paramsMap.get("nid");
                }
            } else {
                nid.add(paramsMap.get("nid"));
            }
            nid = this.guidToObjIdList(selectIds);
            /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
            CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.conn, this.userView);
            if (infokind != null && "1".equalsIgnoreCase(infokind) && StringUtils.isNotEmpty(userbase)) {//liuy 2014-10-23 只有人员才需要判断人员库
                if (!this.userView.isSuper_admin()) {
                    String paramBasePre = userbase;
                    String returnBasePre = safeBo.checkDb(paramBasePre);//这个方法当不越权时返回传进去的人员库，越权时返回当前人员的第一个人员库
                    /**当返回的人员库值的长度大于0并且不等于传进去的人员库时说明越权**/
                    if (returnBasePre.trim().length() > 0 && !paramBasePre.equals(returnBasePre)) {//如果当前用户的人员库没有这个选中人员的人员库，终止导入
                        throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
                    }
                }
                if (!this.userView.isSuper_admin()) {
                    /**验证管理范围，如果越权则返回实有的管理范围**/
                    String paramManapriv = this.userView.getManagePrivCodeValue();
                    String realManapriv = safeBo.checkOrg(paramManapriv, "");
                    String paramPre = userbase;//这里所有的人员库都进行了验证，如果越权的人员库，在上面就结束了
                    String paramA0100 = (String) nid.get(0);//这里的A0100尚未进行验证
                    if (StringUtils.isNotEmpty(paramA0100)) {//liuy 2015-3-24 8205：首页/登记表/员工工作证，切换到劳务人员库，没有人员时，批量生成PDF，提示：人员权限越权，操作被终止
                        if (paramA0100.indexOf("`") > -1) {
                            paramA0100 = paramA0100.split("`")[1];
                        }
                        String realA0100 = safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
                        if (realA0100.trim().length() > 0 && !realA0100.equals(paramA0100)) {
//                            throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
                        }
                    }
                }
            }
            /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员end**/
            if ("0".equals(istype)) {
/*
                XmlParameter xml = new XmlParameter("UN", userView.getUserOrgId(), "00");
                xml.ReadOutParameterXml("SS_SETCARD", this.conn);
                cardid = xml.getCard_id();

                if (tabid == null || tabid.length() <= 0) {
                    String flags = xml.getFlag();
                    CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.conn);
                    ArrayList cardidlist = cardConstantSet.setCardidSelect(this.conn, this.userView, flags, pre, nid.get(0).toString(), b0110);
                    if (cardidlist != null && cardidlist.size() > 0) {
                        CommonData dataobj = (CommonData) cardidlist.get(0);
                        tabid = dataobj.getDataValue();
                        cardid = tabid;
                    }
                } else {
                    cardid = tabid;
                }
                String type = xml.getType();              //0条件1时间
                if ("0".equals(type)) {
                    querytype = "0";
                }
*/

            } else if ("1".equals(istype)) {
                querytype = "0";
            }

            if ("all".equals(flagType)) {//选择导出全部时 应从查询结果表中取对应数据
                nid = this.getObjidList(infokind, this.userView);
            }

            YkcardOutWord outWord = new YkcardOutWord(this.userView, this.conn);
            if (StringUtils.isNotEmpty(autoSize)) {
                if ("true".equals(autoSize)) {
                    outWord.setAutoSize(true);
                } else {
                    outWord.setAutoSize(false);
                }
            }
            if ("5".equalsIgnoreCase(infokind)) {
                String plan_id = (String) paramsMap.get("plan_id");
                outWord.setPlan_id(plan_id);
            }
            outWord.setQueryTypeTime(cyear, cmonth, cyear, cmonth, season, cyear, ctimes, cdatestart, cdateend);
            String url = "";
            String filePath = "";
            if ("1".equals(flag)) {//批量生成单个文件 已压缩文件夹形式导出
                ArrayList filnames = new ArrayList();
                for (int i = 0; i < nid.size(); i++) {
                    String id = (String) nid.get(i);
                    if ("1".equals(infokind) && id.indexOf("`") > -1) {
                        userbase = id.split("`")[0];
                        id = id.split("`")[1];
                    }
                    String filename = "";
                    filename = outWord.outWordYkcard(Integer.parseInt(cardid), id, querytype, infokind, userbase, userpriv, userpriv, fieldpurv);
                    if (!"word".equals(fileFlag)) {
                        filePath = System.getProperty("java.io.tmpdir") + File.separator + filename;
                        filename = wordToPdf(filePath, filename);
                    }
                    filnames.add(filename);
                }
                url = createZipFile(filnames, outWord.getExportName());
            } else {
                url = outWord.outWordYkcard(Integer.parseInt(cardid), nid, querytype, infokind, userbase, userpriv, userpriv, fieldpurv);
                if (!"word".equals(fileFlag)) {
                    filePath = System.getProperty("java.io.tmpdir") + File.separator + url;
                    url = wordToPdf(filePath, url);
                }
            }

            if (!isMobile) {
                url = PubFunc.encrypt(url);
            }

            dataMap.put("url", url);
            if ("1".equals(flag)) {
                dataMap.put("fileFlag", "zip");
            } else {
                dataMap.put("fileFlag", fileFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }
    /**
     * 通过guidkey获取objid的List：Usr`00000001
     * @author: caoqy
     * @param selectIds:
     * @return: java.util.ArrayList
     * @date: 2019-8-12 12:00
     */
    private ArrayList guidToObjIdList(String selectIds) {
        ArrayList<String> ObjIdList = new ArrayList<String>();
        StringBuffer sql = new StringBuffer("select a0100 from UsrA01 where 1=2");
        List<String> sqlList = new ArrayList<String>();
        String[] selectIdsArr = StringUtils.split(selectIds, ",");
        for (String selectId : selectIdsArr) {
            sql.append(" or guidkey=?");
            sqlList.add(selectId);
        }
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), sqlList);
            StringBuffer objId = new StringBuffer();
            while (rs.next()) {
                objId.append("Usr`").append(rs.getString("a0100"));
                ObjIdList.add(objId.toString());
                objId.setLength(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rs);
        }
        return ObjIdList;
    }

    /**
     * 下载成绩导入模板(仅提供报名"安排面试中"（04）状态的人员)
     *
     * @author: caoqy
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @date: 2019-8-2 16:21
     * @param fromValue
     * @param statusValue
     * @param posValue
     */
    @Override
    public String downloadScoreTemplate(String fromValue, String statusValue, String posValue) throws GeneralException {
        String fileName = "";
        HSSFWorkbook wb = null;
        FileOutputStream fileOut = null;
        try {
            //封装所需指标
            List<FieldItem> fieldItemList = this.getScoreTemplateFields();
            //获取查询数据sql
            String sql = this.getExcelSql(fieldItemList, fromValue, statusValue, posValue);
            // 创建新的Excel 工作簿
            wb = new HSSFWorkbook();
            fileName = this.createSheet(exportExcelType.scope.getValue(), fieldItemList, wb, sql);
            
            String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
            fileOut = new FileOutputStream(filePath);
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralException e) {
            throw new GeneralException(e.getErrorDescription());
        } finally {
        	PubFunc .closeResource(wb);
        	PubFunc .closeResource(fileOut);
        }
        return fileName;
    }
    /**
     * 通过excel模板导入成绩
     * @author: caoqy
     * @param fileId 文件加密id
     * @return: java.util.ArrayList<java.lang.Object>
     * @date: 2019-8-7 13:45
     */
    @Override
    public String importData(String fileId) throws GeneralException{
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

                        if ("Z8301".equalsIgnoreCase(field)) {
                            map.put(c, "Z8301");
                            continue;
                        }

                        //记录要要改的字段的位置
                        if (!dWizard.isExistField("z83", field.toUpperCase(), false)
                                &&!dWizard.isExistField("z81", field.toUpperCase(), false)
                                &&!dWizard.isExistField("usra01", field.toUpperCase(), false)) {
                            throw new GeneralException("导入的Excel中“" + title + "”这个指标在竞聘人员表里面不存在或未构库，请用导出的Excel模板来导入数据！");
                        }
                        map.put(c, field);
                    }
                }
                boolean isOwnExcel = false;
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    String itemId = entry.getValue();
                    if ("Z8307".equalsIgnoreCase(itemId)) {
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
     * 输出导入成绩错误日志
     * @author: caoqy
     * @param msgList:
     * @return: java.lang.String 文件名，空值为导入成功
     * @date: 2019-8-8 17:10
     */
    private String exportErrorLog(ArrayList<Object> msgList) {
        String logFileName = userView.getUserName() + "_成绩导入错误日志.txt";
        String logPath = System.getProperty("java.io.tmpdir") + File.separator + logFileName;
        //错误信息不为0输出错误日志
        StringBuffer textStr = new StringBuffer();
        List errorLogList = (List) msgList.get(0);
        if (errorLogList.size()!=0) {
            textStr.append("导入成绩数据情况如下:" + "\r\n");
            for (int i = 0; i < errorLogList.size(); i++) {
                String msgStr = (String) errorLogList.get(i);
                textStr.append(msgStr + "\r\n");
            }
        }else {
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
     * 获取excel内容
     * @author: caoqy
     * @param sheet:
     * @param map:
     * @return: java.util.ArrayList<java.lang.Object>
     * @date: 2019-8-7 15:08
     */
    private ArrayList<Object> getExcelInfo(Sheet sheet, Map<Integer, String> map) {
        //返回信息
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            indexs.add(entry.getKey());
        }
        Row row = null;
        Cell cell = null;
        StringBuffer sql = new StringBuffer();


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
        sql.append("UPDATE Z83 set Z8307=? WHERE Z8301=? AND Z8101=? AND Z8303='04'");
//        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            //用于计数
            int count = 0;
            String jobTitles = "";
            ArrayList<String> jobTitlesList = new ArrayList<String>();
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            //判断登录用户的招聘渠道的权限
            RecruitPrivBo rpbo = new RecruitPrivBo();
//            HashMap<String, Object> parame = rpbo.getChannelPrivMap(userView, conn);
            //excel每行数据遍历
            for (int i = 1; i < numberOfRows; i++) {
                String tmp = "";
                String jobTitle = null;
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


                    String z8301Value = "";
                    String z8101Value = "";
                //单行数据每列遍历
                for (Integer c : indexs) {
                    Object value = null;
                    cell = row.getCell(c);
                    //拿到标注
                    String field = map.get(c);
                    FieldItem item = DataDictionary.getFieldItem(field);
                    String itemId = item.getItemid();
                    if (!"Z8301".equals(itemId.toUpperCase())
                            && !"Z8101".equals(itemId.toUpperCase())
                            && !"Z8307".equals(itemId.toUpperCase())) {
                        continue;
                    }
                    //判断该字段在业务字典和页面业务中是否设置为必填项了
                    boolean required = this.getRequired(item);
                    if (cell != null) {
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }

                        String cellValue = cell.getStringCellValue();
                        if (required && StringUtils.isEmpty(cellValue)) {
                            msg.add("导入的Excel中[" + item.getItemdesc() + "]列必填项中有数据为空！请填写完整再导入");
                            msglist.add(msg);
                            valueLists.clear();
                            userView.getHm().put("valueLists", valueLists);
                            return msglist;
                        }

                        if (StringUtils.isNotEmpty(cellValue)) {
                            //判断成绩的数据类型代码类
                            String codeSql = "";
                            //判断数据类型
                            //代码型
                            if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype())) {
                                String tempCellValue = cell.getStringCellValue().trim();
                                value = this.getUnitId(tempCellValue);
                                String values = (String) value;
                                if (StringUtils.isBlank(values)) {
                                    int number = msg.size();
                                    msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标的机构在库中没有对应的机构id");
                                }
                            }
                            //数值型
                            else if ("N".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype())) {
                                if (cell.getStringCellValue() != null) {
                                    value = cell.getStringCellValue().trim();
                                    int fieldLength = item.getItemlength();
                                    String values = (String) value;
                                    //校验数字
                                    //不能为负数
                                    if (StringUtils.contains(values, "-")) {
                                        int number = msg.size();
                                        msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中的值不能为负数！");
                                        continue;
                                    }
                                    //如果带有小数
                                    if (StringUtils.isNotBlank(values) && StringUtils.contains(values, ".")) {
                                        int decimalLength = item.getDecimalwidth();
                                        String[] valuesArr = StringUtils.split(values, ".");
                                        int valueLength = valuesArr[0].length();
                                        int valueDecimalLength = valuesArr[1].length();
                                        if (valueLength > fieldLength || valueDecimalLength > decimalLength) {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度！");
                                            continue;
                                        }
                                    }
                                    //如果是整数
                                    else {
                                        int valueLength = values.length();
                                        PubFunc.doStringLength(values, fieldLength);
                                        if (valueLength > fieldLength) {
                                            int number = msg.size();
                                            msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]指标中值的长度超过指标长度。");
                                            continue;
                                        }
                                    }
                                    if (!"".equals(value)) {
                                        if (((String) value).indexOf(".") > 0) {
                                            String intNum = ((String) value).substring(0, ((String) value).indexOf("."));
                                            String floatNum = ((String) value).substring(((String) value).indexOf(".") + 1);
                                            if (floatNum.length() > DataDictionary.getFieldItem(map.get(c)).getItemlength()) {
                                                floatNum = floatNum.substring(0, DataDictionary.getFieldItem(map.get(c)).getDecimalwidth());
                                            }

                                            value = intNum + "." + floatNum;
                                            value = Double.parseDouble((String) value);
                                        } else {
                                            try {
                                                value = Float.parseFloat((String) value);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                int number = msg.size();
                                                msg.add((number + 1) + ". 第" + (i + 1) + "行[" + item.getItemdesc() + "]列单元格的值为非数字，请修改！");
                                                continue;
                                            }
                                        }

                                    } else {
                                        value = null;
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
                            value = null;
                        }
                    } else {
                        value = null;
                    }
                    if (StringUtils.equalsIgnoreCase(itemId, "Z8101")) {
                        z8101Value = (String) value;
                    }else if (StringUtils.equalsIgnoreCase(itemId, "Z8301")) {
                        z8301Value = (String) value;
                    }
                    if ("Z8307".equalsIgnoreCase(itemId)) {
                        rowList.add(0, value);
                    }else {
                        rowList.add(value);
                    }
                }
                boolean valueFlag = true;
                if (StringUtils.isNotBlank(z8101Value) && StringUtils.isNotBlank(z8301Value)) {
                    valueFlag = this.isCanUpdate(z8101Value,z8301Value);
                }
                if (!valueFlag) {
                    int number = msg.size();
                    msg.add((number + 1) + ". 第" + (i + 1) + "行的人员不是“安排面试中”状态，不可更新成绩。");
                    continue;
                }
                if ("false".equals(tmp)) {
                    continue;
                }
                valueLists.add(rowList);
/*                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    int result = startTime.compareTo(endTime);
                    if (result > 0) {
                        int number = msg.size();
                        msg.add((number + 1) + ". 第" + (i + 1) + "行的[" + startTimeName + "]大于[" + endTimeName + "]， 数据错误， 请修改。");
                        continue;
                    }
                }*/
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
     * 功能描述: 判断此行数据是否为安排面试中
     * @author: caoqy
     * @param z8101Value:
     * @param z8301Value:
     * @return: boolean
     * @date: 2019-9-4 16:11
     */
    private boolean isCanUpdate(String z8101Value, String z8301Value) {
        boolean flag = true;
        String sql = "SELECT count(*) as num FROM z83 WHERE Z8101=? and z8301=? and z8303<>'04'";
        List list = new ArrayList();
        list.add(z8101Value);
        list.add(z8301Value);
        RowSet rs = null;
        try {
            rs = dao.search(sql, list);
            if (rs.next()) {
                int num = rs.getInt("num");
                if (num > 0) {
                    flag = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据名称获取机构编码
     * @author: caoqy
     * @param tempCellValue:
     * @return: java.lang.String
     * @date: 2019-8-8 18:44
     */
    private String getUnitId(String tempCellValue) {
        String unitCodeId = "";
        RowSet rs = null;
        List<String> list = new ArrayList<String>();
        list.add(tempCellValue);
        String sql = "select codeitemid from organization where codeitemdesc=?";
        try {
            rs = dao.search(sql, list);
            if (rs.next()) {
                unitCodeId = rs.getString("codeitemid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unitCodeId;
    }

    /**
     * 导入excel数据
     * @author: caoqy
     * @param sql:
     * @param valueLists:
     * @return: java.lang.String
     * @date: 2019-8-7 15:20
     */
    private String importExcel(String sql, ArrayList valueLists) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.batchUpdate(sql, valueLists);
            return "导入完成，导入" + valueLists.size() + "条数据";
        } catch (Exception e) {
            e.printStackTrace();
            return "导入数据出错";
        }
    }

    /**
     * 根据导入日期字符串长度获取对应的日期格式
     * @author: caoqy
     * @param dateValue:
     * @return: java.lang.String
     * @date: 2019-8-7 15:17
     */
    private String getDateFormat(String dateValue) {
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
     * 校验日期字符串
     * @author: caoqy
     * @param str: 日期字符串
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
     * @author: caoqy
     * @param date:
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
     * @author: caoqy
     * @param date:
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
     * @author: caoqy
     * @param year:
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
     * 判断excel本行数据是否为空
     * @param row
     * @return
     * @throws Exception
     */
    private boolean isRowEmpty(Row row) {
        Cell cell = row.getCell(0);
        if (cell==null) {
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
     * 获取excel表页
     * @author: caoqy
     * @param fileId 文件加密id
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
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(input);
            PubFunc.closeResource(work);
        }
        return sheet;
    }

    /**
     * 获取Excel的sql
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-6 13:31
     * @param fieldItemList
     * @param fromValue
     * @param statusValue
     * @param posValue
     */
    private String getExcelSql(List<FieldItem> fieldItemList, String fromValue, String statusValue, String posValue) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get(COMPETITIONPSN);
        String filterSql = cache.getFilterSql();
        String querySql = cache.getQuerySql();
        sql.append("SELECT ");
        //固定展现给操作人员用于辨识的字段，现单位、部门、岗位、姓名
        sql.append("U.B0110,U.E0122,U.E01A1,U.A0101");
        //添加Z81,Z83中所需的自定义字段
        for (FieldItem fieldItem : fieldItemList) {
            String itemId = fieldItem.getItemid().toUpperCase();
            if ("B0110,E0122,E01A1,A0101,Z8307".contains(itemId)) {
                continue;
            }
            sql.append(",").append(fieldItem.getFieldsetid()).append(".").append(fieldItem.getItemid());
        }
        //竞聘分数
        sql.append(",Z83.Z8307");
        sql.append(" FROM Z81,Z83,UsrA01 U");
        //仅查询安排面试中（'04'）的人员
        sql.append(" WHERE (Z81.z8101=Z83.Z8101 and Z83.z8301=U.GUIDKEY and Z83.z8303='04') ");
        //添加过滤条件
        if (StringUtils.isNotBlank(filterSql)) {
            sql.append(" ").append(filterSql).append(" ");
        }
        if (StringUtils.isNotBlank(querySql)) {
            querySql = querySql.replace("myGridData.a0101", "U.a0101");
            querySql = querySql.replace("myGridData.z8305", "Z83.z8305");
            sql.append(" ").append(querySql).append(" ");
        }
        //穿透图进来的
        if (StringUtils.isNotBlank(statusValue)) {
            //点击柱子穿透条件
            if (StringUtils.isNotBlank(posValue)) {
                posValue = PubFunc.decrypt(posValue);
                sql.append(" and Z83.Z8101='" + posValue + "' ");
            }else{
                //点击门户页面上方的方块进来
                sql.append(" and Z83.Z8101 in (select z8101 from z81 where z8103 in");
                if (CURRENT.equals(statusValue)) {
                    sql.append("('04','05'))");
                } else if (HISTORY.equals(statusValue)) {
                    sql.append(TalentMarketsUtils.END_STATUS + ")");
                }
            }
        }
        if (!this.userView.isSuper_admin()) {
            String manprivv = this.userView.getUnitIdByBusi("4");
            if (StringUtils.isBlank(manprivv)) {
                throw new GeneralException("此人员无组织范围权限");
            } else {
                String[] manprivvArr = StringUtils.split(manprivv, "`");
                sql.append(" and (1=2 ");
                for (String tempManprivv : manprivvArr) {
                    String orgId = tempManprivv.substring(2);
                    sql.append(" or Z83.Z8305 like '").append(orgId).append("%' ");
                }
                sql.append(")");
            }
        }
        sql.append(" ORDER BY Z81.Z8101");

        return sql.toString();
    }

    /**
     * 创建根据指标创建excel页
     * @author: caoqy
     * @param value :
     * @param fieldItemList :
     * @param wb
     * @param excelSql
     * @return: String
     * @date: 2019-8-5 15:04
     */
    private String createSheet(String value, List<FieldItem> fieldItemList, HSSFWorkbook wb, String excelSql) throws GeneralException{
        String sheetName = this.userView.getUserName() + "_成绩导入模板";
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
            if("z8301".equalsIgnoreCase(fieldName)|| "z8101".equalsIgnoreCase(fieldName)){
                sheet.setColumnWidth((i), 0);
            }else {
                sheet.setColumnWidth((i), w * 350);
            }
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
            rs = dao.search(excelSql);
            while (rs.next()) {
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
                        cell.setCellValue(rs.getDouble(itemId));
                    }
                    //字符型
                    else if("A".equals(itemtype)&&"0".equals(field.getCodesetid())){
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(rs.getString(itemId));
                    }
                    //代码型
                    else if ("A".equals(itemtype) && !"0".equals(field.getCodesetid())) {
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String codeSetId = field.getCodesetid();
                        String codeId = rs.getString(itemId);
                        CodeItem codeItem = AdminCode.getCode(codeSetId,codeId);
                        String codeDesc = codeItem == null ? "" : codeItem.getCodename();
                        cell.setCellValue(new HSSFRichTextString(codeDesc));
                    }
                    //日期型
                    else if ("D".equals(itemtype)) {
                        cell.setCellStyle(style1);
                        Date date = rs.getDate(itemId);
                        if (date == null) {
                            cell.setCellValue("");
                        } else {
                            String cellDate = DateUtils.format(date, "yyyy-MM-dd");
                            cell.setCellValue(new HSSFRichTextString(cellDate));
                        }
                    }
                    //备注型
                    else if ("M".equalsIgnoreCase(itemtype)) {
                        cell.setCellStyle(styleN);
                        // 判断数据字典里的指标类型
                        String richText = Sql_switcher.readMemo(rs, itemId);
                        cell.setCellValue(new HSSFRichTextString(richText.replace("`", "\r\n")));
                    }
                }
                rowCount++;
            }
            if (rowCount==1) {
                throw new GeneralException("当前用户权限下，无状态为“安排面试中”的人员");
            }
            rowCount--;

            HSSFSheet codesetSheet = sheet;
            // 下拉数据放到最后，依次为 HZ、HY、HX......
/*            String[] firstUpper = {"H", "G", "F", "E", "D", "C", "B", "A"};
            String[] lettersUpper = {"Z", "Y", "X", "W", "V", "U", "T", "S", "R", "Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"};
            //不再加载单位部门岗位的代码下拉项，仅用作展示

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
*//*                    if (codeCol1 == z0101Column) {
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
                    }*//*
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
            }*/
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            String error = "";
            if (e instanceof GeneralException) {
                error = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(error);
        }finally {
            PubFunc.closeResource(rs);
        }

        return fieldsetdesc;
    }

    /**
     * 返回富文本单元格
     * @author: caoqy
     * @param context:
     * @return: org.apache.poi.hssf.usermodel.HSSFRichTextString
     * @date: 2019-8-5 15:30
     */
    private HSSFRichTextString cellStr(String context) {
        return new HSSFRichTextString(context);
    }

    /**
     * 判断此指标，在指标体系或业务字典中是否为必填项
     * @author: caoqy
     * @param field:
     * @return: boolean
     * @date: 2019-8-5 15:30
     */
    private boolean getRequired(FieldItem field) {
        //业务字典中是否设置为必填项了
        boolean required = field.isFillable();
        String fieldName = field.getItemid().toLowerCase();
        //下载模板为导入成绩，主键与分数指标为必填
        return required || "Z8301".equalsIgnoreCase(fieldName) || "Z8307".equalsIgnoreCase(fieldName) ||"Z8301".equalsIgnoreCase(fieldName);
    }
    /**
     * 根据长度得到占位符
     * @author: caoqy
     * @param len:
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
     * @author: caoqy
     * @param workbook:
     * @return: org.apache.poi.hssf.usermodel.HSSFCellStyle
     * @date: 2019-8-5 15:33
     */
    private HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
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

    /**
     * 获取下载模板指标List
     * @author: caoqy
     * @return: java.util.List<com.hrms.hjs0j.sys.FieldItem>
     * @date: 2019-8-2 17:01
     */
    private List<FieldItem> getScoreTemplateFields() {
        List<String> fieldList = new ArrayList<String>();
        List<FieldItem> list = new ArrayList<FieldItem>();
        //人员guidkey
        fieldList.add("Z8301");
        //竞聘岗位主键
        fieldList.add("Z8101");
        //单位
        fieldList.add("B0110");
        //部门
        fieldList.add("E0122");
        //岗位
        fieldList.add("E01A1");
        //姓名
        fieldList.add("A0101");
        //竞聘岗位
        fieldList.add("Z8305");
        //分数
        fieldList.add("Z8307");
        for (String itemId : fieldList) {
            FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
            if (fieldItem != null) {
                list.add(fieldItem);
            }
        }
        return list;
    }

    /**
     * 功能描述: word to PDF
     *
     * @author: caoqy
     * @param filePath: 输入路径
     * @param url: 输出路径
     * @return: java.lang.String
     * @date: 2019-8-2 11:23
     */
    private String wordToPdf(String filePath, String url) {
        try {
            Document doc = new Document(filePath);
            int lastindex = url.lastIndexOf(".");
            url = url.substring(0, lastindex) + ".pdf";
            doc.save(System.getProperty("java.io.tmpdir") + File.separator + url);
            //清除生成的word(tomcat临时文件中)
            File docfile = new File(filePath);
            if (docfile.exists()) {
                docfile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 功能描述: 创建压缩包
     *
     * @author: caoqy
     * @param filenames: 文件名
     * @param tabName: 压缩包名
     * @return: java.lang.String
     * @date: 2019-8-2 11:23
     */
    private String createZipFile(ArrayList filenames, String tabName) {
        String tmpFileName = this.userView.getUserName() + "_" + tabName + ".zip";
        byte[] buffer = new byte[2048];
        String filePath = System.getProperty("java.io.tmpdir") + File.separator;
        String strZipPath = filePath + tmpFileName;
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        InputStream fis = null;
        try {
        	out = new ZipOutputStream(new FileOutputStream(filePath + tmpFileName));
            out.setEncoding("GBK");
            for (int i = 0; i < filenames.size(); i++) {
                File file = null;
                try {
                    file = new File(filePath + filenames.get(i));
                    fis = new FileInputStream(file);
                    origin = new BufferedInputStream(fis, 2048);
                    out.putNextEntry(new ZipEntry(file.getName()));
                    int count;
                    while ((count = origin.read(buffer, 0, 2048)) != -1) {
                        out.write(buffer, 0, count);
                    }
                } finally {
                    PubFunc.closeResource(origin);
                    PubFunc.closeResource(fis);
                }
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
        	PubFunc.closeResource(origin);
        	PubFunc.closeResource(out);
        	PubFunc.closeResource(fis);
        }
        return tmpFileName;
    }

    /**
     * 功能描述: 获取人员objIdList
     *
     * @author: caoqy
     * @param inforKind:
     * @param userview:
     * @return: java.util.ArrayList<java.lang.String>
     * @date: 2019-8-2 11:24
     */
    private ArrayList<String> getObjidList(String inforKind, UserView userview) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search("select objid,nbase from t_card_result where username=? and flag=?", Arrays.asList(userview.getUserName(), inforKind));
            while (rs.next()) {
                if ("1".equals(inforKind)) {
                    list.add(rs.getString("nbase") + "`" + rs.getString("objid"));
                } else {
                    list.add(rs.getString("objid"));
                }
            }

        } catch (Exception e) {
            throw e;
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    @Override
   	public List listMobileMyCompetitionPost(String competitionType, String guidkey) throws GeneralException {
   		ArrayList list = new ArrayList();
       	StringBuffer sql = new StringBuffer();
       	ContentDAO dao = new ContentDAO(this.conn);
       	sql.append("select Z81.Z8101,Z83.Z8303,Z81.B0110,Z81.E0122,Z81.E01A1,Z81.Z8107,"+Sql_switcher.dateToChar("Z81.Z8113","yyyy-MM-dd")+" Z8113 from Z81,Z83 where Z81.Z8101=Z83.Z8101 and Z83.Z8301=? ");
       	if("executing".equalsIgnoreCase(competitionType)){//正在竞聘的状态
       		sql.append(" and Z83.Z8303 not in ('03','06','09','12','13') ");
       	}else if("end".equalsIgnoreCase(competitionType)){//结束竞聘状态
       		sql.append(" and Z83.Z8303 in ('03','06','09','12','13') ");
       	}
       	sql.append(" order by Z83.create_time desc ");
       	RowSet rs = null;
       	try {
   			rs = dao.search(sql.toString(),Arrays.asList(guidkey));
   			while(rs.next()){
   				Map map = new HashMap();
   				map.put("id", PubFunc.encrypt(rs.getString("Z8101")));
   				String Z8303 = rs.getString("Z8303");//竞聘状态
   				if("executing".equalsIgnoreCase(competitionType)){//正在竞聘
   					String codesetid = DataDictionary.getFieldItem("z8303").getCodesetid();
   					map.put("Z8303", AdminCode.getCode(codesetid, Z8303).getCodename());
   				}else{//竞聘结束状态前台翻译
   					map.put("Z8303", Z8303);
   				}
   				if(StringUtils.isNotBlank(rs.getString("B0110"))){
   					map.put("B0110", AdminCode.getCode("UN", rs.getString("B0110")).getCodename());
   				}
   				if(StringUtils.isNotBlank(rs.getString("E0122"))){
   					map.put("E0122", AdminCode.getCode("UM", rs.getString("E0122")).getCodename());
   				}
   				if(StringUtils.isNotBlank(rs.getString("E01A1"))){
   					map.put("E01A1", AdminCode.getCode("@K", rs.getString("E01A1")).getCodename());
   				}
   				map.put("Z8107", rs.getInt("Z8107"));
   				map.put("Z8113", rs.getString("Z8113"));
   				list.add(map);
   			}
   		} catch (Exception e) {
   			e.printStackTrace();
   			//获取我竞聘过的岗位信息出错
   			throw new GeneralException("getMyCompetitionPostListError");
   		}finally{
   			PubFunc.closeDbObj(rs);
   		}
   		return list;
   	}


   	@Override
   	public boolean checkMobileMyCompetitionPost(String guidkey) throws GeneralException {
   		String sql = "select 1 from Z83 where Z8301=? and Z8303 not in ('03','06','09','12','13')";
   		ContentDAO dao = new ContentDAO(this.conn);
   		RowSet rs = null;
   		try {
   			rs = dao.search(sql, Arrays.asList(guidkey));
   			if(rs.next()){
   				return true;
   			}
   		} catch (Exception e) {
   			e.printStackTrace();
   			//查询正在竞聘岗位出错
   			throw new GeneralException("searchMyCompetitionPostError");
   		}finally{
   			PubFunc.closeDbObj(rs);
   		}

   		return false;
   	}
}

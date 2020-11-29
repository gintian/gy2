package com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl;


import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.JobPreparationService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.module.template.utils.BusinessService;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 岗位编制接口实现类
 * @Author manjg
 * @Date 2019/7/24 10:50
 * @Version V1.0
 **/
public class JobPreparationServiceImpl implements JobPreparationService, BusinessService {

    private Connection conn;
    private UserView userView;
    private String subModuleId;

    public JobPreparationServiceImpl(String subModuleId, Connection frameconn, UserView userView) {
        super();
        this.subModuleId = subModuleId;
        this.conn = frameconn;
        this.userView = userView;
    }

    public JobPreparationServiceImpl() {
    }

    /**
     * 获取表格列信息
     *
     * @return 表格列信息
     */
    private ArrayList<ColumnsInfo> getTableCloumns() throws GeneralException {
        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        //获取配置编制子集信息项
        Map<String, String> subsetConfig = TalentMarketsUtils.getSubsetConfig(this.conn);
        //岗位编制子集 K17
        String subset = subsetConfig.get("psSet");
        //定员人数 K1703
        String workFixed = subsetConfig.get("psWorkfixed");
        //实有人数 K17Z2
        String workExist = subsetConfig.get("psWorkexist");

        ArrayList<FieldItem> itemList = userView.getPrivFieldList(subset);
        String workFixedDesc = "";
        String workExistDesc = "";

        Map<String, Object> extraParam = new HashMap<String, Object>();
        //单位
        extraParam.put("editableValidFunc", "false");
        ColumnsInfo columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "UN", "B0110", ResourceFactory.getProperty("column.sys.org"), ColumnsInfo.LOADTYPE_BLOCK, 200, extraParam);
        columnsList.add(columnsInfo);
        //extraParam.clear();

        //部门
        //extraParam.put("editableValidFunc","false");
        extraParam.put("doFilterOnLoad", true);
        extraParam.put("ctrltype", "3");
        extraParam.put("nmodule", "4");
        columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "UM", "E0122", ResourceFactory.getProperty("column.sys.dept"), ColumnsInfo.LOADTYPE_BLOCK, 150, extraParam);
        columnsList.add(columnsInfo);
        extraParam.clear();

        //岗位名称
        extraParam.put("editableValidFunc", "false");
        extraParam.remove("doFilterOnLoad");
        extraParam.put("locked", true);
        extraParam.put("ctrltype", "3");
        extraParam.put("nmodule", "4");
        columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "@K", "E01A1", ResourceFactory.getProperty("column.sys.pos"), ColumnsInfo.LOADTYPE_BLOCK, 150, extraParam);
        columnsList.add(columnsInfo);
        extraParam.clear();

        //业务字典中编制子集指标
        for (FieldItem fieldItem : itemList) {
            extraParam.put("editableValidFunc", "false");
            FieldItem item = DataDictionary.getFieldItem(fieldItem.getItemid());
            //过滤定员人数列和实有人数列
            if (workFixed.equalsIgnoreCase(item.getItemid())) {
                workFixedDesc = item.getItemdesc();
                continue;
            } else if (workExist.equalsIgnoreCase(item.getItemid())) {
                workExistDesc = item.getItemdesc();
                continue;
            }
            if (StringUtils.contains(item.getItemid(), "z0") || StringUtils.contains(item.getItemid(), "z1")) {
                continue;
            }
            //非数值型数据居左显示
            if (!"N".equalsIgnoreCase(item.getItemtype())) {
                extraParam.put("textAlign", "left");
            }
            columnsInfo = TalentMarketsUtils.getColumnsInfo(fieldItem, ColumnsInfo.LOADTYPE_BLOCK, 100, extraParam);
            columnsList.add(columnsInfo);
            extraParam.clear();
        }

        //编制总数
        if (StringUtils.isNotEmpty(workFixedDesc)) {
            extraParam.put("editableValidFunc", "false");
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", workFixed, workFixedDesc, ColumnsInfo.LOADTYPE_BLOCK, 100, extraParam);
            columnsList.add(columnsInfo);
        }

        //extraParam.clear();

        //实有人数
        //extraParam.put("editableValidFunc","false");
        if (StringUtils.isNotEmpty(workExistDesc)) {
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", workExist, workExistDesc, ColumnsInfo.LOADTYPE_BLOCK, 100, extraParam);
            columnsList.add(columnsInfo);
            extraParam.clear();
        }

        return columnsList;
    }

    /**
     * 获取功能按钮
     *
     * @return
     */
    private ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();
        ArrayList navigationList = new ArrayList();

        //功能导航
        LazyDynaBean oneBean = new LazyDynaBean();
        oneBean.set("text", ResourceFactory.getProperty("talentmarkets.competitionJobs.exportExcel"));
        oneBean.set("id", "export");
        oneBean.set("fntype", ButtonInfo.FNTYPE_EXPORT);
        navigationList.add(oneBean);
        String menu = TalentMarketsUtils.getMenuStr(ResourceFactory.getProperty("talentmarkets.competitionJobs.functionalNavigation"), navigationList);
        buttonList.add(menu);

        buttonList.add("-");
        //栏目设置
        buttonList.add(new ButtonInfo(ResourceFactory.getProperty("label.grid.scheme"), "jobPreparation.doTableScheme()"));
        //发布申请
        buttonList.add(new ButtonInfo(ResourceFactory.getProperty("talentMarkets.jobPreparation.publishingApplication"), "jobPreparation.publishApplication()"));
        buttonList.add("-");
        //返回
        buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.return"), "jobPreparation.toBack()"));
        //设置查询面板按钮
        ButtonInfo querybox = new ButtonInfo();
        //设置点击应用时所调用的交易类
        querybox.setFunctionId("TM000000013");
        //此值为ButtonInfo.TYPE_QUERYBOX时tablebuilder.js会生成查询面板
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        //按钮显示文字
        querybox.setText(ResourceFactory.getProperty("talentMarkets.jobPreparation.queryboxtext"));
        buttonList.add(querybox);

        return buttonList;
    }

    /**
     * 获取表格sql
     *
     * @param queryMethod
     * @return
     */
    private String getDataSql(int queryMethod) {
        StringBuffer sql = new StringBuffer("");
        StringBuffer innerSql = new StringBuffer();
        Map<String, String> subsetConfig = TalentMarketsUtils.getSubsetConfig(this.conn);
        //获取配置的岗位编制子集
        String psSet = subsetConfig.get("psSet");
        //定员人数
        String psWorkfixed = subsetConfig.get("psWorkfixed");
        //实有人数
        String psWorkexist = subsetConfig.get("psWorkexist");
        //判断权限SQL "4"代表组织机构
        String privSql = TalentMarketsUtils.getPrivSql("t", "4", this.userView).replaceFirst("and", "where");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate = sdf.format(new Date());

        sql.append("select * from (");
        innerSql.append("select Nk.*,UNORG.b0110,E0122 FROM (SELECT E01A1,");
        ArrayList<FieldItem> itemList = userView.getPrivFieldList(psSet);
        for (FieldItem fieldItem : itemList) {
            innerSql.append(fieldItem.getItemid()).append(",");
        }
        innerSql.setLength(innerSql.length()-1);//去掉一个逗号
        innerSql.append(" from ").append(psSet).append(" LEFT JOIN ORGANIZATION OG ON OG.CODEITEMID= ");
        innerSql.append(psSet).append(".E01A1 ");
        innerSql.append(" where I9999=(SELECT Max(I9999) FROM ").append(psSet).append(" KTMEP where ");
        innerSql.append(psSet).append(".E01A1=KTMEP.E01A1) and ");
        innerSql.append(Sql_switcher.dateValue(backdate)).append(" between start_date and end_date)NK LEFT JOIN");
        innerSql.append(" (select max(o.codeitemid) b0110,");
        innerSql.append(psSet).append(".E01A1").append(" FROM organization o,");
        innerSql.append(psSet).append(" where o.codeitemid =");
        innerSql.append(Sql_switcher.substr(psSet+".E01A1", "1", Sql_switcher.length("o.codeitemid")));
        innerSql.append(" and o.codesetid = 'UN'");
        innerSql.append(" and (").append(Sql_switcher.dateValue(backdate)).append("between start_date and end_date)");
        innerSql.append(" group by E01A1)UNORG ON UNORG.E01A1=NK.E01A1 ");
        innerSql.append(" LEFT JOIN (SELECT E0122,E01A1 from (");
        innerSql.append("SELECT PARENTID as E0122,E01A1 from organization o,");
        innerSql.append(psSet).append(" where o.CODEITEMID=");
        innerSql.append(psSet).append(".E01A1)o1,organization o2");
        innerSql.append(" where o2.CODEITEMID=o1.E0122 and CODESETID='UM' group by E01A1,E0122 )UMORG ON UMORG.E01A1=NK.E01A1");
        //过滤已发布的岗位信息
        innerSql.append(" where NK.E01A1 not in (select E01A1 from z81 where Z8103 in ('01','02','03','04','05','10'))");
        //sql.append(" and ").append(Sql_switcher.dateValue(backdate)).append(" between start_date and end_date");
        if (queryMethod != -1) {
            //innerSql.append(" where ").append(psWorkfixed).append(" > ").append(psWorkexist);
            innerSql.append(" and ").append(psWorkfixed).append(" > ").append(psWorkexist);
        }
        sql.append(innerSql);
        sql.append(" ) t ").append(privSql);
        return sql.toString();
    }

    /**
     * 获取config
     *
     * @return
     */
    @Override
    public String getTableConfig() throws GeneralException {
        String config = "";

        try {
            //获取列头
            ArrayList<ColumnsInfo> columnsList = this.getTableCloumns();
            //获取操作按钮
            ArrayList buttonList = this.getButtonList();
            //获取sql语句
            String sql = this.getDataSql(-1);
            TableConfigBuilder builder = new TableConfigBuilder(this.subModuleId, columnsList, this.subModuleId, this.userView, this.conn);
            builder.setTitle(ResourceFactory.getProperty("pos.posparameter.ps_posworkout"));
            builder.setSelectable(true);
            builder.setColumnFilter(true);
            builder.setScheme(true);
            builder.setPageSize(20);
            builder.setLockable(true);
            builder.setTableTools(buttonList);
            builder.setEditable(true);
            builder.setTdMaxHeight(120);
            builder.setDataSql(sql);
            builder.setOrderBy(" order by E01A1");
            config = builder.createExtTableConfig();
        } catch (Exception e) {
            throw new GeneralException("tm.contendPos.getGridpanelError");
        }
        return config;
    }


    /**
     * 查询方案重新刷新数据
     *
     * @param queryMethod
     */
    @Override
    public void refreshTableData(int queryMethod) {
        TableDataConfigCache configCache = (TableDataConfigCache) this.userView.getHm().get(this.subModuleId);
        String sql = this.getDataSql(queryMethod);
        configCache.setTableSql(sql);
        this.userView.getHm().put(this.subModuleId, configCache);
    }

    /**
     * 检验发布申请岗位状态
     * @param postList
     */
    @Override
    public void checkPostStatus(ArrayList postList) throws GeneralException{
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
                            || StringUtils.equalsIgnoreCase(status,"04")|| StringUtils.equalsIgnoreCase(status,"05")){
                        throw new GeneralException("tm.contendPos.postPublishedFail");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            String msg = "tm.contendPos.checkStatusSqlFail";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }finally {
            PubFunc.closeResource(rowSet);
        }
    }

    @Override
    public void execution(ArrayList recordVoList, int tabid, String opt, UserView userview) throws GeneralException {
        Connection connection = AdminDb.getConnection();
        ContentDAO dao = new ContentDAO(connection);
        RowSet rowSet = null;

        try {
            TemplateBo templateBo = new TemplateBo(connection, userview, tabid);
            TemplateParam templateParam = templateBo.getParamBo();
            JSONObject fieldRelation = null;
            //审批意见指标itemid  只能是变化后
            String opinionField = templateParam.getOpinion_field() + "_2";
            //内部竞聘发布申请模板tabid
            String internalCompetitionTabId = TalentMarketsUtils.getReleasePostTemplate();
            //应聘人员申请模板tabid
            String applyTemplateTabId = TalentMarketsUtils.getApplyTemplate();
            //录用审批模板tabid
            String hireTemplateTabId = TalentMarketsUtils.getHireTemplate();
            //发布简历模板id
            String talentDisplayTemplateId = TalentMarketsUtils.getTalentDisplayTemplate();
            //撤销简历模板id
            String cancelTemplateId = TalentMarketsUtils.getCancelTemplate();
            //取得系统配置的唯一标识
            //con为数据库的链接
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(connection);
            /*
            param1:Sys_Oth_Parameter.CHK_UNIQUENESS 为取得唯一性指标设置的内容
            param2: 0获取唯一性指标 1获取身份证指标
            param3: 传固定值name,是要获取name属性的值出来
            */
            String uniquenessField = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");

            IDGenerator idg = new IDGenerator(2, connection);
            boolean isExits = false;
            if (StringUtils.equalsIgnoreCase(internalCompetitionTabId, String.valueOf(tabid))) {
                fieldRelation = TalentMarketsUtils.getReleasePostTemplateRelation();
                for (Object recordObject : recordVoList) {
                    List<RecordVo> interviewArrangementList = new ArrayList<RecordVo>();//面试官信息表
                    RecordVo recordVo = new RecordVo("z81");
                    LazyDynaBean record = (LazyDynaBean) recordObject;
                    String opinionFieldValue = (String) record.get(opinionField.toLowerCase());
                    String primaryKeyValue = (String) record.get(fieldRelation.getString("z8101"));
                    //没有z8101值  说明是手工选择进来的  流程发起
                    if (StringUtils.equalsIgnoreCase("apply", opt)) {
                        if (StringUtils.isEmpty(primaryKeyValue)) {
                            String tableName = "templet_" + tabid;
                            primaryKeyValue = idg.getId("z81.z8101");
                            RecordVo recordVo1 = new RecordVo(tableName);
                            recordVo1.setObject("e01a1", record.get("e01a1"));
                            recordVo1.setObject("ins_id", record.get("ins_id"));
                            recordVo1 = dao.findByPrimaryKey(recordVo1);
                            recordVo1.setObject(fieldRelation.getString("z8101"), primaryKeyValue);
                            dao.updateValueObject(recordVo1);
                        }
                    }
                    recordVo.setObject("z8101", primaryKeyValue);
                    //已有记录 则更新
                    if (dao.isExistRecordVo(recordVo)) {
                        isExits = true;
                        recordVo = dao.findByPrimaryKey(recordVo);
                    } else {
                        recordVo.setObject("create_time", new Date());
                        recordVo.setObject("create_user", userview.getUserName());
                    }
                    String z8103 = "";
                    if (StringUtils.equalsIgnoreCase(opt, "apply")) {
                        z8103 = "02";
                    } else if (StringUtils.equalsIgnoreCase(opt, "submit")) {
                        z8103 = "03";
                        //往面试名单表中插入 选中的面试官数据
                        //获取面试名单对应字段
                        String interviewArrangementField = "";
                        if(fieldRelation.containsKey("interviewArrangement")){
                            interviewArrangementField = fieldRelation.getString("interviewArrangement");
                        }
                        if(StringUtils.isNotEmpty(interviewArrangementField)){
                            //获取面试官信息
                            String interviewArrangementValue = (String) record.get(interviewArrangementField);
                            if(StringUtils.isNotEmpty(interviewArrangementValue)){
                                String[] interviewArrangementValues = interviewArrangementValue.split("、");
                                StringBuffer uniquenessValues = new StringBuffer();
                                for (String temp : interviewArrangementValues){
                                    String uniqueness = temp.split(":")[1];
                                    uniquenessValues.append("'").append(uniqueness).append("'").append(",");
                                }
                                uniquenessValues.setLength(uniquenessValues.length()-1);
                                //获取登录认证库
                                StringBuffer sql = new StringBuffer();
                                String loginTableStr = TalentMarketsUtils.getLoginTableStr();
                                if (StringUtils.isNotBlank(loginTableStr)&&StringUtils.isNotEmpty(interviewArrangementField)) {

                                    String[] loginTableArr = loginTableStr.split(",");
                                    for (int i = 0; i < loginTableArr.length; i++) {
                                        String daPre = loginTableArr[i];
                                        sql.append("select guidkey from ").append(daPre).append("A01");
                                        sql.append(" where ").append(uniquenessField).append(" in( ");
                                        sql.append(uniquenessValues).append(")");
                                        if (i < loginTableArr.length - 1) {
                                            sql.append(" union all ");
                                        }
                                    }
                                    rowSet = dao.search(sql.toString());
                                    while (rowSet.next()){
                                        String guidkey = rowSet.getString("guidkey");
                                        RecordVo vo = new RecordVo("jp_interviewer");
                                        vo.setObject("z8101",primaryKeyValue);
                                        vo.setObject("interviewer",guidkey);
                                        vo.setObject("isjoin","1");
                                        interviewArrangementList.add(vo);
                                    }
                                }
                            }
                        }


                    } else if (StringUtils.equalsIgnoreCase(opt, "cancel")) {
                        z8103 = "08";
                        opinionFieldValue = this.getOpinionDesc("approver", opinionFieldValue, userview);
                    } else if (StringUtils.equalsIgnoreCase(opt, "stop")) {
                        z8103 = "08";
                        if (!StringUtils.contains(opinionFieldValue, "\n批注：" + userview.getUserFullName() + "：终止流程")) {
                            opinionFieldValue = this.getOpinionDesc("applicant", opinionFieldValue, userview);
                        }
                    }else if(StringUtils.equalsIgnoreCase(opt,"recall")){//撤回
                        z8103 = "08";
                    }
                    if (StringUtils.isNotBlank(z8103)) {
                        recordVo.setObject("z8103", z8103);
                    }
                    String e01a1 = (String) record.get("e01a1");
                    String b0110 = TalentMarketsUtils.getOrgItemid(e01a1, dao, "UN");
                    String e0122 = TalentMarketsUtils.getOrgItemid(e01a1, dao, "UM");
                    recordVo.setObject("b0110", b0110);
                    recordVo.setObject("e0122", e0122);
                    recordVo.setObject("e01a1", e01a1);
                    recordVo.setObject("z8105", record.get(fieldRelation.getString("z8105")));
                    recordVo.setObject("z8107", record.get(fieldRelation.getString("z8107")));
                    //recordVo.setObject("z8109", record.get(fieldRelation.getString("z8109")));
                    recordVo.setObject("z8117", opinionFieldValue);
                    if (isExits) {
                        dao.updateValueObject(recordVo);
                    } else {
                        dao.addValueObject(recordVo);
                    }
                    //保证Z81数据是正常的情况下再处理其相关的面试官
                    if (interviewArrangementList.size() > 0 && StringUtils.equalsIgnoreCase(opt, "submit")) {
                        dao.addValueObject(interviewArrangementList);
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(applyTemplateTabId, String.valueOf(tabid))) {
                this.initApplyTemplate(opt, recordVoList, opinionField, dao, userview);

            } else if (StringUtils.equalsIgnoreCase(hireTemplateTabId, String.valueOf(tabid))) {
                fieldRelation = TalentMarketsUtils.getHireTemplateRelation();
                String z8301Field = fieldRelation.getString("z8301");
                String z8101Field = fieldRelation.getString("z8101");
                String z8303 = "";
                if (StringUtils.equalsIgnoreCase(opt, "apply")) {
                    z8303 = "07";
                } else if (StringUtils.equalsIgnoreCase(opt, "submit")) {
                    z8303 = "08";
                }else if (StringUtils.equalsIgnoreCase(opt, "cancel") || StringUtils.equalsIgnoreCase(opt,"recall")) {
                    z8303 = "09";
                }else if (StringUtils.equalsIgnoreCase(opt, "stop")) {
                    z8303 = "09";
                }
                for (Object recordObject : recordVoList) {
                    LazyDynaBean record = (LazyDynaBean) recordObject;
                    //String opinionFieldValue = (String) record.get(opinionField.toLowerCase());
                    RecordVo recordVo = new RecordVo("z83");
                    String z8101 = (String) record.get(z8101Field);
                    String z8301 = (String) record.get(z8301Field);
                    recordVo.setObject("z8101", z8101);
                    recordVo.setObject("z8301", z8301);
                    if (dao.isExistRecordVo(recordVo)) {
                        recordVo = dao.findByPrimaryKey(recordVo);
                        if (StringUtils.isNotEmpty(z8303)) {
                            recordVo.setObject("z8303", z8303);
                        }
                        //recordVo.setObject("z8309", opinionFieldValue);
                        dao.updateValueObject(recordVo);
                    }

                }
            }else if(StringUtils.equalsIgnoreCase(talentDisplayTemplateId, String.valueOf(tabid))){
               if(StringUtils.equalsIgnoreCase(opt,"submit")){
                   //String guidkey = userview.getGuidkey();
                   LazyDynaBean record = (LazyDynaBean) recordVoList.get(0);
                   String tableName = (String) record.get("basepre")+"A01";
                   String a0100 = (String)record.get("a0100");
                   String guidkey = this.getGuidkey(tableName,a0100,dao);
                   RecordVo z85Vo = new RecordVo("z85");
                   z85Vo.setObject("z8501",guidkey);
                   if(dao.isExistRecordVo(z85Vo)){
                       z85Vo = dao.findByPrimaryKey(z85Vo);
                       z85Vo.setObject("z8503",1);
                       z85Vo.setObject("z8505", new Date());
                       dao.updateValueObject(z85Vo);
                   }else{
                       z85Vo.setObject("create_time", new Date());
                       z85Vo.setObject("create_user", userview.getUserName());
                       z85Vo.setObject("z8503", 1);
                       z85Vo.setObject("z8505", new Date());
                   }
                   dao.addValueObject(z85Vo);
               }
            }else if(StringUtils.equalsIgnoreCase(cancelTemplateId, String.valueOf(tabid))){
                if (StringUtils.equalsIgnoreCase(opt, "submit")) {
                    LazyDynaBean record = (LazyDynaBean) recordVoList.get(0);
                    String tableName = (String) record.get("basepre")+"A01";
                    String a0100 = (String)record.get("a0100");
                    String guidkey = this.getGuidkey(tableName,a0100,dao);
                    RecordVo z85Vo = new RecordVo("z85");
                    z85Vo.setObject("z8501", guidkey);
                    z85Vo = dao.findByPrimaryKey(z85Vo);
                    z85Vo.setObject("z8503", 0);
                    dao.updateValueObject(z85Vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
            PubFunc.closeResource(connection);
        }


    }
    private String getGuidkey(String tableName,String a0100,ContentDAO dao){
        String guidkey ="";
        try {
            RecordVo a01Vo = new RecordVo(tableName);
            a01Vo.setObject("a0100", a0100);
            a01Vo = dao.findByPrimaryKey(a01Vo);
            guidkey = a01Vo.getString("guidkey");

        }catch (Exception e){
            e.printStackTrace();
        }
        return guidkey;
    }
    private synchronized void initApplyTemplate(String opt, List recordVoList, String opinionField, ContentDAO dao, UserView userview) throws Exception {
        JSONObject fieldRelation = null;
        fieldRelation = TalentMarketsUtils.getApplyTemplateRelation();
        RecordVo z81Vo = new RecordVo("z81");
        String z8301Field = fieldRelation.getString("z8301");
        String z8101Field = fieldRelation.getString("z8101");
        String z8303 = "";
        if (StringUtils.equalsIgnoreCase(opt, "apply")) {
            z8303 = "01";
        } else if (StringUtils.equalsIgnoreCase(opt, "submit")) {
            z8303 = "02";
        }else if (StringUtils.equalsIgnoreCase(opt, "stop") || StringUtils.equalsIgnoreCase(opt,"cancel")) {//撤销 功能  才是审批未通过
            z8303 = "03";
        }
        for (Object recordObject : recordVoList) {
            LazyDynaBean record = (LazyDynaBean) recordObject;
            String opinionFieldValue = (String) record.get(opinionField.toLowerCase());
            RecordVo recordVo = new RecordVo("z83");
            String z8101 = (String) record.get(z8101Field);
            String z8301 = (String) record.get(z8301Field);
            recordVo.setObject("z8101", z8101);
            recordVo.setObject("z8301", z8301);
            if (dao.isExistRecordVo(recordVo)) {
                recordVo = dao.findByPrimaryKey(recordVo);
                if (StringUtils.isNotBlank(z8303)) {
                    recordVo.setObject("z8303", z8303);
                }
                if (StringUtils.equalsIgnoreCase(opt, "stop")) {
                    if (!StringUtils.contains(opinionFieldValue, "\n批注：" + userview.getUserFullName() + "：终止流程")) {
                        opinionFieldValue = this.getOpinionDesc("approver", opinionFieldValue, userview);
                    }
                    recordVo.setObject("z8309", opinionFieldValue);
                    dao.updateValueObject(recordVo);
                    //更新 人事异动 审批意见
                }else if (StringUtils.equalsIgnoreCase(opt, "recall") && !StringUtils.equalsIgnoreCase("03",recordVo.getString("z8303"))) {//已经是审批未通过 状态 不允许删除
                    dao.deleteValueObject(recordVo);
                    z81Vo.setObject("z8101", z8101);
                    z81Vo = dao.findByPrimaryKey(z81Vo);
                    int z8109 = z81Vo.getInt("z8109");
                    z81Vo.setInt("z8109", z8109 - 1);
                    dao.updateValueObject(z81Vo);
                }else if (StringUtils.equalsIgnoreCase(opt, "cancel")) {
                    opinionFieldValue = this.getOpinionDesc("approver", opinionFieldValue, userview);
                    recordVo.setObject("z8309", opinionFieldValue);
                    dao.updateValueObject(recordVo);
                }else{
                    recordVo.setObject("z8309", opinionFieldValue);
                    dao.updateValueObject(recordVo);
                }
            } else {
                if (StringUtils.isNotBlank(z8303)) {
                    recordVo.setObject("z8303", z8303);
                }
                recordVo.setObject("z8309", opinionFieldValue);
                recordVo.setObject("create_time", new Date());
                recordVo.setObject("create_user", userview.getUserName());
                z81Vo.setObject("z8101", z8101);
                z81Vo = dao.findByPrimaryKey(z81Vo);
                //竞聘岗位
                recordVo.setObject("z8305", z81Vo.getString("e01a1"));
                dao.addValueObject(recordVo);
                //更新应聘人数
                int z8109 = z81Vo.getInt("z8109");
                z81Vo.setInt("z8109", z8109 + 1);
                dao.updateValueObject(z81Vo);
            }

        }

    }


    @Override
    public void execution(ArrayList recordVoList, int tabid, String opt, UserView userview, String busi_tab, String mapping_str) throws GeneralException {

    }


    private String getOpinionDesc(String type, String opinionFieldValue, UserView userView) {
        opinionFieldValue += "\r\n\n" + AdminCode.getCodeName("UN", userView.getUserOrgId());
        if (StringUtils.isNotEmpty(AdminCode.getCodeName("UM", userView.getUserDeptId()))) {
            opinionFieldValue += "/";
            String deptDesc = AdminCode.getOrgUpCodeDesc(userView.getUserDeptId(), 2, 0);
            deptDesc.replaceAll("\\\\", "/");
            String spTypeDesc = "(审批人)：";
            if (StringUtils.equalsIgnoreCase(type, "applicant")) {
                spTypeDesc = "(申请人)：";
            } else if (StringUtils.equalsIgnoreCase(type, "approver")) {
                spTypeDesc = "(审批人)：";
            }
            opinionFieldValue += deptDesc + spTypeDesc + "\n" + userView.getUserFullName();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = simpleDateFormat.format(new Date());
            opinionFieldValue += "  " + date;
            opinionFieldValue += "\n批注：" + userView.getUserFullName() + "：终止流程";
        }
        return opinionFieldValue;
    }

}

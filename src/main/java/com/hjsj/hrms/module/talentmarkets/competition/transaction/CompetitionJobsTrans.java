package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionJobsServiceImpl;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 内部竞聘岗位列表交易类
 * @Author wangz
 * @Date 2019/7/24 10:17
 * @Version V1.0
 **/
public class CompetitionJobsTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //操作类型
        String operateType = (String) this.getFormHM().get("operateType");
        Map returnData = new HashMap();
        String return_code = "success";
        //空 表示无错误
        String return_msg_code = "";
        //快速查询面板进来的
        String type = (String) this.getFormHM().get("type");
        CompetitionJobsService competitionJobsService = new CompetitionJobsServiceImpl(this.getFrameconn(), this.userView);
        try {
            //获取竞聘岗位信息
            if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.search.toString())) {
                String initFlag = (String) this.getFormHM().get("init");
                String fromFlag = (String) this.getFormHM().get("from");
                String statusFlag = (String) this.getFormHM().get("status");
                String query = "0";
                String init = "1";
                String from = "portal";
                if (StringUtils.equalsIgnoreCase(initFlag, init)) {
                    String gridConfig = "";
                    if(StringUtils.equalsIgnoreCase(fromFlag, from)){
                        gridConfig = competitionJobsService.getCompetitionJobsGridConfigs(statusFlag);
                    }else{
                        gridConfig = competitionJobsService.getCompetitionJobsGridConfigs("all");
                    }
                    String postDetailRnameId = TalentMarketsUtils.getPostDetailRname();
                    Map psnOrPosPrivMap = TalentMarketsUtils.getPsnOrPosPriv(this.userView);

                    Map<String,String> noticeData = competitionJobsService.getNoticeData();
                    boolean isHavePosCardId = competitionJobsService.isHaveThePosTab();
                    //获取快速审批配置项
                    boolean quickApprove = TalentMarketsUtils.getQuickApprove(this.frameconn);
                    returnData.put("alternativeItems",noticeData.get("alternativeItems"));
                    returnData.put("groupItems",noticeData.get("groupItems"));
                    returnData.put("gridconfig", gridConfig);
                    returnData.put("postDetailRnameId", postDetailRnameId);
                    returnData.put("psnOrPosPrivMap", psnOrPosPrivMap);
                    returnData.put("isHavePosCardId", isHavePosCardId);
                    returnData.put("quickApprove", quickApprove);
                } else if (StringUtils.equalsIgnoreCase(initFlag, query)) {
                    String queryMethod = (String) this.getFormHM().get("queryMethod");
                    competitionJobsService.refsTableData(queryMethod);
                }
            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.save.toString())) {
                List dataList = (List) this.getFormHM().get("modifyDatas");
                competitionJobsService.saveCompetitionJobsData(dataList);

            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.delete.toString())) {
                String jobIds = (String) this.getFormHM().get("jobIds");
                String isConfim = (String) this.getFormHM().get("isConfim");
                String needConfim = competitionJobsService.deleteCompetitionJobsData(jobIds, isConfim);
                returnData.put("needConfim", needConfim);

            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.importData.toString())) {
                //导入时  清空岗位发布 临时表中的数据
                String tabid = TalentMarketsUtils.getReleasePostTemplate();
                if(StringUtils.isNotEmpty(tabid)){
                    String tableName = this.userView.getUserName() + "templet_" + tabid;
                    DbWizard dbwizard = new DbWizard(this.frameconn);
                    if (dbwizard.isExistTable(tableName)) {
                        try {
                            String sql = "delete from " + tableName;
                            ContentDAO dao = new ContentDAO(this.frameconn);
                            dao.update(sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                String fileId = (String) getFormHM().get("fileId");
                String errorLogPath = competitionJobsService.importData(fileId);
                if (StringUtils.isBlank(errorLogPath)) {
                    return_code = "success";
                    this.getFormHM().put("return_code", "success");
                } else {
                    return_code = "fail";
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("templateUrl", errorLogPath);
                    returnData = map;
                }
            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.importInit.toString())) {
                String fileName = competitionJobsService.downloadTemplate();
                Map<String, String> map = new HashMap<String, String>();
                map.put("templateUrl", PubFunc.encrypt(fileName));
                returnData = map;
            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.changeState.toString())) {
                //记录 id字符串 以,分隔
                String ids = (String) this.getFormHM().get("ids");
                String status = (String) this.getFormHM().get("state");
                String notice_time = (String) this.getFormHM().get("notice_time");
                competitionJobsService.changeState(status, ids,notice_time);
                //结束操作  有竞聘人员处于报名审核中的数据 结束其流程
                if(StringUtils.equalsIgnoreCase("06",status)){
                    boolean isContinue = (Boolean) this.getFormHM().get("isContinue");
                    if(isContinue){
                        competitionJobsService.endApplyTask(ids);
                    }
                }

            }else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.publicity.toString())) {
                //记录 id字符串 以,分隔
                String ids = (String) this.getFormHM().get("ids");
                List dataList = competitionJobsService.getPublicityPeopleData(ids);
                returnData.put("selectData",dataList);
            } else if (StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.getCompetitiveScopeDesc.toString())) {
                Map<String, String> z8115Map = PubFunc.DynaBean2Map((MorphDynaBean) this.getFormHM().get("z8115Map"));
                Map<String, String> e01a1Map = PubFunc.DynaBean2Map((MorphDynaBean) this.getFormHM().get("e01a1Map"));
                competitionJobsService.assembleDescAndEncryptData(z8115Map, e01a1Map);
                returnData.put("z8115Map", z8115Map);
                returnData.put("e01a1Map", e01a1Map);
            } else if (StringUtils.isNotEmpty(type)) {
                String subModuleId = (String) this.getFormHM().get("subModuleId");
                TableDataConfigCache configCache = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
                String fastQuery = "1";
                String complexQuery = "2";
                //快速查询
                if (StringUtils.equalsIgnoreCase(type, fastQuery)) {
                    ArrayList<String> valuesList = new ArrayList<String>();
                    // 输入的内容
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                    StringBuffer querySql = new StringBuffer();
                    if (valuesList != null) {
                        String sqlCondition = competitionJobsService.getSqlCondition(valuesList);
                        querySql.append(sqlCondition);
                        configCache.setQuerySql(querySql.toString());
                    } else if (valuesList == null || valuesList.isEmpty()) {
                        //刷新userView中的sql参数
                        configCache.setQuerySql(querySql.toString());
                        userView.getHm().put(subModuleId, configCache);
                    }
                } else if (StringUtils.equalsIgnoreCase(type, complexQuery)) {
                    StringBuffer condSql = new StringBuffer();
                    HashMap queryFields = configCache.getQueryFields();
                    String exp = (String) this.getFormHM().get("exp");
                    exp = SafeCode.decode(exp);
                    exp = PubFunc.keyWord_reback(exp);
                    String cond = (String) this.getFormHM().get("cond");
                    cond = SafeCode.decode(cond);
                    cond = PubFunc.keyWord_reback(cond);
                    //调用解析公共类传入查询字段集合queryFields 解析时就不会把非数据字典字段过滤掉了
                    FactorList parser = new FactorList(exp, cond, userView.getUserName(), queryFields);
                    String sqlExp = parser.getSingleTableSqlExpression("myGridData");
                    if (StringUtils.isNotEmpty(sqlExp)) {
                        condSql.append(" and ").append(sqlExp);
                    }
                    configCache.setQuerySql(condSql.toString());
                }
                userView.getHm().put(subModuleId, configCache);
            }
            //获取审批过程格式化数据
            else if(StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.approvalFormat.toString())){
                String approvalValue = (String) this.formHM.get("approvalValue");
                //ArrayList approvalValueList = TemplateUtilBo.formatOptionFiledValue(approvalValue);
                ArrayList approvalValueList = TalentMarketsUtils.formatOptionFiledValue(approvalValue);
                returnData.put("approvalValueList",approvalValueList);
            }
            //保存岗位发布通知信息
            else if(StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.savePublishedNotice.toString())){
                ArrayList selectList = (ArrayList<String>) this.formHM.get("selectArr");
                ArrayList postList = (ArrayList<String>) this.formHM.get("postArr");
                String topic = (String) this.formHM.get("topic");
                competitionJobsService.savePublishedNotice(selectList,postList,topic);
            }else if(StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.encryptE01a1.toString())){
                String e01a1 = (String) this.getFormHM().get("e01a1");
                String e01a1_e = PubFunc.encrypt(e01a1);
                returnData.put("e01a1_e",e01a1_e);
            }else if(StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.changePersonnelStatus.toString())){
                //记录 id字符串 以,分隔
                String ids = (String) this.getFormHM().get("ids");
                competitionJobsService.changePersonnelStatus(ids);
            }else if(StringUtils.equalsIgnoreCase(operateType, CompetitionJobsService.operateType.exportInterviewList.toString())){
                //记录 id字符串 以,分隔
                String z8101_e = (String) this.getFormHM().get("z8101_e");
                String fileName = competitionJobsService.exportInterviewList(z8101_e);
                returnData.put("fileName",fileName);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.createInitData.toString())){
                Map data = competitionJobsService.getCreatePostFieldList();
                //根据组织机构业务范围过滤可选面试官
                String privOrgIdStr = TalentMarketsUtils.getAllPrivOrgIdStr(this.userView);
                //获取面试安排配置项
                boolean openInterview = TalentMarketsUtils.getOpenInterview(this.frameconn);
                //新建时  清空岗位发布 临时表中的数据
                String tabid = TalentMarketsUtils.getReleasePostTemplate();
                if(StringUtils.isNotEmpty(tabid)){
                    String tableName = this.userView.getUserName() + "templet_" + tabid;
                    DbWizard dbwizard = new DbWizard(this.frameconn);
                    if (dbwizard.isExistTable(tableName)) {
                        try {
                            String sql = "delete from " + tableName;
                            ContentDAO dao = new ContentDAO(this.frameconn);
                            dao.update(sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                returnData.put("fieldMap",data);
                returnData.put("orgIdStr",privOrgIdStr);
                returnData.put("openInterview",openInterview);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.saveCreatePostData.toString())){
                //基本信息数据
                MorphDynaBean basicInformationValues = (MorphDynaBean)this.getFormHM().get("basicInformationValues");
                //竞聘范围数据
                List competitiveScopeData = (List)this.getFormHM().get("competitiveScopeData");
                //面试官数据
                List interviewerData = (List)this.getFormHM().get("interviewerData");
                competitionJobsService.saveCreatePostData(PubFunc.DynaBean2Map(basicInformationValues),competitiveScopeData,interviewerData);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.checkPostStatus.toString())){
                String e01a1 = (String) this.getFormHM().get("e01a1");
                ArrayList param = new ArrayList();
                param.add(e01a1);
                boolean isExits = competitionJobsService.checkPostStatus(param);
                returnData.put("isExits",isExits);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.getIngE01a1.toString())){
                List e01a1List = competitionJobsService.getIngE01a1();
                returnData.put("e01a1List",e01a1List);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.checkIngPersonInPost.toString())){
                String ids = (String) this.getFormHM().get("ids");
                String statusType = (String) this.getFormHM().get("statusType");
                String status = "";
                if(StringUtils.equalsIgnoreCase(statusType,"1")){
                    status = "'01'";
                }else if(StringUtils.equalsIgnoreCase(statusType,"2")){
                    status = "'02','04','05','07'";
                }
                List postList  = competitionJobsService.isHaveIngPersonInPostList(ids,status);
                returnData.put("isHavePostList",postList);
            }else if(StringUtils.equalsIgnoreCase(operateType,CompetitionJobsService.operateType.checkPublicityPersonStatus.toString())){

            }
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg_code = e.getErrorDescription();
            e.printStackTrace();
        }finally {
            this.getFormHM().put("return_code", return_code);
            this.getFormHM().put("return_data", returnData);
            this.getFormHM().put("return_msg_code", return_msg_code);
        }
    }
}

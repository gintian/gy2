package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionServiceImpl;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title CompetitorsTrans
 * @Description 竞聘人员列表页面、竞聘岗位-面试安排（init，save）交易类
 * @Company hjsj
 * @Author wangbs、caoqy
 * @Date 2019/7/24
 * @Version 1.0.0
 */
public class CompetitorsTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        CompetitionService compeService = new CompetitionServiceImpl(this.userView, this.frameconn);
        String returnCode = "success";
        String operateType = (String) this.formHM.get("operateType");
        try {
            //获取竞聘人员列表数据
            if (CompetitionService.SEARCH.equalsIgnoreCase(operateType)) {
                //有值表示从门户跳过来
                String fromValue = (String) this.formHM.get("from");
                //查询当前||历史
                String statusValue = (String) this.formHM.get("status");
                //点击柱子查询某岗位的申报人列表
                String posValue = (String) this.formHM.get("posId");

                String tableConfig = compeService.getTableConfig(fromValue, statusValue, posValue);
                String tabid = TalentMarketsUtils.getApplyResumeRname();
                boolean openInterview = TalentMarketsUtils.getOpenInterview(this.frameconn);
                this.formHM.put("openInterview", openInterview);

                tabid = StringUtils.isBlank(tabid) ? "" : tabid;
                //默认未配置登记表
                String tablePriv = "notSetTable";
                if (StringUtils.isNotBlank(tabid)) {
                    Boolean haveTablePriv = this.userView.isHaveResource(IResourceConstant.CARD, tabid);
                    if(haveTablePriv){
                        tablePriv = "true";
                    }else{
                        tablePriv = "false";
                    }
                }
                this.formHM.put("tableConfig", tableConfig);
                this.formHM.put("tabid", tabid);
                this.formHM.put("tablePriv", tablePriv);

            }
            //加密nbaseA0100
            else if (CompetitionService.ENCRYPT.equalsIgnoreCase(operateType)) {
                String nbaseA0100 = (String) this.formHM.get("nbaseA0100");
                String nBase = PubFunc.decrypt(nbaseA0100.split("`")[0]);
                String a0100 = PubFunc.decrypt(nbaseA0100.split("`")[1]);
                nbaseA0100 = PubFunc.encrypt(nBase + "`" + a0100);
                this.formHM.put("nbaseA0100", nbaseA0100);
            }
            //保存列表中修改的信息
            else if (CompetitionService.SAVEGRIDDATA.equalsIgnoreCase(operateType)) {
                List modifyDataList = (ArrayList) this.formHM.get("modifyDataList");
                compeService.saveGridData(modifyDataList);
            }
            //获取面试安排页面数据
            else if (CompetitionService.INTERVIEW_INIT.equalsIgnoreCase(operateType)) {
                String selectId = (String) this.formHM.get("selectIds");
                selectId = PubFunc.decrypt(selectId);

                Map interviewInfo = compeService.getInterviewPageInfo(selectId);
                this.formHM.put("return_data", interviewInfo);

            }
            //提交面试安排信息
            else if (CompetitionService.INTERVIEW_SAVE.equalsIgnoreCase(operateType)) {
                //被安排的岗位描述
                String posDesc = (String) this.formHM.get("posDesc");
                //面试官是否是继承上次标识
                boolean extendFlag = (Boolean) this.formHM.get("extendFlag");
                //竞聘岗位编号
                String compePosNum = (String) this.formHM.get("compePosNum");
                compePosNum = PubFunc.decrypt(compePosNum);
                //面试安排
                Map interviewPlan = PubFunc.DynaBean2Map((MorphDynaBean) this.formHM.get("interviewPlan"));
                //更新竞聘状态
                List changeCompeStatusList = (ArrayList) this.formHM.get("changeCompeStatusArr");

                //通知方式
                String noticeWay = (String) this.formHM.get("noticeWay");
                //通知title
                String noticeTitle = (String) this.formHM.get("noticeTitle");
                //通知内容
                String noticeContent = (String) this.formHM.get("noticeContent");
                //面试官list
                List interviewersList = (ArrayList) this.formHM.get("noticeInterviewerArr");
                //候选人list
                List sendCandidatesNoticeList = (ArrayList) this.formHM.get("sendCandidatesNoticeList");
                //加载第几页的数据
                int targetPage = (Integer) this.formHM.get("targetPage");
                //一页几条
                int customPageSize = (Integer) this.formHM.get("customPageSize");

                //保存面试官和候选人信息
                compeService.saveInterviewPlan(compePosNum, interviewPlan, extendFlag);

                Map candidatesTableInfo = compeService.getCandidatesTableInfo(compePosNum, targetPage, customPageSize);
                this.formHM.put("candidatesTableInfo", candidatesTableInfo);

                if (CollectionUtils.isNotEmpty(changeCompeStatusList)) {
                    //发通知
                    List checkErrorList = compeService.checkSendNoticeServer(noticeWay);
                    if (CollectionUtils.isNotEmpty(checkErrorList)) {
                        this.formHM.put("return_code", "fail");
                        this.formHM.put("checkErrorList", checkErrorList);
                        return;
                    } else {
                        List errorMsgList = compeService.sendNoticeAndChangeStatus(compePosNum, noticeWay, noticeTitle, noticeContent, posDesc, interviewersList, sendCandidatesNoticeList, changeCompeStatusList);
                        if (CollectionUtils.isNotEmpty(errorMsgList)) {
                            this.formHM.put("return_code", "fail");
                            this.formHM.put("errorMsgList", errorMsgList);
                            return;
                        }
                    }
                }
            }
            //发送通知
            else if(CompetitionService.SEND_NOTICE.equalsIgnoreCase(operateType)){
                //竞聘岗位编号
                String compePosNum = (String) this.formHM.get("compePosNum");
                compePosNum = PubFunc.decrypt(compePosNum);
                //被安排的岗位描述
                String posDesc = (String) this.formHM.get("posDesc");
                //通知方式
                String noticeWay = (String) this.formHM.get("noticeWay");
                //通知title
                String noticeTitle = (String) this.formHM.get("noticeTitle");
                //通知内容
                String noticeContent = (String) this.formHM.get("noticeContent");
                //面试官list
                List interviewersList = (ArrayList) this.formHM.get("noticeInterviewerArr");
                //候选人list
                List sendCandidatesNoticeList = (ArrayList) this.formHM.get("sendCandidatesNoticeList");
                //更新竞聘状态
                List changeCompeStatusList = (ArrayList) this.formHM.get("changeCompeStatusArr");

                if (CollectionUtils.isNotEmpty(sendCandidatesNoticeList)) {
                    compeService.sendNoticeAndChangeStatus(compePosNum, noticeWay, noticeTitle, noticeContent, posDesc, interviewersList, sendCandidatesNoticeList, changeCompeStatusList);
                }
            }
            //保存面试官信息
            else if(CompetitionService.SAVE_INTERVIEWERS_DATA.equalsIgnoreCase(operateType)){
                //竞聘岗位编号
                String compePosNum = (String) this.formHM.get("compePosNum");
                compePosNum = PubFunc.decrypt(compePosNum);
                //面试官是否是继承上次标识
                boolean extendFlag = (Boolean) this.formHM.get("extendFlag");
                //面试安排
                Map interviewPlan = PubFunc.DynaBean2Map((MorphDynaBean) this.formHM.get("interviewPlan"));
                compeService.saveInterviewPlan(compePosNum, interviewPlan, extendFlag);
            }
            //翻页、刷新、保存按钮时保存候选人信息
            else if (CompetitionService.SAVE_CANDIDATES_DATA.equalsIgnoreCase(operateType)) {
                //竞聘岗位编号
                String compePosNum = (String) this.formHM.get("compePosNum");
                int targetPage = (Integer) this.formHM.get("targetPage");
                int customPageSize = (Integer) this.formHM.get("customPageSize");
                compePosNum = PubFunc.decrypt(compePosNum);

                List candidatesDataList = (ArrayList) this.formHM.get("candidatesData");

                compeService.saveCandidatesData(compePosNum, candidatesDataList);
                Map candidatesTableInfo = compeService.getCandidatesTableInfo(compePosNum, targetPage, customPageSize);
                this.formHM.put("candidatesTableInfo", candidatesTableInfo);

            }
            //导出简历PDF
            else if (CompetitionService.EXPORT_PDF.equalsIgnoreCase(operateType)) {
                //将所需参数封装进Map
                Map<String, Object> paramsMap = this.getPdfParamsToMap();
                //导出简历PDF
                Map<String, String> dataMap = compeService.exportPdf(paramsMap);
                this.getFormHM().put("return_data", dataMap);
            }
            //导入数据——下载模板
            else if (CompetitionService.IMPORT_INIT.equalsIgnoreCase(operateType)) {
                //有值表示从门户跳过来
                String fromValue = (String) this.formHM.get("from");
                //查询当前||历史
                String statusValue = (String) this.formHM.get("status");
                //点击柱子查询某岗位的申报人列表
                String posValue = (String) this.formHM.get("posId");
                String fileName = compeService.downloadScoreTemplate(fromValue, statusValue, posValue);
                Map<String, String> map = new HashMap<String, String>();
                map.put("templateUrl", PubFunc.encrypt(fileName));
                this.getFormHM().put("return_data", map);
            }
            //导入数据
            else if (CompetitionService.IMPORT_DATA.equalsIgnoreCase(operateType)) {
                String fileId = (String) getFormHM().get("fileId");
                String errorLogPath = compeService.importData(fileId);
                if (StringUtils.isNotBlank(errorLogPath)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("templateUrl", errorLogPath);
                    returnCode = "fail";
                    this.getFormHM().put("return_data", map);
                }
            }
            //审批过程格式化数据
            else if(CompetitionService.APPROVE_FORMAT_DATA.equalsIgnoreCase(operateType)){
                String approvalValue = (String) this.formHM.get("approvalValue");
                ArrayList approvalValueList = TalentMarketsUtils.formatOptionFiledValue(approvalValue);
                this.getFormHM().put("approvalValueList",approvalValueList);
            }
            //operateType为空说明是查询组件调用该交易类
            else if (StringUtils.isBlank(operateType)) {
                //type：1快速查询  2复杂查询
                String type = (String) this.getFormHM().get("type");
                //类型不为空
                if (StringUtils.isNotBlank(type)) {
                    String subModuleId = (String) this.getFormHM().get("subModuleId");
                    TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm().get(subModuleId);

                    if (CompetitionService.QUICK_SEARCH.equalsIgnoreCase(type)) {
                        // 输入的内容
                        ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                        if (CollectionUtils.isNotEmpty(valuesList)) {
                            //拼装查询控件的查询条件
                            String sqlCondition = compeService.getSqlCondition(valuesList);
                            cache.setQuerySql(sqlCondition);
                        } else {
                            //快速条件全部删除，展示全部数据
                            cache.setQuerySql("");
                        }

                    } else if (CompetitionService.COMPLEX_SEARCH.equalsIgnoreCase(type)) {
                        StringBuffer condsql = new StringBuffer();
                        HashMap queryFields = cache.getQueryFields();
                        //表达式之间的关系
                        String exp = (String) this.getFormHM().get("exp");
                        exp = SafeCode.decode(exp);
                        exp = PubFunc.keyWord_reback(exp);
                        //表达式内容
                        String cond = (String) this.getFormHM().get("cond");
                        cond = SafeCode.decode(cond);
                        cond = PubFunc.keyWord_reback(cond);
                        //调用解析公共类时传入查询字段集合queryFields，解析时就不会将非数据字典字段过滤掉了
                        FactorList parser = new FactorList(exp, cond, this.userView.getUserName(), queryFields);
                        String sqlExp = parser.getSingleTableSqlExpression("myGridData");
                        if (StringUtils.isNotBlank(sqlExp)) {
                            condsql.append(" and ").append(sqlExp);
                        }
                        cache.setQuerySql(condsql.toString());

                    }
                    this.userView.getHm().put(subModuleId, cache);
                }
            }
            this.formHM.put("return_code", returnCode);
        } catch (GeneralException e) {
            e.printStackTrace();
            this.formHM.put("return_code", "fail");
            this.formHM.put("return_msg", e.getErrorDescription());
        }
    }

    /**
     * 封装导出简历PDF所需参数
     */
    private Map<String,Object> getPdfParamsToMap() {
        //false 单个人   all 全部人员  1 部分人员选中人员
        String flag = "1";
        String selectIds = (String) this.getFormHM().get("selectIds");//“XXXXXX,XXXXX,XXXXX”//人员的guidkey，多个使用逗号进行分割
        //日期查询类型  1月 2时间段 3季度 4年 不是查询就是0
        String querytype = "0";
        //登记表tabid
        String cardid = (String) this.getFormHM().get("cardid");
        //=1人员,=2单位,=3职位，这里是1
        String infokind = "1";
        //Usr
        String userbase = "Usr";
        //pdf  word
        String fileFlag = "pdf";
        //true
        String autoSize = "true";
        //默认值：noinfo
        String userpriv = "noinfo";
        /*0代表薪酬1登记表*/
        String istype = "1";
        //空
        String cyear = null;
        //空
        String cmonth = null;
        //空
        String season = null;
        //空
        String ctimes = null;
        //空
        String cdatestart = null;
        //空
        String cdateend = null;
        //空
        String fieldpurv = null;
        //true手机端，false pc端
        boolean isMobile = false;
        //all 多人一文档  1：一人一文档
        String flagType = (String) this.getFormHM().get("flagType");
        ArrayList nid = new ArrayList();
        //nid ObjId的List，格式：Usr`00000001
        if ("all".equals(flag) || "1".equals(flag)) {//查询多个
            if (this.getFormHM().get("nid") != null) {
                nid = (ArrayList) this.getFormHM().get("nid");
            }
        } else {
            nid.add((String) this.getFormHM().get("nid"));
        }
        Map<String,Object> map = new HashMap();
        map.put("flag", flag);
        map.put("selectIds", selectIds);
        map.put("cyear", cyear);
        map.put("querytype", querytype);
        map.put("cmonth", cmonth);
        map.put("userpriv", userpriv);
        map.put("istype", istype);
        map.put("season", season);
        map.put("ctimes", ctimes);
        map.put("cdatestart", cdatestart);
        map.put("cdateend", cdateend);
        map.put("cardid", cardid);
        map.put("infokind", infokind);
        map.put("userbase", userbase);
        map.put("fieldpurv", fieldpurv);
        map.put("fileFlag", fileFlag);
        map.put("autoSize", autoSize);
        map.put("isMobile", isMobile);
        map.put("flagType", flagType);
        map.put("nid", nid);
        if ("5".equals(infokind)) {
            //空
            String plan_id = (String) this.getFormHM().get("plan_id");
            map.put("plan_id", plan_id);
        }
        return map;
    }
}

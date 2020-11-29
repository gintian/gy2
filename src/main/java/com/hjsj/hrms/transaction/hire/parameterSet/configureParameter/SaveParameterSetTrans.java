package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SaveParameterSetTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String testTemplateID = (String) this.getFormHM().get("testTemplateID");
            testTemplateID = testTemplateID.replaceAll("＃", "#");
            String mark_type = (String) this.getFormHM().get("mark_type");
            mark_type = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(mark_type);
            // String posCardID=(String)this.getFormHM().get("posCardID");
            String musterFieldIDs = (String) this.getFormHM().get("musterFieldIDs");
            String hire_emailContext = (String) this.getFormHM().get("hire_emailContext");
            // String
            // orgFieldIDs=(String)this.getFormHM().get("orgFieldIDs");//单位介绍参数
            String posQueryFieldIDs = (String) this.getFormHM().get("posQueryFieldIDs"); // 职位查询字段
            String viewPosFieldIDs = (String) this.getFormHM().get("viewPosFieldIDs"); // 职位描述参数
            String resumeFieldsIds = (String) this.getFormHM().get("resumeFieldIds");// 浏览简历指标
            String resumeStateFieldsIds = (String) this.getFormHM().get("resumeStateFieldIds");// 简历状态指标
            String personTypeId = (String) this.getFormHM().get("personTypeId");
            /** 安全平台改造，将全角的#替换回来 **/
            personTypeId = personTypeId.replaceAll("＃", "#");
            resumeStateFieldsIds = resumeStateFieldsIds.replaceAll("＃", "#");
            String resumeLevelId = (String) this.getFormHM().get("resumeLevelIds");
            resumeLevelId = resumeLevelId.replaceAll("＃", "#");
            String resumeStaticIds = (String) this.getFormHM().get("resumeStaticIds");
            String max_count = (String) this.getFormHM().get("max_count");
            String hireObjectId = (String) this.getFormHM().get("hireObjectId");// 招聘对象指标
            hireObjectId = hireObjectId.replaceAll("＃", "#");
            String previewTableId = (String) this.getFormHM().get("previewTableId");
            previewTableId = previewTableId.replaceAll("＃", "#");
            String commonQueryIds = (String) this.getFormHM().get("commonQueryIds");
            String photo = (String) this.getFormHM().get("photo");
            String explaination = (String) this.getFormHM().get("explaination");
            String attach = (String) this.getFormHM().get("attach");
            String businessTemplateIds = (String) this.getFormHM().get("businessTemplateIds");
            String resumeCodeValue = (String) this.getFormHM().get("resumeCodeValue");
            String cultureCode = (String) this.getFormHM().get("cultureCode");
            cultureCode = cultureCode.replaceAll("＃", "#");
            String cultureCodeItem = (String) this.getFormHM().get("cultureCodeItem");
            cultureCodeItem = cultureCodeItem.replaceAll("＃", "#");
            String netHref = (String) this.getFormHM().get("netHref");
            netHref = netHref.replaceAll("？", "?");
            netHref = netHref.replaceAll("／", "/");
            netHref = netHref.replaceAll("＝", "=");
            netHref = netHref.replaceAll("＆", "&");
            String admissionCard = (String) this.getFormHM().get("admissionCard");
            admissionCard = admissionCard.replaceAll("＃", "#");
            /** 是否只显示本级单位的招聘岗位: **/
            String hirePostByLayer = (String) this.getFormHM().get("hirePostByLayer");
            /** 是否使用复杂密码 **/
            String complexPassword = (String) this.getFormHM().get("complexPassword");
            /** 密码最小长度: **/
            String passwordMinLength = (String) this.getFormHM().get("passwordMinLength");
            /** 密码最大长度 **/
            String passwordMaxLength = (String) this.getFormHM().get("passwordMaxLength");
            /** 最大登录失败次数 **/
            String failedTime = (String) this.getFormHM().get("failedTime");
            /** 解锁时间间隔 **/
            String unlockTime = (String) this.getFormHM().get("unlockTime");
            /** 是否启动简历解析服务 0不启动 1启动 **/
            String startResumeAnalysis = (String) this.getFormHM().get("startResumeAnalysis");
            /** 简历解析服务 用户名 **/
            String resumeAnalysisName = (String) this.getFormHM().get("resumeAnalysisName");
            /** 简历解析服务 密码 **/
            String resumeAnalysisPassword = (String) this.getFormHM().get("resumeAnalysisPassword");
            /** 简历解析服务密码中包含特殊字符应该转换回来 **/
            if (resumeAnalysisPassword != null) {
                resumeAnalysisPassword = PubFunc.hireKeyWord_filter_reback(resumeAnalysisPassword);
            }

            /** 简历解析服务 对外应聘职位 **/
            String resumeAnalysisForeignJob = (String) this.getFormHM().get("resumeAnalysisForeignJob");
            resumeAnalysisForeignJob = resumeAnalysisForeignJob.replaceAll("＃", "#");
            /** 招聘需求上报进行工资总额控制 */
            String isCtrlReportGZ = (String) this.getFormHM().get("isCtrlReportGZ");
            /** 招聘需求上报进行编制控制 */
            String isCtrlReportBZ = (String) this.getFormHM().get("isCtrlReportBZ");
            String positionSalaryStandardItem = (String) this.getFormHM().get("positionSalaryStandardItem");
            String interviewingRevertItemid = (String) this.getFormHM().get("interviewingRevertItemid");
            interviewingRevertItemid = interviewingRevertItemid.replaceAll("＃", "#");
            /** 面试过程是否记录 */
            String isRemenberExamine = (String) this.getFormHM().get("isRemenberExamine");
            /** 面试过程记录子集 */
            String remenberExamineSet = (String) this.getFormHM().get("remenberExamineSet");
            remenberExamineSet = remenberExamineSet.replaceAll("＃", "#");
            /** 单位部门预算表 */
            String orgWillTableId = (String) this.getFormHM().get("orgWillTableId");
            orgWillTableId = orgWillTableId.replaceAll("＃", "#");
            String moreLevelSP = (String) this.getFormHM().get("moreLevelSP");
            String spRelation = (String) this.getFormHM().get("spRelation");
            spRelation = spRelation.replaceAll("＃", "#");
            String activeField = (String) this.getFormHM().get("activeField");
            activeField = activeField.replaceAll("＃", "#");
            String positionNumber = (String) this.getFormHM().get("positionNumber");
            String hirePositionItem = "";// (String)this.getFormHM().get("hirePositionItem");
            String hirePositionNotUnionOrg = "0";// (String)this.getFormHM().get("hirePositionNotUnionOrg");
            {
                // 动态产生评测打分信息表（zp_test_template） 和 评测结果表（zp_test_result）
                ParameterSetBo parameterSetBo = new ParameterSetBo(this.getFrameconn());
                boolean flag = parameterSetBo.createEvaluatingTable(testTemplateID);
            }
            String posCommQueryFieldIDs = (String) this.getFormHM().get("posCommQueryFieldIDs");
            String smg = (String) this.getFormHM().get("smg");
            String newTime = (String) this.getFormHM().get("newTime");
            if ("#".equals(PubFunc.keyWord_reback(hireObjectId)))
                hireObjectId = "";
            // if(posCardID.equals("#"))
            // posCardID="";
            if (musterFieldIDs == null || "#".equals(PubFunc.keyWord_reback(musterFieldIDs)))
                musterFieldIDs = "";
            if (posQueryFieldIDs == null || "#".equals(PubFunc.keyWord_reback(posQueryFieldIDs)))
                posQueryFieldIDs = "";
            if (viewPosFieldIDs == null || "#".equals(PubFunc.keyWord_reback(viewPosFieldIDs)))
                viewPosFieldIDs = "";
            // if(orgFieldIDs==null)
            // orgFieldIDs="";
            if (resumeFieldsIds == null || "#".equals(PubFunc.keyWord_reback(resumeFieldsIds)))
                resumeFieldsIds = "";
            if (resumeStateFieldsIds == null || "#".equals(PubFunc.keyWord_reback(resumeStateFieldsIds)))
                resumeStateFieldsIds = "";
            if (personTypeId == null || "#".equals(PubFunc.keyWord_reback(personTypeId)))
                personTypeId = "";
            if (resumeLevelId == null || "#".equals(PubFunc.keyWord_reback(resumeLevelId)))
                resumeLevelId = "";
            if (resumeStaticIds == null || "#".equals(PubFunc.keyWord_reback(resumeStaticIds)))
                resumeStaticIds = "";
            if (previewTableId == null || "#".equals(PubFunc.keyWord_reback(previewTableId)))
                previewTableId = "";
            if (commonQueryIds == null)
                commonQueryIds = "";
            StringBuffer testTempId = new StringBuffer("");
            ParameterSetBo parameterSetBo = new ParameterSetBo(this.getFrameconn());
            String cardIDs = (String) this.getFormHM().get("cardIDs");
            StringBuffer cardIds = new StringBuffer("");
            ArrayList hireObjList = parameterSetBo.getCodeValueList();// 取得招聘对象集合
            for (int i = 0; i < hireObjList.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) hireObjList.get(i);
                if (hireObjList.size() == 1) {
                    if (cardIDs != null && !"".equals(cardIDs))
                        cardIds.append("~" + (String) abean.get("codeitemid") + "`" + cardIDs);
                    testTempId.append("~" + (String) abean.get("codeitemid") + "^" + testTemplateID);
                } else {
                    testTempId.append("~" + (String) abean.get("codeitemid") + "^" + testTemplateID.split("~")[i]);
                    if (cardIDs != null && !"".equals(cardIDs))
                        cardIds.append("~" + (String) abean.get("codeitemid") + "`" + cardIDs.split("`")[i]);
                }

            }
            String title = (String) this.getFormHM().get("titleField");
            String content = (String) this.getFormHM().get("contentField");
            String level = (String) this.getFormHM().get("levelField");
            String comment_user = (String) this.getFormHM().get("commentUserField");
            String comment_date = (String) this.getFormHM().get("commentDateField");
            String acountBeActived = (String) this.getFormHM().get("acountBeActived");
            HashMap infoMap = new HashMap();
            infoMap.put("title", title);
            infoMap.put("content", content);
            infoMap.put("level", level);
            infoMap.put("comment_user", comment_user);
            infoMap.put("comment_date", comment_date);
            String schoolPosition = (String) this.getFormHM().get("schoolPosition");
            HashMap property = new HashMap();
            property.put("schoolPosition", schoolPosition);
            String workExperience = (String) this.getFormHM().get("workExperience");
            workExperience = workExperience.replaceAll("＃", "#");
            property.put("workExperience", workExperience);
            String hireMajor = (String) this.getFormHM().get("hireMajor");
            hireMajor = hireMajor.replaceAll("＃", "#");
            property.put("hireMajor", hireMajor);
            String hireMajorCode = (String) this.getFormHM().get("hireMajorCode");
            hireMajorCode = hireMajorCode.replaceAll("＃", "#");
            String isCharField = parameterSetBo.getIsCharField(hireMajor);
            if ("0".equals(isCharField))
                hireMajorCode = "";
            property.put("hireMajorCode", hireMajorCode);
            String answerSet = (String) this.getFormHM().get("answerSet");
            answerSet = answerSet.replaceAll("＃", "#");
            property.put("answerSet", answerSet);
            property.put("acountBeActived", acountBeActived);
            String pos_listfield = (String) this.getFormHM().get("pos_listfield");
            pos_listfield = pos_listfield == null ? "" : pos_listfield;
            property.put("pos_listfield", pos_listfield);
            String pos_listfield_sort = (String) this.getFormHM().get("pos_listfield_sort");
            pos_listfield_sort = pos_listfield_sort == null ? "" : pos_listfield_sort;
            property.put("pos_listfield_sort", pos_listfield_sort);
            property.put("cards", cardIds.toString());
            ParameterSetBo.cultureList = null;
            ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
            bo.getCodeItemNet(cultureCode);
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            parameterXMLBo.setSmg(smg);
            parameterXMLBo.setNewTime(newTime);
            parameterXMLBo.setPosCommQueryFieldIDs(posCommQueryFieldIDs);// dml
                                                                         // 2011-6-22
                                                                         // 10:53:22
            parameterXMLBo.insertParam(musterFieldIDs, testTempId.substring(1), posQueryFieldIDs, viewPosFieldIDs, resumeFieldsIds, resumeStateFieldsIds, personTypeId, resumeLevelId, resumeStaticIds,
                    hireObjectId, mark_type, max_count == null ? "" : max_count, previewTableId, commonQueryIds, photo, explaination, attach, businessTemplateIds, resumeCodeValue, cultureCode,
                    cultureCodeItem, netHref, interviewingRevertItemid, isCtrlReportGZ, isCtrlReportBZ, positionSalaryStandardItem, isRemenberExamine, remenberExamineSet, orgWillTableId, infoMap,
                    moreLevelSP, hirePositionItem, hirePositionNotUnionOrg, activeField, admissionCard, positionNumber, property, hirePostByLayer, complexPassword, passwordMinLength,
                    passwordMaxLength, failedTime, unlockTime, startResumeAnalysis, resumeAnalysisName, resumeAnalysisPassword, resumeAnalysisForeignJob, spRelation);
            // zzk 2014/2/15 启动简历解析服务 在往后台作业加作业类
            if ("1".equals(startResumeAnalysis)) {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                String sql = "select * from t_sys_jobs where jobclass='com.hjsj.hrms.businessobject.sys.job.ZpAutoImpFromEmail'";
                this.frowset = dao.search(sql);
                if (!this.frowset.next()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String dateValue = sdf.format(date) + " | |-1|20";
                    sql = "insert into t_sys_jobs (job_id,description,jobclass,job_time,status,trigger_flag) select  max(job_id) +1 ,'自动从邮箱导入简历','com.hjsj.hrms.businessobject.sys.job.ZpAutoImpFromEmail','"
                            + dateValue + "','0','0' from t_sys_jobs";
                    dao.update(sql);
                }
            }
            EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn());
            employNetPortalBo.refreshPosViewAttribute();
            HashMap map = parameterXMLBo.getAttributeValues();
            if (resumeCodeValue == null || "".equals(resumeCodeValue) || "#".equals(resumeCodeValue)) {
                EmployNetPortalBo.resume_code = "";
            }
            String[] arr = resumeCodeValue.split(",");
            StringBuffer buf = new StringBuffer("");
            String resume_state = "";
            if (map.get("resume_state") != null && ((String) map.get("resume_state")).length() > 0) {
                resume_state = (String) map.get("resume_state");
            }
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == null || "".equals(arr[i]))
                    continue;
                buf.append(" or  " + resume_state + "='" + arr[i] + "' ");
            }
            if (buf.toString().length() > 0)
                EmployNetPortalBo.resume_code = buf.toString().substring(4);
            EmployNetPortalBo.netHref = netHref;
            employNetPortalBo.getInterviewingRevertItemCodeList(interviewingRevertItemid);
            EmployNetPortalBo.workExperience = null;
            employNetPortalBo.getWorkExperience();
            EmployNetPortalBo.posListField = null;
            employNetPortalBo.getPosListField();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}

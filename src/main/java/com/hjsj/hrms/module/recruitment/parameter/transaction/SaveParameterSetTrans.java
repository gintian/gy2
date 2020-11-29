package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SaveParameterSetTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ParameterSetBo parameterSetBo = new ParameterSetBo(this.getFrameconn());
            String testTemplateID = (String) this.getFormHM().get("testTemplateID");
            testTemplateID = testTemplateID.replaceAll("＃", "#").trim();
            String mark_type = (String) this.getFormHM().get("mark_type");
            mark_type = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(mark_type);
            // String posCardID=(String)this.getFormHM().get("posCardID");
            String musterFieldIDs = (String) this.getFormHM().get("musterFieldIDs");
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
            String photo = (String) this.getFormHM().get("photoH");
            String explaination = (String) this.getFormHM().get("explainationH");
            String attach = (String) this.getFormHM().get("attachH");
            String businessTemplateIds = (String) this.getFormHM().get("businessTemplateIds");
            String resumeCodeValue = (String) this.getFormHM().get("resumeCodeValue");
            String cultureCode = (String) this.getFormHM().get("cultureCode");
            cultureCode = cultureCode.replaceAll("＃", "#");
            String cultureCodeItem = (String) this.getFormHM().get("cultureCodeItem");
            cultureCodeItem = cultureCodeItem.replaceAll("＃", "#");
            String attachCodeset = (String) this.getFormHM().get("attachCodeset");
            attachCodeset = attachCodeset.replaceAll("＃", "#");
            String attachHire = (String) this.getFormHM().get("attachHire");
            attachHire = attachHire.replaceAll("＂", "\"");
            
            this.saveIdNumber();
            String hireChannelPriv = (String) this.getFormHM().get("hireChannelPriv");
            if(! "0".equalsIgnoreCase(hireChannelPriv)){
                hireChannelPriv = hireChannelPriv.replaceAll("＂", "\"");
                Map maps = (Map) JSON.parse(hireChannelPriv);
                Iterator entries = maps.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String keys = (String) entry.getKey();
                    Map value = (Map) entry.getValue();
                    String emp_id = (String) value.get("emp_id");
                    String role_id = (String) value.get("role_id");
                    String user_name = (String) value.get("user_name");
                    if (emp_id != null && !"".equalsIgnoreCase(emp_id)) {
                        String[] empIds = emp_id.split(",");
                        ContentDAO dao = new ContentDAO(this.frameconn);
                        StringBuffer sql = new StringBuffer("");
                        String nbase = "";
                        String a0100 = "";
                        for (int i = 0; i < empIds.length; i++) {
                            String empId = empIds[i];
                            if (StringUtils.isEmpty(empId)) 
                            	continue;
    
                            empId = PubFunc.decrypt(empId);
                            nbase = empId.substring(0, 3);
                            a0100 = empId.substring(3);
                            
                            ArrayList values = new ArrayList();
                            values.add(a0100);
                            
                            sql.setLength(0);
                            sql.append("select GUIDKEY ");
                            sql.append(" from " + nbase + "A01 ");
                            sql.append(" where a0100= ? " );
                            this.frowset =dao.search(sql.toString(),values);
                            if (this.frowset.next()) {
                                empId = this.frowset.getString("GUIDKEY");
                            }
                            empIds[i] = empId;
                        }
                        emp_id = StringUtils.join(empIds, ",");
                        value.put("emp_id", emp_id);
                    } else {
                        value.put("emp_id", "");
                    }
    
                    if (role_id != null && !"".equalsIgnoreCase(role_id)) {
                        String[] roleIds = role_id.split(",");
                        for (int i = 0; i < roleIds.length; i++) {
                            String roleId = roleIds[i];
                            if (StringUtils.isEmpty(roleId)) 
                            	continue;
    
                            roleId = PubFunc.decrypt(roleId);
                            roleIds[i] = roleId;
    
                        }
                        role_id = StringUtils.join(roleIds, ",");
                        value.put("role_id", role_id);
                    } else {
                        value.put("role_id", "");
                    }
                    maps.put(keys, value);
    
                    if (user_name == null || "".equalsIgnoreCase(user_name)) {
                        value.put("user_name", "");
                    }else{
                        String[] userName = user_name.split(",");
                        for (int i = 0; i < userName.length; i++) {
                            String userId = userName[i];
                            if (StringUtils.isEmpty(userId)) 
                                continue;
    
                            userId.replaceAll("＠", "@");
                            userId = PubFunc.decrypt(userId);
                            userName[i] = userId;
                        }
                        user_name = StringUtils.join(userName, ",");
                        value.put("user_name", user_name);
                    }
                }
                JSONObject jsonObject = JSONObject.fromObject(maps);
                hireChannelPriv = jsonObject.toString();
            }
            
            //应聘人员身份关联35号代码
            String candidate_status = (String) this.getFormHM().get("candidate_status");
            candidate_status = candidate_status.replaceAll("＃", "#");
            
            //证件类型关联AC号代码
            String certificate_type = (String) this.getFormHM().get("certificate_type");
            certificate_type = certificate_type.replaceAll("＃", "#");
            
            String netHref = (String) this.getFormHM().get("netHref");
            netHref = netHref.replaceAll("？", "?");
            netHref = netHref.replaceAll("／", "/");
            netHref = netHref.replaceAll("＝", "=");
            netHref = netHref.replaceAll("＆", "&");
            
            /** 准考证 **/
            String admissionCard = (String) this.getFormHM().get("admissionCard");
            admissionCard = admissionCard.replaceAll("＃", "#");
            
            /** 考试成绩模板 **/
            String scoreCard = (String) this.getFormHM().get("scoreCard");
            scoreCard = scoreCard.replaceAll("＃", "#");
            /** 社会招聘模板 **/
           /* String socialCard = (String) this.getFormHM().get("socialCard");
            socialCard = socialCard.replaceAll("＃", "#");
            *//** 校园招聘模板 **//*
            String schoolCard = (String) this.getFormHM().get("schoolCard");
            schoolCard = schoolCard.replaceAll("＃", "#");*/

            /** 是否只显示本级单位的招聘岗位: **/
            String hirePostByLayer = (String) this.getFormHM().get("hirePostByLayerH");
            /** 是否使用复杂密码 **/
            String complexPassword = (String) this.getFormHM().get("complexPasswordH");
            /** 密码最小长度: **/
            String passwordMinLength = (String) this.getFormHM().get("passwordMinLength");
            /** 密码最大长度 **/
            String passwordMaxLength = (String) this.getFormHM().get("passwordMaxLength");
            /** 最大登录失败次数 **/
            String failedTime = (String) this.getFormHM().get("failedTime");
            /** 解锁时间间隔 **/
            String unlockTime = (String) this.getFormHM().get("unlockTime");
            /** 外网已申请职位列表显示指标  **/
            String appliedPosItems = (String) this.getFormHM().get("appliedPosItems");
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
            
            // 动态产生评测打分信息表（zp_test_template） 和 评测结果表（zp_test_result）
            parameterSetBo.createEvaluatingTable(testTemplateID);

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
            
            //老招聘模板设置
            String oldCardIDs = (String) this.getFormHM().get("cardIDs");//~01`3~02`1~03`＃ //后台传过来的值，现在拼接好，如果是前台传过来的不用改变
            StringBuffer newCardIds = new StringBuffer("");  //   ~01`3~02`1~03`＃                                   ~01`3~02`1~03`＃
            ArrayList hireObjList = parameterSetBo.getCodeValueList();// 取得招聘对象集合
            String allItemsId = (String) this.getFormHM().get("allItemsId");
            HashMap mapCardsId = new HashMap();
            if(StringUtils.isNotBlank(allItemsId)) {
	            allItemsId = allItemsId.replaceAll("＃", "#").replaceAll("；", ";");
	            String[] allItemsIdArray = allItemsId.split(";");
	            for(int i = 1; i < allItemsIdArray.length; i++) {
	            	String[] aidArray = allItemsIdArray[i].split("~");
	            	mapCardsId.put(aidArray[0], aidArray[1]);
	            }
            }
            for (int i = 0; i < hireObjList.size(); i++) {
            	
                LazyDynaBean abean = (LazyDynaBean) hireObjList.get(i);
                if (hireObjList.size() == 1) {
//                    if (cardIDs != null && !cardIDs.equals(""))
//                        cardIds.append("~" + (String) abean.get("codeitemid") + "`" + cardIDs);
                    testTempId.append("~" + (String) abean.get("codeitemid") + "^" + testTemplateID);
                } else {
                	if(!"".equals(testTemplateID))
                		testTempId.append("~" + (String) abean.get("codeitemid") + "^" + testTemplateID.split("~")[i]);
                	else
                		testTempId.append("~" + (String) abean.get("codeitemid") + "^");
//                    if (cardIDs != null && !cardIDs.equals(""))
//                        cardIds.append("~" + (String) abean.get("codeitemid") + "`" + cardIDs.split("`")[i]);
                }
                if(mapCardsId.size() != 0) {
	                String key="CARDTABLE_"+(String)abean.get("codeitemid");
	             	String sValue = String.valueOf(mapCardsId.get(key));
	             	if(!"03".equals((String) abean.get("codeitemid")) && !"03".equals((String) abean.get("codeitemid"))) {
		             	if(StringUtils.isNotBlank(sValue)) {
		                	sValue = sValue.replaceAll("＃", "#");
		                	newCardIds.append("~" + (String) abean.get("codeitemid") + "`" + sValue);
		             	}
	             	}
                }
            }
            
            String title = (String) this.getFormHM().get("titleField");
            String content = (String) this.getFormHM().get("contentField");
            String level = (String) this.getFormHM().get("levelField");
            String comment_user = (String) this.getFormHM().get("commentUserField");
            String comment_date = (String) this.getFormHM().get("commentDateField");
            String acountBeActived = (String) this.getFormHM().get("acountBeActivedH");
            String destNbase = (String) this.getFormHM().get("destNbase");
            String register_endtime = (String) this.getFormHM().get("register_endtime");
            
            
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
            //2015 xiexd 将操作指标放置到最后
            StringBuffer pos_listfield2 = new StringBuffer();
            String[] pos_listfields = pos_listfield.split("`");
            int flag = 0;
            for(int i=0;i<pos_listfields.length;i++)
            {
            	String str = pos_listfields[i];
            	if("ypljl".equalsIgnoreCase(str))
            	{
            		flag = 1;
            	}else{
            		pos_listfield2.append(str+"`");
            	}
            }
            if(flag>0)
            {
            	pos_listfield2.append("ypljl");
            }else if(pos_listfield2.length()>0)
            {
            	pos_listfield2.setLength(pos_listfield2.length()-1);            	
            }
            pos_listfield = pos_listfield2.toString();
            property.put("pos_listfield", pos_listfield);
            String pos_listfield_sort = (String) this.getFormHM().get("pos_listfield_sort");
            pos_listfield_sort = pos_listfield_sort == null ? "" : pos_listfield_sort;
            property.put("pos_listfield_sort", pos_listfield_sort);
            if(mapCardsId.size() != 0)
            	property.put("cards", newCardIds.toString().replaceAll("＃", "#"));
            else 
            	property.put("cards", oldCardIDs.toString().replaceAll("＃", "#"));
            
            String unitLevel = (String) this.getFormHM().get("unitLevel");
            unitLevel = unitLevel == null ? "" : unitLevel;
            String unitOrDepart = (String) this.getFormHM().get("unitOrDepart");
            unitOrDepart = unitOrDepart == null ? "" : unitOrDepart;
            unitOrDepart = unitOrDepart.replaceAll("＃", "#");
            
            String maxFileSize = (String) this.getFormHM().get("maxFileSize");
            maxFileSize = maxFileSize == null ? "10" : maxFileSize;
            
            parameterSetBo.getCodeItemNet(cultureCode);
            
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            parameterXMLBo.setSmg(smg);
            parameterXMLBo.setNewTime(newTime);
            parameterXMLBo.setPosCommQueryFieldIDs(posCommQueryFieldIDs);// dml
                                                                         // 2011-6-22
                                                                         // 10:53:22
            parameterXMLBo.setScoreCard(scoreCard);
            parameterXMLBo.setAppliedPosItems(appliedPosItems);
            parameterXMLBo.setUnitOrDepart(unitOrDepart);
            parameterXMLBo.setAttachCodeset(attachCodeset);
            parameterXMLBo.setAttachHire(attachHire);
            parameterXMLBo.setHireChannelPriv(hireChannelPriv);
            parameterXMLBo.setCandidate_status(candidate_status);
            parameterXMLBo.setCertificate_type(certificate_type);
            parameterXMLBo.setDestNbase(destNbase);
            parameterXMLBo.setRegister_endtime(register_endtime);
            parameterXMLBo.insertParam(musterFieldIDs, testTempId.substring(1), posQueryFieldIDs, viewPosFieldIDs, resumeFieldsIds, resumeStateFieldsIds, personTypeId, resumeLevelId, resumeStaticIds,
                    hireObjectId, mark_type, max_count == null ? "" : max_count, previewTableId, commonQueryIds, photo, explaination, attach, businessTemplateIds, resumeCodeValue, cultureCode,
                    cultureCodeItem, netHref, interviewingRevertItemid, isCtrlReportGZ, isCtrlReportBZ, positionSalaryStandardItem, isRemenberExamine, remenberExamineSet, orgWillTableId, infoMap,
                    moreLevelSP, hirePositionItem, hirePositionNotUnionOrg, activeField, admissionCard, positionNumber, property, hirePostByLayer, complexPassword, passwordMinLength,
                    passwordMaxLength, failedTime, unlockTime, startResumeAnalysis, resumeAnalysisName, resumeAnalysisPassword, resumeAnalysisForeignJob, spRelation, maxFileSize, unitLevel);
           /*
            * 保存人员库设置
            */
            this.saveDbPriv();
            
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
            //刷新employNetPortalBo中的静态变量，招聘外网要用到，否则，部分参数设置完外网不生效
            employNetPortalBo.refreshPosViewAttribute();
            employNetPortalBo.refreshStaticAttribute();
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
            EmployNetPortalBo.posListField = null;
            employNetPortalBo.getWorkExperience();
            employNetPortalBo.getPosListField();
            employNetPortalBo.refreshStaticAttribute();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 保存人员库
     */
    private void saveDbPriv() {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
        List paramList=new ArrayList();
        StringBuffer strsql=new StringBuffer();  
        String func=(String)this.getFormHM().get("personStore");
        try{
           strsql.append("delete from constant where constant='ZP_DBNAME'");
		   dao.delete(strsql.toString(),paramList);    //删除常量表中的查询设值得的项
		   String sql = "insert into constant(constant,type,str_value,Describe) values('ZP_DBNAME','','"+func+"','人才库')";
		   dao.insert(sql,paramList); //添加纪录在常量表中
		   
		   RecordVo avo2=new RecordVo("constant");
	   	 	avo2.setString("constant","ZP_DBNAME");
	   	 	avo2.setString("describe","人才库");
	   	 	avo2.setString("str_value",func.toString());
	   	 	ConstantParamter.putConstantVo(avo2,"ZP_DBNAME");
       }catch(SQLException e)
	   {
          e.printStackTrace();
       }
    }
    
    /**
     * 保存证件号码指标
     */
    public void saveIdNumber()
    {
    	try
    	{
    		String func_only=(String)this.getFormHM().get("func_only");
    		if(!StringUtils.isEmpty(func_only))
    			func_only = "A01."+ func_only;
    			
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String sql="select * from constant where UPPER(constant)='ZP_ONLY_FIELD'";
    		RowSet rs=dao.search(sql);
    		if(rs.next())
    		{
    	    	String sqlsql = "update constant set str_value = '"+(func_only==null?"":func_only)+"' where constant='ZP_ONLY_FIELD'";
    	    	dao.update(sqlsql,new ArrayList());
        	}
    		else
    		{
    			String sqlsql = "insert into constant(constant,type,describe,str_value) values('ZP_ONLY_FIELD','A','招聘唯一性校验指标','"+(func_only==null?"":func_only)+"')";
    	    	dao.insert(sqlsql, new ArrayList());
    		}
    	 	RecordVo avo=new RecordVo("constant");
    	 	avo.setString("constant","ZP_ONLY_FIELD");
    	 	avo.setString("describe","招聘唯一性校验指标");
    	 	avo.setString("str_value",func_only==null?"":func_only);
    	 	ConstantParamter.putConstantVo(avo,"ZP_ONLY_FIELD");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
	
}

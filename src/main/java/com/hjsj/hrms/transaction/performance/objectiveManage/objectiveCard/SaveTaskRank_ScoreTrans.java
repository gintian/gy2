package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.attestation.zgpt.IMISPendProceed;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:1保存目标制订的 权重和分值 2:上报目标卡</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:Jun 3, 2008</p>
 *
 * @author dengcan
 * @version 4.0
 */
public class SaveTaskRank_ScoreTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            // 1 保存目标制订的 权重和分值  2上报目标卡  3.批准目标卡 4.驳回  5:校验  6:普天 确认
            String operator = (String) this.getFormHM().get("operator");
            String object_id = (String) this.getFormHM().get("object_id");
            String planid = (String) this.getFormHM().get("planid");
            String object_type = "";

            //目标卡确认
            if ("6".equals(operator)) {
                String pendingCode = (String) this.getFormHM().get("pendingCode");
                if (pendingCode != null && pendingCode.length() > 0) {
                    IMISPendProceed imip = new IMISPendProceed();
                    String pendingType = "目标制订";
                    imip.updatePendingsStateByUID(pendingCode, 1, pendingType);
                }
            } else {
                String model = (String) this.getFormHM().get("model");
                String body_id = (String) this.getFormHM().get("body_id");
                ObjectCardBo bo = new ObjectCardBo(this.getFrameconn(), planid, object_id, this.getUserView(), model, body_id, "1");
                bo.initData();  //初始化一些数据，为加扣分项目作判断

                String info = "";
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                //目标卡填写完整才允许提交 绩效报告提交才能提交 判断
                //2上报目标卡  3.批准目标卡
                if ("2".equals(operator) || "3".equals(operator)) {
                    //目标卡填写完整才允许提交
                    String targetCompleteThenGoOn = (String) bo.getPlanParam().get("TargetCompleteThenGoOn");
                    //显示绩效报告
                    String summaryFlag = (String) bo.getPlanParam().get("SummaryFlag");
                    String hql = "select object_type from per_plan where plan_id='" + planid + "'";
                    RowSet rhs = dao.search(hql);
                    while (rhs.next()) {
                        //考核对象类型
                        object_type = rhs.getString("object_type");
                    }
                    if ("True".equalsIgnoreCase(targetCompleteThenGoOn)) {
                        if ("True".equalsIgnoreCase(summaryFlag)) {
                            String sql = "";
                            if ("2".equals(object_type)) {
                                sql = "select count(*) from per_article where plan_id='" + planid + "' and A0100='" + object_id + "' and state='1'";
                            } else {
                                String str = "select * from per_mainbody  where plan_id='" + planid + "' and Object_id='" + object_id + "' and Body_id='-1'";//查出团队考核计划的团队负责人
                                RowSet rss = null;
                                rss = dao.search(str);
                                String Mainbody_id = "";
                                while (rss.next()) {
                                    Mainbody_id = rss.getString("Mainbody_id");//团队负责人id
                                }
                                sql = "select count(*) from per_article where plan_id='" + planid + "' and A0100='" + Mainbody_id + "' and state='1'";
                            }

                            RowSet ros = null;
                            info = ResourceFactory.getProperty("jx.report.uncommitted") + "!";//绩效报告没有提交
                            ros = dao.search(sql);
                            while (ros.next()) {
                                String num = ros.getString(1);
                                int number = Integer.parseInt(num);
                                if (number > 0) {
                                    info = "";
                                }
                            }
                        }

                        StringBuffer _sql = new StringBuffer();
                        RecordVo vo = bo.getPlan_vo();
                        String template_id = vo.getString("template_id");//此处改成，如果模板中的最底层项目在p04中没能存在，则说明有任务指标没填写   zhaoxg add 2014-7-23
                        if ("2".equals(object_type)) {
                            _sql.append("select * from  per_template_item where template_id='" + template_id + "' and  ");
                            _sql.append("item_id  not in ( select " + Sql_switcher.isnull("parent_id", "0") + " from per_template_item where template_id='" + template_id + "' ) ");
                            _sql.append("and item_id not in (select item_id from P04 where plan_id='" + planid + "' and A0100='" + object_id + "')");
//								 sql= "select * from P04 where plan_id='"+planid+"' and A0100='"+object_id+"' and (fromflag='1' or fromflag='3')";//个人绩效有有无个性项目
                        } else {
                            _sql.append("select * from  per_template_item where template_id='" + template_id + "' and  ");
                            _sql.append("item_id  not in ( select " + Sql_switcher.isnull("parent_id", "0") + " from per_template_item where template_id='" + template_id + "' ) ");
                            _sql.append("and item_id not in (select item_id from P04 where plan_id='" + planid + "' and B0110='" + object_id + "')");
//								 sql= "select * from P04 where plan_id='"+planid+"' and B0110='"+object_id+"' and (fromflag='1' or fromflag='3')";//团队绩效有无个性项目
                        }
                        RowSet ros = null;
                        ros = dao.search(_sql.toString());
                        while (ros.next() && !bo.isMinZero(planid, ros.getString("item_id"))) {
                            info = ResourceFactory.getProperty("jx.task.not.filled") + "!";//任务目标没有填写
                        }
                    }
                    this.getFormHM().put("info", info);
                    if (!"".equals(info)) {
                        return;
                    }

                }

                bo.setIsEmail(this.getFormHM().get("isEmail") != null?(String) this.getFormHM().get("isEmail"):"1");
                //不是驳回
                if (!"4".equals(operator)) {
                    ArrayList pointList = (ArrayList) this.getFormHM().get("valueList");
                    if (SystemConfig.getPropertyValue("noShowCommonPonit") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noShowCommonPonit"))) {
                        if (bo.getPlan_vo().getInt("status") == 8 && ("2".equals(operator) || "3".equals(operator))) {
                            if ("False".equalsIgnoreCase((String) bo.getPlanParam().get("taskAdjustNeedNew")) && pointList == null) {

                            } else {
                                if (pointList == null) {
                                    pointList = new ArrayList();
                                    pointList.addAll(bo.getCommonTask(2));
                                }
                                pointList.addAll(bo.getCommonTask(1));
                            }
                        }
                    }
                    ArrayList itemPointInfo = bo.getItemPointInfo();
                    info = "";  //评估打分允许新增考核指标 不受校验规则控制
                    // 报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)
                    if (bo.getPlanParam().get("ProcessNoVerifyAllScore") != null && "true".equalsIgnoreCase((String) bo.getPlanParam().get("ProcessNoVerifyAllScore"))) {
                        if ("1".equals(operator)) {
                            if ("07".equalsIgnoreCase(bo.getObjectSpFlag()) && "False".equalsIgnoreCase((String) bo.getPlanParam().get("taskAdjustNeedNew"))) {

                            } else {
                                //评估打分不允许新增考核指标
                                if (pointList != null && pointList.size() > 0) {
                                    info = bo.validateTask(pointList, itemPointInfo, 0);
                                }
                            }
                        } else {
                            if ((!"3".equals(model) && (object_id.equalsIgnoreCase(this.userView.getA0100()) || bo.getUn_functionary().equalsIgnoreCase(this.userView.getA0100())))
                                    || ("01".equals(bo.getObjectSpFlag()) && ("True".equalsIgnoreCase((String) bo.getPlanParam().get("allowLeadAdjustCard"))))) {
                                if (info.length() == 0 && pointList != null && ("3".equals(operator) || "2".equals(operator))) {
                                    bo.updateP04_value(pointList);
                                }
                                if (info.length() == 0) {
                                    if ("2".equals(operator) || "3".equals(operator)) {
                                        info = bo.validateRule(null);
                                        if (info.length() == 0) {
                                            if ("true".equalsIgnoreCase(bo.getIsLimitPointValue())) {
                                                info = bo.validateIsLimit(null);
                                            }
                                        }
                                    }
                                }
                                if (info.length() == 0) {
                                    if ((pointList == null || pointList.size() == 0) || ("False".equalsIgnoreCase((String) bo.getPlanParam().get("taskAdjustNeedNew")) && bo.getIsAdjustPoint() && "07".equalsIgnoreCase(bo.getObjectSpFlag()))) {
                                        info = bo.validateTaskScore();
                                    } else {
                                        info = bo.validateTask(pointList, itemPointInfo, 1);
                                    }
                                }
                            } else if (("2".equals(operator) || "3".equals(operator)) && "True".equalsIgnoreCase((String) bo.getPlanParam().get("allowLeadAdjustCard"))) {

                                if (info.length() == 0 && pointList != null) {
                                    bo.updateP04_value(pointList);
                                }
                                if (info.length() == 0) {
                                    if ("2".equals(operator) || "3".equals(operator)) {
                                        info = bo.validateRule(null);
                                        if (info.length() == 0) {
                                            if ("true".equalsIgnoreCase(bo.getIsLimitPointValue())) {
                                                info = bo.validateIsLimit(null);
                                            }
                                        }
                                    }
                                }
                                if (info.length() == 0) {
                                    if (pointList == null || pointList.size() == 0) {
                                        info = bo.validateTaskScore();
                                    } else {
                                        info = bo.validateTask(pointList, itemPointInfo, 1);
                                    }
                                }
                            }
                        }

                    } else {
                        if ("2".equals(operator) || "3".equals(operator)) {
                            if (info.length() == 0 && pointList != null) {
                                bo.updateP04_value(pointList);
                            }
                            if (info.length() == 0) {
                                info = bo.validateRule(null);
                                if (info.length() == 0) {
                                    if ("true".equalsIgnoreCase(bo.getIsLimitPointValue())) {
                                        info = bo.validateIsLimit(null);
                                    }
                                }
                            }
                        }
                    }

                    if (info.length() == 0 && "1".equals(operator))  //保存目标制订的 权重和分值
                    {
                        if (pointList != null) {
                            bo.updateP04_value(pointList);
                        }
                    }

                    if ("1".equalsIgnoreCase(operator)) {
                        String str = "";
                        //分值
                        if ("0".equals(bo.getTemplate_vo().getString("status"))) {
                            str += "总分：";
                        }
                        //权重
                        else if ("1".equals(bo.getTemplate_vo().getString("status"))) {
                            str += "权重：";
                        }

                        this.getFormHM().put("object_id", object_id);
                        this.getFormHM().put("planid", planid);
                        this.getFormHM().put("body_id", body_id);
                        this.getFormHM().put("mainbodyid", this.userView.getA0100());
                        this.getFormHM().put("_score", str + bo.getTaskScore());

                    }
                    //上报目标卡
                    else if ("2".equals(operator)) {

                        if (info.length() == 0) {
                            info = bo.validateFollowPointMustFill(2);
                            if (info.length() == 0) {
                                if (pointList != null) {
                                    bo.updateP04_value(pointList);
                                }

                                String appealObject_id = (String) this.getFormHM().get("appealObject_id");
                                String url_p = (String) this.getFormHM().get("url_p");

                                //普天集团
                                if ("true".equalsIgnoreCase(bo.getCreatCard_mail())) {
                                    if (SystemConfig.getPropertyValue("clientName") != null && "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                                        String pendingCode = (String) this.getFormHM().get("pendingCode");
                                        bo.setPri_pendingCode(pendingCode);
                                    }
                                }
                                bo.appealSpObject(object_id, appealObject_id, this.userView.getA0100(), planid, "Usr", url_p);

                                /********************zzk 2014/2/25 团队标准考核关系 目标卡交办时往per_mainbody 表中插入记录**************************************/
                                ResultSet res = null;
                                try {
                                    String kh_relations = ""; //0标准 1非标准
                                    String sql = " select kh_relations from per_object where plan_id=" + planid + " and object_id='" + object_id + "'";
                                    res = dao.search(sql);
                                    while (res.next()) {
                                        kh_relations = res.getString("kh_relations");
                                    }
                                    res.close();
                                    if (!"2".equals(object_type) && !"1".equals(kh_relations)) {
                                        String[] temps = appealObject_id.replaceAll("／", "/").split("/");
                                        String mainbody_id = temps[0];
                                        String level = temps[1];
                                        sql = " select * from per_mainbody where plan_id=" + planid + " and mainbody_id='" + mainbody_id + "'";
                                        res = dao.search(sql);
                                        if (!res.next()) {
                                            RecordVo a01_vo = bo.getSelfVo(mainbody_id, this.userView.getDbname());
                                            RecordVo vo = new RecordVo("per_mainbody");
                                            IDGenerator idg = new IDGenerator(2, this.frameconn);
                                            String aid = idg.getId("per_mainbody.id");
                                            vo.setInt("id", Integer.parseInt(aid));
                                            vo.setString("b0110", a01_vo.getString("b0110"));
                                            vo.setString("e0122", a01_vo.getString("e0122"));
                                            vo.setString("e01a1", a01_vo.getString("e01a1"));
                                            vo.setInt("body_id", Integer.parseInt(level));
                                            vo.setString("object_id", object_id);
                                            vo.setString("mainbody_id", mainbody_id);
                                            vo.setString("a0101", a01_vo.getString("a0101"));
                                            vo.setInt("plan_id", Integer.parseInt(planid));
                                            vo.setInt("status", 0);
                                            dao.addValueObject(vo);
                                        }

                                    }
                                }catch (Exception e){

                                }finally {
                                    PubFunc.closeResource(res);
                                }

                            } else {
                                info += "\r\n" + ResourceFactory.getProperty("edit_report.appeal.noSuccess") + "!";
                            }

                        } else {
                            info += "\r\n" + ResourceFactory.getProperty("edit_report.appeal.noSuccess") + "!";
                        }
                    }
                    //批准目标卡
                    else if ("3".equals(operator)) {
                        if (info.length() == 0) {
                            info = bo.validateFollowPointMustFill(2);
                            if (info.trim().length() == 0) {

                                //普天集团
                                if ("true".equalsIgnoreCase(bo.getCreatCard_mail())) {
                                    if (SystemConfig.getPropertyValue("clientName") != null && "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                                        String pendingCode = (String) this.getFormHM().get("pendingCode");
                                        bo.setPri_pendingCode(pendingCode);
                                    }
                                }
                                bo.approveSpObject(object_id, this.userView.getA0100(), planid, "Usr");
                                bo.optPersonalComment("2");

                            } else {
                                info += "\r\n" + ResourceFactory.getProperty("edit_report.appeal.noRatify") + "!";
                            }
                        } else {
                            info += "\r\n" + ResourceFactory.getProperty("edit_report.appeal.noRatify") + "!";
                        }
                    }
                    this.getFormHM().put("info", SafeCode.encode(info));

                } else {
                    //驳回操作
                    String reject_cause = SafeCode.decode((String) this.getFormHM().get("reject_cause"));
                    String rejectObj = (String) this.getFormHM().get("rejectObj");
                    //普天集团
                    if ("true".equalsIgnoreCase(bo.getCreatCard_mail())) {
                        if (SystemConfig.getPropertyValue("clientName") != null && "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                            String pendingCode = (String) this.getFormHM().get("pendingCode");
                            bo.setPri_pendingCode(pendingCode);
                        }
                    }

                    bo.rejectSpObject(object_id, this.userView.getA0100(), planid, "Usr", reject_cause, rejectObj);
                    if (rejectObj.equalsIgnoreCase(object_id)) {
                        bo.optPersonalComment("3");
                    }
                    this.getFormHM().put("info", "");
                }
                this.getFormHM().put("operator", operator);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

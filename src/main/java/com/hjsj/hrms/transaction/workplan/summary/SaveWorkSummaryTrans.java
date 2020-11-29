package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanCommunicationBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.Arrays;
import java.util.Date;

public class SaveWorkSummaryTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            String opt = (String) this.getFormHM().get("opt");
            String p0100 = (String) this.getFormHM().get("p0100");
            p0100 = WorkPlanUtil.decryption(p0100);
            String summaryCycle = (String) this.getFormHM().get("cycle");
            String summaryYear = (String)this.getFormHM().get("year");
            String summaryMonth = (String)this.getFormHM().get("month");
            
			String nbase = (String) this.getFormHM().get("nbase");
			nbase = WorkPlanUtil.decryption(nbase);
			String a0100 = (String) this.getFormHM().get("a0100");
			a0100 = WorkPlanUtil.decryption(a0100);

            WorkSummaryMethodBo workMothodBo = new WorkSummaryMethodBo(userView, this.getFrameconn());
            //保存评价
            if ("savepingyu".equalsIgnoreCase(opt)) {
                String p0113 = (String) this.getFormHM().get("p0113");
                p0113 = SafeCode.decode(p0113);
                String score = (String) this.getFormHM().get("score");
                score = null == score || "".equals(score) || Integer.parseInt(score)< 0 ? "-1" : score;
                
                workMothodBo.saveContent(p0100, p0113, score);
                this.getFormHM().put("score", score);
                this.getFormHM().put("showtext", WorkPlanUtil.formatText(p0113));
            } else if ("reject".equalsIgnoreCase(opt)) {
            	//退回
            	String rejectValue = (String)this.getFormHM().get("rejectValue");
            	if (StringUtils.isEmpty(rejectValue)) {
            		rejectValue = "无";
				} else {
					rejectValue = SafeCode.decode(rejectValue);
				}
            	if("填写退回原因".equalsIgnoreCase(rejectValue)) {
            		rejectValue = "无";
            	}
            	// 添加沟通信息
				String communicationMsg = this.userView.getUserFullName()
						+ "退回了您的工作总结，原因：" + rejectValue;
            	WorkPlanCommunicationBo workBo = new WorkPlanCommunicationBo(this.getFrameconn(), userView);
            	String dateValue = DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm");
            	workBo.publishMessage("3", p0100, communicationMsg, dateValue, "");
            	workMothodBo.rejectWorkSummary(p0100);
            	WorkPlanBo pb = new WorkPlanBo(this.getFrameconn(), userView);
            	String photoUrl = pb.getPhotoPath(userView.getDbname(), userView.getA0100());
            	this.getFormHM().put("rejectMsg", communicationMsg);
            	this.getFormHM().put("dateValue", dateValue);
            	this.getFormHM().put("photoUrl", photoUrl);
            	this.getFormHM().put("name", userView.getUserFullName());
            	
            }  else if ("approve".equalsIgnoreCase(opt)) {
            	//批准
            	workMothodBo.approveWorkSummary(p0100);
            } else if ("savescope".equalsIgnoreCase(opt)) {
            	//保存范围
            	int scope = Integer.parseInt((String) this.getFormHM().get("scope"));
            	workMothodBo.saveScopeSummary(p0100,scope);
            }else if("validatePreNow".equals(opt)){
            	Integer validPre = Integer.parseInt((String) this.getFormHM().get("validPre"));
            	Integer validNow = Integer.parseInt((String) this.getFormHM().get("validNow"));
            	String selectWeek = (String) this.getFormHM().get("week");
            	boolean fillSummary = workMothodBo.validPreNow(summaryCycle, summaryYear, summaryMonth, selectWeek, validPre, validNow);
            	this.getFormHM().put("fillSummary", fillSummary);
            }else {
                String e0122 = (String) this.getFormHM().get("e0122");
                String b01ps = (String) this.getFormHM().get("b01ps");
                // 可见范围
                int scope = Integer.parseInt((String) this.getFormHM().get("scope"));
                // 本周工作总结
                String thisWorkSummary = (String) this.getFormHM().get("thisWorkSummary");
                thisWorkSummary = SafeCode.decode(thisWorkSummary);
                // 下周工作计划
                String nextWorkPlan = (String) this.getFormHM().get("nextWorkSummary");
                nextWorkPlan = SafeCode.decode(nextWorkPlan);
                // 审批标志
                String saveState = (String) this.getFormHM().get("saveState");
                saveState = "".equals(saveState) || saveState == null ? "01" : saveState;

                if ("add".equalsIgnoreCase(opt)) {
                    //String type = (String) this.getFormHM().get("type");
                    String selectWeek = (String) this.getFormHM().get("week");
                    // 工作总结类型   0:个人； 2:部门
                    String belong_type = (String) this.getFormHM().get("belong_type");

                    if ("".equals(p0100)) {
                        WorkPlanSummaryBo wsBo = new WorkPlanSummaryBo();
                        //总结序号（上、下半年、第几季度、第几月、第几周）
                        int cycleIndex = Integer.parseInt(selectWeek);
                        if("2".equals(summaryCycle)){//月报时只取月份
                        	cycleIndex = Integer.parseInt((String) this.getFormHM().get("month"));
                        }
                        //总结起止日期
                        String[] summaryDates = wsBo.getSummaryDates(summaryCycle, summaryYear, summaryMonth, cycleIndex);

                        String submitDate = "";
                        if ("02".equalsIgnoreCase(saveState) || "01".equalsIgnoreCase(saveState) || "03".equalsIgnoreCase(saveState)) {
                            submitDate = DateUtils.FormatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                        }
                        if ("2".equalsIgnoreCase(belong_type)) {
                            if (e0122 != null && e0122.trim().length() > 0) {
                                e0122 = WorkPlanUtil.decryption(e0122);
                            }
                            if (b01ps != null && b01ps.trim().length() > 0) {
                            	b01ps = WorkPlanUtil.decryption(b01ps);
                            }
                        }else if ("0".equalsIgnoreCase(belong_type)) {
                            e0122 = this.userView.getUserDeptId();
                            b01ps = this.userView.getUserPosId();
                        } 
                        LazyDynaBean bean = new LazyDynaBean();
                        bean.set("p0109", thisWorkSummary);
                        bean.set("p0120", nextWorkPlan);
                        bean.set("state", summaryCycle);
                        bean.set("p0104", summaryDates[0]);
                        bean.set("p0106", summaryDates[1]);
                        bean.set("time", Integer.toString(cycleIndex));
                        bean.set("p0115", saveState);
                        bean.set("p0114", submitDate);
                        bean.set("scope", Integer.toString(scope));
                        bean.set("e0122", e0122);
                        bean.set("b01ps", b01ps);
                        bean.set("score", "-1");
                        bean.set("belong_type", belong_type);
                        
                        String pid = "";
                        if(nbase == null || "".equals(nbase) || a0100 == null || "".equals(a0100) ){
                        
	                        pid = workMothodBo.addWorkSummary(bean);
	
                        }else{
                        	ContentDAO dao = new ContentDAO(this.frameconn);
                    		RowSet rs = null;
                    		try {
                    			String sql = "select p0100 from p01 where nbase=? and a0100=? and state=? and p0104="+Sql_switcher.dateValue(summaryDates[0])+" and p0106="+Sql_switcher.dateValue(summaryDates[1])+" and time=? and belong_type=?";
                    			rs = dao.search(sql, Arrays.asList(nbase, a0100, summaryCycle, cycleIndex , belong_type));
                    			if (rs.next()) {// 先查是否存在这个人的总结，如果没有在添加chent 
                    				pid = String.valueOf(rs.getInt("p0100"));
                    			}else {
                    				// 自动添加别人的工作总结
                                	pid = workMothodBo.addWorkSummary(nbase, a0100, summaryCycle, summaryDates[0], summaryDates[1], cycleIndex, saveState, submitDate, scope, e0122,b01ps, belong_type);
                    			}

                    		} catch (Exception e) {
                    			e.printStackTrace();
                    			throw GeneralExceptionHandler.Handle(e);
                    		} finally {
                    			PubFunc.closeDbObj(rs);
                    		}
                        }
                        this.getFormHM().put("pulishResult",  submitDate.substring(0, 16));
                        this.getFormHM().put("p0100", WorkPlanUtil.encryption(pid));
                        this.getFormHM().put("p0115", saveState);
                    } else {
                        workMothodBo.updateWorkSummary(p0100, e0122 ,b01ps, thisWorkSummary, nextWorkPlan, scope);
                    }
                    //haosl add 20170509 
                    this.getFormHM().put("thisWorkSummary", thisWorkSummary);
                    this.getFormHM().put("nextWorkSummary", nextWorkPlan);
                } else if ("publish".equalsIgnoreCase(opt)) {

                    String pulishResult = workMothodBo.publishWorkSummary(p0100, e0122,b01ps,thisWorkSummary, nextWorkPlan, scope,saveState);

                    this.getFormHM().put("pulishResult", pulishResult.substring(0, 16));

                } else if ("edit".equalsIgnoreCase(opt)) {

                    //修改审批状态，返回结果
                    LazyDynaBean bean = workMothodBo.editWorkSummary(p0100);

                    this.getFormHM().put("thisWorkSummary", bean.get("p0109"));
                    this.getFormHM().put("nextWorkSummary", bean.get("p0120"));
                    this.getFormHM().put("p0115", bean.get("p0115"));
                    this.getFormHM().put("scope", bean.get("scope"));
                } 

                // 返回结果
                this.getFormHM().put("thisSummary", thisWorkSummary);
                this.getFormHM().put("nextPlan", nextWorkPlan);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}

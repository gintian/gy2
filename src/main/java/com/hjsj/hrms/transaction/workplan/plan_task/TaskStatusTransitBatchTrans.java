package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.workplan.*;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:批量迁移任务状态</p>
 * <p> Description:批量迁移任务状态,包含报批和批准操作,funcId:9028000777</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2015-4-14:下午15:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class TaskStatusTransitBatchTrans extends IBusiness {

	private static final long serialVersionUID = 3157510426367064522L;

	public void execute() throws GeneralException {
		String action = (String) formHM.get("action");
		String sP0700 = (String) (formHM.get("p0700") == null ? "" : formHM.get("p0700")); // 计划id
		this.getFormHM().put("info", "");// ajax的漏洞(command.js)，_outParameters属性会保留上次结果，因此用空覆盖掉info的值
		sP0700 = WorkPlanUtil.decryption(sP0700);
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		WorkPlanBo wpBo = new WorkPlanBo(frameconn, userView);
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			int p0700 = Integer.parseInt(sP0700);
			RecordVo p07Vo = wpBo.getP07Vo(p0700);
			String p0723 = p07Vo.getString("p0723");
            String objectid = "";
            if("1".equals(p0723)){//人员
            	objectid = p07Vo.getString("nbase") + p07Vo.getString("a0100");
            }else if("2".equals(p0723)){
            	objectid = p07Vo.getString("p0707");
            }
			boolean b = wpUtil.isHaveDirectSuper(objectid, p0723);
			formHM.put("isHaveDirectSuper", b+"");
			int addLogFlag = 0;
			PlanTaskTreeTableBo planBo = new PlanTaskTreeTableBo(frameconn, p0700, userView);
			// 如果设置了权重之和强制为100时校验权重 chent 20160413 start
			WorkPlanBo workPlanBo = new WorkPlanBo(frameconn, userView);
			WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.frameconn);
			Map<String, String> config = workPlanConfigBo.getXmlData();//获取配置参数
			String plan_weight = config.get("plan_weight");
			if("1".equals(plan_weight)){//控制权重
				PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(this.frameconn, p0700);
				String sumRank = taskTreeBo.getSumRank();//获取计划下权重之和
				int from = Integer.parseInt(config.get("from"));
				int to = Integer.parseInt(config.get("to"));
				int rankSum = (int)Float.parseFloat(sumRank);
				
				if(rankSum < from || rankSum > to) {
					String msg = "";
					if(from == to) {
						if(rankSum<from)
							msg = "权重之和不足"+from+"%，不允许提交！";
						else
							msg = "权重之和超过"+from+"%，不允许提交！";
					} else {
						msg = "权重之和必须在"+from+"%至"+to+"%之间！";
					}
					this.getFormHM().put("info", msg);
					return ;
				}
			}
			// 如果设置了权重之和强制为100时校验权重 chent 20160413 end
			List beans = planBo.getTableData(""); // 获得计划下的任务数据
			for (int i = 0, len = beans.size(); i < len; i++) {
				LazyDynaBean bean = (LazyDynaBean) beans.get(i);
				int p0800 = Integer.parseInt((String) bean.get("p0800"));
				RecordVo task = bo.getTask(p0800);

				if ("publish".equals(action) && bo.ifNeedToPublish(p0700, p0800)) { // 发布(报批)
					if(b){
						task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
					}else{
						task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
						task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
					}
					dao.updateValueObject(task);
					//记录日志 提交任务变更申请wusy
					RecordVo p08Vo = new RecordVo("p08");
			    	p08Vo.setInt("p0800", p0800);
			    	try {
						p08Vo = dao.findByPrimaryKey(p08Vo);
					} catch (Exception e) {
						e.printStackTrace();
					} 
					if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
						String logcontent = "提交了任务变更申请";
						new WorkPlanOperationLogBo(frameconn, userView).addLog(p0800, logcontent);
					}
					
					wpBo.deleteCooperationTask(p0800);//先删除协办任务表中数据
					// 更新协作任务待办
					WorkPlanUtil util = new WorkPlanUtil(this.frameconn, this.userView);
	                util.update_cooperationTask("1", (String) bean.get("p0800"));
	                util.updatePending_BackPlan((String) bean.get("p0800"));
				} else if ("approve".equals(action) && bo.ifNeedToApprove(p0700, p0800)) { // 批准
					task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
					String logcontent = "";
					if(task.getInt("p0833") == WorkPlanConstant.TaskChangedStatus.Changed || task.getInt("p0833") == WorkPlanConstant.TaskChangedStatus.add){
						logcontent = "批准了任务变更申请";
					}
					if (task.getInt("p0833") != WorkPlanConstant.TaskChangedStatus.Cancel) {
						task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
					} else {
						task.setString("p0809", WorkPlanConstant.TaskExecuteStatus.CANCEL);
					}
					
					dao.updateValueObject(task);
					if(!"".equals(logcontent) && addLogFlag == 0){
						RecordVo p08Vo = new RecordVo("p08");
				    	p08Vo.setInt("p0800", p0800);
				    	try {
							p08Vo = dao.findByPrimaryKey(p08Vo);
						} catch (Exception e) {
							e.printStackTrace();
						} 
						if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
							new WorkPlanOperationLogBo(frameconn, userView).addLog(p0800, logcontent);
						}
						
					}
				}
			}
			
			if("approve".equals(action)){
				// 协办任务：计划审批后，把计划下的协办任务推送给协办人上级 chent 20160607 
				wpBo.releaseCooperationTasks(sP0700);
			}
			
			// 更新操作日志
			if("publish".equals(action)){
				 //添加沟通信息
                String communicationMsg="我重新发布了工作计划";
                new WorkPlanCommunicationBo(this.frameconn,this.userView)
                .publishMessage("1",sP0700,communicationMsg,
                        DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm"),"");
                
                p07Vo.setInt("changeflag", 1); // 更新【变更标识】为已变更 chent 20160415
                //b=true 有上级时为已发布状态，没有上级时为已批准状态  haosl  update
                p07Vo.setInt("p0719", b?WorkPlanConstant.PlanApproveStatus.HandIn:WorkPlanConstant.PlanApproveStatus.Pass);// 更新【变更标识】为报批 chent 20160415
                dao.updateValueObject(p07Vo);
                
			}else if("approve".equals(action)){
				 //添加沟通信息
                String communicationMsg=this.userView.getUserFullName()+"批准了变更的工作计划";
                new WorkPlanCommunicationBo(this.frameconn,this.userView)
                .publishMessage("1",sP0700,communicationMsg,
                        DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm"),"");
                addLogFlag = 1;
                p07Vo.setInt("changeflag", 0);// 更新【变更标识】为未变更 chent 20160415
                p07Vo.setInt("p0719", WorkPlanConstant.PlanApproveStatus.Pass);// 更新【变更标识】为已批 chent 20160415
                dao.updateValueObject(p07Vo);
			}

			// 发送邮件、微信、待办
			send(action, planBo.getP07_vo());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/** 邮件标题内计划的描述：2014年8月份 */
	private String timeDesc(RecordVo plan, WorkPlanUtil util) {
		String type = plan.getString("p0725");
		String year = plan.getString("p0727");
		String month = plan.getString("p0729");
		String week = plan.getString("p0731");

		return util.getPlanPeriodDesc(type, year, month, week);
	}
	
	private void send(String action, RecordVo plan) throws Exception {
		WorkPlanUtil util = new WorkPlanUtil(frameconn, userView);
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
		wpbo.initPlan(plan.getInt("p0700"));
		
		int p0723 = plan.getInt("p0723");
		String objectid = p0723 == 2 ? plan.getString("p0707") // 部门编号
				: plan.getString("nbase") + plan.getString("a0100"); // Usr00000009

		// 部门计划时，邮件接收者为部门负责人
		String to = "";
		String bodyText = "";
		String subject = "";
		String href = "";
		
		String timeDesc = timeDesc(plan, util); // 邮件标题的计划描述时间部分
		String from = userView.getUserFullName(); // 发送者姓名
		if ("publish".equals(action)) { // 发布，接收者是当前用户上级
			// 接收者
			to = util.getMyApprovedSuperPerson(userView.getDbname(), userView.getA0100());
			if(!("".equals(to))){
                String superNbase="";
                String superA0100="";
			    if (to!=null && !"".equals(to)){
                    superNbase =to.substring(0, 3);
                    superA0100 =to.substring(3); 
                } 
				//String toName = bo.getA0101(to);
				String toName =util.getUsrA0101(superNbase, superA0100);
				
				// 邮件标题
				StringBuffer _s = new StringBuffer();
				_s.append(from).append("变更了").append(timeDesc).append("的工作计划，请批准");
				subject = _s.toString();
				
				// 邮件正文
				StringBuffer bt = new StringBuffer();
				bt.append(toName).append("，您好！<br /><br />");
				bt.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(from).append("变更了").append(timeDesc).append("的工作计划，请查阅并审批他/她的工作计划变更情况。<br /><br />");
				bt.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
				bodyText = bt.toString();
				
			       //发送待办 
				String deptName ="";
				if (p0723==2){
					deptName =util.getOrgDesc(objectid) ;
				}
				String plan_title =timeDesc+"工作计划变更申请(审批)";
                String pending_title="";
                if (deptName.length()==0){//个人计划
                	pending_title=this.userView.getUserFullName()+"的"+plan_title;
                }
                else {
                	pending_title=this.userView.getUserFullName()+"负责部门"+deptName+"的"+plan_title;
                }              
                String pending_url=wpbo.getPendingPlanUrl();
                LazyDynaBean pendingBean = new  LazyDynaBean();
                pendingBean.set("pending_url", pending_url);
                pendingBean.set("pending_title", pending_title);
                String receiver =util.getUserNameByA0100(superNbase,superA0100);
                util.sendPending_publishPlan(this.userView.getUserName(), 
                		receiver, pendingBean, plan.getString("p0700"));
                
                
			}else{
				return ;
			}
		} else if ("approve".equals(action)) { // 批准，接收者是被查看的用户
			// 接收者
			to = p0723 == 1 ? objectid : util.getFirstDeptLeaders(objectid);
			String toName = bo.getA0101(to);
			
			// 邮件标题
			StringBuffer _s = new StringBuffer();
			_s.append(from).append("批准了您的").append(timeDesc).append("工作计划变更申请");
			subject = _s.toString();
			
			// 邮件正文
			StringBuffer bt = new StringBuffer();
			bt.append(toName).append("，您好！<br /><br />");
			bt.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			bt.append(from).append("已经批准了您的").append(timeDesc).append("工作计划变更申请。<br /><br />");
			bt.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
			bodyText = bt.toString();
			
			// 批准后 更新待办信息          
			util.updatePending_approvePlan( plan.getString("p0700"));
			
		} else {
			return;
		}
		
		// 查看链接
		href = wpbo.getRemindEmail_PlanHref(to.substring(0, 3), to.substring(3), true);

		LazyDynaBean email = new LazyDynaBean();
		email.set("objectId", to);
		email.set("subject", subject);
		email.set("bodyText", bodyText);
		email.set("href", href);
		email.set("hrefDesc", "去查看计划");
		new AsyncEmailBo(frameconn, userView).send(email);
		util.sendWeixinMessageFromEmail(email);
	}

}

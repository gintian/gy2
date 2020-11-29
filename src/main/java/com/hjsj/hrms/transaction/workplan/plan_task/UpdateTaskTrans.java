package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:任务编辑</p>
 * <p>Description:更新任务</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-16:下午16:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class UpdateTaskTrans extends IBusiness {
	
	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		String fromflag=(String)params.get("fromflag");
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String othertask = (String) params.get("othertask"); // 1是穿透任务
		this.getFormHM().put("p0800", p0800);
		this.getFormHM().put("p0700", p0700);
		this.getFormHM().put("p0723", p0723);
		this.getFormHM().put("objectid", objectid);
		String taskEditFlag = (String) formHM.get("taskEditFlag");//如果是从计划页面来的会得到taskEditFlag=noReturn
		//continueUpdate:是否继续更新,当任务也处于编辑状态,不小心点击计划也关闭了,需要保存未保存的信息,此时不需要验证,不过不能保存,catch中不做任何处理
		boolean continueUpdate = false;
		if("noReturn".equalsIgnoreCase(taskEditFlag)){
			continueUpdate = true;
		}
		String taskName="";  // 子任务名称
		String p0831="";//父任务的id
		StringBuffer logcontent = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo p08 = new RecordVo("p08");
		PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
		ptbo.setFromflag(fromflag);
		p08.setInt("p0800", Integer.parseInt(p0800));
		try {
			p08 = dao.findByPrimaryKey(p08);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//获取更新前的数据,用于对比
		String oldTaskName = p08.getString("p0801");
		Date oldStart = p08.getDate("p0813");
		String oldStartTime = "";
		if(oldStart != null){
			oldStartTime = new SimpleDateFormat("yyyy-MM-dd").format(oldStart);
		}
		Date oldEnd = p08.getDate("p0815");
		String oldEndTime = "";
		if(oldEnd != null){
			oldEndTime = new SimpleDateFormat("yyyy-MM-dd").format(oldEnd);
		}
		Float oldRank = ptbo.GetRankValue(Integer.parseInt(p0700), Integer.parseInt(p0800));
		int superiorEdit = ptbo.isSuperiorEdit(params);
		try {
			if((superiorEdit >= 1 && superiorEdit <= 4) && ptbo.isSubCanEdit(params)){
				//是上级,获得下级在此任务中的权限,如果下级能动,上级就能动
			}else{
				if (!ptbo.isMyTask(params)) {
					if("0".equals(othertask) && superiorEdit!=5){
						throw new Exception("您没有更新的权限");
					}
				}
			}
			Object action = formHM.get("action");
			if ("before".equals(action)) { // 保存前先让用户知晓保存权重会带来哪些影响
				String rank = (String) formHM.get("rank");
				Number oValue = rank == null || "".equals(rank) ? null : new Float(rank);
				oValue = oValue == null ? 0.0 : Float.valueOf(oValue.floatValue() / 100);
				formHM.put("msg", ptbo.getRankMessage(params, oValue));
				return; 
			}
			
			String values = (String) formHM.get("values"); // field1=value1|field2=value2|...
			values = values == null ? "" : values;
			values = SafeCode.decode(values);			
			
			RecordVo task = ptbo.getTask(Integer.parseInt(p0800));
			//检查任务是否重名  
			p0831=task.getString("p0831").equals(p0800)?"":task.getString("p0831");
			String p0801 = values.split("`")[0].split("=")[0];
			if("p0801".equals(p0801)){
				//haosl update 20170314 修改任务名称时，当任务名称为“”时，出现角标越界异常
				String[] temp = values.split("`")[0].split("=");
				if(temp.length>1 &&!"".equals(temp[1].trim()))//任务名称为“”时，保存原来的任务名称
					taskName = temp[1];
				else//任务名称为空时给提示！
					throw GeneralExceptionHandler.Handle(new Exception("任务名称不能为空！"));
			}else{
				taskName = oldTaskName;
			}
			formHM.put("p0801",taskName);
			//formHM.put("p0803", values.split("`")[1].split("=").length >1 ?values.split("`")[1].split("=")[1]:"");
			//formHM.put("p0837", values.split("`")[8].split("=").length > 1 ?values.split("`")[8].split("=")[1] : "");
			PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700));
	        if (treeBo.taskNameIsRepeated(p0831,p0800,taskName)){            
	            throw new GeneralException("已存在同名任务,不能保存！");
	        }
	        
	        RecordVo plan = ptbo.getPlan(Integer.parseInt(p0700));
	        RecordVo copy = ptbo.getTask(Integer.parseInt(p0800));
	        RecordVo copy2 = ptbo.getTask(Integer.parseInt(p0800));
			String[] arr = values.split("`");
			Float rankValue = null;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null) {
					continue;
				}
				
				// field1=value1|field2=value2|...
				String field = arr[i].split("=")[0];
				String value = null;
				if (arr[i].indexOf("=") < arr[i].length() - 1) { // field=，这种情况[1]是无效的
					//linbz 24750 当文本中包含‘=’时，更改截取方法
					if(arr[i].split("=").length == 2){
						value = arr[i].split("=")[1];
					}else{
						String first = arr[i].split("=")[0];
						value = arr[i].substring(first.length()+1);
					}
					value = value.replace("^", "\r\n");//textarea中的字符串有回车换行符,js不支持,替换成^,后台再转回来
				}
				
				if ("rank".equalsIgnoreCase(field)) {
					if(!"asOld".equals(value)){//asOld:前台数据校验未通过,指定的标识(数据校验失败,不进行更新操作)
						float oldRanka = ptbo.GetRankValue(Integer.parseInt(p0700), Integer.parseInt(p0800));
						try {
							rankValue = value == null || "".equals(value) ? null : new Float(value);
						} catch (Exception e) {
							rankValue = oldRanka;//自动保存时校验权重是否是数字 ，不是则不保存 lis 20160625
							e.printStackTrace();
						}
						if(rankValue!=oldRanka){
							updateRank(Integer.parseInt(p0700), Integer.parseInt(p0800), copy, value, 0);
						}
					}
				} else {
					FieldItem item = DataDictionary.getFieldItem(field, 1);
					if (value == null) {
						copy.setObject(field, null);
						copy2.setObject(field, null);
					} else {
						if(!"asOld".equals(value)){
							Object itemValue = PlanTaskBo.getFieldActualValue(item, value, continueUpdate);
							if(itemValue!=null){
								copy.setObject(field, itemValue);
								copy2.setObject(field, itemValue);
							}
						}
					}
					if("p0837".equalsIgnoreCase(field)){//进度说明
						WorkPlanBo planBo = new WorkPlanBo(getFrameconn(), getUserView());
						planBo.syncP04(p0800, "p0409", value , "M");//同步目标卡的值 chent 20160414
					}
				}
			}
			//copy = ptbo.getTask(Integer.parseInt(p0800));
			// 更新任务状态
			switch(copy.getInt("p0835")) {
				case 0: copy.setString("p0809", "1"); break;
				case 100: copy.setString("p0809", "3"); break;
				default: copy.setString("p0809", "2");
			}
			
			
			// 验证任务起止时间是否合逻辑
			Date p0813 = copy.getDate("p0813"); // 开始
			String p0813s = "";
			if(p0813 != null){
				p0813s = new SimpleDateFormat("yyyy-MM-dd").format(p0813);
			}
			Date p0815 = copy.getDate("p0815"); // 结束
			String p0815s = "";
			if(p0815 != null){
				p0815s = new SimpleDateFormat("yyyy-MM-dd").format(p0815);
			}
			
			boolean validateDate = false;
			if (p0813 != null && p0815 != null && p0813.after(p0815)) { // 开始结束日期都不为空且结束日期早于开始日期，抛异常
				validateDate = true;
				if(!continueUpdate)
					throw new Exception("开始日期大于结束日期, 保存失败");
			}
			
			try{
				if(continueUpdate){
					if(validateDate){//开始日期大于结束日期   不更新  lis 20160628
						copy2.removeValue("p0813");
						copy2.removeValue("p0815");
					}
					dao.updateValueObject(copy2);		
				}
			}catch (Exception e) {
				//不做任何处理,继续执行
			}
			
			// daosl，更新操作延后，避免未校验日期就将数据存入。
			//dao.updateValueObject(copy);		
			// 判断更新是否需要改动变更状态
			// 首先不是公司领导(没有上级)发起的操作，才涉及到变更状态的切换
			if (!ptbo.isTopLeader(objectid, p0723) &&
					ptbo.ifCauseChangedStatusAltering(plan, copy)) {
				Iterator iter = copy.getValues().keySet().iterator();
				while (iter.hasNext()) {
					String field = (String) iter.next();
					if (!WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD.contains(field.toUpperCase())) {
						continue;
					}
					if (!PlanTaskBo.equal(task.getObject(field), copy.getObject(field))) { // 要改动变更状态
						copy.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Changed);
						break;
					}
				}
			}
			//上级修改下级任务,涉及到的任务状态变化  wusy
			if(superiorEdit==1 || superiorEdit==3 || superiorEdit ==4){
				copy.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
				
			}else if(superiorEdit == 2){
				copy.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Changed);
				copy.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
			}
			//上级修改下级任务,如果计划状态是已批,则任务自动批准
			if((superiorEdit >=1 && superiorEdit <= 4) && plan.getInt("p0719")==2){
				copy.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
			}
			
			// 执行更新
			if(!continueUpdate){
				 //treeBo.getRowJson(p0800, seq, p0723, othertask);
				dao.updateValueObject(copy);
			}
			if(!taskName.equals(oldTaskName)){
				logcontent.append("将任务名修改为\"").append(taskName).append("\"原任务名称为\"").append(oldTaskName).append("\"\r\n");
			}
			if(oldStart != null && !oldStart.equals(p0813)){
				if(p0813 == null){
					p0813s = "无";
				}
				logcontent.append("修改了任务开始日期,原开始日期:").append(oldStartTime).append(",新开始日期:").append(p0813s).append("\r\n");
			}
			if(oldStart == null && p0813 != null){
				logcontent.append("修改了任务开始日期,原开始日期:无,新开始日期:").append(p0813s).append("\r\n");
			}
			if(oldEnd != null && !oldEnd.equals(p0815)){
				if(p0815 == null){
					p0815s = "无";
				}
				logcontent.append("修改了任务截止日期,原截止日期:").append(oldEndTime).append(",新截止日期:").append(p0815s).append("\r\n");
			}
			if(oldEnd == null && p0815 != null){
				logcontent.append("修改了任务截止日期,原截止日期:无,新截止日期:").append(p0815s).append("\r\n");
			}
			if(rankValue != null && oldRank*100 != rankValue){
				logcontent.append("将任务权重从").append(oldRank == 0 ? 0 : (String.valueOf(oldRank*100).split("\\.")[0] + "%")).append("调整为").append(rankValue == 0 ? 0 : ((String.valueOf(rankValue).split("\\.")[0]) + "%")).append("\r\n");
			}
			if(logcontent.length() != 0){
				RecordVo p08Vo = new RecordVo("p08");
		    	p08Vo.setInt("p0800", Integer.parseInt(p0800));
		    	try {
					p08Vo = dao.findByPrimaryKey(p08Vo);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
					new WorkPlanOperationLogBo(frameconn, userView).addLog(Integer.parseInt(p0800), logcontent.toString());
				}
			}
			// 任务结束时间改变需要发送邮件通知
			long oldP0815 = task.getDate("p0815") == null ? 0 : task.getDate("p0815").getTime();
			long newP0815 = copy.getDate("p0815") == null ? 0 : copy.getDate("p0815").getTime();
			if (oldP0815 != newP0815) {
				ptbo.sendEmailsToAll(params, "endDate");
			}
			//任务名称改变了需要发送邮件
			if(!taskName.equals(oldTaskName)){
				params.put("oldTaskName", oldTaskName);
				ptbo.sendEmailsToAll(params, "changeTaskName");
			}
			//任务权重改变了需要发送邮件 //haosl 新旧权重比较方式不对  float*100会丢失精度
			if(rankValue != null && new Float(oldRank*100).intValue() != rankValue.intValue()){
				params.put("oldRank", oldRank*100);
				params.put("newRank", rankValue);
				ptbo.sendEmailsToAll(params, "changeRank");
			}
			
			LazyDynaBean b = ptbo.privilege(params);
			formHM.put("privilege", b); // 无需回显数据
			
			ptbo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (frowset != null) {
				try {
					frowset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 更新rank，来自per_task_map */
	private void updateRank(int p0700, int p0800, RecordVo copy, String value, int flag) throws Exception {
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		RecordVo plan = bo.getPlan(p0700);
		
		// 更新权重
		if (!bo.updateRank(p0700, p0800, value, flag)) {
			return;
		}
			 
		// 清除上下级的权重
		bo.clearBranchRank(p0700, p0800);
		String clearIDs = bo.getClearRankTaskIds(p0700, Integer.valueOf(p0800));
		this.formHM.put("clearIDs", clearIDs);
		
		// 首先不是公司领导(没有上级)发起的操作，才涉及到变更状态的切换
		String p0723 = plan.getString("p0723");
		String objectid = "";
		if ("1".equals(p0723)) {
			objectid = plan.getString("nbase") + plan.getString("a0100");
		} else if ("2".equals(p0723)) {
			objectid = plan.getString("p0707");
		}
		
		if (bo.isTopLeader(objectid, p0723)) {
			return;
		}
		
		// 如果权重的修改需要改动任务变更状态
		if (WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD.contains("RANK")) {
			if (bo.ifCauseChangedStatusAltering(plan, copy)) {
				copy.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Changed);
			}
		}
	}
	
}

package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:任务编辑</p>
 * <p>Description:编辑任务</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-10:下午14:56:28</p>
 * @author 刘蒙
 * @version 1.0
 */
@SuppressWarnings("all")
public class InitTaskInfoOnPageLoadTrans extends IBusiness {

	private static final long serialVersionUID = -8183024307824432578L;
	private ContentDAO dao = null;

	public void execute() throws GeneralException {
		try {
			dao = new ContentDAO(this.frameconn);
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			Map params = PlanTaskBo.setOutParams(formHM);//将请求传递的参数p0700,p0800,p0723,objectid经过转码解密后存入新的集合中,返回
			bo.setFromflag(params.get("fromflag")!=null?(String)params.get("fromflag"):"");
			if (params.size() == 0) {
				return;
			}
			//判断当前任务是否存在
			String p0800 = (String) (formHM.get("p0800") == null ? "" : formHM.get("p0800")); // 任务id
			p0800 = WorkPlanUtil.decryption(p0800);
			RowSet rs;
			rs=bo.getTask(p0800);
			if(!rs.next()){
					throw new Exception("当前任务已经被删除！");
			}
			
			// 重置任务编辑/查看界面涉及到字段的编辑情况
			Map editableFields = bo.getEditableFields(params);
			
			String step = (String) formHM.get("step");
			int iStep = step == null ? 0 : Integer.parseInt(step);
			switch (iStep) {
				case 1: step1(params, editableFields);break;
				case 2: step2(params, editableFields);break;
				case 3: step3(params, editableFields);break;
				default: ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (this.frowset != null) {
				try {
					this.frowset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/** step1: 1、计划信息; 2、[load=init]; 3、进度条; 4、对被查看人的评分 */
	private void step1(Map params, Map editableFields) throws Exception {
		plan(params);
		init(params, editableFields);
		score(params);
	}
	
	/** step2: 1、负责人; 2、参与人; 3、关注人; 4、父任务; 5、子任务 */
	private void step2(Map params, Map editableFields) throws Exception {
		staff(params, editableFields);
		parentTask(params);
		subtask(params, editableFields);
		PlanTaskBo ptb=new PlanTaskBo(frameconn, userView);
		ptb.setFromflag(params.get("fromflag")!=null?(String)params.get("fromflag"):"");
		formHM.put("superiorEdit", ptb.isSuperiorEdit(params));//上级修改下级任务
		String p0800 = (String) params.get("p0800");
		StringBuffer sbf = new StringBuffer();
		sbf.append("select org_id, nbase, a0100 from per_task_map where p0800 = ?");
		RowSet rs = null;
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
		String orgid = "";
		String nbase = "";
		String a0100 = "";
		if(rs.next()){
			orgid = rs.getString("org_id");
			nbase = rs.getString("nbase");
			a0100  = rs.getString("a0100");
			if(nbase == null){
				String userId = wpUtil.getFirstDeptLeaders(orgid);
				if(StringUtils.isNotBlank(userId)){
					nbase = userId.substring(0, 3);
					a0100 = userId.substring(3);
				}
			}
		}
		formHM.put("thisTaskDirector", this.userView.getUserFullName());//原来wpUtil.getUsrA0101(nbase, a0100)
	}
	
	
	
	/** step3: 1、待评价的任务; 2、动态展现的字段; 3、能否删除,取消,发布/批准的权限 */
	private void step3(Map params, Map editableFields) throws Exception {
		rank(params, editableFields);
		dynamic(params, editableFields);
		PlanTaskBo ptb=new PlanTaskBo(frameconn, userView);
		ptb.setFromflag(params.get("fromflag")!=null?(String)params.get("fromflag"):"");
		formHM.put("privilege", ptb.privilege(params));
	}
	
	/** 页面加载完毕，将数据回显到页面[load=init]元素中 */
	private void init(Map params, Map editableFields) throws Exception {
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 当前任务人
		String p0723 = (String) params.get("p0723"); // 任务类别
		WorkPlanBo planBo = new WorkPlanBo(this.getFrameconn(),this.userView);
		if("1".equals(p0723)){
			planBo.setNBase(objectid.substring(0,3));
			planBo.setA0100(objectid.substring(3));
		}else
			planBo.setObjectId(objectid);
		planBo.setP0723(p0723);
//		boolean subplan = planBo.isMySubTeamPeople();//是否是我的下级
		
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		RecordVo task = bo.getTask(Integer.parseInt(p0800));
		
		String init = (String) formHM.get("init");
		init = init == null ? "" : init;
		List initFields = Arrays.asList(init.split(","));
		for (int i = 0, len = initFields.size(); i < len; i++) {
			String field = (String) initFields.get(i);
			FieldItem item = DataDictionary.getFieldItem(field, 1);
			String id = item.getItemid();
			Object value = task.getObject(id);

			LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", id);
			bean.set("load", "init");
			bean.set("length", item.getItemlength() + "");
			bean.set("codeSetId", item.getCodesetid());
			bean.set("type", item.getItemtype());
			bean.set("desc", item.getItemdesc());
			bean.set("deWidth", item.getDecimalwidth() + "");
			
			//任务详情中，已发布任务名称不可编辑，lis 20160624 start
			String edit = bo.getEditStatus(editableFields, id);//always:总是可编辑, normal:需要按钮触发, none:总是不可编辑
			if("always".equals(edit) || "normal".equals(edit)){
				//linbz 20170315 已批的任务名称都不可编辑  ‘subplan’ 不知为何校验是否是本人或上下级等
//				if(!subplan && "p0801".equalsIgnoreCase(id)){
				if("p0801".equalsIgnoreCase(id)){
					String p0811 = task.getString("p0811");//审批状态，lis 20160623
					if("p0801".equalsIgnoreCase(field))//是任务名称
						if("03".equals(p0811))//如果是已批过，则不可修改名称
							bean.set("edit", "none");
						else
							bean.set("edit", edit);
				}else
					bean.set("edit", edit);
			}
			else
				bean.set("edit", edit);
			//lis 20160624 end
			
			if ("p0719".equalsIgnoreCase(id)) { // 如果是计划审批状态字段
				RecordVo plan = bo.getPlan(Integer.parseInt(p0700));
				bean.set("value", PlanTaskBo.getFieldStringValue(item, plan.getObject(id)));
			} else if ("p0809".equalsIgnoreCase(id)) { // 任务执行状态
				RecordVo plan = bo.getPlan(Integer.parseInt(p0700));
				if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Pass
						&& WorkPlanConstant.TaskStatus.APPROVE.equals(task.getString("p0811"))) { // 处于待批准状态的任务，p0809显示待批准
					bean.set("value", "待批准");
				} else {
					bean.set("value", PlanTaskBo.getFieldStringValue(item, value));
				}
			} else if ("p0835".equalsIgnoreCase(id)) { // 任务进度
				bean.set("actualValue", PlanTaskBo.getFieldStringValue(item, task.getObject(id)));
				if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(task.getString("p0809"))) { // 取消状态的任务不显示进度
					bean.set("value", PlanTaskBo.getFieldStringValue(item, value));//fuj 取消的任务也显示进度。
				} else {
					bean.set("value", PlanTaskBo.getFieldStringValue(item, value));
				}
			} else {
				if ("p0823".equalsIgnoreCase(id) && (value == null || "".equals(value))) {
					value = "1";
				}
				if("p0801".equalsIgnoreCase(id)){
					String temp = PlanTaskBo.getFieldStringValue(item, value);
					temp = SafeCode.encode(temp);
					bean.set("value",temp);
				}else
					bean.set("value", PlanTaskBo.getFieldStringValue(item, value));
			}
			
			// 对于日期或代码类型的数据，数据库中的值与展现的值会有不同
			if ("D".equals(item.getItemtype())) {
				if (null == task.getObject(id)) {
					bean.set("actualValue", "");
				} else {
					bean.set("actualValue", new SimpleDateFormat("yyyy.MM.dd").format(task.getObject(id)));
				}
			} else if (item.isCode()) {
				bean.set("actualValue", WorkPlanUtil.nvl(value, ""));
				bean.set("type", "Code");
			}

			formHM.put(bean.get("id"), bean);
		}
	}
	
	/** 查询负责人和参与人、关注人 */
	private void staff(Map params, Map editableFields) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		
		List director = new ArrayList();
		List members = new ArrayList();
		List followers = new ArrayList();
		PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
		WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
		WorkPlanUtil util = new WorkPlanUtil(frameconn, userView);
		
		String sql = "SELECT * FROM P09 WHERE p0901=2 AND p0903=?";
		frowset = dao.search(sql, Arrays.asList(new Object[] {new Integer(p0800)}));
		while (frowset.next()) {
			LazyDynaBean bean = new LazyDynaBean();
			
			int p0905 = frowset.getInt("p0905"); // 成员标识: 1、负责人;2、参与人(协办人);5、创建人
			String nbase = frowset.getString("nbase");
			String a0100 = frowset.getString("a0100");
			String fullName = frowset.getString("p0913") == null ? "" : frowset.getString("p0913");
			
			bean.set("photo", wpbo.getPhotoPath(nbase, a0100));
			bean.set("id", WorkPlanUtil.encryption(nbase + a0100));
			bean.set("fullName", fullName == null ? "" : fullName);
			bean.set("abbr", util.getTruncateA0101(fullName));
			switch (p0905) {
				case 1: director.add(bean);break;
				case 2: members.add(bean); break;
				case 3: followers.add(bean);break;
				default:;
			}
		}
		
		formHM.put("director_edit", ptbo.getEditStatus(editableFields, "director"));
		formHM.put("director", director);
		formHM.put("member_edit", ptbo.getEditStatus(editableFields, "member"));
		formHM.put("member", members);
		formHM.put("follower_edit", ptbo.getEditStatus(editableFields, "follower"));
		formHM.put("follower", followers);
	}
	
	/** 查询待评价的任务，根据权重来判断 */
	private void rank(Map params, Map editableFields) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		
		StringBuffer toEvaluateSql = new StringBuffer();
		
		if ("1".equals(p0723)) { // 人员计划
			if (objectid.length() < 4) { // 连"Usr8"长都不够
				throw new Exception("对象id无效");
			}
			String nbase = objectid.substring(0, 3);
			String a0100 = objectid.substring(3);
			
			toEvaluateSql.append("SELECT * FROM per_task_map WHERE p0800=").append(p0800);
			toEvaluateSql.append(" AND nbase='").append(nbase).append("' AND A0100='");
			toEvaluateSql.append(a0100).append("'");
		} else if ("2".equals(p0723)) { // 团队计划
			toEvaluateSql.append("SELECT * FROM per_task_map WHERE p0800=").append(p0800).append(" AND org_id='").append(objectid).append("' AND nbase is null");
		}
		
		this.frowset = dao.search(toEvaluateSql.toString());
		
//		FieldItem item = DataDictionary.getFieldItem("rank");
		FieldItem item = DataDictionary.getFieldItem("rank", "per_task_map");
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("length", item.getItemlength() + "");
		bean.set("deWidth", item.getDecimalwidth() + "");
		bean.set("id", item.getItemid());
		bean.set("desc", item.getItemdesc());
		bean.set("type", item.getItemtype());
		bean.set("edit", new PlanTaskBo(frameconn, userView).getEditStatus(editableFields, item.getItemid()));
		
		if (frowset.next()) {
			float rank = frowset.getFloat("rank");
			rank *= 100;
			
			DecimalFormat f = new DecimalFormat("###.#");
			bean.set("value", rank * 1 == 0 ? "" : f.format(rank));
		}

		formHM.put("rank", bean);
	}
	
	/** 查询需要动态展现的字段 */
	private void dynamic(Map params, Map editableFields) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		
		List usedFields = DataDictionary.getFieldList("P08", Constant.USED_FIELD_SET); // 可用的字段
		FieldItem rank=DataDictionary.getFieldItem("rank", 1);
		usedFields.add(rank);
		List dyncFields = new ArrayList(); // 可见的且不在排除范围的字段,即需动态展现的字段
		List staticFields = new ArrayList(); // 无需动态展现的字段
		for (int i = 0, len = usedFields.size(); i < len; i++) {
			FieldItem item = (FieldItem) usedFields.get(i);
			// 可见的且不在排除范围的字段,即需动态展现的字段
			if (item.isVisible() && WorkPlanConstant.TaskInfo.TASK_EXCLUDE_FIELD.indexOf(item.getItemid().toUpperCase()) == -1) {
				dyncFields.add(item);
			}else{
				LazyDynaBean staticBean = new LazyDynaBean();
				staticBean.set("feildsetid", item.getFieldsetid());
				staticBean.set("itemid", item.getItemid());
				staticBean.set("itemdesc", item.getItemdesc());
				staticFields.add(staticBean);
				
			}
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM P08 WHERE p0800=").append(p0800);
		
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		frowset = dao.search(sql.toString());
		List dyncBeans = new ArrayList();
		if (frowset.next()) {
			for (Iterator iter = dyncFields.iterator(); iter.hasNext();) {
				FieldItem item = (FieldItem) iter.next();
				if ("1".equals(params.get("p0723")) && "p0839,p0843".contains(item.getItemid())) { // 个人任务p0839不显示
					continue;
				}
				
				LazyDynaBean bean = new LazyDynaBean();
				Object actualValue = frowset.getObject(item.getItemid());
				if ("M".equals(item.getItemtype())) {
					actualValue=Sql_switcher.readMemo(frowset, item.getItemid());
				} 
				
				bean.set("id", item.getItemid());
				bean.set("length", item.getItemlength() + "");
				bean.set("deWidth", item.getDecimalwidth() + "");
				bean.set("codeSetId", item.getCodesetid());
				bean.set("type", item.getItemtype());
				bean.set("desc", item.getItemdesc());
				bean.set("edit", bo.getEditStatus(editableFields, item.getItemid()));
				bean.set("value", PlanTaskBo.getFieldStringValue(item, actualValue));

				// 对于日期或代码类型的数据，数据库中的值与展现的值会有不同
				if ("D".equals(item.getItemtype())) {
					if (actualValue != null) {
						bean.set("actualValue", new SimpleDateFormat("yyyy.MM.dd").format(actualValue));
					} else {
						bean.set("actualValue", "");
					}
				} else if (item.isCode()) {
					bean.set("actualValue", actualValue == null ? "" : actualValue);
					bean.set("type", "Code");
				}
				
				dyncBeans.add(bean);
			}
		}
		
		formHM.put("dync", dyncBeans);
		formHM.put("staticFields", staticFields);
	}
	
	/** 加载子任务 */
	private void subtask(Map params, Map editableFields) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id

		PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
		PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700));
		
		List subtasks = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		/** 业务需要，这部分代码暂时注释掉
		int[] ids = ptbo.getVisibleSubtaskIDs(Integer.parseInt(p0700), Integer.parseInt(p0800));
		 */
		int[] ids = ptbo.getAllSubtaskIDs(Integer.parseInt(p0800));
		for (int i = 0, len = ids.length; i < len; i++) {
			int taskId = ids[i];
			
			LazyDynaBean bean = new LazyDynaBean();
			
			RecordVo sub = ptbo.getTask(taskId);
			
			/* 过滤掉不是自己创建且是起草状态的任务 */
			String creater = ptbo.getCreater(taskId);
			if (PlanTaskBo.isEmpty(creater)) {
				continue;
			}
			if (!creater.equals(userView.getDbname() + userView.getA0100()) &&
					WorkPlanConstant.TaskStatus.DRAFT.equals(sub.getString("p0811"))) {
				continue;
			}
			
			
			bean.set("p0800", WorkPlanUtil.nvl(sub.getString("p0800"), ""));
			bean.set("subtaskName", SafeCode.encode(WorkPlanUtil.nvl(sub.getString("p0801"), "")));
			RecordVo directorVo =ptbo.getDirector(taskId);
			if (directorVo!=null) 	
			    bean.set("director", directorVo.getString("a0101"));
			else 
			    bean.set("director", ptbo.getDirectorA0101(taskId));
			bean.set("url", ptbo.getTaskUrl(formHM, sub.getString("p0800")));
			
			Date p0813 = sub.getDate("p0813");
			Date p0815 = sub.getDate("p0815");
			if (p0813 != null) {
				bean.set("p0813", format.format(p0813));
			}
			if (p0815 != null) {
				bean.set("p0815", format.format(p0815));
			}
			bean.set("timeDesc", treeBo.getTimeArrangeText(bean));
			
			subtasks.add(bean);
		}
		
		formHM.put("subTask_add", ptbo.getEditStatus(editableFields, "subTask"));
		formHM.put("subtasks", subtasks);
	}
	
	/** 父任务链接 */
	private void parentTask(Map params) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.p0800,t.p0801 FROM P08 p");
		sql.append(" LEFT JOIN P08 t ON p.p0831=t.p0800");
		sql.append(" WHERE p.p0800=").append(p0800);
		
		frowset = dao.search(sql.toString());
		if (frowset.next() && !p0800.equals(frowset.getString("p0800"))) {
			if (frowset.getString("p0801")!=null){
			    LazyDynaBean bean = new LazyDynaBean();
			    bean.set("url", new PlanTaskBo(frameconn, userView).getTaskUrl(formHM, frowset.getString("p0800")));
			    bean.set("p0801", SafeCode.encode(frowset.getString("p0801")));
			    
			    formHM.put("parentTask", bean);
			}
		}
	}
	
	/** 计划 */
	private void plan(Map params) throws Exception {
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		
		if ("2".equals(p0723)) { // 团队计划取其所处部门的岗位负责人id
			objectid = new WorkPlanUtil(frameconn, userView).getFirstDeptLeaders(objectid);
		}
		
		PlanTaskTreeTableBo bo = new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700));
		RecordVo plan_vo = bo.getP07Vo(Integer.parseInt(p0700)); // 计划
		RecordVo task_vo = new PlanTaskBo(frameconn, userView).getTask(Integer.parseInt(p0800));
		
		LazyDynaBean bean = new LazyDynaBean(); // 计划的描述
		/** 计划 */
		String planDesc = new WorkPlanBo(frameconn, userView).getPlanDescription(plan_vo);
		bean.set("planDesc", planDesc);
		
		/** 任务 */
		if (task_vo.getString("create_fullname").equals(userView.getUserFullName())) {
			bean.set("planBuilder", "我创建的任务");
		} else {
			bean.set("planBuilder", task_vo.getString("create_fullname") + "创建的任务");
		}
		
		// 负责人照片地址
		WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
		bean.set("planPhoto", wpbo.getPhotoPath(objectid.substring(0, 3), objectid.substring(3)));
		
		formHM.put("planInfo", bean);
	}
	
	/** 查询对被查看用户评价过得的有效分数<br />
	 * 两种情形：
	 * 1、查看自己：显示上级对我的评分
	 * 2、查看他人，显示自己对其的评分
	 */
	private void score(Map params) throws Exception {
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);  
		List all = bo.getAllEvaluations(params);
		for (int i = 0, len = all.size(); i < len; i++) {
			LazyDynaBean bean = (LazyDynaBean) all.get(i);
			String evaluatorId = bean.get("evaluator_nbase") + "" + bean.get("evaluator_a0100");
			
			if (bo.isMySuperior(evaluatorId)
					|| evaluatorId.equals(userView.getDbname() + userView.getA0100())) {
				String score = WorkPlanUtil.nvl(String.valueOf(bean.get("score")), "");
				formHM.put("evaluationScore", score);
				return;
			}
		}
	}
}

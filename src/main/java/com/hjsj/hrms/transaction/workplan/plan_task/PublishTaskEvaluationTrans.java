package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>Title:发表任务评价</p>
 * <p>Description:发表任务评价</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-10-8:下午16:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class PublishTaskEvaluationTrans extends IBusiness {
	
	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		String commandStr = (String) formHM.get("commandStr");
		if("addEvaluation".equals(commandStr)){
			addEvaluation();
		}else if("updateEvaluation".equals(commandStr)){
			updateEvaluation();
		}else if("delEvaluation".equals(commandStr)){
			delEvaluation();
		}else if("reloadScore".equals(commandStr)){
			String reloadScore = reloadScore();
			formHM.put("reloadScore", reloadScore);
		}else if("selectDirecSupEval".equals(commandStr)){
			String averageScore = selectDirecSupEval();
			formHM.put("directSupScore", averageScore);
		}
	}
	
	/**
	 * 根据p0800查询某条任务直接上级评价的平均分
	 * @return
	 */
	private String selectDirecSupEval() {
		String score = "";
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id:
		String usrId = "";
		String usrNbase = "";
		String usrA0100 = "";
		if("1".equals(p0723)){
			usrId = objectid;
			usrNbase = objectid.substring(0, 3);
			usrA0100 = objectid.substring(3);
		}else{
			usrId = wpUtil.getFirstDeptLeaders(objectid);
			usrNbase = usrId.substring(0, 3);
			usrA0100 = usrId.substring(3);
		}
		StringBuffer sbf = new StringBuffer();
		sbf.append("select p0800, AVG(score) as score from per_task_evaluation where p0800="+p0800+" and evaluator_nbase='"+userView.getDbname()+"' and evaluator_a0100='"+userView.getA0100()+"' and nbase = '"+usrNbase+"' and a0100='"+usrA0100+"' group by p0800");
		ContentDAO dao = new ContentDAO(frameconn);
		try {
			frowset = dao.search(sbf.toString());
			if(frowset.next()){
				score = frowset.getString("score");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(frowset);
		}
		return score;
	}

	private String reloadScore() {
		String score = "";
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		RecordVo p07_vo;
		try {
			p07_vo = bo.getPlan(Integer.parseInt(p0700));
			StringBuffer sql = new StringBuffer();
			String planOwnerId = "";
			if("1".equals(p0723)){
				planOwnerId = p07_vo.getString("nbase")+p07_vo.getString("a0100");
				sql.append("select p0800, AVG(score) as score from per_task_evaluation where evaluator_nbase='"+userView.getDbname()+"' and evaluator_a0100='"+userView.getA0100()+"' and nbase = '"+planOwnerId.substring(0, 3)+"' and a0100='"+planOwnerId.substring(3)+"' and p0800 = '"+p0800+"' group by p0800");
			}else if("2".equals(p0723)){
				planOwnerId = p07_vo.getString("p0707");
				sql.append("select p0800, AVG(score) as score from per_task_evaluation where evaluator_nbase='"+userView.getDbname()+"' and evaluator_a0100='"+userView.getA0100()+"' and org_id = '"+planOwnerId+"' and  p0800 = '"+p0800+"' group by p0800");
			}
			ContentDAO dao = new ContentDAO(frameconn);
			frowset = dao.search(sql.toString());
			if(frowset.next()){
				score = frowset.getInt("score")+"";
			}
		}catch (Exception e) {
				e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(frowset);
		}
		return score;
	}

	private void delEvaluation() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String evaluationId = (String) formHM.get("evaluationId");
		String score = (String) formHM.get("score");
		score = score == null || "".equals(score) ? "0" : score;
		String description = (String) formHM.get("description");
		description = SafeCode.decode(description);
		
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		int	role = wpUtil.getLoaderRole(Integer.parseInt(p0700));//上下级关系  lis 20160322
		formHM.put("role",role);
		
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			
			// 先检查是否符合任务评价的条件
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			/*if (!bo.isEvaluable(params)) {//非关键操作，前台根据权限已显示该按钮，则此处不再判断
				return;
			}*/
			// 先查询当前用户给被评价者上一次评价记录,如果存在则更新,否则新增
			StringBuffer sql = new StringBuffer();
			//sql.append("select top 1 * from per_task_evaluation where p0800 = ? and evaluator_nbase = ?  and evaluator_a0100 = ? order by evaluate_time desc");
			sql.append("delete from per_task_evaluation where id = ?");
			dao.delete(sql.toString(), Arrays.asList(new Object[]{evaluationId}));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(frowset);
		}
	}

	private void updateEvaluation() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		int	role = wpUtil.getLoaderRole(Integer.parseInt(p0700));//上下级关系  lis 20160322
		String score = (String) formHM.get("score");
		score = score == null || "".equals(score) ? "0" : score;
		String description = (String) formHM.get("description");
		description = SafeCode.decode(description);
		
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			
			// 先检查是否符合任务评价的条件
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			/*if (!bo.isEvaluable(params)) {//非关键操作，前台根据权限已显示该按钮，则此处不再判断
				return;
			}*/
			// 先查询当前用户给被评价者上一次评价记录,如果存在则更新,否则新增
			
			StringBuffer sql = new StringBuffer();
//			sql.append("SELECT * FROM per_task_evaluation WHERE evaluator_nbase=? AND evaluator_a0100=? AND p0800=? AND ?=");
//			sql.append("(CASE WHEN flag=1 THEN nbase").append(Sql_switcher.concat()).append("a0100 WHEN flag=2 THEN org_id ELSE 'NULL' END)");
			int dbServer = Sql_switcher.searchDbServer();
			if(dbServer==1){//mssql
				sql.append("select top 1 * from per_task_evaluation where p0800 = ? and evaluator_nbase = ?  and evaluator_a0100 = ? order by evaluate_time desc");
			}else if(dbServer==2){//oracle
				sql.append("select * from (select * from per_task_evaluation where p0800 = ? and evaluator_nbase = ?  and evaluator_a0100 = ? order by evaluate_time desc) a where rownum < 2");
			}
			
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[] {
				new Integer(p0800),
				userView.getDbname(),
				userView.getA0100()
			}));
			if(frowset.next()){
				RecordVo vo = new RecordVo("per_task_evaluation");
				vo.setInt("id", frowset.getInt("id"));
				vo = dao.findByPrimaryKey(vo);
				
				vo.setInt("score", Integer.parseInt(score));
				vo.setString("description", description);
				dao.updateValueObject(vo);
				
				formHM.put("role",role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(frowset);
		}
	}

	private void addEvaluation() throws GeneralException{
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		int	role = wpUtil.getLoaderRole(Integer.parseInt(p0700));//上下级关系  lis 20160322
		
		String score = (String) formHM.get("score");
		score = score == null || "".equals(score) ? "0" : score;
		String description = (String) formHM.get("description");
		description = SafeCode.decode(description);
		
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			
			// 先检查是否符合任务评价的条件
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			/*if (!bo.isEvaluable(params)) {//非关键操作，前台根据权限已显示该按钮，则此处不再判断
				return;
			}*/
			/**
			// 先查询当前用户给被评价者上一次评价记录,如果存在则更新,否则新增
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM per_task_evaluation WHERE evaluator_nbase=? AND evaluator_a0100=? AND p0800=? AND ?=");
			sql.append("(CASE WHEN flag=1 THEN nbase").append(Sql_switcher.concat()).append("a0100 WHEN flag=2 THEN org_id ELSE 'NULL' END)");
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[] {
				userView.getDbname(),
				userView.getA0100(),
				new Integer(p0800),
				objectid
			}));
			*/
			RecordVo vo = new RecordVo("per_task_evaluation");
			//if (frowset.next()) {
//				vo.setInt("id", frowset.getInt("id"));
//				vo = dao.findByPrimaryKey(vo);
//				
//				vo.setInt("score", Integer.parseInt(score));
//				vo.setString("description", description);
//				
//				dao.updateValueObject(vo);
			//} else {
				IDGenerator idg = new IDGenerator(2, frameconn);
				int id = Integer.parseInt(idg.getId("per_task_evaluation.id"));
				
				vo.setInt("id", id);
				vo.setInt("p0800", Integer.parseInt(p0800));
				vo.setInt("score", Integer.parseInt(score));
				vo.setString("description", description);
				vo.setString("evaluator_nbase", userView.getDbname());
				vo.setString("evaluator_a0100", userView.getA0100());
				vo.setDate("evaluate_time", new java.sql.Date(System.currentTimeMillis()));
				
				if ("1".equals(p0723)) { // 个人任务
					vo.setInt("flag", 1);
					vo.setString("nbase", objectid.substring(0, 3));
					vo.setString("a0100", objectid.substring(3));
				} else if ("2".equals(p0723)) { // 团队任务
					vo.setInt("flag", 2);
					vo.setString("org_id", objectid);
				} else {
					throw new Exception("未知的任务类型,评价失败");
				}
				
				dao.addValueObject(vo);
			
			formHM.put("role", role);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(frowset);
		}
	}
	
}

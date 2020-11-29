package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.Arrays;
import java.util.Map;

/**
 * <p>Title:删除任务相关人员</p>
 * <p>Description:删除任务相关人员:: 参与人和关注人</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-28:上午10:11:28</p>
 * @author 刘蒙
 * @version 2.0
 */
public class RemoveTaskStaffTrans extends IBusiness {

	private static final long serialVersionUID = -3586072572710160779L;

	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			Map params = PlanTaskBo.setOutParams(formHM);
			String p0800 = (String) params.get("p0800"); // 任务id
			String staffId = (String) formHM.get("staffid"); // 被删除人id
			String staff = (String) formHM.get("staff"); // 人员类别：member|follower
			staffId = WorkPlanUtil.decryption(staffId);
			String b0110 = "";
		 	b0110 = new WorkPlanUtil(this.getFrameconn(), userView).getFristMainDept(staffId.substring(0,3), staffId.substring(3));
			if (staffId == null || staffId.length() < 4) {
				return;
			}
			
			int type = 0; // 将人员类别转换成数字，与P09表对应
			if ("member".equals(staff)) {
				type = 2;
			} else if ("follower".equals(staff)) {
				type = 3;
			}
			if (type == 0) {
				return;
			}

			// 删除:p09
			String del_p09 = "DELETE FROM P09 WHERE p0905=? AND p0901=2 AND p0903=? AND nbase=? AND a0100=?";
			dao.delete(del_p09, Arrays.asList(new Object[] {
				new Integer(type),
				new Integer(p0800),
				staffId.substring(0, 3),
				staffId.substring(3)
			}));
			// 删除:per_task_map,参与人
			if (type ==2 ) {
				String del_map = "DELETE FROM per_task_map WHERE nbase=? AND a0100=? AND flag=? AND p0800=?";
				dao.delete(del_map, Arrays.asList(new Object[] {
					staffId.substring(0, 3),
					staffId.substring(3),
					new Integer(type),
					new Integer(p0800)
				}));
				PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
				String a0101 = bo.getA0101(staffId);
//				String sqlExist = "select a0000 from UsrA01 where A0100 = ?";
//				RowSet rs = null;
//				rs = dao.search(sqlExist, Arrays.asList(staffId.substring(3)));
				if(!"".equals(a0101)){
					//记录日志 移除任务成员  wusy
					RecordVo p08Vo = new RecordVo("p08");
			    	p08Vo.setInt("p0800", Integer.parseInt(p0800));
			    	try {
						p08Vo = dao.findByPrimaryKey(p08Vo);
					} catch (Exception e) {
						e.printStackTrace();
					} 
					if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
						String logcontent = "移除了任务成员@" + a0101;
						new WorkPlanOperationLogBo(frameconn, userView).addLog(Integer.parseInt(p0800), logcontent);
					}
					//删除任务成员,如果任务成员是部门负责人,需要多删除一条数据wusy
					if(!"".equals(b0110)){
						String del_map2 = "DELETE FROM per_task_map WHERE flag=1 AND p0800=?";
						dao.delete(del_map2, Arrays.asList(new Object[] {
							new Integer(p0800)
						}));
					}
				
				
					// 发送邮件
					
					RecordVo task = bo.getTask(Integer.parseInt(p0800));
//					RecordVo a01 = new RecordVo(staffId.substring(0, 3) + "A01");
//					a01.setString("a0100", staffId.substring(3));
//					a01 = dao.findByPrimaryKey(a01);
		
					if (!(userView.getDbname() + userView.getA0100()).equals(staffId)) { // 当前操作人不发送邮件
						LazyDynaBean body = new LazyDynaBean();
						
						body.set("target", bo.getA0101(staffId));
						body.set("operator", userView.getUserFullName());
						body.set("taskName", task.getString("p0801"));
						body.set("position", type == 2 ? "成员" : "关注人");
						String bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffRemoving());
						String subject =userView.getUserFullName()+"将您从任务\""+task.getString("p0801")+"\"的"
						 +(String)body.get("position")+"名单中移除";
						
						LazyDynaBean email = new LazyDynaBean();
						email.set("objectId", staffId);
						email.set("subject", subject);
						email.set("bodyText", bodyText);
						email.set("bodySubject", "任务提醒");
						
						params.put("objectid", staffId); // 需要将id设成被删除人的id
		//				email.set("href", bo.getHref(params));
						bo.send(email, task.getString("p0811"));
					}
				}
			}
			formHM.put("removed", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}

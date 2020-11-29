package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:保存任务相关人员</p>
 * <p>Description:负责人、参与人、关注人</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-28:上午10:11:28</p>
 * @author 刘蒙
 * @version 2.0
 */
@SuppressWarnings("all")
public class SaveTaskStaffTrans extends IBusiness {

	private static final long serialVersionUID = -3586072572710160779L;
	private List removeFollowers = new ArrayList();	//存放需要移除的关注人 	haosl 20160701

	public void execute() throws GeneralException {
		String staff = (String) formHM.get("staff"); // 人员类别: director(负责人)、member(参与人)、follower(关注人)
		try {
			if (staff == null || "".equals(staff.trim())) {
				return;
			}
			if("getStaffids".equals(staff)){
				addMemberLog();
			}
			
			LazyDynaBean bean = null; // 返回前台的bean
			
			if ("director".equals(staff)) {
				bean = director();	//保存负责人
				formHM.put("bean", bean);
			} else if ("member".equals(staff)) {
			//haosl 20160630 修改批量添加任务成员========start=====
				List list = new ArrayList();
				if(formHM.get("staffids")==null){	////staffids为null则前台是传的sttafid,不使用批量添加的方法 
					bean = memberOrFollower(2);
					formHM.put("bean", bean);
				}else{//使用批量添加成员的方法
					String str = String.valueOf(formHM.get("staffids"));
					String[] staffids = str.split("`");
					for(int i=0;i<staffids.length;i++){
						this.formHM.put("staffid", staffids[i]);
						bean = memberOrFollower(2);	//保存多个任务成员
						list.add(bean);
					}
					formHM.put("beans", list);
					formHM.put("removeFollowers", removeFollowers);
				}
				//haosl 20160630 修改批量添加任务成员========end=====
			} else if ("follower".equals(staff)) {
				List list = new ArrayList();
				if(formHM.get("staffids")==null){	////staffids为null则前台是传的sttafid,不使用批量添加的方法 
					bean = memberOrFollower(3);//保存任务关注人
					formHM.put("bean", bean);
				}else{//使用批量添加成员的方法
					String str = String.valueOf(formHM.get("staffids"));
					String[] staffids = str.split("`");
					for(int i=0;i<staffids.length;i++){
						this.formHM.put("staffid", staffids[i]);
						bean = memberOrFollower(3);	//保存多个任务关注人
						list.add(bean);
					}
					formHM.put("beans", list);
				}
			}
			PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
			ptbo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
		} catch (Exception e) {
			e.printStackTrace();
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
	
	private void addMemberLog(){
		ContentDAO dao = new ContentDAO(frameconn);
    	Map params = PlanTaskBo.setOutParams(formHM);
    	String p0800 = (String) params.get("p0800");
    	String p0723 = (String) params.get("p0723");
    	String objectid = (String) params.get("objectid");
    	String staffids = (String) formHM.get("staffids");
    	String role = (String) formHM.get("role");
    	if("".equals(staffids)){
    		return;
    	}
    	String[] arr = staffids.split("`");
    	String staffid = "";
    	if(StringUtils.isBlank(role)){
    		return;
    	}
    	String logcontent = "";
    	if("3".equals(role)){
    		logcontent = "添加了任务成员";
    	}else{
    		logcontent = "添加了任务关注人";
    	}
    	for(int i=0; i<arr.length; i++){
    		staffid = arr[i];
    		staffid = PubFunc.decryption(staffid);
    		RecordVo vo = new RecordVo(staffid.substring(0, 3) + "A01");
    		vo.setString("a0100", staffid.substring(3));
    		try {
				vo = dao.findByPrimaryKey(vo);
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		logcontent += "@" + vo.getString("a0101") + ",";
    	}
    	RecordVo p08Vo = new RecordVo("p08");
    	p08Vo.setInt("p0800", Integer.parseInt(p0800));
    	try {
			p08Vo = dao.findByPrimaryKey(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
			new WorkPlanOperationLogBo(frameconn, userView).addLog(Integer.parseInt(p0800), logcontent.substring(0, logcontent.length()-1));
		}
    	return;
	}
	
	/** 保存任务成员或关注人
	 * @param flag 2: 参与人, 3: 关注人
	 */
	
	private LazyDynaBean memberOrFollower(int flag) throws Exception {
		ContentDAO dao = new ContentDAO(frameconn);
		WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		Map params = PlanTaskBo.setOutParams(formHM);
		String b0110 = "";
		String objectid = (String) params.get("objectid");
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0723 = (String) params.get("p0723");
		Integer iP0800 = new Integer("".equals(p0800) ? "0" : p0800);
		String staffId = (String) formHM.get("staffid"); // 新人id
		staffId = PubFunc.decryption(staffId);
		
		// 查询原来的人员中是否存在staffId
		Integer iP0905 = new Integer(flag);
		StringBuffer sql = new StringBuffer();
		if(flag == 2){//保存负责人时
			
			/**  添加负责人时，如果在关注人中存在，则会删除关注人 **/
			sql.append("SELECT * FROM P09 WHERE p0901=2 AND p0905=3 AND p0903=? and nbase=? and a0100=?");
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[] {iP0800,staffId.substring(0, 3),staffId.substring(3),}));
			if (frowset.next()) {
				if(formHM.get("staffids")==null){	//staffids为null则前台是传的sttafid,不使用批量添加的方法
					formHM.put("removeFollower", WorkPlanUtil.encryption(staffId)); // 提示浏览器要从关注人删掉同样的staffId
				}else{
					removeFollowers.add(WorkPlanUtil.encryption(staffId)); // 提示浏览器要从关注人删掉同样的staffId
				}
			}
			sql.setLength(0);
			sql.append( "SELECT * FROM P09 WHERE p0901=2 AND p0905 in (1,2) AND p0903=?");
		}
		if(flag == 3){
			sql.append( "SELECT * FROM P09 WHERE p0901=2 AND p0905 in (1,2,3) AND p0903=?");
		}
		frowset = dao.search(sql.toString(), Arrays.asList(new Object[] {iP0800}));
		while (frowset.next()) {
			String objId = frowset.getString("nbase") + frowset.getString("a0100");
			if (objId.equalsIgnoreCase(staffId)) { // staffId已经存在了
				return null;
			}
		}
		
		/** ######################### 新人 per_task_map表(参与人) ######################## */
		if (flag == 2) { // 参与人需要同步映射表
			// 不新增重复记录
			frowset = dao.search("SELECT * FROM per_task_map WHERE p0800=? AND nbase=? AND a0100=?", Arrays.asList(new Object[] {
				Integer.valueOf(p0800),
				staffId.substring(0, 3),
				staffId.substring(3)
			}));
			boolean duplicate = false; // 映射表里不能出现nbase+a0100相同的记录
			while (frowset.next()) {
				if (staffId.equals(frowset.getString("nbase") + frowset.getString("a0100"))) {
					duplicate = true;
					break;
				}
			}
			
			if (!duplicate) {
				RecordVo member_map = new RecordVo("per_task_map");
				// per_task_map表的id生成器
				String id_map = new IDGenerator(2, this.frameconn).getId("per_task_map.id");
				member_map.setInt("id", Integer.parseInt(id_map));
				member_map.setInt("p0800", iP0800.intValue());
				PlanTaskTreeTableBo ptbo = new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700));
				member_map.setInt("seq", ptbo.getSeq(staffId, bo.getTask(iP0800.intValue()).getInt("p0831"), 1));
				member_map.setInt("flag", 2);
				member_map.setString("nbase", staffId.substring(0, 3));
				member_map.setString("a0100", staffId.substring(3));
				member_map.setDouble("rank", 0.0);
				member_map.setInt("p0700", Integer.parseInt(p0700));
				member_map.setDate("create_time", new Date());
				member_map.setString("create_user", userView.getUserName());
				member_map.setString("create_fullname", userView.getUserFullName());
				//添加新字段,区别是个人任务分配过来的还是团队任务分配过来的  wusy
				member_map.setInt("belongflag", Integer.parseInt(p0723));
				//如果被分配的负责人是部门负责人  任务分解到部门  wusy
				String nbase = staffId.substring(0,3);
				String a0100 = staffId.substring(3);
				b0110 = new WorkPlanUtil(this.getFrameconn(), userView).getFristMainDept(nbase, a0100);
				if(!"".equals(b0110)){
					member_map.setString("org_id", b0110);
				}
				member_map.setInt("dispatchflag", 0);
				dao.addValueObject(member_map);
				
				//分派到部门
				if (!"".equals(b0110)){
					bo.addDeptTask(b0110, p0700, p0800,p0723);
				}
				
			}
		}

		/** ######################### 新人 p09表 ######################## */
		RecordVo p09 = new RecordVo("P09");
		// P09表的id生成器
		String id = new IDGenerator(2, this.frameconn).getId("P09.P0900");
		p09.setInt("p0900", Integer.parseInt(id));
		p09.setInt("p0901", 2);
		p09.setInt("p0903", iP0800.intValue());
		p09.setString("nbase", staffId.substring(0, 3));
		p09.setString("a0100", staffId.substring(3));
		p09.setInt("p0905", flag);
		if(!"".equals(b0110)){
			p09.setString("org_id", b0110);
		}
		RecordVo a01 = new RecordVo(staffId.substring(0, 3) + "A01");
		a01.setString("a0100", staffId.substring(3));
		a01 = dao.findByPrimaryKey(a01);
		p09.setString("p0907", a01.getString("b0110"));
		p09.setString("p0909", a01.getString("e0122"));
		p09.setString("p0911", a01.getString("e01a1"));
		p09.setString("p0913", a01.getString("a0101"));
		
		dao.addValueObject(p09);
		
		RecordVo task = bo.getTask(iP0800.intValue());
		// 需要改动任务变更状态
		PlanTaskBo ptBo = new PlanTaskBo(frameconn, userView);
		int superiorEdit = ptBo.isSuperiorEdit(params);
		//if(!ptBo.isTopLeader(objectid, p0723)){
		RecordVo plan = bo.getPlan(Integer.parseInt(p0700));
		// 上级查看下级计划且任务不是取消状态
		if (!(superiorEdit>=1 && superiorEdit<=4)){ 
			if ((WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD.contains("MEMBER") && flag == 2) || 
					(WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD.contains("FOLLOWER") && flag == 3)) {
					if (bo.ifCauseChangedStatusAltering(plan, task)) {
						task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Changed);
						dao.updateValueObject(task);
					}
			}
		}else{
			if("2".equals(plan.getString("p0719")) && !"03".equals(task.getString("p0811"))){
				task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				dao.updateValueObject(task);
				formHM.put("approveInfo", "approved");
			}
		}
		formHM.put("privilege", bo.privilege(params));
		
		
		// 发送邮件
		LazyDynaBean body = new LazyDynaBean();
		body.set("target", bo.getA0101(staffId));
		body.set("operator", userView.getUserFullName());
		body.set("taskName", task.getString("p0801"));
		body.set("position", flag == 2 ? "任务成员" : "关注人");
		String bodyText = "";
		if (task.getDate("p0815") != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			body.set("endDate", sdf.format(task.getDate("p0815")));
			bodyText += PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffSetting());
		} else {
			bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffSettingWithoutEndDate());
		}
		String subject=userView.getUserFullName()+"将您添加为任务 \""+task.getString("p0801")+"\" 的"
			+(String)body.get("position")+",请查看";
		
		LazyDynaBean email = new LazyDynaBean();
		email.set("objectId", staffId);//邮件接收人
		email.set("subject", subject);//邮件标题
		email.set("bodyText", bodyText);//邮件内容
		email.set("bodySubject", "任务提醒");
		
		String objectId = null;
		if (flag == 2) {//保存任务成员
			objectId = staffId;
		}else{
			objectId = plan.getString("nbase")+plan.getString("a0100");
		}
		
		Map p = new HashMap();
		p.putAll(params);
		if(flag == 3){
			p.put("role", "follower");	// 角色,为后面方法区分
		}
		p.put("logonUser", staffId); // 从邮件进入网页的登录人，需要将id设成新人的id
		p.put("objectid", objectId); // 需要将id设成新人的id
		email.set("href", bo.getHref(p));
		bo.send(email, task.getString("p0811"));
//		if (!(userView.getDbname() + userView.getA0100()).equals(staffId)) { // 当前操作人不发送邮件
//		}
		
		// 返回前台的bean
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("id", WorkPlanUtil.encryption(staffId));
		bean.set("fullName", a01.getString("a0101"));
		String photo = new WorkPlanBo(frameconn, userView).getPhotoPath(staffId.substring(0, 3), staffId.substring(3));
		bean.set("photo", photo);
		bean.set("abbr", new WorkPlanUtil(frameconn, userView).getTruncateA0101(a01.getString("a0101")));
		
		return bean;
	}
	
	/** ################################## 处理负责人 ################################## */
	private LazyDynaBean director() throws Exception {
		ContentDAO dao = new ContentDAO(frameconn);
		PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
		WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
		Map params = PlanTaskBo.setOutParams(formHM);
		String objectid = (String) params.get("objectid");
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		Integer iP0800 = new Integer("".equals(p0800) ? "0" : p0800);
		String directorId = (String) formHM.get("staffid"); // 新的负责人id
		directorId = PubFunc.decryption(directorId);
		String b0110 = "";//制定负责人负责的部门
		String nbase = directorId.substring(0,3);
		String a0100 = directorId.substring(3);
		b0110 = new WorkPlanUtil(this.getFrameconn(), userView).getFristMainDept(nbase, a0100);
		
		/** ############################# 删除老负责人 ############################# */
		// 先拿到老负责人,和其所有的成员标识
		StringBuffer oldDirectorSql = new StringBuffer();
		oldDirectorSql.append("SELECT * FROM p09 WHERE p0901=2 AND p0903=? AND nbase ").append(Sql_switcher.concat()).append(" a0100=");
		oldDirectorSql.append("(SELECT nbase ").append(Sql_switcher.concat()).append(" a0100 FROM p09 WHERE p0901=2 AND p0903=? AND p0905=1)");
		frowset = dao.search(oldDirectorSql.toString(), Arrays.asList(new Object[] {iP0800, iP0800}));
		
		String oldId = ""; // 原负责人的id: Usr00000019
		String allP0905 = ""; // 原负责人所有的成员标识: 1,3
		boolean isDirectorIn=false;//判断当前老负责人是否存在
		while (frowset.next()) {
			oldId = frowset.getString("nbase") + frowset.getString("a0100");
			allP0905 += frowset.getInt("p0905") + ",";
		}
		if (directorId.equalsIgnoreCase(oldId)) { // 说明负责人没有换,无需任何操作
			return null;
		}
		
		RecordVo task = bo.getTask(Integer.parseInt(p0800));
		IDGenerator idg = new IDGenerator(2, this.frameconn);
		
		/* ######################### 处理原负责人 ############################ */
		if (!"".equals(oldId)) {
			// 删除
			String del_map = "DELETE FROM per_task_map WHERE flag=1 AND p0800=?";
			dao.delete(del_map, Arrays.asList(new Object[] {iP0800}));
			String del_p09 = "DELETE FROM p09 WHERE p0905=1 AND p0903=? AND p0901=2";
			dao.delete(del_p09, Arrays.asList(new Object[] {iP0800}));
			
			int superiorEdit = bo.isSuperiorEdit(params);
			// 需要改动任务变更状态
			//如果没有上级岗位,  wusy
			 if (new WorkPlanUtil(frameconn, userView).isHaveDirectSuper(objectid,p0723)){
				if (WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD.contains("DIRECTOR")) {
					RecordVo plan = bo.getPlan(Integer.parseInt(p0700));
					if(!(superiorEdit>=1 && superiorEdit<=4)){// 不是上级操作
						if(bo.ifCauseChangedStatusAltering(plan, task)) {
							task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Changed);
						}
					}else{//上级操作，直接批准
						if("2".equals(plan.getString("p0719")) && !"03".equals(task.getString("p0811"))){
							task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
							
							formHM.put("approveInfo", "approved");
						}
					}
					dao.updateValueObject(task);
					formHM.put("privilege", bo.privilege(params));
				}
			 }
			
			if (allP0905.contains("3")) { // 原负责人同时是计划的关注人,且不是当前操作人
				// 发送邮件
				LazyDynaBean body = new LazyDynaBean();
				body.set("target", bo.getA0101(oldId));
				body.set("operator", userView.getUserFullName());
				body.set("taskName", task.getString("p0801"));
				body.set("position", "负责人");
				String bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffRemoving());
				String subject =userView.getUserFullName()+"将您从任务\""+task.getString("p0801")+"\"的负责人名单中移除";
				
				LazyDynaBean email = new LazyDynaBean();
				email.set("objectId", oldId);
				email.set("subject", subject);
				email.set("bodyText", bodyText);
				email.set("bodySubject", "任务提醒");
				
				Map p = new HashMap();
				p.putAll(params);
				p.put("logonUser", oldId); // 需要将id设成原负责人的id
				p.put("objectid", oldId); // 需要将id设成原负责人的id
				email.set("href", bo.getHref(p));
				bo.send(email, task.getString("p0811"));
			} else {
				RecordVo newFan = new RecordVo("P09");
				// P09表的id生成器
				String id = idg.getId("P09.P0900");
				newFan.setInt("p0900", Integer.parseInt(id));
				newFan.setInt("p0901", 2);
				newFan.setInt("p0903", Integer.parseInt(p0800));
				newFan.setString("nbase", oldId.substring(0, 3));
				newFan.setString("a0100", oldId.substring(3));
				newFan.setInt("p0905", 3);
				
				/*RecordVo a01 = new RecordVo(oldId.substring(0, 3) + "A01");
				a01.setString("a0100", oldId.substring(3));
				a01 = dao.findByPrimaryKey(a01);*/
				String sql="select * from "+oldId.substring(0, 3)+"A01 where a0100='"+oldId.substring(3)+"'";
				RowSet rset=dao.search(sql);
				if(rset.next()){
					isDirectorIn=true;
					newFan.setString("p0907", rset.getString("b0110"));
					newFan.setString("p0909", rset.getString("e0122"));
					newFan.setString("p0911", rset.getString("e01a1"));
					newFan.setString("p0913", rset.getString("a0101"));
					
					dao.addValueObject(newFan);
					
					// 发送邮件
//					if (!(userView.getDbname() + userView.getA0100()).equals(oldId)) { // 当前操作人不发送邮件
//					}
					LazyDynaBean body = new LazyDynaBean();
					body.set("target", bo.getA0101(oldId));
					body.set("operator", userView.getUserFullName());
					body.set("taskName", task.getString("p0801"));
					body.set("position", "负责人");
					String bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffRemoving() + "系统自动将您指定为此项任务的关注人。");
					String subject =userView.getUserFullName()+"将您从任务\""+task.getString("p0801")+"\"的负责人名单中移除";
					
					LazyDynaBean email = new LazyDynaBean();
					email.set("objectId", oldId);
					email.set("subject", subject);
					email.set("bodyText", bodyText);
					email.set("bodySubject", "任务提醒");
					
					Map p = new HashMap();
					p.putAll(params);
					p.put("logonUser", oldId); // 需要将id设成原负责人的id
					p.put("objectid", oldId); // 需要将id设成原负责人的id
					email.set("href", bo.getHref(p));
					bo.send(email, task.getString("p0811"));
				}
			}
			
			// 删除协作任务原负责人
			wpbo.deleteCooperationTask(Integer.parseInt(p0800));
		}
		
		/* ######################### 处理新负责人 ############################ */
		if ("".equals(directorId)) { // 没有选择新人
			return null;
		}
		/* #########################  新增负责人,需要保证该人员不能是该任务的成员,如果是需要清掉其任务成员的信息  */
		StringBuffer delSql = new StringBuffer();
		delSql.append("delete from p09 where p0901 = 2 and p0903 = ? and p0905 = 2 and nbase = ? and a0100 = ?");
		dao.update(delSql.toString(), Arrays.asList(new Object[]{iP0800, directorId.substring(0, 3), directorId.substring(3)}));
		delSql.delete(0, delSql.length());
		if("".equals(b0110)){
			delSql.append("delete from per_task_map where p0800 = ? and flag = 2 and nbase = ? and a0100 = ?");
			dao.update(delSql.toString(), Arrays.asList(new Object[]{iP0800, directorId.substring(0, 3), directorId.substring(3)}));
		}else{
			delSql.append("delete from per_task_map where p0800 = ? and flag = 2 and org_id = ?");
			dao.update(delSql.toString(), Arrays.asList(new Object[]{iP0800, b0110}));
		}
		// 同步映射表,不新增重复记录
		frowset = dao.search("SELECT * FROM per_task_map WHERE p0800=? AND nbase=? AND a0100=? AND flag=5", Arrays.asList(new Object[] {
			Integer.valueOf(p0800),
			directorId.substring(0, 3),
			directorId.substring(3)
		}));
		boolean duplicate = false; // 映射表里不能出现nbase+a0100相同的记录
		while (frowset.next()) {
			if (directorId.equals(frowset.getString("nbase") + frowset.getString("a0100"))) {
				duplicate = true;
				break;
			}
		}
		//  wusy
		
		if (!duplicate) {
			RecordVo newDirector_map = new RecordVo("per_task_map");
			String id_map = idg.getId("per_task_map.id");// per_task_map表的id生成器
			newDirector_map.setInt("id", Integer.parseInt(id_map));	
			newDirector_map.setInt("p0800", Integer.parseInt(p0800));
			newDirector_map.setInt("flag", 1);
			newDirector_map.setInt("seq", new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700)).getSeq(directorId, task.getInt("p0831"), 1));
			newDirector_map.setString("nbase", directorId.substring(0, 3));
			newDirector_map.setString("a0100", directorId.substring(3));
			newDirector_map.setDouble("rank", 0.0);
			newDirector_map.setInt("p0700", Integer.parseInt(p0700));
			newDirector_map.setDate("create_time", new Date());
			newDirector_map.setString("create_user", userView.getUserName());
			newDirector_map.setString("create_fullname", userView.getUserFullName());
			//添加新字段,区别是个人任务分配过来的还是团队任务分配过来的  wusy
			newDirector_map.setInt("belongflag", Integer.parseInt(p0723));
			//如果被分配的负责人是部门负责人且该任务的创建人不是被分配的人员  任务分解到部门  wusy
			if(!"".equals(b0110)){
				newDirector_map.setString("org_id", b0110);
				
			}
			newDirector_map.setInt("dispatchflag", 0);
			dao.addValueObject(newDirector_map);
			//分派到部门 抽取方法  wusy
			if (!"".equals(b0110)  && !bo.isCreater(Integer.parseInt(p0800), directorId)){
				bo.addDeptTask(b0110, p0700, p0800,p0723);
			}
		}
		// P09表
		RecordVo newDirector_p09 = new RecordVo("P09");
		// P09表的id生成器
		String id_p0900 = idg.getId("P09.P0900");
		newDirector_p09.setInt("p0900", Integer.parseInt(id_p0900));
		newDirector_p09.setInt("p0901", 2);
		newDirector_p09.setInt("p0903", Integer.parseInt(p0800));
		newDirector_p09.setString("nbase", directorId.substring(0, 3));
		newDirector_p09.setString("a0100", directorId.substring(3));
		newDirector_p09.setInt("p0905", 1);
		if(!"".equals(b0110)){
			newDirector_p09.setString("org_id", b0110);
		}
		RecordVo a01 = new RecordVo(directorId.substring(0, 3) + "A01");
		a01.setString("a0100", directorId.substring(3));
		a01 = dao.findByPrimaryKey(a01);
		newDirector_p09.setString("p0907", a01.getString("b0110"));
		newDirector_p09.setString("p0909", a01.getString("e0122"));
		newDirector_p09.setString("p0911", a01.getString("e01a1"));
		newDirector_p09.setString("p0913", a01.getString("a0101"));
		
		dao.addValueObject(newDirector_p09);
		
		// 同步协作任务新负责人
		int superiorEdit = bo.isSuperiorEdit(params);
		if(superiorEdit>=1 && superiorEdit<=4){//上级操作时，给协办人上级发通知
			WorkPlanBo workPlanBo = new WorkPlanBo(this.frameconn, this.userView);
			workPlanBo.SuperiorOperation(Integer.parseInt(p0800));
		}
		
		//更换负责人后查找到新的p700 lis 20160321 start
		RecordVo plan = bo.getPlan(Integer.parseInt(p0700));
		int period_type = plan.getInt("p0725");
		int period_year = plan.getInt("p0727");
		int period_month = 0;
		int period_week = 0;
		if(period_type==2||period_type==3||period_type==4||period_type==5)
			period_month = plan.getInt("p0729");
		if(period_type==5)
			period_week = plan.getInt("p0731");
		int directorP0700 = wpbo.getPeoplePlanId(nbase, a0100, String.valueOf(period_type), String.valueOf(period_year), String.valueOf(period_month), String.valueOf(period_week));
		formHM.put("directorP0700", WorkPlanUtil.encryption(directorP0700+""));
		formHM.put("p0723",  WorkPlanUtil.encryption(plan.getString("p0723")));
		formHM.put("p0800",  WorkPlanUtil.encryption(p0800));
		//lis 20160321 end
		
		//记录日志,更换负责人  wusy
		RecordVo p08Vo = new RecordVo("p08");
    	p08Vo.setInt("p0800", Integer.parseInt(p0800));
    	try {
			p08Vo = dao.findByPrimaryKey(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
			String logcontent = "任务委托给@" + a01.getString("a0101");
			new WorkPlanOperationLogBo(frameconn, userView).addLog(Integer.parseInt(p0800), logcontent);
		}
		// 发送邮件
		if (!(userView.getDbname() + userView.getA0100()).equals(directorId)) { // 当前操作人不发送邮件
			
			WorkPlanUtil util = new WorkPlanUtil(this.frameconn, this.userView);
			if(!util.isOpenCooperationTask() || !util.isCooperationTask(Integer.parseInt(p0800), true)){//【没有启用协作任务】 或 【不是协作任务】 时才给负责人发送通知
				
				LazyDynaBean body = new LazyDynaBean();
				body.set("target", bo.getA0101(directorId));
				body.set("operator", userView.getUserFullName());
				body.set("taskName", task.getString("p0801"));
				body.set("position", "负责人");
				String bodyText = null;
				if (task.getDate("p0815") != null) {
					body.set("endDate", new SimpleDateFormat("yyyy年M月d日").format(task.getDate("p0815")));
					bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffSetting());
				} else {
					bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfStaffSettingWithoutEndDate());
				}
				String subject=userView.getUserFullName()+"将您添加为任务 \""+task.getString("p0801")+"\" 的负责人,请查看";
				
				LazyDynaBean email = new LazyDynaBean();
				email.set("objectId", directorId);
				email.set("subject", subject);
				email.set("bodyText", bodyText);
				email.set("bodySubject", "任务提醒");
				
				Map p = new HashMap();
				p.putAll(params);
				p.put("logonUser", directorId); // 需要将id设成新负责人的id
				p.put("objectid", directorId); // 需要将id设成新负责人的id
				email.set("href", bo.getHref(p));
				bo.send(email, task.getString("p0811"));
			}
		}
		if(isDirectorIn){
			// 原负责人被移除后变为关注人
			LazyDynaBean newFollower = bo.getStaffNode(oldId);
			if (newFollower != null) {
				formHM.put("newFollower", newFollower);
			}
			
			formHM.put("toRemove", WorkPlanUtil.encryption(directorId)); // 提示浏览器要从参与人删掉同样的directorId
			
		}
		
		
		return bo.getStaffNode(directorId);
		
		
	}
		
}

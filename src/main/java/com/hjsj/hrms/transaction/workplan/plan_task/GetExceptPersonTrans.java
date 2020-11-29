package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:获取需排除人员</p>
 * <p>Create time:2015-9-10</p>
 * @author chent
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GetExceptPersonTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String condition = (String)formHM.get("condition");//按钮区分
		String type = (String)formHM.get("type"); //计划类型
		String id = (String)formHM.get("id"); //id
		id = WorkPlanUtil.decryption(id);
		String p0905 = (String)formHM.get("p0905");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			ArrayList<String> except = new ArrayList<String>();
			String sql = "";
			ArrayList<String> list = new ArrayList<String>();
			if("1".equals(condition)){//1、计划界面关注人@按钮
				sql  = "select nbase,a0100 from P09 where p0901=? and p0903=? ";
				list.add(type);
				list.add(id);
			}else if("2".equals(condition)){//2、任务详情界面选负责人
				sql = "select nbase,a0100 from P09 where p0901=? and p0903=? and p0905=?";
				list.add(type);
				list.add(id);
				list.add(p0905);
			}else if("3".equals(condition)){//3、任务详情界面选任务成员    排除创建人、负责人、参与人
				sql = "select org_id,nbase,a0100 from per_task_map where P0800=?";
				list.add(id);
			}else if("4".equals(condition)){//4：任务详情界面添加关注人
				sql = "select nbase,a0100 from P09 where p0901=? and p0903=? ";
				list.add(type);
				list.add(id);
			}
			rs = dao.search(sql, list);
			while(rs.next()){
				String nbase = rs.getString("nbase");
				String a0100 = rs.getString("a0100");
				except.add(PubFunc.encrypt(nbase+a0100));
				try{
					String org_id = rs.getString("org_id");
					if(!StringUtils.isEmpty(org_id)){ // 如果是部门计划，排除部门负责人
						WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.frameconn, this.userView);
						String leader = workPlanUtil.getFirstDeptLeaders(org_id);// 排除部门负责人
						except.add(PubFunc.encrypt(leader));
					}
				}catch(Exception e){}
			}
			
			if("1".equals(condition)){//1、计划界面关注人@按钮,排除自己
				String nbase = this.userView.getDbname();
				String a0100 = this.userView.getA0100();
				except.add(PubFunc.encrypt(nbase+a0100));
			}
			formHM.put("except", except);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}

}

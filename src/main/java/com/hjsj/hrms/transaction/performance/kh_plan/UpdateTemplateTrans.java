package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:UpdateTemplateTrans.java</p>
 * <p>Description:更新考核模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-10-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class UpdateTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("planId");
		String templateID = (String) hm.get("templateId");
		hm.remove("planId");

		// 更换了模板需要清空必填指标
		ExamPlanBo bo = new ExamPlanBo(planId, this.frameconn);
		RecordVo plan_vo = bo.getPlanVo();
		if (plan_vo == null) {
			throw new GeneralException("加载计划失败");
		}

		// 如果模板为个性化模板就将考核方法更新为目标管理
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.frameconn);
		String strSql = "select * from per_template_item where kind='2' and template_id='" + templateID + "'";
		try {
			RowSet rs = dao.search(strSql);
			if (rs.next()) {
				flag = true;// 个性化
			}
			
			String tplId = plan_vo.getString("template_id"); // 数据库中的模板id,修改前
			ArrayList list = new ArrayList();
			int method = 2;
			if (flag) {
				plan_vo.setInt("method", 2);
				method = 2;
			} else {
				plan_vo.setInt("method", 1);
				method = 1;
			}
			plan_vo.setString("template_id", templateID);
//			dao.updateValueObject(plan_vo);   orcale库这里会报错   索性就直接写原始sql修改了   zhaoxg add 2014-10-20
			String sql = "update per_plan set method=?,template_id=? where plan_id=?";
			list.add(method+"");
			list.add(templateID);
			list.add(planId);
			dao.update(sql, list);
			if (!tplId.equals(templateID)) {
				bo.clearRequiredField();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

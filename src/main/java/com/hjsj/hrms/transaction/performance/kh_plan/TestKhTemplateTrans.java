package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * <p>Title:TestKhTemplateTrans.java</p>
 * <p>Description:测试考核模板是否为个性化模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-11-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class TestKhTemplateTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String templateID = (String)this.getFormHM().get("template_id");
		String plan_id = (String)this.getFormHM().get("plan_id");
		ContentDAO dao = new ContentDAO(this.frameconn);
		//判断是否存在最底层的个性项目
		String strSql = "select * from per_template_item where kind='2'  and child_id is null and template_id='"+templateID+"'";
		String flag = "0";
		String isTargetTemplate = "false";
		try
		{
			RowSet rs = dao.search(strSql);
			if(rs.next()) {
				flag = "1";//个性化
				isTargetTemplate = "true";
			}

			ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
			RecordVo plan_vo = bo.getPlanVo();

			// 区分页面加载与切换模板的情况
		    if (plan_vo != null && plan_vo.getString("template_id").equals(templateID)) {
		    	// 数据库中原始计划类型为目标的，将flag置为“1”
		    	flag = plan_vo.getInt("method") == 2 ? "1" : "0";
	    	}
		    
			// 更换了模板需要清空必填指标
		    if (plan_vo != null && !plan_vo.getString("template_id").equals(templateID)) {
		    	bo.clearRequiredField();
		    }

		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("isTargetTemplate", isTargetTemplate);
    }

}

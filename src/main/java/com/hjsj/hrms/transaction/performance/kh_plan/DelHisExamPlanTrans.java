package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:DelHisExamPlanTrans.java</p>
 * <p>Description:删除历史考核计划，将删除所有与该考核计划相关的内容</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-14 09:21:56</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class DelHisExamPlanTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String paramStr = (String) this.getFormHM().get("paramStr");
		paramStr = paramStr.replaceAll("／", "/");
		String[] planids = paramStr.split("/");
		String strSql = "";
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    for (int i = 0; i < planids.length; i++)
		    {
				String[] temp = planids[i].split(":");
				String planId = temp[0];
				String status = temp[1];
				// 删掉自己
				strSql = "delete from per_plan where plan_id=" + planId;
				dao.delete(strSql, list);
				dao.delete("delete from t_hr_pendingtask where ext_flag like '%_"+planId+"%' and pending_type='33'", new ArrayList());//删除对应待办
				if (!"0".equals(status))
				{
				    // 删掉计划的主体类别
				    strSql = "delete from per_plan_body where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删掉计划的考核对象
				    strSql = "delete from per_object where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删掉计划的考核指标票素的动态权重
				    strSql = "delete from per_dyna_rank where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删掉计划的考核主体的动态权重
				    strSql = "delete from per_dyna_bodyrank where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删掉计划的考核主体
				    strSql = "delete from per_mainbody where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删掉主体的考核指标的权限表
				    // strSql = "drop table per_pointpriv_" + planId;
				    DbWizard dbWizard = new DbWizard(this.frameconn);
				    if (dbWizard.isExistTable("per_pointpriv_" + planId, false))
				    	dbWizard.dropTable("per_pointpriv_" + planId);
		
				    // 目标管理的计划还要删掉p04表中的相关计划（p04表中的计划都是目标管理类型的计划所以可以这样写来删除）
				    strSql = "delete from p04 where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除历史记录
				    strSql = "delete from per_history_result where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除结果修正
				    strSql = "delete from per_result_correct where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除面谈结果
				    strSql = "delete from per_interview where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除综合评价
				    strSql = "delete from per_appraise where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除主体类别得分
				    strSql = "delete from per_objectbody_score where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除总体评价票数
				    strSql = "delete from per_object_vote where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    // 删除了解程度票数
				    strSql = "delete from per_object_know where plan_id=" + planId;
				    dao.delete(strSql, list);
				    // 删除权限
				    if (dbWizard.isExistTable("per_right_" + planId, false))
				    	dbWizard.dropTable("per_right_" + planId);
				    // 删除得分
				    if (dbWizard.isExistTable("per_data_" + planId, false))
				    	dbWizard.dropTable("per_data_" + planId);
				    // 删除考核结果
				    if (dbWizard.isExistTable("per_result_" + planId, false))
				    	dbWizard.dropTable("per_result_" + planId);
				    // 删除主体分数
				    if (dbWizard.isExistTable("per_bodyscore_" + planId, false))
				    	dbWizard.dropTable("per_bodyscore_" + planId);
				    // 删除主体选票统计
				    if (dbWizard.isExistTable("per_bodyvote_" + planId, false))
				    	dbWizard.dropTable("per_bodyvote_" + planId);
				    // 删除指标选票统计
				    if (dbWizard.isExistTable("per_pointvote_" + planId, false))
				    	dbWizard.dropTable("per_pointvote_" + planId);
				    // 删除得分明细
				    strSql = "delete from per_scoreDetail where plan_id=" + planId;
				    dao.delete(strSql, list);
				    // 删除主体得分统计
				    if (dbWizard.isExistTable("per_bodyscorestatistics_" + planId, false))
				    	dbWizard.dropTable("per_bodyscorestatistics_" + planId);
				    // 删除目标管理等表内容
				    strSql = "delete from per_target_evaluation where plan_id=" + planId;
				    dao.delete(strSql, list);
				    //删除项目权限表
				    if (dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
				    	dbWizard.dropTable("PER_ITEMPRIV_" + planId);
				    
				    strSql = "delete from P04 where plan_id=" + planId;
				    dao.delete(strSql, list);
		
				    strSql = "delete from per_article where plan_id=" + planId;
				    dao.delete(strSql, list);
				    
				    // 清除计划相关的代办信息  haosl 2017-12-12
				    PendingTask pe = new PendingTask();
				    String str = "select * from t_hr_pendingtask  where Pending_type='33' and ext_flag like 'PER%_"+planId+"%' and pending_status<>1";
	    			RowSet rs = dao.search(str);
				    while(rs.next()){
				    	pe.updatePending("P", "PER"+rs.getString("pending_id"), 1, "计划删除",this.userView);
	    			}
					str = "update t_hr_pendingtask set pending_status=1 where Pending_type='33' and ext_flag like 'PER%_"+planId+"%' and pending_status<>1";
					dao.update(str);
				}
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}

    }
}

package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.ResultFiledBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:SaveResultFiledTrans.java</p>
 * <p>Description:保存结果归档</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-28 10:56:32</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveResultFiledTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		ArrayList sourceCodes = (ArrayList) this.getFormHM().get("sourceCodes");
		ArrayList sourceNames = (ArrayList) this.getFormHM().get("sourceNames");
		ArrayList destCodes = (ArrayList) this.getFormHM().get("destCodes");
		ArrayList destTypes = (ArrayList) this.getFormHM().get("destTypes");
		String setName = (String) this.getFormHM().get("setName");
		String planId = (String) this.getFormHM().get("planID");
		String filedType = (String) this.getFormHM().get("filedType");
		String oper = (String) this.getFormHM().get("oper");// 1 试归档 2 归档 3 结束	
		
		ResultFiledBo bo = new ResultFiledBo(planId, this.getFrameconn(), filedType);
		boolean flag = false;
	        String isHaveTeamLeader = "0";
		try
		{
		    // 生成新的归档方案
		    bo.genetateXML(sourceCodes, sourceNames, destCodes, destTypes, setName);
		    // 将当前的记录保存到子集里
	
		    String userName = this.getUserView().getUserName();
		    flag = bo.save(sourceCodes, sourceNames, destCodes, destTypes, setName, userName,oper);
	
		    if ("2".equals(filedType))// 团队 单位 部门的归档
		    {
				ContentDAO dao = new ContentDAO(this.frameconn);
		
				String sql = "select body_id  from per_plan_body where plan_id=" + planId + " and body_id=-1";
				this.frowset = dao.search(sql);
				if (this.frowset.next())// 有团队负责人要继续进行团队负责人的归档
				    isHaveTeamLeader = "1";
	
		    }
		    
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer strSql = new StringBuffer();
			strSql.append("select name from per_plan where plan_id  ="+planId);
			RowSet rs = dao.search(strSql.toString());
			StringBuffer context = new StringBuffer();
			if("1".equals(oper)){
				context.append("试归档：");
			}else if("2".equals(oper)){
				context.append("归档：");
			}else if("3".equals(oper)){
				context.append("结束：");
			}
			while(rs.next()){
				context.append(rs.getString("name")+",");
			}
			if(context.length()>0){
				this.getFormHM().put("@eventlog", context.toString());
			}
			
		    /** 归档后，待办置为已办 chent 20150925 start */
		    if("2".equals(oper) || "3".equals(oper)){//归档、结束
		    	String pf_ext_flag = "PERPF_"+planId;//评分 待办
		    	String sp_ext_flag = "PERSP_"+planId;//审批 待办
		    	String zd_ext_flag = "PERZD_"+planId;//制定 待办
		    	String sql = "update t_hr_pendingtask set pending_status=1 where (ext_flag like '"+pf_ext_flag+"%' or ext_flag='"+sp_ext_flag+"' or ext_flag like '"+zd_ext_flag+"%') and pending_status=0";
		    	/*ArrayList<String> list = new ArrayList<String>();
		    	list.add(pf_ext_flag);
		    	list.add(sp_ext_flag);*/
		    	
		    	// 处理外部待办
		    	String selSql = "select pending_id from  t_hr_pendingtask where (ext_flag like '"+pf_ext_flag+"%' or ext_flag='"+sp_ext_flag+"' or ext_flag like '"+zd_ext_flag+"%') and pending_status=0";
		    	//this.frowset = dao.search(selSql, list);
		    	this.frowset = dao.search(selSql);
		    	while(this.frowset.next()){
		    		String pending_id = this.frowset.getString("pending_id");
		    		PendingTask pe = new PendingTask();
		    		pe.updatePending("P", "PER"+pending_id, 1, "结果归档/结束", this.userView);
		    	}

		    	dao.update(sql);//内部待办
				
		    }
		    /** 归档后，待办置为已办 chent 20150925 end */
		} catch (Exception e)
		{
		    e.printStackTrace();
		    flag = false;
		    throw GeneralExceptionHandler.Handle(e);
		}
		if (flag)
		    this.getFormHM().put("flag", "success");
		else
		    this.getFormHM().put("flag", "failure");
		this.getFormHM().put("filedType", filedType);
		this.getFormHM().put("isHaveTeamLeader", isHaveTeamLeader);
		this.getFormHM().put("planId", planId);
		this.getFormHM().put("oper", oper);
    }

}

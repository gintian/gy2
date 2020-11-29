package com.hjsj.hrms.module.questionnaire.plan;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class ChangePlanStatus extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String errorMsg = "";
          try{
        	  	  String action = (String)this.getFormHM().get("action");
              String planid = (String)this.getFormHM().get("planid");
              String qnid = (String)this.getFormHM().get("qnid");
              DbWizard dbwizard= new DbWizard(this.frameconn);
	          if("start".equals(action)){
	        	  errorMsg="发布失败！";
	        	  TemplateBo bo = new TemplateBo();
	        	  bo.publishTemplate(planid, qnid);
	        	  bo.updatePendingTaskPlanName(this.frameconn,planid);
	          }else if("pause".equals(action)){//清空数据并暂停  bug 44210 wangb 2019-01-25
	        	  errorMsg="暂停失败！";
		        	   String sql = "update qn_plan set status=?,pubtime=null where planid=?";
	       	       ArrayList value = new ArrayList();
	       	       value.add(2);
	       	       value.add(planid);
	       	       ContentDAO dao = new ContentDAO(frameconn);
	       	       int i = dao.update(sql, value);
	       	       if(i > 0){
	       	    	    processPendTask(planid, dao);
	       	       }
	       	       value.clear();
	       	       value.add(planid);
	       	       //清空计划相关答题数据
	       	       if(dbwizard.isExistTable("qn_"+qnid+"_data", false)){
	       	    	   dao.update("delete from qn_"+qnid+"_data where planid=?",value);
	       	       }
	       	       //清空计划相关答题数据
	       	       if(dbwizard.isExistTable("qn_matrix_"+qnid+"_data", false)){
	       	    	   dao.update("delete from qn_matrix_"+qnid+"_data where planid=?",value);
	       	       }
		       	   dao.update("update qn_plan set recoveryCount=0 where planid=?",value);
	          }else if("stop".equals(action)){
	        	   errorMsg="结束失败！";
	        	   String sql = "update qn_plan set status=?,pubtime=null where planid=?";
	       	       ArrayList value = new ArrayList();
	       	       value.add(3);
	       	       value.add(planid);
	       	       ContentDAO dao = new ContentDAO(frameconn);
	       	       int i = dao.update(sql, value);
	       	       if(i>0){
	       	    	    processPendTask(planid, dao);
	       	       }  
	          }
          }catch(Exception e){
        	      e.printStackTrace();
        	      this.formHM.put("error", errorMsg);
          }
	}

	   /*
        * 结束调查问卷修改对应t_hr_pendingtask中的pending_status   
        * xiegh
        * 2017/3/22
        * pending_status =0:待办， 1：已办  3：已阅 4：无效
        */
	private void processPendTask(String planid, ContentDAO dao)
			throws SQLException {
		ArrayList taskValue = new ArrayList();
		taskValue.add(4);
		taskValue.add(planid);
		StringBuffer strsql = new StringBuffer();
		strsql.append(" update t_hr_pendingtask set pending_status=? ");
		strsql.append(" where pending_title =( ");
		strsql.append(" select planName from qn_plan where status  = 3 and planid = ? ");
		strsql.append(" ) ");
		dao.update(strsql.toString(), taskValue);
	}

}

package com.hjsj.hrms.module.questionnaire.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 我的问卷删除清空操作
 * 
 * @createtime Sep 01, 2015 9:07:55 AM
 * @author liubq   
 * @update  guodd 2015-09-17
 * 
 */
public class DeletePlanTrans extends IBusiness{


	@Override
    public void execute() throws GeneralException {
		
		try {
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			String action = (String)this.getFormHM().get("action");//删除清空标记
			String planids = (String)this.getFormHM().get("planids");
			
			DbWizard dbwizard= new DbWizard(this.frameconn);
			StringBuffer selectedIdSql =  new StringBuffer();
			String[] planArray = planids.split(",");
			for(int i=0;i<planArray.length;i++){
				selectedIdSql.append(planArray[i]+",");
			}
			selectedIdSql.append("-1");
			
			//区分是查看问卷模板还是创建问卷 changxy 20160808
			//SeeTemplate rpc 传递的参数，map("SeeTemplate","SeeTemplate")
			String SeeTemplate=this.getFormHM().get("SeeTemplate")==null?"":(String)this.getFormHM().get("SeeTemplate");
			StringBuffer sql = new StringBuffer();
			if("SeeTemplate".equals(SeeTemplate)){//删除问卷模板操作
				ArrayList sqlArray = new ArrayList();
				for (int i = 0; i < planArray.length; i++) {
					String qnid=planArray[i];
					sqlArray.add("delete qn_template where qnid="+qnid);
					sqlArray.add("delete qn_template_library where qnid="+qnid);
					sqlArray.add("delete qn_question_item where qnid="+qnid);
					sqlArray.add("delete qn_question_item_opts where qnid="+qnid);
					sqlArray.add("delete  qn_question_item_matrix_opts where qnid="+qnid);
				}
				dao.batchUpdate(sqlArray);
				
			}else{
				//查询 需要操作计划 的相关试卷id
				sql.append("select qnid,planid from qn_plan where planid in (");
				sql.append(selectedIdSql);
				sql.append(")");
				this.frowset = dao.search(sql.toString());
				//如果是删除
				if("delete".equals(action)){
					//先清空试卷相关书数据
					while(this.frowset.next()){
						int qnid = this.frowset.getInt("qnid");
						int planid = this.frowset.getInt("planid");
						sql.setLength(0);
						//查询计划关联的试卷是否有别的计划也在使用
						sql.append("select sum(1) num from qn_plan where qnid=");
						sql.append(qnid);
						this.frecset = dao.search(sql.toString());
						this.frecset.next();
						//如果小于2说明没有别的计划引用，直接删除试卷和试卷原始数据表(答题结果表)
						if(frecset.getInt("num")<2){
							ArrayList sqlArray = new ArrayList();
							sqlArray.add("delete qn_template where qnid="+qnid);
							sqlArray.add("delete qn_template_library where qnid="+qnid);
							sqlArray.add("delete qn_question_item where qnid="+qnid);
							sqlArray.add("delete qn_question_item_opts where qnid="+qnid);
							sqlArray.add("delete  qn_question_item_matrix_opts where qnid="+qnid);
							dao.batchUpdate(sqlArray);
							//删除原始数据表
							if(dbwizard.isExistTable("qn_"+qnid+"_data", false)){
								dao.update("drop table qn_"+qnid+"_data");
							}
							if(dbwizard.isExistTable("qn_matrix_"+qnid+"_data", false)){
								dao.update("drop table qn_matrix_"+qnid+"_data");
							}
						}else{//否则说明有其他计划关联，不能删除试卷，只清空 试卷原始数据表（答题结果表）里此计划的答题数据
							if(dbwizard.isExistTable("qn_"+qnid+"_data", false)){
								dao.update("delete from qn_"+qnid+"_data where planid="+planid);
							}
							if(dbwizard.isExistTable("qn_matrix_"+qnid+"_data", false)){
								dao.update("delete from qn_matrix_"+qnid+"_data where planid="+planid);
							}
						}
					}
					//删除选中的计划
					sql.setLength(0);
					sql.append(" delete qn_plan where planid in (");
					sql.append(selectedIdSql);
					sql.append(")");
					dao.update(sql.toString());
				}else{//清空操作
					while(this.frowset.next()){
						int qnid = this.frowset.getInt("qnid");
						int planid = this.frowset.getInt("planid");
						//清空计划相关答题数据
						if(dbwizard.isExistTable("qn_"+qnid+"_data", false)){
							dao.update("delete from qn_"+qnid+"_data where planid="+planid);
						}
						//清空计划相关答题数据
						if(dbwizard.isExistTable("qn_matrix_"+qnid+"_data", false)){
							dao.update("delete from qn_matrix_"+qnid+"_data where planid="+planid);
						}
					}
					sql.setLength(0);
					sql.append(" update qn_plan set recoveryCount=0 where planid in (");
					sql.append(selectedIdSql);
					sql.append(")");
					dao.update(sql.toString());
				}
			}
			
			this.formHM.clear();
			this.formHM.put("action", action);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

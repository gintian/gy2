package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchCooperationTaskTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		String condSql = " ";
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get("workplan_appr_p10");
		String sql = cache.getTableSql();
		StringBuilder newsql = new StringBuilder();
		ArrayList<String> valuesList = new ArrayList<String>();
		String subModuleId = (String) this.getFormHM().get("subModuleId");// 模块id
		CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
		if("workplan_apprv_p10".equals(subModuleId)){
			// 查询类型，1为输入查询
			String type = (String) this.getFormHM().get("type");
			if("1".equals(type)) {
				// 输入的内容
				valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
				// 快速查询
				for(int i = 0; i < valuesList.size(); i++){
					String queryVal = valuesList.get(i);
					queryVal = SafeCode.decode(queryVal);
					if(i == 0){
						newsql.append(" and (");
					}else{
						newsql.append(" or ");
					}
					/**
					 * 姓名 姓名简称
					 * 发起人姓名 P1005 
					 * 协办人姓名 P1015
					 * */
					newsql.append("P1005 LIKE '%"+queryVal+"%' OR P1015 LIKE '%"+queryVal+"%' ");
					/**
					 * email
					 * 对应字段 C0102 
					 * */
					newsql.append("OR EXISTS (SELECT * FROM "+this.userView.getDbname()+"A01 WHERE ("+this.userView.getDbname()+"A01.GUIDKEY = myGridData.GUIDKE_CREATER AND "+this.userView.getDbname()+"A01.C0102 LIKE '%"+queryVal+"%')    ");
					newsql.append("OR ("+this.userView.getDbname()+"A01.GUIDKEY = myGridData.GUIDKE_OWNER AND "+this.userView.getDbname()+"A01.C0102 LIKE '%"+queryVal+"%') ) ");
					/**
					 * 单位名称 部门名称
					 * */
					newsql.append("OR P1003 LIKE '%"+queryVal+"%' OR P1013 LIKE '%"+queryVal+"%'  ");
					
					/**
					 * 任务名称 P1009
					 * */
					newsql.append("OR P1009 LIKE '%"+queryVal+"%' OR P1009 LIKE '%"+queryVal+"%'  ");
				}
				if(valuesList.size() > 0){
					newsql.append(" ) ");
					condSql += newsql.toString();
				}
			}else{
				int status = (Integer)this.getFormHM().get("status");
				int taskType = (Integer)this.getFormHM().get("tasktype");
				int all = (Integer)this.getFormHM().get("all");
				if(all==1)
					newsql.append("");
				else{
					if(status!=0){
						if(status==1){
							newsql.append(" and (P1019 = '待批') ");
						}else if(status==2){
							newsql.append(" and (P1019 = '已批') ");
						}else{
							newsql.append(" and (P1019 = '已退回') ");
						}
					}
					if(taskType!=0){
						String guidKey = bo.getGuidKey(this.userView.getA0100(),this.userView.getDbname());
						if(taskType==4){
							newsql.append(" and (GUIDKE_CREATER = '"+guidKey+"') ");
						}else{
							newsql.append(" and (GUIDKE_OWNER = '"+guidKey+"') ");
						}
					}
				}
				condSql += newsql.toString();
			} 
		}
		cache.setQuerySql(condSql.toString());
	}
}

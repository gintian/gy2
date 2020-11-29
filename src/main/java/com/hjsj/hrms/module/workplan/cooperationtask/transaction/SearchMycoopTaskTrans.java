package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchMycoopTaskTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		String condSql = " ";
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get("workplan_my_p10");
		HashMap map = new HashMap();
		String sql = cache.getTableSql();
		StringBuilder newsql = new StringBuilder();
		StringBuilder newsql2 = new StringBuilder();
		ArrayList<String> valuesList = new ArrayList<String>();
		String subModuleId = (String) this.getFormHM().get("subModuleId");// 模块id
		CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
		if("workplan_my_p10".equals(subModuleId)){
			// 查询类型，1为输入查询
			String type = (String) this.getFormHM().get("type");
			// 输入的内容
			valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
			if(cache.getCustomParamHM()==null){
				map.put("queryboxsql", "");
				map.put("querycommsql", "");
				cache.setCustomParamHM(map);
			}
			// 快速查询
			if(valuesList!=null){
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
					map.put("queryboxsql", newsql.toString());
				}
			}else{
				String queryboxsql = (String) cache.getCustomParamHM().get("queryboxsql");
				if(newsql.length()<=0&&queryboxsql!=null&&queryboxsql.length()>0){
					condSql +=queryboxsql;
					map.put("queryboxsql", queryboxsql);
				}else{
					map.put("queryboxsql", "");
				}
			}

			if(type==null||"".equals(type)){
				int status = (Integer)this.getFormHM().get("status");
				int taskType = (Integer)this.getFormHM().get("tasktype");
				int all = (Integer)this.getFormHM().get("all");
				if(all==1)
					newsql2.append("");
				else{
					if(status!=0){
						if(status==1){
							newsql2.append(" and (P1019 = '待批') ");
						}else if(status==2){
							newsql2.append(" and (P1019 = '已批') ");
						}else{
							newsql2.append(" and (P1019 = '已退回') ");
						}
					}
					if(taskType!=0){
						String guidKey = bo.getGuidKey(this.userView.getA0100(),this.userView.getDbname());
						if(taskType==4){
							newsql2.append(" and (GUIDKE_CREATER = '"+guidKey+"') ");
						}else{
							newsql2.append(" and (GUIDKE_OWNER = '"+guidKey+"') ");
						}
					}
				}
				condSql += newsql2.toString();
				map.put("querycommsql", newsql2.toString());
			}else{
				String querycommsql = (String) cache.getCustomParamHM().get("querycommsql");
				if(newsql2.length()<=0&&querycommsql!=null&&querycommsql.length()>0){
					condSql +=querycommsql;
					map.put("querycommsql", querycommsql);
				}else{
					map.put("querycommsql", "");
				}
			}
		}
		cache.setCustomParamHM(map);
		cache.setQuerySql(condSql.toString());
	}
}

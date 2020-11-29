/**
 * 
 */
package com.hjsj.hrms.module.template.templatelist.transaction;

import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 人事异动列表过滤后，符合条件的记录默认为选中状态
 * @author linbz
 *
 */
public class TemplateFilterFlagTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		String subModuleId = (String)this.formHM.get("subModuleId");
        String taskId = "";
		String setname=""; 
		String filterSql = "";
		String search_sql = "";
		StringBuffer tableSql = new StringBuffer("");
		if(StringUtils.isNotEmpty(subModuleId)){
			TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
			filterSql = cache.getFilterSql();//表格过滤的过滤条件
			tableSql.append(cache.getTableSql());
			HashMap map = cache.getCustomParamHM();
			setname = (String) map.get("tableName");
			taskId = (String) map.get("taskId");
			search_sql = (String) map.get("search_sql");//查询控件的查询条件
			//29802 获取条件语句时出现T. 需去掉
			if(search_sql.indexOf("T.") != -1){
				search_sql = search_sql.replace("T.", "");
			}
			if(" and 1=1 ".equalsIgnoreCase(search_sql)) {
				map.put("search_sql", "");
			}
		}
		this.userView.getHm().put("templateList_filterSql"+subModuleId, filterSql);
		if(StringUtils.isEmpty(subModuleId) || StringUtils.isEmpty(setname) 
				|| (StringUtils.isEmpty(filterSql) && StringUtils.isEmpty(search_sql)))
			return;
		
		TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn,this.userView);
		ArrayList list = new ArrayList();
		RowSet rst = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("0".equals(taskId))
			{
				//查看能不能查出数据
				if(StringUtils.isNotEmpty(filterSql))
					tableSql.append(filterSql);
				rst = dao.search(tableSql.toString());
				if(rst.next()){
					StringBuffer updateSql = new StringBuffer("");
					updateSql.append("update " + setname + " set submitflag=? where 1=1 ");
					
					//增加查询控件的查询条件
					if(StringUtils.isNotEmpty(search_sql)){
						updateSql.append(search_sql);
					}
					//过滤条件
					if(StringUtils.isNotEmpty(filterSql)){
						updateSql.append(filterSql);
					}
					//先全部设置为不选中
					dao.update("update " + setname + " set submitflag=0 where 1=1 ");
					list.add("1");
					dao.update(updateSql.toString(),list);
				}
			}
			else
			{
				if(StringUtils.isNotBlank(taskId)){
					if(StringUtils.isNotEmpty(filterSql))
						tableSql.append(filterSql);
					rst = dao.search(tableSql.toString()); 
					if(rst.next()){
						ArrayList listnot = new ArrayList();
						StringBuffer updateSql = new StringBuffer("");
						StringBuffer updateSql1 = new StringBuffer("");
						StringBuffer updateSql2 = new StringBuffer("");
						
						updateSql1.append("update t_wf_task_objlink set submitflag=? ");
						updateSql1.append(" where seqnum in (select seqnum from "+setname+" where 1=1 ");
						updateSql1.append(" and ins_id=? "); 
						
						updateSql2.append("   ) ");
						updateSql2.append("  and  task_id=?");
						
						//然后加上过滤条件设置选中
						updateSql.append(updateSql1.toString());
						//增加查询控件的查询条件
						if(StringUtils.isNotEmpty(search_sql)){
							updateSql.append(search_sql);
						}
						//过滤条件
						if(StringUtils.isNotEmpty(filterSql)){
							updateSql.append(filterSql);
						}
						updateSql.append(updateSql2.toString());
						//批量的情况 task_id 有逗号
						ArrayList listAll = utilBo.getTaskIdtoInsId(taskId);
						for(int i=0;i<listAll.size();i++){
							HashMap map = (HashMap) listAll.get(i);
							String insId = (String)map.get("ins_id");
							String taskIdo = (String)map.get("task_id");
							ArrayList listTemp = new ArrayList();
							listTemp.add("1");
							listTemp.add(insId);
							listTemp.add(taskIdo);
							list.add(listTemp);
							
							ArrayList listnotTemp = new ArrayList();
							listnotTemp.add("0");
							listnotTemp.add(insId);
							listnotTemp.add(taskIdo);
							listnot.add(listnotTemp);
						}
						
						//先全部设置为不选中
						dao.batchUpdate(updateSql1.toString()+updateSql2.toString(),listnot);
						dao.batchUpdate(updateSql.toString(),list);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rst);
		}
	}

}

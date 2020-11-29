package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 项目名称 ：ehr
 * 类名称：BatchApproveTrans
 * 类描述：批量审批
 * 创建人： lis
 * 创建时间：2016-4-21
 */
public class BatchApproveTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		//Boolean doSelectAll=(Boolean) this.getFormHM().get("doSelectAll");//全选
		//doSelectAll为false时是勾选数据，doSelectAll是true时是未勾选数据
		ArrayList selectedlist=(ArrayList) this.getFormHM().get("selectdata");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try
		{   		
			String tab_id = null;//模板id
			String ins_id = null;//实例id
			TemplateTableBo tablebo=null;
			boolean flag=false;//当前审批人是否是单一节点下多审批人中的一个
			String pre_tab_id="";//用于控制所选的记录都属于一个模板
			StringBuffer taskIds = new StringBuffer();//id串，以“，”分割
			StringBuffer taskIds_en = new StringBuffer();//id加密串，以“，”分割
			/*TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt("dbtask")));
			if(doSelectAll.booleanValue()){//全选
				StringBuffer task_ids = new StringBuffer();//未勾选任务id串
				for(int i=0;i<selectedlist.size();i++)
				{					
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					String task_id=PubFunc.decrypt((String)rec.get("task_id"));
					task_ids.append("," + task_id);
				}
				//查询数据sql
				StringBuffer tableSql = new StringBuffer(tableCache.getTableSql());
				tableSql.append(" and task_id not in(-1");
				if(StringUtils.isNotBlank(task_ids.toString()))
					tableSql.append(task_ids.toString());
				tableSql.append(") ");
				this.frowset = dao.search(tableSql.toString());
				
				while(this.frowset.next()){
					String task_id = this.frowset.getString("task_id");
					tab_id = this.frowset.getString("tabid");
					ins_id = this.frowset.getString("ins_id");
					if (tablebo==null){
		                tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
		            }
					*//** 校验是否可以批量审批 *//*
					if(this.frowset.isFirst())
						flag = this.checkBatchApprove(tablebo, true, task_id, tab_id, pre_tab_id,ins_id);
					else
						flag = this.checkBatchApprove(tablebo, false, task_id, tab_id, pre_tab_id,ins_id);
					pre_tab_id = tab_id;
					taskIds.append(","+task_id);
				}
			}else{*/
				for(int i=0;i<selectedlist.size();i++)
				{					
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					if ("0".equals((String)rec.get("task_id"))){
					    continue;
					}
					String task_id=PubFunc.decrypt((String)rec.get("task_id"));
					tab_id = (String)rec.get("tabid");
					ins_id = (String)rec.get("ins_id");
					if (tablebo==null){
						tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
					}
					/** 校验是否可以批量审批 */
					if(i == 0)
						flag = this.checkBatchApprove(tablebo, true, task_id, tab_id, pre_tab_id,ins_id);
					else
						flag = this.checkBatchApprove(tablebo, false, task_id, tab_id, pre_tab_id,ins_id);
					pre_tab_id = tab_id;
					taskIds.append(","+task_id);
					taskIds_en.append(","+PubFunc.encrypt(task_id));
				}
			//}
			/**审批模式=0自动流转，=1手工指派*/
			int sp_model=tablebo.getSp_mode();
			if(sp_model==1 && flag)
			{
				 throw new GeneralException("不能对执行（考核关系角色）手动指派的流程任务进行批量审批！");//什么意思？？？
			}
			//liuyz bug26306 原来不会在更新submitflag状态，导致如果有数据submitflag值为0导致数据不能被全选。
			if(taskIds.length()>0)
			{
				String updateSql="update t_wf_task_objlink set submitflag=1 where task_id in("+taskIds.substring(1)+")";
				dao.update(updateSql);
			}
			this.getFormHM().put("taskIds", taskIds_en.substring(1));
			this.getFormHM().put("tab_id", tab_id);
		}
		catch(Exception e)
		{
			e .printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 校验是否可以批量审批
	 * @date 2016-4-21
	 * @param tablebo1
	 * @param task_id
	 * @throws GeneralException
	 */
	public boolean checkBatchApprove(TemplateTableBo tablebo1,boolean isFirst,String task_id,String tab_id,String pre_tab_id,String ins_id) throws GeneralException{
		if(!isFirst)
        {
            //只能对同一个业务模板的任务进行批量处理！
            if(!tab_id.equalsIgnoreCase(pre_tab_id))
            {
                throw new GeneralException(ResourceFactory.getProperty("error.equal.template"));
            }
        }
        /**审批模式=0自动流转，=1手工指派*/
        if(tablebo1.getSp_mode()==1){
            if (tablebo1.isDef_flow_self(Integer.parseInt(task_id))){
                throw new GeneralException("自定义审批流程的任务暂不支持批量审批！");  
            }
        }
		boolean flag=false;//当前审批人是否是单一节点下多审批人中的一个
      //当前审批人是否是单一节点下多审批人中的一个
        if(isParallel(Integer.parseInt(task_id),Integer.parseInt(ins_id),tab_id))
        {
            flag=true;
            // throw new GeneralException("不能对执行（考核关系角色）流程的任务进行批量审批！");
        } 
        return flag;
	}
	
	/**
	 * @author lis
	 * @Description: 当前审批人是否是单一节点下多审批人中的一个
	 * @date 2016-4-21
	 * @param taskid
	 * @param ins_id
	 * @param tabid
	 * @return
	 */
	public boolean isParallel(int taskid,int ins_id,String tabid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			buf.append("select  count(tr.role_property) from t_wf_node tn,t_wf_actor ta,t_sys_role tr where tn.node_id=ta.node_id  and ta.actorid=tr.role_id ");
			buf.append(" and tn.tabid="+tabid+" and actor_type=2 and role_property in (9,10,11,12,13,14)");
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				if(rset.getInt(1)>0)
					flag=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
}

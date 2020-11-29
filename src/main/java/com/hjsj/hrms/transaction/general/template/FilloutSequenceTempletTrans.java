/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:人员调入模板新增人员</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2011-6-01:上午17:01:12</p> 
 *@author xgq
 *@version 4.0
 */
public class FilloutSequenceTempletTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("setname");
		String codeid=(String)hm.get("codeid");
		String needcondition = (String) this.userView.getHm().get("template_sql_1");//PubFunc.keyWord_reback((String)hm.get("needcondition"));
		hm.remove(needcondition);
		String whl = PubFunc.keyWord_reback((String)hm.get("whl"));
		hm.remove(whl);
		String tab_id = (String)hm.get("tabid");
		String infor_type  = (String)hm.get("infor_type");
		String ins_id=(String)this.getFormHM().get("ins_id");
		hm.remove(ins_id);
		String task_id=(String)this.getFormHM().get("task_id");
		/**安全平台,判断taskid是否存在于后台**/
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
		if(templateMap!=null&&!templateMap.containsKey(task_id)){
			throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
		}
		*/
		hm.remove(task_id);
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		hm.remove(sp_batch);
		if(ins_id==null|| "".equals(ins_id))
			ins_id="0";
		if(task_id==null|| "".equals(task_id))
			task_id="0";
		if(sp_batch==null|| "".equals(sp_batch))
			sp_batch="0";//单个任务审批
		ArrayList tasklist= new ArrayList();
		if("1".equals(sp_batch))
		{
				String batch_task=(String)this.getFormHM().get("batch_task");
				tasklist=getTaskList(batch_task);
		}
		else
		{
			tasklist=new ArrayList();
			tasklist.add(task_id);
		}	
		ContentDAO dao=null;
		try
		{
			String a0100=null;
			String basepre="";
            dao=new ContentDAO(this.getFrameconn());
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
			//组成sql语句
			StringBuffer buf = new StringBuffer();
			if(name!=null&&name.startsWith("g_templet")){
				a0100 = ""+hm.get("a0100");
				basepre = ""+hm.get("basepre");
				hm.remove("a0100");
				hm.remove("basepre");
				tablebo.filloutSequence(a0100,basepre,name);  
			}else{
			if(!"0".equals(ins_id))
			{
				if("1".equals(infor_type)){
					buf.append("select basepre,a0100,ins_id from ");
					}else if("2".equals(infor_type)){
					buf.append("select b0110,ins_id from ");
					}else if("3".equals(infor_type)){
					buf.append("select e01a1,ins_id from ");
					}else{
						buf.append("select basepre,a0100,ins_id from ");
					}
				buf.append(name);					
				if("1".equals(sp_batch))
				{
					
					buf.append(" where  exists (select null from t_wf_task_objlink where "+name+".seqnum=t_wf_task_objlink.seqnum and "+name+".ins_id=t_wf_task_objlink.ins_id ");
					buf.append("    and submitflag=1  and state<>3  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
					buf.append(" and task_id in (");
					for(int i=0;i<tasklist.size();i++)
					{
						if(i!=0)
							buf.append(",");
						buf.append(tasklist.get(i));
					}
					buf.append(")");
				}
				else
				{
					buf.append(" where  exists (select null from t_wf_task_objlink where "+name+".seqnum=t_wf_task_objlink.seqnum and "+name+".ins_id=t_wf_task_objlink.ins_id ");
					buf.append("    and submitflag=1  and state<>3   and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
					if(task_id.trim().length()>0)
					{
						buf.append("  and task_id=");
						buf.append(task_id);
						buf.append(" )");
					}
					else  
					{
						buf.append(" )");
						buf.append("  and ins_id=");
						buf.append(ins_id);
					}
				}
			}
			else
			{
				if("1".equals(infor_type)){
					buf.append("select basepre,a0100  from ");	
					}else if("2".equals(infor_type)){
					buf.append("select b0110 from ");
					}else if("3".equals(infor_type)){
					buf.append("select e01a1 from ");
					}else{
						buf.append("select basepre,a0100  from ");	
					}
				buf.append(name);	
				buf.append(" where submitflag=1");
				if(needcondition!=null&&needcondition.trim().length()>0)
					buf.append(" "+needcondition);
				if(whl!=null&&whl.trim().length()>0)
					buf.append(" and "+whl);
				
			}
			this.frowset =dao.search(buf.toString());
			while(this.frowset.next()){
				if(infor_type!=null){
					if("1".equals(infor_type)){
						a0100 = this.frowset.getString("a0100");
						basepre = this.frowset.getString("basepre");
					}else if("2".equals(infor_type)){
						a0100 = this.frowset.getString("b0110");
					}else if("3".equals(infor_type)){
						a0100 = this.frowset.getString("e01a1");
					}
				}
				 if("1".equals(tablebo.getId_gen_manual())){
					 tablebo.filloutSequence(a0100,basepre , name);  	
	             }else{
	            	        
	             }
			}
			}
		
			
			
		

	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		ArrayList list=new ArrayList();
		for(int i=0;i<lists.length;i++){
			/**安全平台,判断taskid是否存在于后台**/
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(lists[i])){
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			list.add(lists[i]);
		}
		return list;
		
	}
}





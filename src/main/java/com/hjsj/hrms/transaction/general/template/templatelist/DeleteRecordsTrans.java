package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

public class DeleteRecordsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String task_id=(String)this.getFormHM().get("task_id");
			int ins_id=Integer.parseInt((String)this.getFormHM().get("ins_id"));
			String tabid=(String)this.getFormHM().get("tabid");
			String setname=(String)this.getFormHM().get("setname");
			String isDelMsg=(String)this.getFormHM().get("isDelMsg");
			String selected=(String)this.getFormHM().get("selected");
			String infor_type=(String)this.getFormHM().get("infor_type");
			String operationtype=(String)this.getFormHM().get("operationtype");
			
			String sp_batch=(String)this.getFormHM().get("sp_batch");
			String batch_task=(String)this.getFormHM().get("batch_task");
			
			String task_ids=""; 
			ArrayList tasklist=new ArrayList();
			if("1".equals(sp_batch))
			{
				String[] temps=batch_task.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]!=null&&temps[i].trim().length()>0)
					{	
						task_ids+=","+temps[i];
						tasklist.add(temps[i]);
					}
				}
			}
			else if(!"0".equals(task_id))
				tasklist.add(task_id);
			
			
			
			//删除组员，直接删除，删除组长，把组员的to_id值控  条件：组织单位的合并与划转，岗位的合并
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer("");
			if("1".equals(isDelMsg))
			{
				String codename ="a0100";
				if("1".equals(infor_type))
					codename="a0100";
				else if("2".equals(infor_type))
					codename="b0110";
				else if("3".equals(infor_type))
					codename="b0110";
				String codename2 ="a0100";
				if("1".equals(infor_type))
					codename2="a0100";
				else if("2".equals(infor_type))
					codename2="b0110";
				else if("3".equals(infor_type))
					codename2="e01a1";
				
				sql.append("delete from tmessage where exists (");
				sql.append(" select null from "+setname+" where tmessage."+codename+"="+setname+"."+codename2);
				if("1".equals(infor_type)){
					sql.append("  and  lower(tmessage.db_type)=lower("+setname+".basepre) ");
				}
				if("1".equals(selected))  //选中的记录
				{ 
					if("0".equals(sp_batch))
					{
						if("0".equals(task_id))
							sql.append(" and  submitflag=1 ");
						else
							sql.append(" and  seqnum in (select seqnum from t_wf_task_objlink where task_id="+task_id+"  and tab_id="+tabid+" and (state is null or state<>3) and submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )  ) ");
					}
					else if("1".equals(sp_batch))
					{ 
						sql.append(" and  seqnum in (select seqnum from t_wf_task_objlink where task_id in ("+task_ids.substring(1)+")  and (state is null or state<>3)  and tab_id="+tabid+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ))");
					} 
				} 
				sql.append(" ) and noticetempid="+tabid);
				dao.update(sql.toString());
			}
			
		
			ArrayList list3 = new ArrayList();
			RecordVo vo=new RecordVo(setname);
		 	if("3".equals(infor_type)|| "2".equals(infor_type))
			{ 
				if("0".equals(sp_batch))
				{
					if("0".equals(task_id))
						this.frowset = dao.search(" select * from "+setname+"  where  submitflag=1 ");
					else
						this.frowset = dao.search(" select * from "+setname+"  where  seqnum in (select seqnum from t_wf_task_objlink where task_id="+task_id+"  and tab_id="+tabid+" and (state is null or state<>3) and submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
				}
				else if("1".equals(sp_batch))
				{ 
					this.frowset = dao.search(" select * from "+setname+" where  seqnum in (select seqnum from t_wf_task_objlink where task_id in ("+task_ids.substring(1)+")  and (state is null or state<>3)  and tab_id="+tabid+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ))");
				}
					
				while(this.frowset.next()){
					if(("3".equals(infor_type)&&"8".equals(operationtype))){
						ArrayList list2 = new ArrayList();
						if(this.frowset.getString("e01a1")!=null&&this.frowset.getString("e01a1").equals(this.frowset.getString("to_id"))){
							list2.add(this.frowset.getString("e01a1"));
							list3.add(list2);
						}
					}else if(("2".equals(infor_type)&&("8".equals(operationtype)||"9".equals(operationtype)))){
						if(this.frowset.getString("b0110")!=null&&this.frowset.getString("b0110").equals(this.frowset.getString("to_id"))){
							ArrayList list2 = new ArrayList();
							list2.add(this.frowset.getString("b0110"));
							list3.add(list2);
						}
					}
				}
			}
			
			
			
			
			
			if("0".equals(sp_batch))
			{
				if("0".equals(task_id)){
				    TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
					if("1".equals(selected))  //选中的记录
					{ 
                        tablebo.updateWorkCodeState(setname,"submitflag=1");
					    dao.update("delete from "+setname+"  where  submitflag=1 ");
					    
					}else{//删除全部 
                        tablebo.updateWorkCodeState(setname,"");
					    dao.update("delete from "+setname+" ");	
					    
					}
				}
				else
					dao.update("update t_wf_task_objlink set state=3  where task_id="+task_id+"  and tab_id="+tabid+" and (state is null or state=0 ) and submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
			}
			else if("1".equals(sp_batch))
			{ 
					dao.update("update t_wf_task_objlink set state=3   where task_id in ("+task_ids.substring(1)+")  and (state is null  or state=0)  and tab_id="+tabid+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
			}
			
			
			
			
			if(list3.size()>0){
				if(("3".equals(infor_type)&&"8".equals(operationtype))){
				dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3);	
				}
				else if(("2".equals(infor_type)&&("8".equals(operationtype)||"9".equals(operationtype)))){
					dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3);	
				}
			}
			
			/**正在处理的任务*/
			if("1".equals(sp_batch)||!"0".equals(task_id))
			{
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				for(int i=0;i<tasklist.size();i++)
				{
					String _taskid=(String)tasklist.get(i);
					String topic=tablebo.getRecordBusiTopic(Integer.parseInt(_taskid),0); 
					if(topic.indexOf(",共0")!=-1)
					{
						RecordVo task_vo=new RecordVo("t_wf_task");
						task_vo.setInt("task_id",Integer.parseInt(_taskid));
						task_vo=dao.findByPrimaryKey(task_vo);
						if(task_vo!=null)
						{
							task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
							task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
							task_vo.setString("task_topic", topic);
							dao.updateValueObject(task_vo);
						} 
					}
					else
						dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+_taskid);
					
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	 

}

/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 17, 2008:9:07:36 AM</p>
 * @author chenmengqing
 * @version 1.0
 *
 */
public class BatchDeleteRecTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		String ins_id=(String)this.getFormHM().get("ins_id");
		ArrayList list=(ArrayList)this.getFormHM().get("a0100s");//dbname|a0100|task_id
		String tab_id=(String)this.getFormHM().get("tabid");
		String setname=(String)this.getFormHM().get("setname");//数据库表的名称
		String isDelMsg=(String)this.getFormHM().get("isDelMsg");
		
		String sp_batch=(String)this.getFormHM().get("sp_batch");//批量审批
		String batch_task=(String)this.getFormHM().get("batch_task");//存储着所有的任务号，而不仅仅是选中的
		String task_id=(String)this.getFormHM().get("task_id");
		
		String ins_ids=(String)this.getFormHM().get("ins_ids");
		String infor_type=(String)this.getFormHM().get("infor_type");
		String operationtype=(String)this.getFormHM().get("operationtype");
		String selectAll = (String)this.getFormHM().get("selectAll");//是否全选
		
		ArrayList objectidlist = new ArrayList();//形式：如果是人员模板 usr00000008 组织模板 0102 岗位模板 01010101
		getObjectidAndNbaseList(infor_type,list,objectidlist);//生成objectidlist
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		String task_ids="";//批量处理时的任务号 形式为'687','457'.
		ArrayList tasklist=new ArrayList();//审批流中所有的任务号
		boolean isProcessEnd = false;//是否流程终止 lis 20160418
		if("1".equals(sp_batch)){
			String[] temps=batch_task.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0)//判断是否全选中
				{	
					task_ids+=","+temps[i];
					tasklist.add(temps[i]);
				}
			}
		}
		else if(!"0".equals(task_id)){
			tasklist.add(task_id);
		}
		
		try
		{
				ArrayList list3 = new ArrayList();
				RecordVo vo=new RecordVo(setname);
				//处理组织和岗位
			 	if("3".equals(infor_type)|| "2".equals(infor_type)){//组织、岗位
					if("0".equals(sp_batch)){
						if("0".equals(task_id))
							this.frowset = dao.search(" select * from "+setname+"  where  submitflag=1 ");
						else{
							this.frowset = dao.search(" select * from "+setname+"  where  seqnum in (select seqnum from t_wf_task_objlink where task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state<>3) and submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
						}
					}
					else if("1".equals(sp_batch)){
						this.frowset = dao.search(" select * from "+setname+" where  seqnum in (select seqnum from t_wf_task_objlink where task_id in ("+task_ids.substring(1)+")  and (state is null or state<>3)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ))");
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
			 	
				if("0".equals(sp_batch)&& "0".equals(task_id)){//如果还没有进入审批流
					for(int i=0;i<list.size();i++)
					{
						String value=(String)list.get(i);
						if("".equals(value))
							continue;
						Object[] arr=PubFunc.split(value,"|");// StringUtils.split(value,'|');
						if("1".equals(infor_type))
						{
							vo.setString("basepre", (String)arr[0]);
							vo.setString("a0100", (String)arr[1]);
						}
						else if("2".equals(infor_type))
						{
							vo.setString("b0110", (String)arr[0]);
						}
						else if("3".equals(infor_type))
						{
							vo.setString("e01a1", (String)arr[0]);
						}
						
						vo=dao.findByPrimaryKey(vo);
						//来自于消息,如果删除了此记录，则需要把tmessage中的消息 置为未处理状态
						String state_flag=vo.getString("state");
						if(state_flag==null|| "".equalsIgnoreCase(state_flag))
							state_flag="0";
						int from_msg=Integer.parseInt(state_flag);
						if(from_msg==1)					
						{
							if("0".equals(isDelMsg))
								setMessageState(vo,tab_id,infor_type);
							else
							{
								if("1".equals(infor_type))
									dao.update("delete from tmessage where a0100='"+vo.getString("a0100")+"' and lower(db_type)='"+vo.getString("basepre").toLowerCase()+"' and noticetempid="+tab_id);
								else if("2".equals(infor_type))
									dao.update("delete from tmessage where b0110='"+(String)arr[0]+"' and object_type=2  and noticetempid="+tab_id);
								else if("3".equals(infor_type))
									dao.update("delete from tmessage where b0110='"+(String)arr[0]+"' and object_type=3  and noticetempid="+tab_id);
							}
						}
						
					}
				}
				
				if(list3.size()>0){
					if(("3".equals(infor_type)&&"8".equals(operationtype))){
						dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3);	
					}
					else if(("2".equals(infor_type)&&("8".equals(operationtype)||"9".equals(operationtype)))){
						dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3);	
					}
				}
				
				
				///开始撤销数据
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.getFrameconn(),this.userView);
				
				if("0".equals(sp_batch)){//单个处理
					if("0".equals(task_id)){//未进入审批流
					    TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
					    tablebo.updateWorkCodeState(setname,"submitflag=1");
					    dao.update("delete from "+setname+"  where  submitflag=1 ");
					}
					else{//进入了审批流
						StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id);
						strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
						strsql.append("  and task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state=0 ) and submitflag=1 and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
						//if(ins.isStartNode(task_id))
						//{
						ins.insertKqApplyTable(strsql.toString(),tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
						//	}
						dao.update("update t_wf_task_objlink set state=3  where task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state=0 ) and submitflag=1 and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
					}
				}else if("1".equals(sp_batch)){//批量处理，（肯定是在审批流中了）
					StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id); 
					strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append(" and  task_id=xxx  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
					String[] temps=task_ids.split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0/*&&ins.isStartNode(temps[i])*/)
						{ 
							ins.insertKqApplyTable(strsql.toString().replaceAll("xxx",temps[i]),tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
						}
					}	
					dao.update("update t_wf_task_objlink set state=3   where task_id in ("+task_ids.substring(1)+")  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
				}
				
				
				
				/**结束正在处理的任务*/
				if("1".equals(sp_batch)||!"0".equals(task_id)){//在审批流中
					TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
					for(int i=0;i<tasklist.size();i++)
					{
						String _taskid=(String)tasklist.get(i);
						if(ins.isStartNode(task_id) && isAllSelectedTaskId(dao, tab_id, _taskid)){
							isProcessEnd = true;//流程终止 lis 20160418
							ins.processEnd(Integer.valueOf(_taskid), Integer.valueOf(tab_id), userView,0);					
						}else{
							String topic=tablebo.getRecordBusiTopic(Integer.parseInt(_taskid),0); 
							if(topic.indexOf(",共0")!=-1)
							{ 
								RecordVo task_vo=new RecordVo("t_wf_task");
								task_vo.setInt("task_id",Integer.parseInt(_taskid));
								task_vo=dao.findByPrimaryKey(task_vo);
								if(task_vo!=null)
								{
									topic=tablebo.getRecordBusiTopicByState(Integer.parseInt(_taskid),3);
									task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
									task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
									task_vo.setString("task_topic", topic);
									
									String fullsender=this.userView.getUserFullName();
									if(fullsender==null|| "".equalsIgnoreCase(fullsender))
										fullsender=this.userView.getUserName(); 
									String sender=null;
									if(this.userView.getStatus()!=0)
										sender=this.userView.getDbname()+this.userView.getA0100();
									else
										sender=this.userView.getUserId();
									String appuser=task_vo.getString("appuser")+this.userView.getUserName()+",";
									task_vo.setString("appuser", appuser);
									task_vo.setString("a0100",sender);
									task_vo.setString("a0101",fullsender);
									task_vo.setString("content","撤销记录");
									dao.updateValueObject(task_vo);
								}
							}
							else
								dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+_taskid);
						}
						
					}
					 
				}
				
			    //开始删除附件（审批过程中不能删除附件）  郭峰
				if(sp_batch==null || "0".equals(sp_batch)){//单个处理
					if(ins_id==null || "0".equals(ins_id)){//未进入审批流
						deleteAttachment(tab_id,"0",selectAll,infor_type,objectidlist);
					}
				}
				PendingTask imip=new PendingTask();
				String queryPersonSql = "";
				if(!isProcessEnd){//不是流程终止,lis 20160418
					if(sp_batch==null || "0".equals(sp_batch)){
						queryPersonSql = "select distinct(task_id) from t_wf_task_objlink where task_id="+task_id+" and (state<>3 or state is null)";
						this.frowset = dao.search(queryPersonSql);
						if(!this.frowset.next()){
							
							String pendingCode="HRMS-"+PubFunc.encrypt(task_id); 
							String pendingType="业务模板";  
							imip.updatePending("T",pendingCode,100,pendingType,this.userView); 
						}
					}else{
						queryPersonSql = "select distinct(task_id) from t_wf_task_objlink where task_id in( "+task_ids.substring(1)+ ") and state<>3";
						this.frowset = dao.search(queryPersonSql);
						while(this.frowset.next()){
							String tempTaskId= String.valueOf(this.frowset.getInt("task_id"));
							tasklist.remove(tempTaskId);
						}
						for(int i=0;i<tasklist.size();i++){
							String pendingCode="HRMS-"+PubFunc.encrypt((String)tasklist.get(i)); 
							String pendingType="业务模板";  
							imip.updatePending("T",pendingCode,100,pendingType,this.userView); 
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
	
	/** objectidlist的形式：里面有许多小List。每个小list存放着objectid,basepre的信息（当infor_type=1时）。
	 * 参数：  list:形式为dbname|a0100|task_id
	 * */
	private void getObjectidAndNbaseList(String infor_type,ArrayList list,ArrayList objectidlist){
		if(list==null || list.size()<=0){
			return;
		}
		if("1".equals(infor_type)){
			int n = list.size();
			for(int i=0;i<n;i++){
				String tempstr = (String)list.get(i);
				String[] arr = tempstr.split("\\|");
				ArrayList templist = new ArrayList();
				templist.add(arr[0]);
				templist.add(arr[1]);
				objectidlist.add(templist);
			}
		}else if("2".equals(infor_type) || "3".equals(infor_type)){
			int n = list.size();
			for(int i=0;i<n;i++){
				String tempstr = (String)list.get(i);
				String[] arr = tempstr.split("\\|");
				ArrayList templist = new ArrayList();
				templist.add(arr[0]);
				objectidlist.add(templist);
			}
		}
	}
	
	/**撤销人员时，顺便也要删除个人附件。如果是全部撤销，就要把公共附件也删除掉
	 * 参数：objectidlist：里面有许多小List。每个小list存放着objectid,basepre的信息（当infor_type=1时）。
	 * */
	private void deleteAttachment(String tabid,String ins_id,String selectAll,String infor_type,ArrayList objectidlist){
		try{
			StringBuffer sb = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String username = this.userView.getUserName();
			//只要撤销，就要删除个人附件
			if(objectidlist.size()>0){
				sb.append("delete from t_wf_file where ins_id =" + ins_id + " and tabid="+tabid+" and create_user='"+username+"' and attachmenttype=1");
				if("1".equals(infor_type)){
					sb.append(" and basepre=? and objectid=?");
				}else{
					sb.append(" and objectid=?");
				}
				dao.batchUpdate(sb.toString(), objectidlist);
			}
			if("1".equals(selectAll)){//只有全部撤销，才有可能删除公共附件附件。
				//删除公共附件
				sb.setLength(0);
				sb.append("delete from t_wf_file where ins_id =" + ins_id + " and tabid="+tabid+" and create_user='"+username+"' and (attachmenttype=0 or attachmenttype is null)");
				dao.update(sb.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 设置消息的状态
	 * @param vo
	 * @param template_id
	 * @throws GeneralException
	 */
	private void setMessageState(RecordVo vo,String template_id,String infor_type)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			if("1".equals(infor_type))
			{
	
				buf.append("update tmessage set state=0 where a0100=? and db_type=? and noticetempid=?"); 
				paralist.add(vo.getString("a0100"));
				paralist.add(vo.getString("basepre"));
				paralist.add(template_id);
			}
			else
			{
				buf.append("update tmessage set state=0 where b0110=? and noticetempid=?"); 
				if("2".equals(infor_type))
					paralist.add(vo.getString("b0110"));
				else if("3".equals(infor_type))
					paralist.add(vo.getString("e01a1"));
				paralist.add(template_id); 
			}
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**   
	 * @Title: isSelectedTaskId   
	 * @Description: 判断单据里面的记录是否被选中，如果没有选中的，则后续不处理   
	 * @param @param dao
	 * @param @param tabid
	 * @param @param task_id
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @author:wangrd   
	 * @throws   
	*/
	private boolean isAllSelectedTaskId(ContentDAO dao,String tabid,String task_id)throws GeneralException
    {
	    boolean b=false;
        try
        {
            String sqlstr = "select count(*) from templet_"+tabid 
                +" where  exists (select null from t_wf_task_objlink where templet_"
                +tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "
                +"  and task_id="+task_id+"   and submitflag=0  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            this.frowset=dao.search(sqlstr);
            if(this.frowset.next())
            {
                if(this.frowset.getInt(1)==0)
                   b=true; 
            }
                    
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }

}

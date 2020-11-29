/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:任务重新分派</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 3, 200611:39:21 AM
 * @author chenmengqing
 * @version 4.0
 */
public class ReAssignTaskTrans extends IBusiness {

	/**
	 * 设置选中状态
	 * @param task_id
	 * @param tabid
	 * @throws GeneralException
	 */
	private void setSubmitflag(int task_id,int tabid)throws GeneralException
	{
		String tablename="templet_"+tabid;
		StringBuffer buf=new StringBuffer();
//		buf.append("update ");
//		buf.append(tablename);
//		buf.append(" set submitflag=1 where task_id=");
//		buf.append(task_id);
		//这里不能限制本人或具有范围的角色
		buf.append("update t_wf_task_objlink set submitflag=1 where exists (select null from "+tablename+" where t_wf_task_objlink.seqnum="+tablename+".seqnum) and task_id="+task_id+"  ");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	
	public void execute() throws GeneralException {
		ArrayList ins_list=(ArrayList)this.getFormHM().get("selectedlist");
		String actorid=(String)this.getFormHM().get("actorid");
		String actorname=(String)this.getFormHM().get("actorname");
		String objecttype=(String)this.getFormHM().get("actortype");
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String specialOperate_self=(String)hm.get("specialOperate_self");  //业务模板中人员需要报送给各自领导进行审批处理
		hm.remove("specialOperate_self");
		String specialRoleUserStr=(String)this.getFormHM().get("specialRoleUserStr"); //特殊角色指定的用户   nodeid:xxxx,nodeid:yyyy
		if(specialRoleUserStr==null)
			specialRoleUserStr="";
		/**根据actorid求得对象类型，单位、部门、职位及人员
		 * UN/UM/@K
		 * */
		if(actorid==null|| "".equals(actorid))
			throw new GeneralException(ResourceFactory.getProperty("error.notselect.object"));
		if(actorid.startsWith("UN")||actorid.startsWith("UM")||actorid.startsWith("@K"))
			objecttype="3";
		if(ins_list==null||ins_list.size()==0)
			throw new GeneralException(ResourceFactory.getProperty("error.wf.notobject"));
		
		//判断是否是当前任务是否是自定义审批流程？自定义的只能重新分配给自助用户
		if (!"1".equals(objecttype)){
		    checkDef_flow_selfTask(ins_list);
		}
		try
		{
			java.util.Date d=new java.util.Date();
			for(int i=0;i<ins_list.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)ins_list.get(i);
				String ins_id=(String)dynabean.get("ins_id");//流程实例
				String tabid=(String)dynabean.get("tabid");//表格号
				String taskid=(String)dynabean.get("task_id");
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
				RecordVo ins_vo=new RecordVo("t_wf_instance");
				ins_vo.setInt("ins_id",Integer.parseInt(ins_id));	
				/**把重新分配任务选中*/
				setSubmitflag(Integer.parseInt(taskid),Integer.parseInt(tabid));
				WF_Actor wf_actor=new WF_Actor(actorid,objecttype);
				wf_actor.setContent(ResourceFactory.getProperty("button.reassign")); //批复内容
				wf_actor.setEmergency("1"); //优先级
				wf_actor.setSp_yj("03");//审批意见
				wf_actor.setActorname(actorname);
				ins.setIns_id(Integer.parseInt(ins_id));
				ins.setSpecialOperate(specialOperate_self);
				if(specialRoleUserStr.length()>0) 
					wf_actor.setSpecialRoleUserList(specialRoleUserStr); 
				
				
				ins.setReAssign(true);
                if (isFinishedTask(taskid)){
                    throw new GeneralException("当前单据已被处理,操作失败");
                }
				if(ins.reAssignTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView))
				{
					if(true)
					continue;
					//	当前审批人是否是单一节点下多审批人中的一员
					ContentDAO dao=new ContentDAO(this.frameconn);
					RowSet rowSet=dao.search("select td.*,t.pri_task_id from t_wf_task_datalink td,t_wf_task t where  td.task_id=t.task_id and td.task_id="+taskid+" and td.ins_id="+ins_id);
					ArrayList list=new ArrayList();
					if(rowSet.next())
					{
						String _seqnum=rowSet.getString("seqnum");
						int _ins_id=rowSet.getInt("ins_id");
						int _task_id=rowSet.getInt("task_id");
						int _node_id=rowSet.getInt("node_id");
						String _role_id=rowSet.getString("role_id");
						String pri_task_id=rowSet.getString("pri_task_id");
						
						String topic=tablebo.getTable_vo().getString("name")+"(,共0人)"; //    ins.getRecordBusiTopic(Integer.parseInt(taskid),Integer.parseInt(ins_id));
						
					/*	ArrayList tempList=new ArrayList();
						tempList.add(topic);
						tempList.add("重新分派");	
						tempList.add(DateStyle.getSystemTime());
						tempList.add(new Integer(_task_id));
						list.add(tempList);
						*/
						RecordVo task_vo=new RecordVo("t_wf_task");
						task_vo.setInt("task_id",_task_id);
						task_vo=dao.findByPrimaryKey(task_vo);
						task_vo.setString("task_topic", topic);
						task_vo.setString("content", "重新分派");
						task_vo.setDate("end_date",new java.util.Date());
						task_vo.setInt("task_state",5);
						task_vo.setString("a0100", this.userView.getUserId());
						task_vo.setString("a0101", this.userView.getUserFullName());
						dao.updateValueObject(task_vo);
						
						
						
						String sql="select t.* from t_wf_task t,t_wf_task_datalink td where t.task_id=td.task_id and t.ins_id="+ins_id;
						 sql+=" and td.ins_id="+ins_id+" and td.seqnum='"+_seqnum+"' and t.pri_task_id="+pri_task_id;
						rowSet=dao.search(sql);
						while(rowSet.next())
						{
							int task_id=rowSet.getInt("task_id");
							
							task_vo=new RecordVo("t_wf_task");
							task_vo.setInt("task_id",task_id);
							task_vo=dao.findByPrimaryKey(task_vo);
							task_vo.setString("task_topic", topic);
							task_vo.setString("content", "重新分派");
							task_vo.setDate("end_date",new java.util.Date());
							task_vo.setInt("task_state",5);
							task_vo.setString("a0100", this.userView.getUserId());
							task_vo.setString("a0101", this.userView.getUserFullName());
							dao.updateValueObject(task_vo);
							
						/*	tempList=new ArrayList();
							tempList.add(topic);
							tempList.add("重新分派"); 
							tempList.add(DateStyle.getSystemTime());
							tempList.add(new Integer(task_id));
							list.add(tempList);*/
							
							dao.update("update t_wf_task_datalink set state=1 where task_id="+task_id+" and ins_id="+ins_id);
						}
						dao.update("update t_wf_task_datalink set state=1 where task_id="+taskid+" and ins_id="+ins_id);	
				//		dao.batchUpdate("update t_wf_task set task_topic=?,content=?,end_date=?,task_state=5,a0100='"+this.userView.getUserId()+"',a0101='"+this.userView.getUserFullName()+"' where task_id=?", list);
					}
				}			
			}			/**loop end.*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
    /**
     * 判断是否是已结束的单据
     * @param task_id
     * @return
     */
    public boolean isFinishedTask(String task_id)
    {
        boolean bFinished=false;
        RowSet rowSet=null;
        try
        {
            ContentDAO dao=new ContentDAO(this.frameconn);
            rowSet=dao.search("select count(task_id) from t_wf_task where task_id="+task_id
                           +" and (task_state='4' or task_state='5' or task_state='6')");
            if(rowSet.next())
            {
                if(rowSet.getInt(1)>0)
                    bFinished=true;
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            PubFunc.closeResource(rowSet);
        }
        
        return bFinished;
    }
	private void checkDef_flow_selfTask(ArrayList ins_list) throws GeneralException
	{
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            for(int i=0;i<ins_list.size();i++)
            {
                LazyDynaBean dynabean=(LazyDynaBean)ins_list.get(i);
                String tabid=(String)dynabean.get("tabid");//表格号
                String taskid=(String)dynabean.get("task_id");
                TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
                if (tablebo.isDef_flow_self(Integer.parseInt(taskid))){
                    RecordVo task_vo=new RecordVo("t_wf_task");
                    task_vo.setInt("task_id",Integer.parseInt(taskid));
                    task_vo=dao.findByPrimaryKey(task_vo);
                    throw new GeneralException("任务["+task_vo.getString("task_topic")+"]是自定义审批流程，只能分配给自助用户审批！");
                }
                
            }    

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
	}

}

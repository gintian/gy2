package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:任务重新分派</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 3, 20160425
 * @author lis
 */
public class ReAssignTaskTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		//Boolean doSelectAll=(Boolean) this.getFormHM().get("doSelectAll");//全选
		//doSelectAll为false时是勾选数据，doSelectAll是true时是未勾选数据
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedList");
		String codesetid=(String)this.getFormHM().get("codesetid");
		String actorid=(String)this.getFormHM().get("actorid");
		if(StringUtils.isNotBlank(codesetid))
			actorid = codesetid + PubFunc.decrypt(actorid);
		else
			actorid = PubFunc.decrypt(actorid);
		String actorname=(String)this.getFormHM().get("actorname");
		String objecttype=(String)this.getFormHM().get("actortype");
		/*if(true)
			return;*/
		ContentDAO dao=new ContentDAO(this.frameconn);
		String specialOperate_self=(String)this.getFormHM().get("specialOperate_self");  //业务模板中人员需要报送给各自领导进行审批处理
		String specialRoleUserStr=(String)this.getFormHM().get("specialRoleUserStr"); //特殊角色指定的用户   nodeid:xxxx,nodeid:yyyy
		if(specialRoleUserStr==null)
			specialRoleUserStr="";
		/**根据actorid求得对象类型，单位、部门、职位及人员
		 * UN/UM/@K
		 * */
		if(actorid==null|| "".equals(actorid))
			throw new GeneralException(ResourceFactory.getProperty("error.notselect.object"));
		/*if(codesetid.equalsIgnoreCase("UN")||codesetid.equalsIgnoreCase("UM")||codesetid.equalsIgnoreCase("@K"))
			objecttype="3";*/
		if(selectedlist==null||selectedlist.size()==0)
			throw new GeneralException(ResourceFactory.getProperty("error.wf.notobject"));
		
		try
		{
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt("ctrltask")));
			
			/*if(doSelectAll.booleanValue()){//全选
				StringBuffer task_ids = new StringBuffer();//未勾选任务id串
				for(int i=0;i<selectedlist.size();i++)
				{					
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					String task_id=PubFunc.decrypt((String)rec.get("task_id_e"));
					task_ids.append("," + task_id);
				}
				//查询数据sql
				StringBuffer tableSql = new StringBuffer(tableCache.getTableSql());
				tableSql.append(" and task_id not in(-1");
				if(StringUtils.isNotBlank(task_ids.toString()))
					tableSql.append(task_ids);
				tableSql.append(") ");
				this.frowset = dao.search(tableSql.toString());
				while(this.frowset.next()){
					MorphDynaBean dynabean= new MorphDynaBean();
					dynabean.set("ins_id", this.frowset.getString("ins_id"));
					dynabean.set("tabid", this.frowset.getString("tabid"));
					dynabean.set("task_id", PubFunc.encrypt(this.frowset.getString("task_id")));
					this.reAssignTask(dynabean, actorid, actorname, objecttype, specialOperate_self, specialRoleUserStr);
				}
			}else{*/
				//判断是否是当前任务是否是自定义审批流程？自定义的只能重新分配给自助用户
				if (!"1".equals(objecttype)){
					checkDef_flow_selfTask(selectedlist);
				}
				for(int i=0;i<selectedlist.size();i++)
				{
					DynaBean dynabean=(DynaBean)selectedlist.get(i);
					this.reAssignTask(dynabean, actorid, actorname, objecttype, specialOperate_self, specialRoleUserStr);
				}	
			//}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	
	/**
	 * @author lis
	 * @Description: 重新分派
	 * @date 2016-4-25
	 * @param dynabean 数据集合
	 * @param actorid  用户id或组织代码
	 * @param actorname 用户名称或组织名称
	 * @param objecttype 分派类型
	 * @param specialOperate_self 是否特殊角色
	 * @param specialRoleUserStr 特殊角色
	 * @return
	 * @throws GeneralException
	 */
	private void reAssignTask(DynaBean dynabean,String actorid,String actorname,String objecttype,String specialOperate_self,String specialRoleUserStr) throws GeneralException{
		try {

			String ins_id=(String)dynabean.get("ins_id");//流程实例
			String tabid=(String)dynabean.get("tabid");//表格号
			String taskid=(String)dynabean.get("task_id_e");
			taskid = PubFunc.decrypt(taskid);
			
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
			ins.reAssignTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);			
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
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
            	DynaBean dynabean=(DynaBean)ins_list.get(i);
                String tabid=(String)dynabean.get("tabid");//表格号
                String taskid=(String)dynabean.get("task_id_e");
                taskid = PubFunc.decrypt(taskid);
                TemplateBo tablebo=new TemplateBo(this.frameconn, this.userView, Integer.valueOf(tabid));
                if (tablebo.isDef_flow_self(Integer.parseInt(taskid))){
                    RecordVo task_vo=new RecordVo("t_wf_task");
                    task_vo.setInt("task_id",Integer.parseInt(taskid));
                    task_vo=dao.findByPrimaryKey(task_vo);
                    throw new GeneralException("任务["+task_vo.getString("task_topic")+"]是自定义审批流程，只能分配给自助用户审批");
                }
                
            }    

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
	}

}

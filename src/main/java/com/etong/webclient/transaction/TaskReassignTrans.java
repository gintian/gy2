package com.etong.webclient.transaction;

import java.util.HashMap;
import org.jbpm.*;
import org.jbpm.model.execution.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.*;


/**
 * <p>Title:任务指派 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskReassignTrans extends IBusiness
{
  public TaskReassignTrans()
  {
  }
  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    /**
     * href中传过来的参数
     */
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String token_id=(String)hm.get("a_taskid");
    String advice=(String)this.getFormHM().get("advice");
    String advice_type=(String)this.getFormHM().get("advice_type");
    String next_actorId=(String)this.getFormHM().get("actor_id");
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    Token token=executionService.getToken(new Long(token_id));

    //做记录
    String actorId=token.getActorId();
    String instanceId=token.getProcessInstance().getId().toString();
    String definitionId=token.getProcessInstance().getDefinition().getId().toString();
    String state=token.getState().getId().toString();

    ContentDAO dao=new ContentDAO(this.getFrameconn());
    RecordVo  votmp=new RecordVo("t_bpm_advice");
    votmp.setString("process_id",definitionId);
    votmp.setString("instance_id",instanceId);
    votmp.setString("actor_id",actorId);
    votmp.setString("node_name",state);
    votmp.setString("advice_type",advice_type);
    votmp.setString("advice_value",advice);
    votmp.setString("status","1");
    votmp.setString("start_date",DateStyle.dateformat(token.getStart(),"yyyy-MM-dd HH:mm:ss"));
    java.util.Date date=new java.util.Date();
    votmp.setString("end_date",DateStyle.dateformat(date,"yyyy-MM-dd HH:mm:ss"));

    try
    {
      dao.addValueObject(votmp);
      if(next_actorId!=null||!"".equals(next_actorId)||!"#".equals(next_actorId))
        executionService.reassign(token.getId(),next_actorId);
      else
        throw new GeneralException("","未指定下环节的业务人员,分派任务失败！","","");
      executionService.close();
    }
    catch(ExecutionException ee)
    {
      ee.printStackTrace();
      throw GeneralExceptionHandler.Handle(ee);
    }
    catch(Exception eee)
    {
      eee.printStackTrace();
      throw GeneralExceptionHandler.Handle(eee);
    }

  }

}
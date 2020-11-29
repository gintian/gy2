package com.etong.webclient.transaction;

import java.util.*;
import java.sql.*;
import org.jbpm.*;
import org.jbpm.model.execution.*;

import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.DateStyle;


/**
 * <p>Title:工作任务审批,结束当前审批环节 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskSubmitTrans extends IBusiness
{
  public TaskSubmitTrans()
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

    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    Token token=executionService.getToken(new Long(token_id));

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

    /**
     *variable in the token.variable's length <=250.
     */
    Map variables=executionService.getVariables(token.getId());
    //variables.put(userView.getUserId()+"_yj",advice);
    variables.put("advice_type",advice_type);
    try
    {
      dao.addValueObject(votmp);
      executionService.endOfState(new Long(token_id), variables);
    }
    catch(ExecutionException ee)
    {
      ee.printStackTrace();
      throw GeneralExceptionHandler.Handle(ee);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
    finally
    {
      executionService.close();
    }

  }

}
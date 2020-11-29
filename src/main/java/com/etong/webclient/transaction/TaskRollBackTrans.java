package com.etong.webclient.transaction;

import java.util.*;
import javax.sql.RowSet;
import java.sql.*;
import org.jbpm.*;
import org.jbpm.model.execution.*;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.*;
import com.hrms.struts.exception.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.frame.utility.DBExecute;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskRollBackTrans extends IBusiness
{
  public TaskRollBackTrans()
  {
  }

  /**
   * 回退到一个审批环节
   * @param token
   */
  private void rollbackToken(Token token) throws GeneralException
  {
    String instance_id=token.getProcessInstance().getId().toString();
    String token_id=token.getId().toString();
    StringBuffer strsql=new StringBuffer();
    //查找上一环节的算法是否存在问题？因为批文审批步骤只能在运算过程求得.
    strsql.append("select node_name,actor_id from t_bpm_advice where status='1' and instance_id=");
    strsql.append(instance_id);
    strsql.append(" order by advice_id desc");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    try
    {
      this.frowset=dao.search(strsql.toString());
      if (this.getFrowset().next())
      {
        String actor_id=this.getFrowset().getString("actor_id");
        String state_id=this.getFrowset().getString("node_name");
        strsql.setLength(0);

        strsql.append("update jbpm_token set actorid=?,state=? where id=?");
        ArrayList list=new ArrayList();
        list.add(actor_id);
        list.add(state_id);
        list.add(token_id);
        dao.update(strsql.toString(),list);
      }//if loop end.
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      throw GeneralExceptionHandler.Handle(sqle);
    }
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
//    String pre_actor_id=null;
//    if(token.getParent()!=null)
//        pre_actor_id=token.getParent().getActorId();
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
    votmp.setString("status","0");
    votmp.setString("start_date",DateStyle.dateformat(token.getStart(),"yyyy-MM-dd HH:mm:ss"));
    java.util.Date date=new java.util.Date();
    votmp.setString("end_date",DateStyle.dateformat(date,"yyyy-MM-dd HH:mm:ss"));
    /**
     * 回退上一个环节处理,把JBPM_TOKEN中state 和actorId置为上一环节的状态和流程参与者。
     */
    try
    {
      rollbackToken(token);
      dao.addValueObject(votmp);
      executionService.close();
//      executionService.undo(token.getId(),DateStyle.parseDate("2005-3-8 20:21:40"));
//      if(!(pre_actor_id==null||pre_actor_id.equals("")))
//        executionService.reassign(token.getId(),pre_actor_id);
//      else
//        throw new GeneralException("","找不到上一环节的处理人员,不能回退！","","");
    }
//    catch(ExecutionException ee)
//    {
//      ee.printStackTrace();
//      throw GeneralExceptionHandler.Handle(ee);
//    }
    catch(Exception eee)
    {
      eee.printStackTrace();
      throw GeneralExceptionHandler.Handle(eee);
    }

  }

}
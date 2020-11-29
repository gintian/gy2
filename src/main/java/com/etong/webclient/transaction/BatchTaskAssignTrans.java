package com.etong.webclient.transaction;

import java.util.*;
import org.jbpm.*;
import org.jbpm.model.execution.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.*;
import com.etong.webclient.valueobject.*;



/**
 * <p>Title:批量重新指派任务 </p>
 * <p>Description:BatchTaskAssignTrans </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class BatchTaskAssignTrans extends IBusiness
{
  public BatchTaskAssignTrans()
  {
  }
  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    String next_actorId=(String)this.getFormHM().get("actor_id");
    ArrayList list=(ArrayList)this.getFormHM().get("selectedtasklist");
    if(list==null)
      return;
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    try
    {
      for(int i=0;i<list.size();i++)
      {
        String tokenid=((TaskView)list.get(i)).getId();
        Token token=executionService.getToken(new Long(tokenid));
        cat.debug("tokenid="+tokenid);
        executionService.reassign(token.getId(), next_actorId);
      }
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
    finally
    {
      executionService.close();
    }

  }

}
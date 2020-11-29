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
 * <p>Title:BatchTaskEndTrans </p>
 * <p>Description:批量终止流程 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class BatchTaskEndTrans extends IBusiness
{
  public BatchTaskEndTrans()
  {
  }
  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    ArrayList list=(ArrayList)this.getFormHM().get("selectedtasklist");
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    try
    {
      for(int i=0;i<list.size();i++)
      {
        String instance_id=((TaskView)list.get(i)).getInstance_id();
        executionService.cancelProcessInstance (new Long(instance_id));
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
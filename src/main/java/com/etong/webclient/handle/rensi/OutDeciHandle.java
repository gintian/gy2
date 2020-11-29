package com.etong.webclient.handle.rensi;

import org.jbpm.delegation.DecisionHandler;
import org.jbpm.delegation.ExecutionContext;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class OutDeciHandle implements DecisionHandler
{
  public OutDeciHandle()
  {
  }
  @Override
  public String decide(ExecutionContext parm1)
  {
    String user_id=(String)parm1.getVariable("user_id");
    if("bbb".equals(user_id))
      return "异地";
    else
    {
      return "本地";
    }

  }

}
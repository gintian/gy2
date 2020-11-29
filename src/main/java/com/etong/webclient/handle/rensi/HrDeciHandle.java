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

public class HrDeciHandle implements DecisionHandler
{
  public HrDeciHandle()
  {
  }

  @Override
  public String decide(ExecutionContext parm1)
  {
    String user_id=(String)parm1.getVariable("user_id");
    String approve=(String)parm1.getVariable("advice_type");
    if("bbb".equals(user_id))
    {
      return "写库";
    }
    else
    {
      if ("02".equals(approve))
        return "集团驳回";
      else
        return "写库";
    }
  }

}
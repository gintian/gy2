package com.etong.webclient.test;

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

public class ManagerDecisionHandle implements DecisionHandler
{
  public ManagerDecisionHandle()
  {
  }
  @Override
  public String decide(ExecutionContext parm1)
  {
    String approve=(String)parm1.getVariable("advice_type");
    if("02".equals(approve))
      return "经理驳回";
    else
      return "经理同意";
  }

}
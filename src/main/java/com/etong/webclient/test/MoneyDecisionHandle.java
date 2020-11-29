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

public class MoneyDecisionHandle implements DecisionHandler
{
  public MoneyDecisionHandle()
  {
  }
  @Override
  public String decide(ExecutionContext parm1)
  {
    String  money=(String)parm1.getVariable("money");
    if(Double.parseDouble(money)>=10000)
    {
      return "大于等于10000";
    }
    else
    {
      return "小于10000";
    }
  }

}
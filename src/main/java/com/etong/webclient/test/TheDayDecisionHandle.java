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

public class TheDayDecisionHandle implements DecisionHandler
{
  public TheDayDecisionHandle()
  {
  }
  @Override
  public String decide(ExecutionContext parm1)
  {
    String  theday=(String)parm1.getVariable("theday");
    if(Integer.parseInt(theday)>=5)
    {
      return "天数大于等于5";
    }
    else
    {
      return "天数小于5";
    }
  }

}
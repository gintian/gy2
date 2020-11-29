package com.etong.webclient.test;

import org.jbpm.delegation.DecisionHandler;
import org.jbpm.delegation.ExecutionContext;
import com.hrms.struts.valueobject.UserView;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class ApproveDecisionHandle implements DecisionHandler
{
  public ApproveDecisionHandle()
  {
  }
  @Override
  public String decide(ExecutionContext parm1)
  {
    //UserView user=(UserView)parm1.getVariable("userview");
    //String pos=user.getUserId();
    String pos=(String)parm1.getVariable("user_id");
    System.out.println("approve decision ="+pos);
    if("ni168".equals(pos))
      return "boss_approve";
    else
      return "manager_approve";
  }

}
package com.etong.webclient.test;

import org.jbpm.delegation.AssignmentHandler;
import org.jbpm.delegation.AssignmentContext;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class EmployAssignHandle implements AssignmentHandler
{
  public EmployAssignHandle()
  {
  }

  @Override
  public String selectActor(AssignmentContext parm1)
  {
    String user_id=(String)parm1.getVariable("user_id");
    System.out.println("------->"+user_id);
    return user_id;
  }

}
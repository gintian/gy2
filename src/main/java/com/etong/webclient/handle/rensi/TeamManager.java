package com.etong.webclient.handle.rensi;

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

public class TeamManager implements AssignmentHandler
{
  public TeamManager()
  {
  }

  @Override
  public String selectActor(AssignmentContext parm1)
  {
    return (String)parm1.getVariable("user_id");
  }

}
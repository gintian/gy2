package com.etong.webclient.test;

import org.jbpm.delegation.AssignmentHandler;
import org.jbpm.delegation.AssignmentContext;

import com.hrms.struts.valueobject.UserView;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class ManageAssignHandle implements AssignmentHandler
{
  public ManageAssignHandle()
  {
  }
  @Override
  public String selectActor(AssignmentContext parm1)
  {
//    UserView user=(UserView)parm1.getVariable("userview");
//    if(user.getUserId()=="mj")
//      return "mj";
//    else
//      return "";
    return "ni168";
  }

}
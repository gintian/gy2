package com.etong.webclient.handle.rensi;

import org.jbpm.delegation.ActionHandler;
import org.jbpm.delegation.ExecutionContext;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class Writedb_handle implements ActionHandler
{
  public Writedb_handle()
  {
  }
  @Override
  public void execute(ExecutionContext parm1)
  {
	  
    String user_id=(String)parm1.getVariable("user_id");
  }

}
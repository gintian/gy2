package com.etong.webclient.transaction;

import java.util.*;
import java.sql.*;
import org.jbpm.*;
import org.jbpm.delegation.*;
import org.jbpm.model.execution.*;
import org.jbpm.model.definition.*;


import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.utility.DateStyle;
import com.etong.webclient.valueobject.TaskView;
import com.hrms.frame.dao.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskListTrans extends IBusiness
{
  public TaskListTrans()
  {
  }

  @Override
  public void execute() throws GeneralException
  {
    ArrayList tasklist = getTaskListBySQL();//getTaskListByJbpmService();
    this.getFormHM().put("tasklist",tasklist);
  }

  /**
   * 可以查阅流程的发启者
   * @return
   * @throws GeneralException
   */
  private ArrayList getTaskListBySQL()throws GeneralException
  {
    ArrayList tasklist=new ArrayList();
    StringBuffer strsql=new StringBuffer();
    strsql.append("select a.id id,start_,b.actor_id,b.name name from jbpm_token a,t_bpm_instance b where a.processinstance=");
    strsql.append("b.instance_id and a.actorId='");
    strsql.append(userView.getUserId());
    strsql.append("'");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    try
    {
      this.frowset = dao.search(strsql.toString());
      while(this.frowset.next())
      {
        TaskView taskview=new TaskView();
        taskview.setId(this.getFrowset().getString("id"));
        taskview.setName(this.getFrowset().getString("name"));
        taskview.setStart_actorid(this.getFrowset().getString("actor_id"));
        taskview.setStart_date(this.getFrowset().getString("start_"));
        tasklist.add(taskview);
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      throw GeneralExceptionHandler.Handle(sqle);
    }

    return tasklist;
  }

  private ArrayList getTaskListByJbpmService()
  {
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    // get the task list login's name
    Collection tasks = executionService.getTaskList(userView.getUserId());
    //executionService.get
    ArrayList tasklist=new ArrayList();
    Iterator iterator=tasks.iterator();
    Token token =null;

    while(iterator.hasNext())
    {
      token=(Token) iterator.next();
      //System.out.println("------>"+token.getId().toString());
      TaskView taskview=new TaskView();
      taskview.setId(token.getId().toString());
      taskview.setName(token.getProcessInstance().getDefinition().getName());
      taskview.setStart_actorid(token.getProcessInstance().getRoot().getActorId());
      taskview.setStart_date(DateStyle.dateformat(token.getStart(),"yyyy-MM-dd HH:mm:ss"));
      //taskview.setEnd_date(token.getEnd().toLocaleString());
      tasklist.add(taskview);
    }
    executionService.close();
    return tasklist;
  }

}
package com.etong.webclient.transaction;

import java.util.*;
import java.sql.*;
import org.jbpm.*;
import org.jbpm.model.execution.*;


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

public class AllTaskListTrans extends IBusiness
{
  public AllTaskListTrans()
  {
  }
  /**
   * 查找未完成的token list
   * @return
   */
  private ArrayList findTokenIdList()throws GeneralException
  {
    ArrayList tasklist=new ArrayList();
    StringBuffer strsql=new StringBuffer();
    strsql.append("select id from jbpm_token where end_ is null");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    try
    {
      this.frowset = dao.search(strsql.toString());
      while(this.frowset.next())
      {
        Long id=new Long(this.getFrowset().getString("id"));
        cat.debug("id======>"+id.toString());
        tasklist.add(id);
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      throw GeneralExceptionHandler.Handle(sqle);
    }
    return tasklist;
  }

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    ArrayList taskidlist=findTokenIdList();
    ArrayList list=new ArrayList();
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    for(int i=0;i<taskidlist.size();i++)
    {
      Token token=executionService.getToken((Long)taskidlist.get(i));
      TaskView taskview=new TaskView();
      taskview.setId(token.getId().toString());
      taskview.setName(token.getProcessInstance().getDefinition().getName());
      taskview.setStart_actorid(token.getActorId());
      taskview.setStart_date(DateStyle.dateformat(token.getStart(),"yyyy-MM-dd HH:mm:ss"));
      taskview.setState(token.getState().getName());
      taskview.setInstance_id(token.getProcessInstance().getId().toString());
      list.add(taskview);
    }
    executionService.close();
    this.getFormHM().put("tasklist",list);
  }

}
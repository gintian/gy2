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

public class HistoryTaskListTrans extends IBusiness
{
  public HistoryTaskListTrans()
  {
  }

  private ArrayList getHistoryTaskList()throws GeneralException
  {
    ArrayList tasklist=new ArrayList();
    StringBuffer strsql=new StringBuffer();
    strsql.append("select advice_id, process_id,a.instance_id instance_id,start_date,end_date,b.actor_id,b.name name from t_bpm_advice a,t_bpm_instance b where a.instance_id=");
    strsql.append("b.instance_id and a.actor_Id='");
    strsql.append(userView.getUserId());
    strsql.append("'");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    try
    {
      this.frowset = dao.search(strsql.toString());
      while(this.frowset.next())
      {
        TaskView taskview=new TaskView();
        taskview.setId(this.getFrowset().getString("advice_id"));
        taskview.setInstance_id(this.getFrowset().getString("instance_id"));
        taskview.setName(this.getFrowset().getString("name"));
        taskview.setStart_actorid(this.getFrowset().getString("actor_id"));
        taskview.setStart_date(this.getFrowset().getString("start_date"));
        taskview.setEnd_date(this.getFrowset().getString("end_date"));
        taskview.setDefinition_id(this.getFrowset().getString("process_id"));
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

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    ArrayList historytasklist = getHistoryTaskList();
    this.getFormHM().put("tasklist",historytasklist);
  }

}
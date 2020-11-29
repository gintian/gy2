package com.etong.webclient.valueobject;

import java.io.Serializable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskView implements Serializable
{
  /**
   * 流程定义id
   */
  private String definition_id;
  /**
   * 实例id
   */
  private String instance_id;
  /**
   * 流程执行者
   */
  private String start_actorid;
  /**
   * task id
   */
  private String id;
  /**
   * process's name
   */
  private String name;
  /**
   * 节点状态名
   */
  private String state;
  /**
   * process's start date
   */
  private String start_date;
  private String end_date;

  public TaskView()
  {
  }
  public String getId()
  {
    return id;
  }
  public void setId(String id)
  {
    this.id = id;
  }
  public String getName()
  {
    return name;
  }
  public void setName(String name)
  {
    this.name = name;
  }
  public String getStart_date()
  {
    return start_date;
  }
  public void setStart_date(String start_date)
  {
    this.start_date = start_date;
  }
  public String getEnd_date()
  {
    return end_date;
  }
  public void setEnd_date(String end_date)
  {
    this.end_date = end_date;
  }
  public String getStart_actorid()
  {
    return start_actorid;
  }
  public void setStart_actorid(String start_actorid)
  {
    this.start_actorid = start_actorid;
  }
  public String getInstance_id()
  {
    return instance_id;
  }
  public void setInstance_id(String instance_id)
  {
    this.instance_id = instance_id;
  }
  public String getDefinition_id()
  {
    return definition_id;
  }
  public void setDefinition_id(String definition_id)
  {
    this.definition_id = definition_id;
  }
  public String getState()
  {
    return state;
  }
  public void setState(String state)
  {
    this.state = state;
  }

}
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

public class ProcessView implements Serializable
{
  private String id;
  private String name;
  private String description;
  private String edit_eform_path;
  private String view_eform_path;
  private String input_result_path;
  private String view_result_path;
  public ProcessView()
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
  public String getDescription()
  {
    return description;
  }
  public void setDescription(String description)
  {
    this.description = description;
  }
  public String getEdit_eform_path()
  {
    return edit_eform_path;
  }
  public void setEdit_eform_path(String edit_eform_path)
  {
    this.edit_eform_path = edit_eform_path;
  }
  public String getView_eform_path()
  {
    return view_eform_path;
  }
  public void setView_eform_path(String view_eform_path)
  {
    this.view_eform_path = view_eform_path;
  }
  public String getInput_result_path()
  {
    return input_result_path;
  }
  public void setInput_result_path(String input_result_path)
  {
    this.input_result_path = input_result_path;
  }
  public String getView_result_path()
  {
    return view_result_path;
  }
  public void setView_result_path(String view_result_path)
  {
    this.view_result_path = view_result_path;
  }

}
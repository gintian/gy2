package com.etong.webclient.actionform;

import java.util.ArrayList;

import com.hrms.struts.action.*;
import com.hrms.struts.valueobject.*;
import com.hrms.frame.dao.RecordVo;
import org.apache.struts.action.*;
import javax.servlet.http.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class WorkListForm extends FrameForm
{
  public WorkListForm()
  {
  }
  /**
   * 审批表单参数中
   */
  private String eform_param;
  /**
   * actor_id 任务参与者
   */
  private String actor_id;
  /**
   * advice's list
   */
  private PaginationForm advice_list=new PaginationForm();

  /**
   * 查阅审批的电子表单WEB url地址
   */
  private String view_eform_path;
  /**
   * 签署的意见代码=01同意,=02不同意,=03未审批,=04其它意见
   */
  private String advice_type;
  /**
   * 具体意见内容
   */
  private String advice;

  private RecordVo advice_vo=new RecordVo("t_bpm_advice");

  /**
   * process's list
   */
  private PaginationForm processListForm=new PaginationForm();
  /**
   * task's list
   */
  private PaginationForm taskListForm=new PaginationForm();

  @Override
  public void outPutFormHM()
  {
    this.setView_eform_path((String)this.getFormHM().get("view_eform_path"));
    this.getAdvice_list().setList((ArrayList)this.getFormHM().get("advicelist"));
    this.setAdvice_type((String)this.getFormHM().get("advice_type"));
    this.setAdvice((String)this.getFormHM().get("advice"));
    this.setAdvice_vo((RecordVo)this.getFormHM().get("advice_vo"));
    this.setEform_param((String)this.getFormHM().get("eform_param"));
    this.getProcessListForm().setList((ArrayList)this.getFormHM().get("processlist"));
    this.getTaskListForm().setList((ArrayList)this.getFormHM().get("tasklist"));
  }

  @Override
  public void inPutTransHM()
  {
    this.getFormHM().put("advice_type",this.getAdvice_type());
    this.getFormHM().put("advice",this.getAdvice());
    this.getFormHM().put("actor_id",this.getActor_id());
    this.getFormHM().put("selectedtasklist",(ArrayList)this.getTaskListForm().getSelectedList());
    //if(this.getTaskListForm().getSelectedList()!=null)
    //this.getFormHM().put("tasklist",(ArrayList)this.getTaskListForm().getSelectedList());
  }

  public PaginationForm getProcessListForm()
  {
    return processListForm;
  }
  public void setProcessListForm(PaginationForm processListForm)
  {
    this.processListForm = processListForm;
  }
  public PaginationForm getTaskListForm()
  {
    return taskListForm;
  }
  public void setTaskListForm(PaginationForm taskListForm)
  {
    this.taskListForm = taskListForm;
  }
  public String getAdvice()
  {
    return advice;
  }
  public PaginationForm getAdvice_list()
  {
    return advice_list;
  }
  public String getAdvice_type()
  {
    return advice_type;
  }
  public RecordVo getAdvice_vo()
  {
    return advice_vo;
  }
  public void setAdvice(String advice)
  {
    this.advice = advice;
  }
  public void setAdvice_list(PaginationForm advice_list)
  {
    this.advice_list = advice_list;
  }
  public void setAdvice_type(String advice_type)
  {
    this.advice_type = advice_type;
  }
  public void setAdvice_vo(RecordVo advice_vo)
  {
    this.advice_vo = advice_vo;
  }
  public String getView_eform_path()
  {
    return view_eform_path;
  }
  public void setView_eform_path(String view_eform_path)
  {
    this.view_eform_path = view_eform_path;
  }
  public String getActor_id()
  {
    return actor_id;
  }
  public void setActor_id(String actor_id)
  {
    this.actor_id = actor_id;
  }
  @Override
  public void reset(ActionMapping parm1, HttpServletRequest parm2)
  {
    this.setActor_id("");
    this.setAdvice("");
    this.setAdvice_type("");
    super.reset(parm1, parm2);
  }
  public String getEform_param()
  {
    return eform_param;
  }
  public void setEform_param(String eform_param)
  {
    this.eform_param = eform_param;
  }

}
package com.etong.webclient.transaction;

import java.util.HashMap;
import org.jbpm.*;
import org.jbpm.model.log.InvocationLog;

import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.IDGenerator;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class StartMoneyTrans extends IBusiness
{
  public StartMoneyTrans()
  {
  }
  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String process_id=(String)hm.get("a_processid");//definition_id
    RecordVo money_vo=(RecordVo)this.getFormHM().get("money_vo");
    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
    String id=idg.getId("demo_leave");
    money_vo.setString("status","0");
    money_vo.setString("id",id);
    cat.debug("money_vo="+money_vo.toString());
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId());
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    //executionService.s

    Long definition_id=new Long(process_id);
    HashMap vs=new HashMap();
    vs.put("user_id",userView.getUserId());
    //把请假天数放去流程控制环节中去,主要用于分支去向
    vs.put("money",money_vo.getString("money"));
    vs.put("id",id);
    try
    {
      dao.addValueObject(money_vo);
      InvocationLog log=executionService.startProcessInstance(definition_id,vs);
      Long instance_id=log.getProcessInstance().getId();
      String actor_id=log.getActorId();
      RecordVo vo=new RecordVo("t_bpm_instance");
      vo.setString("instance_id",instance_id.toString());
      vo.setString("actor_id",actor_id);
      vo.setString("edit_eform_params","&id="+id);
      vo.setString("view_eform_params","&id="+id);
      vo.setString("name",log.getProcessInstance().getDefinition().getName());
      dao.addValueObject(vo);
      executionService.close();
    }
    catch(ExecutionException ee)
    {
      ee.printStackTrace();
      throw GeneralExceptionHandler.Handle(ee);
    }
    catch(Exception ee)
    {
      ee.printStackTrace();
      throw GeneralExceptionHandler.Handle(ee);
    }
  }

}
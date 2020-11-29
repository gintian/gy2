package com.etong.webclient.transaction.rensi;

import java.util.HashMap;
import org.jbpm.*;
import org.jbpm.model.log.InvocationLog;

import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.*;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.frame.utility.DateStyle;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class StartRensiTrans extends IBusiness
{
  public StartRensiTrans()
  {
  }
  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String process_id=(String)hm.get("a_processid");//definition_id
    DateStyle first_date=(DateStyle)this.getFormHM().get("first_date");
    DateStyle second_date=(DateStyle)this.getFormHM().get("second_date");
    RecordVo bian_vo=(RecordVo)this.getFormHM().get("bian_vo");
    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
    String id=idg.getId("demo_leave");
    bian_vo.setString("id",id);
    bian_vo.setString("status","0");
    bian_vo.setString("come_date",second_date.getDataStringToSecond());
    bian_vo.setString("post_date",first_date.getDataStringToSecond());
    cat.debug("bian_vo="+bian_vo.toString());
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    //executionService.s

    Long definition_id=new Long(process_id);
    HashMap vs=new HashMap();
    vs.put("user_id",userView.getUserId());
    //把请假天数放去流程控制环节中去,主要用于分支去向

    try
    {
      dao.addValueObject(bian_vo);

      InvocationLog log=executionService.startProcessInstance(definition_id,vs);

      Long instance_id=log.getProcessInstance().getId();
      String actor_id=log.getActorId();
      RecordVo vo=new RecordVo("t_bpm_instance");
      vo.setString("instance_id",instance_id.toString());
      vo.setString("actor_id",actor_id);
      vo.setString("edit_eform_params","&id="+id);
      vo.setString("view_eform_params","&id="+id);
      vo.setString("name",log.getProcessInstance().getDefinition().getName());
      cat.debug("instance_vo==="+vo.toString());
      dao.addValueObject(vo);
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
    finally
    {
      executionService.close();
    }
  }

}
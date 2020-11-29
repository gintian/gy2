package com.etong.webclient.transaction;

import com.etong.webclient.utils.ParsingExFileOfDefintion;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jbpm.ExecutionService;
import org.jbpm.JbpmServiceFactory;
import org.jbpm.model.execution.Token;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class TaskViewTrans extends IBusiness
{
  public TaskViewTrans()
  {
  }
//查找参数
  private RecordVo findHref_Params(String instance_id) throws GeneralException
  {
    RecordVo vo=new RecordVo("t_bpm_instance");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    vo.setString("instance_id",instance_id);
    try
    {
      vo = dao.findByPrimaryKey(vo);
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      throw GeneralExceptionHandler.Handle(sqle);
    }
    return vo;
  }

  private ArrayList findPreAdviceList(Token token) throws GeneralException
  {
    ArrayList list=new ArrayList();
    Long definition_id=token.getProcessInstance().getDefinition().getId();
    Long instance_id=token.getProcessInstance().getId();
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    StringBuffer strsql=new StringBuffer();
    strsql.append("select advice_id,actor_id from t_bpm_advice where process_id=");
    strsql.append(definition_id.toString());
    strsql.append(" and instance_id=");
    strsql.append(instance_id.toString());
    strsql.append(" order by advice_id");
    try
    {
     this.frecset=dao.search(strsql.toString());
     while(this.frecset.next())
     {
       RecordVo vo=new RecordVo("t_bpm_advice");
       vo.setString("advice_id",this.getFrecset().getString("advice_id"));
       vo.setString("actor_id",this.getFrecset().getString("actor_id"));
       list.add(vo);
     }
    }
    catch(SQLException sqle)
    {
      GeneralExceptionHandler.Handle(sqle);
    }
    return list;
  }

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String toke_id=(String)hm.get("a_taskid");
    String str_param="";
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    Token token=executionService.getToken(new Long(toke_id));
    Long definition_id=token.getProcessInstance().getDefinition().getId();

    ParsingExFileOfDefintion parser=new ParsingExFileOfDefintion(definition_id.toString(),this.getFrameconn());
    String view_eform_path=parser.getWebUrl("view_eform_path");
    //实例号
    String instance_id=token.getProcessInstance().getId().toString();
    //参数
    RecordVo vo=findHref_Params(instance_id);
    if(vo!=null)
      str_param=(String)vo.getObject("view_eform_params");
    //各级领导处理意见
    ArrayList advicelist=findPreAdviceList(token);
    /**
     *variable in the token.
     */
//    Map variables=executionService.getVariables(token.getId());
//
//    Iterator iterator0=variables.entrySet().iterator();
//    while(iterator0.hasNext())
//    {
//      java.util.Map.Entry entry = (java.util.Map.Entry) iterator0.next();
//      String name=entry.getKey().toString();
//      String value=entry.getValue().toString();
//      this.getFormHM().put(name,value);
//      System.out.println("====>" + name+":"+value);
//    }
    executionService.close();
    this.getFormHM().put("view_eform_path",view_eform_path);
    this.getFormHM().put("advicelist",advicelist);
    this.getFormHM().put("eform_param",str_param);

  }

}
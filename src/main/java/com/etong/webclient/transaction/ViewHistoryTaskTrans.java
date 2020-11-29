package com.etong.webclient.transaction;

import java.util.*;
import java.sql.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.frame.dao.*;
import com.hrms.struts.exception.*;
import com.etong.webclient.utils.ParsingExFileOfDefintion;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class ViewHistoryTaskTrans extends IBusiness
{
  public ViewHistoryTaskTrans()
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

  private ArrayList findPreAdviceList(String instance_id,String advice_id) throws GeneralException
  {
    ArrayList list=new ArrayList();

    ContentDAO dao=new ContentDAO(this.getFrameconn());
    StringBuffer strsql=new StringBuffer();
    strsql.append("select advice_id,actor_id from t_bpm_advice where ");
    strsql.append(" instance_id=");
    strsql.append(instance_id);
    strsql.append(" and advice_id<=");
    strsql.append(advice_id);
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
      throw GeneralExceptionHandler.Handle(sqle);
    }
    return list;
  }

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String instance_id=(String)hm.get("a_instance_id");
    String advice_id=(String)hm.get("a_advice_id");
    String a_definition=(String)hm.get("a_definition");
    String str_param=null;
    ParsingExFileOfDefintion parser=new ParsingExFileOfDefintion(a_definition,this.getFrameconn());
    String view_eform_path=parser.getWebUrl("view_eform_path");
    //参数
    RecordVo in_vo=findHref_Params(instance_id);
    if(in_vo!=null)
      str_param=(String)in_vo.getObject("view_eform_params");
      //各级领导处理意见
    ArrayList advicelist=findPreAdviceList(instance_id,advice_id);
    
    RecordVo vo=new RecordVo("t_bpm_advice");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    vo.setString("advice_id",advice_id);
    try
    {
      vo = dao.findByPrimaryKey(vo);
      this.getFormHM().put("advice_vo",vo);
      this.getFormHM().put("view_eform_path",view_eform_path);
      this.getFormHM().put("advicelist",advicelist);
      this.getFormHM().put("eform_param",str_param);
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      throw GeneralExceptionHandler.Handle(sqle);
    }
  }

}
package com.etong.webclient.transaction;


import java.util.*;
import java.sql.*;

import org.jbpm.*;
import org.jbpm.delegation.*;
import org.jbpm.model.execution.*;
import org.jbpm.model.definition.*;

import com.hrms.struts.exception.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.frame.dao.*;
import com.etong.webclient.utils.ParsingExFileOfDefintion;
import com.etong.webclient.valueobject.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class ProcessListTrans extends IBusiness
{
  public ProcessListTrans()
  {
  }

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    cat.debug("-----------------1");
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(userView.getUserId(),this.getFrameconn());
    cat.debug("-----------------2");
    Collection definitions=executionService.getLatestDefinitions();
    ArrayList definitionlist=new ArrayList();
    ArrayList processlist=new ArrayList();
    Iterator iterator=definitions.iterator();
    Definition definition=null;
    while(iterator.hasNext())
    {
      definition=(Definition)iterator.next();
      String id=definition.getId().toString();
      ParsingExFileOfDefintion parser=new ParsingExFileOfDefintion(id,this.getFrameconn());
      ProcessView processview=new ProcessView();
      processview.setId(id);
      processview.setName (definition.getName());
      processview.setDescription(definition.getDescription());
      processview.setEdit_eform_path(parser.getWebUrl("edit_eform_path"));
      processview.setView_eform_path(parser.getWebUrl("view_eform_path"));
      processview.setInput_result_path(parser.getWebUrl("input_result_path"));
      processview.setView_result_path(parser.getWebUrl("view_result_path"));
      processlist.add(processview);
    }
    cat.debug("-----------------3");
    this.getFormHM().put("processlist",processlist);
    executionService.close();
//    String strsql="select  id,name,description from jbpm_definition";
//    ContentDAO dao=new ContentDAO(this.getFrameconn());
//    ArrayList processlist=new ArrayList();
//    try
//    {
//      this.frecset=dao.search(strsql.toString(), new ArrayList());
//      while(this.frecset.next())
//      {
//          String  id=this.getFrecset().getString("id");
//          //System.out.println("----->"+id);
//          ParsingExFileOfDefintion parser=new ParsingExFileOfDefintion(id,this.getFrameconn());
//          ProcessView processview=new ProcessView();
//          processview.setId(this.getFrecset().getString("id"));
//          processview.setName (this.getFrecset().getString("name"));
//          processview.setDescription(this.getFrecset().getString("description"));
//          processview.setEdit_eform_path(parser.getWebUrl("edit_eform_path"));
//          processview.setView_eform_path(parser.getWebUrl("view_eform_path"));
//          processview.setInput_result_path(parser.getWebUrl("input_result_path"));
//          processview.setView_result_path(parser.getWebUrl("view_result_path"));
//          processlist.add(processview);
//      }
//      /**
//       * 取得的结果返回到前台Form中
//       */
//      this.getFormHM().put("processlist",processlist);
//    }
//    catch(Exception sqle)
//    {
//      sqle.printStackTrace();
//      GeneralExceptionHandler.Handle(sqle);
//    }

  }

}
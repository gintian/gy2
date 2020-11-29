package com.etong.webclient.transaction.rensi;

import java.util.*;
import java.sql.*;
import javax.sql.RowSet;
import org.jbpm.*;
import org.jbpm.model.execution.*;
import com.hrms.struts.facade.transaction.*;
import com.hrms.struts.exception.*;
import com.hrms.frame.dao.*;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class RensiChangeTrans extends IBusiness
{
  public RensiChangeTrans()
  {
  }

  private RecordVo find_Vo(RecordVo  vo) throws GeneralException
  {
    ContentDAO dao=new ContentDAO(this.getFrameconn());
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

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String id=(String)hm.get("id");

    if(id==null|| "".equals(id))
       return;
    RecordVo vo=new RecordVo("t_rensi_biandong");
    vo.setString("id",id);
    cat.debug("vo="+vo.toString());
    vo=find_Vo(vo);
    cat.debug("vo="+vo.toString());
    this.getFormHM().put("bian_vo",vo);
  }

}
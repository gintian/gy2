package com.etong.webclient.transaction;

import java.util.HashMap;
import java.sql.*;
import javax.sql.RowSet;

import com.hrms.struts.facade.transaction.*;
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

public class ViewAdviceTrans extends IBusiness
{
  public ViewAdviceTrans()
  {
  }

  @Override
  public void execute() throws com.hrms.struts.exception.GeneralException
  {
    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    String advice_id=(String)hm.get("a_advice_id");
    ContentDAO dao=new ContentDAO(this.getFrameconn());
    RecordVo vo=new RecordVo("t_bpm_advice");
    vo.setString("advice_id",advice_id);
    cat.debug("------>advice_id="+advice_id);
    try
    {
      vo = dao.findByPrimaryKey(vo);
      this.getFormHM().put("advice_vo",vo);
//      this.getFormHM().put("advice_type",vo.getString("advice_type"));
//      this.getFormHM().put("advice",vo.getString("advice_value"));
    }
    catch(SQLException ss)
    {
      ss.printStackTrace();
      throw GeneralExceptionHandler.Handle(ss);
    }

  }

}
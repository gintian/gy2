package com.etong.webclient.test;

import java.sql.*;

import org.jbpm.delegation.ActionHandler;
import org.jbpm.delegation.ExecutionContext;
import org.jbpm.*;

import com.hrms.frame.dao.*;
import com.hrms.frame.utility.*;
import com.hrms.struts.exception.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class EndActionHandle implements ActionHandler
{
  public EndActionHandle()
  {
  }

  @Override
  public void execute(ExecutionContext parm1)
  {
    //审批同意的金额
    String  money=(String)parm1.getVariable("money");
    //意见标识
    String status=(String)parm1.getVariable("advice_type");
    String id=(String)parm1.getVariable("id");
    System.out.println("------------>action execute!");
    Connection conn = null;
    try
    {
      conn = AdminDb.getConnection();
      ContentDAO dao=new ContentDAO(conn);
      RecordVo vo=new RecordVo("t_demo_money");
      vo.setString("id",id);
      vo.setString("status","1");
      dao.updateValueObject(vo);
      System.out.println("------------>action execute Ok!");
    }
    catch(Exception ss)
    {
      ss.printStackTrace();
    }finally{
    	if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    }
  }

}
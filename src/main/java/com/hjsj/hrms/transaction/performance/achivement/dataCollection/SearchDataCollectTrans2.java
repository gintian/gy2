package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:SearchDataCollectTrans2.java</p>
 * <p>Description:数据采集目标跟踪</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 13:00:00</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class SearchDataCollectTrans2 extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		HashMap rhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String codesetid = (String) rhm.get("codeset");
		String a_code = (String) rhm.get("a_code");
		String object_type = (String) this.getFormHM().get("object_type");
		String sqlStr = (String) this.getFormHM().get("sql");
		sqlStr = PubFunc.keyWord_reback(sqlStr);
		StringBuffer sql = new StringBuffer(sqlStr);
	
		/*String isFromTarget = (String) this.getFormHM().get("isFromTarget");
		if (isFromTarget.equals("0"))
		{
		    if (a_code != null && a_code.trim().length() > 0)
		    {
				String codesetid = a_code.substring(0, 2);
				String value = a_code.substring(2);
				if (value.length() > 0)
				{
				    if (object_type.equals("2"))
				    {
						if (codesetid.equalsIgnoreCase("UN"))
						{
						    sql.append(" and b0110 like '");
						    sql.append(value);
						    sql.append("%' ");
						} else if (codesetid.equalsIgnoreCase("UM"))
						{
						    sql.append(" and e0122 like '");
						    sql.append(value);
						    sql.append("%' ");
						} else if (codesetid.equalsIgnoreCase("@K"))
						{
						    sql.append(" and e01a1 like '");
						    sql.append(value);
						    sql.append("%' ");
						} else if (a_code.length() > 2 && a_code.substring(0, 3).equalsIgnoreCase("Usr"))
						{
						    sql.append(" and a0100 = '");
						    sql.append(a_code.substring(3));
						    sql.append("' ");
						}
				    } else
				    {
						sql.append(" and b0110 like '");
						sql.append(value);
						sql.append("%' ");
				    }
				}
		    }
		} else
		{
		    if (a_code != null && a_code.trim().length() > 0)
		    {
				String codesetid = a_code.substring(0, 2);
				String value = a_code.substring(2);
				if (value.length() > 0)
				{
				    if (object_type.equals("2"))
				    {
						if (codesetid.equalsIgnoreCase("UN"))
						{
						    sql.append(" and b0110 = '");
						    sql.append(value);
						    sql.append("' ");
						} else if (codesetid.equalsIgnoreCase("UM"))
						{
						    sql.append(" and e0122 = '");
						    sql.append(value);
						    sql.append("' ");
						} else if (codesetid.equalsIgnoreCase("@K"))
						{
						    sql.append(" and e01a1 = '");
						    sql.append(value);
						    sql.append("' ");
						} else if (a_code.length() > 2 && a_code.substring(0, 3).equalsIgnoreCase("Usr"))
						{
						    sql.append(" and a0100 = '");
						    sql.append(a_code.substring(3));
						    sql.append("' ");
						}
				    } else
				    {
						sql.append(" and b0110 = '");
						sql.append(value);
						sql.append("' ");
				    }
				}
		    }
		}*/
		if (a_code != null && a_code.trim().length() > 0 && codesetid==null){
		    sql.append(" and ( score_org is null or score_org ='' or score_org like '"+a_code.substring(2)+"%') ") ;
		}
		sql.append(" order by b0110");
		if ("2".equals(object_type))
		    sql.append(",a0101");
	
		ContentDAO dao = new ContentDAO(this.frameconn);
		String isHaveRecords = "0";
		
		try
		{
			
		    this.frowset = dao.search(sql.toString());
		    if (this.frowset.next())
		    	isHaveRecords = "1";
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("isHaveRecords", isHaveRecords);
		this.getFormHM().put("sqlWhere", sql.toString());
    }
}

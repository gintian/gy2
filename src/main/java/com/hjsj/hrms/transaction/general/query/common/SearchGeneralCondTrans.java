/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.common;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 *<p>Title:根据条件号查询常用条件</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-5-21:下午05:43:20</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGeneralCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		String curr_id=(String)this.getFormHM().get("curr_id");
		try
		{

			StringBuffer buf=new StringBuffer();
			StringBuffer lexp=new StringBuffer();
			buf.append("select lexpr,factor from lexpr where id=?");
			ArrayList paralist=new ArrayList();
			paralist.add(curr_id);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
			{
				lexp.append(rset.getString("lexpr"));
				lexp.append("|");
				lexp.append(rset.getString("factor"));
			}

			this.getFormHM().put("lexpr", lexp.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
